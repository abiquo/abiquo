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

/**
 * 
 */
package com.abiquo.api.resources.cloud;

import static com.abiquo.api.common.Assert.assertLinkExist;
import static com.abiquo.api.common.UriTestResolver.resolveVirtualApplianceActionGetIPsURI;
import static com.abiquo.api.common.UriTestResolver.resolveVirtualApplianceURI;
import static com.abiquo.api.common.UriTestResolver.resolveVirtualMachinesURI;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;

import java.util.Random;

import javax.ws.rs.core.Response.Status;

import org.apache.wink.client.ClientResponse;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import com.abiquo.api.resources.AbstractJpaGeneratorIT;
import com.abiquo.server.core.cloud.VirtualAppliance;
import com.abiquo.server.core.cloud.VirtualApplianceDto;
import com.abiquo.server.core.cloud.VirtualDatacenter;
import com.abiquo.server.core.enterprise.Enterprise;
import com.abiquo.server.core.infrastructure.Datacenter;
import com.abiquo.server.core.infrastructure.network.IpsPoolManagementDto;

/**
 *  Test class to check the functionality of the resource {@link VirtualApplianceResource}
 *  
 * @author jdevesa@abiquo.com
 */
public class VirtualApplianceResourceIT extends AbstractJpaGeneratorIT
{
    protected Enterprise ent;
    
    protected Datacenter datacenter;
    
    protected VirtualDatacenter vdc;
    
    @BeforeMethod
    public void setUp()
    {
        ent = enterpriseGenerator.createUniqueInstance();
        datacenter = datacenterGenerator.createUniqueInstance();
        vdc = vdcGenerator.createInstance(datacenter, ent);
    }
    
    @AfterMethod
    public void tearDown()
    {
        tearDown("ip_pool_management", "rasd_management", "virtualapp", "virtualdatacenter", "vlan_network",
            "network_configuration", "dhcp_service", "remote_service", "hypervisor",
            "physicalmachine", "rack", "datacenter", "network", "enterprise");
    }
    
    /**
     * Check a 'get virtual appliances' call after creating the instances in DB. 
     * Creating a virtual datacenter with two virtual appliances. Check all of them
     * are accessible.
     */
    @Test
    public void getVirtualApplianceTest()
    {
        VirtualAppliance vapp1 = vappGenerator.createInstance(vdc);
        VirtualAppliance vapp2 = vappGenerator.createInstance(vdc);
        
        setup(ent, datacenter, vdc, vapp1, vapp2);
        
        // Check for vapp1
        ClientResponse response = get(resolveVirtualApplianceURI(vdc.getId(), vapp1.getId()));
        assertEquals(response.getStatusCode(), Status.OK.getStatusCode());
        VirtualApplianceDto vappdto = response.getEntity(VirtualApplianceDto.class);
        assertNotNull(vappdto);
        assertLinkExist(vappdto, resolveVirtualApplianceActionGetIPsURI(vdc.getId(), vapp1.getId()), "action", IpAddressesResource.IP_ADDRESSES);
        assertLinkExist(vappdto, resolveVirtualMachinesURI(vdc.getId(), vapp1.getId()), VirtualMachineResource.VIRTUAL_MACHINE);
        
        // Check for vapp2
        response = get(resolveVirtualApplianceURI(vdc.getId(), vapp2.getId()));
        assertEquals(response.getStatusCode(), Status.OK.getStatusCode());
        vappdto = response.getEntity(VirtualApplianceDto.class);
        assertNotNull(vappdto);
        assertLinkExist(vappdto, resolveVirtualApplianceActionGetIPsURI(vdc.getId(), vapp2.getId()), "action", IpAddressesResource.IP_ADDRESSES);
        assertLinkExist(vappdto, resolveVirtualMachinesURI(vdc.getId(), vapp2.getId()), VirtualMachineResource.VIRTUAL_MACHINE);
    }
    
    /**
     * Check a 'get virtual appliances' 404 NOT FOUND error code when the
     * identifier of the virtual appliance is a random number.
     */
    @Test 
    public void getVirtualApplianceRaises404ErrorWhenVappRandomIdentifier()
    {
        setup(ent, datacenter, vdc);
        ClientResponse response = get(resolveVirtualApplianceURI(vdc.getId(), new Random().nextInt()));
        assertEquals(response.getStatusCode(), Status.NOT_FOUND.getStatusCode());
    }
    
    /**
     * Create a virtual datacenter and a Virtual appliance. 
     * Check the 'get virtual appliances' 404 NOT FOUND error code when
     * the virtual appliance is OK, but the identifier of the virtual datacenter is not.
     * 
     * In other words, the virtual appliance exists but doesn't belong to
     * the virtual datacenter.
     */
    @Test 
    public void getVirtualApplianceRaises404WhenVappNotBelongsToVDC()
    {
        VirtualAppliance vapp1 = vappGenerator.createInstance(vdc);
        setup(ent, datacenter, vdc, vapp1);
        ClientResponse response = get(resolveVirtualApplianceURI(new Random().nextInt(), vapp1.getId()));
        assertEquals(response.getStatusCode(), Status.NOT_FOUND.getStatusCode());       
    }
    
    //TODO: Create a test to return a non-empty list of IPs.
    
    /**
     * Create a virtual appliance. Check the action resource returns an empty list
     */
    @Test
    public void getVirtualApplianceActionIPsEmptyList()
    {
        VirtualAppliance vapp1 = vappGenerator.createInstance(vdc);
        setup(ent, datacenter, vdc, vapp1);
        
        ClientResponse response = get(resolveVirtualApplianceActionGetIPsURI(vdc.getId(), vapp1.getId()));
        assertEquals(response.getStatusCode(), Status.OK.getStatusCode());
        
        IpsPoolManagementDto entity = response.getEntity(IpsPoolManagementDto.class);
        assertNotNull(entity);
        assertNotNull(entity.getCollection());
        assertEquals(entity.getCollection().size(), 0);
    }
    
    /**
     * Create a virtual appliance. Ask the IPs for an invalid virtual appliance identifier value.
     */
    @Test
    public void getVirtualApplianceActionIPsRaises404WhenVappIsARandomValue()
    {
        setup(ent, datacenter, vdc);
        ClientResponse response = get(resolveVirtualApplianceActionGetIPsURI(vdc.getId(), new Random().nextInt()));
        assertEquals(response.getStatusCode(), Status.NOT_FOUND.getStatusCode());
    }
    
    /**
     * Create a virtual appliance. Ask the IPs for a valid virtual appliance but invalid virtual datacenter.
     */
    @Test
    public void getVirtualApplianceActionIPsRaises404WhenVappNotBelongsToVDC()
    {
        VirtualAppliance vapp1 = vappGenerator.createInstance(vdc);
        setup(ent, datacenter, vdc, vapp1);
        ClientResponse response = get(resolveVirtualApplianceActionGetIPsURI(new Random().nextInt(), vapp1.getId()));
        assertEquals(response.getStatusCode(), Status.NOT_FOUND.getStatusCode());  
    }    
    
}
