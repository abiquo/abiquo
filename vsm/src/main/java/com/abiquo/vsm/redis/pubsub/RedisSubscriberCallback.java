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

package com.abiquo.vsm.redis.pubsub;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import redis.clients.jedis.JedisPubSub;

import com.abiquo.commons.amqp.impl.vsm.VSMProducer;
import com.abiquo.commons.amqp.impl.vsm.domain.VirtualSystemEvent;
import com.abiquo.commons.amqp.util.RabbitMQUtils;
import com.abiquo.vsm.events.VMEventType;
import com.abiquo.vsm.model.PhysicalMachine;
import com.abiquo.vsm.model.VirtualMachine;
import com.abiquo.vsm.monitor.Monitor.Type;
import com.abiquo.vsm.redis.dao.RedisDao;
import com.abiquo.vsm.redis.dao.RedisDaoFactory;
import com.abiquo.vsm.redis.pubsub.notifier.GenericNotifier;
import com.abiquo.vsm.redis.pubsub.notifier.impl.ESXiNotifier;
import com.abiquo.vsm.redis.pubsub.notifier.impl.HyperVNotifier;
import com.abiquo.vsm.redis.pubsub.notifier.impl.LibvirtNotifier;
import com.abiquo.vsm.redis.pubsub.notifier.impl.VirtualBoxNotifier;
import com.abiquo.vsm.redis.pubsub.notifier.impl.XenServerNotifier;

/**
 * Responsible to process and send each published event.
 * 
 * @see http://code.google.com/p/redis/wiki/PublishSubscribe
 * @see RedisSubscriberCallback
 * @see RedisPublisher.publishEvent
 * @see GenericNotifier
 * @author eruiz@abiquo.com
 */
public class RedisSubscriberCallback extends JedisPubSub
{
    private final static Logger logger = LoggerFactory.getLogger(RedisSubscriberCallback.class);

    private VSMProducer broker;

    private RedisDao dao;

    private Map<String, GenericNotifier> notifiers;

    public RedisSubscriberCallback(String host, int port)
    {
        // RabbitMQ producer
        broker = new VSMProducer();

        // DAO for redis persistence
        dao = RedisDaoFactory.getInstance();

        // Instance all available types of notifiers
        notifiers = new HashMap<String, GenericNotifier>();

        notifiers.put(Type.VMX_04.name(), new ESXiNotifier());
        notifiers.put(Type.HYPERV_301.name(), new HyperVNotifier());
        notifiers.put(Type.XENSERVER.name(), new XenServerNotifier());
        notifiers.put(Type.KVM.name(), new LibvirtNotifier());
        notifiers.put(Type.XEN_3.name(), new LibvirtNotifier());
        notifiers.put(Type.VBOX.name(), new VirtualBoxNotifier());
    }

    /**
     * Called when an event is published in redis channel.
     * 
     * @param channel The channel name.
     * @param message The 'serialized' notification info.
     */
    @Override
    public void onMessage(String channel, String message)
    {
        String fields[] = getMessageFields(message);

        if (fields != null)
        {
            String virtualMachineName = fields[0];
            String eventName = fields[1];
            String physicalMachineAddress = fields[2];

            VirtualMachine virtualMachine = dao.findVirtualMachineByName(virtualMachineName);
            PhysicalMachine machine = dao.findPhysicalMachineByAddress(physicalMachineAddress);
            VMEventType event = VMEventType.valueOf(eventName);

            if (virtualMachine != null && virtualMachine.getPhysicalMachine() != null
                && machine != null)
            {
                notifyEvent(virtualMachine, machine, event);
            }
            else
            {
                if (virtualMachine == null)
                {
                    logger.trace("Unable to find a virtual machine with name, {}.",
                        virtualMachineName);
                }
                else if (virtualMachine.getPhysicalMachine() == null)
                {
                    logger
                        .trace(
                            "Unable to find the physical machine {} referenced by virtual machine with name, {}.",
                            physicalMachineAddress, virtualMachineName);
                }
                else if (machine == null)
                {
                    logger.trace("Unable to find a physical machine with address, {}.",
                        physicalMachineAddress);
                }
            }
        }
        else
        {
            logger.trace("Invalid message (skipped): {}", message);
        }
    }

