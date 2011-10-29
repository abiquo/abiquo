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

import static com.abiquo.am.services.OVFPackageConventions.END_OF_FILE_MARK;
import static com.abiquo.am.services.OVFPackageConventions.FORMATS_PATH;
import static com.abiquo.am.services.OVFPackageConventions.OVF_BUNDLE_IMPORTED_PREFIX;
import static com.abiquo.am.services.OVFPackageConventions.OVF_STATUS_DOWNLOADING_MARK;
import static com.abiquo.am.services.OVFPackageConventions.OVF_STATUS_ERROR_MARK;
import static com.abiquo.am.services.OVFPackageConventions.cleanOVFurlForOldRepo;
import static com.abiquo.am.services.OVFPackageConventions.createBundleOvfId;
import static com.abiquo.am.services.OVFPackageConventions.getBundleMasterOvfId;
import static com.abiquo.am.services.OVFPackageConventions.getBundleSnapshot;
import static com.abiquo.am.services.OVFPackageConventions.getOVFPackagePath;
import static com.abiquo.am.services.OVFPackageConventions.getRelativeOVFPath;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Scanner;
import java.util.Set;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.dmtf.schemas.ovf.envelope._1.EnvelopeType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.abiquo.am.exceptions.AMError;
import com.abiquo.am.services.util.BundleImageFileFilter;
import com.abiquo.am.services.util.FormatsFilter;
import com.abiquo.am.services.util.TimeoutFSUtils;
import com.abiquo.appliancemanager.exceptions.AMException;
import com.abiquo.appliancemanager.transport.OVFPackageInstanceStateDto;
import com.abiquo.appliancemanager.transport.OVFStatusEnumType;
import com.abiquo.ovfmanager.ovf.OVFReferenceUtils;
import com.abiquo.ovfmanager.ovf.xml.OVFSerializer;

public class OVFPackageInstanceFileSystem
{

    private final static Logger LOG = LoggerFactory.getLogger(OVFPackageInstanceFileSystem.class);

    /**
     * if (diskId == null || diskId.length() == 0) { }// remove all the package
     */
    public static EnvelopeType getEnvelope(final String enterpriseRepositoryPath, final String ovfId)
    {
        TimeoutFSUtils.getInstance().canUseRepository();

        EnvelopeType envelope;

        String ovfPath = enterpriseRepositoryPath + getRelativeOVFPath(ovfId);
        File ovfFile = new File(ovfPath);

        if (!ovfFile.exists())
        {
            // for pre 1.6 repository file system structure
            final String oldOvfId = cleanOVFurlForOldRepo(ovfId);

            if (!oldOvfId.equalsIgnoreCase(ovfId))
            {
                return getEnvelope(enterpriseRepositoryPath, oldOvfId);
            }
            else
            {
                throw new AMException(AMError.OVF_NOT_FOUND, ovfId);
            }
        }

        FileInputStream fileIs = null;
        try
        {
            fileIs = new FileInputStream(ovfFile);
            envelope = OVFSerializer.getInstance().readXMLEnvelope(fileIs);
        }
        catch (Exception e)
        {
            throw new AMException(AMError.OVF_MALFORMED, ovfId, e);
        }
        finally
        {
            try
            {
                if (fileIs != null)
                {
                    fileIs.close();
                }
            }
            catch (IOException e)
            {
                e.printStackTrace();
            }
        }

        return envelope;
    }

    public static OVFPackageInstanceStateDto getOVFStatus(final String enterpriseRepositoryPath,
        final String ovfId)
    {
        final OVFPackageInstanceStateDto state = new OVFPackageInstanceStateDto();
        state.setOvfId(ovfId);

        if (!OVFPackageConventions.isValidOVFLocation(ovfId))
        {
            state.setStatus(OVFStatusEnumType.ERROR);
            state.setErrorCause(AMError.OVF_INVALID_LOCATION.toString());
            return state;
        }

        TimeoutFSUtils.getInstance().canUseRepository();

        final String packagePath = getOVFPackagePath(enterpriseRepositoryPath, ovfId);
        final String ovfEnvelopePath =
            FilenameUtils.concat(enterpriseRepositoryPath, getRelativeOVFPath(ovfId));

        File errorMarkFile = new File(packagePath + OVF_STATUS_ERROR_MARK);
        if (errorMarkFile.exists())
        {
            state.setStatus(OVFStatusEnumType.ERROR);
            state.setErrorCause(readErrorMarkFile(errorMarkFile));
        }
        else if (new File(packagePath + OVF_STATUS_DOWNLOADING_MARK).exists())
        {
            state.setStatus(OVFStatusEnumType.DOWNLOADING);

        }
        else if (!new File(ovfEnvelopePath).exists())
        {
            state.setStatus(OVFStatusEnumType.NOT_DOWNLOAD);
        }
        else
        {
            state.setStatus(OVFStatusEnumType.DOWNLOAD);
        }

        return state;
    }

