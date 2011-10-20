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

package com.abiquo.scheduler;

import javax.annotation.Resource;
import javax.jms.ResourceAllocationException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.abiquo.api.services.UserService;
import com.abiquo.scheduler.check.IMachineCheck;
import com.abiquo.scheduler.limit.EnterpriseLimitChecker;
import com.abiquo.scheduler.limit.LimitExceededException;
import com.abiquo.scheduler.limit.VirtualMachineRequirements;
import com.abiquo.scheduler.workload.AllocatorException;
import com.abiquo.scheduler.workload.NotEnoughResourcesException;
import com.abiquo.scheduler.workload.VirtualimageAllocationService;
import com.abiquo.server.core.cloud.State;
import com.abiquo.server.core.cloud.VirtualAppliance;
import com.abiquo.server.core.cloud.VirtualApplianceDAO;
import com.abiquo.server.core.cloud.VirtualApplianceRep;
import com.abiquo.server.core.cloud.VirtualImage;
import com.abiquo.server.core.cloud.VirtualMachine;
import com.abiquo.server.core.cloud.VirtualMachineDAO;
import com.abiquo.server.core.cloud.VirtualMachineDto;
import com.abiquo.server.core.infrastructure.InfrastructureRep;
import com.abiquo.server.core.infrastructure.Machine;
import com.abiquo.server.core.infrastructure.management.RasdManagementDAO;
import com.abiquo.server.core.infrastructure.network.NetworkAssignmentDAO;
import com.abiquo.server.core.scheduler.FitPolicyRule.FitPolicy;
import com.abiquo.server.core.scheduler.FitPolicyRuleDAO;

/**
 * @author apuig
 */
@Service
@Transactional(readOnly = false, propagation = Propagation.REQUIRED)
public class Allocator implements IAllocator
{
    protected final static Logger log = LoggerFactory.getLogger(Allocator.class);

    @Autowired
    VirtualApplianceDAO virtualAppDao;

    @Autowired
    VirtualMachineDAO virtualMachineDao;

    @Autowired
    RasdManagementDAO rasdManDao;

    @Autowired
    FitPolicyRuleDAO fitPolicyDao;

    @Autowired
    InfrastructureRep datacenterRepo;

    @Autowired
    VirtualApplianceDAO virtualApplianceDao;

    @Autowired
    VirtualApplianceRep virtualAppRep;

    @Autowired
    NetworkAssignmentDAO networkAssignmentDao;

    @Autowired
    VirtualimageAllocationService allocationService;

    // Autowired in setter in order to set premium implementation
    // @Autowired
    VirtualMachineFactory vmFactory;

    // Autowired in setter in order to set premium implementation
    // @Autowired
    IMachineCheck machineChecker;

    /** All the entities to check its limits. Premium adds */
    @Autowired
    EnterpriseLimitChecker checkEnterpirse;

    /** Only used on the HA reallocate. */
    @Autowired
    ResourceUpgradeUse upgradeUse;

    @Autowired
    UserService userService;

    /** If the check machine fails, how many times the allocator try a new target machine. */
    protected final static Integer RETRIES_AFTER_CHECK = 5;

    @Override
    public void checkEditVirtualMachineResources(final Integer idVirtualApp,
        final Integer virtualMachineId, final VirtualMachineDto newVmRequirements,
        final boolean foreceEnterpriseSoftLimits) throws AllocatorException
    {

        final VirtualMachine vmachine = virtualMachineDao.findById(virtualMachineId);
        final VirtualAppliance vapp = virtualAppDao.findById(idVirtualApp);
        final Machine machine = vmachine.getHypervisor().getMachine();

        final VirtualMachineRequirements increaseRequirements =
            getVirtualMachineRequirements(vmachine, newVmRequirements);

        checkLimist(vapp, increaseRequirements, foreceEnterpriseSoftLimits);

        final VirtualImage increaseVirtualImage = getVirtualImage(increaseRequirements);

        boolean check =
            allocationService.checkVirtualMachineResourceIncrease(machine, increaseVirtualImage,
                idVirtualApp);

        if (!check)
        {
            final String cause =
                String.format("Current workload rules (RAM and CPU oversubscription) "
                    + "on the target machine: %s disallow the required resources increment.",
                    machine.getName());
            throw new AllocatorException(cause);
        }
    }

    private VirtualMachineRequirements getVirtualMachineRequirements(final VirtualMachine vmachine,
        final VirtualMachineDto newVmRequirements)
    {
        Integer cpu = newVmRequirements.getCpu() - vmachine.getCpu();
        Integer ram = newVmRequirements.getRam() - vmachine.getRam();

        cpu = cpu > 0 ? cpu : 0;
        ram = ram > 0 ? ram : 0;

        return new VirtualMachineRequirements(cpu.longValue(), ram.longValue(), 0l, 0l, 0l, 0l, 0l);
    }

