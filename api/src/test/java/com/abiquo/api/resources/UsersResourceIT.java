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

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.apache.wink.client.ClientResponse;
import org.apache.wink.common.internal.utils.UriHelper;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import com.abiquo.model.enumerator.Privileges;
import com.abiquo.model.rest.RESTLink;
import com.abiquo.server.core.enterprise.Enterprise;
import com.abiquo.server.core.enterprise.Privilege;
import com.abiquo.server.core.enterprise.Role;
import com.abiquo.server.core.enterprise.User;
import com.abiquo.server.core.enterprise.UserDto;
import com.abiquo.server.core.enterprise.UsersDto;

public class UsersResourceIT extends AbstractJpaGeneratorIT
{
    private static final String SYSADMIN = "sysadmin";

    private static final String ENTADMIN = "entadmin";

    private static final String USER = "user";

    @BeforeMethod
    public void setupSysadmin()
    {
        Enterprise ent = enterpriseGenerator.createUniqueInstance();
        Role role = roleGenerator.createInstanceSysAdmin();
        User user = userGenerator.createInstance(ent, role, SYSADMIN, SYSADMIN);

        List<Object> entitiesToPersist = new ArrayList<Object>();

        entitiesToPersist.add(ent);
        for (Privilege p : role.getPrivileges())
        {
            entitiesToPersist.add(p);
        }
        entitiesToPersist.add(role);
        entitiesToPersist.add(user);

        setup(entitiesToPersist.toArray());
    }

    @Test
    public void getUsersList() throws Exception
    {
        User user = userGenerator.createUniqueInstance();

        List<Object> entitiesToSetup = new ArrayList<Object>();

        for (Privilege p : user.getRole().getPrivileges())
        {
            entitiesToSetup.add(p);
        }
        entitiesToSetup.add(user.getRole());
        entitiesToSetup.add(user.getEnterprise());
        entitiesToSetup.add(user);

        setup(entitiesToSetup.toArray());

        ClientResponse response =
            get(resolveUsersURI(user.getEnterprise().getId()), SYSADMIN, SYSADMIN);

        assertEquals(response.getStatusCode(), 200);

        UsersDto entity = response.getEntity(UsersDto.class);

        assertNotNull(entity);
        assertNotNull(entity.getCollection());
        assertEquals(entity.getCollection().size(), 1);
    }

    @Test
    public void getUsersListFilteredByID() throws Exception
    {
        Enterprise e1 = enterpriseGenerator.createUniqueInstance();
        Enterprise e2 = enterpriseGenerator.createUniqueInstance();
        Role r = roleGenerator.createUniqueInstance();
        User u1 = userGenerator.createInstance(e1, r, "p1", "u1", "s1", "e1", "neck");
        User u2 = userGenerator.createInstance(e2, r, "p2", "u2", "s2", "e2", "nack");

        List<Object> entitiesToPersist = new ArrayList<Object>();
        entitiesToPersist.add(e1);
        entitiesToPersist.add(e2);
        for (Privilege p : r.getPrivileges())
        {
            entitiesToPersist.add(p);
        }
        entitiesToPersist.add(r);
        entitiesToPersist.add(u1);
        entitiesToPersist.add(u2);

        setup(entitiesToPersist.toArray());

        String uri = resolveUsersURI("_");
        uri =
            UriHelper.appendQueryParamsToPath(uri,
                Collections.singletonMap("orderBy", new String[] {"nick"}), false);
        uri =
            UriHelper.appendQueryParamsToPath(uri,
                Collections.singletonMap("filter", new String[] {u1.getNick()}), false);

        ClientResponse response = get(uri, SYSADMIN, SYSADMIN);

        assertEquals(response.getStatusCode(), 200);

        UsersDto entity = response.getEntity(UsersDto.class);

        assertNotNull(entity);
        assertNotNull(entity.getCollection());
        assertEquals(entity.getCollection().size(), 1);
        UserDto u = entity.getCollection().iterator().next();
        org.testng.Assert.assertEquals(u.getNick(), "neck");
    }

