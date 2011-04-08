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
import static com.abiquo.api.common.UriTestResolver.resolveEnterpriseActionGetIPsURI;
import static com.abiquo.api.common.UriTestResolver.resolveEnterpriseURI;
import static com.abiquo.api.common.UriTestResolver.resolvePrivateNetworksURI;
import static com.abiquo.api.common.UriTestResolver.resolveVirtualAppliancesURI;
import static com.abiquo.api.common.UriTestResolver.resolveVirtualDatacenterActionGetIPsURI;
import static com.abiquo.api.common.UriTestResolver.resolveVirtualDatacenterURI;
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
import org.testng.annotations.Test;

import com.abiquo.api.exceptions.APIError;
import com.abiquo.api.resources.AbstractJpaGeneratorIT;
import com.abiquo.api.resources.AbstractResource;
import com.abiquo.model.enumerator.RemoteServiceType;
import com.abiquo.model.transport.error.ErrorsDto;
import com.abiquo.server.core.cloud.VirtualAppliance;
import com.abiquo.server.core.cloud.VirtualDatacenter;
import com.abiquo.server.core.cloud.VirtualDatacenterDto;
import com.abiquo.server.core.infrastructure.RemoteService;
import com.abiquo.server.core.infrastructure.management.RasdManagement;
import com.abiquo.server.core.infrastructure.network.IpPoolManagement;
import com.abiquo.server.core.infrastructure.network.IpsPoolManagementDto;
import com.abiquo.server.core.infrastructure.network.VLANNetwork;
import com.abiquo.server.core.util.network.IPAddress;
import com.abiquo.server.core.util.network.IPNetworkRang;

public class VirtualDatacenterResourceIT extends AbstractJpaGeneratorIT
{

    @Test
    public void getVirtualDatacenterDoesntExist() throws ClientWebException
    {
        Resource resource = client.resource(resolveVirtualDatacenterURI(12345));

        ClientResponse response = resource.accept(MediaType.APPLICATION_XML).get();
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
        assertLinkExist(dto, resolvePrivateNetworksURI(vdc.getId()), PrivateNetworksResource.PRIVATE_NETWORKS_PATH);
        assertLinkExist(dto, resolveVirtualDatacenterURI(vdc.getId()), "edit");
        assertLinkExist(dto, resolveVirtualAppliancesURI(vdc.getId()), VirtualApplianceResource.VIRTUAL_APPLIANCE);
        assertLinkExist(dto, resolveVirtualDatacenterActionGetIPsURI(vdc.getId()), "action", IpAddressesResource.IP_ADDRESSES);
    }

    private VirtualDatacenterDto getValidVdc(VirtualDatacenter vdc)
    {
        ClientResponse response = get(resolveVirtualDatacenterURI(vdc.getId()));

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

        ClientResponse response = put(resolveVirtualDatacenterURI(vdc.getId()), dto);
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

        ClientResponse response = put(resolveVirtualDatacenterURI(vdc.getId()), dto);
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

        ClientResponse response = delete(resolveVirtualDatacenterURI(vdc.getId()));
        assertEquals(response.getStatusCode(), 204);
    }

    @Test
    public void deleteVirtualDatacenterFailsWhenContainsVirtualAppliances()
    {
        VirtualAppliance vapp = virtualApplianceGenerator.createUniqueInstance();
        VirtualDatacenter vdc = vapp.getVirtualDatacenter();
        setup(vdc.getDatacenter(), vdc.getEnterprise(), vdc.getNetwork(), vdc, vapp);

        ClientResponse response = delete(resolveVirtualDatacenterURI(vdc.getId()));
        assertErrors(response, APIError.VIRTUAL_DATACENTER_CONTAINS_VIRTUAL_APPLIANCES);
    }

    @Test
    public void deleteVirtualDatacenterFailsWhenHasVolumesAttached()
    {
        RasdManagement rasd = rasdGenerator.createInstance("8");
        VirtualDatacenter vdc = rasd.getVirtualDatacenter();
        setup(vdc.getDatacenter(), vdc.getEnterprise(), vdc.getNetwork(), vdc, rasd);

        ClientResponse response = delete(resolveVirtualDatacenterURI(vdc.getId()));
        assertErrors(response, APIError.VIRTUAL_DATACENTER_CONTAINS_RESOURCES);
    }

