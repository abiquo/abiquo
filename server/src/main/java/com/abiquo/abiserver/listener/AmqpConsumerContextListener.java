package com.abiquo.abiserver.listener;

import java.io.IOException;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.context.support.WebApplicationContextUtils;

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
    public static final Logger LOGGER = LoggerFactory.getLogger(AmqpConsumerContextListener.class);

    /** The RabbitMQ consumer for AM **/
    protected AMConsumer amconsumer;

    /** The RabbitMQ consumer for VSM */
    protected VSMConsumer eventsConsumer;

    @Override
    public void contextInitialized(ServletContextEvent sce)
    {
        try
        {
            initializeAMListener(sce);
            initializeEventsConsumer(sce);
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
            shutdownEventsConsumer();
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
                .getBean("OVFPackageInstanceStatusEventProcessor", AMCallback.class);

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

    /**
     * Creates an instance of {@link VSMConsumer}, add all the needed listeners
     * {@link EventingProcessor} and starts the consuming.
     * 
     * @throws IOException When there is some network error.
     */
    protected void initializeEventsConsumer(ServletContextEvent sce) throws IOException
    {
        eventsConsumer = new VSMConsumer(VSMConfiguration.EVENT_SYNK_QUEUE);

        VSMCallback callback =
            WebApplicationContextUtils.getRequiredWebApplicationContext(sce.getServletContext())
                .getBean(VSMCallback.class);

        eventsConsumer.addCallback(callback);
        eventsConsumer.start();
    }

    /**
     * Stops the {@link VSMConsumer}.
     * 
     * @throws IOException When there is some network error.
     */
    protected void shutdownEventsConsumer() throws IOException
    {
        eventsConsumer.stop();
    }
}
