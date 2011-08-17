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

import static com.abiquo.api.common.Assert.assertErrors;
import static com.abiquo.api.common.Assert.assertLinkExist;
import static com.abiquo.api.common.Assert.assertNonEmptyErrors;
import static com.abiquo.api.common.UriTestResolver.resolveDatacenterURI;
import static com.abiquo.api.common.UriTestResolver.resolveEnterpriseURI;
import static com.abiquo.api.common.UriTestResolver.resolvePrivateNetworksURI;
import static com.abiquo.api.common.UriTestResolver.resolveVirtualAppliancesURI;
import static com.abiquo.api.common.UriTestResolver.resolveVirtualDatacenterActionGetDHCPInfoURI;
import static com.abiquo.api.common.UriTestResolver.resolveVirtualDatacenterActionGetIPsURI;
import static com.abiquo.api.common.UriTestResolver.resolveVirtualDatacenterURI;
import static com.abiquo.testng.TestConfig.BASIC_INTEGRATION_TESTS;
import static com.abiquo.testng.TestConfig.NETWORK_INTEGRATION_TESTS;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response.Status;

import org.apache.wink.client.ClientResponse;
import org.apache.wink.client.ClientWebException;
import org.apache.wink.client.Resource;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import com.abiquo.api.exceptions.APIError;
import com.abiquo.api.resources.AbstractJpaGeneratorIT;
import com.abiquo.api.resources.AbstractResource;
import com.abiquo.model.enumerator.RemoteServiceType;
import com.abiquo.model.transport.error.ErrorsDto;
import com.abiquo.server.core.cloud.VirtualAppliance;
import com.abiquo.server.core.cloud.VirtualDatacenter;
import com.abiquo.server.core.cloud.VirtualDatacenterDto;
import com.abiquo.server.core.enterprise.Enterprise;
import com.abiquo.server.core.enterprise.Privilege;
import com.abiquo.server.core.enterprise.Role;
import com.abiquo.server.core.enterprise.User;
import com.abiquo.server.core.infrastructure.RemoteService;
import com.abiquo.server.core.infrastructure.network.IpPoolManagement;
import com.abiquo.server.core.infrastructure.network.IpsPoolManagementDto;
import com.abiquo.server.core.infrastructure.network.VLANNetwork;
import com.abiquo.server.core.infrastructure.storage.VolumeManagement;
import com.abiquo.server.core.util.network.IPAddress;
import com.abiquo.server.core.util.network.IPNetworkRang;

public class VirtualDatacenterResourceIT extends AbstractJpaGeneratorIT
{

    private static final String SYSADMIN = "sysadmin";

