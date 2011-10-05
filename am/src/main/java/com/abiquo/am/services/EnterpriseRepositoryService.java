/**
 * Abiquo community edition
 * cloud management application for hybrid clouds
 * Copyright (C) 2008-2010 - Abiquo Holdings S.L.
 *
 * This application is free software; you can redistribute it and/or
 * modify it under the terms of the GNU LESSER GENERAL PUBLIC
 * LICENSE as published by the Free Software Foundation under
 * version 3 of the License
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * LESSER GENERAL PUBLIC LICENSE v.3 for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the
 * Free Software Foundation, Inc., 59 Temple Place - Suite 330,
 * Boston, MA 02111-1307, USA.
 */

package com.abiquo.am.services;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.FilenameFilter;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import javax.ws.rs.core.Response.Status;

import org.apache.commons.io.FileUtils;
import org.dmtf.schemas.ovf.envelope._1.DiskSectionType;
import org.dmtf.schemas.ovf.envelope._1.EnvelopeType;
import org.dmtf.schemas.ovf.envelope._1.FileType;
import org.dmtf.schemas.ovf.envelope._1.VirtualDiskDescType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.abiquo.am.exceptions.AMError;
import com.abiquo.am.services.util.OVFPackageInstanceToOVFEnvelope;
import com.abiquo.am.services.util.TimeoutFSUtils;
import com.abiquo.api.service.DefaultApiService;
import com.abiquo.appliancemanager.config.AMConfigurationManager;
import com.abiquo.appliancemanager.exceptions.AMException;
import com.abiquo.appliancemanager.exceptions.DownloadException;
import com.abiquo.appliancemanager.exceptions.RepositoryException;
import com.abiquo.appliancemanager.transport.OVFPackageInstanceStatusType;
import com.abiquo.ovfmanager.ovf.OVFEnvelopeUtils;
import com.abiquo.ovfmanager.ovf.OVFReferenceUtils;
import com.abiquo.ovfmanager.ovf.exceptions.IdNotFoundException;
import com.abiquo.ovfmanager.ovf.exceptions.XMLException;
import com.abiquo.ovfmanager.ovf.xml.OVFSerializer;

import static com.abiquo.am.services.OVFPackageConventions.*;

/**
 * Each enterprise have its own logical separation on the current physical Repository. This is
 * implemented using a folder (with the Enterprise identifier)
 */
public class EnterpriseRepositoryService extends DefaultApiService
{
    /** The constant logger object. */
    private final static Logger logger = LoggerFactory.getLogger(EnterpriseRepositoryService.class);

    private final static String BASE_REPO_PATH = AMConfigurationManager.getInstance()
        .getAMConfiguration().getRepositoryPath();

    private final static Integer FS_TIMOUT_MS = AMConfigurationManager.getInstance()
        .getAMConfiguration().getFsTimeoutMs();

    /** Immutable singelton instances base on its Enterprise Identifier. */
    private static Map<String, EnterpriseRepositoryService> enterpriseHandlers =
        new HashMap<String, EnterpriseRepositoryService>();

    /** Repository path particular of the current enterprise. */
    private final String enterpriseRepositoryPath;

    private EnterpriseRepositoryService(final String idEnterprise)
    {
        enterpriseRepositoryPath = codifyEnterpriseRepositoryPath(BASE_REPO_PATH, idEnterprise);

        validateEnterpirseRepositoryPathFile();
    }

    /**
     * Factory method, maitains a single object reference for each enterprise identifier.
     */
    public static synchronized EnterpriseRepositoryService getRepo(final String idEnterprise)
    {
        return getRepo(idEnterprise, false);
    }

    public static synchronized EnterpriseRepositoryService getRepo(final String idEnterprise,
        final boolean checkCanWrite)
    {
        if (checkCanWrite)
        {
            TimeoutFSUtils.getInstance().canWriteRepository();
        }

        if (!enterpriseHandlers.containsKey(idEnterprise))
        {
            enterpriseHandlers.put(idEnterprise, new EnterpriseRepositoryService(idEnterprise));
        }

        return enterpriseHandlers.get(idEnterprise);
    }

