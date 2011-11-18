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

import static com.abiquo.am.services.OVFPackageConventions.OVF_BUNDLE_PATH_IDENTIFIER;
import static com.abiquo.am.services.OVFPackageConventions.OVF_LOCATION_PREFIX;
import static com.abiquo.am.services.OVFPackageConventions.codifyBundleOVFId;
import static com.abiquo.am.services.OVFPackageConventions.codifyEnterpriseRepositoryPath;
import static com.abiquo.am.services.OVFPackageConventions.createBundleOvfId;
import static com.abiquo.am.services.OVFPackageConventions.getBundleMasterOvfId;
import static com.abiquo.am.services.OVFPackageConventions.getBundleSnapshot;
import static com.abiquo.am.services.OVFPackageConventions.getMasterOVFPackage;
import static com.abiquo.am.services.OVFPackageConventions.getOVFPackageName;
import static com.abiquo.am.services.OVFPackageConventions.getOVFPackagePath;
import static com.abiquo.am.services.OVFPackageConventions.getRelativeOVFPath;
import static com.abiquo.am.services.OVFPackageConventions.isBundleOvfId;
import static com.abiquo.am.services.OVFPackageConventions.isImportedBundleOvfId;
import static com.abiquo.am.services.filesystem.EnterpriseRepositoryFileSystem.validateEnterpirseRepositoryPathFile;
import static com.abiquo.am.services.filesystem.OVFPackageInstanceFileSystem.getEnvelope;
import static com.abiquo.am.services.filesystem.OVFPackageInstanceFileSystem.getFileByPath;
import static com.abiquo.am.services.filesystem.OVFPackageInstanceFileSystem.writeOVFFileToOVFPackageDir;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;

import org.dmtf.schemas.ovf.envelope._1.EnvelopeType;
import org.dmtf.schemas.ovf.envelope._1.FileType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.abiquo.am.data.AMRedisDao;
import com.abiquo.am.exceptions.AMError;
import com.abiquo.am.services.filesystem.EnterpriseRepositoryFileSystem;
import com.abiquo.am.services.filesystem.OVFPackageInstanceFileSystem;
import com.abiquo.am.services.ovfformat.OVFPackageInstanceFromOVFEnvelope;
import com.abiquo.am.services.ovfformat.OVFPackageInstanceToOVFEnvelope;
import com.abiquo.appliancemanager.config.AMConfigurationManager;
import com.abiquo.appliancemanager.exceptions.AMException;
import com.abiquo.appliancemanager.transport.OVFPackageInstanceStateDto;
import com.abiquo.appliancemanager.transport.OVFStatusEnumType;
import com.abiquo.ovfmanager.ovf.OVFReferenceUtils;
import com.abiquo.ovfmanager.ovf.exceptions.IdNotFoundException;
import com.abiquo.ovfmanager.ovf.exceptions.XMLException;
import com.abiquo.ovfmanager.ovf.xml.OVFSerializer;

/**
 * Each enterprise have its own logical separation on the current physical Repository. This is
 * implemented using a folder (with the Enterprise identifier)
 */
public class EnterpriseRepositoryService
{
    private final static Logger LOG = LoggerFactory.getLogger(EnterpriseRepositoryService.class);

    private final static String BASE_REPO_PATH = AMConfigurationManager.getInstance()
        .getAMConfiguration().getRepositoryPath();

    /** Repository path particular of the current enterprise. */
    private final String erepoPath;

    private final String erId;

    /**
     * Created in {@link ErepoFactory}
     */
    protected EnterpriseRepositoryService(final String idEnterprise)
    {
        this.erId = idEnterprise;
        this.erepoPath = codifyEnterpriseRepositoryPath(BASE_REPO_PATH, idEnterprise);
        validateEnterpirseRepositoryPathFile(erepoPath);

        LOG.debug("Create repository index for {} ", erepoPath);
        List<OVFPackageInstanceStateDto> availables =
            EnterpriseRepositoryFileSystem.getAllOVF(erepoPath, false);

        AMRedisDao dao = AMRedisDao.getDao();
        dao.init(idEnterprise, availables);
        AMRedisDao.returnDao(dao);
    }

    public List<OVFPackageInstanceStateDto> getOVFStates()
    {
        final List<OVFPackageInstanceStateDto> states;

        AMRedisDao dao = AMRedisDao.getDao();
        states = dao.getAll(erId);
        AMRedisDao.returnDao(dao);

        return states;
    }

    /** ########## OVF DIRECT ACCESS ########## **/

    /**
     * Create the folder and write to it the OVF.
     * 
     * @return the OVF envelope document obtained form the OVF location.
     * @throws RepositoryException, if can not create any of the required folders on the Enterprise
     *             Repository.
     */
    public EnvelopeType createOVFPackageFolder(final String ovfId, final EnvelopeType envelope)
    {
        final String envelopePath = erepoPath + getRelativeOVFPath(ovfId); // XXX

        writeOVFFileToOVFPackageDir(envelopePath, envelope);

        return envelope;
    }

    public void createOVFPackageFolder(final String ovfId)
    {
        OVFPackageInstanceFileSystem.createOVFPackageFolder(erepoPath, ovfId);
    }

