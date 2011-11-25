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

package com.abiquo.api.services.cloud;

import static com.abiquo.testng.TestConfig.STORAGE_UNIT_TESTS;
import static org.testng.Assert.assertEquals;

import java.util.List;
import java.util.Random;

import javax.persistence.EntityManager;

import org.springframework.security.context.SecurityContextHolder;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import com.abiquo.api.common.AbstractUnitTest;
import com.abiquo.api.common.BasicUserAuthentication;
import com.abiquo.api.exceptions.BadRequestException;
import com.abiquo.api.exceptions.ConflictException;
import com.abiquo.api.exceptions.NotFoundException;
import com.abiquo.api.services.StorageService;
import com.abiquo.server.core.cloud.NodeVirtualImage;
import com.abiquo.server.core.cloud.VirtualAppliance;
import com.abiquo.server.core.cloud.VirtualApplianceState;
import com.abiquo.server.core.cloud.VirtualDatacenter;
import com.abiquo.server.core.cloud.VirtualMachine;
import com.abiquo.server.core.cloud.VirtualMachineState;
import com.abiquo.server.core.enterprise.DatacenterLimits;
import com.abiquo.server.core.enterprise.Enterprise;
import com.abiquo.server.core.enterprise.Role;
import com.abiquo.server.core.enterprise.User;
import com.abiquo.server.core.infrastructure.storage.DiskManagement;

/**
 * Unit tests for disk management features.
 * 
 * @author jdevesa
 */
@Test(groups = {STORAGE_UNIT_TESTS})
public class DiskManagementServiceTest extends AbstractUnitTest
{
    private static long MEGABYTE = 1048576;

    /** Service we are testing */
    protected StorageService service;

    protected VirtualAppliance vapp;

    protected VirtualDatacenter vdc;

    protected VirtualMachine vm;
    

    @BeforeMethod(groups = {STORAGE_UNIT_TESTS})
    public void setUp()
    {
        Enterprise e = enterpriseGenerator.createInstanceNoLimits("test enterprise");
        Role r = roleGenerator.createInstance();
        User u = userGenerator.createInstance(e, r, "basicUser", "basicUser");
        setup(e, r, u);

        vdc = vdcGenerator.createInstance(e);
        vapp = vappGenerator.createInstance(vdc);
        vapp.setState(VirtualApplianceState.NOT_DEPLOYED);
        vm = vmGenerator.createInstance(e);
        NodeVirtualImage nvi = nodeVirtualImageGenerator.createInstance(vapp, vm);
        nvi.getVirtualImage().setDiskFileSize(2000000);

        DatacenterLimits dclimit = new DatacenterLimits(vdc.getEnterprise(), vdc.getDatacenter());

        // Set the correct properties to virtualmachine
        vm.getHypervisor().getMachine().setDatacenter(vdc.getDatacenter());
        vm.getHypervisor().getMachine().getRack().setDatacenter(vdc.getDatacenter());
        vm.setUser(u);

        // TODO vdc datacenter and virutal image datacenter ARE NOT THE SAME
        setup(vdc.getDatacenter(), vdc, dclimit, vapp, vm.getVirtualImage().getCategory(), vm
            .getVirtualImage().getRepository().getDatacenter(), vm.getVirtualImage()
            .getRepository(), vm.getVirtualImage(), vm.getHypervisor().getMachine().getRack(), vm
            .getHypervisor().getMachine(), vm.getHypervisor(), vm, nvi);

        SecurityContextHolder.getContext().setAuthentication(new BasicUserAuthentication());
    }

    @Override
    @AfterMethod(groups = {STORAGE_UNIT_TESTS})
    public void tearDown()
    {
        super.tearDown();
    }

    /**
     * Check the creation of the disk at virtual datacenter works.
     */
    @Test
    public void createDiskTest()
    {
        // retrieve them
        EntityManager em = getEntityManagerWithAnActiveTransaction();
        service = new StorageService(em);

        // Assert there is only one disk
        // create a new one
        service.createHardDisk(vdc.getId(), 12000L);

        // Assert disk has been created
        List<DiskManagement> disks = service.getListOfHardDisksByVirtualDatacenter(vdc.getId());
        assertEquals(disks.size(), 1);

        assertEquals(disks.get(0).getSizeInMb(), Long.valueOf(12000L));
        commitActiveTransaction(em);
    }