    /**
     * Check if it exist or create it.
     */
    private void validateEnterpirseRepositoryPathFile()
    {
        TimeoutFSUtils.getInstance().canUseRepository();

        File enterpriseRepositoryFile = new File(enterpriseRepositoryPath);

        if (!enterpriseRepositoryFile.exists())
        {
            if (!enterpriseRepositoryFile.mkdirs())
            {
                addError(new AMException(AMError.REPO_NOT_ACCESSIBLE, enterpriseRepositoryPath));
                flushErrors();
            }
        }

        if (!(enterpriseRepositoryFile.exists() && enterpriseRepositoryFile.canWrite() && enterpriseRepositoryFile
            .isDirectory()))
        {
            addError(new AMException(AMError.REPO_NOT_ACCESSIBLE, enterpriseRepositoryPath));
            flushErrors();
        }
    }

    public List<String> getAllOVF(final boolean includeBundeles)
    {

        /**
         * TODO caching results
         */
        File enterpriseRepositoryFile = new File(enterpriseRepositoryPath);

        TimeoutFSUtils.getInstance().canUseRepository();

        List<String> availableOvs = null;

        ExecutorService executor = Executors.newSingleThreadExecutor();

        final Future<List<String>> futureAvailable =
            executor.submit(new TraverseFSWithTimeout(enterpriseRepositoryFile.getAbsolutePath(),
                new String(),
                includeBundeles,
                false));

        try
        {
            availableOvs = futureAvailable.get(FS_TIMOUT_MS, TimeUnit.MILLISECONDS);
        }
        catch (TimeoutException e)
        {
            futureAvailable.cancel(true);
        }
        catch (Exception e)
        {
            logger.error("Can't inspect the folder " + enterpriseRepositoryPath, e);
        }
        finally
        {
            executor.shutdownNow();
        }

        if (availableOvs != null)
        {
            cachedpackages = availableOvs;
        }
        else if (cachedpackages != null)
        {
            logger.warn("Slow file system, can't list folder [{}] content (timout), "
                + "using cached result.", enterpriseRepositoryPath);

            availableOvs = cachedpackages;
        }
        else
        {
            addError(new AMException(AMError.REPO_TIMEOUT_REFRESH, enterpriseRepositoryPath));
        }

        List<String> cleanovfids = new LinkedList<String>();

        for (String ovfid : availableOvs)
        {
            cleanovfids.add(cleanOVFurlOnOldRepo(ovfid));
        }

        return cleanovfids;
    }

    /***
     * @throws IdNotFoundException, if the OVF package do not exist or is not on DOWNLOAD state.
     */
    public void deleteOVF(final String ovfId) throws IdNotFoundException, RepositoryException
    {
        TimeoutFSUtils.getInstance().canUseRepository();

        if (isImportedBundleOvfId(ovfId))
        {
            deleteImportedBundle(ovfId);
            return;
        }

        if (isBundleOvfId(ovfId))
        {
            deleteBundle(ovfId);
            return;
        }

        OVFPackageInstanceStatusType status = getOVFStatus(ovfId);

        if (status == OVFPackageInstanceStatusType.NOT_DOWNLOAD)
        {
            // already deleted
            return;
        }

        final String packagePath = getOVFPackagePath(enterpriseRepositoryPath, ovfId);

        File packageFile = new File(packagePath);
        String[] bundles = packageFile.list(new BundleImageFileFilter());
        if (bundles == null || bundles.length == 0)
        {
            logger.debug("There are any bundle, deleting all the folder");
            try
            {
                FileUtils.deleteDirectory(packageFile);
            }
            catch (IOException e)
            {
                // caused by .nfs temp files (try retry in 5s)
                if (e instanceof FileNotFoundException)
                {
                    try
                    {
                        Thread.sleep(5000);
                    }
                    catch (InterruptedException e1)
                    {
                        e1.printStackTrace();
                    }

                    try
                    {
                        FileUtils.deleteDirectory(packageFile);
                    }
                    catch (IOException e1)
                    {
                        addError(new AMException(AMError.OVFPI_DELETE, enterpriseRepositoryPath, e1));
                        flushErrors();
                    }
                }// nfs issue

            }
            return;
        }
        else
        {
            final StringBuffer stBuffer = new StringBuffer();
            stBuffer
                .append(String.format(
                    "The selected OVF Package Instance [%s] has snapshot instances associates:",
                    ovfId));
            for (String bund : bundles)
            {
                stBuffer.append("\n" + bund);
            }

            stBuffer.append("\n It can not be deleted.");

            addError(new AMException(AMError.OVFPI_DELETE_INSTANCES, stBuffer.toString()));
            flushErrors();
        }
    }

