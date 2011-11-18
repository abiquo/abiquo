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

/**
 * 
 */
package com.abiquo.api.resources.cloud;

import static com.abiquo.api.common.UriTestResolver.resolveVirtualMachineDiskUri;
import static com.abiquo.api.common.UriTestResolver.resolveVirtualMachineDisksUri;
import static com.abiquo.testng.TestConfig.STORAGE_INTEGRATION_TESTS;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;

import javax.ws.rs.core.Response.Status;

import org.apache.wink.client.ClientResponse;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import com.abiquo.api.resources.AbstractJpaGeneratorIT;
import com.abiquo.server.core.appslibrary.VirtualImage;
import com.abiquo.server.core.cloud.NodeVirtualImage;
import com.abiquo.server.core.cloud.VirtualAppliance;
import com.abiquo.server.core.cloud.VirtualApplianceState;
import com.abiquo.server.core.cloud.VirtualDatacenter;
import com.abiquo.server.core.cloud.VirtualMachine;
import com.abiquo.server.core.enterprise.DatacenterLimits;
import com.abiquo.server.core.enterprise.Enterprise;
import com.abiquo.server.core.enterprise.Role;
import com.abiquo.server.core.enterprise.User;
import com.abiquo.server.core.infrastructure.management.RasdManagement;
import com.abiquo.server.core.infrastructure.storage.DiskManagement;
import com.abiquo.server.core.infrastructure.storage.DiskManagementDto;
import com.abiquo.server.core.infrastructure.storage.DisksManagementDto;

/**
 * Integration tests for disk management features.
 * 
 * @author jdevesa@abiquo.com
 */
@Test(groups = {STORAGE_INTEGRATION_TESTS})
public class VirtualMachineStorageConfigurationResourceIT extends AbstractJpaGeneratorIT
{
    protected VirtualAppliance vapp;

    protected VirtualDatacenter vdc;

    protected VirtualMachine vm;

    @BeforeMethod(groups = {STORAGE_INTEGRATION_TESTS})
    public void setUp()
    {
        Enterprise e = enterpriseGenerator.createUniqueInstance();
        Role r = roleGenerator.createInstance();
        User u = userGenerator.createInstance(e, r, "basicUser", "basicUser");
        setup(e, r, u);

        vdc = vdcGenerator.createInstance(e);
        vapp = vappGenerator.createInstance(vdc);
        vapp.setState(VirtualApplianceState.NOT_DEPLOYED);

        VirtualImage vimage = virtualImageGenerator.createInstance(e, vdc.getDatacenter());
        vm = vmGenerator.createInstance(vimage);

        NodeVirtualImage nvi = nodeVirtualImageGenerator.createInstance(vapp, vm);

        DatacenterLimits dclimit = new DatacenterLimits(vdc.getEnterprise(), vdc.getDatacenter());

        // Set the correct properties to virtualmachine
        vm.getHypervisor().getMachine().setDatacenter(vdc.getDatacenter());
        vm.getHypervisor().getMachine().getRack().setDatacenter(vdc.getDatacenter());
        vm.setUser(u);

        setup(vdc.getDatacenter(), vdc, dclimit, vapp, vimage.getRepository(),
            vimage.getCategory(), vimage, vm.getEnterprise(), vm.getHypervisor().getMachine()
                .getRack(), vm.getHypervisor().getMachine(), vm.getHypervisor(), vm, nvi);
    }

    @Override
    @AfterMethod(groups = {STORAGE_INTEGRATION_TESTS})
    public void tearDown()
    {
        super.tearDown();
    }

    /**
     * Test to create a disk.
     */
    @Test
    public void createDiskIT()
    {
        String uri = resolveVirtualMachineDisksUri(vdc.getId(), vapp.getId(), vm.getId());
        DiskManagementDto dto = new DiskManagementDto();
        dto.setSizeInMb(100000L);

        ClientResponse response = post(uri, dto, "basicUser", "basicUser");
        assertEquals(response.getStatusCode(), Status.CREATED.getStatusCode());
    }

    /**
     * Test to delete a disk.
     */
    @Test
    public void deleteDiskIT()
    {
        DiskManagement inputDisk2 =
            new DiskManagement(vm, 9000L, RasdManagement.FIRST_ATTACHMENT_SEQUENCE);
        setup(inputDisk2.getRasd(), inputDisk2);

        // Assert the disk is deleted
        String uri =
            resolveVirtualMachineDiskUri(vdc.getId(), vapp.getId(), vm.getId(),
                Long.valueOf(inputDisk2.getAttachmentOrder()).intValue());
        ClientResponse response = delete(uri, "basicUser", "basicUser");
        assertEquals(response.getStatusCode(), Status.NO_CONTENT.getStatusCode());
    }

    /**
     * Test to get a list of disks.
     */
    @Test
    public void getDisksIT()
    {
        DiskManagement inputDisk2 =
            new DiskManagement(vm, 9000L, RasdManagement.FIRST_ATTACHMENT_SEQUENCE);
        setup(inputDisk2.getRasd(), inputDisk2);

        // Assert the disks are in the list
        String uri = resolveVirtualMachineDisksUri(vdc.getId(), vapp.getId(), vm.getId());

        ClientResponse response = get(uri, "basicUser", "basicUser");
        assertEquals(response.getStatusCode(), Status.OK.getStatusCode());

        DisksManagementDto outDto = response.getEntity(DisksManagementDto.class);
        assertNotNull(outDto);
        assertEquals(outDto.getCollection().size(), 2);

    }

    /**
     * Test to get a disk.
     */
    @Test
    public void getDiskIT()
    {
        DiskManagement inputDisk2 =
            new DiskManagement(vm, 9000L, RasdManagement.FIRST_ATTACHMENT_SEQUENCE);
        setup(inputDisk2.getRasd(), inputDisk2);

        // Assert the disk is created
        String uri =
            resolveVirtualMachineDiskUri(vdc.getId(), vapp.getId(), vm.getId(),
                Long.valueOf(inputDisk2.getAttachmentOrder()).intValue());

        ClientResponse response = get(uri, "basicUser", "basicUser");
        assertEquals(response.getStatusCode(), Status.OK.getStatusCode());

        DiskManagementDto outDto = response.getEntity(DiskManagementDto.class);
        assertNotNull(outDto);
        assertEquals(outDto.getSizeInMb(), inputDisk2.getSizeInMb());
    }
}
