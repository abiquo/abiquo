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
import javax.ws.rs.GET;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.Context;

import org.apache.wink.common.annotations.Parent;
import org.springframework.stereotype.Controller;

import com.abiquo.api.exceptions.APIError;
import com.abiquo.api.exceptions.NotFoundException;
import com.abiquo.api.services.RackService;
import com.abiquo.api.transformer.ModelTransformer;
import com.abiquo.api.util.IRESTBuilder;
import com.abiquo.server.core.infrastructure.Rack;
import com.abiquo.server.core.infrastructure.RackDto;

@Parent(RacksResource.class)
@Path(RackResource.RACK_PARAM)
@Controller
public class RackResource extends AbstractResource
{
    public static final String RACK = "rack";

    public static final String RACK_PARAM = "{" + RACK + "}";

    @Resource(name = "rackService")
    RackService service;

    @GET
    public RackDto getRack(@PathParam(DatacenterResource.DATACENTER) Integer datacenterId,
        @PathParam(RACK) Integer rackId, @Context IRESTBuilder restBuilder) throws Exception
    {
        validatePathParameters(datacenterId, rackId);

        Rack rack = service.getRack(rackId);
        return createTransferObject(rack, restBuilder);
    }

    @PUT
    public RackDto modifyRack(@PathParam(DatacenterResource.DATACENTER) Integer datacenterId,
        @PathParam(RACK) Integer rackId, RackDto rack, @Context IRESTBuilder restBuilder)
        throws Exception
    {
        validatePathParameters(datacenterId, rackId);

        Rack r = service.modifyRack(rackId, rack);

        return createTransferObject(r, restBuilder);
    }

    // @DELETE
    public void deleteRack(@PathParam(DatacenterResource.DATACENTER) Integer datacenterId,
        @PathParam(RACK) Integer rackId)
    {
        validatePathParameters(datacenterId, rackId);
        service.removeRack(rackId);
    }

    private static RackDto addLinks(IRESTBuilder restBuilder, RackDto rack, Integer datacenterId)
    {
        rack.setLinks(restBuilder.buildRackLinks(datacenterId, rack));
        return rack;
    }

    public static RackDto createTransferObject(Rack rack, IRESTBuilder restBuilder)
        throws Exception
    {
        RackDto dto = ModelTransformer.transportFromPersistence(RackDto.class, rack);
        dto = addLinks(restBuilder, dto, rack.getDatacenter().getId());
        return dto;
    }

    public static Rack createPersistenceObject(RackDto rack) throws Exception
    {
        return ModelTransformer.persistenceFromTransport(Rack.class, rack);
    }

    protected void validatePathParameters(final Integer datacenterId, final Integer rackId)
        throws NotFoundException
    {
        if (!service.isAssignedTo(datacenterId, rackId))
        {
            throw new NotFoundException(APIError.NOT_ASSIGNED_RACK_DATACENTER);
        }
    }
}
