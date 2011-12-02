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
import javax.validation.constraints.NotNull;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.UriInfo;

import org.apache.wink.common.annotations.Parent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import com.abiquo.aimstub.Datastore;
import com.abiquo.api.exceptions.APIError;
import com.abiquo.api.exceptions.BadRequestException;
import com.abiquo.api.exceptions.InternalServerErrorException;
import com.abiquo.api.resources.TaskResourceUtils;
import com.abiquo.api.services.TaskService;
import com.abiquo.api.services.VirtualMachineAllocatorService;
import com.abiquo.api.services.cloud.VirtualMachineService;
import com.abiquo.api.util.IRESTBuilder;
import com.abiquo.model.transport.AcceptedRequestDto;
import com.abiquo.server.core.appslibrary.VirtualImage;
import com.abiquo.server.core.cloud.Hypervisor;
import com.abiquo.server.core.cloud.NodeVirtualImage;
import com.abiquo.server.core.cloud.VirtualApplianceDto;
import com.abiquo.server.core.cloud.VirtualMachine;
import com.abiquo.server.core.cloud.VirtualMachineDeployDto;
import com.abiquo.server.core.cloud.VirtualMachineDto;
import com.abiquo.server.core.cloud.VirtualMachineState;
import com.abiquo.server.core.cloud.VirtualMachineStateDto;
import com.abiquo.server.core.cloud.VirtualMachineWithNodeDto;
import com.abiquo.server.core.enterprise.Enterprise;
import com.abiquo.server.core.enterprise.User;
import com.abiquo.server.core.infrastructure.Machine;
import com.abiquo.server.core.infrastructure.Rack;
import com.abiquo.server.core.task.Task;
import com.abiquo.server.core.task.TaskDto;
import com.abiquo.server.core.task.TasksDto;
import com.abiquo.server.core.task.enums.TaskOwnerType;

@Parent(VirtualMachinesResource.class)
@Controller
@Path(VirtualMachineResource.VIRTUAL_MACHINE_PARAM)
public class VirtualMachineResource
{
    public static final String VIRTUAL_MACHINE = "virtualmachine";

    public static final String VIRTUAL_MACHINE_PARAM = "{" + VIRTUAL_MACHINE + "}";

    public static final String VIRTUAL_MACHINE_ACTION_POWER_ON = "/action/poweron";

    public static final String VIRTUAL_MACHINE_ACTION_POWER_OFF = "/action/poweroff";

    public static final String VIRTUAL_MACHINE_ACTION_DEPLOY = "/action/deploy";

    public static final String VIRTUAL_MACHINE_ACTION_UNDEPLOY = "/action/undeploy";

    public static final String VIRTUAL_MACHINE_ACTION_RESUME = "/action/resume";

    public static final String VIRTUAL_MACHINE_ACTION_PAUSE = "/action/pause";

    public static final String VIRTUAL_MACHINE_STATE = "/state";

    public static final String VIRTUAL_MACHINE_ACTION_DEPLOY_REL = "deploy";

    public static final String VIRTUAL_MACHINE_ACTION_UNDEPLOY_REL = "undeploy";

    public static final String VIRTUAL_MACHINE_STATE_REL = "state";

    // Chef constants to help link builders. Method implementation are premium.
    public static final String VIRTUAL_MACHINE_RUNLIST_PATH = "/config/runlist";

    public static final String VIRTUAL_MACHINE_BOOTSTRAP_PATH = "/config/bootstrap";

    public static final String VM_NODE_MEDIA_TYPE = "application/vnd.vm-node+xml";

    @Autowired
    private VirtualMachineService vmService;

    @Autowired
    private VirtualMachineAllocatorService service;

    @Autowired
    private TaskService taskService;

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
    @Produces(MediaType.APPLICATION_XML)
    public VirtualMachineDto getVirtualMachine(
        @PathParam(VirtualDatacenterResource.VIRTUAL_DATACENTER) @NotNull @Min(1) final Integer vdcId,
        @PathParam(VirtualApplianceResource.VIRTUAL_APPLIANCE) @NotNull @Min(1) final Integer vappId,
        @PathParam(VirtualMachineResource.VIRTUAL_MACHINE) @NotNull @Min(1) final Integer vmId,
        @Context final IRESTBuilder restBuilder) throws Exception
    {
        VirtualMachine vm = vmService.getVirtualMachine(vdcId, vappId, vmId);

        return createTransferObject(vm, vdcId, vappId, restBuilder);
    }

