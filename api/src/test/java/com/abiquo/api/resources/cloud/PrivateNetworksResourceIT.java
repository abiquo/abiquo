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
import static com.abiquo.api.common.UriTestResolver.resolvePrivateNetworksURI;
import static com.abiquo.testng.TestConfig.BASIC_INTEGRATION_TESTS;
import static com.abiquo.testng.TestConfig.NETWORK_INTEGRATION_TESTS;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;
import static org.testng.Assert.assertTrue;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response.Status;

import org.apache.wink.client.ClientResponse;
import org.apache.wink.client.Resource;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import com.abiquo.api.exceptions.APIError;
import com.abiquo.api.resources.AbstractJpaGeneratorIT;
import com.abiquo.model.enumerator.RemoteServiceType;
import com.abiquo.model.transport.error.ErrorDto;
import com.abiquo.model.transport.error.ErrorsDto;
import com.abiquo.server.core.cloud.VirtualDatacenter;
import com.abiquo.server.core.enterprise.Enterprise;
import com.abiquo.server.core.enterprise.Privilege;
import com.abiquo.server.core.enterprise.Role;
import com.abiquo.server.core.enterprise.User;
import com.abiquo.server.core.infrastructure.RemoteService;
import com.abiquo.server.core.infrastructure.network.VLANNetwork;
import com.abiquo.server.core.infrastructure.network.VLANNetworkDto;
import com.abiquo.server.core.infrastructure.network.VLANNetworksDto;

/**
 * Acceptance test for the creation of PrivateNetworks and retrieve of a list of them.
 * 
 * @author jdevesa@abiquo.com
 */
@Test(groups = {NETWORK_INTEGRATION_TESTS})
public class PrivateNetworksResourceIT extends AbstractJpaGeneratorIT
{

    VirtualDatacenter vdc;

    RemoteService rs;

    VLANNetwork vlan;

    Enterprise sysEnterprise;

    @BeforeMethod(groups = {BASIC_INTEGRATION_TESTS, NETWORK_INTEGRATION_TESTS})
    public void setUp()
    {
        rs = remoteServiceGenerator.createInstance(RemoteServiceType.DHCP_SERVICE);
        vdc = vdcGenerator.createInstance(rs.getDatacenter());
        sysEnterprise = enterpriseGenerator.createUniqueInstance();
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
        entitiesToSetup.add(vdc.getDatacenter());
        entitiesToSetup.add(rs);
        entitiesToSetup.add(vdc.getEnterprise());
        entitiesToSetup.add(vdc.getNetwork());
        entitiesToSetup.add(vdc);

        setup(entitiesToSetup.toArray());
    }

    @Override
    @AfterMethod(groups = {BASIC_INTEGRATION_TESTS, NETWORK_INTEGRATION_TESTS})
    public void tearDown()
    {
        super.tearDown();
    }

    @Test
    public void getPrivateNetworks()
    {
        Resource resource = client.resource(resolvePrivateNetworksURI(vdc.getId()));

        vlan = vlanGenerator.createInstance(vdc.getNetwork(), rs, "255.255.255.0");
        VLANNetwork vlan2 = vlanGenerator.createInstance(vdc.getNetwork(), rs, "255.255.255.0");

        setup(vlan.getConfiguration(), vlan, vlan2.getConfiguration(), vlan2);

        ClientResponse response = resource.accept(MediaType.APPLICATION_XML).get();
        assertEquals(200, response.getStatusCode());
        VLANNetworksDto entity = response.getEntity(VLANNetworksDto.class);
        assertNotNull(entity);
        assertNotNull(entity.getCollection());
        assertEquals(entity.getCollection().size(), 2);
    }

    @Test
    public void getPrivateNetworksListInvalidVDC() throws Exception
    {
        Resource resource = client.resource(resolvePrivateNetworksURI(new Random().nextInt(1000)));

        ClientResponse response = resource.accept(MediaType.APPLICATION_XML).get();
        assertEquals(response.getStatusCode(), Status.NOT_FOUND.getStatusCode());
    }

