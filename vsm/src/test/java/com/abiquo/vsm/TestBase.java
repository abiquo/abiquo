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
import org.testng.annotations.BeforeMethod;

import redis.clients.jedis.JedisPool;

import com.abiquo.vsm.redis.dao.RedisTestDaoFactory;

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

    @BeforeMethod
    public void setUpDatabase() throws Exception
    {
        setUp();
        pool = RedisTestDaoFactory.selectDatabase(TEST_HOST, TEST_PORT, TEST_DATABASE);
        RedisTestDaoFactory.cleanDatabase(pool);
    }

    @AfterMethod
    public void tearDown() throws Exception
    {
        RedisTestDaoFactory.cleanDatabase(pool);
    }

    protected abstract void setUp() throws Exception;
}
