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

import javax.ws.rs.core.MediaType;

import org.apache.wink.client.ClientResponse;
import org.apache.wink.client.Resource;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.Test;

import com.abiquo.server.core.enterprise.Role;
import com.abiquo.server.core.enterprise.RolesDto;

public class RolesResourceIT extends AbstractJpaGeneratorIT
{
    private String rolesURI = resolveRolesURI();

    @AfterMethod
    public void tearDown()
    {
        tearDown("role");
    }

    @Test
    public void getRolesList() throws Exception
    {
        Role role = roleGenerator.createUniqueInstance();
        setup(role);

        Resource resource = client.resource(rolesURI).accept(MediaType.APPLICATION_XML);

        ClientResponse response = resource.get();
        assertEquals(200, response.getStatusCode());

        RolesDto entity = response.getEntity(RolesDto.class);
        assertNotNull(entity);
        assertNotNull(entity.getCollection());
        assertEquals(entity.getCollection().size(), 1);
    }
}
