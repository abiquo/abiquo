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

import javax.jms.ResourceAllocationException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.abiquo.api.exceptions.APIError;
import com.abiquo.model.enumerator.FitPolicy;
import com.abiquo.model.transport.error.CommonError;
import com.abiquo.scheduler.ResourceUpgradeUse;
import com.abiquo.scheduler.ResourceUpgradeUseException;
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
import com.abiquo.server.core.cloud.VirtualMachineState;
import com.abiquo.server.core.infrastructure.Machine;
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
 * <p>
 * TODO transactional required. read-only
 * 
 * @author apuig
 */
@Service
@Transactional(readOnly = false, propagation = Propagation.REQUIRED)
// TODO method level
public class VirtualMachineAllocatorService extends DefaultApiService
{
    protected final static Logger LOG = LoggerFactory
        .getLogger(VirtualMachineAllocatorService.class);

    /** If the check machine fails, how many times the allocator try a new target machine. */
    protected final static Integer RETRIES_AFTER_CHECK = 5;

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

    public VirtualMachineAllocatorService()
    {

    }

    public VirtualMachineAllocatorService(final EntityManager em)
    {
        this.virtualAppDao = new VirtualApplianceDAO(em);
        this.allocationService = new VirtualimageAllocationService();
        this.vmFactory = new VirtualMachineFactory(em);
        this.machineChecker = new MachineCheck();
        this.virtualMachineDao = new VirtualMachineDAO(em);
        this.checkEnterpirse = new EnterpriseLimitChecker(em);
        this.upgradeUse = new ResourceUpgradeUse(em);
        this.vmRequirements = new VirtualMachineRequirementsFactory();

    }


    /**
     * Check if we can allocate the new virtual machine according to the new values.
     * 
     * @param idVirtualApp
     * @param vmachine
     * @param newvmachine
     * @param foreceEnterpriseSoftLimits
     */
    public void checkAllocate(final Integer idVirtualApp, final Integer virtualMachineId,
        final VirtualMachineRequirements increaseRequirements,
        final boolean foreceEnterpriseSoftLimits)
    {

        final VirtualMachine vmachine = virtualMachineDao.findById(virtualMachineId);
        final VirtualAppliance vapp = virtualAppDao.findById(idVirtualApp);
        final Machine machine = vmachine.getHypervisor().getMachine();

        if (vmachine.getHypervisor() == null || vmachine.getHypervisor().getMachine() == null)
        {
            addConflictErrors(APIError.CHECK_EDIT_NO_TARGET_MACHINE);
            flushErrors();
        }

        try
        {
            checkLimist(vapp, increaseRequirements, foreceEnterpriseSoftLimits);

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

            upgradeUse.updateUsed(vmachine.getHypervisor().getMachine(), increaseRequirements);

        }
        catch (NotEnoughResourcesException e)
        {
            addConflictErrors(createErrorWithExceptionDetails(APIError.NOT_ENOUGH_RESOURCES,
                vmachine.getId(), e));
        }
        catch (LimitExceededException limite)
        {
            addConflictErrors(new CommonError(APIError.LIMIT_EXCEEDED.name(), limite.toString()));
        }
        catch (AllocatorException e)
        {
            addConflictErrors(createErrorWithExceptionDetails(APIError.ALLOCATOR_ERROR,
                vmachine.getId(), e));
        }
        catch (Exception e)
        {
            addUnexpectedErrors(createErrorWithExceptionDetails(APIError.ALLOCATOR_ERROR,
                vmachine.getId(), e));
        }
        finally
        {
            flushErrors();
        }
    }

    /**
     * Creates a virtual machine using some hypervisor on the current virtual appliance datacenter.
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
    public VirtualMachine allocateVirtualMachine(final Integer virtualMachineId,
        final Integer idVirtualApp, final Boolean foreceEnterpriseSoftLimits)
    {

        VirtualMachine allocatedVirtualMachine = null;
        final VirtualMachine vmachine = virtualMachineDao.findById(virtualMachineId);
        final VirtualMachineRequirements requirements =
            vmRequirements.createVirtualMachineRequirements(vmachine);
        final VirtualAppliance vapp = virtualAppDao.findById(idVirtualApp);

        try
        {
            final Integer idDatacenter = vapp.getVirtualDatacenter().getDatacenter().getId();
            final FitPolicy fitPolicy = getAllocationFitPolicyOnDatacenter(idDatacenter);

            /*
             * ENTERPRISE LIMIT CHECK
             */
            checkLimist(vapp, requirements, foreceEnterpriseSoftLimits);

            /*
             * PHYSICAL MACHINE ALLOCATION
             */
            Machine targetMachine = null;

            int retry = 0;
            String errorCause = null;
            while (targetMachine == null && retry < RETRIES_AFTER_CHECK)
            {
                retry++;

                allocatedVirtualMachine =
                    selectPhysicalMachineAndAllocateResources(vmachine, idVirtualApp, fitPolicy,
                        requirements);
                targetMachine = allocatedVirtualMachine.getHypervisor().getMachine();

                // BEST REAL MACHINE
                if (!machineChecker.check(targetMachine))
                {
                    upgradeUse.rollbackUse(allocatedVirtualMachine);

                    LOG.error("Machine [{}] is not MANAGED", targetMachine.getName());
                    errorCause =
                        String.format("Machine : %s error: %s", targetMachine.getName(),
                            "is not MANAGED");
                    targetMachine = null;

                }
            }// retry until check

