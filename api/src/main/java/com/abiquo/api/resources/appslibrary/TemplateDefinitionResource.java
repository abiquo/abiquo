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
import javax.ws.rs.POST;
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
import com.abiquo.api.services.appslibrary.TemplateDefinitionService;
import com.abiquo.api.transformer.AppsLibraryTransformer;
import com.abiquo.api.util.IRESTBuilder;
import com.abiquo.appliancemanager.transport.TemplateStateDto;
import com.abiquo.server.core.appslibrary.TemplateDefinition;
import com.abiquo.server.core.appslibrary.TemplateDefinitionDto;

@Parent(TemplateDefinitionsResource.class)
@Path(TemplateDefinitionResource.TEMPLATE_DEFINITION_PARAM)
@Controller
public class TemplateDefinitionResource extends AbstractResource
{

    public static final String TEMPLATE_DEFINITION = "templateDefinition";

    public static final String TEMPLATE_DEFINITION_PARAM = "{" + TEMPLATE_DEFINITION + "}";

    public static final String TEMPLATE_DEFINITION_INSTALL_ACTION_PATH =
        "actions/repositoryInstall";

    public static final String TEMPLATE_DEFINITION_UN_INSTALL_ACTION_PATH =
        "actions/repositoryUninstall";

    public static final String TEMPLATE_DEFINITION_REPOSITORY_STATUS_PATH =
        "actions/repositoryStatus";

    public static final String TEMPLATE_DEFINITION_REPOSITORY_STATUS_DATACENTER_QUERY_PARAM =
        "datacenterId";


    /** Internal logic. */
    @Autowired
    private TemplateDefinitionService service;

    /** Can not be used ModelTransformer duet Category, Icon and Format. */
    @Autowired
    private AppsLibraryTransformer transformer;

    @GET
    public TemplateDefinitionDto getTemplateDefinition(
        @PathParam(TEMPLATE_DEFINITION) final Integer templateDefinitionId,
        @Context final IRESTBuilder restBuilder) throws Exception
    {
        TemplateDefinition templateDef = service.getTemplateDefinition(templateDefinitionId);
        return transformer.createTransferObject(templateDef, restBuilder);
    }

    @GET
    @Path(TEMPLATE_DEFINITION_REPOSITORY_STATUS_PATH)
    public TemplateStateDto getTemplateState(
        @PathParam(TEMPLATE_DEFINITION) final Integer templateDefId,
        @PathParam(EnterpriseResource.ENTERPRISE) final Integer idEnterprise,
        @QueryParam(TEMPLATE_DEFINITION_REPOSITORY_STATUS_DATACENTER_QUERY_PARAM) final Integer datacenterId,

        @Context final IRESTBuilder restBuilder) throws Exception
    {
        return service.getTemplateState(templateDefId, datacenterId, idEnterprise);
    }

    @PUT
    public TemplateDefinitionDto updateTemplateDefinition(final TemplateDefinitionDto templateDef,
        @PathParam(TEMPLATE_DEFINITION) final Integer templateDefId,
        @PathParam(EnterpriseResource.ENTERPRISE) final Integer idEnterprise,
        @Context final IRESTBuilder restBuilder) throws Exception
    {
        TemplateDefinition d = transformer.createPersistenceObject(templateDef);

        d = service.updateTemplateDefinition(templateDefId, d, idEnterprise);

        return transformer.createTransferObject(d, restBuilder);
    }

    @DELETE
    public void deleteTemplateDefinition(@PathParam(TEMPLATE_DEFINITION) final Integer templateDefId)
    {
        service.removeTemplateDefinition(templateDefId);
    }

    /**
     * TODO use the datacenter URI on the post
     */
    @POST
    @Consumes(MediaType.TEXT_PLAIN)
    @Path(TemplateDefinitionResource.TEMPLATE_DEFINITION_INSTALL_ACTION_PATH)
    public Void installTemplateOnDatacenterRepository(
        @PathParam(EnterpriseResource.ENTERPRISE) final Integer idEnterprise,
        @PathParam(TEMPLATE_DEFINITION) final Integer templateDefId, final String datacenterId,
        @Context final IRESTBuilder restBuilder) throws Exception
    {
        service.installTemplateDefinition(templateDefId, Integer.valueOf(datacenterId),
            idEnterprise);
        return null;
    }

    /**
     * TODO use the datacenter URI on the post
     */
    @POST
    @Consumes(MediaType.TEXT_PLAIN)
    @Path(TemplateDefinitionResource.TEMPLATE_DEFINITION_UN_INSTALL_ACTION_PATH)
    public Void uninstallTemplateOnDatacenterRepository(
        @PathParam(EnterpriseResource.ENTERPRISE) final Integer idEnterprise,
        @PathParam(TEMPLATE_DEFINITION) final Integer templateDefId, final String datacenterId,
        @Context final IRESTBuilder restBuilder) throws Exception

    {
        service.uninstallTemplateDefinition(templateDefId, Integer.valueOf(datacenterId),
            idEnterprise);
        return null;
    }
}
