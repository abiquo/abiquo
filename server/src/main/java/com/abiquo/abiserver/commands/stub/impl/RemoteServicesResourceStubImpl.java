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

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import org.apache.wink.client.ClientResponse;
import org.jclouds.abiquo.domain.infrastructure.Datacenter;
import org.jclouds.abiquo.predicates.infrastructure.RemoteServicePredicates;

import com.abiquo.abiserver.abicloudws.RemoteServiceClient;
import com.abiquo.abiserver.commands.BasicCommand;
import com.abiquo.abiserver.commands.stub.AbstractAPIStub;
import com.abiquo.abiserver.commands.stub.RemoteServicesResourceStub;
import com.abiquo.abiserver.exception.RemoteServiceException;
import com.abiquo.abiserver.pojo.authentication.UserSession;
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
    public DataResult<RemoteService> addRemoteService(final RemoteService remoteService)
    {
        DataResult<RemoteService> result = new DataResult<RemoteService>();

        try
        {
            Datacenter dc =
                getApiClient().getAdministrationService().getDatacenter(
                    remoteService.getIdDataCenter());
            org.jclouds.abiquo.domain.infrastructure.RemoteService rs =
                org.jclouds.abiquo.domain.infrastructure.RemoteService
                    .builder(getApiClient(), dc)
                    .ip(remoteService.getDomainName())
                    .type(
                        RemoteServiceType
                            .valueOf(remoteService.getRemoteServiceType().getValueOf()))
                    .port(remoteService.getPort()).build();
            rs.save();

            result.setData(RemoteService.create(rs.unwrap(), remoteService.getIdDataCenter()));
            result.setSuccess(Boolean.TRUE);

            // ApplianceManager with errors isn't added
            if (rs.getType() == RemoteServiceType.APPLIANCE_MANAGER)
            {
                boolean amError = !rs.unwrap().getConfigurationErrors().isEmpty();
                if (amError)
                {
                    result.setSuccess(Boolean.FALSE);
                    result.setData(null);
                    result.setMessage(rs.unwrap().getConfigurationErrors().toString());
                }
            }
        }
        catch (Exception ex)
        {
            populateErrors(ex, result, "addRemoteService");
        }
        finally
        {
            releaseApiClient();
        }

        return result;
    }

    private RemoteService getReponseService(final RemoteService remoteService,
        final DataResult<RemoteService> result, final ClientResponse response,
        final EventType eventType)
    {
        RemoteServiceDto responseDto = response.getEntity(RemoteServiceDto.class);

        RemoteService responseService =
            RemoteService.create(responseDto, remoteService.getIdDataCenter());

        if (responseDto.getConfigurationErrors() != null
            && !responseDto.getConfigurationErrors().isEmpty())
        {
            result.setSuccess(false);
            result.setMessage(responseDto.getConfigurationErrors().toString());

            BasicCommand.traceLog(SeverityType.MAJOR, ComponentType.DATACENTER, eventType,
                currentSession, null, null, responseDto.getConfigurationErrors().toString(), null,
                null, null, null, null);
        }
        return responseService;
    }

    @Override
    public DataResult<Boolean> deleteRemoteService(final RemoteService remoteService)
    {
        DataResult<Boolean> result = new DataResult<Boolean>();

        try
        {
            Datacenter dc =
                getApiClient().getAdministrationService().getDatacenter(
                    remoteService.getIdDataCenter());
            org.jclouds.abiquo.domain.infrastructure.RemoteService rs =
                dc.findRemoteService(RemoteServicePredicates.type(RemoteServiceType
                    .valueOf(remoteService.getRemoteServiceType().getValueOf())));

            rs.delete();
            result.setData(Boolean.TRUE);
            result.setSuccess(Boolean.TRUE);
        }
        catch (Exception ex)
        {
            populateErrors(ex, result, "deleteRemoteService");
        }
        finally
        {
            releaseApiClient();
        }

        return result;
    }

    @Override
    public DataResult<RemoteService> modifyRemoteService(final RemoteService remoteService)
    {
        DataResult<RemoteService> result = new DataResult<RemoteService>();

        try
        {
            Datacenter dc =
                getApiClient().getAdministrationService().getDatacenter(
                    remoteService.getIdDataCenter());
            org.jclouds.abiquo.domain.infrastructure.RemoteService rs =
                dc.findRemoteService(RemoteServicePredicates.type(RemoteServiceType
                    .valueOf(remoteService.getRemoteServiceType().getValueOf())));
            rs.setUri(remoteService.getUri());
            rs.update();

            result.setData(RemoteService.create(rs.unwrap(), dc.getId()));
            result.setSuccess(Boolean.TRUE);
        }
        catch (Exception ex)
        {
            populateErrors(ex, result, "modifyRemoteService");
        }
        finally
        {
            releaseApiClient();
        }

        return result;
    }

    @Override
    public DataResult<List<RemoteService>> getRemoteServices(final Integer idDatacenter,
        final String type)
    {
        DataResult<List<RemoteService>> result = new DataResult<List<RemoteService>>();
        result.setData(new ArrayList<RemoteService>());

        try
        {
            Datacenter dc = getApiClient().getAdministrationService().getDatacenter(idDatacenter);
            org.jclouds.abiquo.domain.infrastructure.RemoteService rs =
                dc.findRemoteService(RemoteServicePredicates.type(RemoteServiceType.valueOf(type)));
            result.getData().add(RemoteService.create(rs.unwrap(), idDatacenter));
            result.setSuccess(Boolean.TRUE);
        }
        catch (Exception ex)
        {
            populateErrors(ex, result, "getRemoteServices");
        }
        finally
        {
            releaseApiClient();
        }

        return result;
    }

    @Override
    public DataResult<Boolean> checkRemoteService(final Integer idDatacenter, final String type)
    {
        DataResult<Boolean> result = new DataResult<Boolean>();

        try
        {
            Datacenter dc = getApiClient().getAdministrationService().getDatacenter(idDatacenter);
            org.jclouds.abiquo.domain.infrastructure.RemoteService rs =
                dc.findRemoteService(RemoteServicePredicates.type(RemoteServiceType.valueOf(type)));
            result.setData(rs.isAvailable());
            result.setSuccess(Boolean.TRUE);
        }
        catch (Exception ex)
        {
            populateErrors(ex, result, "checkRemoteService");
        }
        finally
        {
            releaseApiClient();
        }

        return result;
    }

    @Override
    public DataResult<Boolean> checkRemoteService(final UserSession userSession, final String type,
        final String uri)
    {
        DataResult<Boolean> result = new DataResult<Boolean>();

        RemoteServiceType rsType = RemoteServiceType.valueOf(type);

        // If a Remote Service cannot be checked by definition, we assume it is
        // OK
        if (!rsType.canBeChecked())
        {
            result.setSuccess(Boolean.TRUE);
            result.setData(Boolean.TRUE);
            return result;
        }

        try
        {
            new URL(uri);
        }
        catch (MalformedURLException ex)
        {
            result.setSuccess(Boolean.FALSE);
            result.setData(Boolean.FALSE);
            result.setMessage("Inavlid url to check (" + uri + ")");
            return result;
        }

        RemoteServiceClient remoteServiceClient = new RemoteServiceClient(uri);
        try
        {
            remoteServiceClient.ping();
            result.setSuccess(Boolean.TRUE);
            result.setData(Boolean.TRUE);
        }
        catch (RemoteServiceException ex)
        {
            result.setSuccess(Boolean.TRUE);
            result.setData(Boolean.FALSE);
        }
        catch (IllegalArgumentException ex)
        {
            result.setSuccess(Boolean.FALSE);
            result.setData(Boolean.FALSE);
            result.setMessage(ex.getMessage());
        }

        return result;
    }

}
