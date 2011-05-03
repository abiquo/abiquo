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
import static com.abiquo.api.util.URIResolver.buildPath;
import static com.abiquo.api.util.URIResolver.resolveFromURI;

import java.util.Collection;

import javax.annotation.Resource;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MultivaluedMap;

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
import com.abiquo.api.services.cloud.VirtualDatacenterService;
import com.abiquo.api.util.IRESTBuilder;
import com.abiquo.model.rest.RESTLink;
import com.abiquo.server.core.cloud.VirtualDatacenter;
import com.abiquo.server.core.cloud.VirtualDatacenterDto;
import com.abiquo.server.core.cloud.VirtualDatacentersDto;
import com.abiquo.server.core.enterprise.Enterprise;
import com.abiquo.server.core.infrastructure.Datacenter;

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

    @GET
    public VirtualDatacentersDto getVirtualDatacenters(
        @QueryParam(ENTERPRISE) Integer enterpriseId, @QueryParam(DATACENTER) Integer datacenterId,
        @Context IRESTBuilder restBuilder) throws Exception
    {
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

        Collection<VirtualDatacenter> all = service.getVirtualDatacenters(enterprise, datacenter);
        VirtualDatacentersDto vdcs = new VirtualDatacentersDto();

        for (VirtualDatacenter d : all)
        {
            vdcs.add(createTransferObject(d, restBuilder));
        }

        return vdcs;
    }

    @POST
    public VirtualDatacenterDto postVirtualDatacenter(VirtualDatacenterDto dto,
        @QueryParam(DATACENTER) Integer datacenterId, @QueryParam(ENTERPRISE) Integer enterpriseId,
        @Context IRESTBuilder restBuilder) throws Exception
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

        VirtualDatacenter vdc = service.createVirtualDatacenter(dto, datacenter, enterprise);

        VirtualDatacenterDto response =
            VirtualDatacenterResource.createTransferObject(vdc, restBuilder);

        return response;
    }

    private Datacenter getDatacenter(RESTLink datacenterLink)
    {
        Integer datacenterId =
            getLinkId(datacenterLink, DatacentersResource.DATACENTERS_PATH,
                DatacenterResource.DATACENTER_PARAM, DATACENTER, APIError.NON_EXISTENT_DATACENTER);

        return getDatacenter(datacenterId);
    }

    private Datacenter getDatacenter(Integer datacenterId)
    {
        Datacenter datacenter = datacenterService.getDatacenter(datacenterId);
        if (datacenter == null)
        {
            throw new NotFoundException(APIError.NON_EXISTENT_DATACENTER);
        }

        return datacenter;
    }

    private Enterprise getEnterprise(RESTLink enterpriseLink)
    {
        Integer enterpriseId =
            getLinkId(enterpriseLink, EnterprisesResource.ENTERPRISES_PATH,
                EnterpriseResource.ENTERPRISE_PARAM, ENTERPRISE, APIError.NON_EXISTENT_ENTERPRISE);

        return getEnterprise(enterpriseId);
    }

    private Enterprise getEnterprise(Integer enterpriseId)
    {
        Enterprise enterprise = enterpriseService.getEnterprise(enterpriseId);
        if (enterprise == null)
        {
            throw new NotFoundException(APIError.NON_EXISTENT_ENTERPRISE);
        }

        return enterprise;
    }
    

    private Integer getLinkId(RESTLink link, String path, String param, String key, APIError error)
    {
        if (link == null)
        {
            throw new NotFoundException(error);
        }

        String buildPath = buildPath(path, param);
        MultivaluedMap<String, String> values = resolveFromURI(buildPath, link.getHref());

        if (values == null || !values.containsKey(key))
        {
            throw new NotFoundException(error);
        }

        return Integer.valueOf(values.getFirst(key));
    }
}
