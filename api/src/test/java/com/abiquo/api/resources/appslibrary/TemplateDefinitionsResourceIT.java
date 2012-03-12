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

import static com.abiquo.api.common.UriTestResolver.resolveTemplateDefinitionsURI;
import static com.abiquo.testng.TestConfig.APPS_INTEGRATION_TESTS;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;

import java.util.ArrayList;
import java.util.List;

import org.apache.wink.client.ClientResponse;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import com.abiquo.api.resources.AbstractJpaGeneratorIT;
import com.abiquo.model.enumerator.DiskFormatType;
import com.abiquo.model.rest.RESTLink;
import com.abiquo.server.core.appslibrary.AppsLibrary;
import com.abiquo.server.core.appslibrary.Category;
import com.abiquo.server.core.appslibrary.TemplateDefinition;
import com.abiquo.server.core.appslibrary.TemplateDefinitionDto;
import com.abiquo.server.core.appslibrary.TemplateDefinitionsDto;
import com.abiquo.server.core.enterprise.Enterprise;
import com.abiquo.server.core.enterprise.Privilege;
import com.abiquo.server.core.enterprise.Role;
import com.abiquo.server.core.enterprise.User;

@Test(groups = {APPS_INTEGRATION_TESTS})
public class TemplateDefinitionsResourceIT extends AbstractJpaGeneratorIT
{

    private static final String SYSADMIN = "sysadmin";

    Enterprise enterprise;

    AppsLibrary appslib;

    @BeforeMethod(groups = {APPS_INTEGRATION_TESTS})
    public void setUpUser()
    {
        enterprise = enterpriseGenerator.createUniqueInstance();
        appslib = appsLibraryGenerator.createUniqueInstance(enterprise);

        Role role = roleGenerator.createInstanceSysAdmin();
        User user = userGenerator.createInstance(enterprise, role, SYSADMIN, SYSADMIN);

        List<Object> entitiesToSetup = new ArrayList<Object>();
        entitiesToSetup.add(enterprise);
        entitiesToSetup.add(appslib);

        for (Privilege p : role.getPrivileges())
        {
            entitiesToSetup.add(p);
        }
        entitiesToSetup.add(role);
        entitiesToSetup.add(user);

        setup(entitiesToSetup.toArray());
    }

    @AfterMethod
    @Override
    public void tearDown()
    {
        super.tearDown();
    }

    @Test(groups = {APPS_INTEGRATION_TESTS})
    public void getTemplateDefinitionsLists() throws Exception
    {
        // Resource resource = client.resource(ovfPackagesURI).accept(MediaType.APPLICATION_XML);

        Category category = categoryGenerator.createUniqueInstance();

        TemplateDefinition ovfPackage0 = templateDefGenerator.createInstance(appslib, category);
        TemplateDefinition ovfPackage1 = templateDefGenerator.createInstance(appslib, category);
        TemplateDefinition ovfPackage2 = templateDefGenerator.createInstance(appslib, category);
        List<Object> entitiesToSetup = new ArrayList<Object>();

        entitiesToSetup.add(category);
        entitiesToSetup.add(ovfPackage0);
        entitiesToSetup.add(ovfPackage1);
        entitiesToSetup.add(ovfPackage2);

        setup(entitiesToSetup.toArray());

        ClientResponse response =
            get(resolveTemplateDefinitionsURI(enterprise.getId()), SYSADMIN, SYSADMIN,
                    TemplateDefinitionsDto.MEDIA_TYPE);

        assertEquals(response.getStatusCode(), 200);

        TemplateDefinitionsDto entity = response.getEntity(TemplateDefinitionsDto.class);
        assertNotNull(entity);
        assertNotNull(entity.getCollection());
        assertEquals(entity.getCollection().size(), 3);
    }

    @Test(groups = {APPS_INTEGRATION_TESTS})
    public void createTemplateDefinition()
    {
        TemplateDefinitionDto p = new TemplateDefinitionDto();
        p.setDescription("test_created_desc");
        p.setProductName("created_dec");
        p.setDiskFileSize(100l);

        p.setUrl("http://www.abiquo.com");
        p.setDiskFormatType(String.valueOf(DiskFormatType.UNKNOWN.name())); // test this is a
        // necessary
        // field
        RESTLink categoryLink = new RESTLink(CategoryResource.CATEGORY, "");
        categoryLink.setTitle("category_1");
        p.addLink(categoryLink);

        p.setIconUrl("http://www.google.com/logos/2011/Albert_Szent_Gyorgyi-2011-hp.jpg");

        ClientResponse response =
            post(resolveTemplateDefinitionsURI(enterprise.getId()), p, SYSADMIN, SYSADMIN);

        assertEquals(response.getStatusCode(), 201);

        TemplateDefinitionDto entityPost = response.getEntity(TemplateDefinitionDto.class);
        assertNotNull(entityPost);
        assertEquals(p.getDescription(), entityPost.getDescription());
        assertEquals(p.getDiskFormatType(), entityPost.getDiskFormatType());
        assertEquals(p.getName(), entityPost.getName());
    }
}
