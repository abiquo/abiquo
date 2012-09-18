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

import org.hibernate.NonUniqueResultException;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import com.abiquo.server.core.common.persistence.DefaultDAOTestBase;
import com.abiquo.server.core.common.persistence.TestDataAccessManager;
import com.abiquo.server.core.infrastructure.Machine;
import com.abiquo.server.core.infrastructure.MachineGenerator;
import com.softwarementors.bzngine.engines.jpa.test.configuration.EntityManagerFactoryForTesting;
import com.softwarementors.bzngine.entities.test.PersistentInstanceTester;

public class InitiatorMappingDAOTest extends
    DefaultDAOTestBase<InitiatorMappingDAO, InitiatorMapping>
{
    private MachineGenerator machineGenerator;

    private VolumeManagementGenerator volumeGenerator;

    @Override
    @BeforeMethod
    protected void methodSetUp()
    {
        super.methodSetUp();
        this.machineGenerator = new MachineGenerator(getSeed());
        this.volumeGenerator = new VolumeManagementGenerator(getSeed());
    }

    @Override
    protected InitiatorMappingDAO createDao(final EntityManager entityManager)
    {
        return new InitiatorMappingDAO(entityManager);
    }

    @Override
    protected PersistentInstanceTester<InitiatorMapping> createEntityInstanceGenerator()
    {
        return new InitiatorMappingGenerator(getSeed());
    }

    @Override
    protected EntityManagerFactoryForTesting getFactory()
    {
        return TestDataAccessManager.getFactory();
    }

    @Override
    public InitiatorMappingGenerator eg()
    {
        return (InitiatorMappingGenerator) super.eg();
    }

    @Test
    public void testFindByVolumeAndInitiator()
    {
        InitiatorMapping mapping = eg().createUniqueInstance();

        List<Object> entitiesToPersist = new ArrayList<Object>();
        eg().addAuxiliaryEntitiesToPersist(mapping, entitiesToPersist);
        persistAll(ds(), entitiesToPersist, mapping);

        InitiatorMappingDAO dao = createDaoForRollbackTransaction();

        InitiatorMapping result =
            dao.findByVolumeAndInitiator(mapping.getVolumeManagement().getId(),
                mapping.getInitiatorIqn());

        assertNotNull(result);
        eg().assertAllPropertiesEqual(result, mapping);
    }

    @Test
    public void testFindByVolumeAndInitiatorReturnsNullIfUnexisting()
    {
        InitiatorMappingDAO dao = createDaoForRollbackTransaction();

        InitiatorMapping result = dao.findByVolumeAndInitiator(5, "dummy");
        assertNull(result);
    }

    @Test(expectedExceptions = NonUniqueResultException.class)
    public void testFindByVolumeAndInitiatorFailsIfMoreThanOne()
    {
        InitiatorMapping mapping1 = eg().createUniqueInstance();
        InitiatorMapping mapping2 =
            eg().createInstance(mapping1.getInitiatorIqn(), mapping1.getVolumeManagement());

        List<Object> entitiesToPersist = new ArrayList<Object>();
        eg().addAuxiliaryEntitiesToPersist(mapping1, entitiesToPersist);
        persistAll(ds(), entitiesToPersist, mapping1, mapping2);

        InitiatorMappingDAO dao = createDaoForRollbackTransaction();

        dao.findByVolumeAndInitiator(mapping1.getVolumeManagement().getId(),
            mapping1.getInitiatorIqn());
    }

    @Test
    public void testFindByVolumeId()
    {
        InitiatorMapping mapping = eg().createUniqueInstance();

        List<Object> entitiesToPersist = new ArrayList<Object>();
        eg().addAuxiliaryEntitiesToPersist(mapping, entitiesToPersist);
        persistAll(ds(), entitiesToPersist, mapping);

        InitiatorMappingDAO dao = createDaoForRollbackTransaction();

        List<InitiatorMapping> result = dao.findByVolumeId(mapping.getVolumeManagement().getId());

        assertNotNull(result);
        assertEquals(result.size(), 1);
        eg().assertAllPropertiesEqual(result.get(0), mapping);
    }

    @Test
    public void testFindByVolumeIdDoesNotfailIfUnexisting()
    {
        InitiatorMappingDAO dao = createDaoForRollbackTransaction();

        List<InitiatorMapping> result = dao.findByVolumeId(5);
        assertNotNull(result);
        assertEmpty(result);
    }

    @Test
    public void testFindBPMMappingForVolumeReturnsNullIfUnexisting()
    {
        Machine machine = machineGenerator.createUniqueInstance();
        VolumeManagement volume = volumeGenerator.createInstance(machine.getDatacenter());
        InitiatorMapping mapping = eg().createInstance(machine.getInitiatorIQN(), volume);

        List<Object> entitiesToPersist = new ArrayList<Object>();
        volumeGenerator.addAuxiliaryEntitiesToPersist(volume, entitiesToPersist);
        persistAll(ds(), entitiesToPersist, volume, machine, mapping);

        InitiatorMappingDAO dao = createDaoForRollbackTransaction();

        InitiatorMapping result = dao.findBPMMappingForVolume(volume.getId());
        assertNull(result);
    }

    @Test
    public void testFindBPMMappingForVolume()
    {
        Machine machine = machineGenerator.createUniqueInstance();
        VolumeManagement volume = volumeGenerator.createInstance(machine.getDatacenter());
        InitiatorMapping machineMapping = eg().createInstance(machine.getInitiatorIQN(), volume);
        InitiatorMapping bpmMapping =
            eg().createInstance(InitiatorMappingGenerator.DEFAULT_INITIATOR + "-bpm", volume);

        List<Object> entitiesToPersist = new ArrayList<Object>();
        volumeGenerator.addAuxiliaryEntitiesToPersist(volume, entitiesToPersist);
        persistAll(ds(), entitiesToPersist, volume, machine, machineMapping, bpmMapping);

        InitiatorMappingDAO dao = createDaoForRollbackTransaction();

        InitiatorMapping result = dao.findBPMMappingForVolume(volume.getId());
        assertNotNull(result);
        eg().assertAllPropertiesEqual(result, bpmMapping);
    }

    @Test(expectedExceptions = NonUniqueResultException.class)
    public void testFindBPMMappingForVolumeFailsIfMultipleMappings()
    {
        Machine machine = machineGenerator.createUniqueInstance();
        VolumeManagement volume = volumeGenerator.createInstance(machine.getDatacenter());
        InitiatorMapping machineMapping = eg().createInstance(machine.getInitiatorIQN(), volume);
        InitiatorMapping bpmMapping1 =
            eg().createInstance(InitiatorMappingGenerator.DEFAULT_INITIATOR + "-bpm1", volume);
        InitiatorMapping bpmMapping2 =
            eg().createInstance(InitiatorMappingGenerator.DEFAULT_INITIATOR + "-bpm2", volume);

        List<Object> entitiesToPersist = new ArrayList<Object>();
        volumeGenerator.addAuxiliaryEntitiesToPersist(volume, entitiesToPersist);
        persistAll(ds(), entitiesToPersist, volume, machine, machineMapping, bpmMapping1,
            bpmMapping2);

        InitiatorMappingDAO dao = createDaoForRollbackTransaction();

        dao.findBPMMappingForVolume(volume.getId());
    }
}
