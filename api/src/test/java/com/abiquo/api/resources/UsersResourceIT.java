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

import static com.abiquo.api.common.UriTestResolver.resolveRoleURI;
import static com.abiquo.api.common.UriTestResolver.resolveUsersURI;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;

import java.util.Collections;

import javax.ws.rs.core.MediaType;

import org.apache.wink.client.ClientResponse;
import org.apache.wink.client.Resource;
import org.apache.wink.common.internal.utils.UriHelper;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import com.abiquo.model.rest.RESTLink;
import com.abiquo.server.core.enterprise.Enterprise;
import com.abiquo.server.core.enterprise.Role;
import com.abiquo.server.core.enterprise.User;
import com.abiquo.server.core.enterprise.UserDto;
import com.abiquo.server.core.enterprise.UsersDto;

public class UsersResourceIT extends AbstractJpaGeneratorIT
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
        tearDown("session", "user", "enterprise", "role");
    }

    @Test
    public void getUsersList() throws Exception
    {
        User user = userGenerator.createUniqueInstance();
        setup(user.getRole(), user.getEnterprise(), user);
        Resource resource = client.resource(resolveUsersURI(user.getEnterprise().getId()));

        ClientResponse response = resource.accept(MediaType.APPLICATION_XML).get();

        assertEquals(response.getStatusCode(), 200);

        UsersDto entity = response.getEntity(UsersDto.class);

        assertNotNull(entity);
        assertNotNull(entity.getCollection());
        assertEquals(entity.getCollection().size(), 1);
    }

    @Test
    public void getUsersListDescOrder() throws Exception
    {
        User user = userGenerator.createUniqueInstance();
        User user2 = userGenerator.createInstance(user.getEnterprise(), user.getRole());
        setup(user.getRole(), user.getEnterprise(), user, user2);

        String uri = resolveUsersURI(user.getEnterprise().getId());
        uri =
            UriHelper.appendQueryParamsToPath(uri, Collections.singletonMap("desc",
                new String[] {"true"}), false);

        Resource resource = client.resource(uri);

        ClientResponse response = resource.accept(MediaType.APPLICATION_XML).get();

        assertEquals(response.getStatusCode(), 200);

        UsersDto entity = response.getEntity(UsersDto.class);

        assertNotNull(entity);
        assertNotNull(entity.getCollection());
        assertEquals(entity.getCollection().size(), 2);
    }

    @Test
    public void createUsers()
    {
        User user = userGenerator.createUniqueInstance();
        setup(user.getRole(), user.getEnterprise(), user);

        UserDto dto = getValidUser(user);

        ClientResponse response =
            post(resolveUsersURI(user.getEnterprise().getId()), dto, "sysadmin", "sysadmin");

        assertEquals(response.getStatusCode(), 201);

        assertUserResponse(dto, response);
    }

    @Test
    public void createUsersWithAvailableDatacenters()
    {
        User user = userGenerator.createUniqueInstance();
        setup(user.getRole(), user.getEnterprise(), user);

        UserDto dto = getValidUser(user);
        dto.setAvailableVirtualDatacenters("1,2");

        ClientResponse response =
            post(resolveUsersURI(user.getEnterprise().getId()), dto, "sysadmin", "sysadmin");

        assertEquals(response.getStatusCode(), 201);

        assertUserResponse(dto, response);
        UserDto entityPost = response.getEntity(UserDto.class);
        assertEquals(entityPost.getAvailableVirtualDatacenters(), dto
            .getAvailableVirtualDatacenters());
    }

    @Test
    public void findUsersConnected()
    {
        User user = userGenerator.createUserWithSession();
        User withoutSession = userGenerator.createInstance(user.getEnterprise(), user.getRole());

        setup(user.getEnterprise(), user.getRole(), user, withoutSession);

        String uri = resolveUsersURI(user.getEnterprise().getId());
        uri =
            UriHelper.appendQueryParamsToPath(uri, Collections.singletonMap("connected",
                new String[] {"true"}), false);

        ClientResponse response = get(uri);

        assertEquals(response.getStatusCode(), 200);
        UsersDto entity = response.getEntity(UsersDto.class);
        assertEquals(entity.getCollection().size(), 1);
    }

    private UserDto getValidUser(User user)
    {
        UserDto dto = new UserDto();

        dto.setActive(1);
        dto.setEmail("earl.hickey@abiquo.com");
        dto.setLocale("EN");
        dto.setName("Earl");
        dto.setPassword("karma");
        dto.setSurname("Hickey");
        dto.setNick("ehickey");
        dto.setDescription("user description");
        dto.addLink(new RESTLink(RoleResource.ROLE, resolveRoleURI(user.getRole().getId())));
        return dto;
    }

    private void assertUserResponse(UserDto dto, ClientResponse response)
    {
        UserDto entityPost = response.getEntity(UserDto.class);

        assertNotNull(entityPost);

        assertEquals(dto.isActive(), entityPost.isActive());
        assertEquals(dto.getEmail(), entityPost.getEmail());
        assertEquals(dto.getLocale(), entityPost.getLocale());
        assertEquals(dto.getName(), entityPost.getName());
        assertEquals(dto.getPassword(), entityPost.getPassword());
        assertEquals(dto.getSurname(), entityPost.getSurname());
        assertEquals(dto.getNick(), entityPost.getNick());
        assertEquals(entityPost.getDescription(), dto.getDescription());
    }
}
