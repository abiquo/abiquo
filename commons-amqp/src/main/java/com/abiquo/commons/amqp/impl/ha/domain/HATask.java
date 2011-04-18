package com.abiquo.commons.amqp.impl.ha.domain;

import com.abiquo.commons.amqp.domain.Queuable;
import com.abiquo.commons.amqp.util.JSONUtils;

public class HATask implements Queuable
{
    @Override
    public byte[] toByteArray()
    {
        return JSONUtils.serialize(this);
    }

    public static HATask fromByteArray(final byte[] bytes)
    {
        return JSONUtils.deserialize(bytes, HATask.class);
    }
}
