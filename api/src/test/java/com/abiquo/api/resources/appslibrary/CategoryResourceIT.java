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

import static com.abiquo.api.common.Assert.assertError;
import static com.abiquo.api.common.Assert.assertLinkExist;
import static com.abiquo.api.common.UriTestResolver.resolveCategoryURI;
import static org.testng.Assert.assertEquals;

import org.apache.wink.client.ClientResponse;
import org.apache.wink.client.ClientWebException;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import com.abiquo.api.exceptions.APIError;
import com.abiquo.api.resources.AbstractJpaGeneratorIT;
import com.abiquo.server.core.appslibrary.Category;
import com.abiquo.server.core.appslibrary.CategoryDto;

public class CategoryResourceIT extends AbstractJpaGeneratorIT
{
    protected Category category;

    @Override
    @BeforeMethod
    public void setup()
    {
        category = categoryGenerator.createUniqueInstance();
        setup(category);
    }

    @Test
    public void getCategory() throws Exception
    {
        String categoryURI = resolveCategoryURI(category.getId());

        ClientResponse response = get(categoryURI);
        assertEquals(response.getStatusCode(), 200);

        CategoryDto catDto = response.getEntity(CategoryDto.class);

        assertEquals(catDto.getName(), category.getName());
        assertEquals(catDto.isDefaultCategory(), category.isDefaultCategory());
        assertEquals(catDto.isErasable(), category.isErasable());
        assertLinkExist(catDto, resolveCategoryURI(category.getId()), "edit");
    }

    @Test
    public void getCategoryDoesntExist() throws ClientWebException
    {
        ClientResponse response = get(resolveCategoryURI(12345));
        assertError(response, 404, APIError.NON_EXISTENT_CATEGORY);
    }

    @Test
    public void modifyCategory() throws Exception
    {
        Category cat1 = categoryGenerator.createUniqueInstance();
        setup(cat1);

        String categoryURI = resolveCategoryURI(cat1.getId());
        ClientResponse response = get(categoryURI);
        CategoryDto dto = response.getEntity(CategoryDto.class);
        dto.setName("Name_modified");

        response = put(categoryURI, dto);
        assertEquals(response.getStatusCode(), 200);
        CategoryDto modifiedDto = response.getEntity(CategoryDto.class);

        assertEquals(modifiedDto.getName(), "Name_modified");
    }

    @Test
    public void deleteCategory() throws Exception
    {
        String categoryURL = resolveCategoryURI(category.getId());

        ClientResponse response = delete(categoryURL);
        assertEquals(response.getStatusCode(), 204);

        response = get(categoryURL);
        assertError(response, 404, APIError.NON_EXISTENT_CATEGORY);
    }

}
