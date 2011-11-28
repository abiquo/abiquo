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

import java.net.URLDecoder;
import java.util.Collection;
import java.util.List;

import javax.validation.constraints.Min;
import javax.ws.rs.DELETE;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.UriInfo;

import org.apache.wink.common.annotations.Parent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import com.abiquo.api.exceptions.APIError;
import com.abiquo.api.exceptions.InternalServerErrorException;
import com.abiquo.api.resources.cloud.IpAddressesResource;
import com.abiquo.api.resources.cloud.VirtualDatacenterResource;
import com.abiquo.api.resources.cloud.VirtualMachinesResource;
import com.abiquo.api.services.DatacenterService;
import com.abiquo.api.services.EnterpriseService;
import com.abiquo.api.services.NetworkService;
import com.abiquo.api.services.UserService;
import com.abiquo.api.services.cloud.VirtualApplianceService;
import com.abiquo.api.services.cloud.VirtualDatacenterService;
import com.abiquo.api.services.cloud.VirtualMachineService;
import com.abiquo.api.spring.security.SecurityService;
import com.abiquo.api.util.IRESTBuilder;
import com.abiquo.model.enumerator.Privileges;
import com.abiquo.server.core.cloud.NodeVirtualImage;
import com.abiquo.server.core.cloud.VirtualAppliance;
import com.abiquo.server.core.cloud.VirtualDatacenter;
import com.abiquo.server.core.cloud.VirtualDatacentersDto;
import com.abiquo.server.core.cloud.VirtualMachine;
import com.abiquo.server.core.cloud.VirtualMachinesDto;
import com.abiquo.server.core.enterprise.Enterprise;
import com.abiquo.server.core.enterprise.EnterpriseDto;
import com.abiquo.server.core.enterprise.User;
import com.abiquo.server.core.infrastructure.network.IpPoolManagement;
import com.abiquo.server.core.infrastructure.network.IpsPoolManagementDto;
import com.abiquo.server.core.util.PagedList;

@Parent(EnterprisesResource.class)
@Path(EnterpriseResource.ENTERPRISE_PARAM)
@Controller
public class EnterpriseResource extends AbstractResource
{
    public static final String ENTERPRISE = "enterprise";

    // enterprise as query param
    public static final String ENTERPRISE_AS_PARAM = "identerprise";

    public static final String ENTERPRISE_PARAM = "{" + ENTERPRISE + "}";

    public static final String ENTERPRISE_ACTION_GET_IPS_PATH = "action/ips";

    public static final String ENTERPRISE_ACTION_GET_VIRTUALMACHINES_PATH =
        "action/virtualmachines";

    public static final String ENTERPRISE_ACTION_GET_VIRTUALDATACENTERS_PATH =
        "action/virtualdatacenters";

    protected static final Logger LOGGER = LoggerFactory.getLogger(EnterpriseResource.class);

    @Autowired
    EnterpriseService service;

    @Autowired
    NetworkService netService;

    @Autowired
    VirtualMachineService vmService;

    @Autowired
    DatacenterService dcService;

    @Autowired
    VirtualDatacenterService vdcService;

    @Autowired
    VirtualApplianceService vappService;

    @Context
    UriInfo uriInfo;

    @Autowired
    UserService userService;

    @Autowired
    SecurityService securityService;

    @GET
    public EnterpriseDto getEnterprise(@PathParam(ENTERPRISE) final Integer enterpriseId,
        @Context final IRESTBuilder restBuilder) throws Exception
    {
        if (!securityService.hasPrivilege(Privileges.USERS_VIEW))
        {
            User currentUser = userService.getCurrentUser();
            if (currentUser.getEnterprise().getId().equals(enterpriseId))
            {
                Enterprise enterprise = service.getEnterprise(enterpriseId);
                return createTransferObject(enterprise, restBuilder);
            }
            // We need to return the enterprise of the external VLAN to edit it,
            // and for that wee need to have DC_ENUMERATE privilege.
            else if (securityService.hasPrivilege(Privileges.PHYS_DC_ENUMERATE))
            {
                Enterprise enterprise = service.getEnterprise(enterpriseId);
                return createTransferObject(enterprise, restBuilder);
            }
            else
            {
                // throws access denied exception
                securityService.requirePrivilege(Privileges.USERS_VIEW);
            }

        }

        Enterprise enterprise = service.getEnterprise(enterpriseId);

        return createTransferObject(enterprise, restBuilder);
    }

    @PUT
    public EnterpriseDto modifyEnterprise(final EnterpriseDto enterprise,
        @PathParam(ENTERPRISE) final Integer enterpriseId, @Context final IRESTBuilder restBuilder)
        throws Exception
    {
        Enterprise e = service.modifyEnterprise(enterpriseId, enterprise);

        return createTransferObject(e, restBuilder);
    }

    @DELETE
    public void deleteEnterprise(@PathParam(ENTERPRISE) final Integer enterpriseId)
    {
        service.removeEnterprise(enterpriseId);
    }

