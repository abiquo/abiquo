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
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.UriInfo;

import org.apache.wink.common.annotations.Parent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import com.abiquo.api.exceptions.APIError;
import com.abiquo.api.exceptions.BadRequestException;
import com.abiquo.api.exceptions.InternalServerErrorException;
import com.abiquo.api.resources.AbstractResource;
import com.abiquo.api.resources.TaskResourceUtils;
import com.abiquo.api.services.TaskService;
import com.abiquo.api.services.cloud.VirtualDatacenterService;
import com.abiquo.api.services.cloud.VirtualMachineLock;
import com.abiquo.api.services.cloud.VirtualMachineService;
import com.abiquo.api.util.IRESTBuilder;
import com.abiquo.model.transport.AcceptedRequestDto;
import com.abiquo.model.transport.SeeOtherDto;
import com.abiquo.scheduler.SchedulerLock;
import com.abiquo.server.core.appslibrary.VirtualMachineTemplate;
import com.abiquo.server.core.cloud.Hypervisor;
import com.abiquo.server.core.cloud.NodeVirtualImage;
import com.abiquo.server.core.cloud.VirtualDatacenter;
import com.abiquo.server.core.cloud.VirtualMachine;
import com.abiquo.server.core.cloud.VirtualMachineDto;
import com.abiquo.server.core.cloud.VirtualMachineInstanceDto;
import com.abiquo.server.core.cloud.VirtualMachineState;
import com.abiquo.server.core.cloud.VirtualMachineStateDto;
import com.abiquo.server.core.cloud.VirtualMachineStateTransition;
import com.abiquo.server.core.cloud.VirtualMachineTaskDto;
import com.abiquo.server.core.cloud.VirtualMachineWithNodeDto;
import com.abiquo.server.core.cloud.VirtualMachineWithNodeExtendedDto;
import com.abiquo.server.core.enterprise.Enterprise;
import com.abiquo.server.core.enterprise.User;
import com.abiquo.server.core.infrastructure.Machine;
import com.abiquo.server.core.infrastructure.Rack;
import com.abiquo.server.core.infrastructure.network.IpPoolManagement;
import com.abiquo.server.core.task.Task;
import com.abiquo.server.core.task.TaskDto;
import com.abiquo.server.core.task.TasksDto;
import com.abiquo.server.core.task.enums.TaskOwnerType;

@Parent(VirtualMachinesResource.class)
@Controller
@Path(VirtualMachineResource.VIRTUAL_MACHINE_PARAM)
public class VirtualMachineResource extends AbstractResource
{
    public static final String VIRTUAL_MACHINE = "virtualmachine";

    public static final String VIRTUAL_MACHINE_PARAM = "{" + VIRTUAL_MACHINE + "}";

    public static final String VIRTUAL_MACHINE_DEPLOY_PATH = "action/deploy";

    public static final String VIRTUAL_MACHINE_DEPLOY_REL = "deploy";

    public static final String VIRTUAL_MACHINE_UNDEPLOY_PATH = "action/undeploy";

    public static final String VIRTUAL_MACHINE_UNDEPLOY_REL = "undeploy";

    public static final String VIRTUAL_MACHINE_ACTION_RESET = "action/reset";

    public static final String VIRTUAL_MACHINE_STATE_PATH = "state";

    public static final String VIRTUAL_MACHINE_STATE_REL = "state";

    // Chef constants to help link builders. Method implementation are premium.
    public static final String VIRTUAL_MACHINE_RUNLIST_PATH = "config/runlist";

    public static final String VIRTUAL_MACHINE_RUNLIST_REL = "runlist";

    public static final String VIRTUAL_MACHINE_BOOTSTRAP_PATH = "config/bootstrap";

    public static final String VIRTUAL_MACHINE_BOOTSTRAP_REL = "bootstrap";

    public static final String VIRTUAL_MACHINE_ACTION_DEPLOY_REL = "deploy";

    public static final String VIRTUAL_MACHINE_ACTION_SNAPSHOT_REL = "instance";

    public static final String VIRTUAL_MACHINE_ACTION_SNAPSHOT = "action/instance";

    public static final String VIRTUAL_MACHINE_ACTION_UNDEPLOY_REL = "undeploy";

    public static final String VIRTUAL_MACHINE_ACTION_RESET_REL = "reset";

    public static final String FORCE_UNDEPLOY = "force";

    public static final String FORCE = "force";

    @Autowired
    private VirtualMachineService vmService;

    @Autowired
    private VirtualDatacenterService vdcService;

    @Autowired
    private TaskService taskService;

    @Autowired
    private VirtualMachineLock vmLock;

