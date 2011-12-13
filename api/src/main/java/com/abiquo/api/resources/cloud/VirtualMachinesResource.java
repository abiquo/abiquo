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

import static com.abiquo.api.resources.EnterpriseResource.ENTERPRISE;
import static com.abiquo.api.resources.appslibrary.VirtualMachineTemplateResource.VIRTUAL_MACHINE_TEMPLATE;
import static com.abiquo.api.util.URIResolver.buildPath;

import java.util.Collection;
import java.util.List;

import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;

import org.apache.wink.common.annotations.Parent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import com.abiquo.api.exceptions.APIError;
import com.abiquo.api.resources.AbstractResource;
import com.abiquo.api.resources.EnterpriseResource;
import com.abiquo.api.resources.EnterprisesResource;
import com.abiquo.api.resources.appslibrary.DatacenterRepositoriesResource;
import com.abiquo.api.resources.appslibrary.DatacenterRepositoryResource;
import com.abiquo.api.resources.appslibrary.VirtualMachineTemplateResource;
import com.abiquo.api.resources.appslibrary.VirtualMachineTemplatesResource;
import com.abiquo.api.services.cloud.VirtualApplianceService;
import com.abiquo.api.services.cloud.VirtualMachineService;
import com.abiquo.api.util.IRESTBuilder;
import com.abiquo.model.util.ModelTransformer;
import com.abiquo.server.core.appslibrary.VirtualMachineTemplate;
import com.abiquo.server.core.cloud.Hypervisor;
import com.abiquo.server.core.cloud.NodeVirtualImage;
import com.abiquo.server.core.cloud.VirtualAppliance;
import com.abiquo.server.core.cloud.VirtualMachine;
import com.abiquo.server.core.cloud.VirtualMachineDto;
import com.abiquo.server.core.cloud.VirtualMachinesDto;
import com.abiquo.server.core.cloud.VirtualMachinesWithNodeDto;
import com.abiquo.server.core.enterprise.Enterprise;
import com.abiquo.server.core.enterprise.User;
import com.abiquo.server.core.infrastructure.Datacenter;
import com.abiquo.server.core.infrastructure.Machine;
import com.abiquo.server.core.infrastructure.Rack;

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

    @Produces(MediaType.APPLICATION_XML)
    @GET
    public VirtualMachinesDto getVirtualMachines(
        @PathParam(VirtualDatacenterResource.VIRTUAL_DATACENTER) final Integer vdcId,
        @PathParam(VirtualApplianceResource.VIRTUAL_APPLIANCE) final Integer vappId,
        @Context final IRESTBuilder restBuilder) throws Exception
    {
        final VirtualAppliance vapp = vappService.getVirtualAppliance(vdcId, vappId);

        final List<VirtualMachine> all = service.findByVirtualAppliance(vapp);
        final VirtualMachinesDto vappsDto = new VirtualMachinesDto();

        if (all != null && !all.isEmpty())
        {
            for (final VirtualMachine v : all)
            {
                vappsDto.add(VirtualMachineResource.createTransferObject(v, vapp
                    .getVirtualDatacenter().getId(), vapp.getId(), restBuilder));
            }
        }

        return vappsDto;
    }

    public static VirtualMachinesDto createAdminTransferObjects(
        final Collection<VirtualMachine> vms, final IRESTBuilder restBuilder) throws Exception
    {
        final VirtualMachinesDto machines = new VirtualMachinesDto();
        for (final VirtualMachine vm : vms)
        {
            machines.add(createAdminTransferObjects(vm, restBuilder));
        }

        return machines;
    }

    public static VirtualMachineDto createAdminTransferObjects(final VirtualMachine vm,
        final IRESTBuilder restBuilder) throws Exception
    {

        final VirtualMachineDto vmDto =
            VirtualMachineResource.createTransferObject(vm, restBuilder);

        final Hypervisor hypervisor = vm.getHypervisor();
        final Machine machine = hypervisor == null ? null : hypervisor.getMachine();
        final Rack rack = machine == null ? null : machine.getRack();
        final Datacenter dc = rack == null ? null : rack.getDatacenter();

        final Enterprise enterprise = vm.getEnterprise() == null ? null : vm.getEnterprise();
        final User user = vm.getUser() == null ? null : vm.getUser();

        vmDto.addLinks(restBuilder.buildVirtualMachineAdminLinks(dc == null ? null : dc.getId(),
            rack == null ? null : rack.getId(), machine == null ? null : machine.getId(),
            enterprise == null ? null : enterprise.getId(), user == null ? null : user.getId()));

        final VirtualMachineTemplate vmtemplate = vm.getVirtualMachineTemplate();
        vmDto.addLink(restBuilder.buildVirtualMachineTemplateLink(vmtemplate.getEnterprise().getId(), vmtemplate
            .getRepository().getDatacenter().getId(), vmtemplate.getId()));
        return vmDto;
    }

    /**
     * Creates a resource {@link VirtualMachine} under this root.
     * 
     * @param v virtual machine
     * @param vdcId identifier of the virtual datacenter
     * @param vappId identifier of the virtual appliance
     * @param restBuilder {@link IRESTBuilder} object injected by context.
     * @return the generate {@link VirtualMachineDto} object.
     * @throws Exception
     */
    @POST
    public VirtualMachineDto createVirtualMachine(
        @PathParam(VirtualDatacenterResource.VIRTUAL_DATACENTER) final Integer vdcId,
        @PathParam(VirtualApplianceResource.VIRTUAL_APPLIANCE) final Integer vappId,
        final VirtualMachineDto virtualMachineDto, @Context final IRESTBuilder restBuilder)
        throws Exception
    {

        final VirtualMachine virtualMachine =
            service.createVirtualMachine(vdcId, vappId, virtualMachineDto);

        final VirtualMachineDto vappsDto =
            VirtualMachineResource.createTransferObject(virtualMachine, vdcId, vappId, restBuilder);

        return vappsDto;
    }

    @GET
    @Produces(VirtualMachineResource.VM_NODE_MEDIA_TYPE)
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
                restBuilder));
        }

        return vappsDto;
    }

    public static VirtualMachinesDto createTransferObjects(final Collection<VirtualMachine> vms,
        final IRESTBuilder restBuilder)
    {

        VirtualMachinesDto dtos = new VirtualMachinesDto();
        for (VirtualMachine m : vms)
        {
            dtos.getCollection().add(VirtualMachineResource.createTransferObject(m, restBuilder));
        }
        return dtos;
    }

}
