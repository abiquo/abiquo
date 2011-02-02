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

import static com.abiquo.api.resources.cloud.VirtualApplianceResource.createTransferObject;

import java.util.List;

import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.Context;

import org.apache.wink.common.annotations.Parent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import com.abiquo.api.services.cloud.VirtualApplianceService;
import com.abiquo.api.util.IRESTBuilder;
import com.abiquo.server.core.cloud.VirtualAppliance;
import com.abiquo.server.core.cloud.VirtualApplianceDto;
import com.abiquo.server.core.cloud.VirtualAppliancesDto;

@Parent(VirtualDatacenterResource.class)
@Path(VirtualAppliancesResource.VIRTUAL_APPLIANCES_PATH)
@Controller
public class VirtualAppliancesResource
{
    public static final String VIRTUAL_APPLIANCES_PATH = "virtualappliances";

    @Autowired
    VirtualApplianceService service;

    @GET
    public VirtualAppliancesDto getVirtualAppliances(
        @PathParam(VirtualDatacenterResource.VIRTUAL_DATACENTER) Integer vdcId,
        @Context IRESTBuilder restBuilder) throws Exception
    {
        List<VirtualAppliance> all = service.getVirtualAppliancesByVirtualDatacenter(vdcId);
        VirtualAppliancesDto vappsDto = new VirtualAppliancesDto();

        if (all != null && !all.isEmpty())
        {
            for (VirtualAppliance v : all)
            {
                vappsDto.add(createTransferObject(v, restBuilder));
            }
        }

        return vappsDto;
    }

    @POST
    public VirtualApplianceDto createVirtualAppliance(
        @PathParam(VirtualDatacenterResource.VIRTUAL_DATACENTER) Integer vdcId,
        VirtualApplianceDto dto, @Context IRESTBuilder restBuilder) throws Exception
    {
        VirtualAppliance response = service.createVirtualAppliance(vdcId, dto);

        return createTransferObject(response, restBuilder);
    }

}
