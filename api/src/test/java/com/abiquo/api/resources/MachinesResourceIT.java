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

package com.abiquo.api.resources;

import static com.abiquo.api.common.Assert.assertError;
import static com.abiquo.api.common.UriTestResolver.resolveDatastoresURI;
import static com.abiquo.api.common.UriTestResolver.resolveMachinesURI;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;

import java.util.UUID;

import javax.ws.rs.core.Response.Status;

import org.apache.wink.client.ClientResponse;
import org.apache.wink.client.Resource;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import com.abiquo.api.exceptions.APIError;
import com.abiquo.api.services.stub.NodecollectorServiceStubMock;
import com.abiquo.model.enumerator.HypervisorType;
import com.abiquo.model.enumerator.RemoteServiceType;
import com.abiquo.model.transport.error.ErrorsDto;
import com.abiquo.server.core.cloud.Hypervisor;
import com.abiquo.server.core.infrastructure.Datacenter;
import com.abiquo.server.core.infrastructure.DatastoreDto;
import com.abiquo.server.core.infrastructure.DatastoresDto;
import com.abiquo.server.core.infrastructure.Machine;
import com.abiquo.server.core.infrastructure.MachineDto;
import com.abiquo.server.core.infrastructure.MachinesDto;
import com.abiquo.server.core.infrastructure.MachinesToCreateDto;
import com.abiquo.server.core.infrastructure.Rack;
import com.abiquo.server.core.infrastructure.RemoteService;
import com.abiquo.server.core.infrastructure.UcsRack;

public class MachinesResourceIT extends AbstractJpaGeneratorIT
{
    private String machinesURI;

    private String datastoresURI;

    private Machine machine;

    private Hypervisor hypervisor;

    @Override
    @BeforeMethod
    public void setup()
    {
        hypervisor = hypervisorGenerator.createUniqueInstance();
        hypervisor.setIpService(NodecollectorServiceStubMock.IP_DISCOVER_FIRST);

        machine = hypervisor.getMachine();

        RemoteService vsm =
            machine.getDatacenter().createRemoteService(RemoteServiceType.VIRTUAL_SYSTEM_MONITOR,
                "http://localhost:8080/fooo", 1);
        RemoteService nc =
            machine.getDatacenter().createRemoteService(RemoteServiceType.NODE_COLLECTOR,
                "http://localhost:8080/bar", 1);
        setup(machine.getDatacenter(), machine.getRack(), machine, hypervisor, vsm, nc);

        machinesURI =
            resolveMachinesURI(machine.getDatacenter().getId(), machine.getRack().getId());

    }

    @Test
    public void getMachinesList() throws Exception
    {
        Resource resource = client.resource(machinesURI);

        ClientResponse response = resource.accept(MachinesDto.MEDIA_TYPE).get();

        assertEquals(200, response.getStatusCode());

        MachinesDto entity = response.getEntity(MachinesDto.class);

        assertNotNull(entity);
        assertNotNull(entity.getCollection());
        assertEquals(entity.getCollection().size(), 1);
    }

    @Test
    public void getMachinesListFiltered() throws Exception
    {
        String filter = "?filter=notMatches";
        Resource resource = client.resource(machinesURI + filter);

        ClientResponse response = resource.accept(MachinesDto.MEDIA_TYPE).get();

        assertEquals(200, response.getStatusCode());

        MachinesDto entity = response.getEntity(MachinesDto.class);

        assertNotNull(entity);
        assertNotNull(entity.getCollection());
        assertEquals(entity.getCollection().size(), 0);
    }

    @Test
    public void createMachinesWithDatastores()
    {
        MachineDto m = getValidMachine();
        DatastoreDto dto = new DatastoreDto();
        dto.setName("datastoreName");
        dto.setRootPath("/");
        dto.setDirectory("var/lib/virt");
        dto.setDatastoreUUID(UUID.randomUUID().toString());
        dto.setEnabled(Boolean.TRUE);
        m.getDatastores().getCollection().add(dto);

        // HypervisorDto hypervisor = HypervisorResourceIT.getValidHypervisor();
        // m.setHypervisor(hypervisor);

        ClientResponse response = post(machinesURI, m);

        assertEquals(response.getStatusCode(), Status.CREATED.getStatusCode());

        MachineDto entityPost = response.getEntity(MachineDto.class);

        assertNotNull(entityPost);
        assertEquals(m.getName(), entityPost.getName());
        assertEquals(m.getDescription(), entityPost.getDescription());
        assertEquals(m.getType(), entityPost.getType());
        assertEquals(m.getIp(), entityPost.getIp());
        assertEquals(m.getIpService(), entityPost.getIpService());
        assertEquals(entityPost.getVirtualSwitch(), m.getVirtualSwitch());

        // Check the datastore was correctly created.
        datastoresURI =
            resolveDatastoresURI(machine.getDatacenter().getId(), machine.getRack().getId(),
                entityPost.getId());

        response = get(datastoresURI, DatastoresDto.MEDIA_TYPE);

        assertEquals(response.getStatusCode(), Status.OK.getStatusCode());
        DatastoresDto datastoresGET = response.getEntity(DatastoresDto.class);
        assertEquals(datastoresGET.getCollection().size(), 1);

    }

