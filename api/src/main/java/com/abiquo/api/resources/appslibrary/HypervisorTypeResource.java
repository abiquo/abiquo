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

package com.abiquo.api.resources.appslibrary;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.Context;

import org.apache.wink.common.annotations.Parent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import com.abiquo.api.resources.AbstractResource;
import com.abiquo.api.services.appslibrary.HypervisorTypeService;
import com.abiquo.api.util.IRESTBuilder;
import com.abiquo.model.enumerator.HypervisorType;
import com.abiquo.server.core.cloud.HypervisorTypeDto;

@Parent(HypervisorTypesResource.class)
@Path(HypervisorTypeResource.HYPERVISOR_TYPE_PARAM)
@Controller
public class HypervisorTypeResource extends AbstractResource
{
    // Define the static variables that represent the URI and the PARAM.
    public static final String HYPERVISOR_TYPE = "hypervisortype";

    public static final String HYPERVISOR_TYPE_PARAM = "{" + HYPERVISOR_TYPE + "}";

    @Autowired
    private HypervisorTypeService service;

    @GET
    public HypervisorTypeDto getHypervisorType(
        @PathParam(HYPERVISOR_TYPE) @NotNull @Min(1) final Integer hypervisorTypeId,
        @Context final IRESTBuilder restBuilder) throws Exception
    {
        HypervisorType hypervisorType = service.getHypervisorType(hypervisorTypeId);
        return createTransferObject(hypervisorType, restBuilder);
    }

    public static HypervisorTypeDto createTransferObject(final HypervisorType hypervisorType,
        final IRESTBuilder restBuilder) throws Exception
    {
        HypervisorTypeDto dto = new HypervisorTypeDto();
        dto.setId(hypervisorType.id());
        dto.setBaseFormat(hypervisorType.baseFormat);
        dto.setName(hypervisorType.name());
        dto.setCompatibilityTable(hypervisorType.compatibilityTable);
        dto.setDefaultPort(hypervisorType.defaultPort);
        return dto;
    }

}
