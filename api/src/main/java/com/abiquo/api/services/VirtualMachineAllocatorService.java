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

import java.util.List;

import javax.jms.ResourceAllocationException;
import javax.persistence.EntityManager;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.abiquo.api.exceptions.APIError;
import com.abiquo.api.exceptions.ConflictException;
import com.abiquo.model.enumerator.FitPolicy;
import com.abiquo.model.enumerator.MachineState;
import com.abiquo.model.transport.error.CommonError;
import com.abiquo.scheduler.ResourceUpgradeUse;
import com.abiquo.scheduler.ResourceUpgradeUseException;
import com.abiquo.scheduler.SchedulerLock;
import com.abiquo.scheduler.VirtualMachineFactory;
import com.abiquo.scheduler.VirtualMachineRequirementsFactory;
import com.abiquo.scheduler.check.IMachineCheck;
import com.abiquo.scheduler.check.MachineCheck;
import com.abiquo.scheduler.limit.EnterpriseLimitChecker;
import com.abiquo.scheduler.limit.LimitExceededException;
import com.abiquo.scheduler.workload.AllocatorException;
import com.abiquo.scheduler.workload.NotEnoughResourcesException;
import com.abiquo.scheduler.workload.VirtualimageAllocationService;
import com.abiquo.server.core.cloud.VirtualAppliance;
import com.abiquo.server.core.cloud.VirtualApplianceDAO;
import com.abiquo.server.core.cloud.VirtualMachine;
import com.abiquo.server.core.cloud.VirtualMachineDAO;
import com.abiquo.server.core.cloud.VirtualMachineRep;
import com.abiquo.server.core.infrastructure.Machine;
import com.abiquo.server.core.infrastructure.Rack;
import com.abiquo.server.core.infrastructure.UcsRack;
import com.abiquo.server.core.scheduler.VirtualMachineRequirements;

/**
 * Selects the target machine to allocate a virtual machines.
 * <p>
 * Virtual machine requirements are defined on the virtual image and additional storage or network
 * configurations.
 * <p>
 * Before select the machine check if the current allowed limits are exceeded.
 * <p>
 * Enterprise edition support the definition of affinity, exclusion and work load rules.
 * 
 * @author apuig
 */
@Service
public class VirtualMachineAllocatorService extends DefaultApiService
{
    protected final static Logger LOG = LoggerFactory
        .getLogger(VirtualMachineAllocatorService.class);

    @Autowired
    private VirtualMachineRequirementsFactory vmRequirements;

    @Autowired
    private VirtualApplianceDAO virtualAppDao;

    @Autowired
    protected VirtualimageAllocationService allocationService;

    @Autowired
    protected VirtualMachineFactory vmFactory;

    @Autowired
    protected IMachineCheck machineChecker;

    @Autowired
    protected VirtualMachineDAO virtualMachineDao;

    @Autowired
    protected EnterpriseLimitChecker checkEnterpirse;

    @Autowired
    protected ResourceUpgradeUse upgradeUse;

    @Autowired
    protected InfrastructureService infrastructureService;

    @Autowired
    protected VirtualMachineRep vmRepo;

    public VirtualMachineAllocatorService()
    {

    }

    public VirtualMachineAllocatorService(final EntityManager em)
    {
        this.virtualAppDao = new VirtualApplianceDAO(em);
        this.allocationService = new VirtualimageAllocationService(em);
        this.vmFactory = new VirtualMachineFactory(em);
        this.machineChecker = new MachineCheck();
        this.virtualMachineDao = new VirtualMachineDAO(em);
        this.checkEnterpirse = new EnterpriseLimitChecker(em);
        this.upgradeUse = new ResourceUpgradeUse(em);
        this.vmRequirements = new VirtualMachineRequirementsFactory();
        this.vmRepo = new VirtualMachineRep(em);
    }