    @BeforeMethod(groups = {BASIC_INTEGRATION_TESTS, NETWORK_INTEGRATION_TESTS})
    public void setupSysadmin()
    {
        Enterprise e = enterpriseGenerator.createUniqueInstance();
        Role r = roleGenerator.createInstanceSysAdmin("sysRole");
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

    @Override
    @AfterMethod(groups = {BASIC_INTEGRATION_TESTS, NETWORK_INTEGRATION_TESTS})
    public void tearDown()
    {
        super.tearDown();
    }

    @Test
    public void getVirtualDatacenterDoesntExist() throws ClientWebException
    {
        ClientResponse response =
            get(resolveVirtualDatacenterURI(12345), SYSADMIN, SYSADMIN, MediaType.APPLICATION_XML);
        assertEquals(404, response.getStatusCode());

        ErrorsDto errors = response.getEntity(ErrorsDto.class);
        assertNonEmptyErrors(errors);
    }

    @Test
    public void getVirtualDatacenter() throws Exception
    {
        VirtualDatacenter vdc = vdcGenerator.createUniqueInstance();
        setup(vdc.getDatacenter(), vdc.getEnterprise(), vdc.getNetwork(), vdc);

        VirtualDatacenterDto dto = getValidVdc(vdc);
        assertNotNull(dto);

        assertLinkExist(dto, resolveDatacenterURI(vdc.getDatacenter().getId()), "datacenter");
        assertLinkExist(dto, resolveEnterpriseURI(vdc.getEnterprise().getId()), "enterprise");
        assertLinkExist(dto, resolvePrivateNetworksURI(vdc.getId()),
            PrivateNetworksResource.PRIVATE_NETWORKS_PATH);
        assertLinkExist(dto, resolveVirtualDatacenterURI(vdc.getId()), "edit");
        assertLinkExist(dto, resolveVirtualAppliancesURI(vdc.getId()),
            VirtualApplianceResource.VIRTUAL_APPLIANCE);
        assertLinkExist(dto, resolveVirtualDatacenterActionGetIPsURI(vdc.getId()), "action",
            IpAddressesResource.IP_ADDRESSES);
    }

    private VirtualDatacenterDto getValidVdc(final VirtualDatacenter vdc)
    {
        ClientResponse response = get(resolveVirtualDatacenterURI(vdc.getId()), SYSADMIN, SYSADMIN);

        assertEquals(200, response.getStatusCode());

        VirtualDatacenterDto dto = response.getEntity(VirtualDatacenterDto.class);
        return dto;
    }

    @Test
    public void updateVirtualDatacenter()
    {
        VirtualDatacenter vdc = vdcGenerator.createUniqueInstance();
        setup(vdc.getDatacenter(), vdc.getEnterprise(), vdc.getNetwork(), vdc);

        VirtualDatacenterDto dto = getValidVdc(vdc);

        dto.setName("vdc_name_updated");

        ClientResponse response =
            put(resolveVirtualDatacenterURI(vdc.getId()), dto, SYSADMIN, SYSADMIN);
        assertEquals(response.getStatusCode(), 200);

        VirtualDatacenterDto responseDto = response.getEntity(VirtualDatacenterDto.class);
        assertEquals(responseDto.getName(), dto.getName());
    }

    @Test
    public void updateVirtualDatacenterModifyLimits()
    {
        VirtualDatacenter vdc = vdcGenerator.createUniqueInstance();
        setup(vdc.getDatacenter(), vdc.getEnterprise(), vdc.getNetwork(), vdc);

        VirtualDatacenterDto dto = getValidVdc(vdc);
        dto.setCpuCountLimits(1000, 1001);

        ClientResponse response =
            put(resolveVirtualDatacenterURI(vdc.getId()), dto, SYSADMIN, SYSADMIN);
        assertEquals(response.getStatusCode(), 200);

        VirtualDatacenterDto dto2 = getValidVdc(vdc);
        assertEquals(dto2.getCpuCountSoftLimit(), dto.getCpuCountSoftLimit());
        assertEquals(dto2.getCpuCountHardLimit(), dto.getCpuCountHardLimit());
    }

    @Test
    public void deleteVirtualDatacenter()
    {
        VirtualDatacenter vdc = vdcGenerator.createUniqueInstance();
        setup(vdc.getDatacenter(), vdc.getEnterprise(), vdc.getNetwork(), vdc);

        ClientResponse response =
            delete(resolveVirtualDatacenterURI(vdc.getId()), SYSADMIN, SYSADMIN);
        assertEquals(response.getStatusCode(), 204);
    }

    @Test
    public void deleteVirtualDatacenterFailsWhenContainsVirtualAppliances()
    {
        VirtualAppliance vapp = virtualApplianceGenerator.createUniqueInstance();
        VirtualDatacenter vdc = vapp.getVirtualDatacenter();
        setup(vdc.getDatacenter(), vdc.getEnterprise(), vdc.getNetwork(), vdc, vapp);

        ClientResponse response =
            delete(resolveVirtualDatacenterURI(vdc.getId()), SYSADMIN, SYSADMIN);
        assertErrors(response, 409, APIError.VIRTUAL_DATACENTER_CONTAINS_VIRTUAL_APPLIANCES);
    }

    @Test
    public void deleteVirtualDatacenterFailsWhenHasVolumesAttached()
    {
        VolumeManagement volume = volumeManagementGenerator.createUniqueInstance();
        VirtualDatacenter vdc = volume.getVirtualDatacenter();

        List<Object> entitiesToPersist = new ArrayList<Object>();
        volumeManagementGenerator.addAuxiliaryEntitiesToPersist(volume, entitiesToPersist);
        entitiesToPersist.add(volume);

        setup(entitiesToPersist.toArray());

        ClientResponse response =
            delete(resolveVirtualDatacenterURI(vdc.getId()), SYSADMIN, SYSADMIN);
        assertErrors(response, 409, APIError.VIRTUAL_DATACENTER_CONTAINS_RESOURCES);
    }

    @Test(groups = {NETWORK_INTEGRATION_TESTS})
    public void deleteVirtualDatacenterWithIps()
    {
        VirtualDatacenter vdc = vdcGenerator.createUniqueInstance();
        IpPoolManagement ip = ipGenerator.createInstance(vdc, vdc.getNetwork());
        RemoteService rs =
            remoteServiceGenerator.createInstance(RemoteServiceType.DHCP_SERVICE,
                vdc.getDatacenter());
        ip.getDhcp().setRemoteService(rs);

        List<Object> entitiesToPersist = new ArrayList<Object>();
        ipGenerator.addAuxiliaryEntitiesToPersist(ip, entitiesToPersist);
        entitiesToPersist.add(ip);

        setup(entitiesToPersist.toArray());

        ClientResponse response =
            delete(resolveVirtualDatacenterURI(vdc.getId()), SYSADMIN, SYSADMIN);
        assertEquals(response.getStatusCode(), 204);
    }

    // TESTS refered to the action of GET IPs by VDC

    /**
     * Create a VirtualDatacenter without IPs and check the 'HTTP Conflict' error
     */
    @Test
    public void getVirtualDatacenterEmptyListWhenHasVLANsWithoutIPs()
    {
        RemoteService rs = remoteServiceGenerator.createInstance(RemoteServiceType.DHCP_SERVICE);
        VirtualDatacenter vdc = vdcGenerator.createInstance(rs.getDatacenter());
        setup(vdc.getDatacenter(), rs, vdc.getEnterprise(), vdc.getNetwork(), vdc);
        VLANNetwork vlan = vlanGenerator.createInstance(vdc.getNetwork(), rs);
        setup(vlan.getConfiguration().getDhcp(), vlan.getConfiguration(), vlan);
        String validURI = resolveVirtualDatacenterActionGetIPsURI(vdc.getId());

        ClientResponse response = get(validURI, SYSADMIN, SYSADMIN, MediaType.APPLICATION_XML);
        assertEquals(200, response.getStatusCode());
    }

    /**
     * Check if the link of the action when GET a VDC exists
     */
    @Test(groups = {NETWORK_INTEGRATION_TESTS})
    public void getPrivateNetworkIPsByVirtualDatacenter()
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

        persistIP(ip, lastIP, vdc, vlan);

        VLANNetwork vlan2 = vlanGenerator.createInstance(vdc.getNetwork(), rs, "255.255.255.0");
        setup(vlan2.getConfiguration().getDhcp(), vlan2.getConfiguration(), vlan2);

        IPAddress ip2 =
            IPAddress.newIPAddress(vlan2.getConfiguration().getAddress()).nextIPAddress();
        IPAddress lastIP2 =
            IPNetworkRang.lastIPAddressWithNumNodes(
                IPAddress.newIPAddress(vlan2.getConfiguration().getAddress()),
                IPNetworkRang.masktoNumberOfNodes(vlan2.getConfiguration().getMask()));

        persistIP(ip2, lastIP2, vdc, vlan2);

        String validURI = resolveVirtualDatacenterActionGetIPsURI(vdc.getId());

        ClientResponse response = get(validURI);
        assertEquals(response.getStatusCode(), Status.OK.getStatusCode());

    }