    /**
     * Return the virtual machine if exists.
     * 
     * @title Retrieve a virtual machine
     * @param vdcId identifier of the virtual datacenter.
     * @param vappId identifier of the virtual appliance.
     * @param vmId identifier of the virtual machine
     * @param restBuilder to build the links
     * @return the {@link VirtualMachineDto} transfer object for the virtual machine.
     * @throws Exception
     */
    @GET
    @Produces(VirtualMachineDto.MEDIA_TYPE)
    public VirtualMachineDto getVirtualMachine(
        @PathParam(VirtualDatacenterResource.VIRTUAL_DATACENTER) @NotNull @Min(1) final Integer vdcId,
        @PathParam(VirtualApplianceResource.VIRTUAL_APPLIANCE) @NotNull @Min(1) final Integer vappId,
        @PathParam(VirtualMachineResource.VIRTUAL_MACHINE) @NotNull @Min(1) final Integer vmId,
        @Context final IRESTBuilder restBuilder) throws Exception
    {
        VirtualMachine vm = vmService.getVirtualMachine(vdcId, vappId, vmId);
        VirtualDatacenter vdc = vdcService.getVirtualDatacenter(vdcId);

        return createTransferObject(vm, vdc, vappId, restBuilder, getVolumeIds(vm), getDiskIds(vm),
            vm.getIps());
    }

    /***
     * @title Modify a virtual machine
     * @param vdcId VirtualDatacenter id
     * @param vappId VirtualAppliance id
     * @param vmId VirtualMachine id
     * @param restBuilder injected restbuilder context parameter
     * @return a link where you can keep track of the progress and the virtual machine.
     * @throws Exception AcceptedRequestDto
     */
    @PUT
    @Consumes(VirtualMachineDto.MEDIA_TYPE)
    @Produces(AcceptedRequestDto.MEDIA_TYPE)
    public AcceptedRequestDto<String> updateVirtualMachine(
        @PathParam(VirtualDatacenterResource.VIRTUAL_DATACENTER) @NotNull @Min(1) final Integer vdcId,
        @PathParam(VirtualApplianceResource.VIRTUAL_APPLIANCE) @NotNull @Min(1) final Integer vappId,
        @PathParam(VirtualMachineResource.VIRTUAL_MACHINE) @NotNull @Min(1) final Integer vmId,
        @QueryParam(FORCE) @DefaultValue("false") final Boolean force, final VirtualMachineDto dto,
        @Context final IRESTBuilder restBuilder, @Context final UriInfo uriInfo) throws Exception
    {
        VirtualMachineState originalState =
            vmLock.lockVirtualMachineBeforeReconfiguring(vdcId, vappId, vmId);

        try
        {
            String taskId =
                vmService.reconfigureVirtualMachine(vdcId, vappId, vmId, dto, originalState, force);
            if (taskId == null)
            {
                // If there is no async task the VM must be unlocked here
                vmLock.unlockVirtualMachine(vmId, originalState);
                // If the link is null no Task was performed
                return null;
            }
            return buildAcceptedRequestDtoWithTaskLink(taskId, uriInfo);
        }
        catch (Exception ex)
        {
            // Make sure virtual machine is unlocked if reconfigure fails
            vmLock.unlockVirtualMachine(vmId, originalState);
            throw ex;
        }
    }

    /**
     * Updates this virtual Machine Node information (e.g. name)
     * 
     * @title Modify the virtual machine Node information
     * @param vdcId
     * @param vappId
     * @param vmId
     * @param dto
     * @param restBuilder
     * @param uriInfo
     * @return
     * @throws Exception
     */
    @PUT
    @Consumes(VirtualMachineWithNodeDto.MEDIA_TYPE)
    @Produces(AcceptedRequestDto.MEDIA_TYPE)
    public AcceptedRequestDto<String> updateVirtualMachineNode(
        @PathParam(VirtualDatacenterResource.VIRTUAL_DATACENTER) @NotNull @Min(1) final Integer vdcId,
        @PathParam(VirtualApplianceResource.VIRTUAL_APPLIANCE) @NotNull @Min(1) final Integer vappId,
        @PathParam(VirtualMachineResource.VIRTUAL_MACHINE) @NotNull @Min(1) final Integer vmId,
        @QueryParam(FORCE) @DefaultValue("false") final Boolean force,
        final VirtualMachineWithNodeDto dto, @Context final IRESTBuilder restBuilder,
        @Context final UriInfo uriInfo) throws Exception
    {
        vmService.updateNodeVirtualImageInfo(vdcId, vappId, vmId, dto);
        return updateVirtualMachine(vdcId, vappId, vmId, force, dto, restBuilder, uriInfo);
    }

