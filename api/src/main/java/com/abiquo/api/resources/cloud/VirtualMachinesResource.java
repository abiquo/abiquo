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

package com.abiquo.api.resources.cloud;

import java.util.List;

import javax.validation.constraints.Min;
import javax.ws.rs.Consumes;
import javax.ws.rs.DefaultValue;
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

import com.abiquo.api.resources.AbstractResource;
import com.abiquo.api.services.cloud.VirtualApplianceService;
import com.abiquo.api.services.cloud.VirtualDatacenterService;
import com.abiquo.api.services.cloud.VirtualMachineService;
import com.abiquo.api.util.IRESTBuilder;
import com.abiquo.server.core.cloud.NodeVirtualImage;
import com.abiquo.server.core.cloud.VirtualAppliance;
import com.abiquo.server.core.cloud.VirtualDatacenter;
import com.abiquo.server.core.cloud.VirtualMachine;
import com.abiquo.server.core.cloud.VirtualMachineDto;
import com.abiquo.server.core.cloud.VirtualMachineWithNodeDto;
import com.abiquo.server.core.cloud.VirtualMachinesDto;
import com.abiquo.server.core.cloud.VirtualMachinesWithNodeDto;
import com.abiquo.server.core.cloud.VirtualMachinesWithNodeExtendedDto;

@Parent(VirtualApplianceResource.class)
@Path(VirtualMachinesResource.VIRTUAL_MACHINES_PATH)
@Controller
public class VirtualMachinesResource extends AbstractResource
{
    public static final String VIRTUAL_MACHINES_PATH = "virtualmachines";

    @Autowired
    protected VirtualMachineService service;

    @Autowired
    protected VirtualApplianceService vappService;

    @Autowired
    protected VirtualDatacenterService vdcService;

    /**
     * Returns all virtual machines
     * 
     * @title Retrive all virtual machines
     * @param vdcId
     * @param vappId
     * @param startwith
     * @param orderBy
     * @param filter
     * @param limit
     * @param descOrAsc
     * @param restBuilder
     * @return
     * @throws Exception
     */
    @GET
    @Produces(VirtualMachinesDto.MEDIA_TYPE)
    public VirtualMachinesDto getVirtualMachines(
        @PathParam(VirtualDatacenterResource.VIRTUAL_DATACENTER) final Integer vdcId,
        @PathParam(VirtualApplianceResource.VIRTUAL_APPLIANCE) final Integer vappId,
        @QueryParam(START_WITH) @DefaultValue("0") @Min(0) final Integer startwith,
        @QueryParam(BY) @DefaultValue("name") final String orderBy,
        @QueryParam(FILTER) @DefaultValue("") final String filter,
        @QueryParam(LIMIT) @Min(1) @DefaultValue(DEFAULT_PAGE_LENGTH_STRING) final Integer limit,
        @QueryParam(ASC) @DefaultValue("true") final Boolean descOrAsc,
        @Context final IRESTBuilder restBuilder) throws Exception
    {
        final VirtualAppliance vapp = vappService.getVirtualAppliance(vdcId, vappId);

        final List<VirtualMachine> all =
            service.findByVirtualAppliance(vapp, startwith, orderBy, filter, limit, descOrAsc);
        final VirtualMachinesDto vappsDto = new VirtualMachinesDto();

        if (all != null && !all.isEmpty())
        {
            for (final VirtualMachine v : all)
            {
                vappsDto.add(VirtualMachineResource.createTransferObject(v,
                    vapp.getVirtualDatacenter(), vapp.getId(), restBuilder, null, null, null));
            }
        }

        return vappsDto;
    }

    /**
     * Creates a resource {@link VirtualMachine} under this root.
     * 
     * @title Create a virtual machine
     * @wiki The name of the virtual machine is not the same as you can see through our GUI. But the
     *       identifier of the virtual machine itself in the hypervisor. If you want to change the
     *       virtual machine's name to a more friendly name you must change the node name. A node is
     *       the representation of a member of a virtual appliance with a name. To be able to set
     *       that name you must create the virtual machine with its node data. This representation
     *       has 'application/vnd.abiquo.virtualmachinewithnode+xml' media type. Here is the minimal
     *       data required for creating a virtual machine.
     * @param virtualMachineDto virtual machine
     * @param vdcId identifier of the virtual datacenter
     * @param vappId identifier of the virtual appliance
     * @param restBuilder {@link IRESTBuilder} object injected by context.
     * @return the generate {@link VirtualMachineDto} object.
     * @throws Exception
     */
    @POST
    @Consumes(VirtualMachineDto.MEDIA_TYPE)
    @Produces(VirtualMachineDto.MEDIA_TYPE)
    public VirtualMachineDto createVirtualMachine(
        @PathParam(VirtualDatacenterResource.VIRTUAL_DATACENTER) final Integer vdcId,
        @PathParam(VirtualApplianceResource.VIRTUAL_APPLIANCE) final Integer vappId,
        final VirtualMachineDto virtualMachineDto, @Context final IRESTBuilder restBuilder)
        throws Exception
    {
        final VirtualMachine virtualMachine =
            service.createVirtualMachine(vdcId, vappId, virtualMachineDto);

        VirtualDatacenter vdc = vdcService.getVirtualDatacenter(vdcId);

        final VirtualMachineDto vappsDto =
            VirtualMachineResource.createTransferObject(virtualMachine, vdc, vappId, restBuilder,
                null, null, null);

        return vappsDto;
    }

