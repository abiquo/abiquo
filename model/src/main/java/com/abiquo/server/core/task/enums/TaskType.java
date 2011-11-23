package com.abiquo.server.core.task.enums;

import static com.abiquo.server.core.task.enums.TaskOwnerType.VIRTUAL_MACHINE;

public enum TaskType
{
    DEPLOY(VIRTUAL_MACHINE),

    UNDEPLOY(VIRTUAL_MACHINE),

    RECONFIGURE(VIRTUAL_MACHINE),

    POWER_ON(VIRTUAL_MACHINE),

    POWER_OFF(VIRTUAL_MACHINE),

    PAUSE(VIRTUAL_MACHINE),

    RESUME(VIRTUAL_MACHINE),

    RESET(VIRTUAL_MACHINE),

    SNAPSHOT(VIRTUAL_MACHINE),

    HIGH_AVAILABILITY(VIRTUAL_MACHINE);

    protected TaskOwnerType ownerType;

    private TaskType(final TaskOwnerType ownerType)
    {
        this.ownerType = ownerType;
    }
}