    /**
     * Change the {@link VirtualMachineState} the virtual machine
     * 
     * @title Change the state of a virtual machine
     * @wiki The allowed states are: OFF, PAUSED, ON.
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
    @Path(VIRTUAL_MACHINE_STATE_PATH)
    @Produces(AcceptedRequestDto.MEDIA_TYPE)
    @Consumes(VirtualMachineStateDto.MEDIA_TYPE)
    public AcceptedRequestDto<String> powerStateVirtualMachine(
        @PathParam(VirtualDatacenterResource.VIRTUAL_DATACENTER) final Integer vdcId,
        @PathParam(VirtualApplianceResource.VIRTUAL_APPLIANCE) final Integer vappId,
        @PathParam(VirtualMachineResource.VIRTUAL_MACHINE) final Integer vmId,
        final VirtualMachineStateDto state, @Context final IRESTBuilder restBuilder,
        @Context final UriInfo uriInfo) throws Exception
    {
        VirtualMachineState newState = validateState(state);

        // Lock the virtual machine
        VirtualMachineState originalState =
            vmLock.lockVirtualMachineBeforeChangingState(vdcId, vappId, vmId, newState);

        VirtualMachineStateTransition transition =
            VirtualMachineStateTransition.getValidVmStateChangeTransition(originalState, newState);

        try
        {
            String taskId = vmService.applyVirtualMachineState(vmId, vappId, vdcId, transition);
            // If the link is null no Task was performed
            if (taskId == null)
            {
                throw new InternalServerErrorException(APIError.STATUS_INTERNAL_SERVER_ERROR);
            }
            return buildAcceptedRequestDtoWithTaskLinkNoAction(taskId, uriInfo);
        }
        catch (Exception ex)
        {
            // Make sure virtual machine is unlocked if power state change fails
            vmLock.unlockVirtualMachine(vmId, originalState);
            throw ex;
        }
    }

    /**
     * Retrieve the {@link VirtualMachineState} the virtual machine
     * 
     * @title Retrieve the state of the virtual machine
     * @param vdcId VirtualDatacenter id
     * @param vappId VirtualAppliance id
     * @param vmId VirtualMachine id
     * @param restBuilder injected restbuilder context parameter
     * @return state
     * @throws Exception
     */
    @GET
    @Path(VIRTUAL_MACHINE_STATE_PATH)
    @Produces(VirtualMachineStateDto.MEDIA_TYPE)
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
        stateDto.setState(vm.getState());
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
        if (!VirtualMachineState.ON.equals(state.getState())
            && !VirtualMachineState.OFF.equals(state.getState())
            && !VirtualMachineState.PAUSED.equals(state.getState()))
        {
            throw new BadRequestException(APIError.VIRTUAL_MACHINE_EDIT_STATE);
        }
        return state.getState();
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
     * @title Delete a virtual machine
     * @wiki If the virtual machine exists in the hypervisor it will be removed from the hypervisor
     *       as well.
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
        // Check virtual machine state and lock it before starting
        VirtualMachineState originalState =
            vmLock.lockVirtualMachineBeforeDeleting(vdcId, vappId, vmId);

