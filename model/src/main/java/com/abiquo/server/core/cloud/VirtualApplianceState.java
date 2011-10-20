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

package com.abiquo.server.core.cloud;

/**
 * This class represents all of the possible states of a {@link VirtualAppliance}, which is derived
 * form the states of its {@link VirtualMachine}.
 * 
 * @author ssedano
 * @see com.abiquo.server.core.cloud.VirtualMachineState
 */
public enum VirtualApplianceState
{
    /**
     * All the virtual machines exists in the hypervisor and none is in the state
     * {@link com.abiquo.server.core.cloud.VirtualMachineState#UNKNOWN} nor
     * {@link com.abiquo.server.core.cloud.VirtualMachineState#LOCKED}.
     */
    DEPLOYED,
    /**
     * No virtual machines. Or None of the virtual machines exists in the hypervisor and none is in
     * the state {@link com.abiquo.server.core.cloud.VirtualMachineState#UNKNOWN} nor
     * {@link com.abiquo.server.core.cloud.VirtualMachineState#LOCKED}.
     */
    NOT_DEPLOYED,
    /**
     * Some virtual machines exists in the hypervisor and some does not and none is in the state
     * {@link com.abiquo.server.core.cloud.VirtualMachineState#UNKNOWN} nor
     * {@link com.abiquo.server.core.cloud.VirtualMachineState#LOCKED}.
     */
    NEEDS_SYNCHRONIZE,
    /**
     * Any of its virtual machines is in state
     * {@link com.abiquo.server.core.cloud.VirtualMachineState#LOCKED} and none is in the state
     * {@link com.abiquo.server.core.cloud.VirtualMachineState#UNKNOWN}
     */
    LOCKED,
    /**
     * Any of its virtual machines is in state
     * {@link com.abiquo.server.core.cloud.VirtualMachineState#UNKNOWN}.
     */
    UNKNOWN
}
