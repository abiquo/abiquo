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

package com.abiquo.server.core.infrastructure.storage;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.EntityManager;
import javax.validation.ConstraintViolationException;

import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import com.abiquo.model.enumerator.StorageTechnologyType;
import com.abiquo.server.core.cloud.VirtualDatacenter;
import com.abiquo.server.core.cloud.VirtualDatacenterGenerator;
import com.abiquo.server.core.common.persistence.DefaultDAOTestBase;
import com.abiquo.server.core.common.persistence.TestDataAccessManager;
import com.abiquo.server.core.infrastructure.management.Rasd;
import com.softwarementors.bzngine.engines.jpa.test.configuration.EntityManagerFactoryForTesting;
import com.softwarementors.bzngine.entities.test.PersistentInstanceTester;

public class VolumeManagementDAOTest extends
    DefaultDAOTestBase<VolumeManagementDAO, VolumeManagement>
{
    private VirtualDatacenterGenerator vdcGenerator;

    private StoragePoolGenerator poolGenerator;

    @Override
    @BeforeMethod
    protected void methodSetUp()
    {
        super.methodSetUp();
        vdcGenerator = new VirtualDatacenterGenerator(getSeed());
        poolGenerator = new StoragePoolGenerator(getSeed());
    }

    @Override
    protected VolumeManagementDAO createDao(final EntityManager entityManager)
    {
        return new VolumeManagementDAO(entityManager);
    }

    @Override
    protected PersistentInstanceTester<VolumeManagement> createEntityInstanceGenerator()
    {
        return new VolumeManagementGenerator(getSeed());
    }

    @Override
    protected EntityManagerFactoryForTesting getFactory()
    {
        return TestDataAccessManager.getFactory();
    }

    @Override
    public VolumeManagementGenerator eg()
    {
        return (VolumeManagementGenerator) super.eg();
    }

    @Test
    public void testGetVolumesByPool()
    {
        VolumeManagement volume = eg().createUniqueInstance();

        List<Object> entitiesToPersist = new ArrayList<Object>();
        eg().addAuxiliaryEntitiesToPersist(volume, entitiesToPersist);
        persistAll(ds(), entitiesToPersist, volume);

        VolumeManagementDAO dao = createDaoForRollbackTransaction();

        List<VolumeManagement> results = dao.getVolumesByPool(volume.getStoragePool());

        assertSize(results, 1);
        assertAllEntityPropertiesEqual(results.iterator().next(), volume);
    }

    @Test
    public void testGetVolumesByVirtualDatacenter()
    {
        // Test without filtering
        VolumeManagement volume = eg().createUniqueInstance();

        List<Object> entitiesToPersist = new ArrayList<Object>();
        eg().addAuxiliaryEntitiesToPersist(volume, entitiesToPersist);
        persistAll(ds(), entitiesToPersist, volume);

        VolumeManagementDAO dao = createDaoForRollbackTransaction();

        List<VolumeManagement> results =
            dao.getVolumesByVirtualDatacenter(volume.getVirtualDatacenter());

        assertSize(results, 1);
        assertAllEntityPropertiesEqual(results.iterator().next(), volume);
    }

    @Test
    public void testGetVolumeByVirtualDatacenter()
    {
        // Test without filtering
        VolumeManagement volume = eg().createUniqueInstance();

        List<Object> entitiesToPersist = new ArrayList<Object>();
        eg().addAuxiliaryEntitiesToPersist(volume, entitiesToPersist);
        persistAll(ds(), entitiesToPersist, volume);
        VolumeManagementDAO dao = createDaoForRollbackTransaction();

        VolumeManagement result =
            dao.getVolumeByVirtualDatacenter(volume.getVirtualDatacenter(), volume.getId());

        eg().assertAllPropertiesEqual(result, volume);

    }

    @Test
    public void testGetVolumesByRasd()
    {
        VolumeManagement volume = eg().createUniqueInstance();

        List<Object> entitiesToPersist = new ArrayList<Object>();
        eg().addAuxiliaryEntitiesToPersist(volume, entitiesToPersist);
        persistAll(ds(), entitiesToPersist, volume);
        Rasd rasd = volume.getRasd();
        VolumeManagementDAO dao = createDaoForRollbackTransaction();

        VolumeManagement vol = dao.getVolumeByRasd(rasd);

        eg().assertAllPropertiesEqual(vol, volume);
    }

    @Test
    public void testGetStatefulCandidatesWithoutVolumes()
    {
        VirtualDatacenter vdc = vdcGenerator.createUniqueInstance();

        List<Object> entitiesToPersist = new ArrayList<Object>();
        vdcGenerator.addAuxiliaryEntitiesToPersist(vdc, entitiesToPersist);
        persistAll(ds(), entitiesToPersist, vdc);

        VolumeManagementDAO dao = createDaoForRollbackTransaction();

        List<VolumeManagement> results = dao.getStatefulCandidates(vdc);

        assertEmpty(results);
    }

    @Test
    public void testGetStatefulCandidatesWithAssociatedState()
    {
        VolumeManagement volume = eg().createUniqueInstance();
        volume.associate();

        List<Object> entitiesToPersist = new ArrayList<Object>();
        eg().addAuxiliaryEntitiesToPersist(volume, entitiesToPersist);
        persistAll(ds(), entitiesToPersist, volume);

        VolumeManagementDAO dao = createDaoForRollbackTransaction();

        List<VolumeManagement> results = dao.getStatefulCandidates(volume.getVirtualDatacenter());

        assertEmpty(results);
    }

    @Test
    public void testGetStatefulCandidatesWithDifferentVirtualDatacenter()
    {
        VirtualDatacenter vdc = vdcGenerator.createUniqueInstance();
        VirtualDatacenter other =
            vdcGenerator.createInstance(vdc.getDatacenter(), vdc.getEnterprise());
        VolumeManagement volume = eg().createInstance(vdc);

        List<Object> entitiesToPersist = new ArrayList<Object>();
        eg().addAuxiliaryEntitiesToPersist(volume, entitiesToPersist);
        persistAll(ds(), entitiesToPersist, volume, other);

        VolumeManagementDAO dao = createDaoForRollbackTransaction();

        List<VolumeManagement> results = dao.getStatefulCandidates(other);

        assertEmpty(results);
    }

    @Test
    public void testGetStatefulCandidatesWithNormalPool()
    {
        StoragePool pool = poolGenerator.createUniqueInstance();
        VolumeManagement volume = eg().createInstance(pool);

        pool.getDevice().setStorageTechnology(StorageTechnologyType.NEXENTA);

        List<Object> entitiesToPersist = new ArrayList<Object>();
        eg().addAuxiliaryEntitiesToPersist(volume, entitiesToPersist);
        persistAll(ds(), entitiesToPersist, volume);

        VolumeManagementDAO dao = createDaoForRollbackTransaction();

        List<VolumeManagement> results = dao.getStatefulCandidates(volume.getVirtualDatacenter());

        assertEmpty(results);
    }

    @Test
    public void testGetStatefulCandidates()
    {
        StoragePool pool = poolGenerator.createUniqueInstance();
        VolumeManagement volume = eg().createInstance(pool);

        pool.getDevice().setStorageTechnology(StorageTechnologyType.GENERIC_ISCSI);

        List<Object> entitiesToPersist = new ArrayList<Object>();
        eg().addAuxiliaryEntitiesToPersist(volume, entitiesToPersist);
        persistAll(ds(), entitiesToPersist, volume);

        VolumeManagementDAO dao = createDaoForRollbackTransaction();

        List<VolumeManagement> results = dao.getStatefulCandidates(volume.getVirtualDatacenter());

        assertSize(results, 1);
        assertAllEntityPropertiesEqual(results.iterator().next(), volume);
    }

    @Test
    public void testGetStatefulCandidatesInISCSIPoolWithMultipleVolumes()
    {
        StoragePool pool = poolGenerator.createUniqueInstance();
        VolumeManagement volume1 = eg().createInstance(pool);
        VolumeManagement volume2 = eg().createInstance(pool, volume1.getVirtualDatacenter());

        pool.getDevice().setStorageTechnology(StorageTechnologyType.GENERIC_ISCSI);

        List<Object> entitiesToPersist = new ArrayList<Object>();
        eg().addAuxiliaryEntitiesToPersist(volume1, entitiesToPersist);
        persistAll(ds(), entitiesToPersist, volume1, volume2.getRasd(), volume2);

        VolumeManagementDAO dao = createDaoForRollbackTransaction();

        List<VolumeManagement> results = dao.getStatefulCandidates(volume1.getVirtualDatacenter());

        assertSize(results, 2);
    }

    @Test(expectedExceptions = ConstraintViolationException.class)
    public void testCreateVolumeWithTooLargeName()
    {
        VolumeManagement volume = eg().createUniqueInstance();

        StringBuilder name = new StringBuilder();
        for (int i = 0; i < Rasd.ELEMENT_NAME_LENGTH_MAX + 10; i++)
        {
            name.append("v");
        }
        volume.setName(name.toString());

        List<Object> entitiesToPersist = new ArrayList<Object>();
        eg().addAuxiliaryEntitiesToPersist(volume, entitiesToPersist);
        persistAll(ds(), entitiesToPersist, volume);
    }
}
