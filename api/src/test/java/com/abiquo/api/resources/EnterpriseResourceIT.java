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

package com.abiquo.api.resources;

import static com.abiquo.api.common.Assert.assertErrors;
import static com.abiquo.api.common.Assert.assertLinkExist;
import static com.abiquo.api.common.UriTestResolver.resolveEnterpriseActionGetIPsURI;
import static com.abiquo.api.common.UriTestResolver.resolveEnterpriseActionGetVirtualMachinesURI;
import static com.abiquo.api.common.UriTestResolver.resolveEnterpriseURI;
import static com.abiquo.api.common.UriTestResolver.resolveMachineURI;
import static com.abiquo.api.common.UriTestResolver.resolvePrivateNetworkURI;
import static com.abiquo.api.common.UriTestResolver.resolveUserURI;
import static com.abiquo.api.common.UriTestResolver.resolveVirtualDatacenterURI;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;
import static org.testng.Assert.assertTrue;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response.Status;

import org.apache.wink.client.ClientResponse;
import org.apache.wink.client.ClientWebException;
import org.apache.wink.client.Resource;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import com.abiquo.api.exceptions.APIError;
import com.abiquo.api.resources.cloud.IpAddressesResource;
import com.abiquo.api.resources.cloud.PrivateNetworkResource;
import com.abiquo.api.resources.cloud.VirtualMachinesResource;
import com.abiquo.model.enumerator.RemoteServiceType;
import com.abiquo.server.core.cloud.VirtualDatacenter;
import com.abiquo.server.core.cloud.VirtualMachine;
import com.abiquo.server.core.cloud.VirtualMachineDto;
import com.abiquo.server.core.cloud.VirtualMachinesDto;
import com.abiquo.server.core.enterprise.Enterprise;
import com.abiquo.server.core.enterprise.EnterpriseDto;
import com.abiquo.server.core.enterprise.Privilege;
import com.abiquo.server.core.enterprise.Role;
import com.abiquo.server.core.enterprise.User;
import com.abiquo.server.core.infrastructure.Machine;
import com.abiquo.server.core.infrastructure.RemoteService;
import com.abiquo.server.core.infrastructure.network.IpPoolManagement;
import com.abiquo.server.core.infrastructure.network.IpsPoolManagementDto;
import com.abiquo.server.core.infrastructure.network.VLANNetwork;
import com.abiquo.server.core.util.network.IPAddress;
import com.abiquo.server.core.util.network.IPNetworkRang;

public class EnterpriseResourceIT extends AbstractJpaGeneratorIT
{

    @BeforeMethod
    public void setupSysadmin()
    {
        Enterprise e = enterpriseGenerator.createUniqueInstance();
        Role r = roleGenerator.createUniqueInstance();

        User u = userGenerator.createInstance(e, r, "sysadmin", "sysadmin");

        List<Object> entitiesToSetup = new ArrayList<Object>();

        entitiesToSetup.add(e);

        for (Privilege p : r.getPrivileges())
        {
            entitiesToSetup.add(p);
        }
        entitiesToSetup.add(r);
        entitiesToSetup.add(u);

        setup(entitiesToSetup.toArray());

    }

    @Test
    public void getEnterpriseDoesntExist() throws ClientWebException
    {
        Resource resource = client.resource(resolveEnterpriseURI(12345));

        ClientResponse response = resource.accept(MediaType.APPLICATION_XML).get();
        assertEquals(404, response.getStatusCode());
    }

    @Test
    public void getEnterprise() throws Exception
    {
        Enterprise enterprise = enterpriseGenerator.createUniqueInstance();
        setup(enterprise);

        String uri = resolveEnterpriseURI(enterprise.getId());

        ClientResponse response = get(uri, "sysadmin", "sysadmin");

        EnterpriseDto dto = response.getEntity(EnterpriseDto.class);

        assertNotNull(dto);
    }

    @Test
    public void enterpriseContainCorrectLinks() throws ClientWebException
    {
        Enterprise enterprise = enterpriseGenerator.createUniqueInstance();
        setup(enterprise);

        String href = resolveEnterpriseURI(enterprise.getId());

        ClientResponse response = get(href, "sysadmin", "sysadmin");

        EnterpriseDto dto = response.getEntity(EnterpriseDto.class);

        assertNotNull(dto.getLinks());

        assertLinkExist(dto, href, "edit");
        assertLinkExist(dto, href + "/users", "users");

        assertLinkExist(dto, resolveEnterpriseActionGetIPsURI(enterprise.getId()), "action",
            IpAddressesResource.IP_ADDRESSES);
        assertLinkExist(dto, resolveEnterpriseActionGetVirtualMachinesURI(enterprise.getId()),
            "action", VirtualMachinesResource.VIRTUAL_MACHINES_PATH);
    }

