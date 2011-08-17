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
import static com.abiquo.api.common.UriTestResolver.resolvePrivateNetworkIPsURI;
import static com.abiquo.api.common.UriTestResolver.resolvePrivateNetworkURI;
import static com.abiquo.api.common.UriTestResolver.resolveVirtualDatacenterURI;
import static com.abiquo.testng.TestConfig.BASIC_INTEGRATION_TESTS;
import static com.abiquo.testng.TestConfig.NETWORK_INTEGRATION_TESTS;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;

import java.util.ArrayList;
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
import com.abiquo.model.enumerator.RemoteServiceType;
import com.abiquo.model.util.ModelTransformer;
import com.abiquo.server.core.cloud.VirtualDatacenter;
import com.abiquo.server.core.enterprise.DatacenterLimits;
import com.abiquo.server.core.enterprise.Enterprise;
import com.abiquo.server.core.enterprise.Role;
import com.abiquo.server.core.enterprise.User;
import com.abiquo.server.core.infrastructure.RemoteService;
import com.abiquo.server.core.infrastructure.network.IpPoolManagement;
import com.abiquo.server.core.infrastructure.network.IpPoolManagementDto;
import com.abiquo.server.core.infrastructure.network.IpsPoolManagementDto;
import com.abiquo.server.core.infrastructure.network.VLANNetwork;
import com.abiquo.server.core.infrastructure.network.VLANNetworkDto;
import com.abiquo.server.core.util.network.IPAddress;
import com.abiquo.server.core.util.network.IPNetworkRang;

@Test(groups = {NETWORK_INTEGRATION_TESTS})
public class PrivateNetworkResourceIT extends AbstractJpaGeneratorIT
{

    private String validURI;

    VirtualDatacenter vdc;

    VLANNetwork vlan;

    RemoteService rs;

    @Override
    @BeforeMethod(groups = {BASIC_INTEGRATION_TESTS, NETWORK_INTEGRATION_TESTS})
    public void setup()
    {
        Enterprise e = enterpriseGenerator.createUniqueInstance();
        Role r = roleGenerator.createInstance();
        User u = userGenerator.createInstance(e, r, "basicUser", "basicUser");
        setup(e, r, u);

        rs = remoteServiceGenerator.createInstance(RemoteServiceType.DHCP_SERVICE);
        vdc = vdcGenerator.createInstance(rs.getDatacenter(), e);

        DatacenterLimits dclimit = new DatacenterLimits(vdc.getEnterprise(), vdc.getDatacenter());
        vlan = vlanGenerator.createInstance(vdc.getNetwork(), rs, "255.255.255.0");
        vlan.setEnterprise(vdc.getEnterprise());
        vdc.setDefaultVlan(vlan);
        setup(vdc.getDatacenter(), rs, vdc.getNetwork(), vlan.getConfiguration().getDhcp(),
            vlan.getConfiguration(), vlan, vdc, dclimit);

        validURI = resolvePrivateNetworkURI(vdc.getId(), vlan.getId());

    }

