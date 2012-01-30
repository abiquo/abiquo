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
import static com.abiquo.api.common.UriTestResolver.resolveTemplateDefinitionListsURI;
import static com.abiquo.testng.TestConfig.APPS_INTEGRATION_TESTS;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;

import java.util.ArrayList;
import java.util.List;

import javax.ws.rs.core.MediaType;

import org.apache.wink.client.ClientResponse;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import com.abiquo.api.exceptions.APIError;
import com.abiquo.api.resources.AbstractJpaGeneratorIT;
import com.abiquo.server.core.appslibrary.AppsLibrary;
import com.abiquo.server.core.appslibrary.TemplateDefinition;
import com.abiquo.server.core.appslibrary.TemplateDefinitionList;
import com.abiquo.server.core.appslibrary.TemplateDefinitionListDto;
import com.abiquo.server.core.appslibrary.TemplateDefinitionListsDto;
import com.abiquo.server.core.enterprise.Enterprise;
import com.abiquo.server.core.enterprise.Privilege;
import com.abiquo.server.core.enterprise.Role;
import com.abiquo.server.core.enterprise.User;

@Test(groups = {APPS_INTEGRATION_TESTS})
public class TemplateDefinitionListsResourceIT extends AbstractJpaGeneratorIT
{

    private String validURI;

    private Enterprise enterprise;

    private static final String SYSADMIN = "sysadmin";

    @BeforeMethod(groups = {APPS_INTEGRATION_TESTS})
    public void setUpUser()
    {
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

        setup(entitiesToSetup.toArray());
    }

    @Test(groups = {APPS_INTEGRATION_TESTS})
    public void getTemplateDefinitionsListsByEnterprise() throws Exception
    {

        TemplateDefinitionList list = new TemplateDefinitionList("new", "http://listurl.com/index.xml");

        TemplateDefinition tempDef0 = templateDefGenerator.createUniqueInstance();
        list.addTemplateDefinition(tempDef0);

        AppsLibrary app = appsLibraryGenerator.createUniqueInstance();
        app.setEnterprise(enterprise);

        tempDef0.setAppsLibrary(app);
        list.setAppsLibrary(app);

        List<Object> entitiesToSetup = new ArrayList<Object>();

        entitiesToSetup.add(app);
        entitiesToSetup.add(tempDef0.getCategory());
        entitiesToSetup.add(tempDef0.getIcon());
        entitiesToSetup.add(tempDef0);
        entitiesToSetup.add(list);
        setup(entitiesToSetup.toArray());

        validURI = resolveTemplateDefinitionListsURI(enterprise.getId());

        ClientResponse response = get(validURI, SYSADMIN, SYSADMIN);
        assertEquals(response.getStatusCode(), 200);

        TemplateDefinitionListsDto entity = response.getEntity(TemplateDefinitionListsDto.class);
        assertNotNull(entity);
        assertNotNull(entity.getCollection());
        assertEquals(entity.getCollection().size(), 1);
    }

    @Test(groups = {APPS_INTEGRATION_TESTS})
    public void getTemplateDefinitionListsByNonExistentEnterpriseRises404() throws Exception
    {
        validURI = resolveTemplateDefinitionListsURI(2);
        ClientResponse response = get(validURI);
        assertError(response, 404, APIError.NON_EXISTENT_ENTERPRISE);
    }

    @Test(groups = {APPS_INTEGRATION_TESTS})
    public void createEmptyTemplateDefinitionList()
    {
        TemplateDefinitionListDto templateDefList = new TemplateDefinitionListDto(); // empty list
        templateDefList.setName("Empty List");
        templateDefList.setUrl("http://listurl.com/index.xml");
        AppsLibrary app = appsLibraryGenerator.createUniqueInstance();
        app.setEnterprise(enterprise);
        setup(app);

        validURI = resolveTemplateDefinitionListsURI(enterprise.getId());

        ClientResponse response = post(validURI, templateDefList, SYSADMIN, SYSADMIN);

        assertEquals(response.getStatusCode(), 201);

        TemplateDefinitionListDto entityPost = response.getEntity(TemplateDefinitionListDto.class);
        assertNotNull(entityPost);
        assertEquals(templateDefList.getName(), entityPost.getName());
    }

