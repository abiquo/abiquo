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

package com.abiquo.abiserver.commands.stub.impl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.ws.rs.core.Response.Status;

import org.apache.commons.lang.StringUtils;
import org.apache.wink.client.ClientResponse;
import org.apache.wink.common.http.HttpStatus;
import org.jclouds.abiquo.domain.DomainWrapper;
import org.jclouds.abiquo.domain.cloud.VirtualDatacenter;
import org.jclouds.abiquo.domain.exception.AbiquoException;
import org.jclouds.rest.AuthorizationException;

import com.abiquo.abiserver.abicloudws.AbiCloudConstants;
import com.abiquo.abiserver.business.UserSessionException;
import com.abiquo.abiserver.business.hibernate.pojohb.infrastructure.StateEnum;
import com.abiquo.abiserver.business.hibernate.pojohb.virtualappliance.VirtualmachineHB;
import com.abiquo.abiserver.business.hibernate.pojohb.virtualhardware.ResourceManagementHB;
import com.abiquo.abiserver.commands.BasicCommand;
import com.abiquo.abiserver.commands.stub.AbstractAPIStub;
import com.abiquo.abiserver.commands.stub.VirtualApplianceResourceStub;
import com.abiquo.abiserver.exception.VirtualApplianceCommandException;
import com.abiquo.abiserver.pojo.authentication.UserSession;
import com.abiquo.abiserver.pojo.infrastructure.HyperVisor;
import com.abiquo.abiserver.pojo.infrastructure.HyperVisorType;
import com.abiquo.abiserver.pojo.infrastructure.State;
import com.abiquo.abiserver.pojo.infrastructure.VirtualMachine;
import com.abiquo.abiserver.pojo.networking.Network;
import com.abiquo.abiserver.pojo.result.BasicResult;
import com.abiquo.abiserver.pojo.result.DataResult;
import com.abiquo.abiserver.pojo.user.Enterprise;
import com.abiquo.abiserver.pojo.user.User;
import com.abiquo.abiserver.pojo.virtualappliance.Node;
import com.abiquo.abiserver.pojo.virtualappliance.NodeVirtualImage;
import com.abiquo.abiserver.pojo.virtualappliance.TaskStatus;
import com.abiquo.abiserver.pojo.virtualappliance.VirtualAppliance;
import com.abiquo.abiserver.pojo.virtualappliance.VirtualDataCenter;
import com.abiquo.abiserver.pojo.virtualhardware.ResourceAllocationLimit;
import com.abiquo.abiserver.pojo.virtualimage.Category;
import com.abiquo.abiserver.pojo.virtualimage.Icon;
import com.abiquo.abiserver.pojo.virtualimage.VirtualImage;
import com.abiquo.model.enumerator.DiskFormatType;
import com.abiquo.model.enumerator.HypervisorType;
import com.abiquo.model.rest.RESTLink;
import com.abiquo.model.transport.AcceptedRequestDto;
import com.abiquo.model.transport.error.ErrorsDto;
import com.abiquo.server.core.appslibrary.VirtualMachineTemplateDto;
import com.abiquo.server.core.cloud.VirtualApplianceDto;
import com.abiquo.server.core.cloud.VirtualApplianceState;
import com.abiquo.server.core.cloud.VirtualAppliancesDto;
import com.abiquo.server.core.cloud.VirtualDatacenterDto;
import com.abiquo.server.core.cloud.VirtualDatacentersDto;
import com.abiquo.server.core.cloud.VirtualMachineDto;
import com.abiquo.server.core.cloud.VirtualMachineInstanceDto;
import com.abiquo.server.core.cloud.VirtualMachineState;
import com.abiquo.server.core.cloud.VirtualMachineTaskDto;
import com.abiquo.server.core.cloud.VirtualMachineWithNodeDto;
import com.abiquo.server.core.cloud.VirtualMachineWithNodeExtendedDto;
import com.abiquo.server.core.cloud.VirtualMachinesWithNodeExtendedDto;
import com.abiquo.server.core.enterprise.EnterpriseDto;
import com.abiquo.server.core.enterprise.User.AuthType;
import com.abiquo.server.core.enterprise.UserDto;
import com.abiquo.server.core.infrastructure.network.VLANNetworkDto;
import com.abiquo.server.core.task.Job;
import com.abiquo.server.core.task.JobDto;
import com.abiquo.server.core.task.JobsDto;
import com.abiquo.server.core.task.Task;
import com.abiquo.server.core.task.TaskDto;
import com.abiquo.server.core.task.TasksDto;
import com.abiquo.util.ErrorManager;
import com.abiquo.util.URIResolver;
import com.abiquo.util.resources.ResourceManager;

/**
 * @author jdevesa
 */
