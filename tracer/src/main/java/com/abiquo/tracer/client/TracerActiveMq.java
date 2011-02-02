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

package com.abiquo.tracer.client;

import javax.jms.Connection;
import javax.jms.DeliveryMode;
import javax.jms.Destination;
import javax.jms.ExceptionListener;
import javax.jms.JMSException;
import javax.jms.MessageProducer;
import javax.jms.ObjectMessage;
import javax.jms.Session;

import org.apache.activemq.pool.PooledConnectionFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.abiquo.tracer.ComponentType;
import com.abiquo.tracer.Constants;
import com.abiquo.tracer.EventType;
import com.abiquo.tracer.Platform;
import com.abiquo.tracer.SeverityType;
import com.abiquo.tracer.TracerTo;
import com.abiquo.tracer.UserInfo;

public class TracerActiveMq implements Tracer, ExceptionListener
{
    private static final Logger log = LoggerFactory.getLogger(TracerActiveMq.class.getName());

    private PooledConnectionFactory connectionFactory;

    protected TracerActiveMq()
    {
        try
        {
            String brokerUrl =
                System.getProperty(Constants.ABICLOUD_TRACER_BROKER_URL, Constants.BROKER_URL);

            connectionFactory = new PooledConnectionFactory(brokerUrl);

            log.info("TracerActiveMq initialized");
        }
        catch (Exception e)
        {
            log.error("Error initializing TracerActiveMq.", e);
        }
    }

    private synchronized void sendEvent(TracerTo payload) throws Exception
    {
        Connection connection = null;

        Destination destination = null;

        Session session = null;

        MessageProducer producer = null;
        try
        {
            connection = connectionFactory.createConnection();
            connection.start();

            // Create the session
            session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
            destination = session.createQueue(Constants.QUEUE_TRACER_OUT_QUEUE);

            // Create the producer.
            producer = session.createProducer(destination);

            producer.setDeliveryMode(DeliveryMode.NON_PERSISTENT);

            ObjectMessage msg = session.createObjectMessage(payload);
            producer.send(msg);
            log.debug("Sent message back to process queue:" + payload.toString());
        }
        catch (Exception e)
        {
            log.error("Cannot send message:" + payload.toString(), e);
            throw e;
        }
        finally
        {
            try
            {
                if (producer != null)
                {
                    producer.close();
                }
                if (session != null)
                {
                    session.close();
                }
                if (connection != null)
                {
                    connection.close();
                }
            }
            catch (JMSException e)
            {
                log.error("Cannot destroy jms connection", e);
                throw e;
            }
        }
    }

    public void onException(JMSException exception)
    {
        log.error("An error sending to the queue:", exception);
    }

    public void destroy() throws Exception
    {

    }

    @Override
    public void log(SeverityType level, ComponentType component, EventType event,
        String description, UserInfo user, Platform platform)
    {
        TracerTo to = new TracerTo();
        try
        {
            to.setSeverity(level);
            to.setComponent(component);
            to.setEvent(event);
            to.setDescription(description);
            to.setUser(user);
            to.setTimestamp(System.currentTimeMillis());
            to.setPlatform(platform);
            this.sendEvent(to);
        }
        catch (Exception e)
        {
            // No exceptions. It must be transparent to the rest of the
            // application
            log.error("Cannot send trace information: " + to.toString(), e);
        }
    }

    @Override
    public void log(SeverityType level, ComponentType component, EventType event, String description)
    {
        this.log(level, component, event, description, null, null);
    }

    @Override
    public void log(SeverityType level, ComponentType component, EventType event)
    {
        this.log(level, component, event, "", null, null);
    }

    @Override
    public void log(SeverityType level, ComponentType component, EventType event, UserInfo user)
    {
        this.log(level, component, event, "", user, null);
    }

    @Override
    public void log(SeverityType level, ComponentType component, EventType event,
        String description, UserInfo user)
    {
        this.log(level, component, event, description, user, null);
    }

    @Override
    public void log(SeverityType level, ComponentType component, EventType event, UserInfo user,
        Platform platform)
    {
        this.log(level, component, event, "", user, platform);
    }

    @Override
    public void log(SeverityType level, ComponentType component, EventType event,
        String description, Platform platform)
    {
        this.log(level, component, event, description, null, platform);
    }

    @Override
    public void log(SeverityType level, ComponentType component, EventType event, Platform platform)
    {
        this.log(level, component, event, "", null, platform);
    }

}
