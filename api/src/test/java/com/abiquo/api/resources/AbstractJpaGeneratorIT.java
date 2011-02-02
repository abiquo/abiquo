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

import javax.ws.rs.core.MediaType;

import org.apache.commons.codec.binary.Base64;
import org.apache.wink.client.ClientResponse;
import org.apache.wink.client.RestClient;

import com.abiquo.api.common.AbstractGeneratorTest;

public class AbstractJpaGeneratorIT extends AbstractGeneratorTest
{

    protected static final String API_URI = "http://localhost:9009/api";

    protected RestClient client = new RestClient();

    protected ClientResponse get(String uri)
    {
        return client.resource(uri).accept(MediaType.APPLICATION_XML).get();
    }

    protected ClientResponse get(String uri, String username, String password)
    {
        String basicAuth = basicAuth(username, password);

        return client.resource(uri).accept(MediaType.APPLICATION_XML).header("Authorization",
            "Basic " + basicAuth).get();
    }

    protected ClientResponse post(String uri, Object dto)
    {
        return client.resource(uri).accept(MediaType.APPLICATION_XML).contentType(
            MediaType.APPLICATION_XML).post(dto);
    }

    protected ClientResponse post(String uri, Object dto, String username, String password)
    {
        String basicAuth = basicAuth(username, password);

        return client.resource(uri).accept(MediaType.APPLICATION_XML).contentType(
            MediaType.APPLICATION_XML).header("Authorization", "Basic " + basicAuth).post(dto);
    }

    protected ClientResponse put(String uri, Object dto)
    {
        return client.resource(uri).accept(MediaType.APPLICATION_XML).contentType(
            MediaType.APPLICATION_XML).put(dto);
    }

    protected ClientResponse put(String uri, Object dto, String username, String password)
    {
        String basicAuth = basicAuth(username, password);

        return client.resource(uri).accept(MediaType.APPLICATION_XML).contentType(
            MediaType.APPLICATION_XML).header("Authorization", "Basic " + basicAuth).put(dto);
    }

    protected ClientResponse delete(String uri)
    {
        return client.resource(uri).accept(MediaType.APPLICATION_XML).delete();
    }

    protected ClientResponse delete(String uri, String username, String password)
    {
        String basicAuth = basicAuth(username, password);

        return client.resource(uri).accept(MediaType.APPLICATION_XML).contentType(
            MediaType.APPLICATION_XML).header("Authorization", "Basic " + basicAuth).delete();
    }

    private String basicAuth(String username, String password)
    {
        return new String(Base64.encodeBase64((username + ":" + password).getBytes()));
    }
}
