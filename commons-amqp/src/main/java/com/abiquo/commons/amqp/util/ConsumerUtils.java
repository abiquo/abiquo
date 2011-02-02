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

package com.abiquo.commons.amqp.util;

import java.io.IOException;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Consumer;

/**
 * A collection of helper methods to be used by the producers.
 * 
 * @author eruiz@abiquo.com
 */
public class ConsumerUtils
{
    public static void startConsumerRequiredAck(Channel channel, Consumer consumer, String queue)
        throws IOException
    {
        channel.basicConsume(queue, false, consumer);
    }

    public static void ackMessage(Channel channel, long tag) throws IOException
    {
        channel.basicAck(tag, false);
    }

    public static void rejectMessage(Channel channel, long tag) throws IOException
    {
        channel.basicReject(tag, false);
    }

    public static void rejectMessageAndRequeue(Channel channel, long tag) throws IOException
    {
        channel.basicReject(tag, true);
    }
}
