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

import org.apache.commons.codec.binary.Base64;

import com.abiquo.vsm.MockVSMService;
import com.abiquo.vsm.TestBase;
import com.abiquo.vsm.VSMService;
import com.abiquo.vsm.events.VMEventType;
import com.abiquo.vsm.model.transport.PhysicalMachineDto;
import com.abiquo.vsm.model.transport.VirtualMachineDto;
import com.abiquo.vsm.monitor.Monitor.Type;

/**
 * Base class for all REST resource tests.
 * 
 * @author ibarrera
 */
public class ResourceTestBase extends TestBase
{
    /** The subscription resource. */
    protected SubscriptionResource subsResource;

    /** The physical machine resource. */
    protected PhysicalMachineResource pmResource;

    @Override
    public void setUp()
    {
        VSMService mockVSMService = new MockVSMService();

        pmResource = new PhysicalMachineResource();
        subsResource = new SubscriptionResource();

        subsResource.vsmService = mockVSMService;
        pmResource.vsmService = mockVSMService;
    }

    protected PhysicalMachineDto monitor(String physicalMachineAddress, Type type)
    {
        return monitor(physicalMachineAddress, type, "dummy", "dummy");
    }

    protected PhysicalMachineDto monitor(String physicalMachineAddress, Type type, String username,
        String password)
    {
        PhysicalMachineDto dto = new PhysicalMachineDto();
        dto.setAddress(physicalMachineAddress);
        dto.setType(type.name());

        PhysicalMachineDto pm = pmResource.monitor(dto, toBasicAuth(username, password));

        assertNotNull(pm.getId());
        assertEquals(pm.getAddress(), dto.getAddress());
        assertEquals(pm.getType(), dto.getType());

        return pm;
    }

    protected VirtualMachineDto subscribe(PhysicalMachineDto pm, String virtualMachineName)
    {
        assertNotNull(pm.getId());
        assertNotNull(pm.getAddress());
        assertNotNull(pm.getType());

        VirtualMachineDto dto = new VirtualMachineDto();
        dto.setName(virtualMachineName);
        dto.setPhysicalMachine(pm);

        VirtualMachineDto vm = subsResource.subscribe(dto);

        assertNotNull(vm.getId());
        assertEquals(vm.getName(), dto.getName());
        assertEquals(vm.getLastKnownState(), VMEventType.UNKNOWN.name());
        assertEquals(vm.getPhysicalMachine().getId(), pm.getId());
        assertEquals(vm.getPhysicalMachine().getAddress(), pm.getAddress());
        assertEquals(vm.getPhysicalMachine().getType(), pm.getType());

        return vm;
    }

    protected String toBasicAuth(String username, String password)
    {
        String token = username + ":" + password;
        String encoded = new String(Base64.encodeBase64(token.getBytes()));
        return "Basic " + encoded;
    }
}