        try
        {
            vmService.deleteVirtualMachine(vmId, vappId, vdcId, originalState);

            // If everything goes fine, there is no need to unlock the VM since it will be deleted
            // by the handler or here if it was not deployed
        }
        catch (Exception ex)
        {
            // Make sure virtual machine is unlocked if deploy fails
            vmLock.unlockVirtualMachine(vmId, originalState);
            throw ex;
        }
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
     * @title Deploy a virtual machine
     * @wiki Deploys the virtual machine with the given options. There is also possible to do not
     *       specify any options and assume the defaults. This call returns a 202 HTTP code
     *       (accepted) and a URI where you can keep track of the deploy. The options are not
     *       mandatory. The options are : -forceEnterpriseSoftLimits This flag forces a check on the
     *       enterprise soft limits when calculating the available resources. If this check fails
     *       the deploy won't be performed.
     * @param vdcId VirtualDatacenter id
     * @param vappId VirtualAppliance id
     * @param vmId VirtualMachine id
     * @param restBuilder injected restbuilder context parameter
     * @param forceSoftLimits dto of options
     * @return a link where you can keep track of the progress and a message.
     * @throws Exception
     */
    @POST
    @Path(VIRTUAL_MACHINE_DEPLOY_PATH)
    @Consumes(VirtualMachineTaskDto.MEDIA_TYPE)
    @Produces(AcceptedRequestDto.MEDIA_TYPE)
    public AcceptedRequestDto<String> deployVirtualMachine(
        @PathParam(VirtualDatacenterResource.VIRTUAL_DATACENTER) final Integer vdcId,
        @PathParam(VirtualApplianceResource.VIRTUAL_APPLIANCE) final Integer vappId,
        @PathParam(VirtualMachineResource.VIRTUAL_MACHINE) final Integer vmId,
        final VirtualMachineTaskDto forceSoftLimits, @Context final IRESTBuilder restBuilder,
        @Context final UriInfo uriInfo) throws Exception
    {
        final String lockMsg = "Allocate vm " + vmId;

        // Check virtual machine state and lock it before starting
        VirtualMachineState originalState =
            vmLock.lockVirtualMachineBeforeDeploying(vdcId, vappId, vmId);

        try
        {
            SchedulerLock.acquire(lockMsg);

            String taskId =
                vmService.deployVirtualMachine(vmId, vappId, vdcId, forceSoftLimits
                    .isForceEnterpriseSoftLimits());

            return buildAcceptedRequestDtoWithTaskLink(taskId, uriInfo);
        }
        catch (Exception ex)
        {
            // Make sure virtual machine is unlocked if deploy fails
            vmLock.unlockVirtualMachine(vmId, originalState);
            throw ex;
        }
        finally
        {
            SchedulerLock.release(lockMsg);
        }
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
     * @title Deploy a virtual machine
     * @wiki Deploys the virtual machine with the given options. There is also possible to do not
     *       specify any options and assume the defaults. This call returns a 202 HTTP code
     *       (accepted) and a URI where you can keep track of the deploy. The options are not
     *       mandatory. The options are : -forceEnterpriseSoftLimits This flag forces a check on the
     *       enterprise soft limits when calculating the available resources. If this check fails
     *       the deploy won't be performed.
     * @param vdcId VirtualDatacenter id
     * @param vappId VirtualAppliance id
     * @param vmId VirtualMachine id
     * @param restBuilder injected restbuilder context parameter
     * @return a link where you can keep track of the progress and a message.
     * @throws Exception
     */
    @POST
    @Path(VIRTUAL_MACHINE_DEPLOY_PATH)
    @Produces(AcceptedRequestDto.MEDIA_TYPE)
    public AcceptedRequestDto<String> deployVirtualMachine(
        @PathParam(VirtualDatacenterResource.VIRTUAL_DATACENTER) final Integer vdcId,
        @PathParam(VirtualApplianceResource.VIRTUAL_APPLIANCE) final Integer vappId,
        @PathParam(VirtualMachineResource.VIRTUAL_MACHINE) final Integer vmId,
        @Context final IRESTBuilder restBuilder, @Context final UriInfo uriInfo) throws Exception
    {
        VirtualMachineTaskDto force = new VirtualMachineTaskDto();
        force.setForceEnterpriseSoftLimits(false);
        return deployVirtualMachine(vdcId, vappId, vmId, force, restBuilder, uriInfo);
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
     * @title Undeploy the virtual machine
     * @wiki Perform an undeploy. This means that after the call, the virtual machine in Abiquo will
     *       be in the NOT_ALLOCATED state. If the undeploy is successful, the virtual machine will
     *       be deleted from the hypervisor. If the virtual machine is in the ON state, Abiquo will
     *       perform a power off before the deconfigure. You can also set the force undeploy
     *       parameter in the virtual machine task entity. If this is set to true, the imported
     *       virtual machines are also deleted. This call returns a 202 HTTP code (accepted) and a
     *       URI where you can keep track of the undeploy. The possible option for an undeploy is
     *       -forceUndeploy. If this flag is set to false we do not undeploy imported virtual
     *       machines.
     * @param vdcId VirtualDatacenter id
     * @param vappId VirtualAppliance id
     * @param vmId VirtualMachine id
     * @param restBuilder injected restbuilder context parameter
     * @return a link where you can keep track of the progress and a message.
     * @throws Exception
     */
    @POST
    @Path(VIRTUAL_MACHINE_UNDEPLOY_PATH)
    @Consumes(VirtualMachineTaskDto.MEDIA_TYPE)
    @Produces(AcceptedRequestDto.MEDIA_TYPE)
    public AcceptedRequestDto<String> undeployVirtualMachine(
        @PathParam(VirtualDatacenterResource.VIRTUAL_DATACENTER) final Integer vdcId,
        @PathParam(VirtualApplianceResource.VIRTUAL_APPLIANCE) final Integer vappId,
        @PathParam(VirtualMachineResource.VIRTUAL_MACHINE) final Integer vmId,
        final VirtualMachineTaskDto taskOptions, @Context final IRESTBuilder restBuilder,
        @Context final UriInfo uriInfo) throws Exception
    {
        Boolean forceUndeploy;
        if (taskOptions.getForceUndeploy() == null)
        {
            forceUndeploy = Boolean.FALSE;
        }
        else
        {
            forceUndeploy = taskOptions.getForceUndeploy();
        }

        // Lock the virtual machine before undeploying
        VirtualMachineState originalState =
            vmLock.lockVirtualMachineBeforeUndeploying(vdcId, vappId, vmId);

        try
        {
            String taskId =
                vmService.undeployVirtualMachine(vmId, vappId, vdcId, forceUndeploy, originalState);
            // If the link is null no Task was performed
            if (taskId == null)
            {
                throw new InternalServerErrorException(APIError.STATUS_INTERNAL_SERVER_ERROR);
            }
            return buildAcceptedRequestDtoWithTaskLink(taskId, uriInfo);
        }
        catch (Exception ex)
        {
            // Make sure virtual machine is unlocked if undeploy fails
            vmLock.unlockVirtualMachine(vmId, originalState);
            throw ex;
        }
    }

    /**
     * Snapshot a {@link VirtualMachine}.>
     * 
     * @title Snapshot a virtual machine
     * @wiki Instance the virtual machine with the given name. This call returns a 202 HTTP code
     *       (accepted) and a URI where you can keep track of the deploy. The options are mandatory.
     *       The options are : -instanceName The final name of the instance
     * @param vdcId VirtualDatacenter id
     * @param vappId VirtualAppliance id
     * @param vmId VirtualMachine id
     * @param restBuilder injected restbuilder context parameter
     * @return a link where you can keep track of the progress and a message.
     * @throws Exception
     */
    @POST
    @Path(VIRTUAL_MACHINE_ACTION_SNAPSHOT)
    @Consumes(VirtualMachineInstanceDto.MEDIA_TYPE)
    @Produces(AcceptedRequestDto.MEDIA_TYPE)
    public AcceptedRequestDto<String> snapshotVirtualMachine(
        @PathParam(VirtualDatacenterResource.VIRTUAL_DATACENTER) final Integer vdcId,
        @PathParam(VirtualApplianceResource.VIRTUAL_APPLIANCE) final Integer vappId,
        @PathParam(VirtualMachineResource.VIRTUAL_MACHINE) final Integer vmId,
        final VirtualMachineInstanceDto snapshotData, @Context final IRESTBuilder restBuilder,
        @Context final UriInfo uriInfo) throws Exception
    {
        VirtualMachineState originalState =
            vmLock.lockVirtualMachineBeforeSnapshotting(vdcId, vappId, vmId);

        try
        {
            String taskId =
                vmService.instanceVirtualMachine(vmId, vappId, vdcId, snapshotData
                    .getInstanceName(), originalState);
            if (taskId == null)
            {
                throw new InternalServerErrorException(APIError.STATUS_INTERNAL_SERVER_ERROR);
            }
            return buildAcceptedRequestDtoWithTaskLink(taskId, uriInfo);
        }
        catch (Exception ex)
        {
            // Make sure virtual machine is unlocked if snapshot fails
            vmLock.unlockVirtualMachine(vmId, originalState);
            throw ex;
        }
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
        final Integer vdcId, final Integer vappId, final IRESTBuilder restBuilder,
        final Integer[] volumeIds, final Integer[] diskIds, final List<IpPoolManagement> ips)
        throws Exception
    {
        VirtualMachineWithNodeDto dto = new VirtualMachineWithNodeDto();
        dto.setUuid(v.getVirtualMachine().getUuid());
        dto.setCpu(v.getVirtualMachine().getCpu());
        dto.setDescription(v.getVirtualMachine().getDescription());
        dto.setHdInBytes(v.getVirtualMachine().getHdInBytes());
        dto.setHighDisponibility(v.getVirtualMachine().getHighDisponibility());
        dto.setId(v.getVirtualMachine().getId());
        dto.setIdState(v.getVirtualMachine().getState().id());
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
        final VirtualMachineTemplate virtualImage =
            v.getVirtualImage() == null ? null : v.getVirtualImage();

        final VirtualDatacenter vdc = v.getVirtualAppliance().getVirtualDatacenter();

        if (!v.getVirtualMachine().isCaptured())
        {
            if (v.getVirtualMachine().isStateful())
            {
                dto.addLink(restBuilder.buildVirtualMachineTemplateLink(virtualImage
                    .getEnterprise().getId(), v.getVirtualAppliance().getVirtualDatacenter()
                    .getDatacenter().getId(), virtualImage.getId()));
            }
            else
            {
                dto.addLink(restBuilder.buildVirtualMachineTemplateLink(virtualImage
                    .getEnterprise().getId(), virtualImage.getRepository().getDatacenter().getId(),
                    virtualImage.getId()));
            }
        }
        else
        {
            if (v.getVirtualMachine().getState().equals(VirtualMachineState.NOT_ALLOCATED))
            {
                // captured and managed virtual machines but with pm removed
                dto.addLink(restBuilder.buildVirtualMachineTemplateLink(virtualImage
                    .getEnterprise().getId(), v.getVirtualAppliance().getVirtualDatacenter()
                    .getDatacenter().getId(), v.getVirtualImage().getId()));
            }
            else
            {
                // captured virtual machines
                dto.addLink(restBuilder.buildVirtualMachineTemplateLink(virtualImage
                    .getEnterprise().getId(), v.getVirtualMachine().getHypervisor().getMachine()
                    .getRack().getDatacenter().getId(), v.getVirtualImage().getId()));
            }
        }

        dto.addLinks(restBuilder.buildVirtualMachineCloudAdminLinks(vdcId, vappId, v
            .getVirtualMachine(), rack == null ? null : rack.getDatacenter().getId(), rack == null
            ? null : rack.getId(), machine == null ? null : machine.getId(), enterprise == null
            ? null : enterprise.getId(), user == null ? null : user.getId(), v.getVirtualMachine()
            .isChefEnabled(), volumeIds, diskIds, ips, vdc.getHypervisorType(), v
            .getVirtualAppliance()));

        TaskResourceUtils.addTasksLink(dto, dto.getEditLink());

        return dto;
    }

    public static VirtualMachineWithNodeExtendedDto createNodeExtendedTransferObject(
        final NodeVirtualImage v, final Integer vdcId, final Integer vappId,
        final IRESTBuilder restBuilder, final Integer[] volumeIds, final Integer[] diskIds,
        final List<IpPoolManagement> ips) throws Exception
    {
        User userVm = v.getVirtualMachine().getUser();
        VirtualMachineWithNodeDto dto =
            createNodeTransferObject(v, vdcId, vappId, restBuilder, volumeIds, diskIds, ips);
        VirtualMachineWithNodeExtendedDto extendedDto = null;
        if (userVm == null)
        {
            // DELETED USER
            extendedDto = new VirtualMachineWithNodeExtendedDto(dto, "", "", "");
        }
        else
        {
            extendedDto =
                new VirtualMachineWithNodeExtendedDto(dto,
                    userVm.getName(),
                    userVm.getSurname(),
                    userVm.getEnterprise().getName());
        }
        return extendedDto;
    }

    @Deprecated
    // use the integer based version
    public static VirtualMachineDto createTransferObject(final VirtualMachine v,
        final VirtualDatacenter vdc, final IRESTBuilder restBuilder)
    {
        VirtualMachineDto dto = new VirtualMachineDto();

        dto.setCpu(v.getCpu());
        dto.setDescription(v.getDescription());
        dto.setHdInBytes(v.getHdInBytes());
        dto.setHighDisponibility(v.getHighDisponibility());
        dto.setId(v.getId());
        dto.setIdState(v.getState().id());
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
            : user.getId(), vdc.getHypervisorType(), null, v.getId()));

        final VirtualMachineTemplate vmtemplate = v.getVirtualMachineTemplate();
        if (vmtemplate.getRepository() != null)
        {
            dto.addLink(restBuilder.buildVirtualMachineTemplateLink(vmtemplate.getEnterprise()
                .getId(), vmtemplate.getRepository().getDatacenter().getId(), vmtemplate.getId()));
        }
        else
        {
            if (vmtemplate.isStateful())
            {
                // stateful virtual machines (template hasn't got repository)
                dto.addLink(restBuilder.buildVirtualMachineTemplateLink(vmtemplate.getEnterprise()
                    .getId(), vdc.getDatacenter().getId(), vmtemplate.getId()));
            }
            else
            {
                // imported virtual machines
                dto.addLink(restBuilder.buildVirtualMachineTemplateLink(vmtemplate.getEnterprise()
                    .getId(), v.getHypervisor().getMachine().getRack().getDatacenter().getId(),
                    vmtemplate.getId()));
            }
        }

        TaskResourceUtils.addTasksLink(dto, dto.getEditLink());

        return dto;
    }

