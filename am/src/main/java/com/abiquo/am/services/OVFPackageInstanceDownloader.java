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
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import javax.annotation.Resource;

import org.dmtf.schemas.ovf.envelope._1.EnvelopeType;
import org.dmtf.schemas.ovf.envelope._1.FileType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.abiquo.am.exceptions.AMError;
import com.abiquo.am.fileserver.HttpClientOVFPackage;
import com.abiquo.am.fileserver.info.FileInfo;
import com.abiquo.am.fileserver.info.PackageInfo;
import com.abiquo.am.services.util.OVFPackageInstanceToOVFEnvelope;
import com.abiquo.appliancemanager.config.AMConfigurationManager;
import com.abiquo.appliancemanager.exceptions.AMException;
import com.abiquo.appliancemanager.exceptions.DownloadException;
import com.abiquo.appliancemanager.transport.OVFPackageInstanceDto;
import com.abiquo.appliancemanager.transport.OVFPackageInstanceStateDto;
import com.abiquo.appliancemanager.transport.OVFStatusEnumType;

/**
 * Take an OVF-Envelope document and download all its references into the internal repository
 * (exposed by an NFS-Servers), also copy the source OVF document changing the file references to
 * make it repository relative.
 * 
 * @author apuig
 */
@Component(value = "ovfPackageInstanceDownloader")
public class OVFPackageInstanceDownloader
{
    /** The constant logger object. */
    private final static Logger log = LoggerFactory.getLogger(OVFPackageInstanceDownloader.class);

    /** All the being transfered OVF packages indexed by its download URL. */
    private Map<String, PackageInfo> htCurrentTransfers =
        new ConcurrentHashMap<String, PackageInfo>();

    /** Non-blocking HTTP downloads. */
    // @Autowired
    static HttpClientOVFPackage httpClient;

    @Resource(name = "httpClientOVFPackage")
    public void setHttpClient(HttpClientOVFPackage httpClient)
    {
        this.httpClient = httpClient;
    }

    /**
     * Make the provided OVF package available on the enterprise repository. Creates a new directory
     * for the provided OVFPackage into the repository (using the OVF file name), inspect the
     * OVF-Envelope to download all its File References into its package folder. Also change the
     * envelope to ensure use relative paths on the File ''href''. Sets the OVFState to DOWNLOADING
     * on the OVFIndex.
     * 
     * @param ovfid, URL where the OVF can be downloaded.
     * @param enterpirseId, the enterprise requesting its availability.
     * @throws RepositoryException, it the ovfPackageLocation can not be reached or its
     * @throws DownloadException, content is not a valid OVF envelope document or any error during
     *             the download of some file on the package.
     */
    public synchronized void deployOVFPackage(final String enterpriseId, final String ovfId,
        EnvelopeType envelope)
    {

        // TODO check it was not already deployed!

        log.debug("Deloying OVF package from [{}]", ovfId);

        EnterpriseRepositoryService erepo = EnterpriseRepositoryService.getRepo(enterpriseId);

        // // Creates the DiskInfo
        // List<DiskInfo> disks =
        // DiskInfoFromOVF.getDiskInfo(ovfId, envelope, index.getBaseRepositoryPath(),
        // enterpriseId);

        erepo.createOVFPackageFolder(ovfId, envelope);

        PackageInfo packa;
        try
        {
            packa = createFileTransfers(ovfId, envelope, enterpriseId);
        }
        catch (MalformedURLException e)
        {
            throw new DownloadException(ovfId, e);
        }

        htCurrentTransfers.put(ovfId, packa);

        httpClient.addDownload(packa);
    }

    /**
     * Cancel an active OVF package deployment (stopping download all its files).
     * 
     * @param ovfId, the OVF package identifier.
     * @throws RepositoryException if the package is not on DOWNLOADING state.
     */
    public synchronized void cancelDeployOVFPackage(final String ovfId, String enterpriseId)
    {
        EnterpriseRepositoryService enterpriseRepository =
            EnterpriseRepositoryService.getRepo(enterpriseId);

        final OVFPackageInstanceStateDto state = enterpriseRepository.getOVFStatus(ovfId);
        final OVFStatusEnumType status = state.getStatus();

        if (status == OVFStatusEnumType.DOWNLOADING)
        {

            if (htCurrentTransfers.containsKey(ovfId))
            {
                // also delete all the being download files.
                htCurrentTransfers.get(ovfId).cancelDownload(true);

            }
            else
            {
                throw new AMException(AMError.OVF_CANCEL, String.format(
                    "Provided OVF[%s] appears as DOWNLOADING but is not beeing deployed", ovfId));
            }
        }
        else
        {
            throw new AMException(AMError.OVF_CANCEL, String.format(
                "Provided OVF[%s] is not on DOWNLOADING state,"
                    + " its [%s]. So it can not be cancelled", ovfId, status.name()));
        }
    }

