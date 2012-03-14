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

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.Transaction;

/**
 * Repository to manage the redis-based {@link CacheEntryDAO}
 * 
 * @author eruiz@abiquo.com
 */
@Component
public class CacheEntryRep
{
    @Autowired
    protected JedisPool jedisPool;

    @Autowired
    protected CacheEntryDAO entryDao;

    public CacheEntry save(CacheEntry entry)
    {
        Jedis jedis = jedisPool.getResource();
        Transaction transaction = jedis.multi();
        boolean discard = true;

        try
        {
            entryDao.save(entry, transaction);
            transaction.exec();
            discard = false;
        }
        finally
        {
            if (discard)
            {
                transaction.discard();
            }

            jedisPool.returnResource(jedis);
        }

        return entry;
    }

    public void delete(CacheEntry entry)
    {
        Jedis jedis = jedisPool.getResource();
        Transaction transaction = jedis.multi();
        boolean discard = true;

        try
        {
            entryDao.delete(entry, transaction);
            transaction.exec();
            discard = false;
        }
        finally
        {
            if (discard)
            {
                transaction.discard();
            }

            jedisPool.returnResource(jedis);
        }
    }

    public CacheEntry find(final String id)
    {
        Jedis jedis = jedisPool.getResource();

        try
        {
            return entryDao.findById(id, jedis);
        }
        finally
        {
            jedisPool.returnResource(jedis);
        }
    }
}