    /**
     * A machine can not be added to a UCS Rack unless you use the premium functionality.
     * 
     * @throws Exception
     */
    @Test
    public void canNotCreateMachineViaPostInUCSRack() throws Exception
    {
        Datacenter datacenter = machine.getDatacenter();
        UcsRack ucsRack = ucsRackGenerator.createInstance(datacenter);
        setup(ucsRack);

        MachineDto machineDto = getValidMachine();
        DatastoreDto dto = new DatastoreDto();
        dto.setName("datastoreName");
        dto.setRootPath("/");
        dto.setDirectory("var/lib/virt");
        dto.setEnabled(Boolean.TRUE);
        machineDto.getDatastores().getCollection().add(dto);

        machinesURI = resolveMachinesURI(machine.getDatacenter().getId(), ucsRack.getId());

        ClientResponse response = post(machinesURI, machineDto);

        assertError(response, Status.CONFLICT.getStatusCode(),
            APIError.MACHINE_CAN_NOT_BE_ADDED_IN_UCS_RACK);

    }

    /**
     * Test the creation of physical machine fails if the remote service is not created.
     */
    @Test
    public void canNotCreateMachineVSMNotCreated() throws Exception
    {
        Rack rack = rackGenerator.createUniqueInstance();
        RemoteService nc =
            rack.getDatacenter().createRemoteService(RemoteServiceType.NODE_COLLECTOR,
                "http://localhost:8080/bar", 1);

        setup(rack.getDatacenter(), rack, nc);

        MachineDto machineDto = getValidMachine();
        DatastoreDto dto = new DatastoreDto();
        dto.setName("datastoreName");
        dto.setRootPath("/");
        dto.setDirectory("var/lib/virt");
        dto.setDatastoreUUID(UUID.randomUUID().toString());
        dto.setEnabled(Boolean.TRUE);
        machineDto.getDatastores().getCollection().add(dto);

        machinesURI = resolveMachinesURI(rack.getDatacenter().getId(), rack.getId());

        ClientResponse response = post(machinesURI, machineDto);

        ErrorsDto errors = response.getEntity(ErrorsDto.class);
        assertError(response, Status.NOT_FOUND.getStatusCode(),
            APIError.NON_EXISTENT_REMOTE_SERVICE_TYPE);
    }

    /**
     * Create multiple physical machines in the same time.
     * 
     * @throws Exception
     */
    @Test(enabled = false)
    // TODO check directory "" in datastores
    public void createMultipleMachines() throws Exception
    {
        MachinesToCreateDto machinesDto = new MachinesToCreateDto();
        machinesDto.setIpFrom(NodecollectorServiceStubMock.IP_DISCOVER_FIRST);
        machinesDto.setIpTo(NodecollectorServiceStubMock.IP_DISCOVER_LAST);
        machinesDto.setHypervisor(hypervisor.getType().getValue()); // anyHypervisor
        machinesDto.setPassword("anyPassword");
        machinesDto.setPort(0); // anyPort
        machinesDto.setUser("anyUsers");
        machinesDto.setvSwitch("vSwitch0");

        machinesURI =
            resolveMachinesURI(machine.getDatacenter().getId(), machine.getRack().getId());

        Resource resource = client.resource(machinesURI);
        ClientResponse response =
            resource.contentType(MachinesToCreateDto.MEDIA_TYPE)
                .accept(MachinesToCreateDto.MEDIA_TYPE).post(machinesDto);

        // Assert both are created
        assertEquals(response.getStatusCode(), 201);
        MachinesDto machines = response.getEntity(MachinesDto.class);
        assertNotNull(machines);
        assertEquals(machines.getCollection().size(), 2, machines.getErrors().toString());

    }

    /**
     * Checks you can not create a machine with a Trailing Slashh "/" value in the vswitch
     * 
     * @throws Exception
     */
    @Test
    public void canNotCreateMachineWithTrailingSlashInVswitchName() throws Exception
    {
        Resource resource = client.resource(machinesURI);

        MachineDto m = getValidMachine();
        m.setVirtualSwitch(m.getVirtualSwitch() + "/");
        DatastoreDto dto = new DatastoreDto();
        dto.setName("datastoreName");
        dto.setRootPath("/");
        dto.setDirectory("var/lib/virt");
        dto.setDatastoreUUID(UUID.randomUUID().toString());
        dto.setEnabled(Boolean.TRUE);
        m.getDatastores().getCollection().add(dto);

        ClientResponse response = post(machinesURI, m);

        assertError(response, Status.BAD_REQUEST.getStatusCode(),
            APIError.MACHINE_INVALID_VIRTUAL_SWITCH_NAME);
    }

    private MachineDto getValidMachine()
    {
        MachineDto m = new MachineDto();

        m.setName("machine_test");
        m.setDescription("machine_test_description");
        m.setVirtualSwitch("192.168.1.1");

        m.setType(HypervisorType.HYPERV_301);
        m.setIp(NodecollectorServiceStubMock.IP_DISCOVER_LAST);
        m.setIpService(NodecollectorServiceStubMock.IP_DISCOVER_LAST);
        m.setPort(3556);
        m.setUser("foo");
        m.setPassword("bar");

        return m;
    }

    @Test
    public void createMachineWithInvalidVirtualSwitch()
    {
        MachineDto m = getValidMachine();
        m.setVirtualSwitch(null);

        ClientResponse response = post(machinesURI, m);

        assertEquals(response.getStatusCode(), Status.BAD_REQUEST.getStatusCode());
    }

}
