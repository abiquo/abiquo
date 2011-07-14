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

package com.abiquo.vsm.redis.pubsub.notifier;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.abiquo.commons.amqp.impl.vsm.domain.VirtualSystemEvent;
import com.abiquo.vsm.events.VMEventType;
import com.abiquo.vsm.model.PhysicalMachine;
import com.abiquo.vsm.model.VirtualMachine;
import com.abiquo.vsm.redis.pubsub.RedisSubscriberCallback;

/**
 * Abstract class responsible of decide if a received event must be notified. There is a concrete
 * implementation of GenericNotifier class for each hypervisor type.
 * 
 * @see RedisSubscriberCallback
 * @author eruiz@abiquo.com
 */
public abstract class GenericNotifier
{
    private final static Logger logger = LoggerFactory.getLogger(GenericNotifier.class);

    /**
     * Process a received event and returns a collection of events to notify.
     * 
     * @param virtualMachine VirtualMachine that generated the event.
     * @param machine PhysicalMachine where the event was produced.
     * @param event The produced event.
     * @return A list of events to send.
     */
    public List<VirtualSystemEvent> processEvent(final VirtualMachine virtualMachine,
        final PhysicalMachine machine, final VMEventType event)
    {
        logger.trace(String.format("Processing %s %s event from machine %s",
            virtualMachine.getName(), event.name(), machine.getAddress()));

        // Deduce if a MOVED event must be notified
        List<VirtualSystemEvent> notifications = deduceMoveEvent(virtualMachine, machine, event);

        // The machine address where the event have been produced
        String machineAddress = virtualMachine.getPhysicalMachine().getAddress();

        if (!notifications.isEmpty())
        {
            // Virtual machine has been moved to other hypervisor
            machineAddress = machine.getAddress();
        }

        // Process the original event
        switch (event)
        {
            case POWER_ON:
            case POWER_OFF:
            case PAUSED:
            case RESUMED:
            case DESTROYED:

                if (!alreadyNotified(virtualMachine, event))
                {
                    notifications
                        .add(buildVirtualSystemEvent(virtualMachine, machineAddress, event));
                }

                break;

            default:
                break;
        }

        return notifications;
    }

    /**
     * Given the virtual machine that generated the event, the physical machine where the event was
     * produced and the event himself, decides if a MOVED event must be notified.
     * 
     * @param virtualMachine VirtualMachine that generated the event.
     * @param machine PhysicalMachine where the event was produced.
     * @param event The produced event.
     * @return
     */
    protected List<VirtualSystemEvent> deduceMoveEvent(VirtualMachine virtualMachine,
        final PhysicalMachine machine, final VMEventType event)
    {
        List<VirtualSystemEvent> notifications = new ArrayList<VirtualSystemEvent>();

        if (!virtualMachine.getPhysicalMachine().getId().equals(machine.getId()))
        {
            // Build and add a new MOVED notification
            notifications.add(buildVirtualSystemEvent(virtualMachine, machine.getAddress(),
                VMEventType.MOVED));
        }

        return notifications;
    }

    /**
     * Returns true if the event must be notified.
     * 
     * @param virtualMachine VirtualMachine that generated the event.
     * @param event The produced event.
     * @return True if the event must be notified. Otherwise false.
     */
    protected boolean alreadyNotified(final VirtualMachine virtualMachine, final VMEventType event)
    {
        logger.trace(String.format("Last known state for %s is %s.", virtualMachine.getName(),
            virtualMachine.getLastKnownState()));

        if (virtualMachine.getLastKnownState() == null)
        {
            // Unknown last state
            return false;
        }

        // Return true if current event is equals to the last event notified
        return virtualMachine.getLastKnownState().equalsIgnoreCase(event.name());
    }

    /**
     * Builds a VirtualSystemEvent instance.
     * 
     * @param virtualMachine VirtualMachine that generated the event.
     * @param physicalMachineAddress The address of the PhysicalMachine to include.
     * @param event The produced event.
     * @return The new instance.
     */
    protected VirtualSystemEvent buildVirtualSystemEvent(final VirtualMachine virtualMachine,
        final String physicalMachineAddress, final VMEventType event)
    {
        VirtualSystemEvent notification = new VirtualSystemEvent();

        notification.setVirtualSystemAddress(physicalMachineAddress);
        notification.setVirtualSystemType(virtualMachine.getPhysicalMachine().getType());
        notification.setVirtualSystemId(virtualMachine.getName());
        notification.setEventType(event.name());

        return notification;
    }

    /**
     * Builds a VirtualSystemEvent instance.
     * 
     * @param virtualMachine VirtualMachine that generated the event.
     * @param event The produced event.
     * @return The new instance.
     */
    protected VirtualSystemEvent buildVirtualSystemEvent(final VirtualMachine virtualMachine,
        final VMEventType event)
    {
        VirtualSystemEvent notification = new VirtualSystemEvent();

        notification.setVirtualSystemAddress(virtualMachine.getPhysicalMachine().getAddress());
        notification.setVirtualSystemType(virtualMachine.getPhysicalMachine().getType());
        notification.setVirtualSystemId(virtualMachine.getName());
        notification.setEventType(event.name());

        return notification;
    }

    /**
     * Checks if a two physical machines have the same address.
     * 
     * @param one The first machine
     * @param other The second machine
     * @return True if have the same address.
     */
    protected boolean samePhysicalMachineAddress(final PhysicalMachine one,
        final PhysicalMachine other)
    {
        return one.getAddress().equals(other.getAddress());
    }
}
