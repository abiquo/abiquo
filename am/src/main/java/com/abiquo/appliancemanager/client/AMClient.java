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
import javax.ws.rs.core.Response.Status;

import org.apache.wink.client.ClientResponse;
import org.dmtf.schemas.ovf.envelope._1.EnvelopeType;

import com.abiquo.appliancemanager.transport.EnterpriseRepositoryDto;
import com.abiquo.appliancemanager.transport.RepositoryConfigurationDto;
import com.abiquo.appliancemanager.transport.TemplateDto;
import com.abiquo.appliancemanager.transport.TemplateIdsDto;
import com.abiquo.appliancemanager.transport.TemplateStateDto;
import com.abiquo.appliancemanager.transport.TemplateStatusEnumType;
import com.abiquo.appliancemanager.transport.TemplatesStateDto;

public class AMClient extends AMClientResources
{
    public final static String FORAMT = "format";

    /**
     * @param configTimeout, only for am request that will require some repository filesystem action
     */
    public AMClient initialize(final String serviceUri, final boolean configTimeout)
    {
        initializeClient(serviceUri, configTimeout);
        return this;
    }

    // GET
    public TemplateDto getTemplate(final Integer idEnterprise, final String ovfId)
        throws AMClientException
    {
        ClientResponse response = template(idEnterprise, ovfId)//
            .accept(MediaType.APPLICATION_XML).get();
        checkResponseErrors(response);

        return response.getEntity(TemplateDto.class);
    }

    public EnvelopeType getTemplateOVFEnvelope(final Integer idEnterprise, final String ovfId)
        throws AMClientException
    {
        ClientResponse response = template(idEnterprise, ovfId)//
            .accept(MediaType.APPLICATION_XML).queryParam(FORAMT, "envelope").get();
        checkResponseErrors(response);

        return response.getEntity(EnvelopeType.class);
    }

    public static final String QUERY_PRAM_STATE = "state";

    public TemplatesStateDto getTemplatesState(final Integer idEnterprise,
        final TemplateStatusEnumType state) throws AMClientException
    {

        ClientResponse response = templates(idEnterprise)//
            .accept(MediaType.APPLICATION_XML).//
            queryParam(QUERY_PRAM_STATE, state.name()).get();

        checkResponseErrors(response);

        return response.getEntity(TemplatesStateDto.class);
    }

    public TemplatesStateDto getTemplatesState(final Integer idEnterprise, final TemplateIdsDto ids)
        throws AMClientException
    {

        ClientResponse response = templates(idEnterprise)// templates_GetIds(idEnterprise)//
            .accept(MediaType.APPLICATION_XML).contentType(MediaType.APPLICATION_XML).post(ids);

        checkResponseErrors(response);

        return response.getEntity(TemplatesStateDto.class);
    }

    public RepositoryConfigurationDto getRepositoryConfiguration() throws AMClientException
    {
        ClientResponse response = repositories()//
            .accept(MediaType.APPLICATION_XML).get();
        checkResponseErrors(response);

        return response.getEntity(RepositoryConfigurationDto.class);
    }

    public EnterpriseRepositoryDto getRepository(final Integer idEnterprise)
        throws AMClientException
    {
        ClientResponse response = repository(idEnterprise)//
            .accept(MediaType.APPLICATION_XML).get();
        checkResponseErrors(response);

        return response.getEntity(EnterpriseRepositoryDto.class);
    }

    public void refreshRepository(final Integer idEnterprise) throws AMClientException
    {
        ClientResponse response =
            repository(idEnterprise).accept(MediaType.APPLICATION_XML).post(null);

        checkResponseErrors(response);
    }

    public void checkService() throws AMClientException
    {
        ClientResponse response = check()//
            .accept(MediaType.APPLICATION_XML).get();

        checkResponseErrors(response);
    }

    public void deleteTemplate(final Integer idEnterprise, final String ovfId)
        throws AMClientException
    {
        ClientResponse response = template(idEnterprise, ovfId)//
            .accept(MediaType.APPLICATION_XML).delete();
        checkResponseErrors(response);
    }

    public void installTemplateDefinition(final Integer idEnterprise, final String ovfId)
        throws AMClientException
    {
        ClientResponse response = templates(idEnterprise)//
            .accept(MediaType.APPLICATION_XML).contentType(MediaType.TEXT_PLAIN).post(ovfId);

        checkResponseErrors(response);
    }

    /**
     * Current status, eval if uploading.
     * 
     * @param idsOvfpackageIn Name of the item to refresh.
     * @param idEnterprise Id of Enterprise to which this {@link OVFPackage} belongs.
     * @return OVFPackageInstanceStatusDto
     */
    public TemplateStateDto getTemplateStatus(final Integer idEnterprise, final String ovfId)
        throws AMClientException
    {
        ClientResponse response = template(idEnterprise, ovfId)//
            .accept(MediaType.APPLICATION_XML).queryParam(FORAMT, "status").get();

        // not found == not download
        if (response.getStatusCode() == Status.NOT_FOUND.getStatusCode())
        {
            TemplateStateDto notFound = new TemplateStateDto();
            notFound.setOvfId(ovfId);
            notFound.setStatus(TemplateStatusEnumType.NOT_DOWNLOAD);
            return notFound;
        }
        else
        {
            checkResponseErrors(response);
            return response.getEntity(TemplateStateDto.class);
        }
    }

    public String preBundleTemplate(final Integer idEnterprise, final String name)
        throws AMClientException
    {
        // contentType(mediaType)
        ClientResponse response =
            template(idEnterprise, "prebundle.ovf").accept(MediaType.TEXT_PLAIN)
                .contentType(MediaType.TEXT_PLAIN).post(name);

        checkResponseErrors(response);

        return response.getEntity(String.class);
    }

    public String bundleTemplate(final Integer idEnterprise, final String snapshot,
        final TemplateDto diskInfo) throws AMClientException
    {
        // contentType(mediaType)
        ClientResponse response = template(idEnterprise, snapshot) //
            .accept(MediaType.TEXT_PLAIN).contentType(MediaType.APPLICATION_XML).post(diskInfo);

        checkResponseErrors(response);

        return response.getEntity(String.class);
    }
}
