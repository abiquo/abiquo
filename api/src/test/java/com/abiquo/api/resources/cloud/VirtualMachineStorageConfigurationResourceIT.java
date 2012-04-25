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

import static com.abiquo.api.common.Assert.assertError;
import static com.abiquo.api.common.UriTestResolver.resolveDiskUri;
import static com.abiquo.api.common.UriTestResolver.resolveVirtualMachineDiskUri;
import static com.abiquo.api.common.UriTestResolver.resolveVirtualMachineDisksUri;
import static com.abiquo.server.core.common.EnvironmentGenerator.SYSADMIN;
import static com.abiquo.testng.TestConfig.EDIT_VM_INTEGRATION_TESTS;
import static com.abiquo.testng.TestConfig.STORAGE_INTEGRATION_TESTS;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;
import static org.testng.Assert.assertTrue;

import javax.ws.rs.core.Response.Status;

import org.apache.wink.client.ClientResponse;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import com.abiquo.api.exceptions.APIError;
import com.abiquo.api.resources.AbstractJpaGeneratorIT;
import com.abiquo.model.rest.RESTLink;
import com.abiquo.model.transport.AcceptedRequestDto;
import com.abiquo.model.transport.LinksDto;
import com.abiquo.server.core.cloud.VirtualAppliance;
import com.abiquo.server.core.cloud.VirtualDatacenter;
import com.abiquo.server.core.cloud.VirtualMachine;
import com.abiquo.server.core.cloud.VirtualMachineState;
import com.abiquo.server.core.common.EnvironmentGenerator;
import com.abiquo.server.core.infrastructure.Datastore;
import com.abiquo.server.core.infrastructure.storage.DiskManagement;
import com.abiquo.server.core.infrastructure.storage.DiskManagementDto;
import com.abiquo.server.core.infrastructure.storage.DisksManagementDto;

/**
 * Integration tests for disk management features.
 * 
 * @author jdevesa@abiquo.com
 */
public class VirtualMachineStorageConfigurationResourceIT extends AbstractJpaGeneratorIT
{
    private EnvironmentGenerator environment;

    private VirtualDatacenter vdc;

    private VirtualAppliance vapp;

    private VirtualMachine vm;

    private DiskManagement disk;

    private Datastore datastore;

    @BeforeMethod(groups = {EDIT_VM_INTEGRATION_TESTS, STORAGE_INTEGRATION_TESTS})
    public void setupEnvironment()
    {
        // Generate the environment
        environment = new EnvironmentGenerator(seed);
        environment.generateEnterprise();
        environment.generateInfrastructure();
        environment.generateVirtualDatacenter();
        environment.generateAllocatedVirtualMachine();
        environment.generateDisk();

        setup(environment.getEnvironment().toArray());

        // Get the entities we'll need from the environment
        vdc = environment.get(VirtualDatacenter.class);
        vapp = environment.get(VirtualAppliance.class);
        vm = environment.get(VirtualMachine.class);
        disk = environment.get(DiskManagement.class);
        datastore = environment.get(Datastore.class);
    }

    @Override
    @AfterMethod(groups = {EDIT_VM_INTEGRATION_TESTS, STORAGE_INTEGRATION_TESTS})
    public void tearDown()
    {
        super.tearDown();
    }

    @Test
    public void testGetListOfDisksReturns404IfUnexistingVirtualDatacenter()
    {
        String uri = resolveVirtualMachineDisksUri(vdc.getId() + 100, vapp.getId(), vm.getId());
        ClientResponse response = get(uri, SYSADMIN, SYSADMIN, DisksManagementDto.MEDIA_TYPE);
        assertError(response, Status.NOT_FOUND.getStatusCode(),
            APIError.NON_EXISTENT_VIRTUAL_DATACENTER);
    }

    @Test
    public void testGetListOfDisksReturns404IfUnexistingVirtualAppliance()
    {
        String uri = resolveVirtualMachineDisksUri(vdc.getId(), vapp.getId() + 100, vm.getId());
        ClientResponse response = get(uri, SYSADMIN, SYSADMIN, DisksManagementDto.MEDIA_TYPE);
        assertError(response, Status.NOT_FOUND.getStatusCode(),
            APIError.NON_EXISTENT_VIRTUALAPPLIANCE);
    }

