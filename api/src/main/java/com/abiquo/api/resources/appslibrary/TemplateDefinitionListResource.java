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

package com.abiquo.api.resources.appslibrary;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;

import org.apache.wink.common.annotations.Parent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import com.abiquo.api.resources.AbstractResource;
import com.abiquo.api.resources.EnterpriseResource;
import com.abiquo.api.services.appslibrary.TemplateDefinitionListService;
import com.abiquo.api.transformer.AppsLibraryTransformer;
import com.abiquo.api.util.IRESTBuilder;
import com.abiquo.appliancemanager.transport.OVFPackageInstanceStateDto;
import com.abiquo.appliancemanager.transport.OVFPackageInstancesStateDto;
import com.abiquo.server.core.appslibrary.TemplateDefinition;
import com.abiquo.server.core.appslibrary.TemplateDefinitionList;
import com.abiquo.server.core.appslibrary.TemplateDefinitionListDto;

@Parent(TemplateDefinitionListsResource.class)
@Path(TemplateDefinitionListResource.TEMPLATE_DEFINITION_LIST_PARAM)
@Controller
public class TemplateDefinitionListResource extends AbstractResource
{

    public static final String TEMPLATE_DEFINITION_LIST = "templateDefinitionList";

    public static final String TEMPLATE_DEFINITION_LIST_PARAM = "{" + TEMPLATE_DEFINITION_LIST
        + "}";

    public static final String TEMPLATE_DEFINITION_LIST_REPOSITORY_STATUS_PATH =
        "actions/repositoryStatus";

    public static final String TEMPLATE_DEFINITION_LIST_REPOSITORY_STATUS_DATACENTER_QUERY_PARAM =
        "datacenterId";

    @Autowired
    protected TemplateDefinitionListService service;

    @Autowired
    protected AppsLibraryTransformer transformer;

    @GET
    public TemplateDefinitionListDto getTemplateDefinitionList(
        @PathParam(TEMPLATE_DEFINITION_LIST) final Integer TemplateDefinitionListId,
        @Context final IRESTBuilder restBuilder) throws Exception
    {
        TemplateDefinitionList templateDefinitionList =
            service.getTemplateDefinitionList(TemplateDefinitionListId);

        return transformer.createTransferObject(templateDefinitionList, restBuilder);
    }

    @PUT
    public TemplateDefinitionListDto updateTemplateDefinitionList(
        final TemplateDefinitionListDto templateDefinitionList,
        @PathParam(TEMPLATE_DEFINITION_LIST) final Integer templateDefinitionListId,
        @PathParam(EnterpriseResource.ENTERPRISE) final Integer idEnterprise,
        @Context final IRESTBuilder restBuilder) throws Exception
    {
        TemplateDefinitionList d = transformer.createPersistenceObject(templateDefinitionList);

        d = service.updateTemplateDefinitionList(templateDefinitionListId, d, idEnterprise);

        return transformer.createTransferObject(d, restBuilder);
    }

    @PUT
    @Consumes(MediaType.TEXT_PLAIN)
    public TemplateDefinitionListDto refreshTemplateDefinitionListFromUrl(
        @PathParam(TEMPLATE_DEFINITION_LIST) final Integer templateDefinitionListId,
        @PathParam(EnterpriseResource.ENTERPRISE) final Integer idEnterprise,
        @Context final IRESTBuilder restBuilder) throws Exception
    {
        TemplateDefinitionList d;

        d = service.updateTemplateDefinitionList(idEnterprise, templateDefinitionListId);

        return transformer.createTransferObject(d, restBuilder);
    }

    @DELETE
    public void deleteTemplateDefinitionList(
        @PathParam(TEMPLATE_DEFINITION_LIST) final Integer templateDefinitionListId)
    {
        service.removeTemplateDefinitionList(templateDefinitionListId);
    }

    /**
     * Get the all {@link OVFPackageInstanceStateDto} in the provided
     * {@link DatacenterRepositoryResource} for all the {@link TemplateDefinition} in the current
     * list.
     */
    @GET
    @Path(TemplateDefinitionListResource.TEMPLATE_DEFINITION_LIST_REPOSITORY_STATUS_PATH)
    public OVFPackageInstancesStateDto getTemplateStatusList(
        @PathParam(TEMPLATE_DEFINITION_LIST) final Integer templateDefinitionId,
        @PathParam(EnterpriseResource.ENTERPRISE) final Integer idEnterprise,
        @QueryParam(TEMPLATE_DEFINITION_LIST_REPOSITORY_STATUS_DATACENTER_QUERY_PARAM) final Integer datacenterId,
        @Context final IRESTBuilder restBuilder) throws Exception
    {
        return service.getTemplateListStatus(templateDefinitionId, datacenterId, idEnterprise);
    }
}
