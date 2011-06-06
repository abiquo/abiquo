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

package com.abiquo.vsm.redis.pubsub.notifier.impl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.abiquo.commons.amqp.impl.vsm.domain.VirtualSystemEvent;
import com.abiquo.vsm.events.VMEventType;
import com.abiquo.vsm.model.PhysicalMachine;
import com.abiquo.vsm.model.VirtualMachine;
import com.abiquo.vsm.redis.pubsub.notifier.GenericNotifier;

/**
 * Concrete implementation of {@link GenericNotifier} for the KVM hypervisor.
 * 
 * @author eruiz@abiquo.com
 */
public class KVMNotifier extends GenericNotifier
{
    private final static Logger logger = LoggerFactory.getLogger(KVMNotifier.class);

    /** Events that must generate a MOVED when the machine origin is not the same */
    private final Set<VMEventType> mustNotifyMovement;

    /** Valid events to notify */
    private final Set<VMEventType> validEvents;

    public KVMNotifier()
    {
        mustNotifyMovement = new HashSet<VMEventType>();
        mustNotifyMovement.add(VMEventType.RESUMED);
        mustNotifyMovement.add(VMEventType.POWER_ON);

        validEvents = new HashSet<VMEventType>();
        validEvents.add(VMEventType.POWER_ON);
        validEvents.add(VMEventType.POWER_OFF);
        validEvents.add(VMEventType.PAUSED);
        validEvents.add(VMEventType.RESUMED);
        validEvents.add(VMEventType.DESTROYED);
    }

    @Override
    public List<VirtualSystemEvent> processEvent(final VirtualMachine virtualMachine,
        final PhysicalMachine machine, final VMEventType event)
    {
        logger.trace(String.format("Processing %s %s event from machine %s",
            virtualMachine.getName(), event.name(), machine.getAddress()));

        if (samePhysicalMachineAddress(virtualMachine.getPhysicalMachine(), machine))
        {
            if (validEvents.contains(event) && !alreadyNotified(virtualMachine, event))
            {
                return Collections.singletonList(buildVirtualSystemEvent(virtualMachine, event));
            }
        }
        else if (mustNotifyMovement.contains(event))
        {
            return buildMovementNotifications(virtualMachine, machine, event);
        }

        return Collections.emptyList();
    }

    protected List<VirtualSystemEvent> buildMovementNotifications(VirtualMachine virtualMachine,
        final PhysicalMachine machine, final VMEventType event)
    {
        List<VirtualSystemEvent> notifications = new ArrayList<VirtualSystemEvent>();

        notifications.add(buildVirtualSystemEvent(virtualMachine, machine.getAddress(),
            VMEventType.MOVED));

        notifications.add(buildVirtualSystemEvent(virtualMachine, machine.getAddress(),
            VMEventType.POWER_ON));

        return notifications;
    }
}
