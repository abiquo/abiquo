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

import org.apache.commons.lang.StringUtils;
import org.apache.wink.client.ClientResponse;
import org.apache.wink.client.Resource;

import com.abiquo.abiserver.commands.stub.AbstractAPIStub;
import com.abiquo.abiserver.commands.stub.VirtualMachineTemplateResourceStub;
import com.abiquo.abiserver.pojo.result.BasicResult;
import com.abiquo.abiserver.pojo.result.DataResult;
import com.abiquo.abiserver.pojo.virtualimage.Category;
import com.abiquo.abiserver.pojo.virtualimage.Icon;
import com.abiquo.abiserver.pojo.virtualimage.VirtualImage;
import com.abiquo.model.enumerator.DiskFormatType;
import com.abiquo.model.rest.RESTLink;
import com.abiquo.server.core.appslibrary.VirtualMachineTemplateDto;
import com.abiquo.server.core.appslibrary.VirtualMachineTemplatesDto;

public class VirtualMachineTemplateResourceStubImpl extends AbstractAPIStub implements
    VirtualMachineTemplateResourceStub
{

    public final static String VIRTUAL_MACHINE_TEMPLATE_GET_CATEGORY_QUERY_PARAM = "categoryName";

    public final static String VIRTUAL_MACHINE_TEMPLATE_GET_HYPERVISOR_COMATIBLE_QUERY_PARAM =
        "hypervisorTypeName";

    public final static String VIRTUAL_MACHINE_TEMPLATE_GET_STATEFUL_QUERY_PARAM = "stateful";

    /**
     * @param idRepo, if 0, indicate stateful templates
     * @param idCategory, if 0 indicate return all the categories
     */
    @Override
    public DataResult<List<VirtualImage>> getVirtualMachineTemplateByCategory(
        final Integer idEnterprise, final Integer datacenterId, final String categoryName)
    {
        final String hypervisorTypeName = null;

        return getVirtualMachineTemplateByCategoryAndHypervisorCompatible(idEnterprise,
            datacenterId, categoryName, hypervisorTypeName, false);
    }

    /**
     * @param idRepo, if 0, indicate stateful templates
     * @param idCategory, if 0 indicate return all the categories
     */
    @Override
    public DataResult<List<VirtualImage>> getVirtualMachineTemplateByCategoryAndHypervisorCompatible(
        final Integer idEnterprise, final Integer datacenterId, final String categoryName,
        final String hypervisorTypeName, final Boolean includeStateful)

    {
        final DataResult<List<VirtualImage>> result = new DataResult<List<VirtualImage>>();

        final String uri = createVirtualMachineTemplatesLink(idEnterprise, datacenterId);
        Resource vmtemplatesResource = resource(uri);

        if (StringUtils.isNotEmpty(hypervisorTypeName))
        {
            vmtemplatesResource =
                vmtemplatesResource.queryParam(
                    VIRTUAL_MACHINE_TEMPLATE_GET_HYPERVISOR_COMATIBLE_QUERY_PARAM,
                    valueOf(hypervisorTypeName));

        }

        if (StringUtils.isNotEmpty(categoryName))
        {
            vmtemplatesResource =
                vmtemplatesResource.queryParam(VIRTUAL_MACHINE_TEMPLATE_GET_CATEGORY_QUERY_PARAM,
                    valueOf(categoryName));
        }

        if (includeStateful)
        {
            vmtemplatesResource =
                vmtemplatesResource.queryParam(VIRTUAL_MACHINE_TEMPLATE_GET_STATEFUL_QUERY_PARAM,
                    valueOf(true));
        }

        ClientResponse response = vmtemplatesResource.get();

        if (response.getStatusCode() / 200 == 1)
        {
            VirtualMachineTemplatesDto templatess =
                response.getEntity(VirtualMachineTemplatesDto.class);

            result.setSuccess(true);
            result.setData(transformToFlex(templatess));
        }
        else
        {
            populateErrors(response, result, "getVirtualImageByCategoryAndHypervisorCompatible");
        }

        return result;

    }

    @Override
    public BasicResult deleteVirtualMachineTemplate(final Integer enterpriseId,
        final Integer datacenterId, final Integer virtualMachineTemplateId)
    {
        BasicResult result = new BasicResult();

        String uri =
            createVirtualMachineTemplateLink(enterpriseId, datacenterId, virtualMachineTemplateId);

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

    private List<VirtualImage> transformToFlex(final VirtualMachineTemplatesDto vmtemplates)
    {
        List<VirtualImage> vlst = new LinkedList<VirtualImage>();
        for (VirtualMachineTemplateDto template : vmtemplates.getCollection())
        {
            vlst.add(transformToFlex(template));
        }

        return vlst;
    }

    private VirtualImage transformToFlex(final VirtualMachineTemplateDto vi)
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
        img.setOvfId(getLink("templatedefinition", vi.getLinks()).getHref());
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
    public DataResult<VirtualImage> editVirtualImage(final Integer idEnterprise,
        final Integer idDatacenter, final VirtualImage vimage)
    {
        final DataResult<VirtualImage> result = new DataResult<VirtualImage>();

        String uri = createVirtualMachineTemplateLink(idEnterprise, idDatacenter, vimage.getId());

        ClientResponse response = put(uri, createDtoObject(vimage, idDatacenter));

        if (response.getStatusCode() == 200)
        {
            VirtualMachineTemplateDto dto = response.getEntity(VirtualMachineTemplateDto.class);
            result.setData(transformToFlex(dto));
            result.setSuccess(Boolean.TRUE);
        }
        else
        {
            populateErrors(response, result, "editVirtualImage");
        }

        return result;
    }

    private VirtualMachineTemplateDto createDtoObject(final VirtualImage vimage,
        final Integer datacenterId)
    {

        VirtualMachineTemplateDto dto = new VirtualMachineTemplateDto();

        Integer enterpriseId = vimage.getIdEnterprise();

        dto.setCostCode(vimage.getCostCode());
        dto.setCpuRequired(vimage.getCpuRequired());
        dto.setDescription(vimage.getDescription());
        dto.setDiskFileSize(vimage.getDiskFileSize());
        dto.setDiskFormatType(vimage.getDiskFormatType().getName());
        dto.setHdRequired(vimage.getHdRequired());
        dto.setId(vimage.getId());
        dto.setName(vimage.getName());
        dto.setPath(vimage.getPath());
        dto.setRamRequired(vimage.getRamRequired());
        dto.setShared(vimage.isShared());
        dto.setChefEnabled(vimage.isChefEnabled());

        RESTLink enterpriseLink = new RESTLink("enterprise", createEnterpriseLink(enterpriseId));
        dto.addLink(enterpriseLink);

        RESTLink datacenterRepoLink =
            new RESTLink("datacenterrepository", createDatacenterRepositoryLink(enterpriseId,
                datacenterId));
        dto.addLink(datacenterRepoLink);

        if (vimage.getMaster() != null)
        {
            RESTLink masterLink =
                new RESTLink("master", createVirtualMachineTemplateLink(enterpriseId, datacenterId,
                    vimage.getMaster().getId()));
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