    @Test
    public void testGetListOfDisksReturns404IfUnexistingVirtualMachine()
    {
        String uri = resolveVirtualMachineDisksUri(vdc.getId(), vapp.getId(), vm.getId() + 100);
        ClientResponse response = get(uri, SYSADMIN, SYSADMIN, DisksManagementDto.MEDIA_TYPE);
        assertError(response, Status.NOT_FOUND.getStatusCode(),
            APIError.NON_EXISTENT_VIRTUALMACHINE);
    }

    @Test
    public void testGetVolumeReturns404IfUnexistingDisk()
    {
        String uri =
            resolveVirtualMachineDiskUri(vdc.getId(), vapp.getId(), vm.getId(), disk.getId() + 100);
        ClientResponse response = get(uri, SYSADMIN, SYSADMIN, DiskManagementDto.MEDIA_TYPE);
        assertError(response, Status.NOT_FOUND.getStatusCode(), APIError.HD_NON_EXISTENT_HARD_DISK);
    }

    @Test(groups = {EDIT_VM_INTEGRATION_TESTS})
    public void testAttachDisksReturns400IfInvalidVirtualDatacenter()
    {
        String invalidLink = resolveDiskUri(vdc.getId() + 100, disk.getId());
        LinksDto request = new LinksDto();
        request.addLink(new RESTLink(VirtualMachineStorageConfigurationResource.DISK, invalidLink));

        String uri = resolveVirtualMachineDisksUri(vdc.getId(), vapp.getId(), vm.getId());
        ClientResponse response =
            post(uri, request, SYSADMIN, SYSADMIN, AcceptedRequestDto.MEDIA_TYPE,
                LinksDto.MEDIA_TYPE);
        assertError(response, Status.BAD_REQUEST.getStatusCode(),
            APIError.HD_ATTACH_INVALID_VDC_LINK);
    }

    @Test(groups = {EDIT_VM_INTEGRATION_TESTS})
    public void testDetachHardDiskReturns404IfUnexistingHardDisk()
    {
        String uri =
            resolveVirtualMachineDiskUri(vdc.getId(), vapp.getId(), vm.getId(), disk.getId() + 100);
        ClientResponse response = delete(uri, SYSADMIN, SYSADMIN);
        assertError(response, Status.NOT_FOUND.getStatusCode(), APIError.HD_NON_EXISTENT_HARD_DISK);
    }

    @Test(groups = {EDIT_VM_INTEGRATION_TESTS})
    public void testDetachHardDiskReturns404IfHardDiskNotAttached()
    {
        String uri =
            resolveVirtualMachineDiskUri(vdc.getId(), vapp.getId(), vm.getId(), disk.getId());
        ClientResponse response = delete(uri, SYSADMIN, SYSADMIN);
        assertError(response, Status.NOT_FOUND.getStatusCode(), APIError.HD_NON_EXISTENT_HARD_DISK);
    }

    @Test
    public void testGetListOfHardDisks()
    {
        disk.attach(1, vm);
        update(disk.getRasd(), disk);

        String uri = resolveVirtualMachineDisksUri(vdc.getId(), vapp.getId(), vm.getId());
        ClientResponse response = get(uri, SYSADMIN, SYSADMIN, DisksManagementDto.MEDIA_TYPE);
        assertEquals(response.getStatusCode(), Status.OK.getStatusCode());

        DisksManagementDto dto = response.getEntity(DisksManagementDto.class);
        assertEquals(dto.getCollection().size(), 1);
    }

    @Test
    public void testGetHardDisk()
    {
        disk.attach(1, vm);
        update(disk.getRasd(), disk);

        String uri =
            resolveVirtualMachineDiskUri(vdc.getId(), vapp.getId(), vm.getId(), disk.getId());
        ClientResponse response = get(uri, SYSADMIN, SYSADMIN, DiskManagementDto.MEDIA_TYPE);
        assertEquals(response.getStatusCode(), Status.OK.getStatusCode());

        DiskManagementDto dto = response.getEntity(DiskManagementDto.class);
        assertNotNull(dto);
    }

    @Test(groups = {EDIT_VM_INTEGRATION_TESTS})
    public void testAttachDisksNoLinks()
    {
        String uri = resolveVirtualMachineDisksUri(vdc.getId(), vapp.getId(), vm.getId());
        ClientResponse response =
            post(uri, new LinksDto(), SYSADMIN, SYSADMIN, AcceptedRequestDto.MEDIA_TYPE,
                LinksDto.MEDIA_TYPE);
        assertEquals(response.getStatusCode(), Status.BAD_REQUEST.getStatusCode());

        // Verify that no volume is attached
        response = get(uri, SYSADMIN, SYSADMIN, DisksManagementDto.MEDIA_TYPE);

        // Can not attach a hard disk empty
        assertEquals(response.getStatusCode(), Status.OK.getStatusCode());

        DisksManagementDto dto = response.getEntity(DisksManagementDto.class);
        assertTrue(dto.isEmpty());
    }

