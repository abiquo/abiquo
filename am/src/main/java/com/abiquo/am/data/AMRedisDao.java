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

package com.abiquo.am.data;

import static java.lang.System.getProperty;

import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import org.apache.commons.lang.StringUtils;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

import com.abiquo.appliancemanager.transport.TemplateStateDto;
import com.abiquo.appliancemanager.transport.TemplateStatusEnumType;

public class AMRedisDao
{

    // TODO move to AMConfig
    public static final String REDIS_HOS = getProperty("abiquo.redis.host", "localhost");

    public static final Integer REDIS_PORT = Integer.parseInt(getProperty("abiquo.redis.port",
        "6379"));

    private static JedisPoolConfig REDIS_POOL_CONF;

    /** Thread-safe Redis clients pool. */
    public static JedisPool REDIS_POOL;

    static
    {
        initPool();
    }

    public static void initPool()
    {
        REDIS_POOL_CONF = new JedisPoolConfig();
        REDIS_POOL_CONF.setTestOnBorrow(true);

        // REDIS_POOL_CONF.setMaxIdle(4);
        // REDIS_POOL_CONF.setMaxActive(4);
        REDIS_POOL_CONF.setMaxWait(5000); // ms to obtain a redis client

        REDIS_POOL = new JedisPool(REDIS_POOL_CONF, REDIS_HOS, REDIS_PORT);

    }

    /** ########## ########## */

    private final static String EREPOS_KEYS = "erepoKeys:";

    private final static String EREPO = "erepo:";

    private final static String TEMPLATE = "ovf:";

    private final static String STATE = "state";

    private final static String PROGRESS = "progress";

    private final static String ERROR = "error";

    /** ########## ########## */

    private final Jedis redis;

    private AMRedisDao()
    {
        redis = REDIS_POOL.getResource();
    }

    public static synchronized AMRedisDao getDao()
    {
        return new AMRedisDao();
    }

    public static void returnDao(final AMRedisDao dao)
    {
        REDIS_POOL.returnResource(dao.redis);
    }

    /** ########## SET ########## */

    public void init(final String erId, final List<TemplateStateDto> states)
    {
        for (String ovfKey : getOvfKeys(erId))
        {
            redis.srem(key(erId), ovfKey);
            redis.del(ovfKey);
        }

        for (TemplateStateDto state : states)
        {
            if (state.getStatus() == TemplateStatusEnumType.ERROR)
            {
                setError(erId, state.getOvfId(), state.getErrorCause());
            }
            else
            {
                setState(erId, state.getOvfId(), state.getStatus());
            }
        }
    }

    /**
     * @retrurn true if the progress is update (false if remain the same)
     */
    public boolean setDownloadProgress(final String erId, final String ovfId, final Integer progress)
    {
        checkKeyIndex(erId, ovfId);

        final String currentSt = redis.hget(key(erId, ovfId), PROGRESS);
        final Integer current = StringUtils.isEmpty(currentSt) ? 0 : Integer.parseInt(currentSt);
        if (current == 0)
        {
            redis.hset(key(erId, ovfId), STATE, TemplateStatusEnumType.DOWNLOADING.name());
        }

        if (current != progress)
        {
            redis.hset(key(erId, ovfId), PROGRESS, String.valueOf(progress));
        }
        return current != progress;
    }

    public void setState(final String erId, final String ovfId, final TemplateStatusEnumType state)
    {
        checkKeyIndex(erId, ovfId);

        if (state == TemplateStatusEnumType.NOT_DOWNLOAD)
        {
            synchronized (this)
            {
                redis.srem(key(erId), key(erId, ovfId));
                redis.del(key(erId, ovfId));
            }
        }
        else
        {
            redis.hset(key(erId, ovfId), STATE, state.name());
        }
    }

    /** setState ERROR */
    public void setError(final String erId, final String ovfId, final String error)
    {
        checkKeyIndex(erId, ovfId);

        synchronized (this)
        {
            redis.hset(key(erId, ovfId), STATE, TemplateStatusEnumType.ERROR.name());
            redis.hset(key(erId, ovfId), ERROR, error);
        }
    }

    /** ########## GET ########## */

    public Integer getDownloadProgress(final String erId, final String ovfId)
    {
        final String current = redis.hget(key(erId, ovfId), PROGRESS);
        try
        {
            return Integer.parseInt(current != null ? current : "0");
        }
        catch (Exception e)
        {
            return 0;
        }
    }

    /** return NOT_FOUND */
    public TemplateStatusEnumType getStatus(final String erId, final String ovfId)
    {
        if (!redis.sismember(key(erId), key(erId, ovfId)))
        {
            return TemplateStatusEnumType.NOT_DOWNLOAD;
        }

        // if (current == null)
        // {
        // redis.srem(key(erId), key(erId, ovfId));
        // return TemplateStatusEnumType.NOT_DOWNLOAD;
        // }
        final String current = redis.hget(key(erId, ovfId), STATE);
        return TemplateStatusEnumType.valueOf(current);
    }

    public String getError(final String erId, final String ovfId)
    {
        return redis.hget(key(erId, ovfId), ERROR);
    }

    public List<TemplateStateDto> getAll(final String erId)
    {
        final List<TemplateStateDto> statusLst = new LinkedList<TemplateStateDto>();

        for (String keyOvf : getOvfKeys(erId))
        {
            final List<String> fields = redis.hmget(keyOvf, STATE, PROGRESS, ERROR);
            statusLst.add(createOVFStatus(keyOvf, fields));
        }

        return statusLst;
    }

    /** ########## ########## */

    private static TemplateStateDto createOVFStatus(final String keyOvf, final List<String> fields)
    {
        TemplateStateDto status = new TemplateStateDto();
        status.setOvfId(ovfId(keyOvf));
        final String st = fields.get(0);
        final String progress = fields.get(1);
        final String error = fields.get(2);
        status.setStatus(st != null ? TemplateStatusEnumType.valueOf(st)
            : TemplateStatusEnumType.NOT_DOWNLOAD);
        status.setDownloadingProgress(StringUtils.isEmpty(progress) ? null : Double
            .valueOf(progress));
        status.setErrorCause(StringUtils.isEmpty(error) ? null : error);

        return status;
    }

    /** ########## ########## */

    private void checkKeyIndex(final String erId, final String ovfId)
    {
        redis.sadd(key(erId), key(erId, ovfId));
    }

    private Set<String> getOvfKeys(final String erId)
    {
        return redis.smembers(key(erId));
    }

    private static String key(final String erId)
    {
        return EREPOS_KEYS + erId;
    }

    private static String key(final String erId, final String ovfId)
    {
        return EREPO + erId + ':' + TEMPLATE + ovfId;
    }

    private static String ovfId(final String keyOovfId)
    {
        return keyOovfId.substring(keyOovfId.indexOf("http://"));
    }
}
