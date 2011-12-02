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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.abiquo.api.exceptions.APIError;
import com.abiquo.api.resources.appslibrary.CategoryResource;
import com.abiquo.api.resources.appslibrary.IconResource;
import com.abiquo.api.services.DefaultApiService;
import com.abiquo.api.util.AbiquoLinkBuilder;
import com.abiquo.api.util.IRESTBuilder;
import com.abiquo.model.enumerator.DiskFormatType;
import com.abiquo.model.rest.RESTLink;
import com.abiquo.server.core.appslibrary.AppsLibraryRep;
import com.abiquo.server.core.appslibrary.Category;
import com.abiquo.server.core.appslibrary.Icon;
import com.abiquo.server.core.appslibrary.OVFPackage;
import com.abiquo.server.core.appslibrary.OVFPackageDto;
import com.abiquo.server.core.appslibrary.OVFPackageList;
import com.abiquo.server.core.appslibrary.OVFPackageListDto;
import com.abiquo.server.core.appslibrary.OVFPackagesDto;

@Service
public class AppsLibraryTransformer extends DefaultApiService
{
    @Autowired
    private AppsLibraryRep appslibraryRep;

    /**
     * ModelTransformer.transportFromPersistence(OVFPackageDto.class, ovfPackage);
     */
    public OVFPackageDto createTransferObject(final OVFPackage ovfPackage,
        final IRESTBuilder builder) throws Exception
    {

        OVFPackageDto dto = new OVFPackageDto();

        dto.setDescription(ovfPackage.getDescription());
        dto.setId(ovfPackage.getId());
        dto.setProductName(ovfPackage.getProductName());
        dto.setProductUrl(ovfPackage.getProductUrl());
        dto.setProductVendor(ovfPackage.getProductVendor());
        dto.setProductVersion(ovfPackage.getProductVersion());
        dto.setUrl(ovfPackage.getUrl());
        dto.setDiskFileSize(ovfPackage.getDiskFileSize());

        dto.setDiskFormatTypeUri(ovfPackage.getType().uri);

        final Integer idEnterprise = ovfPackage.getAppsLibrary().getEnterprise().getId();
        dto.addLinks(builder.buildOVFPackageLinks(idEnterprise, dto, ovfPackage.getCategory(),
            ovfPackage.getIcon()));

        return dto;
    }

    public OVFPackageListDto createTransferObject(final OVFPackageList ovfPackageList,
        final IRESTBuilder builder) throws Exception
    {

        OVFPackagesDto ovfpackDtoList = new OVFPackagesDto();
        for (OVFPackage ovfPack : ovfPackageList.getOvfPackages())
        {
            ovfPack.setAppsLibrary(ovfPackageList.getAppsLibrary());
            ovfpackDtoList.add(createTransferObject(ovfPack, builder));
        }
        OVFPackageListDto dto = new OVFPackageListDto();
        dto.setName(ovfPackageList.getName());
        dto.setId(ovfPackageList.getId());
        dto.setOvfPackages(ovfpackDtoList);
        dto.setUrl(ovfPackageList.getUrl());

        final Integer idEnterprise = ovfPackageList.getAppsLibrary().getEnterprise().getId();
        dto.setLinks(builder.buildOVFPackageListLinks(idEnterprise, dto));

        return dto;
    }

    /**
     * ModelTransformer.persistenceFromTransport(OVFPackage.class, ovfPackage);
     */
    @Transactional(readOnly = false, propagation = Propagation.REQUIRED)
    public OVFPackage createPersistenceObject(final OVFPackageDto ovfDto) throws Exception
    {
        DiskFormatType diskFormatType = DiskFormatType.fromURI(ovfDto.getDiskFormatTypeUri());
        if (diskFormatType == null)
        {
            addValidationErrors(APIError.INVALID_DISK_FORMAT_TYPE);
            flushErrors();
        }

        RESTLink categoryLink = ovfDto.searchLink(CategoryResource.CATEGORY);
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

        RESTLink iconLink = ovfDto.searchLink(IconResource.ICON);
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

        OVFPackage pack = new OVFPackage();
        // pack.setAppsLibrary(appsLibrary) //XXX outside
        pack.setCategory(category);
        pack.setType(diskFormatType);
        pack.setIcon(icon);

        pack.setId(ovfDto.getId());
        pack.setName(ovfDto.getProductName()); // XXX TODO
        pack.setDescription(ovfDto.getDescription());
        pack.setUrl(ovfDto.getUrl());
        pack.setProductName(ovfDto.getProductName());
        pack.setProductUrl(ovfDto.getProductUrl());
        pack.setProductVendor(ovfDto.getProductVendor());
        pack.setProductVersion(ovfDto.getProductVersion());
        pack.setDiskFileSize(ovfDto.getDiskFileSize());

        return pack;
    }

    @Transactional(readOnly = false, propagation = Propagation.REQUIRED)
    public OVFPackageList createPersistenceObject(final OVFPackageListDto ovfDto) throws Exception
    {

        List<OVFPackage> ovfPackList = new LinkedList<OVFPackage>();
        if (ovfDto.getOvfPackages() != null)
        {
            for (OVFPackageDto ovfPackDto : ovfDto.getOvfPackages().getCollection())
            {
                ovfPackList.add(createPersistenceObject(ovfPackDto));
            }
        }

        OVFPackageList pack = new OVFPackageList();

        // pack.setAppsLibrary(appsLibrary) // XXX outside
        pack.setId(ovfDto.getId());
        pack.setName(ovfDto.getName());
        pack.setOvfPackages(ovfPackList);

        return pack;
    }
}
