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

import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.Context;

import org.apache.wink.common.annotations.Parent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import com.abiquo.api.resources.AbstractResource;
import com.abiquo.api.services.IpAddressService;
import com.abiquo.api.services.UserService;
import com.abiquo.api.services.VirtualMachineAllocatorService;
import com.abiquo.api.services.cloud.VirtualMachineService;
import com.abiquo.api.transformer.ModelTransformer;
import com.abiquo.api.util.IRESTBuilder;
import com.abiquo.server.core.cloud.State;
import com.abiquo.server.core.cloud.VirtualApplianceDto;
import com.abiquo.server.core.cloud.VirtualMachine;
import com.abiquo.server.core.cloud.VirtualMachineDto;
import com.abiquo.server.core.infrastructure.network.IpPoolManagement;
import com.abiquo.server.core.infrastructure.network.IpsPoolManagementDto;

@Parent(VirtualMachinesResource.class)
@Controller
@Path(VirtualMachineResource.VIRTUAL_MACHINE_PARAM)
public class VirtualMachineResource extends AbstractResource
{

    public static final String VIRTUAL_MACHINE = "virtualmachine";

    public static final String VIRTUAL_MACHINE_PARAM = "{" + VIRTUAL_MACHINE + "}";

    public static final String VIRTUAL_MACHINE_ACTION_GET_IPS = "/action/ips";

    public static final String VIRTUAL_MACHINE_ACTION_POWER_ON = "/action/poweron";

    public static final String VIRTUAL_MACHINE_ACTION_POWER_OFF = "/action/poweroff";

    public static final String VIRTUAL_MACHINE_ACTION_RESUME = "/action/resume";

    public static final String VIRTUAL_MACHINE_ACTION_PAUSE = "/action/pause";

    @Autowired
    VirtualMachineService vmService;

    @Autowired
    VirtualMachineAllocatorService service;

    @Autowired
    UserService userService;

    @Autowired
    IpAddressService ipService;

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
    public VirtualMachineDto getVirtualMachine(
        @PathParam(VirtualDatacenterResource.VIRTUAL_DATACENTER) Integer vdcId,
        @PathParam(VirtualApplianceResource.VIRTUAL_APPLIANCE) Integer vappId,
        @PathParam(VirtualMachineResource.VIRTUAL_MACHINE) Integer vmId,
        @Context IRESTBuilder restBuilder) throws Exception
    {
        VirtualMachine vm = vmService.getVirtualMachine(vdcId, vappId, vmId);

        return VirtualMachinesResource.createCloudTransferObject(vm, vdcId, vappId, restBuilder);
    }

    @GET
    @Path(VirtualMachineResource.VIRTUAL_MACHINE_ACTION_GET_IPS)
    public IpsPoolManagementDto getIPsByVirtualMachine(
        @PathParam(VirtualDatacenterResource.VIRTUAL_DATACENTER) Integer vdcId,
        @PathParam(VirtualApplianceResource.VIRTUAL_APPLIANCE) Integer vappId,
        @PathParam(VirtualMachineResource.VIRTUAL_MACHINE) Integer vmId,
        @Context IRESTBuilder restBuilder) throws Exception
    {
        VirtualMachine vm = vmService.getVirtualMachine(vdcId, vappId, vmId);

        List<IpPoolManagement> all = ipService.getListIpPoolManagementByMachine(vm);
        IpsPoolManagementDto ips = new IpsPoolManagementDto();
        for (IpPoolManagement ip : all)
        {
            ips.add(IpAddressesResource.createTransferObject(ip, restBuilder));
        }

        return ips;
    }

    @PUT
    public VirtualMachineDto updateVirtualMachine(
        @PathParam(VirtualDatacenterResource.VIRTUAL_DATACENTER) Integer vdcId,
        @PathParam(VirtualApplianceResource.VIRTUAL_APPLIANCE) Integer vappId,
        @PathParam(VirtualMachineResource.VIRTUAL_MACHINE) Integer vmId, VirtualMachineDto dto,
        @Context IRESTBuilder restBuilder) throws Exception
    {
        VirtualMachine vm = vmService.updateVirtualMachine(vdcId, vappId, vmId, dto);

        return VirtualMachinesResource.createCloudTransferObject(vm, vdcId, vappId, restBuilder);
    }

    @PUT
    @Path("action/allocate")
    public synchronized VirtualMachineDto allocate(
        @PathParam(VirtualApplianceResource.VIRTUAL_APPLIANCE) Integer virtualApplianceId,
        @PathParam(VirtualMachineResource.VIRTUAL_MACHINE) Integer virtualMachineId,
        String forceEnterpriseLimitsStr, @Context IRESTBuilder restBuilder) throws Exception
    {

        Boolean forceEnterpriseLimits = Boolean.parseBoolean(forceEnterpriseLimitsStr);
        // get user form the authentication layer
        // User user = userService.getCurrentUser();

        VirtualMachine vmachine =
            service.allocateVirtualMachine(virtualMachineId, virtualApplianceId,
                forceEnterpriseLimits);

        service.updateVirtualMachineUse(virtualApplianceId, vmachine);

        return ModelTransformer.transportFromPersistence(VirtualMachineDto.class, vmachine);
    }

    // TODO forceEnterpriseLimits = true

