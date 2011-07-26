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

import static com.abiquo.api.common.Assert.assertNotEmpty;
import static com.abiquo.api.common.UriTestResolver.resolveDatacentersURI;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;

import java.util.ArrayList;
import java.util.List;

import org.apache.wink.client.ClientResponse;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import com.abiquo.model.enumerator.RemoteServiceType;
import com.abiquo.server.core.enterprise.DatacenterLimits;
import com.abiquo.server.core.enterprise.Enterprise;
import com.abiquo.server.core.enterprise.Privilege;
import com.abiquo.server.core.enterprise.Role;
import com.abiquo.server.core.enterprise.User;
import com.abiquo.server.core.infrastructure.Datacenter;
import com.abiquo.server.core.infrastructure.DatacenterDto;
import com.abiquo.server.core.infrastructure.DatacentersDto;
import com.abiquo.server.core.infrastructure.RemoteServiceDto;
import com.abiquo.server.core.infrastructure.RemoteServicesDto;

public class DatacentersResourceIT extends AbstractJpaGeneratorIT
{
    Enterprise e1;

    String SYSADMIN = "SYSADMIN";

    @BeforeMethod
    public void setupSysadmin()
    {
        e1 = enterpriseGenerator.createUniqueInstance();
        Role r = roleGenerator.createInstanceSysAdmin();
        r.setEnterprise(e1);
        User u = userGenerator.createInstance(e1, r, SYSADMIN, SYSADMIN);

        List<Object> entitiesToSetup = new ArrayList<Object>();
        entitiesToSetup.add(e1);
        for (Privilege p : r.getPrivileges())
        {
            entitiesToSetup.add(p);
        }
        entitiesToSetup.add(r);
        entitiesToSetup.add(u);

        setup(entitiesToSetup.toArray());
    }

    @Test
    public void getDatacentersList() throws Exception
    {
        DatacenterDto d = new DatacenterDto();
        d.setName("datacenter_test");
        d.setLocation("situation_datacenter_test");

        ClientResponse response = post(resolveDatacentersURI(), d, SYSADMIN, SYSADMIN);

        assertEquals(response.getStatusCode(), 201);

        DatacenterDto entityPost = response.getEntity(DatacenterDto.class);
        assertNotNull(entityPost);
        assertEquals(d.getName(), entityPost.getName());

        response = get(resolveDatacentersURI());
        assertEquals(200, response.getStatusCode());

        DatacentersDto entity = response.getEntity(DatacentersDto.class);
        assertNotNull(entity);
        assertNotEmpty(entity.getCollection());
    }

    @Test
    public void getDatacentersListbyEnt() throws Exception
    {
        Enterprise e2 = enterpriseGenerator.createUniqueInstance();
        Datacenter d1 = datacenterGenerator.createUniqueInstance();
        Datacenter d2 = datacenterGenerator.createUniqueInstance();
        Datacenter d3 = datacenterGenerator.createUniqueInstance();
        DatacenterLimits dl1 = datacenterLimitsGenerator.createInstance(e1, d1);
        DatacenterLimits dl2 = datacenterLimitsGenerator.createInstance(e1, d2);
        DatacenterLimits dl3 = datacenterLimitsGenerator.createInstance(e2, d3);

        List<Object> entitiesToSetup = new ArrayList<Object>();
        entitiesToSetup.add(e2);
        entitiesToSetup.add(d1);
        entitiesToSetup.add(d2);
        entitiesToSetup.add(d3);
        entitiesToSetup.add(dl1);
        entitiesToSetup.add(dl2);
        entitiesToSetup.add(dl3);
        setup(entitiesToSetup.toArray());

        ClientResponse response =
            get(resolveDatacentersURI() + "?idEnterprise=" + e1.getId(), SYSADMIN, SYSADMIN);
        assertEquals(200, response.getStatusCode());

        DatacentersDto entity = response.getEntity(DatacentersDto.class);
        assertNotNull(entity);
        assertNotEmpty(entity.getCollection());
        assertEquals(entity.getCollection().size(), 2);
    }

    /**
     * Test the creation of the datacenter with remote services in the same call.
     * 
     * @throws Exception like any test
     */
    @Test
    public void createDatacenterWithRemotServicesTest() throws Exception
    {
        // Create the two remote services for the datacenters
        // Remote services conection mustn't be checked
        RemoteServiceDto rsAm = new RemoteServiceDto();
        rsAm.setType(RemoteServiceType.DHCP_SERVICE);
        rsAm.setUri("http://example.com/dhcp");
        RemoteServiceDto rsSSM = new RemoteServiceDto();
        rsSSM.setType(RemoteServiceType.BPM_SERVICE);
        rsSSM.setUri("http://example.com/bpm");
        RemoteServicesDto rsList = new RemoteServicesDto();
        rsList.add(rsAm);
        rsList.add(rsSSM);

        DatacenterDto d = new DatacenterDto();
        d.setName("datacenter_test");
        d.setLocation("situation_datacenter_test");
        d.setRemoteServices(rsList);

        // Assert creation
        ClientResponse response = post(resolveDatacentersURI(), d, SYSADMIN, SYSADMIN);
        // System.out.println(response.getStatusCode() + ' ' + response.getMessage());
        assertEquals(response.getStatusCode(), 201);

        // Assert there is a Datacenter and it has two Remote Services
        response = get(resolveDatacentersURI());
        assertEquals(200, response.getStatusCode());

        DatacentersDto entities = response.getEntity(DatacentersDto.class);
        assertEquals(entities.getCollection().size(), 1);

    }

    @Test
    public void testDatacentersAsCollection() throws Exception
    {
        Datacenter datacenter0 = datacenterGenerator.createUniqueInstance();
        Datacenter datacenter1 = datacenterGenerator.createUniqueInstance();

        setup(datacenter0, datacenter1);

        ClientResponse response = get(resolveDatacentersURI());

        assertEquals(response.getStatusCode(), 200);
        assertEquals(response.getEntity(DatacentersDto.class).getCollection().isEmpty(), false);
    }
}
