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

import static com.abiquo.am.services.TemplateConventions.OVF_BUNDLE_PATH_IDENTIFIER;
import static com.abiquo.am.services.TemplateConventions.createBundleOvfId;
import static com.abiquo.am.services.TemplateConventions.getBundleMasterOvfId;
import static com.abiquo.am.services.TemplateConventions.getBundleSnapshot;
import static com.abiquo.am.services.TemplateConventions.isBundleOvfId;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

import javax.ws.rs.core.Response;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang.StringUtils;
import org.dmtf.schemas.ovf.envelope._1.EnvelopeType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.abiquo.am.data.AMRedisDao;
import com.abiquo.am.services.download.OVFDocumentFetch;
import com.abiquo.am.services.download.TemplateDownloader;
import com.abiquo.am.services.filesystem.TemplateFileSystem;
import com.abiquo.am.services.notify.AMNotifier;
import com.abiquo.am.services.ovfformat.TemplateFromOVFEnvelope;
import com.abiquo.am.services.ovfformat.TemplateToOVFEnvelope;
import com.abiquo.appliancemanager.exceptions.DownloadException;
import com.abiquo.appliancemanager.exceptions.EventException;
import com.abiquo.appliancemanager.transport.TemplateDto;
import com.abiquo.appliancemanager.transport.TemplateStateDto;
import com.abiquo.appliancemanager.transport.TemplateStatusEnumType;

@Service
public class TemplateService
{

    @Autowired
    TemplateDownloader downloader;

    @Autowired
    AMNotifier notifier;

    @Autowired
    OVFDocumentFetch fetch;

    /** ######### POST ######### **/

    public void startDownload(final String erId, final String ovfId)
    {
        // first create the folder in order to allow the creation of ERROR marks.
        ErepoFactory.getRepo(erId).createTemplateFolder(ovfId);

        EnvelopeType envelope = fetch.obtainEnvelope(ovfId);
        // TODO check envelope is compatible

        ErepoFactory.getRepo(erId).createTemplateFolder(ovfId, envelope);

        downloader.deployTemplate(erId, ovfId, envelope);

        // sets the current state to start downloading
        notifier.setTemplateStatus(erId, ovfId, TemplateStatusEnumType.DOWNLOADING);
    }

    public void upload(final TemplateDto diskinfo, final File diskFile, final String errorMsg)
        throws IOException, EventException
    {

        if (!StringUtils.isBlank(errorMsg))
        {
            notifier.setTemplateStatusError(String.valueOf(diskinfo.getEnterpriseRepositoryId()),
                diskinfo.getUrl(), errorMsg);
        }

        downloader.uploadTemplate(diskinfo, diskFile);

        // sets the current state to start downloading
        notifier.setTemplateStatus(String.valueOf(diskinfo.getEnterpriseRepositoryId()),
            diskinfo.getUrl(), TemplateStatusEnumType.DOWNLOAD);
    }

    public void delete(final String erId, final String ovfId)
    {
        TemplateStatusEnumType status =
            ErepoFactory.getRepo(erId).getTemplateStatus(ovfId).getStatus();

        boolean requireNotifyError = true;
        switch (status)
        {
            case NOT_DOWNLOAD:
                return; // TODO status code

            case DOWNLOADING:

                try
                {
                    downloader.cancelDeployTemplate(ovfId, erId);
                    requireNotifyError = false;
                }
                catch (Exception e)
                {
                    e.printStackTrace();
                }

            default:
                ErepoFactory.getRepo(erId).deleteTemplate(ovfId);
                break;
        }

        if (!requireNotifyError)
        {
            return;
        }

        notifier.setTemplateStatus(erId, ovfId, TemplateStatusEnumType.NOT_DOWNLOAD);
    }

    /** ######### GET ######### **/

