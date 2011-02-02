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

package com.abiquo.vsm.migration;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.Transaction;

/**
 * Wraps the redis commands for subscription objects.
 * 
 * @author eruiz@abiquo.com
 */
public class RedisWrapper
{
    private final static Logger logger = LoggerFactory.getLogger(RedisWrapper.class);

    /** Redis client */
    private Jedis redis;

    /** The beginning of each subscription object key */
    private final String keyBeginning = "subscription";

    /** Used to compose the event notification message. */
    private final String separator = "|";

    /** The regex to split the event notification message fields. */
    public static final String regexSeparator = "\\|";

    private final Keymaker namespace;

    private int database;

    public RedisWrapper(String host, int port, int databaseNumber)
    {
        redis = new Jedis(host, port);
        namespace = new Keymaker(keyBeginning);
        database = databaseNumber;
    }

    private boolean connect()
    {
        try
        {
            redis.connect();
            redis.select(database);
        }
        catch (Exception e)
        {
            logger.error(e.getMessage());
            return false;
        }

        return true;
    }

    private void disconnect()
    {
        try
        {
            redis.disconnect();
        }
        catch (IOException e)
        {
            logger.error(e.getMessage());
        }
    }

    private String sinkKey(String id)
    {
        return namespace.build(id, "sink").toString();
    }

    private String hypervisorUrlKey(String id)
    {
        return namespace.build(id, "hypervisorurl").toString();
    }

    private String hypervisorTypeKey(String id)
    {
        return namespace.build(id, "hypervisortype").toString();
    }

    private String userKey(String id)
    {
        return namespace.build(id, "user").toString();
    }

    private String passwordKey(String id)
    {
        return namespace.build(id, "password").toString();
    }

    private String virtualSystemIdKey(String id)
    {
        return namespace.build(id, "uuid").toString();
    }

    public boolean ping()
    {
        boolean success = false;

        if (connect())
        {
            success = redis.ping().equalsIgnoreCase("pong");
            disconnect();
        }

        return success;
    }

    /**
     * Checks if some Object in list is null.
     * 
     * @param objects The list of Object
     * @return True if some Object in list is null. Otherwise false.
     */
    private boolean thereAreNullObjects(Object... objects)
    {
        boolean nulls = false;

        for (Object o : objects)
        {
            if (o == null)
            {
                nulls = true;
                break;
            }
        }

        return nulls;
    }

    /**
     * Checks if some String in list is empty.
     * 
     * @param strings The list of String
     * @return True if some String in list is empty. Otherwise false.
     */
    private boolean thereAreEmptyStrings(String... strings)
    {
        boolean empty = false;

        for (String s : strings)
        {
            if (s.length() == 0)
            {
                empty = true;
                break;
            }
        }

        return empty;
    }

    /**
     * Inserts a subscription object to redis.
     * 
     * @param id The virtual system Id.
     * @param eventSink The notification URL.
     * @param hypervisorURL The hypervisor URL.
     * @param hypervisorType The hypervisor type.
     * @param user The admin user for the hypervisor.
     * @param password The admin password for the hypervisor
     * @return True if the object is inserted. Otherwise false.
     */
    public boolean insertSubscription(String id, String eventSink, String hypervisorURL,
        String hypervisorType, String user, String password)
    {
        if (thereAreNullObjects(id, eventSink, hypervisorURL, hypervisorType, user, password))
            return false;

        if (thereAreEmptyStrings(id, eventSink, hypervisorURL, hypervisorType, user, password))
            return false;

        boolean inserted = false;

        if (connect())
        {
            Transaction t = redis.multi();

            t.set(sinkKey(id), eventSink);
            t.set(hypervisorUrlKey(id), hypervisorURL);
            t.set(hypervisorTypeKey(id), hypervisorType);
            t.set(userKey(id), user);
            t.set(passwordKey(id), password);
            t.set(virtualSystemIdKey(id), id);

            t.exec();

            inserted = true;

            disconnect();
        }

        return inserted;
    }

    /**
     * Deletes a subscription object with the specified id.
     * 
     * @param id The virtual system Id
     * @return True if the object is deleted. Otherwise false.
     */
    public boolean deleteSubscription(String id)
    {
        if (thereAreNullObjects(id))
            return false;

        if (thereAreEmptyStrings(id))
            return false;

        boolean deleted = false;

        if (connect())
        {
            Transaction t = redis.multi();

            t.del(sinkKey(id));
            t.del(hypervisorUrlKey(id));
            t.del(hypervisorTypeKey(id));
            t.del(userKey(id));
            t.del(passwordKey(id));
            t.del(virtualSystemIdKey(id));

            t.exec();

            deleted = true;

            disconnect();
        }

        return deleted;
    }

    /**
     * Checks if a subscription exist.
     * 
     * @param id The virtual system id.
     * @return True if the subscription object exist. Otherwise false.
     */
    public boolean existSubscription(String id)
    {
        if (thereAreNullObjects(id))
            return false;

        if (thereAreEmptyStrings(id))
            return false;

        boolean exists = false;

        if (connect())
        {
            exists = redis.exists(sinkKey(id));
            exists &= redis.exists(hypervisorUrlKey(id));
            exists &= redis.exists(hypervisorTypeKey(id));
            exists &= redis.exists(userKey(id));
            exists &= redis.exists(passwordKey(id));
            exists &= redis.exists(virtualSystemIdKey(id));

            disconnect();
        }

        return exists;
    }

    private String getValue(String key)
    {
        String value = null;

        if (connect())
        {
            value = redis.get(key);

            if (value != null)
            {
                if (value.equalsIgnoreCase("nil"))
                {
                    value = null;
                }
            }

            disconnect();
        }

        return value;
    }

    public String getEventSink(String id)
    {
        return getValue(sinkKey(id));
    }

    public String getHypervisorType(String id)
    {
        return getValue(hypervisorTypeKey((id)));
    }

    public String getHypervisorUrl(String id)
    {
        return getValue(hypervisorUrlKey((id)));
    }

    public String getUser(String id)
    {
        return getValue(userKey(id));
    }

    public String getPassword(String id)
    {
        return getValue(passwordKey(id));
    }

    public String getVirtualSystemId(String id)
    {
        return getValue(virtualSystemIdKey(id));
    }

    /**
     * Publish, if it exist, an event to the eventing channel.
     * 
     * @param id The virtual system Id.
     * @param type The event type.
     */
    // public void publishEvent(String id, EventTypeEnumeration type)
    // {
    // String message = createPublishMessage(id, type);
    //
    // if (connect())
    // {
    // redis.publish(RedisSubscriber.eventingChannel, message);
    // disconnect();
    // }
    // }

    // private String createPublishMessage(String id, EventTypeEnumeration type)
    // {
    // StringBuffer message = new StringBuffer();
    //
    // message.append(id).append(separator).append(type.value());
    //
    // return message.toString();
    // }

    /**
     * Returns the Id's of all the subscription objects.
     * 
     * @return A list with the Id's
     */
    public List<String> getAllSubscriptionIds()
    {
        ArrayList<String> ids = new ArrayList<String>();

        if (connect())
        {
            for (String key : redis.keys(virtualSystemIdKey("*")))
            {
                ids.add(redis.get(key));
            }

            disconnect();
        }

        return ids;
    }
}
