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

import java.util.Collection;

import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.Context;

import org.apache.wink.common.annotations.Parent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import com.abiquo.api.exceptions.APIError;
import com.abiquo.api.exceptions.NotFoundException;
import com.abiquo.api.resources.cloud.VirtualMachinesResource;
import com.abiquo.api.services.InfrastructureService;
import com.abiquo.api.services.MachineService;
import com.abiquo.api.services.cloud.VirtualMachineService;
import com.abiquo.api.transformer.ModelTransformer;
import com.abiquo.api.util.IRESTBuilder;
import com.abiquo.model.enumerator.HypervisorType;
import com.abiquo.server.core.cloud.Hypervisor;
import com.abiquo.server.core.cloud.VirtualMachine;
import com.abiquo.server.core.cloud.VirtualMachinesDto;
import com.abiquo.server.core.infrastructure.Datastore;
import com.abiquo.server.core.infrastructure.DatastoreDto;
import com.abiquo.server.core.infrastructure.Machine;
import com.abiquo.server.core.infrastructure.MachineDto;
import com.abiquo.server.core.infrastructure.Rack;
import com.abiquo.server.core.infrastructure.RackDto;

@Parent(MachinesResource.class)
@Path(MachineResource.MACHINE_PARAM)
@Controller
public class MachineResource extends AbstractResource
{
    public static final String MACHINE = "machine";

    public static final String MACHINE_PARAM = "{" + MACHINE + "}";

    public static final String MOVE_TARGET_QUERY_PARAM = "target";

    public static final String MACHINE_ACTION_GET_VIRTUALMACHINES = "action/virtualmachines";

    @Autowired
    MachineService service;

    @Autowired
    InfrastructureService rackService;

    @Autowired
    VirtualMachineService vmService;

    @GET
    public MachineDto getMachine(@PathParam(DatacenterResource.DATACENTER) Integer datacenterId,
        @PathParam(RackResource.RACK) Integer rackId, @PathParam(MACHINE) Integer machineId,
        @Context IRESTBuilder restBuilder) throws Exception
    {
        validatePathParameters(datacenterId, rackId, machineId);

        Machine machine = service.getMachine(machineId);
        return createTransferObject(machine, restBuilder);
    }

    @PUT
    public MachineDto modifyMachine(@PathParam(DatacenterResource.DATACENTER) Integer datacenterId,
        @PathParam(RackResource.RACK) Integer rackId, @PathParam(MACHINE) Integer machineId,
        MachineDto machine, @Context IRESTBuilder restBuilder) throws Exception
    {
        validatePathParameters(datacenterId, rackId, machineId);

        Machine m = service.modifyMachine(machineId, machine);

        return createTransferObject(m, restBuilder);
    }

    @DELETE
    public void deleteMachine(@PathParam(DatacenterResource.DATACENTER) Integer datacenterId,
        @PathParam(RackResource.RACK) Integer rackId, @PathParam(MACHINE) Integer machineId)
    {
        validatePathParameters(datacenterId, rackId, machineId);
        service.removeMachine(machineId);
    }

    @GET
    @Path(MachineResource.MACHINE_ACTION_GET_VIRTUALMACHINES)
    public VirtualMachinesDto getVirtualMachines(
        @PathParam(DatacenterResource.DATACENTER) Integer datacenterId,
        @PathParam(RackResource.RACK) Integer rackId,
        @PathParam(MachineResource.MACHINE) Integer machineId, @Context IRESTBuilder restBuilder)
        throws Exception
    {
        Hypervisor hypervisor = getHypervisor(datacenterId, rackId, machineId);

        Collection<VirtualMachine> vms = vmService.findByHypervisor(hypervisor);

        VirtualMachinesDto vmDto =
            VirtualMachinesResource.createAdminTransferObjects(vms, restBuilder);
        return vmDto;
    }

    @DELETE
    @Path(MachineResource.MACHINE_ACTION_GET_VIRTUALMACHINES)
    public void deleteVirtualMachinesNotManaged(
        @PathParam(DatacenterResource.DATACENTER) Integer datacenterId,
        @PathParam(RackResource.RACK) Integer rackId,
        @PathParam(MachineResource.MACHINE) Integer machineId, @Context IRESTBuilder restBuilder)
        throws Exception
    {
        Hypervisor hypervisor = getHypervisor(datacenterId, rackId, machineId);

        vmService.deleteNotManagedVirtualMachines(hypervisor);
    }