    @Test
    public void deleteVirtualDatacenterWithIps()
    {
        VirtualDatacenter vdc = vdcGenerator.createUniqueInstance();
        IpPoolManagement ip = ipGenerator.createInstance(vdc, vdc.getNetwork());
        RemoteService rs =
            remoteServiceGenerator.createInstance(RemoteServiceType.DHCP_SERVICE, vdc
                .getDatacenter());
        ip.getDhcp().setRemoteService(rs);

        setup(vdc.getDatacenter(), vdc.getEnterprise(), vdc.getNetwork(), vdc, rs, ip.getDhcp(), ip
            .getVlanNetwork().getConfiguration(), ip.getVlanNetwork(), ip);

        ClientResponse response = delete(resolveVirtualDatacenterURI(vdc.getId()));
        assertEquals(response.getStatusCode(), 204);
    }
    
    // TESTS refered to the action of GET IPs by VDC
    
    /**
     * Create a VirtualDatacenter without IPs and check the 'HTTP Conflict' error
     */
    @Test
    public void getVirtualDatacenterRaises409ErrorWhenHasVLANsWithoutIPs()
    {
        RemoteService rs = remoteServiceGenerator.createInstance(RemoteServiceType.DHCP_SERVICE);
        VirtualDatacenter vdc = vdcGenerator.createInstance(rs.getDatacenter());
        setup(vdc.getDatacenter(), rs, vdc.getEnterprise(), vdc.getNetwork(), vdc);
        VLANNetwork vlan = vlanGenerator.createInstance(vdc.getNetwork(), rs);
        setup(vlan.getConfiguration().getDhcp(), vlan.getConfiguration(), vlan);
        String validURI = resolveVirtualDatacenterActionGetIPsURI(vdc.getId());

        Resource resource = client.resource(validURI);

        ClientResponse response = resource.accept(MediaType.APPLICATION_XML).get();
        assertEquals(Status.CONFLICT.getStatusCode(), response.getStatusCode());
    }
    
    /**
     * Check if the link of the action when GET a VDC exists 
     */
    @Test
    public void getPrivateNetworkIPsByVirtualDatacenter()
    {
        RemoteService rs = remoteServiceGenerator.createInstance(RemoteServiceType.DHCP_SERVICE);
        VirtualDatacenter vdc = vdcGenerator.createInstance(rs.getDatacenter());
        setup(vdc.getDatacenter(), rs, vdc.getEnterprise(), vdc.getNetwork(), vdc);
        VLANNetwork vlan = vlanGenerator.createInstance(vdc.getNetwork(), rs, "255.255.255.0");
        setup(vlan.getConfiguration().getDhcp(), vlan.getConfiguration(), vlan);

        IPAddress ip = IPAddress.newIPAddress(vlan.getConfiguration().getAddress()).nextIPAddress();
        IPAddress lastIP =
            IPNetworkRang.lastIPAddressWithNumNodes(IPAddress.newIPAddress(vlan.getConfiguration().getAddress()),
                IPNetworkRang.masktoNumberOfNodes(vlan.getConfiguration().getMask()));
        List<IpPoolManagement> ips = new ArrayList<IpPoolManagement>();
        while (!ip.equals(lastIP))
        {
            IpPoolManagement ippool = ipGenerator.createInstance(vdc, vlan, ip.toString());
            ips.add(ippool);
            ip = ip.nextIPAddress();
        }
        setup(ips.toArray());
        
        
        VLANNetwork vlan2 = vlanGenerator.createInstance(vdc.getNetwork(), rs, "255.255.255.0");
        setup(vlan2.getConfiguration().getDhcp(), vlan2.getConfiguration(), vlan2);

        IPAddress ip2 = IPAddress.newIPAddress(vlan2.getConfiguration().getAddress()).nextIPAddress();
        IPAddress lastIP2 =
            IPNetworkRang.lastIPAddressWithNumNodes(IPAddress.newIPAddress(vlan2.getConfiguration().getAddress()),
                IPNetworkRang.masktoNumberOfNodes(vlan2.getConfiguration().getMask()));
        while (!ip2.equals(lastIP2))
        {
            IpPoolManagement ippool = ipGenerator.createInstance(vdc, vlan2, ip2.toString());
            setup(ippool);
            ip2 = ip2.nextIPAddress();
        }
        

        String validURI = resolveVirtualDatacenterActionGetIPsURI(vdc.getId());
        Resource resource = client.resource(validURI);

        ClientResponse response = resource.accept(MediaType.APPLICATION_XML).get();
        assertEquals(200, response.getStatusCode());

    }
    