    /**
     * synch to avoid multiple changes on the package folder (before 'clearOVFStatusMarks').
     * 
     * @throws RepositoryException
     */
    public static synchronized void createOVFStatusMarks(final String enterpriseRepositoryPath,
        final String ovfId, final OVFStatusEnumType status, final String errorMsg)
    {
        final String packagePath = getOVFPackagePath(enterpriseRepositoryPath, ovfId);

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
                    throw new AMException(AMError.OVFPI_UNKNOW_STATUS, status.name());

            }// switch

        }
        catch (IOException ioe)
        {
            throw new AMException(AMError.OVFPI_CHANGE_STATUS, mark.getAbsoluteFile()
                .getAbsolutePath());
        }

        if (errorCreate)
        {
            throw new AMException(AMError.OVFPI_CHANGE_STATUS, mark.getAbsoluteFile()
                .getAbsolutePath());
        }
    }

    private static void clearOVFStatusMarks(final String packagePath)
    {
        File errorMarkFile = new File(packagePath + OVF_STATUS_ERROR_MARK);
        File downloadingMarkFile = new File(packagePath + OVF_STATUS_DOWNLOADING_MARK);

        if (errorMarkFile.exists())
        {
            if (!errorMarkFile.delete())
            {
                throw new AMException(AMError.OVFPI_CHANGE_STATUS, "removing "
                    + errorMarkFile.getAbsolutePath());
            }
        }

        if (downloadingMarkFile.exists())
        {
            if (!downloadingMarkFile.delete())
            {
                throw new AMException(AMError.OVFPI_CHANGE_STATUS, "removing "
                    + downloadingMarkFile.getAbsolutePath());
            }
        }
    }

    private static String readErrorMarkFile(final File errorMarkFile)
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

    public static File getFileByPath(final String filePath)
    {
        File f = new File(filePath);
        if (!f.exists())
        {
            throw new AMException(AMError.DISK_FILE_NOT_FOUND, filePath);
        }

        return f;
    }

    public static void copyFileToOVFPackagePath(final String packagePath, final File file)
    {
        // Transfer the upload content into the repository file system
        final String filePath = packagePath + file.getName();

        File f = new File(filePath);
        // TODO check do not exist and can be created

        try
        {
            FileUtils.moveFile(file, f);
        }
        catch (IOException e)
        {
            throw new AMException(AMError.DISK_FILE_MOVE, packagePath, e);

        }
    }

    private static void createOVFPackageFormatsFolder(final String packagePath)
    {

        final String formatsPath = packagePath + '/' + FORMATS_PATH;

        File formatsFolder = new File(formatsPath);

        if (!formatsFolder.exists())
        // !! XXX assume if the enterpriseRepositoryPath can be written then also all the package
        // and formats path.
        {
            if (!formatsFolder.mkdir())
            {
                throw new AMException(AMError.OVF_INSTALL, "creating format folder");

            }
        }
    }

    public static void createOVFPackageFolder(final String enterpriseRepositoryPath,
        final String ovfId)
    {

        final String packagePath = getOVFPackagePath(enterpriseRepositoryPath, ovfId);
        // final String packageName = repository.getOVFPackageName(ovfId);
        // should be equals to concatenation

        // just create the folder
        File packFile = new File(packagePath);

        if (!packFile.exists())
        {
            if (!packFile.mkdirs())
            {
                throw new AMException(AMError.OVF_INSTALL, packagePath);

            }
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
    public static void writeOVFFileToOVFPackageDir(final String envelopePath,
        final EnvelopeType envelope)
    {

        File envelopeFile = new File(envelopePath);

        if (envelopeFile.exists())
        {
            throw new AMException(AMError.OVF_INSTALL, "already exist");
        }

        try
        {
            if (!envelopeFile.createNewFile())
            {
                throw new AMException(AMError.OVF_INSTALL, envelopePath);
            }
        }
        catch (IOException e)
        {
            throw new AMException(AMError.OVF_INSTALL, envelopePath);
        }

        FileOutputStream envelopeStream = null;
        try
        {
            envelopeStream = new FileOutputStream(envelopeFile);
            OVFSerializer.getInstance().writeXML(envelope, envelopeStream);
        }
        catch (Exception e1) // IOException or XMLException
        {
            throw new AMException(AMError.OVF_INSTALL, envelopePath, e1);
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
                    throw new AMException(AMError.OVF_INSTALL, envelopePath);
                }
            }
        }// finally
    }

    public static void deleteBundleConversion(final String packagePath, final String fileName)
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
                LOG.error(cause);
            }
        }
    }

    public static void deleteOVFPackage(final String packagePath)
    {
        File packageFile = new File(packagePath);
        String[] bundles = packageFile.list(new BundleImageFileFilter());
        if (bundles == null || bundles.length == 0)
        {
            LOG.debug("There are any bundle, deleting all the folder");
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
                        throw new AMException(AMError.OVFPI_DELETE,
                            packageFile.getAbsolutePath(),
                            e1);
                    }
                }// nfs issue

            }
            return;
        }
        else
        {
            final StringBuffer stBuffer = new StringBuffer();
            stBuffer.append("The selected OVF Package Instance has snapshot instances associates:");
            for (String bund : bundles)
            {
                stBuffer.append("\n" + bund);
            }

            stBuffer.append("\n It can not be deleted.");

            throw new AMException(AMError.OVFPI_DELETE_INSTANCES, stBuffer.toString());
        }
    }

    /**
     * imported bundles do not use the ''enterpriserepopath''
     */
    public static void deleteImportedBundle(final String BASE_REPO_PATH, final String ovfId)
    {
        final String path =
            ovfId.substring(OVF_BUNDLE_IMPORTED_PREFIX.length(), ovfId.lastIndexOf('/'));
        final String absPath = BASE_REPO_PATH + path;

        File importBundleDir = new File(absPath);

        if (!importBundleDir.exists() || !importBundleDir.isDirectory())
        {
            throw new AMException(AMError.OVFPI_SNAPSHOT_IMPORT_NOT_EXIST, ovfId);
        }

        try
        {
            FileUtils.deleteDirectory(importBundleDir);
        }
        catch (IOException e)
        {
            LOG.error("Can not delete the bundle of an imported virtual machine, on folder {}",
                absPath);
        }
    }

    public static void deleteBundle(final String enterpriseRepositoryPath, final String ovfId)
    {
        final String masterOvf = getBundleMasterOvfId(ovfId);
        final String snapshot = getBundleSnapshot(ovfId);

        final String packagePath = getOVFPackagePath(enterpriseRepositoryPath, ovfId);
        final EnvelopeType envelope = getEnvelope(enterpriseRepositoryPath, masterOvf);
        Set<String> fileLocations = OVFReferenceUtils.getAllReferencedFileLocations(envelope);

        for (String fileLocation : fileLocations)
        {
            final String absoultePath = packagePath + fileLocation;
            final String bundleAbsoultePath = createBundleOvfId(absoultePath, snapshot);

            File file = new File(bundleAbsoultePath);

            if (!file.exists())
            {
                LOG.warn("Path [{}] not exist, try to remove files starting with [{}]",
                    absoultePath, snapshot);

                String[] filesStarting = new File(packagePath).list(new FormatsFilter(snapshot));

                if (filesStarting != null)
                {
                    for (String fileStart : filesStarting)
                    {
                        File fileStartFile = new File(packagePath + fileStart);
                        if (!fileStartFile.delete())
                        {
                            LOG.error("Try to remove the path [{}] but is not possible", fileStart);
                        }
                    }
                }
            }
            else
            {
                if (!file.delete())
                {
                    LOG.error("Try to remove the path [{}] but is not possible", absoultePath);
                }
            }

            final String relativePath =
                bundleAbsoultePath.substring(bundleAbsoultePath.lastIndexOf('/') + 1);
            deleteBundleConversion(packagePath, relativePath);
        }// all files
    }
}