    /**
     * imported bundles do not use the ''enterpriserepopath''
     */
    public void deleteImportedBundle(final String ovfId)     
    {
        final String path =
            ovfId.substring(OVF_BUNDLE_IMPORTED_PREFIX.length(), ovfId.lastIndexOf('/'));
        final String absPath = BASE_REPO_PATH + path;

        File importBundleDir = new File(absPath);

        if (!importBundleDir.exists())
        {
            addError(new AMException(AMError.OVFPI_SNAPSHOT_NOT_EXIST, msg));
            
            
        }
        else if (!importBundleDir.isDirectory())
        {
            throw new IdNotFoundException(String.format(
                "The path is not a folder. %s\nShould be a bundle of an imported virtual machine.",
                absPath));
        }
        try
        {
            FileUtils.deleteDirectory(importBundleDir);
        }
        catch (IOException e)
        {
            logger.error("Can not delete the bundle of an imported virtual machine, on folder {}",
                absPath);
        }

    }

    public void deleteBundle(final String ovfId) throws IdNotFoundException
    {
        final String masterOvf = getBundleMasterOvfId(ovfId);
        final String snapshot = getBundleSnapshot(ovfId);

        final String packagePath = getOVFPackagePath(ovfId);
        final EnvelopeType envelope = getEnvelope(masterOvf);
        Set<String> fileLocations = OVFReferenceUtils.getAllReferencedFileLocations(envelope);

        for (String fileLocation : fileLocations)
        {
            final String absoultePath = packagePath + fileLocation;
            final String bundleAbsoultePath = createBundleOvfId(absoultePath, snapshot);

            File file = new File(bundleAbsoultePath);

            if (!file.exists())
            {
                logger.warn("Path [{}] not exist, try to remove files starting with [{}]",
                    absoultePath, snapshot);

                String[] filesStarting = new File(packagePath).list(new FormatsFilter(snapshot));

                if (filesStarting != null)
                {
                    for (String fileStart : filesStarting)
                    {
                        File fileStartFile = new File(packagePath + fileStart);
                        if (!fileStartFile.delete())
                        {
                            logger.error("Try to remove the path [{}] but is not possible",
                                fileStart);
                        }
                    }
                }
            }
            else
            {
                if (!file.delete())
                {
                    logger.error("Try to remove the path [{}] but is not possible", absoultePath);
                }
            }

            final String relativePath =
                bundleAbsoultePath.substring(bundleAbsoultePath.lastIndexOf('/') + 1);
            deleteBundleConversion(packagePath, relativePath);
        }// all files
    }

    private void deleteBundleConversion(final String packagePath, final String fileName)
    {
        final String formatsPath = packagePath + "formats";

        String[] formatFiles = new File(formatsPath).list(new FormatsFilter(fileName));

        for (String formatFile : formatFiles)
        {
            final String filePath = formatsPath + formatFile;
            final File f = new File(filePath);

            // file should exist, just retrieved from filenamefilter
            if (!f.delete())
            {
                final String cause =
                    String.format("Can not delte the converted disk at [%s]", filePath);
                logger.error(cause);
            }
        }

    }

    class FormatsFilter implements FilenameFilter
    {
        final String baseFile;

        public FormatsFilter(final String baseFile)
        {
            this.baseFile = baseFile;
        }

        @Override
        public boolean accept(final File dir, final String name)
        {
            return name.startsWith(baseFile);
        }
    }

    public String getDiskFilePath(final String ovfid) throws RepositoryException
    {

        EnvelopeType envelope;
        DiskSectionType diskSection;
        try
        {
            envelope = getEnvelope(ovfid);
            diskSection = OVFEnvelopeUtils.getSection(envelope, DiskSectionType.class);
        }
        catch (Exception e)// SectionNotPresentException InvalidSectionException
        {
            final String cause =
                String.format("The provided envelope [%s] do not have DiskSection", ovfid);
            throw new RepositoryException(cause);
        }

        List<VirtualDiskDescType> disks = diskSection.getDisk();

        if (disks == null || disks.isEmpty() || disks.size() != 1)
        {
            final String cause =
                String.format("The provided envelope [%s] do not have a single disk", ovfid);
            throw new RepositoryException(cause);
        }

        final String diskFileId = disks.get(0).getFileRef();
        FileType file;
        try
        {
            file = OVFReferenceUtils.getReferencedFile(envelope, diskFileId);
        }
        catch (IdNotFoundException e)
        {
            final String cause =
                String.format("The disk file [%s] is not found on envelope [%s] ", diskFileId,
                    ovfid);
            throw new RepositoryException(cause);
        }

        return file.getHref();
    }