    /**
     * Check if the request 'action/ips' of the virtualdatacenter resource allows the 'by=ip' query param
     */
    @Test
    public void getPrivateNetworkIPsByVirtualDatacenterOrderByIp()
    {
        RemoteService rs = remoteServiceGenerator.createInstance(RemoteServiceType.DHCP_SERVICE);
        VirtualDatacenter vdc = vdcGenerator.createInstance(rs.getDatacenter());
        setup(vdc.getDatacenter(), rs, vdc.getEnterprise(), vdc.getNetwork(), vdc);
        VLANNetwork vlan = vlanGenerator.createInstance(vdc.getNetwork(), rs, "255.255.255.0");
        setup(vlan.getConfiguration().getDhcp(), vlan.getConfiguration(), vlan);

        IPAddress ip = IPAddress.newIPAddress(vlan.getConfiguration().getAddress()).nextIPAddress();
        IPAddress lastIP =
            IPNetworkRang.lastIPAddressWithNumNodes(IPAddress.newIPAddress(vlan.getConfiguration().getAddress()),
                IPNetworkRang.masktoNumberOfNodes(vlan.getConfiguration().getMask()));
        List<IpPoolManagement> ips = new ArrayList<IpPoolManagement>();
        while (!ip.equals(lastIP))
        {
            IpPoolManagement ippool = ipGenerator.createInstance(vdc, vlan, ip.toString());
            ips.add(ippool);
            ip = ip.nextIPAddress();
        }
        setup(ips.toArray());
        
        String validURI = resolveVirtualDatacenterActionGetIPsURI(vdc.getId());
        validURI = validURI + "?by=ip";
        Resource resource = client.resource(validURI);

        ClientResponse response = resource.accept(MediaType.APPLICATION_XML).get();
        assertEquals(200, response.getStatusCode());
    }
    
    /**
     * Check if the request 'action/ips' of the virtualdatacenter resource allows the 'by=quarantine' query param
     */
    @Test
    public void getPrivateNetworkIPsByVirtualDatacenterOrderByQuarantine()
    {
        RemoteService rs = remoteServiceGenerator.createInstance(RemoteServiceType.DHCP_SERVICE);
        VirtualDatacenter vdc = vdcGenerator.createInstance(rs.getDatacenter());
        setup(vdc.getDatacenter(), rs, vdc.getEnterprise(), vdc.getNetwork(), vdc);
        VLANNetwork vlan = vlanGenerator.createInstance(vdc.getNetwork(), rs, "255.255.255.0");
        setup(vlan.getConfiguration().getDhcp(), vlan.getConfiguration(), vlan);

        IPAddress ip = IPAddress.newIPAddress(vlan.getConfiguration().getAddress()).nextIPAddress();
        IPAddress lastIP =
            IPNetworkRang.lastIPAddressWithNumNodes(IPAddress.newIPAddress(vlan.getConfiguration().getAddress()),
                IPNetworkRang.masktoNumberOfNodes(vlan.getConfiguration().getMask()));
        List<IpPoolManagement> ips = new ArrayList<IpPoolManagement>();
        while (!ip.equals(lastIP))
        {
            IpPoolManagement ippool = ipGenerator.createInstance(vdc, vlan, ip.toString());
            ips.add(ippool);
            ip = ip.nextIPAddress();
        }
        setup(ips.toArray());
        
        String validURI = resolveVirtualDatacenterActionGetIPsURI(vdc.getId());
        validURI = validURI + "?by=quarantine";
        Resource resource = client.resource(validURI);

        ClientResponse response = resource.accept(MediaType.APPLICATION_XML).get();
        assertEquals(200, response.getStatusCode());
    }
    