    /**
     * Creates a resource {@link VirtualMachine} under this root.
     * 
     * @title Create a virtual machine and a node
     * @param v virtual machine
     * @param vdcId identifier of the virtual datacenter
     * @param vappId identifier of the virtual appliance
     * @param restBuilder {@link IRESTBuilder} object injected by context.
     * @return the generate {@link VirtualMachineDto} object.
     * @throws Exception
     */
    @POST
    @Consumes(VirtualMachineWithNodeDto.MEDIA_TYPE)
    @Produces(VirtualMachineDto.MEDIA_TYPE)
    public VirtualMachineDto createVirtualMachineWithNode(
        @PathParam(VirtualDatacenterResource.VIRTUAL_DATACENTER) final Integer vdcId,
        @PathParam(VirtualApplianceResource.VIRTUAL_APPLIANCE) final Integer vappId,
        final VirtualMachineWithNodeDto virtualMachineWithNodeDto,
        @Context final IRESTBuilder restBuilder) throws Exception
    {
        final VirtualMachine virtualMachine =
            service.createVirtualMachine(vdcId, vappId, virtualMachineWithNodeDto);
        VirtualDatacenter vdc = vdcService.getVirtualDatacenter(vdcId);

        final VirtualMachineDto vappsDto =
            VirtualMachineResource.createTransferObject(virtualMachine, vdc, vappId, restBuilder,
                null, null, null);

        return vappsDto;
    }

    /**
     * Returns all virtual machines with own node
     * 
     * @title Retrieve all virtual machines with own node
     * @param vdcId
     * @param vappId
     * @param restBuilder
     * @return
     * @throws Exception
     */
    @GET
    @Produces({VirtualMachinesWithNodeDto.MEDIA_TYPE})
    public VirtualMachinesWithNodeDto getVirtualMachinesWithNode(
        @PathParam(VirtualDatacenterResource.VIRTUAL_DATACENTER) final Integer vdcId,
        @PathParam(VirtualApplianceResource.VIRTUAL_APPLIANCE) final Integer vappId,
        @Context final IRESTBuilder restBuilder) throws Exception
    {
        vappService.getVirtualAppliance(vdcId, vappId);

        final List<NodeVirtualImage> all = service.getNodeVirtualImages(vdcId, vappId);
        final VirtualMachinesWithNodeDto vappsDto = new VirtualMachinesWithNodeDto();

        for (final NodeVirtualImage n : all)
        {
            vappsDto.add(VirtualMachineResource.createNodeTransferObject(n, vdcId, vappId,
                restBuilder, null, null, null));
        }

        return vappsDto;
    }

    /**
     * Returns all virutal machines with own node and user extra info
     * 
     * @title Retrieve all virutal machines with own node and user extra info
     * @param vdcId
     * @param vappId
     * @param restBuilder
     * @return
     * @throws Exception
     */
    @GET
    @Produces(VirtualMachinesWithNodeExtendedDto.MEDIA_TYPE)
    public VirtualMachinesWithNodeExtendedDto getVirtualMachinesWithNodeExtended(
        @PathParam(VirtualDatacenterResource.VIRTUAL_DATACENTER) final Integer vdcId,
        @PathParam(VirtualApplianceResource.VIRTUAL_APPLIANCE) final Integer vappId,
        @Context final IRESTBuilder restBuilder) throws Exception
    {
        vappService.getVirtualAppliance(vdcId, vappId);
        final List<NodeVirtualImage> all = service.getNodeVirtualImages(vdcId, vappId);
        final VirtualMachinesWithNodeExtendedDto vmsDto = new VirtualMachinesWithNodeExtendedDto();

        for (final NodeVirtualImage n : all)
        {
            vmsDto.add(VirtualMachineResource.createNodeExtendedTransferObject(n, vdcId, vappId,
                restBuilder, null, null, null));
        }

        return vmsDto;
    }

    public static VirtualMachinesDto createTransferObjects(final List<VirtualMachine> vms,
        final List<VirtualDatacenter> vdcs, final IRESTBuilder restBuilder)
    {
        VirtualMachinesDto dtos = new VirtualMachinesDto();
        for (int i = 0; i < vms.size(); i++)
        {
            dtos.getCollection().add(
                VirtualMachineResource.createTransferObject(vms.get(i), vdcs.get(i), restBuilder));
        }
        return dtos;
    }

}