    /**
     * if (diskId == null || diskId.length() == 0) { }// remove all the package
     */
    public EnvelopeType getEnvelope(final String ovfId) throws IdNotFoundException
    {
        assert getOVFStatus(ovfId) != OVFPackageInstanceStatusType.NOT_DOWNLOAD;

        TimeoutFSUtils.getInstance().canUseRepository();

        EnvelopeType envelope;

        String ovfPath = enterpriseRepositoryPath + getRelativeOVFPath(ovfId);
        File ovfFile = new File(ovfPath);
        FileInputStream fileIs = null;

        try
        {
            if (!ovfFile.exists())
            {
                // for pre 1.6 repository file system structure
                try
                {
                    final String oldOvfId = cleanOVFurlForOldRepo(ovfId);

                    if (!oldOvfId.equalsIgnoreCase(ovfId))
                    {
                        return getEnvelope(oldOvfId);
                    }
                    else
                    {
                        throw new FileNotFoundException(ovfPath);
                    }
                }
                catch (IdNotFoundException ide)
                {
                    throw new FileNotFoundException(ovfPath);
                }
            }

            fileIs = new FileInputStream(ovfFile);
            envelope = OVFSerializer.getInstance().readXMLEnvelope(fileIs);

        }
        catch (Exception e)
        {
            final String msg = "Can not obtain the OVF Envelope file of OVF[" + ovfId + "]";

            throw new IdNotFoundException(msg, e);
        }
        finally
        {
            // if (fileIs != null) // file exist grant for ''getOVFPath''

            try
            {
                if (fileIs != null)
                {
                    fileIs.close();
                }
            }
            catch (IOException e)
            {
                logger.warn("Can not close [{}]", ovfFile);
            }

        }

        return envelope;
    }

    public File getOVFDiskFile(final String ovfId) throws IdNotFoundException
    {
        boolean isBundle = isBundleOvfId(ovfId);

        final String ovfPath = enterpriseRepositoryPath + getRelativeOVFPath(ovfId);

        String masterOvfId;

        if (!isBundle)
        {
            masterOvfId = ovfId;
        }
        else
        {
            masterOvfId = getBundleMasterOvfId(ovfId);
        }

        final EnvelopeType envelope = getEnvelope(masterOvfId);

        DiskSectionType diskSection;
        try
        {
            diskSection = OVFEnvelopeUtils.getSection(envelope, DiskSectionType.class);
        }
        catch (Exception e) // Sections
        {
            final String cause =
                String.format("The envelope for OVF [%s] do not contains the DiskSection", ovfId);
            throw new IdNotFoundException(cause);
        }

        if (diskSection.getDisk().size() != 1)
        {
            final String cause =
                String.format("There are more than one disk on the OVF [%s]", ovfId);
            throw new IdNotFoundException(cause);
        }

        final String fileId = diskSection.getDisk().get(0).getFileRef();

        final FileType refFile = OVFReferenceUtils.getReferencedFile(envelope, fileId);

        final String relPath = refFile.getHref();

        final String ovfFolder = ovfPath.substring(0, ovfPath.lastIndexOf('/'));

        String filePath = ovfFolder + '/' + relPath;

        if (isBundle)
        {
            final String snapshot = getBundleSnapshot(ovfId);
            filePath = createBundleOvfId(filePath, snapshot);
        }

        File f = new File(filePath);

        if (!f.exists())
        {
            final String msg =
                String.format("File [%s] not found for OVF[%s] hRef[%s]", filePath, ovfId, relPath);
            throw new IdNotFoundException(msg);
        }

        return f;
    }

    public OVFPackageInstanceStatusType getOVFStatus(final String ovfId)
    {
        TimeoutFSUtils.getInstance().canUseRepository();

        final String packagePath = getOVFPackagePath(ovfId);
        final String ovfEnvelopePath = enterpriseRepositoryPath + getRelativeOVFPath(ovfId);

        File errorMarkFile = new File(packagePath + OVF_STATUS_ERROR_MARK);
        if (errorMarkFile.exists())
        {
            OVFPackageInstanceStatusType status = OVFPackageInstanceStatusType.ERROR;
            status.setErrorCause(readErrorMarkFile(errorMarkFile));
            return status;
        }

        if (new File(packagePath + OVF_STATUS_DOWNLOADING_MARK).exists())
        {
            return OVFPackageInstanceStatusType.DOWNLOADING;
        }

        if (!new File(ovfEnvelopePath).exists())
        {
            return OVFPackageInstanceStatusType.NOT_DOWNLOAD;
        }
        else
        {
            return OVFPackageInstanceStatusType.DOWNLOAD; // XXX Check exist the OVF description ??
            // and all its files
        }
    }

