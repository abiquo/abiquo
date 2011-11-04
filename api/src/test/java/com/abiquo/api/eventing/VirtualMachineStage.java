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
