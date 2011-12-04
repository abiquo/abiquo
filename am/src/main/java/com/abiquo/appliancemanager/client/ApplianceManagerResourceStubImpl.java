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

import javax.ws.rs.core.MediaType;

import org.apache.wink.client.ClientResponse;
import org.apache.wink.client.Resource;
import org.dmtf.schemas.ovf.envelope._1.EnvelopeType;

import com.abiquo.appliancemanager.transport.EnterpriseRepositoryDto;
import com.abiquo.appliancemanager.transport.RepositoryConfigurationDto;
import com.abiquo.appliancemanager.transport.TemplateDto;
import com.abiquo.appliancemanager.transport.TemplateStateDto;
import com.abiquo.appliancemanager.transport.TemplatesStateDto;

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
    public TemplateDto getTemplate(final String idEnterprise, final String ovfId)
    {
        Resource resource = template(idEnterprise, ovfId);

        ClientResponse response = resource.accept(MEDIA_TYPE).get();
        // default queryParam(FORAMT, "ovfpi")

        checkResponse(response);

        return response.getEntity(TemplateDto.class);
    }

    public EnvelopeType getTemplateOVFEnvelope(final String idEnterprise, final String ovfId)
    {
        Resource resource = template(idEnterprise, ovfId);

        ClientResponse response = resource.accept(MEDIA_TYPE).queryParam(FORAMT, "envelope").get();

        checkResponse(response);

        return response.getEntity(EnvelopeType.class);
    }

    public TemplatesStateDto getTemplatesState(final String idEnterprise)
    {
        Resource resource = templatesTimeout(idEnterprise);

        ClientResponse response = resource.accept(MEDIA_TYPE).get();

        checkResponse(response);

        return response.getEntity(TemplatesStateDto.class);
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
        Resource resource = template(idEnterprise, ovfId);

        ClientResponse response = resource.accept(MEDIA_TYPE).delete();

        checkResponse(response);
    }

    /**
     * start download
     */
    public void installTemplateDefinition(final String idEnterprise, final String ovfId)
    {
        Resource resource = templates(idEnterprise);

        // contentType(mediaType)
        ClientResponse response =
            resource.accept(MEDIA_TYPE).contentType(MediaType.TEXT_PLAIN).post(ovfId);

        checkResponse(response);

    }

    /**
     * Current status, eval if uploading.
     * 
     * @param idsOvfpackageIn Name of the item to refresh.
     * @param idEnterprise Id of Enterprise to which this {@link OVFPackage} belongs.
     * @return OVFPackageInstanceStatusDto
     */
    public TemplateStateDto getTemplateStatus(final String idEnterprise, final String ovfId)
    {
        Resource resource = template(idEnterprise, ovfId);

        ClientResponse response = resource.accept(MEDIA_TYPE).queryParam(FORAMT, "status").get();

        checkResponse(response);

        return response.getEntity(TemplateStateDto.class);
        // final int httpStatus = response.getStatusCode();
        // if (httpStatus == 404)
        // {
        // return uploading(ovfId);
        // }
        // checkErrorStatusResponse(response, httpStatus);
    }

    public String preBundleTemplate(final String idEnterprise, final String name)
    {
        Resource resource = template(idEnterprise, "prebundle.ovf"); // FIXME

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
    public String bundleTemplate(final String idEnterprise, final String snapshot,
        final TemplateDto diskInfo)
    {

        Resource resource = template(idEnterprise, snapshot);

        // contentType(mediaType)
        ClientResponse response =
            resource.accept(MediaType.TEXT_PLAIN).contentType(MEDIA_TYPE).post(diskInfo);

        checkResponse(response);

        return response.getEntity(String.class);
    }

    // /**
    // * This {@link TemplateDefinition} is uploading.
    // *
    // * @param ovfId id {@link TemplateDefinition}.
    // */
    // private TemplateStateDto uploading(final String ovfId)
    // {
    // TemplateStateDto statusUploading = new TemplateStateDto();
    // statusUploading.setOvfId(ovfId);
    // statusUploading.setDownloadingProgress(0d);
    // statusUploading.setStatus(TemplateStatusEnumType.DOWNLOAD);
    // return statusUploading;
    // }
    //
    // /**
    // * Returns the proper error.
    // *
    // * @param response response.
    // * @param httpStatus code.
    // */
    // private void checkErrorStatusResponse(final ClientResponse response, final Integer
    // httpStatus)
    // {
    // String cause = null;
    // try
    // {
    // cause = response.getEntity(String.class);
    // }
    // catch (Exception e)
    // {
    // cause = response.getMessage();
    //
    // }
    //
    // throw new ApplianceManagerStubException(String.format("%d - %s\n %s", httpStatus,
    // response.getMessage(), cause));
    // }
}