    @Test(groups = {APPS_INTEGRATION_TESTS})
    public void createTemplateDefinitionList()
    {

        AppsLibrary app = appsLibraryGenerator.createUniqueInstance();
        app.setEnterprise(enterprise);
        setup(app);

        validURI = resolveTemplateDefinitionListsURI(enterprise.getId());

        String xmlindexURI = "http://localhost:7979/testovf/ovfindex.xml";

        String basicAuth = basicAuth(SYSADMIN, SYSADMIN);

        ClientResponse response =
            client.resource(validURI).accept(MediaType.APPLICATION_XML)
                .contentType(MediaType.TEXT_PLAIN).header("Authorization", "Basic " + basicAuth)
                .post(xmlindexURI);

        assertEquals(response.getStatusCode(), 201);

        TemplateDefinitionListDto entityPost = response.getEntity(TemplateDefinitionListDto.class);
        assertNotNull(entityPost);
        assertEquals(entityPost.getName(), "Abiquo Official Repository");
    }

    @Test(groups = {APPS_INTEGRATION_TESTS})
    public void createTemplateDefinitionListFromOtherXml()
    {

        AppsLibrary app = appsLibraryGenerator.createUniqueInstance();
        app.setEnterprise(enterprise);
        setup(app);

        validURI = resolveTemplateDefinitionListsURI(enterprise.getId());

        String xmlindexURI = "http://localhost:7979/testovf/anyother.xml";

        String basicAuth = basicAuth(SYSADMIN, SYSADMIN);

        ClientResponse response =
            client.resource(validURI).accept(MediaType.APPLICATION_XML)
                .contentType(MediaType.TEXT_PLAIN).header("Authorization", "Basic " + basicAuth)
                .post(xmlindexURI);

        assertEquals(response.getStatusCode(), 201);

        TemplateDefinitionListDto entityPost = response.getEntity(TemplateDefinitionListDto.class);
        assertNotNull(entityPost);
        assertEquals(entityPost.getName(), "Abiquo Official Repository");
    }

    @Test(groups = {APPS_INTEGRATION_TESTS})
    public void createTemplateDefinitionListwithBadURLRises404()
    {

        AppsLibrary app = appsLibraryGenerator.createUniqueInstance();
        app.setEnterprise(enterprise);
        setup(app);

        validURI = resolveTemplateDefinitionListsURI(enterprise.getId());

        String badURL = "http://localhost:7979/testovf/nonexistent/ovfindex.xml";

        String basicAuth = basicAuth(SYSADMIN, SYSADMIN);

        ClientResponse response =
            client.resource(validURI).accept(MediaType.APPLICATION_XML)
                .contentType(MediaType.TEXT_PLAIN).header("Authorization", "Basic " + basicAuth)
                .post(badURL);

        assertError(response, 404, APIError.NON_EXISTENT_REPOSITORY_SPACE);
    }

    @Test(groups = {APPS_INTEGRATION_TESTS})
    public void createTemplateDefinitionListBadFormatXMLrises400()
    {

        AppsLibrary app = appsLibraryGenerator.createUniqueInstance();
        app.setEnterprise(enterprise);
        setup(app);

        validURI = resolveTemplateDefinitionListsURI(enterprise.getId());

        String xmlindexURI = "http://localhost:7979/testovf/invalidovfindex/ovfindex.xml";

        ClientResponse response =
            client.resource(validURI).accept(MediaType.APPLICATION_XML)
                .contentType(MediaType.TEXT_PLAIN).post(xmlindexURI);

        assertError(response, 400, APIError.INVALID_OVF_INDEX_XML);
    }
}