    /**
     * Check if the request 'action/ips' of the virtualdatacenter resource allows the 'by=mac' query param
     */
    @Test
    public void getPrivateNetworkIPsByVirtualDatacenterOrderByMAC()
    {
        RemoteService rs = remoteServiceGenerator.createInstance(RemoteServiceType.DHCP_SERVICE);
        VirtualDatacenter vdc = vdcGenerator.createInstance(rs.getDatacenter());
        setup(vdc.getDatacenter(), rs, vdc.getEnterprise(), vdc.getNetwork(), vdc);
        VLANNetwork vlan = vlanGenerator.createInstance(vdc.getNetwork(), rs, "255.255.255.0");
        setup(vlan.getConfiguration().getDhcp(), vlan.getConfiguration(), vlan);

        IPAddress ip = IPAddress.newIPAddress(vlan.getConfiguration().getAddress()).nextIPAddress();
        IPAddress lastIP =
            IPNetworkRang.lastIPAddressWithNumNodes(IPAddress.newIPAddress(vlan.getConfiguration().getAddress()),
                IPNetworkRang.masktoNumberOfNodes(vlan.getConfiguration().getMask()));
        List<IpPoolManagement> ips = new ArrayList<IpPoolManagement>();
        while (!ip.equals(lastIP))
        {
            IpPoolManagement ippool = ipGenerator.createInstance(vdc, vlan, ip.toString());
            ips.add(ippool);
            ip = ip.nextIPAddress();
        }
        setup(ips.toArray());
        
        String validURI = resolveVirtualDatacenterActionGetIPsURI(vdc.getId());
        validURI = validURI + "?by=mac";
        Resource resource = client.resource(validURI);

        ClientResponse response = resource.accept(MediaType.APPLICATION_XML).get();
        assertEquals(200, response.getStatusCode());
    }
    
    /**
     * Check if the request 'action/ips' of the virtualdatacenter resource allows the 'by=lease' query param
     */
    @Test
    public void getPrivateNetworkIPsByVirtualDatacenterOrderByLease()
    {
        RemoteService rs = remoteServiceGenerator.createInstance(RemoteServiceType.DHCP_SERVICE);
        VirtualDatacenter vdc = vdcGenerator.createInstance(rs.getDatacenter());
        setup(vdc.getDatacenter(), rs, vdc.getEnterprise(), vdc.getNetwork(), vdc);
        VLANNetwork vlan = vlanGenerator.createInstance(vdc.getNetwork(), rs, "255.255.255.0");
        setup(vlan.getConfiguration().getDhcp(), vlan.getConfiguration(), vlan);

        IPAddress ip = IPAddress.newIPAddress(vlan.getConfiguration().getAddress()).nextIPAddress();
        IPAddress lastIP =
            IPNetworkRang.lastIPAddressWithNumNodes(IPAddress.newIPAddress(vlan.getConfiguration().getAddress()),
                IPNetworkRang.masktoNumberOfNodes(vlan.getConfiguration().getMask()));
        List<IpPoolManagement> ips = new ArrayList<IpPoolManagement>();
        while (!ip.equals(lastIP))
        {
            IpPoolManagement ippool = ipGenerator.createInstance(vdc, vlan, ip.toString());
            ips.add(ippool);
            ip = ip.nextIPAddress();
        }
        setup(ips.toArray());
        
        String validURI = resolveVirtualDatacenterActionGetIPsURI(vdc.getId());
        validURI = validURI + "?by=lease";
        Resource resource = client.resource(validURI);

        ClientResponse response = resource.accept(MediaType.APPLICATION_XML).get();
        assertEquals(200, response.getStatusCode());
    }
    
