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

import static com.abiquo.api.common.UriTestResolver.resolvePrivateNetworkIPsURI;
import static com.abiquo.api.common.UriTestResolver.resolvePrivateNetworkURI;
import static com.abiquo.api.common.UriTestResolver.resolveVirtualDatacenterActionGetIPsURI;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;

import java.util.ArrayList;
import java.util.List;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response.Status;

import org.apache.wink.client.ClientResponse;
import org.apache.wink.client.Resource;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import com.abiquo.model.enumerator.RemoteServiceType;
import com.abiquo.server.core.cloud.VirtualDatacenter;
import com.abiquo.server.core.infrastructure.RemoteService;
import com.abiquo.server.core.infrastructure.network.IpPoolManagement;
import com.abiquo.server.core.infrastructure.network.IpsPoolManagementDto;
import com.abiquo.server.core.infrastructure.network.VLANNetwork;
import com.abiquo.server.core.util.network.IPAddress;
import com.abiquo.server.core.util.network.IPNetworkRang;

/**
 * Acceptance tests not only for the resource {@link IpAddressesResource} but all the resources that need an used/declared 
 * {@link IpPoolManagement} info.
 * 
 * @author jdevesa@abiquo.com
 */
public class IpAddressesResourceIT extends AbstractJpaGeneratorIT
{
    private String validURI;

    private String invalidNetworkURI = resolvePrivateNetworkURI(1, 12);

    private String invalidVDCURI = resolvePrivateNetworkURI(12, 1);

    VirtualDatacenter vdc;

    RemoteService rs;

    @BeforeMethod
    public void setup()
    {
        rs = remoteServiceGenerator.createInstance(RemoteServiceType.DHCP_SERVICE);
        vdc = vdcGenerator.createInstance(rs.getDatacenter());
        setup(vdc.getDatacenter(), rs, vdc.getEnterprise(), vdc.getNetwork(), vdc);

        invalidNetworkURI = resolvePrivateNetworkURI(vdc.getId(), 12);
        invalidVDCURI = resolvePrivateNetworkURI(12, 32452);

    }

    /**
     * Check a correct VLAN creation.
     */
    @Test(enabled=false)
    public void createAndGetPrivateNetworkIPsByVLAN()
    {
        // The mask indicates the number of 
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

        validURI = resolvePrivateNetworkIPsURI(vdc.getId(), vlan.getId());

        Resource resource = client.resource(validURI);

        ClientResponse response = resource.accept(MediaType.APPLICATION_XML).get();
        assertEquals(200, response.getStatusCode());

        IpsPoolManagementDto entity = response.getEntity(IpsPoolManagementDto.class);
        assertNotNull(entity);
        assertNotNull(entity.getCollection());
        assertEquals(entity.getCollection().size(), 25);

    }
    
    
    /**
     * Create a network without IPs and check the 'HTTP Conflict' error
     */
    @Test(enabled=false)
    public void createVLANRaisesErrorWhenWithoutIPs()
    {

        VLANNetwork vlan = vlanGenerator.createInstance(vdc.getNetwork(), rs);
        setup(vlan.getConfiguration().getDhcp(), vlan.getConfiguration(), vlan);
        validURI = resolvePrivateNetworkIPsURI(vdc.getId(), vlan.getId());

        Resource resource = client.resource(validURI);

        ClientResponse response = resource.accept(MediaType.APPLICATION_XML).get();
        assertEquals(Status.CONFLICT.getStatusCode(), response.getStatusCode());
    }
    
}