    /**
     * Check if we can allocate the new virtual machine according to the new values.
     * 
     * @param idVirtualApp
     * @param vmachine
     * @param newvmachine
     * @param foreceEnterpriseSoftLimits
     */
    @Transactional(readOnly = true, propagation = Propagation.REQUIRED)
    public void checkAllocate(final Integer idVirtualApp, final VirtualMachine vmachine,
        final VirtualMachineRequirements increaseRequirements,
        final boolean foreceEnterpriseSoftLimits)
    {
        final VirtualAppliance vapp = virtualAppDao.findById(idVirtualApp);
        final Machine machine = vmachine.getHypervisor().getMachine();

        if (vmachine.getHypervisor() == null || vmachine.getHypervisor().getMachine() == null)
        {
            addConflictErrors(APIError.CHECK_EDIT_NO_TARGET_MACHINE);
            flushErrors();
        }

        try
        {
            checkLimist(vapp, increaseRequirements, foreceEnterpriseSoftLimits, true);

            boolean check =
                allocationService.checkVirtualMachineResourceIncrease(machine,
                    increaseRequirements, idVirtualApp);

            if (!check)
            {
                final String cause =
                    String.format("Current workload rules (RAM and CPU oversubscription) "
                        + "on the target machine: %s disallow the required resources increment.",
                        machine.getName());
                throw new AllocatorException(cause);
            }

            upgradeUse.updateUsed(vmachine.getHypervisor().getMachine(), vmachine.getDatastore(),
                increaseRequirements);
            upgradeUse.updateNetworkingResources(vmachine.getHypervisor().getMachine(), vmachine,
                vapp);

        }
        catch (NotEnoughResourcesException e)
        {
            addConflictErrors(createErrorWithExceptionDetails(APIError.NOT_ENOUGH_RESOURCES,
                vmachine, e));
        }
        catch (LimitExceededException limite)
        {
            if (limite.isHardLimit())
            {
                addConflictErrors(new CommonError(APIError.LIMIT_EXCEEDED.name(), limite.toString()));
            }
            else
            {
                addConflictErrors(new CommonError(APIError.SOFT_LIMIT_EXCEEDED.name(),
                    limite.toString()));
            }
        }
        catch (AllocatorException e)
        {
            addConflictErrors(createErrorWithExceptionDetails(APIError.ALLOCATOR_ERROR, vmachine, e));
        }
        catch (Exception e)
        {
            addUnexpectedErrors(createErrorWithExceptionDetails(APIError.ALLOCATOR_ERROR, vmachine,
                e));
        }
        finally
        {
            flushErrors();
        }
    }

    /**
     * Test entrance
     */
    @Transactional(readOnly = false, propagation = Propagation.REQUIRED)
    protected VirtualMachine allocate(final Integer vmid, final Integer vapid,
        final Boolean foreceEnterpriseSoftLimits)
    {
        return allocateVirtualMachine(virtualMachineDao.findById(vmid),
            virtualAppDao.findById(vapid), foreceEnterpriseSoftLimits);
    }

    @Transactional(readOnly = false, propagation = Propagation.REQUIRED)
    public void deallocate(final Integer vmid)
    {
        deallocateVirtualMachine(virtualMachineDao.findById(vmid));
    }

    /**
     * Creates a virtual machine using some hypervisor on the current virtual appliance datacenter.
     * <p>
     * Physical Infrastructure synchronized. @see {@link SchedulerLock}
     * 
     * @param targetImage, target vmtemplate to deploy (virtual machine template). Determine basic
     *            resource utilization (CPU, RAM, HD) (additionally repository utilization)
     * @param resources, additional resources configurations to be added on the virtual machine
     * @param user, the user performing the virtual machine creation.
     * @param virtualAppId, the virtual appliance id requiring this virtual machine (contains
     *            information about the target {@link VirtualDataCenterHB}).
     * @param foreceEnterpriseSoftLimits, indicating if the virtual appliance should be started even
     *            when the soft limit is exceeded. if false and the soft limit is reached a
     *            {@link SoftLimitExceededException} is thrown, otherwise generate a EVENT TRACE.
     * @return a Virtual Machine based on the provided virtual machine template on some (best)
     *         machine.
     * @throws AllocatorException, direct subclasses {@link HardLimitExceededException} when the
     *             current virtual machine template requirements will exceed the total allowed
     *             resource reservation, {@link SoftLimitExceededException} on
     *             ''foreceEnterpriseSoftLimits'' = false and the soft limit is exceeded.
     * @throws ResourceAllocationException, if none of the machines on the datacenter can be used to
     *             perform this operation.
     */
    @Transactional(readOnly = false, propagation = Propagation.REQUIRED)
    public VirtualMachine allocateVirtualMachine(final VirtualMachine vmachine,
        final VirtualAppliance vapp, final Boolean foreceEnterpriseSoftLimits)
    {

        try
        {
            final VirtualMachineRequirements requirements =
                vmRequirements.createVirtualMachineRequirements(vmachine);

            final Integer idDatacenter = vapp.getVirtualDatacenter().getDatacenter().getId();
            final FitPolicy fitPolicy = getAllocationFitPolicyOnDatacenter(idDatacenter);

            checkLimist(vapp, requirements, foreceEnterpriseSoftLimits, false);

            VirtualMachine allocatedvm =
                selectPhysicalMachineAndAllocateResources(vmachine, vapp, fitPolicy, requirements);

            // vmdao.detachVirtualMachine(vmachine);

            return allocatedvm;
        }
        catch (NotEnoughResourcesException e)
        {
            addConflictErrors(createErrorWithExceptionDetails(APIError.NOT_ENOUGH_RESOURCES,
                vmachine, e));
        }
        catch (LimitExceededException limite)
        {
            if (limite.isHardLimit())
            {
                addConflictErrors(new CommonError(APIError.LIMIT_EXCEEDED.name(), limite.toString()));
            }
            else
            {
                addConflictErrors(new CommonError(APIError.SOFT_LIMIT_EXCEEDED.name(),
                    limite.toString()));
            }
        }
        catch (AllocatorException e)
        {
            addConflictErrors(createErrorWithExceptionDetails(APIError.ALLOCATOR_ERROR, vmachine, e));
        }
        catch (Exception e)
        {
            addUnexpectedErrors(createErrorWithExceptionDetails(APIError.ALLOCATOR_ERROR, vmachine,
                e));
        }
        finally
        {
            flushErrors();
        }

        return null; // unreachable code
    }

