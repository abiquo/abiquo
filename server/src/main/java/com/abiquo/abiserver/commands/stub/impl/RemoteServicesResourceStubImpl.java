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

import org.apache.wink.client.ClientResponse;

import com.abiquo.abiserver.commands.BasicCommand;
import com.abiquo.abiserver.commands.stub.AbstractAPIStub;
import com.abiquo.abiserver.commands.stub.RemoteServicesResourceStub;
import com.abiquo.abiserver.pojo.result.DataResult;
import com.abiquo.abiserver.pojo.service.RemoteService;
import com.abiquo.model.enumerator.RemoteServiceType;
import com.abiquo.server.core.infrastructure.RemoteServiceDto;
import com.abiquo.tracer.ComponentType;
import com.abiquo.tracer.EventType;
import com.abiquo.tracer.SeverityType;

public class RemoteServicesResourceStubImpl extends AbstractAPIStub implements
    RemoteServicesResourceStub
{

    @Override
    public DataResult<RemoteService> addRemoteService(RemoteService remoteService)
    {
        DataResult<RemoteService> result = new DataResult<RemoteService>();
        RemoteServiceDto dto = getApiResource(remoteService);

        String uri = createRemoteServicesLink(remoteService.getIdDataCenter());

        ClientResponse response = post(uri, dto);
        if (response.getStatusCode() == 201)
        {
            result.setSuccess(true);

            RemoteService responseService =
                getReponseService(remoteService, result, response, EventType.REMOTE_SERVICES_CREATE);

            result.setData(responseService);
        }
        else
        {
            populateErrors(response, result, "addRemoteService");
        }

        return result;
    }

    private RemoteService getReponseService(RemoteService remoteService,
        DataResult<RemoteService> result, ClientResponse response, EventType eventType)
    {
        RemoteServiceDto responseDto = response.getEntity(RemoteServiceDto.class);

        RemoteService responseService =
            RemoteService.create(responseDto, remoteService.getIdDataCenter());

        if (responseDto.getConfigurationErrors() != null
            && !responseDto.getConfigurationErrors().isEmpty())
        {
            result.setMessage(responseDto.getConfigurationErrors().toString());

            BasicCommand.traceLog(SeverityType.MAJOR, ComponentType.DATACENTER, eventType,
                currentSession, null, null, responseDto.getConfigurationErrors().toString(), null,
                null, null, null, null);
        }
        return responseService;
    }

    @Override
    public DataResult<Boolean> deleteRemoteService(RemoteService remoteService)
    {
        DataResult<Boolean> result = new DataResult<Boolean>();

        String uri =
            createRemoteServiceLink(remoteService.getIdDataCenter(), remoteService
                .getRemoteServiceType().toEnum().toString());

        ClientResponse response = delete(uri);
        if (response.getStatusCode() == 204)
        {
            result.setSuccess(true);
            result.setData(true);
        }
        else
        {
            populateErrors(response, result, "deleteRemoteService");
            result.setData(false);
        }

        return result;
    }

    @Override
    public DataResult<RemoteService> modifyRemoteService(RemoteService remoteService)
    {
        DataResult<RemoteService> result = new DataResult<RemoteService>();
        RemoteServiceDto dto = getApiResource(remoteService);

        String uri =
            createRemoteServiceLink(remoteService.getIdDataCenter(), remoteService
                .getRemoteServiceType().toEnum().toString());

        ClientResponse response = put(uri, dto);
        if (response.getStatusCode() == 200)
        {
            result.setSuccess(true);

            RemoteService responseService =
                getReponseService(remoteService, result, response, EventType.REMOTE_SERVICES_UPDATE);

            result.setData(responseService);
        }
        else
        {
            populateErrors(response, result, "modifyRemoteService");
        }

        return result;
    }

    private RemoteServiceDto getApiResource(RemoteService remoteService)
    {
        RemoteServiceDto dto = new RemoteServiceDto();
        dto.setUri(remoteService.getUri());
        dto.setType(RemoteServiceType.valueOf(remoteService.getRemoteServiceType().toEnum()
            .toString()));

        return dto;
    }

}