    /**
     * Creates a PackageInfo object containing as FileInfo objects as files on the envelope's
     * References section.
     * 
     * @param ovfId, the OVF identifier.
     * @param envelope, the OVF envelope document.
     * @param enterprise, the current enterprise being deploying.
     * @throws DownloadException,
     * @throws RepositoryException, if there isn't enough free space to deploy all its referenced
     *             files.
     * @throws MalformedURLException
     */
    private PackageInfo createFileTransfers(final String ovfId, final EnvelopeType envelope,
        final String enterpriseId) throws MalformedURLException
    {
        PackageInfo packa = new PackageInfo(ovfId, enterpriseId, htCurrentTransfers);

        EnterpriseRepositoryService enterpirseRepository =
            EnterpriseRepositoryService.getRepo(enterpriseId);

        for (FileType fileType : envelope.getReferences().getFile())
        {
            final String destinationPath =
                OVFPackageConventions.createFileInfo(
                    enterpirseRepository.getEnterpriseRepositoryPath(), fileType, ovfId);

            /**
             * TODO change the envelope in order to set the package relative path (if href is http)
             */

            final URL fileURL = getFileUrl(fileType.getHref(), ovfId);

            FileInfo fileInf =
                new FileInfo(fileURL.toString(), fileType.getSize().longValue(), destinationPath);

            packa.addFileInfo(fileInf);
        }// for each fileRef

        final Long allExpectedSize = packa.getAllExpectedSize();

        if (!enterpirseRepository.isEnoughtSpaceOn(allExpectedSize))
        {
            final String repoLoc =
                AMConfigurationManager.getInstance().getAMConfiguration().getRepositoryLocation();
            final Long expectedMb = allExpectedSize / 1048576;

            throw new AMException(AMError.REPO_NO_SPACE, String.format(
                "There is not enough free space on [%s] to download "
                    + "the OVF [%s], it requires [%s]Mb", repoLoc, ovfId, expectedMb.toString()));
        }

        return packa;
    }

    /**
     * Gets an URL from the hRef attribute on a File's (References section). Try to interpret the
     * hRef attribute as an absolute URL (like http://some.where.com/file.vmdk), and if it fails try
     * to interpret the hRef as OVF package URI relative.
     * 
     * @param relativeFilePath, the value on the hRef attribute for the File.
     * @param ovfId, the OVF Package identifier (and locator).
     * @throws DownloadException, it the URL can not be created.
     */
    private URL getFileUrl(String relativeFilePath, String ovfId) throws DownloadException
    {
        URL fileURL;

        try
        {
            fileURL = new URL(relativeFilePath);
        }
        catch (MalformedURLException e1)
        {
            // its a relative path
            if (e1.getMessage().startsWith("no protocol"))
            {
                try
                {
                    String packageURL = ovfId.substring(0, ovfId.lastIndexOf('/'));
                    fileURL = new URL(packageURL + '/' + relativeFilePath);
                }
                catch (MalformedURLException e)
                {
                    final String msg = "Invalid file reference " + ovfId + '/' + relativeFilePath;
                    throw new DownloadException(msg, e1);
                }
            }
            else
            {
                final String msg = "Invalid file reference " + relativeFilePath;
                throw new DownloadException(msg, e1);
            }
        }

        log.debug("Using repository location relative file [{}]", fileURL.toExternalForm());

        return fileURL;
    }

    /**
     * XXX Block until the upload transfer ends. OVFId and EnterpriseId form the disk info
     * 
     * @return the OVFid of the just uploaded package
     * @throws IOException
     */
    public synchronized String uploadOVFPackage(OVFPackageInstanceDto diskInfo, File diskFile)
        throws IOException
    {

        final long idEnterprise = diskInfo.getIdEnterprise();
        final String ovfId = diskInfo.getOvfId(); // XXX

        EnterpriseRepositoryService enterpriseRepository =
            EnterpriseRepositoryService.getRepo(String.valueOf(idEnterprise));

        // create and write the OVF Envelope
        EnvelopeType envelope =
            OVFPackageInstanceToOVFEnvelope.createEnvelopeFromOVFPackageInstance(diskInfo);

        enterpriseRepository.createOVFPackageFolder(ovfId);
        enterpriseRepository.createOVFPackageFolder(ovfId, envelope);
        enterpriseRepository.copyFileToOVFPackagePath(ovfId, diskFile);

        return ovfId;
    }// up

    public synchronized  Double getDownloadProgress(final String ovfId) throws DownloadException
    {
        if (htCurrentTransfers.containsKey(ovfId))
        {
            return htCurrentTransfers.get(ovfId).getProgress();

        }
        else
        {
            return 100.0; // just downloaded
            
        }
    }
}