    @Transactional(readOnly = true, propagation = Propagation.REQUIRED)
    private VirtualMachine selectPhysicalMachineAndAllocateResources(final VirtualMachine vmachine,
        final VirtualAppliance vapp, final FitPolicy fitPolicy,
        final VirtualMachineRequirements requirements)
    {

        Machine targetMachine = allocationService.findBestTarget(requirements, fitPolicy, vapp);

        LOG.info("Attempt to use physical machine [{}] to allocate VirtualMachine [{}]",
            targetMachine.getName(), vmachine.getName());

        // CREATE THE VIRTUAL MACHINE
        VirtualMachine allocatedVirtualMachine =
            vmFactory.createVirtualMachine(targetMachine, vmachine);

        try
        {
            upgradeUse.updateUse(vapp, allocatedVirtualMachine);
        }
        catch (ResourceUpgradeUseException e) // TODO with this error no other machine candidate
        {
            APIError error = APIError.NOT_ENOUGH_RESOURCES;
            error.addCause(String.format("%s\n%s", virtualMachineInfo(vmachine), e.getMessage()));
            addConflictErrors(error);
        }
        finally
        {
            flushErrors();
        }

        return allocatedVirtualMachine;
    }

    /**
     * <p>
     * .DO NOT perform any resource limitation check (Enterprise, VDC or DC). As the original
     * virtual machine running on the original hypervisor will be deallocated one the hypervisor can
     * be reached.
     * 
     * @param, vmachineId, an already allocated virtual machine (hypervisor and datastore are set)
     *         but we wants to move it.
     */
    @Transactional(readOnly = false, propagation = Propagation.REQUIRES_NEW)
    public VirtualMachine allocateHAVirtualMachine(final VirtualMachine vmId,
        final VirtualMachineRequirements requirements) throws AllocatorException,
        ResourceAllocationException
    {
        LOG.error("community can't *allocateHAVirtualMachine*");
        return null;
    }

    @Transactional(readOnly = false, propagation = Propagation.REQUIRED)
    public void updateResourcesUsageOnTargetMachine(final VirtualMachine virtualMachine,
        final Machine machine) throws ResourceAllocationException
    {
        LOG.error("community can't *updateResourcesUsageOnTargetMachine*");
    }

    protected CommonError createErrorWithExceptionDetails(final APIError apiError,
        final VirtualMachine virtualMachine, final Exception e)
    {
        final String msg =
            String.format("%s (%s)\n%s", apiError.getMessage(), virtualMachineInfo(virtualMachine),
                e.getMessage());

        return new CommonError(apiError.getCode(), msg);
    }

