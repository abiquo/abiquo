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

package com.abiquo.am.services.download;

import static com.abiquo.am.services.TemplateConventions.getFileUrl;
import static com.abiquo.appliancemanager.config.AMConfiguration.HTTP_CONNECTION_TIMEOUT;
import static com.abiquo.appliancemanager.config.AMConfiguration.HTTP_IDLE_TIMEOUT;
import static com.abiquo.appliancemanager.config.AMConfiguration.HTTP_MAX_CONNECTIONS;
import static com.abiquo.appliancemanager.config.AMConfiguration.HTTP_REQUEST_TIMEOUT;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.dmtf.schemas.ovf.envelope._1.EnvelopeType;
import org.dmtf.schemas.ovf.envelope._1.FileType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.abiquo.am.exceptions.AMError;
import com.abiquo.am.services.EnterpriseRepositoryService;
import com.abiquo.am.services.ErepoFactory;
import com.abiquo.am.services.TemplateConventions;
import com.abiquo.am.services.notify.AMNotifier;
import com.abiquo.am.services.ovfformat.TemplateToOVFEnvelope;
import com.abiquo.appliancemanager.config.AMConfiguration;
import com.abiquo.appliancemanager.config.AMConfigurationManager;
import com.abiquo.appliancemanager.exceptions.AMException;
import com.abiquo.appliancemanager.exceptions.DownloadException;
import com.abiquo.appliancemanager.transport.TemplateDto;
import com.abiquo.appliancemanager.transport.TemplateStateDto;
import com.abiquo.appliancemanager.transport.TemplateStatusEnumType;
import com.ning.http.client.AsyncHttpClient;
import com.ning.http.client.AsyncHttpClientConfig;
import com.ning.http.client.ProxyServer;

/**
 * Take an OVF-Envelope document and download all its references into the internal repository
 * (exposed by an NFS-Servers), also copy the source OVF document changing the file references to
 * make it repository relative.
 * 
 * @author apuig
 */
@Component
public class TemplateDownloader
{
    private final static Logger LOG = LoggerFactory.getLogger(TemplateDownloader.class);

    /** used in the {@link DownloadingFile} constructor */
    @Autowired
    private AMNotifier notifier;

    private static AsyncHttpClientConfig clientConf = createHttpClientConf();

    private final AsyncHttpClient httpClient = new AsyncHttpClient(clientConf);

    private final Map<String, DownloadingFile> inprogress = new HashMap<String, DownloadingFile>();

    /**
     * Make the provided template available on the enterprise repository. Creates a new directory
     * for the provided template definition into the repository (using the OVF file name), inspect the
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
    public void deployTemplate(final String enterpriseId, final String ovfId,
        final EnvelopeType envelope)
    {
        LOG.debug("Deploy request [{}]", ovfId);

        try
        {
            final DownloadingFile DownloadingFile =
                createFileTransfers(ovfId, envelope, enterpriseId);
            httpClient.prepareGet(DownloadingFile.fileUrl).execute(DownloadingFile);

            inprogress.put(ovfId, DownloadingFile);
            purgeInprogress();
        }
        catch (IOException e)
        {
            throw new AMException(AMError.TEMPLATE_INSTALL, e);
        }
    }

    private void purgeInprogress()
    {
        for (String ovfid : inprogress.keySet())
        {
            if (inprogress.get(ovfid).done())
            {
                inprogress.remove(ovfid);
            }
        }
    }

    /**
     * @param envelope, the OVF envelope document.
     * @param enterprise, the current enterprise being deploying.
     */
    private DownloadingFile createFileTransfers(final String ovfId, final EnvelopeType envelope,
        final String enterpriseId)
    {
        // TODO prior validation
        if (envelope.getReferences().getFile().size() != 1)
        {
            throw new AMException(AMError.TEMPLATE_INVALID_MULTIPLE_FILES);
        }

        final EnterpriseRepositoryService enterpirseRepository = ErepoFactory.getRepo(enterpriseId);

        final FileType fileType = envelope.getReferences().getFile().get(0);

        final Long expectedBytes = fileType.getSize().longValue();
        if (!enterpirseRepository.isEnoughtSpaceOn(expectedBytes))
        {
            // note the expected bytes are from the OVF document, but for progress we use the
            // content-length header
            throw new AMException(AMError.REPO_NO_SPACE, String.format("Requested %s MB",
                String.valueOf((expectedBytes / 1048576))));
        }

        final String destinationPath =
            TemplateConventions.createFileInfo(enterpirseRepository.path(), fileType, ovfId);

        final String fileURL = getFileUrl(fileType.getHref(), ovfId);

        return new DownloadingFile(fileURL, destinationPath, enterpriseId, ovfId, notifier);
    }

