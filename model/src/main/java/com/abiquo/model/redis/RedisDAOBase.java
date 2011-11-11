package com.abiquo.model.redis;

import redis.clients.jedis.Transaction;

public abstract class RedisDAOBase<T extends RedisEntityBase>
{
    public abstract void delete(T entity, Transaction transaction);

    public abstract void save(T entity, Transaction transaction);
}