    @Test
    public void modifyEnterprise() throws ClientWebException
    {
        Enterprise enterprise = enterpriseGenerator.createUniqueInstance();
        setup(enterprise);

        String href = resolveEnterpriseURI(enterprise.getId());

        ClientResponse response = get(href, "sysadmin", "sysadmin");

        EnterpriseDto dto = response.getEntity(EnterpriseDto.class);
        dto.setName("enterprise_changed");

        response = put(href, dto, "sysadmin", "sysadmin");
        assertEquals(response.getStatusCode(), 200);

        EnterpriseDto modified = response.getEntity(EnterpriseDto.class);
        assertEquals("enterprise_changed", modified.getName());
    }

    @Test
    public void modifyEnterpriseWithDuplicatedName()
    {
        Enterprise enterprise = enterpriseGenerator.createUniqueInstance();
        Enterprise enterprise2 = enterpriseGenerator.createUniqueInstance();
        setup(enterprise, enterprise2);

        String uri1 = resolveEnterpriseURI(enterprise.getId());
        String uri2 = resolveEnterpriseURI(enterprise2.getId());

        ClientResponse response1 = get(uri1, "sysadmin", "sysadmin");
        ClientResponse response2 = get(uri2, "sysadmin", "sysadmin");

        EnterpriseDto dto1 = response1.getEntity(EnterpriseDto.class);
        EnterpriseDto dto2 = response2.getEntity(EnterpriseDto.class);

        dto2.setName(dto1.getName());

        ClientResponse response = put(uri2, dto2, "sysadmin", "sysadmin");

        assertErrors(response, APIError.ENTERPRISE_DUPLICATED_NAME.getCode());
    }

    // TESTS refered to the action of GET IPs by Enterprise
    /**
     * Check if the action of get the IPs by an enterprise exists.
     */
    @Test
    public void createAndGetPrivateNetworkIPsByEnterprise()
    {
        RemoteService rs = remoteServiceGenerator.createInstance(RemoteServiceType.DHCP_SERVICE);
        VirtualDatacenter vdc = vdcGenerator.createInstance(rs.getDatacenter());
        setup(vdc.getDatacenter(), rs, vdc.getEnterprise(), vdc.getNetwork(), vdc);
        VLANNetwork vlan = vlanGenerator.createInstance(vdc.getNetwork(), rs, "255.255.255.0");
        setup(vlan.getConfiguration().getDhcp(), vlan.getConfiguration(), vlan);

        IPAddress ip = IPAddress.newIPAddress(vlan.getConfiguration().getAddress()).nextIPAddress();
        IPAddress lastIP =
            IPNetworkRang.lastIPAddressWithNumNodes(
                IPAddress.newIPAddress(vlan.getConfiguration().getAddress()),
                IPNetworkRang.masktoNumberOfNodes(vlan.getConfiguration().getMask()));

        while (!ip.equals(lastIP))
        {
            IpPoolManagement ippool = ipGenerator.createInstance(vdc, vlan, ip.toString());
            setup(ippool.getRasd(), ippool);
            ip = ip.nextIPAddress();
        }

        String validURI = resolveEnterpriseActionGetIPsURI(vdc.getEnterprise().getId());
        Resource resource = client.resource(validURI);

        ClientResponse response = resource.accept(MediaType.APPLICATION_XML).get();
        assertEquals(200, response.getStatusCode());

        IpsPoolManagementDto entity = response.getEntity(IpsPoolManagementDto.class);
        assertNotNull(entity);
        assertNotNull(entity.getCollection());
        assertEquals(entity.getCollection().size(), 25);

        // Get the first object and ensure it have at least the links of virtualdatacenter
        // and the link of private network that belongs to
        assertLinkExist(entity.getCollection().get(0),
            resolvePrivateNetworkURI(vdc.getId(), vlan.getId()),
            PrivateNetworkResource.PRIVATE_NETWORK);
        assertLinkExist(entity.getCollection().get(0), resolveVirtualDatacenterURI(vdc.getId()),
            "virtualdatacenter");

    }

