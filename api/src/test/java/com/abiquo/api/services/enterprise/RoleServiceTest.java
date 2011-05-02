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

import javax.persistence.EntityManager;

import org.springframework.security.context.SecurityContextHolder;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import com.abiquo.api.common.AbstractGeneratorTest;
import com.abiquo.api.common.Assert;
import com.abiquo.api.common.SysadminAuthentication;
import com.abiquo.api.exceptions.APIError;
import com.abiquo.api.exceptions.NotFoundException;
import com.abiquo.api.services.RoleService;
import com.abiquo.api.spring.security.SecurityService;
import com.abiquo.server.core.enterprise.Enterprise;
import com.abiquo.server.core.enterprise.Privilege;
import com.abiquo.server.core.enterprise.Role;
import com.abiquo.server.core.enterprise.RoleDto;
import com.abiquo.server.core.enterprise.User;
import com.softwarementors.bzngine.engines.jpa.EntityManagerHelper;

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
        r = roleGenerator.createInstance(p1, p2);
        u = userGenerator.createInstance(e, r, "sysadmin", "sysadmin");

        setup(e, p1, p2, r, u);

        SecurityContextHolder.getContext().setAuthentication(new SysadminAuthentication());

    }

    @Test
    public void modifyRole()
    {
        Role oldRole = roleGenerator.createInstance();
        Role oldRoleBlocked = roleGenerator.createInstanceBlocked();
        setup(oldRole, oldRoleBlocked);

        RoleDto rl = new RoleDto();
        rl.setId(oldRole.getId());
        rl.setName("newRoleName");
        RoleDto rlBloked = new RoleDto();
        rlBloked.setId(oldRoleBlocked.getId());
        rlBloked.setName("newRoleBlokedName");
        rlBloked.setBlocked(true);

        EntityManager em = getEntityManagerWithAnActiveTransaction();
        RoleService service = new RoleService(em);

        service.modifyRole(oldRole.getId(), rl);
        EntityManagerHelper.commit(em);

        EntityManagerHelper.beginRollbackTransaction(em);
        Role newRole = service.getRole(oldRole.getId());
        Assert.assertNotNull(newRole);
        Assert.assertEquals(newRole.getName(), rl.getName());

        try
        {
            service.modifyRole(oldRoleBlocked.getId(), rlBloked);
            Assert.fail("");
        }
        catch (NotFoundException e)
        {
            Assert.assertEquals(APIError.NON_MODIFICABLE_ROLE.getCode(), e.getCode());
        }
        finally
        {
            rollbackActiveTransaction(em);
        }

    }

}