    @Test(groups = {EDIT_VM_INTEGRATION_TESTS})
    public void testAttachHardDisksInNotDeployedVM()
    {
        vm.setState(VirtualMachineState.NOT_ALLOCATED);
        update(vm);

        LinksDto request = new LinksDto();
        request.addLink(new RESTLink(VirtualMachineStorageConfigurationResource.DISK,
            resolveDiskUri(vdc.getId(), disk.getId())));

        String uri = resolveVirtualMachineDisksUri(vdc.getId(), vapp.getId(), vm.getId());
        ClientResponse response = post(uri, request, SYSADMIN, SYSADMIN, null, LinksDto.MEDIA_TYPE);
        assertEquals(response.getStatusCode(), Status.NO_CONTENT.getStatusCode());

        // Verify that the disk is attached
        response = get(uri, SYSADMIN, SYSADMIN, DisksManagementDto.MEDIA_TYPE);
        assertEquals(response.getStatusCode(), Status.OK.getStatusCode());

        DisksManagementDto dto = response.getEntity(DisksManagementDto.class);
        assertEquals(dto.getCollection().size(), 1);
    }

    @Test(groups = {EDIT_VM_INTEGRATION_TESTS})
    public void testAttachHardDisksInDeployedVM()
    {
        LinksDto request = new LinksDto();
        request.addLink(new RESTLink(VirtualMachineStorageConfigurationResource.DISK,
            resolveDiskUri(vdc.getId(), disk.getId())));

        String uri = resolveVirtualMachineDisksUri(vdc.getId(), vapp.getId(), vm.getId());
        ClientResponse response =
            post(uri, request, SYSADMIN, SYSADMIN, AcceptedRequestDto.MEDIA_TYPE,
                LinksDto.MEDIA_TYPE);
        assertEquals(response.getStatusCode(), Status.ACCEPTED.getStatusCode());

        // Verify that the operation returned a task reference
        AcceptedRequestDto< ? > dto = response.getEntity(AcceptedRequestDto.class);
        assertNotNull(dto);
        assertNotNull(dto.getEntity());
    }

    @Test(groups = {EDIT_VM_INTEGRATION_TESTS})
    public void testDetachHardDisksInNotDeployedVM()
    {
        vm.setState(VirtualMachineState.NOT_ALLOCATED);
        disk.attach(1, vm);
        update(vm, disk.getRasd(), disk);

        String uri = resolveVirtualMachineDisksUri(vdc.getId(), vapp.getId(), vm.getId());
        ClientResponse response = delete(uri, SYSADMIN, SYSADMIN);
        assertEquals(response.getStatusCode(), Status.NO_CONTENT.getStatusCode());

        // Verify that the volume is not attached
        response = get(uri, SYSADMIN, SYSADMIN, DisksManagementDto.MEDIA_TYPE);
        assertEquals(response.getStatusCode(), Status.OK.getStatusCode());

        DisksManagementDto dto = response.getEntity(DisksManagementDto.class);
        assertTrue(dto.isEmpty());
    }

    @Test(groups = {EDIT_VM_INTEGRATION_TESTS}, enabled = false)
    public void testDetachVolumesInDeployedVM()
    {
        disk.attach(1, vm);
        datastore.setUsedSize(datastore.getUsedSize() + disk.getSizeInMb() * 1024 * 1024);
        update(disk.getRasd(), disk, datastore);

        String uri = resolveVirtualMachineDisksUri(vdc.getId(), vapp.getId(), vm.getId());
        ClientResponse response = delete(uri, SYSADMIN, SYSADMIN);
        assertEquals(response.getStatusCode(), Status.ACCEPTED.getStatusCode());

        // Verify that the operation returned a task reference
        AcceptedRequestDto< ? > dto = response.getEntity(AcceptedRequestDto.class);
        assertNotNull(dto);
        assertNotNull(dto.getEntity());
    }

