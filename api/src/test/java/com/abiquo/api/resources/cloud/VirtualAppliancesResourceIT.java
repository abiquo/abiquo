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

import static com.abiquo.api.common.UriTestResolver.resolveVirtualAppliancesURI;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;

import javax.ws.rs.core.Response.Status;

import org.apache.wink.client.ClientResponse;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import com.abiquo.api.resources.AbstractJpaGeneratorIT;
import com.abiquo.server.core.cloud.VirtualAppliance;
import com.abiquo.server.core.cloud.VirtualApplianceDto;
import com.abiquo.server.core.cloud.VirtualAppliancesDto;
import com.abiquo.server.core.cloud.VirtualDatacenter;
import com.abiquo.server.core.enterprise.Enterprise;
import com.abiquo.server.core.infrastructure.Datacenter;

/**
 * Test class to check the functionality of the resource {@link VirtualAppliancesResource}
 * 
 * @author jdevesa
 */
public class VirtualAppliancesResourceIT extends AbstractJpaGeneratorIT
{
    private Enterprise ent;

    private Datacenter datacenter;

    @BeforeMethod
    public void setUp()
    {
        ent = enterpriseGenerator.createUniqueInstance();
        datacenter = datacenterGenerator.createUniqueInstance();
    }

    @AfterMethod
    public void tearDown()
    {
        tearDown("ip_pool_management", "rasd_management", "virtualapp", "virtualdatacenter",
            "vlan_network", "network_configuration", "dhcp_service", "remote_service",
            "hypervisor", "physicalmachine", "rack", "datacenter", "network", "enterprise");
    }

    /**
     * Check a 'get virtual appliances' call after creating the instances in DB. Creating two
     * virtual datacenters. The first one will have two virtual appliances, the second one will have
     * no virtual appliance. Check the response code is 200 even if the virtual datacenters doesn't
     * have virtual appliances. In that case it will return an empty list of vapp.
     */
    @Test
    public void getVirtualAppliancesTest()
    {
        VirtualDatacenter vdc1 = vdcGenerator.createInstance(datacenter, ent);
        VirtualDatacenter vdc2 = vdcGenerator.createInstance(datacenter, ent);
        VirtualAppliance vapp1 = vappGenerator.createInstance(vdc1);
        VirtualAppliance vapp2 = vappGenerator.createInstance(vdc1);

        setup(ent, datacenter, vdc1, vdc2, vapp1, vapp2);

        // Check for vdc1
        ClientResponse response = get(resolveVirtualAppliancesURI(vdc1.getId()));
        assertEquals(response.getStatusCode(), Status.OK.getStatusCode());
        VirtualAppliancesDto vapps = response.getEntity(VirtualAppliancesDto.class);
        assertNotNull(vapps);
        assertNotNull(vapps.getCollection());
        assertEquals(vapps.getCollection().size(), 2);

        // Check for vdc2
        response = get(resolveVirtualAppliancesURI(vdc2.getId()));
        assertEquals(response.getStatusCode(), Status.OK.getStatusCode());
        vapps = response.getEntity(VirtualAppliancesDto.class);
        assertNotNull(vapps);
        assertNotNull(vapps.getCollection());
        assertEquals(vapps.getCollection().size(), 0);
    }

    @Test
    public void createVirtualAppliance()
    {
        VirtualDatacenter vdc1 = vdcGenerator.createInstance(datacenter, ent);
        setup(ent, datacenter, vdc1);

        VirtualApplianceDto dto = new VirtualApplianceDto();
        dto.setName("wadus");

        ClientResponse response = post(resolveVirtualAppliancesURI(vdc1.getId()), dto);

        assertEquals(201, response.getStatusCode());
    }
}
