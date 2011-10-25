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
import com.abiquo.abiserver.pojo.virtualimage.Icon;
import com.abiquo.server.core.appslibrary.OVFPackageListDto;
import com.abiquo.server.core.appslibrary.OVFPackagesDto;

public interface AppsLibraryStub
{

    List<String> getOVFPackageListName(final Integer idEnterprise);

    OVFPackageListDto getOVFPackageList(final Integer idEnterprise, final String nameOVFPackageList);

    OVFPackageListDto createOVFPackageList(final Integer idEnterprise,
        final String ovfpackageListURL);

    OVFPackageListDto refreshOVFPackageList(final Integer idEnterprise,
        final String nameOvfpackageList);

    void deleteOVFPackageList(final Integer idEnterprise, final String nameOvfpackageList);

    void deleteIcon(final Integer idIcon);

    /**
     * Recupera la
     * 
     * @param idEnterprise
     * @param nameOVFPackageList
     * @return
     */
    public OVFPackagesDto getOVFPackages(final Integer idEnterprise, final String nameOVFPackageList);

    public BasicResult editIcon(final UserSession userSession, final Icon icon);

}
