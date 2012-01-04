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

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response.Status;

import org.apache.commons.lang.StringUtils;
import org.apache.wink.client.ClientConfig;
import org.apache.wink.client.ClientResponse;
import org.apache.wink.client.Resource;
import org.apache.wink.client.RestClient;
import org.apache.wink.common.internal.utils.UriHelper;

import com.abiquo.abiserver.abicloudws.AbiCloudConstants;
import com.abiquo.abiserver.business.UserSessionException;
import com.abiquo.abiserver.commands.BasicCommand;
import com.abiquo.abiserver.commands.stub.AbstractAPIStub;
import com.abiquo.abiserver.commands.stub.VirtualMachineResourceStub;
import com.abiquo.abiserver.exception.HardLimitExceededException;
import com.abiquo.abiserver.exception.NotEnoughResourcesException;
import com.abiquo.abiserver.exception.SchedulerException;
import com.abiquo.abiserver.exception.SoftLimitExceededException;
import com.abiquo.abiserver.pojo.authentication.UserSession;
import com.abiquo.abiserver.pojo.infrastructure.VirtualMachine;
import com.abiquo.abiserver.pojo.result.BasicResult;
import com.abiquo.abiserver.pojo.result.DataResult;
import com.abiquo.abiserver.pojo.virtualappliance.Node;
import com.abiquo.abiserver.pojo.virtualappliance.NodeVirtualImage;
import com.abiquo.model.rest.RESTLink;
import com.abiquo.model.transport.AcceptedRequestDto;
import com.abiquo.model.transport.error.ErrorsDto;
import com.abiquo.server.core.cloud.VirtualMachineDto;
import com.abiquo.server.core.cloud.VirtualMachineInstanceDto;
import com.abiquo.server.core.cloud.VirtualMachineState;
import com.abiquo.server.core.cloud.VirtualMachineStateDto;
import com.abiquo.tracer.ComponentType;
import com.abiquo.tracer.EventType;
import com.abiquo.tracer.Platform;
import com.abiquo.tracer.SeverityType;
import com.abiquo.tracer.client.TracerFactory;
import com.abiquo.util.ErrorManager;
import com.abiquo.util.URIResolver;
import com.abiquo.util.resources.ResourceManager;

