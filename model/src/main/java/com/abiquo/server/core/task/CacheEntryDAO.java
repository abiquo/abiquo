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

import static com.abiquo.model.redis.RedisEntityUtils.getEntityKey;

import org.springframework.stereotype.Component;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.Transaction;

import com.abiquo.model.redis.RedisDAOBase;

/**
 * This base class provides Redis-persistence logic for {@link CacheEntry} entity. <h3>Instance to
 * persist</h3>
 * 
 * <pre>
 * job.id = 0
 * job.values = ["A", "B"]
 * </pre>
 * 
 * <h3>Redis structure</h3>
 * 
 * <pre>
 * LPUSH Entry:0 "A"
 * LPUSH Entry:0 "B"
 * </pre>
 * 
 * @author eruiz@abiquo.com
 */
@Component
public class CacheEntryDAO extends RedisDAOBase<CacheEntry>
{
    @Override
    public void delete(CacheEntry entity, Transaction transaction)
    {
        transaction.del(entity.getEntityKey());
    }

    @Override
    public void save(CacheEntry entity, Transaction transaction)
    {
        delete(entity, transaction);

        for (String value : entity.getValues())
        {
            transaction.lpush(entity.getEntityKey(), value);
        }
    }

    public CacheEntry findById(final String id, Jedis jedis)
    {
        String key = getEntityKey(CacheEntry.class, id);
        CacheEntry entry = null;

        if (jedis.exists(key))
        {
            entry = new CacheEntry(id);
            entry.getValues().addAll(jedis.lrange(key, 0, -1));
        }

        return entry;
    }
}
