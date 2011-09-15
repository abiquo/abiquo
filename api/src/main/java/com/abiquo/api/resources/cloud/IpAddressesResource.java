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

/**
 * 
 */
package com.abiquo.api.resources.cloud;

import java.util.List;

import javax.validation.constraints.Min;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.UriInfo;

import org.apache.wink.common.annotations.Parent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import com.abiquo.api.resources.AbstractResource;
import com.abiquo.api.services.NetworkService;
import com.abiquo.api.util.IRESTBuilder;
import com.abiquo.model.util.ModelTransformer;
import com.abiquo.server.core.infrastructure.network.IpPoolManagement;
import com.abiquo.server.core.infrastructure.network.IpPoolManagementDto;
import com.abiquo.server.core.infrastructure.network.IpsPoolManagementDto;
import com.abiquo.server.core.util.PagedList;

/**
 * @author jdevesa
 */
@Parent(PrivateNetworkResource.class)
@Path(IpAddressesResource.IP_ADDRESSES)
@Controller
public class IpAddressesResource extends AbstractResource
{

    public static final String IP_ADDRESSES = "ips";

    public static final String ONLYAVAILABLE = "onlyAvailable";

    public static final String FREE_IPS = "free";

    public static final String IP_ADDRESS = "ip";

    public static final String IP_ADDRESS_PARAM = "{" + IP_ADDRESS + "}";

    @Autowired
    private NetworkService service;

    @Context
    UriInfo uriInfo;

    @GET
    public IpsPoolManagementDto getIPAddresses(
        @PathParam(VirtualDatacenterResource.VIRTUAL_DATACENTER) final Integer vdcId,
        @PathParam(PrivateNetworkResource.PRIVATE_NETWORK) final Integer vlanId,
        @QueryParam(START_WITH) @DefaultValue("0") @Min(0) final Integer startwith,
        @QueryParam(BY) @DefaultValue("ip") final String orderBy,
        @QueryParam(FILTER) @DefaultValue("") final String filter,
        @QueryParam(LIMIT) @Min(0) @DefaultValue(DEFAULT_PAGE_LENGTH_STRING) final Integer limit,
        @QueryParam(ASC) @DefaultValue("true") final Boolean descOrAsc,
        @QueryParam(ONLYAVAILABLE) @DefaultValue("false") final Boolean available,
        @QueryParam(FREE_IPS) @DefaultValue("false") final Boolean freeIps,
        @Context final IRESTBuilder restBuilder) throws Exception
    {

        List<IpPoolManagement> all =
            service.getListIpPoolManagementByVlan(vdcId, vlanId, startwith, orderBy, filter, limit,
                descOrAsc, freeIps);

        IpsPoolManagementDto ips = new IpsPoolManagementDto();

        for (IpPoolManagement ip : all)
        {
            ips.add(createTransferObject(ip, restBuilder));
        }

        ips.addLinks(restBuilder.buildPaggingLinks(uriInfo.getAbsolutePath().toString(),
            (PagedList) all));
        ips.setTotalSize(((PagedList) all).getTotalResults());

        return ips;
    }

    public static IpPoolManagementDto createTransferObject(final IpPoolManagement ip,
        final IRESTBuilder restBuilder) throws Exception
    {
        IpPoolManagementDto dto =
            ModelTransformer.transportFromPersistence(IpPoolManagementDto.class, ip);

        // Create the links to the resources where the IP object is assigned to
        dto.addLinks(restBuilder.buildIpRasdLinks(ip));
        dto.addLinks(restBuilder.buildRasdLinks(ip));

        return dto;
    }
}
