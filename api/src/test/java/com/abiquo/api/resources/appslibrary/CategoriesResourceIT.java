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

import static com.abiquo.api.common.Assert.assertErrors;
import static com.abiquo.api.common.UriTestResolver.resolveCategoriesURI;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;

import java.util.ArrayList;
import java.util.List;

import javax.ws.rs.core.Response.Status;

import org.apache.wink.client.ClientResponse;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import com.abiquo.api.exceptions.APIError;
import com.abiquo.api.resources.AbstractJpaGeneratorIT;
import com.abiquo.server.core.appslibrary.CategoriesDto;
import com.abiquo.server.core.appslibrary.Category;
import com.abiquo.server.core.appslibrary.CategoryDto;
import com.abiquo.server.core.enterprise.Enterprise;
import com.abiquo.server.core.enterprise.Privilege;
import com.abiquo.server.core.enterprise.Role;
import com.abiquo.server.core.enterprise.User;

public class CategoriesResourceIT extends AbstractJpaGeneratorIT
{
    protected Category category;

    protected Enterprise enterprise;

    private static final String SYSADMIN = "sysadmin";

    @Override
    @BeforeMethod
    public void setup()
    {
        category = categoryGenerator.createUniqueInstance();

        enterprise = enterpriseGenerator.createUniqueInstance();
        Role role = roleGenerator.createInstanceSysAdmin();
        User user = userGenerator.createInstance(enterprise, role, SYSADMIN, SYSADMIN);

        List<Object> entitiesToSetup = new ArrayList<Object>();
        entitiesToSetup.add(enterprise);

        for (Privilege p : role.getPrivileges())
        {
            entitiesToSetup.add(p);
        }
        entitiesToSetup.add(role);
        entitiesToSetup.add(user);

        entitiesToSetup.add(category);

        setup(entitiesToSetup.toArray());
    }

    @Test
    public void getCategories() throws Exception
    {
        Category c1 = categoryGenerator.createUniqueInstance();
        Category c2 = categoryGenerator.createUniqueInstance();
        setup(c1, c2);

        String categoriesURI = resolveCategoriesURI();
        ClientResponse response = get(categoriesURI, CategoriesDto.MEDIA_TYPE);

        CategoriesDto dtos = response.getEntity(CategoriesDto.class);
        assertEquals(dtos.getCollection().size(), 3);
    }

    @Test
    public void postCategory() throws Exception
    {
        Category cat1 = categoryGenerator.createUniqueInstance();
        CategoryDto dto = createCategoryDto(cat1);

        String href = resolveCategoriesURI();

        // Assert response code is OK
        ClientResponse response = post(href, dto, SYSADMIN, SYSADMIN);
        assertEquals(response.getStatusCode(), 201);

        // Ensure all the fields are the same than before but with an id assigned
        dto = response.getEntity(CategoryDto.class);

        assertNotNull(dto.getId());
        assertEquals(dto.getName(), cat1.getName());
    }

    @Test
    public void postCategoryRaises409WhenDuplicatedName()
    {
        CategoryDto cat = createCategoryDto(category);

        String href = resolveCategoriesURI();
        ClientResponse response = post(href, cat);

        assertErrors(response, Status.CONFLICT.getStatusCode(), APIError.CATEGORY_DUPLICATED_NAME);
    }

    // ********************* Helper methods ************************
    private CategoryDto createCategoryDto(final Category category)
    {
        CategoryDto dto = new CategoryDto();
        dto.setId(category.getId());
        dto.setName(category.getName());
        dto.setErasable(category.isErasable());
        dto.setDefaultCategory(category.isDefaultCategory());
        return dto;
    }
}
