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

import static com.abiquo.testng.TestConfig.ALL_INTEGRATION_TESTS;
import static com.abiquo.testng.TestConfig.DEFAULT_SERVER_PORT;
import static com.abiquo.testng.TestConfig.getParameter;

import javax.ws.rs.core.MediaType;

import org.apache.commons.codec.binary.Base64;
import org.apache.wink.client.ClientConfig;
import org.apache.wink.client.ClientResponse;
import org.apache.wink.client.Resource;
import org.apache.wink.client.RestClient;
import org.springframework.security.context.SecurityContextHolder;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import com.abiquo.api.common.AbstractGeneratorTest;
import com.abiquo.api.common.SysadminAuthentication;
import com.abiquo.model.transport.SingleResourceTransportDto;

@Test(groups = ALL_INTEGRATION_TESTS)
public class AbstractJpaGeneratorIT extends AbstractGeneratorTest
{
    protected static final String API_URI = "http://localhost:"
        + String.valueOf(getEmbededServerPort()) + "/api";

    protected static final String WEBAPP_PORT = "webapp.port";

    protected static int getEmbededServerPort()
    {
        return Integer.valueOf(getParameter(WEBAPP_PORT, DEFAULT_SERVER_PORT));
    }

    protected RestClient client;

    public AbstractJpaGeneratorIT()
    {
        super();
        ClientConfig restConfig = new ClientConfig();
        restConfig.followRedirects(false);
        client = new RestClient(restConfig);
    }

    @BeforeMethod
    @Override
    public void setup()
    {
        // Do not remove. This method must be in this class in order to properly handle TestNG
        // groups
        super.setup();
    }

    @AfterMethod
    @Override
    public void tearDown()
    {
        // Do not remove. This method must be in this class in order to properly handle TestNG
        // groups
        super.tearDown();
    }

    protected Resource resource(final String uri, final String username, final String password)
    {
        String basicAuth = basicAuth(username, password);

        return client.resource(uri).accept(MediaType.APPLICATION_XML)
            .header("Authorization", "Basic " + basicAuth);
    }

    protected ClientResponse get(final String uri, final String username, final String password)
    {
        String basicAuth = basicAuth(username, password);

        return client.resource(uri).accept(MediaType.APPLICATION_XML)
            .header("Authorization", "Basic " + basicAuth).get();
    }

    protected ClientResponse get(final String uri, final String mediaType)
    {
        return client.resource(uri).accept(mediaType).get();
    }

    protected ClientResponse get(final String uri, final String username, final String password,
        final String mediaType)
    {
        String basicAuth = basicAuth(username, password);

        return client.resource(uri).accept(mediaType).header("Authorization", "Basic " + basicAuth)
            .get();
    }

    protected ClientResponse get(final String uri, final String username, final String password,
        final String acceptType, final String contentType)
    {
        String basicAuth = basicAuth(username, password);

        return client.resource(uri).accept(acceptType).contentType(contentType)
            .header("Authorization", "Basic " + basicAuth).get();
    }

    protected ClientResponse post(final String uri, final SingleResourceTransportDto dto)
    {
        Resource resource = client.resource(uri);
        if (dto != null)
        {
            // Only add the headers if the request has a body
            resource.accept(dto.getMediaType());
            resource.contentType(dto.getMediaType());
        }
        return resource.post(dto);
    }

    protected ClientResponse post(final String uri, final SingleResourceTransportDto dto,
        final String mediaType)
    {
        return client.resource(uri).accept(mediaType).contentType(mediaType).post(dto);
    }

    protected ClientResponse post(final String uri, final SingleResourceTransportDto dto,
        final String username, final String password)
    {
        String basicAuth = basicAuth(username, password);
        Resource resource = client.resource(uri).header("Authorization", "Basic " + basicAuth);
        if (dto != null)
        {
            // Only add the headers if the request has a body
            resource.accept(dto.getMediaType());
            resource.contentType(dto.getMediaType());
        }
        return resource.post(dto);
    }

    protected ClientResponse post(final String uri, final SingleResourceTransportDto dto,
        final String username, final String password, final String accept, final String contentType)
    {
        String basicAuth = basicAuth(username, password);

        Resource res = client.resource(uri).header("Authorization", "Basic " + basicAuth);
        if (accept != null)
        {
            res.accept(accept);
        }
        if (contentType != null)
        {
            res.contentType(contentType);
        }
        return res.post(dto);
    }

    protected ClientResponse put(final String uri, final SingleResourceTransportDto dto)
    {
        Resource resource = client.resource(uri);
        if (dto != null)
        {
            // Only add the headers if the request has a body
            resource.accept(dto.getMediaType());
            resource.contentType(dto.getMediaType());
        }
        return resource.put(dto);
    }

    protected ClientResponse put(final String uri, final SingleResourceTransportDto dto,
        final String mediaType)
    {
        return client.resource(uri).accept(mediaType).contentType(mediaType).put(dto);
    }

    protected ClientResponse put(final String uri, final SingleResourceTransportDto dto,
        final String username, final String password)
    {
        String basicAuth = basicAuth(username, password);
        Resource resource = client.resource(uri).header("Authorization", "Basic " + basicAuth);
        if (dto != null)
        {
            // Only add the headers if the request has a body
            resource.accept(dto.getMediaType());
            resource.contentType(dto.getMediaType());
        }
        return resource.put(dto);
    }

    protected ClientResponse put(final String uri, final SingleResourceTransportDto dto,
        final String username, final String password, final String mediaType)
    {
        String basicAuth = basicAuth(username, password);

        return client.resource(uri).accept(mediaType).contentType(mediaType)
            .header("Authorization", "Basic " + basicAuth).put(dto);
    }

    protected ClientResponse put(final String uri, final SingleResourceTransportDto dto,
        final String username, final String password, final String accept, final String contentType)
    {
        String basicAuth = basicAuth(username, password);
        Resource resource = client.resource(uri);
        if (accept != null)
        {
            resource.accept(accept);
        }
        if (contentType != null)
        {
            resource.contentType(contentType);
        }
        return resource.header("Authorization", "Basic " + basicAuth).put(dto);
    }

    protected ClientResponse delete(final String uri)
    {
        return client.resource(uri).delete();
    }

    protected ClientResponse delete(final String uri, final String username, final String password)
    {
        String basicAuth = basicAuth(username, password);

        return client.resource(uri).header("Authorization", "Basic " + basicAuth).delete();
    }

    protected ClientResponse delete(final String uri, final String username, final String password,
        final String mediaType)
    {
        String basicAuth = basicAuth(username, password);

        return client.resource(uri).accept(mediaType).contentType(mediaType)
            .header("Authorization", "Basic " + basicAuth).delete();
    }

    protected String basicAuth(final String username, final String password)
    {
        return new String(Base64.encodeBase64((username + ":" + password).getBytes()));
    }

    protected void loginAsSysAdmin()
    {
        SecurityContextHolder.getContext().setAuthentication(new SysadminAuthentication());
    }
}
