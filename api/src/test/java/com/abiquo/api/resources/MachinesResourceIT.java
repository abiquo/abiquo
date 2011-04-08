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

import static com.abiquo.api.common.UriTestResolver.resolveDatastoresURI;
import static com.abiquo.api.common.UriTestResolver.resolveMachinesURI;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;

import javax.ws.rs.core.MediaType;

import org.apache.wink.client.ClientResponse;
import org.apache.wink.client.Resource;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import com.abiquo.api.common.Assert;
import com.abiquo.model.enumerator.RemoteServiceType;
import com.abiquo.server.core.cloud.Hypervisor;
import com.abiquo.server.core.enumerator.HypervisorType;
import com.abiquo.server.core.infrastructure.DatastoreDto;
import com.abiquo.server.core.infrastructure.DatastoresDto;
import com.abiquo.server.core.infrastructure.Machine;
import com.abiquo.server.core.infrastructure.MachineDto;
import com.abiquo.server.core.infrastructure.MachinesDto;
import com.abiquo.server.core.infrastructure.RemoteService;
import com.abiquo.server.core.infrastructure.Machine.State;

public class MachinesResourceIT extends AbstractJpaGeneratorIT
{
    private String machinesURI;
    private String datastoresURI;
    private Machine machine;

    @BeforeMethod
    public void setup()
    {
        Hypervisor hypervisor = hypervisorGenerator.createUniqueInstance();
        machine = hypervisor.getMachine();

        RemoteService rs =
            machine.getDatacenter().createRemoteService(RemoteServiceType.VIRTUAL_SYSTEM_MONITOR,
                "http://localhost:8080/fooo", 1);
        setup(machine.getDatacenter(), machine.getRack(), machine, hypervisor, rs);

        machinesURI =
            resolveMachinesURI(machine.getDatacenter().getId(), machine.getRack().getId());
        
    }

    @Test
    public void getMachinesList() throws Exception
    {
        Resource resource = client.resource(machinesURI);

        ClientResponse response = resource.accept(MediaType.APPLICATION_XML).get();

        assertEquals(200, response.getStatusCode());

        MachinesDto entity = response.getEntity(MachinesDto.class);

        assertNotNull(entity);
        assertNotNull(entity.getCollection());
        assertEquals(entity.getCollection().size(), 1);
    }

    @Test
    public void createMachines()
    {
        Resource resource = client.resource(machinesURI);

        MachineDto m = getValidMachine();

        // HypervisorDto hypervisor = HypervisorResourceIT.getValidHypervisor();
        // m.setHypervisor(hypervisor);

        ClientResponse response =
            resource.contentType(MediaType.APPLICATION_XML).accept(MediaType.APPLICATION_XML).post(
                m);

        assertEquals(response.getStatusCode(), 201);

        MachineDto entityPost = response.getEntity(MachineDto.class);

        assertNotNull(entityPost);

        assertEquals(m.getName(), entityPost.getName());
        assertEquals(m.getDescription(), entityPost.getDescription());
        assertEquals(m.getVirtualCpuCores(), entityPost.getVirtualCpuCores());
        assertEquals(m.getRealCpuCores(), entityPost.getRealCpuCores());
        assertEquals(m.getVirtualHardDiskInMb(), entityPost.getVirtualHardDiskInMb());
        assertEquals(m.getVirtualRamUsedInMb(), entityPost.getVirtualRamUsedInMb());
        assertEquals(m.getVirtualCpusUsed(), entityPost.getVirtualCpusUsed());
        assertEquals(m.getVirtualHardDiskUsedInMb(), entityPost.getVirtualHardDiskUsedInMb());
        assertEquals(m.getVirtualCpusPerCore(), entityPost.getVirtualCpusPerCore());
        assertEquals(m.getType(), entityPost.getType());
        assertEquals(m.getIp(), entityPost.getIp());
        assertEquals(m.getIpService(), entityPost.getIpService());
        assertEquals(m.getUser(), entityPost.getUser());
        assertEquals(m.getPassword(), entityPost.getPassword());
        assertEquals(entityPost.getRealCpuCores(), m.getRealCpuCores());
        assertEquals(entityPost.getRealHardDiskInMb(), m.getRealHardDiskInMb());
        assertEquals(entityPost.getRealRamInMb(), m.getRealRamInMb());
        assertEquals(entityPost.getState(), m.getState());
        assertEquals(entityPost.getVirtualSwitch(), m.getVirtualSwitch());
    }
    
