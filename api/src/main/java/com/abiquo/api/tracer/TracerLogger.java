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
package com.abiquo.api.tracer;

import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.abiquo.commons.amqp.impl.tracer.TracerProducer;
import com.abiquo.commons.amqp.impl.tracer.domain.Trace;
import com.abiquo.tracer.ComponentType;
import com.abiquo.tracer.EventType;
import com.abiquo.tracer.SeverityType;

/**
 * Logger to the Abiquo tracing system.
 * 
 * @author ibarrera
 */
public class TracerLogger
{
    /** The log system logger. */
    private static final Logger LOGGER = LoggerFactory.getLogger(TracerLogger.class);

    /** The tracer logger. */
    private static TracerLogger tracerLogger;

    /** The RabbitMQ producer */
    private TracerProducer producer;

    /**
     * Gets the singleton instance of the logger.
     * 
     * @return The singleton instance of the logger.
     */
    public static TracerLogger getInstance()
    {
        if (tracerLogger == null)
        {
            tracerLogger = new TracerLogger();
        }

        return tracerLogger;
    }

    /**
     * Private constructor to ensure singleton access.
     */
    private TracerLogger()
    {
        producer = new TracerProducer();
    }

    /**
     * Log the message to the event system.
     * 
     * @param severity The severity of the trace.
     * @param component The component that generated the trace.
     * @param event The event being traced.
     * @param message The message to trace.
     */
    public void log(final SeverityType severity, final ComponentType component,
        final EventType event, final String message)
    {
        try
        {
            TracerContext tracerContext = TracerContextHolder.getContext();
            Trace trace = new Trace();

            trace.setSeverity(severity.name());
            trace.setComponent(component.name());
            trace.setEvent(event.name());
            trace.setHierarchy(tracerContext.getHierarchy());
            trace.setEnterpriseId(tracerContext.getEnterpriseId());
            trace.setEnterpriseName(tracerContext.getEnterpriseName());
            trace.setUserId(tracerContext.getUserId());
            trace.setUsername(tracerContext.getUsername());

            LOGGER.info(trace.toString());

            producer.openChannel();
            producer.publish(trace);
            producer.closeChannel();
        }
        catch (IllegalStateException ex)
        {
            // Just ignore this error for the moment; it appears if the method is invoked outside
            // the servlet container and the TracerFilter has not been invoked. E.g. In unit tests
            LOGGER.warn("Could not send the trace.");
        }
        catch (IOException e)
        {
            LOGGER.error("Could not publish the trace.", e);
        }
    }
}
