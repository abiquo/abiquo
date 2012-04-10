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

import java.util.List;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
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
import com.abiquo.api.services.MachineService;
import com.abiquo.api.util.IRESTBuilder;
import com.abiquo.model.util.ModelTransformer;
import com.abiquo.server.core.infrastructure.Datastore;
import com.abiquo.server.core.infrastructure.DatastoreDto;
import com.abiquo.server.core.infrastructure.DatastoresDto;

/**
 * @wiki This resource allows you to manage datastores from physical machines in the cloud
 *       infrastructure.
 */
@Parent(MachineResource.class)
@Controller
@Path(DatastoresResource.DATASTORES_PATH)
public class DatastoresResource extends AbstractResource
{
    public static final String DATASTORES_PATH = "datastores";

    public static final String REFRESH_ACTION_PATH = "action/refresh";

    public static final String REFRESH_ACTION_REL = "refresh";

    public static DatastoreDto createTransferObject(final Datastore datastore,
        final Integer datacenterId, final Integer rackId, final Integer machineId,
        final IRESTBuilder restBuilder) throws Exception
    {
        DatastoreDto dto = ModelTransformer.transportFromPersistence(DatastoreDto.class, datastore);

        dto.setLinks(restBuilder.buildDatastoreLinks(datacenterId, rackId, machineId, datastore));

        return dto;
    }

    @Autowired
    MachineService machineService;

    @Autowired
    DatastoreService service;

    /**
     * Returns all datastores from a machine
     * 
     * @title Retrive a list of Datastores from a physical machine
     * @param datacenterId identifier of the datacenter
     * @param rackId identifier of the rack
     * @param machineId identifier of the machine
     * @param restBuilder a Context-injected object to create the links of the Dto
     * @return a {DatastoresDto} with all datastores from the machine
     * @throws Exception
     */
    @GET
    @Produces(DatastoresDto.MEDIA_TYPE)
    public DatastoresDto getDatastores(
        @PathParam(DatacenterResource.DATACENTER) final Integer datacenterId,
        @PathParam(RackResource.RACK) final Integer rackId,
        @PathParam(MachineResource.MACHINE) final Integer machineId,
        @Context final IRESTBuilder restBuilder) throws Exception
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

    /**
     * Creates a datastore and returns it after ceration
     * 
     * @title Create a new Datastore in a physical machine
     * @param datacenterId identifier of the datacenter
     * @param rackId identifier of the rack
     * @param machineId identifier of the machine
     * @param dto datastore to create
     * @param restBuilder a Context-injected object to create the links of the Dto
     * @return a {DatastoreDto} object with the created datastore
     * @throws Exception
     */
    @POST
    @Consumes(DatastoreDto.MEDIA_TYPE)
    @Produces(DatastoreDto.MEDIA_TYPE)
    public DatastoreDto postDatastore(
        @PathParam(DatacenterResource.DATACENTER) final Integer datacenterId,
        @PathParam(RackResource.RACK) final Integer rackId,
        @PathParam(MachineResource.MACHINE) final Integer machineId, final DatastoreDto dto,
        @Context final IRESTBuilder restBuilder) throws Exception
    {
        if (!machineService.isAssignedTo(datacenterId, rackId, machineId))
        {
            throw new NotFoundException(APIError.NOT_ASSIGNED_MACHINE_DATACENTER_RACK);
        }

        Datastore datastore = service.addDatastore(dto, machineId);

        return createTransferObject(datastore, datacenterId, rackId, machineId, restBuilder);
    }

    /**
     * @wiki Refreshes the list of datastores of the current physical machine. NOTE: In current
     *       version (2.0.1), only detects new datastores and don't delete old ones to avoid
     *       problems.
     * @param datacenterId identifier of the datacenter
     * @param rackId identifier of the rack
     * @param machineId identifier of the machine
     * @param restBuilder context-injected link builder.
     * @throws Exception any exception thrown.
     */
    @Path(DatastoresResource.REFRESH_ACTION_PATH)
    @GET
    public void refreshDatastores(
        @PathParam(DatacenterResource.DATACENTER) @NotNull @Min(1) final Integer datacenterId,
        @PathParam(RackResource.RACK) final Integer rackId,
        @PathParam(MachineResource.MACHINE) final Integer machineId,
        @Context final IRESTBuilder restBuilder) throws Exception
    {
        service.refreshDatastores(datacenterId, rackId, machineId);
    }

}
