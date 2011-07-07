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
import com.abiquo.api.services.stub.VSMStub;
import com.abiquo.api.services.stub.VSMStubImpl;
import com.abiquo.model.enumerator.RemoteServiceType;
import com.abiquo.server.core.cloud.Hypervisor;
import com.abiquo.server.core.cloud.NodeVirtualImage;
import com.abiquo.server.core.cloud.State;
import com.abiquo.server.core.cloud.VirtualAppliance;
import com.abiquo.server.core.cloud.VirtualDatacenterRep;
import com.abiquo.server.core.cloud.VirtualMachine;
import com.abiquo.server.core.infrastructure.Datacenter;
import com.abiquo.server.core.infrastructure.DatastoreDto;
import com.abiquo.server.core.infrastructure.InfrastructureRep;
import com.abiquo.server.core.infrastructure.Machine;
import com.abiquo.server.core.infrastructure.MachineDto;
import com.abiquo.server.core.infrastructure.Rack;
import com.abiquo.server.core.infrastructure.RemoteService;

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
    protected VSMStub vsm;

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
        vsm = new VSMStubImpl();
        remoteServiceService = new RemoteServiceService(em);
        virtualMachineService = new VirtualMachineService(em);
        virtualDatacenterRep = new VirtualDatacenterRep(em);
    }

    public List<Machine> getMachinesByRack(final Integer rackId)
    {
        Rack rack = repo.findRackById(rackId);
        return repo.findRackMachines(rack);
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public Machine addMachine(final MachineDto machineDto, final Integer rackId)
    {
        Rack rack = repo.findRackById(rackId);
        Datacenter datacenter = rack.getDatacenter();

        // Part 1: Insert the machine into database
        Machine machine =
            datacenter.createMachine(machineDto.getName(), machineDto.getDescription(),

            machineDto.getVirtualRamInMb(), machineDto.getRealRamInMb(),
                machineDto.getVirtualRamUsedInMb(),

                machineDto.getVirtualHardDiskInMb(), machineDto.getRealHardDiskInMb(),
                machineDto.getVirtualHardDiskUsedInMb(),

                machineDto.getRealCpuCores(), machineDto.getVirtualCpuCores(),
                machineDto.getVirtualCpusUsed(), machineDto.getVirtualCpusPerCore(),

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
        if (repo.existAnyHypervisorWithIp(machineDto.getIp()))
        {
            addConflictErrors(APIError.HYPERVISOR_EXIST_IP);
        }

        if (repo.existAnyHypervisorWithIpService(machineDto.getIpService()))
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
                if (dataDto.getDatastoreUUID() == null){
                    dataDto.setDatastoreUUID(UUID.randomUUID().toString());
                }
                dataService.addDatastore(dataDto, machine.getId());
            }
        }

        repo.insertHypervisor(hypervisor);

        return machine;
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

    public Machine getMachine(Integer datacenterId, Integer rackId, Integer machineId)
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
    {
        Machine old = getMachine(machineId);

        old.setName(machineDto.getName());
        old.setDescription(machineDto.getDescription());
        old.setState(machineDto.getState());

        old.setVirtualRamInMb(machineDto.getVirtualRamInMb());
        old.setRealRamInMb(machineDto.getRealRamInMb());
        old.setVirtualRamUsedInMb(machineDto.getVirtualRamUsedInMb());

        old.setVirtualCpuCores(machineDto.getVirtualCpuCores());
        old.setRealCpuCores(machineDto.getRealCpuCores());
        old.setVirtualCpusUsed(machineDto.getVirtualCpusUsed());
        old.setVirtualCpusPerCore(machineDto.getVirtualCpusPerCore());

        old.setVirtualHardDiskInBytes(machineDto.getVirtualHardDiskInMb());
        old.setRealHardDiskInBytes(machineDto.getRealHardDiskInMb());
        old.setVirtualHardDiskUsedInBytes(machineDto.getVirtualHardDiskUsedInMb());

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
        for (VirtualMachine vm : virtualMachines)
        {
            vm.setState(State.NOT_DEPLOYED);
            vm.setDatastore(null);
            vm.setHypervisor(null);

            VirtualAppliance vapp = virtualDatacenterRep.findVirtualApplianceByVirtualMachine(vm);

            State newState = State.NOT_DEPLOYED;
            for (NodeVirtualImage node : vapp.getNodes())
            {
                if (node.getVirtualMachine().getState() != State.NOT_DEPLOYED)
                {
                    newState = State.APPLY_CHANGES_NEEDED;
                    break;
                }
            }

            vapp.setState(newState);
            vapp.setSubState(newState);
            virtualDatacenterRep.updateVirtualAppliance(vapp);
            virtualMachineService.updateVirtualMachine(vm);
        }

        repo.deleteMachine(machine);
    }

    public boolean isAssignedTo(final Integer datacenterId, final Integer rackId,
        final Integer machineId)
    {
        Machine machine = getMachine(machineId);

        if (machine == null)
        {
            return false;
        }

        return (machine.getDatacenter().getId().equals(datacenterId) && machine.getRack().getId()
            .equals(rackId));
    }

    private void isValidMachine(final Machine machine)
    {
        if (!machine.isValid())
        {
            addValidationErrors(machine.getValidationErrors());
        }

        flushErrors();
    }
}

