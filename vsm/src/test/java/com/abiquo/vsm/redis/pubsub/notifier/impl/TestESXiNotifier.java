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

import java.util.List;
import java.util.UUID;

import org.testng.AssertJUnit;
import org.testng.annotations.Test;

import com.abiquo.commons.amqp.impl.vsm.domain.VirtualSystemEvent;
import com.abiquo.vsm.events.VMEventType;
import com.abiquo.vsm.model.PhysicalMachine;
import com.abiquo.vsm.model.VirtualMachine;
import com.abiquo.vsm.monitor.Monitor.Type;
import com.abiquo.vsm.redis.pubsub.notifier.TestNotifierBase;

/**
 * Concrete tests for ESXiNotifier. Mainly related with virtual machine movement.
 * 
 * @author eruiz@abiquo.com
 */
public class TestESXiNotifier extends TestNotifierBase<ESXiNotifier>
{
    protected ESXiNotifier notifier = null;

    @Override
    protected ESXiNotifier getNotifierInstance()
    {
        if (notifier == null)
        {
            notifier = new ESXiNotifier();
        }

        return notifier;
    }

    @Test
    public void test_move()
    {
        VirtualMachine vm = new VirtualMachine();
        PhysicalMachine pm0 = new PhysicalMachine();
        PhysicalMachine pm1 = new PhysicalMachine();

        pm0.setId(0);
        pm0.setAddress("http://10.30.1.203:443/");
        pm0.setUsername("");
        pm0.setPassword("");
        pm0.setType(Type.VMX_04.name());

        pm1.setId(1);
        pm1.setAddress("http://10.30.1.205:443/");
        pm1.setUsername("");
        pm1.setPassword("");
        pm1.setType(Type.VMX_04.name());

        vm.setId(0);
        vm.setLastKnownState(null);
        vm.setName("ABQ_" + UUID.randomUUID().toString());
        vm.setPhysicalMachine(pm0);

        List<VirtualSystemEvent> notifications;

        // Should notify a CREATED event if the vm.getPhysicalMachine is different that the
        // machine where the event was produced.
        notifications = notifier.processEvent(vm, pm1, VMEventType.CREATED);
        AssertJUnit.assertEquals(notifications.size(), 1);
        AssertJUnit.assertTrue(containsMovedEvent(notifications));

        // No notifications of a DESTROYED event if the vm.getPhysicalMachine is different that the
        // machine where the event was produced.
        vm.setPhysicalMachine(pm1);
        notifications = notifier.processEvent(vm, pm0, VMEventType.DESTROYED);
        AssertJUnit.assertTrue(notifications.isEmpty());

        // Should notify a DESTROYED event if the vm.getPhysicalMachine is the same that produced
        // the event.
        vm.setPhysicalMachine(pm0);
        notifications = notifier.processEvent(vm, pm0, VMEventType.DESTROYED);
        AssertJUnit.assertFalse(containsMovedEvent(notifications));

        // Only detects movement with CREATED events.
        vm.setPhysicalMachine(pm0);
        notifications = notifier.processEvent(vm, pm1, VMEventType.POWER_ON);
        AssertJUnit.assertFalse(containsMovedEvent(notifications));

        notifications = notifier.processEvent(vm, pm1, VMEventType.POWER_OFF);
        AssertJUnit.assertFalse(containsMovedEvent(notifications));

        notifications = notifier.processEvent(vm, pm1, VMEventType.PAUSED);
        AssertJUnit.assertFalse(containsMovedEvent(notifications));

        notifications = notifier.processEvent(vm, pm1, VMEventType.RESUMED);
        AssertJUnit.assertFalse(containsMovedEvent(notifications));
    }

    @Test
    public void test_invalidEvents()
    {
        VirtualMachine vm = new VirtualMachine();
        PhysicalMachine pm0 = new PhysicalMachine();
        PhysicalMachine pm1 = new PhysicalMachine();

        pm0.setId(0);
        pm0.setAddress("http://10.30.1.203:443/");
        pm0.setUsername("");
        pm0.setPassword("");
        pm0.setType(Type.VMX_04.name());

        pm1.setId(1);
        pm1.setAddress("http://10.30.1.205:443/");
        pm1.setUsername("");
        pm1.setPassword("");
        pm1.setType(Type.VMX_04.name());

        vm.setId(0);
        vm.setLastKnownState(null);
        vm.setName("ABQ_" + UUID.randomUUID().toString());
        vm.setPhysicalMachine(pm0);

        List<VirtualSystemEvent> notifications;

        // Invalid event when vm.getPhysicalMachine is not the same that generated the current
        // event.
        notifications = notifier.processEvent(vm, pm1, VMEventType.POWER_ON);
        AssertJUnit.assertTrue(notifications.isEmpty());

        notifications = notifier.processEvent(vm, pm1, VMEventType.POWER_OFF);
        AssertJUnit.assertTrue(notifications.isEmpty());

        notifications = notifier.processEvent(vm, pm1, VMEventType.PAUSED);
        AssertJUnit.assertTrue(notifications.isEmpty());

        notifications = notifier.processEvent(vm, pm1, VMEventType.RESUMED);
        AssertJUnit.assertTrue(notifications.isEmpty());
    }
}
