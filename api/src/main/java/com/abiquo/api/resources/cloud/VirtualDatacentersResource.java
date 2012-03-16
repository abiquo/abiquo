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

import static com.abiquo.api.resources.DatacenterResource.DATACENTER;
import static com.abiquo.api.resources.EnterpriseResource.ENTERPRISE;
import static com.abiquo.api.resources.cloud.VirtualDatacenterResource.createTransferObject;

import java.util.Collection;

import javax.annotation.Resource;
import javax.validation.constraints.Min;
import javax.ws.rs.Consumes;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;

import org.apache.wink.common.annotations.Workspace;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import com.abiquo.api.exceptions.APIError;
import com.abiquo.api.exceptions.NotFoundException;
import com.abiquo.api.resources.AbstractResource;
import com.abiquo.api.resources.DatacenterResource;
import com.abiquo.api.resources.DatacentersResource;
import com.abiquo.api.resources.EnterpriseResource;
import com.abiquo.api.resources.EnterprisesResource;
import com.abiquo.api.services.DatacenterService;
import com.abiquo.api.services.EnterpriseService;
import com.abiquo.api.services.UserService;
import com.abiquo.api.services.cloud.VirtualDatacenterService;
import com.abiquo.api.spring.security.SecurityService;
import com.abiquo.api.util.IRESTBuilder;
import com.abiquo.model.enumerator.Privileges;
import com.abiquo.model.rest.RESTLink;
import com.abiquo.server.core.cloud.VirtualDatacenter;
import com.abiquo.server.core.cloud.VirtualDatacenterDto;
import com.abiquo.server.core.cloud.VirtualDatacentersDto;
import com.abiquo.server.core.enterprise.Enterprise;
import com.abiquo.server.core.infrastructure.Datacenter;

/**
 * @wiki The VirtualDatacenter Resource offers the functionality of managing the virtual datacenters
 */
@Path(VirtualDatacentersResource.VIRTUAL_DATACENTERS_PATH)
@Controller
@Workspace(workspaceTitle = "Abiquo cloud workspace", collectionTitle = "VirtualDatacenters")
public class VirtualDatacentersResource extends AbstractResource
{
    public static final String VIRTUAL_DATACENTERS_PATH = "cloud/virtualdatacenters";

    // @Autowired
    @Resource(name = "virtualDatacenterService")
    private VirtualDatacenterService service;

    @Autowired
    private DatacenterService datacenterService;

    @Autowired
    private EnterpriseService enterpriseService;

    @Autowired
    private UserService userService;

    @Autowired
    private SecurityService securityService;

    /**
     * Returns all virtual datacenters
     * 
     * @title Retrieve all virtual datacenters
     * @param enterpriseId identifier of an enterprise
     * @param datacenterId identifier of a datacenter
     * @param startwith
     * @param orderBy
     * @param filter
     * @param limit
     * @param descOrAsc
     * @param restBuilder a Context-injected object to create the links of the Dto
     * @return a {VirtualDatacentersDto} object with all virtual datacenters
     * @throws Exception
     */
    @GET
    @Produces(VirtualDatacentersDto.MEDIA_TYPE)
    public VirtualDatacentersDto getVirtualDatacenters(
        @QueryParam(ENTERPRISE) final Integer enterpriseId,
        @QueryParam(DATACENTER) final Integer datacenterId,
        @QueryParam(START_WITH) @DefaultValue("0") @Min(0) final Integer startwith,
        @QueryParam(BY) @DefaultValue("name") final String orderBy,
        @QueryParam(FILTER) @DefaultValue("") final String filter,
        @QueryParam(LIMIT) @Min(1) @DefaultValue(DEFAULT_PAGE_LENGTH_STRING) final Integer limit,
        @QueryParam(ASC) @DefaultValue("true") final Boolean descOrAsc,
        @Context final IRESTBuilder restBuilder) throws Exception
    {

        if (!securityService.hasPrivilege(Privileges.VDC_ENUMERATE)
            && !securityService.hasPrivilege(Privileges.ENTERPRISE_ENUMERATE)
            && !securityService.hasPrivilege(Privileges.USERS_MANAGE_OTHER_ENTERPRISES))
        {
            if (enterpriseId != null
                && !enterpriseId.equals(userService.getCurrentUser().getEnterprise().getId()))
            {
                // throws access denied exception
                securityService.requirePrivilege(Privileges.VDC_ENUMERATE);
            }
        }

        Datacenter datacenter = null;
        if (datacenterId != null)
        {
            datacenter = getDatacenter(datacenterId);
        }

        Enterprise enterprise = null;
        if (enterpriseId != null)
        {
            enterprise = getEnterprise(enterpriseId);
        }

        Collection<VirtualDatacenter> all =
            service.getVirtualDatacenters(enterprise, datacenter, null);
        VirtualDatacentersDto vdcs = new VirtualDatacentersDto();

        for (VirtualDatacenter d : all)
        {
            vdcs.add(createTransferObject(d, restBuilder));
        }

        return vdcs;
    }

