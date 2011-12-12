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

import static com.abiquo.api.common.Assert.assertLinkExist;
import static com.abiquo.api.common.UriTestResolver.resolveEnterpriseURI;
import static com.abiquo.api.common.UriTestResolver.resolveVirtualMachineTemplateURI;
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
import com.abiquo.api.resources.appslibrary.VirtualMachineTemplateResource;
import com.abiquo.model.rest.RESTLink;
import com.abiquo.server.core.appslibrary.VirtualMachineTemplate;
import com.abiquo.server.core.cloud.NodeVirtualImage;
import com.abiquo.server.core.cloud.VirtualAppliance;
import com.abiquo.server.core.cloud.VirtualDatacenter;
import com.abiquo.server.core.cloud.VirtualMachine;
import com.abiquo.server.core.cloud.VirtualMachineDto;
import com.abiquo.server.core.cloud.VirtualMachinesDto;
import com.abiquo.server.core.enterprise.DatacenterLimits;
import com.abiquo.server.core.enterprise.Enterprise;
import com.abiquo.server.core.enterprise.Privilege;
import com.abiquo.server.core.enterprise.Role;
import com.abiquo.server.core.enterprise.User;
import com.abiquo.server.core.infrastructure.Datacenter;
import com.abiquo.server.core.infrastructure.Machine;
import com.abiquo.server.core.infrastructure.network.IpPoolManagement;
import com.abiquo.server.core.infrastructure.network.IpPoolManagement.Type;
import com.abiquo.server.core.infrastructure.network.VLANNetwork;
import com.abiquo.server.core.util.network.IPAddress;
import com.abiquo.server.core.util.network.IPNetworkRang;

public class VirtualMachinesResourceIT extends AbstractJpaGeneratorIT
{
    protected Enterprise ent;

    protected DatacenterLimits dcallowed;

    protected Datacenter datacenter;

    protected VirtualDatacenter vdc;

    protected VirtualAppliance vapp;

    protected VirtualMachineTemplate vmtemplate;

    protected VLANNetwork vlan;

    @BeforeMethod
    public void setUp()
    {
        ent = enterpriseGenerator.createUniqueInstance();
        datacenter = datacenterGenerator.createUniqueInstance();
        dcallowed = datacenterLimitsGenerator.createInstance(ent, datacenter);

        vdc = vdcGenerator.createInstance(datacenter, ent);
        vapp = vappGenerator.createInstance(vdc);
        vmtemplate = virtualMachineTemplateGenerator.createInstance(ent, datacenter);
        vlan = vlanGenerator.createInstance(vdc.getNetwork());
        vdc.setDefaultVlan(vlan);
    }

