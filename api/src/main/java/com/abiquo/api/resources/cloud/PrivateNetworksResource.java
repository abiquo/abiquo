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

import static com.abiquo.api.resources.cloud.PrivateNetworkResource.createTransferObject;

import java.util.Collection;

import javax.validation.constraints.Min;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.Context;

import org.apache.wink.common.annotations.Parent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import com.abiquo.api.exceptions.APIError;
import com.abiquo.api.exceptions.NotFoundException;
import com.abiquo.api.resources.AbstractResource;
import com.abiquo.api.services.NetworkService;
import com.abiquo.api.util.IRESTBuilder;
import com.abiquo.server.core.infrastructure.network.VLANNetwork;
import com.abiquo.server.core.infrastructure.network.VLANNetworkDto;
import com.abiquo.server.core.infrastructure.network.VLANNetworksDto;

@Parent(VirtualDatacenterResource.class)
@Path(PrivateNetworksResource.PRIVATE_NETWORKS_PATH)
@Controller
public class PrivateNetworksResource extends AbstractResource
{
    public static final String PRIVATE_NETWORKS_PATH = "privatenetworks";

    @Autowired
    private NetworkService service;

    @GET
    public VLANNetworksDto getPrivateNetworks(
        @PathParam(VirtualDatacenterResource.VIRTUAL_DATACENTER) final Integer virtualDatacenterId,
        @Context final IRESTBuilder restBuilder) throws Exception
    {
        Collection<VLANNetwork> all = service.getPrivateNetworks(virtualDatacenterId);

        if (all == null)
        {
            throw new NotFoundException(APIError.NON_EXISTENT_VIRTUAL_DATACENTER);
        }

        VLANNetworksDto networks = new VLANNetworksDto();

        for (VLANNetwork n : all)
        {
            networks.add(createTransferObject(n, virtualDatacenterId, restBuilder));
        }

        return networks;
    }

    @POST
    public VLANNetworkDto createNetwork(
        @PathParam(VirtualDatacenterResource.VIRTUAL_DATACENTER) @Min(0) final Integer virtualDatacenterId,
        final VLANNetworkDto dto, @Context final IRESTBuilder restBuilder) throws Exception
    {
        VLANNetwork network = PrivateNetworkResource.createPersistenceObject(dto);
        network = service.createPrivateNetwork(virtualDatacenterId, network);
        return PrivateNetworkResource.createTransferObject(network, virtualDatacenterId,
            restBuilder);
    }

}
