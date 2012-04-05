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
import com.abiquo.abiserver.pojo.virtualimage.OVFPackageInstanceStatus;
import com.abiquo.abiserver.pojo.virtualimage.OVFPackageList;
import com.abiquo.appliancemanager.transport.TemplateStateDto;
import com.abiquo.server.core.appslibrary.CategoryDto;
import com.abiquo.server.core.appslibrary.TemplateDefinitionsDto;

public interface AppsLibraryStub
{

    /**
     * OVFPackages
     */
    public DataResult<List<String>> getTemplateDefinitionListNames(final Integer idEnterprise);

    DataResult<OVFPackageList> getTemplateDefinitionList(final Integer idEnterprise,
        final String nameOVFPackageList);

    public DataResult<OVFPackageList> createTemplateDefinitionListFromOVFIndexUrl(
        final Integer idEnterprise, final String ovfpackageListURL);

    public DataResult<OVFPackageList> refreshTemplateDefinitionListFromRepository(
        final Integer idEnterprise, final String nameOvfpackageList);

    public BasicResult deleteTemplateDefinitionList(final Integer idEnterprise,
        final String nameOvfpackageList);

    public TemplateDefinitionsDto getTemplateDefinitions(final Integer idEnterprise,
        final String nameOVFPackageList);

    /**
     * ICONS
     */
    public DataResult<List<String>> getIcons(final Integer idEnterprise);

    /**
     * CATEGORIES
     */

    public DataResult<List<Category>> getCategories(final Integer idEnterprise);

    public DataResult<Category> createCategory(final CategoryDto categoryDto,
        final Integer idEnterprise);

    public BasicResult deleteCategory(final Integer idCategory);

    /*
     * DISK FORMMAT TYPES
     */

    public DataResult<List<com.abiquo.abiserver.pojo.virtualimage.DiskFormatType>> getDiskFormatTypes();

    /**
     * API will update the {@link TemplateStateDto} the creation state in the provided datacenter of
     * the (AM datacenter communication)
     */

    public DataResult<List<OVFPackageInstanceStatus>> getTemplatesState(String nameOVFPackageList,
        Integer idEnterprise, Integer datacenterId);

    public DataResult<List<OVFPackageInstanceStatus>> getTemplatesState(List<String> ovfUrls,
        Integer idEnterprise, Integer datacenterId);

    public DataResult<OVFPackageInstanceStatus> getTemplateState(String ovfUrl,
        Integer idEnterprise, Integer datacenterId);

    public BasicResult installTemplateDefinitionsInDatacenter(List<String> ovfUrls,
        Integer idEnterprise, Integer datacenterId);

    public DataResult<OVFPackageInstanceStatus> uninstallTemplateDefinitionInDatacenter(
        String ovfUrl, Integer idEnterprise, Integer datacenterId);
}