    /**
     * XXX Block until the upload transfer ends. OVFId and EnterpriseId form the disk info
     * 
     * @return the OVFid of the just uploaded package
     * @throws IOException
     */
    public synchronized String uploadTemplate(final TemplateDto diskInfo,
        final File diskFile) throws IOException
    {

        final long idEnterprise = diskInfo.getEnterpriseRepositoryId();
        final String ovfId = diskInfo.getUrl(); // XXX

        EnterpriseRepositoryService enterpriseRepository =
            ErepoFactory.getRepo(String.valueOf(idEnterprise));

        // create and write the OVF Envelope
        EnvelopeType envelope =
            TemplateToOVFEnvelope.createOVFEnvelopeFromTemplate(diskInfo);

        enterpriseRepository.createTemplateFolder(ovfId);
        enterpriseRepository.createTemplateFolder(ovfId, envelope);
        enterpriseRepository.copyFileToOVFPackagePath(ovfId, diskFile);

        return ovfId;
    }

    /**
     * Cancel an active OVF package deployment (stopping download all its files).
     * 
     * @param ovfId, the OVF package identifier.
     * @throws RepositoryException if the package is not on DOWNLOADING state.
     */
    public synchronized void cancelDeployTemplate(final String ovfId, final String enterpriseId)
    {
        EnterpriseRepositoryService enterpriseRepository = ErepoFactory.getRepo(enterpriseId);

        final TemplateStateDto state = enterpriseRepository.getTemplateStatus(ovfId);
        final TemplateStatusEnumType status = state.getStatus();

        if (status == TemplateStatusEnumType.DOWNLOADING)
        {

            if (inprogress.containsKey(ovfId))
            {
                // also delete all the being download files.
                inprogress.get(ovfId).onCancel(true);
                inprogress.remove(ovfId);
            }
            else
            {
                throw new AMException(AMError.TEMPLATE_CANCEL, String.format(
                    "Provided OVF[%s] appears as DOWNLOADING but is not beeing deployed", ovfId));
            }
        }
        else
        {
            throw new AMException(AMError.TEMPLATE_CANCEL, String.format(
                "Provided OVF[%s] is not on DOWNLOADING state,"
                    + " its [%s]. So it can not be cancelled", ovfId, status.name()));
        }

        purgeInprogress();
    }

    private static AsyncHttpClientConfig createHttpClientConf()
    {
        AMConfiguration amconf = AMConfigurationManager.getInstance().getAMConfiguration();

        AsyncHttpClientConfig.Builder builder = new AsyncHttpClientConfig.Builder(). //
            setFollowRedirects(true).//
            setCompressionEnabled(true).//
            setIdleConnectionInPoolTimeoutInMs(HTTP_IDLE_TIMEOUT).//
            setConnectionTimeoutInMs(HTTP_CONNECTION_TIMEOUT).//
            setRequestTimeoutInMs(HTTP_REQUEST_TIMEOUT).//
            setMaximumConnectionsTotal(HTTP_MAX_CONNECTIONS);

        if (amconf.getProxyHost() != null && amconf.getProxyPort() != null)
        {
            LOG.info("Configure HTTP connections to use the proxy [{}] [{}]",
                amconf.getProxyHost(), amconf.getProxyPort());

            ProxyServer proxy = new ProxyServer(amconf.getProxyHost(), amconf.getProxyPort());
            builder = builder.setProxyServer(proxy);
        }

        return builder.build();
    }
}