    protected Hypervisor getHypervisor(Integer datacenterId, Integer rackId, Integer machineId)
    {
        if (!service.isAssignedTo(datacenterId, rackId, machineId))
        {
            throw new NotFoundException(APIError.NOT_ASSIGNED_MACHINE_DATACENTER_RACK);
        }

        Hypervisor hypervisor = service.getMachine(machineId).getHypervisor();

        if (hypervisor == null)
        {
            throw new NotFoundException(APIError.VIRTUAL_MACHINE_WITHOUT_HYPERVISOR);
        }
        return hypervisor;
    }

    protected static MachineDto addLinks(IRESTBuilder restBuilder, Integer datacenterId,
        Integer rackId, MachineDto machine)
    {
        machine.setLinks(restBuilder.buildMachineLinks(datacenterId, rackId, machine));

        return machine;
    }

    public static MachineDto createTransferObject(Machine machine, IRESTBuilder restBuilder)
        throws Exception
    {
        MachineDto dto = new MachineDto();

        dto.setDescription(machine.getDescription());
        dto.setId(machine.getId());
        dto.setName(machine.getName());
        dto.setRealCpuCores(machine.getRealCpuCores());
        dto.setRealHardDiskInMb(machine.getRealHardDiskInBytes());
        dto.setRealRamInMb(machine.getRealRamInMb());
        dto.setState(machine.getState());
        dto.setVirtualCpuCores(machine.getVirtualCpuCores());
        dto.setVirtualCpusPerCore(machine.getVirtualCpusPerCore());
        dto.setVirtualCpusUsed(machine.getVirtualCpusUsed());
        dto.setVirtualHardDiskInMb(machine.getVirtualHardDiskInBytes());
        dto.setVirtualHardDiskUsedInMb(machine.getVirtualHardDiskUsedInBytes());
        dto.setVirtualRamInMb(machine.getVirtualRamInMb());
        dto.setVirtualRamUsedInMb(machine.getVirtualRamUsedInMb());
        dto.setVirtualSwitch(machine.getVirtualSwitch());
        
        if (machine.getHypervisor() != null)
        {
            dto.setIp(machine.getHypervisor().getIp());
            dto.setIpService(machine.getHypervisor().getIpService());
            dto.setType(machine.getHypervisor().getType());
            dto.setUser(machine.getHypervisor().getUser());
            dto.setPassword(machine.getHypervisor().getPassword());
        }
        
        if (machine.getDatastores() != null)
        {
            for (Datastore datastore : machine.getDatastores())
            {
                DatastoreDto dataDto = new DatastoreDto();
                dataDto.setDirectory(datastore.getDirectory());
                dataDto.setEnabled(datastore.isEnabled());
                dataDto.setId(datastore.getId());
                dataDto.setName(datastore.getName());
                dataDto.setRootPath(datastore.getRootPath());
                dataDto.setShared(datastore.isShared());
                dataDto.setSize(datastore.getSize());
                dataDto.setUsedSize(datastore.getUsedSize());
                
                dto.getDatastores().add(dataDto);
            }
        }
        
        // if the machine comes from the discovery manager it is not already saved in database and it does not have
        // any rack nor datacenter. Don't build the links.
        if (machine.getRack() != null)
        {            
            dto = addLinks(restBuilder, machine.getDatacenter().getId(), machine.getRack().getId(), dto);
        }
        
        return dto;
    }

    protected void validatePathParameters(final Integer datacenterId, final Integer rackId,
        final Integer machineId) throws NotFoundException
    {
        if (!service.isAssignedTo(datacenterId, rackId, machineId))
        {
            throw new NotFoundException(APIError.NOT_ASSIGNED_MACHINE_DATACENTER_RACK);
        }
    }
    
    // Create the persistence object.
    public static Machine createPersistenceObject(MachineDto dto) throws Exception
    {
        // Set the machine values.
        Machine machine = ModelTransformer.persistenceFromTransport(Machine.class, dto);
                
        HypervisorType type = dto.getType();
        String ip = dto.getIp();
        String ipService = dto.getIpService();
        Integer port = dto.getPort();
        String user = dto.getUser();
        String password = dto.getPassword();
        Hypervisor hypervisor = new Hypervisor(machine, type, ip, ipService, port, user, password);
        machine.setHypervisor(hypervisor);
        
        // Set the datastores
        for (DatastoreDto datastoreDto : dto.getDatastores().getCollection())
        {
            machine.getDatastores().add(DatastoreResource.createPersistenceObject(datastoreDto));
        }
        
        return machine;
        
    }
}
