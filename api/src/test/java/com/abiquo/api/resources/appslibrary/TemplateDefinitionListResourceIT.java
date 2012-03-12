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
import static com.abiquo.api.common.UriTestResolver.resolveTemplateDefinitionURI;
import static com.abiquo.testng.TestConfig.APPS_INTEGRATION_TESTS;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;

import java.util.ArrayList;
import java.util.List;

import org.apache.wink.client.ClientResponse;
import org.apache.wink.client.ClientWebException;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import com.abiquo.api.common.UriTestResolver;
import com.abiquo.api.exceptions.APIError;
import com.abiquo.api.resources.AbstractJpaGeneratorIT;
import com.abiquo.server.core.appslibrary.AppsLibrary;
import com.abiquo.server.core.appslibrary.Category;
import com.abiquo.server.core.appslibrary.TemplateDefinition;
import com.abiquo.server.core.appslibrary.TemplateDefinitionList;
import com.abiquo.server.core.appslibrary.TemplateDefinitionListDto;
import com.abiquo.server.core.appslibrary.TemplateDefinitionListsDto;
import com.abiquo.server.core.enterprise.Enterprise;
import com.abiquo.server.core.enterprise.Privilege;
import com.abiquo.server.core.enterprise.Role;
import com.abiquo.server.core.enterprise.User;

public class TemplateDefinitionListResourceIT extends AbstractJpaGeneratorIT
{
    private static final String SYSADMIN = "sysadmin";

    protected Enterprise enterprise;

    protected AppsLibrary appsLibrary;

    protected Category category;

    protected TemplateDefinitionList list;

    protected TemplateDefinition templateDef0;

    @BeforeMethod(groups = {APPS_INTEGRATION_TESTS})
    public void setUpUser()
    {
        enterprise = enterpriseGenerator.createUniqueInstance();
        appsLibrary = appsLibraryGenerator.createUniqueInstance(enterprise);

        category = categoryGenerator.createUniqueInstance();

        Role role = roleGenerator.createInstanceSysAdmin();
        User user = userGenerator.createInstance(enterprise, role, SYSADMIN, SYSADMIN);
        List<Object> entitiesToSetup = new ArrayList<Object>();
        entitiesToSetup.add(enterprise);
        entitiesToSetup.add(appsLibrary);
        entitiesToSetup.add(category);

        for (Privilege p : role.getPrivileges())
        {
            entitiesToSetup.add(p);
        }
        entitiesToSetup.add(role);
        entitiesToSetup.add(user);

        setup(entitiesToSetup.toArray());

        templateDef0 = templateDefGenerator.createInstance(appsLibrary, category);
        TemplateDefinition templateDef1 =
            templateDefGenerator.createInstance(appsLibrary, category);
        TemplateDefinition templateDef2 =
            templateDefGenerator.createInstance(appsLibrary, category);

        list = new TemplateDefinitionList("templateDefinitionList_1", "http://www.abiquo.com");
        list.setAppsLibrary(appsLibrary);
        setup(list);

        templateDef0.addToTemplateDefinitionLists(list);
        templateDef1.addToTemplateDefinitionLists(list);
        templateDef2.addToTemplateDefinitionLists(list);

        list.addTemplateDefinition(templateDef0);
        list.addTemplateDefinition(templateDef1);
        list.addTemplateDefinition(templateDef2);

        // List<TemplateDefinition> listofpackages = new ArrayList<TemplateDefinition>();
        // list.setTemplateDefinitions(listofpackages);
        List<Object> entitiesToSetup2 = new ArrayList<Object>();
        entitiesToSetup2.add(templateDef0);
        entitiesToSetup2.add(templateDef1);
        entitiesToSetup2.add(templateDef2);

        setup(entitiesToSetup2.toArray());
    }

    @AfterMethod(groups = {APPS_INTEGRATION_TESTS})
    public void tearDownTest()
    {
        super.tearDown();
    }

    @Test(groups = {APPS_INTEGRATION_TESTS})
    public void getTemplateDefinitionList()
    {
        TemplateDefinitionListsDto lists = getLists();

        for (TemplateDefinitionListDto o : lists.getCollection())
        {
            ClientResponse response =
                get(UriTestResolver.resolveTemplateDefinitionListURI(enterprise.getId(), o.getId()),
                    SYSADMIN, SYSADMIN, TemplateDefinitionListDto.MEDIA_TYPE);

            TemplateDefinitionListDto result = response.getEntity(TemplateDefinitionListDto.class);
            assertNotNull(result);
            assertEquals(result.getName(), "templateDefinitionList_1");
        }
    }

    TemplateDefinitionListsDto getLists()
    {

        ClientResponse response =
            get(UriTestResolver.resolveTemplateDefinitionListsURI(enterprise.getId()), SYSADMIN,
                SYSADMIN, TemplateDefinitionListsDto.MEDIA_TYPE);

        assertEquals(response.getStatusCode(), 200);
        TemplateDefinitionListsDto lists = response.getEntity(TemplateDefinitionListsDto.class);
        assertNotNull(lists);
        assertEquals(lists.getCollection().size(), 1);
        return lists;
    }

    @Test(groups = {APPS_INTEGRATION_TESTS})
    public void modifyNonExistentTemplateDefinitionListRises404()
    {
        TemplateDefinitionListDto list = new TemplateDefinitionListDto();
        ClientResponse response =
            put(UriTestResolver.resolveTemplateDefinitionListURI(enterprise.getId(), 2), list,
                SYSADMIN, SYSADMIN, TemplateDefinitionListDto.MEDIA_TYPE);

        assertError(response, 404, APIError.NON_EXISTENT_TEMPLATE_DEFINITION_LIST);
    }

    @Test(groups = {APPS_INTEGRATION_TESTS})
    public void deleteTemplateDefinitionList()
    {
        TemplateDefinitionListsDto lists = getLists();

        for (TemplateDefinitionListDto o : lists.getCollection())
        {
            ClientResponse response =
                delete(
                    UriTestResolver.resolveTemplateDefinitionListURI(enterprise.getId(), o.getId()),
                    SYSADMIN, SYSADMIN, TemplateDefinitionListDto.MEDIA_TYPE);
            assertEquals(response.getStatusCode(), 204);
        }
    }

    @Test(groups = {APPS_INTEGRATION_TESTS})
    public void deleteTemplateDefinitionFromList() throws ClientWebException
    {
        TemplateDefinitionListsDto lists = getLists();

        ClientResponse response =
            delete(resolveTemplateDefinitionURI(enterprise.getId(), templateDef0.getId()),
                SYSADMIN, SYSADMIN, TemplateDefinitionListsDto.MEDIA_TYPE);

        assertEquals(response.getStatusCode(), 204);

        for (TemplateDefinitionListDto o : lists.getCollection())
        {
            response =
                get(UriTestResolver.resolveTemplateDefinitionListURI(enterprise.getId(), o.getId()),
                    SYSADMIN, SYSADMIN, TemplateDefinitionListDto.MEDIA_TYPE);
            assertEquals(response.getStatusCode(), 200);
            TemplateDefinitionListDto result = response.getEntity(TemplateDefinitionListDto.class);
            assertNotNull(result);
            assertEquals(result.getName(), "templateDefinitionList_1");
            assertEquals(result.getTemplateDefinitions().getCollection().size(), 2);
        }
    }
}
