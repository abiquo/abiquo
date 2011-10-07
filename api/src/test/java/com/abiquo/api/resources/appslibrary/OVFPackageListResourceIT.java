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

import static com.abiquo.api.common.UriTestResolver.resolveOVFPackageListURI;
import static com.abiquo.api.common.UriTestResolver.resolveOVFPackageURI;
import static com.abiquo.testng.TestConfig.APPS_INTEGRATION_TESTS;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.ws.rs.core.MediaType;

import org.apache.wink.client.ClientResponse;
import org.apache.wink.client.ClientWebException;
import org.apache.wink.client.Resource;
import org.apache.wink.client.RestClient;
import org.testng.annotations.Test;

import com.abiquo.api.common.UriTestResolver;
import com.abiquo.api.resources.AbstractJpaGeneratorIT;
import com.abiquo.server.core.appslibrary.AppsLibrary;
import com.abiquo.server.core.appslibrary.Category;
import com.abiquo.server.core.appslibrary.Icon;
import com.abiquo.server.core.appslibrary.OVFPackage;
import com.abiquo.server.core.appslibrary.OVFPackageDto;
import com.abiquo.server.core.appslibrary.OVFPackageList;
import com.abiquo.server.core.appslibrary.OVFPackageListDto;
import com.abiquo.server.core.appslibrary.OVFPackageListsDto;
import com.abiquo.server.core.enterprise.Enterprise;
import com.abiquo.server.core.infrastructure.Datacenter;

public class OVFPackageListResourceIT extends AbstractJpaGeneratorIT
{

    protected Category category;

    protected Enterprise enterprise;

    protected Datacenter datacenter;

    protected OVFPackage ovfPackage;

    protected OVFPackageList list;

    protected AppsLibrary appsLibrary;

    protected Icon icon;

    private String validOVFPackageList = resolveOVFPackageListURI(1, 1);

    @Test(groups = {APPS_INTEGRATION_TESTS})
    public void getOVFPackageList()
    {
        category = categoryGeneartor.createUniqueInstance();
        icon = iconGenerator.createUniqueInstance();
        enterprise = enterpriseGenerator.createUniqueInstance();
        appsLibrary = appsLibraryGenerator.createUniqueInstance();
        appsLibrary.setEnterprise(enterprise);
        ovfPackage = ovfPackageGenerator.createInstance(appsLibrary, category, icon);

        OVFPackage ovfPackage1 = ovfPackageGenerator.createInstance(appsLibrary, category, icon);
        OVFPackage ovfPackage2 = ovfPackageGenerator.createInstance(appsLibrary, category, icon);
        List<OVFPackage> listofpackages = new ArrayList<OVFPackage>();

        list = new OVFPackageList("ovfPackageList_1", "http://www.abiquo.com");
        ovfPackage.addToOvfPackageLists(list);
        ovfPackage1.addToOvfPackageLists(list);
        ovfPackage2.addToOvfPackageLists(list);

        list.addToOvfPackages(ovfPackage);
        list.addToOvfPackages(ovfPackage1);
        list.addToOvfPackages(ovfPackage2);
        list.setOvfPackages(listofpackages);
        list.setAppsLibrary(appsLibrary);
        List<Object> entitiesToSetup = new ArrayList<Object>();

        entitiesToSetup.add(appsLibrary.getEnterprise());
        entitiesToSetup.add(appsLibrary);
        entitiesToSetup.add(category);
        entitiesToSetup.add(icon);
        entitiesToSetup.add(ovfPackage);
        entitiesToSetup.add(ovfPackage1);
        entitiesToSetup.add(ovfPackage2);

        entitiesToSetup.add(list);

        setup(entitiesToSetup.toArray());

        ClientResponse response =
            get(UriTestResolver.resolveOVFPackageListsURI(enterprise.getId()));

        OVFPackageListsDto lists = response.getEntity(OVFPackageListsDto.class);
        assertNotNull(lists);
        assertEquals(lists.getCollection().size(), 1);

        for (OVFPackageListDto o : lists.getCollection())
        {
            response = get(UriTestResolver.resolveOVFPackageListURI(enterprise.getId(), o.getId()));
            OVFPackageListDto result = response.getEntity(OVFPackageListDto.class);
            assertNotNull(result);
            assertEquals(result.getName(), "ovfPackageList_1");
        }
    }

    private OVFPackageListDto getValidOVFPackageList()
    {
        RestClient client = new RestClient();
        Resource resource = client.resource(validOVFPackageList);

        ClientResponse response = resource.accept(MediaType.APPLICATION_XML).get();
        assertEquals(response.getStatusCode(), 200);
        return response.getEntity(OVFPackageListDto.class);
    }

    @Test
    public void modifyOVFPackageList()
    {

        OVFPackageListDto ovfPackageListDto = getValidOVFPackageList();
        assertNotNull(ovfPackageListDto);
        assertEquals(ovfPackageListDto.getName(), "ovfPackageList_1");

        // modifications
        ovfPackageListDto.setName("changed_name");

        List<OVFPackageDto> newList = ovfPackageListDto.getOvfPackages();
        newList.remove(0);
        ovfPackageListDto.setOvfPackages(newList);

        // ovfPackageListDto.getOvfPackages().remove(0);
        // assertEquals(ovfPackageListDto.getOvfPackages().size(), 2);

        Resource resource = client.resource(validOVFPackageList);
        ClientResponse response =
            resource.contentType(MediaType.APPLICATION_XML).accept(MediaType.APPLICATION_XML).put(
                ovfPackageListDto);

        assertEquals(response.getStatusCode(), 200);

        OVFPackageListDto retrievedOVFPackageListDto = getValidOVFPackageList();
        assertNotNull(retrievedOVFPackageListDto);
        assertEquals(retrievedOVFPackageListDto.getName(), "changed_name");
        assertEquals(retrievedOVFPackageListDto.getOvfPackages().size(), 2);

    }

    @Test
    public void deleteOVFPackageList() throws ClientWebException
    {
        OVFPackageListDto ovfPackageListDto = getValidOVFPackageList();
        assertNotNull(ovfPackageListDto);

        Resource resource = client.resource(validOVFPackageList);
        ClientResponse response =
            resource.contentType(MediaType.APPLICATION_XML).accept(MediaType.APPLICATION_XML)
                .delete();

        assertEquals(response.getStatusCode(), 204);

        response = resource.accept(MediaType.APPLICATION_XML).get();
        assertEquals(response.getStatusCode(), 404);
    }

    @Test(enabled = false)
    public void deleteOVFPackageFromList() throws ClientWebException
    {
        OVFPackageListDto ovfPackageListDto = getValidOVFPackageList();
        assertNotNull(ovfPackageListDto);
        assertEquals(ovfPackageListDto.getOvfPackages().size(), 3);

        // OVFPackageDto pack = ovfPackageListDto.getOvfPackages().get(0);

        // Resource resource = client.resource(resolveOVFPackageURI(1, pack
        // .getId()));
        Resource resource = client.resource(resolveOVFPackageURI(1, 1));
        ClientResponse response =
            resource.contentType(MediaType.APPLICATION_XML).accept(MediaType.APPLICATION_XML)
                .delete();
        assertEquals(response.getStatusCode(), 204);

        // Checks if item is removed from list

        OVFPackageListDto retrievedOVFPackageListDto = getValidOVFPackageList();
        assertNotNull(retrievedOVFPackageListDto);
        assertEquals(retrievedOVFPackageListDto.getOvfPackages().size(), 2);

    }

}
