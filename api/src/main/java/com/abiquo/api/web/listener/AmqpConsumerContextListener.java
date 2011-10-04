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

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.context.support.WebApplicationContextUtils;

import com.abiquo.api.services.appslibrary.event.OVFPackageInstanceStatusEventProcessor;
import com.abiquo.commons.amqp.impl.am.AMCallback;
import com.abiquo.commons.amqp.impl.am.AMConsumer;

/**
 * Set up connections to AMQP broker using commons-amqp facilities.
 */
public class AmqpConsumerContextListener implements ServletContextListener
{
    public static final Logger LOGGER = LoggerFactory.getLogger(AmqpConsumerContextListener.class);

    /** The RabbitMQ consumer for AM **/
    protected AMConsumer amconsumer;

    @Override
    public void contextInitialized(ServletContextEvent sce)
    {
        try
        {
            initializeAMListener(sce);
        }
        catch (IOException e)
        {
            LOGGER.error("can't connect amqp consumer connection");
        }
    }

    @Override
    public void contextDestroyed(ServletContextEvent arg0)
    {
        try
        {
            shutdownAMListener();
        }
        catch (IOException e)
        {
            LOGGER.error("can't disconnect amqp consumer connection");
        }
    }

    /**
     * Creates an instance of {@link AMConsumer}, add all the needed listeners
     * {@link OVFPackageInstanceStatusEventProcessor} and starts the consuming.
     * 
     * @throws IOException When there is some network error.
     */
    protected void initializeAMListener(ServletContextEvent sce) throws IOException
    {
        AMCallback processor =
            WebApplicationContextUtils.getRequiredWebApplicationContext(sce.getServletContext())
                .getBean("ovfPackageInstanceStatusEventProcessor", AMCallback.class);

        amconsumer = new AMConsumer();
        amconsumer.addCallback(processor);
        amconsumer.start();
    }

    /**
     * Stops the {@link AMConsumer}.
     * 
     * @throws IOException When there is some network error.
     */
    private void shutdownAMListener() throws IOException
    {
        amconsumer.stop();
    }

}
