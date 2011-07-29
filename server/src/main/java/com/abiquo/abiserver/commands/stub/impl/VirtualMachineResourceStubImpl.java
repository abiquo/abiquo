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

import java.util.HashMap;
import java.util.Map;

import javax.ws.rs.core.MediaType;

import org.apache.wink.client.ClientConfig;
import org.apache.wink.client.ClientResponse;
import org.apache.wink.client.Resource;
import org.apache.wink.client.RestClient;
import org.apache.wink.common.internal.utils.UriHelper;

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
import com.abiquo.model.transport.error.ErrorsDto;
import com.abiquo.server.core.cloud.VirtualMachineDto;
import com.abiquo.tracer.ComponentType;
import com.abiquo.tracer.EventType;
import com.abiquo.tracer.Platform;
import com.abiquo.tracer.SeverityType;
import com.abiquo.tracer.client.TracerFactory;
import com.abiquo.util.URIResolver;

public class VirtualMachineResourceStubImpl extends AbstractAPIStub implements
    VirtualMachineResourceStub
{

    private final static Integer TIMEOUT = 3 * 60 * 1000; // 3 minutes

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
        String vmachineUrl =
            resolveVirtualMachineUrl(virtualDatacenterId, virtualApplianceId,
                virtualMachine.getId());

        ClientResponse response = put(vmachineUrl, createTransferObject(virtualMachine));

        if (response.getStatusCode() == 200)
        {
            result.setSuccess(true);
        }
        else
        {
            populateErrors(response, result, "updateVirtualMachine");
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
    public void checkEdit(final UserSession userSession, final Integer virtualDatacenterId,
        final Integer virtualApplianceId, final Integer virtualMachineId, final int newcpu,
        final int newram) throws HardLimitExceededException, SoftLimitExceededException,
        SchedulerException, NotEnoughResourcesException
    {

        VirtualMachineDto newRequirements = new VirtualMachineDto();
        newRequirements.setCpu(newcpu);
        newRequirements.setRam(newram);

        String vmachineUrl =
            resolveVirtualMachineUrl(virtualDatacenterId, virtualApplianceId, virtualMachineId);

        vmachineUrl = UriHelper.appendPathToBaseUri(vmachineUrl, "action/checkedit");

        Resource vmachineResource = resource(vmachineUrl);

        ClientResponse response =
            vmachineResource.contentType(MediaType.APPLICATION_XML)
                .accept(MediaType.APPLICATION_XML).put(newRequirements);

        // ClientResponse response = put(vappUrl, String.valueOf(forceEnterpirseLimits));

        if (response.getStatusCode() / 200 != 1)
        {
            onError(userSession, response);
        }
    }

    @Override
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
    public void deallocate(final UserSession userSession, final Integer virtualDatacenterId,
        final Integer virtualApplianceId, final Integer virtualMachineId)
        throws HardLimitExceededException, SoftLimitExceededException, SchedulerException,
        NotEnoughResourcesException
    {
        String vmachineUrl =
            resolveVirtualMachineUrl(virtualDatacenterId, virtualApplianceId, virtualMachineId);

        vmachineUrl = UriHelper.appendPathToBaseUri(vmachineUrl, "action/deallocate");

        ClientResponse response = resource(vmachineUrl).delete();

        if (response.getStatusCode() / 200 != 1)
        {
            onError(userSession, response);
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

    private String resolveVirtualMachineUrl(final Integer virtualDatacenterId,
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

    private VirtualMachineDto createTransferObject(final VirtualMachine virtualMachine)
    {
        VirtualMachineDto dto = new VirtualMachineDto();

        dto.setCpu(virtualMachine.getCpu());
        dto.setRam(virtualMachine.getRam());
        dto.setDescription(virtualMachine.getDescription());
        dto.setHd(virtualMachine.getHd());
        dto.setHighDisponibility(virtualMachine.getHighDisponibility() ? 1 : 0);
        dto.setPassword(virtualMachine.getPassword());
        dto.setName(virtualMachine.getName());
        dto.setVdrpIP(virtualMachine.getVdrpIP());
        dto.setVdrpPort(virtualMachine.getVdrpPort());

        return dto;
    }
}
