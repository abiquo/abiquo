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

import java.util.List;
import java.util.UUID;

import org.testng.AssertJUnit;
import org.testng.annotations.Test;

import com.abiquo.commons.amqp.impl.vsm.domain.VirtualSystemEvent;
import com.abiquo.vsm.events.VMEventType;
import com.abiquo.vsm.model.PhysicalMachine;
import com.abiquo.vsm.model.VirtualMachine;
import com.abiquo.vsm.monitor.Monitor.Type;

/**
 * Base class for unit tests on Notifiers {@link GenericNotifier}.
 * 
 * @author eruiz@abiquo.com
 * @param <T> The notifier type to test.
 */
public abstract class TestNotifierBase<T extends GenericNotifier>
{
    protected abstract T getNotifierInstance();

    @Test
    public void test_mustNotifyEvents()
    {
        VirtualMachine vm = new VirtualMachine();
        PhysicalMachine machine = new PhysicalMachine();

        machine.setId(0);
        machine.setAddress("http://10.30.1.203:443/");
        machine.setUsername("");
        machine.setPassword("");
        machine.setType(Type.VMX_04.name());

        vm.setId(0);
        vm.setLastKnownState(null);
        vm.setName("ABQ_" + UUID.randomUUID().toString());
        vm.setPhysicalMachine(machine);

        List<VirtualSystemEvent> notifications;
        T notifier = getNotifierInstance();
        VMEventType event;

        // POWER_ON
        event = VMEventType.POWER_ON;
        notifications = notifier.processEvent(vm, machine, event);
        AssertJUnit.assertEquals(notifications.size(), 1);

        // POWER_OFF
        event = VMEventType.POWER_OFF;
        notifications = notifier.processEvent(vm, machine, event);
        AssertJUnit.assertEquals(notifications.size(), 1);

        // PAUSED
        event = VMEventType.PAUSED;
        notifications = notifier.processEvent(vm, machine, event);
        AssertJUnit.assertEquals(notifications.size(), 1);

        // RESUMED
        event = VMEventType.RESUMED;
        notifications = notifier.processEvent(vm, machine, event);
        AssertJUnit.assertEquals(notifications.size(), 1);

        // DESTROYED
        event = VMEventType.DESTROYED;
        notifications = notifier.processEvent(vm, machine, event);
        AssertJUnit.assertEquals(notifications.size(), 1);
    }

    @Test
    public void test_mustIgnoreEvents()
    {
        VirtualMachine vm = new VirtualMachine();
        PhysicalMachine machine = new PhysicalMachine();

        machine.setId(0);
        machine.setAddress("http://10.30.1.203:443/");
        machine.setUsername("");
        machine.setPassword("");
        machine.setType(Type.VMX_04.name());

        vm.setId(0);
        vm.setLastKnownState("");
        vm.setName("ABQ_" + UUID.randomUUID().toString());
        vm.setPhysicalMachine(machine);

        List<VirtualSystemEvent> notifications;
        T notifier = getNotifierInstance();
        VMEventType event;

        // SAVED
        event = VMEventType.SAVED;
        notifications = notifier.processEvent(vm, machine, event);
        AssertJUnit.assertTrue(notifications.isEmpty());

        // UNKNOWN
        event = VMEventType.UNKNOWN;
        notifications = notifier.processEvent(vm, machine, event);
        AssertJUnit.assertTrue(notifications.isEmpty());

        // CREATED
        event = VMEventType.CREATED;
        notifications = notifier.processEvent(vm, machine, event);
        AssertJUnit.assertTrue(notifications.isEmpty());
    }

    @Test
    public void testAlreadyNotifieds()
    {
        VirtualMachine vm = new VirtualMachine();
        PhysicalMachine machine = new PhysicalMachine();

        machine.setId(0);
        machine.setAddress("http://10.30.1.203:443/");
        machine.setUsername("");
        machine.setPassword("");
        machine.setType(Type.VMX_04.name());

        vm.setId(0);
        vm.setLastKnownState("");
        vm.setName("ABQ_" + UUID.randomUUID().toString());
        vm.setPhysicalMachine(machine);

        List<VirtualSystemEvent> notifications;
        T notifier = getNotifierInstance();
        VMEventType event;

        // POWER_ON
        event = VMEventType.POWER_ON;
        vm.setLastKnownState(event.name());
        notifications = notifier.processEvent(vm, machine, event);
        AssertJUnit.assertTrue(notifications.isEmpty());

        // POWER_OFF
        event = VMEventType.POWER_OFF;
        vm.setLastKnownState(event.name());
        notifications = notifier.processEvent(vm, machine, event);
        AssertJUnit.assertTrue(notifications.isEmpty());

        // PAUSED
        event = VMEventType.PAUSED;
        vm.setLastKnownState(event.name());
        notifications = notifier.processEvent(vm, machine, event);
        AssertJUnit.assertTrue(notifications.isEmpty());

        // RESUMED
        event = VMEventType.RESUMED;
        vm.setLastKnownState(event.name());
        notifications = notifier.processEvent(vm, machine, event);
        AssertJUnit.assertTrue(notifications.isEmpty());

        // DESTROYED
        event = VMEventType.DESTROYED;
        vm.setLastKnownState(event.name());
        notifications = notifier.processEvent(vm, machine, event);
        AssertJUnit.assertTrue(notifications.isEmpty());
    }

    protected boolean containsMovedEvent(final List<VirtualSystemEvent> notifications)
    {
        return containsEvent(VMEventType.MOVED, notifications);
    }

    protected boolean containsEvent(VMEventType event, final List<VirtualSystemEvent> notifications)
    {
        for (VirtualSystemEvent notification : notifications)
        {
            if (notification.getEventType().equalsIgnoreCase(event.name()))
            {
                return true;
            }
        }

        return false;
    }
}