    @SuppressWarnings("unchecked")
    @GET
    @Path(EnterpriseResource.ENTERPRISE_ACTION_GET_IPS_PATH)
    public IpsPoolManagementDto getIPsByEnterprise(@PathParam(ENTERPRISE) @Min(0) final Integer id,
        @QueryParam(START_WITH) @DefaultValue("0") @Min(0) final Integer startwith,
        @QueryParam(BY) @DefaultValue("ip") final String orderBy,
        @QueryParam(FILTER) @DefaultValue("") final String filter,
        @QueryParam(LIMIT) @DefaultValue(DEFAULT_PAGE_LENGTH_STRING) @Min(1) final Integer limit,
        @QueryParam(ASC) @DefaultValue("true") final Boolean desc_or_asc,
        @Context final IRESTBuilder restBuilder) throws Exception
    {

        // Set query Params by default if they are not informed
        String filterwith = URLDecoder.decode(filter, "UTF-8");
        List<IpPoolManagement> all =
            netService.getListIpPoolManagementByEnterprise(id, startwith, limit, filterwith,
                orderBy, desc_or_asc);

        if (all == null)
        {
            LOGGER.error("Unexpected null value getting the list of ip pools by enterprise.");
            throw new InternalServerErrorException(APIError.INTERNAL_SERVER_ERROR);
        }

        IpsPoolManagementDto ips = new IpsPoolManagementDto();

        for (IpPoolManagement ip : all)
        {
            ips.add(IpAddressesResource.createTransferObject(ip, restBuilder));
        }
        ips.setTotalSize(((PagedList) all).getTotalResults());
        ips.addLinks(restBuilder.buildPaggingLinks(uriInfo.getAbsolutePath().toString(),
            (PagedList) all));

        return ips;
    }

    /**
     * Retrieves the list Of Virtual machines defined into an enterprise.
     * 
     * @param enterpriseId identifier of the enterprise
     * @param restBuilder {@linnk IRESTBuilder} object injected by context
     * @return the {@link VirtualMachinesDto} object. A {@link VirtualMachineDto} wrapper.
     * @throws Exception
     */
    @GET
    @Path(EnterpriseResource.ENTERPRISE_ACTION_GET_VIRTUALMACHINES_PATH)
    public VirtualMachinesDto getVirtualMachines(
        @PathParam(EnterpriseResource.ENTERPRISE) final Integer enterpriseId,
        @Context final IRESTBuilder restBuilder) throws Exception
    {

        Enterprise enterprise = service.getEnterprise(enterpriseId);
        Collection<NodeVirtualImage> nvimgs =
            vdcService.getNodeVirtualImageByEnterprise(enterprise);

        VirtualMachinesDto vmDto = new VirtualMachinesDto();
        for (NodeVirtualImage nvimg : nvimgs)
        {
            VirtualAppliance vapp = nvimg.getVirtualAppliance();
            VirtualMachine vm = nvimg.getVirtualMachine();

            vmDto.add(VirtualMachinesResource.createCloudAdminTransferObject(vm, vapp
                .getVirtualDatacenter().getId(), vapp.getId(), restBuilder));
        }
        return vmDto;

    }

    /**
     * Retrieves the list Of Virtual datacenters defined into an enterprise.
     * 
     * @param enterpriseId identifier of the enterprise
     * @param restBuilder {@linnk IRESTBuilder} object injected by context
     * @return the {@link VirtualDatacentersDto} object. A {@link VirtualDatacenterDto} wrapper.
     * @throws Exception
     */
    @GET
    @Path(EnterpriseResource.ENTERPRISE_ACTION_GET_VIRTUALDATACENTERS_PATH)
    public VirtualDatacentersDto getVirtualDatacenters(
        @PathParam(EnterpriseResource.ENTERPRISE) final Integer enterpriseId,
        @Context final IRESTBuilder restBuilder) throws Exception
    {

        Enterprise enterprise = service.getEnterprise(enterpriseId);

        Collection<VirtualDatacenter> all = vdcService.getVirtualDatacenters(enterprise, null);
        VirtualDatacentersDto vdcs = new VirtualDatacentersDto();

        for (VirtualDatacenter d : all)
        {
            vdcs.add(VirtualDatacenterResource.createTransferObject(d, restBuilder));
        }

        return vdcs;

    }

    private static EnterpriseDto addLinks(final IRESTBuilder restBuilder,
        final EnterpriseDto enterprise)
    {
        enterprise.setLinks(restBuilder.buildEnterpriseLinks(enterprise));

        return enterprise;
    }

    public static EnterpriseDto createTransferObject(final Enterprise e,
        final IRESTBuilder restBuilder) throws Exception
    {
        EnterpriseDto dto = new EnterpriseDto();
        dto.setId(e.getId());
        dto.setName(e.getName());
        dto.setCpuCountHardLimit(e.getCpuCountHardLimit().intValue());
        dto.setCpuCountSoftLimit(e.getCpuCountSoftLimit().intValue());
        dto.setHdHardLimitInMb(e.getHdHardLimitInMb());
        dto.setHdSoftLimitInMb(e.getHdSoftLimitInMb());
        dto.setPublicIpsHard(e.getPublicIpsHard());
        dto.setPublicIpsSoft(e.getPublicIpsSoft());
        dto.setVlansHard(e.getVlanHard());
        dto.setVlansSoft(e.getVlanSoft());
        dto.setRamHardLimitInMb(e.getRamHardLimitInMb().intValue());
        dto.setRamSoftLimitInMb(e.getRamSoftLimitInMb().intValue());
        dto.setStorageHard(e.getStorageHard());
        dto.setStorageSoft(e.getStorageSoft());
        dto.setRepositorySoft(e.getRepositorySoft());
        dto.setRepositoryHard(e.getRepositoryHard());
        dto.setIsReservationRestricted(e.getIsReservationRestricted());
        if (e.getPricingTemplate() != null)
        {
            dto.setIdPricingTemplate(e.getPricingTemplate().getId());
        }

        dto = addLinks(restBuilder, dto);
        return dto;
    }

}
