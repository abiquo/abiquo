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

import static com.abiquo.api.resources.appslibrary.HypervisorTypeResource.createTransferObject;

import java.util.Collection;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.core.Context;

import org.apache.wink.common.annotations.Workspace;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import com.abiquo.api.resources.AbstractResource;
import com.abiquo.api.services.appslibrary.HypervisorTypeService;
import com.abiquo.api.util.IRESTBuilder;
import com.abiquo.model.enumerator.HypervisorType;
import com.abiquo.server.core.cloud.HypervisorTypesDto;

@Path(HypervisorTypesResource.HYPERVISOR_TYPES_PATH)
@Controller
@Workspace(workspaceTitle = "Abiquo configuration workspace", collectionTitle = "HypervisorTypes")
public class HypervisorTypesResource extends AbstractResource
{
    public static final String HYPERVISOR_TYPES_PATH = "config/hypervisortypes";

    @Autowired
    private HypervisorTypeService service;

    @GET
    public HypervisorTypesDto getHypervisorTypes(@Context final IRESTBuilder restBuilder)
        throws Exception
    {
        service = new HypervisorTypeService();
        Collection<HypervisorType> all = service.getHypervisorTypes();

        HypervisorTypesDto hypervisorTypesDto = new HypervisorTypesDto();
        for (HypervisorType d : all)
        {
            hypervisorTypesDto.add(createTransferObject(d, restBuilder));
        }

        return hypervisorTypesDto;
    }

}
