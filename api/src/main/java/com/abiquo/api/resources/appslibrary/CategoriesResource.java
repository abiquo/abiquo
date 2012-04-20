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

import static com.abiquo.api.resources.appslibrary.CategoryResource.createTransferObject;

import java.util.Collection;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;

import org.apache.wink.common.annotations.Workspace;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import com.abiquo.api.resources.AbstractResource;
import com.abiquo.api.services.appslibrary.CategoryService;
import com.abiquo.api.util.IRESTBuilder;
import com.abiquo.server.core.appslibrary.CategoriesDto;
import com.abiquo.server.core.appslibrary.Category;
import com.abiquo.server.core.appslibrary.CategoryDto;

@Path(CategoriesResource.CATEGORIES_PATH)
@Controller
@Workspace(workspaceTitle = "Abiquo configuration workspace", collectionTitle = "Categories")
public class CategoriesResource extends AbstractResource
{
    /** The logger. */
    private static final Logger LOGGER = LoggerFactory.getLogger(CategoriesResource.class);

    public static final String CATEGORIES_PATH = "config/categories";

    public final static String CATEGORIES_OF_ENTERPRISE_QUERYPARAM = "idEnterprise";

    // TODO get allowed categories

    @Autowired
    private CategoryService service;

    /**
     * Returns all categories
     * 
     * @title Retrieve all categories
     * @wiki Returns the global categories by default. If you supply the id of a enterprise, the
     *       global categories and the local categories of the the given id will be retrieved. If no
     *       id is supplied, global categories will be retrieved. This feature is available from
     *       version 2.0-HF1
     * @param idEnterprise If you supply the id of a enterprise, the global categories and the local
     *            categories of the the given enterprise will be retrieved. If no id is supplied,
     *            global categories will be retrieved.
     * @since 2.0-HF1
     * @param restBuilder a Context-injected object to create the links of the Dto
     * @return a {CategoriesDto} object with all categories
     * @throws Exception
     */
    @GET
    @Produces(CategoriesDto.MEDIA_TYPE)
    public CategoriesDto getCategory(
        @QueryParam(CATEGORIES_OF_ENTERPRISE_QUERYPARAM) final Integer idEnterprise,
        @Context final IRESTBuilder restBuilder) throws Exception
    {
        Collection<Category> all =
            idEnterprise == null ? service.getCategories(0, false) : service.getCategories(
                idEnterprise, false);

        CategoriesDto categories = new CategoriesDto();
        for (Category c : all)
        {
            categories.add(createTransferObject(c, restBuilder));
        }

        return categories;
    }

    /**
     * @wiki Creates a category and returns it after creation. If you provide a link to the
     *       enterprise it will create a local category, and without any link in the dto it will try
     *       to create a global category. To create global category user must have the role
     *       APPLIB_MANAGE_GLOBAL_CATEGORIES. The division in local and global categories is
     *       available from versin 2.0-HF1
     * @since 2.0-HF1
     * @param categoryDto category to create
     * @param builder a Context-injected object to create the links of the Dto
     * @return a {CategoryDto} with the created category
     * @throws Exception
     */
    @POST
    @Consumes(CategoryDto.MEDIA_TYPE)
    @Produces(CategoryDto.MEDIA_TYPE)
    public CategoryDto postCategory(final CategoryDto categoryDto,
        @Context final IRESTBuilder builder) throws Exception
    {
        LOGGER.info("Adding new category: {}", categoryDto.getName());
        Category cat = service.addCategory(categoryDto);
        return createTransferObject(cat, builder);
    }

}