    /**
     * Roll back the changes on the target physical machine after the virtual machine is destroyed
     * (of excluded by some exception on the virtual machine creation on the hypervisor).
     * <p>
     * Physical Infrastructure synchronized. @see {@link SchedulerLock}
     * 
     * @param machine, the target machine holding the virtual machine to be undeployed.
     * @throws AllocationException, it there are some problem updating the physical machine
     */
    @Transactional(readOnly = false, propagation = Propagation.REQUIRED)
    public void deallocateVirtualMachine(final VirtualMachine vmachine)
    {
        try
        {
            if (vmachine.isManaged())
            {
                upgradeUse.rollbackUse(vmachine);
            }
            else
            {
                vmRepo.deleteVirtualMachine(vmachine);
            }

        }
        catch (ResourceUpgradeUseException e)
        {
            APIError error = APIError.NOT_ENOUGH_RESOURCES;
            error.addCause(String.format("%s\n%s", virtualMachineInfo(vmachine), e.getMessage()));
            addConflictErrors(error);
        }
        finally
        {
            flushErrors();
        }
    }

    @Transactional(readOnly = false, propagation = Propagation.REQUIRED)
    public void deallocateVirtualMachineOnSourceMachine(final VirtualMachine vmachine,
        final Machine machine) throws AllocatorException, ResourceAllocationException
    {
        LOG.error("community can't *deallocateHAVirtualMachine*");
    }

    protected String virtualMachineInfo(final VirtualMachine vm)
    {

        return String.format("Virtual Machine id:%d name:%s UUID:%s.", vm.getId(), vm.getName(),
            vm.getUuid());
    }

    /** ##### CHECK LIMITS ###### */
    /**
     * Check the current allowed Enterprise resource utilization is not exceeded. Overloaded method
     * because en case of deploying VM is not necessary check VLAN limits.
     * <p>
     * The checks are performed on the specified order. Enterprise limits are higher than Datacenter
     * limits, and Datacenter higher than VirtualDatacenter (this requirement is satisfied on the
     * Limit creation)
     * 
     * @param vapp, the target virtual appliance.
     * @param required, the required resources.
     * @param force, if false the soft limits can not be exceeded (throws an
     *            SoftLimitExceededException) otherwise only be traced if reached.
     * @throws ResourceAllocationException, (NotEnoughResources) if there aren't any machine to full
     *             fits the requirements.
     * @throws LimitExceededException, it the allowed resources are exceeded.
     *             {@link HardLimitExceededException} when the current virtual machine template
     *             requirements will exceed the total allowed resource reservation,
     *             {@link SoftLimitExceededException} on ''foreceEnterpriseSoftLimits'' = false and
     *             the soft limit is exceeded.
     */
    @Transactional(readOnly = true, propagation = Propagation.REQUIRED)
    public void checkLimist(final VirtualAppliance vapp, final VirtualMachineRequirements required,
        final Boolean force)
    {
        try
        {
            checkLimist(vapp, required, force, false);
        }
        catch (LimitExceededException limite)
        {
            if (limite.isHardLimit())
            {
                addConflictErrors(new CommonError(APIError.LIMIT_EXCEEDED.name(), limite.toString()));
            }
            else
            {
                addConflictErrors(new CommonError(APIError.SOFT_LIMIT_EXCEEDED.name(),
                    limite.toString()));
            }
        }
        catch (Exception e)
        {
            addUnexpectedErrors(new CommonError(APIError.ALLOCATOR_ERROR.name(), e.toString()));
        }
        finally
        {
            flushErrors();
        }
    }

    /**
     * @param vapp
     * @param required
     * @param force
     * @param checkVLAN
     * @throws LimitExceededException
     */
    protected void checkLimist(final VirtualAppliance vapp,
        final VirtualMachineRequirements required, final Boolean force, final Boolean checkVLAN)
        throws LimitExceededException
    {

        checkEnterpirse.checkLimits(vapp.getEnterprise(), required, force, checkVLAN, false);
    }

    /**
     * Gets the {@link FitPolicy} of the current datacenter.
     * 
     * @param idDatacenter, fit policy is based at datacenter level.
     * @return the default ''Global'' Fit Policy (for any Datacenter).
     */
    protected FitPolicy getAllocationFitPolicyOnDatacenter(final Integer idDatacenter)
    {
        return FitPolicy.PROGRESSIVE; // community fix the fit policy
    }

