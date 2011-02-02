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

package com.abiquo.commons.amqp.impl.am;

import java.io.IOException;

import com.abiquo.commons.amqp.config.DefaultConfiguration;
import com.rabbitmq.client.Channel;

public class AMConfiguration extends DefaultConfiguration
{
    public static final String AM_EXCHANGE = "abq.am";

    public static final String AM_ROUTING_KEY = "abq.am.dowloads";

    public static final String AM_QUEUE = AM_ROUTING_KEY;

    private static AMConfiguration singleton = null;

    public static AMConfiguration getInstance()
    {
        if (singleton == null)
        {
            singleton = new AMConfiguration();
        }

        return singleton;
    }

    @Override
    public void declareBrokerConfiguration(Channel channel) throws IOException
    {
        channel.exchangeDeclare(AM_EXCHANGE, DirectExchange, Durable);

        channel.queueDeclare(AM_QUEUE, Durable, NonExclusive, NonAutodelete, null);
        channel.queueBind(AM_QUEUE, AM_EXCHANGE, AM_ROUTING_KEY);
    }
}
