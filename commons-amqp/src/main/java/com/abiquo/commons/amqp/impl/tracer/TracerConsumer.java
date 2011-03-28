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

import static com.abiquo.commons.amqp.impl.tracer.TracerConfiguration.TRACER_QUEUE;
import static com.abiquo.commons.amqp.util.ConsumerUtils.ackMessage;
import static com.abiquo.commons.amqp.util.ConsumerUtils.rejectMessage;

import java.io.IOException;

import com.abiquo.commons.amqp.consumer.BasicConsumer;
import com.abiquo.commons.amqp.impl.tracer.domain.Trace;
import com.rabbitmq.client.Envelope;

public class TracerConsumer extends BasicConsumer<TracerConfiguration, TracerCallback>
{
    public TracerConsumer()
    {
        super(TRACER_QUEUE);
    }

    @Override
    public TracerConfiguration configurationInstance()
    {
        return TracerConfiguration.getInstance();
    }

    @Override
    public void consume(Envelope envelope, byte[] body) throws IOException
    {
        Trace trace = Trace.fromByteArray(body);

        if (trace != null)
        {
            for (TracerCallback callback : callbacks)
            {
                callback.onTrace(trace);
            }

            ackMessage(channel, envelope.getDeliveryTag());
        }
        else
        {
            rejectMessage(channel, envelope.getDeliveryTag());
        }
    }
}
