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

package com.abiquo.api.services.cloud;

import java.util.HashMap;
import java.util.Map;

import javax.persistence.EntityManager;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.abiquo.api.exceptions.APIError;
import com.abiquo.api.services.DefaultApiService;
import com.abiquo.model.enumerator.HypervisorType;
import com.abiquo.server.core.cloud.NodeVirtualImage;
import com.abiquo.server.core.cloud.VirtualAppliance;
import com.abiquo.server.core.cloud.VirtualApplianceState;
import com.abiquo.server.core.cloud.VirtualMachine;
import com.abiquo.server.core.cloud.VirtualMachineRep;
import com.abiquo.server.core.cloud.VirtualMachineState;
import com.abiquo.server.core.cloud.VirtualMachineStateTransition;
import com.abiquo.tracer.ComponentType;
import com.abiquo.tracer.EventType;
import com.abiquo.tracer.SeverityType;

/**
 * {@link VirtualMachine} lock and unlock functionality.
 * <p>
 * The operation this class executes must be atomic, to ensure that the lock will be effective at
 * the moment it is requested. To achieve this every operation must be executed in an isolated
 * transaction.
 * 
 * @author Ignasi Barrera
 */

@Service
public class VirtualMachineLock extends DefaultApiService
{
    /** The logger. **/
    private final static Logger LOGGER = LoggerFactory.getLogger(VirtualMachineLock.class);

    @Autowired
    private VirtualMachineRep vmRepo;

    @Autowired
    private VirtualMachineService vmService;

    @Autowired
    private VirtualApplianceService vappService;

    public VirtualMachineLock()
    {

    }

    public VirtualMachineLock(final EntityManager em)
    {
        this.vmRepo = new VirtualMachineRep(em);
        this.vmService = new VirtualMachineService(em);
        this.vappService = new VirtualApplianceService(em);
    }

    @Transactional(readOnly = false, propagation = Propagation.REQUIRES_NEW)
    public VirtualMachineState lockVirtualMachineBeforeDeploying(final Integer vdcId,
        final Integer vappId, final Integer vmId)
    {
        VirtualMachine vm = vmService.getVirtualMachine(vdcId, vappId, vmId);
        VirtualMachineState originalState = vm.getState();

        if (!VirtualMachineState.NOT_ALLOCATED.equals(originalState))
        {
            tracer.systemLog(SeverityType.CRITICAL, ComponentType.VIRTUAL_MACHINE,
                EventType.VM_DEPLOY, "virtualMachine.deployedOrAllocated.system", vm.getName(),
                originalState.name());
            tracer.log(SeverityType.CRITICAL, ComponentType.VIRTUAL_MACHINE, EventType.VM_DEPLOY,
                "virtualMachine.deployedOrAllocated", vm.getName());
            addConflictErrors(APIError.VIRTUAL_MACHINE_INVALID_STATE_DEPLOY);
            flushErrors();
        }

        LOGGER.debug("The virtual machine is in state {} and is valid for deploy. Locking it..."
            + originalState.name());

        lock(vm);

        return originalState;
    }

    @Transactional(readOnly = false, propagation = Propagation.REQUIRES_NEW)
    public VirtualMachineState lockVirtualMachineBeforeUndeploying(final Integer vdcId,
        final Integer vappId, final Integer vmId)
    {
        VirtualMachine vm = vmService.getVirtualMachine(vdcId, vappId, vmId);
        VirtualMachineState originalState = vm.getState();
        if (originalState == VirtualMachineState.LOCKED)
        {
            tracer.log(SeverityType.CRITICAL, ComponentType.VIRTUAL_MACHINE, EventType.VM_UNDEPLOY,
                APIError.VIRTUAL_MACHINE_INVALID_STATE_UNDEPLOY.getMessage());
            tracer.systemLog(SeverityType.CRITICAL, ComponentType.VIRTUAL_MACHINE,
                EventType.VM_UNDEPLOY, "virtualMachine.cannotUndeployed", vm.getName());
            addConflictErrors(APIError.VIRTUAL_MACHINE_INVALID_STATE_UNDEPLOY);
            flushErrors();

        }
        if (originalState.existsInHypervisor())
        {
            lock(vm);
        }

        return originalState;
    }