    @BeforeMethod
    public void setupSysadmin()
    {
        final Enterprise sysEnterprise = enterpriseGenerator.createUniqueInstance();
        final Role r = roleGenerator.createInstanceSysAdmin();
        final User u = userGenerator.createInstance(sysEnterprise, r, "sysadmin", "sysadmin");

        final List<Object> entitiesToSetup = new ArrayList<Object>();

        entitiesToSetup.add(sysEnterprise);
        for (final Privilege p : r.getPrivileges())
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
        final VirtualMachine vm = vmGenerator.createInstance(ent);
        final VirtualMachine vm2 = vmGenerator.createInstance(ent);

        final Machine machine = vm.getHypervisor().getMachine();
        machine.setDatacenter(vdc.getDatacenter());
        machine.setRack(null);

        final Machine machine2 = vm2.getHypervisor().getMachine();
        machine2.setDatacenter(vdc.getDatacenter());
        machine2.setRack(null);

        final VirtualAppliance vapp2 = vappGenerator.createInstance(vdc);

        // Asociate it to the created virtual appliance
        final NodeVirtualImage nvi = nodeVirtualImageGenerator.createInstance(vapp, vm);
        final NodeVirtualImage nvi2 = nodeVirtualImageGenerator.createInstance(vapp, vm2);

        vm.getVirtualMachineTemplate().getRepository()
            .setDatacenter(vm.getHypervisor().getMachine().getDatacenter());
        vm2.getVirtualMachineTemplate().getRepository()
            .setDatacenter(vm2.getHypervisor().getMachine().getDatacenter());

        final List<Object> entitiesToSetup = new ArrayList<Object>();

        entitiesToSetup.add(ent);
        entitiesToSetup.add(datacenter);
        entitiesToSetup.add(dcallowed);
        entitiesToSetup.add(vdc.getNetwork());
        entitiesToSetup.add(vdc.getDefaultVlan().getConfiguration());
        entitiesToSetup.add(vdc.getDefaultVlan());
        entitiesToSetup.add(vdc);
        entitiesToSetup.add(vapp);
        entitiesToSetup.add(vapp2);

        for (final Privilege p : vm.getUser().getRole().getPrivileges())
        {
            entitiesToSetup.add(p);
        }

        entitiesToSetup.add(vm.getUser().getRole());
        entitiesToSetup.add(vm.getUser());
        entitiesToSetup.add(vm.getVirtualMachineTemplate().getRepository());
        entitiesToSetup.add(vm.getVirtualMachineTemplate().getCategory());
        entitiesToSetup.add(vm.getVirtualMachineTemplate());
        entitiesToSetup.add(machine);
        entitiesToSetup.add(vm.getHypervisor());
        entitiesToSetup.add(vm);
        entitiesToSetup.add(nvi);

        for (final Privilege p : vm2.getUser().getRole().getPrivileges())
        {
            entitiesToSetup.add(p);
        }

        entitiesToSetup.add(vm2.getUser().getRole());
        entitiesToSetup.add(vm2.getUser());
        entitiesToSetup.add(vm2.getVirtualMachineTemplate().getRepository());
        entitiesToSetup.add(vm2.getVirtualMachineTemplate().getCategory());
        entitiesToSetup.add(vm2.getVirtualMachineTemplate());
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
        setup(ent, datacenter, dcallowed, vdc.getNetwork(),
            vdc.getDefaultVlan().getConfiguration(), vdc.getDefaultVlan(), vdc, vapp);

        final ClientResponse response =
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
        setup(ent, datacenter, dcallowed, vdc.getNetwork(),
            vdc.getDefaultVlan().getConfiguration(), vdc.getDefaultVlan(), vdc, vapp);

        final ClientResponse response =
            get(resolveVirtualMachinesURI(new Random().nextInt(), vapp.getId()));
        assertEquals(response.getStatusCode(), Status.NOT_FOUND.getStatusCode());
    }

    /**
     * Creates a virtual machine.
     */
    @Test
    public void createVirtualMachine()
    {
        setup(ent, datacenter, dcallowed, vdc.getNetwork(),
            vdc.getDefaultVlan().getConfiguration(), vdc.getDefaultVlan(), vdc, vapp);
        setup(vmtemplate.getRepository(), vmtemplate.getCategory(), vmtemplate);

        IPAddress ip =
            IPAddress.newIPAddress(vdc.getDefaultVlan().getConfiguration().getAddress())
                .nextIPAddress();
        IPAddress lastIP =
            IPNetworkRang.lastIPAddressWithNumNodes(IPAddress.newIPAddress(vdc.getDefaultVlan()
                .getConfiguration().getAddress()), IPNetworkRang.masktoNumberOfNodes(vdc
                .getDefaultVlan().getConfiguration().getMask()));

        List<Object> arrayIps = new ArrayList<Object>();
        while (!ip.equals(lastIP))
        {
            IpPoolManagement ippool =
                ipGenerator.createInstance(vdc, vdc.getDefaultVlan(), ip.toString());
            ippool.setType(Type.PRIVATE);
            arrayIps.add(ippool.getRasd());
            arrayIps.add(ippool);
            ip = ip.nextIPAddress();
        }
        setup(arrayIps.toArray());

        final VirtualMachine vm = vmGenerator.createInstance(vmtemplate, ent, "Template");
        final VirtualMachineDto dto = fromVirtualMachineToDto(vm);
        final ClientResponse response =
            post(resolveVirtualMachinesURI(vdc.getId(), vapp.getId()), dto, "sysadmin", "sysadmin");
        assertEquals(response.getStatusCode(), Status.CREATED.getStatusCode(),
            response.getEntity(String.class).toString());

        String vmtemplateUrl =
            resolveVirtualMachineTemplateURI(vmtemplate.getEnterprise().getId(), vmtemplate
                .getRepository().getDatacenter().getId(), vmtemplate.getId());
        assertLinkExist(dto, vmtemplateUrl, VirtualMachineTemplateResource.VIRTUAL_MACHINE_TEMPLATE);
    }

