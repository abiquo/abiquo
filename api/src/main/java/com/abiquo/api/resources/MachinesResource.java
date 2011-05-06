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
import com.abiquo.api.services.MachineService;
import com.abiquo.api.services.InfrastructureService;
import com.abiquo.api.util.IRESTBuilder;
import com.abiquo.server.core.infrastructure.Machine;
import com.abiquo.server.core.infrastructure.MachineDto;
import com.abiquo.server.core.infrastructure.MachinesDto;

@Parent(RackResource.class)
@Path(MachinesResource.MACHINES_PATH)
@Controller
public class MachinesResource extends AbstractResource
{
    public static final String MACHINES_PATH = "machines";

    @Autowired
    private MachineService machineService;

    @Autowired
    private InfrastructureService rackService;

    @GET
    public MachinesDto getMachines(@PathParam(DatacenterResource.DATACENTER) Integer datacenterId,
        @PathParam(RackResource.RACK) Integer rackId, @Context IRESTBuilder restBuilder)
        throws Exception
    {
        if (!rackService.isAssignedTo(datacenterId, rackId))
        {
            throw new NotFoundException(APIError.NOT_ASSIGNED_RACK_DATACENTER);
        }

        List<Machine> all = machineService.getMachinesByRack(rackId);

        return transformMachinesDto(restBuilder, all);
    }
    
    @POST
    public MachineDto postMachines(@PathParam(DatacenterResource.DATACENTER) Integer datacenterId,
        @PathParam(RackResource.RACK) Integer rackId, MachineDto machine,
        @Context IRESTBuilder restBuilder) throws Exception
    {
        if (!rackService.isAssignedTo(datacenterId, rackId))
        {
            throw new NotFoundException(APIError.NOT_ASSIGNED_RACK_DATACENTER);
        }

        Machine m = machineService.addMachine(machine, rackId);
        MachineDto transfer = createTransferObject(m, restBuilder);

        return transfer;
    }

    public static MachinesDto transformMachinesDto(IRESTBuilder restBuilder,
        Collection<Machine> machines) throws Exception
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
