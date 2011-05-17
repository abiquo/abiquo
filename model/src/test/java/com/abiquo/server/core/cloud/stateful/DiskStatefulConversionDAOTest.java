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

package com.abiquo.server.core.cloud.stateful;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.EntityManager;

import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import com.abiquo.server.core.common.persistence.DefaultDAOTestBase;
import com.abiquo.server.core.common.persistence.TestDataAccessManager;
import com.abiquo.server.core.infrastructure.storage.VolumeManagement;
import com.abiquo.server.core.infrastructure.storage.VolumeManagementGenerator;
import com.softwarementors.bzngine.engines.jpa.test.configuration.EntityManagerFactoryForTesting;
import com.softwarementors.bzngine.entities.test.PersistentInstanceTester;

public class DiskStatefulConversionDAOTest extends
    DefaultDAOTestBase<DiskStatefulConversionDAO, DiskStatefulConversion>
{
    private VolumeManagementGenerator volumeManagementGenerator;

    @Override
    @BeforeMethod
    protected void methodSetUp()
    {
        super.methodSetUp();
        volumeManagementGenerator = new VolumeManagementGenerator(getSeed());
    }

    @Override
    protected DiskStatefulConversionDAO createDao(final EntityManager entityManager)
    {
        return new DiskStatefulConversionDAO(entityManager);
    }

    @Override
    protected PersistentInstanceTester<DiskStatefulConversion> createEntityInstanceGenerator()
    {
        return new DiskStatefulConversionGenerator(getSeed());
    }

    @Override
    protected EntityManagerFactoryForTesting getFactory()
    {
        return TestDataAccessManager.getFactory();
    }

    @Override
    public DiskStatefulConversionGenerator eg()
    {
        return (DiskStatefulConversionGenerator) super.eg();
    }

    @Test
    public void testGetByVolume()
    {
        VolumeManagement volume = volumeManagementGenerator.createUniqueInstance();
        DiskStatefulConversion diskStatefulConversion = eg().createInstance(volume);

        List<Object> entitiesToPersist = new ArrayList<Object>();
        eg().addAuxiliaryEntitiesToPersist(diskStatefulConversion, entitiesToPersist);
        persistAll(ds(), entitiesToPersist, diskStatefulConversion);

        DiskStatefulConversionDAO diskStatefulConversionDAO = createDaoForRollbackTransaction();

        DiskStatefulConversion result = diskStatefulConversionDAO.getByVolume(volume.getId());

        assertNotNull(result);
        assertAllEntityPropertiesEqual(result, diskStatefulConversion);
    }

    @Test
    public void testGetByUnexistingVolume()
    {
        VolumeManagement volume = volumeManagementGenerator.createUniqueInstance();
        DiskStatefulConversion diskStatefulConversion = eg().createInstance(volume);

        List<Object> entitiesToPersist = new ArrayList<Object>();
        eg().addAuxiliaryEntitiesToPersist(diskStatefulConversion, entitiesToPersist);
        persistAll(ds(), entitiesToPersist, diskStatefulConversion);

        DiskStatefulConversionDAO diskStatefulConversionDAO = createDaoForRollbackTransaction();

        DiskStatefulConversion result = diskStatefulConversionDAO.getByVolume(volume.getId() + 1);

        assertNull(result);
    }

}
