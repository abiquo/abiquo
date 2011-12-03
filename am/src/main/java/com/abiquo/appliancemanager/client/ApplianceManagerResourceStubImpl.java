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
package com.abiquo.appliancemanager.client;

import java.io.File;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.apache.wink.client.ClientResponse;
import org.apache.wink.client.Resource;
import org.apache.wink.common.internal.ResponseImpl.ResponseBuilderImpl;
import org.dmtf.schemas.ovf.envelope._1.EnvelopeType;

import com.abiquo.appliancemanager.transport.RepositoryConfigurationDto;
import com.abiquo.appliancemanager.transport.EnterpriseRepositoryDto;
import com.abiquo.appliancemanager.transport.OVFPackageInstanceDto;
import com.abiquo.appliancemanager.transport.OVFPackageInstanceStateDto;
import com.abiquo.appliancemanager.transport.OVFPackageInstancesStateDto;
import com.abiquo.appliancemanager.transport.OVFStatusEnumType;

//@Service
// @Transactional
public class ApplianceManagerResourceStubImpl extends ApplianceManagerResourceStub
{

    public class ApplianceManagerStubException extends RuntimeException
    {
        public ApplianceManagerStubException(final String statusMsg)
        {
            super(statusMsg);
        }
    }

    public final static String MEDIA_TYPE = MediaType.APPLICATION_XML;

    public final static String FORAMT = "format";

    public ApplianceManagerResourceStubImpl(final String baseUrl)
    {
        super(baseUrl);
    }

    private void checkResponse(final ClientResponse response)
    {
        final Integer httpStatus = response.getStatusCode();
        if (httpStatus / 200 != 1)
        {
            String cause = null;
            try
            {
                cause = response.getEntity(String.class);
            }
            catch (Exception e)
            {
                cause = response.getMessage();

            }

            throw new ApplianceManagerStubException(String.format("%d - %s\n %s", httpStatus,
                response.getMessage(), cause));
        }
    }

    // GET
    public OVFPackageInstanceDto getOVFPackageInstance(final String idEnterprise, final String ovfId)
    {
        Resource resource = ovfPackage(idEnterprise, ovfId);

        ClientResponse response = resource.accept(MEDIA_TYPE).get();
        // default queryParam(FORAMT, "ovfpi")

        checkResponse(response);

        return response.getEntity(OVFPackageInstanceDto.class);
    }

    public OVFPackageInstanceStateDto getOVFPackageInstanceStatus(final String idEnterprise,
        final String ovfId)
    {
        Resource resource = ovfPackage(idEnterprise, ovfId);

        ClientResponse response = resource.accept(MEDIA_TYPE).queryParam(FORAMT, "status").get();

        checkResponse(response);

        return response.getEntity(OVFPackageInstanceStateDto.class);
    }

    public EnvelopeType getOVFPackageInstanceEnvelope(final String idEnterprise, final String ovfId)
    {
        Resource resource = ovfPackage(idEnterprise, ovfId);

        ClientResponse response = resource.accept(MEDIA_TYPE).queryParam(FORAMT, "envelope").get();

        checkResponse(response);

        return response.getEntity(EnvelopeType.class);
    }

    @Deprecated
    File getOVFPackageInstanceDiskFie(final String idEnterprise, final String ovfId)
        throws ApplianceManagerStubException
    {
        Resource resource = ovfPackage(idEnterprise, ovfId);

        ClientResponse response =
            resource.accept(MediaType.APPLICATION_OCTET_STREAM).queryParam(FORAMT, "envelope")
                .get();

        checkResponse(response);

        if (true)
            throw new RuntimeException("wink client can get a file ??"); // ja ja
        return response.getEntity(File.class);
    }

    public OVFPackageInstancesStateDto getOVFPackagInstanceStatusList(final String idEnterprise)
    {
        Resource resource = ovfPackagesTimeout(idEnterprise);

        ClientResponse response = resource.accept(MEDIA_TYPE).get();

        checkResponse(response);

        return response.getEntity(OVFPackageInstancesStateDto.class);
    }

    public RepositoryConfigurationDto getRepositoryConfiguration()
    {
        Resource resource = repositories();

        ClientResponse response = resource.accept(MEDIA_TYPE).get();

        checkResponse(response);

        return response.getEntity(RepositoryConfigurationDto.class);
    }

