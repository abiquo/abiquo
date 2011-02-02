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

package com.abiquo.vsm.redis.dao;

import java.util.concurrent.TimeoutException;

import org.apache.commons.pool.impl.GenericObjectPool;
import org.apache.commons.pool.impl.GenericObjectPool.Config;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.johm.JOhm;

public class RedisTestDaoFactory
{
    public static JedisPool selectDatabase(String host, int port, int database)
    {
        JedisPool pool = new SelectableJedisPool(host, port, database);
        JOhm.setPool(pool);
        return pool;
    }

    public static void cleanDatabase(JedisPool pool) throws TimeoutException
    {
        Jedis jedis = pool.getResource();
        jedis.flushDB();
        pool.returnResource(jedis);
    }

    private static class SelectableJedisPool extends JedisPool
    {
        private int database;

        public SelectableJedisPool(final GenericObjectPool.Config poolConfig, final String host,
            int databaseNumber)
        {
            super(poolConfig, host);
            this.database = databaseNumber;
        }

        public SelectableJedisPool(String host, int port, int databaseNumber)
        {
            super(host, port);
            this.database = databaseNumber;
        }

        public SelectableJedisPool(final Config poolConfig, final String host, int port,
            int timeout, final String password, int databaseNumber)
        {
            super(poolConfig, host, port, timeout, password);
            this.database = databaseNumber;
        }

        public SelectableJedisPool(final GenericObjectPool.Config poolConfig, final String host,
            final int port, int databaseNumber)
        {
            super(poolConfig, host, port);
            this.database = databaseNumber;
        }

        public SelectableJedisPool(final GenericObjectPool.Config poolConfig, final String host,
            final int port, final int timeout, int databaseNumber)
        {
            super(poolConfig, host, port, timeout);
            this.database = databaseNumber;

        }

        @Override
        public Jedis getResource()
        {
            Jedis resource = super.getResource();
            resource.select(database);

            return resource;
        }
    }
}