    /**
     * Attempt to create a virtual machine with a vmtemplate not in the same repository
     */
    @Test
    public void createVirtualMachineInvalidVirtualMachineTemplateDifferentDatacenter()
    {
        Datacenter otherDc = datacenterGenerator.createUniqueInstance();
        VirtualMachineTemplate otherVmtemplate =
            virtualMachineTemplateGenerator.createInstance(ent, otherDc);

        setup(ent, datacenter, dcallowed, vdc.getNetwork(),
            vdc.getDefaultVlan().getConfiguration(), vdc.getDefaultVlan(), vdc, vapp);
        setup(otherDc, otherVmtemplate.getRepository(), otherVmtemplate.getCategory(),
            otherVmtemplate);

        final VirtualMachine vm = vmGenerator.createInstance(otherVmtemplate, ent, "Template");
        final VirtualMachineDto dto = fromVirtualMachineToDto(vm);
        final ClientResponse response =
            post(resolveVirtualMachinesURI(vdc.getId(), vapp.getId()), dto, "sysadmin", "sysadmin");
        assertEquals(response.getStatusCode(), Status.CONFLICT.getStatusCode());
    }

    /**
     * Attempt to create a virtual machine with a vmtemplate not in the same enterprise
     */
    @Test
    public void createVirtualMachineInvalidVirtualMachineTemplateDifferentEnterprise()
    {
        Enterprise otherEnt = enterpriseGenerator.createUniqueInstance();
        VirtualMachineTemplate otherVmtemplate =
            virtualMachineTemplateGenerator.createInstance(otherEnt, datacenter);

        setup(ent, datacenter, dcallowed, vdc.getNetwork(),
            vdc.getDefaultVlan().getConfiguration(), vdc.getDefaultVlan(), vdc, vapp);
        setup(otherEnt, otherVmtemplate.getRepository(), otherVmtemplate.getCategory(),
            otherVmtemplate);

        final VirtualMachine vm = vmGenerator.createInstance(otherVmtemplate, ent, "Template");
        final VirtualMachineDto dto = fromVirtualMachineToDto(vm);
        final ClientResponse response =
            post(resolveVirtualMachinesURI(vdc.getId(), vapp.getId()), dto, "sysadmin", "sysadmin");
        assertEquals(response.getStatusCode(), Status.CONFLICT.getStatusCode());
    }

