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

import static com.abiquo.api.util.URIResolver.buildPath;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;

import org.apache.wink.common.annotations.Parent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import com.abiquo.api.exceptions.APIError;
import com.abiquo.api.resources.AbstractResource;
import com.abiquo.api.resources.EnterpriseResource;
import com.abiquo.api.resources.EnterprisesResource;
import com.abiquo.api.services.appslibrary.CategoryService;
import com.abiquo.api.util.IRESTBuilder;
import com.abiquo.api.util.URIResolver;
import com.abiquo.model.rest.RESTLink;
import com.abiquo.server.core.appslibrary.Category;
import com.abiquo.server.core.appslibrary.CategoryDto;

@Parent(CategoriesResource.class)
@Path(CategoryResource.CATEGORY_PARAM)
@Controller
public class CategoryResource extends AbstractResource
{
    private static final Logger LOGGER = LoggerFactory.getLogger(CategoryResource.class);

    // Define the static variables that represent the URI and the PARAM.
    public static final String CATEGORY = "category";

    public static final String CATEGORY_PARAM = "{" + CATEGORY + "}";

    @Autowired
    private CategoryService service;

    /**
     * Returns a category
     * 
     * @title Retrieve a category
     * @param categoryId identifier of the category
     * @param restBuilder a Context-injected object to create the links of the Dto
     * @return a {CategoryDto} with the requested category
     * @throws Exception
     */
    @GET
    @Produces(CategoryDto.MEDIA_TYPE)
    public CategoryDto getCategory(@PathParam(CATEGORY) @NotNull @Min(1) final Integer categoryId,
        @Context final IRESTBuilder restBuilder) throws Exception
    {
        Category category = service.getCategory(categoryId);
        return createTransferObject(category, restBuilder);
    }

    /**
     * Modifies a category
     * 
     * @title Modify a category
     * @param categoryId identifier of the category
     * @param categoryDto category to modify
     * @param restBuilder a Context-injected object to create the links of the Dto
     * @return a {CategoryDto} object with the modified category
     * @throws Exception
     */
    @PUT
    @Consumes(CategoryDto.MEDIA_TYPE)
    @Produces(CategoryDto.MEDIA_TYPE)
    public CategoryDto modifyCategory(
        @PathParam(CATEGORY) @NotNull @Min(1) final Integer categoryId,
        final CategoryDto categoryDto, @Context final IRESTBuilder restBuilder) throws Exception
    {
        LOGGER.info("Updating category with id {}", categoryId);

        Category category = service.modifyCategory(categoryDto, categoryId);

        return createTransferObject(category, restBuilder);
    }

    /**
     * Deletes a category
     * 
     * @title Delete a category
     * @param categoryId identifier of the category
     */
    @DELETE
    public void deleteCategory(@PathParam(CATEGORY) final Integer categoryId)
    {
        LOGGER.info("Deleting category with id {}", categoryId);
        service.removeCategory(categoryId);
    }

    public static CategoryDto createTransferObject(final Category category,
        final IRESTBuilder restBuilder) throws Exception
    {
        CategoryDto dto = new CategoryDto();
        dto.setId(category.getId());
        dto.setName(category.getName());
        dto.setDefaultCategory(category.isDefaultCategory());
        dto.setErasable(category.isErasable());

        // Add the links.
        dto.addLinks(restBuilder.buildCategoryLinks(category));
        return dto;
    }

    // Create the persistence object.

}
