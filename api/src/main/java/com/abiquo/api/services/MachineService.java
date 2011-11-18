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
import com.abiquo.server.core.cloud.Hypervisor;
import com.abiquo.server.core.cloud.NodeVirtualImage;
import com.abiquo.server.core.cloud.VirtualAppliance;
import com.abiquo.server.core.cloud.VirtualApplianceState;
import com.abiquo.server.core.cloud.VirtualDatacenterRep;
import com.abiquo.server.core.cloud.VirtualMachine;
import com.abiquo.server.core.cloud.VirtualMachineState;
import com.abiquo.server.core.infrastructure.Datastore;
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
    protected VsmServiceStub vsm;

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
    public Machine modifyMachine(final Integer machineId, final MachineDto machineDto)
        throws Exception
    {
        Machine old = getMachine(machineId);

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

        Collection<VirtualMachine> virtualMachines =
            virtualMachineService.findByHypervisor(hypervisor);

        if (virtualMachines != null && !virtualMachines.isEmpty())
        {
            for (VirtualMachine vm : virtualMachines)
            {
                VirtualAppliance vapp =
                    virtualDatacenterRep.findVirtualApplianceByVirtualMachine(vm);

                VirtualApplianceState newState = VirtualApplianceState.NOT_DEPLOYED;
                for (NodeVirtualImage node : vapp.getNodes())
                {
                    if (node.getVirtualMachine().getState() != VirtualMachineState.NOT_ALLOCATED)
                    {
                        if (!force)
                        {
                            addConflictErrors(APIError.RACK_CANNOT_REMOVE_VMS);
                            flushErrors();
                        }
                        else
                        {
                            newState = VirtualApplianceState.NEEDS_SYNC;
                            break;
                        }
                    }
                }

                vm.setState(VirtualMachineState.NOT_ALLOCATED);
                vm.setDatastore(null);
                vm.setHypervisor(null);

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