    /**
     * Creates a virtual datacenter and returns it after creation
     * 
     * @title Create a virtual datacenter
     * @param dto the virtual datacetner to create
     * @param datacenterId identifier of the datacenter
     * @param enterpriseId identifier of the enterprise
     * @param restBuilder a Context-injected object to create the links of the Dto
     * @return a {VirtualDatacenterDto} object with the created virtual datacenter
     * @throws Exception
     */
    @POST
    @Consumes(VirtualDatacenterDto.MEDIA_TYPE)
    @Produces(VirtualDatacenterDto.MEDIA_TYPE)
    public VirtualDatacenterDto postVirtualDatacenter(final VirtualDatacenterDto dto,
        @QueryParam(DATACENTER) final Integer datacenterId,
        @QueryParam(ENTERPRISE) final Integer enterpriseId, @Context final IRESTBuilder restBuilder)
        throws Exception
    {
        Datacenter datacenter = null;
        Enterprise enterprise = null;
        if (datacenterId != null)
        {
            datacenter = getDatacenter(datacenterId);
        }
        else
        {
            datacenter = getDatacenter(dto.searchLink(DATACENTER));
        }

        if (enterpriseId != null)
        {
            enterprise = getEnterprise(enterpriseId);
        }
        else
        {
            enterprise = getEnterprise(dto.searchLink(ENTERPRISE));
        }
        userService.checkCurrentEnterpriseForPostMethods(enterprise);

        VirtualDatacenter vdc = service.createVirtualDatacenter(dto, datacenter, enterprise);

        VirtualDatacenterDto response =
            VirtualDatacenterResource.createTransferObject(vdc, restBuilder);

        return response;
    }

    private Datacenter getDatacenter(final RESTLink datacenterLink)
    {
        Integer datacenterId =
            getLinkId(datacenterLink, DatacentersResource.DATACENTERS_PATH,
                DatacenterResource.DATACENTER_PARAM, DATACENTER, APIError.NON_EXISTENT_DATACENTER);

        return getDatacenter(datacenterId);
    }

    private Datacenter getDatacenter(final Integer datacenterId)
    {
        Datacenter datacenter = datacenterService.getDatacenter(datacenterId);
        if (datacenter == null)
        {
            throw new NotFoundException(APIError.NON_EXISTENT_DATACENTER);
        }

        return datacenter;
    }

    private Enterprise getEnterprise(final RESTLink enterpriseLink)
    {
        Integer enterpriseId =
            getLinkId(enterpriseLink, EnterprisesResource.ENTERPRISES_PATH,
                EnterpriseResource.ENTERPRISE_PARAM, ENTERPRISE, APIError.NON_EXISTENT_ENTERPRISE);

        return getEnterprise(enterpriseId);
    }

    private Enterprise getEnterprise(final Integer enterpriseId)
    {
        Enterprise enterprise = enterpriseService.getEnterprise(enterpriseId);

        return enterprise;
    }

}
