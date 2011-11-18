/**
 * Abiquo community edition
 * cloud management application for hybrid clouds
 * Copyright (C) 2008-2010 - Abiquo Holdings S.L.
 *
 * This application is free software; you can redistribute it and/or
 * modify it under the terms of the GNU LESSER GENERAL PUBLIC
 * LICENSE as published by the Free Software Foundation under
 * version 3 of the License
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * LESSER GENERAL PUBLIC LICENSE v.3 for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the
 * Free Software Foundation, Inc., 59 Temple Place - Suite 330,
 * Boston, MA 02111-1307, USA.
 */

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
public abstract class RedisDAOTestBase
{
    protected final static int DB_NUMBER = 1;

    protected static JedisPool jedisPool;

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
