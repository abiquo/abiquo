package com.abiquo.api.web.listener;

import java.io.IOException;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

import com.abiquo.api.services.appslibrary.event.OVFPackageInstanceStatusEventProcessor;
import com.abiquo.commons.amqp.impl.am.AMConsumer;

/**
 * Set up connections to AMQP broker using commons-amqp facilities.
 */
public class AmqpConsumerContextListener implements ServletContextListener, ApplicationContextAware
{
    public static final Logger LOGGER = LoggerFactory.getLogger(AmqpConsumerContextListener.class);

    ApplicationContext applicationContext;

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException
    {
        this.applicationContext = applicationContext;
    }

    /** The RabbitMQ consumer for AM **/
    protected AMConsumer amconsumer;

    @Override
    public void contextInitialized(ServletContextEvent sce)
    {
        try
        {
            initializeAMListener();
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
    protected void initializeAMListener() throws IOException
    {
        OVFPackageInstanceStatusEventProcessor processor =
            applicationContext.getBean(OVFPackageInstanceStatusEventProcessor.class);

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