    public void checkService() throws ApplianceManagerStubException
    {
        Resource resource = check();

        ClientResponse response = resource.accept(MEDIA_TYPE).get();

        checkResponse(response);
    }

    public EnterpriseRepositoryDto getRepository(final String idEnterprise)

    {
        return getRepository(idEnterprise, false);
    }

    public EnterpriseRepositoryDto getRepository(final String idEnterprise,
        final boolean checkCanWrite)

    {
        Resource resource = repository(idEnterprise, checkCanWrite);

        ClientResponse response = resource.accept(MEDIA_TYPE).get();

        checkResponse(response);

        return response.getEntity(EnterpriseRepositoryDto.class);
    }

    public void delete(final String idEnterprise, final String ovfId)
    {
        Resource resource = ovfPackage(idEnterprise, ovfId);

        ClientResponse response = resource.accept(MEDIA_TYPE).delete();

        checkResponse(response);
    }

    /**
     * start download
     */
    public void createOVFPackageInstance(final String idEnterprise, final String ovfId)
    {
        Resource resource = ovfPackages(idEnterprise);

        // contentType(mediaType)
        ClientResponse response =
            resource.accept(MEDIA_TYPE).contentType(MediaType.TEXT_PLAIN).post(ovfId);

        checkResponse(response);

    }

    public String preBundleOVFPackage(final String idEnterprise, final String name)
    {
        Resource resource = ovfPackage(idEnterprise, "prebundle.ovf"); // FIXME

        // contentType(mediaType)
        ClientResponse response =
            resource.accept(MediaType.TEXT_PLAIN).contentType(MediaType.TEXT_PLAIN).post(name);

        checkResponse(response);

        return response.getEntity(String.class);
    }

    /**
     * ovfId alos on diskInfo
     * 
     * @throws ApplianceManagerStubException
     */
    public String bundleOVFPackage(final String idEnterprise, final String snapshot,
        final OVFPackageInstanceDto diskInfo)
    {

        Resource resource = ovfPackage(idEnterprise, snapshot);

        // contentType(mediaType)
        ClientResponse response =
            resource.accept(MediaType.TEXT_PLAIN).contentType(MEDIA_TYPE).post(diskInfo);

        checkResponse(response);

        return response.getEntity(String.class);
    }

    /**
     * Current status, eval if uploading.
     * 
     * @param idsOvfpackageIn Name of the item to refresh.
     * @param idEnterprise Id of Enterprise to which this {@link OVFPackage} belongs.
     * @return OVFPackageInstanceStatusDto
     */
    public OVFPackageInstanceStateDto getTemplateStatus(final String idEnterprise,
        final String ovfId)
    {
        Resource resource = ovfPackage(idEnterprise, ovfId);

        ClientResponse response = resource.accept(MEDIA_TYPE).queryParam(FORAMT, "status").get();

        final int httpStatus = response.getStatusCode();
        if (httpStatus == 200)
        {
            return response.getEntity(OVFPackageInstanceStateDto.class);
        }
        if (httpStatus == 404)
        {
            return uploading(ovfId);
        }

        checkErrorStatusResponse(response, httpStatus);

        return response.getEntity(OVFPackageInstanceStateDto.class);
    }

    /**
     * This {@link OVFPackage} is uploading.
     * 
     * @param ovfId id {@link OVFPackage}.
     * @return OVFPackageInstanceStatusDto
     */
    private OVFPackageInstanceStateDto uploading(final String ovfId)
    {
        OVFPackageInstanceStateDto statusUploading = new OVFPackageInstanceStateDto();
        statusUploading.setOvfId(ovfId);
        statusUploading.setDownloadingProgress(0d);
        statusUploading.setStatus(OVFStatusEnumType.DOWNLOAD);
        return statusUploading;
    }

    /**
     * Returns the proper error.
     * 
     * @param response response.
     * @param httpStatus code.
     */
    private void checkErrorStatusResponse(final ClientResponse response, Integer httpStatus)
    {
        String cause = null;
        try
        {
            cause = response.getEntity(String.class);
        }
        catch (Exception e)
        {
            cause = response.getMessage();

        }

        throw new ApplianceManagerStubException(String.format("%d - %s\n %s", httpStatus,
            response.getMessage(), cause));
    }
}
