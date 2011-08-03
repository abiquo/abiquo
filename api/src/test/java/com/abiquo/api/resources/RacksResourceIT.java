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

import static com.abiquo.api.common.UriTestResolver.resolveRacksURI;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;

import javax.ws.rs.core.MediaType;

import org.apache.wink.client.ClientResponse;
import org.apache.wink.client.Resource;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import com.abiquo.api.exceptions.APIError;
import com.abiquo.model.transport.error.ErrorsDto;
import com.abiquo.server.core.infrastructure.Rack;
import com.abiquo.server.core.infrastructure.RackDto;
import com.abiquo.server.core.infrastructure.RacksDto;

public class RacksResourceIT extends AbstractJpaGeneratorIT
{
    private String validRacksUri;

    @BeforeMethod
    public void setup()
    {
        Rack rack = rackGenerator.createUniqueInstance();
        setup(rack.getDatacenter(), rack);

        validRacksUri = resolveRacksURI(rack.getDatacenter().getId());
    }

    @Test
    public void getRacksList() throws Exception
    {
        Resource resource = client.resource(validRacksUri);

        ClientResponse response = resource.accept(MediaType.APPLICATION_XML).get();

        assertEquals(response.getStatusCode(), 200);

        RacksDto entity = response.getEntity(RacksDto.class);
        assertNotNull(entity);
        assertNotNull(entity.getCollection());
        assertEquals(entity.getCollection().size(), 1);
    }

    @Test
    public void createRack()
    {
        ClientResponse response =
            createRack("rack_test", "rack_description", "large_rack_description");

        assertEquals(response.getStatusCode(), 201);

        RackDto entityPost = response.getEntity(RackDto.class);
        assertNotNull(entityPost);
        assertEquals(entityPost.getName(), "rack_test");
        assertEquals(entityPost.getShortDescription(), "rack_description");
        assertEquals(entityPost.getLongDescription(), "large_rack_description");
    }

    @Test
    public void createRackFailWithDuplicatedName()
    {
        ClientResponse response =
            createRack("rack_test", "rack_description", "large_rack_description");

        assertEquals(response.getStatusCode(), 201);

        response = createRack("rack_test", "rack_description", "large_rack_description");
        assertEquals(response.getStatusCode(), 409);

        ErrorsDto errors = response.getEntity(ErrorsDto.class);
        assertEquals(errors.getCollection().get(0).getCode(), APIError.RACK_DUPLICATED_NAME
            .getCode());
    }

    protected ClientResponse createRack(String name, String shortDescription, String longDescription)
    {
        Resource resource = client.resource(validRacksUri);

        RackDto r = new RackDto();
        r.setName(name);
        r.setShortDescription(shortDescription);
        r.setLongDescription(longDescription);

        return resource.contentType(MediaType.APPLICATION_XML).accept(MediaType.APPLICATION_XML)
            .post(r);
    }
}