    @Test(groups = {BASIC_INTEGRATION_TESTS})
    public void createPrivateNetwork()
    {
        VLANNetworkDto dto = createValidNetworkDto();

        ClientResponse response =
            post(resolvePrivateNetworksURI(vdc.getId()), dto, "sysadmin", "sysadmin");

        assertEquals(response.getStatusCode(), Status.CREATED.getStatusCode());
        VLANNetworkDto dtoResponse = response.getEntity(VLANNetworkDto.class);

        assertEquals(dto.getName(), dtoResponse.getName());
        assertEquals(dto.getAddress(), dtoResponse.getAddress());
        assertEquals(dto.getGateway(), dtoResponse.getGateway());
        assertTrue(dto.getMask() == dtoResponse.getMask());
        assertEquals(dto.getPrimaryDNS(), dtoResponse.getPrimaryDNS());
        assertEquals(dto.getSecondaryDNS(), dtoResponse.getSecondaryDNS());
        assertEquals(dto.getSufixDNS(), dtoResponse.getSufixDNS());

    }

    /**
     * Checks it return a 404 NotFound when trying to access a VirtualDatacenter invalid
     */
    @Test
    void createPrivateNetworkRaises404WhenVDCDoesNotExist()
    {
        VLANNetworkDto dto = createValidNetworkDto();
        Integer integer = new Random().nextInt();
        integer = integer < 0 ? integer * -1 : integer;
        ClientResponse response =
            post(resolvePrivateNetworksURI(integer), dto, "sysadmin", "sysadmin");
        assertErrors(response, 404, APIError.NON_EXISTENT_VIRTUAL_DATACENTER);
    }

    /**
     * Checks it return a 400 BadRequest when the VirtualDatacenter param is a negative number
     */
    @Test
    void createPrivateNetworkRaises400WhenVDCIdentifierNegative()
    {
        VLANNetworkDto dto = createValidNetworkDto();
        ClientResponse response = post(resolvePrivateNetworksURI(-1), dto, "sysadmin", "sysadmin");
        assertEquals(response.getStatusCode(), 400);
        ErrorsDto errors = response.getEntity(ErrorsDto.class);
        assertEquals(errors.getCollection().size(), 1);
        ErrorDto error = errors.getCollection().get(0);
        assertEquals(error.getCode(), "CONSTR-MIN");
    }

    /**
     * Launch a several test to check 400-Bad Request. Since it never creates any VLAN, I put all
     * tests in the same method to avoid 'setUp' and 'tearDown' rutines and improve the performance
     * of the tests. Check the constraints
     */
    @Test
    void createPrivateNetworkRaises400ManyCases()
    {
        Resource res =
            client.resource(resolvePrivateNetworksURI(vdc.getId()))
                .accept(MediaType.APPLICATION_XML).contentType(MediaType.APPLICATION_XML)
                .header("Authorization", "Basic " + basicAuth("sysadmin", "sysadmin"));

        // Name null
        VLANNetworkDto dto = createValidNetworkDto();
        dto.setName(null);
        ClientResponse response = res.post(dto);
        assertEquals(response.getStatusCode(), 400);

        // Address null
        dto = createValidNetworkDto();
        dto.setAddress(null);
        response = res.post(dto);
        assertEquals(response.getStatusCode(), 400);

        // Address invalid (not IP)
        dto = createValidNetworkDto();
        dto.setAddress("192.168");
        response = res.post(dto);
        assertEquals(response.getStatusCode(), 400);

        // Gateway null
        dto = createValidNetworkDto();
        dto.setGateway(null);
        response = res.post(dto);
        assertEquals(response.getStatusCode(), 400);

        // Address invalid (not IP)
        dto = createValidNetworkDto();
        dto.setGateway("192.168");
        response = res.post(dto);
        assertEquals(response.getStatusCode(), 400);

        // Mask null
        dto = createValidNetworkDto();
        dto.setMask(null);
        response = res.post(dto);
        assertEquals(response.getStatusCode(), 400);

        // Mask less than 1
        dto = createValidNetworkDto();
        dto.setMask(-1);
        response = res.post(dto);
        assertEquals(response.getStatusCode(), 400);

        // Mask more than 32
        dto = createValidNetworkDto();
        dto.setMask(33);
        response = res.post(dto);
        assertEquals(response.getStatusCode(), 400);

    }