    @Test
    public void getUsersListDescOrder() throws Exception
    {
        User user = userGenerator.createUniqueInstance();
        User user2 = userGenerator.createInstance(user.getEnterprise(), user.getRole());

        List<Object> entitiesToSetup = new ArrayList<Object>();

        for (Privilege p : user.getRole().getPrivileges())
        {
            entitiesToSetup.add(p);
        }
        entitiesToSetup.add(user.getRole());
        entitiesToSetup.add(user.getEnterprise());
        entitiesToSetup.add(user);
        entitiesToSetup.add(user2);

        setup(entitiesToSetup.toArray());

        String uri = resolveUsersURI(user.getEnterprise().getId());
        uri =
            UriHelper.appendQueryParamsToPath(uri,
                Collections.singletonMap("desc", new String[] {"true"}), false);

        ClientResponse response = get(uri, SYSADMIN, SYSADMIN);

        assertEquals(response.getStatusCode(), 200);

        UsersDto entity = response.getEntity(UsersDto.class);

        assertNotNull(entity);
        assertNotNull(entity.getCollection());
        assertEquals(entity.getCollection().size(), 2);
    }

    @Test
    public void checkGetUserPermissions() throws Exception
    {
        // Create an enterprise with a user and an enterprise admin
        Enterprise ent = enterpriseGenerator.createUniqueInstance();
        Role userRole = roleGenerator.createInstance();
        Role entRole = roleGenerator.createInstanceEnterprisAdmin();
        User entUser = userGenerator.createInstance(ent, entRole, ENTADMIN, ENTADMIN);
        User user = userGenerator.createInstance(ent, userRole, USER, USER);

        List<Object> entitiesToPersist = new ArrayList<Object>();

        entitiesToPersist.add(ent);
        for (Privilege p : entRole.getPrivileges())
        {
            entitiesToPersist.add(p);
        }
        if (userRole.getPrivileges() != null)
        {
            for (Privilege p : userRole.getPrivileges())
            {
                entitiesToPersist.add(p);
            }
        }
        entitiesToPersist.add(entRole);
        entitiesToPersist.add(userRole);
        entitiesToPersist.add(entUser);
        entitiesToPersist.add(user);

        setup(entitiesToPersist.toArray());

        // Test the get response depending on the user who performs the request
        String wildwardURI = resolveUsersURI("_");
        assertUsersCount(get(wildwardURI, SYSADMIN, SYSADMIN), 3);
        assertUsersCount(get(wildwardURI, ENTADMIN, ENTADMIN), 2);
        assertUsersCount(get(wildwardURI, USER, USER), 1);

        String uri = resolveUsersURI(ent.getId());
        assertUsersCount(get(uri, SYSADMIN, SYSADMIN), 2);
        assertUsersCount(get(uri, ENTADMIN, ENTADMIN), 2);
        assertUsersCount(get(uri, USER, USER), 1);
    }

    @Test
    public void checkGetUserPermissionsInvalidEnterprise() throws Exception
    {
        // Create an enterprise with a user and an enterprise admin
        Enterprise ent = enterpriseGenerator.createUniqueInstance();
        Enterprise ent2 = enterpriseGenerator.createUniqueInstance();
        Role userRole = roleGenerator.createInstance();
        Role entRole = roleGenerator.createInstanceEnterprisAdmin();
        User entUser = userGenerator.createInstance(ent, entRole, ENTADMIN, ENTADMIN);
        User user = userGenerator.createInstance(ent, userRole, USER, USER);

        List<Object> entitiesToPersist = new ArrayList<Object>();

        entitiesToPersist.add(ent);
        entitiesToPersist.add(ent2);
        for (Privilege p : entRole.getPrivileges())
        {
            entitiesToPersist.add(p);
        }
        if (userRole.getPrivileges() != null)
        {
            for (Privilege p : userRole.getPrivileges())
            {
                entitiesToPersist.add(p);
            }
        }
        entitiesToPersist.add(entRole);
        entitiesToPersist.add(userRole);
        entitiesToPersist.add(entUser);
        entitiesToPersist.add(user);

        setup(entitiesToPersist.toArray());

        // Test the get response depending on the user who performs the request
        String uri = resolveUsersURI(ent2.getId());
        assertUsersCount(get(uri, SYSADMIN, SYSADMIN), 0);
        assertAccessDenied(get(uri, ENTADMIN, ENTADMIN));
        assertAccessDenied(get(uri, USER, USER));
    }

