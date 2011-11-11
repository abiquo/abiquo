package com.abiquo.server.core.task;

import org.apache.commons.pool.impl.GenericObjectPool.Config;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.Transaction;

@Test(groups = "redisaccess")
public abstract class RedisAccessTestBase
{
    protected final static int DB_NUMBER = 1;

    protected JedisPool jedisPool;

    protected Jedis jedis;

    @BeforeTest
    public void testSetUp()
    {
        Config config = new Config();
        config.testOnBorrow = true;

        this.jedisPool = new JedisPool(config, "localhost");
    }

    @BeforeMethod
    public void methodSetUp()
    {
        this.jedis = this.jedisPool.getResource();
        this.jedis.select(DB_NUMBER);
    }

    @AfterMethod
    public void methodTearDown()
    {
        this.jedis.flushDB();
        this.jedisPool.returnResource(this.jedis);
    }

    protected Transaction getTransaction()
    {
        return jedis.multi();
    }
}