    public static VirtualMachineDto createTransferObject(final VirtualMachine v,
        final VirtualDatacenter vdc, final Integer vappId, final IRESTBuilder restBuilder,
        final Integer[] volumeIds, final Integer diskIds[], final List<IpPoolManagement> ips)
    {

        VirtualMachineDto dto = new VirtualMachineDto();

        dto.setUuid(v.getUuid());
        dto.setCpu(v.getCpu());
        dto.setDescription(v.getDescription());
        dto.setHdInBytes(v.getHdInBytes());
        dto.setHighDisponibility(v.getHighDisponibility());
        dto.setId(v.getId());
        dto.setIdState(v.getState().id());
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

        dto.addLinks(restBuilder.buildVirtualMachineCloudAdminLinks(vdc.getId(), vappId, v,
            rack == null ? null : rack.getDatacenter().getId(), rack == null ? null : rack.getId(),
            machine == null ? null : machine.getId(), enterprise == null ? null : enterprise
                .getId(), user == null ? null : user.getId(), v.isChefEnabled(), volumeIds,
            diskIds, ips, vdc.getHypervisorType(), null));

        final VirtualMachineTemplate vmtemplate = v.getVirtualMachineTemplate();
        if (vmtemplate.getRepository() != null)
        {
            dto.addLink(restBuilder.buildVirtualMachineTemplateLink(vmtemplate.getEnterprise()
                .getId(), vmtemplate.getRepository().getDatacenter().getId(), vmtemplate.getId()));
        }
        else
        {
            if (vmtemplate.isStateful())
            {
                // stateful virtual machines (template hasn't got repository)
                dto.addLink(restBuilder.buildVirtualMachineTemplateLink(vmtemplate.getEnterprise()
                    .getId(), vdc.getDatacenter().getId(), vmtemplate.getId()));
            }
            else
            {
                // imported virtual machines
                dto.addLink(restBuilder.buildVirtualMachineTemplateLink(vmtemplate.getEnterprise()
                    .getId(), v.getHypervisor().getMachine().getRack().getDatacenter().getId(),
                    vmtemplate.getId()));
            }
        }

        return dto;
    }