    @Test
    public void createUsers()
    {
        User user = userGenerator.createUniqueInstance();

        List<Object> entitiesToSetup = new ArrayList<Object>();

        for (Privilege p : user.getRole().getPrivileges())
        {
            entitiesToSetup.add(p);
        }
        entitiesToSetup.add(user.getRole());
        entitiesToSetup.add(user.getEnterprise());
        entitiesToSetup.add(user);

        setup(entitiesToSetup.toArray());

        UserDto dto = getValidUser(user);

        ClientResponse response =
            post(resolveUsersURI(user.getEnterprise().getId()), dto, SYSADMIN, SYSADMIN);

        assertEquals(response.getStatusCode(), 201);

        assertUserResponse(dto, response);
    }

    @Test
    public void createUsersWithAvailableDatacenters()
    {
        User user = userGenerator.createUniqueInstance();

        List<Object> entitiesToSetup = new ArrayList<Object>();

        Privilege pToRemove = null;
        for (Privilege p : user.getRole().getPrivileges())
        {
            if (!p.getName().equals(Privileges.USERS_PROHIBIT_VDC_RESTRICTION.name()))
            {
                entitiesToSetup.add(p);
            }
            else
            {
                pToRemove = p;
            }
        }
        if (pToRemove != null)
        {
            user.getRole().getPrivileges().remove(pToRemove);
        }
        entitiesToSetup.add(user.getRole());
        entitiesToSetup.add(user.getEnterprise());
        entitiesToSetup.add(user);

        setup(entitiesToSetup.toArray());

        UserDto dto = getValidUser(user);
        dto.setAvailableVirtualDatacenters("1,2");

        ClientResponse response =
            post(resolveUsersURI(user.getEnterprise().getId()), dto, SYSADMIN, SYSADMIN);

        assertEquals(response.getStatusCode(), 201);

        assertUserResponse(dto, response);
        UserDto entityPost = response.getEntity(UserDto.class);
        assertEquals(entityPost.getAvailableVirtualDatacenters(),
            dto.getAvailableVirtualDatacenters());
    }

    @Test
    public void findUsersConnected()
    {
        User user = userGenerator.createUserWithSession();
        User withoutSession = userGenerator.createInstance(user.getEnterprise(), user.getRole());

        List<Object> entitiesToSetup = new ArrayList<Object>();

        for (Privilege p : user.getRole().getPrivileges())
        {
            entitiesToSetup.add(p);
        }
        entitiesToSetup.add(user.getRole());
        entitiesToSetup.add(user.getEnterprise());
        entitiesToSetup.add(user);
        entitiesToSetup.add(withoutSession);

        setup(entitiesToSetup.toArray());

        String uri = resolveUsersURI(user.getEnterprise().getId());
        uri =
            UriHelper.appendQueryParamsToPath(uri,
                Collections.singletonMap("connected", new String[] {"true"}), false);

        ClientResponse response = get(uri, SYSADMIN, SYSADMIN);

        assertEquals(response.getStatusCode(), 200);
        UsersDto entity = response.getEntity(UsersDto.class);
        assertEquals(entity.getCollection().size(), 1);
    }

    private UserDto getValidUser(final User user)
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

    private void assertUserResponse(final UserDto dto, final ClientResponse response)
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

    private void assertUsersCount(final ClientResponse response, final int userCount)
    {
        assertEquals(response.getStatusCode(), 200);

        UsersDto entity = response.getEntity(UsersDto.class);

        assertNotNull(entity);
        assertNotNull(entity.getCollection());
        assertEquals(entity.getCollection().size(), userCount);
    }

    private void assertAccessDenied(final ClientResponse response)
    {
        assertEquals(response.getStatusCode(), 403);
    }

    @Override
    protected ClientResponse get(final String uri, final String username, final String password)
    {
        return super.get(uri, username, password, AbstractResource.LINK_MEDIA_TYPE,
            AbstractResource.LINK_MEDIA_TYPE);
    }
}
