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

package com.abiquo.api.resources.cloud;

import static com.abiquo.api.common.UriTestResolver.resolveEnterpriseURI;
import static com.abiquo.api.common.UriTestResolver.resolveVirtualMachinesURI;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import javax.ws.rs.core.Response.Status;

import org.apache.wink.client.ClientResponse;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import com.abiquo.api.resources.AbstractJpaGeneratorIT;
import com.abiquo.model.rest.RESTLink;
import com.abiquo.server.core.cloud.NodeVirtualImage;
import com.abiquo.server.core.cloud.VirtualAppliance;
import com.abiquo.server.core.cloud.VirtualDatacenter;
import com.abiquo.server.core.cloud.VirtualImage;
import com.abiquo.server.core.cloud.VirtualMachine;
import com.abiquo.server.core.cloud.VirtualMachineDto;
import com.abiquo.server.core.cloud.VirtualMachinesDto;
import com.abiquo.server.core.enterprise.Enterprise;
import com.abiquo.server.core.enterprise.Privilege;
import com.abiquo.server.core.enterprise.Role;
import com.abiquo.server.core.enterprise.User;
import com.abiquo.server.core.infrastructure.Datacenter;
import com.abiquo.server.core.infrastructure.Machine;

public class VirtualMachinesResourceIT extends AbstractJpaGeneratorIT
{

    protected Enterprise ent;

    protected Datacenter datacenter;

    protected VirtualDatacenter vdc;

    protected VirtualAppliance vapp;

    protected VirtualImage vImage;

    @BeforeMethod
    public void setUp()
    {
        ent = enterpriseGenerator.createUniqueInstance();
        datacenter = datacenterGenerator.createUniqueInstance();
        vdc = vdcGenerator.createInstance(datacenter, ent);
        vapp = vappGenerator.createInstance(vdc);
        vImage = virtualImageGenerator.createInstance(ent);
    }

    @BeforeMethod
    public void setupSysadmin()
    {
        Enterprise sysEnterprise = enterpriseGenerator.createUniqueInstance();
        Role r = roleGenerator.createInstanceSysAdmin();
        User u = userGenerator.createInstance(sysEnterprise, r, "sysadmin", "sysadmin");

        List<Object> entitiesToSetup = new ArrayList<Object>();

        entitiesToSetup.add(sysEnterprise);
        for (Privilege p : r.getPrivileges())
        {
            entitiesToSetup.add(p);
        }
        entitiesToSetup.add(r);
        entitiesToSetup.add(u);

        setup(entitiesToSetup.toArray());
    }

    /**
     * Create a virtual appliance. Insert tow virtual machines in the virtual appliance and check
     * it. Check also an 'empty' virtual appliance result
     */
    @Test
    public void getVirtualMachinesTest()
    {
        // Create a virtual machine
        VirtualMachine vm = vmGenerator.createInstance(ent);
        VirtualMachine vm2 = vmGenerator.createInstance(ent);

        Machine machine = vm.getHypervisor().getMachine();
        machine.setDatacenter(vdc.getDatacenter());
        machine.setRack(null);

        Machine machine2 = vm2.getHypervisor().getMachine();
        machine2.setDatacenter(vdc.getDatacenter());
        machine2.setRack(null);

        VirtualAppliance vapp2 = vappGenerator.createInstance(vdc);

        // Asociate it to the created virtual appliance
        NodeVirtualImage nvi = nodeVirtualImageGenerator.createInstance(vapp, vm);
        NodeVirtualImage nvi2 = nodeVirtualImageGenerator.createInstance(vapp, vm2);

        List<Object> entitiesToSetup = new ArrayList<Object>();

        entitiesToSetup.add(ent);
        entitiesToSetup.add(datacenter);
        entitiesToSetup.add(vdc);
        entitiesToSetup.add(vapp);
        entitiesToSetup.add(vapp2);

        for (Privilege p : vm.getUser().getRole().getPrivileges())
        {
            entitiesToSetup.add(p);
        }

        entitiesToSetup.add(vm.getUser().getRole());
        entitiesToSetup.add(vm.getUser());
        entitiesToSetup.add(vm.getVirtualImage());
        entitiesToSetup.add(machine);
        entitiesToSetup.add(vm.getHypervisor());
        entitiesToSetup.add(vm);
        entitiesToSetup.add(nvi);

        for (Privilege p : vm2.getUser().getRole().getPrivileges())
        {
            entitiesToSetup.add(p);
        }

        entitiesToSetup.add(vm2.getUser().getRole());
        entitiesToSetup.add(vm2.getUser());
        entitiesToSetup.add(vm2.getVirtualImage());
        entitiesToSetup.add(machine2);
        entitiesToSetup.add(vm2.getHypervisor());
        entitiesToSetup.add(vm2);
        entitiesToSetup.add(nvi2);

        setup(entitiesToSetup.toArray());

        // Check for vapp
        ClientResponse response = get(resolveVirtualMachinesURI(vdc.getId(), vapp.getId()));
        assertEquals(response.getStatusCode(), Status.OK.getStatusCode());
        VirtualMachinesDto vms = response.getEntity(VirtualMachinesDto.class);
        assertNotNull(vms);
        assertNotNull(vms.getCollection());
        assertEquals(vms.getCollection().size(), 2);

        // Check for vapp2
        response = get(resolveVirtualMachinesURI(vdc.getId(), vapp2.getId()));
        assertEquals(response.getStatusCode(), Status.OK.getStatusCode());
        vms = response.getEntity(VirtualMachinesDto.class);
        assertNotNull(vms);
        assertNotNull(vms.getCollection());
        assertEquals(vms.getCollection().size(), 0);

    }

