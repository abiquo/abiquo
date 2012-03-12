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
import javax.ws.rs.core.Response.Status;

import org.apache.wink.client.ClientResponse;
import org.apache.wink.client.ClientWebException;
import org.apache.wink.client.Resource;
import org.apache.wink.client.RestClient;
import org.testng.annotations.Test;

import com.abiquo.model.enumerator.RemoteServiceType;
import com.abiquo.model.transport.error.ErrorsDto;
import com.abiquo.server.core.infrastructure.RemoteService;
import com.abiquo.server.core.infrastructure.RemoteServiceDto;

public class RemoteServiceResourceIT extends AbstractJpaGeneratorIT
{

    @Test
    public void getRemoteService() throws Exception
    {
        RemoteService rs = remoteServiceGenerator.createInstance(RemoteServiceType.VIRTUAL_FACTORY);
        setup(rs.getDatacenter(), rs);

        String uri = resolveRemoteServiceURI(rs.getDatacenter().getId(), rs.getType());

        ClientResponse response = get(uri, RemoteServiceDto.MEDIA_TYPE);
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

        ClientResponse response = get(uri, RemoteServiceDto.MEDIA_TYPE);
        assertEquals(response.getStatusCode(), 404);

        assertNonEmptyErrors(response.getEntity(ErrorsDto.class));
    }

    @Test
    public void getRemoteServiceWithWrongDatacenter() throws ClientWebException
    {
        RemoteService rs = remoteServiceGenerator.createInstance(RemoteServiceType.VIRTUAL_FACTORY);
        setup(rs.getDatacenter(), rs);

        String uri = resolveRemoteServiceURI(1234, rs.getType());

        ClientResponse response = get(uri, RemoteServiceDto.MEDIA_TYPE);
        assertEquals(404, response.getStatusCode());

        assertNonEmptyErrors(response.getEntity(ErrorsDto.class));
    }

    @Test
    public void modifyRemoteService() throws ClientWebException
    {
        RemoteService rs = remoteServiceGenerator.createInstance(RemoteServiceType.DHCP_SERVICE);
        setup(rs.getDatacenter(), rs);

        String uri = resolveRemoteServiceURI(rs.getDatacenter().getId(), rs.getType());

        RemoteServiceDto remoteService =
            get(uri, RemoteServiceDto.MEDIA_TYPE).getEntity(RemoteServiceDto.class);
        remoteService.setUri("http://example.com:8080/nodecollector");

        ClientResponse response = put(uri, remoteService);
        assertEquals(response.getStatusCode(), Status.OK.getStatusCode());

        RemoteServiceDto modified = response.getEntity(RemoteServiceDto.class);
        assertEquals("http://example.com:8080/nodecollector", modified.getUri());
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

        RemoteServiceDto remoteService =
            get(validUri, RemoteServiceDto.MEDIA_TYPE).getEntity(RemoteServiceDto.class);
        remoteService.setType(RemoteServiceType.STORAGE_SYSTEM_MONITOR);

        ClientResponse response = put(invalidUri, remoteService);

        assertEquals(response.getStatusCode(), Status.NOT_FOUND.getStatusCode());
    }

    @Test
    public void modifyRemoteServiceWrongDatacenter() throws ClientWebException
    {
        RemoteService rs = remoteServiceGenerator.createInstance(RemoteServiceType.VIRTUAL_FACTORY);
        setup(rs.getDatacenter(), rs);

        String validUri = resolveRemoteServiceURI(rs.getDatacenter().getId(), rs.getType());
        String invalidUri = resolveRemoteServiceURI(1234, rs.getType());

        RemoteServiceDto remoteService =
            get(validUri, RemoteServiceDto.MEDIA_TYPE).getEntity(RemoteServiceDto.class);
        String old = remoteService.getUri();

        remoteService.setUri("http://10.60.1.11:8080/path");

        ClientResponse response = put(invalidUri, remoteService);

        assertEquals(404, response.getStatusCode());

        assertNonEmptyErrors(response.getEntity(ErrorsDto.class));

        remoteService =
            get(validUri, RemoteServiceDto.MEDIA_TYPE).getEntity(RemoteServiceDto.class);

        assertEquals(old, remoteService.getUri());
    }

    @Test
    public void removeRemoteService() throws ClientWebException
    {
        RemoteService rs = remoteServiceGenerator.createInstance(RemoteServiceType.VIRTUAL_FACTORY);
        setup(rs.getDatacenter(), rs);

        String uri = resolveRemoteServiceURI(rs.getDatacenter().getId(), rs.getType());

        ClientResponse response = delete(uri);
        assertEquals(response.getStatusCode(), Status.NO_CONTENT.getStatusCode());
    }

    @Test
    public void removeRemoteServiceDoesntExist() throws ClientWebException
    {
        RemoteService rs = remoteServiceGenerator.createInstance(RemoteServiceType.VIRTUAL_FACTORY);
        setup(rs.getDatacenter(), rs);

        String uri =
            resolveRemoteServiceURI(rs.getDatacenter().getId(),
                RemoteServiceType.STORAGE_SYSTEM_MONITOR);

        ClientResponse response = delete(uri);
        assertEquals(response.getStatusCode(), Status.NOT_FOUND.getStatusCode());
    }

    @Test
    public void removeRemoteServiceWrongDatacenter() throws ClientWebException
    {
        RemoteService rs = remoteServiceGenerator.createInstance(RemoteServiceType.VIRTUAL_FACTORY);
        setup(rs.getDatacenter(), rs);

        String uri = resolveRemoteServiceURI(1234, rs.getType());

        ClientResponse response = delete(uri);
        assertEquals(404, response.getStatusCode());

        response =
            get(resolveRemoteServiceURI(rs.getDatacenter().getId(), rs.getType()),
                RemoteServiceDto.MEDIA_TYPE);
        RemoteServiceDto remoteService = response.getEntity(RemoteServiceDto.class);

        assertEquals(200, response.getStatusCode());
        assertNotNull(remoteService);
    }

}