    /**
     * Check if the request 'action/ips' of the virtualdatacenter resource allows the 'by=ip' query
     * param
     */
    @Test(groups = {NETWORK_INTEGRATION_TESTS})
    public void getPrivateNetworkIPsByVirtualDatacenterOrderByIp()
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

        persistIP(ip, lastIP, vdc, vlan);

        String validURI = resolveVirtualDatacenterActionGetIPsURI(vdc.getId());
        validURI = validURI + "?by=ip";

        ClientResponse response = get(validURI);
        assertEquals(response.getStatusCode(), Status.OK.getStatusCode());
    }

    /**
     * Check if the request 'action/ips' of the virtualdatacenter resource allows the
     * 'by=quarantine' query param
     */
    @Test(groups = {NETWORK_INTEGRATION_TESTS})
    public void getPrivateNetworkIPsByVirtualDatacenterOrderByQuarantine()
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

        persistIP(ip, lastIP, vdc, vlan);

        String validURI = resolveVirtualDatacenterActionGetIPsURI(vdc.getId());
        validURI = validURI + "?by=quarantine";

        ClientResponse response = get(validURI);
        assertEquals(response.getStatusCode(), Status.OK.getStatusCode());
    }

    /**
     * Check if the request 'action/ips' of the virtualdatacenter resource allows the 'by=mac' query
     * param
     */
    @Test(groups = {NETWORK_INTEGRATION_TESTS})
    public void getPrivateNetworkIPsByVirtualDatacenterOrderByMAC()
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

        persistIP(ip, lastIP, vdc, vlan);

        String validURI = resolveVirtualDatacenterActionGetIPsURI(vdc.getId());
        validURI = validURI + "?by=mac";

        ClientResponse response = get(validURI);
        assertEquals(response.getStatusCode(), Status.OK.getStatusCode());
    }

    /**
     * Check if the request 'action/ips' of the virtualdatacenter resource allows the 'by=lease'
     * query param
     */
    @Test(groups = {NETWORK_INTEGRATION_TESTS})
    public void getPrivateNetworkIPsByVirtualDatacenterOrderByLease()
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

        persistIP(ip, lastIP, vdc, vlan);

        String validURI = resolveVirtualDatacenterActionGetIPsURI(vdc.getId());
        validURI = validURI + "?by=lease";

        ClientResponse response = get(validURI);
        assertEquals(response.getStatusCode(), Status.OK.getStatusCode());
    }

    /**
     * Check if the request 'action/ips' of the virtualdatacenter resource allows the 'by=vlan'
     * query param
     */
    @Test(groups = {NETWORK_INTEGRATION_TESTS})
    public void getPrivateNetworkIPsByVirtualDatacenterOrderByVlan()
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

        persistIP(ip, lastIP, vdc, vlan);

        String validURI = resolveVirtualDatacenterActionGetIPsURI(vdc.getId());
        validURI = validURI + "?by=vlan";

        ClientResponse response = get(validURI);
        assertEquals(response.getStatusCode(), Status.OK.getStatusCode());
    }

    /**
     * Check if the request 'action/ips' of the virtualdatacenter resource allows the
     * 'by=virtualdatacenter' query param
     */
    @Test(groups = {NETWORK_INTEGRATION_TESTS})
    public void getPrivateNetworkIPsByVirtualDatacenterOrderByVirtualDatacenter()
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

        persistIP(ip, lastIP, vdc, vlan);

        String validURI = resolveVirtualDatacenterActionGetIPsURI(vdc.getId());
        validURI = validURI + "?by=virtualdatacenter";

        ClientResponse response = get(validURI);
        assertEquals(response.getStatusCode(), Status.OK.getStatusCode());
    }

    /**
     * Check if the request 'action/ips' of the virtualdatacenter resource allows the
     * 'by=virtualmachine' query param
     */
    @Test(groups = {NETWORK_INTEGRATION_TESTS})
    public void getPrivateNetworkIPsByVirtualDatacenterOrderByVirtualMachine()
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

        persistIP(ip, lastIP, vdc, vlan);

        String validURI = resolveVirtualDatacenterActionGetIPsURI(vdc.getId());
        validURI = validURI + "?by=virtualmachine";

        ClientResponse response = get(validURI);
        assertEquals(response.getStatusCode(), Status.OK.getStatusCode());
    }

    /**
     * Check if the request 'action/ips' of the virtualdatacenter resource allows the
     * 'by=virtualappliance' query param
     */
    @Test(groups = {NETWORK_INTEGRATION_TESTS})
    public void getPrivateNetworkIPsByVirtualDatacenterOrderByVirtualAppliance()
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

        persistIP(ip, lastIP, vdc, vlan);

        String validURI = resolveVirtualDatacenterActionGetIPsURI(vdc.getId());
        validURI = validURI + "?by=virtualappliance";

        ClientResponse response = get(validURI);
        assertEquals(response.getStatusCode(), Status.OK.getStatusCode());
    }

    /**
     * Check if the request 'action/ips' of the virtualdatacenter resource allows the
     * 'by=virtualappliance' query param
     */
    @Test(enabled = false)
    public void getPrivateNetworkIPsByVirtualDatacenterTestLimit()
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

        persistIP(ip, lastIP, vdc, vlan);

        // Test Default
        String validURI = resolveVirtualDatacenterActionGetIPsURI(vdc.getId());
        ClientResponse response = get(validURI);
        IpsPoolManagementDto entity = response.getEntity(IpsPoolManagementDto.class);
        assertEquals(200, response.getStatusCode());
        assertNotNull(entity);
        assertNotNull(entity.getCollection());
        assertEquals(Integer.valueOf(entity.getCollection().size()),
            AbstractResource.DEFAULT_PAGE_LENGTH);

        // Test 30
        validURI = resolveVirtualDatacenterActionGetIPsURI(vdc.getId());
        validURI = validURI + "?limit=30";
        response = get(validURI);
        assertEquals(200, response.getStatusCode());
        assertNotNull(entity);
        assertNotNull(entity.getCollection());
        assertEquals(entity.getCollection().size(), 30);

        // Test 120
        validURI = resolveVirtualDatacenterActionGetIPsURI(vdc.getId());
        validURI = validURI + "?limit=120";
        response = get(validURI);
        assertEquals(200, response.getStatusCode());
        assertNotNull(entity);
        assertNotNull(entity.getCollection());
        assertEquals(entity.getCollection().size(), 120);

    }

    /**
     * Check if the request 'action/ips' of the virtualdatacenter resource doesnt allow a
     * 'by={randomvalue}' query param
     */
    @Test(groups = {NETWORK_INTEGRATION_TESTS})
    public void getPrivateNetworkIPsByVirtualDatacenterRaises400WhenOrderByRandomParameter()
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

        persistIP(ip, lastIP, vdc, vlan);

        String validURI = resolveVirtualDatacenterActionGetIPsURI(vdc.getId());
        validURI = validURI + "?by=" + Integer.valueOf(new Random().nextInt());

        ClientResponse response = get(validURI);
        assertEquals(response.getStatusCode(), Status.BAD_REQUEST.getStatusCode());
    }

    // TESTS refered to the action of GET dhcpinfo by VDC

    @Test(groups = {NETWORK_INTEGRATION_TESTS})
    public void getdhcpInfoByVirtualDatacenter()
    {
        RemoteService rs = remoteServiceGenerator.createInstance(RemoteServiceType.DHCP_SERVICE);
        VirtualDatacenter vdc = vdcGenerator.createInstance(rs.getDatacenter());
        setup(vdc.getDatacenter(), rs, vdc.getEnterprise(), vdc.getNetwork(), vdc);
        String URI = resolveVirtualDatacenterActionGetDHCPInfoURI(vdc.getId());
        ClientResponse response = get(URI);
        assertEquals(response.getStatusCode(), Status.OK.getStatusCode());
        String entity = response.getEntity(String.class);
        assertNotNull(entity);
    }

    private void persistIP(IPAddress ip, final IPAddress lastIP, final VirtualDatacenter vdc,
        final VLANNetwork vlan)
    {
        List<Object> lists = new ArrayList<Object>();
        while (!ip.equals(lastIP))
        {
            IpPoolManagement ippool = ipGenerator.createInstance(vdc, vlan, ip.toString());
            lists.add(ippool);
            ip = ip.nextIPAddress();
        }
        setup(lists.toArray());
    }
}
