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

import com.abiquo.scheduler.check.IMachineCheck;
import com.abiquo.scheduler.limit.EnterpriseLimitChecker;
import com.abiquo.scheduler.limit.LimitExceededException;
import com.abiquo.scheduler.limit.VirtualMachineRequirements;
import com.abiquo.scheduler.workload.AllocatorException;
import com.abiquo.scheduler.workload.NotEnoughResourcesException;
import com.abiquo.scheduler.workload.VirtualimageAllocationService;
import com.abiquo.server.core.cloud.VirtualAppliance;
import com.abiquo.server.core.cloud.VirtualApplianceDAO;
import com.abiquo.server.core.cloud.VirtualApplianceRep;
import com.abiquo.server.core.cloud.VirtualImage;
import com.abiquo.server.core.cloud.VirtualMachine;
import com.abiquo.server.core.cloud.VirtualMachineDAO;
import com.abiquo.server.core.cloud.VirtualMachineDto;
import com.abiquo.server.core.infrastructure.DatacenterRep;
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
    DatacenterRep datacenterRepo;

    @Autowired
    VirtualApplianceDAO virtualApplianceDao;

    @Autowired
    VirtualApplianceRep virtualAppRep;

    @Autowired
    NetworkAssignmentDAO networkAssignmentDao;

    // ///////

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

    /** If the check machine fails, how many times the allocator try a new target machine. */
    protected final static Integer RETRIES_AFTER_CHECK = 5;

    public void checkEditVirtualMachineResources(Integer idVirtualApp, Integer virtualMachineId,
        VirtualMachineDto newVmRequirements, boolean foreceEnterpriseSoftLimits)
        throws AllocatorException, ResourceAllocationException
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

    private VirtualMachineRequirements getVirtualMachineRequirements(VirtualMachine vmachine,
        VirtualMachineDto newVmRequirements)
    {
        Integer cpu = newVmRequirements.getCpu() - vmachine.getCpu();
        Integer ram = newVmRequirements.getRam() - vmachine.getRam();

        cpu = cpu > 0 ? cpu : 0;
        ram = ram > 0 ? ram : 0;

        return new VirtualMachineRequirements(cpu.longValue(), ram.longValue(), 0l, 0l, 0l, 0l, 0l);
    }

    private VirtualImage getVirtualImage(VirtualMachineRequirements increaseRequirements)
    {
        VirtualImage vimage = new VirtualImage(null); // doesn't care about the enterprise
        vimage.setCpuRequired(increaseRequirements.getCpu().intValue());
        vimage.setRamRequired(increaseRequirements.getRam().intValue());
        return vimage;
    }

    @Override
    public VirtualMachine allocateVirtualMachine(Integer idVirtualApp, Integer virtualMachineId,
        Boolean foreceEnterpriseSoftLimits) throws AllocatorException, ResourceAllocationException
    {

        VirtualMachine vmachine = virtualMachineDao.findById(virtualMachineId);
        final VirtualAppliance vapp = virtualAppDao.findById(idVirtualApp);

        // VirtualImage vi = vmachine.getVirtualImage();

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

    public VirtualMachine allocateHAVirtualMachine(VirtualMachine vmachine)
        throws AllocatorException, ResourceAllocationException
    {

        if (vmachine.getHypervisor() == null)
        {
            // XXX check also is on HA ???
            throw new ResourceAllocationException(String.format(
                "The virtual machine isn't allocated "
                    + "(do not have original hypervisor, can't be moved)", vmachine.getName()));
        }

        if (vmachine.getDatastore() == null)
        {
            throw new ResourceAllocationException(String.format(
                "The virtual machine isn't allocated "
                    + "(do not have original datastore, can't be moved)", vmachine.getName()));
        }

        final VirtualAppliance vapp = virtualAppRep.findVirtualApplianceByVirtualMachine(vmachine);
        final VirtualImage vimage = getVirtualImageWithVirtualMachineResourceRequirements(vmachine);

        final Integer idEnterprise = vapp.getEnterprise().getId();
        final Integer idVirtualDatacenter = vapp.getVirtualDatacenter().getId();
        final Integer idDatacenter = vapp.getVirtualDatacenter().getDatacenter().getId();

        // HA specific
        final Integer idRack = vmachine.getHypervisor().getMachine().getRack().getId();
        final String datastoreUuid = vmachine.getDatastore().getDatastoreUUID();
        final Integer originalHypervisorId = vmachine.getHypervisor().getId();

        final FitPolicy fitPolicy = getAllocationFitPolicyOnDatacenter(idDatacenter);

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
            targetMachine =
                allocationService.findBestTarget(vimage, fitPolicy, vapp.getId(), datastoreUuid,
                    originalHypervisorId, idRack);

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
                    "Allocator can not select a machine on the current virtual datacenter "
                        + "and rack with the required datastore (HA). "
                        + "Last candidate error : %s.", errorCause != null ? errorCause
                        : "can not be confirmed as MANAGED.");
            throw new NotEnoughResourcesException(cause);
        }

        log.info("Selected physical machine [{}] to perform HA over VirtualMachine [{}]",
            targetMachine.getName(), vmachine.getName());

        return vmachine;
    }

    // This is duet the virtual machine actually carry the virtual image requirements (should be
    // something like VirtualMachineTemplate)
    private VirtualImage getVirtualImageWithVirtualMachineResourceRequirements(
        VirtualMachine vmachine)
    {
        VirtualImage vimage = new VirtualImage(null); // doesn't care about enterprise

        vimage.setCpuRequired(vmachine.getCpu());
        vimage.setRamRequired(vmachine.getRam());

        if (vmachine.getVirtualImage().getStateful() == 0)
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
     * Check the current allowed Enterprise resource utilization is not exceeded.
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

        checkEnterpirse.checkLimits(vapp.getEnterprise(), required, force);

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
    protected VirtualMachineRequirements getVirtualMachineRequirements(VirtualMachine vmachine)
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
    public void setVmFactory(VirtualMachineFactory vmFactory)
    {
        this.vmFactory = vmFactory;
    }

    @Resource(name = "machineCheck")
    public void setMachineChecke(IMachineCheck machineChecker)
    {
        this.machineChecker = machineChecker;
    }
}
