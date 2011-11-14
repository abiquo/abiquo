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

package com.abiquo.abiserver.commands.stub.impl;

import static java.lang.String.valueOf;

import java.util.LinkedList;
import java.util.List;

import org.apache.wink.client.ClientResponse;
import org.apache.wink.client.Resource;

import com.abiquo.abiserver.commands.stub.AbstractAPIStub;
import com.abiquo.abiserver.commands.stub.VirtualImageResourceStub;
import com.abiquo.abiserver.pojo.authentication.UserSession;
import com.abiquo.abiserver.pojo.result.BasicResult;
import com.abiquo.abiserver.pojo.result.DataResult;
import com.abiquo.abiserver.pojo.virtualimage.Category;
import com.abiquo.abiserver.pojo.virtualimage.Icon;
import com.abiquo.abiserver.pojo.virtualimage.VirtualImage;
import com.abiquo.model.enumerator.DiskFormatType;
import com.abiquo.model.rest.RESTLink;
import com.abiquo.server.core.appslibrary.VirtualImageDto;
import com.abiquo.server.core.appslibrary.VirtualImagesDto;

public class VirtualImageResourceStubImpl extends AbstractAPIStub implements
    VirtualImageResourceStub
{

    public final static String VIRTUAL_IMAGE_GET_CATEGORY_QUERY_PARAM = "categoryId";

    public final static String VIRTUAL_IMAGE_GET_HYPERVISOR_COMATIBLE_QUERY_PARAM =
        "hypervisorTypeId";

    /**
     * @param idRepo, if 0, indicate stateful images
     * @param idCategory, if 0 indicate return all the categories
     */
    @Override
    public DataResult<List<VirtualImage>> getVirtualImageByCategory(final Integer idEnterprise,
        final Integer datacenterId, final Integer idCategory)
    {
        final Integer idHypervisorType = null;
        return getVirtualImageByCategoryAndHypervisorCompatible(idEnterprise, datacenterId,
            idCategory, idHypervisorType);
    }

    /**
     * @param idRepo, if 0, indicate stateful images
     * @param idCategory, if 0 indicate return all the categories
     */
    @Override
    public DataResult<List<VirtualImage>> getVirtualImageByCategoryAndHypervisorCompatible(
        final Integer idEnterprise, final Integer datacenterId, final Integer idCategory,
        final Integer idHypervisorType)

    {
        final DataResult<List<VirtualImage>> result = new DataResult<List<VirtualImage>>();

        final String uri = createVirtualImagesLink(idEnterprise, datacenterId);
        Resource vimagesResource = resource(uri);

        if (idHypervisorType != null)
        {
            vimagesResource =
                vimagesResource.queryParam(VIRTUAL_IMAGE_GET_HYPERVISOR_COMATIBLE_QUERY_PARAM,
                    valueOf(idHypervisorType));

        }

        if (idCategory != null)
        {
            vimagesResource =
                vimagesResource.queryParam(VIRTUAL_IMAGE_GET_CATEGORY_QUERY_PARAM,
                    valueOf(idCategory));
        }

        ClientResponse response = vimagesResource.get();

        if (response.getStatusCode() / 200 == 1)
        {
            VirtualImagesDto images = response.getEntity(VirtualImagesDto.class);

            result.setSuccess(true);
            result.setData(transformToFlex(images));
        }
        else
        {
            populateErrors(response, result, "getVirtualImageByCategoryAndHypervisorCompatible");
        }

        return result;

    }

    public BasicResult deleteVirtualImage(final Integer enterpriseId, final Integer datacenterId,
        final Integer virtualimageId)
    {
        BasicResult result = new BasicResult();

        String uri = createVirtualImageLink(enterpriseId, datacenterId, virtualimageId);

        ClientResponse response = delete(uri);

        if (response.getStatusCode() / 200 == 1)
        {
            result.setSuccess(true);
        }
        else
        {
            populateErrors(response, result, "deleteVirtualImage");
        }

        return result;
    }

    private List<VirtualImage> transformToFlex(final VirtualImagesDto images)
    {
        List<VirtualImage> vlst = new LinkedList<VirtualImage>();
        for (VirtualImageDto image : images.getCollection())
        {
            vlst.add(transformToFlex(image));
        }

        return vlst;
    }

    private VirtualImage transformToFlex(final VirtualImageDto vi)
    {
        VirtualImage img = new VirtualImage();

        img.setId(vi.getId());
        img.setName(vi.getName());
        img.setDescription(vi.getDescription());
        img.setPath(vi.getPath());
        img.setHdRequired(vi.getHdRequired());
        img.setRamRequired(vi.getRamRequired());
        img.setCpuRequired(vi.getCpuRequired());
        img.setShared(vi.isShared());
        img.setStateful(vi.isShared());
        img.setOvfId(getLink("ovfpackage", vi.getLinks()).getHref());
        img.setDiskFileSize(vi.getDiskFileSize());
        img.setCostCode(vi.getCostCode());
        img.setCategory(createCategoryFromLink(getLink("category", vi.getLinks())));
        img.setIcon(createIconFromLink(getLink("icon", vi.getLinks())));
        img.setRepository(createRepositoryFromLinks());
        img.setDiskFormatType(createDiskFormatType(DiskFormatType.valueOf(vi.getDiskFormatType())));
        img.setCreationUser(vi.getCreationUser());
        img.setCreationDate(vi.getCreationDate());
        // img.setIdEnterprise(idEnterprise); // // XXX (in AppslLibraryService this value is set
        // properly)
        // private VirtualImage master; // TODO master instance images

        return img;
    }

    private com.abiquo.abiserver.pojo.virtualimage.DiskFormatType createDiskFormatType(
        final DiskFormatType formattype)
    {
        com.abiquo.abiserver.pojo.virtualimage.DiskFormatType forma =
            new com.abiquo.abiserver.pojo.virtualimage.DiskFormatType();
        // forma.setAlias(alias);
        forma.setId(formattype.id());
        forma.setName(formattype.name());
        forma.setDescription(formattype.description);
        forma.setUri(formattype.uri);
        return forma;
    }

    private com.abiquo.abiserver.pojo.virtualimage.Repository createRepositoryFromLinks()
    {
        com.abiquo.abiserver.pojo.virtualimage.Repository repo =
            new com.abiquo.abiserver.pojo.virtualimage.Repository();
        // repo.setId(2); // XXX (in AppslLibraryService this value is set properly)
        repo.setName("myrepo");
        // repo.setDatacenter(datacenter);
        return repo;
    }

    private Icon createIconFromLink(final RESTLink link)
    {
        if (link == null)
        {
            return null;
        }

        Icon i = new Icon();
        i.setId(Integer.valueOf(link.getHref().substring(link.getHref().lastIndexOf("/") + 1)));
        i.setPath(link.getTitle());
        i.setName("defaultIconName"); // TODO default
        return i;
    }

    private Category createCategoryFromLink(final RESTLink link)
    {
        Category c = new Category();
        c.setId(Integer.valueOf(link.getHref().substring(link.getHref().lastIndexOf("/") + 1)));
        c.setName(link.getTitle());
        return c;
    }

    private RESTLink getLink(final String rel, final List<RESTLink> links)
    {
        for (RESTLink link : links)
        {
            if (link.getRel().equalsIgnoreCase(rel))
            {
                return link;
            }
        }
        return null; // TODO check error. i guess could be null
    }

    @Override
    public DataResult<VirtualImage> editVirtualImage(final VirtualImage vimage)
    {
        final DataResult<VirtualImage> result = new DataResult<VirtualImage>();

        String uri =
            createVirtualImageLink(vimage.getIdEnterprise(), vimage.getRepository().getDatacenter()
                .getId(), vimage.getId());

        ClientResponse response = put(uri, createDtoObject(vimage));

        if (response.getStatusCode() == 200)
        {
            VirtualImageDto dto = response.getEntity(VirtualImageDto.class);
            result.setData(transformToFlex(dto));
            result.setSuccess(Boolean.TRUE);
        }
        else
        {
            populateErrors(response, result, "editVirtualImage");
        }

        return result;
    }

    private VirtualImageDto createDtoObject(final VirtualImage vimage)
    {

        VirtualImageDto dto = new VirtualImageDto();

        Integer enterpriseId = vimage.getIdEnterprise();
        Integer datacenterId = vimage.getRepository().getId();

        dto.setCostCode(vimage.getCostCode());
        dto.setCpuRequired(vimage.getCpuRequired());
        dto.setDescription(vimage.getDescription());
        dto.setDiskFileSize(vimage.getDiskFileSize());
        dto.setDiskFormatType(vimage.getDiskFormatType().toString());
        dto.setHdRequired(vimage.getHdRequired());
        dto.setId(vimage.getId());
        dto.setName(vimage.getName());
        dto.setPath(vimage.getPath());
        dto.setRamRequired(vimage.getRamRequired());
        dto.setShared(vimage.isShared());

        RESTLink enterpriseLink = new RESTLink("enterprise", createEnterpriseLink(enterpriseId));
        dto.addLink(enterpriseLink);

        RESTLink datacenterRepoLink =
            new RESTLink("datacenterrepository", createDatacenterRepositoryLink(enterpriseId,
                datacenterId));
        dto.addLink(datacenterRepoLink);

        if (vimage.getMaster() != null)
        {
            RESTLink masterLink =
                new RESTLink("master", createVirtualImageLink(enterpriseId, datacenterId, vimage
                    .getMaster().getId()));
            dto.addLink(masterLink);
        }
        if (vimage.getCategory() != null)
        {
            RESTLink categoryLink =
                new RESTLink("category", createCategoryLink(vimage.getCategory().getId()));
            dto.addLink(categoryLink);
        }
        if (vimage.getIcon() != null)

        {
            RESTLink iconLink = new RESTLink("icon", createIconLink(vimage.getIcon().getId()));
            dto.addLink(iconLink);
        }
        return dto;

    }
}
