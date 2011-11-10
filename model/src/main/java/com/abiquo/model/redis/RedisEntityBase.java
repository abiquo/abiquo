package com.abiquo.model.redis;

public abstract class RedisEntityBase
{
    protected abstract String getIdAsString();

    public String getEntityKey()
    {
        return RedisEntityUtils.getEntityKey(this.getClass(), this.getIdAsString());
    }
}