    /**
     * Return the virtual machine if exists.
     * 
     * @title Retrieve a virtual machine with the node
     * @param vdcId identifier of the virtual datacenter.
     * @param vappId identifier of the virtual appliance.
     * @param vmId identifier of the virtual machine
     * @param restBuilder to build the links
     * @return the {@link VirtualMachineWithNodeDto} transfer object for the virtual machine with
     *         the node.
     * @throws Exception
     */
    @GET
    @Produces(VirtualMachineWithNodeDto.MEDIA_TYPE)
    public VirtualMachineWithNodeDto getVirtualMachineWithNode(
        @PathParam(VirtualDatacenterResource.VIRTUAL_DATACENTER) @NotNull @Min(1) final Integer vdcId,
        @PathParam(VirtualApplianceResource.VIRTUAL_APPLIANCE) @NotNull @Min(1) final Integer vappId,
        @PathParam(VirtualMachineResource.VIRTUAL_MACHINE) @NotNull @Min(1) final Integer vmId,
        @Context final IRESTBuilder restBuilder) throws Exception
    {
        NodeVirtualImage node = vmService.getNodeVirtualImage(vdcId, vappId, vmId);

        return createNodeTransferObject(node, vdcId, vappId, restBuilder, getVolumeIds(node
            .getVirtualMachine()), getDiskIds(node.getVirtualMachine()), node.getVirtualMachine()
            .getIps());
    }

