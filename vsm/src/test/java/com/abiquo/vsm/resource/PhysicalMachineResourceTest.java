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

import javax.ws.rs.core.Response.Status;

import org.testng.annotations.Test;

import com.abiquo.vsm.exception.VSMException;
import com.abiquo.vsm.model.transport.PhysicalMachineDto;
import com.abiquo.vsm.model.transport.PhysicalMachinesDto;
import com.abiquo.vsm.monitor.Monitor.Type;

/**
 * Unit tests for the {@link PhysicalMachineResource} class.
 * 
 * @author ibarrera
 */
public class PhysicalMachineResourceTest extends ResourceTestBase
{

    @Test
    public void test_monitor()
    {
        monitor("10.60.1.78", Type.HYPERV_301);
    }

    @Test
    public void test_getAllMonitoredMachines()
    {
        monitor("10.60.1.78", Type.HYPERV_301);
        monitor("10.60.1.73", Type.HYPERV_301);

        PhysicalMachinesDto pms = pmResource.getMonitoredMachines(null);

        assertNotNull(pms);
        assertEquals(pms.getCollection().size(), 2);
    }

    @Test
    public void test_getMonitoredMachine()
    {
        PhysicalMachineDto pm = monitor("10.60.1.78", Type.HYPERV_301);
        monitor("10.60.1.73", Type.HYPERV_301);

        PhysicalMachinesDto pms = pmResource.getMonitoredMachines("10.60.1.78");

        assertNotNull(pms);
        assertEquals(pms.getCollection().size(), 1);

        PhysicalMachineDto found = pms.getCollection().get(0);
        assertNotNull(found.getId());
        assertEquals(found.getId(), pm.getId());
        assertEquals(found.getAddress(), pm.getAddress());
        assertEquals(found.getType(), pm.getType());
    }

    @Test
    public void test_getMonitoredMachineUnexisting()
    {
        try
        {
            pmResource.getMonitoredMachines("10.60.1.78");
            fail("Physical machine with adddress 10.60.1.78 should not exist");
        }
        catch (VSMException ex)
        {
            assertEquals(ex.getResponse().getStatus(), Status.NOT_FOUND.getStatusCode());
        }
    }

    @Test
    public void test_shutdown()
    {
        PhysicalMachineDto pm1 = monitor("10.60.1.78", Type.HYPERV_301);
        PhysicalMachineDto pm2 = monitor("10.60.1.73", Type.HYPERV_301);

        pmResource.shutdown(pm1.getId().toString());

        PhysicalMachinesDto pms = pmResource.getMonitoredMachines(null);

        assertNotNull(pms);
        assertEquals(pms.getCollection().size(), 1);

        PhysicalMachineDto found = pms.getCollection().get(0);
        assertNotNull(found.getId());
        assertEquals(found.getId(), pm2.getId());
        assertEquals(found.getAddress(), pm2.getAddress());
        assertEquals(found.getType(), pm2.getType());
    }

    @Test
    public void test_shutdownUnexisting()
    {
        try
        {
            pmResource.shutdown("15");
            fail("Physical machine with id 15 should not exist");
        }
        catch (VSMException ex)
        {
            assertEquals(ex.getResponse().getStatus(), Status.NOT_FOUND.getStatusCode());
        }
    }

}
