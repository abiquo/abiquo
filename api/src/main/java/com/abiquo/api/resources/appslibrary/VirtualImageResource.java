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

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.Context;

import org.apache.wink.client.Resource;
import org.apache.wink.common.annotations.Parent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import com.abiquo.api.resources.AbstractResource;
import com.abiquo.api.resources.EnterpriseResource;
import com.abiquo.api.services.InfrastructureService;
import com.abiquo.api.services.appslibrary.VirtualImageService;
import com.abiquo.api.util.IRESTBuilder;
import com.abiquo.appliancemanager.client.ApplianceManagerResourceStub;
import com.abiquo.model.enumerator.RemoteServiceType;
import com.abiquo.model.rest.RESTLink;
import com.abiquo.model.util.ModelTransformer;
import com.abiquo.server.core.cloud.VirtualImage;
import com.abiquo.server.core.cloud.VirtualImageDto;
import com.abiquo.server.core.config.Category;

@Parent(VirtualImagesResource.class)
@Path(VirtualImageResource.VIRTUAL_IMAGE_PARAM)
@Controller
public class VirtualImageResource extends AbstractResource
{

    public final static String VIRTUAL_IMAGE = "virtualimage";

    public final static String VIRTUAL_IMAGE_PARAM = "{" + VIRTUAL_IMAGE + "}";

    @Autowired
    private VirtualImageService vimageService;

    @Autowired
    private InfrastructureService infService;

    /**
     * Return the virtual image if exists.
     */
    @GET
    public VirtualImageDto getVirtualImage(
        @PathParam(EnterpriseResource.ENTERPRISE) final Integer enterpId,
        @PathParam(DatacenterRepositoryResource.DATACENTER_REPOSITORY) final Integer dcId,
        @PathParam(VirtualImageResource.VIRTUAL_IMAGE) final Integer vimageId,
        @Context final IRESTBuilder restBuilder) throws Exception
    {
        // TODO check enterprise can use the datacenter
        VirtualImage vimage = vimageService.getVirtualImage(vimageId);

        final String amUri =
            infService.getRemoteService(dcId, RemoteServiceType.APPLIANCE_MANAGER).getUri();

        return createTransferObject(vimage, enterpId, dcId, amUri, restBuilder);
    }

    /**
     * Return the {@link VirtualImageDto}o object from the POJO {@link VirtualImage}
     */
    protected static VirtualImageDto createTransferObject(final VirtualImage vimage,
        final Integer enterpId, final Integer dcId, final String amUri, final IRESTBuilder builder)
        throws Exception
    {
        VirtualImageDto dto =
            ModelTransformer.transportFromPersistence(VirtualImageDto.class, vimage);

        dto =
            addLinks(builder, dto, enterpId, dcId, vimage.getId(), amUri, vimage.getMaster(),
                vimage.getCategory());

        return dto;
    }

    private static VirtualImageDto addLinks(final IRESTBuilder builder, final VirtualImageDto dto,
        final Integer enterpriseId, final Integer dcId, final Integer vimageId, final String amUri,
        final VirtualImage master, final Category category)
    {
        dto.setLinks(builder.buildVirtualImageLinks(enterpriseId, dcId, vimageId, master, category));
        addApplianceManagerLinks(dto, amUri, vimageId, dto.getOvfid());

        return dto;
    }

    private static void addApplianceManagerLinks(final VirtualImageDto dto, final String amUri,
        final Integer enterpriseId, final String ovfid)
    {
        ApplianceManagerResourceStub am = new ApplianceManagerResourceStub(amUri);
        Resource resource = am.ovfPackage(enterpriseId.toString(), ovfid);
        String href = resource.getUriBuilder().build(new Object[] {}).toString();

        dto.addLink(new RESTLink("ovfpackageinstance", href));
        dto.addLink(new RESTLink("ovfpackagestatus", href + "?format=status"));
        dto.addLink(new RESTLink("ovfdocument", href + "?format=envelope"));
        dto.addLink(new RESTLink("imagefile", href + "?format=diskFile"));
    }
}
