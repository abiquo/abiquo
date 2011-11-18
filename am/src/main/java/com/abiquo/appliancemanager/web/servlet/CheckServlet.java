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
package com.abiquo.appliancemanager.web.servlet;

import static com.abiquo.am.data.AMRedisDao.REDIS_POOL;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.exceptions.JedisConnectionException;

import com.abiquo.am.exceptions.AMError;
import com.abiquo.appliancemanager.config.AMConfigurationManager;
import com.abiquo.appliancemanager.exceptions.AMException;

/** Performs specific Appliance Manager checks. */
public class CheckServlet extends AbstractCheckServlet
{
    private static final long serialVersionUID = -4320236147538190023L;

    @Override
    protected boolean check() throws Exception
    {

        if (!AMConfigurationManager.getInstance().validateAMConfiguration())
        {
            throw new AMException(AMError.AM_CHECK, AMConfigurationManager.getInstance()
                .getConfigurationError());
        }

        if (!checkRedis())
        {
            throw new AMException(AMError.AM_CHECK, "No connection to Redis server");
        }

        return true;
    }

    public static boolean checkRedis()
    {
        Jedis redis = null;
        try
        {
            redis = REDIS_POOL.getResource();
            return "PONG".equalsIgnoreCase(redis.ping());
        }
        catch (final JedisConnectionException e)
        {
            return false;
        }
        finally
        {
            if (redis != null)
            {
                REDIS_POOL.returnResource(redis);
            }
        }
    }
}
