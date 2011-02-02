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
package com.abiquo.vsm.resource;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;
import static org.testng.Assert.fail;

import java.util.UUID;

import javax.ws.rs.core.Response.Status;

import org.testng.annotations.Test;

import com.abiquo.vsm.exception.VSMException;
import com.abiquo.vsm.model.transport.PhysicalMachineDto;
import com.abiquo.vsm.model.transport.VirtualMachineDto;
import com.abiquo.vsm.model.transport.VirtualMachinesDto;
import com.abiquo.vsm.monitor.Monitor.Type;

/**
 * Unit tests for the {@link PhysicalMachineResource} class.
 * 
 * @author ibarrera
 */
public class SubscriptionResourceTest extends ResourceTestBase
{

    @Test
    public void test_subscribe()
    {
        PhysicalMachineDto pm = monitor("10.60.1.78", Type.HYPERV_301);
        subscribe(pm, UUID.randomUUID().toString());
    }

    @Test
    public void test_subscribeInvalidPhysicalMachine()
    {
        monitor("10.60.1.78", Type.HYPERV_301);

        PhysicalMachineDto unexisting = new PhysicalMachineDto();
        unexisting.setId(23);
        unexisting.setAddress("10.60.1.100");
        unexisting.setType(Type.HYPERV_301.name());

        try
        {
            subscribe(unexisting, UUID.randomUUID().toString());
            fail("Physical machine " + unexisting.getAddress() + " should not exist");
        }
        catch (VSMException ex)
        {
            assertEquals(ex.getResponse().getStatus(), Status.INTERNAL_SERVER_ERROR.getStatusCode());
        }
    }

    @Test
    public void test_getAllSubscriptions()
    {
        PhysicalMachineDto pm = monitor("10.60.1.78", Type.HYPERV_301);
        subscribe(pm, UUID.randomUUID().toString());
        subscribe(pm, UUID.randomUUID().toString());

        VirtualMachinesDto vms = subsResource.getSubscriptions(null);

        assertNotNull(vms);
        assertEquals(vms.getCollection().size(), 2);
    }

    @Test
    public void test_getSubscription()
    {
        PhysicalMachineDto pm = monitor("10.60.1.78", Type.HYPERV_301);
        VirtualMachineDto vm = subscribe(pm, "vm-to-find");
        subscribe(pm, UUID.randomUUID().toString());

        VirtualMachinesDto vms = subsResource.getSubscriptions("vm-to-find");

        assertNotNull(vms);
        assertEquals(vms.getCollection().size(), 1);

        VirtualMachineDto found = vms.getCollection().get(0);
        assertNotNull(found.getId());
        assertEquals(found.getId(), vm.getId());
        assertEquals(found.getName(), vm.getName());
        assertEquals(found.getPhysicalMachine().getId(), pm.getId());
        assertEquals(found.getPhysicalMachine().getAddress(), pm.getAddress());
        assertEquals(found.getPhysicalMachine().getType(), pm.getType());
    }

    @Test
    public void test_getSubscriptionUnexisting()
    {
        try
        {
            subsResource.getSubscriptions("vm-to-find");
            fail("Subscription for virtual machine 'vm-to-find' should not exist");
        }
        catch (VSMException ex)
        {
            assertEquals(ex.getResponse().getStatus(), Status.NOT_FOUND.getStatusCode());
        }
    }

    @Test
    public void test_unsubscribe()
    {
        PhysicalMachineDto pm = monitor("10.60.1.78", Type.HYPERV_301);
        VirtualMachineDto vm1 = subscribe(pm, UUID.randomUUID().toString());
        VirtualMachineDto vm2 = subscribe(pm, UUID.randomUUID().toString());

        subsResource.unsubscribe(vm1.getId().toString());

        VirtualMachinesDto vms = subsResource.getSubscriptions(null);

        assertNotNull(vms);
        assertEquals(vms.getCollection().size(), 1);

        VirtualMachineDto found = vms.getCollection().get(0);
        assertNotNull(found.getId());
        assertEquals(found.getId(), vm2.getId());
        assertEquals(found.getName(), vm2.getName());
        assertEquals(found.getPhysicalMachine().getId(), pm.getId());
        assertEquals(found.getPhysicalMachine().getAddress(), pm.getAddress());
        assertEquals(found.getPhysicalMachine().getType(), pm.getType());
    }

    @Test
    public void test_unsubscribeUnexisting()
    {
        try
        {
            subsResource.unsubscribe("15");
            fail("Physical machine with id 'vm-unexisting' should not exist");
        }
        catch (VSMException ex)
        {
            assertEquals(ex.getResponse().getStatus(), Status.NOT_FOUND.getStatusCode());
        }
    }

}
