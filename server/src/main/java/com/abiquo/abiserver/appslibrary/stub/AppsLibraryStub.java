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
package com.abiquo.abiserver.appslibrary.stub;

import java.util.List;

import com.abiquo.abiserver.pojo.result.BasicResult;
import com.abiquo.abiserver.pojo.result.DataResult;
import com.abiquo.abiserver.pojo.virtualimage.Category;
import com.abiquo.abiserver.pojo.virtualimage.Icon;
import com.abiquo.abiserver.pojo.virtualimage.OVFPackageInstanceStatus;
import com.abiquo.abiserver.pojo.virtualimage.OVFPackageList;
import com.abiquo.appliancemanager.transport.OVFPackageInstanceStateDto;
import com.abiquo.server.core.appslibrary.CategoryDto;
import com.abiquo.server.core.appslibrary.IconDto;
import com.abiquo.server.core.appslibrary.OVFPackagesDto;

public interface AppsLibraryStub
{

    /**
     * OVFPackages
     */
    public DataResult<List<String>> getOVFPackageListName(final Integer idEnterprise);

    DataResult<OVFPackageList> getOVFPackageList(final Integer idEnterprise,
        final String nameOVFPackageList);

    public DataResult<OVFPackageList> createOVFPackageList(final Integer idEnterprise,
        final String ovfpackageListURL);

    public DataResult<OVFPackageList> refreshOVFPackageList(final Integer idEnterprise,
        final String nameOvfpackageList);

    public BasicResult deleteOVFPackageList(final Integer idEnterprise,
        final String nameOvfpackageList);

    public OVFPackagesDto getOVFPackages(final Integer idEnterprise, final String nameOVFPackageList);

    /**
     * ICONS
     */
<<<<<<< HEAD

    public DataResult<List<Icon>> getIcons();

    public DataResult<Icon> createIcon(final IconDto icon);

    public BasicResult editIcon(final Icon icon);

    public BasicResult deleteIcon(final Integer idIcon);

    /**
     * CATEGORIES
     */

    public DataResult<List<Category>> getCategories();

    public DataResult<Category> createCategory(final CategoryDto categoryDto);

    public BasicResult deleteCategory(final Integer idCategory);

    /*
     * DISK FORMMAT TYPES
     */

    public DataResult<List<com.abiquo.abiserver.pojo.virtualimage.DiskFormatType>> getDiskFormatTypes();

    /**
     * API will update the {@link OVFPackageInstanceStateDto} the creation state in the provided
     * datacenter of the (AM datacenter communication)
     */

    public DataResult<List<OVFPackageInstanceStatus>> getOVFPackageListState(
        String nameOVFPackageList, Integer idEnterprise, Integer datacenterId);

    public DataResult<List<OVFPackageInstanceStatus>> getOVFPackagesState(List<String> ovfUrls,
        Integer idEnterprise, Integer datacenterId);

    public DataResult<OVFPackageInstanceStatus> getOVFPackageState(String ovfUrl,
        Integer idEnterprise, Integer datacenterId);

    public BasicResult installOVFPackagesInDatacenter(List<String> ovfUrls, Integer idEnterprise,
        Integer datacenterId);

    public DataResult<OVFPackageInstanceStatus> uninstallOVFPackageInDatacenter(String ovfUrl,
        Integer idEnterprise, Integer datacenterId);
=======
    public OVFPackagesDto getOVFPackages(final Integer idEnterprise, final String nameOVFPackageList);
>>>>>>> stable
}
