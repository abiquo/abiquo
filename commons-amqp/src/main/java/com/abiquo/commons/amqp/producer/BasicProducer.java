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

package com.abiquo.commons.amqp.producer;

import java.io.IOException;

import com.abiquo.commons.amqp.config.DefaultConfiguration;
import com.abiquo.commons.amqp.domain.Queuable;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.ShutdownListener;
import com.rabbitmq.client.ShutdownSignalException;

public abstract class BasicProducer<C extends DefaultConfiguration, T extends Queuable> implements
    ShutdownListener
{
    protected C config;

    protected Channel channel;

    public BasicProducer()
    {
        config = configurationInstance();
        channel = null;
    }

    public void openChannel() throws IOException
    {
        if (channel == null || !channel.isOpen())
        {
            channel = config.createChannel();
            channel.addShutdownListener(this);

            config.declareBrokerConfiguration(channel);
        }
    }

    public void closeChannel() throws IOException
    {
        channel.removeShutdownListener(this);
        channel = config.closeChannel(channel);
        channel = null;
    }

    @Override
    public void shutdownCompleted(ShutdownSignalException cause)
    {
        channel = null;
    }

    public abstract void publish(final T message) throws IOException;

    public abstract C configurationInstance();
}
