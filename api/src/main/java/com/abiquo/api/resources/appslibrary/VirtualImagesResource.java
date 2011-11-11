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

import static com.abiquo.api.resources.appslibrary.VirtualImageResource.createTransferObject;

import java.util.List;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;

import org.apache.commons.collections.CollectionUtils;
import org.apache.wink.common.annotations.Parent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import com.abiquo.api.resources.AbstractResource;
import com.abiquo.api.resources.EnterpriseResource;
import com.abiquo.api.services.InfrastructureService;
import com.abiquo.api.services.appslibrary.VirtualImageService;
import com.abiquo.api.util.IRESTBuilder;
import com.abiquo.model.enumerator.RemoteServiceType;
import com.abiquo.server.core.appslibrary.VirtualImage;
import com.abiquo.server.core.appslibrary.VirtualImagesDto;

@Parent(DatacenterRepositoryResource.class)
@Path(VirtualImagesResource.VIRTUAL_IMAGES_PATH)
@Controller
public class VirtualImagesResource extends AbstractResource
{
    public final static String VIRTUAL_IMAGES_PATH = "virtualimages";

    public final static String VIRTUAL_IMAGE_GET_CATEGORY_QUERY_PARAM = "categoryId";

    public final static String VIRTUAL_IMAGE_GET_HYPERVISOR_COMATIBLE_QUERY_PARAM =
        "hypervisorTypeId";

    public final static String VIRTUAL_IMAGE_GET_STATEFUL_QUERY_PARAM = "stateful";

    @Autowired
    private VirtualImageService service;

    @Autowired
    private InfrastructureService infrastructureService;

    @GET
    public VirtualImagesDto getVirtualImages(
        @PathParam(EnterpriseResource.ENTERPRISE) final Integer enterpriseId,
        @PathParam(DatacenterRepositoryResource.DATACENTER_REPOSITORY) final Integer datacenterId,
        @QueryParam(VIRTUAL_IMAGE_GET_CATEGORY_QUERY_PARAM) final Integer categoryId,
        @QueryParam(VIRTUAL_IMAGE_GET_HYPERVISOR_COMATIBLE_QUERY_PARAM) final Integer hypervisorTypeId,
        @QueryParam(VIRTUAL_IMAGE_GET_STATEFUL_QUERY_PARAM) final Boolean stateful,
        @Context final IRESTBuilder restBuilder) throws Exception
    {
        // TODO use categoryName and hypervisorType (optinals)
        // TODO query params : categoryName and HyeprvisorType.name()
        final String amUri =
            infrastructureService.getRemoteService(datacenterId,
                RemoteServiceType.APPLIANCE_MANAGER).getUri();

        List<VirtualImage> all = null;

        if (stateful == null || !stateful)
        {
            all =
                service.getVirtualImages(enterpriseId, datacenterId, categoryId, hypervisorTypeId);
        }
        else
        {
            if (categoryId != null && categoryId != 0)
            {
                all =
                    service.findStatefulVirtualImagesByCategoryAndDatacenter(enterpriseId,
                        datacenterId, categoryId);
            }
            else
            {
                all = service.findStatefulVirtualImagesByDatacenter(enterpriseId, datacenterId);
            }
        }

        VirtualImagesDto imagesDto = new VirtualImagesDto();

        if (!CollectionUtils.isEmpty(all))
        {
            for (VirtualImage vimage : all)
            {
                imagesDto.add(createTransferObject(vimage, enterpriseId, datacenterId, amUri,
                    restBuilder));
            }
        }

        return imagesDto;
    }
}