    /**
     * Create a virtual machine with a vmtemplate not in the same enterprise but shared
     */
    @Test
    public void createVirtualMachineSharedVirtualMachineTemplateDifferentEnterprise()
    {
        setup(ent, datacenter, dcallowed, vdc.getNetwork(),
            vdc.getDefaultVlan().getConfiguration(), vdc.getDefaultVlan(), vdc, vapp);
        
        DatacenterLimits otherDcLimits = datacenterLimitsGenerator.createInstance(datacenter);
        Enterprise otherEnt = otherDcLimits.getEnterprise();
        VirtualMachineTemplate otherVmtemplate =
            virtualMachineTemplateGenerator.createInstance(otherEnt, datacenter);
        otherVmtemplate.setShared(true);
        setup(otherEnt, otherDcLimits, otherVmtemplate.getRepository(), otherVmtemplate.getCategory(),
            otherVmtemplate);

        IPAddress ip =
            IPAddress.newIPAddress(vdc.getDefaultVlan().getConfiguration().getAddress())
                .nextIPAddress();
        IPAddress lastIP =
            IPNetworkRang.lastIPAddressWithNumNodes(IPAddress.newIPAddress(vdc.getDefaultVlan()
                .getConfiguration().getAddress()), IPNetworkRang.masktoNumberOfNodes(vdc
                .getDefaultVlan().getConfiguration().getMask()));

        List<Object> arrayIps = new ArrayList<Object>();
        while (!ip.equals(lastIP))
        {
            IpPoolManagement ippool =
                ipGenerator.createInstance(vdc, vdc.getDefaultVlan(), ip.toString());
            ippool.setType(Type.PRIVATE);
            arrayIps.add(ippool.getRasd());
            arrayIps.add(ippool);
            ip = ip.nextIPAddress();
        }
        setup(arrayIps.toArray());

        final VirtualMachine vm = vmGenerator.createInstance(otherVmtemplate, ent, "Template");
        final VirtualMachineDto dto = fromVirtualMachineToDto(vm);
        final ClientResponse response =
            post(resolveVirtualMachinesURI(vdc.getId(), vapp.getId()), dto, "sysadmin", "sysadmin");
        assertEquals(response.getStatusCode(), Status.CREATED.getStatusCode());
    }

    /**
     * Attempts to create a virtual machine for a non existent vmtemplate
     */
    @Test
    public void createVirtualMachine404VirtualMachineTemplateNotFound()
    {
        setup(ent, datacenter, dcallowed, vdc.getNetwork(),
            vdc.getDefaultVlan().getConfiguration(), vdc.getDefaultVlan(), vdc, vapp);
        setup(vmtemplate.getRepository(), vmtemplate.getCategory(), vmtemplate);

        final VirtualMachine vm = vmGenerator.createInstance(vmtemplate, ent, "Template");
        final VirtualMachineDto dto = fromVirtualMachineToDto(vm);

        tearDown("virtualimage");

        final ClientResponse response =
            post(resolveVirtualMachinesURI(vdc.getId(), vapp.getId()), dto, "sysadmin", "sysadmin");
        assertEquals(response.getStatusCode(), Status.NOT_FOUND.getStatusCode());
    }

    /**
     * Attempts to create a virtual machine using a malformed vmtemplate link
     */
    @Test
    public void createVirtualMachine400VirtualMachineTemplateInvalidLink()
    {
        setup(ent, datacenter, dcallowed, vdc.getNetwork(),
            vdc.getDefaultVlan().getConfiguration(), vdc.getDefaultVlan(), vdc, vapp);
        setup(vmtemplate.getRepository(), vmtemplate.getCategory(), vmtemplate);

        final VirtualMachine vm = vmGenerator.createInstance(vmtemplate, ent, "Template");
        final VirtualMachineDto dto = fromVirtualMachineToDto(vm);
        for (RESTLink vmtemplateLink : dto.getLinks())
        {
            if (vmtemplateLink.getRel().equalsIgnoreCase("virtualmachinetemplate"))
            {
                vmtemplateLink.setHref("http://i/m/dummy/user");
            }
        }

        final ClientResponse response =
            post(resolveVirtualMachinesURI(vdc.getId(), vapp.getId()), dto, "sysadmin", "sysadmin");
        assertEquals(response.getStatusCode(), Status.BAD_REQUEST.getStatusCode());
    }

