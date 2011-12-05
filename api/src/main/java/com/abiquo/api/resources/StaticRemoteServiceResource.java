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

import javax.annotation.Resource;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.PUT;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.Context;

import com.abiquo.api.util.IRESTBuilder;
import com.abiquo.server.core.infrastructure.RemoteServiceDto;

public abstract class StaticRemoteServiceResource extends AbstractResource
{
    // @Autowired
    @Resource(name = "remoteServiceResource")
    private RemoteServiceResource remoteServiceResource;

    @GET
    public RemoteServiceDto getRemoteService(
        @PathParam(DatacenterResource.DATACENTER) final Integer datacenterId,
        @Context final IRESTBuilder restBuilder) throws Exception
    {
        return remoteServiceResource.getRemoteService(datacenterId, getRemoteServicePath(),
            restBuilder);
    }

    @PUT
    public RemoteServiceDto modifyRemoteService(
        @PathParam(DatacenterResource.DATACENTER) final Integer datacenterId,
        final RemoteServiceDto remoteService, @Context final IRESTBuilder restBuilder)
        throws Exception
    {
        return remoteServiceResource.modifyRemoteService(datacenterId, getRemoteServicePath(),
            remoteService, restBuilder);
    }

    @DELETE
    public void deleteRemoteService(
        @PathParam(DatacenterResource.DATACENTER) final Integer datacenterId)
    {
        remoteServiceResource.deleteRemoteService(datacenterId, getRemoteServicePath());
    }

    protected abstract String getRemoteServiceType();

    protected abstract String getRemoteServicePath();

}
