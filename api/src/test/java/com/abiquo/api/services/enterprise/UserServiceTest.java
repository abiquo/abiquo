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

package com.abiquo.api.services.enterprise;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.persistence.EntityManager;

import org.springframework.security.context.SecurityContextHolder;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import com.abiquo.api.common.AbstractUnitTest;
import com.abiquo.api.common.Assert;
import com.abiquo.api.common.SysadminAuthentication;
import com.abiquo.api.common.UriTestResolver;
import com.abiquo.api.exceptions.APIException;
import com.abiquo.api.exceptions.BadRequestException;
import com.abiquo.api.services.UserService;
import com.abiquo.model.enumerator.Privileges;
import com.abiquo.model.rest.RESTLink;
import com.abiquo.server.core.enterprise.Enterprise;
import com.abiquo.server.core.enterprise.Privilege;
import com.abiquo.server.core.enterprise.Role;
import com.abiquo.server.core.enterprise.User;
import com.abiquo.server.core.enterprise.UserDto;

public class UserServiceTest extends AbstractUnitTest
{
    private Enterprise e;

    private Role r;

    private User u;

    @BeforeMethod
    public void setupSysadmin()
    {
        e = enterpriseGenerator.createUniqueInstance();
        Privilege p1 = new Privilege(Privileges.USERS_MANAGE_OTHER_ENTERPRISES);
        Privilege p2 = new Privilege(Privileges.USERS_MANAGE_USERS);
        r = roleGenerator.createInstance(p1, p2);
        u = userGenerator.createInstance(e, r, "sysadmin", "sysadmin");

        setup(e, p1, p2, r, u);

        SecurityContextHolder.getContext().setAuthentication(new SysadminAuthentication());

    }

    @Test
    public void findUsersWithEnterpriseWildcard()
    {
        Enterprise e1 = enterpriseGenerator.createUniqueInstance();
        Enterprise e2 = enterpriseGenerator.createUniqueInstance();
        User u1 = userGenerator.createInstance(e1);
        User u2 = userGenerator.createInstance(e2);

        List<Object> entitiesToPersist = new ArrayList<Object>();
        entitiesToPersist.add(e1);
        entitiesToPersist.add(e2);
        for (Privilege p : u1.getRole().getPrivileges())
        {
            entitiesToPersist.add(p);
        }
        for (Privilege p : u2.getRole().getPrivileges())
        {
            entitiesToPersist.add(p);
        }
        entitiesToPersist.add(u1.getRole());
        entitiesToPersist.add(u2.getRole());
        entitiesToPersist.add(u1);
        entitiesToPersist.add(u2);

        setup(entitiesToPersist.toArray());

        UserService service = new UserService(getEntityManagerWithAnActiveTransaction());

        Collection<User> users = service.getUsersByEnterprise("_", null, null, false);
        Assert.assertSize(users, 3);
    }

    @Test
    public void findUsersFiltered()
    {
        Enterprise e1 = enterpriseGenerator.createUniqueInstance();
        Enterprise e2 = enterpriseGenerator.createUniqueInstance();
        Role r = roleGenerator.createUniqueInstance();
        User u1 = userGenerator.createInstance(e1, r, "p1", "u1", "s1", "e1", "n1");
        User u2 = userGenerator.createInstance(e2, r, "p2", "u2", "s2", "e2", "n2");

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

        UserService service = new UserService(getEntityManagerWithAnActiveTransaction());

        Collection<User> users = service.getUsersByEnterprise("_", u1.getName(), null, false);
        Assert.assertSize(users, 1);

        users = service.getUsersByEnterprise("_", u1.getSurname(), null, false);
        Assert.assertSize(users, 1);

        users = service.getUsersByEnterprise("_", u1.getEmail(), null, false);
        Assert.assertSize(users, 1);

        users = service.getUsersByEnterprise("_", u1.getNick(), null, false);
        Assert.assertSize(users, 1);
    }

    @Test
    public void findUsersOrdered()
    {
        Enterprise e1 = enterpriseGenerator.createUniqueInstance();
        Enterprise e2 = enterpriseGenerator.createUniqueInstance();
        Role r = roleGenerator.createUniqueInstance();
        User u1 = userGenerator.createInstance(e1, r, "p1", "u1", "s1", "e1", "nick");
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

        UserService service = new UserService(getEntityManagerWithAnActiveTransaction());

        Collection<User> users = service.getUsersByEnterprise("_", null, "nick", false);
        User u = users.iterator().next();
        org.testng.Assert.assertEquals(u.getNick(), "nack");

        users = service.getUsersByEnterprise("_", null, "nick", true);
        u = users.iterator().next();
        org.testng.Assert.assertEquals(u.getNick(), "sysadmin");
    }

    @Test
    public void addUserWithDuplicatedNick()
    {
        EntityManager em = getEntityManagerWithAnActiveTransaction();
        UserService service = new UserService(em);

        UserDto dto =
            new UserDto("foo",
                "foo",
                "foo@foo.com",
                u.getNick(),
                "foo",
                "ES",
                "",
                User.AuthType.ABIQUO.name());

        try
        {
            service.addUser(dto, e.getId(), r);
            org.testng.Assert.fail("");
        }
        catch (APIException e)
        {
            Assert.assertSize(e.getErrors(), 1);
        }
        finally
        {
            rollbackActiveTransaction(em);
        }
    }

    @Test
    public void updateUserWithDuplicatedNick()
    {
        User u2 = userGenerator.createInstance(e, r, "p2", "u2", "s2", "e2", "nack");

        setup(u2);

        EntityManager em = getEntityManagerWithAnActiveTransaction();
        UserService service = new UserService(em);

        UserDto dto =
            new UserDto("foo",
                "foo",
                "foo@foo.com",
                u.getNick(),
                "foo",
                "ES",
                "",
                User.AuthType.ABIQUO.name());
        String roleURI = UriTestResolver.resolveRoleURI(r.getId());
        dto.addLink(new RESTLink("role", roleURI));

        dto.setId(u2.getId());

        try
        {
            service.modifyUser(u2.getId(), dto);
            org.testng.Assert.fail("");
        }
        catch (APIException e)
        {
            Assert.assertSize(e.getErrors(), 1);
        }
        finally
        {
            rollbackActiveTransaction(em);
        }
    }

    @Test(expectedExceptions = {BadRequestException.class})
    public void addUserWithOutNick()
    {
        EntityManager em = getEntityManagerWithAnActiveTransaction();
        UserService service = new UserService(em);

        UserDto dto =
            new UserDto("foo", "foo", "foo@foo.com", null, "foo", "ES", "", User.AuthType.ABIQUO
                .name());

        service.addUser(dto, e.getId(), r);

    }

    @Test(expectedExceptions = {BadRequestException.class})
    public void addUserWithEmptyName()
    {
        EntityManager em = getEntityManagerWithAnActiveTransaction();
        UserService service = new UserService(em);

        UserDto dto =
            new UserDto("", "foo", "foo@foo.com", "newuser", "foo", "ES", "", User.AuthType.ABIQUO
                .name());

        service.addUser(dto, e.getId(), r);

    }

    @Test(expectedExceptions = {BadRequestException.class})
    public void addUserWithEmptyNick()
    {
        EntityManager em = getEntityManagerWithAnActiveTransaction();
        UserService service = new UserService(em);

        UserDto dto =
            new UserDto("foo", "foo", "foo@foo.com", "", "foo", "ES", "", User.AuthType.ABIQUO
                .name());

        service.addUser(dto, e.getId(), r);

    }
}