public class VirtualMachineResourceStubImpl extends AbstractAPIStub implements
    VirtualMachineResourceStub
{
    /* Set the timeout to the double fo time of the set in the system properties */
    private final static Integer TIMEOUT = Integer.parseInt(System.getProperty(
        "abiquo.nodecollector.timeout", "0")) * 2 * 2; // 3 minutes the second * 2 is due to the
                                                       // synchronization on allocate

    public VirtualMachineResourceStubImpl()
    {
        super();

        ClientConfig conf = new ClientConfig();
        conf.readTimeout(TIMEOUT);

        this.client = new RestClient(conf);
    }

    @Override
    public BasicResult updateVirtualMachine(final Integer virtualDatacenterId,
        final Integer virtualApplianceId, final VirtualMachine virtualMachine)
    {

        BasicResult result = new BasicResult();

        try
        {
            // Retrieve the VirtualDatacenter to associate the new virtual appliance
            org.jclouds.abiquo.domain.cloud.VirtualAppliance vapp =
                getApiClient().getCloudService().getVirtualDatacenter(virtualDatacenterId)
                    .getVirtualAppliance(virtualApplianceId);

            org.jclouds.abiquo.domain.cloud.VirtualMachine vm =
                vapp.getVirtualMachine(virtualMachine.getId());

            if (vm.getCpu() != virtualMachine.getCpu() || vm.getRam() != virtualMachine.getRam()
                || virtualMachine.getPassword() != null
                && !virtualMachine.getPassword().equals(vm.getPassword()))
            {
                vm.setCpu(virtualMachine.getCpu());
                vm.setRam(virtualMachine.getRam());

                vm.setPassword(virtualMachine.getPassword());

                // Here we actually perform the request to create the virtual machine
                vm.update();
            }
            // else all updated

            result.setSuccess(Boolean.TRUE);
        }
        catch (Exception e)
        {
            populateErrors(e, result, "updateVirtualMachine");
        }
        finally
        {
            releaseApiClient();
        }

        return result;
    }

    public void pause(final UserSession userSession, final Integer virtualDatacenterId,
        final Integer virtualApplianceId, final Integer virtualMachineId, final int newcpu,
        final int newram) throws HardLimitExceededException, SoftLimitExceededException,
        SchedulerException, NotEnoughResourcesException
    {

        String vmachineUrl =
            resolveVirtualMachineUrl(virtualDatacenterId, virtualApplianceId, virtualMachineId);

        vmachineUrl = UriHelper.appendPathToBaseUri(vmachineUrl, "action/pause");

        Resource vmachineResource = resource(vmachineUrl);

        ClientResponse response =
            vmachineResource.contentType(MediaType.APPLICATION_XML)
                .accept(MediaType.APPLICATION_XML).post(null);

        // ClientResponse response = put(vappUrl, String.valueOf(forceEnterpirseLimits));

        if (response.getStatusCode() / 200 != 1)
        {
            onError(userSession, response);
        }
    }

    @Override
    @Deprecated
    public void allocate(final UserSession userSession, final Integer virtualDatacenterId,
        final Integer virtualApplianceId, final Integer virtualMachineId,
        final boolean forceEnterpirseLimits) throws HardLimitExceededException,
        SoftLimitExceededException, SchedulerException, NotEnoughResourcesException
    {

        String vmachineUrl =
            resolveVirtualMachineUrl(virtualDatacenterId, virtualApplianceId, virtualMachineId);

        vmachineUrl = UriHelper.appendPathToBaseUri(vmachineUrl, "action/allocate");

        Resource vmachineResource = resource(vmachineUrl);

        ClientResponse response =
            vmachineResource.contentType(MediaType.TEXT_PLAIN).accept(MediaType.APPLICATION_XML)
                .put(String.valueOf(forceEnterpirseLimits));

        // ClientResponse response = put(vappUrl, String.valueOf(forceEnterpirseLimits));

        if (response.getStatusCode() / 200 != 1)
        {
            onError(userSession, response);
        }

        VirtualMachineDto vmachineDto = response.getEntity(VirtualMachineDto.class);

        if (!vmachineDto.getId().equals(virtualMachineId))
        {
            throw new SchedulerException("Virtual machine changes its identifier");
        }
    }

    @Override
    @Deprecated
    public void deallocate(final UserSession userSession, final Integer virtualDatacenterId,
        final Integer virtualApplianceId, final Integer virtualMachineId)
        throws HardLimitExceededException, SoftLimitExceededException, SchedulerException,
        NotEnoughResourcesException
    {
        String vmachineUrl =
            resolveVirtualMachineUrl(virtualDatacenterId, virtualApplianceId, virtualMachineId);

        vmachineUrl = UriHelper.appendPathToBaseUri(vmachineUrl, "action/deallocate");

        int i = 3;
        while (i-- > 0)
        {
            ClientResponse response = resource(vmachineUrl).delete();
            int statusCode = response.getStatusCode();
            if (statusCode == Status.NO_CONTENT.getStatusCode())
            {
                return;
            }
            if (statusCode == Status.SERVICE_UNAVAILABLE.getStatusCode())
            {
                BasicCommand.traceLog(SeverityType.CRITICAL, ComponentType.VIRTUAL_MACHINE,
                    EventType.VM_DESTROY, userSession, null, "VDC id " + virtualDatacenterId,
                    "The API returned a 503 - Service Unavailable. We will try up to " + i
                        + " times again", null, null, null, null, null);
            }
        }
    }

    private void onError(final UserSession userSession, final ClientResponse response)
        throws HardLimitExceededException, SoftLimitExceededException, SchedulerException,
        NotEnoughResourcesException
    {
        ErrorsDto errors = response.getEntity(ErrorsDto.class);
        String message = errors.toString();

        /*
         * LIMIT_EXCEEDED("LIMIT", "The required resources exceed the allowed limits"),
         * NOT_ENOUGH_RESOURCES( "ALLOC-0",
         * "There isn't enough resources to create the virtual machine"), //
         * ALLOCATOR_ERROR("ALLOC-1", "Can not create virtual machine"), //
         */

        if (message.startsWith("LIMIT"))
        {
            if (message.contains("HARD_LIMIT"))
            {
                throw new HardLimitExceededException(message);
            }
            else if (message.contains("SOFT_LIMIT"))
            {
                throw new SoftLimitExceededException(message);
            }
            else
            {
                // Enterprise or datacenter hard limits exceeded
                throw new NotEnoughResourcesException(message);
            }
        }
        else if (message.startsWith("ALLOC-0"))
        {
            // trace to system with all the detailed cause.
            TracerFactory.getTracer().log(SeverityType.NORMAL, ComponentType.VIRTUAL_APPLIANCE,
                EventType.VAPP_POWERON, message, Platform.SYSTEM_PLATFORM);

            // the user can't see the details of the detailed error cause.
            throw new NotEnoughResourcesException("There are not enough resources to create the virtual machine.");
        }
        else
        {

            BasicCommand
                .traceLog(SeverityType.CRITICAL, ComponentType.VIRTUAL_APPLIANCE,
                    EventType.VAPP_CREATE, userSession, null, "", message, null, null, null, null,
                    null);

            throw new SchedulerException(message);
        }
    }

    protected String resolveVirtualMachineUrl(final Integer virtualDatacenterId,
        final Integer virtualApplianceId, final Integer virtualMachineId)
    {
        Map<String, String> params = new HashMap<String, String>();
        params.put("virtualDatacenter", String.valueOf(virtualDatacenterId));
        params.put("vapp", String.valueOf(virtualApplianceId));
        params.put("virtualMachine", String.valueOf(virtualMachineId));

        return URIResolver
            .resolveURI(
                apiUri,
                "cloud/virtualdatacenters/{virtualDatacenter}/virtualappliances/{vapp}/virtualmachines/{virtualMachine}",
                params);
    }

    protected VirtualMachineDto createTransferObject(final VirtualMachine virtualMachine)
    {
        VirtualMachineDto dto = new VirtualMachineDto();

        dto.setCpu(virtualMachine.getCpu());
        dto.setRam(virtualMachine.getRam());
        dto.setDescription(virtualMachine.getDescription());
        dto.setHdInBytes(virtualMachine.getHd());
        dto.setHighDisponibility(virtualMachine.getHighDisponibility() ? 1 : 0);
        dto.setPassword(virtualMachine.getPassword());
        dto.setName(virtualMachine.getName());
        dto.setVdrpIP(virtualMachine.getVdrpIP());
        dto.setVdrpPort(virtualMachine.getVdrpPort());
        dto.setUuid(virtualMachine.getUUID());
        return dto;
    }

    @Override
    public BasicResult deleteVirtualMachine(final Integer virtualDatacenterId,
        final Integer virtualApplianceId, final VirtualMachine virtualMachine)
    {
        BasicResult result = new BasicResult();
        String vmachineUrl =
            resolveVirtualMachineUrl(virtualDatacenterId, virtualApplianceId,
                virtualMachine.getId());

        ClientResponse response = delete(vmachineUrl);

        if (response.getStatusCode() == Status.NO_CONTENT.getStatusCode())
        {
            result.setSuccess(true);
        }
        else
        {
            populateErrors(response, result, "deleteVirtualMachine");
        }

        return result;
    }

    @Override
    public DataResult editVirtualMachineState(final Integer virtualDatacenterId,
        final Integer virtualApplianceId, final VirtualMachine virtualMachine,
        final VirtualMachineState virtualMachineState)
    {
        DataResult result = new DataResult();
        String url =
            createEditVirtualMachineStateUrl(virtualDatacenterId, virtualApplianceId,
                virtualMachine.getId());
        VirtualMachineStateDto dto = new VirtualMachineStateDto();
        dto.setPower(virtualMachineState);
        ClientResponse response = put(url, dto);

        if (response.getStatusCode() == Status.ACCEPTED.getStatusCode())
        {
            result.setSuccess(true);
            AcceptedRequestDto acc = response.getEntity(AcceptedRequestDto.class);
            result.setData(acc.getLinks());
        }
        else
        {
            populateErrors(response, result, "editVirtualMachineState");
        }

        return result;
    }

    @Override
    public DataResult powerOffVirtualMachine(final Integer virtualDatacenterId,
        final Integer virtualApplianceId, final VirtualMachine virtualMachine)
    {
        return editVirtualMachineState(virtualDatacenterId, virtualApplianceId, virtualMachine,
            VirtualMachineState.OFF);
    }

    @Override
    public DataResult powerOnVirtualMachine(final Integer virtualDatacenterId,
        final Integer virtualApplianceId, final VirtualMachine virtualMachine)
    {
        return editVirtualMachineState(virtualDatacenterId, virtualApplianceId, virtualMachine,
            VirtualMachineState.ON);
    }

    @Override
    public DataResult pauseVirtualMachine(final Integer virtualDatacenterId,
        final Integer virtualApplianceId, final VirtualMachine virtualMachine)
    {
        return editVirtualMachineState(virtualDatacenterId, virtualApplianceId, virtualMachine,
            VirtualMachineState.PAUSED);
    }

    @Override
    public DataResult instanceVirtualMachines(final Integer virtualDatacenterId,
        final Integer virtualApplianceId, final Collection<Node> nodes)
    {
        Collection<RESTLink> links = new HashSet<RESTLink>();
        StringBuilder errors = new StringBuilder();
        DataResult result = new DataResult();

        for (Node node : nodes)
        {
            NodeVirtualImage nvi = (NodeVirtualImage) node;
            Integer virtualMachineId = nvi.getVirtualMachine().getId();
            String instanceName = node.getName();

            String url =
                createVirtualMachineInstanceUrl(virtualDatacenterId, virtualApplianceId,
                    virtualMachineId);

            VirtualMachineInstanceDto options = new VirtualMachineInstanceDto();
            options.setSnapshotName(instanceName);

            ClientResponse response = post(url, options);

            if (response.getStatusCode() == 202)
            {
                AcceptedRequestDto entity = response.getEntity(AcceptedRequestDto.class);
                links.addAll(entity.getLinks());
            }
            else
            {
                addErrors(result, errors, response, "instanceVirtualMachines");
            }
        }

        result.setData(links);
        result.setMessage(errors.toString());
        result.setSuccess(StringUtils.isBlank(result.getMessage()));

        return result;
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
}
