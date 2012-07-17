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

import java.util.ArrayList;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.OptimisticLockException;

import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import com.abiquo.server.core.cloud.VirtualDatacenter;
import com.abiquo.server.core.cloud.VirtualDatacenterGenerator;
import com.abiquo.server.core.common.Limit;
import com.abiquo.server.core.common.persistence.DefaultDAOTestBase;
import com.abiquo.server.core.infrastructure.storage.VolumeManagement;
import com.abiquo.server.core.infrastructure.storage.VolumeManagementGenerator;
import com.softwarementors.bzngine.engines.jpa.EntityManagerHelper;
import com.softwarementors.bzngine.entities.test.PersistentInstanceTester;

@Test
public class EnterpriseDAOTest extends DefaultDAOTestBase<EnterpriseDAO, Enterprise>
{
    private VirtualDatacenterGenerator vdcGenerator;

    private VolumeManagementGenerator volumeGenerator;

    @Override
    @BeforeMethod
    protected void methodSetUp()
    {
        super.methodSetUp();
        this.vdcGenerator = new VirtualDatacenterGenerator(getSeed());
        this.volumeGenerator = new VolumeManagementGenerator(getSeed());
    }

    @Override
    protected EnterpriseDAO createDao(final EntityManager entityManager)
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

    public void test_getStorageUsageWithoutVolumes()
    {
        Enterprise ent = eg().createUniqueInstance();
        VirtualDatacenter vdc = vdcGenerator.createInstance(ent);

        List<Object> entitiesToPersist = new ArrayList<Object>();
        vdcGenerator.addAuxiliaryEntitiesToPersist(vdc, entitiesToPersist);

        persistAll(ds(), entitiesToPersist, vdc);

        EnterpriseDAO dao = createDaoForRollbackTransaction();
        Long used = dao.getStorageUsage(ent.getId());

        assertNotNull(used);
        assertEquals(used.longValue(), 0L);
    }

    public void test_getStorageUsageWithoutVolumesInSingleEnterprise()
    {
        Enterprise ent = eg().createUniqueInstance();
        VirtualDatacenter vdc = vdcGenerator.createInstance(ent);

        VolumeManagement vol1 = volumeGenerator.createInstance(vdc);
        VolumeManagement vol2 = volumeGenerator.createInstance(vol1.getStoragePool(), vdc);

        List<Object> entitiesToPersist = new ArrayList<Object>();
        vdcGenerator.addAuxiliaryEntitiesToPersist(vdc, entitiesToPersist);

        entitiesToPersist.add(vdc);
        entitiesToPersist.add(vol1.getStoragePool().getDevice());
        entitiesToPersist.add(vol1.getStoragePool().getTier());
        entitiesToPersist.add(vol1.getStoragePool());
        entitiesToPersist.add(vol1.getRasd());
        entitiesToPersist.add(vol1);
        entitiesToPersist.add(vol2.getRasd());
        entitiesToPersist.add(vol2);

        persistAll(ds(), entitiesToPersist);

        EnterpriseDAO dao = createDaoForRollbackTransaction();
        Long used = dao.getStorageUsage(ent.getId());

        assertNotNull(used);
        assertEquals(used.longValue(), vol1.getSizeInMB() + vol2.getSizeInMB());
    }

    public void test_getStorageUsageWithoutVolumesInMultipleEnterprises()
    {
        Enterprise ent1 = eg().createUniqueInstance();
        Enterprise ent2 = eg().createUniqueInstance();
        VirtualDatacenter vdc1 = vdcGenerator.createInstance(ent1);
        VirtualDatacenter vdc2 = vdcGenerator.createInstance(vdc1.getDatacenter(), ent2);

        VolumeManagement vol1 = volumeGenerator.createInstance(vdc1);
        VolumeManagement vol2 = volumeGenerator.createInstance(vol1.getStoragePool(), vdc1);
        VolumeManagement vol3 = volumeGenerator.createInstance(vol1.getStoragePool(), vdc2);
        VolumeManagement vol4 = volumeGenerator.createInstance(vol1.getStoragePool(), vdc2);

        List<Object> entitiesToPersist = new ArrayList<Object>();
        vdcGenerator.addAuxiliaryEntitiesToPersist(vdc1, entitiesToPersist);

        entitiesToPersist.add(vdc1);
        entitiesToPersist.add(ent2);
        entitiesToPersist.add(vdc2);

        entitiesToPersist.add(vol1.getStoragePool().getDevice());
        entitiesToPersist.add(vol1.getStoragePool().getTier());
        entitiesToPersist.add(vol1.getStoragePool());
        entitiesToPersist.add(vol1.getRasd());
        entitiesToPersist.add(vol1);
        entitiesToPersist.add(vol2.getRasd());
        entitiesToPersist.add(vol2);
        entitiesToPersist.add(vol3.getRasd());
        entitiesToPersist.add(vol3);
        entitiesToPersist.add(vol4.getRasd());
        entitiesToPersist.add(vol4);

        persistAll(ds(), entitiesToPersist);

        EnterpriseDAO dao = createDaoForRollbackTransaction();
        Long used = dao.getStorageUsage(ent1.getId());
        assertNotNull(used);
        assertEquals(used.longValue(), vol1.getSizeInMB() + vol2.getSizeInMB());

        used = dao.getStorageUsage(ent2.getId());
        assertNotNull(used);
        assertEquals(used.longValue(), vol3.getSizeInMB() + vol4.getSizeInMB());
    }

}
