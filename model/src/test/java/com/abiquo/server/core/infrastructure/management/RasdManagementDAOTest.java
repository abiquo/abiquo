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

package com.abiquo.server.core.infrastructure.management;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.persistence.EntityManager;

import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import com.abiquo.server.core.cloud.VirtualMachine;
import com.abiquo.server.core.cloud.VirtualMachineGenerator;
import com.abiquo.server.core.common.persistence.DefaultDAOTestBase;
import com.abiquo.server.core.common.persistence.TestDataAccessManager;
import com.abiquo.server.core.infrastructure.storage.DiskManagement;
import com.abiquo.server.core.infrastructure.storage.DiskManagementGenerator;
import com.abiquo.server.core.infrastructure.storage.VolumeManagement;
import com.abiquo.server.core.infrastructure.storage.VolumeManagementGenerator;
import com.softwarementors.bzngine.engines.jpa.test.configuration.EntityManagerFactoryForTesting;
import com.softwarementors.bzngine.entities.test.PersistentInstanceTester;

public class RasdManagementDAOTest extends DefaultDAOTestBase<RasdManagementDAO, RasdManagement>
{
    private VirtualMachineGenerator vmGenerator;

    private DiskManagementGenerator diskGenerator;

    private VolumeManagementGenerator volumeGenerator;

    @Override
    @BeforeMethod
    protected void methodSetUp()
    {
        super.methodSetUp();
        vmGenerator = new VirtualMachineGenerator(getSeed());
        diskGenerator = new DiskManagementGenerator(getSeed());
        volumeGenerator = new VolumeManagementGenerator(getSeed());
    }

    @Override
    protected RasdManagementDAO createDao(final EntityManager entityManager)
    {
        return new RasdManagementDAO(entityManager);
    }

    @Override
    protected PersistentInstanceTester<RasdManagement> createEntityInstanceGenerator()
    {
        return new RasdManagementGenerator(getSeed());
    }

    @Override
    protected EntityManagerFactoryForTesting getFactory()
    {
        return TestDataAccessManager.getFactory();
    }

    @Override
    public RasdManagementGenerator eg()
    {
        return (RasdManagementGenerator) super.eg();
    }

    @Test
    public void testFindByVirtualMachine()
    {
        RasdManagement rasdManagement = eg().createUniqueInstance();
        VirtualMachine vm = vmGenerator.createUniqueInstance();
        rasdManagement.setVirtualMachine(vm);

        List<Object> entitiesToPersist = new ArrayList<Object>();
        eg().addAuxiliaryEntitiesToPersist(rasdManagement, entitiesToPersist);
        persistAll(ds(), entitiesToPersist, rasdManagement);

        RasdManagementDAO dao = createDaoForRollbackTransaction();

        Collection<RasdManagement> results = dao.findByVirtualMachine(vm);

        assertEquals(results.size(), 1);
        eg().assertAllPropertiesEqual(results.iterator().next(), rasdManagement);
    }

    @Test
    public void testFindByVirtualDatacenterAndResourceType()
    {
        RasdManagement rasdManagement = eg().createInstance(VolumeManagement.DISCRIMINATOR);

        List<Object> entitiesToPersist = new ArrayList<Object>();
        eg().addAuxiliaryEntitiesToPersist(rasdManagement, entitiesToPersist);
        persistAll(ds(), entitiesToPersist, rasdManagement);

        RasdManagementDAO dao = createDaoForRollbackTransaction();

        Collection<RasdManagement> results =
            dao.findByVirtualDatacenterAndResourceType(rasdManagement.getVirtualDatacenter(),
                VolumeManagement.DISCRIMINATOR);

        assertEquals(results.size(), 1);
        eg().assertAllPropertiesEqual(results.iterator().next(), rasdManagement);
    }

    @Test
    public void testFindDisksAndVolumesByVirtualMachineOnlyDisks()
    {
        DiskManagement disk1 = diskGenerator.createUniqueInstance();
        DiskManagement disk2 = diskGenerator.createInstance(disk1.getVirtualDatacenter());
        
        VirtualMachine vm = vmGenerator.createInstance(disk1.getVirtualDatacenter().getEnterprise());

        // Set reverse order to test DAO ordering
        disk1.setAttachmentOrder(2);
        disk2.setAttachmentOrder(1);

        List<Object> entitiesToPersist = new ArrayList<Object>();
        diskGenerator.addAuxiliaryEntitiesToPersist(disk1, entitiesToPersist);
        disk1.setVirtualMachine(vm);
        disk2.setVirtualMachine(vm);
        persistAll(ds(), entitiesToPersist, disk1, disk2.getRasd(), disk2);

        RasdManagementDAO dao = createDaoForRollbackTransaction();
        List<RasdManagement> disks = dao.findDisksAndVolumesByVirtualMachine(vm);

        assertNotNull(disks);
        assertEquals(disks.size(), 2);
        assertEquals(disks.get(0).getAttachmentOrder(), 1);
        assertEquals(disks.get(1).getAttachmentOrder(), 2);
    }