    /**
     * Check if the request 'action/ips' of the virtualdatacenter resource allows the 'by=vlan' query param
     */
    @Test
    public void getPrivateNetworkIPsByVirtualDatacenterOrderByVlan()
    {
        RemoteService rs = remoteServiceGenerator.createInstance(RemoteServiceType.DHCP_SERVICE);
        VirtualDatacenter vdc = vdcGenerator.createInstance(rs.getDatacenter());
        setup(vdc.getDatacenter(), rs, vdc.getEnterprise(), vdc.getNetwork(), vdc);
        VLANNetwork vlan = vlanGenerator.createInstance(vdc.getNetwork(), rs, "255.255.255.0");
        setup(vlan.getConfiguration().getDhcp(), vlan.getConfiguration(), vlan);

        IPAddress ip = IPAddress.newIPAddress(vlan.getConfiguration().getAddress()).nextIPAddress();
        IPAddress lastIP =
            IPNetworkRang.lastIPAddressWithNumNodes(IPAddress.newIPAddress(vlan.getConfiguration().getAddress()),
                IPNetworkRang.masktoNumberOfNodes(vlan.getConfiguration().getMask()));
        List<IpPoolManagement> ips = new ArrayList<IpPoolManagement>();
        while (!ip.equals(lastIP))
        {
            IpPoolManagement ippool = ipGenerator.createInstance(vdc, vlan, ip.toString());
            ips.add(ippool);
            ip = ip.nextIPAddress();
        }
        setup(ips.toArray());
        
        String validURI = resolveVirtualDatacenterActionGetIPsURI(vdc.getId());
        validURI = validURI + "?by=vlan";
        Resource resource = client.resource(validURI);

        ClientResponse response = resource.accept(MediaType.APPLICATION_XML).get();
        assertEquals(200, response.getStatusCode());
    }
    
    /**
     * Check if the request 'action/ips' of the virtualdatacenter resource allows the 'by=virtualdatacenter' query param
     */
    @Test
    public void getPrivateNetworkIPsByVirtualDatacenterOrderByVirtualDatacenter()
    {
        RemoteService rs = remoteServiceGenerator.createInstance(RemoteServiceType.DHCP_SERVICE);
        VirtualDatacenter vdc = vdcGenerator.createInstance(rs.getDatacenter());
        setup(vdc.getDatacenter(), rs, vdc.getEnterprise(), vdc.getNetwork(), vdc);
        VLANNetwork vlan = vlanGenerator.createInstance(vdc.getNetwork(), rs, "255.255.255.0");
        setup(vlan.getConfiguration().getDhcp(), vlan.getConfiguration(), vlan);

        IPAddress ip = IPAddress.newIPAddress(vlan.getConfiguration().getAddress()).nextIPAddress();
        IPAddress lastIP =
            IPNetworkRang.lastIPAddressWithNumNodes(IPAddress.newIPAddress(vlan.getConfiguration().getAddress()),
                IPNetworkRang.masktoNumberOfNodes(vlan.getConfiguration().getMask()));
        List<IpPoolManagement> ips = new ArrayList<IpPoolManagement>();
        while (!ip.equals(lastIP))
        {
            IpPoolManagement ippool = ipGenerator.createInstance(vdc, vlan, ip.toString());
            ips.add(ippool);
            ip = ip.nextIPAddress();
        }
        setup(ips.toArray());
        
        String validURI = resolveVirtualDatacenterActionGetIPsURI(vdc.getId());
        validURI = validURI + "?by=virtualdatacenter";
        Resource resource = client.resource(validURI);

        ClientResponse response = resource.accept(MediaType.APPLICATION_XML).get();
        assertEquals(200, response.getStatusCode());
    }
    