    /**
     * Create the maximum of private networks available per virtualdatacenter. After that, checkt it
     * returns a 409 when trying to add another vlan.
     */
    @Test
    public void createPrivateNetworkRaises409WhenMaximumIsReached()
    {
        VLANNetworkDto dto;
        ClientResponse response;

        // Get the max from system property
        Integer maxVlans =
            Integer.valueOf(System.getProperty("abiquo.server.networking.vlanPerVdc"));

        // Create the n-1 first vlans
        for (int i = 0; i < maxVlans; i++)
        {
            dto = createValidNetworkDto();
            dto.setName(dto.getName() + i);
            response = post(resolvePrivateNetworksURI(vdc.getId()), dto, "sysadmin", "sysadmin");
            assertEquals(201, response.getStatusCode());
        }

        // Assert it doesn't allow you to create more
        dto = createValidNetworkDto();
        dto.setName(dto.getName() + "nonono");
        response = post(resolvePrivateNetworksURI(vdc.getId()), dto, "sysadmin", "sysadmin");
        assertErrors(response, 409, APIError.VLANS_PRIVATE_MAXIMUM_REACHED);

    }

    /**
     * Check we can not create two vlans with the same name in a virtualdatacenter.
     */
    @Test
    public void createPrivateNetworkRaises409WhenDuplicatedVlanNameInTheSameVDC()
    {
        VLANNetworkDto dto = createValidNetworkDto();
        ClientResponse response =
            post(resolvePrivateNetworksURI(vdc.getId()), dto, "sysadmin", "sysadmin");
        assertEquals(201, response.getStatusCode());

        // Check we can not create the dto again caused by the network name.
        response = post(resolvePrivateNetworksURI(vdc.getId()), dto, "sysadmin", "sysadmin");
        assertErrors(response, 409, APIError.VLANS_DUPLICATED_VLAN_NAME_VDC);

        // Ensure we can create it with the same name into another vdc.
        VirtualDatacenter vdc2 = vdcGenerator.createInstance(rs.getDatacenter());
        setup(vdc2.getEnterprise(), vdc2.getNetwork(), vdc2);
        response = post(resolvePrivateNetworksURI(vdc2.getId()), dto, "sysadmin", "sysadmin");
        assertEquals(201, response.getStatusCode());

    }

    /**
     * Ensure we can not use any other rangs than the private ones
     */
    @Test
    public void createPrivateNetworkRaises400WhenNetworkAddressIsNotPrivate()
    {
        // Loopback address
        VLANNetworkDto dto = createValidNetworkDto();
        dto.setAddress("127.0.0.0");
        ClientResponse response =
            post(resolvePrivateNetworksURI(vdc.getId()), dto, "sysadmin", "sysadmin");
        assertErrors(response, 400, APIError.VLANS_PRIVATE_ADDRESS_WRONG);

        // Multicast address
        dto = createValidNetworkDto();
        dto.setAddress("224.12.1.0");
        response = post(resolvePrivateNetworksURI(vdc.getId()), dto, "sysadmin", "sysadmin");
        assertErrors(response, 400, APIError.VLANS_PRIVATE_ADDRESS_WRONG);

        // Wrong private address
        dto = createValidNetworkDto();
        dto.setAddress("172.32.111.0");
        response = post(resolvePrivateNetworksURI(vdc.getId()), dto, "sysadmin", "sysadmin");
        assertErrors(response, 400, APIError.VLANS_PRIVATE_ADDRESS_WRONG);

        dto = createValidNetworkDto();
        dto.setAddress("172.15.111.0");
        response = post(resolvePrivateNetworksURI(vdc.getId()), dto, "sysadmin", "sysadmin");
        assertErrors(response, 400, APIError.VLANS_PRIVATE_ADDRESS_WRONG);

        // Random address
        dto = createValidNetworkDto();
        dto.setAddress("34.123.4.56");
        response = post(resolvePrivateNetworksURI(vdc.getId()), dto, "sysadmin", "sysadmin");
        assertErrors(response, 400, APIError.VLANS_PRIVATE_ADDRESS_WRONG);
    }

