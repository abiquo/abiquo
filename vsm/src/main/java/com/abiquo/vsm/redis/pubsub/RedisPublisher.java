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

import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import redis.clients.jedis.Jedis;

import com.abiquo.vsm.events.VMEvent;

/**
 * Wraps the redis commands for notification business.
 * 
 * @author eruiz@abiquo.com
 */
public class RedisPublisher
{
    private final static Logger logger = LoggerFactory.getLogger(RedisPublisher.class);

    /** Used to compose the event notification message. */
    private final String Separator = "|";

    /** The regex to split the event notification message fields. */
    public static final String RegexSeparator = "\\|";

    /** Redis client */
    private Jedis jedis;

    public RedisPublisher(String host, int port)
    {
        jedis = new Jedis(host, port);
    }

    /**
     * Publish an event to the eventing channel.
     * 
     * @param name The virtual machine name.
     * @param type The event type.
     */
    public void publishEvent(VMEvent event) throws IOException
    {
        String message = createPublishMessage(event);

        jedis.connect();
        jedis.publish(RedisSubscriber.EventingChannel, message);
        jedis.disconnect();
    }

    private String createPublishMessage(VMEvent event)
    {
        StringBuffer message = new StringBuffer();

        message.append(event.getVirtualMachineName()).append(Separator);
        message.append(event.getType().name()).append(Separator);
        message.append(event.getPhysicalMachineAddress());

        return message.toString();
    }
}
