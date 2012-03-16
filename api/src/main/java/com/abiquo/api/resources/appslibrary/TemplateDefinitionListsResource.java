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

import java.net.SocketTimeoutException;
import java.util.List;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;

import org.apache.wink.common.annotations.Parent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import com.abiquo.api.exceptions.APIError;
import com.abiquo.api.exceptions.BadRequestException;
import com.abiquo.api.resources.AbstractResource;
import com.abiquo.api.resources.EnterpriseResource;
import com.abiquo.api.services.appslibrary.TemplateDefinitionListService;
import com.abiquo.api.transformer.AppsLibraryTransformer;
import com.abiquo.api.util.IRESTBuilder;
import com.abiquo.server.core.appslibrary.TemplateDefinitionList;
import com.abiquo.server.core.appslibrary.TemplateDefinitionListDto;
import com.abiquo.server.core.appslibrary.TemplateDefinitionListsDto;

/**
 * @wiki A Template Definition List providing a way to organize multiple Template Definitions. A
 *       single Template Definition can be shared by many lists. Its compatible with ovfindex.xml
 *       format.
 * @author apuig@abiquo.com
 */
@Parent(EnterpriseResource.class)
@Path(TemplateDefinitionListsResource.TEMPLATE_DEFINITION_LISTS_PATH)
@Controller
public class TemplateDefinitionListsResource extends AbstractResource
{
    public static final String TEMPLATE_DEFINITION_LISTS_PATH = "appslib/templateDefinitionLists";

    @Autowired
    private TemplateDefinitionListService service;

    @Autowired
    private AppsLibraryTransformer transformer;

    /**
     * Returns all template definition lists
     * 
     * @title Retrieve all template definition lists
     * @param idEnterprise identifier of the enterprise
     * @param restBuilder a Context-injected object to create the links of the Dto
     * @return a {TemplateDefinitionListsDto} object with all requested template definition lists
     * @throws Exception
     * @throws SocketTimeoutException
     */
    @GET
    @Produces(TemplateDefinitionListsDto.MEDIA_TYPE)
    public TemplateDefinitionListsDto getTemplateDefinitionLists(
        @PathParam(EnterpriseResource.ENTERPRISE) final Integer idEnterprise,
        @Context final IRESTBuilder restBuilder) throws Exception, SocketTimeoutException
    {
        List<TemplateDefinitionList> all =
            service.getTemplateDefinitionListsByEnterprise(idEnterprise);

        TemplateDefinitionListsDto templateDefListsDto = new TemplateDefinitionListsDto();

        Integer totalSize = 0;
        if (all != null && !all.isEmpty())
        {
            for (TemplateDefinitionList r : all)
            {
                templateDefListsDto.add(transformer.createTransferObject(r, restBuilder));
            }
            totalSize = all.size();
        }

        templateDefListsDto.setTotalSize(totalSize);

        return templateDefListsDto;
    }

    /**
     * if TEMPLATE_DEFINITION_POST_QUERY_PARM is set do not use the content body
     * {@link TemplateDefinitionListDto}.
     * 
     * @title Create a template definition list
     * @wiki All the contained Template Definitions will also be created.
     */
    @POST
    @Consumes(TemplateDefinitionListDto.MEDIA_TYPE)
    @Produces(TemplateDefinitionListDto.MEDIA_TYPE)
    public TemplateDefinitionListDto postTemplateDefinitionList(
        @PathParam(EnterpriseResource.ENTERPRISE) final Integer idEnterprise,
        final TemplateDefinitionListDto templateDefList, @Context final IRESTBuilder restBuilder)
        throws Exception
    {
        // Validate template definition list name
        if (templateDefList.getName() == null || templateDefList.getName().isEmpty())
        {
            throw new BadRequestException(APIError.TEMPLATE_DEFINITION_LIST_NAME_NOT_FOUND);
        }

        TemplateDefinitionList opl = transformer.createPersistenceObject(templateDefList);
        opl = service.addTemplateDefinitionList(opl, idEnterprise);

        return transformer.createTransferObject(opl, restBuilder);
    }

    /**
     * if TEMPLATE_DEFINITION_POST_QUERY_PARM is set do not use the content body
     * {@link TemplateDefinitionListDto}.
     * 
     * @title Create a template definition list from OVF
     */
    @POST
    @Produces(TemplateDefinitionListDto.MEDIA_TYPE)
    @Consumes(MediaType.TEXT_PLAIN)
    public TemplateDefinitionListDto postTemplateDefinitionListFromOVFIndexUrl(
        @PathParam(EnterpriseResource.ENTERPRISE) final Integer idEnterprise,
        final String ovfindexURL, @Context final IRESTBuilder restBuilder) throws Exception
    {
        TemplateDefinitionList opl = service.addTemplateDefinitionList(ovfindexURL, idEnterprise);

        return transformer.createTransferObject(opl, restBuilder);
    }
}
