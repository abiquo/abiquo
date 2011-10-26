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

import com.abiquo.abiserver.business.hibernate.pojohb.virtualimage.IconHB;
import com.abiquo.abiserver.pojo.authentication.UserSession;
import com.abiquo.abiserver.pojo.result.BasicResult;
import com.abiquo.abiserver.pojo.result.DataResult;
import com.abiquo.abiserver.pojo.virtualimage.Icon;
import com.abiquo.abiserver.pojo.virtualimage.OVFPackageList;
import com.abiquo.server.core.appslibrary.IconDto;
import com.abiquo.server.core.appslibrary.OVFPackageListDto;
import com.abiquo.server.core.appslibrary.OVFPackagesDto;

public interface AppsLibraryStub
{

    public DataResult<List<String>> getOVFPackageListName(final Integer idEnterprise);

    DataResult<OVFPackageList> getOVFPackageList(final Integer idEnterprise,
        final String nameOVFPackageList);

    public DataResult<OVFPackageList> createOVFPackageList(final Integer idEnterprise,
        final String ovfpackageListURL);

    public DataResult<OVFPackageList> refreshOVFPackageList(final Integer idEnterprise,
        final String nameOvfpackageList);

    public BasicResult deleteOVFPackageList(final Integer idEnterprise,
        final String nameOvfpackageList);

    public BasicResult deleteIcon(final Integer idIcon);

    public DataResult<Icon> createIcon(final Integer idEnterprise, final IconDto icon);

    /**
     * Recupera la
     * 
     * @param idEnterprise
     * @param nameOVFPackageList
     * @return
     */
    public OVFPackagesDto getOVFPackages(final Integer idEnterprise, final String nameOVFPackageList);

    public BasicResult editIcon(final Icon icon);

    public DataResult<List<Icon>> getIcons(final Integer idEnterprise);

}
