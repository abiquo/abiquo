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

import static com.abiquo.server.core.cloud.VirtualMachineState.ALLOCATED;
import static com.abiquo.server.core.cloud.VirtualMachineState.CONFIGURED;
import static com.abiquo.server.core.cloud.VirtualMachineState.NOT_ALLOCATED;
import static com.abiquo.server.core.cloud.VirtualMachineState.OFF;
import static com.abiquo.server.core.cloud.VirtualMachineState.ON;
import static com.abiquo.server.core.cloud.VirtualMachineState.PAUSED;
import static java.util.Collections.singleton;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

/**
 * Valid transactions between {@link VirtualMachineState}.
 */
public enum VirtualMachineStateTransition
{
    // Configure transition
    CONFIGURE(singleton(ALLOCATED), CONFIGURED),

    // Reconfigure transition
    RECONFIGURE(singleton(OFF), CONFIGURED),

    // Deconfigure transition
    DECONFIGURE(singleton(CONFIGURED), ALLOCATED),

    // PowerOn transition
    POWERON(new HashSet<VirtualMachineState>(Arrays.asList(CONFIGURED, OFF)), ON),

    // PowerOff transition
    POWEROFF(singleton(ON), OFF),

    // Reset transition
    RESET(singleton(ON), ON),

    // Pause transition
    PAUSE(singleton(ON), PAUSED),

    // Resume transition
    RESUME(singleton(PAUSED), ON),

    // Snapshot transition
    SNAPSHOT(singleton(OFF), OFF),

    // Not allocated yet
    ALLOCATE(singleton(NOT_ALLOCATED), ALLOCATED),

    // Exists the machine in Abiquo, and has hypervisor assigned, but does not exists in hypervisor
    DEALLOCATE(singleton(ALLOCATED), NOT_ALLOCATED);

    private Set<VirtualMachineState> origins;

    private VirtualMachineState end;

    private VirtualMachineStateTransition(final Set<VirtualMachineState> origins, final VirtualMachineState end)
    {
        this.origins = origins;
        this.end = end;
    }

    public VirtualMachineState getEndState()
    {
        return this.end;
    }

    public boolean isValidOrigin(final VirtualMachineState origin)
    {
        return this.origins.contains(origin);
    }

    public static VirtualMachineStateTransition fromValue(final String value)
    {
        return VirtualMachineStateTransition.valueOf(value.toUpperCase());
    }

    public static VirtualMachineStateTransition rollback(final VirtualMachineStateTransition s)
    {
        switch (s)
        {
            case CONFIGURE:
                return DECONFIGURE;

            case DECONFIGURE:
                return CONFIGURE;

            case POWEROFF:
                return POWERON;
            case POWERON:
                return POWEROFF;

            case PAUSE:
                return RESUME;

            case RESUME:
                return PAUSE;

            default: // reset reconfigure snapshot
                return s;
        }
    }

    public static boolean isValidTransition(final VirtualMachineState origin, final VirtualMachineState end)
    {
        for (VirtualMachineStateTransition l : VirtualMachineStateTransition.values())
        {
            if (l.isValidOrigin(origin) && l.getEndState().equals(end))
            {
                return true;
            }
        }
        return false;
    }

    public static VirtualMachineStateTransition getValidTransition(final VirtualMachineState origin, final VirtualMachineState end)
    {
        for (VirtualMachineStateTransition l : VirtualMachineStateTransition.values())
        {
            if (l.isValidOrigin(origin) && l.getEndState().equals(end))
            {
                return l;
            }
        }
        return null;
    }
}
