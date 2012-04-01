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

package com.abiquo.api.resources;

import static com.abiquo.server.core.infrastructure.RemoteService.STATUS_ERROR;
import static com.abiquo.server.core.infrastructure.RemoteService.STATUS_SUCCESS;

import java.util.ArrayList;
import java.util.List;

import javax.validation.constraints.NotNull;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;

import org.apache.wink.common.annotations.Parent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import com.abiquo.api.exceptions.APIError;
import com.abiquo.api.exceptions.ConflictException;
import com.abiquo.api.exceptions.NotFoundException;
import com.abiquo.api.services.InfrastructureService;
import com.abiquo.api.util.IRESTBuilder;
import com.abiquo.model.enumerator.RemoteServiceType;
import com.abiquo.model.util.ModelTransformer;
import com.abiquo.server.core.infrastructure.RemoteService;
import com.abiquo.server.core.infrastructure.RemoteServiceDto;
import com.abiquo.server.core.infrastructure.RemoteServicesDto;

@Parent(RemoteServicesResource.class)
@Path(RemoteServiceResource.REMOTE_SERVICE_PARAM)
@Controller
public class RemoteServiceResource extends AbstractResource
{
    @Autowired
    private InfrastructureService service;

    public static final String REMOTE_SERVICE = "remoteservice";

    public static final String REMOTE_SERVICE_PARAM = "{" + REMOTE_SERVICE + "}";

    public static final String CHECK_RESOURCE = "action/" + InfrastructureService.CHECK_RESOURCE;

    /*
     * REST methods
     */

    /**
     * Returns a remote service from a datacenter
     * 
     * @title Retrieve a Remote Service
     * @param datacenterId identifier of the datacenter
     * @param serviceType remote service type to retrieve, must be one of the followings: {
     *            virtualfactory, storagesystemmonitor, virtualsystemmonitor, nodecollector,
     *            dhcpservice, bpmservice, appliancemanager }
     * @param restBuilder a Context-injected object to create the links of the Dto
     * @return a {RemoteSerivceDto} object with the requested remote service
     * @throws Exception
     */
    @GET
    @Produces(RemoteServiceDto.MEDIA_TYPE)
    public RemoteServiceDto getRemoteService(
        @PathParam(DatacenterResource.DATACENTER) final Integer datacenterId,
        @PathParam(REMOTE_SERVICE) @com.abiquo.model.validation.RemoteService @NotNull final String serviceType,
        @Context final IRESTBuilder restBuilder) throws Exception
    {
        validatePathParameters(datacenterId, serviceType);

        RemoteServiceType type = RemoteServiceType.valueFromName(serviceType);
        RemoteService remoteService = service.getRemoteService(datacenterId, type);

        return createTransferObject(remoteService, restBuilder);
    }

    /**
     * Checks a remote service status and updates the state
     * 
     * @title Check the status of a Remote Service
     * @wiki Returns the status of a remoter service in a HTTP response code
     * @param datacenterId identifier of the datacenter
     * @param serviceType remote service type to check, must be one of the followings: {
     *            virtualfactory, storagesystemmonitor, virtualsystemmonitor, nodecollector,
     *            dhcpservice, bpmservice, appliancemanager }
     * @param restBuilder a Context-injected object to create the links of the Dto
     * @throws Exception
     */
    @GET
    @Path(CHECK_RESOURCE)
    public void pingRemoteService(
        @PathParam(DatacenterResource.DATACENTER) final Integer datacenterId,
        @PathParam(REMOTE_SERVICE) @com.abiquo.model.validation.RemoteService @NotNull final String serviceType,
        @Context final IRESTBuilder restBuilder) throws Exception
    {
        validatePathParameters(datacenterId, serviceType);
        RemoteService rs =
            service.getRemoteService(datacenterId, RemoteServiceType.valueFromName(serviceType));
        service.checkRemoteServiceStatus(rs.getDatacenter(), rs.getType(), rs.getUri(), true);
    }

