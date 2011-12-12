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

package com.abiquo.model.util.redis;

import org.apache.commons.pool.impl.GenericObjectPool.Config;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

public class JedisPoolForTesting extends JedisPool
{
    public final int REDIS_DB_TEST_NUMBER = Integer.valueOf(System.getProperty(
        "abiquo.test.redis.db", "1"));

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
        jedis.select(REDIS_DB_TEST_NUMBER);

        return jedis;
    }
}
