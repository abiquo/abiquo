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

import static com.abiquo.commons.amqp.impl.tracer.TracerConfiguration.TRACER_EXCHANGE;
import static com.abiquo.commons.amqp.impl.tracer.TracerConfiguration.TRACER_ROUTING_KEY;
import static com.abiquo.commons.amqp.util.ProducerUtils.publishPersistentText;

import java.io.IOException;

import com.abiquo.commons.amqp.impl.tracer.domain.Trace;
import com.abiquo.commons.amqp.producer.BasicProducer;

public class TracerProducer extends BasicProducer<TracerConfiguration, Trace>
{
    @Override
    public TracerConfiguration configurationInstance()
    {
        return TracerConfiguration.getInstance();
    }

    @Override
    public void publish(Trace message) throws IOException
    {
        publishPersistentText(channel, TRACER_EXCHANGE, TRACER_ROUTING_KEY, message.toByteArray());
    }
}
