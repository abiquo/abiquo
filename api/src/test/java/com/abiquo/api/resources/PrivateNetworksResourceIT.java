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
import static com.abiquo.api.common.UriTestResolver.resolvePrivateNetworksURI;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;
import static org.testng.Assert.assertTrue;

import java.util.Random;

import javax.ws.rs.core.MediaType;

import org.apache.wink.client.ClientResponse;
import org.apache.wink.client.Resource;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import com.abiquo.api.exceptions.APIError;
import com.abiquo.model.enumerator.RemoteServiceType;
import com.abiquo.model.transport.error.ErrorDto;
import com.abiquo.model.transport.error.ErrorsDto;
import com.abiquo.server.core.cloud.VirtualDatacenter;
import com.abiquo.server.core.enterprise.Enterprise;
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
public class PrivateNetworksResourceIT extends AbstractJpaGeneratorIT
{

    VirtualDatacenter vdc;
    RemoteService rs;
    VLANNetwork vlan;
    Enterprise sysEnterprise;

    @BeforeMethod
    public void setUp()
    {
        rs = remoteServiceGenerator.createInstance(RemoteServiceType.DHCP_SERVICE);
        vdc = vdcGenerator.createInstance(rs.getDatacenter());
        sysEnterprise = enterpriseGenerator.createUniqueInstance();
        Role r = roleGenerator.createInstance(Role.Type.SYS_ADMIN);

        User u = userGenerator.createInstance(sysEnterprise, r, "sysadmin", "sysadmin");
        setup(sysEnterprise, r, u, vdc.getDatacenter(), rs, vdc.getEnterprise(), vdc.getNetwork(), vdc);

    }

    @Test
    public void getPrivateNetworks()
    {
        Resource resource = client.resource(resolvePrivateNetworksURI(vdc.getId()));

        vlan = vlanGenerator.createInstance(vdc.getNetwork(), rs, "255.255.255.0");
        VLANNetwork vlan2 = vlanGenerator.createInstance(vdc.getNetwork(), rs, "255.255.255.0");

        setup(vlan.getConfiguration().getDhcp(), vlan.getConfiguration(), vlan, vlan2
            .getConfiguration().getDhcp(), vlan2.getConfiguration(), vlan2);

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
        Resource resource = client.resource(resolvePrivateNetworksURI(new Random().nextInt()));

        ClientResponse response = resource.accept(MediaType.APPLICATION_XML).get();
        assertEquals(404, response.getStatusCode());
    }
    
    @Test
    public void createPrivateNetwork()
    {        
        VLANNetworkDto dto = createValidNetworkDto();
        
        ClientResponse response = post(resolvePrivateNetworksURI(vdc.getId()), dto, "sysadmin", "sysadmin");
        
        assertEquals(201, response.getStatusCode());
        VLANNetworkDto dtoResponse = response.getEntity(VLANNetworkDto.class);
        
        assertEquals(dto.getName(), dtoResponse.getName());
        assertEquals(dto.getDefaultNetwork(), dtoResponse.getDefaultNetwork());        
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
        integer = (integer < 0)? integer*(-1) : integer;
        ClientResponse response = post(resolvePrivateNetworksURI(integer), dto, "sysadmin", "sysadmin");    
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
     * Launch a several test to check 400-Bad Request. Since it never creates any VLAN, I put all tests in the same method
     * to avoid 'setUp' and 'tearDown' rutines and improve the performance of the tests. Check the constraints
     */
    @Test
    void createPrivateNetworkRaises400ManyCases()
    {
        Resource res = client.resource(resolvePrivateNetworksURI(vdc.getId())).accept(MediaType.APPLICATION_XML).contentType(
            MediaType.APPLICATION_XML).header("Authorization", "Basic " + basicAuth("sysadmin", "sysadmin"));
            
        // Name null
        VLANNetworkDto dto = createValidNetworkDto();   
        dto.setName(null);
        ClientResponse response = res.post(dto);        
        assertEquals(response.getStatusCode(), 400);

        // Default network null
        dto = createValidNetworkDto();   
        dto.setDefaultNetwork(null);
        response = res.post(dto);
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
        
        //Mask more than 32
        dto = createValidNetworkDto();
        dto.setMask(33);
        response = res.post(dto);
        assertEquals(response.getStatusCode(), 400);
        
        
    }
    
    private VLANNetworkDto createValidNetworkDto()
    {       
        VLANNetworkDto networkDto = new VLANNetworkDto();
        networkDto.setName("Default Network");
        networkDto.setDefaultNetwork(Boolean.TRUE);
        networkDto.setAddress("192.168.0.0");
        networkDto.setGateway("192.168.0.1");
        networkDto.setMask(24);
        networkDto.setPrimaryDNS("10.0.0.1");
        networkDto.setSecondaryDNS("10.0.0.1");
        networkDto.setSufixDNS("bcn.abiquo.com");        
        return networkDto;
    }
}
