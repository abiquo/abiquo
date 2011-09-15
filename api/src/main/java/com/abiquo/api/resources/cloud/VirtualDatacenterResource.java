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

import static com.abiquo.api.util.URIResolver.buildPath;

import java.util.List;

import javax.annotation.Resource;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.UriInfo;

import org.apache.wink.common.annotations.Parent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import com.abiquo.api.exceptions.APIError;
import com.abiquo.api.exceptions.BadRequestException;
import com.abiquo.api.resources.AbstractResource;
import com.abiquo.api.services.NetworkService;
import com.abiquo.api.services.UserService;
import com.abiquo.api.services.cloud.VirtualDatacenterService;
import com.abiquo.api.util.IRESTBuilder;
import com.abiquo.api.util.URIResolver;
import com.abiquo.model.rest.RESTLink;
import com.abiquo.model.transport.LinksDto;
import com.abiquo.server.core.cloud.VirtualDatacenter;
import com.abiquo.server.core.cloud.VirtualDatacenterDto;
import com.abiquo.server.core.infrastructure.network.IpPoolManagement;
import com.abiquo.server.core.infrastructure.network.IpsPoolManagementDto;
import com.abiquo.server.core.infrastructure.network.VLANNetwork;
import com.abiquo.server.core.infrastructure.network.VLANNetworkDto;
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

    public static final String ACTION_DEFAULT_VLAN = "/action/defaultvlan";

    // @Autowired
    @Resource(name = "virtualDatacenterService")
    VirtualDatacenterService service;

    @Autowired
    NetworkService netService;

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
        @QueryParam(START_WITH) @Min(0) final Integer startwith,
        @QueryParam(BY) final String orderBy, @QueryParam(FILTER) final String filter,
        @QueryParam(LIMIT) @Min(0) final Integer limit, @QueryParam(ASC) final Boolean desc_or_asc,
        @Context final IRESTBuilder restBuilder) throws Exception
    {
        // Set query Params by default if they are not informed
        Integer firstElem = startwith == null ? 0 : startwith;
        String by = orderBy == null || orderBy.isEmpty() ? "ip" : orderBy;
        String has = filter == null ? "" : filter;
        Integer numElem = limit == null ? DEFAULT_PAGE_LENGTH : limit;
        Boolean asc = desc_or_asc == null ? true : desc_or_asc;

        List<IpPoolManagement> all =
            netService.getListIpPoolManagementByVdc(id, firstElem, numElem, has, by, asc);
        /*
         * if (all == null || all.isEmpty()) { throw new
         * ConflictException(APIError.VIRTUAL_DATACENTER_INVALID_NETWORKS); }
         */

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
            netService.getListIpPoolManagementByVdc(id, 0, DEFAULT_PAGE_LENGTH, "", "ip", true);
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

    // ALERT! this method is @override in enterprise version, any change here
    // should be also changed in enterprise version.
    @GET
    @Path(VirtualDatacenterResource.ACTION_DEFAULT_VLAN)
    public VLANNetworkDto getDefaultVlan(
        @PathParam(VIRTUAL_DATACENTER) @NotNull @Min(1) final Integer id,
        @Context final IRESTBuilder restBuilder) throws Exception
    {
        VLANNetwork vlan = netService.getDefaultNetworkForVirtualDatacenter(id);
        return PrivateNetworkResource.createTransferObject(vlan, id, restBuilder);
    }

    // ALERT! this method is @override in enterprise version, any change here
    // should be also changed in enterprise version.
    @PUT
    @Path(VirtualDatacenterResource.ACTION_DEFAULT_VLAN)
    public void setDefaultVlan(@PathParam(VIRTUAL_DATACENTER) @NotNull @Min(1) final Integer id,
        @NotNull final LinksDto links, @Context final IRESTBuilder restBuilder) throws Exception
    {
        RESTLink privateLink = links.searchLink("internalnetwork");
        if (privateLink == null)
        {
            throw new BadRequestException(APIError.INVALID_LINK);
        }

        // Parse the URI with the expected parameters and extract the identifier values.
        String buildPath =
            buildPath(VirtualDatacentersResource.VIRTUAL_DATACENTERS_PATH,
                VirtualDatacenterResource.VIRTUAL_DATACENTER_PARAM,
                PrivateNetworksResource.PRIVATE_NETWORKS_PATH,
                PrivateNetworkResource.PRIVATE_NETWORK_PARAM);
        MultivaluedMap<String, String> ipsValues =
            URIResolver.resolveFromURI(buildPath, privateLink.getHref());
        // Private IP must belong to the same Virtual Datacenter where the Virtual Machine
        // belongs to.
        Integer vdcId =
            Integer.parseInt(ipsValues.getFirst(VirtualDatacenterResource.VIRTUAL_DATACENTER));
        if (!vdcId.equals(id))
        {
            throw new BadRequestException(APIError.VLANS_IP_LINK_INVALID_VDC);
        }
        // Extract the vlanId and ipId values to execute the association.
        Integer vlanId =
            Integer.parseInt(ipsValues.getFirst(PrivateNetworkResource.PRIVATE_NETWORK));

        netService.setInternalNetworkAsDefaultInVirtualDatacenter(vdcId, vlanId);
    }

    public static VirtualDatacenterDto createTransferObject(final VirtualDatacenter vdc,
        final IRESTBuilder builder) throws Exception
    {
        VirtualDatacenterDto response = createTransferObject(vdc);
        VLANNetworkDto vlan =
            PrivateNetworkResource.createTransferObject(vdc.getDefaultVlan(), vdc.getId(), builder);
        response.setVlan(vlan);
        response.setLinks(builder.buildVirtualDatacenterLinks(vdc, vdc.getDatacenter().getId(), vdc
            .getEnterprise().getId()));

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