    /***
     * @param vdcId VirtualDatacenter id
     * @param vappId VirtualAppliance id
     * @param vmId VirtualMachine id
     * @param restBuilder injected restbuilder context parameter
     * @return a link where you can keep track of the progress and the virtual machine.
     * @throws Exception AcceptedRequestDto
     */
    @PUT
    public AcceptedRequestDto updateVirtualMachine(
        @PathParam(VirtualDatacenterResource.VIRTUAL_DATACENTER) @NotNull @Min(1) final Integer vdcId,
        @PathParam(VirtualApplianceResource.VIRTUAL_APPLIANCE) @NotNull @Min(1) final Integer vappId,
        @PathParam(VirtualMachineResource.VIRTUAL_MACHINE) @NotNull @Min(1) final Integer vmId,
        final VirtualMachineDto dto, @Context final IRESTBuilder restBuilder) throws Exception
    {
        String link = vmService.reconfigureVirtualMachine(vdcId, vappId, vmId, dto);
        AcceptedRequestDto request = new AcceptedRequestDto();
        request.setStatusUrlLink(link);
        request.setEntity(dto);

        return request;
    }

    /**
     * Change the {@link VirtualMachineState} the virtual machine
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
     * @param restBuilder injected restbuilder context parameter * @return a link where you can keep
     *            track of the progress and a message.
     * @throws Exception
     */
    @PUT
    @Path(VIRTUAL_MACHINE_STATE)
    public AcceptedRequestDto powerStateVirtualMachine(
        @PathParam(VirtualDatacenterResource.VIRTUAL_DATACENTER) final Integer vdcId,
        @PathParam(VirtualApplianceResource.VIRTUAL_APPLIANCE) final Integer vappId,
        @PathParam(VirtualMachineResource.VIRTUAL_MACHINE) final Integer vmId,
        final VirtualMachineStateDto state, @Context final IRESTBuilder restBuilder)
        throws Exception
    {
        VirtualMachineState newState = validateState(state);
        String link = vmService.applyVirtualMachineState(vmId, vappId, vdcId, newState);

        // If the link is null no Task was performed
        if (link == null)
        {
            throw new InternalServerErrorException(APIError.STATUS_INTERNAL_SERVER_ERROR);
        }
        AcceptedRequestDto request = new AcceptedRequestDto();
        request.setStatusUrlLink(link);
        request.setEntity(null);
        return request;
    }

