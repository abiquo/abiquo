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
 * The {@link VirtualMachine} State model. The transitions between states are in
 * {@link VirtualMachineStateTransition}.
 * 
 * @author ssedano
 * @see com.abiquo.server.core.cloud.VirtualMachineStateTransition
 */
public enum VirtualMachineState
{

    /**
     * The virtual machine only exists in Abiquo and has not yet a physical machine assigned.
     */
    NOT_ALLOCATED,
    /**
     * The virtual machine does not exists in the hypervisor but has physical machine assigned.
     */
    ALLOCATED,
    /**
     * The virtual machine exists in the hypervisor.
     */
    CONFIGURED,
    /**
     * The virtual machine exists in the hypervisor and is ON.
     */
    ON,
    /**
     * The virtual machine exists in the hypervisor and is SUSPENDED.
     */
    PAUSED,
    /**
     * The virtual machine exists in the hypervisor and is OFF.
     */
    OFF,
    /**
     * Some operation is being performed on the virtual machine.
     */
    LOCKED,
    /**
     * Abiquo does know the actual state of the virtual machine. But it exists in the hypervisor.
     */
    UNKNOWN;

    public static VirtualMachineState fromValue(final String value)
    {
        return VirtualMachineState.valueOf(value.toUpperCase());
    }

    public VirtualMachineState travel(final VirtualMachineStateTransition transaction)
    {
        if (!transaction.isValidOrigin(this))
        {
            throw new RuntimeException("Invalid origin " + this + " for transaction " + transaction);
        }

        return transaction.getEndState();
    }

    public int id()
    {
        return ordinal() + 1;
    }

    public static VirtualMachineState fromId(final int id)
    {
        return VirtualMachineState.values()[id - 1];
    }

    public String toOVF()
    {
        switch (this)
        {
            case ON:
                return "POWERUP_ACTION";
            case PAUSED:
                return "PAUSE_ACTION";
            case OFF:
                return "POWERDOWN_ACTION";
        }
        return null;
    }

    public String toResourceState()
    {
        switch (this)
        {
            case ON:
                return "PowerUp";
            case PAUSED:
                return "Pause";
            case OFF:
                return "PowerOff";
                // case REBOOTED:
                // return "Resume";
        }
        return null;
    }

    @Override
    public String toString()
    {
        return "";
    }

    public boolean isDeployed()
    {
        switch (this)
        {
            case ON:
            case OFF:
            case PAUSED:
                return true;
                // Configured state. The VM is in the hypervisor but it
                // has never been powered on. The Chef agent has not run
                // yet and the node does not exist in the Chef server.
            case CONFIGURED:
            default:
                return false;
        }
    }
}
