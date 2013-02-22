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
package com.abiquo.vsm;

import org.testng.annotations.AfterMethod;
import org.testng.annotations.AfterTest;
import org.testng.annotations.BeforeTest;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

/**
 * Base class for unit tests that persist data on Redis.
 * 
 * @author ibarrera
 */
public abstract class TestBase
{
    /** The Redis test database. */
    protected static final int TEST_DATABASE = 1;

    /** The redis test host */
    protected static final String TEST_HOST = "localhost";

    /** The redis test port */
    protected static final int TEST_PORT = 6379;

    /** The Redis pool. */
    protected JedisPool pool;

    public TestBase()
    {
        pool = JedisPoolForTesting.instance();
    }

    @BeforeTest
    public void setUpDatabase() throws Exception
    {
        cleanDatabase();
    }

    @AfterMethod
    @AfterTest
    public void tearDown() throws Exception
    {
        cleanDatabase();
    }

    protected void cleanDatabase()
    {
        Jedis redis = pool.getResource();
        redis.flushDB();
        pool.returnResource(redis);
    }
}
