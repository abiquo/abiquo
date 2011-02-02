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

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.Context;

import org.apache.wink.common.annotations.Parent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import com.abiquo.api.exceptions.APIError;
import com.abiquo.api.exceptions.NotFoundException;
import com.abiquo.api.resources.AbstractResource;
import com.abiquo.api.services.PrivateNetworkService;
import com.abiquo.api.transformer.ModelTransformer;
import com.abiquo.api.util.IRESTBuilder;
import com.abiquo.server.core.infrastructure.network.NetworkConfigurationDto;
import com.abiquo.server.core.infrastructure.network.VLANNetwork;
import com.abiquo.server.core.infrastructure.network.VLANNetworkDto;

@Parent(PrivateNetworksResource.class)
@Path(PrivateNetworkResource.PRIVATE_NETWORK_PARAM)
@Controller
public class PrivateNetworkResource extends AbstractResource
{
    public static final String PRIVATE_NETWORK = "privatenetwork";

    public static final String PRIVATE_NETWORK_PARAM = "{" + PRIVATE_NETWORK + "}";

    @Autowired
    PrivateNetworkService service;

    @GET
    public VLANNetworkDto getPrivateNetwork(
        @PathParam(VirtualDatacenterResource.VIRTUAL_DATACENTER) Integer virtualDatacenterId,
        @PathParam(PRIVATE_NETWORK) Integer networkId, @Context IRESTBuilder restBuilder)
        throws Exception
    {
        validatePathParameters(virtualDatacenterId, networkId);

        VLANNetwork network = service.getNetwork(networkId);

        return createTransferObject(network, virtualDatacenterId, restBuilder);
    }

    private static VLANNetworkDto addLinks(IRESTBuilder restBuilder, VLANNetworkDto network,
        Integer virtualDatacenterId)
    {
        network.setLinks(restBuilder.buildPrivateNetworkLinks(virtualDatacenterId, network));
        return network;
    }

    public static VLANNetworkDto createTransferObject(VLANNetwork network,
        Integer virtualDatacenterId, IRESTBuilder restBuilder) throws Exception
    {
        VLANNetworkDto dto =
            ModelTransformer.transportFromPersistence(VLANNetworkDto.class, network);

        dto.setNetworkConfiguration(ModelTransformer.transportFromPersistence(
            NetworkConfigurationDto.class, network.getConfiguration()));

        dto = addLinks(restBuilder, dto, virtualDatacenterId);

        return dto;
    }

    // TODO createPersistenceObject

    private void validatePathParameters(final Integer virtualDatacenterId, final Integer networkId)
        throws NotFoundException
    {
        if (!service.isAssignedTo(virtualDatacenterId, networkId))
        {
            throw new NotFoundException(APIError.NOT_ASSIGNED_NETWORK_VIRTUAL_DATACENTER);
        }
    }
}
