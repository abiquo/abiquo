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

import java.util.List;

import javax.annotation.Resource;
import javax.validation.constraints.Min;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.UriInfo;

import org.apache.wink.common.annotations.Parent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import com.abiquo.api.exceptions.APIError;
import com.abiquo.api.exceptions.ConflictException;
import com.abiquo.api.exceptions.NotFoundException;
import com.abiquo.api.resources.AbstractResource;
import com.abiquo.api.services.IpAddressService;
import com.abiquo.api.services.UserService;
import com.abiquo.api.services.cloud.VirtualDatacenterService;
import com.abiquo.api.util.IRESTBuilder;
import com.abiquo.server.core.cloud.VirtualDatacenter;
import com.abiquo.server.core.cloud.VirtualDatacenterDto;
import com.abiquo.server.core.infrastructure.network.IpPoolManagement;
import com.abiquo.server.core.infrastructure.network.IpsPoolManagementDto;
import com.abiquo.server.core.util.PagedList;

@Parent(VirtualDatacentersResource.class)
@Path(VirtualDatacenterResource.VIRTUAL_DATACENTER_PARAM)
@Controller
public class VirtualDatacenterResource extends AbstractResource
{
    public static final String VIRTUAL_DATACENTER = "virtualdatacenter";

    public static final String VIRTUAL_DATACENTER_PARAM = "{" + VIRTUAL_DATACENTER + "}";

    public static final String VIRTUAL_DATACENTER_ACTION_GET_IPS = "/action/ips";

    public static final String VIRTUAL_DATACENTER_ACTION_GET_DHCP_INFO = "/action/dhcpinfo";

    // @Autowired
    @Resource(name = "virtualDatacenterService")
    VirtualDatacenterService service;

    @Autowired
    IpAddressService ipService;

    @Autowired
    UserService userService;
    
    @Context
    UriInfo uriInfo;

    @GET
    public VirtualDatacenterDto getVirtualDatacenter(
        @PathParam(VIRTUAL_DATACENTER) final Integer id, @Context final IRESTBuilder restBuilder)
        throws Exception
    {
        VirtualDatacenter vdc = service.getVirtualDatacenter(id);
        return createTransferObject(vdc, restBuilder);
    }

    @PUT
    public VirtualDatacenterDto updateVirtualDatacenter(
        @PathParam(VIRTUAL_DATACENTER) final Integer id, final VirtualDatacenterDto dto,
        @Context final IRESTBuilder restBuilder) throws Exception
    {
        VirtualDatacenter vdc = service.updateVirtualDatacenter(id, dto);
        userService.checkCurrentEnterpriseForPostMethods(vdc.getEnterprise());
        return createTransferObject(vdc, restBuilder);
    }

    @DELETE
    public void deleteVirtualDatacenter(@PathParam(VIRTUAL_DATACENTER) final Integer id)
    {
        service.deleteVirtualDatacenter(id);
    }

    @SuppressWarnings("unchecked")
    @GET
    @Path(VirtualDatacenterResource.VIRTUAL_DATACENTER_ACTION_GET_IPS)
    public IpsPoolManagementDto getIPsByVirtualDatacenter(
        @PathParam(VIRTUAL_DATACENTER) final Integer id,
        @QueryParam(START_WITH) @Min(0) final Integer startwith, @QueryParam(BY) final String orderBy,
        @QueryParam(FILTER) final String filter, @QueryParam(LIMIT) @Min(0)final Integer limit,
        @QueryParam(ASC) final Boolean desc_or_asc, @Context final IRESTBuilder restBuilder)
        throws Exception
    {
        // Set query Params by default if they are not informed
        Integer firstElem = (startwith == null) ? 0 : startwith;
        String by = (orderBy == null || orderBy.isEmpty()) ? "ip" : orderBy;
        String has = (filter == null) ? "" : filter;
        Integer numElem = (limit == null) ? DEFAULT_PAGE_LENGTH : limit;
        Boolean asc = (desc_or_asc == null) ? true : desc_or_asc;

        List<IpPoolManagement> all =
            ipService.getListIpPoolManagementByVdc(id, firstElem, numElem, has, by, asc);
        /*
        if (all == null || all.isEmpty())
        {
            throw new ConflictException(APIError.VIRTUAL_DATACENTER_INVALID_NETWORKS);
        }*/

        IpsPoolManagementDto ips = new IpsPoolManagementDto();

        for (IpPoolManagement ip : all)
        {
            ips.add(IpAddressesResource.createTransferObject(ip, restBuilder));
        }

        ips.setTotalSize(((PagedList) all).getTotalResults());
        ips.setLinks(restBuilder.buildPaggingLinks(uriInfo.getAbsolutePath().toString(),
            (PagedList) all));

        return ips;
    }

