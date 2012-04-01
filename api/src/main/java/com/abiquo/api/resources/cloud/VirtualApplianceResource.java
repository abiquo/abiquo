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
import java.util.Map;
import java.util.Map.Entry;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.UriInfo;

import org.apache.commons.lang.StringUtils;
import org.apache.wink.common.annotations.Parent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import com.abiquo.api.resources.AbstractResource;
import com.abiquo.api.resources.TaskResourceUtils;
import com.abiquo.api.services.NetworkService;
import com.abiquo.api.services.cloud.VirtualApplianceService;
import com.abiquo.api.services.cloud.VirtualMachineLock;
import com.abiquo.api.util.IRESTBuilder;
import com.abiquo.model.rest.RESTLink;
import com.abiquo.model.transport.AcceptedRequestDto;
import com.abiquo.model.util.ModelTransformer;
import com.abiquo.scheduler.SchedulerLock;
import com.abiquo.server.core.appslibrary.VirtualMachineTemplateDto;
import com.abiquo.server.core.cloud.VirtualAppliance;
import com.abiquo.server.core.cloud.VirtualApplianceDto;
import com.abiquo.server.core.cloud.VirtualApplianceState;
import com.abiquo.server.core.cloud.VirtualApplianceStateDto;
import com.abiquo.server.core.cloud.VirtualMachineState;
import com.abiquo.server.core.cloud.VirtualMachineTaskDto;
import com.abiquo.server.core.infrastructure.network.IpPoolManagement;
import com.abiquo.server.core.infrastructure.network.IpsPoolManagementDto;
import com.abiquo.server.core.task.Task;
import com.abiquo.server.core.task.TasksDto;

@Parent(VirtualAppliancesResource.class)
@Path(VirtualApplianceResource.VIRTUAL_APPLIANCE_PARAM)
@Controller
public class VirtualApplianceResource extends AbstractResource
{
    public static final String VIRTUAL_APPLIANCE = "virtualappliance";

    public static final String VIRTUAL_APPLIANCE_PARAM = "{" + VIRTUAL_APPLIANCE + "}";

    public static final String VIRTUAL_APPLIANCE_GET_IPS_PATH = "action/ips";

    public static final String VIRTUAL_APPLIANCE_DEPLOY_PATH = "action/deploy";

    public static final String VIRTUAL_APPLIANCE_DEPLOY_REL = "deploy";

    public static final String VIRTUAL_APPLIANCE_UNDEPLOY_PATH = "action/undeploy";

    public static final String VIRTUAL_APPLIANCE_UNDEPLOY_REL = "undeploy";

    public static final String VIRTUAL_APPLIANCE_PRICE_PATH = "action/price";

    public static final String VIRTUAL_APPLIANCE_PRICE_REL = "price";

    public static final String VIRTUAL_APPLIANCE_STATE_REL = "state";

    public static final String VIRTUAL_APPLIANCE_FORCE_DELETE_PARAM = "force";

    public static final String VIRTUAL_APPLIANCE_ACTION_PRICE = "/action/price";

    @Autowired
    private VirtualApplianceService service;

    @Autowired
    private NetworkService netService;

    @Autowired
    private VirtualMachineLock vmLock;

    /**
     * Return the virtual appliance if exists. And also the expanded nodes. <br>
     * <ul>
     * <li>Last Task: returns the last {@link Task} of every node
     * </ul>
     * 
     * @title Retrieve a virtual appliance
     * @param vdcId identifier of the virtual datacenter.
     * @param vappId identifier of the virtual appliance.
     * @param restBuilder to build the links
     * @return the {@link VirtualApplianceDto} transfer object for the virtual appliance.
     * @throws Exception
     */
    @GET
    @Produces(VirtualApplianceDto.MEDIA_TYPE)
    public VirtualApplianceDto getVirtualAppliance(
        @PathParam(VirtualDatacenterResource.VIRTUAL_DATACENTER) final Integer vdcId,
        @PathParam(VirtualApplianceResource.VIRTUAL_APPLIANCE) final Integer vappId,
        @QueryParam(value = "expand") final String expand, @Context final IRESTBuilder restBuilder,
        @Context final UriInfo uriInfo) throws Exception
    {
        VirtualAppliance vapp = service.getVirtualAppliance(vdcId, vappId);
        VirtualApplianceDto dto = createTransferObject(vapp, restBuilder);

        expandNodes(vdcId, vappId, expand, uriInfo, dto);

        return dto;
    }