    @Test
    public void testFindDisksAndVolumesByVirtualMachineOnlyVolumes()
    {
        VolumeManagement vol1 = volumeGenerator.createUniqueInstance();
        VolumeManagement vol2 =
            volumeGenerator.createInstance(vol1.getStoragePool(), vol1.getVirtualDatacenter());
        VirtualMachine vm = vmGenerator.createInstance(vol1.getVirtualDatacenter().getEnterprise());

        vol1.setVirtualMachine(vm);
        vol2.setVirtualMachine(vm);
        // Set reverse order to test DAO ordering
        vol1.setAttachmentOrder(2);
        vol2.setAttachmentOrder(1);

        List<Object> entitiesToPersist = new ArrayList<Object>();
        volumeGenerator.addAuxiliaryEntitiesToPersist(vol1, entitiesToPersist);
        persistAll(ds(), entitiesToPersist, vol1, vol2.getRasd(), vol2);

        RasdManagementDAO dao = createDaoForRollbackTransaction();
        List<RasdManagement> volumes = dao.findDisksAndVolumesByVirtualMachine(vm);

        assertNotNull(volumes);
        assertEquals(volumes.size(), 2);
        assertEquals(volumes.get(0).getAttachmentOrder(), 1);
        assertEquals(volumes.get(1).getAttachmentOrder(), 2);
    }

    @Test
    public void testFindDisksAndVolumesByVirtualMachine()
    {
        VolumeManagement vol1 = volumeGenerator.createUniqueInstance();
        VolumeManagement vol2 =
            volumeGenerator.createInstance(vol1.getStoragePool(), vol1.getVirtualDatacenter());
        VirtualMachine vm = vmGenerator.createInstance(vol1.getVirtualDatacenter().getEnterprise());
        DiskManagement disk1 = diskGenerator.createInstance(vol1.getVirtualDatacenter());
        DiskManagement disk2 = diskGenerator.createInstance(vol1.getVirtualDatacenter());

        vol1.setVirtualMachine(vm);
        vol2.setVirtualMachine(vm);
        disk1.setVirtualMachine(vm);
        disk2.setVirtualMachine(vm);
        
        // Set order to test DAO ordering
        disk1.setAttachmentOrder(4);
        disk2.setAttachmentOrder(1);
        vol1.setAttachmentOrder(2);
        vol2.setAttachmentOrder(3);

        List<Object> entitiesToPersist = new ArrayList<Object>();
        volumeGenerator.addAuxiliaryEntitiesToPersist(vol1, entitiesToPersist);
        persistAll(ds(), entitiesToPersist, vol1, vol2.getRasd(), vol2, disk1.getRasd(), disk1,
            disk2.getRasd(), disk2);

        RasdManagementDAO dao = createDaoForRollbackTransaction();
        List<RasdManagement> disks = dao.findDisksAndVolumesByVirtualMachine(vm);

        assertNotNull(disks);
        assertEquals(disks.size(), 4);

        assertEquals(disks.get(0).getAttachmentOrder(), 1);
        assertEquals(disks.get(1).getAttachmentOrder(), 2);
        assertEquals(disks.get(2).getAttachmentOrder(), 3);
        assertEquals(disks.get(3).getAttachmentOrder(), 4);

        assertTrue(disks.get(0) instanceof DiskManagement);
        assertTrue(disks.get(1) instanceof VolumeManagement);
        assertTrue(disks.get(2) instanceof VolumeManagement);
        assertTrue(disks.get(3) instanceof DiskManagement);
    }

    @Test
    public void testFindDisksAndVolumesByVirtualMachineEmptyList()
    {
        VirtualMachine vm = vmGenerator.createUniqueInstance();

        List<Object> entitiesToPersist = new ArrayList<Object>();
        vmGenerator.addAuxiliaryEntitiesToPersist(vm, entitiesToPersist);
        persistAll(ds(), entitiesToPersist, vm);

        RasdManagementDAO dao = createDaoForRollbackTransaction();
        List<RasdManagement> disks = dao.findDisksAndVolumesByVirtualMachine(vm);

        assertNotNull(disks);
        assertEquals(disks.size(), 0);
    }
}
