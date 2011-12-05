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

package com.abiquo.api.resources.cloud;

import static com.abiquo.api.common.UriTestResolver.resolveDisksUri;
import static com.abiquo.api.common.UriTestResolver.resolveDiskUri;
import static com.abiquo.testng.TestConfig.STORAGE_INTEGRATION_TESTS;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;

import javax.ws.rs.core.Response.Status;

import org.apache.wink.client.ClientResponse;
import org.springframework.security.context.SecurityContextHolder;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import com.abiquo.api.common.BasicUserAuthentication;
import com.abiquo.api.exceptions.BadRequestException;
import com.abiquo.api.resources.AbstractJpaGeneratorIT;
import com.abiquo.api.services.StorageService;
import com.abiquo.api.services.cloud.DiskManagementServiceTest;
import com.abiquo.server.core.cloud.NodeVirtualImage;
import com.abiquo.server.core.cloud.VirtualAppliance;
import com.abiquo.server.core.cloud.VirtualApplianceState;
import com.abiquo.server.core.cloud.VirtualDatacenter;
import com.abiquo.server.core.cloud.VirtualMachine;
import com.abiquo.server.core.enterprise.DatacenterLimits;
import com.abiquo.server.core.enterprise.Enterprise;
import com.abiquo.server.core.enterprise.Role;
import com.abiquo.server.core.enterprise.User;
import com.abiquo.server.core.infrastructure.storage.DiskManagement;
import com.abiquo.server.core.infrastructure.storage.DiskManagementDto;
import com.abiquo.server.core.infrastructure.storage.DisksManagementDto;

/**
 * Acceptance test for the creation of Disks and retrieve of a list of them. This class only test
 * the use causes that can cause a {@link BadRequestException} due a client's bad call. The rest of
 * the use cases are tested in class {@link DiskManagementServiceTest}. Tests at the uri:
 * http://host/api/cloud/virtualdatacenters/{id_vdc}/disks
 * 
 * @author jdevesa@abiquo.com
 */
@Test(groups = {STORAGE_INTEGRATION_TESTS})
public class DisksResourceIT extends AbstractJpaGeneratorIT
{
    /** Service we are testing */
    protected StorageService service;

    protected VirtualAppliance vapp;

    protected VirtualDatacenter vdc;

    protected VirtualMachine vm;

    @BeforeMethod(groups = {STORAGE_INTEGRATION_TESTS})
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
        setup(vdc.getDatacenter(), vdc, dclimit, vapp, vm.getVirtualMachineTemplate().getCategory(), vm
            .getVirtualMachineTemplate().getRepository().getDatacenter(), vm.getVirtualMachineTemplate()
            .getRepository(), vm.getVirtualMachineTemplate(), vm.getHypervisor().getMachine().getRack(), vm
            .getHypervisor().getMachine(), vm.getHypervisor(), vm, nvi);

