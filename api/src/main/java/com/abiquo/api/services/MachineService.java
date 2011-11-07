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

package com.abiquo.api.services;

import java.util.Collection;
import java.util.List;
import java.util.UUID;

import javax.persistence.EntityManager;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import com.abiquo.api.exceptions.APIError;
import com.abiquo.api.exceptions.InternalServerErrorException;
import com.abiquo.api.services.cloud.VirtualMachineService;
import com.abiquo.api.services.stub.VsmServiceStub;
import com.abiquo.api.tracer.TracerLogger;
import com.abiquo.model.enumerator.RemoteServiceType;
import com.abiquo.model.enumerator.VirtualMachineState;
import com.abiquo.server.core.cloud.Hypervisor;
import com.abiquo.server.core.cloud.NodeVirtualImage;
import com.abiquo.server.core.cloud.VirtualAppliance;
import com.abiquo.server.core.cloud.VirtualDatacenterRep;
import com.abiquo.server.core.cloud.VirtualMachine;
import com.abiquo.server.core.infrastructure.Datacenter;
import com.abiquo.server.core.infrastructure.Datastore;
import com.abiquo.server.core.infrastructure.DatastoreDto;
import com.abiquo.server.core.infrastructure.InfrastructureRep;
import com.abiquo.server.core.infrastructure.Machine;
import com.abiquo.server.core.infrastructure.MachineDto;
import com.abiquo.server.core.infrastructure.Rack;
import com.abiquo.server.core.infrastructure.RemoteService;
import com.abiquo.server.core.infrastructure.UcsRack;
import com.abiquo.tracer.ComponentType;
import com.abiquo.tracer.EventType;
import com.abiquo.tracer.SeverityType;

@Service
@Transactional(readOnly = false)
public class MachineService extends DefaultApiService
{
    protected static final Logger logger = LoggerFactory.getLogger(MachineService.class);

    @Autowired
    protected InfrastructureRep repo;

    @Autowired
    protected DatastoreService dataService;

    @Autowired
    private VsmServiceStub vsm;

    @Autowired
    protected RemoteServiceService remoteServiceService;

    @Autowired
    protected VirtualMachineService virtualMachineService;

    @Autowired
    protected VirtualDatacenterRep virtualDatacenterRep;

    public MachineService()
    {

    }

    public MachineService(final EntityManager em)
    {
        repo = new InfrastructureRep(em);
        dataService = new DatastoreService(em);
        vsm = new VsmServiceStub();
        virtualMachineService = new VirtualMachineService(em);
        virtualDatacenterRep = new VirtualDatacenterRep(em);
        remoteServiceService = new RemoteServiceService(em);
        tracer = new TracerLogger();
    }

    public List<Machine> getMachinesByRack(final Integer rackId)
    {
        return getMachinesByRack(rackId, null);
    }

    public List<Machine> getMachinesByRack(final Integer rackId, final String filter)
    {
        Rack rack = repo.findRackById(rackId);
        List<Machine> machines = repo.findRackMachines(rack, filter);

        // If it is an UCS rack, put the property 'belongsToManagedRack' as true.
        // If they belong to a managed rack, a new {@link RESTLink} will be created
        // in the Dto informing the managed machines special functionality.

        UcsRack ucsRack = repo.findUcsRackById(rackId);
        if (ucsRack != null)
        {
            for (Machine machine : machines)
            {
                machine.setBelongsToManagedRack(Boolean.TRUE);
            }
        }

        return machines;
    }

    public Machine getMachine(final Integer id)
    {
        if (id == 0)
        {
            addValidationErrors(APIError.INVALID_ID);
            flushErrors();
        }

        Machine machine = repo.findMachineById(id);
        if (machine == null)
        {
            addNotFoundErrors(APIError.NON_EXISTENT_MACHINE);
            flushErrors();
        }

        return machine;
    }

    public Machine getMachine(final Integer datacenterId, final Integer rackId,
        final Integer machineId)
    {
        Machine machine = repo.findMachineByIds(datacenterId, rackId, machineId);

        if (machine == null)
        {
            addNotFoundErrors(APIError.NON_EXISTENT_MACHINE);
            flushErrors();
        }
        return machine;

    }