    @PUT
    @Path("action/checkedit")
    public synchronized void checkEditAllocate(
        @PathParam(VirtualApplianceResource.VIRTUAL_APPLIANCE) Integer virtualApplianceId,
        @PathParam(VirtualMachineResource.VIRTUAL_MACHINE) Integer virtualMachineId,
        VirtualMachineDto vmachine, @Context IRESTBuilder restBuilder) throws Exception
    {
        // Boolean forceEnterpriseLimits = Boolean.parseBoolean(forceEnterpriseLimitsStr);
        // get user form the authentication layer
        // User user = userService.getCurrentUser();

        service.checkAllocate(virtualApplianceId, virtualMachineId, vmachine, true);
    }

    @DELETE
    @Path("action/deallocate")
    public synchronized void deallocate(
        @PathParam(VirtualApplianceResource.VIRTUAL_APPLIANCE) Integer virtualApplianceId,
        @PathParam(VirtualMachineResource.VIRTUAL_MACHINE) Integer virtualMachineId,
        @Context IRESTBuilder restBuilder) throws Exception
    {

        service.deallocateVirtualMachine(virtualMachineId);
    }

    /**
     * Power on the VirtualMachine
     * 
     * @param vdcId VirtualDatacenter id
     * @param vappId VirtualAppliance id
     * @param vmId VirtualMachine id
     * @param restBuilder injected restbuilder context parameter
     * @throws Exception
     */
    @POST
    @Path("action/poweron")
    public void powerOnVirtualMachine(
        @PathParam(VirtualDatacenterResource.VIRTUAL_DATACENTER) Integer vdcId,
        @PathParam(VirtualApplianceResource.VIRTUAL_APPLIANCE) Integer vappId,
        @PathParam(VirtualMachineResource.VIRTUAL_MACHINE) Integer vmId,
        @Context IRESTBuilder restBuilder) throws Exception
    {
        VirtualMachine vm = vmService.getVirtualMachine(vdcId, vappId, vmId);
        if (!vmService.sameState(vm, State.RUNNING))
        {
            vmService.validMachineStateChange(vm.getState(), State.RUNNING);

            vmService.blockVirtualMachine(vm);

            vmService.changeVirtualMachineState(vmId, vappId, vdcId, State.RUNNING);
        }
    }

    /**
     * Power off the virtual machine
     * 
     * @param vdcId VirtualDatacenter id
     * @param vappId VirtualAppliance id
     * @param vmId VirtualMachine id
     * @param restBuilder injected restbuilder context parameter
     * @throws Exception
     */
    @POST
    @Path("action/poweroff")
    public void powerOffVirtualMachine(
        @PathParam(VirtualDatacenterResource.VIRTUAL_DATACENTER) Integer vdcId,
        @PathParam(VirtualApplianceResource.VIRTUAL_APPLIANCE) Integer vappId,
        @PathParam(VirtualMachineResource.VIRTUAL_MACHINE) Integer vmId,
        @Context IRESTBuilder restBuilder) throws Exception
    {
        VirtualMachine vm = vmService.getVirtualMachine(vdcId, vappId, vmId);
        if (!vmService.sameState(vm, State.POWERED_OFF))
        {
            vmService.validMachineStateChange(vm.getState(), State.POWERED_OFF);
           
            vmService.blockVirtualMachine(vm);
           
            vmService.changeVirtualMachineState(vmId, vappId, vdcId, State.POWERED_OFF);
        }
    }

    /**
     * Resume the Virtual Machine
     * 
     * @param vdcId VirtualDatacenter id
     * @param vappId VirtualAppliance id
     * @param vmId VirtualMachine id
     * @param restBuilder injected restbuilder context parameter
     * @throws Exception
     */
    @POST
    @Path("action/resume")
    public void resumeVirtualMachine(
        @PathParam(VirtualDatacenterResource.VIRTUAL_DATACENTER) Integer vdcId,
        @PathParam(VirtualApplianceResource.VIRTUAL_APPLIANCE) Integer vappId,
        @PathParam(VirtualMachineResource.VIRTUAL_MACHINE) Integer vmId,
        @Context IRESTBuilder restBuilder) throws Exception
    {
        VirtualMachine vm = vmService.getVirtualMachine(vdcId, vappId, vmId);
        if (!vmService.sameState(vm, State.REBOOTED))
        {
            vmService.validMachineStateChange(vm.getState(), State.REBOOTED);
            
            vmService.blockVirtualMachine(vm);

            vmService.changeVirtualMachineState(vmId, vappId, vdcId, State.REBOOTED);
        }
    }

    /**
     * Pause the VirtualMachine
     * 
     * @param vdcId VirtualDatacenter id
     * @param vappId VirtualAppliance id
     * @param vmId VirtualMachine id
     * @param restBuilder injected restbuilder context parameter
     * @throws Exception
     */
    @POST
    @Path("action/pause")
    public void pauseVirtualMachine(
        @PathParam(VirtualDatacenterResource.VIRTUAL_DATACENTER) Integer vdcId,
        @PathParam(VirtualApplianceResource.VIRTUAL_APPLIANCE) Integer vappId,
        @PathParam(VirtualMachineResource.VIRTUAL_MACHINE) Integer vmId,
        @Context IRESTBuilder restBuilder) throws Exception
    {
        VirtualMachine vm = vmService.getVirtualMachine(vdcId, vappId, vmId);
        if (!vmService.sameState(vm, State.PAUSED))
        {
            vmService.validMachineStateChange(vm.getState(), State.PAUSED);
            
            vmService.blockVirtualMachine(vm);
            
            vmService.changeVirtualMachineState(vmId, vappId, vdcId, State.PAUSED);
        }
    }
}