            // SOME CANDIDATE ?
            if (targetMachine == null)
            {
                final String cause =
                    String.format(
                        "Allocator can not select a machine on the current virtual datacenter. "
                            + "Last candidate error : %s.", errorCause != null ? errorCause
                            : "can not be confirmed as MANAGED.");
                throw new NotEnoughResourcesException(cause);
            }

            return allocatedVirtualMachine;
        }
        catch (NotEnoughResourcesException e)
        {
            addConflictErrors(createErrorWithExceptionDetails(APIError.NOT_ENOUGH_RESOURCES,
                virtualMachineId, e));
        }
        catch (LimitExceededException limite)
        {
            addConflictErrors(new CommonError(APIError.LIMIT_EXCEEDED.name(), limite.toString()));
        }
        catch (AllocatorException e)
        {
            addConflictErrors(createErrorWithExceptionDetails(APIError.ALLOCATOR_ERROR,
                virtualMachineId, e));
        }
        catch (Exception e)
        {
            addUnexpectedErrors(createErrorWithExceptionDetails(APIError.ALLOCATOR_ERROR,
                virtualMachineId, e));
        }
        finally
        {
            flushErrors();
        }

        return vmachine; // unreachable code
    }

    @Transactional(readOnly = false, propagation = Propagation.REQUIRES_NEW)
    private VirtualMachine selectPhysicalMachineAndAllocateResources(final VirtualMachine vmachine,
        final Integer vappId, final FitPolicy fitPolicy,
        final VirtualMachineRequirements requirements)
    {

        Machine targetMachine = allocationService.findBestTarget(requirements, fitPolicy, vappId);

        LOG.info("Attemp to use physical machine [{}] to allocate VirtualMachine [{}]",
            targetMachine.getName(), vmachine.getName());

        // CREATE THE VIRTUAL MACHINE
        VirtualMachine allocatedVirtualMachine =
            vmFactory.createVirtualMachine(targetMachine, vmachine);

        // refresh vmachine with the information added on the VirtualMachineFactory
        // virtualMachineDao.flush(); // FIXME

        try
        {
            // UPGRADE PHYSICAL MACHINE USE
            upgradeUse.updateUse(vappId, allocatedVirtualMachine);
        }
        catch (ResourceUpgradeUseException e) // TODO with this error no other machine candidate
        {
            APIError error = APIError.NOT_ENOUGH_RESOURCES;
            error.addCause(String.format("%s\n%s", virtualMachineInfo(vmachine.getId()),
                e.getMessage()));
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
    public VirtualMachine allocateHAVirtualMachine(final Integer vmId,
        final VirtualMachineRequirements requirements, final VirtualMachineState targetState)
        throws AllocatorException, ResourceAllocationException
    {
        LOG.error("community can't *allocateHAVirtualMachine*");
        return null;
    }

    private CommonError createErrorWithExceptionDetails(final APIError apiError,
        final Integer virtualMachineId, final Exception e)
    {
        final String msg =
            String.format("%s (%s)\n%s", apiError.getMessage(),
                virtualMachineInfo(virtualMachineId), e.getMessage());

        return new CommonError(apiError.getCode(), msg);
    }

    /**
     * Roll back the changes on the target physical machine after the virtual machine is destroyed
     * (of excluded by some exception on the virtual machine creation on the hypervisor).
     * 
     * @param machine, the target machine holding the virtual machine to be undeployed.
     * @throws AllocationException, it there are some problem updating the physical machine
     */
    public void deallocateVirtualMachine(final Integer idVirtualMachine)
    {
        VirtualMachine vmachine = virtualMachineDao.findById(idVirtualMachine);

        try
        {
            upgradeUse.rollbackUse(vmachine);
        }
        catch (ResourceUpgradeUseException e)
        {
            APIError error = APIError.NOT_ENOUGH_RESOURCES;
            error.addCause(String.format("%s\n%s", virtualMachineInfo(idVirtualMachine),
                e.getMessage()));
            addConflictErrors(error);
        }
        finally
        {
            flushErrors();
        }
    }

    private String virtualMachineInfo(final Integer vmid)
    {
        VirtualMachine vm = virtualMachineDao.findById(vmid);

        return String.format("Virtual Machine id:%d name:%s UUID:%s.", vm.getId(), vm.getName(),
            vm.getUuid());
    }

    /** ##### CHECK LIMITS ###### */
    /**
     * Check the current allowed Enterprise resource utilization is not exceeded. Overloaded method
     * because en case of deploying VM is not necessary check VLAN limits.
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
    protected void checkLimist(final VirtualAppliance vapp,
        final VirtualMachineRequirements required, final Boolean force)
        throws LimitExceededException
    {

        checkLimist(vapp, required, force, true);

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

}
