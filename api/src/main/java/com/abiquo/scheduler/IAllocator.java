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

import javax.jms.ResourceAllocationException;

import com.abiquo.model.enumerator.VirtualMachineState;
import com.abiquo.scheduler.limit.VirtualMachineRequirements;
import com.abiquo.scheduler.workload.AllocatorException;
import com.abiquo.server.core.cloud.VirtualMachine;
import com.abiquo.server.core.cloud.VirtualMachineDto;

/**
 * Selects the target machine to allocate a virtual machines.
 * <p>
 * Virtual machine requirements are defined on the virtual image and additional storage or network
 * configurations.
 * <p>
 * Before select the machine check if the current allowd limits are exceeded.
 * <p>
 * Enterprise edition support the definition of affinity, exclusion and work load rules.
 * <p>
 * TODO transactional required. read-only
 * 
 * @author apuig
 */
public interface IAllocator
{

    /**
     * Creates a virtual machine using some hypervisor on the current virtual appliance datacenter.
     * 
     * @param targetImage, target image to deploy (virtual machine template). Determine basic
     *            resource utilization (CPU, RAM, HD) (additionally repository utilization)
     * @param resources, additional resources configurations to be added on the virtual machine
     * @param user, the user performing the virtual machine creation.
     * @param virtualAppId, the virtual appliance id requiring this virtual machine (contains
     *            information about the target {@link VirtualDataCenterHB}).
     * @param foreceEnterpriseSoftLimits, indicating if the virtual appliance should be started even
     *            when the soft limit is exceeded. if false and the soft limit is reached a
     *            {@link SoftLimitExceededException} is thrown, otherwise generate a EVENT TRACE.
     * @return a Virtual Machine based on the provided virtual image on some (best) machine.
     * @throws AllocatorException, direct subclasses {@link HardLimitExceededException} when the
     *             current virtual image requirements will exceed the total allowed resource
     *             reservation, {@link SoftLimitExceededException} on ''foreceEnterpriseSoftLimits''
     *             = false and the soft limit is exceeded.
     * @throws ResourceAllocationException, if none of the machines on the datacenter can be used to
     *             perform this operation.
     */
    // VirtualMachine allocateVirtualMachine(VirtualImage vimage, Collection<RasdManagement>
    // resources,
    // User user, Integer idVirtualApp, Boolean foreceEnterpriseSoftLimits)
    // throws AllocatorException, ResourceAllocationException;

    // TODO get idVirtualApp from vmachine (using nodevirtualimage)
    VirtualMachine allocateVirtualMachine(Integer idVirtualApp, Integer vmachineId,
        Boolean foreceEnterpriseSoftLimits) throws AllocatorException, ResourceAllocationException;

    /**
     * <p>
     * .DO NOT perform any resource limitation check (Enterprise, VDC or DC). As the original
     * virtual machine running on the original hypervisor will be deallocated one the hypervisor can
     * be reached.
     * 
     * @param, vmachineId, an already allocated virtual machine (hypervisor and datastore are set)
     *         but we wants to move it.
     */
    VirtualMachine allocateHAVirtualMachine(Integer vmachineId, VirtualMachineState targetState)
        throws AllocatorException, ResourceAllocationException;

    /**
     * Roll back the changes on the target physical machine after the virtual machine is destroyed
     * (of excluded by some exception on the virtual machine creation on the hypervisor).
     * 
     * @param machine, the target machine holding the virtual machine to be undeployed.
     * @param image, the virtual image requirements to be deallocated.
     * @throws AllocationException, it there are some problem updating the physical machine
     */
    // Void deallocateVirtualMachine(VirtualMachine vmachien) throws AllocatorException;

    /**
     * if resources are increased check the limits and the workload rules applied to the already
     * selected machine. PRE: the virtualMachine is already deployed to some target hypervisor.
     */
    void checkEditVirtualMachineResources(Integer idVirtualApp, Integer virtualMachineId,
        VirtualMachineDto newVmRequirements, boolean foreceEnterpriseSoftLimits)
        throws AllocatorException, ResourceAllocationException;

    VirtualMachineRequirements getVirtualMachineRequirements(VirtualMachine virtualMachine);

}
