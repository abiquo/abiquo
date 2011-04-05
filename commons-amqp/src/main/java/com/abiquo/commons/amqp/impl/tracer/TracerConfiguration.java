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

package com.abiquo.commons.amqp.impl.tracer;

import java.io.IOException;

import com.abiquo.commons.amqp.config.DefaultConfiguration;
import com.rabbitmq.client.Channel;

/**
 * Common RabbitMQ Broker configuration for Tracer consumer and producer.
 * 
 * @author eruiz@abiquo.com
 */
public class TracerConfiguration extends DefaultConfiguration
{
    public static final String TRACER_EXCHANGE = "abiquo.tracer";

    public static final String TRACER_ROUTING_KEY = "abiquo.tracer.traces";

    public static final String TRACER_QUEUE = TRACER_ROUTING_KEY;

    private static TracerConfiguration singleton = null;

    public static TracerConfiguration getInstance()
    {
        if (singleton == null)
        {
            singleton = new TracerConfiguration();
        }

        return singleton;
    }

    @Override
    public void declareBrokerConfiguration(Channel channel) throws IOException
    {
        channel.exchangeDeclare(TRACER_EXCHANGE, DirectExchange, Durable);

        channel.queueDeclare(TRACER_QUEUE, Durable, NonExclusive, NonAutodelete, null);
        channel.queueBind(TRACER_QUEUE, TRACER_EXCHANGE, TRACER_ROUTING_KEY);
    }
}