    @Transactional(propagation = Propagation.REQUIRED)
    public Machine addMachine(final MachineDto machineDto, final Integer rackId)
    {
        Rack rack = repo.findRackById(rackId);
        Datacenter datacenter = rack.getDatacenter();

        // Part 1: Insert the machine into database
        Machine machine =
            datacenter.createMachine(machineDto.getName(), machineDto.getDescription(),

            machineDto.getVirtualRamInMb(), machineDto.getVirtualRamUsedInMb(),

            machineDto.getVirtualCpuCores(), machineDto.getVirtualCpusUsed(),
                machineDto.getVirtualCpusPerCore(),

                machineDto.getState(), machineDto.getVirtualSwitch());

        machine.setRack(rack);

        isValidMachine(machine);

        // Monitoring machine
        RemoteService vsmRS =
            remoteServiceService.getRemoteService(datacenter.getId(),
                RemoteServiceType.VIRTUAL_SYSTEM_MONITOR);

        Hypervisor hypervisor =
            machine.createHypervisor(machineDto.getType(), machineDto.getIp(),
                machineDto.getIpService(), machineDto.getPort(), machineDto.getUser(),
                machineDto.getPassword());

        vsm.monitor(vsmRS.getUri(), hypervisor.getIp(), hypervisor.getPort(), hypervisor.getType()
            .name(), hypervisor.getUser(), hypervisor.getPassword());

        repo.insertMachine(machine);

        // Part 2: Insert the hypervisor into database.
        if (repo.existAnyHypervisorWithIpInDatacenter(machineDto.getIp(), datacenter.getId()))
        {
            addConflictErrors(APIError.HYPERVISOR_EXIST_IP);
        }

        if (repo.existAnyHypervisorWithIpServiceInDatacenter(machineDto.getIpService(),
            datacenter.getId()))
        {
            addConflictErrors(APIError.HYPERVISOR_EXIST_SERVICE_IP);
        }
        flushErrors();

        if (!hypervisor.isValid())
        {
            addValidationErrors(hypervisor.getValidationErrors());
        }
        flushErrors();

        // Part 3. Call the Datastores resource to create them also
        // Add the Remote Services in database in case are informed in the request
        if (machineDto.getDatastores() != null)
        {
            for (DatastoreDto dataDto : machineDto.getDatastores().getCollection())
            {
                // FIXME: All Datastores need to have an UUID in DB
                if (dataDto.getDatastoreUUID() == null)
                {
                    dataDto.setDatastoreUUID(UUID.randomUUID().toString());
                }
                dataService.addDatastore(dataDto, machine.getId());
            }
        }

        repo.insertHypervisor(hypervisor);

        return machine;
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public Machine modifyMachine(final Integer machineId, final MachineDto machineDto)
        throws Exception
    {
        Machine old = getMachine(machineId);
        if (old.getBelongsToManagedRack())
        {
            if (!old.getName().equalsIgnoreCase(machineDto.getName()))
            {
                addValidationErrors(APIError.MANAGED_MACHINE_CANNOT_CHANGE_NAME);
                flushErrors();
            }
        }

        old.setName(machineDto.getName());
        old.setDescription(machineDto.getDescription());
        old.setState(machineDto.getState());

        old.setVirtualRamInMb(machineDto.getVirtualRamInMb());
        old.setVirtualRamUsedInMb(machineDto.getVirtualRamUsedInMb());

        old.setVirtualCpuCores(machineDto.getVirtualCpuCores());
        old.setVirtualCpusUsed(machineDto.getVirtualCpusUsed());
        old.setVirtualCpusPerCore(machineDto.getVirtualCpusPerCore());

        isValidMachine(old);

        // [ABICLOUDPREMIUM-1516] If ip service changes, must change the vrdp ip of the
        // virtual machines deployed in that hypervisor
        if (StringUtils.hasText(machineDto.getIpService())
            && !machineDto.getIpService().equals(old.getHypervisor().getIpService()))
        {
            old.getHypervisor().setIpService(machineDto.getIpService());
            updateVirtualMachines(old.getHypervisor(), machineDto.getIpService());
        }

        repo.updateMachine(old);

        tracer
            .log(SeverityType.INFO, ComponentType.MACHINE, EventType.MACHINE_CREATE, "Machine "
                + old.getName() + "[ ip: " + old.getHypervisor().getIp() + " type: "
                + old.getHypervisor().getType() + " state: " + old.getState()
                + "] created succesfully");

        return old;
    }

    private void updateVirtualMachines(final Hypervisor hypervisor, final String ipService)
    {
        Collection<VirtualMachine> vms = virtualMachineService.findByHypervisor(hypervisor);
        if (vms != null && !vms.isEmpty())
        {
            for (VirtualMachine vm : vms)
            {
                if (StringUtils.hasText(vm.getVdrpIP()))
                {
                    vm.setVdrpIP(ipService);
                }
            }
        }
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public void removeMachine(final Integer id)
    {
        removeMachine(id, true);
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public void removeMachine(final Integer id, final boolean force)
    {
        Machine machine = repo.findMachineById(id);
        RemoteService vsmRS =
            remoteServiceService.getRemoteService(machine.getDatacenter().getId(),
                RemoteServiceType.VIRTUAL_SYSTEM_MONITOR);

        Hypervisor hypervisor = machine.getHypervisor();
        try
        {
            vsm.shutdownMonitor(vsmRS.getUri(), hypervisor.getIp(), hypervisor.getPort());
        }
        catch (InternalServerErrorException e)
        {
            // we can ignore this error
        }

        // Delete not maneged vms is needed before update virtual appliances
        virtualMachineService.deleteNotManagedVirtualMachines(hypervisor, true);

        Collection<VirtualMachine> virtualMachines =
            virtualMachineService.findByHypervisor(hypervisor);

        if (virtualMachines != null && !virtualMachines.isEmpty())
        {
            for (VirtualMachine vm : virtualMachines)
            {
                VirtualAppliance vapp =
                    virtualDatacenterRep.findVirtualApplianceByVirtualMachine(vm);

                VirtualMachineState newState = VirtualMachineState.NOT_DEPLOYED;
                for (NodeVirtualImage node : vapp.getNodes())
                {
                    if (node.getVirtualMachine().getState() != VirtualMachineState.NOT_DEPLOYED)
                    {
                        if (!force)
                        {
                            addConflictErrors(APIError.RACK_CANNOT_REMOVE_VMS);
                            flushErrors();
                        }
                        else
                        {
                            newState = VirtualMachineState.APPLY_CHANGES_NEEDED;
                            break;
                        }
                    }
                }

                vm.setState(VirtualMachineState.NOT_DEPLOYED);
                vm.setDatastore(null);
                vm.setHypervisor(null);

                vapp.setState(newState);
                vapp.setSubState(newState);
                virtualDatacenterRep.updateVirtualAppliance(vapp);
                virtualMachineService.updateVirtualMachine(vm);
            }
        }

        deleteMachineLoadRulesFromMachine(machine);

        if (machine.getDatastores() != null && !machine.getDatastores().isEmpty())
        {
            for (Datastore d : machine.getDatastores())
            {
                repo.deleteDatastore(d);
            }
        }

        repo.deleteMachine(machine);

        tracer.log(SeverityType.INFO, ComponentType.MACHINE, EventType.MACHINE_DELETE, "Machine "
            + machine.getName() + "[ ip: " + machine.getHypervisor().getIp() + " type: "
            + machine.getHypervisor().getType() + " state: " + machine.getState()
            + "] deleted succesfully");
    }

    protected void deleteMachineLoadRulesFromMachine(final Machine machine)
    {
        // PREMIUM
    }

    public boolean isAssignedTo(final Integer datacenterId, final Integer rackId,
        final Integer machineId)
    {
        Machine machine = getMachine(machineId);

        if (machine == null)
        {
            return false;
        }

        return machine.getDatacenter().getId().equals(datacenterId)
            && machine.getRack().getId().equals(rackId);
    }

    private void isValidMachine(final Machine machine)
    {
        if (!machine.isValid())
        {
            addValidationErrors(machine.getValidationErrors());
        }

        flushErrors();
    }

    // Needed in unit testing

    public VsmServiceStub getVsm()
    {
        return vsm;
    }

    public void setVsm(final VsmServiceStub vsm)
    {
        this.vsm = vsm;
    }
}
