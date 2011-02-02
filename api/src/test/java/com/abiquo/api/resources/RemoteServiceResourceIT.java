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
import static com.abiquo.api.common.UriTestResolver.resolveRemoteServiceURI;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;

import javax.ws.rs.core.MediaType;

import org.apache.wink.client.ClientResponse;
import org.apache.wink.client.ClientWebException;
import org.apache.wink.client.Resource;
import org.apache.wink.client.RestClient;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.Test;

import com.abiquo.model.enumerator.RemoteServiceType;
import com.abiquo.model.transport.error.ErrorsDto;
import com.abiquo.server.core.infrastructure.RemoteService;
import com.abiquo.server.core.infrastructure.RemoteServiceDto;

public class RemoteServiceResourceIT extends AbstractJpaGeneratorIT
{

    @AfterMethod
    public void tearDown()
    {
        tearDown("remote_service", "datacenter");
    }

    @Test
    public void getRemoteService() throws Exception
    {
        RemoteService rs = remoteServiceGenerator.createInstance(RemoteServiceType.VIRTUAL_FACTORY);
        setup(rs.getDatacenter(), rs);

        String uri = resolveRemoteServiceURI(rs.getDatacenter().getId(), rs.getType());

        Resource resource = client.resource(uri);

        ClientResponse response = resource.accept(MediaType.APPLICATION_XML).get();
        RemoteServiceDto remoteService = response.getEntity(RemoteServiceDto.class);

        assertEquals(200, response.getStatusCode());
        assertNotNull(remoteService);

        assertLinkExist(remoteService, uri, "edit");
        assertLinkExist(remoteService, resolveDatacenterURI(rs.getDatacenter().getId()),
            "datacenter");
    }

    @Test
    public void getRemoteServiceDoesntExist() throws Exception
    {
        RemoteService rs = remoteServiceGenerator.createInstance(RemoteServiceType.VIRTUAL_FACTORY);
        setup(rs.getDatacenter(), rs);

        String uri =
            resolveRemoteServiceURI(rs.getDatacenter().getId(),
                RemoteServiceType.STORAGE_SYSTEM_MONITOR);

        Resource resource = client.resource(uri);

        ClientResponse response = resource.accept(MediaType.APPLICATION_XML).get();
        assertEquals(response.getStatusCode(), 404);

        assertNonEmptyErrors(response.getEntity(ErrorsDto.class));
    }

    @Test
    public void getRemoteServiceWithWrongDatacenter() throws ClientWebException
    {
        RemoteService rs = remoteServiceGenerator.createInstance(RemoteServiceType.VIRTUAL_FACTORY);
        setup(rs.getDatacenter(), rs);

        String uri = resolveRemoteServiceURI(1234, rs.getType());
        Resource resource = client.resource(uri);

        ClientResponse response = resource.accept(MediaType.APPLICATION_XML).get();
        assertEquals(404, response.getStatusCode());

        assertNonEmptyErrors(response.getEntity(ErrorsDto.class));
    }

    @Test
    public void modifyRemoteService() throws ClientWebException
    {
        RemoteService rs = remoteServiceGenerator.createInstance(RemoteServiceType.VIRTUAL_FACTORY);
        setup(rs.getDatacenter(), rs);

        String uri = resolveRemoteServiceURI(rs.getDatacenter().getId(), rs.getType());

        Resource resource = client.resource(uri).accept(MediaType.APPLICATION_XML);

        RemoteServiceDto remoteService = resource.get(RemoteServiceDto.class);
        remoteService.setType(RemoteServiceType.DHCP_SERVICE);

        ClientResponse response =
            resource.contentType(MediaType.APPLICATION_XML).put(remoteService);
        assertEquals(200, response.getStatusCode());

        RemoteServiceDto modified = response.getEntity(RemoteServiceDto.class);
        assertEquals(RemoteServiceType.DHCP_SERVICE, modified.getType());
    }

