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
 * Concrete tests for KVMNotifier. Mainly related with virtual machine movement.
 * 
 * @author eruiz@abiquo.com
 */
public class TestKVMNotifier extends TestNotifierBase<KVMNotifier>
{
    protected KVMNotifier notifier = null;

    @Override
    protected KVMNotifier getNotifierInstance()
    {
        if (notifier == null)
        {
            notifier = new KVMNotifier();
        }

        return notifier;
    }

    @Test
    public void test_migration()
    {
        VirtualMachine vm = new VirtualMachine();
        PhysicalMachine pm0 = new PhysicalMachine();
        PhysicalMachine pm1 = new PhysicalMachine();

        pm0.setId(0);
        pm0.setAddress("http://10.30.1.203:443/");
        pm0.setUsername("");
        pm0.setPassword("");
        pm0.setType(Type.KVM.name());

        pm1.setId(1);
        pm1.setAddress("http://10.30.1.205:443/");
        pm1.setUsername("");
        pm1.setPassword("");
        pm1.setType(Type.KVM.name());

        vm.setId(0);
        vm.setLastKnownState(null);
        vm.setName("ABQ_" + UUID.randomUUID().toString());
        vm.setPhysicalMachine(pm0);

        List<VirtualSystemEvent> notifications;

        // Live migration
        notifications = notifier.processEvent(vm, pm1, VMEventType.RESUMED);
        AssertJUnit.assertEquals(notifications.size(), 2);
        AssertJUnit.assertTrue(containsMovedEvent(notifications));
        AssertJUnit.assertTrue(containsEvent(VMEventType.POWER_ON, notifications));

        vm.setPhysicalMachine(pm1);
        notifications = notifier.processEvent(vm, pm0, VMEventType.POWER_OFF);
        AssertJUnit.assertTrue(notifications.isEmpty());

        vm.setPhysicalMachine(pm1);
        notifications = notifier.processEvent(vm, pm1, VMEventType.CREATED);
        AssertJUnit.assertTrue(notifications.isEmpty());

        // Migration
        vm.setPhysicalMachine(pm0);

        notifications = notifier.processEvent(vm, pm1, VMEventType.POWER_ON);
        AssertJUnit.assertEquals(notifications.size(), 2);
        AssertJUnit.assertTrue(containsMovedEvent(notifications));
        AssertJUnit.assertTrue(containsEvent(VMEventType.POWER_ON, notifications));

        vm.setPhysicalMachine(pm1);
        notifications = notifier.processEvent(vm, pm0, VMEventType.POWER_OFF);
        AssertJUnit.assertTrue(notifications.isEmpty());

        vm.setPhysicalMachine(pm1);
        notifications = notifier.processEvent(vm, pm1, VMEventType.CREATED);
        AssertJUnit.assertTrue(notifications.isEmpty());

        // Non-movement
        vm.setPhysicalMachine(pm0);

        notifications = notifier.processEvent(vm, pm1, VMEventType.POWER_OFF);
        AssertJUnit.assertFalse(containsMovedEvent(notifications));

        notifications = notifier.processEvent(vm, pm1, VMEventType.PAUSED);
        AssertJUnit.assertFalse(containsMovedEvent(notifications));

        notifications = notifier.processEvent(vm, pm1, VMEventType.CREATED);
        AssertJUnit.assertFalse(containsMovedEvent(notifications));
    }
}
