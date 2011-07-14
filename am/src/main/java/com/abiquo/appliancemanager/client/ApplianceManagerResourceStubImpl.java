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

import org.apache.wink.client.ClientResponse;
import org.apache.wink.client.Resource;
import org.dmtf.schemas.ovf.envelope._1.EnvelopeType;

import com.abiquo.appliancemanager.transport.AMConfigurationDto;
import com.abiquo.appliancemanager.transport.EnterpriseRepositoryDto;
import com.abiquo.appliancemanager.transport.OVFPackageInstanceDto;
import com.abiquo.appliancemanager.transport.OVFPackageInstanceStatusDto;
import com.abiquo.appliancemanager.transport.OVFPackageInstanceStatusListDto;
import com.abiquo.appliancemanager.transport.OVFPackageInstanceStatusType;

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

    public OVFPackageInstanceStatusDto getOVFPackageInstanceStatus(final String idEnterprise,
        final String ovfId)
    {
        Resource resource = ovfPackage(idEnterprise, ovfId);

        ClientResponse response = resource.accept(MEDIA_TYPE).queryParam(FORAMT, "status").get();

        checkResponse(response);

        return response.getEntity(OVFPackageInstanceStatusDto.class);
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

    public OVFPackageInstanceStatusListDto getOVFPackagInstanceStatusList(final String idEnterprise)
    {
        Resource resource = ovfPackagesTimeout(idEnterprise);

        ClientResponse response = resource.accept(MEDIA_TYPE).get();

        checkResponse(response);

        return response.getEntity(OVFPackageInstanceStatusListDto.class);
    }

    public AMConfigurationDto getAMConfiguration()
    {
        Resource resource = repositories();

        ClientResponse response = resource.accept(MEDIA_TYPE).get();

        checkResponse(response);

        return response.getEntity(AMConfigurationDto.class);
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
     * @param idEnterprise Id of  Enterprise to which this {@link OVFPackage} belongs.
     * @return OVFPackageInstanceStatusDto
     */
    public OVFPackageInstanceStatusDto getCurrentOVFPackageInstanceStatus(
        final String idEnterprise, final String ovfId)
    {
        Resource resource = ovfPackage(idEnterprise, ovfId);

        ClientResponse response = resource.accept(MEDIA_TYPE).queryParam(FORAMT, "status").get();

        final int httpStatus = response.getStatusCode();
        if (httpStatus == 200)
        {
            return response.getEntity(OVFPackageInstanceStatusDto.class);
        }
        if (httpStatus == 404)
        {
            return uploading(ovfId);
        }

        checkErrorStatusResponse(response, httpStatus);

        return response.getEntity(OVFPackageInstanceStatusDto.class);
    }

    /**
     * This {@link OVFPackage} is uploading.
     * 
     * @param ovfId id {@link OVFPackage}.
     * @return OVFPackageInstanceStatusDto
     */
    private OVFPackageInstanceStatusDto uploading(final String ovfId)
    {
        OVFPackageInstanceStatusDto statusUploading = new OVFPackageInstanceStatusDto();
        statusUploading.setOvfId(ovfId);
        statusUploading.setProgress(0d);
        statusUploading.setOvfPackageStatus(OVFPackageInstanceStatusType.DOWNLOAD);
        return statusUploading;
    }

    /**
     * Returns the proper error.
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
