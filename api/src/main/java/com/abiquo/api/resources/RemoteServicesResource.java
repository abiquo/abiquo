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

import static com.abiquo.api.resources.RemoteServiceResource.createPersistenceObject;
import static com.abiquo.api.resources.RemoteServiceResource.createTransferObject;

import java.util.List;

import javax.annotation.Resource;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;

import org.apache.wink.common.annotations.Parent;
import org.springframework.stereotype.Controller;

import com.abiquo.api.services.InfrastructureService;
import com.abiquo.api.util.IRESTBuilder;
import com.abiquo.server.core.infrastructure.RemoteService;
import com.abiquo.server.core.infrastructure.RemoteServiceDto;
import com.abiquo.server.core.infrastructure.RemoteServicesDto;

/**
 * @author scastro
 * @wiki The Remote Service resource offers the functionality of managing the remote services of a
 *       datacenter in a logical way.
 */
@Parent(DatacenterResource.class)
@Path(RemoteServicesResource.REMOTE_SERVICES_PATH)
@Controller
public class RemoteServicesResource extends AbstractResource
{
    public final static String REMOTE_SERVICES_PATH = "remoteservices";

    // @Autowired
    @Resource(name = "infrastructureService")
    private InfrastructureService service;

    /**
     * Returns all remote services from a datacenter
     * 
     * @title Retrive a list of Remote Services
     * @param datacenterId identifier of the datacenter
     * @param restBuilder a Context-injected object to create the links of the Dto
     * @return a {RemoteServicesDto} object with all remote services from the datacenter
     * @throws Exception
     */
    @GET
    @Produces(RemoteServicesDto.MEDIA_TYPE)
    public RemoteServicesDto getRemoteServices(
        @PathParam(DatacenterResource.DATACENTER) final Integer datacenterId,
        @Context final IRESTBuilder restBuilder) throws Exception
    {
        List<RemoteService> all = service.getRemoteServicesByDatacenter(datacenterId);
        RemoteServicesDto remoteServices = new RemoteServicesDto();

        if (all != null && !all.isEmpty())
        {
            for (RemoteService r : all)
            {
                remoteServices.add(createTransferObject(r, restBuilder));
            }
        }

        return remoteServices;
    }

    /**
     * Creates a remote service and returns it after creation
     * 
     * @title Create a Remote Service
     * @param datacenterId identifier of the datacenter
     * @param remoteService remote service to create
     * @param restBuilder a Context-injected object to create the links of the Dto
     * @return a {RemoteSerivceDto} object with the created remote service
     * @throws Exception
     */
    @POST
    @Consumes(RemoteServiceDto.MEDIA_TYPE)
    @Produces(RemoteServiceDto.MEDIA_TYPE)
    public RemoteServiceDto postRemoteService(
        @PathParam(DatacenterResource.DATACENTER) final Integer datacenterId,
        final RemoteServiceDto remoteService, @Context final IRESTBuilder restBuilder)
        throws Exception
    {
        RemoteService rs = createPersistenceObject(remoteService);
        RemoteServiceDto persistentService = service.addRemoteService(rs, datacenterId);

        return RemoteServiceResource.addLinks(restBuilder, persistentService, datacenterId);
    }

}
