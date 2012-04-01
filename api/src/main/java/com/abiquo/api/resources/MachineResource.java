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

import java.util.ArrayList;
import java.util.List;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Context;

import org.apache.wink.common.annotations.Parent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import com.abiquo.api.exceptions.APIError;
import com.abiquo.api.exceptions.APIException;
import com.abiquo.api.exceptions.ConflictException;
import com.abiquo.api.exceptions.NotFoundException;
import com.abiquo.api.services.InfrastructureService;
import com.abiquo.api.services.MachineService;
import com.abiquo.api.services.cloud.VirtualApplianceService;
import com.abiquo.api.services.cloud.VirtualDatacenterService;
import com.abiquo.api.services.cloud.VirtualMachineService;
import com.abiquo.api.util.IRESTBuilder;
import com.abiquo.model.enumerator.HypervisorType;
import com.abiquo.model.enumerator.MachineState;
import com.abiquo.model.util.ModelTransformer;
import com.abiquo.server.core.cloud.Hypervisor;
import com.abiquo.server.core.enterprise.Enterprise;
import com.abiquo.server.core.infrastructure.Datastore;
import com.abiquo.server.core.infrastructure.DatastoreDto;
import com.abiquo.server.core.infrastructure.Machine;
import com.abiquo.server.core.infrastructure.MachineDto;
import com.abiquo.server.core.infrastructure.MachineStateDto;
import com.abiquo.server.core.infrastructure.MachinesDto;

@Parent(MachinesResource.class)
@Path(MachineResource.MACHINE_PARAM)
@Controller
public class MachineResource extends AbstractResource
{

    public static final String MACHINE = "machine";

    public static final String MACHINE_PARAM = "{" + MACHINE + "}";

    public static final String MOVE_TARGET_QUERY_PARAM = "target";

    public static final String MACHINE_ACTION_GET_VIRTUALMACHINES_PATH = "action/virtualmachines";

    public static final String MACHINE_ACTION_POWER_OFF_PATH = "action/poweroff";

    public static final String MACHINE_ACTION_POWER_OFF_REL = "poweroff";

    public static final String MACHINE_ACTION_POWER_ON_PATH = "action/poweron";

    public static final String MACHINE_ACTION_POWER_ON_REL = "poweron";

    public static final String MACHINE_ACTION_CHECK = "action/checkstate";

    public static final String MACHINE_CHECK = "checkstate";

    public static final String MACHINE_ACTION_LED_ON = "action/ledon";

    public static final String MACHINE_ACTION_LED_ON_REL = "ledon";

    public static final String MACHINE_ACTION_LS = "logicserver";

    public static final String SHOW_CREDENTIALS_QUERY_PARAM = "credentials";

    public static final String MACHINE_ACTION_GET_VIRTUALMACHINES = "action/virtualmachines";

    public static final String MACHINE_ACTION_LED_OFF = "action/ledoff";

    public static final String MACHINE_ACTION_LED_OFF_REL = "ledoff";

    public static final String MACHINE_ACTION_LS__REL = "logicserver";

    public static final String MACHINE_LOCATOR_LED = "led";

    public static final String MACHINE_LOCATOR_LED_REL = "led";

    @Autowired
    MachineService service;

    @Autowired
    InfrastructureService infraService;

    @Autowired
    VirtualMachineService vmService;

    @Autowired
    VirtualDatacenterService vdcService;

    @Autowired
    VirtualApplianceService vappService;

    /**
     * Returns a machine
     * 
     * @title Retrieve a Machine
     * @param datacenterId identifier of the datacenter
     * @param rackId identifier of the rack
     * @param machineId identifier of the machine
     * @param showCredentials boolean to indicate if the machine credentials will be returned
     * @param restBuilder a Context-injected object to create the links of the Dto
     * @return a {machineDto} object with the requested machines
     * @throws Exception
     */
    @GET
    @Produces(MachineDto.MEDIA_TYPE)
    public MachineDto getMachine(
        @PathParam(DatacenterResource.DATACENTER) final Integer datacenterId,
        @PathParam(RackResource.RACK) final Integer rackId,
        @PathParam(MACHINE) final Integer machineId,
        @QueryParam(SHOW_CREDENTIALS_QUERY_PARAM) final Boolean showCredentials,
        @Context final IRESTBuilder restBuilder) throws Exception
    {
        validatePathParameters(datacenterId, rackId, machineId);

        Machine machine = service.getMachine(machineId);
        MachineDto dto = createTransferObject(machine, restBuilder);

        // Credentials are only returned if they are requested
        if (showCredentials != null && showCredentials.equals(Boolean.TRUE)
            && machine.getHypervisor() != null)
        {
            dto.setUser(machine.getHypervisor().getUser());
            dto.setPassword(machine.getHypervisor().getPassword());
        }

        return dto;
    }

