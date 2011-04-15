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

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import javax.ws.rs.GET;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;

import org.apache.wink.common.annotations.Parent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import com.abiquo.api.services.DatacenterService;
import com.abiquo.api.services.IpAddressService;
import com.abiquo.api.transformer.ModelTransformer;
import com.abiquo.api.util.IRESTBuilder;
import com.abiquo.model.enumerator.HypervisorType;
import com.abiquo.server.core.cloud.HypervisorTypesDto;
import com.abiquo.server.core.enterprise.Enterprise;
import com.abiquo.server.core.enterprise.EnterprisesDto;
import com.abiquo.server.core.infrastructure.Datacenter;
import com.abiquo.server.core.infrastructure.DatacenterDto;
import com.abiquo.server.core.util.PagedList;

@Parent(DatacentersResource.class)
@Path(DatacenterResource.DATACENTER_PARAM)
@Controller
public class DatacenterResource extends AbstractResource
{

    public static final String DATACENTER = "datacenter";

    public static final String DATACENTER_PARAM = "{" + DATACENTER + "}";

    public static final String HYPERVISORS_PATH = "hypervisors";

    public static final String ENTERPRISES_PATH = "action/enterprises";

    public static final String NETWORK = "network";

    @Autowired
    DatacenterService service;

    @Autowired
    IpAddressService ipService;

    @GET
    public DatacenterDto getDatacenter(@PathParam(DATACENTER) final Integer datacenterId,
        @Context final IRESTBuilder restBuilder) throws Exception
    {
        Datacenter datacenter = service.getDatacenter(datacenterId);

        return createTransferObject(datacenter, restBuilder);
    }

    @PUT
    public DatacenterDto modifyDatacenter(final DatacenterDto datacenter,
        @PathParam(DATACENTER) final Integer datacenterId, @Context final IRESTBuilder restBuilder)
        throws Exception
    {
        Datacenter d = service.getDatacenter(datacenterId);

        d = service.modifyDatacenter(datacenterId, datacenter);

        return createTransferObject(d, restBuilder);
    }

    @GET
    @Path(ENTERPRISES_PATH)
    public EnterprisesDto getEnterprises(@PathParam(DATACENTER) final Integer datacenterId,
        @QueryParam(START_WITH) final Integer startwith, @QueryParam(NETWORK) Boolean network,
        @QueryParam(LIMIT) final Integer limit, @Context final IRESTBuilder restBuilder)
        throws Exception

    {
        Integer firstElem = (startwith == null) ? 0 : startwith;
        Integer numElem = (limit == null) ? DEFAULT_PAGE_LENGTH : limit;
        if (network == null)
            network = false;

        Datacenter datacenter = service.getDatacenter(datacenterId);
        List<Enterprise> enterprises =
            service
                .findEnterprisesByDatacenterWithNetworks(datacenter, network, firstElem, numElem);
        EnterprisesDto enterprisesDto = new EnterprisesDto();
        for (Enterprise e : enterprises)
        {
            enterprisesDto.add(EnterpriseResource.createTransferObject(e, restBuilder));
        }
        enterprisesDto.setTotalSize(((PagedList) enterprises).getTotalResults());
        return enterprisesDto;

    }

    @GET
    @Path(HYPERVISORS_PATH)
    public HypervisorTypesDto getAvailableHypervisors(
        @PathParam(DATACENTER) final Integer datacenterId, @Context final IRESTBuilder restBuilder)
        throws Exception
    {
        Datacenter datacenter = service.getDatacenter(datacenterId);

        Set<HypervisorType> types = service.getHypervisorTypes(datacenter);

        HypervisorTypesDto dto = new HypervisorTypesDto();
        dto.setCollection(new ArrayList<HypervisorType>(types));

        return dto;
    }

    // FIXME: Not allowed right now
    // @DELETE
    // public void deleteDatacenter(@PathParam(DATACENTER) Integer datacenterId)
    // {
    // service.removeDatacenter(datacenterId);
    // }

    public static DatacenterDto addLinks(final IRESTBuilder builder, final DatacenterDto datacenter)
    {
        datacenter.setLinks(builder.buildDatacenterLinks(datacenter));

        return datacenter;
    }

    public static DatacenterDto createTransferObject(final Datacenter datacenter,
        final IRESTBuilder builder) throws Exception
    {
        DatacenterDto dto =
            ModelTransformer.transportFromPersistence(DatacenterDto.class, datacenter);
        dto = addLinks(builder, dto);
        return dto;
    }

    public static Datacenter createPersistenceObject(final DatacenterDto datacenter)
        throws Exception
    {
        return ModelTransformer.persistenceFromTransport(Datacenter.class, datacenter);
    }
}