    @Transactional(readOnly = false, propagation = Propagation.REQUIRES_NEW)
    public VirtualMachineState lockVirtualMachineBeforeChangingState(final Integer vdcId,
        final Integer vappId, final Integer vmId, final VirtualMachineState newState)
    {
        VirtualMachine vm = vmService.getVirtualMachine(vdcId, vappId, vmId);
        VirtualMachineState originalState = vm.getState();

        // Pause is not allowed in XEN
        if (vm.getHypervisor().getType() == HypervisorType.XEN_3
            && newState == VirtualMachineState.PAUSED)
        {
            addConflictErrors(APIError.VIRTUAL_MACHINE_PAUSE_UNSUPPORTED);
            flushErrors();
        }

        // The change state applies on the hypervisor. Now there is a NOT_ALLOCATED to get rid of
        // the if(!hypervisor)
        if (!originalState.existsInHypervisor())
        {
            addConflictErrors(APIError.VIRTUAL_MACHINE_UNALLOCATED_STATE);
            flushErrors();
        }

        // Validate the state transition
        VirtualMachineStateTransition validTransition =
            VirtualMachineStateTransition.getValidVmStateChangeTransition(originalState, newState);
        if (validTransition == null)
        {
            addConflictErrors(APIError.VIRTUAL_MACHINE_STATE_CHANGE_ERROR);
            flushErrors();
        }

        LOGGER.debug(
            "The virtual machine is in state {} and is valid for changing its state to {}. "
                + "Locking it..." + originalState.name(), newState.name());

        lock(vm);

        return originalState;
    }

    @Transactional(readOnly = false, propagation = Propagation.REQUIRES_NEW)
    public VirtualMachineState lockVirtualMachineBeforeResetting(final Integer vdcId,
        final Integer vappId, final Integer vmId)
    {
        VirtualMachine vm = vmService.getVirtualMachine(vdcId, vappId, vmId);
        VirtualMachineState originalState = vm.getState();

        // The change state applies on the hypervisor. Now there is a NOT_ALLOCATED to get rid of
        // the if(!hypervisor)
        if (!originalState.existsInHypervisor())
        {
            addConflictErrors(APIError.VIRTUAL_MACHINE_UNALLOCATED_STATE);
            flushErrors();
        }

        if (originalState != VirtualMachineState.ON)
        {
            addConflictErrors(APIError.VIRTUAL_MACHINE_INVALID_STATE_RESET);
            flushErrors();
        }

        LOGGER.debug("The virtual machine is in state {} and is valid for resetting. Locking it..."
            + originalState.name());

        lock(vm);

        return originalState;
    }

    @Transactional(readOnly = false, propagation = Propagation.REQUIRES_NEW)
    public VirtualMachineState lockVirtualMachineBeforeSnapshotting(final Integer vdcId,
        final Integer vappId, final Integer vmId)
    {
        VirtualMachine vm = vmService.getVirtualMachine(vdcId, vappId, vmId);
        VirtualMachineState originalState = vm.getState();

        if (!originalState.isDeployed())
        {
            // TODO Add some more specific APIError here
            addConflictErrors(APIError.VIRTUAL_MACHINE_NOT_DEPLOYED);
            flushErrors();
        }

        LOGGER
            .debug("The virtual machine is in state {} and is valid for snapshotting. Locking it..."
                + originalState.name());

        lock(vm);

        return originalState;
    }

    @Transactional(readOnly = false, propagation = Propagation.REQUIRES_NEW)
    public VirtualMachineState lockVirtualMachineBeforeReconfiguring(final Integer vdcId,
        final Integer vappId, final Integer vmId)
    {
        VirtualMachine vm = vmService.getVirtualMachine(vdcId, vappId, vmId);
        VirtualMachineState originalState = vm.getState();

        if (!originalState.reconfigureAllowed())
        {
            final String current =
                String.format("VirtualMachine %s in %s", vm.getUuid(), originalState.name());

            tracer.log(SeverityType.CRITICAL, ComponentType.VIRTUAL_MACHINE,
                EventType.VM_RECONFIGURE, APIError.VIRTUAL_MACHINE_INCOHERENT_STATE.getMessage());

            tracer.systemLog(SeverityType.CRITICAL, ComponentType.VIRTUAL_MACHINE,
                EventType.VM_RECONFIGURE, APIError.VIRTUAL_MACHINE_INCOHERENT_STATE.getMessage()
                    + "\n" + current);

            addConflictErrors(APIError.VIRTUAL_MACHINE_INCOHERENT_STATE);
            flushErrors();
        }

        if (vm.isImported())
        {
            tracer.log(SeverityType.CRITICAL, ComponentType.VIRTUAL_MACHINE,
                EventType.VM_RECONFIGURE,
                APIError.VIRTUAL_MACHINE_IMPORTED_CAN_NOT_RECONFIGURE.getMessage());

            addConflictErrors(APIError.VIRTUAL_MACHINE_IMPORTED_CAN_NOT_RECONFIGURE);
            flushErrors();
        }

        LOGGER
            .debug("The virtual machine is in state {} and is valid for reconfiguring. Locking it..."
                + originalState.name());

        lock(vm);

        return originalState;
    }

