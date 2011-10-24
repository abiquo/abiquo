package com.abiquo.api.services.cloud;

import static com.abiquo.testng.TestConfig.NETWORK_UNIT_TESTS;
import static com.abiquo.testng.TestConfig.STORAGE_UNIT_TESTS;
import static org.testng.Assert.assertEquals;

import java.util.List;

import javax.persistence.EntityManager;

import org.springframework.security.context.SecurityContextHolder;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import com.abiquo.api.common.AbstractUnitTest;
import com.abiquo.api.common.BasicUserAuthentication;
import com.abiquo.api.services.StorageService;
import com.abiquo.server.core.cloud.NodeVirtualImage;
import com.abiquo.server.core.cloud.State;
import com.abiquo.server.core.cloud.VirtualAppliance;
import com.abiquo.server.core.cloud.VirtualDatacenter;
import com.abiquo.server.core.cloud.VirtualMachine;
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
    protected VirtualDatacenter vdc;

    protected VirtualAppliance vapp;

    protected VirtualMachine vm;

    /** Service we are testing */
    protected StorageService service;

    @BeforeMethod(groups = {STORAGE_UNIT_TESTS})
    public void setUpVirtualMachine()
    {
        Enterprise e = enterpriseGenerator.createUniqueInstance();
        Role r = roleGenerator.createInstance();
        User u = userGenerator.createInstance(e, r, "basicUser", "basicUser");
        setup(e, r, u);

        vdc = vdcGenerator.createInstance(e);
        vapp = vappGenerator.createInstance(vdc);
        vapp.setState(State.NOT_DEPLOYED);
        vapp.setState(State.NOT_DEPLOYED);
        vm = vmGenerator.createInstance(e);
        NodeVirtualImage nvi = nodeVirtualImageGenerator.createInstance(vapp, vm);

        DatacenterLimits dclimit = new DatacenterLimits(vdc.getEnterprise(), vdc.getDatacenter());

        // Set the correct properties to virtualmachine
        vm.getHypervisor().getMachine().setDatacenter(vdc.getDatacenter());
        vm.getHypervisor().getMachine().getRack().setDatacenter(vdc.getDatacenter());
        vm.setUser(u);

        setup(vdc.getDatacenter(), vdc, dclimit, vapp, vm.getVirtualImage(), vm.getHypervisor()
            .getMachine().getRack(), vm.getHypervisor().getMachine(), vm.getHypervisor(), vm, nvi);

        SecurityContextHolder.getContext().setAuthentication(new BasicUserAuthentication());
    }

    /**
     * Check by default there is a disk into virtual machine corresponding to virtual image's disk.
     */
    @Test
    public void getAllDisksByDefaultTest()
    {
        EntityManager em = getEntityManagerWithAnActiveTransaction();
        service = new StorageService(em);
        List<DiskManagement> defaultDisks =
            service.getListOfHardDisksByVM(vdc.getId(), vapp.getId(), vm.getId());

        // Assert there is only one disk
        assertEquals(defaultDisks.size(), 1);

        // Assert this disk has always the 'attachmentOrder' 0
        DiskManagement disk = defaultDisks.get(0);
        assertEquals(disk.getAttachmentOrder(), 0L);

        // Assert is 'readOnly'
        assertEquals(disk.getReadOnly(), Boolean.TRUE);

        // Assert its capacity is the same than the virtual image
        assertEquals(disk.getSizeInMb(), Long.valueOf(vm.getVirtualImage().getDiskFileSize()));
    }

    /**
     * Setup a couple of extra hard disks, and check they are retrieved ok.
     */
    @Test
    public void getAllDisksTest()
    {
        DiskManagement inputDisk1 = new DiskManagement(vdc, vapp, vm, 7000L, 1);
        DiskManagement inputDisk2 = new DiskManagement(vdc, vapp, vm, 9000L, 2);
        setup(inputDisk1.getRasd(), inputDisk1, inputDisk2.getRasd(), inputDisk2);

        // retrieve them
        EntityManager em = getEntityManagerWithAnActiveTransaction();
        service = new StorageService(em);
        List<DiskManagement> disks =
            service.getListOfHardDisksByVM(vdc.getId(), vapp.getId(), vm.getId());

        // Assert there is only one disk
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

    }

    /**
     * Check by default there is a disk into virtual machine corresponding to virtual image's disk.
     * and its diskOrder is always 0
     */
    @Test
    public void getDefaultDiskTest()
    {
        EntityManager em = getEntityManagerWithAnActiveTransaction();
        service = new StorageService(em);
        DiskManagement disk = service.getHardDiskByVM(vdc.getId(), vapp.getId(), vm.getId(), 0);

        // Assert this disk has always the 'attachmentOrder' 0
        assertEquals(disk.getAttachmentOrder(), 0L);

        // Assert is 'readOnly'
        assertEquals(disk.getReadOnly(), Boolean.TRUE);

        // Assert its capacity is the same than the virtual image
        assertEquals(disk.getSizeInMb(), Long.valueOf(vm.getVirtualImage().getDiskFileSize()));
    }

    /**
     * Setup a couple of extra hard disks, and check they are retrieved ok.
     */
    @Test
    public void getExtraDisksTest()
    {
        DiskManagement inputDisk1 = new DiskManagement(vdc, vapp, vm, 7000L, 1);
        DiskManagement inputDisk2 = new DiskManagement(vdc, vapp, vm, 9000L, 2);
        setup(inputDisk1.getRasd(), inputDisk1, inputDisk2.getRasd(), inputDisk2);

        // retrieve them
        EntityManager em = getEntityManagerWithAnActiveTransaction();
        service = new StorageService(em);
        List<DiskManagement> disks =
            service.getListOfHardDisksByVM(vdc.getId(), vapp.getId(), vm.getId());

        // Assert there is only one disk
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

    }

    @Override
    @AfterMethod(groups = {NETWORK_UNIT_TESTS})
    public void tearDown()
    {
        super.tearDown();
    }
}
