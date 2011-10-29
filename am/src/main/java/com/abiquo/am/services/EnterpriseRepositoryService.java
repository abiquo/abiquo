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

import static com.abiquo.am.services.EnterpriseRepositoryFileSystem.validateEnterpirseRepositoryPathFile;
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
import static com.abiquo.am.services.OVFPackageInstanceFileSystem.getEnvelope;
import static com.abiquo.am.services.OVFPackageInstanceFileSystem.getFileByPath;
import static com.abiquo.am.services.OVFPackageInstanceFileSystem.writeOVFFileToOVFPackageDir;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.dmtf.schemas.ovf.envelope._1.DiskSectionType;
import org.dmtf.schemas.ovf.envelope._1.EnvelopeType;
import org.dmtf.schemas.ovf.envelope._1.FileType;
import org.dmtf.schemas.ovf.envelope._1.VirtualDiskDescType;

import com.abiquo.am.exceptions.AMError;
import com.abiquo.am.services.util.TimeoutFSUtils;
import com.abiquo.api.service.DefaultApiService;
import com.abiquo.appliancemanager.config.AMConfigurationManager;
import com.abiquo.appliancemanager.exceptions.AMException;
import com.abiquo.appliancemanager.transport.OVFPackageInstanceStateDto;
import com.abiquo.appliancemanager.transport.OVFStatusEnumType;
import com.abiquo.ovfmanager.ovf.OVFEnvelopeUtils;
import com.abiquo.ovfmanager.ovf.OVFReferenceUtils;
import com.abiquo.ovfmanager.ovf.exceptions.IdNotFoundException;

/**
 * Each enterprise have its own logical separation on the current physical Repository. This is
 * implemented using a folder (with the Enterprise identifier)
 */
public class EnterpriseRepositoryService extends DefaultApiService
{
    private final static String BASE_REPO_PATH = AMConfigurationManager.getInstance()
        .getAMConfiguration().getRepositoryPath();

    /** Immutable singelton instances base on its Enterprise Identifier. */
    private static Map<String, EnterpriseRepositoryService> enterpriseHandlers =
        new HashMap<String, EnterpriseRepositoryService>();

    /** Repository path particular of the current enterprise. */
    private final String enterpriseRepositoryPath;

    private EnterpriseRepositoryService(final String idEnterprise)
    {
        enterpriseRepositoryPath = codifyEnterpriseRepositoryPath(BASE_REPO_PATH, idEnterprise);

        validateEnterpirseRepositoryPathFile(enterpriseRepositoryPath);
    }

    public String getEnterpriseRepositoryPath()
    {
        return enterpriseRepositoryPath;
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

    /***
     * @throws IdNotFoundException, if the OVF package do not exist or is not on DOWNLOAD state.
     */
    public void deleteOVF(final String ovfId)
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

        OVFPackageInstanceStateDto state = getOVFStatus(ovfId);

        if (state.getStatus() == OVFStatusEnumType.NOT_DOWNLOAD)
        {
            // already deleted
            return;
        }

        final String packagePath = getOVFPackagePath(enterpriseRepositoryPath, ovfId);

        OVFPackageInstanceFileSystem.deleteOVFPackage(packagePath);
    }

    public void deleteBundle(final String ovfId)
    {
        OVFPackageInstanceFileSystem.deleteBundle(enterpriseRepositoryPath, ovfId);
    }

    public String getDiskFilePath(final String ovfid)
    {

        EnvelopeType envelope = null;

        FileType file = null;

        envelope = getEnvelope(enterpriseRepositoryPath, ovfid);

        final String diskFileId = getDisk(envelope).getFileRef();

        try
        {
            file = OVFReferenceUtils.getReferencedFile(envelope, diskFileId);
        }
        catch (IdNotFoundException e)
        {
            addError(new AMException(AMError.OVF_INVALID, "Disk id not found on the envelope"));
            flushErrors();
        }

        return file.getHref();
    }

    private VirtualDiskDescType getDisk(EnvelopeType envelope)
    {
        DiskSectionType diskSection = null;
        try
        {
            diskSection = OVFEnvelopeUtils.getSection(envelope, DiskSectionType.class);
        }
        catch (Exception e)// SectionNotPresentException InvalidSectionException
        {
            addError(new AMException(AMError.OVF_INVALID, "missing DiskSection"));
            flushErrors();
        }

        List<VirtualDiskDescType> disks = diskSection.getDisk();

        if (disks == null || disks.isEmpty() || disks.size() != 1)
        {
            addError(new AMException(AMError.OVF_INVALID, "multiple Disk not supported"));
            flushErrors();
        }

        return disks.get(0);

    }