    /**
     * Ensure the network address and the mask are coherent in terms of network range.
     */
    @Test
    public void createPrivateNetworkRaises409WhenInvalidMasksRelatedToNetworkAddress()
    {
        // Loopback address
        VLANNetworkDto dto = createValidNetworkDto();
        dto.setAddress("10.60.1.0");
        dto.setMask(20);
        ClientResponse response =
            post(resolvePrivateNetworksURI(vdc.getId()), dto, "sysadmin", "sysadmin");
        assertErrors(response, 409, APIError.VLANS_TOO_BIG_NETWORK);

        // Multicast address
        dto = createValidNetworkDto();
        dto.setAddress("192.168.1.0");
        dto.setMask(22);
        response = post(resolvePrivateNetworksURI(vdc.getId()), dto, "sysadmin", "sysadmin");
        assertErrors(response, 409, APIError.VLANS_TOO_BIG_NETWORK_II);

        // Wrong private address
        dto = createValidNetworkDto();
        dto.setAddress("10.40.1.23");
        dto.setMask(31);
        response = post(resolvePrivateNetworksURI(vdc.getId()), dto, "sysadmin", "sysadmin");
        assertErrors(response, 409, APIError.VLANS_TOO_SMALL_NETWORK);
    }

    /**
     * Ensure the network address and the mask are coherent in terms of network adrress.
     */
    @Test
    public void createPrivateNetworkRaises400WhenInvalidNetworkRelatedToNetworkMask()
    {
        // 192.168.1.128 is not vaild in terms of mask range
        VLANNetworkDto dto = createValidNetworkDto();
        dto.setAddress("192.168.1.128");
        dto.setMask(24);
        ClientResponse response =
            post(resolvePrivateNetworksURI(vdc.getId()), dto, "sysadmin", "sysadmin");
        assertErrors(response, 400, APIError.VLANS_INVALID_NETWORK_AND_MASK);

        // but is valid for the 25 mask...
        dto.setMask(25);
        dto.setGateway("192.168.1.130");
        response = post(resolvePrivateNetworksURI(vdc.getId()), dto, "sysadmin", "sysadmin");
        assertEquals(201, response.getStatusCode());

    }

    /**
     * Check if the gateway is inside the range of the network with a couple examples.
     */
    @Test
    public void createPrivateNetworkRaises400WhenGatewayOutOfRange()
    {
        // 192.168.1.128 is not vaild in terms of mask range
        VLANNetworkDto dto = createValidNetworkDto();
        dto.setAddress("192.168.1.0");
        dto.setMask(24);
        dto.setGateway("192.168.3.0");
        ClientResponse response =
            post(resolvePrivateNetworksURI(vdc.getId()), dto, "sysadmin", "sysadmin");
        assertErrors(response, 400, APIError.VLANS_GATEWAY_OUT_OF_RANGE);

        // another non-so-obvious example
        dto = createValidNetworkDto();
        dto.setAddress("192.168.1.0");
        dto.setMask(25);
        dto.setGateway("192.168.1.200");
        response = post(resolvePrivateNetworksURI(vdc.getId()), dto, "sysadmin", "sysadmin");
        assertErrors(response, 400, APIError.VLANS_GATEWAY_OUT_OF_RANGE);

    }

    /**
     * Steps: create VLAN (vlan1) inside the virtual datacenter. * vlan1 is the default one. *
     * create vlan2 and put it as default. * ensure vlan2 is the default. * ensure vlan1 is not the
     * default any more. TODO: do it after the GET method is developed
     */
    @Test
    public void createPrivateNewtorkSwapBetweenDefaultNetwork()
    {

    }

    private VLANNetworkDto createValidNetworkDto()
    {
        VLANNetworkDto networkDto = new VLANNetworkDto();
        networkDto.setName("Default Network");
        networkDto.setAddress("192.168.0.0");
        networkDto.setGateway("192.168.0.1");
        networkDto.setMask(24);
        networkDto.setPrimaryDNS("10.0.0.1");
        networkDto.setSecondaryDNS("10.0.0.1");
        networkDto.setSufixDNS("bcn.abiquo.com");
        return networkDto;
    }
}
