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
package com.abiquo.vsm.monitor;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;
import static org.testng.Assert.assertNull;

import java.util.UUID;

import org.testng.annotations.Test;

import com.abiquo.vsm.TestBase;
import com.abiquo.vsm.events.VMEventType;
import com.abiquo.vsm.exception.MonitorException;
import com.abiquo.vsm.model.PhysicalMachine;
import com.abiquo.vsm.model.VirtualMachine;
import com.abiquo.vsm.monitor.MockMonitor.InfiniteMachineMonitor;
import com.abiquo.vsm.monitor.MockMonitor.MulipleMachineMonitor;
import com.abiquo.vsm.monitor.MockMonitor.SingleMachineMonitor;
import com.abiquo.vsm.monitor.Monitor.Type;

/**
 * Unit tests for the {@link MonitorManager} class.
 * 
 * @author ibarrera
 */
public class MonitorManagerTest extends TestBase
{
    /** The monitor manager to test. */
    private MockMonitorManager monitorManager;

    @Override
    public void setUp() throws Exception
    {
        // Create the manager and load the monitors
        monitorManager = new MockMonitorManager();
    }

    @Test
    public void test_createMonitor() throws Exception
    {
        AbstractMonitor monitor1 = monitorManager.createMonitor(SingleMachineMonitor.TYPE);
        AbstractMonitor monitor2 = monitorManager.createMonitor(MulipleMachineMonitor.TYPE);
        AbstractMonitor monitor3 = monitorManager.createMonitor(InfiniteMachineMonitor.TYPE);

        assertNotNull(monitor1);
        assertNotNull(monitor2);
        assertNotNull(monitor3);

        assertEquals(monitor1.getClass(), SingleMachineMonitor.class);
        assertEquals(monitor2.getClass(), MulipleMachineMonitor.class);
        assertEquals(monitor3.getClass(), InfiniteMachineMonitor.class);
    }

    @Test
    public void test_monitor() throws Exception
    {
        monitor("10.60.1.11", SingleMachineMonitor.TYPE);
        monitor("10.60.1.12", MulipleMachineMonitor.TYPE);
        monitor("10.60.1.13", InfiniteMachineMonitor.TYPE);

        assertNull(monitorManager.findAvailableMonitor(SingleMachineMonitor.TYPE));
        assertNotNull(monitorManager.findAvailableMonitor(MulipleMachineMonitor.TYPE));
        assertNotNull(monitorManager.findAvailableMonitor(InfiniteMachineMonitor.TYPE));
    }

    @Test
    public void test_shutdown() throws Exception
    {
        monitor("10.60.1.11", SingleMachineMonitor.TYPE);
        monitor("10.60.1.12", SingleMachineMonitor.TYPE);
        monitor("10.60.1.13", MulipleMachineMonitor.TYPE);
        monitor("10.60.1.14", MulipleMachineMonitor.TYPE);
        monitor("10.60.1.15", InfiniteMachineMonitor.TYPE);

        assertNull(monitorManager.findAvailableMonitor(SingleMachineMonitor.TYPE));
        assertNotNull(monitorManager.findAvailableMonitor(MulipleMachineMonitor.TYPE));
        assertNotNull(monitorManager.findAvailableMonitor(InfiniteMachineMonitor.TYPE));

        monitorManager.shutdown("10.60.1.11", SingleMachineMonitor.TYPE);
        monitorManager.shutdown("10.60.1.13", MulipleMachineMonitor.TYPE);
        monitorManager.shutdown("10.60.1.15", InfiniteMachineMonitor.TYPE);

        // Verify running monitors
        assertNull(monitorManager.findRunningMonitor("10.60.1.11", SingleMachineMonitor.TYPE));
        assertNull(monitorManager.findRunningMonitor("10.60.1.13", MulipleMachineMonitor.TYPE));
        assertNull(monitorManager.findRunningMonitor("10.60.1.15", InfiniteMachineMonitor.TYPE));
        assertNotNull(monitorManager.findRunningMonitor("10.60.1.12", SingleMachineMonitor.TYPE));
        assertNotNull(monitorManager.findRunningMonitor("10.60.1.14", MulipleMachineMonitor.TYPE));

        // Verify available monitors
        assertNull(monitorManager.findAvailableMonitor(SingleMachineMonitor.TYPE));
        assertNotNull(monitorManager.findAvailableMonitor(MulipleMachineMonitor.TYPE));
        assertNull(monitorManager.findAvailableMonitor(InfiniteMachineMonitor.TYPE));
    }

