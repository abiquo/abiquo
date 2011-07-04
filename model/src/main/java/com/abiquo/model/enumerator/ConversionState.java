package com.abiquo.model.enumerator;

public enum ConversionState
{
    ENQUEUED, FINISHED, FAILED;

    public int id()
    {
        return ordinal() + 1;
    }

    public static ConversionState fromId(final int id)
    {
        return values()[id - 1];
    }
}
