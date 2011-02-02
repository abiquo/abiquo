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

/**
 * abiCloud  community version
 * cloud management application for hybrid clouds
 * Copyright (C) 2008-2010 - Soluciones Grid SL
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

import java.util.List;

import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.Context;

import org.apache.wink.common.annotations.Parent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import com.abiquo.api.exceptions.APIError;
import com.abiquo.api.exceptions.NotFoundException;
import com.abiquo.api.services.DatastoreService;
import com.abiquo.api.services.MachineService;
import com.abiquo.api.transformer.ModelTransformer;
import com.abiquo.api.util.IRESTBuilder;
import com.abiquo.server.core.infrastructure.Datastore;
import com.abiquo.server.core.infrastructure.DatastoreDto;
import com.abiquo.server.core.infrastructure.DatastoresDto;

@Parent(MachineResource.class)
@Controller
@Path(DatastoresResource.DATASTORES_PATH)
public class DatastoresResource
{
    public static final String DATASTORES_PATH = "datastores";

    @Autowired
    DatastoreService service;

    @Autowired
    MachineService machineService;

    @GET
    public DatastoresDto getDatastores(
        @PathParam(DatacenterResource.DATACENTER) Integer datacenterId,
        @PathParam(RackResource.RACK) Integer rackId,
        @PathParam(MachineResource.MACHINE) Integer machineId, @Context IRESTBuilder restBuilder)
        throws Exception
    {
        if (!machineService.isAssignedTo(datacenterId, rackId, machineId))
        {
            throw new NotFoundException(APIError.NOT_ASSIGNED_MACHINE_DATACENTER_RACK);
        }

        List<Datastore> datastores = service.getMachineDatastores(machineId);
        DatastoresDto dto = new DatastoresDto();

        for (Datastore datastore : datastores)
        {
            dto.add(createTransferObject(datastore, datacenterId, rackId, machineId, restBuilder));
        }

        return dto;
    }

    @POST
    public DatastoreDto postDatastore(
        @PathParam(DatacenterResource.DATACENTER) Integer datacenterId,
        @PathParam(RackResource.RACK) Integer rackId,
        @PathParam(MachineResource.MACHINE) Integer machineId, DatastoreDto dto,
        @Context IRESTBuilder restBuilder) throws Exception
    {
        if (!machineService.isAssignedTo(datacenterId, rackId, machineId))
        {
            throw new NotFoundException(APIError.NOT_ASSIGNED_MACHINE_DATACENTER_RACK);
        }

        Datastore datastore = service.addDatastore(dto, machineId);

        return createTransferObject(datastore, datacenterId, rackId, machineId, restBuilder);
    }

    public static DatastoreDto createTransferObject(Datastore datastore, Integer datacenterId,
        Integer rackId, Integer machineId, IRESTBuilder restBuilder) throws Exception
    {
        DatastoreDto dto = ModelTransformer.transportFromPersistence(DatastoreDto.class, datastore);

        dto.setLinks(restBuilder.buildDatastoreLinks(datacenterId, rackId, machineId, datastore));

        return dto;
    }
}
