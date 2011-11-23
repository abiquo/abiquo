package com.abiquo.server.core.task.enums;

public enum TaskOwnerType
{
    VIRTUAL_MACHINE("VirtualMachine");

    protected String name;

    private TaskOwnerType(String name)
    {
        this.name = name;
    }
}
