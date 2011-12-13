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

import com.abiquo.server.core.cloud.VirtualMachine;
import com.abiquo.server.core.infrastructure.Machine;
import com.abiquo.server.core.scheduler.VirtualMachineRequirements;

/**
 * Updates the physical machine resource utilization when a new virtual machine is instantiated.
 */
public interface IResourceUpgradeUse
{
    /**
     * Increments the physical machine resource utilization
     * 
     * @param machine, the target physical machine to increment its resource utilization.
     * @param virtualMachine, the new resource requirements (based on its virtual machine template
     *            and additional resource configuration).
     * @param virtualApplianceId, the virtual appliance the virtual machine belongs to.
     * @throws ResourceUpgradeUseException, if the operation can be performed: there isn't enough
     *             resources to allocate the virtual machine, the virtual appliances is not on any
     *             virtual datacenter.
     */
    public void updateUse(Integer virtualApplianceId, VirtualMachine virtualMachine);

    /**
     * Do not update the datastore utilization.
     * 
     * @param sourceMachineId, the machine id of the source (where the HA vmachine were deployed)
     */
    public void updateUseHa(Integer virtualApplianceId, VirtualMachine virtualMachine,
        Integer sourceMachineId);

    /**
     * Decrements the physical machine resource utilization
     * 
     * @param machine, the target physical machine to decrements its resource utilization.
     * @param virtualMachine, the resource requirements to be deallocated (based on its virtual
     *            machine template and additional resource configuration).
     */
    public void rollbackUse(VirtualMachine virtual);

    /**
     * Increase the ram and cpu resources on the provided machine.
     * 
     * @param requirements, the increased resources (now only used for CPU and RAM)
     */
    public void updateUsed(final Machine machine, final VirtualMachineRequirements requirements);

}