    /**
     * Returns all tasks for a machine
     * 
     * @title Retrive all tasks
     * @wiki Displays the tasks on the virtual machine. Tasks are a set of jobs (operations on
     *       hypervisors). Any of these tasks may still be in progress.
     * @param vdcId identifier of the virtual datacenter
     * @param vappId identifier of the virtual appliance
     * @param vmId identifier of the virtual machine
     * @param uriInfo
     * @return a {TasksDto} with all tasks for the machine
     * @throws Exception
     */
    @GET
    @Produces(TasksDto.MEDIA_TYPE)
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

    /**
     * Returns a task for a virtual machine
     * 
     * @title Retrieve a task
     * @wiki Displays a specific task on the virtual machine. Tasks are a set of jobs (operations on
     *       hypervisors). Any of these tasks may still be in progress. Every task has a UUID.
     * @param vdcId identifier of the virtual datacenter
     * @param vappId identifier of the virtual appliance
     * @param vmId identifier of the virtual machine
     * @param taskId identifier of the task
     * @param uriInfo
     * @return a {TaksDto} with the task of the machine
     * @throws Exception
     */
    @GET
    @Produces(TaskDto.MEDIA_TYPE)
    @Path(TaskResourceUtils.TASK_PATH)
    public TaskDto getTask(
        @PathParam(VirtualDatacenterResource.VIRTUAL_DATACENTER) @NotNull @Min(1) final Integer vdcId,
        @PathParam(VirtualApplianceResource.VIRTUAL_APPLIANCE) @NotNull @Min(1) final Integer vappId,
        @PathParam(VirtualMachineResource.VIRTUAL_MACHINE) @NotNull @Min(1) final Integer vmId,
        @PathParam(TaskResourceUtils.TASK) @NotNull final String taskId,
        @Context final UriInfo uriInfo) throws Exception
    {
        vmService.getVirtualMachine(vdcId, vappId, vmId);

        if (taskId.equalsIgnoreCase(TaskResourceUtils.UNTRACEABLE_TASK))
        {
            return buildSeeOtherDto(uriInfo);
        }

        Task task = taskService.findTask(vmId.toString(), taskId);

        return TaskResourceUtils.transform(task, uriInfo);
    }

    protected Integer[] getVolumeIds(final VirtualMachine vm)
    {
        return null; // Community impl
    }

    protected Integer[] getDiskIds(final VirtualMachine vm)
    {
        return null; // Community impl
    }

    /**
     * Reset a {@link VirtualMachine}.
     * 
     * @title Reset a virtual machine
     * @param vdcId VirtualDatacenter id
     * @param vappId VirtualAppliance id
     * @param vmId VirtualMachine id
     * @param restBuilder injected restbuilder context parameter
     * @return a link where you can keep track of the progress and a message.
     * @throws Exception
     */
    @POST
    @Path(VIRTUAL_MACHINE_ACTION_RESET)
    @Produces(AcceptedRequestDto.MEDIA_TYPE)
    public AcceptedRequestDto<String> resetVirtualMachine(
        @PathParam(VirtualDatacenterResource.VIRTUAL_DATACENTER) final Integer vdcId,
        @PathParam(VirtualApplianceResource.VIRTUAL_APPLIANCE) final Integer vappId,
        @PathParam(VirtualMachineResource.VIRTUAL_MACHINE) final Integer vmId,
        @Context final IRESTBuilder restBuilder, @Context final UriInfo uriInfo) throws Exception
    {
        VirtualMachineState originalState =
            vmLock.lockVirtualMachineBeforeResetting(vdcId, vappId, vmId);

        try
        {
            String taskId =
                vmService.resetVirtualMachine(vmId, vappId, vdcId,
                    VirtualMachineStateTransition.RESET);
            // If the link is null no Task was performed
            if (taskId == null)
            {
                throw new InternalServerErrorException(APIError.STATUS_INTERNAL_SERVER_ERROR);
            }
            return buildAcceptedRequestDtoWithTaskLink(taskId, uriInfo);
        }
        catch (Exception ex)
        {
            // Make sure virtual machine is unlocked if reset fails
            vmLock.unlockVirtualMachine(vmId, originalState);
            throw ex;
        }
    }

