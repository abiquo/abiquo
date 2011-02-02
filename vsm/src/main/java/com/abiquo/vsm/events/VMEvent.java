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

package com.abiquo.vsm.events;

/**
 * Event propagated by the monitor.
 */
public class VMEvent
{
    /** The type of the event. */
    public VMEventType type;

    /** The physical machine where the event was generated. */
    public String physicalMachineAddress;

    /** The virtual machine that fired the event. */
    public String virtualMachineName;

    /**
     * Creates an new <code>VMEvent</code>.
     * 
     * @param type The type of the event.
     * @param physicalMachineAddress The address of the physical machine where the event was fired.
     * @param virtualMachineName The name of the virtual machine that generated the event.
     */
    public VMEvent(VMEventType type, String physicalMachineAddress, String virtualMachineName)
    {
        super();
        this.type = type;
        this.physicalMachineAddress = physicalMachineAddress;
        this.virtualMachineName = virtualMachineName;
    }

    public VMEventType getType()
    {
        return type;
    }

    public String getPhysicalMachineAddress()
    {
        return physicalMachineAddress;
    }

    public String getVirtualMachineName()
    {
        return virtualMachineName;
    }

    @Override
    public String toString()
    {
        return String.format("[%s, vm: %s, pm: %s]", type.name(), virtualMachineName,
            physicalMachineAddress);
    }

}
