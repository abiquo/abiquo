package com.abiquo.am.data;

import static java.lang.System.getProperty;

import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import org.apache.commons.lang.StringUtils;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

import com.abiquo.appliancemanager.transport.OVFPackageInstanceStateDto;
import com.abiquo.appliancemanager.transport.OVFStatusEnumType;

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
        REDIS_POOL = new JedisPool(REDIS_POOL_CONF, REDIS_HOS, REDIS_PORT);

    }

    /** ########## ########## */

    private final static String STATE = "state";

    private final static String PROGRESS = "progress";

    private final static String ERROR = "error";

    /** ########## ########## */

    private final Jedis redis;

    private AMRedisDao()
    {
        redis = REDIS_POOL.getResource();
    }

    public static AMRedisDao getDao()
    {
        return new AMRedisDao();
    }

    public static void returnDao(final AMRedisDao dao)
    {
        REDIS_POOL.returnResource(dao.redis);
    }

    /** ########## SET ########## */

    public void init(final String erId, final List<OVFPackageInstanceStateDto> states)
    {
        for (String ovfKey : getOvfKeys(erId))
        {
            redis.del(ovfKey);
        }

        for (OVFPackageInstanceStateDto state : states)
        {
            if (state.getStatus() == OVFStatusEnumType.ERROR)
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
            redis.hset(key(erId, ovfId), STATE, OVFStatusEnumType.DOWNLOADING.name());
        }

        if (current != progress)
        {
            redis.hset(key(erId, ovfId), PROGRESS, String.valueOf(progress));
        }
        return current != progress;
    }

    public void setState(final String erId, final String ovfId, final OVFStatusEnumType state)
    {
        checkKeyIndex(erId, ovfId);

        if (state == OVFStatusEnumType.NOT_DOWNLOAD)
        {
            redis.srem(key(erId), key(erId, ovfId));
            redis.del(key(erId, ovfId));
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

        redis.hset(key(erId, ovfId), STATE, OVFStatusEnumType.ERROR.name());
        redis.hset(key(erId, ovfId), ERROR, error);
    }

    /** ########## GET ########## */

    public Integer getDownloadProgress(final String erId, final String ovfId)
    {
        final String current = redis.hget(key(erId, ovfId), PROGRESS);
        return Integer.parseInt(current != null ? current : "0");
    }

    /** return NOT_FOUND */
    public OVFStatusEnumType getStatus(final String erId, final String ovfId)
    {
        if (!redis.sismember(key(erId), key(erId, ovfId)))
        {
            return OVFStatusEnumType.NOT_DOWNLOAD;
        }

        final String current = redis.hget(key(erId, ovfId), STATE);
        return OVFStatusEnumType.valueOf(current);
    }

    public String getError(final String erId, final String ovfId)
    {
        return redis.hget(key(erId, ovfId), ERROR);
    }

    public List<OVFPackageInstanceStateDto> getAll(final String erId)
    {
        final List<OVFPackageInstanceStateDto> statusLst =
            new LinkedList<OVFPackageInstanceStateDto>();

        for (String keyOvf : getOvfKeys(erId))
        {
            final List<String> fields = redis.hmget(keyOvf, STATE, PROGRESS, ERROR);
            statusLst.add(createOVFStatus(keyOvf, fields));
        }

        return statusLst;
    }

    /** ########## ########## */

    private static OVFPackageInstanceStateDto createOVFStatus(final String keyOvf,
        final List<String> fields)
    {
        OVFPackageInstanceStateDto status = new OVFPackageInstanceStateDto();
        status.setOvfId(ovfId(keyOvf));
        final String st = fields.get(0);
        final String progress = fields.get(1);
        final String error = fields.get(2);
        status.setStatus(st != null ? OVFStatusEnumType.valueOf(st)
            : OVFStatusEnumType.NOT_DOWNLOAD);
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
        return "erepoKes:" + erId;
    }

    private static String key(final String erId, final String ovfId)
    {
        return "erepo:" + erId + ":ovf:" + ovfId;
    }

    private static String ovfId(final String keyOovfId)
    {
        return keyOovfId.substring(keyOovfId.lastIndexOf("ovf:") + 4);
    }
}