    @Transactional(readOnly = false, propagation = Propagation.REQUIRES_NEW)
    public VirtualMachineState lockVirtualMachineBeforeDeleting(final Integer vdcId,
        final Integer vappId, final Integer vmId)
    {
        VirtualMachine vm = vmService.getVirtualMachine(vdcId, vappId, vmId);
        VirtualMachineState originalState = vm.getState();

        if (VirtualMachineState.LOCKED.equals(originalState))
        {
            LOGGER.error("Delete virtual machine error, the state is LOCKED");
            tracer.log(SeverityType.CRITICAL, ComponentType.VIRTUAL_MACHINE, EventType.VM_DELETE,
                "virtualMachine.deleteFailed", vm.getName(), originalState.name());
            addConflictErrors(APIError.VIRTUAL_MACHINE_INVALID_STATE_DELETE);
            flushErrors();
        }

        LOGGER.debug("The virtual machine is in state {} and is valid for deleting. Locking it..."
            + originalState.name());

        lock(vm);

        return originalState;
    }

    @Transactional(readOnly = false, propagation = Propagation.REQUIRES_NEW)
    public Map<Integer, VirtualMachineState> lockVirtualApplianceBeforeDeploying(
        final Integer vdcId, final Integer vappId)
    {
        VirtualAppliance vapp = vappService.getVirtualAppliance(vdcId, vappId);

        Map<Integer, VirtualMachineState> originalStates =
            new HashMap<Integer, VirtualMachineState>();

        for (NodeVirtualImage node : vapp.getNodes())
        {
            VirtualMachine vm = node.getVirtualMachine();
            originalStates.put(vm.getId(), vm.getState());
            lockVirtualMachineBeforeDeploying(vdcId, vappId, vm.getId());
        }

        return originalStates;
    }

    @Transactional(readOnly = false, propagation = Propagation.REQUIRES_NEW)
    public Map<Integer, VirtualMachineState> lockVirtualApplianceBeforeUndeploying(
        final Integer vdcId, final Integer vappId)
    {
        VirtualAppliance vapp = vappService.getVirtualAppliance(vdcId, vappId);

        Map<Integer, VirtualMachineState> originalStates =
            new HashMap<Integer, VirtualMachineState>();

        for (NodeVirtualImage node : vapp.getNodes())
        {
            VirtualMachine vm = node.getVirtualMachine();
            originalStates.put(vm.getId(), vm.getState());
            lockVirtualMachineBeforeUndeploying(vdcId, vappId, vm.getId());
        }

        return originalStates;
    }

    @Transactional(readOnly = false, propagation = Propagation.REQUIRES_NEW)
    public Map<Integer, VirtualMachineState> lockVirtualApplianceBeforeDeleting(
        final Integer vdcId, final Integer vappId)
    {
        VirtualAppliance vapp = vappService.getVirtualAppliance(vdcId, vappId);

        if (vapp.getState() != VirtualApplianceState.UNKNOWN
            && vapp.getState() != VirtualApplianceState.NOT_DEPLOYED)
        {
            LOGGER.error("Delete virtual appliance error, the state is {}", vapp.getState());
            tracer.log(SeverityType.CRITICAL, ComponentType.VIRTUAL_APPLIANCE,
                EventType.VAPP_DELETE, "virtualAppliance.deleteFailed", vapp.getName(), vapp
                    .getState().name());
            addConflictErrors(APIError.VIRTUALAPPLIANCE_INVALID_STATE_DELETE);
            flushErrors();
        }

        Map<Integer, VirtualMachineState> originalStates =
            new HashMap<Integer, VirtualMachineState>();

        for (NodeVirtualImage node : vapp.getNodes())
        {
            VirtualMachine vm = node.getVirtualMachine();
            originalStates.put(vm.getId(), vm.getState());
            lockVirtualMachineBeforeDeleting(vdcId, vappId, vm.getId());
        }

        return originalStates;
    }

    @Transactional(readOnly = false, propagation = Propagation.REQUIRES_NEW)
    public void unlockVirtualMachine(final Integer vmId, final VirtualMachineState originalState)
    {
        VirtualMachine vm = vmRepo.findVirtualMachineById(vmId);
        LOGGER
            .debug("Unlocking virtual machine {}. Current state: {}", vm.getName(), vm.getState());
        vm.setState(originalState);
        vmRepo.update(vm);
        LOGGER.debug("Virtual machine {} in state {} after unlock", vm.getName(), vm.getState());
    }

    @Transactional(readOnly = false, propagation = Propagation.REQUIRES_NEW)
    public void unlockVirtualMachines(final Map<Integer, VirtualMachineState> originalStates)
    {
        for (Integer vmId : originalStates.keySet())
        {
            unlockVirtualMachine(vmId, originalStates.get(vmId));
        }
    }

    private void lock(final VirtualMachine vm)
    {
        vm.setState(VirtualMachineState.LOCKED);
        LOGGER.debug("The virtual machine is now in state {}." + vm.getState().name());
    }

}
