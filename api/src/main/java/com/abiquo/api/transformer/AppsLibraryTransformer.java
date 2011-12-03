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

package com.abiquo.api.transformer;

import java.util.LinkedList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.abiquo.api.exceptions.APIError;
import com.abiquo.api.resources.appslibrary.CategoryResource;
import com.abiquo.api.resources.appslibrary.IconResource;
import com.abiquo.api.services.DefaultApiService;
import com.abiquo.api.util.IRESTBuilder;
import com.abiquo.model.enumerator.DiskFormatType;
import com.abiquo.model.rest.RESTLink;
import com.abiquo.server.core.appslibrary.AppsLibraryRep;
import com.abiquo.server.core.appslibrary.Category;
import com.abiquo.server.core.appslibrary.Icon;
import com.abiquo.server.core.appslibrary.TemplateDefinition;
import com.abiquo.server.core.appslibrary.TemplateDefinitionDto;
import com.abiquo.server.core.appslibrary.TemplateDefinitionList;
import com.abiquo.server.core.appslibrary.TemplateDefinitionListDto;
import com.abiquo.server.core.appslibrary.TemplateDefinitionsDto;

@Service
public class AppsLibraryTransformer extends DefaultApiService
{
    @Autowired
    private AppsLibraryRep appslibraryRep;

    public TemplateDefinitionDto createTransferObject(final TemplateDefinition templateDef,
        final IRESTBuilder builder) throws Exception
    {

        TemplateDefinitionDto dto = new TemplateDefinitionDto();

        dto.setDescription(templateDef.getDescription());
        dto.setId(templateDef.getId());
        dto.setProductName(templateDef.getProductName());
        dto.setProductUrl(templateDef.getProductUrl());
        dto.setProductVendor(templateDef.getProductVendor());
        dto.setProductVersion(templateDef.getProductVersion());
        dto.setUrl(templateDef.getUrl());
        dto.setDiskFileSize(templateDef.getDiskFileSize());

        dto.setDiskFormatTypeUri(templateDef.getType().uri);

        final Integer idEnterprise = templateDef.getAppsLibrary().getEnterprise().getId();
        dto.addLinks(builder.buildTemplateDefinitionLinks(idEnterprise, dto,
            templateDef.getCategory(), templateDef.getIcon()));

        return dto;
    }

    public TemplateDefinitionListDto createTransferObject(
        final TemplateDefinitionList templateDefList, final IRESTBuilder builder) throws Exception
    {

        TemplateDefinitionsDto listDto = new TemplateDefinitionsDto();
        for (TemplateDefinition templateDef : templateDefList.getTemplateDefinitions())
        {
            templateDef.setAppsLibrary(templateDefList.getAppsLibrary());
            listDto.add(createTransferObject(templateDef, builder));
        }
        TemplateDefinitionListDto dto = new TemplateDefinitionListDto();
        dto.setName(templateDefList.getName());
        dto.setId(templateDefList.getId());
        dto.setTemplateDefinitions(listDto);
        dto.setUrl(templateDefList.getUrl());

        final Integer idEnterprise = templateDefList.getAppsLibrary().getEnterprise().getId();
        dto.setLinks(builder.buildTemplateDefinitionListLinks(idEnterprise, dto));

        return dto;
    }

    @Transactional(readOnly = false, propagation = Propagation.REQUIRED)
    public TemplateDefinition createPersistenceObject(final TemplateDefinitionDto templateDef)
        throws Exception
    {
        DiskFormatType diskFormatType = DiskFormatType.fromURI(templateDef.getDiskFormatTypeUri());
        if (diskFormatType == null)
        {
            addValidationErrors(APIError.INVALID_DISK_FORMAT_TYPE);
            flushErrors();
        }

        RESTLink categoryLink = templateDef.searchLink(CategoryResource.CATEGORY);
        Category category = appslibraryRep.findCategoryByName(categoryLink.getTitle());
        if (category == null)
        {
            if (categoryLink.getTitle() == null)
            {
                category = appslibraryRep.findCategoryByName("Others");
            }
            else
            {
                category = new Category(categoryLink.getTitle());
                appslibraryRep.insertCategory(category);
            }
        }

        RESTLink iconLink = templateDef.searchLink(IconResource.ICON);
        Icon icon = appslibraryRep.findIconByPath(iconLink.getTitle());
        if (icon == null)
        {
            if (iconLink.getTitle() != null)
            {
                icon = new Icon("Icon name", iconLink.getTitle()); // TODO: icon name
                appslibraryRep.insertIcon(icon);
            }
            // icon is optional
        }

        TemplateDefinition pack = new TemplateDefinition();
        // pack.setAppsLibrary(appsLibrary) //XXX outside
        pack.setCategory(category);
        pack.setType(diskFormatType);
        pack.setIcon(icon);

        pack.setId(templateDef.getId());
        pack.setName(templateDef.getProductName()); // XXX TODO
        pack.setDescription(templateDef.getDescription());
        pack.setUrl(templateDef.getUrl());
        pack.setProductName(templateDef.getProductName());
        pack.setProductUrl(templateDef.getProductUrl());
        pack.setProductVendor(templateDef.getProductVendor());
        pack.setProductVersion(templateDef.getProductVersion());
        pack.setDiskFileSize(templateDef.getDiskFileSize());

        return pack;
    }

    @Transactional(readOnly = false, propagation = Propagation.REQUIRED)
    public TemplateDefinitionList createPersistenceObject(final TemplateDefinitionListDto templateDefListDto)
        throws Exception
    {

        List<TemplateDefinition> templateDefinitions = new LinkedList<TemplateDefinition>();
        if (templateDefListDto.getTemplateDefinitions() != null)
        {
            for (TemplateDefinitionDto templateDefDto : templateDefListDto.getTemplateDefinitions().getCollection())
            {
                templateDefinitions.add(createPersistenceObject(templateDefDto));
            }
        }

        TemplateDefinitionList pack = new TemplateDefinitionList();

        // pack.setAppsLibrary(appsLibrary) // XXX outside
        pack.setId(templateDefListDto.getId());
        pack.setName(templateDefListDto.getName());
        pack.setTemplateDefinitions(templateDefinitions);

        return pack;
    }
}
