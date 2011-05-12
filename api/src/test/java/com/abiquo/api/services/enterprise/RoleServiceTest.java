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

import org.springframework.security.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import com.abiquo.api.common.AbstractGeneratorTest;
import com.abiquo.api.common.Assert;
import com.abiquo.api.common.SysadminAuthentication;
import com.abiquo.api.services.RoleService;
import com.abiquo.api.spring.security.SecurityService;
import com.abiquo.server.core.enterprise.Enterprise;
import com.abiquo.server.core.enterprise.Privilege;
import com.abiquo.server.core.enterprise.Role;
import com.abiquo.server.core.enterprise.User;

@Service
@Transactional(readOnly = true, propagation = Propagation.REQUIRED)
public class RoleServiceTest extends AbstractGeneratorTest
{
    private Enterprise e;

    private Role r;

    private User u;

    @BeforeMethod
    public void setupSysadmin()
    {
        e = enterpriseGenerator.createUniqueInstance();
        Privilege p1 = new Privilege(SecurityService.OTHER_ENTERPRISES_PRIVILEGE);
        Privilege p2 = new Privilege(SecurityService.OTHER_USERS_PRIVILEGE);
        r = roleGenerator.createInstance("sys", p1, p2);
        u = userGenerator.createInstance(e, r, "sysadmin", "sysadmin");

        setup(e, p1, p2, r, u);

        SecurityContextHolder.getContext().setAuthentication(new SysadminAuthentication());

    }

    @Test
    public void findRoles()
    {
        Enterprise e1 = enterpriseGenerator.createUniqueInstance();
        Enterprise e2 = enterpriseGenerator.createUniqueInstance();
        Role r1 = roleGenerator.createInstance(e1);
        Role r2 = roleGenerator.createInstance(e2);

        List<Object> entitiesToPersist = new ArrayList<Object>();
        entitiesToPersist.add(e1);
        entitiesToPersist.add(e2);
        entitiesToPersist.add(r1);
        entitiesToPersist.add(r2);

        setup(entitiesToPersist.toArray());

        RoleService service = new RoleService(getEntityManagerWithAnActiveTransaction());

        Collection<Role> roles = service.getRolesByEnterprise(e1.getId(), null, null, false);
        Assert.assertSize(roles, 2);

        roles = service.getRolesByEnterprise(e2.getId(), null, null, false);
        Assert.assertSize(roles, 2);

        // There is the adminRole, without enterprise, created before each method.
        roles = service.getRolesByEnterprise(0, null, null, false);
        Assert.assertSize(roles, 1);
    }

    @Test
    public void findRolesFiltered()
    {
        Enterprise e1 = enterpriseGenerator.createUniqueInstance();
        Role r1 = roleGenerator.createInstance("r1", e1);
        Role r2 = roleGenerator.createInstance("r2", e1);

        List<Object> entitiesToPersist = new ArrayList<Object>();
        entitiesToPersist.add(e1);
        entitiesToPersist.add(r1);
        entitiesToPersist.add(r2);

        setup(entitiesToPersist.toArray());

        RoleService service = new RoleService(getEntityManagerWithAnActiveTransaction());

        Collection<Role> roles =
            service.getRolesByEnterprise(e1.getId(), r1.getName(), null, false);
        Assert.assertSize(roles, 1);

        roles = service.getRolesByEnterprise(e1.getId(), r2.getName(), null, false);
        Assert.assertSize(roles, 1);

        roles = service.getRolesByEnterprise(e1.getId(), "Any", null, false);
        Assert.assertSize(roles, 0);

    }

    @Test
    public void findRolesOrdered()
    {
        Enterprise e1 = enterpriseGenerator.createUniqueInstance();
        Role r1 = roleGenerator.createInstance("r1", e1);
        Role r2 = roleGenerator.createInstance("r2", e1);

        List<Object> entitiesToPersist = new ArrayList<Object>();
        entitiesToPersist.add(e1);
        entitiesToPersist.add(r1);
        entitiesToPersist.add(r2);

        setup(entitiesToPersist.toArray());

        RoleService service = new RoleService(getEntityManagerWithAnActiveTransaction());

        Collection<Role> roles = service.getRolesByEnterprise(e1.getId(), null, "name", false);
        Role r = roles.iterator().next();
        Assert.assertEquals(r.getName(), "r1");

        roles = service.getRolesByEnterprise(e1.getId(), null, "name", true);
        r = roles.iterator().next();
        Assert.assertEquals(u.getNick(), "sysadmin");
    }

}
