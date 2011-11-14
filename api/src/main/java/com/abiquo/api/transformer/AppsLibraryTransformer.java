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

import com.abiquo.api.util.IRESTBuilder;
import com.abiquo.model.enumerator.DiskFormatType;
import com.abiquo.server.core.appslibrary.AppsLibraryRep;
import com.abiquo.server.core.appslibrary.Category;
import com.abiquo.server.core.appslibrary.Icon;
import com.abiquo.server.core.appslibrary.OVFPackage;
import com.abiquo.server.core.appslibrary.OVFPackageDto;
import com.abiquo.server.core.appslibrary.OVFPackageList;
import com.abiquo.server.core.appslibrary.OVFPackageListDto;

@Service
public class AppsLibraryTransformer
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

        if (ovfPackage.getCategory() != null)
        {
            dto.setName(ovfPackage.getCategory().getName());
        }
        else
        {
            dto.setName("Others");
        }

        dto.setDiskFormatTypeUri(ovfPackage.getType().uri);

        // Icon is optional
        if (ovfPackage.getIcon() != null)
        {
            dto.setIconPath(ovfPackage.getIcon().getPath());
        }

        final Integer idEnterprise = ovfPackage.getAppsLibrary().getEnterprise().getId();
        dto.setLinks(builder.buildOVFPackageLinks(idEnterprise, dto));

        return dto;
    }

    public OVFPackageListDto createTransferObject(final OVFPackageList ovfPackageList,
        final IRESTBuilder builder) throws Exception
    {

        List<OVFPackageDto> ovfpackDtoList = new LinkedList<OVFPackageDto>();
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
            final String cause =
                String.format("Invalid DiskFormatType URI [%s]", ovfDto.getDiskFormatTypeUri());
            throw new Exception(cause);
        }

        Category category = appslibraryRep.findCategoryByName(ovfDto.getName());
        if (category == null)
        {
            category = new Category(ovfDto.getName());
            appslibraryRep.insertCategory(category);
        }

        Icon icon = appslibraryRep.findIconByPath(ovfDto.getIconPath());
        if (icon == null)
        {
            icon = new Icon("Icon name", ovfDto.getIconPath()); // TODO: icon name
            appslibraryRep.insertIcon(icon);
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
            for (OVFPackageDto ovfPackDto : ovfDto.getOvfPackages())
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
