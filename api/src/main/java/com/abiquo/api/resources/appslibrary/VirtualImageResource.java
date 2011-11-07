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
import javax.ws.rs.PUT;
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
import com.abiquo.server.core.appslibrary.VirtualImage;
import com.abiquo.server.core.appslibrary.VirtualImageDto;

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
    private InfrastructureService infrastructureService;

    @GET
    public VirtualImageDto getVirtualImage(
        @PathParam(EnterpriseResource.ENTERPRISE) final Integer enterpriseId,
        @PathParam(DatacenterRepositoryResource.DATACENTER_REPOSITORY) final Integer datacenterId,
        @PathParam(VirtualImageResource.VIRTUAL_IMAGE) final Integer virtualImageId,
        @Context final IRESTBuilder restBuilder) throws Exception
    {
        VirtualImage vimage =
            vimageService.getVirtualImage(enterpriseId, datacenterId, virtualImageId);

        final String amUri =
            infrastructureService.getRemoteService(datacenterId,
                RemoteServiceType.APPLIANCE_MANAGER).getUri();

        return createTransferObject(vimage, enterpriseId, datacenterId, amUri, restBuilder);
    }

    @PUT
    public VirtualImageDto editVirtualImage(
        @PathParam(EnterpriseResource.ENTERPRISE) final Integer enterpriseId,
        @PathParam(DatacenterRepositoryResource.DATACENTER_REPOSITORY) final Integer datacenterId,
        @PathParam(VirtualImageResource.VIRTUAL_IMAGE) final Integer virtualImageId,
        final VirtualImageDto vImageDto, @Context final IRESTBuilder restBuilder) throws Exception
    {
        VirtualImage vimage =
            vimageService.updateVirtualImage(enterpriseId, datacenterId, virtualImageId, vImageDto);

        final String amUri =
            infrastructureService.getRemoteService(datacenterId,
                RemoteServiceType.APPLIANCE_MANAGER).getUri();

        return createTransferObject(vimage, enterpriseId, datacenterId, amUri, restBuilder);

    }

    /**
     * Return the {@link VirtualImageDto}o object from the POJO {@link VirtualImage}
     */
    protected static VirtualImageDto createTransferObject(final VirtualImage vimage,
        final Integer enterpId, final Integer dcId, final String amUri, final IRESTBuilder builder)
        throws Exception
    {
        VirtualImageDto dto = new VirtualImageDto();
        dto.setId(vimage.getId());
        dto.setCpuRequired(vimage.getCpuRequired());
        dto.setDescription(vimage.getDescription());
        dto.setDiskFileSize(vimage.getDiskFileSize());
        dto.setHdRequired(vimage.getHdRequiredInBytes());
        dto.setName(vimage.getName());
        dto.setPath(vimage.getPath());
        dto.setRamRequired(vimage.getRamRequired());
        dto.setShared(vimage.isShared());
        dto.setDiskFormatType(vimage.getDiskFormatType().name());
        dto.setCostCode(vimage.getCostCode());
        dto.setCreationDate(vimage.getCreationDate());
        dto.setCreationUser(vimage.getCreationUser());

        return addLinks(builder, dto, enterpId, dcId, vimage, amUri);
    }

    private static VirtualImageDto addLinks(final IRESTBuilder builder, final VirtualImageDto dto,
        final Integer enterpriseId, final Integer dcId, final VirtualImage vimage,
        final String amUri)
    {
        dto
            .setLinks(builder
                .buildVirtualImageLinks(enterpriseId, dcId, vimage, vimage.getMaster()));
        addApplianceManagerLinks(dto, amUri, enterpriseId, vimage.getOvfid());
        return dto;
    }

    private static void addApplianceManagerLinks(final VirtualImageDto dto, final String amUri,
        final Integer enterpriseId, final String ovfid)
    {
        if (ovfid != null)
        {
            ApplianceManagerResourceStub am = new ApplianceManagerResourceStub(amUri);
            Resource resource = am.ovfPackage(enterpriseId.toString(), ovfid);
            String href = resource.getUriBuilder().build(new Object[] {}).toString();

            dto.addLink(new RESTLink("ovfpackage", ovfid));
            dto.addLink(new RESTLink("ovfpackageinstance", href));
            dto.addLink(new RESTLink("ovfpackagestatus", href + "?format=status"));
            dto.addLink(new RESTLink("ovfdocument", href + "?format=envelope"));
            dto.addLink(new RESTLink("imagefile", href + "?format=diskFile"));
        }
    }
}
