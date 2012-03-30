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
import javax.ws.rs.Produces;
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
import com.abiquo.appliancemanager.transport.TemplateStateDto;
import com.abiquo.appliancemanager.transport.TemplatesStateDto;
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
    
    public static final String TEMPLATE_DEFINITION_LIST_REPOSITORY_STATUS_REL =
        "repositoryStatus";

    public static final String TEMPLATE_DEFINITION_LIST_REPOSITORY_STATUS_DATACENTER_QUERY_PARAM =
        "datacenterId";

    @Autowired
    protected TemplateDefinitionListService service;

    @Autowired
    protected AppsLibraryTransformer transformer;

    /**
     * Returns a template definition list
     * 
     * @title Retrieve a template definition list
     * @param idEnterprise identifier of the enterprise
     * @param templateDefinitionListId identifier of the template definition
     * @param restBuilder a Context-injected object to create the links of the Dto
     * @return a {TemplateDefinitionListDto} object with the requested template list
     * @throws Exception
     */
    @GET
    @Produces(TemplateDefinitionListDto.MEDIA_TYPE)
    public TemplateDefinitionListDto getTemplateDefinitionList(
        @PathParam(EnterpriseResource.ENTERPRISE) final Integer idEnterprise,
        @PathParam(TEMPLATE_DEFINITION_LIST) final Integer templateDefinitionListId,
        @Context final IRESTBuilder restBuilder) throws Exception
    {
        TemplateDefinitionList templateDefinitionList =
            service.getTemplateDefinitionList(templateDefinitionListId, idEnterprise);

        return transformer.createTransferObject(templateDefinitionList, restBuilder);
    }

    /**
     * Modifies a template definition list
     * 
     * @title Modify a template definition list
     * @wiki Fetch new Template Definitions from an ovfindex.xml source, only apply if the list is
     *       associated to an ovfindex.xml (url datamodel).
     * @param templateDefinitionList the template definition list to modify
     * @param idEnterprise identifier of the enterprise
     * @param templateDefinitionListId identifier of the template definition list
     * @param restBuilder a Context-injected object to create the links of the Dto
     * @return a {TemplateDefinitionListDto} object with the modified template definition list
     * @throws Exception
     */
    @PUT
    @Consumes(TemplateDefinitionListDto.MEDIA_TYPE)
    @Produces(TemplateDefinitionListDto.MEDIA_TYPE)
    public TemplateDefinitionListDto updateTemplateDefinitionList(
        final TemplateDefinitionListDto templateDefinitionList,
        @PathParam(EnterpriseResource.ENTERPRISE) final Integer idEnterprise,
        @PathParam(TEMPLATE_DEFINITION_LIST) final Integer templateDefinitionListId,
        @Context final IRESTBuilder restBuilder) throws Exception
    {
        TemplateDefinitionList d = transformer.createPersistenceObject(templateDefinitionList);

        d = service.updateTemplateDefinitionList(templateDefinitionListId, d, idEnterprise);

        return transformer.createTransferObject(d, restBuilder);
    }

    /**
     * Refreshes a template definition list from the url
     * 
     * @title Refresh a template definition list from the url
     * @wiki Deleting the list doesn't delete the containing Template Definitions.
     * @param idEnterprise identifier of the enterprise
     * @param templateDefinitionListId itentifier of the template definition list
     * @param restBuilder a Context-injected object to create the links of the Dto
     * @return a {TemplateDefinitionListDto} object with the refreshed template definition list
     * @throws Exception
     */
    @PUT
    @Consumes(MediaType.TEXT_PLAIN)
    @Produces(TemplateDefinitionListDto.MEDIA_TYPE)
    public TemplateDefinitionListDto refreshTemplateDefinitionListFromUrl(
        @PathParam(EnterpriseResource.ENTERPRISE) final Integer idEnterprise,
        @PathParam(TEMPLATE_DEFINITION_LIST) final Integer templateDefinitionListId,
        @Context final IRESTBuilder restBuilder) throws Exception
    {
        TemplateDefinitionList d;

        d = service.refreshTemplateDefinitionList(idEnterprise, templateDefinitionListId);

        return transformer.createTransferObject(d, restBuilder);
    }

    /**
     * Deletes a template definition list
     * 
     * @title Delete a template definition list
     * @param idEnterprise identifier of the enterprise
     * @param templateDefinitionListId identifier of the template definition list
     */
    @DELETE
    public void deleteTemplateDefinitionList(
        @PathParam(EnterpriseResource.ENTERPRISE) final Integer idEnterprise,
        @PathParam(TEMPLATE_DEFINITION_LIST) final Integer templateDefinitionListId)
    {
        service.removeTemplateDefinitionList(templateDefinitionListId, false, idEnterprise);
    }

    /**
     * Get the all {@link TemplateStateDto} in the provided {@link DatacenterRepositoryResource} for
     * all the {@link TemplateDefinition} in the current list.
     * 
     * @title Retrieve a list of the status of all template status list
     * @wiki Template Definition are available sources, but in order to create a Virtual Machine the
     *       Defininition should be downloaded into the Datacenter Repository (NFS filesystem).
     */
    @GET
    @Path(TemplateDefinitionListResource.TEMPLATE_DEFINITION_LIST_REPOSITORY_STATUS_PATH)
    @Produces(TemplatesStateDto.MEDIA_TYPE)
    public TemplatesStateDto getTemplateStatusList(
        @PathParam(EnterpriseResource.ENTERPRISE) final Integer idEnterprise,
        @PathParam(TEMPLATE_DEFINITION_LIST) final Integer templateDefinitionId,
        @QueryParam(TEMPLATE_DEFINITION_LIST_REPOSITORY_STATUS_DATACENTER_QUERY_PARAM) final Integer datacenterId,
        @Context final IRESTBuilder restBuilder) throws Exception
    {
        return service.getTemplateListStatus(templateDefinitionId, datacenterId, idEnterprise);
    }
}
