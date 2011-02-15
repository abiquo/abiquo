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
import static com.abiquo.api.common.UriTestResolver.resolveDatacenterURI;
import static com.abiquo.api.common.UriTestResolver.resolveHypervisorTypesURI;
import static com.abiquo.api.common.UriTestResolver.resolveRacksURI;
import static com.abiquo.api.common.UriTestResolver.resolveRemoteServicesURI;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;

import java.io.IOException;

import javax.ws.rs.core.MediaType;

import org.apache.wink.client.ClientResponse;
import org.apache.wink.client.ClientWebException;
import org.apache.wink.client.Resource;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.Test;

import com.abiquo.api.exceptions.APIError;
import com.abiquo.server.core.cloud.HypervisorTypesDto;
import com.abiquo.server.core.infrastructure.Datacenter;
import com.abiquo.server.core.infrastructure.DatacenterDto;
import com.abiquo.server.core.infrastructure.Machine;

public class DatacenterResourceIT extends AbstractJpaGeneratorIT
{

    @AfterMethod
    public void tearDown()
    {
        tearDown("hypervisor", "physicalmachine", "rack", "datacenter");
    }

    @Test
    public void getDatacenterDoesntExist() throws ClientWebException
    {
        ClientResponse response = get(resolveDatacenterURI(12345));
        assertEquals(404, response.getStatusCode());
        assertErrors(response, 404, APIError.NON_EXISTENT_DATACENTER.getCode());
    }

    @Test
    public void getDatacenter() throws Exception
    {
        assertNotNull(createDatacenter());
    }

    @Test
    public void existRackLink()
    {
        DatacenterDto dc = createDatacenter();
        assertLinkExist(dc, resolveRacksURI(dc.getId()), RacksResource.RACKS_PATH);
    }

    @Test
    public void existHypervisorsLink()
    {
        DatacenterDto dc = createDatacenter();
        assertLinkExist(dc, resolveHypervisorTypesURI(dc.getId()), DatacenterResource.HYPERVISORS_PATH);
    }

    @Test
    public void existRemoteServicesLink()
    {

        DatacenterDto dc = createDatacenter();
        assertLinkExist(dc, resolveRemoteServicesURI(dc.getId()), RemoteServicesResource.REMOTE_SERVICES_PATH);
    }

    @Test
    public void existEditLink()
    {
        DatacenterDto dc = createDatacenter();
        assertLinkExist(dc, resolveDatacenterURI(dc.getId()), "edit");
    }

    @Test
    public void modifyDatacenter() throws ClientWebException
    {
        DatacenterDto dc = createDatacenter();
        String uri = dc.getEditLink().getHref();

        DatacenterDto datacenter = get(uri).getEntity(DatacenterDto.class);

        datacenter.setLocation("datacenter_situation_changed");
        ClientResponse response = put(uri, datacenter);
        assertEquals(200, response.getStatusCode());

        datacenter = get(uri).getEntity(DatacenterDto.class);
        assertEquals("datacenter_situation_changed", datacenter.getLocation());
    }

    @Test
    public void getHypervisorTypes() throws ClientWebException, IOException
    {
        Datacenter datacenter = datacenterGenerator.createUniqueInstance();
        Machine m0 = machineGenerator.createMachine(datacenter);
        Machine m1 = machineGenerator.createMachine(datacenter);

        m0.setHypervisor(hypervisorGenerator.createInstance(m0));
        m1.setHypervisor(hypervisorGenerator.createInstance(m1));

        setup(datacenter, m0, m1);

        String uri = resolveHypervisorTypesURI(datacenter.getId());

        ClientResponse response = get(uri);
        assertEquals(response.getStatusCode(), 200);

        HypervisorTypesDto types = response.getEntity(HypervisorTypesDto.class);
        assertNotNull(types);
        assertEquals(types.getCollection().isEmpty(), false);

        response = get(resolveHypervisorTypesURI(12345));
        assertEquals(404, response.getStatusCode());
        assertErrors(response, 404, APIError.NON_EXISTENT_DATACENTER.getCode());
    }

    /* delete is disabled at this moment */
    @Test(enabled = false)
    public void removeDatacenter() throws ClientWebException
    {
        DatacenterDto dc = createDatacenter();

        Resource resource = client.resource(dc.getEditLink().getHref());

        ClientResponse response = resource.accept(MediaType.APPLICATION_XML).delete();
        assertEquals(204, response.getStatusCode());
    }

    protected DatacenterDto createDatacenter()
    {
        Datacenter datacenter = datacenterGenerator.createUniqueInstance();
        setup(datacenter);

        String href = resolveDatacenterURI(datacenter.getId());
        
        return get(href).getEntity(DatacenterDto.class);
    }
}
