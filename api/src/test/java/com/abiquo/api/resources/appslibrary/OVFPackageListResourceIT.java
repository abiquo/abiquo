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

import static com.abiquo.api.common.UriTestResolver.resolveOVFPackageURI;
import static com.abiquo.testng.TestConfig.APPS_INTEGRATION_TESTS;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;

import java.util.ArrayList;
import java.util.List;

import org.apache.wink.client.ClientResponse;
import org.apache.wink.client.ClientWebException;
import org.testng.annotations.Test;

import com.abiquo.api.common.UriTestResolver;
import com.abiquo.api.resources.AbstractJpaGeneratorIT;
import com.abiquo.server.core.appslibrary.AppsLibrary;
import com.abiquo.server.core.appslibrary.Category;
import com.abiquo.server.core.appslibrary.OVFPackage;
import com.abiquo.server.core.appslibrary.OVFPackageList;
import com.abiquo.server.core.appslibrary.OVFPackageListDto;
import com.abiquo.server.core.appslibrary.OVFPackageListsDto;
import com.abiquo.server.core.config.Icon;
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

    @Test(groups = {APPS_INTEGRATION_TESTS})
    public void modifyOVFPackageList()
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
            result.setName("newName");
            response =
                put(UriTestResolver.resolveOVFPackageListURI(enterprise.getId(), o.getId()), result);
            result = response.getEntity(OVFPackageListDto.class);
            assertEquals(result.getName(), "newName");
        }

    }

    @Test(groups = {APPS_INTEGRATION_TESTS})
    public void deleteOVFPackageList()
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
            response =
                delete(UriTestResolver.resolveOVFPackageListURI(enterprise.getId(), o.getId()));
            assertEquals(response.getStatusCode(), 204);
        }
    }

    @Test(groups = {APPS_INTEGRATION_TESTS})
    public void deleteOVFPackageFromList() throws ClientWebException
    {
        {
            category = categoryGeneartor.createUniqueInstance();
            icon = iconGenerator.createUniqueInstance();
            enterprise = enterpriseGenerator.createUniqueInstance();
            appsLibrary = appsLibraryGenerator.createUniqueInstance();
            appsLibrary.setEnterprise(enterprise);
            ovfPackage = ovfPackageGenerator.createInstance(appsLibrary, category, icon);

            OVFPackage ovfPackage1 =
                ovfPackageGenerator.createInstance(appsLibrary, category, icon);
            OVFPackage ovfPackage2 =
                ovfPackageGenerator.createInstance(appsLibrary, category, icon);
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

            response = delete(resolveOVFPackageURI(enterprise.getId(), ovfPackage.getId()));

            for (OVFPackageListDto o : lists.getCollection())
            {
                response =
                    get(UriTestResolver.resolveOVFPackageListURI(enterprise.getId(), o.getId()));
                assertEquals(response.getStatusCode(), 200);
                OVFPackageListDto result = response.getEntity(OVFPackageListDto.class);
                assertNotNull(result);
                assertEquals(result.getName(), "ovfPackageList_1");
                assertEquals(result.getOvfPackages().size(), 2);
            }
        }
    }
}
