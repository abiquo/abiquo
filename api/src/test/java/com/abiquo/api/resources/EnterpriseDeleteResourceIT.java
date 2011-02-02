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

import static com.abiquo.api.common.Assert.assertErrors;
import static com.abiquo.api.common.UriTestResolver.resolveEnterpriseURI;
import static org.testng.Assert.assertEquals;

import org.apache.wink.client.ClientResponse;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import com.abiquo.api.exceptions.APIError;
import com.abiquo.server.core.cloud.VirtualDatacenter;
import com.abiquo.server.core.cloud.VirtualImage;
import com.abiquo.server.core.enterprise.Enterprise;
import com.abiquo.server.core.enterprise.Role;
import com.abiquo.server.core.enterprise.User;

public class EnterpriseDeleteResourceIT extends AbstractJpaGeneratorIT
{

    @BeforeMethod
    public void setupSysadmin()
    {
        Enterprise e = enterpriseGenerator.createUniqueInstance();
        Role r = roleGenerator.createInstance(Role.Type.SYS_ADMIN);

        User u = userGenerator.createInstance(e, r, "sysadmin", "sysadmin");
        setup(e, r, u);
    }

    @AfterMethod
    public void tearDown()
    {
        tearDown("user", "role", "enterprise");
    }

    @Test
    public void shouldDeleteEnterpriseWhenDoesNotContainVirtualDatacenters()
    {
        Enterprise enterprise = enterpriseGenerator.createUniqueInstance();
        setup(enterprise);

        String uri = resolveEnterpriseURI(enterprise.getId());

        ClientResponse response = delete(uri, "sysadmin", "sysadmin");
        assertEquals(response.getStatusCode(), 204);

        response = get(uri, "sysadmin", "sysadmin");
        assertEquals(response.getStatusCode(), 404);

        tearDown("user", "role", "enterprise");
    }

    @Test
    public void shouldNotDeleteEnterpriseWhenContainsVirtualDatacenters()
    {
        VirtualDatacenter vdc = vdcGenerator.createUniqueInstance();
        setup(vdc.getEnterprise(), vdc.getDatacenter(), vdc.getNetwork(), vdc);

        String uri = resolveEnterpriseURI(vdc.getEnterprise().getId());

        ClientResponse response = delete(uri, "sysadmin", "sysadmin");
        assertEquals(response.getStatusCode(), 400);

        assertErrors(response, APIError.ENTERPRISE_DELETE_ERROR_WITH_VDCS.getCode());

        tearDown("virtualdatacenter", "user", "role", "enterprise", "datacenter", "network");
    }

    @Test
    public void shouldDeleteEnterpriseWhenContainsVirtualImages()
    {
        VirtualImage image = virtualImageGenerator.createUniqueInstance();
        setup(image.getEnterprise(), image);

        String uri = resolveEnterpriseURI(image.getEnterprise().getId());

        ClientResponse response = delete(uri, "sysadmin", "sysadmin");
        assertEquals(response.getStatusCode(), 204);

        tearDown("virtualimage", "user", "role", "enterprise");
    }

    @Test
    public void shouldDeleteEnterpriseWhenContainsUsers()
    {
        User user = userGenerator.createUniqueInstance();
        setup(user.getEnterprise(), user.getRole(), user);

        String uri = resolveEnterpriseURI(user.getEnterprise().getId());

        ClientResponse response = delete(uri, "sysadmin", "sysadmin");
        assertEquals(response.getStatusCode(), 204);

        tearDown("user", "role", "enterprise");
    }
}