    @Test
    public void createMachinesWithDatastores()
    {
        Resource resource = client.resource(machinesURI);

        MachineDto m = getValidMachine();
        DatastoreDto dto = new DatastoreDto();
        dto.setName("datastoreName");
        dto.setRootPath("/");
        dto.setDirectory("var/lib/virt");
        dto.setShared(Boolean.TRUE);
        dto.setEnabled(Boolean.TRUE);
        m.getDatastores().getCollection().add(dto);
        

        // HypervisorDto hypervisor = HypervisorResourceIT.getValidHypervisor();
        // m.setHypervisor(hypervisor);

        ClientResponse response =
            resource.contentType(MediaType.APPLICATION_XML).accept(MediaType.APPLICATION_XML).post(
                m);

        assertEquals(response.getStatusCode(), 201);

        MachineDto entityPost = response.getEntity(MachineDto.class);

        assertNotNull(entityPost);
        assertEquals(m.getName(), entityPost.getName());
        assertEquals(m.getDescription(), entityPost.getDescription());
        assertEquals(m.getVirtualCpuCores(), entityPost.getVirtualCpuCores());
        assertEquals(m.getRealCpuCores(), entityPost.getRealCpuCores());
        assertEquals(m.getVirtualHardDiskInMb(), entityPost.getVirtualHardDiskInMb());
        assertEquals(m.getVirtualRamUsedInMb(), entityPost.getVirtualRamUsedInMb());
        assertEquals(m.getVirtualCpusUsed(), entityPost.getVirtualCpusUsed());
        assertEquals(m.getVirtualHardDiskUsedInMb(), entityPost.getVirtualHardDiskUsedInMb());
        assertEquals(m.getVirtualCpusPerCore(), entityPost.getVirtualCpusPerCore());
        assertEquals(m.getType(), entityPost.getType());
        assertEquals(m.getIp(), entityPost.getIp());
        assertEquals(m.getIpService(), entityPost.getIpService());
        assertEquals(m.getUser(), entityPost.getUser());
        assertEquals(m.getPassword(), entityPost.getPassword());
        assertEquals(entityPost.getRealCpuCores(), m.getRealCpuCores());
        assertEquals(entityPost.getRealHardDiskInMb(), m.getRealHardDiskInMb());
        assertEquals(entityPost.getRealRamInMb(), m.getRealRamInMb());
        assertEquals(entityPost.getState(), m.getState());
        assertEquals(entityPost.getVirtualSwitch(), m.getVirtualSwitch());
        
        // Check the datastore was correctly created.
        datastoresURI =
            resolveDatastoresURI(machine.getDatacenter().getId(), machine.getRack().getId(),
                entityPost.getId());
        
        resource = client.resource(datastoresURI);
        response = resource.contentType(MediaType.APPLICATION_XML_TYPE).accept(MediaType.APPLICATION_XML_TYPE).get();
        
        assertEquals(response.getStatusCode(), 200);
        DatastoresDto datastoresGET = response.getEntity(DatastoresDto.class);
        assertEquals(datastoresGET.getCollection().size(), 1);        
        
    }

    private MachineDto getValidMachine()
    {
        MachineDto m = new MachineDto();

        m.setName("machine_test");
        m.setDescription("machine_test_description");

        m.setRealRamInMb(200);
        m.setVirtualRamInMb(2);
        m.setVirtualRamUsedInMb(4);

        m.setRealCpuCores(200);
        m.setVirtualCpuCores(18);
        m.setVirtualCpusPerCore(2);
        m.setVirtualCpusUsed(0);

        m.setRealHardDiskInMb(200L);
        m.setVirtualHardDiskInMb(100L);
        m.setVirtualHardDiskUsedInMb(10L);

        m.setState(State.STOPPED);
        m.setVirtualSwitch("192.168.1.1");

        m.setType(HypervisorType.HYPERV_301);
        m.setIp("10.0.0.1");
        m.setIpService("10.0.0.1");
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

        ClientResponse response = getMachineResource().post(m);

        assertEquals(response.getStatusCode(), 400);
        Assert.assertErrors(response, "virtualSwitch");
    }

    private Resource getMachineResource()
    {
        return client.resource(machinesURI).contentType(MediaType.APPLICATION_XML).accept(
            MediaType.APPLICATION_XML);
    }
}
