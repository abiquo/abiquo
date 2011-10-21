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

import org.apache.wink.common.annotations.Parent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import com.abiquo.api.resources.AbstractResource;
import com.abiquo.api.services.appslibrary.CategoryService;
import com.abiquo.api.util.IRESTBuilder;
import com.abiquo.server.core.appslibrary.Category;
import com.abiquo.server.core.appslibrary.CategoryDto;

@Parent(CategoriesResource.class)
@Path(CategoryResource.CATEGORY_PARAM)
@Controller
public class CategoryResource extends AbstractResource
{
    // Define the static variables that represent the URI and the PARAM.
    public static final String CATEGORY = "category";

    public static final String CATEGORY_PARAM = "{" + CATEGORY + "}";

    private static final Logger logger = LoggerFactory.getLogger(CategoryResource.class);

    @Autowired
    private CategoryService service;

    @GET
    public CategoryDto getCategory(@PathParam(CATEGORY) @NotNull @Min(1) final Integer categoryId,
        @Context final IRESTBuilder restBuilder) throws Exception
    {
        Category category = service.getCategory(categoryId);

        logger.info(String.format("Getting category id %s", categoryId));

        return createTransferObject(category, restBuilder);
    }

    @PUT
    @Consumes(MediaType.APPLICATION_XML)
    @Produces(MediaType.APPLICATION_XML)
    public CategoryDto modifyCategory(
        @PathParam(CATEGORY) @NotNull @Min(1) final Integer categoryId,
        final CategoryDto categoryDto, @Context final IRESTBuilder restBuilder) throws Exception
    {

        logger.info("Updating category with id " + categoryId);

        Category category = createPersistenceObject(categoryDto);

        category = service.modifyCategory(category, categoryId);

        logger.info("Category with id " + categoryId + " updated successfully");

        return createTransferObject(category, restBuilder);
    }

    @DELETE
    public void deleteCategory(@PathParam(CATEGORY) final Integer categoryId)
    {
        logger.info(String.format("Deleting category id %s", categoryId));

        service.removeCategory(categoryId);
    }

    public static CategoryDto createTransferObject(final Category category,
        final IRESTBuilder restBuilder) throws Exception
    {
        CategoryDto dto = new CategoryDto();
        dto.setId(category.getId());
        dto.setName(category.getName());
        dto.setDefaultCategory(category.isDefaultCategory());
        dto.setErasable(dto.isErasable());

        // Add the links.
        dto.addLinks(restBuilder.buildCategoryLinks(dto));

        return dto;
    }

    // Create the persistence object.
    public static Category createPersistenceObject(final CategoryDto dto) throws Exception
    {
        Category category = new Category(dto.getName());
        category.setId(dto.getId());
        category.setDefaultCategory(dto.isDefaultCategory());
        category.setErasable(dto.isErasable());
        return category;
    }

}