    private String[] getMessageFields(final String message)
    {
        String fields[] = message.split(RedisPublisher.RegexSeparator);

        if (fields.length != 3)
        {
            logger.error("Malformed message {}", message);
            return null;
        }

        return fields;
    }

    /**
     * Process the event, update Redis database and send the notifications to RabbitMQ broker.
     * 
     * @param virtualMachine Virtual machine affected.
     * @param machine Where the event was produced.
     * @param event The produced event.
     */
    private VirtualMachine notifyEvent(VirtualMachine virtualMachine,
        final PhysicalMachine machine, final VMEventType event)
    {
        GenericNotifier notifier = notifiers.get(machine.getType());

        if (notifier == null)
        {
            logger.error("Unknown type {} for the physical machine {}", machine.getType(), machine
                .getAddress());
            return virtualMachine;
        }

        List<VirtualSystemEvent> notifications =
            notifier.processEvent(virtualMachine, machine, event);

        if (notifications.isEmpty())
        {
            // There are no events to notify
            return virtualMachine;
        }

        if (enqueueNotifications(notifications))
        {
            VirtualSystemEvent last = notifications.get(notifications.size() - 1);

            virtualMachine.setLastKnownState(last.getEventType());

            if (containsMovedEvent(notifications))
            {
                // Update the PhysicalMachine where the VirtualMachine is.
                virtualMachine.setPhysicalMachine(machine);
            }

            virtualMachine = dao.save(virtualMachine);
        }

        return virtualMachine;
    }

    private boolean containsMovedEvent(final List<VirtualSystemEvent> notifications)
    {
        for (VirtualSystemEvent notification : notifications)
        {
            if (notification.getEventType().equals(VMEventType.MOVED.name()))
            {
                return true;
            }
        }

        return false;
    }

    private boolean enqueueNotifications(final List<VirtualSystemEvent> notifications)
    {
        try
        {
            broker.openChannel();

            for (VirtualSystemEvent n : notifications)
            {
                broker.publish(n);

                logger.info("Published a {} event.", n.getEventType());
                logger.info("\tVirtual machine name: {}", n.getVirtualSystemId());
                logger.info("\tPhysical machine address: {}", n.getVirtualSystemAddress());
                logger.info("\tPhysical machine type: {}", n.getVirtualSystemType());
            }

            broker.closeChannel();

            return true;
        }
        catch (IOException e)
        {
            logger
                .error("The send of the following notifications may has failed. RabbitMQ is unreachable.");

            for (VirtualSystemEvent n : notifications)
            {
                logger.error("{} event on.", n.getEventType());
                logger.error("\tVirtual machine name: {}", n.getVirtualSystemId());
                logger.error("\tPhysical machine address: {}", n.getVirtualSystemAddress());
                logger.error("\tPhysical machine type: {}", n.getVirtualSystemType());
            }

            if (!RabbitMQUtils.pingRabbitMQ())
            {
                logger
                    .error("RabbitMQ is not alive, unsubscribing in order to stop the event consuming.");

                unsubscribe();
            }

            return false;
        }
    }

    @Override
    public void onPMessage(String arg0, String arg1, String arg2)
    {
        // Auto-generated method stub
    }

    @Override
    public void onPSubscribe(String arg0, int arg1)
    {
        // Auto-generated method stub
    }

    @Override
    public void onPUnsubscribe(String arg0, int arg1)
    {
        // Auto-generated method stub
    }

    @Override
    public void onSubscribe(String channel, int arg)
    {
        logger.info("VSM subscribed to redis {} channel", channel);
    }

    @Override
    public void onUnsubscribe(String channel, int arg)
    {
        logger.info("VSM unsubscribed to redis {} channel", channel);
    }
}
