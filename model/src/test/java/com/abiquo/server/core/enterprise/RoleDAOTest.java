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

package com.abiquo.server.core.enterprise;

import java.util.Collection;

import javax.persistence.EntityManager;

import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import com.abiquo.server.core.common.persistence.DefaultDAOTestBase;
import com.abiquo.server.core.common.persistence.TestDataAccessManager;
import com.softwarementors.bzngine.engines.jpa.test.configuration.EntityManagerFactoryForTesting;
import com.softwarementors.bzngine.entities.test.PersistentInstanceTester;
import com.softwarementors.commons.testng.AssertEx;

public class RoleDAOTest extends DefaultDAOTestBase<RoleDAO, Role>
{

    private EnterpriseGenerator enterpriseGenerator;

    @Override
    @BeforeMethod
    protected void methodSetUp()
    {
        super.methodSetUp();
        this.enterpriseGenerator = new EnterpriseGenerator(getSeed());
    }

    @Override
    protected RoleDAO createDao(final EntityManager entityManager)
    {
        return new RoleDAO(entityManager);
    }

    @Override
    protected PersistentInstanceTester<Role> createEntityInstanceGenerator()
    {
        return new RoleGenerator(getSeed());
    }

    @Override
    protected EntityManagerFactoryForTesting getFactory()
    {
        return TestDataAccessManager.getFactory();
    }

    @Override
    public RoleGenerator eg()
    {
        return (RoleGenerator) super.eg();
    }

    @Test
    public void findRoles()
    {
        Role role1 = eg().createInstance();
        Enterprise enterprise = this.enterpriseGenerator.createUniqueInstance();
        Role role2 = eg().createInstance(enterprise);

        ds().persistAll(role1, enterprise, role2);

        RoleDAO dao = createDaoForRollbackTransaction();

        Collection<Role> roles = dao.find(enterprise, null, null, false, 0, 25);
        AssertEx.assertSize(roles, 2);

        roles = dao.find(null, null, null, false, 0, 25);
        AssertEx.assertSize(roles, 1);

    }

}
