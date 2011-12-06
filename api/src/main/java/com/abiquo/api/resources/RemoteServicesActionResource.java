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
import javax.validation.constraints.NotNull;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;

import org.apache.wink.common.annotations.Parent;
import org.springframework.stereotype.Controller;

import com.abiquo.api.services.InfrastructureService;
import com.abiquo.api.util.IRESTBuilder;
import com.abiquo.model.enumerator.RemoteServiceType;
import com.abiquo.server.core.infrastructure.Datacenter;

@Parent(RemoteServicesResource.class)
@Path("action")
@Controller
public class RemoteServicesActionResource extends AbstractResource
{
    public final static String URI = "uri";

    @Resource(name = "infrastructureService")
    private InfrastructureService service;

    @GET
    @Path(InfrastructureService.CHECK_RESOURCE)
    public void pingRemoteService(
        @PathParam(DatacenterResource.DATACENTER) final Integer datacenterId,
        @QueryParam(RemoteServiceResource.REMOTE_SERVICE) @NotNull @com.abiquo.model.validation.RemoteService final String type,
        @QueryParam(URI) @NotNull final String uri, @Context final IRESTBuilder restBuilder)
        throws Exception
    {
        Datacenter dc = service.getDatacenter(datacenterId);
        service.checkRemoteServiceStatus(dc, RemoteServiceType.valueFromName(type), uri, true);
    }

}
