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

import static com.abiquo.api.resources.RackResource.createPersistenceObject;
import static com.abiquo.api.resources.RackResource.createTransferObject;

import java.util.List;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;

import org.apache.wink.common.annotations.Parent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import com.abiquo.api.exceptions.APIError;
import com.abiquo.api.exceptions.BadRequestException;
import com.abiquo.api.services.InfrastructureService;
import com.abiquo.api.util.IRESTBuilder;
import com.abiquo.server.core.infrastructure.Rack;
import com.abiquo.server.core.infrastructure.RackDto;
import com.abiquo.server.core.infrastructure.RacksDto;

/**
 * @author scastro
 * @wiki The Rack Resource offers the functionality of managing the rack infrastructure in a logical
 *       way. Distribute your machines into the Rack resource.
 */
@Parent(DatacenterResource.class)
@Path(RacksResource.RACKS_PATH)
@Controller
public class RacksResource extends AbstractResource
{
    // Define the static variables that represent the URI
    public static final String RACKS_PATH = "racks";

    // Define its service. It should only have ONE service!
    @Autowired
    protected InfrastructureService infrastructureService;

    /**
     * Returns all racks from a datacenter.
     * 
     * @title Retrieve a list of Racks
     * @param datacenterId identifier of the datacenter
     * @param filter
     * @param restBuilder a Context-injected object to create the links of the Dto
     * @return a {RacksDto} object with all racks from a datacenter
     * @throws Exception
     */
    @GET
    @Produces(RacksDto.MEDIA_TYPE)
    public RacksDto getRacks(
        @PathParam(DatacenterResource.DATACENTER) @NotNull @Min(1) final Integer datacenterId,
        @QueryParam("filter") final String filter, @Context final IRESTBuilder restBuilder)
        throws Exception
    {

        // Receive the Racks and convert them as RacksDto in the 'createTransferObject' loop.
        List<Rack> all = infrastructureService.getRacksByDatacenter(datacenterId, filter);
        RacksDto racks = new RacksDto();
        if (all != null && !all.isEmpty())
        {
            for (Rack r : all)
            {
                racks.add(createTransferObject(r, restBuilder));
            }
        }
        return racks;
    }

    /**
     * Creates a rack and returns it after creation
     * 
     * @title Create a new Rack
     * @param datacenterId identifier of the datacenter
     * @param rackDto rack to create
     * @param restBuilder a Context-injected object to create the links of the Dto
     * @return a {RackDto} object with the created rack
     * @throws Exception
     */
    @POST
    @Consumes(RackDto.MEDIA_TYPE)
    @Produces(RackDto.MEDIA_TYPE)
    public RackDto postRack(@PathParam(DatacenterResource.DATACENTER) final Integer datacenterId,
        final RackDto rackDto, @Context final IRESTBuilder restBuilder) throws Exception
    {
        // The rack must not exists
        if (rackDto.getId() != null)
        {
            throw new BadRequestException(APIError.STATUS_BAD_REQUEST);
        }

        Rack rack = createPersistenceObject(rackDto);
        Rack r = infrastructureService.addRack(rack, datacenterId);
        return createTransferObject(r, restBuilder);
    }
}
