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

import java.util.Collection;

import org.springframework.security.context.SecurityContextHolder;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import com.abiquo.api.common.AbstractGeneratorTest;
import com.abiquo.api.common.Assert;
import com.abiquo.api.common.SysadminAuthenticationStub;
import com.abiquo.api.common.UriTestResolver;
import com.abiquo.api.exceptions.ExtendedAPIException;
import com.abiquo.api.services.UserService;
import com.abiquo.model.rest.RESTLink;
import com.abiquo.server.core.enterprise.Enterprise;
import com.abiquo.server.core.enterprise.Role;
import com.abiquo.server.core.enterprise.User;
import com.abiquo.server.core.enterprise.UserDto;

public class UserServiceTest extends AbstractGeneratorTest
{

    Enterprise e;

    Role r;

    User u;

    @BeforeMethod
    public void setupSysadmin()
    {
        e = enterpriseGenerator.createUniqueInstance();
        r = roleGenerator.createInstance();
        u = userGenerator.createInstance(e, r, "sysadmin", "sysadmin");
        setup(e, r, u);

        SecurityContextHolder.getContext().setAuthentication(new SysadminAuthenticationStub());
    }

    @Override
    @AfterMethod
    public void tearDown()
    {
        tearDown("virtualapp", "ip_pool_management", "rasd_management", "virtualdatacenter",
            "vlan_network", "network_configuration", "dhcp_service", "virtualmachine",
            "hypervisor", "physicalmachine", "rack", "datacenter", "virtualimage", "user",
            "enterprise", "role", "privilege");
    }

    @Test
    public void findUsersWithEnterpriseWildcard()
    {
        Enterprise e1 = enterpriseGenerator.createUniqueInstance();
        Enterprise e2 = enterpriseGenerator.createUniqueInstance();
        User u1 = userGenerator.createInstance(e1);
        User u2 = userGenerator.createInstance(e2);

        setup(e1, e2, u1.getRole(), u2.getRole(), u1, u2);

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

        setup(e1, e2, r, u1, u2);

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

        setup(e1, e2, r, u1, u2);

        UserService service = new UserService(getEntityManagerWithAnActiveTransaction());

        Collection<User> users = service.getUsersByEnterprise("_", null, "nick", false);
        User u = users.iterator().next();
        Assert.assertEquals(u.getNick(), "nack");

        users = service.getUsersByEnterprise("_", null, "nick", true);
        u = users.iterator().next();
        Assert.assertEquals(u.getNick(), "sysadmin");
    }

    @Test
    public void addUserWithDuplicatedNick()
    {
        UserService service = new UserService(getEntityManagerWithAnActiveTransaction());

        UserDto dto = new UserDto("foo", "foo", "foo@foo.com", u.getNick(), "foo", "ES", "");

        try
        {
            service.addUser(dto, e.getId(), r);
            Assert.fail("");
        }
        catch (ExtendedAPIException e)
        {
            Assert.assertSize(e.getErrors(), 1);
        }
    }

    @Test
    public void updateUserWithDuplicatedNick()
    {
        User u2 = userGenerator.createInstance(e, r, "p2", "u2", "s2", "e2", "nack");

        setup(u2);

        UserService service = new UserService(getEntityManagerWithAnActiveTransaction());

        UserDto dto = new UserDto("foo", "foo", "foo@foo.com", u.getNick(), "foo", "ES", "");
        String roleURI = UriTestResolver.resolveRoleURI(r.getId());
        dto.addLink(new RESTLink("role", roleURI));

        dto.setId(u2.getId());

        try
        {
            service.modifyUser(u2.getId(), dto);
            Assert.fail("");
        }
        catch (ExtendedAPIException e)
        {
            Assert.assertSize(e.getErrors(), 1);
        }
    }
}