    @GET
    @Path(VirtualDatacenterResource.VIRTUAL_DATACENTER_ACTION_GET_DHCP_INFO)
    public String getDHCPInfoByVirtualDatacenter(@PathParam(VIRTUAL_DATACENTER) final Integer id,
        @Context final IRESTBuilder restBuilder) throws Exception
    {
        List<IpPoolManagement> all =
            ipService.getListIpPoolManagementByVdc(id, 0, DEFAULT_PAGE_LENGTH, "", "ip", true);
        StringBuilder formattedData = new StringBuilder();
        formattedData.append("## AbiCloud DHCP configuration for network "
            + service.getVirtualDatacenter(id).getNetwork().getUuid() + "\n");
        formattedData
            .append("## Please copy and paste the following lines into your DHCP server\n");
        for (IpPoolManagement ipPool : all)
        {

            formattedData.append("host " + ipPool.getName() + " {\n");

            // VirtualBox mac format
            if (!ipPool.getMac().contains(":"))
            {
                String unformattedMA = ipPool.getMac();
                StringBuilder formattedMA = new StringBuilder(unformattedMA.substring(0, 2) + ":");
                formattedMA.append(unformattedMA.substring(2, 4) + ":");
                formattedMA.append(unformattedMA.substring(4, 6) + ":");
                formattedMA.append(unformattedMA.substring(6, 8) + ":");
                formattedMA.append(unformattedMA.substring(8, 10) + ":");
                formattedMA.append(unformattedMA.substring(10, 12));
                formattedData.append("\thardware ethernet " + formattedMA + ";\n");

            }
            else
            {
                formattedData.append("\thardware ethernet " + ipPool.getMac() + ";\n");
            }
            formattedData.append("\tfixed-address " + ipPool.getIp() + ";\n");
            formattedData.append("}\n\n");

        }
        return formattedData.toString();
    }

    private static VirtualDatacenterDto addLinks(final IRESTBuilder builder,
        final VirtualDatacenterDto vdc, final Integer datacenterId, final Integer enterpriseId)
    {
        vdc.setLinks(builder.buildVirtualDatacenterLinks(vdc, datacenterId, enterpriseId));

        return vdc;
    }

    public static VirtualDatacenterDto createTransferObject(final VirtualDatacenter vdc,
        final IRESTBuilder builder) throws Exception
    {
        VirtualDatacenterDto response = createTransferObject(vdc);
        response =
            addLinks(builder, response, vdc.getDatacenter().getId(), vdc.getEnterprise().getId());

        return response;
    }

    public static VirtualDatacenterDto createTransferObject(final VirtualDatacenter vdc)
    {
        VirtualDatacenterDto response = new VirtualDatacenterDto();
        response.setId(vdc.getId());
        response.setHypervisorType(vdc.getHypervisorType());
        response.setName(vdc.getName());
        response.setCpuCountLimits(vdc.getCpuCountSoftLimit().intValue(), vdc
            .getCpuCountHardLimit().intValue());
        response.setHdLimitsInMb(vdc.getHdSoftLimitInMb(), vdc.getHdHardLimitInMb());
        response.setRamLimitsInMb(vdc.getRamSoftLimitInMb().intValue(), vdc.getRamHardLimitInMb()
            .intValue());
        response.setStorageLimits(vdc.getStorageSoft(), vdc.getStorageHard());
        response.setVlansLimits(vdc.getVlanSoft(), vdc.getVlanHard());
        response.setPublicIPLimits(vdc.getPublicIpsSoft(), vdc.getPublicIpsHard());
        return response;
    }

}