    @Test
    public void test_shutdownMultiple() throws Exception
    {
        monitor("10.60.1.10", MulipleMachineMonitor.TYPE);
        monitor("10.60.1.11", MulipleMachineMonitor.TYPE);

        assertNotNull(monitorManager.findAvailableMonitor(MulipleMachineMonitor.TYPE));

        monitorManager.shutdown("10.60.1.10", MulipleMachineMonitor.TYPE);
        monitorManager.shutdown("10.60.1.11", MulipleMachineMonitor.TYPE);

        // Verify running and available monitors
        assertNull(monitorManager.findRunningMonitor("10.60.1.10", MulipleMachineMonitor.TYPE));
        assertNull(monitorManager.findRunningMonitor("10.60.1.11", MulipleMachineMonitor.TYPE));
        assertNull(monitorManager.findAvailableMonitor(MulipleMachineMonitor.TYPE));
    }

    @Test
    public void test_subscribe() throws Exception
    {
        monitor("10.60.1.11", SingleMachineMonitor.TYPE);

        assertNull(monitorManager.findAvailableMonitor(SingleMachineMonitor.TYPE));

        String machineName = UUID.randomUUID().toString();
        monitorManager.subscribe("10.60.1.11", SingleMachineMonitor.TYPE, machineName);

        // Check subscription
        VirtualMachine vm = monitorManager.dao.findVirtualMachineByName(machineName);
        assertNotNull(vm);
        assertNotNull(vm.getPhysicalMachine());
        assertEquals(vm.getLastKnownState(), VMEventType.UNKNOWN.name());
        assertEquals(vm.getPhysicalMachine().getAddress(), "10.60.1.11");
        assertEquals(vm.getPhysicalMachine().getType(), SingleMachineMonitor.TYPE.name());
    }

    @Test(expectedExceptions = MonitorException.class)
    public void test_subscribeUnexistingPhysicalMachine() throws Exception
    {
        String machineName = UUID.randomUUID().toString();
        monitorManager.subscribe("10.60.1.11", SingleMachineMonitor.TYPE, machineName);
    }

    @Test
    public void test_unsubscribe() throws Exception
    {
        monitor("10.60.1.11", SingleMachineMonitor.TYPE);

        assertNull(monitorManager.findAvailableMonitor(SingleMachineMonitor.TYPE));

        String machineName = UUID.randomUUID().toString();
        monitorManager.subscribe("10.60.1.11", SingleMachineMonitor.TYPE, machineName);

        // Check subscription
        VirtualMachine vm = monitorManager.dao.findVirtualMachineByName(machineName);
        assertNotNull(vm);
        assertNotNull(vm.getPhysicalMachine());
        assertEquals(vm.getLastKnownState(), VMEventType.UNKNOWN.name());
        assertEquals(vm.getPhysicalMachine().getAddress(), "10.60.1.11");
        assertEquals(vm.getPhysicalMachine().getType(), SingleMachineMonitor.TYPE.name());

        monitorManager.unsubscribe("10.60.1.11", SingleMachineMonitor.TYPE, machineName);

        // Check unsubscription
        assertNull(monitorManager.dao.findVirtualMachineByName(machineName));
    }

    @Test(expectedExceptions = MonitorException.class)
    public void test_unsubscribeUnexistingSubscription() throws Exception
    {
        monitor("10.60.1.11", SingleMachineMonitor.TYPE);

        assertNull(monitorManager.findAvailableMonitor(SingleMachineMonitor.TYPE));

        String machineName = UUID.randomUUID().toString();
        monitorManager.subscribe("10.60.1.11", SingleMachineMonitor.TYPE, machineName);

        // Check subscription
        VirtualMachine vm = monitorManager.dao.findVirtualMachineByName(machineName);
        assertNotNull(vm);
        assertNotNull(vm.getPhysicalMachine());
        assertEquals(vm.getLastKnownState(), VMEventType.UNKNOWN.name());
        assertEquals(vm.getPhysicalMachine().getAddress(), "10.60.1.11");
        assertEquals(vm.getPhysicalMachine().getType(), SingleMachineMonitor.TYPE.name());

        monitorManager.unsubscribe("10.60.1.11", SingleMachineMonitor.TYPE, machineName);

        // Check unsubscription
        assertNull(monitorManager.dao.findVirtualMachineByName(machineName));

        // This second unsuibscription should fail
        monitorManager.unsubscribe("10.60.1.11", SingleMachineMonitor.TYPE, machineName);
    }

    @Test(expectedExceptions = MonitorException.class)
    public void test_getStateOfUnexistingMachine() throws Exception
    {
        monitorManager.getState("10.60.1.78", SingleMachineMonitor.TYPE, UUID.randomUUID()
            .toString());
    }

    private PhysicalMachine monitor(String address, Type type) throws Exception
    {
        PhysicalMachine pm = monitorManager.monitor(address, type, "", "");

        assertNotNull(pm);
        assertNotNull(pm.getId());
        assertNotNull(pm.getUsername());
        assertNotNull(pm.getPassword());
        assertEquals(pm.getAddress(), address);
        assertEquals(pm.getType(), type.name());

        assertNotNull(monitorManager.findRunningMonitor(pm.getAddress(), type));

        return pm;
    }
}