        SecurityContextHolder.getContext().setAuthentication(new BasicUserAuthentication());
    }

    @Override
    @AfterMethod(groups = {STORAGE_INTEGRATION_TESTS})
    public void tearDown()
    {
        super.tearDown();
    }

    /**
     * Just check the entity can be created throught the API.
     */
    @Test
    public void createHardDiskEndToEndTest()
    {
        String uri = resolveDisksUri(vdc.getId());
        DiskManagementDto newDisk = new DiskManagementDto();
        newDisk.setSizeInMb(12000L);

        ClientResponse response = post(uri, newDisk, "basicUser", "basicUser");
        assertEquals(response.getStatusCode(), Status.CREATED.getStatusCode());
    }

    /**
     * Identifier of VDC always should be bigger or equal than 0 when creating
     */
    @Test
    public void createHardDiskRaises400WhenVDCIdIsLowerThan1()
    {
        String uri = resolveDisksUri(-40);
        DiskManagementDto newDisk = new DiskManagementDto();
        newDisk.setSizeInMb(12000L);

        ClientResponse response = post(uri, newDisk);
        assertEquals(response.getStatusCode(), Status.BAD_REQUEST.getStatusCode());
    }

    /**
     * Just check the get of multiple hard disks works.
     */
    @Test
    public void getListHardDisksEndToEndTest()
    {
        DiskManagement disk1 = diskGenerator.createInstance(vdc);
        DiskManagement disk2 = diskGenerator.createInstance(vdc);
        DiskManagement disk3 = diskGenerator.createInstance(vdc);
        setup(disk1.getRasd(), disk2.getRasd(), disk3.getRasd(), disk1, disk2, disk3);

        String uri = resolveDisksUri(vdc.getId());
        ClientResponse response = get(uri, "basicUser", "basicUser");

        assertEquals(response.getStatusCode(), Status.OK.getStatusCode());
        assertNotNull(response.getEntity(DisksManagementDto.class));

        DisksManagementDto dtos = response.getEntity(DisksManagementDto.class);
        assertEquals(dtos.getCollection().size(), 3);
    }

    /**
     * Identifier of VDC always should be bigger or equal than 0 when getting the list of HDs
     */
    @Test
    public void getListHardDisksRaises400WhenVDCIdIsLowerThan1()
    {
        DiskManagement disk1 = diskGenerator.createInstance(vdc);
        DiskManagement disk2 = diskGenerator.createInstance(vdc);
        DiskManagement disk3 = diskGenerator.createInstance(vdc);
        setup(disk1.getRasd(), disk2.getRasd(), disk3.getRasd(), disk1, disk2, disk3);

        String uri = resolveDisksUri(-12);
        ClientResponse response = get(uri);

        assertEquals(response.getStatusCode(), Status.BAD_REQUEST.getStatusCode());
    }

    /**
     * Just check the get of hard disk works.
     */
    @Test
    public void getHardDiskEndToEndTest()
    {
        DiskManagement disk1 = diskGenerator.createInstance(vdc);
        setup(disk1.getRasd(), disk1);

        String uri = resolveDiskUri(vdc.getId(), disk1.getId());
        ClientResponse response = get(uri, "basicUser", "basicUser");

        assertEquals(response.getStatusCode(), Status.OK.getStatusCode());
        assertNotNull(response.getEntity(DiskManagementDto.class));
    }

    /**
     * Identifier of VDC and identifier of the disk always should be bigger or equal than 0 when
     * getting the list of HDs
     */
    @Test
    public void getHardDisksRaises400WhenValuesLowerThan1()
    {
        DiskManagement disk1 = diskGenerator.createInstance(vdc);
        setup(disk1.getRasd(), disk1);

        String uri = resolveDiskUri(-12, disk1.getId());
        ClientResponse response = get(uri);
        assertEquals(response.getStatusCode(), Status.BAD_REQUEST.getStatusCode());
        
        uri = resolveDiskUri(vdc.getId(), -21);
        response = get(uri);
        assertEquals(response.getStatusCode(), Status.BAD_REQUEST.getStatusCode());
    }
    
    /**
     * Just check the delte of hard disk works.
     */
    @Test
    public void deleteHardDisksEndToEndTest()
    {
        DiskManagement disk1 = diskGenerator.createInstance(vdc);
        setup(disk1.getRasd(), disk1);

        String uri = resolveDiskUri(vdc.getId(), disk1.getId());
        ClientResponse response = delete(uri, "basicUser", "basicUser");

        assertEquals(response.getStatusCode(), Status.NO_CONTENT.getStatusCode());
        
        // Not found should be raised afeter delete the disk
        response = get(uri);
        assertEquals(response.getStatusCode(), Status.NOT_FOUND.getStatusCode());
    }

    /**
     * Identifier of VDC and identifier of the disk always should be bigger or equal than 0 when
     * deleting the list of HDs
     */
    @Test
    public void deleteHardDisksRaises400WhenValuesLowerThan1()
    {
        DiskManagement disk1 = diskGenerator.createInstance(vdc);
        setup(disk1.getRasd(), disk1);

        String uri = resolveDiskUri(-12, disk1.getId());
        ClientResponse response = delete(uri);
        assertEquals(response.getStatusCode(), Status.BAD_REQUEST.getStatusCode());
        
        uri = resolveDiskUri(vdc.getId(), -21);
        response = delete(uri);
        assertEquals(response.getStatusCode(), Status.BAD_REQUEST.getStatusCode());
    }

}