    /** Transfer the upload content into the repository file system */
    public void copyFileToOVFPackagePath(final String ovfid, final File file)
    {
        final String packagePAth = getOVFPackagePath(erepoPath, ovfid);

        OVFPackageInstanceFileSystem.copyFileToOVFPackagePath(packagePAth, file);
    }

    /***
     * @throws IdNotFoundException, if the OVF package do not exist or is not on DOWNLOAD state.
     */
    public void deleteOVF(final String ovfId)
    {
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

        OVFPackageInstanceStateDto state = getOVFStatus(ovfId);

        if (state.getStatus() == OVFStatusEnumType.NOT_DOWNLOAD)
        {
            // already deleted
            return;
        }

        final String packagePath = getOVFPackagePath(erepoPath, ovfId);

        OVFPackageInstanceFileSystem.deleteOVFPackage(packagePath);
    }

    public String path()
    {
        return erepoPath;
    }

    /** ########## DISC ########## */

    public String getDiskFilePath(final String ovfid)
    {

        EnvelopeType envelope = null;

        FileType file = null;

        envelope = getEnvelope(erepoPath, ovfid);

        final String diskFileId = OVFPackageInstanceFromOVFEnvelope.getDisk(envelope).getFileRef();

        try
        {
            file = OVFReferenceUtils.getReferencedFile(envelope, diskFileId);
        }
        catch (IdNotFoundException e)
        {
            throw new AMException(AMError.OVF_INVALID, "Disk id not found on the envelope");
        }

        return file.getHref();
    }

    public File getOVFDiskFile(final String ovfId)
    {
        boolean isBundle = isBundleOvfId(ovfId);

        final String ovfPath = erepoPath + getRelativeOVFPath(ovfId);

        String masterOvfId;

        if (!isBundle)
        {
            masterOvfId = ovfId;
        }
        else
        {
            masterOvfId = getBundleMasterOvfId(ovfId);
        }

        final String relPath = getDiskFilePath(masterOvfId);

        final String ovfFolder = ovfPath.substring(0, ovfPath.lastIndexOf('/'));
        String filePath = ovfFolder + '/' + relPath;

        if (isBundle)
        {
            final String snapshot = getBundleSnapshot(ovfId);
            filePath = createBundleOvfId(filePath, snapshot);
        }

        return getFileByPath(filePath);
    }

    /** ######## BOUNDLE RELATED FUNCTIONS ######## */

    public String prepareBundle(final String name)
    {

        final String ovfId = OVF_LOCATION_PREFIX + "bundle/" + name + '/' + name + ".ovf";

        final String packPath = getOVFPackagePath(erepoPath, ovfId);

        OVFPackageInstanceFileSystem.createOVFPackageFolder(packPath, ovfId);

        return ovfId;
    }

    public String createBundle(final String ovfId, final String snapshot,
        final EnvelopeType envelopeBundle)
    {
        final String masterOvf = getMasterOVFPackage(ovfId);
        final String packagePath = getOVFPackagePath(erepoPath, ovfId);
        // final String originalDiskPath = getDiskFilePath(masterOvf);
        final String packageName = getOVFPackageName(masterOvf);

        // write the new (bundle) OVF envelope with the file references changes
        final String snapshotMark =
            createBundleInFolder(packagePath, snapshot, packageName, envelopeBundle);

        return codifyBundleOVFId(ovfId, snapshotMark, packageName);
    }

    private String createBundleInFolder(final String packagePath, final String snapshot,
        final String packageName, EnvelopeType envelopeBundle)
    {

        final String snapshotMark = snapshot + OVF_BUNDLE_PATH_IDENTIFIER;
        final String bundlePath = packagePath + snapshotMark + packageName;

        File envelopeBundleFile = new File(bundlePath);
        if (envelopeBundleFile.exists())
        {
            throw new AMException(AMError.OVFPI_SNAPSHOT_ALREADY_EXIST, bundlePath);
        }

        FileOutputStream bundleEnvelopeStream = null;
        try
        {
            envelopeBundleFile.createNewFile();
            bundleEnvelopeStream = new FileOutputStream(envelopeBundleFile);
        }
        catch (Exception e1)
        {
            throw new AMException(AMError.OVFPI_SNAPSHOT_ALREADY_EXIST, bundlePath);
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
            throw new AMException(AMError.OVFPI_SNAPSHOT_CREATE, bundlePath, e);
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
                LOG.error(cause);
            }
        }// close envelope write stream

        return snapshotMark;
    }

    private void deleteBundle(final String ovfId)
    {
        OVFPackageInstanceFileSystem.deleteBundle(erepoPath, ovfId);
    }

    /** Imported bundles do not use the ''enterpriserepopath'' */
    private void deleteImportedBundle(final String ovfId)
    {
        OVFPackageInstanceFileSystem.deleteImportedBundle(BASE_REPO_PATH, ovfId);
    }

    /** ######## REPOSITORY FILESYSTEM USAGE INFO ######## */

    public Long getUsedMb()
    {
        return EnterpriseRepositoryFileSystem.getUsedMb(erepoPath);
    }

    public OVFPackageInstanceStateDto getOVFStatus(final String ovfId)
    {

        return OVFPackageInstanceFileSystem.getOVFStatus(erepoPath, ovfId);
    }

    public boolean isEnoughtSpaceOn(final Long expected)
    {
        return EnterpriseRepositoryFileSystem.isEnoughtSpaceOn(erepoPath, expected);
    }
}
