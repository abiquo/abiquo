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

import java.lang.reflect.Field;

import org.testng.annotations.AfterTest;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

@Test(groups = "redisaccess")
public abstract class RedisRepoTestBase
{
    protected AsyncTaskRep repo = new AsyncTaskRep();

    protected JedisPool jedisPool;

    @BeforeTest
    public void testSetUp() throws Exception
    {
        Field jobDaoField = AsyncTaskRep.class.getDeclaredField("jobDao");
        Field taskDaoField = AsyncTaskRep.class.getDeclaredField("taskDao");
        Field repoField = AsyncTaskRep.class.getDeclaredField("jedisPool");

        jobDaoField.setAccessible(true);
        taskDaoField.setAccessible(true);
        repoField.setAccessible(true);

        jobDaoField.set(repo, JobDAO.class.newInstance());
        taskDaoField.set(repo, TaskDAO.class.newInstance());

        JedisPoolConfig jedisPoolConfig = new JedisPoolConfig();
        jedisPoolConfig.setTestOnBorrow(true);

        jedisPool = new JedisPool(jedisPoolConfig, "localhost");
        repoField.set(repo, jedisPool);
    }

    @AfterTest
    public void testTearDown()
    {
        Jedis jedis = jedisPool.getResource();
        jedis.flushDB();
        jedisPool.returnResource(jedis);
    }
}