    /**
     * Check if the request 'action/ips' of the virtualdatacenter resource allows the 'by=virtualmachine' query param
     */
    @Test
    public void getPrivateNetworkIPsByVirtualDatacenterOrderByVirtualMachine()
    {
        RemoteService rs = remoteServiceGenerator.createInstance(RemoteServiceType.DHCP_SERVICE);
        VirtualDatacenter vdc = vdcGenerator.createInstance(rs.getDatacenter());
        setup(vdc.getDatacenter(), rs, vdc.getEnterprise(), vdc.getNetwork(), vdc);
        VLANNetwork vlan = vlanGenerator.createInstance(vdc.getNetwork(), rs, "255.255.255.0");
        setup(vlan.getConfiguration().getDhcp(), vlan.getConfiguration(), vlan);

        IPAddress ip = IPAddress.newIPAddress(vlan.getConfiguration().getAddress()).nextIPAddress();
        IPAddress lastIP =
            IPNetworkRang.lastIPAddressWithNumNodes(IPAddress.newIPAddress(vlan.getConfiguration().getAddress()),
                IPNetworkRang.masktoNumberOfNodes(vlan.getConfiguration().getMask()));
        List<IpPoolManagement> ips = new ArrayList<IpPoolManagement>();
        while (!ip.equals(lastIP))
        {
            IpPoolManagement ippool = ipGenerator.createInstance(vdc, vlan, ip.toString());
            ips.add(ippool);
            ip = ip.nextIPAddress();
        }
        setup(ips.toArray());
        
        String validURI = resolveVirtualDatacenterActionGetIPsURI(vdc.getId());
        validURI = validURI + "?by=virtualmachine";
        Resource resource = client.resource(validURI);

        ClientResponse response = resource.accept(MediaType.APPLICATION_XML).get();
        assertEquals(200, response.getStatusCode());
    }
    
    /**
     * Check if the request 'action/ips' of the virtualdatacenter resource allows the 'by=virtualappliance' query param
     */
    @Test
    public void getPrivateNetworkIPsByVirtualDatacenterOrderByVirtualAppliance()
    {
        RemoteService rs = remoteServiceGenerator.createInstance(RemoteServiceType.DHCP_SERVICE);
        VirtualDatacenter vdc = vdcGenerator.createInstance(rs.getDatacenter());
        setup(vdc.getDatacenter(), rs, vdc.getEnterprise(), vdc.getNetwork(), vdc);
        VLANNetwork vlan = vlanGenerator.createInstance(vdc.getNetwork(), rs, "255.255.255.0");
        setup(vlan.getConfiguration().getDhcp(), vlan.getConfiguration(), vlan);

        IPAddress ip = IPAddress.newIPAddress(vlan.getConfiguration().getAddress()).nextIPAddress();
        IPAddress lastIP =
            IPNetworkRang.lastIPAddressWithNumNodes(IPAddress.newIPAddress(vlan.getConfiguration().getAddress()),
                IPNetworkRang.masktoNumberOfNodes(vlan.getConfiguration().getMask()));
        List<IpPoolManagement> ips = new ArrayList<IpPoolManagement>();
        while (!ip.equals(lastIP))
        {
            IpPoolManagement ippool = ipGenerator.createInstance(vdc, vlan, ip.toString());
            ips.add(ippool);
            ip = ip.nextIPAddress();
        }
        setup(ips.toArray());
        
        String validURI = resolveVirtualDatacenterActionGetIPsURI(vdc.getId());
        validURI = validURI + "?by=virtualappliance";
        Resource resource = client.resource(validURI);

        ClientResponse response = resource.accept(MediaType.APPLICATION_XML).get();
        assertEquals(200, response.getStatusCode());
    }
    
