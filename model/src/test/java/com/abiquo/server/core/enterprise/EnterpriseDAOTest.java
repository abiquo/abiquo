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
import javax.persistence.OptimisticLockException;

import org.testng.Assert;
import org.testng.annotations.Test;

import com.abiquo.server.core.common.Limit;
import com.abiquo.server.core.common.persistence.DefaultDAOTestBase;
import com.softwarementors.bzngine.engines.jpa.EntityManagerHelper;
import com.softwarementors.bzngine.entities.test.PersistentInstanceTester;

@Test
public class EnterpriseDAOTest extends DefaultDAOTestBase<EnterpriseDAO, Enterprise>
{

    @Override
    protected EnterpriseDAO createDao(EntityManager entityManager)
    {
        return new EnterpriseDAO(entityManager);
    }

    @Override
    protected PersistentInstanceTester<Enterprise> createEntityInstanceGenerator()
    {
        return new EnterpriseGenerator(getSeed());
    }

    @Override
    public EnterpriseGenerator eg()
    {
        return (EnterpriseGenerator) super.eg();
    }

    @Test
    public void test_toMakeCodeCoverageHappy_aboutDefaultDaoBase()
    {
        Enterprise enterprise = createUniqueEntity();
        Enterprise enterprise2 = createUniqueEntity();
        ds().persistAll(enterprise, enterprise2);

        EnterpriseDAO dao = createDaoForRollbackTransaction();
        Assert.assertFalse(dao.isManaged2(enterprise));
        Assert.assertTrue(dao.isManaged2(reload(dao, enterprise)));
    }

    @Test
    public void existsAnyWithName()
    {
        Enterprise enterprise1 = eg().createInstance("abcd");
        ds().persistAll(enterprise1);

        EnterpriseDAO dao = createDaoForReadWriteTransaction();

        assertTrue(dao.existsAnyWithName("aBCd"));

        assertFalse(dao.existsAnyWithName("inexistent"));
    }

    @Test
    public void existsAnyOtherWithName()
    {
        Enterprise enterprise1 = eg().createInstance("abcd");
        Enterprise enterprise2 = eg().createInstance("mnbc");
        ds().persistAll(enterprise1, enterprise2);

        EnterpriseDAO dao = createDaoForRollbackTransaction();
        Enterprise enterprise1b = reload(dao, enterprise1);
        assertFalse(dao.existsAnyOtherWithName(enterprise1b, "abcd"));
        assertFalse(dao.existsAnyOtherWithName(enterprise1b, "inexistent"));

        assertTrue(dao.existsAnyOtherWithName(enterprise1b, "mnbc"));
    }

    // CODE_COVERAGE: wrong report by Emma
    @Test
    // (expectedExceptions=OptimisticLockException.class)
    public void test_concurrentModification()
    {
        Enterprise enterprise1 = eg().createInstance("abcd");
        ds().persistAll(enterprise1);

        EnterpriseDAO dao = createDaoForReadWriteTransaction();
        Enterprise enterprise1B = dao.findById(enterprise1.getId());
        enterprise1B.setRamLimitsInMb(new Limit(1L, 10L));
        EnterpriseDAO dao2 = createDaoForReadWriteTransaction();
        Enterprise concurrentEnterprise = dao2.findById(enterprise1B.getId());
        EntityManagerHelper.commitAndClose(dao.getEntityManager());

        concurrentEnterprise.setHdLimitsInMb(new Limit(2L, 200L));
        try
        {
            dao2.flush();
            fail(); // /CLVER
        }
        catch (OptimisticLockException e)
        {
            // Expected!
        }
    }

    /*
     * TODO: PAG, pending of full upgrade considerations
     * @Test public void logicalRemove() { Enterprise enterprise1 = eg().createInstance("abcd");
     * ds().persistAll(enterprise1); EnterpriseDAO dao = createDaoForReadWriteTransaction();
     * Enterprise enterprise1B = dao.findById(enterprise1.getId()); enterprise1B.delete();
     * EntityManagerHelper.commitAndClose(dao.getEntityManager()); EnterpriseDAO dao2 =
     * createDaoForRollbackTransaction(); Enterprise enterprise1C =
     * dao2.findById(enterprise1.getId(), true); assertNull(dao2.findById(enterprise1.getId()));
     * assertNotNull(enterprise1C); assertTrue(enterprise1C.isDeleted()); }
     * @Test public void findById() { Enterprise enterprise1 = eg().createInstance("abcd");
     * ds().persistAll(enterprise1); EnterpriseDAO dao = createDaoForReadWriteTransaction();
     * assertNotNull(dao.findById(enterprise1.getId(), false));
     * assertNotNull(dao.findById(enterprise1.getId(), true)); reload(dao, enterprise1).delete();
     * EntityManagerHelper.commitAndClose(dao.getEntityManager()); EnterpriseDAO dao2 =
     * createDaoForRollbackTransaction(); assertNotNull(dao2.findById(enterprise1.getId(), true));
     * assertNull(dao2.findById(enterprise1.getId(), false)); }
     */

}
