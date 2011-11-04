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

package com.abiquo.api.eventing;

import java.util.UUID;

import com.abiquo.server.core.cloud.VirtualMachineState;
import com.abiquo.vsm.events.VMEventType;

public class VirtualMachineStage
{
    protected VirtualMachineState state;

    protected VirtualMachineState expected;

    protected String event;

    protected String name = UUID.randomUUID().toString();

    public static VirtualMachineStage createVirtualMachineStage()
    {
        return new VirtualMachineStage();
    }

    public VirtualMachineStage in(VirtualMachineState state)
    {
        this.state = state;
        return this;
    }

    public VirtualMachineStage expecting(VirtualMachineState state)
    {
        this.expected = state;
        return this;
    }

    public VirtualMachineStage onEvent(VMEventType event)
    {
        this.event = event.name();
        return this;
    }

    public VirtualMachineStage onEvent(String event)
    {
        this.event = event;
        return this;
    }

    public VirtualMachineState getState()
    {
        return state;
    }

    public VirtualMachineState getExpected()
    {
        return expected;
    }

    public String getEvent()
    {
        return event;
    }

    public String getName()
    {
        return name;
    }
}