    public File getOVFDiskFile(final String ovfId)
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

        final EnvelopeType envelope = getEnvelope(enterpriseRepositoryPath, masterOvfId);
        final String fileId = getDisk(envelope).getFileRef();
        FileType refFile = null;

        try
        {
            refFile = OVFReferenceUtils.getReferencedFile(envelope, fileId);
        }
        catch (IdNotFoundException e)
        {
            addError(new AMException(AMError.OVF_INVALID, "file reference not found in OVF"));
            flushErrors();
        }

        final String relPath = refFile.getHref();
        final String ovfFolder = ovfPath.substring(0, ovfPath.lastIndexOf('/'));
        String filePath = ovfFolder + '/' + relPath;

        if (isBundle)
        {
            final String snapshot = getBundleSnapshot(ovfId);
            filePath = createBundleOvfId(filePath, snapshot);
        }

        return getFileByPath(filePath);
    }

    public String prepareBundle(final String name)
    {
        TimeoutFSUtils.getInstance().canUseRepository();

        final String ovfId = OVF_LOCATION_PREFIX + "bundle/" + name + '/' + name + ".ovf";

        final String packPath = getOVFPackagePath(enterpriseRepositoryPath, ovfId);

        OVFPackageInstanceFileSystem.createOVFPackageFolder(packPath, ovfId);

        return ovfId;
    }

    /**
     * Create the folder and write to it the OVF.
     * 
     * @return the OVF envelope document obtained form the OVF location.
     * @throws RepositoryException, if can not create any of the required folders on the Enterprise
     *             Repository.
     */
    public EnvelopeType createOVFPackageFolder(final String ovfId, final EnvelopeType envelope)
    {
        final String envelopePath = enterpriseRepositoryPath + getRelativeOVFPath(ovfId); // XXX

        writeOVFFileToOVFPackageDir(envelopePath, envelope);

        return envelope;
    }

    public void createOVFPackageFolder(final String ovfId)
    {
        TimeoutFSUtils.getInstance().canUseRepository();

        OVFPackageInstanceFileSystem.createOVFPackageFolder(enterpriseRepositoryPath, ovfId);
    }

    public void copyFileToOVFPackagePath(final String ovfid, final File file)
    {
        // Transfer the upload content into the repository file system
        final String packagePAth = getOVFPackagePath(enterpriseRepositoryPath, ovfid);

        OVFPackageInstanceFileSystem.copyFileToOVFPackagePath(packagePAth, file);
    }

    public String createBundle(final String ovfId, final String snapshot,
        EnvelopeType envelopeBundle)
    {
        TimeoutFSUtils.getInstance().canUseRepository();

        final String masterOvf = getMasterOVFPackage(ovfId);
        final String packagePath = getOVFPackagePath(enterpriseRepositoryPath, ovfId);
        // final String originalDiskPath = getDiskFilePath(masterOvf);
        final String packageName = getOVFPackageName(masterOvf);

        // write the new (bundle) OVF envelope with the file references changes
        final String snapshotMark =
            EnterpriseRepositoryFileSystem.createBundle(packagePath, snapshot, packageName,
                envelopeBundle);

        return codifyBundleOVFId(ovfId, snapshotMark, packageName);
    }

    /**
     * imported bundles do not use the ''enterpriserepopath''
     */
    public void deleteImportedBundle(final String ovfId)
    {
        OVFPackageInstanceFileSystem.deleteImportedBundle(BASE_REPO_PATH, ovfId);
    }

    public Long getUsedMb()
    {
        return EnterpriseRepositoryFileSystem.getUsedMb(enterpriseRepositoryPath);
    }

    public OVFPackageInstanceStateDto getOVFStatus(final String ovfId)
    {
 
        return OVFPackageInstanceFileSystem.getOVFStatus(enterpriseRepositoryPath, ovfId);
    }

    public boolean isEnoughtSpaceOn(final Long expected)
    {
        return EnterpriseRepositoryFileSystem.isEnoughtSpaceOn(enterpriseRepositoryPath, expected);
    }
}
