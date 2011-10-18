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

import com.abiquo.api.exceptions.APIError;
import com.abiquo.api.exceptions.BadRequestException;
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
import com.abiquo.server.core.cloud.VirtualMachineDeployDto;
import com.abiquo.server.core.cloud.VirtualMachineDto;
import com.abiquo.server.core.cloud.VirtualMachineStateDto;
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

    public static final String VIRTUAL_MACHINE_ACTION_DEPLOY = "/action/deploy";

    public static final String VIRTUAL_MACHINE_ACTION_UNDEPLOY = "/action/undeploy";

    public static final String VIRTUAL_MACHINE_ACTION_RESUME = "/action/resume";

    public static final String VIRTUAL_MACHINE_ACTION_PAUSE = "/action/pause";

    public static final String VIRTUAL_MACHINE_STATE = "/state";

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
        @PathParam(VirtualDatacenterResource.VIRTUAL_DATACENTER) final Integer vdcId,
        @PathParam(VirtualApplianceResource.VIRTUAL_APPLIANCE) final Integer vappId,
        @PathParam(VirtualMachineResource.VIRTUAL_MACHINE) final Integer vmId,
        @Context final IRESTBuilder restBuilder) throws Exception
    {
        VirtualMachine vm = vmService.getVirtualMachine(vdcId, vappId, vmId);

        return VirtualMachinesResource.createCloudTransferObject(vm, vdcId, vappId, restBuilder);
    }

    @GET
    @Path(VirtualMachineResource.VIRTUAL_MACHINE_ACTION_GET_IPS)
    public IpsPoolManagementDto getIPsByVirtualMachine(
        @PathParam(VirtualDatacenterResource.VIRTUAL_DATACENTER) final Integer vdcId,
        @PathParam(VirtualApplianceResource.VIRTUAL_APPLIANCE) final Integer vappId,
        @PathParam(VirtualMachineResource.VIRTUAL_MACHINE) final Integer vmId,
        @Context final IRESTBuilder restBuilder) throws Exception
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
        @PathParam(VirtualDatacenterResource.VIRTUAL_DATACENTER) final Integer vdcId,
        @PathParam(VirtualApplianceResource.VIRTUAL_APPLIANCE) final Integer vappId,
        @PathParam(VirtualMachineResource.VIRTUAL_MACHINE) final Integer vmId,
        final VirtualMachineDto dto, @Context final IRESTBuilder restBuilder) throws Exception
    {
        VirtualMachine vm = vmService.updateVirtualMachine(vdcId, vappId, vmId, dto);

        return VirtualMachinesResource.createCloudTransferObject(vm, vdcId, vappId, restBuilder);
    }

    @PUT
    @Path("action/allocate")
    public synchronized VirtualMachineDto allocate(
        @PathParam(VirtualApplianceResource.VIRTUAL_APPLIANCE) final Integer virtualApplianceId,
        @PathParam(VirtualMachineResource.VIRTUAL_MACHINE) final Integer virtualMachineId,
        final String forceEnterpriseLimitsStr, @Context final IRESTBuilder restBuilder)
        throws Exception
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
        @PathParam(VirtualApplianceResource.VIRTUAL_APPLIANCE) final Integer virtualApplianceId,
        @PathParam(VirtualMachineResource.VIRTUAL_MACHINE) final Integer virtualMachineId,
        final VirtualMachineDto vmachine, @Context final IRESTBuilder restBuilder) throws Exception
    {
        // Boolean forceEnterpriseLimits = Boolean.parseBoolean(forceEnterpriseLimitsStr);
        // get user form the authentication layer
        // User user = userService.getCurrentUser();

        service.checkAllocate(virtualApplianceId, virtualMachineId, vmachine, true);
    }

    @DELETE
    @Path("action/deallocate")
    public synchronized void deallocate(
        @PathParam(VirtualApplianceResource.VIRTUAL_APPLIANCE) final Integer virtualApplianceId,
        @PathParam(VirtualMachineResource.VIRTUAL_MACHINE) final Integer virtualMachineId,
        @Context final IRESTBuilder restBuilder) throws Exception
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
     * @deprecated use
     *             {@link #powerStateVirtualMachine(Integer, Integer, Integer, VirtualMachineStateDto, IRESTBuilder)}
     *             instead
     */
    @POST
    @Deprecated
    @Path("action/poweron")
    public void powerOnVirtualMachine(
        @PathParam(VirtualDatacenterResource.VIRTUAL_DATACENTER) final Integer vdcId,
        @PathParam(VirtualApplianceResource.VIRTUAL_APPLIANCE) final Integer vappId,
        @PathParam(VirtualMachineResource.VIRTUAL_MACHINE) final Integer vmId,
        @Context final IRESTBuilder restBuilder) throws Exception
    {
        VirtualMachine vm = vmService.getVirtualMachine(vdcId, vappId, vmId);
        userService.checkCurrentEnterpriseForPostMethods(vm.getEnterprise());
        if (!vmService.sameState(vm, State.ON))
        {

            vmService.changeVirtualMachineState(vmId, vappId, vdcId, State.ON);

        }
    }

    /**
     * Power off the VirtualMachine
     * 
     * @param vdcId VirtualDatacenter id
     * @param vappId VirtualAppliance id
     * @param vmId VirtualMachine id
     * @param restBuilder injected restbuilder context parameter
     * @throws Exception
     * @deprecated use
     *             {@link #powerStateVirtualMachine(Integer, Integer, Integer, VirtualMachineStateDto, IRESTBuilder)}
     *             instead
     */
    @POST
    @Deprecated
    @Path("action/poweroff")
    public void powerOffVirtualMachine(
        @PathParam(VirtualDatacenterResource.VIRTUAL_DATACENTER) final Integer vdcId,
        @PathParam(VirtualApplianceResource.VIRTUAL_APPLIANCE) final Integer vappId,
        @PathParam(VirtualMachineResource.VIRTUAL_MACHINE) final Integer vmId,
        @Context final IRESTBuilder restBuilder) throws Exception
    {
        VirtualMachine vm = vmService.getVirtualMachine(vdcId, vappId, vmId);
        userService.checkCurrentEnterpriseForPostMethods(vm.getEnterprise());
        if (!vmService.sameState(vm, State.OFF))
        {

            vmService.changeVirtualMachineState(vmId, vappId, vdcId, State.OFF);

        }
    }

    /**
     * Change the {@link State} the virtual machine
     * 
     * @param vdcId VirtualDatacenter id
     * @param vappId VirtualAppliance id
     * @param vmId VirtualMachine id
     * @param state allowed
     *            <ul>
     *            <li><b>OFF</b></li>
     *            <li><b>ON</b></li>
     *            <li><b>PAUSED</b></li>
     *            </ul>
     * @param restBuilder injected restbuilder context parameter
     * @throws Exception
     */
    @PUT
    @Path("state")
    public void powerStateVirtualMachine(
        @PathParam(VirtualDatacenterResource.VIRTUAL_DATACENTER) final Integer vdcId,
        @PathParam(VirtualApplianceResource.VIRTUAL_APPLIANCE) final Integer vappId,
        @PathParam(VirtualMachineResource.VIRTUAL_MACHINE) final Integer vmId,
        final VirtualMachineStateDto state, @Context final IRESTBuilder restBuilder)
        throws Exception
    {
        State newState = validateState(state);
        vmService.applyVirtualMachineState(vmId, vappId, vdcId, newState);
    }

    /**
     * Retrieve the {@link State} the virtual machine
     * 
     * @param vdcId VirtualDatacenter id
     * @param vappId VirtualAppliance id
     * @param vmId VirtualMachine id
     * @param restBuilder injected restbuilder context parameter
     * @return state
     * @throws Exception
     */
    @GET
    @Path("state")
    public VirtualMachineStateDto stateVirtualMachine(
        @PathParam(VirtualDatacenterResource.VIRTUAL_DATACENTER) final Integer vdcId,
        @PathParam(VirtualApplianceResource.VIRTUAL_APPLIANCE) final Integer vappId,
        @PathParam(VirtualMachineResource.VIRTUAL_MACHINE) final Integer vmId,
        @Context final IRESTBuilder restBuilder) throws Exception
    {
        VirtualMachine vm = vmService.getVirtualMachine(vdcId, vappId, vmId);

        VirtualMachineStateDto stateDto =
            virtualMachineStateToDto(vdcId, vappId, vmId, restBuilder, vm);
        return stateDto;
    }

    private VirtualMachineStateDto virtualMachineStateToDto(final Integer vdcId,
        final Integer vappId, final Integer vmId, final IRESTBuilder restBuilder,
        final VirtualMachine vm)
    {
        VirtualMachineStateDto stateDto = new VirtualMachineStateDto();
        stateDto.setPower(vm.getState().name());
        stateDto.addLinks(restBuilder.buildVirtualMachineStateLinks(vappId, vdcId, vmId));
        return stateDto;
    }

    /**
     * Validate that the state is allowed. <br>
     * 
     * @param state<ul>
     *            <li><b>OFF</b></li>
     *            <li><b>ON</b></li>
     *            <li><b>PAUSED</b></li>
     *            </ul>
     * @return State
     */
    private State validateState(final VirtualMachineStateDto state)
    {
        if (!State.ON.name().equals(state.getPower()) && !State.OFF.name().equals(state.getPower())
            && !State.PAUSED.name().equals(state.getPower()))
        {
            throw new BadRequestException(APIError.VIRTUAL_MACHINE_EDIT_STATE);
        }
        return State.valueOf(state.getPower());
    }

    /**
     * Resume the Virtual Machine
     * 
     * @param vdcId VirtualDatacenter id
     * @param vappId VirtualAppliance id
     * @param vmId VirtualMachine id
     * @param restBuilder injected restbuilder context parameter
     * @throws Exception
     * @deprecated use
     *             {@link #powerStateVirtualMachine(Integer, Integer, Integer, VirtualMachineStateDto, IRESTBuilder)}
     *             instead
     */
    @POST
    @Deprecated
    @Path("action/resume")
    public void resumeVirtualMachine(
        @PathParam(VirtualDatacenterResource.VIRTUAL_DATACENTER) final Integer vdcId,
        @PathParam(VirtualApplianceResource.VIRTUAL_APPLIANCE) final Integer vappId,
        @PathParam(VirtualMachineResource.VIRTUAL_MACHINE) final Integer vmId,
        @Context final IRESTBuilder restBuilder) throws Exception
    {
        VirtualMachine vm = vmService.getVirtualMachine(vdcId, vappId, vmId);
        userService.checkCurrentEnterpriseForPostMethods(vm.getEnterprise());

        if (!vmService.sameState(vm, State.PAUSED))
        {
            vmService.changeVirtualMachineState(vmId, vappId, vdcId, State.PAUSED);
        }
    }

    /**
     * Pause the VirtualMachine
     * 
     * @param vdcId VirtualDatacenter id
     * @param vappId VirtualAppliance id
     * @param vmId VirtualMachine id
     * @param restBuilder injected restbuilder context parameter
     * @throws Exception * @deprecated use
     *             {@link #powerStateVirtualMachine(Integer, Integer, Integer, VirtualMachineStateDto, IRESTBuilder)}
     *             instead
     */
    @Deprecated
    @POST
    @Path("action/pause")
    public void pauseVirtualMachine(
        @PathParam(VirtualDatacenterResource.VIRTUAL_DATACENTER) final Integer vdcId,
        @PathParam(VirtualApplianceResource.VIRTUAL_APPLIANCE) final Integer vappId,
        @PathParam(VirtualMachineResource.VIRTUAL_MACHINE) final Integer vmId,
        @Context final IRESTBuilder restBuilder) throws Exception
    {
        VirtualMachine vm = vmService.getVirtualMachine(vdcId, vappId, vmId);
        userService.checkCurrentEnterpriseForPostMethods(vm.getEnterprise());

        if (!vmService.sameState(vm, State.PAUSED))
        {
            vmService.changeVirtualMachineState(vmId, vappId, vdcId, State.PAUSED);
        }
    }

    /**
     * Deletes the virtual machine.<br>
     * A {@link VirtualMachine} can only be deleted if is in one allowed are NOT_ALLOCATED and
     * UNKNOWN. allowed
     * <ul>
     * <li><b>NOT_ALLOCATED</b></li>
     * <li><b>UNKNOWN</b></li>
     * </ul>
     * 
     * @param vdcId VirtualDatacenter id
     * @param vappId VirtualAppliance id
     * @param vmId VirtualMachine id
     * @param restBuilder injected restbuilder context parameter
     * @throws Exception
     */
    @DELETE
    public void deleteVirtualMachine(
        @PathParam(VirtualDatacenterResource.VIRTUAL_DATACENTER) final Integer vdcId,
        @PathParam(VirtualApplianceResource.VIRTUAL_APPLIANCE) final Integer vappId,
        @PathParam(VirtualMachineResource.VIRTUAL_MACHINE) final Integer vmId,
        @Context final IRESTBuilder restBuilder) throws Exception
    {
        vmService.deleteVirtualMachine(vmId, vappId, vdcId);
    }

    /**
     * Deploys a {@link VirtualMachine}. This involves some steps. <br>
     * <ul>
     * <li>Select a machine to allocate the virtual machine</li>
     * <li>Check limits</li>
     * <li>Check resources</li>
     * <li>Check remote services</li>
     * <li>In premium call initiator</li>
     * <li>Subscribe to VSM</li>
     * <li>Build the Task DTO</li>
     * <li>Enqueue in tarantino</li>
     * <li>Register in redis</li>
     * <li>Add Task DTO to rabbitmq</li>
     * <li>Enable the resource <code>Progress<code></li>
     * </ul>
     * 
     * @param vdcId VirtualDatacenter id
     * @param vappId VirtualAppliance id
     * @param vmId VirtualMachine id
     * @param restBuilder injected restbuilder context parameter
     * @param forceSoftLimits dto of options
     * @throws Exception
     */
    @POST
    @Path("action/deploy")
    public void deployVirtualMachine(
        @PathParam(VirtualDatacenterResource.VIRTUAL_DATACENTER) final Integer vdcId,
        @PathParam(VirtualApplianceResource.VIRTUAL_APPLIANCE) final Integer vappId,
        @PathParam(VirtualMachineResource.VIRTUAL_MACHINE) final Integer vmId,
        final VirtualMachineDeployDto forceSoftLimits, @Context final IRESTBuilder restBuilder)
        throws Exception
    {
        vmService.deployVirtualMachine(vmId, vappId, vdcId,
            forceSoftLimits.isForeceEnterpriseSoftLimits());
    }

    /**
     * Deploys a {@link VirtualMachine}. This involves some steps. <br>
     * <ul>
     * <li>Select a machine to allocate the virtual machine</li>
     * <li>Check limits</li>
     * <li>Check resources</li>
     * <li>Check remote services</li>
     * <li>In premium call initiator</li>
     * <li>Subscribe to VSM</li>
     * <li>Build the Task DTO</li>
     * <li>Enqueue in tarantino</li>
     * <li>Register in redis</li>
     * <li>Add Task DTO to rabbitmq</li>
     * <li>Enable the resource <code>Progress<code></li>
     * </ul>
     * 
     * @param vdcId VirtualDatacenter id
     * @param vappId VirtualAppliance id
     * @param vmId VirtualMachine id
     * @param restBuilder injected restbuilder context parameter
     * @throws Exception
     */
    @POST
    @Path("action/deploy")
    public void deployVirtualMachine(
        @PathParam(VirtualDatacenterResource.VIRTUAL_DATACENTER) final Integer vdcId,
        @PathParam(VirtualApplianceResource.VIRTUAL_APPLIANCE) final Integer vappId,
        @PathParam(VirtualMachineResource.VIRTUAL_MACHINE) final Integer vmId,
        @Context final IRESTBuilder restBuilder) throws Exception
    {
        vmService.deployVirtualMachine(vmId, vappId, vdcId, false);
    }

    /**
     * Undeploys a {@link VirtualMachine}. This involves some steps. <br>
     * <ul>
     * <li>Deallocate the virtual machine</li>
     * <li>Delete the relations to the {@link Hypervisor}, {@link Datastore}</li>
     * <li>Set to NOT_DEPLOYED</li>
     * <li>Unsuscribe to VSM</li>
     * <li>Enqueue in tarantino</li>
     * <li>Register in redis</li>
     * <li>Add Task DTO to rabbitmq</li>
     * <li>Enable the resource <code>Progress<code></li>
     * </ul>
     * 
     * @param vdcId VirtualDatacenter id
     * @param vappId VirtualAppliance id
     * @param vmId VirtualMachine id
     * @param restBuilder injected restbuilder context parameter
     * @throws Exception
     */
    @POST
    @Path("action/undeploy")
    public void undeployVirtualMachine(
        @PathParam(VirtualDatacenterResource.VIRTUAL_DATACENTER) final Integer vdcId,
        @PathParam(VirtualApplianceResource.VIRTUAL_APPLIANCE) final Integer vappId,
        @PathParam(VirtualMachineResource.VIRTUAL_MACHINE) final Integer vmId,
        @Context final IRESTBuilder restBuilder) throws Exception
    {
        vmService.undeployVirtualMachine(vmId, vappId, vdcId, false);
    }
}