    /**
     * Check the virtual machines of invalid vitual appliance id. Server response should return a
     * 404 NOT FOUND status code
     */
    @Test
    public void getVirtualMachinesRaises404WhenInvalidVirtualApplianceId()
    {
        setup(ent, datacenter, vdc, vapp);

        ClientResponse response =
            get(resolveVirtualMachinesURI(vdc.getId(), new Random().nextInt()));
        assertEquals(response.getStatusCode(), Status.NOT_FOUND.getStatusCode());
    }

    /**
     * Check the virtual machines list of an invalid virtualdatacenter for a valid virtual appliance
     * id. Server response should return a 404 NOT FOUND status code
     */
    @Test
    public void getVirtualMachinesRaises404WhenInvalidVirtualDatacenterId()
    {
        setup(ent, datacenter, vdc, vapp);

        ClientResponse response =
            get(resolveVirtualMachinesURI(new Random().nextInt(), vapp.getId()));
        assertEquals(response.getStatusCode(), Status.NOT_FOUND.getStatusCode());
    }

    /**
     * Creates a virtual machine. Disabled until the VirtualImage resource is done
     */
    @Test(enabled = false)
    public void createVirtualMachine()
    {
        setup(ent, datacenter, vdc, vapp);
        setup(vImage);

        VirtualMachine vm = vmGenerator.createInstance(vImage, ent, "Image");
        VirtualMachineDto dto = fromVirtualMachineToDto(vm);
        ClientResponse response =
            post(resolveVirtualMachinesURI(vdc.getId(), vapp.getId()), dto, "sysadmin", "sysadmin");
        assertEquals(response.getStatusCode(), Status.CREATED.getStatusCode());
    }

    /**
     * Creates a virtual machine.Disabled until the VirtualImage resource is done
     */
    @Test(enabled = false)
    public void createVirtualMachine404InvalidDatacenterId()
    {
        setup(ent, datacenter, vdc, vapp);

        VirtualMachine vm = vmGenerator.createInstance(ent);
        VirtualMachineDto dto = fromVirtualMachineToDto(vm);
        ClientResponse response =
            post(resolveVirtualMachinesURI(new Random().nextInt(), vapp.getId()), dto, "sysadmin",
                "sysadmin");
        assertEquals(response.getStatusCode(), Status.NOT_FOUND.getStatusCode());
    }

    /**
     * Creates a virtual machine.Disabled until the VirtualImage resource is done
     */
    @Test(enabled = false)
    public void createVirtualMachine404InvalidVapp()
    {
        setup(ent, datacenter, vdc, vapp);

        VirtualMachine vm = vmGenerator.createInstance(ent);
        VirtualMachineDto dto = fromVirtualMachineToDto(vm);
        ClientResponse response =
            post(resolveVirtualMachinesURI(vdc.getId(), new Random().nextInt()), dto, "sysadmin",
                "sysadmin");
        assertEquals(response.getStatusCode(), Status.NOT_FOUND.getStatusCode());
    }

    private VirtualMachineDto fromVirtualMachineToDto(final VirtualMachine vm)
    {
        VirtualMachineDto dto = new VirtualMachineDto();
        dto.setCpu(vm.getCpu());
        dto.setDescription(vm.getDescription());
        dto.setHd(vm.getHdInBytes());
        dto.setHighDisponibility(vm.getHighDisponibility());
        dto.setName(vm.getName());

        // dto.setIdState(v.getidState)
        if (vm.getIdType() == 0)
        {
        	dto.setType("NOT_MANAGED");
        }
        else
        {
        	dto.setType("MANAGED");
        }
        dto.setPassword(vm.getPassword());
        dto.setRam(vm.getRam());
        dto.setVdrpIP(vm.getVdrpIP());
        dto.setVdrpPort(vm.getVdrpPort());
        RESTLink enterpriseLink =
            new RESTLink("enterprise", resolveEnterpriseURI(vm.getEnterprise().getId()));
        dto.addLink(enterpriseLink);

        RESTLink vImageLink =
            new RESTLink("virtualimage", resolveEnterpriseURI(vm.getVirtualImage().getId()));
        dto.addLink(vImageLink);

        return dto;
    }

}