    /**
     * Check if the request 'action/ips' of the enterprise resource allows the 'by=ip' query param
     */
    @Test
    public void getPrivateNetworkIPsByEnterpriseOrderByIp()
    {
        VirtualDatacenter vdc = vdcGenerator.createUniqueInstance();
        setup(vdc.getDatacenter(), vdc.getEnterprise(), vdc.getNetwork(), vdc);
        String validURI = resolveEnterpriseActionGetIPsURI(vdc.getEnterprise().getId());

        validURI = validURI + "?by=ip";
        Resource resource = client.resource(validURI);

        ClientResponse response = resource.accept(MediaType.APPLICATION_XML).get();
        assertEquals(200, response.getStatusCode());
    }

    /**
     * Check if the request 'action/ips' of the enterprise resource allows the 'by=quarantine' query
     * param
     */
    @Test
    public void getPrivateNetworkIPsByEnterpriseOrderByQuarantine()
    {
        VirtualDatacenter vdc = vdcGenerator.createUniqueInstance();
        setup(vdc.getDatacenter(), vdc.getEnterprise(), vdc.getNetwork(), vdc);
        String validURI = resolveEnterpriseActionGetIPsURI(vdc.getEnterprise().getId());

        validURI = validURI + "?by=quarantine";
        Resource resource = client.resource(validURI);

        ClientResponse response = resource.accept(MediaType.APPLICATION_XML).get();
        assertEquals(200, response.getStatusCode());
    }

    /**
     * Check if the request 'action/ips' of the enterprise resource allows the 'by=mac' query param
     */
    @Test
    public void getPrivateNetworkIPsByEnterpriseOrderByMAC()
    {
        VirtualDatacenter vdc = vdcGenerator.createUniqueInstance();
        setup(vdc.getDatacenter(), vdc.getEnterprise(), vdc.getNetwork(), vdc);
        String validURI = resolveEnterpriseActionGetIPsURI(vdc.getEnterprise().getId());

        validURI = validURI + "?by=mac";
        Resource resource = client.resource(validURI);

        ClientResponse response = resource.accept(MediaType.APPLICATION_XML).get();
        assertEquals(200, response.getStatusCode());
    }

    /**
     * Check if the request 'action/ips' of the enterprise resource allows the 'by=lease' query
     * param
     */
    @Test
    public void getPrivateNetworkIPsByEnterpriseOrderByLease()
    {
        VirtualDatacenter vdc = vdcGenerator.createUniqueInstance();
        setup(vdc.getDatacenter(), vdc.getEnterprise(), vdc.getNetwork(), vdc);
        String validURI = resolveEnterpriseActionGetIPsURI(vdc.getEnterprise().getId());

        validURI = validURI + "?by=lease";
        Resource resource = client.resource(validURI);

        ClientResponse response = resource.accept(MediaType.APPLICATION_XML).get();
        assertEquals(200, response.getStatusCode());
    }

    /**
     * Check if the request 'action/ips' of the enterprise resource allows the 'by=vlan' query param
     */
    @Test
    public void getPrivateNetworkIPsByEnterpriseOrderByVlan()
    {
        VirtualDatacenter vdc = vdcGenerator.createUniqueInstance();
        setup(vdc.getDatacenter(), vdc.getEnterprise(), vdc.getNetwork(), vdc);
        String validURI = resolveEnterpriseActionGetIPsURI(vdc.getEnterprise().getId());

        validURI = validURI + "?by=vlan";
        Resource resource = client.resource(validURI);

        ClientResponse response = resource.accept(MediaType.APPLICATION_XML).get();
        assertEquals(200, response.getStatusCode());
    }

    /**
     * Check if the request 'action/ips' of the enterprise resource allows the
     * 'by=virtualdatacenter' query param
     */
    @Test
    public void getPrivateNetworkIPsByEnterpriseOrderByVirtualDatacenter()
    {
        VirtualDatacenter vdc = vdcGenerator.createUniqueInstance();
        setup(vdc.getDatacenter(), vdc.getEnterprise(), vdc.getNetwork(), vdc);
        String validURI = resolveEnterpriseActionGetIPsURI(vdc.getEnterprise().getId());

        validURI = validURI + "?by=virtualdatacenter";
        Resource resource = client.resource(validURI);

        ClientResponse response = resource.accept(MediaType.APPLICATION_XML).get();
        assertEquals(200, response.getStatusCode());
    }

    /**
     * Check if the request 'action/ips' of the enterprise resource doesnt allow a
     * 'by={randomvalue}' query param
     */
    @Test
    public void getPrivateNetworkIPsByEnterpriseRaises400WhenOrderByRandomParameter()
    {
        VirtualDatacenter vdc = vdcGenerator.createUniqueInstance();
        setup(vdc.getDatacenter(), vdc.getEnterprise(), vdc.getNetwork(), vdc);
        String validURI = resolveEnterpriseActionGetIPsURI(vdc.getEnterprise().getId());

        validURI = validURI + "?by=" + Integer.valueOf(new Random().nextInt());
        Resource resource = client.resource(validURI);

        ClientResponse response = resource.accept(MediaType.APPLICATION_XML).get();
        assertEquals(400, response.getStatusCode());
    }

