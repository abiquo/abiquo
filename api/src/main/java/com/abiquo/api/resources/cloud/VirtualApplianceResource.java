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

import javax.ws.rs.GET;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.Context;

import org.apache.wink.common.annotations.Parent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import com.abiquo.api.services.IpAddressService;
import com.abiquo.api.services.UserService;
import com.abiquo.api.services.cloud.VirtualApplianceService;
import com.abiquo.api.util.IRESTBuilder;
import com.abiquo.model.util.ModelTransformer;
import com.abiquo.server.core.cloud.State;
import com.abiquo.server.core.cloud.VirtualAppliance;
import com.abiquo.server.core.cloud.VirtualApplianceDto;
import com.abiquo.server.core.cloud.VirtualImageDto;
import com.abiquo.server.core.cloud.VirtualMachineChangeStateResultDto;
import com.abiquo.server.core.infrastructure.network.IpPoolManagement;
import com.abiquo.server.core.infrastructure.network.IpsPoolManagementDto;

@Parent(VirtualAppliancesResource.class)
@Path(VirtualApplianceResource.VIRTUAL_APPLIANCE_PARAM)
@Controller
public class VirtualApplianceResource
{
    public static final String VIRTUAL_APPLIANCE = "virtualappliance";

    public static final String VIRTUAL_APPLIANCE_PARAM = "{" + VIRTUAL_APPLIANCE + "}";

    public static final String VIRTUAL_APPLIANCE_ACTION_GET_IPS = "/action/ips";

    public static final String VIRTUAL_APPLIANCE_ACTION_ADD_IMAGE = "/action/addImage";

    public static final String VIRTUAL_APPLIANCE_ACTION_DEPLOY = "/action/deploy";

    public static final String VIRTUAL_APPLIANCE_ACTION_POWERON = "/action/poweron";

    public static final String VIRTUAL_APPLIANCE_ACTION_POWEROFF = "/action/poweroff";

    public static final String VIRTUAL_APPLIANCE_ACTION_PAUSE = "/action/pause";

    public static final String VIRTUAL_APPLIANCE_ACTION_RESUME = "/action/resume";

    @Autowired
    VirtualApplianceService service;

    @Autowired
    IpAddressService ipService;

    @Autowired
    UserService userService;

    /**
     * Return the virtual appliance if exists.
     * 
     * @param vdcId identifier of the virtual datacenter.
     * @param vappId identifier of the virtual appliance.
     * @param restBuilder to build the links
     * @return the {@link VirtualApplianceDto} transfer object for the virtual appliance.
     * @throws Exception
     */
    @GET
    public VirtualApplianceDto getVirtualAppliance(
        @PathParam(VirtualDatacenterResource.VIRTUAL_DATACENTER) final Integer vdcId,
        @PathParam(VirtualApplianceResource.VIRTUAL_APPLIANCE) final Integer vappId,
        @Context final IRESTBuilder restBuilder) throws Exception
    {
        VirtualAppliance vapp = service.getVirtualAppliance(vdcId, vappId);

        return createTransferObject(vapp, restBuilder);
    }

    @PUT
    public VirtualApplianceDto updateVirtualAppliance(
        @PathParam(VirtualDatacenterResource.VIRTUAL_DATACENTER) final Integer vdcId,
        @PathParam(VirtualApplianceResource.VIRTUAL_APPLIANCE) final Integer vappId,
        final VirtualApplianceDto dto, @Context final IRESTBuilder restBuilder) throws Exception
    {
        VirtualAppliance vapp = service.updateVirtualAppliance(vdcId, vappId, dto);

        return createTransferObject(vapp, restBuilder);
    }

    @GET
    @Path(VirtualApplianceResource.VIRTUAL_APPLIANCE_ACTION_GET_IPS)
    public IpsPoolManagementDto getIPsByVirtualAppliance(
        @PathParam(VirtualDatacenterResource.VIRTUAL_DATACENTER) final Integer vdcId,
        @PathParam(VirtualApplianceResource.VIRTUAL_APPLIANCE) final Integer vappId,
        @Context final IRESTBuilder restBuilder) throws Exception
    {
        VirtualAppliance vapp = service.getVirtualAppliance(vdcId, vappId);

        // Get the list of ipPoolManagements objects
        List<IpPoolManagement> all = ipService.getListIpPoolManagementByVirtualApp(vapp);
        IpsPoolManagementDto ips = new IpsPoolManagementDto();
        for (IpPoolManagement ip : all)
        {
            ips.add(IpAddressesResource.createTransferObject(ip, restBuilder));
        }

        return ips;
    }

