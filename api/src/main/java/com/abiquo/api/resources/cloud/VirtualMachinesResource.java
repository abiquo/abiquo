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
import static com.abiquo.api.util.URIResolver.buildPath;
import static com.abiquo.api.util.URIResolver.resolveFromURI;

import java.util.Collection;
import java.util.List;

import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MultivaluedMap;

import org.apache.wink.common.annotations.Parent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import com.abiquo.api.exceptions.APIError;
import com.abiquo.api.exceptions.NotFoundException;
import com.abiquo.api.resources.AbstractResource;
import com.abiquo.api.resources.EnterpriseResource;
import com.abiquo.api.resources.EnterprisesResource;
import com.abiquo.api.services.cloud.VirtualApplianceService;
import com.abiquo.api.services.cloud.VirtualMachineService;
import com.abiquo.api.util.IRESTBuilder;
import com.abiquo.model.rest.RESTLink;
import com.abiquo.model.util.ModelTransformer;
import com.abiquo.server.core.cloud.Hypervisor;
import com.abiquo.server.core.cloud.VirtualAppliance;
import com.abiquo.server.core.cloud.VirtualMachine;
import com.abiquo.server.core.cloud.VirtualMachineDto;
import com.abiquo.server.core.cloud.VirtualMachinesDto;
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

    @GET
    public VirtualMachinesDto getVirtualMachines(
        @PathParam(VirtualDatacenterResource.VIRTUAL_DATACENTER) final Integer vdcId,
        @PathParam(VirtualApplianceResource.VIRTUAL_APPLIANCE) final Integer vappId,
        @Context final IRESTBuilder restBuilder) throws Exception
    {
        VirtualAppliance vapp = vappService.getVirtualAppliance(vdcId, vappId);

        List<VirtualMachine> all = service.findByVirtualAppliance(vapp);
        VirtualMachinesDto vappsDto = new VirtualMachinesDto();

        if (all != null && !all.isEmpty())
        {
            for (VirtualMachine v : all)
            {
                vappsDto.add(createCloudTransferObject(v, vapp.getVirtualDatacenter().getId(),
                    vapp.getId(), restBuilder));
            }
        }

        return vappsDto;
    }

    public static VirtualMachinesDto createAdminTransferObjects(
        final Collection<VirtualMachine> vms, final IRESTBuilder restBuilder) throws Exception
    {
        VirtualMachinesDto machines = new VirtualMachinesDto();
        for (VirtualMachine vm : vms)
        {
            machines.add(createAdminTransferObjects(vm, restBuilder));
        }

        return machines;
    }

    public static VirtualMachineDto createAdminTransferObjects(final VirtualMachine vm,
        final IRESTBuilder restBuilder) throws Exception
    {

        VirtualMachineDto vmDto = VirtualMachineResource.createTransferObject(vm, restBuilder);

        Hypervisor hypervisor = vm.getHypervisor();
        Machine machine = hypervisor == null ? null : hypervisor.getMachine();
        Rack rack = machine == null ? null : machine.getRack();
        Datacenter dc = rack == null ? null : rack.getDatacenter();

        Enterprise enterprise = vm.getEnterprise() == null ? null : vm.getEnterprise();
        User user = vm.getUser() == null ? null : vm.getUser();

        vmDto.addLinks(restBuilder.buildVirtualMachineAdminLinks(dc == null ? null : dc.getId(),
            rack == null ? null : rack.getId(), machine == null ? null : machine.getId(),
            enterprise == null ? null : enterprise.getId(), user == null ? null : user.getId()));

        return vmDto;
    }

    /**
     * Converts to the transfer object for the VirtualMachine POJO when the request is from the
     * /cloud URI
     * 
     * @param v virtual machine
     * @param vdcId identifier of the virtual datacenter
     * @param vappId identifier of the virtual appliance
     * @param restBuilder {@link IRESTBuilder} object injected by context.
     * @return the generate {@link VirtualMachineDto} object.
     * @throws Exception
     */
    public static VirtualMachineDto createCloudTransferObject(final VirtualMachine v,
        final Integer vdcId, final Integer vappId, final IRESTBuilder restBuilder) throws Exception
    {
        VirtualMachineDto vmDto =
            VirtualMachineResource.createTransferObject(v, vdcId, vappId, restBuilder);
        vmDto.addLinks(restBuilder.buildVirtualMachineCloudLinks(vdcId, vappId, v.getId(),
            v.isChefEnabled()));
        return vmDto;
    }

    public static VirtualMachineDto createCloudAdminTransferObject(final VirtualMachine vm,
        final Integer vdcId, final Integer vappId, final IRESTBuilder restBuilder) throws Exception
    {
        VirtualMachineDto vmDto =
            VirtualMachineResource.createTransferObject(vm, vdcId, vappId, restBuilder);
        Hypervisor hypervisor = vm.getHypervisor();
        Machine machine = hypervisor == null ? null : hypervisor.getMachine();
        Rack rack = machine == null ? null : machine.getRack();

        Enterprise enterprise = vm.getEnterprise() == null ? null : vm.getEnterprise();
        User user = vm.getUser() == null ? null : vm.getUser();

        vmDto.addLinks(restBuilder.buildVirtualMachineCloudAdminLinks(vdcId, vappId, vm.getId(),
            rack == null ? null : rack.getDatacenter().getId(), rack == null ? null : rack.getId(),
            machine == null ? null : machine.getId(),
            enterprise == null ? null : enterprise.getId(), user == null ? null : user.getId(),
            vm.isChefEnabled()));

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

        VirtualMachine vm = createVirtualMachineFromDto(virtualMachineDto);

        Integer enterpriseId =
            getLinkId(virtualMachineDto.searchLink(ENTERPRISE),
                EnterprisesResource.ENTERPRISES_PATH, EnterpriseResource.ENTERPRISE_PARAM,
                ENTERPRISE, APIError.NON_EXISTENT_ENTERPRISE);

        // TODO this depends on VirtualImage resource and is not done yet
        Integer vImageId = 1;
        // getLinkId(virtualMachineDto.searchLink("virtualimage"),
        // EnterprisesResource.ENTERPRISES_PATH, EnterpriseResource.ENTERPRISE_PARAM,
        // ENTERPRISE, APIError.NON_EXISTENT_VIRTUAL_IMAGE);

        VirtualMachine virtualMachine =
            service.createVirtualMachine(vm, enterpriseId, vImageId, vdcId, vappId);

        VirtualMachineDto vappsDto =
            createCloudTransferObject(virtualMachine, vdcId, vappId, restBuilder);

        return vappsDto;
    }

    private Integer getVirtualImageId(final RESTLink searchLink)
    {
        // TODO Auto-generated method stub
        return 1;
    }

    /**
     * Creates a {@link VirtualMachine} out of the {@link VirtualMachineDto}.
     * 
     * @param virtualMachineDto
     * @return
     * @throws Exception VirtualMachine
     */
    private VirtualMachine createVirtualMachineFromDto(final VirtualMachineDto virtualMachineDto)
        throws Exception
    {
        return ModelTransformer.persistenceFromTransport(VirtualMachine.class, virtualMachineDto);

    }

    /**
     * Extracts an Id from the link with the given rel.
     * 
     * @param link where the id.
     * @param path the resource.
     * @param param the parameter.
     * @param key the rel.
     * @param error what do we output.
     * @return Integer
     */
    private Integer getLinkId(final RESTLink link, final String path, final String param,
        final String key, final APIError error)
    {
        if (link == null)
        {
            throw new NotFoundException(error);
        }

        String buildPath = buildPath(path, param);
        MultivaluedMap<String, String> values = resolveFromURI(buildPath, link.getHref());

        if (values == null || !values.containsKey(key))
        {
            throw new NotFoundException(error);
        }

        return Integer.valueOf(values.getFirst(key));
    }
}