public class VirtualApplianceResourceStubImpl extends AbstractAPIStub implements
    VirtualApplianceResourceStub
{

    public static String VM_NODE_MEDIA_TYPE = "application/vnd.vm-node+xml"; // TODO:

    // move to
    // VirtualMachineResrouceStub?

    public VirtualApplianceResourceStubImpl()
    {
    }

    /**
     * @see com.abiquo.abiserver.commands.stub.VirtualApplianceResourceStub#deployVirtualAppliance(java.lang.Integer,
     *      java.lang.Integer, java.lang.Boolean)
     */
    @Override
    public DataResult deployVirtualAppliance(final Integer virtualDatacenterId,
        final Integer virtualApplianceId, final Boolean forceEnterpriseSoftLimits)
    {
        DataResult result = new DataResult();
        String link = createVirtualApplianceDeployLink(virtualDatacenterId, virtualApplianceId);

        VirtualMachineTaskDto options = new VirtualMachineTaskDto();
        options.setForceEnterpriseSoftLimits(forceEnterpriseSoftLimits);

        ClientResponse response = post(link, options);

        if (response.getStatusCode() == 202)
        {
            result.setSuccess(Boolean.TRUE);
            AcceptedRequestDto entity = response.getEntity(AcceptedRequestDto.class);
            result.setData(entity.getLinks());

            return this.getVirtualApplianceNodes(virtualDatacenterId, virtualApplianceId,
                "deployVirtualAppliance");
        }
        else
        {
            populateErrors(response, result, "deployVirtualAppliance");
        }

        return result;
    }

    /**
     * We create all the machines that has no id. Then update the app.
     * 
     * @see com.abiquo.abiserver.commands.stub.VirtualApplianceResourceStub#updateVirtualApplianceNodes(java.lang.Integer,
     *      java.lang.Integer)
     */
    @Override
    public DataResult<VirtualAppliance> updateVirtualApplianceNodes(
        final Integer virtualDatacenterId, final VirtualAppliance virtualAppliance)
    {
        DataResult<VirtualAppliance> result = new DataResult<VirtualAppliance>();
        result.setSuccess(Boolean.TRUE);
        String linkVirtualMachines =
            createVirtualMachinesUrl(virtualDatacenterId, virtualAppliance.getId());

        StringBuilder errors = new StringBuilder();
        for (Node m : virtualAppliance.getNodes())
        {
            if (m instanceof NodeVirtualImage)
            {
                NodeVirtualImage n = (NodeVirtualImage) m;

                switch (m.getModified())
                {
                    case Node.NODE_ERASED:
                    {

                        if (n.getVirtualMachine() == null || n.getVirtualMachine().getId() == null)
                        {
                            result.setSuccess(Boolean.FALSE);
                            errors.append("The Virtual Image is null");
                        }
                        String linkVirtualMachine =
                            createVirtualMachineUrl(virtualDatacenterId, virtualAppliance.getId(),
                                n.getVirtualMachine().getId());
                        ClientResponse delete = delete(linkVirtualMachine);
                        if (delete.getStatusCode() != 204)
                        {
                            addErrors(result, errors, delete, "updateVirtualApplianceNodes");
                            result.setSuccess(Boolean.FALSE);

                            result.setMessage(errors.toString());
                        }
                        break;
                    }
                    case Node.NODE_CRASHED:
                    {
                        break;
                    }
                    case Node.NODE_NEW:
                    {
                        if (n.getVirtualMachine() != null)
                        {
                            result.setSuccess(Boolean.FALSE);
                            errors.append("The Virtual Image is null or exists in DB");
                        }
                        VirtualMachine machine = createEmptyVirtualMachine(n);
                        VirtualMachineWithNodeDto virtualMachineDto =
                            virtualImageNodeToDto(virtualAppliance, machine, n,
                                virtualDatacenterId, virtualAppliance.getVirtualDataCenter()
                                    .getIdDataCenter());
                        ClientResponse post =
                            post(linkVirtualMachines, virtualMachineDto, VM_NODE_MEDIA_TYPE);
                        if (post.getStatusCode() != Status.CREATED.getStatusCode())
                        {
                            errors.append(n.getVirtualImage().getName());
                            addErrors(result, errors, post, "updateVirtualApplianceNodes");
                            result.setSuccess(Boolean.FALSE);

                            result.setMessage(errors.toString());
                        }
                        break;
                    }
                    case Node.NODE_MODIFIED:
                    {
                        // We should only update DB without sending a reconfigure operation
                        VirtualMachineWithNodeDto virtualMachineDto =
                            virtualImageNodeToDto(virtualAppliance, n.getVirtualMachine(), n,
                                virtualDatacenterId, virtualAppliance.getVirtualDataCenter()
                                    .getIdDataCenter());

                        String linkVirtualMachine =
                            createVirtualMachineUrl(virtualDatacenterId, virtualAppliance.getId(),
                                n.getVirtualMachine().getId());

                        ClientResponse put =
                            put(linkVirtualMachine, virtualMachineDto, VM_NODE_MEDIA_TYPE);
                        if (put.getStatusCode() != Status.OK.getStatusCode()
                            && put.getStatusCode() != Status.NO_CONTENT.getStatusCode())
                        {
                            addErrors(result, errors, put, "updateVirtualApplianceNodes");
                            result.setSuccess(Boolean.FALSE);

                            result.setMessage(errors.toString());
                        }
                        break;
                    }
                    case Node.NODE_NOT_MODIFIED:
                    {
                        break;
                    }
                    default:
                }
            }
        }
        String linkApp = createVirtualApplianceUrl(virtualDatacenterId, virtualAppliance.getId());

        VirtualApplianceDto appDto = virtualApplianceToDto(virtualAppliance);
        ClientResponse put = put(linkApp, appDto);
        if (put.getStatusCode() != Status.OK.getStatusCode())
        {
            addErrors(result, errors, put, "updateVirtualApplianceNodes");
        }

        ClientResponse response = get(linkApp);
        if (response.getStatusCode() == Status.OK.getStatusCode())
        {
            VirtualApplianceDto entity = response.getEntity(VirtualApplianceDto.class);
            try
            {
                VirtualAppliance app = dtoToVirtualAppliance(entity, virtualDatacenterId, result);
                result.setData(app);
            }
            catch (Exception e)
            {
                errors.append("\n").append(e.getMessage());
                result.setMessage(errors.toString());
            }
        }
        else
        {
            populateErrors(response, result, "updateVirtualApplianceNodes");
        }

        return result;
    }

    /**
     * We only update the name.
     * 
     * @param virtualAppliance
     * @return VirtualApplianceDto
     */
    private VirtualApplianceDto virtualApplianceToDto(final VirtualAppliance virtualAppliance)
    {
        VirtualApplianceDto dto = new VirtualApplianceDto();
        dto.setName(virtualAppliance.getName());
        return dto;
    }

    public VirtualMachine createEmptyVirtualMachine(final NodeVirtualImage nodeVIPojo)
    {
        VirtualMachine virtualMachine = new VirtualMachine();
        VirtualImage virtualImage = nodeVIPojo.getVirtualImage();
        virtualMachine.setState(new State(StateEnum.NOT_ALLOCATED));
        virtualMachine.setName(nodeVIPojo.getName());
        virtualMachine.setDescription(virtualImage.getDescription());
        virtualMachine.setRam(virtualImage.getRamRequired());
        virtualMachine.setCpu(virtualImage.getCpuRequired());
        virtualMachine.setHd(virtualImage.getHdRequired());
        virtualMachine.setVdrpPort(0);
        virtualMachine.setIdType(com.abiquo.server.core.cloud.VirtualMachine.MANAGED);
        virtualMachine.setConversion(null);

        return virtualMachine;
    }

    /**
     * @param virtualApplianceDto
     * @param virtualDatacenterId
     * @param result
     * @return VirtualAppliance
     */
    private VirtualAppliance dtoToVirtualAppliance(final VirtualApplianceDto virtualApplianceDto,
        final int virtualDatacenterId, final DataResult result)
    {
        VirtualAppliance app = new VirtualAppliance();

        app.setError(virtualApplianceDto.getError() == 1 ? Boolean.TRUE : Boolean.FALSE);
        app.setHighDisponibility(virtualApplianceDto.getHighDisponibility() == 1 ? Boolean.TRUE
            : Boolean.FALSE);
        app.setId(virtualApplianceDto.getId());
        app.setIsPublic(virtualApplianceDto.getPublicApp() == 1 ? Boolean.TRUE : Boolean.FALSE);
        app.setName(virtualApplianceDto.getName());
        app.setNodeConnections(virtualApplianceDto.getNodecollections());
        app.setState(new State(StateEnum.valueOf(virtualApplianceDto.getState().name())));
        app.setId(virtualApplianceDto.getId());
        Integer enterpriseId = virtualApplianceDto.getIdFromLink("enterprise");
        String eLink = createEnterpriseLink(enterpriseId);
        ClientResponse enterpriseResponse = get(eLink);
        if (enterpriseResponse.getStatusCode() == Status.OK.getStatusCode())
        {
            EnterpriseDto enterpriseDto = enterpriseResponse.getEntity(EnterpriseDto.class);
            Enterprise enterprise = dtoToEnterprise(enterpriseDto);
            app.setEnterprise(enterprise);
        }
        else
        {
            populateErrors(enterpriseResponse, result, "getEnterprise");
        }
        Integer vdcId = virtualApplianceDto.getIdFromLink("virtualdatacenter");
        String link = createVirtualDatacenterLink(vdcId);
        ClientResponse vdcResponse = get(link);
        if (vdcResponse.getStatusCode() == Status.OK.getStatusCode())
        {
            VirtualDatacenterDto dto = vdcResponse.getEntity(VirtualDatacenterDto.class);
            VirtualDataCenter virtualDataCenter = dtoToVirtualDatacenter(dto, app.getEnterprise());
            app.setVirtualDataCenter(virtualDataCenter);
        }
        else
        {
            populateErrors(enterpriseResponse, result, "getVirtualDatacenter");
        }
        return app;
    }

    private VirtualAppliance dtoToVirtualAppliance(final VirtualApplianceDto virtualApplianceDto,
        final VirtualDataCenter virtualDatacenter, final DataResult result)
    {
        VirtualAppliance app = new VirtualAppliance();

        app.setError(virtualApplianceDto.getError() == 1 ? Boolean.TRUE : Boolean.FALSE);
        app.setHighDisponibility(virtualApplianceDto.getHighDisponibility() == 1 ? Boolean.TRUE
            : Boolean.FALSE);
        app.setId(virtualApplianceDto.getId());
        app.setIsPublic(virtualApplianceDto.getPublicApp() == 1 ? Boolean.TRUE : Boolean.FALSE);
        app.setName(virtualApplianceDto.getName());
        app.setNodeConnections(virtualApplianceDto.getNodecollections());
        app.setState(new State(StateEnum.valueOf(virtualApplianceDto.getState().name())));
        app.setId(virtualApplianceDto.getId());
        Integer enterpriseId = virtualApplianceDto.getIdFromLink("enterprise");
        String eLink = createEnterpriseLink(enterpriseId);
        ClientResponse enterpriseResponse = get(eLink);
        if (enterpriseResponse.getStatusCode() == Status.OK.getStatusCode())
        {
            EnterpriseDto enterpriseDto = enterpriseResponse.getEntity(EnterpriseDto.class);
            Enterprise enterprise = dtoToEnterprise(enterpriseDto);
            app.setEnterprise(enterprise);
        }
        else
        {
            populateErrors(enterpriseResponse, result, "getEnterprise");
        }
        app.setVirtualDataCenter(virtualDatacenter);

        return app;
    }

    private VirtualDataCenter dtoToVirtualDatacenter(final VirtualDatacenterDto dto,
        final Enterprise enterprise)
    {
        VirtualDataCenter vdc = new VirtualDataCenter();

        VLANNetworkDto vlanDto = dto.getVlan();

        vdc.setDefaultVlan(NetworkResourceStubImpl.createFlexObject(vlanDto));

        vdc.setHyperType(HyperVisorType.create(
            dto.getHypervisorType(),
            new com.abiquo.abiserver.pojo.virtualimage.DiskFormatType(dto.getHypervisorType().baseFormat)));
        vdc.setEnterprise(enterprise);
        vdc.setId(dto.getId());
        vdc.setIdDataCenter(dto.getIdFromLink("datacenter"));
        vdc.setName(dto.getName());

        Network network = new Network();
        vdc.setNetwork(network);
        ResourceAllocationLimit limits = new ResourceAllocationLimit();

        vdc.setLimits(limits);
        return vdc;
    }

    private Enterprise dtoToEnterprise(final EnterpriseDto enterpriseDto)
    {
        Enterprise e = new Enterprise();
        e.setChefClient(enterpriseDto.getChefClient());
        e.setChefClientCertificate(enterpriseDto.getChefClientCertificate());
        e.setChefURL(enterpriseDto.getChefURL());
        e.setChefValidator(enterpriseDto.getChefValidator());
        e.setChefValidatorCertificate(enterpriseDto.getChefValidatorCertificate());
        e.setId(enterpriseDto.getId());
        e.setIsReservationRestricted(enterpriseDto.getIsReservationRestricted());
        e.setName(enterpriseDto.getName());
        return e;
    }

    private VirtualMachineDto virtualMachineToDto(final VirtualAppliance virtualAppliance,
        final VirtualMachine virtualMachine, final NodeVirtualImage node,
        final Integer virtualDatacenterId, final Integer datacenterId)
    {
        VirtualMachineDto dto = new VirtualMachineDto();
        dto.setCpu(virtualMachine.getCpu());
        dto.setDescription(virtualMachine.getDescription());
        dto.setHdInBytes(virtualMachine.getHd());
        dto.setHighDisponibility(virtualMachine.getHighDisponibility() ? 1 : 0);
        dto.setIdState(VirtualMachineState.NOT_ALLOCATED.id());
        // It belongs the this app
        dto.setIdType(com.abiquo.server.core.cloud.VirtualMachine.MANAGED);
        dto.setName(node.getName());
        dto.setPassword(virtualMachine.getPassword());
        dto.setRam(virtualMachine.getRam());
        dto.setState(VirtualMachineState.NOT_ALLOCATED);
        dto.setUuid(virtualMachine.getUUID());
        dto.addLink(new RESTLink("virtualmachinetemplate", createVirtualMachineTemplateLink(node
            .getVirtualImage().getIdEnterprise(), datacenterId, node.getVirtualImage().getId())));

        dto.addLink(new RESTLink("enterprise", createEnterpriseLink(virtualAppliance
            .getEnterprise().getId())));

        dto.addLink(new RESTLink("user", createUserLink(virtualAppliance.getEnterprise().getId(),
            currentSession.getUserIdDb())));
        return dto;
    }

    private VirtualMachineWithNodeDto virtualImageNodeToDto(
        final VirtualAppliance virtualAppliance, final VirtualMachine virtualMachine,
        final NodeVirtualImage node, final Integer virtualDatacenterId, final Integer datacenterId)
    {
        VirtualMachineWithNodeDto dto = new VirtualMachineWithNodeDto();
        dto.setCpu(virtualMachine.getCpu());
        dto.setDescription(virtualMachine.getDescription());
        dto.setHdInBytes(virtualMachine.getHd());
        dto.setHighDisponibility(virtualMachine.getHighDisponibility() ? 1 : 0);
        dto.setIdState(VirtualMachineState.NOT_ALLOCATED.id());
        // It belongs the this app
        dto.setIdType(com.abiquo.server.core.cloud.VirtualMachine.MANAGED);
        dto.setName(virtualMachine.getName());
        dto.setPassword(virtualMachine.getPassword());
        dto.setRam(virtualMachine.getRam());
        dto.setState(VirtualMachineState.NOT_ALLOCATED);
        dto.setUuid(virtualMachine.getUUID());

        dto.setNodeName(node.getName());

        dto.setX(node.getPosX());
        dto.setY(node.getPosY());

        dto.addLink(new RESTLink("virtualmachinetemplate", createVirtualMachineTemplateLink(node
            .getVirtualImage().getIdEnterprise(), datacenterId, node.getVirtualImage().getId())));

        dto.addLink(new RESTLink("enterprise", createEnterpriseLink(virtualAppliance
            .getEnterprise().getId())));

        dto.addLink(new RESTLink("user", createUserLink(virtualAppliance.getEnterprise().getId(),
            currentSession.getUserIdDb())));
        return dto;
    }

    private void addErrors(final DataResult result, final StringBuilder errors,
        final ClientResponse response, final String method)
    {
        if (response.getStatusCode() == 401 || response.getStatusCode() == 403)
        {
            ErrorManager.getInstance(AbiCloudConstants.ERROR_PREFIX).reportError(
                new ResourceManager(BasicCommand.class), result,
                "onFaultAuthorization.noPermission", method);
            result.setMessage(response.getMessage());
            result.setResultCode(BasicResult.NOT_AUTHORIZED);
            throw new UserSessionException(result);
        }
        Object entity = response.getEntity(Object.class);
        if (entity instanceof ErrorsDto)
        {
            ErrorsDto error = (ErrorsDto) entity;
            errors.append("\n").append(error.toString());
            if (error.getCollection().get(0).getCode().equals("LIMIT_EXCEEDED"))
            {
                result.setResultCode(BasicResult.HARD_LIMT_EXCEEDED);
            }
        }
        else
        {
            errors.append("\n").append(response.getEntity(String.class));
        }
    }

    @Override
    public DataResult<VirtualAppliance> getVirtualApplianceNodes(final Integer virtualDatacenterId,
        final Integer virtualApplianceId, final String methodName)
    {
        DataResult result = new DataResult();
        String link = createVirtualApplianceUrl(virtualDatacenterId, virtualApplianceId);

        ClientResponse response = get(link);

        if (response.getStatusCode() == Status.OK.getStatusCode())
        {
            try
            {
                result.setSuccess(Boolean.TRUE);
                VirtualApplianceDto entity = response.getEntity(VirtualApplianceDto.class);

                VirtualAppliance app = dtoToVirtualAppliance(entity, virtualDatacenterId, result);
                RESTLink virtualMachines = entity.searchLink("virtualmachines");

                if (virtualMachines != null)
                {
                    DataResult<List<Node>> nodeVirtualImages =
                        getAppNodes(virtualMachines.getHref());
                    app.setNodes(nodeVirtualImages.getData());
                }

                result.setData(app);
            }
            catch (Exception e)
            {
                populateErrors(response, result, methodName);
            }
        }
        else
        {
            populateErrors(response, result, methodName);
        }

        return result;
    }

    private DataResult<List<Node>> getAppNodes(final String virtualMachinesLink)
    {
        List<Node> nodeVirtualImages = new ArrayList<Node>();
        final DataResult<List<Node>> result = new DataResult<List<Node>>();
        result.setSuccess(Boolean.TRUE);
        ClientResponse machinesResponse =
            get(virtualMachinesLink, "application/vnd.vm-node-extended+xml"); // application/vnd.vm-node+xml
        if (machinesResponse.getStatusCode() == Status.OK.getStatusCode())
        {
            VirtualMachinesWithNodeExtendedDto virtualMachinesWithNodeDto =
                machinesResponse.getEntity(VirtualMachinesWithNodeExtendedDto.class);

            try
            {
                nodeVirtualImages.addAll(createNodeVirtualImages(virtualMachinesWithNodeDto));
            }
            catch (Exception ex)
            {
                populateErrors(ex, result, "getVirtualApplianceNodes");
            }
            finally
            {
                releaseApiClient();
            }
        }
        else
        {
            populateErrors(machinesResponse, result, "getVirtualApplianceNodes");
        }
        result.setData(nodeVirtualImages);
        return result;
    }

    @Override
    public DataResult<List<Node>> getAppNodes(final VirtualAppliance entity)
    {
        String link =
            createVirtualApplianceMachinesUrl(entity.getVirtualDataCenter().getId(), entity.getId());
        return getAppNodes(link);
    }

    private List<NodeVirtualImage> createNodeVirtualImages(
        final VirtualMachinesWithNodeExtendedDto virtualMachinesDto)
    {
        List<NodeVirtualImage> nodeVirtualImages = new ArrayList<NodeVirtualImage>();
        for (VirtualMachineWithNodeExtendedDto dto : virtualMachinesDto.getCollection())
        {
            VirtualMachine virtualMachine = dtoToVirtualMachine(dto);
            NodeVirtualImage nodeVirtualImage = new NodeVirtualImage();
            nodeVirtualImage.setName(dto.getNodeName());
            nodeVirtualImage.setPosX(dto.getX());
            nodeVirtualImage.setPosY(dto.getY());
            nodeVirtualImage.setId(dto.getNodeId());
            nodeVirtualImage.setVirtualMachine(virtualMachine);
            RESTLink virtualImage = dto.searchLink("virtualmachinetemplate");
            if (virtualImage != null)
            {
                ClientResponse imageResponse = get(virtualImage.getHref());
                if (imageResponse.getStatusCode() == Status.OK.getStatusCode())
                {

                    VirtualMachineTemplateDto virtualImageDto =
                        imageResponse.getEntity(VirtualMachineTemplateDto.class);
                    VirtualImage image = dtoToVirtualImage(virtualImageDto);
                    nodeVirtualImage.setVirtualImage(image);
                    virtualMachine.setVirtualImage(image);
                }
                else
                {
                    populateErrors(imageResponse, new BasicResult(), "getVirtualImage");
                }
            }

            TaskStatus currentTask = new TaskStatus();

            TasksDto tasks = getApiClient().getApi().getTaskClient().listTasks(dto);
            if (tasks != null && !tasks.isEmpty())
            {
                TaskDto lastTask = tasks.getCollection().get(0);
                currentTask.addUri(lastTask.searchLink("self").getHref());
                currentTask.addTask(dtoToTask(lastTask));
            }

            nodeVirtualImage.setTaskStatus(currentTask);
            nodeVirtualImages.add(nodeVirtualImage);

        }

        return nodeVirtualImages;
    }

    private Task dtoToTask(final TaskDto dto)
    {
        Task task = new Task();

        task.setOwnerId(dto.getOwnerId());
        task.setState(dto.getState());
        task.setTaskId(dto.getTaskId());
        task.setTimestamp(dto.getTimestamp());
        task.setType(dto.getType());
        task.setUserId(dto.getUserId());
        task.getJobs().addAll(dtosToJob(dto.getJobs()));

        return task;
    }

    private List<Job> dtosToJob(final JobsDto dtos)
    {
        List<Job> jobs = new ArrayList<Job>();
        for (JobDto dto : dtos.getCollection())
        {
            Job job = dtoToJob(dto);
            jobs.add(job);
        }
        return jobs;
    }

    private Job dtoToJob(final JobDto dto)
    {
        Job job = new Job();
        job.setId(dto.getId());
        job.setParentTaskId(dto.getParentTaskId());
        job.setRollbackState(dto.getRollbackState());
        job.setState(dto.getState());
        job.setTimestamp(dto.getTimestamp());
        job.setType(dto.getType());
        return job;
    }

    private VirtualImage dtoToVirtualImage(final VirtualMachineTemplateDto virtualImageDto)
    {
        VirtualImage image = new VirtualImage();
        image.setChefEnabled(virtualImageDto.isChefEnabled());
        image.setCostCode(virtualImageDto.getCostCode());
        image.setCpuRequired(virtualImageDto.getCpuRequired());
        image.setCreationDate(virtualImageDto.getCreationDate());
        image.setCreationUser(virtualImageDto.getCreationUser());
        image.setDescription(virtualImageDto.getDescription());
        image.setDiskFileSize(virtualImageDto.getDiskFileSize());
        image
            .setDiskFormatType(new com.abiquo.abiserver.pojo.virtualimage.DiskFormatType(DiskFormatType
                .fromValue(virtualImageDto.getDiskFormatType())));
        image.setHdRequired(virtualImageDto.getHdRequired());
        image.setId(virtualImageDto.getId());
        image.setName(virtualImageDto.getName());
        image.setPath(virtualImageDto.getPath());
        image.setRamRequired(virtualImageDto.getRamRequired());
        image.setShared(virtualImageDto.isShared());

        // Image is stateful if it is linked to a volume
        image.setStateful(virtualImageDto.searchLink("volume") != null);

        // Captured images may not have a category
        RESTLink categoryLink = virtualImageDto.searchLink("category");
        if (categoryLink != null)
        {
            Category category = new Category();
            category.setId(Integer.parseInt(getIdFromLink(categoryLink)));
            category.setName(categoryLink.getTitle());
            image.setCategory(category);
        }

        // Captured images may not have an icon
        RESTLink iconlLink = virtualImageDto.searchLink("icon");
        if (iconlLink != null)
        {
            Icon icon = new Icon();
            icon.setPath(iconlLink.getTitle());
            image.setIcon(icon);
        }

        image.setIdEnterprise(virtualImageDto.getIdFromLink("enterprise"));

        // Captured images may not have a template definition
        RESTLink templateDefinitionLink = virtualImageDto.searchLink("templatedefinition");
        if (templateDefinitionLink != null)
        {
            image.setOvfId(templateDefinitionLink.getHref());
        }

        return image;

    }

    private VirtualMachine dtoToVirtualMachine(
        final VirtualMachineWithNodeExtendedDto virtualMachineDto)
    {
        VirtualMachine vm = new VirtualMachine();
        vm.setCpu(virtualMachineDto.getCpu());
        vm.setDescription(virtualMachineDto.getDescription());
        vm.setHd(virtualMachineDto.getHdInBytes());
        vm.setId(virtualMachineDto.getId());
        vm.setIdType(virtualMachineDto.getIdType());
        vm.setName(virtualMachineDto.getName());
        vm.setPassword(virtualMachineDto.getPassword());
        vm.setRam(virtualMachineDto.getRam());
        vm.setState(new State(StateEnum.valueOf(virtualMachineDto.getState().name())));
        vm.setUUID(virtualMachineDto.getUuid());
        vm.setVdrpIP(virtualMachineDto.getVdrpIP());
        vm.setVdrpPort(virtualMachineDto.getVdrpPort());

        // Build the hypervisor with the information available.
        // It will only be used to check the type.
        RESTLink vdcLink = virtualMachineDto.searchLink("virtualdatacenter");
        HyperVisor hypervisor = new HyperVisor();
        hypervisor.setType(new HyperVisorType(HypervisorType.valueOf(vdcLink.getTitle())));
        vm.setAssignedTo(hypervisor);

        RESTLink entLink = virtualMachineDto.searchLink("enterprise");
        if (entLink != null)
        {
            ClientResponse entResponse = get(entLink.getHref());
            if (entResponse.getStatusCode() == Status.OK.getStatusCode())
            {

                EnterpriseDto entDto = entResponse.getEntity(EnterpriseDto.class);
                Enterprise ent = dtoToEnterprise(entDto);
                vm.setEnterprise(ent);
            }
            else
            {
                populateErrors(entResponse, new BasicResult(), "getUser");
            }

        }

        UserDto userDto =
            new UserDto(virtualMachineDto.getUserName(),
                virtualMachineDto.getUserSurname(),
                null,
                null,
                null,
                null,
                null,
                AuthType.ABIQUO.name());
        User user = dtoToUser(userDto);
        Enterprise ent = new Enterprise();
        ent.setName(virtualMachineDto.getEnterpriseName());
        user.setEnterprise(ent);
        vm.setUser(user);

        return vm;
    }

    private User dtoToUser(final UserDto userDto)
    {
        User u = new User();

        u.setId(userDto.getId());
        u.setEmail(userDto.getEmail());
        u.setLocale(userDto.getLocale());
        u.setName(userDto.getName());
        u.setSurname(userDto.getSurname());
        u.setUser(userDto.getNick());
        u.setDescription(userDto.getDescription());
        if (!StringUtils.isBlank(userDto.getAvailableVirtualDatacenters()))
        {
            String[] split = userDto.getAvailableVirtualDatacenters().split(",");
            if (split != null)
            {
                Integer[] a = new Integer[split.length];
                int i = 0;
                for (String n : split)
                {
                    a[i++] = Integer.valueOf(n);
                }
                u.setAvailableVirtualDatacenters(a);
            }
        }
        u.setAuthType(AuthType.valueOf(userDto.getAuthType()));
        return u;
    }

    @Override
    public DataResult undeployVirtualAppliance(final Integer virtualDatacenterId,
        final Integer virtualApplianceId, final boolean force)
    {
        DataResult result = new DataResult();
        String link = createVirtualApplianceUndeployLink(virtualDatacenterId, virtualApplianceId);

        VirtualMachineTaskDto virtualMachineTaskDto = new VirtualMachineTaskDto();
        virtualMachineTaskDto.setForceUndeploy(force);
        ClientResponse response = post(link, virtualMachineTaskDto);

        if (response.getStatusCode() == 202)
        {
            result.setSuccess(Boolean.TRUE);
            AcceptedRequestDto entity = response.getEntity(AcceptedRequestDto.class);
            result.setData(entity.getLinks());

            return this.getVirtualApplianceNodes(virtualDatacenterId, virtualApplianceId,
                "undeployVirtualAppliance");
        }
        else
        {
            populateErrors(response, result, "undeployVirtualAppliance");
        }
        return result;
    }

    protected VirtualApplianceDto createVirtualApplianceDtoObject(
        final VirtualAppliance virtualAppliance)
    {
        VirtualApplianceDto dto = new VirtualApplianceDto();

        dto.setName(virtualAppliance.getName());

        return dto;
    }

    protected VirtualAppliance createVirtualApplianceFlexObject(final VirtualApplianceDto dto)
    {
        VirtualAppliance vapp = new VirtualAppliance();

        vapp.setName(dto.getName());
        vapp.setId(dto.getId());
        vapp.setState(transformStates(dto.getState()));
        vapp.setNodes(new ArrayList<Node>());

        return vapp;
    }

    private State transformStates(final VirtualApplianceState state)
    {
        State flexState = new State();
        switch (state)
        {
            case DEPLOYED:
                flexState.setDescription(StateEnum.DEPLOYED.name());
                flexState.setId(StateEnum.DEPLOYED.id());
                break;
            case NOT_DEPLOYED:
                flexState.setDescription(StateEnum.NOT_DEPLOYED.name());
                flexState.setId(StateEnum.NOT_DEPLOYED.id());
                break;
            case NEEDS_SYNC:
                flexState.setDescription(StateEnum.NEEDS_SYNC.name());
                flexState.setId(StateEnum.NEEDS_SYNC.id());
                break;
            case LOCKED:
                flexState.setDescription(StateEnum.LOCKED.name());
                flexState.setId(StateEnum.LOCKED.id());
            case UNKNOWN:
                flexState.setDescription(StateEnum.UNKNOWN.name());
                flexState.setId(StateEnum.UNKNOWN.id());
        }

        return flexState;
    }

    @Override
    public DataResult<Collection<VirtualAppliance>> getVirtualAppliancesByEnterprise(
        final UserSession userSession, final Enterprise enterprise)
    {
        DataResult<Collection<VirtualAppliance>> result =
            new DataResult<Collection<VirtualAppliance>>();

        String link = createVirtualDatacentersFromEnterpriseLink(enterprise.getId());
        result.setSuccess(Boolean.TRUE);
        List<VirtualAppliance> list = new ArrayList<VirtualAppliance>();
        ClientResponse eResponse = get(link);
        try
        {
            VirtualDatacentersDto dtos = eResponse.getEntity(VirtualDatacentersDto.class);
            for (VirtualDatacenterDto dto : dtos.getCollection())
            {
                VirtualDataCenter virtualDatacenter = dtoToVirtualDatacenter(dto, enterprise);
                RESTLink app = dto.searchLink("virtualappliances");
                ClientResponse response = get(app.getHref());
                VirtualAppliancesDto virtualAppliancesDto =
                    response.getEntity(VirtualAppliancesDto.class);
                list.addAll(dtosToVirtualAppliance(virtualAppliancesDto, virtualDatacenter, result));
            }
            result.setData(list);
        }
        catch (Exception e)
        {
            populateErrors(eResponse, result, "getVirtualAppliancesByEnterprise");
        }

        return result;
    }

    private List<VirtualAppliance> dtosToVirtualAppliance(
        final VirtualAppliancesDto virtualAppliancesDto, final DataResult result)
    {
        List<VirtualAppliance> apps = new ArrayList<VirtualAppliance>();
        for (VirtualApplianceDto dto : virtualAppliancesDto.getCollection())
        {
            Integer virtualDatacenterId = dto.getIdFromLink("virtualdatacenter");
            VirtualAppliance virtualAppliance =
                dtoToVirtualAppliance(dto, virtualDatacenterId, result);
            apps.add(virtualAppliance);
        }

        return apps;
    }

    private List<VirtualAppliance> dtosToVirtualAppliance(
        final VirtualAppliancesDto virtualAppliancesDto, final VirtualDataCenter vdc,
        final DataResult result)
    {
        List<VirtualAppliance> apps = new ArrayList<VirtualAppliance>();
        for (VirtualApplianceDto dto : virtualAppliancesDto.getCollection())
        {
            VirtualAppliance virtualAppliance = dtoToVirtualAppliance(dto, vdc, result);
            apps.add(virtualAppliance);
        }

        return apps;
    }

    // public BasicResult deleteVirtualAppliance(final VirtualAppliance virtualAppliance,
    // final boolean forceDelete)
    // {
    // BasicResult result = new BasicResult();
    // result.setSuccess(Boolean.TRUE);
    // Map<String, String[]> queryParams = new HashMap<String, String[]>();
    // String link =
    // createVirtualApplianceUrl(virtualAppliance.getVirtualDataCenter().getId(),
    // virtualAppliance.getId());
    //
    // if (forceDelete)
    // {
    // queryParams.put("force", new String[] {String.valueOf(forceDelete)});
    // }
    // ClientResponse response = delete(link);
    // // When not forcing the deletion fail images non managed
    //
    // if (response.getStatusCode() == Status.CONFLICT.getStatusCode())
    // {
    //
    // result.setSuccess(false);
    // ErrorsDto errors = response.getEntity(ErrorsDto.class);
    // if (errors.getCollection() != null && !errors.getCollection().isEmpty())
    // {
    // for (ErrorDto e : errors.getCollection())
    // {
    // // There are not managed Machines
    // if ("VAPP-4".equals(e.getCode()))
    // {
    // result.setResultCode(BasicResult.NOT_MANAGED_VIRTUAL_IMAGE);
    // return result;
    // }
    // }
    // populateErrors(response, result, "deleteVirtualAppliance");
    // }
    //
    // }
    //
    // if (response.getStatusCode() != Status.NO_CONTENT.getStatusCode())
    // {
    // populateErrors(response, result, "deleteVirtualAppliance");
    //
    // }
    //
    // return result;
    // }

    @Override
    public VirtualmachineHB allocate(final Integer virtualDatacenterId,
        final Integer virtualApplianceId, final VirtualmachineHB vmachineRequ,
        final List<ResourceManagementHB> resMans, final boolean forceEnterpirseLimits,
        final ErrorManager errorManager) throws VirtualApplianceCommandException // TODO
    {
        // VirtualImageWithResourcesDto vimageReq =
        // createVirtualImageAndResourceDto(vmachineRequ, resMans, forceEnterpirseLimits);
        //
        // String vappUrl = resolveVirtualApplianceUrl(virtualDatacenterId, virtualApplianceId);
        //
        // ClientResponse response = post(vappUrl, vimageReq);
        //
        // if (response.getStatusCode() != 201)
        // {
        // onError(response);
        // }
        //
        // Integer vmachineId = response.getEntity(Integer.class);
        //
        // BasicCommand.traceLog(SeverityType.INFO, ComponentType.VIRTUAL_APPLIANCE,
        // EventType.VAPP_CREATE, getCurrentUser(), null, "XXX", "Selected virtual machine", null,
        // null, null, null, null);
        //
        // DAOFactory dao = HibernateDAOFactory.instance();
        // dao.beginConnection();
        // VirtualmachineHB vmachine = dao.getVirtualMachineDAO().findById(vmachineId);
        //
        // /**
        // * TODO substitute vmachineRequ with vmachine on the current virutal appliance
        // */
        //
        // dao.endConnection();
        //
        // return vmachine;
        return null;
    }

    /**
     * vimage constains the modified cpu, ram and hd
     */
    // VirtualImageWithResourcesDto createVirtualImageAndResourceDto(VirtualmachineHB vmachine,
    // List<ResourceManagementHB> resMans, boolean forceEnterpirseLimits)
    // {
    // VirtualImageWithResourcesDto vimageReq = new VirtualImageWithResourcesDto();
    //
    // vimageReq.setVirtualImageId(vmachine.getImage().getIdImage());
    //
    // vimageReq.setRequiredCpu(vmachine.getCpu());
    // vimageReq.setRequiredRam(vmachine.getRam());
    // vimageReq.setRequiredHd(vmachine.getHd());
    //
    // vimageReq.setForeceEnterpriseSoftLimits(forceEnterpirseLimits);
    //
    // Collection<Integer> rasdIds = new HashSet<Integer>();
    // for (ResourceManagementHB rasdHb : resMans)
    // {
    // rasdIds.add(rasdHb.getIdManagement());
    // }
    //
    // vimageReq.setRasdIds(rasdIds);
    //
    // return vimageReq;
    // }

    private String resolveVirtualApplianceUrl(final Integer virtualDatacenterId,
        final Integer virtualApplianceId)
    {
        Map<String, String> params = new HashMap<String, String>();
        params.put("{virtualDatacenter}", String.valueOf(virtualDatacenterId));
        params.put("{vapp}", String.valueOf(virtualApplianceId));

        return URIResolver.resolveURI(apiUri,
            "cloud/virtualdatacenters/{virtualDatacenter}/vapps/{vapp}", params);
    }

    @Override
    public BasicResult createVirtualAppliance(final VirtualAppliance virtualAppliance)
    {
        DataResult<VirtualAppliance> result = new DataResult<VirtualAppliance>();

        try
        {

            // Retrieve the VirtualDatacenter to associate the new virtual appliance
            VirtualDatacenter vdc =
                getApiClient().getCloudService().getVirtualDatacenter(
                    virtualAppliance.getVirtualDataCenter().getId());

            // The only data a virtual appliance has is the name
            org.jclouds.abiquo.domain.cloud.VirtualAppliance vapp =
                org.jclouds.abiquo.domain.cloud.VirtualAppliance.builder(getApiClient(), vdc)
                    .name(virtualAppliance.getName()).build();
            // Here we actually perform the request to create the virtual appliance
            vapp.save();

            result.setData(dtoToVirtualAppliance(vapp.unwrap(),
                virtualAppliance.getVirtualDataCenter(), result));
            result.setSuccess(Boolean.TRUE);
        }
        catch (Exception e)
        {
            populateErrors(e, result, "createVirtualAppliance");
        }
        finally
        {
            releaseApiClient();
        }

        return result;
    }

    @Override
    public BasicResult deleteVirtualAppliance(final VirtualAppliance virtualAppliance,
        final boolean forceDelete)
    {
        BasicResult result = new BasicResult();
        try
        {
            // Retrieve the VirtualDatacenter to associate the new virtual appliance
            VirtualDatacenter vdc =
                getApiClient().getCloudService().getVirtualDatacenter(
                    virtualAppliance.getVirtualDataCenter().getId());
            VirtualApplianceDto dto =
                getApiClient().getApi().getCloudClient()
                    .getVirtualAppliance(vdc.unwrap(), virtualAppliance.getId());

            org.jclouds.abiquo.domain.cloud.VirtualAppliance vapp =
                DomainWrapper.wrap(getApiClient(),
                    org.jclouds.abiquo.domain.cloud.VirtualAppliance.class, dto);

            List<org.jclouds.abiquo.domain.cloud.VirtualMachine> virtualMachines =
                vapp.listVirtualMachines();
            if (virtualMachines != null && !virtualMachines.isEmpty())
            {
                vapp.undeploy(Boolean.TRUE);

                // The vapp state is DEPLOYED
                vapp =
                    DomainWrapper.wrap(getApiClient(),
                        org.jclouds.abiquo.domain.cloud.VirtualAppliance.class, dto);

                // Blocking
                getApiClient()
                    .getMonitoringService()
                    .getVirtualMachineMonitor()
                    .awaitCompletionUndeploy(
                        virtualMachines
                            .toArray(new org.jclouds.abiquo.domain.cloud.VirtualMachine[0]));
            }
            // Here we actually perform the request to delete the virtual appliance
            vapp.delete();

            result.setSuccess(Boolean.TRUE);
        }
        catch (Exception e)
        {
            populateErrors(e, result, "deleteVirtualAppliance");
        }
        finally
        {
            releaseApiClient();
        }

        return result;
    }

    @Override
    public DataResult<VirtualAppliance> applyChangesVirtualAppliance(
        final VirtualAppliance virtualAppliance, final UserSession userSession, final boolean force)
    {
        DataResult<VirtualAppliance> result = new DataResult<VirtualAppliance>();
        result.setSuccess(Boolean.TRUE);
        StringBuilder errors = new StringBuilder();
        try
        {
            // Retrieve the VirtualDatacenter to associate the new virtual appliance
            VirtualDatacenter vdc =
                getApiClient().getCloudService().getVirtualDatacenter(
                    virtualAppliance.getVirtualDataCenter().getId());

            org.jclouds.abiquo.domain.cloud.VirtualAppliance appliance =
                vdc.getVirtualAppliance(virtualAppliance.getId());

            for (Node node : virtualAppliance.getNodes())
            {
                try
                {
                    if (node instanceof NodeVirtualImage)
                    {
                        NodeVirtualImage nvi = (NodeVirtualImage) node;
                        if (Node.NODE_ERASED == nvi.getModified())
                        {
                            org.jclouds.abiquo.domain.cloud.VirtualMachine virtualMachine =
                                appliance.getVirtualMachine(nvi.getVirtualMachine().getId());
                            virtualMachine.delete();
                        }
                        else if (!nvi.getVirtualMachine().getState().toEnum().existsInHypervisor())
                        {
                            org.jclouds.abiquo.domain.cloud.VirtualMachine virtualMachine =
                                appliance.getVirtualMachine(nvi.getVirtualMachine().getId());

                            virtualMachine.deploy(force);
                        }
                    }
                }
                catch (Exception e)
                {
                    // populateErrors(e, result, "applyChangesVirtualAppliance");
                    this.populateErrors(e, result, errors, "applyChangesVirtualAppliance");
                }
            }
        }
        finally
        {
            releaseApiClient();
        }
        String errorsMsg = errors.toString();
        if (!StringUtils.isEmpty(errorsMsg))
        {
            result.setMessage(errorsMsg);
        }
        // result.setData(virtualAppliance);
        return result;

    }

    protected void populateErrors(final Exception ex, final BasicResult result,
        final StringBuilder errors, final String methodName)
    {
        result.setSuccess(false);
        if (ex instanceof AuthorizationException)
        {
            populateErrors((AuthorizationException) ex, result, methodName);
        }
        else if (ex instanceof AbiquoException)
        {
            AbiquoException abiquoException = (AbiquoException) ex;

            if (abiquoException.hasError("SOFT_LIMIT_EXCEEDED"))
            {
                result.setResultCode(BasicResult.SOFT_LIMT_EXCEEDED);
                result.setMessage(abiquoException.getMessage());
                // limit exceeded does not include the detail
                if (result.getMessage().length() < 254)
                {
                    result.setResultCode(0);
                }
            }
            else if (abiquoException.hasError("LIMIT_EXCEEDED")
                || BasicResult.HARD_LIMT_EXCEEDED == result.getResultCode())
            {
                result.setResultCode(BasicResult.HARD_LIMT_EXCEEDED);
                result.setMessage(abiquoException.getMessage());
                // limit exceeded does not include the detail
                if (result.getMessage().length() < 254)
                {
                    result.setResultCode(0);
                }
            }
            else
            {
                errors.append(abiquoException.getMessage()).append("\n")
                    .append(abiquoException.getErrors().get(0).getCode());
            }
        }
        else
        {
            errors.append(ex.getMessage()).append("\n");
        }
    }

    @Override
    public DataResult<VirtualAppliance> instanceVirtualApplianceNodes(
        final Integer virtualDatacenterId, final Integer virtualApplianceId,
        final Collection<Node> nodes)
    {
        StringBuilder errors = new StringBuilder();
        DataResult result = new DataResult();
        result.setSuccess(Boolean.TRUE);

        for (Node node : nodes)
        {
            NodeVirtualImage nvi = (NodeVirtualImage) node;
            Integer virtualMachineId = nvi.getVirtualMachine().getId();
            String instanceName = node.getName();

            String url =
                createVirtualMachineInstanceUrl(virtualDatacenterId, virtualApplianceId,
                    virtualMachineId);

            VirtualMachineInstanceDto options = new VirtualMachineInstanceDto();
            options.setInstanceName(instanceName);

            ClientResponse response = post(url, options);

            int statusCode = response.getStatusCode();

            if (statusCode != HttpStatus.ACCEPTED.getCode()
                && statusCode != HttpStatus.SEE_OTHER.getCode())
            {
                result.setSuccess(Boolean.FALSE);
                addErrors(result, errors, response, "instanceVirtualApplianceNodes");
            }
        }

        DataResult<VirtualAppliance> virtualAppData =
            getVirtualApplianceNodes(virtualDatacenterId, virtualApplianceId,
                "instanceVirtualApplianceNodes");

        if (virtualAppData.getSuccess())
        {
            result.setData(virtualAppData.getData());
            result.setMessage(errors.toString());
            return result;
        }
        else
        {
            return virtualAppData;
        }
    }

    @Override
    public DataResult<List<TaskStatus>> updateTask(final TaskStatus task)
    {
        StringBuilder errors = new StringBuilder();
        DataResult<List<TaskStatus>> result = new DataResult<List<TaskStatus>>();
        result.setSuccess(Boolean.TRUE);

        List<TaskStatus> tasks = new ArrayList<TaskStatus>();
        for (String link : task.getUris())
        {
            ClientResponse clientResponse = get(link);
            if (clientResponse.getStatusCode() == Status.OK.getStatusCode()
                || clientResponse.getStatusCode() == Status.SEE_OTHER.getStatusCode())
            {
                addErrors(result, errors, clientResponse, "updateTask");
            }
            else
            {
                TaskDto dto = clientResponse.getEntity(TaskDto.class);
                TaskStatus currentTask = new TaskStatus();
                currentTask.addUri(link);
                currentTask.addTask(dtoToTask(dto));
                tasks.add(currentTask);
            }
        }
        result.setData(tasks);
        return result;
    }
}