    /**
     * synch to avoid multiple changes on the package folder (before 'clearOVFStatusMarks').
     * 
     * @throws RepositoryException
     */
    public synchronized void createOVFStatusMarks(final String ovfId,
        final OVFPackageInstanceStatusType status, final String errorMsg)
        throws RepositoryException
    {
        assert status != OVFPackageInstanceStatusType.ERROR || errorMsg != null;

        final String packagePath = getOVFPackagePath(ovfId);

        clearOVFStatusMarks(packagePath);

        File mark = null;
        boolean errorCreate = false;
        try
        {
            switch (status)
            {
                case DOWNLOAD:
                    // after clean the prev. marks, nothing to do.
                    break;

                case NOT_DOWNLOAD: // once the OVF envelope (.ovf) is deleted its NOT_FOUND
                    break;

                case DOWNLOADING:
                    mark = new File(packagePath + '/' + OVF_STATUS_DOWNLOADING_MARK);
                    errorCreate = !mark.createNewFile();
                    break;

                case ERROR:
                    mark = new File(packagePath + '/' + OVF_STATUS_ERROR_MARK);
                    errorCreate = !mark.createNewFile();

                    if (!errorCreate)
                    {
                        FileWriter fileWriter = new FileWriter(mark);
                        fileWriter.append(errorMsg);
                        fileWriter.close();
                    }
                    break;

                default:
                    final String cause =
                        String.format("Unknow STATUS[%s] for ovf [%s]", status.name(), ovfId);
                    throw new RepositoryException(cause);

            }// switch

        }
        catch (IOException ioe)
        {
            final String cause =
                String.format("Can not create mark file at [%s]", mark.getAbsoluteFile());
            throw new RepositoryException(cause);
        }
        if (errorCreate)
        {
            final String cause =
                String.format("Can not create mark file at [%s]", mark.getAbsoluteFile());
            throw new RepositoryException(cause);
        }
    }

    private void clearOVFStatusMarks(final String packagePath) throws RepositoryException
    {
        File errorMarkFile = new File(packagePath + OVF_STATUS_ERROR_MARK);
        File downloadingMarkFile = new File(packagePath + OVF_STATUS_DOWNLOADING_MARK);

        if (errorMarkFile.exists())
        {
            if (!errorMarkFile.delete())
            {
                final String cause =
                    String.format("Can not remove OVF package status mark file at [%s]",
                        errorMarkFile.getAbsolutePath());
                throw new RepositoryException(cause);
            }
        }

        if (downloadingMarkFile.exists())
        {
            if (!downloadingMarkFile.delete())
            {
                final String cause =
                    String.format("Can not remove OVF package status mark file at [%s]",
                        downloadingMarkFile.getAbsolutePath());
                throw new RepositoryException(cause);
            }
        }
    }

    private String readErrorMarkFile(final File errorMarkFile)
    {
        assert errorMarkFile.exists();

        Scanner errorMarkReader = null;

        try
        {
            errorMarkReader = new Scanner(errorMarkFile).useDelimiter(END_OF_FILE_MARK);
        }
        catch (FileNotFoundException e)
        {
            // was checked
        }

        return errorMarkReader.next();
    }

    public String prepareBundle(final String name)
    {
        TimeoutFSUtils.getInstance().canUseRepository();

        final String ovfId = OVF_LOCATION_PREFIX + "bundle/" + name + '/' + name + ".ovf";

        final String packPath = getOVFPackagePath(ovfId);

        File f2 = new File(packPath + "formats");
        f2.mkdirs();

        // TODO check success
        return ovfId;
    }

    public boolean isEnoughtSpaceOn(final Long expected)
    {
        return new File(enterpriseRepositoryPath).getFreeSpace() > expected;
    }