    /**
     * Return the {@link VirtualApplianceDt}o object from the POJO {@link VirtualAppliance}
     * 
     * @param vapp object to convert
     * @param builder context rest builder
     * @return the result Dto object
     * @throws Exception
     */
    public static VirtualApplianceDto createTransferObject(final VirtualAppliance vapp,
        final IRESTBuilder builder) throws Exception
    {
        VirtualApplianceDto dto =
            ModelTransformer.transportFromPersistence(VirtualApplianceDto.class, vapp);

        dto =
            addLinks(builder, dto, vapp.getVirtualDatacenter().getId(), vapp.getEnterprise()
                .getId());

        return dto;
    }

    private static VirtualApplianceDto addLinks(final IRESTBuilder builder,
        final VirtualApplianceDto dto, final Integer vdcId, final Integer enterpriseId)
    {
        dto.setLinks(builder.buildVirtualApplianceLinks(dto, vdcId, enterpriseId));

        return dto;
    }

    // @PUT
    // @Path(VIRTUAL_APPLIANCE_ACTION_ADD_IMAGE)
    /***********************************/
    /***********************************/
    /* EXPERIMENTAL, NOT AVAILABLE YET */
    /***********************************/
    /***********************************/
    public void addImage(
        @PathParam(VirtualDatacenterResource.VIRTUAL_DATACENTER) final Integer vdcId,
        @PathParam(VirtualApplianceResource.VIRTUAL_APPLIANCE) final Integer vappId,
        final VirtualImageDto image)
    {

    }

    // @POST
    // @Path(VIRTUAL_APPLIANCE_ACTION_DEPLOY)
    /***********************************/
    /***********************************/
    /* EXPERIMENTAL, NOT AVAILABLE YET */
    /***********************************/
    /***********************************/
    public void deploy(
        @PathParam(VirtualDatacenterResource.VIRTUAL_DATACENTER) final Integer vdcId,
        @PathParam(VirtualApplianceResource.VIRTUAL_APPLIANCE) final Integer vappId)
    {
        service.startVirtualAppliance(vdcId, vappId);
    }

    @PUT
    @Path(VIRTUAL_APPLIANCE_ACTION_POWERON)
    public List<VirtualMachineChangeStateResultDto> powerOnVirtualApp(
        @PathParam(VirtualDatacenterResource.VIRTUAL_DATACENTER) final Integer vdcId,
        @PathParam(VirtualApplianceResource.VIRTUAL_APPLIANCE) final Integer vappId,
        @Context final IRESTBuilder restBuilder) throws Exception
    {
        VirtualAppliance vapp = service.getVirtualAppliance(vdcId, vappId);
        userService.checkCurrentEnterpriseForPostMethods(vapp.getEnterprise());
        return service.changeVirtualAppMachinesState(vdcId, vappId, State.RUNNING);

    }

    @PUT
    @Path(VIRTUAL_APPLIANCE_ACTION_POWEROFF)
    public List<VirtualMachineChangeStateResultDto> powerOffVirtualApp(
        @PathParam(VirtualDatacenterResource.VIRTUAL_DATACENTER) final Integer vdcId,
        @PathParam(VirtualApplianceResource.VIRTUAL_APPLIANCE) final Integer vappId,
        @Context final IRESTBuilder restBuilder) throws Exception
    {
        VirtualAppliance vapp = service.getVirtualAppliance(vdcId, vappId);
        userService.checkCurrentEnterpriseForPostMethods(vapp.getEnterprise());
        return service.changeVirtualAppMachinesState(vdcId, vappId, State.POWERED_OFF);

    }

    @PUT
    @Path(VIRTUAL_APPLIANCE_ACTION_PAUSE)
    public List<VirtualMachineChangeStateResultDto> pauseVirtualApp(
        @PathParam(VirtualDatacenterResource.VIRTUAL_DATACENTER) final Integer vdcId,
        @PathParam(VirtualApplianceResource.VIRTUAL_APPLIANCE) final Integer vappId,
        @Context final IRESTBuilder restBuilder) throws Exception
    {
        VirtualAppliance vapp = service.getVirtualAppliance(vdcId, vappId);
        userService.checkCurrentEnterpriseForPostMethods(vapp.getEnterprise());
        return service.changeVirtualAppMachinesState(vdcId, vappId, State.PAUSED);

    }

    @PUT
    @Path(VIRTUAL_APPLIANCE_ACTION_RESUME)
    public List<VirtualMachineChangeStateResultDto> resumeVirtualApp(
        @PathParam(VirtualDatacenterResource.VIRTUAL_DATACENTER) final Integer vdcId,
        @PathParam(VirtualApplianceResource.VIRTUAL_APPLIANCE) final Integer vappId,
        @Context final IRESTBuilder restBuilder) throws Exception
    {
        VirtualAppliance vapp = service.getVirtualAppliance(vdcId, vappId);
        userService.checkCurrentEnterpriseForPostMethods(vapp.getEnterprise());
        return service.changeVirtualAppMachinesState(vdcId, vappId, State.REBOOTED);

    }
}
