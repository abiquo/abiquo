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
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;

import org.apache.wink.common.annotations.Parent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import com.abiquo.api.services.InfrastructureService;
import com.abiquo.api.util.IRESTBuilder;
import com.abiquo.server.core.infrastructure.Rack;
import com.abiquo.server.core.infrastructure.RackDto;
import com.abiquo.server.core.infrastructure.RacksDto;

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

    @GET
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

    @POST
    public RackDto postRack(@PathParam(DatacenterResource.DATACENTER) final Integer datacenterId,
        final RackDto rackDto, @Context final IRESTBuilder restBuilder) throws Exception
    {
        Rack rack = createPersistenceObject(rackDto);
        Rack r = infrastructureService.addRack(rack, datacenterId);
        return createTransferObject(r, restBuilder);
    }
}