    /**
     * Check if the request 'action/ips' of the enterprise resource allows the 'by=virtualmachine'
     * query param
     */
    @Test
    public void getPrivateNetworkIPsByEnterpriseOrderByVirtualMachine()
    {
        VirtualDatacenter vdc = vdcGenerator.createUniqueInstance();
        setup(vdc.getDatacenter(), vdc.getEnterprise(), vdc.getNetwork(), vdc);
        String validURI = resolveEnterpriseActionGetIPsURI(vdc.getEnterprise().getId());

        validURI = validURI + "?by=virtualmachine";
        Resource resource = client.resource(validURI);

        ClientResponse response = resource.accept(MediaType.APPLICATION_XML).get();
        assertEquals(200, response.getStatusCode());
    }

    /**
     * Check if the request 'action/ips' of the enterprise resource allows the 'by=virtualappliance'
     * query param
     */
    @Test
    public void getPrivateNetworkIPsByEnterpriseOrderByVirtualAppliance()
    {
        VirtualDatacenter vdc = vdcGenerator.createUniqueInstance();
        setup(vdc.getDatacenter(), vdc.getEnterprise(), vdc.getNetwork(), vdc);
        String validURI = resolveEnterpriseActionGetIPsURI(vdc.getEnterprise().getId());

        validURI = validURI + "?by=virtualappliance";
        Resource resource = client.resource(validURI);

        ClientResponse response = resource.accept(MediaType.APPLICATION_XML).get();
        assertEquals(200, response.getStatusCode());
    }

    /**
     * Create an Enterprise without VDC (an so, without IPs) and check the empty list
     */
    @Test
    public void createEnterpriseReturnNoContentWhenNoVirtualDatacenterCreated()
    {
        Enterprise enterprise = enterpriseGenerator.createUniqueInstance();
        setup(enterprise);

        ClientResponse response = get(resolveEnterpriseActionGetIPsURI(enterprise.getId()));
        assertEquals(response.getStatusCode(), Status.OK.getStatusCode());
        IpsPoolManagementDto ips = response.getEntity(IpsPoolManagementDto.class);
        assertNotNull(ips);
        assertTrue(ips.getCollection().isEmpty());
    }

    @Test
    public void getVirtualMachinesByEnterprise()
    {
        VirtualMachine vm = vmGenerator.createUniqueInstance();

        List<Object> entitiesToSetup = new ArrayList<Object>();

        entitiesToSetup.add(vm.getEnterprise());

        for (Privilege p : vm.getUser().getRole().getPrivileges())
        {
            entitiesToSetup.add(p);
        }

        entitiesToSetup.add(vm.getUser().getRole());
        entitiesToSetup.add(vm.getUser());
        entitiesToSetup.add(vm.getHypervisor().getMachine().getDatacenter());
        entitiesToSetup.add(vm.getHypervisor().getMachine().getRack());
        entitiesToSetup.add(vm.getHypervisor().getMachine());
        entitiesToSetup.add(vm.getHypervisor());
        entitiesToSetup.add(vm.getVirtualImage().getEnterprise());
        entitiesToSetup.add(vm.getVirtualImage());
        entitiesToSetup.add(vm);

        setup(entitiesToSetup.toArray());

        String uri = resolveEnterpriseActionGetVirtualMachinesURI(vm.getEnterprise().getId());

        Machine m = vm.getHypervisor().getMachine();
        Enterprise e = vm.getEnterprise();
        User u = vm.getUser();

        ClientResponse response = get(uri, "sysadmin", "sysadmin");
        Assert.assertEquals(response.getStatusCode(), 200);

        VirtualMachinesDto vms = response.getEntity(VirtualMachinesDto.class);

        Assert.assertEquals(vms.getCollection().size(), 1);

        VirtualMachineDto vmDto = vms.getCollection().get(0);

        assertLinkExist(vmDto, resolveEnterpriseURI(e.getId()), "enterprise");
        assertLinkExist(vmDto, resolveUserURI(e.getId(), u.getId()), "user");
        assertLinkExist(vmDto,
            resolveMachineURI(m.getDatacenter().getId(), m.getRack().getId(), m.getId()), "machine");
    }
}