    /**
     * Expect a NotFoundException when creating disk and the virtual datacenter does not exist.
     */
    @Test(expectedExceptions = {NotFoundException.class})
    public void createDiskRaiseNotFoundWhenRandomVDCIdTest()
    {
        Integer randomId;
        do
        {
            randomId = new Random().nextInt(10000);
        }
        while (randomId.equals(vdc.getId()));

        EntityManager em = getEntityManagerWithAnActiveTransaction();
        service = new StorageService(em);
        service.createHardDisk(randomId, 100000L);
    }

    /**
     * Ensure the service raises bad request exceptions when adding a disk with negative values.
     */
    @Test(expectedExceptions = {BadRequestException.class})
    public void createDiskRaisesBadRequestWhenCreatingADiskWithNegativeValuesTest()
    {
        EntityManager em = getEntityManagerWithAnActiveTransaction();
        service = new StorageService(em);

        service.createHardDisk(vdc.getId(), -1L);
    }

    /**
     * Ensure the service raises bad request exceptions when adding a disk with null values.
     */
    @Test(expectedExceptions = {BadRequestException.class})
    public void createDiskRaisesBadRequestWhenCreatingADiskWithNullValuesTest()
    {
        EntityManager em = getEntityManagerWithAnActiveTransaction();
        service = new StorageService(em);

        service.createHardDisk(vdc.getId(), null);
    }

    /**
     * Check the deletion of disks works.
     */
    @Test
    public void deleteDiskTest()
    {
        DiskManagement inputDisk1 = diskGenerator.createInstance(vdc);
        setup(inputDisk1.getRasd(), inputDisk1);

        // retrieve them
        EntityManager em = getEntityManagerWithAnActiveTransaction();
        service = new StorageService(em);

        // delete the first one
        service.deleteHardDisk(vdc.getId(), inputDisk1.getId());
        // Assert disk has been created
        commitActiveTransaction(em);
        
        em = getEntityManagerWithAnActiveTransaction();
        // assert it has been deleted.
        service = new StorageService(em);
        assertEquals(service.getListOfHardDisksByVirtualDatacenter(vdc.getId()).size(), 0);


    }

    /**
     * Expect a NotFoundException when deleting disk and the virtual datacenter does not exist.
     */
    @Test(expectedExceptions = {NotFoundException.class})
    public void deleteDiskRaiseNotFoundWhenRandomVDCIdTest()
    {
        DiskManagement inputDisk1 = diskGenerator.createInstance(vdc);
        setup(inputDisk1.getRasd(), inputDisk1);
        
        Integer randomId;
        do
        {
            randomId = new Random().nextInt(10000);
        }
        while (randomId.equals(vdc.getId()));

        EntityManager em = getEntityManagerWithAnActiveTransaction();
        try
        {
            service = new StorageService(em);
            service.deleteHardDisk(randomId, inputDisk1.getId());
        }
        finally
        {
            rollbackActiveTransaction(em);
        }
    }

    /**
     * Expect a NotFoundException when deleting disk and the virtual disk id does not exist.
     */
    @Test(expectedExceptions = {NotFoundException.class})
    public void deleteDiskRaiseNotFoundWhenRandomDiskIdTest()
    {

        DiskManagement inputDisk1 = diskGenerator.createInstance(vdc);
        setup(inputDisk1.getRasd(), inputDisk1);
        Integer randomId;
        do
        {
            randomId = new Random().nextInt(10000);
        }
        while (randomId.equals(inputDisk1.getId()));
        
        EntityManager em = getEntityManagerWithAnActiveTransaction();

        try
        {
            service = new StorageService(em);
            service.deleteHardDisk(vdc.getId(), randomId);
        }
        finally
        {
            rollbackActiveTransaction(em);
        }
    }

