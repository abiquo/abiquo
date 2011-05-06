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

import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import com.abiquo.server.core.common.persistence.DefaultDAOTestBase;
import com.abiquo.server.core.common.persistence.TestDataAccessManager;
import com.abiquo.server.core.infrastructure.management.Rasd;
import com.softwarementors.bzngine.engines.jpa.test.configuration.EntityManagerFactoryForTesting;
import com.softwarementors.bzngine.entities.test.PersistentInstanceTester;

public class VolumeManagementDAOTest extends
    DefaultDAOTestBase<VolumeManagementDAO, VolumeManagement>
{
    @Override
    @BeforeMethod
    protected void methodSetUp()
    {
        super.methodSetUp();
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

        assertEquals(results.size(), 1);
        eg().assertAllPropertiesEqual(results.iterator().next(), volume);
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

        assertEquals(results.size(), 1);
        eg().assertAllPropertiesEqual(results.iterator().next(), volume);
    }

    @Test
    public void testGetVolumesByRasd()
    {
        // Test without filtering
        VolumeManagement volume = eg().createUniqueInstance();

        List<Object> entitiesToPersist = new ArrayList<Object>();
        eg().addAuxiliaryEntitiesToPersist(volume, entitiesToPersist);
        persistAll(ds(), entitiesToPersist, volume);
        Rasd rasd = volume.getRasd();
        VolumeManagementDAO dao = createDaoForRollbackTransaction();

        VolumeManagement vol = dao.getVolumeByRasd(rasd);

        eg().assertAllPropertiesEqual(vol,volume);
    }
}