    /**
     * Attempts to create a virtual machine for a datacenter not allowed (in the vmtemplate)
     */
    @Test
    public void createVirtualMachine409VirtualMachineTemplateInDatacenterNotAllowed()
    {
        setup(ent, datacenter, vdc.getNetwork(), vdc.getDefaultVlan().getConfiguration(),
            vdc.getDefaultVlan(), vdc, vapp); // dcallowed
        setup(vmtemplate.getRepository(), vmtemplate.getCategory(), vmtemplate);

        final VirtualMachine vm = vmGenerator.createInstance(vmtemplate, ent, "Template");
        final VirtualMachineDto dto = fromVirtualMachineToDto(vm);

        final ClientResponse response =
            post(resolveVirtualMachinesURI(vdc.getId(), vapp.getId()), dto, "sysadmin", "sysadmin");
        assertEquals(response.getStatusCode(), Status.CONFLICT.getStatusCode());
    }

    /**
     * Creates a virtual machine.Disabled until the VirtualMachineTemplate resource is done
     */
    @Test(enabled = false)
    public void createVirtualMachine404InvalidDatacenterId()
    {
        setup(ent, datacenter, vdc.getNetwork(), vdc.getDefaultVlan().getConfiguration(),
            vdc.getDefaultVlan(), vdc, vapp);

        final VirtualMachine vm = vmGenerator.createInstance(ent);
        final VirtualMachineDto dto = fromVirtualMachineToDto(vm);
        final ClientResponse response =
            post(resolveVirtualMachinesURI(new Random().nextInt(), vapp.getId()), dto, "sysadmin",
                "sysadmin");
        assertEquals(response.getStatusCode(), Status.NOT_FOUND.getStatusCode());
    }

    /**
     * Creates a virtual machine.Disabled until the VirtualMachineTemplate resource is done
     */
    @Test(enabled = false)
    public void createVirtualMachine404InvalidVapp()
    {
        setup(ent, datacenter, vdc.getNetwork(), vdc.getDefaultVlan().getConfiguration(),
            vdc.getDefaultVlan(), vdc, vapp);

        final VirtualMachine vm = vmGenerator.createInstance(ent);
        final VirtualMachineDto dto = fromVirtualMachineToDto(vm);
        final ClientResponse response =
            post(resolveVirtualMachinesURI(vdc.getId(), new Random().nextInt()), dto, "sysadmin",
                "sysadmin");
        assertEquals(response.getStatusCode(), Status.NOT_FOUND.getStatusCode());
    }

    private VirtualMachineDto fromVirtualMachineToDto(final VirtualMachine vm)
    {
        final VirtualMachineDto dto = new VirtualMachineDto();
        dto.setCpu(vm.getCpu());
        dto.setDescription(vm.getDescription());
        dto.setHdInBytes(vm.getHdInBytes());
        dto.setHighDisponibility(vm.getHighDisponibility());
        dto.setName(vm.getName());

        // dto.setIdState(v.getidState)
        if (vm.getIdType() == 0)
        {
            dto.setIdType(com.abiquo.server.core.cloud.VirtualMachine.NOT_MANAGED);
        }
        else
        {
            dto.setIdType(com.abiquo.server.core.cloud.VirtualMachine.MANAGED);
        }
        dto.setPassword(vm.getPassword());
        dto.setRam(vm.getRam());
        dto.setVdrpIP(vm.getVdrpIP());
        dto.setVdrpPort(vm.getVdrpPort());
        final RESTLink enterpriseLink =
            new RESTLink("enterprise", resolveEnterpriseURI(vm.getEnterprise().getId()));
        dto.addLink(enterpriseLink);

        final RESTLink vmtemplateLink =
            new RESTLink("virtualmachinetemplate", resolveVirtualMachineTemplateURI(vm
                .getVirtualMachineTemplate().getEnterprise().getId(), vm
                .getVirtualMachineTemplate().getRepository().getDatacenter().getId(), vm
                .getVirtualMachineTemplate().getId()));
        dto.addLink(vmtemplateLink);

        return dto;
    }

}