    /**
     * Ensure service raises bad request exceptions when removing a disk with the machine in
     * incoherent state
     */
    @Test(expectedExceptions = {ConflictException.class})
    public void deleteDiskRaisesConflictWhenVirtualMachineIsAssignedTest()
    {
        // set the virtual machine state as 'RUNNING'
        EntityManager em = getEntityManagerWithAnActiveTransaction();
        vm.setState(VirtualMachineState.ON);
        update(vm);
        
        DiskManagement disk = diskGenerator.createInstance(vdc);
        disk.setVirtualMachine(vm);
        setup(disk.getRasd(), disk);

        em = getEntityManagerWithAnActiveTransaction();
        service = new StorageService(em);

        try
        {
            service.deleteHardDisk(vdc.getId(), disk.getId());
        }
        finally
        { 
            rollbackActiveTransaction(em);
        } 
    }

    /**
     * Expect a NotFoundException when getting all disks and the virtual appliance does not exist.
     */
    @Test(expectedExceptions = {NotFoundException.class}, enabled = false)
    public void getAllDiskRaiseNotFoundWhenRandomVappIdTest()
    {
        Integer randomId;
        do
        {
            randomId = new Random().nextInt(10000);
        }
        while (randomId.equals(vapp.getId()));

        EntityManager em = getEntityManagerWithAnActiveTransaction();
        service = new StorageService(em);

        service.getListOfHardDisksByVM(vdc.getId(), randomId, vm.getId());
    }

    /**
     * Expect a NotFoundException when getting all disks and the virtual datacenter does not exist.
     */
    @Test(expectedExceptions = {NotFoundException.class}, enabled = false)
    public void getAllDiskRaiseNotFoundWhenRandomVDCIdTest()
    {
        Integer randomId;
        do
        {
            randomId = new Random().nextInt(10000);
        }
        while (randomId.equals(vdc.getId()));

        EntityManager em = getEntityManagerWithAnActiveTransaction();
        service = new StorageService(em);

        service.getListOfHardDisksByVM(randomId, vapp.getId(), vm.getId());
    }

    /**
     * Expect a NotFoundException when getting all disks and the virtual machine does not exist.
     */
    @Test(expectedExceptions = {NotFoundException.class}, enabled = false)
    public void getAllDiskRaiseNotFoundWhenRandomVirtualMachineIdTest()
    {
        Integer randomId;
        do
        {
            randomId = new Random().nextInt(10000);
        }
        while (randomId.equals(vm.getId()));

        EntityManager em = getEntityManagerWithAnActiveTransaction();
        service = new StorageService(em);

        service.getListOfHardDisksByVM(vdc.getId(), vm.getId(), randomId);
    }

    /**
     * Ensure service raises bad request exceptions when adding a disk with the machine in
     * incoherent state
     */
    @Test(expectedExceptions = {ConflictException.class}, enabled = false)
    public void attachDiskRaisesConflictWhenVirtualMachineIncoherentStateTest()
    {
        // set the virtual machine state as 'RUNNING'
        EntityManager em = getEntityManagerWithAnActiveTransaction();
        vm.setState(VirtualMachineState.ON);
        update(vm);
        commitActiveTransaction(em);

        em = getEntityManagerWithAnActiveTransaction();
        service = new StorageService(em);

        service.attachHardDiskIntoVM(vdc.getId(), vapp.getId(), vm.getId(), 100000);
    }
    
    /**
     * Setup a couple of extra hard disks, and check they are retrieved ok.
     */
    @Test(enabled = false)
    public void getAllDisksTest()
    {
        DiskManagement inputDisk1 = new DiskManagement(vdc, 7000L);
        DiskManagement inputDisk2 = new DiskManagement(vdc, 9000L);
        setup(inputDisk1.getRasd(), inputDisk1, inputDisk2.getRasd(), inputDisk2);

        // retrieve them
        EntityManager em = getEntityManagerWithAnActiveTransaction();
        service = new StorageService(em);
        List<DiskManagement> disks =
            service.getListOfHardDisksByVM(vdc.getId(), vapp.getId(), vm.getId());

        // Assert all disks have been created
        assertEquals(disks.size(), 3);

        // Assert the properties of the 'inputDisk1'
        DiskManagement outputDisk1 = disks.get(1);
        assertEquals(outputDisk1.getAttachmentOrder(), inputDisk1.getAttachmentOrder());
        assertEquals(outputDisk1.getSizeInMb(), inputDisk1.getSizeInMb());
        assertEquals(outputDisk1.getReadOnly(), Boolean.FALSE);

        // Assert the properties of the 'inputDisk2'
        DiskManagement outputDisk2 = disks.get(2);
        assertEquals(outputDisk2.getAttachmentOrder(), inputDisk2.getAttachmentOrder());
        assertEquals(outputDisk2.getSizeInMb(), inputDisk2.getSizeInMb());
        assertEquals(outputDisk2.getReadOnly(), Boolean.FALSE);

        commitActiveTransaction(em);

    }

