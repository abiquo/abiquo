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

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.Context;

import org.apache.wink.common.annotations.Parent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import com.abiquo.api.resources.AbstractResource;
import com.abiquo.api.services.NetworkService;
import com.abiquo.api.util.IRESTBuilder;
import com.abiquo.server.core.infrastructure.network.NetworkConfiguration;
import com.abiquo.model.util.ModelTransformer;
import com.abiquo.server.core.infrastructure.network.VLANNetwork;
import com.abiquo.server.core.infrastructure.network.VLANNetworkDto;
import com.abiquo.server.core.util.network.IPNetworkRang;

@Parent(PrivateNetworksResource.class)
@Path(PrivateNetworkResource.PRIVATE_NETWORK_PARAM)
@Controller
public class PrivateNetworkResource extends AbstractResource
{
    public static final String PRIVATE_NETWORK = "privatenetwork";

    public static final String PRIVATE_NETWORK_PARAM = "{" + PRIVATE_NETWORK + "}";

    @Autowired
    NetworkService service;

    @GET
    public VLANNetworkDto getPrivateNetwork(
        @PathParam(VirtualDatacenterResource.VIRTUAL_DATACENTER) @NotNull @Min(1) final Integer virtualDatacenterId,
        @PathParam(PRIVATE_NETWORK) @NotNull @Min(1) final Integer vlanId,
        @Context final IRESTBuilder restBuilder) throws Exception
    {
        VLANNetwork network = service.getPrivateNetwork(virtualDatacenterId, vlanId);
        return createTransferObject(network, virtualDatacenterId, restBuilder);
    }

    /**
     * Updates a private network.
     * 
     * @param vdcId identifier of the virtual datacenter.
     * @param vlanId identifier of the vlan.
     * @param dto object with the new data to modify
     * @param restBuilder Context-injected REST link builder.
     * @return the Dto with the resulting object.
     * @throws Exception for any exception.
     */
    @PUT
    public VLANNetworkDto updatePrivateNetwork(
        @PathParam(VirtualDatacenterResource.VIRTUAL_DATACENTER) @NotNull @Min(1) final Integer vdcId,
        @PathParam(PRIVATE_NETWORK) @NotNull @Min(1) final Integer vlanId,
        final VLANNetworkDto dto, @Context final IRESTBuilder restBuilder) throws Exception
    {
        VLANNetwork newNetwork = createPersistenceObject(dto);
        newNetwork = service.updatePrivateNetwork(vdcId, vlanId, newNetwork);
        return createTransferObject(newNetwork, vdcId, restBuilder);
    }

    /**
     * Delete an existing private network.
     * 
     * @param vdcId identifier of the virtual datacenter.
     * @param vlanId identifier of the vlan
     * @param restBuilder Context-injected REST link builder.
     */
    @DELETE
    public void deletePrivateNetwork(
        @PathParam(VirtualDatacenterResource.VIRTUAL_DATACENTER) @NotNull @Min(1) final Integer vdcId,
        @PathParam(PRIVATE_NETWORK) @NotNull @Min(1) final Integer vlanId,
        @Context final IRESTBuilder restBuilder)
    {
        service.deletePrivateNetwork(vdcId, vlanId);
    }

    private static VLANNetworkDto addLinks(final IRESTBuilder restBuilder,
        final VLANNetworkDto network, final Integer virtualDatacenterId)
    {
        network.setLinks(restBuilder.buildPrivateNetworkLinks(virtualDatacenterId, network));
        return network;
    }

    public static VLANNetworkDto createTransferObject(final VLANNetwork network,
        final Integer virtualDatacenterId, final IRESTBuilder restBuilder) throws Exception
    {
        VLANNetworkDto dto =
            ModelTransformer.transportFromPersistence(VLANNetworkDto.class, network);

        dto.setId(network.getId());
        dto.setAddress(network.getConfiguration().getAddress());
        dto.setGateway(network.getConfiguration().getGateway());
        dto.setMask(network.getConfiguration().getMask());
        dto.setPrimaryDNS(network.getConfiguration().getPrimaryDNS());
        dto.setSecondaryDNS(network.getConfiguration().getSecondaryDNS());
        dto.setSufixDNS(network.getConfiguration().getSufixDNS());

        dto = addLinks(restBuilder, dto, virtualDatacenterId);

        return dto;
    }

    public static VLANNetwork createPersistenceObject(final VLANNetworkDto dto) throws Exception
    {

        VLANNetwork vlan = ModelTransformer.persistenceFromTransport(VLANNetwork.class, dto);

        vlan.setConfiguration(new NetworkConfiguration(dto.getAddress(),
            dto.getMask(),
            IPNetworkRang.transformIntegerMaskToIPMask(dto.getMask()).toString(),
            dto.getGateway(),
            "bridge"));

        vlan.getConfiguration().setPrimaryDNS(dto.getPrimaryDNS());
        vlan.getConfiguration().setSecondaryDNS(dto.getSecondaryDNS());
        vlan.getConfiguration().setSufixDNS(dto.getSufixDNS());

        return vlan;
    }
}
