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
 * 
 */
package com.abiquo.api.resources;

import java.util.List;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;

import org.apache.wink.common.annotations.Parent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import com.abiquo.api.exceptions.mapper.APIExceptionMapper;
import com.abiquo.api.services.InfrastructureService;
import com.abiquo.api.util.IRESTBuilder;
import com.abiquo.server.core.cloud.VirtualMachine;
import com.abiquo.server.core.cloud.VirtualMachinesDto;

/**
 * <pre>
 * Resource that contains all the methods related to a Virtual Machine Infrastructure lists. Exposes all
 * the methods inside the URI
 * http://{host}/api/admin/datacenters/{dcid}/racks/{rackids}/machines/{machineid}/virtualmachines
 * related to perform actions to a list of virtual machines from the infrastructure path.
 * </pre>
 * 
 * @author jdevesa@abiquo.com
 */
@Parent(MachineResource.class)
@Path(VirtualMachinesInfrastructureResource.VIRTUAL_MACHINES_INFRASTRUCTURE_PARAM)
@Controller
public class VirtualMachinesInfrastructureResource extends AbstractResource
{
    /** Syncronize the GET? */
    public static final String SYNC = "sync";

    /** Name of the input path object. */
    public final static String VIRTUAL_MACHINES_INFRASTRUCTURE_PARAM = "virtualmachines";

    /** Autowired injected sping service */
    @Autowired
    protected InfrastructureService service;

    /**
     * Returns all the virtual machines deployed in a physical machine.
     * 
     * @param datacenterId identifier of the datacenter.
     * @param rackId identifier of the rack.
     * @param machineId identifier of the machine.
     * @param restBuilder a Context-injected object to create the links of the Dto
     * @return the {@link VirtualMachinesDto} object with all the virtualmachines deployed there.
     * @throws Exception any thrown exception. Moved to HTTP status code in the
     *             {@link APIExceptionMapper} exception mapper.
     */
    @GET
    public VirtualMachinesDto getInfrastructureVirtualMachines(
        @PathParam(DatacenterResource.DATACENTER) @NotNull @Min(1) final Integer datacenterId,
        @PathParam(RackResource.RACK) @NotNull @Min(1) final Integer rackId,
        @PathParam(MachineResource.MACHINE) @Min(1) @NotNull final Integer machineId,
        @QueryParam(SYNC) @DefaultValue("false") final Boolean sync,
        @Context final IRESTBuilder restBuilder) throws Exception
    {
        List<VirtualMachine> vms =
            service.getVirtualMachinesFromInfrastructure(datacenterId, rackId, machineId);

        VirtualMachinesDto dto = new VirtualMachinesDto();
        for (VirtualMachine vm : vms)
        {
            dto.getCollection().add(
                VirtualMachineInfrastructureResource.createTransferObject(datacenterId, rackId,
                    machineId, vm, restBuilder));
        }
        return dto;
    }
}