    @Override
    @AfterMethod(groups = {BASIC_INTEGRATION_TESTS, NETWORK_INTEGRATION_TESTS})
    public void tearDown()
    {
        super.tearDown();
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
        String invalidNetworkURI =
            resolvePrivateNetworkURI(vdc.getId(), new Random().nextInt(1000));
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
     * Ensure the uri /cloud/virtualdatacenters/vdc1/privatenetworks/vlan2 throws a
     * 404-non_existent_virtual_network
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

    // PUT VLAN Related Tests //

    /**
     * Updates VLAN in the right way.
     * 
     * @throws Exception
     */
    @Test(groups = {BASIC_INTEGRATION_TESTS})
    public void updateVLANTestEndToEnd() throws Exception
    {
        // Now we create the IPs of the VLAN.
        IPAddress ip = IPAddress.newIPAddress(vlan.getConfiguration().getAddress()).nextIPAddress();
        IPAddress lastIP =
            IPNetworkRang.lastIPAddressWithNumNodes(
                IPAddress.newIPAddress(vlan.getConfiguration().getAddress()),
                IPNetworkRang.masktoNumberOfNodes(vlan.getConfiguration().getMask()));

        ArrayList<Object> ipsObjects = new ArrayList<Object>();
        while (!ip.equals(lastIP))
        {
            IpPoolManagement ippool = ipGenerator.createInstance(vdc, vlan, ip.toString());
            // ipsObjects.add(ippool.getRasd());
            ipsObjects.add(ippool);
            ip = ip.nextIPAddress();
        }
        setup(ipsObjects.toArray());

        VLANNetworkDto dto = createTransferObject(vlan);
        // modify the name and the primary DNS.
        dto.setName("newname");
        dto.setPrimaryDNS("45.45.45.0");

        ClientResponse response = put(validURI, dto, "basicUser", "basicUser");
        assertEquals(response.getStatusCode(), Status.OK.getStatusCode());

        // Ensure the VLAN has changed.
        VLANNetworkDto dtoResponse = response.getEntity(VLANNetworkDto.class);
        assertNotNull(dtoResponse);
        assertEquals(dto.getId(), dtoResponse.getId());
        assertEquals("newname", dtoResponse.getName());
        assertEquals(dto.getDefaultNetwork(), dtoResponse.getDefaultNetwork());
        assertEquals(dto.getAddress(), dtoResponse.getAddress());
        assertEquals("45.45.45.0", dtoResponse.getPrimaryDNS());
        assertEquals(dto.getSecondaryDNS(), dtoResponse.getSecondaryDNS());

        // Ensure the IPs of the VLAN have changed its 'vlanname' attribute. Get a random IP and
        // check it
        String ipsUri = resolvePrivateNetworkIPsURI(vdc.getId(), vlan.getId());
        response = get(ipsUri);
        IpsPoolManagementDto dtoIPs = response.getEntity(IpsPoolManagementDto.class);
        assertNotNull(dtoIPs);
        IpPoolManagementDto dtoIP = dtoIPs.getCollection().get(new Random().nextInt(24));
        assertEquals(dtoIP.getNetworkName(), "newname");

    }

    /**
     * Throw several request to ensure it controls the path parameter constraints.
     * 
     * @throws Exception
     */
    @Test
    public void updateVLANparamsInvalids() throws Exception
    {
        VLANNetworkDto dto = createTransferObject(vlan);
        Resource resource = client.resource(resolvePrivateNetworkURI(0, vlan.getId()));
        ClientResponse response =
            resource.accept(MediaType.APPLICATION_XML).contentType(MediaType.APPLICATION_XML)
                .put(dto);
        assertEquals(response.getStatusCode(), 400);

        resource = client.resource(resolvePrivateNetworkURI(-400, vlan.getId()));
        response =
            resource.accept(MediaType.APPLICATION_XML).contentType(MediaType.APPLICATION_XML)
                .put(dto);
        assertEquals(response.getStatusCode(), 400);

        resource = client.resource(resolvePrivateNetworkURI(vdc.getId(), 0));
        response =
            resource.accept(MediaType.APPLICATION_XML).contentType(MediaType.APPLICATION_XML)
                .put(dto);
        assertEquals(response.getStatusCode(), 400);

        resource = client.resource(resolvePrivateNetworkURI(vdc.getId(), -1000));
        response =
            resource.accept(MediaType.APPLICATION_XML).contentType(MediaType.APPLICATION_XML)
                .put(dto);
        assertEquals(response.getStatusCode(), 400);
    }

    // DELETE-related VLAN tests.
    @Test
    public void deleteVLANparamsInvalids() throws Exception
    {
        Resource resource = client.resource(resolvePrivateNetworkURI(0, vlan.getId()));
        ClientResponse response =
            resource.accept(MediaType.APPLICATION_XML).contentType(MediaType.APPLICATION_XML)
                .delete();
        assertEquals(response.getStatusCode(), 400);

        resource = client.resource(resolvePrivateNetworkURI(-400, vlan.getId()));
        response =
            resource.accept(MediaType.APPLICATION_XML).contentType(MediaType.APPLICATION_XML)
                .delete();
        assertEquals(response.getStatusCode(), 400);

        resource = client.resource(resolvePrivateNetworkURI(vdc.getId(), 0));
        response =
            resource.accept(MediaType.APPLICATION_XML).contentType(MediaType.APPLICATION_XML)
                .delete();
        assertEquals(response.getStatusCode(), 400);

        resource = client.resource(resolvePrivateNetworkURI(vdc.getId(), -1000));
        response =
            resource.accept(MediaType.APPLICATION_XML).contentType(MediaType.APPLICATION_XML)
                .delete();
        assertEquals(response.getStatusCode(), 400);
    }

    /**
     * Performs and end-to-end test for a deletion.
     */
    @Test(groups = {BASIC_INTEGRATION_TESTS})
    public void deleteVLANTestEndToEnd() throws Exception
    {
        // Save the second network. The first one is the default, and you can not delete neither
        // unique nor default-network.
        // So we need to save another VLAN befor create the test
        VLANNetwork vlan2 = vlanGenerator.createInstance(vdc.getNetwork(), rs);
        vlan2.setEnterprise(vdc.getEnterprise());
        setup(vlan2.getConfiguration().getDhcp(), vlan2.getConfiguration(), vlan2);
        String uri = resolvePrivateNetworkURI(vdc.getId(), vlan2.getId());
        ClientResponse response = delete(uri, "basicUser", "basicUser");

        // Response ok.
        assertEquals(response.getStatusCode(), Status.NO_CONTENT.getStatusCode());

        // Perform a GET to ensure the entity has been deleted
        response = get(uri, "basicUser", "basicUser");
        assertEquals(response.getStatusCode(), Status.NOT_FOUND.getStatusCode());

    }

    // DTO-LInk VLAN Related Tests //
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
        assertLinkExist(vlanNetwork, resolvePrivateNetworkIPsURI(vdc.getId(), vlanNetwork.getId()),
            IpAddressesResource.IP_ADDRESSES);
    }

    private VLANNetworkDto getValidPrivateNetwork()
    {
        Resource resource = client.resource(validURI);

        return resource.accept(MediaType.APPLICATION_XML).get(VLANNetworkDto.class);
    }

    /**
     * Creates the VLAN transfer object from the Persistent object.
     * 
     * @param network persistent object
     * @param virtualDatacenterId identifier of the virtual datacenter
     * @return the {@link VLANNetworkDto} transfer object.
     * @throws Exception pim-pam-pum
     */
    public static VLANNetworkDto createTransferObject(final VLANNetwork network) throws Exception
    {
        VLANNetworkDto dto =
            ModelTransformer.transportFromPersistence(VLANNetworkDto.class, network);

        dto.setId(network.getId());
        dto.setAddress(network.getConfiguration().getAddress());
        dto.setGateway(network.getConfiguration().getGateway());
        dto.setMask(network.getConfiguration().getMask());
        dto.setPrimaryDNS(network.getConfiguration().getPrimaryDNS());
        dto.setSecondaryDNS(network.getConfiguration().getSecondaryDNS());
        dto.setSufixDNS(network.getConfiguration().getSufixDNS());

        return dto;
    }
}
