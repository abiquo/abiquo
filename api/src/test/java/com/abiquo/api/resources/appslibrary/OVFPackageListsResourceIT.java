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

import static com.abiquo.api.common.UriTestResolver.resolveOVFPackageListsURI;
import static com.abiquo.testng.TestConfig.APPS_INTEGRATION_TESTS;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;

import java.util.ArrayList;
import java.util.List;

import javax.ws.rs.core.MediaType;

import org.apache.wink.client.ClientResponse;
import org.testng.annotations.Listeners;
import org.testng.annotations.Test;

import com.abiquo.api.resources.AbstractJpaGeneratorIT;
import com.abiquo.server.core.appslibrary.AppsLibrary;
import com.abiquo.server.core.appslibrary.OVFPackage;
import com.abiquo.server.core.appslibrary.OVFPackageList;
import com.abiquo.server.core.appslibrary.OVFPackageListDto;
import com.abiquo.server.core.appslibrary.OVFPackageListsDto;
import com.abiquo.server.core.enterprise.Enterprise;

@Listeners( {com.abiquo.testng.TestServerAndOVFListener.class})
public class OVFPackageListsResourceIT extends AbstractJpaGeneratorIT
{

    private String validURI;

    @Test(groups = {APPS_INTEGRATION_TESTS})
    public void getOVFPackagesListsByEnterprise() throws Exception
    {

        OVFPackageList list = new OVFPackageList("new", "http://listurl.com/index.xml");

        OVFPackage ovf0 = ovfPackageGenerator.createUniqueInstance();
        list.addToOvfPackages(ovf0);

        Enterprise ent = enterpriseGenerator.createUniqueInstance();
        AppsLibrary app = appsLibraryGenerator.createUniqueInstance();
        app.setEnterprise(ent);

        ovf0.setAppsLibrary(app);
        list.setAppsLibrary(app);

        List<Object> entitiesToSetup = new ArrayList<Object>();

        entitiesToSetup.add(ent);
        entitiesToSetup.add(app);
        entitiesToSetup.add(ovf0.getCategory());
        entitiesToSetup.add(ovf0.getIcon());
        entitiesToSetup.add(ovf0);
        entitiesToSetup.add(list);
        setup(entitiesToSetup.toArray());

        validURI = resolveOVFPackageListsURI(ent.getId());

        ClientResponse response = get(validURI);
        assertEquals(response.getStatusCode(), 200);

        OVFPackageListsDto entity = response.getEntity(OVFPackageListsDto.class);
        assertNotNull(entity);
        assertNotNull(entity.getCollection());
        assertEquals(entity.getCollection().size(), 1);
    }

    @Test(groups = {APPS_INTEGRATION_TESTS})
    public void createEmptyOVFPackageList()
    {
        OVFPackageListDto packageList = new OVFPackageListDto(); // empty list
        packageList.setName("Empty List");
        packageList.setUrl("http://listurl.com/index.xml");

        Enterprise ent = enterpriseGenerator.createUniqueInstance();
        AppsLibrary app = appsLibraryGenerator.createUniqueInstance();
        app.setEnterprise(ent);
        setup(ent, app);

        validURI = resolveOVFPackageListsURI(ent.getId());

        ClientResponse response = post(validURI, packageList);

        assertEquals(response.getStatusCode(), 201);

        OVFPackageListDto entityPost = response.getEntity(OVFPackageListDto.class);
        assertNotNull(entityPost);
        assertEquals(packageList.getName(), entityPost.getName());
    }

    @Test(groups = {APPS_INTEGRATION_TESTS})
    public void createOVFPackageList()
    {

        Enterprise ent = enterpriseGenerator.createUniqueInstance();
        AppsLibrary app = appsLibraryGenerator.createUniqueInstance();
        app.setEnterprise(ent);
        setup(ent, app);

        validURI = resolveOVFPackageListsURI(ent.getId());

        String xmlindexURI = "http://localhost:7979/testovf/ovfindex.xml";

        ClientResponse response =
            client.resource(validURI).accept(MediaType.APPLICATION_XML).contentType(
                MediaType.TEXT_PLAIN).post(xmlindexURI);

        assertEquals(response.getStatusCode(), 201);

        OVFPackageListDto entityPost = response.getEntity(OVFPackageListDto.class);
        assertNotNull(entityPost);
        assertEquals(entityPost.getName(), "Abiquo Official Repository");
    }
}