    /**
     * We check how many empty machines are in a rack. Then we power on or off to fit the
     * configuration. In 2.0 only in {@link UcsRack}. This method is not transactional since there
     * is no need to perform rollback. All of the functions that made changes to the database are
     * marked as REQUIRES_NEW.
     * 
     * @param targetMachine machine we are deploy void
     * @since 2.0
     */
    public void adjustPoweredMachinesInRack(final Rack rack)
    {

        if (!(rack instanceof UcsRack))
        {
            LOG.debug("We can only adjust max machines on in UCS");
            return;
        }
        Integer max = ((UcsRack) rack).getMaxMachinesOn();
        if (max == null || max == 0)
        {
            LOG.debug("Max machines on feature is disabled for rack: {}", rack.getId());
            return;
        }

        Integer emptyMachinesOn = this.allocationService.getEmptyOnMachines(rack.getId());
        if (max > emptyMachinesOn)
        {
            LOG.debug("Not enough machines on rack: {} should be {} but there are {}",
                new Object[] {rack.getId(), max, emptyMachinesOn});
            int howMany = max - emptyMachinesOn;
            List<Machine> machines =
                this.allocationService.getRandomMachinesToStartFromRack(rack.getId(), howMany);
            if (machines != null && !machines.isEmpty())
            {
                LOG.debug("Requesting {} machines to boot , retrieved {}", new Object[] {howMany,
                machines.size()});
                powerOnMachine(machines);
                return;
            }
            LOG.debug("There are no machines available to start up on rack: {}", rack.getId());
        }
        else if (max < emptyMachinesOn)
        {
            // If there is more than one machine to power off

            LOG.debug("Too many machines rack: {} should be {} but there are {}", new Object[] {
            rack.getId(), max, emptyMachinesOn});

            int howMany = emptyMachinesOn - max;
            List<Machine> machines = this.allocationService.getAllEmptyOnMachines(rack.getId());
            if (machines != null && !machines.isEmpty())
            {
                LOG.debug("Requesting {} machines to shut , retrieved {}", new Object[] {howMany,
                machines.size()});
                shutDownMachines(machines, howMany);
                return;
            }
            LOG.debug("There are no machines available to shut down on rack: {}", rack.getId());
        }
        else
        {
            LOG.debug("Enough machines rack: {}", rack.getId());
        }
    }

    /**
     * There are special requirements for this method to be ok. Most of those prerequisites can not
     * be satisfied by Abiquo. The machine must be associated with a Logic Server in UCS.
     * 
     * @see com.abiquo.scheduler.Allocator#powerOnMachine(java.util.List)
     */
    protected void powerOnMachine(final List<Machine> machines)
    {
        LOG.debug("Starting {} machines", machines.size());
        for (Machine machine : machines)
        {
            try
            {
                infrastructureService.powerOn(machine.getId());
            }
            catch (Exception e)
            {
                LOG.error(
                    "Could not power on the machine id {} name {} the error: {}: {}",
                    new Object[] {machine.getId(), machine.getName(), e.getClass().getName(),
                    e.getMessage()});
            }
        }
    }

    /**
     * There are special requirements for this method to be ok. Most of those prerequisites can not
     * be satisfied by Abiquo. The machine must be associated with a Logic Server in UCS. Since we
     * do not persist the Logic Server we need to try to shutdown all.
     * 
     * @see com.abiquo.scheduler.Allocator#shutDownMachines(java.util.List)
     */
    protected void shutDownMachines(final List<Machine> machines, int howMany)
    {
        LOG.debug("Stopping {} machines", howMany);
        for (Machine machine : machines)
        {
            // Since we don't know in advance which are associated we must try all of them
            // Even if we precalculate since there is many options of changing the UCS actual status
            // does not guarantees
            if (howMany <= 0)
            {
                LOG.debug(
                    "shutDownMachines UCS not machines need to shutdown, it should be 0 and is {}",
                    howMany);
                return;
            }
            try
            {
                infrastructureService.powerOff(machine.getId(), MachineState.HALTED_FOR_SAVE);
                howMany--;
            }
            catch (ConflictException e)
            {
                LOG.error(
                    "Could not power off the machine id {} name {} the error conflict error: {}: {}",
                    new Object[] {machine.getId(), machine.getName(), e.getErrors(), e.getMessage()});
            }
            catch (Exception e)
            {
                LOG.error(
                    "Could not power off the machine id {} name {} the error: {}: {}",
                    new Object[] {machine.getId(), machine.getName(), e.getClass().getName(),
                    e.getMessage()});
            }
        }
        if (howMany > 0)
        {
            LOG.error(
                "Could not power off all of the required machines, {} left to power off. Either empty or with service profile associated!",
                howMany);
        }
    }
}
