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

import static com.abiquo.api.common.UriTestResolver.resolveRemoteServicesURI;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;
import static org.testng.Assert.assertNull;

import javax.ws.rs.core.MediaType;

import org.apache.wink.client.ClientResponse;
import org.apache.wink.client.Resource;
import org.testng.annotations.Test;

import com.abiquo.api.common.Assert;
import com.abiquo.api.exceptions.APIError;
import com.abiquo.model.enumerator.RemoteServiceType;
import com.abiquo.server.core.infrastructure.Datacenter;
import com.abiquo.server.core.infrastructure.RemoteService;
import com.abiquo.server.core.infrastructure.RemoteServiceDto;
import com.abiquo.server.core.infrastructure.RemoteServicesDto;

public class RemoteServicesResourceIT extends AbstractJpaGeneratorIT
{

    @Test
    public void getRemoteServicesList() throws Exception
    {
        RemoteService rs = remoteServiceGenerator.createInstance(RemoteServiceType.VIRTUAL_FACTORY);
        setup(rs.getDatacenter(), rs);

        String uri = resolveRemoteServicesURI(rs.getDatacenter().getId());

        Resource resource = client.resource(uri).accept(MediaType.APPLICATION_XML);

        ClientResponse response = resource.get();

        assertEquals(response.getStatusCode(), 200);

        RemoteServicesDto entity = response.getEntity(RemoteServicesDto.class);
        assertNotNull(entity);
        assertEquals(entity.getCollection().size(), 1);
    }

    @Test
    public void createRemoteService()
    {
        Datacenter dc = datacenterGenerator.createUniqueInstance();
        setup(dc);

        String uri = resolveRemoteServicesURI(dc.getId());

        Resource resource = client.resource(uri);

        RemoteServiceDto dto = new RemoteServiceDto();
        dto.setType(RemoteServiceType.DHCP_SERVICE);
        dto.setUri("http://localhost:8080/fooooo");

        ClientResponse response =
            resource.contentType(MediaType.APPLICATION_XML).accept(MediaType.APPLICATION_XML)
                .post(dto);

        assertEquals(response.getStatusCode(), 201);

        RemoteServiceDto entityPost = response.getEntity(RemoteServiceDto.class);
        assertNotNull(entityPost);
        assertEquals(dto.getUri(), entityPost.getUri());

        assertNull(entityPost.getConfigurationErrors());
    }

    @Test
    public void createRemoteServiceDuplicatedURL()
    {
        RemoteService rs = remoteServiceGenerator.createInstance(RemoteServiceType.VIRTUAL_FACTORY);
        rs.setUri("http://localhost:8080/ssm");

        setup(rs.getDatacenter(), rs);

        String uri = resolveRemoteServicesURI(rs.getDatacenter().getId());

        Resource resource =
            client.resource(uri).contentType(MediaType.APPLICATION_XML)
                .accept(MediaType.APPLICATION_XML);

        RemoteServiceDto dto = new RemoteServiceDto();
        dto.setType(RemoteServiceType.APPLIANCE_MANAGER);
        dto.setUri("http://localhost:8080/ssm");
        dto.setStatus(1);

        ClientResponse response = resource.post(dto);
        Assert.assertErrors(response, 409, APIError.REMOTE_SERVICE_URL_ALREADY_EXISTS.getCode());
    }

    @Test
    public void createRemoteServiceDuplicatedType()
    {
        RemoteService rs = remoteServiceGenerator.createInstance(RemoteServiceType.NODE_COLLECTOR);
        setup(rs.getDatacenter(), rs);

        String uri = resolveRemoteServicesURI(rs.getDatacenter().getId());

        Resource resource =
            client.resource(uri).contentType(MediaType.APPLICATION_XML)
                .accept(MediaType.APPLICATION_XML);

        RemoteServiceDto dto = new RemoteServiceDto();
        dto.setType(RemoteServiceType.NODE_COLLECTOR);
        dto.setUri("http://remoteService:8080/nc");
        dto.setStatus(1);

        ClientResponse response = resource.post(dto);
        Assert.assertErrors(response, 409, APIError.REMOTE_SERVICE_TYPE_EXISTS.getCode());
    }
}
