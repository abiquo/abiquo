package com.abiquo.server.core.enterprise;

import com.abiquo.server.core.cloud.State;

public enum ApprovalState
{
    PENDING, APPROVED, DENIED;

    public int id()
    {
        return ordinal() + 1;
    }

    public static State fromId(final int id)
    {
        return State.values()[id - 1];
    }
}