    public TemplateStateDto getTemplateStatusIncludeProgress(final String ovfId, final String erId)
        throws DownloadException
    {
        TemplateStateDto state = new TemplateStateDto();
        state.setOvfId(ovfId);

        AMRedisDao dao = AMRedisDao.getDao();
        try
        {
            TemplateStatusEnumType status = dao.getStatus(erId, ovfId);
            state.setStatus(status);
            switch (status)
            {
                case DOWNLOADING:
                    state.setDownloadingProgress((double) dao.getDownloadProgress(erId, ovfId));
                    break;

                case ERROR:
                    state.setErrorCause(dao.getError(erId, ovfId));
                default:
                    break;
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        finally
        {
            AMRedisDao.returnDao(dao);
        }

        return state;
    }

    public TemplateDto getTemplate(final String erId, final String ovfId)
    {
        final String erepoPath = ErepoFactory.getRepo(erId).path();

        if (!isBundleOvfId(ovfId))
        {
            EnvelopeType envelope = TemplateFileSystem.getEnvelope(erepoPath, ovfId);

            String relativePackagePath = TemplateConventions.getRelativePackagePath(ovfId);
            relativePackagePath = erId + '/' + relativePackagePath; // FIXME use EnterpriseRepo

            envelope = validateOrTryToFix(envelope, ovfId);

            TemplateDto packDto = TemplateFromOVFEnvelope.createTemplateDto(ovfId, envelope);
            packDto = fixFilePathWithRelativeTemplatePath(packDto, relativePackagePath);
            packDto.setEnterpriseRepositoryId(Integer.valueOf(erId));

            return packDto;
        }
        else
        {
            final String masterOvf = getBundleMasterOvfId(ovfId);
            final String snapshot = getBundleSnapshot(ovfId);

            TemplateDto packDto = getTemplate(erId, masterOvf);

            packDto.setName(snapshot + OVF_BUNDLE_PATH_IDENTIFIER + packDto.getName());

            final String masterDiskPath = packDto.getDiskFilePath();
            final String bundleDiskPath = createBundleOvfId(masterDiskPath, snapshot);

            packDto.setMasterDiskFilePath(masterDiskPath);
            packDto.setDiskFilePath(bundleDiskPath);
            packDto.setUrl(ovfId);
            // packDto.setDiskFileSize(diskFileSize); TODO change the disk size

            return packDto;
        }

    }

    /** if an ovf arrives in the repository (not using download) */
    public EnvelopeType validateOrTryToFix(final EnvelopeType envelope, final String ovfId)
    {
        try
        {
            fetch.checkEnvelopeIsValid(envelope);
            return envelope;
        }
        catch (Exception e)
        {
            return fetch.checkEnvelopeIsValid(fetch.fixOVfDocument(ovfId, envelope));
        }
    }

    private TemplateDto fixFilePathWithRelativeTemplatePath(final TemplateDto ovfpi,
        final String relativePackagePath)
    {
        String diskPath = ovfpi.getDiskFilePath();
        if (diskPath.startsWith("http://"))
        {
            diskPath = diskPath.substring(diskPath.lastIndexOf('/') + 1);
        }

        ovfpi.setDiskFilePath(FilenameUtils.concat(relativePackagePath, diskPath));

        return ovfpi;
    }

    /**
     * @throws RepositoryException, if some of the Disk files of the bundle do not exist on the
     *             repository.
     */
    public String createTemplateBundle(final TemplateDto diskInfo, final String snapshot)
    {

        final String erId = String.valueOf(diskInfo.getEnterpriseRepositoryId());
        final String ovfIdSnapshot = diskInfo.getUrl();

        final EnvelopeType envelopeBundle =
            TemplateToOVFEnvelope.createOVFEnvelopeFromTemplate(diskInfo);

        return ErepoFactory.getRepo(erId).createBundle(ovfIdSnapshot, snapshot, envelopeBundle);
    }

    // public OVFPackageInstanceDto createBunlde(final OVFPackageInstanceDto master,
    // final String snapshot)
    // {
    //
    // final String ovfId = master.getOvfId();
    // final String bundleOvfId =
    // ovfId.substring(0, ovfId.lastIndexOf('/') + 1) + snapshot + "-snapshot-"
    // + ovfId.substring(ovfId.lastIndexOf('/') + 1, ovfId.length());
    //
    // OVFPackageInstanceDto di = new OVFPackageInstanceDto();
    // di.setName(snapshot + master.getName());
    // di.setDescription("bundle of " + master.getDescription());
    //
    // di.setCpu(master.getCpu());
    // di.setHd(master.getHd());
    // di.setRam(master.getRam());
    // di.setHdSizeUnit(master.getHdSizeUnit());
    // di.setRamSizeUnit(master.getRamSizeUnit());
    //
    // di.setIconPath(master.getIconPath());
    // di.setDiskFileFormat(master.getDiskFileFormat());
    //
    // // di.setImageSize(121212); // XXX not use
    // // di.setDiskFilePath("XXXXXXXXX do not used XXXXXXXXXXX"); // XXX not use
    // di.setMasterDiskFilePath(master.getDiskFilePath());
    // di.setOvfId(bundleOvfId);
    //
    // di.setIdEnterprise(master.getIdEnterprise());
    // // di.setIdUser(2);
    // di.setCategoryName(master.getCategoryName());
    //
    // return di;
    // }

}