    /**
     * Create the folder and write to it the OVF.
     * 
     * @return the OVF envelope document obtained form the OVF location.
     * @throws RepositoryException, if can not create any of the required folders on the Enterprise
     *             Repository.
     */
    public EnvelopeType createOVFPackageFolder(final String ovfId, final EnvelopeType envelope)
        throws RepositoryException
    {
        final String envelopePath = enterpriseRepositoryPath + getRelativeOVFPath(ovfId); // XXX

        writeOVFFileToOVFPackageDir(envelopePath, envelope);

        return envelope;
    }

    public void createOVFPackageFolder(final String ovfId) throws RepositoryException
    {
        TimeoutFSUtils.getInstance().canUseRepository();

        final String packagePath = getOVFPackagePath(ovfId);
        // final String packageName = repository.getOVFPackageName(ovfId);
        // should be equals to concatenation

        // just create the folder
        File packFile = new File(packagePath);

        if (!packFile.exists())
        {
            if (!packFile.mkdirs())
            {
                final String cause =
                    String
                        .format("Can not create the path [%s] to deploy [%s]", packagePath, ovfId);
                throw new RepositoryException(cause);
            }

            logger.debug("Created OVF package folder [{}]", packagePath);
        }

        createOVFPackageFormatsFolder(packagePath);
    }

    /**
     * Write the OVF envelope file to the OVF package folder.
     * 
     * @param envelpePath, path on the Enterprise Repository identifying the current OVF.
     * @param envelope, the OVF envelope document to be write.
     * @param description, the OVF description associated. Change its OVFFile attribute to AM's
     *            internal repository relative path.
     * @throws RepositoryException, if some error occurs during this process, The package already
     *             was being deployed.
     */
    private void writeOVFFileToOVFPackageDir(final String envelopePath, final EnvelopeType envelope)
        throws RepositoryException
    {

        /**
         * TODO check all the file 'hRef' to use relative paths !
         */

        File envelopeFile = new File(envelopePath);

        try
        {
            if (envelopeFile.exists()) // XXX implicit if (!envelopeFile.exists())
            {
                final String cause =
                    String.format("The OVF envelope has been already deployed, "
                        + "file [%s] exist on the repository", envelopePath);
                logger.warn(cause);
                envelopeFile.delete();
            }

            if (!envelopeFile.createNewFile())
            {
                final String cause =
                    String.format("Can not create Envelope file at [%s]", envelopePath);
                throw new RepositoryException(cause);
            }
        }
        catch (IOException e)
        {
            final String cause =
                String.format("Can not create the Envelope file at [%s]", envelopePath);
            throw new RepositoryException(cause);
        }

        FileOutputStream envelopeStream = null;
        try
        {
            envelopeStream = new FileOutputStream(envelopeFile);
            OVFSerializer.getInstance().writeXML(envelope, envelopeStream);
        }
        catch (Exception e1) // IOException or XMLException
        {
            final String msg =
                String.format("The OVF XML envelpe cannot be written to the repository at [%s]",
                    envelopePath);

            throw new RepositoryException(msg, e1);
        }
        finally
        {
            if (envelopeStream != null)
            {
                try
                {
                    envelopeStream.close();
                }
                catch (IOException e)
                {
                    final String cause =
                        String.format("Can not close the Envelope File at [%s]", envelopePath);
                    throw new RepositoryException(cause);
                }
            }
        }// finally
    }

    private void createOVFPackageFormatsFolder(final String packagePath) throws RepositoryException
    {

        final String formatsPath = packagePath + '/' + FORMATS_PATH;

        File formatsFolder = new File(formatsPath);

        if (!formatsFolder.exists())
        // !! XXX assume if the enterpriseRepositoryPath can be written then also all the package
        // and formats path.
        {
            if (!formatsFolder.mkdir())
            {
                final String cause =
                    String.format("Can not create 'Formats' folder on [%s]", formatsPath);
                throw new RepositoryException(cause);
            }
        }

    }

    public void copyFileToOVFPackagePath(final String ovfid, final File file) throws IOException
    {
        // Transfer the upload content into the repository file system
        final String filePath = getOVFPackagePath(ovfid) + file.getName();

        File f = new File(filePath);
        // TODO check do not exist and can be created

        FileUtils.moveFile(file, f);
        // FileUtils.copyFile(file, f);
    }

