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
import static com.abiquo.api.common.Assert.assertNonEmptyErrors;
import static com.abiquo.api.common.UriTestResolver.resolvePrivateNetworkURI;
import static com.abiquo.api.common.UriTestResolver.resolveVirtualDatacenterURI;
import static com.abiquo.api.common.UriTestResolver.resolvePrivateNetworkIPsURI;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;

import java.util.Random;

import javax.ws.rs.core.MediaType;

import org.apache.wink.client.ClientResponse;
import org.apache.wink.client.ClientWebException;
import org.apache.wink.client.Resource;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import com.abiquo.api.exceptions.APIError;
import com.abiquo.api.resources.cloud.IpAddressesResource;
import com.abiquo.api.resources.cloud.VirtualDatacenterResource;
import com.abiquo.model.enumerator.RemoteServiceType;
import com.abiquo.model.transport.error.ErrorsDto;
import com.abiquo.server.core.cloud.VirtualDatacenter;
import com.abiquo.server.core.enterprise.DatacenterLimits;
import com.abiquo.server.core.infrastructure.RemoteService;
import com.abiquo.server.core.infrastructure.network.VLANNetwork;
import com.abiquo.server.core.infrastructure.network.VLANNetworkDto;

public class PrivateNetworkResourceIT extends AbstractJpaGeneratorIT
{

    private String validURI;

    VirtualDatacenter vdc;
    VLANNetwork vlan;
    RemoteService rs;

    @BeforeMethod
    public void setup()
    {
        rs = remoteServiceGenerator.createInstance(RemoteServiceType.DHCP_SERVICE);
        vdc = vdcGenerator.createInstance(rs.getDatacenter());
        
        DatacenterLimits dclimit = new DatacenterLimits(vdc.getEnterprise(), vdc.getDatacenter());
        setup(vdc.getDatacenter(), rs, vdc.getEnterprise(), vdc.getNetwork(), vdc);
        vlan = vlanGenerator.createInstance(vdc.getNetwork(), rs);
        vlan.setEnterprise(vdc.getEnterprise());
        setup(vlan.getConfiguration().getDhcp(), vlan.getConfiguration(), vlan, dclimit);
        
        validURI = resolvePrivateNetworkURI(vdc.getId(), vlan.getId());
       
    }

    @Test
    public void getPrivateNetwork() throws Exception
    {
        Resource resource = client.resource(validURI);

        ClientResponse response = resource.accept(MediaType.APPLICATION_XML).get();
        VLANNetworkDto network = response.getEntity(VLANNetworkDto.class);

        assertEquals(200, response.getStatusCode());
        assertNotNull(network);
    }

    @Test
    public void getPrivateNetworkDoesntExist() throws Exception
    {
        String invalidNetworkURI = resolvePrivateNetworkURI(vdc.getId(), new Random().nextInt(1000));
        Resource resource = client.resource(invalidNetworkURI);

        ClientResponse response = resource.accept(MediaType.APPLICATION_XML).get();
        assertErrors(response, 404, APIError.VLANS_NON_EXISTENT_VIRTUAL_NETWORK);
    }

    @Test
    public void getPrivateNetworkWithWrongVirtualDatacenter() throws ClientWebException
    {
        String invalidVDCURI = resolvePrivateNetworkURI(new Random().nextInt(1000), vlan.getId());
        Resource resource = client.resource(invalidVDCURI);

        ClientResponse response = resource.accept(MediaType.APPLICATION_XML).get();
        assertErrors(response, 404, APIError.NON_EXISTENT_VIRTUAL_DATACENTER);
    }
    
    /**
     * We create the vdc1 with network vlan1 (in setup). Now we create vcd2 with network vlan2.
     * Ensure the uri /cloud/virtualdatacenters/vdc1/privatenetworks/vlan2 throws a 404-non_existent_virtual_network
     */
    @Test
    public void getPrivateNetworkVirtualDatacenterWithUnassignedVLAN() throws Exception
    {
        // Creation of virtualdatacenter2 and vlan2
        VirtualDatacenter vdc2 = vdcGenerator.createInstance(rs.getDatacenter());
        DatacenterLimits dclimit = new DatacenterLimits(vdc2.getEnterprise(), vdc2.getDatacenter());
        setup(vdc2.getEnterprise(), vdc2.getNetwork(), vdc2, dclimit);
        VLANNetwork vlan2 = vlanGenerator.createInstance(vdc2.getNetwork(), rs);
        vlan2.setEnterprise(vdc2.getEnterprise());
        setup(vlan2.getConfiguration().getDhcp(), vlan2.getConfiguration(), vlan2);
        
        // Ensure we have create it correctly.
        Resource resource = client.resource(resolvePrivateNetworkURI(vdc2.getId(), vlan2.getId()));
        ClientResponse response = resource.accept(MediaType.APPLICATION_XML).get();
        VLANNetworkDto network = response.getEntity(VLANNetworkDto.class);
        assertEquals(200, response.getStatusCode());
        assertNotNull(network);
        
        // Try to cross parameters.
        resource = client.resource(resolvePrivateNetworkURI(vdc.getId(), vlan2.getId()));
        response = resource.accept(MediaType.APPLICATION_XML).get();
        
        // The VLAN does not exist!
        assertErrors(response, 404, APIError.VLANS_NON_EXISTENT_VIRTUAL_NETWORK);
        
    }

    @Test
    public void vlanContainsEditLink()
    {
        assertLinkExist(getValidPrivateNetwork(), validURI, "edit");
    }

    @Test
    public void vlanContainsVirtualDatacenterLink()
    {
        VLANNetworkDto vlanNetwork = getValidPrivateNetwork();
        assertLinkExist(vlanNetwork, resolveVirtualDatacenterURI(vdc.getId()),
            VirtualDatacenterResource.VIRTUAL_DATACENTER);
    }
    
    @Test
    public void vlanContainsIPsLink()
    {
        VLANNetworkDto vlanNetwork = getValidPrivateNetwork();
        assertLinkExist(vlanNetwork, resolvePrivateNetworkIPsURI(vdc.getId(), vlanNetwork.getId()), IpAddressesResource.IP_ADDRESSES);
    }

    private VLANNetworkDto getValidPrivateNetwork()
    {
        Resource resource = client.resource(validURI);

        return resource.accept(MediaType.APPLICATION_XML).get(VLANNetworkDto.class);
    }
}