    private void expandNodes(final Integer vdcId, final Integer vappId, final String expand,
        final UriInfo uriInfo, final VirtualApplianceDto dto)
    {
        String[] expands = StringUtils.split(expand, ",");
        if (expands != null)
        {
            for (String e : expands)
            {
                if ("last_task".equalsIgnoreCase(e))
                {
                    List<Task> lastTasks = service.getAllNodesLastTask(vdcId, vappId);
                    if (lastTasks != null && !lastTasks.isEmpty())
                    {
                        TasksDto t = TaskResourceUtils.transform(lastTasks, uriInfo);
                        dto.setLastTasks(t);
                    }
                }
            }
        }
    }

    /**
     * Modifies a virtual appliance
     * 
     * @title Modify a virtual appliance
     * @param vdcId identifier of the virtual datacenter
     * @param vappId identifier of the virtual appliance
     * @param dto virtual appliance to modify
     * @param restBuilder a Context-injected object to create the links of the Dto
     * @return a {VirtualApplianceDto} with the modified virtual appliance
     * @throws Exception
     */
    @PUT
    @Consumes(VirtualApplianceDto.MEDIA_TYPE)
    @Produces(VirtualApplianceDto.MEDIA_TYPE)
    public VirtualApplianceDto updateVirtualAppliance(
        @PathParam(VirtualDatacenterResource.VIRTUAL_DATACENTER) final Integer vdcId,
        @PathParam(VirtualApplianceResource.VIRTUAL_APPLIANCE) final Integer vappId,
        final VirtualApplianceDto dto, @Context final IRESTBuilder restBuilder) throws Exception
    {
        VirtualAppliance vapp = service.updateVirtualAppliance(vdcId, vappId, dto);

        return createTransferObject(vapp, restBuilder);
    }

    /**
     * Returns all IPs from a vrtual appliance
     * 
     * @title Retrive all IPs
     * @param vdcId identifier of the virtual datacenter
     * @param vappId identifier of the virtual appliance
     * @param restBuilder a Context-injected object to create the links of the Dto
     * @return a {IpsPollManagementDto} with all IPs of the virtual appliance
     * @throws Exception
     */
    @GET
    @Path(VirtualApplianceResource.VIRTUAL_APPLIANCE_GET_IPS_PATH)
    @Produces(IpsPoolManagementDto.MEDIA_TYPE)
    public IpsPoolManagementDto getIPsByVirtualAppliance(
        @PathParam(VirtualDatacenterResource.VIRTUAL_DATACENTER) final Integer vdcId,
        @PathParam(VirtualApplianceResource.VIRTUAL_APPLIANCE) final Integer vappId,
        @Context final IRESTBuilder restBuilder) throws Exception
    {
        VirtualAppliance vapp = service.getVirtualAppliance(vdcId, vappId);

        // Get the list of ipPoolManagements objects
        List<IpPoolManagement> all = netService.getListIpPoolManagementByVirtualApp(vapp);
        IpsPoolManagementDto ips = new IpsPoolManagementDto();
        for (IpPoolManagement ip : all)
        {
            ips.add(IpAddressesResource.createTransferObject(ip, restBuilder));
        }

        return ips;
    }

