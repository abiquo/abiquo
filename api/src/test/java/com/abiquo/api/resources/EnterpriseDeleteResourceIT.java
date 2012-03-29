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

import java.util.ArrayList;
import java.util.List;

import javax.ws.rs.core.Response.Status;

import org.apache.wink.client.ClientResponse;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import com.abiquo.api.exceptions.APIError;
import com.abiquo.server.core.appslibrary.VirtualMachineTemplate;
import com.abiquo.server.core.cloud.VirtualDatacenter;
import com.abiquo.server.core.enterprise.Enterprise;
import com.abiquo.server.core.enterprise.EnterpriseDto;
import com.abiquo.server.core.enterprise.Privilege;
import com.abiquo.server.core.enterprise.Role;
import com.abiquo.server.core.enterprise.User;

public class EnterpriseDeleteResourceIT extends AbstractJpaGeneratorIT
{

    @BeforeMethod
    public void setupSysadmin()
    {
        Enterprise e = enterpriseGenerator.createUniqueInstance();
        Role r = roleGenerator.createInstanceSysAdmin();
        User u = userGenerator.createInstance(e, r, "sysadmin", "sysadmin");

        List<Object> entitiesToSetup = new ArrayList<Object>();

        entitiesToSetup.add(e);
        entitiesToSetup.add(r);
        entitiesToSetup.add(u);
        for (Privilege p : r.getPrivileges())
        {
            entitiesToSetup.add(p);
        }
        setup(entitiesToSetup.toArray());

    }

    @Test
    public void shouldDeleteEnterpriseWhenDoesNotContainVirtualDatacenters()
    {
        Enterprise enterprise = enterpriseGenerator.createUniqueInstance();
        setup(enterprise);

        String uri = resolveEnterpriseURI(enterprise.getId());

        ClientResponse response = delete(uri, "sysadmin", "sysadmin");
        assertEquals(response.getStatusCode(), Status.NO_CONTENT.getStatusCode());

        response = get(uri, "sysadmin", "sysadmin", EnterpriseDto.MEDIA_TYPE);
        assertEquals(response.getStatusCode(), Status.NOT_FOUND.getStatusCode());

    }

    @Test
    public void shouldNotDeleteEnterpriseWhenContainsVirtualDatacenters()
    {
        VirtualDatacenter vdc = vdcGenerator.createUniqueInstance();
        setup(vdc.getEnterprise(), vdc.getDatacenter(), vdc.getNetwork(), vdc);

        String uri = resolveEnterpriseURI(vdc.getEnterprise().getId());

        ClientResponse response = delete(uri, "sysadmin", "sysadmin");
        assertErrors(response, 409, APIError.ENTERPRISE_DELETE_ERROR_WITH_VDCS.getCode());

    }

    @Test
    public void shouldDeleteEnterpriseWhenContainsVirtualImages()
    {
        VirtualMachineTemplate image = virtualMachineTemplateGenerator.createUniqueInstance();
        setup(image.getRepository().getDatacenter(), image.getRepository(), image.getEnterprise(),
            image.getCategory(), image);

        String uri = resolveEnterpriseURI(image.getEnterprise().getId());

        ClientResponse response = delete(uri, "sysadmin", "sysadmin");
        assertEquals(response.getStatusCode(), 204);

    }

    @Test
    public void shouldDeleteEnterpriseWhenContainsUsers()
    {
        User user = userGenerator.createUniqueInstance();

        List<Object> entitiesToSetup = new ArrayList<Object>();

        entitiesToSetup.add(user.getEnterprise());

        for (Privilege p : user.getRole().getPrivileges())
        {
            entitiesToSetup.add(p);
        }
        entitiesToSetup.add(user.getRole());
        entitiesToSetup.add(user);

        setup(entitiesToSetup.toArray());

        String uri = resolveEnterpriseURI(user.getEnterprise().getId());

        ClientResponse response = delete(uri, "sysadmin", "sysadmin");
        assertEquals(response.getStatusCode(), 204);

    }
}