    /**
     * Expect a NotFoundException when getting a single disk that does not exist.
     */
    @Test(expectedExceptions = {NotFoundException.class}, enabled = false)
    public void getDiskRaiseNotFoundWhenRandomDiskIdTest()
    {
        EntityManager em = getEntityManagerWithAnActiveTransaction();
        service = new StorageService(em);

        service.getHardDiskByVM(vdc.getId(), vapp.getId(), vm.getId(), 50);
    }

    /**
     * Expect a NotFoundException when getting a single disk and the virtual appliance does not
     * exist.
     */
    @Test(expectedExceptions = {NotFoundException.class}, enabled = false)
    public void getDiskRaiseNotFoundWhenRandomVappIdTest()
    {
        Integer randomId;
        do
        {
            randomId = new Random().nextInt(10000);
        }
        while (randomId.equals(vapp.getId()));

        EntityManager em = getEntityManagerWithAnActiveTransaction();
        service = new StorageService(em);

        service.getHardDiskByVM(vdc.getId(), randomId, vm.getId(), 0);
    }

    /**
     * Expect a NotFoundException when getting a single disk and the virtual datacenter does not
     * exist.
     */
    @Test(expectedExceptions = {NotFoundException.class}, enabled = false)
    public void getDiskRaiseNotFoundWhenRandomVDCIdTest()
    {
        Integer randomId;
        do
        {
            randomId = new Random().nextInt(10000);
        }
        while (randomId.equals(vdc.getId()));

        EntityManager em = getEntityManagerWithAnActiveTransaction();
        service = new StorageService(em);

        service.getHardDiskByVM(randomId, vapp.getId(), vm.getId(), 0);
    }

    /**
     * Expect a NotFoundException when getting a single disk and the virtual machine does not exist.
     */
    @Test(expectedExceptions = {NotFoundException.class}, enabled = false)
    public void getDiskRaiseNotFoundWhenRandomVirtualMachineIdTest()
    {
        Integer randomId;
        do
        {
            randomId = new Random().nextInt(10000);
        }
        while (randomId.equals(vm.getId()));

        EntityManager em = getEntityManagerWithAnActiveTransaction();
        service = new StorageService(em);

        service.getHardDiskByVM(vdc.getId(), vapp.getId(), randomId, 0);
    }

    /**
     * Setup a couple of extra hard disks, and check they are retrieved ok.
     */
    @Test(enabled = false)
    public void getExtraDisksTest()
    {
        DiskManagement inputDisk1 = new DiskManagement(vdc, 7000L);
        DiskManagement inputDisk2 = new DiskManagement(vdc, 9000L);
        setup(inputDisk1.getRasd(), inputDisk1, inputDisk2.getRasd(), inputDisk2);

        // retrieve them
        EntityManager em = getEntityManagerWithAnActiveTransaction();
        service = new StorageService(em);
        List<DiskManagement> disks =
            service.getListOfHardDisksByVM(vdc.getId(), vapp.getId(), vm.getId());

        // Assert the properties of the 'inputDisk1'
        DiskManagement outputDisk1 = disks.get(1);
        assertEquals(outputDisk1.getAttachmentOrder(), inputDisk1.getAttachmentOrder());
        assertEquals(outputDisk1.getSizeInMb(), inputDisk1.getSizeInMb());
        assertEquals(outputDisk1.getReadOnly(), Boolean.FALSE);

        // Assert the properties of the 'inputDisk2'
        DiskManagement outputDisk2 = disks.get(2);
        assertEquals(outputDisk2.getAttachmentOrder(), inputDisk2.getAttachmentOrder());
        assertEquals(outputDisk2.getSizeInMb(), inputDisk2.getSizeInMb());
        assertEquals(outputDisk2.getReadOnly(), Boolean.FALSE);

        commitActiveTransaction(em);
    }

}