    /**
     * Check if the request 'action/ips' of the virtualdatacenter resource allows the 'by=virtualappliance' query param
     */
    @Test(enabled=false)
    public void getPrivateNetworkIPsByVirtualDatacenterTestLimit()
    {
        RemoteService rs = remoteServiceGenerator.createInstance(RemoteServiceType.DHCP_SERVICE);
        VirtualDatacenter vdc = vdcGenerator.createInstance(rs.getDatacenter());
        setup(vdc.getDatacenter(), rs, vdc.getEnterprise(), vdc.getNetwork(), vdc);
        VLANNetwork vlan = vlanGenerator.createInstance(vdc.getNetwork(), rs, "255.255.255.0");
        setup(vlan.getConfiguration().getDhcp(), vlan.getConfiguration(), vlan);

        IPAddress ip = IPAddress.newIPAddress(vlan.getConfiguration().getAddress()).nextIPAddress();
        IPAddress lastIP =
            IPNetworkRang.lastIPAddressWithNumNodes(IPAddress.newIPAddress(vlan.getConfiguration().getAddress()),
                IPNetworkRang.masktoNumberOfNodes(vlan.getConfiguration().getMask()));
        List<IpPoolManagement> ips = new ArrayList<IpPoolManagement>();
        while (!ip.equals(lastIP))
        {
            IpPoolManagement ippool = ipGenerator.createInstance(vdc, vlan, ip.toString());
            ips.add(ippool);
            ip = ip.nextIPAddress();
        }
        setup(ips.toArray());
        
        // Test Default
        String validURI = resolveVirtualDatacenterActionGetIPsURI(vdc.getId());
        Resource resource = client.resource(validURI);
        ClientResponse response = resource.accept(MediaType.APPLICATION_XML).get();
        IpsPoolManagementDto entity = response.getEntity(IpsPoolManagementDto.class);
        assertEquals(200, response.getStatusCode());
        assertNotNull(entity);
        assertNotNull(entity.getCollection());
        assertEquals(Integer.valueOf(entity.getCollection().size()), AbstractResource.DEFAULT_PAGE_LENGTH);
        
        // Test 30
        validURI = resolveVirtualDatacenterActionGetIPsURI(vdc.getId());
        validURI = validURI + "?limit=30";
        resource = client.resource(validURI);
        response = resource.accept(MediaType.APPLICATION_XML).get();
        assertEquals(200, response.getStatusCode());
        assertNotNull(entity);
        assertNotNull(entity.getCollection());
        assertEquals(entity.getCollection().size(), 30);
        
        // Test 120
        validURI = resolveVirtualDatacenterActionGetIPsURI(vdc.getId());
        validURI = validURI + "?limit=120";
        resource = client.resource(validURI);
        response = resource.accept(MediaType.APPLICATION_XML).get();
        assertEquals(200, response.getStatusCode());
        assertNotNull(entity);
        assertNotNull(entity.getCollection());
        assertEquals(entity.getCollection().size(), 120);
        
    }
    
    /**
     * Check if the request 'action/ips' of the virtualdatacenter resource doesnt allow a 'by={randomvalue}' query param
     */
    @Test
    public void getPrivateNetworkIPsByVirtualDatacenterRaises400WhenOrderByRandomParameter()
    {
        RemoteService rs = remoteServiceGenerator.createInstance(RemoteServiceType.DHCP_SERVICE);
        VirtualDatacenter vdc = vdcGenerator.createInstance(rs.getDatacenter());
        setup(vdc.getDatacenter(), rs, vdc.getEnterprise(), vdc.getNetwork(), vdc);
        VLANNetwork vlan = vlanGenerator.createInstance(vdc.getNetwork(), rs, "255.255.255.0");
        setup(vlan.getConfiguration().getDhcp(), vlan.getConfiguration(), vlan);

        IPAddress ip = IPAddress.newIPAddress(vlan.getConfiguration().getAddress()).nextIPAddress();
        IPAddress lastIP =
            IPNetworkRang.lastIPAddressWithNumNodes(IPAddress.newIPAddress(vlan.getConfiguration().getAddress()),
                IPNetworkRang.masktoNumberOfNodes(vlan.getConfiguration().getMask()));
        List<IpPoolManagement> ips = new ArrayList<IpPoolManagement>();
        while (!ip.equals(lastIP))
        {
            IpPoolManagement ippool = ipGenerator.createInstance(vdc, vlan, ip.toString());
            ips.add(ippool);
            ip = ip.nextIPAddress();
        }
        setup(ips.toArray());
        
        String validURI = resolveVirtualDatacenterActionGetIPsURI(vdc.getId());
        validURI = validURI + "?by=" + Integer.valueOf(new Random().nextInt());
        Resource resource = client.resource(validURI);

        ClientResponse response = resource.accept(MediaType.APPLICATION_XML).get();
        assertEquals(400, response.getStatusCode());
    }
    
}