    /**
     * Modifies a machine
     * 
     * @title Update an existing Machine
     * @param datacenterId idenfier of the datacenter
     * @param rackId identifier of the rack
     * @param machineId identifier of the machine
     * @param machine machine to modify
     * @param restBuilder a Context-injected object to create the links of the Dto
     * @return a {MachineDto} objec with the modified machine
     * @throws Exception
     */
    @PUT
    @Consumes(MachineDto.MEDIA_TYPE)
    @Produces(MachineDto.MEDIA_TYPE)
    public MachineDto modifyMachine(
        @PathParam(DatacenterResource.DATACENTER) final Integer datacenterId,
        @PathParam(RackResource.RACK) final Integer rackId,
        @PathParam(MACHINE) final Integer machineId, final MachineDto machine,
        @Context final IRESTBuilder restBuilder) throws Exception
    {
        validatePathParameters(datacenterId, rackId, machineId);

        Machine old = service.getMachine(machineId);

        // if we enable the machine then we force a check
        if (old.getState().equals(MachineState.HALTED)
            && !machine.getState().equals(MachineState.HALTED))
        {
            MachineState newState =
                infraService.checkMachineState(datacenterId, machine.getIp(), machine.getType(),
                    old.getHypervisor().getUser(), old.getHypervisor().getPassword(),
                    machine.getPort());
            // machine will be updated with the given state
            machine.setState(newState);
        }

        Machine m = service.modifyMachine(machineId, machine);

        return createTransferObject(m, restBuilder);
    }

    /**
     * Deletes a machine.
     * 
     * @title Delete a Machine
     * @param datacenterId identifier of the datacenter
     * @param rackId identifier of the rack
     * @param machineId identifier of the machine
     */
    @DELETE
    public void deleteMachine(@PathParam(DatacenterResource.DATACENTER) final Integer datacenterId,
        @PathParam(RackResource.RACK) final Integer rackId,
        @PathParam(MACHINE) final Integer machineId)
    {
        validatePathParameters(datacenterId, rackId, machineId);
        service.removeMachine(machineId);
    }

    /**
     * Checks the machine state and updates it.
     * 
     * @title Check Machine state
     * @wiki This feature checks physical machine state using the discovery manager and returns it.
     *       The sync query parameter allows you to update the database with the value of state
     *       returned by the discovery manager.
     * @param datacenterId The ID of the datacenter where this remote service and machine are
     *            assigned.
     * @param ip The IP of the target cloud node.
     * @param hypervisorType The cloud node hypervisor type.
     * @param user The hypervisor user.
     * @param password The hypervisor password.
     * @param port The hypervisor AIM port.
     * @param restBuilder a Context-injected object to create the links of the Dto
     * @return a {MachineStateDto} object with the actual machine's state.
     */
    @GET
    @Path(MACHINE_ACTION_CHECK)
    @Produces(MachineStateDto.MEDIA_TYPE)
    public MachineStateDto checkMachineState(
        @PathParam(DatacenterResource.DATACENTER) final Integer datacenterId,
        @PathParam(RackResource.RACK) final Integer rackId,
        @PathParam(MachineResource.MACHINE) final Integer machineId,
        @QueryParam("sync") @DefaultValue("false") final boolean sync,
        @Context final IRESTBuilder restBuilder) throws Exception
    {
        try
        {
            Machine m = service.getMachine(machineId);
            Hypervisor h = m.getHypervisor();

            MachineState state =
                infraService.checkMachineState(datacenterId, h.getIp(), h.getType(), h.getUser(),
                    h.getPassword(), h.getPort());

            if (sync)
            {
                m.setState(state);
                MachineDto machineDto = createTransferObject(m, restBuilder);
                service.modifyMachine(machineId, machineDto);
            }

            MachineStateDto dto = new MachineStateDto();
            dto.setState(state);
            return dto;
        }
        catch (Exception e)
        {
            throw translateException(e);
        }
    }