    /**
     * Return the {@link VirtualApplianceDto} object from the POJO {@link VirtualAppliance}
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
        final VirtualMachineTemplateDto vmtemplate)
    {
        /**
         * TODO
         */
    }

    /**
     * Returns the state of the virtual appliance
     * 
     * @title Retrieve the state of the virtual appliance
     * @param vdcId identifier of the virtual datacenter
     * @param vappId identifier of the virtual appliance
     * @param restBuilder a Context-injected object to create the links of the Dto
     * @return a {VirtualAplianceStateDto} object with the state of the virtual appliance
     * @throws Exception
     */
    @GET
    @Path(VIRTUAL_APPLIANCE_STATE_REL)
    @Produces(VirtualApplianceStateDto.MEDIA_TYPE)
    public VirtualApplianceStateDto getChangeState(
        @PathParam(VirtualDatacenterResource.VIRTUAL_DATACENTER) final Integer vdcId,
        @PathParam(VirtualApplianceResource.VIRTUAL_APPLIANCE) final Integer vappId,
        @Context final IRESTBuilder restBuilder) throws Exception
    {
        VirtualApplianceState state = service.getVirtualApplianceState(vdcId, vappId);
        VirtualApplianceStateDto dto =
            virtualApplianceStateToDto(vdcId, vappId, restBuilder, state);
        return dto;

    }

    /**
     * Start a deploy task for all virtual machines of a virtual appliances
     * 
     * @title Deploy a virtual appliance
     * @wiki Deploys all the virtual machines in the virtual appliance with the given options. This
     *       call returns a 202 HTTP code (accepted) and a list of URIs where you can track each
     *       deploy (one for each virtual machine in the virtual appliance). The options are yet to
     *       be finalized. For the purposes of this example we will be using:
     *       -forceEnterpriseSoftLimits = OFF
     * @param vdcId identifier of the virtual datacenter
     * @param vappId identifier of the virtual appliance
     * @param taskOptions options of the task
     * @param restBuilder a Context-injected object to create the links of the Dto
     * @param uriInfo
     * @return a {AcceptedRequestDto} object
     * @throws Exception
     */
    @POST
    @Path(VIRTUAL_APPLIANCE_DEPLOY_PATH)
    @Consumes(VirtualMachineTaskDto.MEDIA_TYPE)
    @Produces(AcceptedRequestDto.MEDIA_TYPE)
    public AcceptedRequestDto<String> deploy(
        @PathParam(VirtualDatacenterResource.VIRTUAL_DATACENTER) final Integer vdcId,
        @PathParam(VirtualApplianceResource.VIRTUAL_APPLIANCE) final Integer vappId,
        final VirtualMachineTaskDto taskOptions, @Context final IRESTBuilder restBuilder,
        @Context final UriInfo uriInfo) throws Exception
    {
        AcceptedRequestDto<String> dto = new AcceptedRequestDto<String>();

        // by default force limits
        final boolean forceLimits =
            taskOptions != null ? taskOptions.isForceEnterpriseSoftLimits() : true;

        // Lock all virtual machines in the vapp
        Map<Integer, VirtualMachineState> originalStates =
            vmLock.lockVirtualApplianceBeforeDeploying(vdcId, vappId);

        final String lockMsg = "Allocate vapp " + vappId;
        try
        {
            SchedulerLock.acquire(lockMsg);

            Map<Integer, String> links = service.deployVirtualAppliance(vdcId, vappId, forceLimits);
            addStatusLinks(links, dto, uriInfo);
        }
        catch (Exception ex)
        {
            // Make sure all virtual machines are unlocked if deploy fails
            vmLock.unlockVirtualMachines(originalStates);
            throw ex;
        }
        finally
        {
            SchedulerLock.release(lockMsg);
        }

        return dto;
    }

    /**
     * Start an undeploy task for all virtual machines of a virtual appliances
     * 
     * @title Uneploy a virtual appliance
     * @wiki Perform an undeploy of all the machines that are DEPLOYED (meaning that they exist in
     *       the hypervisor). This means that after the call, all of the virtual machines in that
     *       virtual appliance in Abiquo will be NOT_ALLOCATED. If an undeploy is successful, all
     *       the virtual machines in that virtual appliance will be deleted from the hypervisor. If
     *       any of the virtual machines are in the ON state, Abiquo will perform a power off before
     *       deconfiguring them. You can also set the force undeploy parameter in the virtual
     *       machine task entity. If this is set to true, it will also delete the imported virtual
     *       machines. This call returns a 202 HTTP code (accepted) and a list of URIs where you can
     *       track each undeploy (one for each virtual machine in the virtual appliance).
     * @param vdcId identifier of the virtual datacenter
     * @param vappId identifier of the virtual appliance
     * @param taskOptions options of the task
     * @param restBuilder a Context-injected object to create the links of the Dto
     * @param uriInfo
     * @return a {AcceptedRequestDto}
     * @throws Exception
     */
    @POST
    @Path(VIRTUAL_APPLIANCE_UNDEPLOY_PATH)
    @Consumes(VirtualMachineTaskDto.MEDIA_TYPE)
    @Produces(AcceptedRequestDto.MEDIA_TYPE)
    public AcceptedRequestDto<String> undeploy(
        @PathParam(VirtualDatacenterResource.VIRTUAL_DATACENTER) final Integer vdcId,
        @PathParam(VirtualApplianceResource.VIRTUAL_APPLIANCE) final Integer vappId,
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

        // Lock all virtual machines in the vapp
        Map<Integer, VirtualMachineState> originalStates =
            vmLock.lockVirtualApplianceBeforeUndeploying(vdcId, vappId);

        try
        {
            Map<Integer, String> links =
                service.undeployVirtualAppliance(vdcId, vappId, forceUndeploy, originalStates);
            AcceptedRequestDto<String> dto = new AcceptedRequestDto<String>();
            addStatusLinks(links, dto, uriInfo);
            return dto;
        }
        catch (Exception ex)
        {
            // Make sure all virtual machines are unlocked if deploy fails
            vmLock.unlockVirtualMachines(originalStates);
            throw ex;
        }
    }

    private VirtualApplianceStateDto virtualApplianceStateToDto(final Integer vdcId,
        final Integer vappId, final IRESTBuilder restBuilder, final VirtualApplianceState state)
    {
        VirtualApplianceStateDto dto = new VirtualApplianceStateDto();
        dto.setPower(state);
        dto.addLinks(restBuilder.buildVirtualApplianceStateLinks(dto, vappId, vdcId));
        return dto;
    }

    private void addStatusLinks(final Map<Integer, String> links,
        final AcceptedRequestDto<String> dto, final UriInfo uriInfo)
    {
        for (Entry<Integer, String> e : links.entrySet())
        {
            Integer vmId = e.getKey();
            String url = e.getValue();
            RESTLink link = buildTaskLink(vmId, url, uriInfo);
            dto.addLink(link);
        }
    }

    /**
     * Delete the virtual appliance if exists.
     * 
     * @title Delete a virtual appliance
     * @wiki This deletes a Virtual Appliance
     * @param vdcId identifier of the virtual datacenter.
     * @param vappId identifier of the virtual appliance.
     * @param restBuilder to build the links
     * @throws Exception
     */
    @DELETE
    public void deleteVirtualAppliance(
        @PathParam(VirtualDatacenterResource.VIRTUAL_DATACENTER) final Integer vdcId,
        @PathParam(VirtualApplianceResource.VIRTUAL_APPLIANCE) final Integer vappId)
        throws Exception
    {
        // Lock all virtual machines in the vapp
        Map<Integer, VirtualMachineState> originalStates =
            vmLock.lockVirtualApplianceBeforeDeleting(vdcId, vappId);

        try
        {
            service.deleteVirtualAppliance(vdcId, vappId, originalStates);

            // TODO: Should return a task list if the vapp is going to be undeployed?
        }
        catch (Exception ex)
        {
            // Make sure all virtual machines are unlocked if deploy fails
            vmLock.unlockVirtualMachines(originalStates);
            throw ex;
        }
    }

    /**
     * Returns a message (String) with the price info of the virtual appliance
     * 
     * @title Retrieve the price message
     * @param vdcId identifier of the virtual datacenter
     * @param vappId identifier of the virtual appliance
     * @param restBuilder a Context-injected object to create the links of the Dto
     * @return a {String} with the price message of the virtual appliance
     * @throws Exception
     */
    @GET
    @Path(VIRTUAL_APPLIANCE_PRICE_PATH)
    @Produces(MediaType.TEXT_PLAIN)
    public String getPriceVirtualAppliance(
        @PathParam(VirtualDatacenterResource.VIRTUAL_DATACENTER) final Integer vdcId,
        @PathParam(VirtualApplianceResource.VIRTUAL_APPLIANCE) final Integer vappId,
        @Context final IRESTBuilder restBuilder) throws Exception
    {
        // VirtualAppliancePriceDto virtualAppliancePriceDto =
        // service.getPriceVirtualAppliance(vdcId, vappId);
        // return virtualAppliancePriceDto;
        String virtualAppliancePrice = service.getPriceVirtualApplianceText(vdcId, vappId);
        return virtualAppliancePrice;
    }

    protected RESTLink buildTaskLink(final Integer vmId, final String taskId, final UriInfo uriInfo)
    {
        // Build task link
        String link = uriInfo.getRequestUri().toString();

        link = link.replaceAll("action.*", "");
        link = link.replaceAll("(/)*$", "");
        link =
            link.concat("/").concat(VirtualMachinesResource.VIRTUAL_MACHINES_PATH).concat("/")
                .concat(String.valueOf(vmId)).concat(TaskResourceUtils.TASKS_PATH).concat("/")
                .concat(taskId);

        return new RESTLink("status", link);
    }
}