    private VirtualImage getVirtualImage(final VirtualMachineRequirements increaseRequirements)
    {
        // We only need the CPU and RAM requirement fields
        VirtualImage vimage = new VirtualImage(null, null, null, null, 0L, null);
        vimage.setCpuRequired(increaseRequirements.getCpu().intValue());
        vimage.setRamRequired(increaseRequirements.getRam().intValue());
        return vimage;
    }

    @Override
    public VirtualMachine allocateVirtualMachine(final Integer idVirtualApp,
        final Integer virtualMachineId, final Boolean foreceEnterpriseSoftLimits)
        throws AllocatorException
    {

        VirtualMachine vmachine = virtualMachineDao.findById(virtualMachineId);
        final VirtualAppliance vapp = virtualAppDao.findById(idVirtualApp);
        userService.checkCurrentEnterpriseForPostMethods(vapp.getEnterprise());

        final VirtualImage vimage = getVirtualImageWithVirtualMachineResourceRequirements(vmachine);

        // CREATES THE VIRTUAL MACHINE REQUIREMENTS
        final VirtualMachineRequirements requirements = getVirtualMachineRequirements(vmachine);

        final Integer idEnterprise = vapp.getEnterprise().getId();
        final Integer idVirtualDatacenter = vapp.getVirtualDatacenter().getId();
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

            // BEST MACHINE
            targetMachine = allocationService.findBestTarget(vimage, fitPolicy, vapp.getId());

            // BEST REAL MACHINE
            if (checkMachine(targetMachine, idVirtualDatacenter, idDatacenter, idEnterprise))
            {
                // CREATE THE VIRTUAL MACHINE
                try
                {
                    vmachine = vmFactory.createVirtualMachine(targetMachine, vmachine);

                    // refresh vmachine with the information added on the VirtualMachineFactory
                    virtualMachineDao.flush();
                }
                catch (final NotEnoughResourcesException e)
                {
                    log.error("Discarded machine [{}] : Not Enough Resources [{}]",
                        targetMachine.getName(), e);

                    errorCause =
                        String.format("Machine : %s error: %s", targetMachine.getName(),
                            e.getMessage());
                    targetMachine = null;
                }
            }
            else
            {
                log.error("Machine [{}] is not MANAGED", targetMachine.getName());
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

        log.info("Selected physical machine [{}] to instantiate VirtualMachine [{}]",
            targetMachine.getName(), vmachine.getName());

        return vmachine;
    }

    @Override
    public VirtualMachine allocateHAVirtualMachine(final Integer vmId, final State state)
        throws AllocatorException, ResourceAllocationException
    {
        log.error("Community doesn't implement HA");
        return null;
    }

    // This is duet the virtual machine actually carry the virtual image requirements (should be
    // something like VirtualMachineTemplate)
    protected VirtualImage getVirtualImageWithVirtualMachineResourceRequirements(
        final VirtualMachine vmachine)
    {
        // We only need the CPU and RAM requirement fields
        VirtualImage vimage = new VirtualImage(null, null, null, null, 0L, null);
        vimage.setCpuRequired(vmachine.getCpu());
        vimage.setRamRequired(vmachine.getRam());

        if (!vmachine.getVirtualImage().isStateful())
        {
            vimage.setHdRequiredInBytes(vmachine.getHdInBytes());
        }
        else
        // stateful virtual images doesn't use the datastores
        {
            vimage.setHdRequiredInBytes(0l);
        }

        return vimage;
    }

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
     *             {@link HardLimitExceededException} when the current virtual image requirements
     *             will exceed the total allowed resource reservation,
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

    /**
     * Creates the machine requirements implementation.
     * 
     * @param vimage, CPU, RAM, HD required by the virtual machine (and virtual image disk file size
     *            for the datacenter repository)
     * @param resources, additional resources configuration
     */
    protected VirtualMachineRequirements getVirtualMachineRequirements(final VirtualMachine vmachine)
    {
        return new VirtualMachineRequirements(vmachine);
    }

    /**
     * Determine if the target machine can be used
     * 
     * @return true if the machine is MANAGED by abiquo infrastructure.
     */
    protected boolean checkMachine(final Machine machine, final Integer idVirtualDatacenter,
        final Integer idDatacenter, final Integer idEnterprise)
    {
        return machineChecker.check(machine);
    }

    /*
     * IoC Community implementation
     */

    @Resource(name = "virtualMachineFactory")
    public void setVmFactory(final VirtualMachineFactory vmFactory)
    {
        this.vmFactory = vmFactory;
    }

    @Resource(name = "machineCheck")
    public void setMachineChecke(final IMachineCheck machineChecker)
    {
        this.machineChecker = machineChecker;
    }
}
