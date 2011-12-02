package com.abiquo.api.common;

import org.apache.commons.pool.impl.GenericObjectPool.Config;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

public class JedisPoolForTesting extends JedisPool
{
    public final int DB_TEST_NUMBER = 1;

    public JedisPoolForTesting(Config poolConfig, String host, int port, int timeout,
        String password)
    {
        super(poolConfig, host, port, timeout, password);
    }

    public JedisPoolForTesting(Config poolConfig, String host, int port, int timeout)
    {
        super(poolConfig, host, port, timeout);
    }

    public JedisPoolForTesting(Config poolConfig, String host, int port)
    {
        super(poolConfig, host, port);
    }

    public JedisPoolForTesting(Config poolConfig, String host)
    {
        super(poolConfig, host);
    }

    public JedisPoolForTesting(String host, int port)
    {
        super(host, port);
    }

    @Override
    public Jedis getResource()
    {
        Jedis jedis = super.getResource();
        jedis.select(DB_TEST_NUMBER);

        return jedis;
    }
}
