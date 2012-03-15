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

import static com.abiquo.api.resources.DatastoresResource.createTransferObject;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;

import org.apache.wink.common.annotations.Parent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import com.abiquo.api.exceptions.APIError;
import com.abiquo.api.exceptions.NotFoundException;
import com.abiquo.api.services.DatastoreService;
import com.abiquo.api.util.IRESTBuilder;
import com.abiquo.model.util.ModelTransformer;
import com.abiquo.server.core.infrastructure.Datastore;
import com.abiquo.server.core.infrastructure.DatastoreDto;

@Parent(DatastoresResource.class)
@Controller
@Path(DatastoreResource.DATASTORE_PATH)
public class DatastoreResource extends AbstractResource
{
    public static final String DATASTORE = "datastore";

    public static final String DATASTORE_PATH = "{" + DATASTORE + "}";

    @Autowired
    DatastoreService service;

    /**
     * Returns a datastore from a machine
     * 
     * @title Retrieve a datastore
     * @param datacenterId identifier of the datacenter
     * @param rackId identifier of the rack
     * @param machineId identifier of the machine
     * @param datastoreId identifier of the datastore
     * @param restBuilder a Context-injected object to create the links of the Dto
     * @return a {DatastoreDto} object with the requested datastore
     * @throws Exception
     */
    @GET
    @Produces(DatastoreDto.MEDIA_TYPE)
    public DatastoreDto getDatastore(
        @PathParam(DatacenterResource.DATACENTER) final Integer datacenterId,
        @PathParam(RackResource.RACK) final Integer rackId,
        @PathParam(MachineResource.MACHINE) final Integer machineId,
        @PathParam(DATASTORE) final Integer datastoreId, @Context final IRESTBuilder restBuilder)
        throws Exception
    {
        validatePathParameters(datacenterId, rackId, machineId, datastoreId);

        Datastore ds = service.getDatastore(datastoreId);

        return createTransferObject(ds, datacenterId, rackId, machineId, restBuilder);
    }

    /**
     * Modifies a datastore
     * 
     * @title Updates a Datastore
     * @param datacenterId identifier of the datacenter
     * @param rackId identifier of the rack
     * @param machineId identifier of the machine
     * @param datastoreId identifier of the datastore
     * @param dto datastore to modify
     * @param restBuilder a Context-injected object to create the links of the Dto
     * @return a {DatastoreDto} with the modified datastore
     * @throws Exception
     */
    @PUT
    @Consumes(DatastoreDto.MEDIA_TYPE)
    @Produces(DatastoreDto.MEDIA_TYPE)
    public DatastoreDto updateDatastore(
        @PathParam(DatacenterResource.DATACENTER) final Integer datacenterId,
        @PathParam(RackResource.RACK) final Integer rackId,
        @PathParam(MachineResource.MACHINE) final Integer machineId,
        @PathParam(DATASTORE) final Integer datastoreId, final DatastoreDto dto,
        @Context final IRESTBuilder restBuilder) throws Exception
    {
        validatePathParameters(datacenterId, rackId, machineId, datastoreId);

        Datastore ds = service.updateDatastore(datastoreId, dto);

        return createTransferObject(ds, datacenterId, rackId, machineId, restBuilder);
    }

    private void validatePathParameters(final Integer datacenterId, final Integer rackId,
        final Integer machineId, final Integer datastoreId) throws NotFoundException
    {
        if (!service.isAssignedTo(datacenterId, rackId, machineId, datastoreId))
        {
            throw new NotFoundException(APIError.DATASTORE_NOT_ASSIGNED_TO_MACHINE);
        }
    }

    // Create the persistence object.
    public static Datastore createPersistenceObject(final DatastoreDto dto) throws Exception
    {
        return ModelTransformer.persistenceFromTransport(Datastore.class, dto);
    }

}
