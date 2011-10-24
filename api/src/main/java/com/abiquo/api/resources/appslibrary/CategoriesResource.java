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

import static com.abiquo.api.resources.appslibrary.CategoryResource.createPersistenceObject;
import static com.abiquo.api.resources.appslibrary.CategoryResource.createTransferObject;

import java.util.Collection;

import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
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

    // TODO get allowed categories

    @Autowired
    private CategoryService service;

    @GET
    public CategoriesDto getCategory(@Context final IRESTBuilder restBuilder) throws Exception
    {
        Collection<Category> all = service.getCategories();

        CategoriesDto categories = new CategoriesDto();
        for (Category c : all)
        {
            categories.add(createTransferObject(c, restBuilder));
        }

        return categories;
    }

    @POST
    public CategoryDto postCategory(final CategoryDto categoryDto,
        @Context final IRESTBuilder builder) throws Exception
    {
        Category category = createPersistenceObject(categoryDto);

        LOGGER.info("Adding new category: {}", category.getName());

        Category cat = service.addCategory(category);

        return createTransferObject(cat, builder);
    }

}
