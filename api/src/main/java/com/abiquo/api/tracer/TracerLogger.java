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
import java.util.HashMap;
import java.util.Map;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.abiquo.api.tracer.hierarchy.HierarchyProcessor;
import com.abiquo.commons.amqp.impl.tracer.TracerProducer;
import com.abiquo.commons.amqp.impl.tracer.domain.Trace;
import com.abiquo.tracer.ComponentType;
import com.abiquo.tracer.Enterprise;
import com.abiquo.tracer.EventType;
import com.abiquo.tracer.SeverityType;
import com.abiquo.tracer.User;

/**
 * Logger to the Abiquo tracing system.
 * 
 * @author ibarrera
 */
@Service
public class TracerLogger
{
    /** The log system logger. */
    private static final Logger LOGGER = LoggerFactory.getLogger(TracerLogger.class);

    /** The processor factory used to get the hierarchy processors. */
    @Resource(name = "hierarchyProcessorFactory")
    private HierarchyProcessor hierarchyProcessor;

    /** The RabbitMQ producer */
    private TracerProducer producer = new TracerProducer();

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
            Trace trace = getTrace(severity, component, event, message);
            LOGGER.info(trace.toString());
            processHierarchy(trace);
            publishTrace(trace);
        }
        catch (IllegalStateException ex)
        {
            // Just ignore this error for the moment; it appears if the method is invoked outside
            // the servlet container and the TracerFilter has not been invoked. E.g. In unit tests
        }
    }

    /**
     * Log a system message to the event system.
     * <p>
     * This method should only be used to log system tasks such as infrastructure check, etc.
     * Actions performed by a user must be logged using the
     * {@link #log(SeverityType, ComponentType, EventType, String)} method.
     * 
     * @param severity The severity of the trace.
     * @param component The component that generated the trace.
     * @param event The event being traced.
     * @param message The message to trace.
     */
    public void systemLog(final SeverityType severity, final ComponentType component,
        final EventType event, final String message)
    {
        Trace trace = getSystemTrace(severity, component, event, message);
        LOGGER.info(trace.toString());
        publishTrace(trace);
    }

    /**
     * Log a message to the event system.
     * <p>
     * Sends a normal trace or a system trace depending on the user logged. If there is no logged
     * user, it will send a system trace.
     * <p>
     * This method should be only used in methods that can be invoked either by user actions and by
     * system processes (such as one time auth protected resources).
     * 
     * @param severity The severity of the trace.
     * @param component The component that generated the trace.
     * @param event The event being traced.
     * @param message The message to trace.
     */
    public void logFromContext(final SeverityType severity, final ComponentType component,
        final EventType event, final String message)
    {

        TracerContext tracerContext = TracerContextHolder.getContext();
        if (tracerContext.getUserId() != null)
        {
            log(severity, component, event, message);
        }
        else
        {
            systemLog(severity, component, event, message);
        }
    }

    /**
     * Log a system error message to the event system.
     * <p>
     * This method should only be used to log system tasks such as infrastructure check, etc.
     * Actions performed by a user must be logged using the
     * {@link #log(SeverityType, ComponentType, EventType, String)} method.
     * 
     * @param severity The severity of the trace.
     * @param component The component that generated the trace.
     * @param event The event being traced.
     * @param message The message to trace.
     * @param ex The error.
     */
    public void systemError(final SeverityType severity, final ComponentType component,
        final EventType event, final String message, final Throwable error)
    {
        Trace trace = getSystemTrace(severity, component, event, message);
        LOGGER.error(trace.toString(), error);
        publishTrace(trace);
    }

    /**
     * Process the hierarchy to extract resource data.
     * 
     * @param trace The trace with the hierarchy to process.
     */
    private void processHierarchy(final Trace trace)
    {
        Map<String, String> hierarchyData = new HashMap<String, String>();
        hierarchyProcessor.process(trace.getHierarchy(), hierarchyData);
        trace.setHierarchyData(hierarchyData);
    }

    /**
     * Publish the trace to the tracing broker.
     * 
     * @param trace The trace to publish.
     */
    private void publishTrace(final Trace trace)
    {
        try
        {
            producer.openChannel();
            producer.publish(trace);
            producer.closeChannel();
        }
        catch (IOException e)
        {
            LOGGER.error("Could not publish the trace.", e);
        }
    }

    private Trace getTrace(final SeverityType severity, final ComponentType component,
        final EventType event, final String message)
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
        trace.setMessage(message);

        return trace;
    }

    private Trace getSystemTrace(final SeverityType severity, final ComponentType component,
        final EventType event, final String message)
    {
        Trace trace = new Trace();

        trace.setSeverity(severity.name());
        trace.setComponent(component.name());
        trace.setEvent(event.name());
        trace.setEnterpriseName(Enterprise.SYSTEM_ENTERPRISE.getName());
        trace.setUsername(User.SYSTEM_USER.getName());
        trace.setMessage(message);

        return trace;
    }
}