    @Test(groups = {EDIT_VM_INTEGRATION_TESTS})
    public void testChangeHardDisksInNotDeployedVM()
    {
        vm.setState(VirtualMachineState.NOT_ALLOCATED);
        update(vm);

        // Create a second disk and attach it
        DiskManagement disk2 = diskGenerator.createInstance(vdc);
        disk2.attach(1, vm);
        setup(disk2.getRasd(), disk2);

        // Create the the DTO to attach the first one and detach the currently attached one
        LinksDto request = new LinksDto();
        request.addLink(new RESTLink(VirtualMachineStorageConfigurationResource.DISK,
            resolveDiskUri(vdc.getId(), disk.getId())));

        String uri = resolveVirtualMachineDisksUri(vdc.getId(), vapp.getId(), vm.getId());
        ClientResponse response = put(uri, request, SYSADMIN, SYSADMIN, null, LinksDto.MEDIA_TYPE);
        assertEquals(response.getStatusCode(), Status.NO_CONTENT.getStatusCode());

        // Verify that there is only one disk attached and it is the first one
        response = get(uri, SYSADMIN, SYSADMIN, DisksManagementDto.MEDIA_TYPE);
        assertEquals(response.getStatusCode(), Status.OK.getStatusCode());

        DisksManagementDto dto = response.getEntity(DisksManagementDto.class);
        assertEquals(dto.getCollection().size(), 1);
    }

    @Test(groups = {EDIT_VM_INTEGRATION_TESTS}, enabled = false)
    public void testChangeHardDisksInDeployedVM()
    {
        // Create a second disk and attach it
        DiskManagement disk2 = diskGenerator.createInstance(vdc);
        disk2.attach(1, vm);
        datastore.setUsedSize(datastore.getUsedSize() + (disk.getSizeInMb() + disk2.getSizeInMb())
            * 1024 * 1024);
        setup(disk2.getRasd(), disk2);
        update(datastore);

        // Create the the DTO to attach the first one and detach the currently attached one
        LinksDto request = new LinksDto();
        request.addLink(new RESTLink(VirtualMachineStorageConfigurationResource.DISK,
            resolveDiskUri(vdc.getId(), disk.getId())));

        String uri = resolveVirtualMachineDisksUri(vdc.getId(), vapp.getId(), vm.getId());
        ClientResponse response =
            put(uri, request, SYSADMIN, SYSADMIN, AcceptedRequestDto.MEDIA_TYPE,
                LinksDto.MEDIA_TYPE);
        assertEquals(response.getStatusCode(), Status.ACCEPTED.getStatusCode());

        // Verify that the operation returned a task reference
        AcceptedRequestDto< ? > dto = response.getEntity(AcceptedRequestDto.class);
        assertNotNull(dto);
        assertNotNull(dto.getEntity());
    }

    @Test(groups = {EDIT_VM_INTEGRATION_TESTS})
    public void testDetachHardDiskInNotDeployedVM()
    {
        vm.setState(VirtualMachineState.NOT_ALLOCATED);
        update(vm);

        disk.attach(1, vm);
        update(disk.getRasd(), disk);

        String uri =
            resolveVirtualMachineDiskUri(vdc.getId(), vapp.getId(), vm.getId(), disk.getId());
        ClientResponse response = delete(uri, SYSADMIN, SYSADMIN);
        assertEquals(response.getStatusCode(), Status.NO_CONTENT.getStatusCode());

        // Verify that the disk is not attached
        uri = resolveVirtualMachineDisksUri(vdc.getId(), vapp.getId(), vm.getId());
        response = get(uri, SYSADMIN, SYSADMIN, DisksManagementDto.MEDIA_TYPE);
        assertEquals(response.getStatusCode(), Status.OK.getStatusCode());

        DisksManagementDto dto = response.getEntity(DisksManagementDto.class);
        assertTrue(dto.isEmpty());
    }

    @Test(groups = {EDIT_VM_INTEGRATION_TESTS}, enabled = false)
    public void testDetachHardDiskInDeployedVM()
    {
        disk.attach(1, vm);
        datastore.setUsedSize(datastore.getUsedSize() + disk.getSizeInMb() * 1024 * 1024);
        update(disk.getRasd(), disk, datastore);

        String uri =
            resolveVirtualMachineDiskUri(vdc.getId(), vapp.getId(), vm.getId(), disk.getId());
        ClientResponse response = delete(uri, SYSADMIN, SYSADMIN);
        assertEquals(response.getStatusCode(), Status.ACCEPTED.getStatusCode());

        // Verify that the operation returned a task reference
        AcceptedRequestDto< ? > dto = response.getEntity(AcceptedRequestDto.class);
        assertNotNull(dto);
        assertNotNull(dto.getEntity());
    }
}
