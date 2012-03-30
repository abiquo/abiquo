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

import java.util.List;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;

import org.apache.wink.common.annotations.Parent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import com.abiquo.api.resources.AbstractResource;
import com.abiquo.api.resources.EnterpriseResource;
import com.abiquo.api.services.appslibrary.TemplateDefinitionService;
import com.abiquo.api.transformer.AppsLibraryTransformer;
import com.abiquo.api.util.IRESTBuilder;
import com.abiquo.server.core.appslibrary.TemplateDefinition;
import com.abiquo.server.core.appslibrary.TemplateDefinitionDto;
import com.abiquo.server.core.appslibrary.TemplateDefinitionsDto;

@Parent(EnterpriseResource.class)
@Path(TemplateDefinitionsResource.TEMPLATE_DEFINITIONS_PATH)
@Controller
public class TemplateDefinitionsResource extends AbstractResource
{
    public static final String TEMPLATE_DEFINITIONS_PATH = "appslib/templateDefinitions";

    @Autowired
    private TemplateDefinitionService service;

    @Autowired
    private AppsLibraryTransformer transformer;

    /**
     * Returns all template definitions
     * 
     * @title Retrieve all template definitions
     * @wiki This method retrieves all the Template Definitions for a given enterprise, including
     *       those the being used on any Template Definition List.
     * @param idEnterprise identifier of the enterprise
     * @param restBuilder a Context-injected object to create the links of the Dto
     * @return a {TemplateDefinitionsDto} object with all template definitions
     * @throws Exception
     */
    @GET
    @Produces(TemplateDefinitionsDto.MEDIA_TYPE)
    public TemplateDefinitionsDto getTemplateDefinitions(
        @PathParam(EnterpriseResource.ENTERPRISE) final Integer idEnterprise,
        @Context final IRESTBuilder restBuilder) throws Exception
    {
        List<TemplateDefinition> all = service.getTemplateDefinitionsByEnterprise(idEnterprise);

        TemplateDefinitionsDto templateDefs = new TemplateDefinitionsDto();
        if (all != null && !all.isEmpty())
        {
            for (TemplateDefinition d : all)
            {
                templateDefs.add(transformer.createTransferObject(d, restBuilder));
            }
        }

        return templateDefs;
    }

    /**
     * Creates a template definition and returns it after creation
     * 
     * @title Create a template definition
     * @wiki In case you have an OVF Envelope document available in an HTTP URL (e.g
     *       http://rs.bcn.abiquo.com:9000/ovf/10/*desc.ovf*) you can use it to add as a template
     *       definition into abiquo. Category and Icon links: you can add new icons and categories
     *       while creating Template Definition, just by adding icon path in the title field of link
     *       and in case of category adding category name in the title field.
     * @param idEnterprise identifier of the enterprise
     * @param templateDef template definition to create
     * @param restBuilder a Context-injected object to create the links of the Dto
     * @return a {TemplateDefinitionDto} object with the created template definition
     * @throws Exception
     */
    @POST
    @Consumes(TemplateDefinitionDto.MEDIA_TYPE)
    @Produces(TemplateDefinitionDto.MEDIA_TYPE)
    public TemplateDefinitionDto postTemplateDefinition(
        @PathParam(EnterpriseResource.ENTERPRISE) final Integer idEnterprise,
        final TemplateDefinitionDto templateDef, @Context final IRESTBuilder restBuilder)
        throws Exception
    {
        TemplateDefinition opl = transformer.createPersistenceObject(templateDef, true);
        opl = service.addTemplateDefinition(opl, idEnterprise);
        return transformer.createTransferObject(opl, restBuilder);
    }

}
