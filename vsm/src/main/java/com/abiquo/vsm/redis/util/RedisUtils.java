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

package com.abiquo.vsm.redis.util;

import java.io.IOException;

import redis.clients.jedis.Jedis;

/**
 * A set of utility methods related to Redis.
 * 
 * @author eruiz@abiquo.com
 */
public class RedisUtils
{
    /**
     * Pings a redis server.
     * 
     * @param host The redis host
     * @param port The redis port
     * @return True on successful ping. Otherwise false.
     */
    public static boolean ping(String host, int port)
    {
        Jedis client = new Jedis(host, port);
        boolean success = false;

        try
        {
            client.connect();
            success = client.ping().equalsIgnoreCase("pong");
            client.disconnect();
        }
        catch (IOException e)
        {
            return false;
        }

        return success;
    }
}