    /**
     * Retrieve the {@link VirtualMachineState} the virtual machine
     * 
     * @param vdcId VirtualDatacenter id
     * @param vappId VirtualAppliance id
     * @param vmId VirtualMachine id
     * @param restBuilder injected restbuilder context parameter
     * @return state
     * @throws Exception
     */
    @GET
    @Path(VIRTUAL_MACHINE_STATE)
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
    private VirtualMachineState validateState(final VirtualMachineStateDto state)
    {
        if (!VirtualMachineState.ON.name().equals(state.getPower())
            && !VirtualMachineState.OFF.name().equals(state.getPower())
            && !VirtualMachineState.PAUSED.name().equals(state.getPower()))
        {
            throw new BadRequestException(APIError.VIRTUAL_MACHINE_EDIT_STATE);
        }
        return VirtualMachineState.valueOf(state.getPower());
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
     * @param forceSoftLimits dto of options * @return a link where you can keep track of the
     *            progress and a message.
     * @throws Exception
     */
    @POST
    @Path(VIRTUAL_MACHINE_ACTION_DEPLOY)
    public AcceptedRequestDto<String> deployVirtualMachine(
        @PathParam(VirtualDatacenterResource.VIRTUAL_DATACENTER) final Integer vdcId,
        @PathParam(VirtualApplianceResource.VIRTUAL_APPLIANCE) final Integer vappId,
        @PathParam(VirtualMachineResource.VIRTUAL_MACHINE) final Integer vmId,
        final VirtualMachineDeployDto forceSoftLimits, @Context final IRESTBuilder restBuilder)
        throws Exception
    {
        String link =
            vmService.deployVirtualMachine(vmId, vappId, vdcId,
                forceSoftLimits.isForceEnterpriseSoftLimits());

        // If the link is null no Task was performed
        if (link == null)
        {
            throw new InternalServerErrorException(APIError.STATUS_INTERNAL_SERVER_ERROR);
        }
        AcceptedRequestDto<String> a202 = new AcceptedRequestDto<String>();
        a202.setStatusUrlLink(link);
        a202.setEntity("You can keep track of the progress in the link");

        return a202;
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
     * @return a link where you can keep track of the progress and a message.
     * @throws Exception
     */
    @POST
    @Path(VIRTUAL_MACHINE_ACTION_DEPLOY)
    public AcceptedRequestDto<String> deployVirtualMachine(
        @PathParam(VirtualDatacenterResource.VIRTUAL_DATACENTER) final Integer vdcId,
        @PathParam(VirtualApplianceResource.VIRTUAL_APPLIANCE) final Integer vappId,
        @PathParam(VirtualMachineResource.VIRTUAL_MACHINE) final Integer vmId,
        @Context final IRESTBuilder restBuilder) throws Exception
    {

        String link = vmService.deployVirtualMachine(vmId, vappId, vdcId, false);
        // If the link is null no Task was performed
        if (link == null)
        {
            throw new InternalServerErrorException(APIError.STATUS_INTERNAL_SERVER_ERROR);
        }
        AcceptedRequestDto<String> a202 = new AcceptedRequestDto<String>();
        a202.setStatusUrlLink(link);
        a202.setEntity("");

        return a202;
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
     * @return a link where you can keep track of the progress and a message.
     * @throws Exception
     */
    @POST
    @Path(VIRTUAL_MACHINE_ACTION_UNDEPLOY)
    public AcceptedRequestDto<String> undeployVirtualMachine(
        @PathParam(VirtualDatacenterResource.VIRTUAL_DATACENTER) final Integer vdcId,
        @PathParam(VirtualApplianceResource.VIRTUAL_APPLIANCE) final Integer vappId,
        @PathParam(VirtualMachineResource.VIRTUAL_MACHINE) final Integer vmId,
        @Context final IRESTBuilder restBuilder) throws Exception
    {
        String link = vmService.undeployVirtualMachine(vmId, vappId, vdcId);
        // If the link is null no Task was performed
        if (link == null)
        {
            throw new InternalServerErrorException(APIError.STATUS_INTERNAL_SERVER_ERROR);
        }
        AcceptedRequestDto<String> a202 = new AcceptedRequestDto<String>();
        a202.setStatusUrlLink(link);
        a202.setEntity("");

        return a202;
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
    public static VirtualMachineWithNodeDto createNodeTransferObject(final NodeVirtualImage v,
        final Integer vdcId, final Integer vappId, final IRESTBuilder restBuilder) throws Exception
    {
        VirtualMachineWithNodeDto dto = new VirtualMachineWithNodeDto();
        dto.setUuid(v.getVirtualMachine().getUuid());
        dto.setCpu(v.getVirtualMachine().getCpu());
        dto.setDescription(v.getVirtualMachine().getDescription());
        dto.setHdInBytes(v.getVirtualMachine().getHdInBytes());
        dto.setHighDisponibility(v.getVirtualMachine().getHighDisponibility());
        dto.setId(v.getVirtualMachine().getId());
        // dto.setIdState(v.getidState)
        dto.setIdType(v.getVirtualMachine().getIdType());

        dto.setName(v.getVirtualMachine().getName());
        dto.setPassword(v.getVirtualMachine().getPassword());
        dto.setRam(v.getVirtualMachine().getRam());
        dto.setState(v.getVirtualMachine().getState());
        dto.setVdrpIP(v.getVirtualMachine().getVdrpIP());
        dto.setVdrpPort(v.getVirtualMachine().getVdrpPort());
        dto.setNodeId(v.getId());
        dto.setNodeName(v.getName());
        dto.setX(v.getX());
        dto.setY(v.getY());
        final Hypervisor hypervisor = v.getVirtualMachine().getHypervisor();
        final Machine machine = hypervisor == null ? null : hypervisor.getMachine();
        final Rack rack = machine == null ? null : machine.getRack();

        final Enterprise enterprise =
            v.getVirtualMachine().getEnterprise() == null ? null : v.getVirtualMachine()
                .getEnterprise();
        final User user =
            v.getVirtualMachine().getUser() == null ? null : v.getVirtualMachine().getUser();
        final VirtualImage virtualImage = v.getVirtualImage() == null ? null : v.getVirtualImage();

        dto.addLink(restBuilder.buildVirtualImageLink(virtualImage.getEnterprise().getId(),
            virtualImage.getRepository().getDatacenter().getId(), virtualImage.getId()));

        dto.addLinks(restBuilder.buildVirtualMachineCloudAdminLinks(vdcId, vappId, v
            .getVirtualMachine().getId(), rack == null ? null : rack.getDatacenter().getId(),
            rack == null ? null : rack.getId(), machine == null ? null : machine.getId(),
            enterprise == null ? null : enterprise.getId(), user == null ? null : user.getId(), v
                .getVirtualMachine().isChefEnabled()));

        TaskResourceUtils.addTasksLink(dto, dto.getEditLink());

        return dto;
    }

    /** ########## DEPRECATED ZONE ########## */

    @PUT
    @Path("action/allocate")
    @Deprecated
    public synchronized VirtualMachineDto allocate(
        @PathParam(VirtualDatacenterResource.VIRTUAL_DATACENTER) @NotNull @Min(1) final Integer virtualDatacenterId,
        @PathParam(VirtualApplianceResource.VIRTUAL_APPLIANCE) @NotNull @Min(1) final Integer virtualApplianceId,
        @PathParam(VirtualMachineResource.VIRTUAL_MACHINE) @NotNull @Min(1) final Integer virtualMachineId,
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

        return createTransferObject(vmachine, virtualApplianceId, virtualDatacenterId, restBuilder);
    }

    public static VirtualMachineDto createCloudTransferObject(final VirtualMachine v,
        final Integer vdcId, final Integer vappId, final IRESTBuilder restBuilder) throws Exception
    {
        VirtualMachineDto vmDto = createTransferObject(v, vdcId, vappId, restBuilder);
        return vmDto;
    }

    public static VirtualMachineDto createTransferObject(final VirtualMachine v,
        final IRESTBuilder restBuilder)
    {
        VirtualMachineDto dto = new VirtualMachineDto();

        dto.setCpu(v.getCpu());
        dto.setDescription(v.getDescription());
        dto.setHdInBytes(v.getHdInBytes());
        dto.setHighDisponibility(v.getHighDisponibility());
        dto.setId(v.getId());
        // dto.setIdState(v.getidState)
        dto.setIdType(v.getIdType());

        dto.setName(v.getName());
        dto.setPassword(v.getPassword());
        dto.setRam(v.getRam());
        dto.setState(v.getState());
        dto.setVdrpIP(v.getVdrpIP());
        dto.setVdrpPort(v.getVdrpPort());

        final Hypervisor hypervisor = v.getHypervisor();
        final Machine machine = hypervisor == null ? null : hypervisor.getMachine();
        final Rack rack = machine == null ? null : machine.getRack();

        final Enterprise enterprise = v.getEnterprise() == null ? null : v.getEnterprise();
        final User user = v.getUser() == null ? null : v.getUser();

        dto.addLinks(restBuilder.buildVirtualMachineAdminLinks(rack == null ? null : rack
            .getDatacenter().getId(), rack == null ? null : rack.getId(), machine == null ? null
            : machine.getId(), enterprise == null ? null : enterprise.getId(), user == null ? null
            : user.getId()));

        final VirtualImage vimage = v.getVirtualImage();
        if (vimage != null)
        {
            dto.addLink(restBuilder.buildVirtualImageLink(vimage.getEnterprise().getId(), vimage
                .getRepository().getDatacenter().getId(), vimage.getId()));
        }

        TaskResourceUtils.addTasksLink(dto, dto.getEditLink());

        return dto;
    }

    public static VirtualMachineDto createTransferObject(final VirtualMachine v,
        final Integer vdcId, final Integer vappId, final IRESTBuilder restBuilder)
    {

        VirtualMachineDto dto = new VirtualMachineDto();

        dto.setUuid(v.getUuid());
        dto.setCpu(v.getCpu());
        dto.setDescription(v.getDescription());
        dto.setHdInBytes(v.getHdInBytes());
        dto.setHighDisponibility(v.getHighDisponibility());
        dto.setId(v.getId());
        // dto.setIdState(v.getidState)
        if (v.getIdType() == 0)
        {
            dto.setIdType(com.abiquo.server.core.cloud.VirtualMachine.NOT_MANAGED);
        }
        else
        {
            dto.setIdType(com.abiquo.server.core.cloud.VirtualMachine.MANAGED);
        }

        dto.setName(v.getName());
        dto.setPassword(v.getPassword());
        dto.setRam(v.getRam());
        dto.setState(v.getState());
        dto.setVdrpIP(v.getVdrpIP());
        dto.setVdrpPort(v.getVdrpPort());

        final Hypervisor hypervisor = v.getHypervisor();
        final Machine machine = hypervisor == null ? null : hypervisor.getMachine();
        final Rack rack = machine == null ? null : machine.getRack();

        final Enterprise enterprise = v.getEnterprise() == null ? null : v.getEnterprise();
        final User user = v.getUser() == null ? null : v.getUser();

        dto.addLinks(restBuilder.buildVirtualMachineCloudAdminLinks(vdcId, vappId, v.getId(),
            rack == null ? null : rack.getDatacenter().getId(), rack == null ? null : rack.getId(),
            machine == null ? null : machine.getId(),
            enterprise == null ? null : enterprise.getId(), user == null ? null : user.getId(),
            v.isChefEnabled()));

        final VirtualImage vimage = v.getVirtualImage();
        if (vimage != null)
        {
            dto.addLink(restBuilder.buildVirtualImageLink(vimage.getEnterprise().getId(), vimage
                .getRepository().getDatacenter().getId(), vimage.getId()));
        }

        TaskResourceUtils.addTasksLink(dto, dto.getEditLink());

        return dto;
    }

    // TODO forceEnterpriseLimits = true

    @PUT
    @Path("action/checkedit")
    @Deprecated
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
    @Deprecated
    @Path("action/deallocate")
    public synchronized void deallocate(
        @PathParam(VirtualApplianceResource.VIRTUAL_APPLIANCE) final Integer virtualApplianceId,
        @PathParam(VirtualMachineResource.VIRTUAL_MACHINE) final Integer virtualMachineId,
        @Context final IRESTBuilder restBuilder) throws Exception
    {
        service.deallocateVirtualMachine(virtualMachineId);
    }

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
    @Produces(VM_NODE_MEDIA_TYPE)
    public VirtualMachineWithNodeDto getVirtualMachineWithNode(
        @PathParam(VirtualDatacenterResource.VIRTUAL_DATACENTER) @NotNull @Min(1) final Integer vdcId,
        @PathParam(VirtualApplianceResource.VIRTUAL_APPLIANCE) @NotNull @Min(1) final Integer vappId,
        @PathParam(VirtualMachineResource.VIRTUAL_MACHINE) @NotNull @Min(1) final Integer vmId,
        @Context final IRESTBuilder restBuilder) throws Exception
    {
        NodeVirtualImage node = vmService.getNodeVirtualImage(vdcId, vappId, vmId);

        return createNodeTransferObject(node, vdcId, vappId, restBuilder);
    }

    @GET
    @Produces(MediaType.APPLICATION_XML)
    @Path(TaskResourceUtils.TASKS_PATH)
    public TasksDto getTasks(
        @PathParam(VirtualDatacenterResource.VIRTUAL_DATACENTER) @NotNull @Min(1) final Integer vdcId,
        @PathParam(VirtualApplianceResource.VIRTUAL_APPLIANCE) @NotNull @Min(1) final Integer vappId,
        @PathParam(VirtualMachineResource.VIRTUAL_MACHINE) @NotNull @Min(1) final Integer vmId,
        @Context final UriInfo uriInfo) throws Exception
    {
        vmService.getVirtualMachine(vdcId, vappId, vmId);
        List<Task> tasks = taskService.findTasks(TaskOwnerType.VIRTUAL_MACHINE, vmId.toString());

        return TaskResourceUtils.transform(tasks, uriInfo);
    }

    @GET
    @Produces(MediaType.APPLICATION_XML)
    @Path(TaskResourceUtils.TASK_PATH)
    public TaskDto getTask(
        @PathParam(VirtualDatacenterResource.VIRTUAL_DATACENTER) @NotNull @Min(1) final Integer vdcId,
        @PathParam(VirtualApplianceResource.VIRTUAL_APPLIANCE) @NotNull @Min(1) final Integer vappId,
        @PathParam(VirtualMachineResource.VIRTUAL_MACHINE) @NotNull @Min(1) final Integer vmId,
        @PathParam(TaskResourceUtils.TASK) @NotNull final String taskId,
        @Context final UriInfo uriInfo) throws Exception
    {
        vmService.getVirtualMachine(vdcId, vappId, vmId);
        Task task = taskService.findTask(vmId.toString(), taskId);

        return TaskResourceUtils.transform(task, uriInfo);
    }
}
