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

package com.abiquo.tracer.server;

import java.util.ArrayList;
import java.util.List;

import javax.jms.Connection;
import javax.jms.ExceptionListener;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageConsumer;
import javax.jms.MessageListener;
import javax.jms.ObjectMessage;
import javax.jms.Queue;
import javax.jms.Session;

import org.apache.activemq.ActiveMQConnectionFactory;
import org.apache.activemq.broker.BrokerService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.abiquo.tracer.Constants;
import com.abiquo.tracer.TracerTo;

public class TracerCollector implements ExceptionListener, MessageListener
{

    private static final Logger log = LoggerFactory.getLogger(TracerCollector.class.getName());

    private String brokerUrl;

    private ActiveMQConnectionFactory connectionFactory;

    private Connection connection;

    private Session session;

    private Queue destination;

    private MessageConsumer consumer;

    private List<TracerProcessor> processors = new ArrayList<TracerProcessor>();

    private BrokerService broker;

    protected TracerCollector()
    {
        broker = new BrokerService();

        // configure the broker
        try
        {
            String propertyBrokerUrl = System.getProperty(Constants.ABICLOUD_TRACER_BROKER_URL);
            brokerUrl = ((propertyBrokerUrl == null) ? Constants.BROKER_URL : propertyBrokerUrl);
            broker.addConnector(brokerUrl);
            broker.setPersistent(false);
            broker.start();
        }
        catch (Exception e)
        {
            log.error("Error creating broker.", e);
        }
    }

    /**
	 * 
	 */
    public void init()
    {
        try
        {
            connectionFactory = new ActiveMQConnectionFactory(brokerUrl);
            connection = connectionFactory.createConnection();
            connection.setExceptionListener(this);
            connection.start();

            session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
            destination = session.createQueue(Constants.QUEUE_TRACER_OUT_QUEUE);
            consumer = session.createConsumer(destination);

            consumer.setMessageListener(this);
            log.info("All queues connected");
        }
        catch (Exception e)
        {
            log.error("Error creating queues.", e);
        }
    }

    public void recover()
    {
        try
        {
            session.recover();
        }
        catch (JMSException e)
        {
            e.printStackTrace();
        }
    }

    public void destroy() throws Exception
    {
        try
        {
            consumer.close();
            session.close();
            connection.close();
            broker.stop();
            broker.waitUntilStopped();
            log.info("Broker and Consumer Tracer DESTROYED");
        }
        catch (Exception e)
        {
            log.error("Error destroying queues.", e);
            throw e;
        }

        for (TracerProcessor processor : processors)
        {
            try
            {
                processor.destroy();
            }
            catch (TracerCollectorException tex)
            {
                log.error("Can not destroy processor", tex);
            }
        }
    }

    public void onMessage(Message message)
    {
        ObjectMessage obj = (ObjectMessage) message;
        TracerTo payload = null;
        try
        {
            payload = (TracerTo) obj.getObject();
            for (TracerProcessor proc : processors)
            {
                log.debug("RECEIVED MESSAGE: " + payload.toString());
                try
                {
                    proc.process(payload);
                    log.debug("Dispatched payload to:" + proc.getClass().getName());
                }
                catch (TracerCollectorException e)
                {
                    log.error("Cannot dispatch to listener:" + payload.toString(), e);
                }
            }
        }
        catch (JMSException e)
        {
            log.error("Error unmarshalling payload of JMS queues.", e);
        }
    }

    @Override
    public void onException(JMSException arg0)
    {
        log.error("JMS onException raised", arg0);
    }

    public void addListener(TracerProcessor proc) throws Exception
    {
        processors.add(proc);
        log.info("Registered in TracerCollector:" + proc.getClass().getName());
    }
}
