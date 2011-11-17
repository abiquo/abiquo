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

package com.abiquo.vsm;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.abiquo.commons.amqp.util.RabbitMQUtils;
import com.abiquo.vsm.model.PhysicalMachine;
import com.abiquo.vsm.redis.dao.RedisDao;
import com.abiquo.vsm.redis.dao.RedisDaoFactory;
import com.abiquo.vsm.redis.pubsub.RedisSubscriber;
import com.abiquo.vsm.redis.util.RedisUtils;

/**
 * Handles the VSM initialization and destruction.
 * 
 * @author eruiz@abiquo.com
 */
public class VSMManager
{
    private final static Logger logger = LoggerFactory.getLogger(VSMManager.class);

    private enum InitializationSteps
    {
        Uninitialized, CheckRabbitMQRunning, StartSubscriber, ReloadMonitors, Initialized;

        public InitializationSteps next()
        {
            return ordinal() < (values().length - 1) ? values()[ordinal() + 1] : this;
        }

        public boolean isInitialized()
        {
            return this.equals(Initialized);
        }
    }

    private final String redisHost;

    private final int redisPort;

    private InitializationSteps current;

    private RedisSubscriber subscriber;

    private static VSMManager instance = null;

    private ExecutorService subscriberExecutor;

    private VSMManager()
    {
        current = InitializationSteps.Uninitialized;

        redisHost = getProperty("abiquo.redis.host", "localhost");
        redisPort = Integer.valueOf(getProperty("abiquo.redis.port", "6379"));

        subscriberExecutor = Executors.newSingleThreadExecutor();
    }

    public static VSMManager getInstance()
    {
        if (instance == null)
        {
            instance = new VSMManager();
        }

        return instance;
    }

    private String getProperty(String name, String defaultValue)
    {
        String value = System.getProperty(name);
        return value == null ? defaultValue : value;
    }

    /**
     * Initializes all the modules. If one is not well initialized, VSM will not handle requests.
     */
    public void initialize()
    {
        InitializationSteps nextStep = current.next();
        boolean success = true;

        while (!current.isInitialized() && success)
        {
            switch (nextStep)
            {
                case CheckRabbitMQRunning:
                    success = isRabbitMQRunning();
                    break;

                case StartSubscriber:
                    success = startSubscriber();
                    break;

                case ReloadMonitors:
                    reloadMonitors();
                    success = true;
                    break;

                default:
                    success = true;
                    break;
            }

            if (success)
            {
                current = nextStep;
                nextStep = current.next();
            }
        }
    }

    private boolean isRabbitMQRunning()
    {
        try
        {
            return RabbitMQUtils.pingRabbitMQ();
        }
        catch (Exception e)
        {
            return false;
        }
    }

    private boolean isRedisRunning()
    {
        return RedisUtils.ping(redisHost, redisPort);
    }

    private boolean startSubscriber()
    {
        logger.info("Starting redis subscriber.");

        boolean ping = isRedisRunning();

        if (ping)
        {
            subscriber = new RedisSubscriber(redisHost, redisPort);
            subscriberExecutor.execute(subscriber);
        }
        else
        {
            logger.error("Redis is not listening at {}:{}. VSM is not properly initialized.",
                redisHost, redisPort);
        }

        return ping;
    }

    /**
     * Checks if all the modules are correctly initialized and working.
     * 
     * @return True if all is OK. Otherwise false.
     */
    public boolean checkSystem()
    {
        logger.info("Checking VSM status...");

        boolean initialized = true;

        if (!current.isInitialized())
        {
            // Continue initialization from last step
            initialize();
            initialized = current.isInitialized();
        }

        if (current == InitializationSteps.Initialized)
        {
            // RabbitMQ could be down
            if (!isRabbitMQRunning())
            {
                logger.error("VSM can not ping RabbitMQ check if RabbitMQ is up and reset VSM.");
                initialized = false;
            }

            // Redis could be down
            if (!isRedisRunning())
            {
                logger.error("Redis is not listening at {}:{}. VSM is not properly initialized.",
                    redisHost, redisPort);
                initialized = false;
            }

            // Redis subscriber could be down
            if (!subscriber.isRunning() && initialized)
            {
                // Try to re-start
                logger.error("The subscriber is not running. Trying to restart.");
                initialized = startSubscriber();
            }
        }

        if (initialized)
        {
            logger.info("VSM is up and running properly.");
        }
        else
        {
            logger.error("VSM is NOT running properly. Check redis and rabbitmq status.");
        }

        return initialized;
    }

    /**
     * Stops the subscriber thread and waits for the correct thread unload.
     */
    public void destroy()
    {
        subscriberExecutor.shutdown();
        VSMService.getInstance().stopAllMonitors();
    }

    /**
     * Starts the monitoring of all redis stored physical machines.
     */
    protected void reloadMonitors()
    {
        RedisDao dao = RedisDaoFactory.getInstance();

        for (PhysicalMachine machine : dao.findAllPhysicalMachines())
        {
            String address = machine.getAddress();
            String type = machine.getType();
            String username = machine.getUsername();
            String password = machine.getPassword();

            try
            {
                VSMService.getInstance().createAndStartMonitor(address, type, username, password);
            }
            catch (Exception e)
            {
                logger.error("Could not start monitoring the machine at address" + address);
            }
        }
    }

    public String getRedisHost()
    {
        return redisHost;
    }

    public int getRedisPort()
    {
        return redisPort;
    }
}