    // protected methods
    protected Hypervisor getHypervisor(final Integer datacenterId, final Integer rackId,
        final Integer machineId)
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

    protected static MachineDto addLinks(final IRESTBuilder restBuilder,
        final Integer datacenterId, final Integer rackId, final Boolean managedRack,
        final Enterprise enterprise, final MachineDto machine)
    {
        machine.setLinks(restBuilder.buildMachineLinks(datacenterId, rackId, managedRack,
            enterprise, machine));

        return machine;
    }

    public static MachineDto createTransferObject(final Machine machine,
        final IRESTBuilder restBuilder) throws Exception
    {
        MachineDto dto = new MachineDto();

        dto.setDescription(machine.getDescription());
        dto.setId(machine.getId());
        dto.setName(machine.getName());
        dto.setState(machine.getState());
        dto.setVirtualCpuCores(machine.getVirtualCpuCores());
        dto.setVirtualCpusUsed(machine.getVirtualCpusUsed());
        dto.setVirtualRamInMb(machine.getVirtualRamInMb());
        dto.setVirtualRamUsedInMb(machine.getVirtualRamUsedInMb());
        dto.setVirtualSwitch(machine.getVirtualSwitch());
        dto.setIpmiIP(machine.getIpmiIP());
        dto.setIpmiPort(machine.getIpmiPort());
        dto.setIpmiUser(machine.getIpmiUser());
        dto.setIpmiPassword(machine.getIpmiPassword());
        dto.setInitiatorIQN(machine.getInitiatorIQN());

        if (machine.getHypervisor() != null)
        {
            dto.setIp(machine.getHypervisor().getIp());
            dto.setIpService(machine.getHypervisor().getIpService());
            dto.setType(machine.getHypervisor().getType());
            dto.setPort(machine.getHypervisor().getPort());
            // Credentials are not returned by default
            // dto.setUser(machine.getHypervisor().getUser());
            // dto.setPassword(machine.getHypervisor().getPassword());
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
                dataDto.setSize(datastore.getSize());
                dataDto.setUsedSize(datastore.getUsedSize());
                dataDto.setDatastoreUUID(datastore.getDatastoreUUID());
                dto.getDatastores().add(dataDto);
            }
        }

        // if the machine comes from the discovery manager it is not already saved in database and
        // it does not have
        // any rack nor datacenter. Don't build the links.
        if (machine.getRack() != null)
        {
            dto =
                addLinks(restBuilder, machine.getDatacenter().getId(), machine.getRack().getId(),
                    machine.getBelongsToManagedRack(), machine.getEnterprise(), dto);
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
    public static Machine createPersistenceObject(final MachineDto dto) throws Exception
    {
        // Set the machine values.
        Machine machine = ModelTransformer.persistenceFromTransport(Machine.class, dto);

        HypervisorType type = dto.getType();
        String ip = dto.getIp();
        String ipService = dto.getIpService();
        Integer port = dto.getPort();
        String user = dto.getUser();
        String password = dto.getPassword();

        // usused Hypervisor
        machine.createHypervisor(type, ip, ipService, port, user, password);

        // Set the datastores
        for (DatastoreDto datastoreDto : dto.getDatastores().getCollection())
        {
            machine.getDatastores().add(DatastoreResource.createPersistenceObject(datastoreDto));
        }

        return machine;

    }

    public static List<Machine> createPersistenceObjects(final MachinesDto machinesDto)
        throws Exception
    {
        List<Machine> machines = new ArrayList<Machine>();
        for (MachineDto machineDto : machinesDto.getCollection())
        {
            machines.add(createPersistenceObject(machineDto));
        }
        return machines;
    }

    public static MachinesDto createTransferObjects(final List<Machine> machinesCreated,
        final IRESTBuilder restBuilder) throws Exception
    {
        MachinesDto machinesDto = new MachinesDto();

        for (Machine currentMachine : machinesCreated)
        {
            machinesDto.getCollection().add(createTransferObject(currentMachine, restBuilder));
        }

        return machinesDto;
    }

    /**
     * Translates the Node Collector client exception into a {@link WebApplicationException}.
     * 
     * @param e The Exception to transform.
     * @return The transformed Exception.
     */
    protected APIException translateException(final Exception e)
    {
        if (e instanceof APIException)
        {
            return (APIException) e;
        }
        return new ConflictException(APIError.NODECOLLECTOR_ERROR);
    }
}