    /**
     * Modifies a remote serivce.
     * 
     * @title Update an existing Remote Service
     * @param datacenterId indentifier of the datacenter
     * @param serviceType remote service type to modify, must be one of the followings: {
     *            virtualfactory, storagesystemmonitor, virtualsystemmonitor, nodecollector,
     *            dhcpservice, bpmservice, appliancemanager }
     * @param remoteService remote service to modify
     * @param restBuilder a Context-injected object to create the links of the Dto
     * @return a {RemoteServiceDto} object with the modified remote service
     * @throws Exception
     */
    @PUT
    @Consumes(RemoteServiceDto.MEDIA_TYPE)
    @Produces(RemoteServiceDto.MEDIA_TYPE)
    public RemoteServiceDto modifyRemoteService(
        @PathParam(DatacenterResource.DATACENTER) final Integer datacenterId,
        @PathParam(REMOTE_SERVICE) @com.abiquo.model.validation.RemoteService @NotNull final String serviceType,
        final RemoteServiceDto remoteService, @Context final IRESTBuilder restBuilder)
        throws Exception
    {
        validatePathParameters(datacenterId, serviceType);

        RemoteServiceType type = RemoteServiceType.valueFromName(serviceType);
        RemoteService old = service.getRemoteService(datacenterId, type);

        if (!old.getType().equals(remoteService.getType()))
        {
            throw new ConflictException(APIError.WRONG_REMOTE_SERVICE_TYPE);
        }

        RemoteServiceDto r = service.modifyRemoteService(old.getId(), remoteService);

        addLinks(restBuilder, r, datacenterId);

        return r;
    }

    /**
     * Deletes a remote serivce
     * 
     * @title Delete a Remote Service
     * @param datacenterId identifier of the datacenter
     * @param serviceType remote service type to delete, must be one of the followings: {
     *            virtualfactory, storagesystemmonitor, virtualsystemmonitor, nodecollector,
     *            dhcpservice, bpmservice, appliancemanager }
     */
    @DELETE
    public void deleteRemoteService(
        @PathParam(DatacenterResource.DATACENTER) final Integer datacenterId,
        @PathParam(REMOTE_SERVICE) @com.abiquo.model.validation.RemoteService @NotNull final String serviceType)
    {
        validatePathParameters(datacenterId, serviceType);

        RemoteServiceType type = RemoteServiceType.valueFromName(serviceType);
        RemoteService remoteService = service.getRemoteService(datacenterId, type);

        service.removeRemoteService(remoteService.getId());
    }

    /*
     * Utility methods TODO: We are duplicating these everywhere, refactor!!
     */

    public static RemoteServiceDto addLinks(final IRESTBuilder restBuilder,
        final RemoteServiceDto remoteService, final Integer datacenterId)
    {
        remoteService.setLinks(restBuilder.buildRemoteServiceLinks(datacenterId, remoteService));
        return remoteService;
    }

    public static RemoteServiceDto createTransferObject(final RemoteService remoteService,
        final IRESTBuilder restBuilder) throws Exception
    {
        RemoteServiceDto dto = createTransferObject(remoteService);

        dto = addLinks(restBuilder, dto, remoteService.getDatacenter().getId());
        return dto;
    }

    public static RemoteServiceDto createTransferObject(final RemoteService remoteService)
    {
        RemoteServiceDto dto = new RemoteServiceDto();
        dto.setId(remoteService.getId());
        dto.setType(remoteService.getType());
        dto.setStatus(remoteService.getStatus());
        dto.setUri(remoteService.getUri());
        return dto;
    }

    private void validatePathParameters(final Integer datacenterId,
        final String remoteServiceMapping) throws NotFoundException
    {
        if (!service.isAssignedTo(datacenterId, remoteServiceMapping))
        {
            throw new NotFoundException(APIError.NOT_ASSIGNED_REMOTE_SERVICE_DATACENTER);
        }
    }

    // Create the persistence objects.
    public static List<RemoteService> createPersistenceObjects(final RemoteServicesDto remoteService)
        throws Exception
    {
        List<RemoteService> rsList = new ArrayList<RemoteService>();
        if (remoteService.getCollection() != null)
        {
            for (RemoteServiceDto rsd : remoteService.getCollection())
            {
                rsList.add(createPersistenceObject(rsd));
            }
        }
        return rsList;
    }

    // Create the persistence object.
    public static RemoteService createPersistenceObject(final RemoteServiceDto remoteService)
        throws Exception
    {
        return ModelTransformer.persistenceFromTransport(RemoteService.class, remoteService);
    }
}
