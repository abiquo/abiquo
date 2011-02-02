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

package com.abiquo.commons.amqp.consumer;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

import com.abiquo.commons.amqp.config.DefaultConfiguration;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Envelope;
import com.rabbitmq.client.ShutdownListener;
import com.rabbitmq.client.ShutdownSignalException;

public abstract class BasicConsumer<C extends DefaultConfiguration, T> implements ShutdownListener
{
    protected Set<T> callbacks;

    protected C config;

    protected Channel channel;

    protected QueueSubscriber<BasicConsumer<C, T>> consumer;

    protected String queueName;

    public BasicConsumer(String queue)
    {
        callbacks = new HashSet<T>();
        config = configurationInstance();
        queueName = queue;
        channel = null;
        consumer = null;
    }

    public void start() throws IOException
    {
        if (channel == null || !channel.isOpen())
        {
            channel = config.createChannel();
            channel.addShutdownListener(this);
            channel.basicQos(getPrefetchCount());

            config.declareBrokerConfiguration(channel);

            consumer = new QueueSubscriber<BasicConsumer<C, T>>(channel, this);
            channel.basicConsume(queueName, false, consumer);
        }
    }

    public void stop() throws IOException
    {
        if (consumer != null && channel != null)
        {
            channel.basicCancel(consumer.getConsumerTag());
            channel.removeShutdownListener(this);
            channel = config.closeChannel(channel);
        }
    }

    public void addCallback(T callback)
    {
        callbacks.add(callback);
    }

    protected int getPrefetchCount()
    {
        return 1;
    }

    @Override
    public void shutdownCompleted(ShutdownSignalException cause)
    {

    }

    public abstract void consume(Envelope envelope, byte[] body) throws IOException;

    public abstract C configurationInstance();
}
