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

package com.abiquo.commons.amqp.config;

import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;

/**
 * Generic broker configuration, each module configuration must extend this class and fill the
 * abstract methods.
 * 
 * @author eruiz@abiquo.com
 */
public abstract class DefaultConfiguration
{
    /** Logger **/
    private final static Logger logger = LoggerFactory.getLogger(DefaultConfiguration.class);

    /** Properties constants **/

    protected final String PropertiesFilename = "rabbitmq.properties";

    protected final String HostProperty = "rabbitmq.host";

    protected final String PortProperty = "rabbitmq.port";

    protected final String UsernameProperty = "rabbitmq.username";

    protected final String PasswordProperty = "rabbitmq.password";

    protected final String VirtualHostProperty = "rabbitmq.virtualhost";

    /** Constants **/

    protected final String FanoutExchange = "fanout";

    protected final String DirectExchange = "direct";

    protected final String TopicExchange = "topic";

    protected final boolean Durable = true;

    protected final boolean NonDurable = false;

    protected final boolean Exclusive = true;

    protected final boolean NonExclusive = false;

    protected final boolean Autodelete = true;

    protected final boolean NonAutodelete = false;

    /** Class fields **/

    private ConnectionFactory factory = null;

    private Connection connection = null;

    public abstract void declareBrokerConfiguration(Channel channel) throws IOException;

    protected DefaultConfiguration()
    {
        String host = getProperty("abiquo.rabbitmq.host", "localhost");
        int port = Integer.parseInt(getProperty("abiquo.rabbitmq.port", "5672"));
        String username = getProperty("abiquo.rabbitmq.username", "guest");
        String password = getProperty("abiquo.rabbitmq.password", "guest");

        logger.info(String.format("RabbitMQ configuration. Host: %s, port: %d, username: %s", host,
            port, username));

        factory = new ConnectionFactory();

        factory.setHost(host);
        factory.setPort(port);
        factory.setUsername(username);
        factory.setPassword(password);
        factory.setVirtualHost("/");

        connection = null;
    }

    private String getProperty(String name, String defaultValue)
    {
        String value = System.getProperty(name);
        return value == null ? defaultValue : value;
    }

    public String getRabbitMQHost()
    {
        return factory.getHost();
    }

    public int getRabbitMQPort()
    {
        return factory.getPort();
    }

    public Channel createChannel() throws IOException
    {
        if (connection == null)
        {
            connection = factory.newConnection();
        }

        return connection.createChannel();
    }

    public Channel closeChannel(Channel channel) throws IOException
    {
        // TODO Close connection when the last channel alive is closed.
        if (channel != null && channel.isOpen())
        {
            channel.close();
        }

        return channel;
    }
}
