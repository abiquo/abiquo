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
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import com.abiquo.api.resources.AbstractJpaGeneratorIT;
import com.abiquo.model.enumerator.DiskFormatType;
import com.abiquo.server.core.appslibrary.AppsLibrary;
import com.abiquo.server.core.appslibrary.Category;
import com.abiquo.server.core.appslibrary.Icon;
import com.abiquo.server.core.appslibrary.OVFPackage;
import com.abiquo.server.core.appslibrary.OVFPackageDto;
import com.abiquo.server.core.enterprise.Enterprise;
import com.abiquo.server.core.enterprise.Privilege;
import com.abiquo.server.core.enterprise.Role;
import com.abiquo.server.core.enterprise.User;
import com.abiquo.server.core.infrastructure.Datacenter;

public class OVFPackageResourceIT extends AbstractJpaGeneratorIT
{
    protected Category category;

    protected Enterprise enterprise;

    protected Datacenter datacenter;

    protected OVFPackage ovfPackage;

    protected AppsLibrary appsLibrary;

    protected Icon icon;

    private static final String SYSADMIN = "sysadmin";

    @BeforeMethod(groups = {APPS_INTEGRATION_TESTS})
    public void setUpUser()
    {
        enterprise = enterpriseGenerator.createUniqueInstance();
        datacenter = datacenterGenerator.createUniqueInstance();
        category = categoryGenerator.createUniqueInstance();
        icon = iconGenerator.createUniqueInstance();

        Role role = roleGenerator.createInstanceSysAdmin();
        User user = userGenerator.createInstance(enterprise, role, SYSADMIN, SYSADMIN);

        List<Object> entitiesToSetup = new ArrayList<Object>();
        entitiesToSetup.add(enterprise);
        entitiesToSetup.add(datacenter);

        for (Privilege p : role.getPrivileges())
        {
            entitiesToSetup.add(p);
        }
        entitiesToSetup.add(role);
        entitiesToSetup.add(user);

        setup(entitiesToSetup.toArray());
    }

    @Test(groups = {APPS_INTEGRATION_TESTS})
    public void getOVFPackage() throws ClientWebException
    {
        appsLibrary = appsLibraryGenerator.createUniqueInstance();
        appsLibrary.setEnterprise(enterprise);
        ovfPackage = ovfPackageGenerator.createInstance(appsLibrary, category, icon);
        ovfPackage.setDescription("ovfPackage_1");
        category.setName("category_1");
        ovfPackage.setType(DiskFormatType.UNKNOWN);

        List<Object> entitiesToSetup = new ArrayList<Object>();

        // entitiesToSetup.add(enterprise);
        entitiesToSetup.add(appsLibrary);
        entitiesToSetup.add(category);
        entitiesToSetup.add(icon);
        entitiesToSetup.add(ovfPackage);

        setup(entitiesToSetup.toArray());
        ClientResponse response =
            get(resolveOVFPackageURI(enterprise.getId(), ovfPackage.getId()), SYSADMIN, SYSADMIN);

        assertEquals(response.getStatusCode(), 200);

        OVFPackageDto ovfPackageDto = response.getEntity(OVFPackageDto.class);
        assertNotNull(ovfPackageDto);
        assertEquals(ovfPackageDto.getDescription(), "ovfPackage_1");
        assertEquals(ovfPackageDto.getDiskFormatTypeUri(), "http://unknown");
        assertEquals(ovfPackageDto.getName(), "category_1");
    }

    @Test(groups = {APPS_INTEGRATION_TESTS})
    public void modifyOVFPackage() throws ClientWebException
    {
        appsLibrary = appsLibraryGenerator.createUniqueInstance();
        appsLibrary.setEnterprise(enterprise);
        ovfPackage = ovfPackageGenerator.createInstance(appsLibrary, category, icon);

        List<Object> entitiesToSetup = new ArrayList<Object>();

        entitiesToSetup.add(appsLibrary);
        entitiesToSetup.add(category);
        entitiesToSetup.add(icon);
        entitiesToSetup.add(ovfPackage);

        setup(entitiesToSetup.toArray());
        ClientResponse response = get(resolveOVFPackageURI(enterprise.getId(), ovfPackage.getId()));

        assertEquals(response.getStatusCode(), 200);

        OVFPackageDto ovfPackageDto = response.getEntity(OVFPackageDto.class);
        assertNotNull(ovfPackageDto);

        // modifications
        ovfPackageDto.setDescription("new_description");

        response =
            put(resolveOVFPackageURI(enterprise.getId(), ovfPackageDto.getId()), ovfPackageDto,
                SYSADMIN, SYSADMIN);

        assertEquals(response.getStatusCode(), 200);
        response =
            get(resolveOVFPackageURI(enterprise.getId(), ovfPackage.getId()), SYSADMIN, SYSADMIN);
        OVFPackageDto retrievedPackageDto = response.getEntity(OVFPackageDto.class);
        assertEquals(retrievedPackageDto.getDescription(), "new_description");
    }

    @Test(groups = {APPS_INTEGRATION_TESTS})
    public void deleteOVFPackage() throws ClientWebException
    {
        appsLibrary = appsLibraryGenerator.createUniqueInstance();
        appsLibrary.setEnterprise(enterprise);
        ovfPackage = ovfPackageGenerator.createInstance(appsLibrary, category, icon);
        ovfPackage.setDescription("ovfPackage_1");
        ovfPackage.setType(DiskFormatType.UNKNOWN);

        List<Object> entitiesToSetup = new ArrayList<Object>();

        entitiesToSetup.add(appsLibrary);
        entitiesToSetup.add(category);
        entitiesToSetup.add(icon);
        entitiesToSetup.add(ovfPackage);

        setup(entitiesToSetup.toArray());

        ClientResponse response =
            delete(resolveOVFPackageURI(enterprise.getId(), ovfPackage.getId()), SYSADMIN, SYSADMIN);

        assertEquals(response.getStatusCode(), 204);

        response =
            get(resolveOVFPackageURI(enterprise.getId(), ovfPackage.getId()), SYSADMIN, SYSADMIN);
        assertEquals(response.getStatusCode(), 404);

    }
}
