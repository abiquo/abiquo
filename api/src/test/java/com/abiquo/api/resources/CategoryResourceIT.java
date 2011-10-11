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

import static com.abiquo.api.common.UriTestResolver.resolveCategoriesURI;
import static com.abiquo.api.common.UriTestResolver.resolveCategoryURI;
import static org.testng.Assert.assertEquals;

import org.apache.wink.client.ClientResponse;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import com.abiquo.server.core.config.Category;
import com.abiquo.server.core.config.CategoryDto;

public class CategoryResourceIT extends AbstractJpaGeneratorIT
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
    public void getCategory() throws Exception
    {
        String categoryURI = resolveCategoryURI(category.getId());

        ClientResponse response = get(categoryURI);
        assertEquals(response.getStatusCode(), 200);

        CategoryDto catDto = response.getEntity(CategoryDto.class);
        assertEquals(catDto.getName(), category.getName());

        // assertLinkExist(catDto, resolveCategoryURI(category.getId()), "edit");
    }

    // @Test
    // public void modifyCategory() throws Exception
    // {
    // Category cat1 = categoryGenerator.createUniqueInstance();
    // setup(cat1);
    //
    // CategoryDto dto = createCategoryDto(cat1);
    // dto.setName("Name modified");
    //
    // String categoryURI = resolveCategoryURI(cat1.getId());
    //
    // ClientResponse response = put(categoryURI, dto);
    //
    // response = get(categoryURI);
    //
    // String name = response.getEntity(Category.class).getName();
    //
    // assertEquals(name, dto.getName());
    // }

    @Test
    public void deleteCategory() throws Exception
    {

        Category cat1 = categoryGenerator.createUniqueInstance();

        CategoryDto dto = createCategoryDto(cat1);

        String categoriesURI = resolveCategoriesURI();

        ClientResponse response = post(categoriesURI, dto);
        Integer id = response.getEntity(CategoryDto.class).getId();

        response = delete(resolveCategoryURI(id));
        assertEquals(response.getStatusCode(), 204);

    }

    // ********************* Helper methods ************************
    private CategoryDto createCategoryDto(final Category category)
    {
        CategoryDto dto = new CategoryDto();
        dto.setId(category.getId());
        dto.setName(category.getName());
        dto.setIsDefault(category.getIsDefault());
        dto.setIsErasable(category.getIsErasable());
        return dto;
    }

}
