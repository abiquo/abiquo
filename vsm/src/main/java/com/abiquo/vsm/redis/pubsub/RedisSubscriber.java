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

package com.abiquo.vsm.redis.pubsub;

import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import redis.clients.jedis.Jedis;

import com.abiquo.vsm.redis.dao.RedisDaoFactory;

/**
 * Performs the subscription to redis eventing channel (where events are published).
 * 
 * @see http://code.google.com/p/redis/wiki/PublishSubscribe
 * @see RedisSubscriberCallback
 * @author eruiz@abiquo.com
 */
public class RedisSubscriber implements Runnable
{
    private final static Logger logger = LoggerFactory.getLogger(RedisSubscriber.class);

    /** Channel where notifications are published */
    public static final String EventingChannel = "EventingChannel";

    /** Tag to publish in order to stop the subscription */
    public static final String StopTag = UUID.randomUUID().toString();

    /** True if the subscriber is running. */
    private boolean running = false;

    /** Redis host */
    private String host;

    /** Redis port */
    private int port;

    public RedisSubscriber(String host, int port)
    {
        this.host = host;
        this.port = port;
    }

    /**
     * Subscribe to redis eventing channel.
     */
    @Override
    public void run()
    {
        try
        {
            Jedis redis = new Jedis(host, port);
            redis.connect();

            running = true;

            RedisSubscriberCallback callback = new RedisSubscriberCallback(host, port);
            redis.subscribe(callback, EventingChannel);

            redis.disconnect();
        }
        catch (Exception e)
        {
            RedisDaoFactory.refreshConnectionsPool();

            logger.error("Error on redis subscription. {}.", e);
            logger.error("VSM is not able to notify events. Please check redis server at {}:{}.",
                host, port);
        }
        finally
        {
            running = false;
        }
    }

    /**
     * Is the subscriber running?
     * 
     * @return True if the subscriber is running. Otherwise false.
     */
    public boolean isRunning()
    {
        return running;
    }
}
