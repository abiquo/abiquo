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

package com.abiquo.api.web.listener;

import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.context.support.WebApplicationContextUtils;

import com.abiquo.api.services.appslibrary.event.AMEventProcessor;
import com.abiquo.commons.amqp.consumer.BasicConsumer;
import com.abiquo.commons.amqp.impl.am.AMCallback;
import com.abiquo.commons.amqp.impl.am.AMConsumer;
import com.abiquo.commons.amqp.impl.vsm.VSMCallback;
import com.abiquo.commons.amqp.impl.vsm.VSMConfiguration;
import com.abiquo.commons.amqp.impl.vsm.VSMConsumer;

/**
 * Set up connections to AMQP broker using commons-amqp facilities.
 */
public class AmqpConsumerContextListener implements ServletContextListener
{
    protected static final Logger LOGGER =
        LoggerFactory.getLogger(AmqpConsumerContextListener.class);

    /** The RabbitMQ consumer for VSM */
    protected VSMConsumer eventsConsumer;

    /** The RabbitMQ consumer for AM */
    protected AMConsumer amConsumer;

    /** Keeps all instantiated consumers */
    protected List<BasicConsumer< ? >> allConsumers;

    @Override
    public void contextInitialized(final ServletContextEvent sce)
    {
        allConsumers = new LinkedList<BasicConsumer< ? >>();

        try
        {
            instantiateAndStartConsumers(sce);
        }
        catch (IOException e)
        {
            LOGGER.error("Can not instantiate and start the AMQP consumers properly", e);
            shutdownAllInstantiatedConsumers();
        }
    }

    @Override
    public void contextDestroyed(final ServletContextEvent arg0)
    {
        shutdownAllInstantiatedConsumers();
    }

    protected void registerConsumer(final BasicConsumer< ? > consumer)
    {
        allConsumers.add(consumer);
    }

    protected void instantiateAndStartConsumers(final ServletContextEvent sce) throws IOException
    {
        initializeEventsConsumer(sce);
        registerConsumer(eventsConsumer);

        initializeAMConsumer(sce);
        registerConsumer(amConsumer);
    }

    /**
     * Stops all instantiated consumers.
     */
    protected void shutdownAllInstantiatedConsumers()
    {
        for (BasicConsumer< ? > consumer : allConsumers)
        {
            try
            {
                consumer.stop();
            }
            catch (IOException e)
            {
                LOGGER.error("Can not stop the AMQP consumer properly", e);
            }
        }
    }

    /**
     * Creates an instance of {@link VSMConsumer}, add all the needed listeners
     * {@link EventingProcessor} and starts the consuming.
     * 
     * @throws IOException When there is some network error.
     */
    protected void initializeEventsConsumer(final ServletContextEvent sce) throws IOException
    {
        eventsConsumer = new VSMConsumer(VSMConfiguration.EVENT_SYNK_QUEUE);

        VSMCallback callback =
            WebApplicationContextUtils.getRequiredWebApplicationContext(sce.getServletContext())
                .getBean(VSMCallback.class);

        eventsConsumer.addCallback(callback);
        eventsConsumer.start();
    }

    /**
     * Creates an instance of {@link AMConsumer}, add all the needed listeners
     * {@link AMEventProcessor} and starts the consuming.
     * 
     * @throws IOException When there is some network error.
     */
    protected void initializeAMConsumer(final ServletContextEvent sce) throws IOException
    {
        AMCallback processor =
            WebApplicationContextUtils.getRequiredWebApplicationContext(sce.getServletContext())
                .getBean("AMEventProcessor", AMCallback.class);

        amConsumer = new AMConsumer();
        amConsumer.addCallback(processor);
        amConsumer.start();
    }
}
