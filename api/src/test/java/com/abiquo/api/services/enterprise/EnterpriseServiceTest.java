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
import java.util.List;

import javax.persistence.EntityManager;

import org.springframework.security.context.SecurityContextHolder;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import com.abiquo.api.common.AbstractUnitTest;
import com.abiquo.api.common.Assert;
import com.abiquo.api.common.SysadminAuthentication;
import com.abiquo.api.exceptions.APIError;
import com.abiquo.api.exceptions.APIException;
import com.abiquo.api.exceptions.NotFoundException;
import com.abiquo.api.services.EnterpriseService;
import com.abiquo.model.enumerator.DiskFormatType;
import com.abiquo.server.core.appslibrary.AppsLibrary;
import com.abiquo.server.core.appslibrary.TemplateDefinition;
import com.abiquo.server.core.appslibrary.TemplateDefinitionList;
import com.abiquo.server.core.enterprise.Enterprise;
import com.abiquo.server.core.enterprise.Privilege;
import com.abiquo.server.core.enterprise.Role;
import com.abiquo.server.core.enterprise.RoleLdap;
import com.abiquo.server.core.enterprise.User;
import com.softwarementors.bzngine.engines.jpa.EntityManagerHelper;

public class EnterpriseServiceTest extends AbstractUnitTest
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

        SecurityContextHolder.getContext().setAuthentication(new SysadminAuthentication());
    }

    @Test(enabled = false)
    public void deleteEnterpriseWithAppsLibrary()
    {
        Enterprise enterprise = enterpriseGenerator.createUniqueInstance();
        AppsLibrary library = new AppsLibrary(enterprise);
        TemplateDefinition ovfPackage = new TemplateDefinition();
        TemplateDefinitionList packageList = new TemplateDefinitionList();

        packageList.setAppsLibrary(library);
        ovfPackage.setAppsLibrary(library);
        ovfPackage.setType(DiskFormatType.RAW);
        ovfPackage.setUrl("http://localhost");

        setup(enterprise, library, ovfPackage, packageList);

        Integer id = enterprise.getId();

        EntityManager em = getEntityManager();
        EntityManagerHelper.beginReadWriteTransaction(em);

        EnterpriseService service = new EnterpriseService(em);

        service.removeEnterprise(id);

        EntityManagerHelper.commit(em);

        EntityManagerHelper.beginRollbackTransaction(em);
        service = new EnterpriseService(em);

        try
        {
            service.getEnterprise(id);
            Assert.fail("");
        }
        catch (NotFoundException e)
        {
            Assert.assertEquals(e.getMessage(), APIError.NON_EXISTENT_ENTERPRISE.getMessage());
        }
    }

    @Test(enabled = false)
    public void deleteUserEnterprise()
    {
        EntityManager em = getEntityManagerWithAnActiveTransaction();

        EnterpriseService service = new EnterpriseService(em);
        try
        {
            service.removeEnterprise(e.getId());
            Assert.fail("");
        }
        catch (APIException e)
        {
            Assert.assertEquals(e.getErrors().iterator().next(),
                APIError.ENTERPRISE_DELETE_OWN_ENTERPRISE);
        }

    }

    @Test
    public void getRoleLdap()
    {
        RoleLdap rl = roleLdapGenerator.createUniqueInstance();

        List<Object> entitiesToSetup = new ArrayList<Object>();

        for (Privilege p : rl.getRole().getPrivileges())
        {
            entitiesToSetup.add(p);
        }
        entitiesToSetup.add(rl.getRole());
        entitiesToSetup.add(rl);

        setup(entitiesToSetup.toArray());

        EntityManager em = getEntityManagerWithAnActiveTransaction();
        EnterpriseService service = new EnterpriseService(em);

        RoleLdap roleLdap = service.getRoleLdap(rl.getRoleLdap());
        Assert.assertNotNull(roleLdap);
        Assert.assertEquals(roleLdap.getId(), rl.getId());

    }
}