    /**
     * @param taskId
     * @param uriInfo
     * @return AcceptedRequestDto<String>
     */
    protected AcceptedRequestDto<String> buildAcceptedRequestDtoWithTaskLink(final String taskId,
        final UriInfo uriInfo)
    {
        // Build task link
        String link = uriInfo.getRequestUri().toString();

        link = link.replaceAll("action.*", "");
        link = link.replaceAll("(/)*$", "");
        link = link.replaceAll("\\?force=(true|false)", "");
        link = link.concat(TaskResourceUtils.TASKS_PATH).concat("/").concat(taskId);

        // Build AcceptedRequestDto
        AcceptedRequestDto<String> a202 = new AcceptedRequestDto<String>();
        a202.setStatusUrlLink(link);
        a202.setEntity("You can keep track of the progress in the link");

        return a202;
    }

    /**
     * This function expects a uri. With its <b>numerical</b> id as a last segment. The final slash
     * is optional and additional uri segments as well. If presents all segments after the id will
     * be deleted. <br>
     * <br>
     * <b>This function does not work with UUID as id<br>
     * <br>
     * </b> TODO UUID <br>
     * <br>
     * <code>
     *     http://example.com/api/cloud/virtualdatacenters/2/virtualappliances/4/virtualmachines/1/state -> http://example.com/api/cloud/virtualdatacenters/2/virtualappliances/4/virtualmachines/1/tasks/taskId <br>
     * 
     *     http://example.com/api/cloud/virtualdatacenters/2/virtualappliances/4/virtualmachines/1 -> http://example.com/api/cloud/virtualdatacenters/2/virtualappliances/4/virtualmachines/1/tasks/taskId <br>
     * 
     *     http://example.com/api/cloud/virtualdatacenters/2/virtualappliances/4/virtualmachines/1/ -> http://example.com/api/cloud/virtualdatacenters/2/virtualappliances/4/virtualmachines/1/tasks/taskId <br>
     * 
     *     http://example.com/api/cloud/virtualdatacenters/2/virtualappliances/4/virtualmachines/1/action/undeploy -> http://example.com/api/cloud/virtualdatacenters/2/virtualappliances/4/virtualmachines/1/tasks/taskId <br>
     * 
     *     http://example.com/api/cloud/virtualdatacenters/2/virtualappliances/4/state -> http://example.com/api/cloud/virtualdatacenters/2/virtualappliances/4/tasks/taskId <br>
     * 
     *     http://example.com/api/cloud/virtualdatacenters/2/virtualappliances/4 -> http://example.com/api/cloud/virtualdatacenters/2/virtualappliances/4/tasks/taskId <br>
     * 
     *     http://example.com/api/cloud/virtualdatacenters/2/virtualappliances/4/ -> http://example.com/api/cloud/virtualdatacenters/2/virtualappliances/4/tasks/taskId <br>
     * 
     *     http://example.com/api/cloud/virtualdatacenters/2/virtualappliances/4/undeploy -> http://example.com/api/cloud/virtualdatacenters/2/virtualappliances//tasks/taskId <br>
     *     
     * 
     *     <b>Even with invalid urls</b>
     *     http://example.com/api/cloud/virtualdatacenters/2/virtualappliances/4/virtualmachines/1j/ -> http://example.com/api/cloud/virtualdatacenters/2/virtualappliances/4/virtualmachines/1/tasks/taskId <br>
     * <code>
     * 
     * @param taskId
     * @param uriInfo
     * @return AcceptedRequestDto<String>
     */
    protected AcceptedRequestDto<String> buildAcceptedRequestDtoWithTaskLinkNoAction(
        final String taskId, final UriInfo uriInfo)
    {
        // Build task link
        String link = uriInfo.getRequestUri().toString();

        Pattern regex = Pattern.compile("(.*/\\d+)[/]?.*$");
        Matcher regexMatcher = regex.matcher(link);
        if (regexMatcher.find())
        {
            link =
                regexMatcher.replaceAll(regexMatcher.group(1).concat(TaskResourceUtils.TASKS_PATH)
                    .concat("/").concat(taskId));
        }
        // Build AcceptedRequestDto
        AcceptedRequestDto<String> a202 = new AcceptedRequestDto<String>();
        a202.setStatusUrlLink(link);
        a202.setEntity("You can keep track of the progress in the link");

        return a202;
    }

    protected SeeOtherDto buildSeeOtherDto(final UriInfo uriInfo)
    {
        // Build state link
        String link = uriInfo.getRequestUri().toString();

        link = TaskResourceUtils.removeTaskSegments(link);
        link = link.concat("/").concat(VIRTUAL_MACHINE_STATE_PATH);

        // Build SeeOtherDto
        return new SeeOtherDto(link);
    }
}
