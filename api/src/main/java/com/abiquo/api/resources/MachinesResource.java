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

import static com.abiquo.api.resources.MachineResource.createTransferObject;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;

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
import javax.ws.rs.core.MediaType;

import org.apache.wink.common.annotations.Parent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import com.abiquo.api.exceptions.APIError;
import com.abiquo.api.exceptions.NotFoundException;
import com.abiquo.api.services.InfrastructureService;
import com.abiquo.api.services.MachineService;
import com.abiquo.api.util.IRESTBuilder;
import com.abiquo.model.transport.error.CommonError;
import com.abiquo.model.transport.error.ErrorsDto;
import com.abiquo.server.core.infrastructure.Machine;
import com.abiquo.server.core.infrastructure.MachineDto;
import com.abiquo.server.core.infrastructure.MachinesDto;
import com.abiquo.server.core.infrastructure.MachinesToCreateDto;

@Parent(RackResource.class)
@Path(MachinesResource.MACHINES_PATH)
@Controller
public class MachinesResource extends AbstractResource
{
    public static final String MACHINES_PATH = "machines";

    public static final String SINGLE_MACHINE_MIME_TYPE = "application/machinedto+xml";

    public static final String MULTIPLE_MACHINES_MIME_TYPE = "application/machinesdto+xml";

    @Autowired
    protected MachineService machineService;

    @Autowired
    protected InfrastructureService infrastructureService;

    @GET
    public MachinesDto getMachines(
        @PathParam(DatacenterResource.DATACENTER) @Min(1) final Integer datacenterId,
        @PathParam(RackResource.RACK) @Min(1) final Integer rackId,
        @QueryParam("filter") final String filter, @Context final IRESTBuilder restBuilder)
        throws Exception
    {
        if (!infrastructureService.isAssignedTo(datacenterId, rackId))
        {
            throw new NotFoundException(APIError.NOT_ASSIGNED_RACK_DATACENTER);
        }

        List<Machine> all = machineService.getMachinesByRack(rackId, filter);

        return transformMachinesDto(restBuilder, all);
    }

    @POST
    @Consumes(SINGLE_MACHINE_MIME_TYPE + "," + MediaType.APPLICATION_XML)
    @Produces(SINGLE_MACHINE_MIME_TYPE + "," + MediaType.APPLICATION_XML)
    public MachineDto postMachine(
        @PathParam(DatacenterResource.DATACENTER) @NotNull @Min(0) final Integer datacenterId,
        @PathParam(RackResource.RACK) @Min(0) final Integer rackId, final MachineDto machine,
        @Context final IRESTBuilder restBuilder) throws Exception
    {
        Machine mToCreate = MachineResource.createPersistenceObject(machine);
        Machine m = infrastructureService.addMachine(mToCreate, datacenterId, rackId);
        MachineDto transfer = createTransferObject(m, restBuilder);

        return transfer;
    }

    @POST
    @Consumes(MULTIPLE_MACHINES_MIME_TYPE)
    @Produces(MULTIPLE_MACHINES_MIME_TYPE)
    @SuppressWarnings("unchecked")
    public MachinesDto postMultipleMachines(
        @PathParam(DatacenterResource.DATACENTER) @NotNull @Min(0) final Integer datacenterId,
        @PathParam(RackResource.RACK) @Min(0) final Integer rackId,
        final MachinesToCreateDto machinesToCreateDto, @Context final IRESTBuilder restBuilder)
        throws Exception
    {

        Map<String, Object> map =
            infrastructureService.addMachines(datacenterId, rackId, machinesToCreateDto);

        List<Machine> machines = (List<Machine>) map.get("machines");
        MachinesDto machinesDto = MachineResource.createTransferObjects(machines, restBuilder);

        if (map.get("errors") != null)
        {
            Set<CommonError> errors = (Set<CommonError>) map.get("errors");
            ErrorsDto errorsDto = new ErrorsDto(errors);
            machinesDto.setErrors(errorsDto);
        }

        return machinesDto;
    }

    public static MachinesDto transformMachinesDto(final IRESTBuilder restBuilder,
        final Collection<Machine> machines) throws Exception
    {
        MachinesDto machinesDto = new MachinesDto();

        if (machines != null && !machines.isEmpty())
        {
            for (Machine m : machines)
            {
                machinesDto.add(createTransferObject(m, restBuilder));
            }
        }

        return machinesDto;
    }
}