    @Test
    public void modifyRemoteServiceDoesntExist() throws ClientWebException
    {
        RemoteService rs = remoteServiceGenerator.createInstance(RemoteServiceType.VIRTUAL_FACTORY);
        setup(rs.getDatacenter(), rs);

        String validUri = resolveRemoteServiceURI(rs.getDatacenter().getId(), rs.getType());
        String invalidUri =
            resolveRemoteServiceURI(rs.getDatacenter().getId(),
                RemoteServiceType.STORAGE_SYSTEM_MONITOR);

        Resource resource = client.resource(validUri).accept(MediaType.APPLICATION_XML);

        RemoteServiceDto remoteService = resource.get(RemoteServiceDto.class);
        remoteService.setType(RemoteServiceType.STORAGE_SYSTEM_MONITOR);

        resource = client.resource(invalidUri);

        ClientResponse response =
            resource.accept(MediaType.APPLICATION_XML).contentType(MediaType.APPLICATION_XML).put(
                remoteService);

        assertEquals(response.getStatusCode(), 404);
    }

    @Test
    public void modifyRemoteServiceWrongDatacenter() throws ClientWebException
    {
        RemoteService rs = remoteServiceGenerator.createInstance(RemoteServiceType.VIRTUAL_FACTORY);
        setup(rs.getDatacenter(), rs);

        String validUri = resolveRemoteServiceURI(rs.getDatacenter().getId(), rs.getType());
        String invalidUri = resolveRemoteServiceURI(1234, rs.getType());

        Resource resource = client.resource(validUri).accept(MediaType.APPLICATION_XML);

        RemoteServiceDto remoteService = resource.get(RemoteServiceDto.class);
        String old = remoteService.getUri();

        remoteService.setUri("http://10.60.1.11:8080/path");

        resource = client.resource(invalidUri);

        ClientResponse response =
            resource.contentType(MediaType.APPLICATION_XML).put(remoteService);

        assertEquals(404, response.getStatusCode());

        assertNonEmptyErrors(response.getEntity(ErrorsDto.class));

        resource = client.resource(validUri);
        remoteService = resource.accept(MediaType.APPLICATION_XML).get(RemoteServiceDto.class);

        assertEquals(old, remoteService.getUri());
    }

    @Test
    public void removeRemoteService() throws ClientWebException
    {
        RemoteService rs = remoteServiceGenerator.createInstance(RemoteServiceType.VIRTUAL_FACTORY);
        setup(rs.getDatacenter(), rs);

        String uri = resolveRemoteServiceURI(rs.getDatacenter().getId(), rs.getType());

        RestClient client = new RestClient();
        Resource resource = client.resource(uri);

        ClientResponse response = resource.accept(MediaType.APPLICATION_XML).delete();
        assertEquals(response.getStatusCode(), 204);
    }

    @Test
    public void removeRemoteServiceDoesntExist() throws ClientWebException
    {
        RemoteService rs = remoteServiceGenerator.createInstance(RemoteServiceType.VIRTUAL_FACTORY);
        setup(rs.getDatacenter(), rs);

        String uri =
            resolveRemoteServiceURI(rs.getDatacenter().getId(),
                RemoteServiceType.STORAGE_SYSTEM_MONITOR);

        RestClient client = new RestClient();
        Resource resource = client.resource(uri);

        ClientResponse response = resource.accept(MediaType.APPLICATION_XML).delete();
        assertEquals(response.getStatusCode(), 404);
    }

    @Test
    public void removeRemoteServiceWrongDatacenter() throws ClientWebException
    {
        RemoteService rs = remoteServiceGenerator.createInstance(RemoteServiceType.VIRTUAL_FACTORY);
        setup(rs.getDatacenter(), rs);

        String uri = resolveRemoteServiceURI(1234, rs.getType());

        RestClient client = new RestClient();
        Resource resource = client.resource(uri);

        ClientResponse response = resource.accept(MediaType.APPLICATION_XML).delete();
        assertEquals(404, response.getStatusCode());

        client = new RestClient();
        resource =
            client.resource(resolveRemoteServiceURI(rs.getDatacenter().getId(), rs.getType()));

        response = resource.accept(MediaType.APPLICATION_XML).get();
        RemoteServiceDto remoteService = response.getEntity(RemoteServiceDto.class);

        assertEquals(200, response.getStatusCode());
        assertNotNull(remoteService);
    }

}
