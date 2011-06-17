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

import javax.persistence.EntityManager;

import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import com.abiquo.server.core.common.persistence.DefaultDAOTestBase;
import com.abiquo.server.core.common.persistence.TestDataAccessManager;
import com.softwarementors.bzngine.engines.jpa.test.configuration.EntityManagerFactoryForTesting;
import com.softwarementors.bzngine.entities.test.PersistentInstanceTester;
import com.softwarementors.commons.testng.AssertEx;

public class RoleLdapDAOTest extends DefaultDAOTestBase<RoleLdapDAO, RoleLdap>
{

    @Override
    @BeforeMethod
    protected void methodSetUp()
    {
        super.methodSetUp();
        // FIXME: Remember to add all entities that have to be removed during tearDown in the
        // method:
        // com.abiquo.server.core.common.persistence.TestDataAccessManager.initializePersistentInstanceRemovalSupport
    }

    @Override
    protected RoleLdapDAO createDao(final EntityManager entityManager)
    {
        return new RoleLdapDAO(entityManager);
    }

    @Override
    protected PersistentInstanceTester<RoleLdap> createEntityInstanceGenerator()
    {
        return new RoleLdapGenerator(getSeed());
    }

    @Override
    protected EntityManagerFactoryForTesting getFactory()
    {
        return TestDataAccessManager.getFactory();
    }

    @Override
    public RoleLdapGenerator eg()
    {
        return (RoleLdapGenerator) super.eg();
    }

    @Test(enabled = false)
    public void findByType()
    {
        RoleLdap ldapRole = eg().createUniqueInstance();
        ds().persistAll(ldapRole);
        RoleLdapDAO dao = createDaoForReadWriteTransaction();

        RoleLdap role = dao.findByType(ldapRole.getLdapRole());
        try
        {
            AssertEx.assertPropertiesEqual(ldapRole, role, RoleLdap.ROLE_PROPERTY);
        }
        catch (Exception e)
        {
            AssertEx.fail(e.getMessage());
        }
    }

    @Test
    @Override
    public void test_findAll()
    {
        // LdapRole entity = createUniqueEntity();
        // LdapRole entityB = createUniqueEntity();
        // List<Object> auxiliaryEntitiesToSaveBefore = new ArrayList<Object>();
        // eg().addAuxiliaryEntitiesToPersist(entity, auxiliaryEntitiesToSaveBefore);
        // eg().addAuxiliaryEntitiesToPersist(entityB, auxiliaryEntitiesToSaveBefore);
        // persistAll(ds(), auxiliaryEntitiesToSaveBefore, entity, entityB);
        //
        // LdapRoleDAO dao = createDaoForRollbackTransaction();
        //
        // List<LdapRole> all = dao.findAll();
        // List<LdapRole> entitiesToCheck = new ArrayList();
        // entitiesToCheck.add(entity);
        // entitiesToCheck.add(entityB);
        // PersistentEntityTestHelper.assertEqualCollectionsById(all, entitiesToCheck);
    }

}
