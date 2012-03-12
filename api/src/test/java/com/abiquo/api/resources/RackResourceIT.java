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

import static com.abiquo.api.common.Assert.assertLinkExist;
import static com.abiquo.api.common.Assert.assertNonEmptyErrors;
import static com.abiquo.api.common.UriTestResolver.resolveDatacenterURI;
import static com.abiquo.api.common.UriTestResolver.resolveMachinesURI;
import static com.abiquo.api.common.UriTestResolver.resolveRackURI;
import static com.abiquo.api.common.UriTestResolver.resolveRacksURI;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response.Status;

import org.apache.wink.client.ClientResponse;
import org.apache.wink.client.ClientWebException;
import org.apache.wink.client.Resource;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import com.abiquo.model.transport.error.ErrorsDto;
import com.abiquo.server.core.infrastructure.Rack;
import com.abiquo.server.core.infrastructure.RackDto;

public class RackResourceIT extends AbstractJpaGeneratorIT
{

    private Rack validRack;

    private String validRackUri;

    private String rackInvalidDatacenter;

    private String invalidRack;

    @Override
    @BeforeMethod
    public void setup()
    {
        Rack rack = rackGenerator.createUniqueInstance();
        setup(rack.getDatacenter(), rack);

        validRack = rack;
        validRackUri = resolveRackURI(rack.getDatacenter().getId(), rack.getId());
        rackInvalidDatacenter = resolveRackURI(1234, rack.getId());
        invalidRack = resolveRackURI(rack.getDatacenter().getId(), 1234);
    }

    @Test
    public void getRack() throws Exception
    {
        ClientResponse response = get(validRackUri, RackDto.MEDIA_TYPE);
        RackDto rack = response.getEntity(RackDto.class);

        assertEquals(response.getStatusCode(), Status.OK.getStatusCode());
        assertNotNull(rack);

        assertLinkExist(rack, validRackUri, "edit");
        assertLinkExist(rack,
            resolveMachinesURI(validRack.getDatacenter().getId(), validRack.getId()), "machines");
        assertLinkExist(rack, resolveDatacenterURI(validRack.getDatacenter().getId()), "datacenter");
    }

    @Test
    public void getRackDoesntExist() throws Exception
    {
        ClientResponse response = get(invalidRack, RackDto.MEDIA_TYPE);
        assertEquals(response.getStatusCode(), Status.NOT_FOUND.getStatusCode());

        assertNonEmptyErrors(response.getEntity(ErrorsDto.class));
    }

    @Test
    public void getRackWithWrongDatacenter() throws ClientWebException
    {
        ClientResponse response = get(rackInvalidDatacenter, RackDto.MEDIA_TYPE);
        assertEquals(response.getStatusCode(), Status.NOT_FOUND.getStatusCode());

        assertNonEmptyErrors(response.getEntity(ErrorsDto.class));
    }

    @Test
    public void modifyRack() throws ClientWebException
    {
        RackDto rack = get(validRackUri, RackDto.MEDIA_TYPE).getEntity(RackDto.class);
        rack.setShortDescription("dummy_description");

        ClientResponse response = put(validRackUri, rack);
        assertEquals(response.getStatusCode(), Status.OK.getStatusCode());

        RackDto modified = response.getEntity(RackDto.class);
        assertEquals("dummy_description", modified.getShortDescription());
    }

    @Test
    public void modifyRackWithDuplicatedName() throws ClientWebException
    {
        ClientResponse response =
            createRack("rack_test", "rack_description", "large_rack_description");

        RackDto rack = get(validRackUri, RackDto.MEDIA_TYPE).getEntity(RackDto.class);
        rack.setName("rack_test");

        response = put(validRackUri, rack);
        assertEquals(response.getStatusCode(), Status.CONFLICT.getStatusCode());

    }

    @Test
    public void modifyRackDoesntExist() throws ClientWebException
    {

        RackDto rack = get(validRackUri, RackDto.MEDIA_TYPE).getEntity(RackDto.class);
        rack.setShortDescription("dummy_description");
        rack.setId(1234);

        ClientResponse response = put(invalidRack, rack);
        assertEquals(response.getStatusCode(), Status.NOT_FOUND.getStatusCode());
    }

    @Test
    public void modifyRackWrongDatacenter() throws ClientWebException
    {
        RackDto rack = get(validRackUri, RackDto.MEDIA_TYPE).getEntity(RackDto.class);
        String old = rack.getShortDescription();

        rack.setShortDescription("dummy_description");

        ClientResponse response = put(rackInvalidDatacenter, rack);
        assertEquals(response.getStatusCode(), Status.NOT_FOUND.getStatusCode());

        assertNonEmptyErrors(response.getEntity(ErrorsDto.class));

        rack = get(validRackUri, RackDto.MEDIA_TYPE).getEntity(RackDto.class);

        assertEquals(old, rack.getShortDescription());
    }

    @Test
    public void removeRack() throws ClientWebException
    {
        Resource resource = client.resource(validRackUri);

        ClientResponse response = resource.accept(MediaType.APPLICATION_XML).delete();
        assertEquals(204, response.getStatusCode());
    }

    @Test
    public void removeRackDoesntExist() throws ClientWebException
    {
        Resource resource = client.resource(invalidRack);

        ClientResponse response = resource.accept(MediaType.APPLICATION_XML).delete();
        assertEquals(response.getStatusCode(), Status.NOT_FOUND.getStatusCode());
    }

    @Test
    public void removeRackWrongDatacenter() throws ClientWebException
    {
        ClientResponse response = delete(rackInvalidDatacenter);
        assertEquals(response.getStatusCode(), Status.NOT_FOUND.getStatusCode());

        response = get(validRackUri, RackDto.MEDIA_TYPE);
        RackDto rack = response.getEntity(RackDto.class);

        assertEquals(200, response.getStatusCode());
        assertNotNull(rack);
    }

    protected ClientResponse createRack(final String name, final String shortDescription,
        final String longDescription)
    {
        String rackUri = resolveRacksURI(validRack.getDatacenter().getId());
        
        RackDto r = new RackDto();
        r.setName(name);
        r.setShortDescription(shortDescription);
        r.setLongDescription(longDescription);

        return post(rackUri, r);
    }
}