    public String createBundle(final String ovfId, final String snapshot,
        EnvelopeType envelopeBundle) throws RepositoryException
    {
        TimeoutFSUtils.getInstance().canUseRepository();

        final String masterOvf = getMasterOVFPackage(ovfId);
        final String snapshotMark = snapshot + OVF_BUNDLE_PATH_IDENTIFIER;
        final String packagePath = getOVFPackagePath(ovfId);

        // Set<String> diskFileIds = new HashSet<String>();
        // DiskSectionType diskSection;

        try
        {
            final String originalDiskPath = getDiskFilePath(masterOvf);

            // TODO envelopeBundle =
            // OVFPackageInstanceToOVFEnvelope.fixFilePathsAndSize(envelopeBundle, snapshot,
            // snapshotMark, packagePath);
        }
        catch (RepositoryException re) // Envelope do not exist or dont have the Disk Section
        {
            final String cause =
                String.format("The bundle [%s] of OVF [%s] can not be created "
                    + "because the original envelope do not exist or do not have Disk Section",
                    snapshot, ovfId);

            throw new RepositoryException(cause, re);
        }

        // write the new (bundle) OVF envelope with the file references changes
        final String packageName = getOVFPackageName(masterOvf);
        final String bundlePath = packagePath + snapshotMark + packageName;
        File envelopeBundleFile = new File(bundlePath);

        if (envelopeBundleFile.exists())
        {
            final String cause =
                String.format("The bundle [%s] of OVF [%s] can not be created "
                    + "because the bundle envelope already exist on [%s]", snapshot, ovfId,
                    bundlePath);
            throw new RepositoryException(cause);
        }

        FileOutputStream bundleEnvelopeStream;
        try
        {
            bundleEnvelopeStream = new FileOutputStream(envelopeBundleFile);
        }
        catch (FileNotFoundException e1)
        {
            // XXX just checked it exist
            throw new RepositoryException(e1);
        }

        envelopeBundle =
            OVFPackageInstanceToOVFEnvelope.fixFilePathsAndSize(envelopeBundle, snapshot,
                packagePath);

        try
        {
            OVFSerializer.getInstance().writeXML(envelopeBundle, bundleEnvelopeStream);
        }
        catch (XMLException e)
        {
            final String cause =
                String.format("The bundle [%s] of OVF [%s] can not be created "
                    + "because the bundle envelope can not be written to [%s]", snapshot, ovfId,
                    bundlePath);
            throw new RepositoryException(cause);
        }
        finally
        {
            try
            {
                bundleEnvelopeStream.close();
            }
            catch (IOException e)
            {
                final String cause = String.format("Can not close the stream to [%s]", bundlePath);
                logger.error(cause); // TODO throws ?
            }
        }// close envelope write stream

        return codifyBundleOVFId(ovfId, snapshotMark, packageName);
    }

    // // TODO dup
    // private String getRelativePackagePath(final String ovfid, final String idEnterprise)
    // {
    // return
    // getOVFPackagePath(ovfid).substring(BASE_REPO_PATH.length());//er.getEnterpriseRepositoryPath().length());
    // }

    public Long getUsedMb()
    {
        TimeoutFSUtils.getInstance().canUseRepository();

        return sizeOfDirectory(new File(enterpriseRepositoryPath)) / (1024 * 1024);
    }

    public static Long getCapacityMb()
    {
        TimeoutFSUtils.getInstance().canUseRepository();

        return new File(BASE_REPO_PATH).getTotalSpace() / (1024 * 1024);
    }

    public Long getFreeMb()
    {
        TimeoutFSUtils.getInstance().canUseRepository();

        return new File(BASE_REPO_PATH).getFreeSpace() / (1024 * 1024);
    }

    private Long sizeOfDirectory(final File f)
    {
        if (f.isFile())
        {
            return f.length();
        }
        else if (f.isDirectory() && f.listFiles().length != 0)
        {
            Long acum = 0l;
            for (File element : f.listFiles())
            {
                acum += sizeOfDirectory(element);
            }

            return acum;
        }
        else
        {
            return 0l;
        }
    }
    //
    // public String getRelativePackagePath(final String ovfid)
    // {
    //
    // final String packPath = super.getRelativePackagePath(ovfid);
    //
    // // // FIXME
    // // if (!new File(packPath).exists())
    // // {
    // // final String oldOvfid = cleanOVFurlForOldRepo(ovfid);
    // // return super.getRelativePackagePath(oldOvfid);
    // // }
    //
    // return packPath.substring(BASE_REPO_PATH.length());
    // }

}
