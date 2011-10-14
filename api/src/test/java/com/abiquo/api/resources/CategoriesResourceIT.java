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

package com.abiquo.api.resources;

import static com.abiquo.api.common.Assert.assertErrors;
import static com.abiquo.api.common.UriTestResolver.resolveCategoriesURI;
import static com.abiquo.api.common.UriTestResolver.resolveCategoryURI;
import static org.testng.Assert.assertEquals;

import org.apache.wink.client.ClientResponse;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import com.abiquo.api.exceptions.APIError;
import com.abiquo.server.core.config.CategoriesDto;
import com.abiquo.server.core.config.Category;
import com.abiquo.server.core.config.CategoryDto;

public class CategoriesResourceIT extends AbstractJpaGeneratorIT
{

    private static final String SYSADMIN = "sysadmin";

    protected Category category;

    @Override
    @BeforeMethod
    public void setup()
    {
        category = categoryGenerator.createUniqueInstance();
        setup(category);
    }

    @Test
    public void getCategories() throws Exception
    {

        Category c1 = categoryGenerator.createUniqueInstance();
        Category c2 = categoryGenerator.createUniqueInstance();
        setup(c1, c2);

        String categoriesURI = resolveCategoriesURI();
        ClientResponse response = get(categoriesURI);

        CategoriesDto dtos = response.getEntity(CategoriesDto.class);

        // 4 already created + 3 entities created during test
        assertEquals(dtos.getCollection().size(), 7);

        String categoryURI = resolveCategoryURI(c1.getId());
        response = get(categoryURI);
        assertEquals(response.getStatusCode(), 200);

        categoryURI = resolveCategoryURI(c2.getId());
        response = get(categoryURI);
        assertEquals(response.getStatusCode(), 200);

    }

    @Test
    public void postCategoryTest() throws Exception
    {
        Category cat1 = categoryGenerator.createUniqueInstance();
        CategoryDto dto = createCategoryDto(cat1);

        String href = resolveCategoriesURI();

        // Assert response code is OK
        ClientResponse response = post(href, dto);
        assertEquals(response.getStatusCode(), 201);

        // Ensure all the fields are the same than before but with an id assigned
        dto = response.getEntity(CategoryDto.class);

        assertEquals(cat1.getName(), dto.getName());
    }

    @Test
    public void postWithDuplicatedNameTest()
    {
        CategoryDto cat = createCategoryDto(category);

        String href = resolveCategoriesURI();
        ClientResponse response = post(href, cat);

        assertErrors(response, 409, APIError.CATEGORY_DUPLICATED_NAME.getCode());
    }

    // ********************* Helper methods ************************
    private CategoryDto createCategoryDto(final Category category)
    {
        CategoryDto dto = new CategoryDto();
        dto.setId(category.getId());
        dto.setName(category.getName());
        dto.setIsErasable(category.getIsErasable());
        return dto;
    }
}
