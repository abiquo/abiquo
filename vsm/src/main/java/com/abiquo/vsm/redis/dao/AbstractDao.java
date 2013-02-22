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

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.TransactionBlock;

public abstract class AbstractDao
{
    protected static final String IdNamespace = "id";

    protected static final String AllNamespace = "all";

    protected final JedisPool redisPool;

    public AbstractDao(final JedisPool redisPool)
    {
        this.redisPool = redisPool;
    }

    protected static String nest(final String root, final String... namespaces)
    {
        StringBuilder builder = new StringBuilder(root);
        for (String namespace : namespaces)
        {
            builder.append(":").append(namespace);
        }

        return builder.toString();
    }

    protected static String nullToEmpty(final String string)
    {
        return string == null ? "" : string;
    }

    protected Integer generateUniqueId(final String namespace, final Jedis redis)
    {
        return redis.incr(nest(namespace, IdNamespace)).intValue();
    }

    protected <T> T execute(final Function<Jedis, T> function)
    {
        Jedis redis = redisPool.getResource();

        try
        {
            return function.apply(redis);
        }
        finally
        {
            redisPool.returnResource(redis);
        }
    }

    protected void executeTransactionBlock(final Function<Jedis, TransactionBlock2> function)
    {
        Jedis redis = redisPool.getResource();

        try
        {
            TransactionBlock transactionBlock = function.apply(redis);

            if (transactionBlock != null)
            {
                redis.multi(transactionBlock);
            }
        }
        finally
        {
            redisPool.returnResource(redis);
        }
    }

    protected void executeTransactionBlockList(final Function<Jedis, TransactionBlockList> function)
    {
        Jedis redis = redisPool.getResource();

        try
        {
            TransactionBlockList transactionBlock = function.apply(redis);

            if (transactionBlock != null)
            {
                redis.multi(transactionBlock);
            }
        }
        finally
        {
            redisPool.returnResource(redis);
        }
    }
}
