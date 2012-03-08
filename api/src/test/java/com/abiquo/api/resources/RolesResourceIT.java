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

import static com.abiquo.api.common.UriTestResolver.resolveRolesURI;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.apache.wink.client.ClientResponse;
import org.apache.wink.common.internal.utils.UriHelper;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import com.abiquo.server.core.enterprise.Enterprise;
import com.abiquo.server.core.enterprise.Privilege;
import com.abiquo.server.core.enterprise.Role;
import com.abiquo.server.core.enterprise.RoleDto;
import com.abiquo.server.core.enterprise.RolesDto;
import com.abiquo.server.core.enterprise.User;

public class RolesResourceIT extends AbstractJpaGeneratorIT
{
    private static final String SYSADMIN = "sysadmin";

    protected String rolesURI = resolveRolesURI();

    @BeforeMethod
    public void setupSysadmin()
    {
        Enterprise e = enterpriseGenerator.createUniqueInstance();
        Role r = roleGenerator.createInstanceSysAdmin("sysRole");
        User u = userGenerator.createInstance(e, r, "sysadmin", "sysadmin");

        List<Object> entitiesToSetup = new ArrayList<Object>();
        entitiesToSetup.add(e);
        for (Privilege p : r.getPrivileges())
        {
            entitiesToSetup.add(p);
        }
        entitiesToSetup.add(r);
        entitiesToSetup.add(u);

        setup(entitiesToSetup.toArray());
    }

    @Test
    public void getRolesList() throws Exception
    {
        Enterprise e1 = enterpriseGenerator.createUniqueInstance();
        Enterprise e2 = enterpriseGenerator.createUniqueInstance();
        Role r1 = roleGenerator.createInstance(e1);
        Role r2 = roleGenerator.createInstance(e2);
        Role r3 = roleGenerator.createInstance();

        List<Object> entitiesToPersist = new ArrayList<Object>();
        entitiesToPersist.add(e1);
        entitiesToPersist.add(e2);
        entitiesToPersist.add(r1);
        entitiesToPersist.add(r2);
        entitiesToPersist.add(r3);
        setup(entitiesToPersist.toArray());

        ClientResponse response = get(rolesURI, SYSADMIN, SYSADMIN, RolesDto.MEDIA_TYPE);
        assertEquals(200, response.getStatusCode());

        RolesDto entity = response.getEntity(RolesDto.class);
        assertNotNull(entity);
        assertNotNull(entity.getCollection());
        assertEquals(entity.getCollection().size(), 2);

        //
        String uri = rolesURI;
        uri =
            UriHelper.appendQueryParamsToPath(uri, Collections
                .singletonMap(EnterpriseResource.ENTERPRISE_AS_PARAM, new String[] {Integer
                    .toString(e1.getId())}), false);

        response = get(uri, SYSADMIN, SYSADMIN, RolesDto.MEDIA_TYPE);

        assertEquals(response.getStatusCode(), 200);

        entity = response.getEntity(RolesDto.class);

        assertNotNull(entity);
        assertNotNull(entity.getCollection());
        assertEquals(entity.getCollection().size(), 3);

    }

    @Test
    public void getRolesListDescOrder() throws Exception
    {
        Enterprise e1 = enterpriseGenerator.createUniqueInstance();
        Role r1 = roleGenerator.createInstance("r1", e1);
        Role r2 = roleGenerator.createInstance("r2", e1);

        List<Object> entitiesToPersist = new ArrayList<Object>();
        entitiesToPersist.add(e1);
        entitiesToPersist.add(r1);
        entitiesToPersist.add(r2);

        setup(entitiesToPersist.toArray());

        String uri = rolesURI;
        uri =
            UriHelper.appendQueryParamsToPath(uri, Collections.singletonMap("desc",
                new String[] {"true"}), false);

        uri =
            UriHelper.appendQueryParamsToPath(uri, Collections
                .singletonMap(EnterpriseResource.ENTERPRISE_AS_PARAM, new String[] {Integer
                    .toString(e1.getId())}), false);

        ClientResponse response = get(uri, SYSADMIN, SYSADMIN, RolesDto.MEDIA_TYPE);

        assertEquals(response.getStatusCode(), 200);

        RolesDto entity = response.getEntity(RolesDto.class);

        assertNotNull(entity);
        assertNotNull(entity.getCollection());
        assertEquals(entity.getCollection().size(), 3);
        RoleDto r = entity.getCollection().iterator().next();

        // assertEquals(r.getName(), "sysRole");

    }

}
