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

package com.abiquo.abiserver.commands;

import java.util.List;

import com.abiquo.abiserver.business.hibernate.pojohb.virtualimage.CategoryHB;
import com.abiquo.abiserver.business.hibernate.pojohb.virtualimage.IconHB;
import com.abiquo.abiserver.business.hibernate.pojohb.virtualimage.RepositoryHB;
import com.abiquo.abiserver.business.hibernate.pojohb.virtualimage.VirtualimageHB;
import com.abiquo.abiserver.exception.AppsLibraryCommandException;
import com.abiquo.abiserver.pojo.authentication.UserSession;
import com.abiquo.abiserver.pojo.virtualimage.DiskFormatType;
import com.abiquo.abiserver.pojo.virtualimage.VirtualImage;
import com.abiquo.appliancemanager.transport.OVFPackageInstanceStatusDto;
import com.abiquo.appliancemanager.transport.OVFPackageInstanceStatusListDto;
import com.abiquo.server.core.appslibrary.OVFPackage;
import com.abiquo.server.core.appslibrary.OVFPackageListDto;
import com.abiquo.server.core.infrastructure.Repository;

/**
 * This command collects all actions related to Virtual Images
 * 
 * @author apuig
 */
public interface AppsLibraryCommand
{

    List<DiskFormatType> getDiskFormatTypes(UserSession userSession);

    RepositoryHB getDatacenterRepository(UserSession userSession, Integer idDatacenter,
        Integer idEnterprise) throws AppsLibraryCommandException; // TODO idEnterprise is not used

    /** Category */
    CategoryHB createCategory(UserSession userSession, Integer idEnterprise, String category)
        throws AppsLibraryCommandException;

    Void deleteCategory(UserSession userSession, Integer idCategory)
        throws AppsLibraryCommandException;

    List<CategoryHB> getCategories(UserSession userSession, Integer idEnterprise)
        throws AppsLibraryCommandException;

    /** Virtual images */

    /**
     * @param idRepo, if 0, indicate stateful images
     * @param idCategory, if 0 indicate return all the categories
     */
    List<VirtualImage> getVirtualImageByCategoryAndHypervisorCompatible(UserSession userSession,
        Integer idEnterprise, Integer idRepo, Integer idCategory, Integer idHypervisorType)
        throws AppsLibraryCommandException;

    /**
     * @param idRepo, if 0, indicate stateful images
     * @param idCategory, if 0 indicate return all the categories
     */
    List<VirtualimageHB> getVirtualImageByCategory(UserSession userSession, Integer idEnterprise,
        Integer idRepo, Integer idCategory) throws AppsLibraryCommandException;

    Void editVirtualImage(UserSession userSession, VirtualimageHB vimage)
        throws AppsLibraryCommandException;

    Void deleteVirtualImage(UserSession userSession, Integer idVirtualImage)
        throws AppsLibraryCommandException;

    /** DC specific status. */
    OVFPackageInstanceStatusListDto getOVFPackageListStatus(UserSession userSession,
        String nameOVFPackageList, Integer idEnterprise, Integer idRepository)
        throws AppsLibraryCommandException;

    /** Upload/download. */
    Void startDownloadOVFPackage(UserSession userSession, List<String> idsOvfpackage,
        Integer idEnterprise, Integer idRepository) throws AppsLibraryCommandException;

    OVFPackageInstanceStatusListDto refreshOVFPackageStatus(UserSession userSession,
        List<String> idsOvfpackage, Integer idEnterprise, Integer idRepository)
        throws AppsLibraryCommandException;

    OVFPackageInstanceStatusDto cancelDownloadOVFPackage(UserSession userSession,
        String idOvfpackage, Integer idEnterprise, Integer idRepository)
        throws AppsLibraryCommandException;

    /**
     * Returns the current status of a {@link OVFPackage} in the DB.
     * 
     * @param userSession Data from the current user.
     * @param idOVFPackageName Name of the item to refresh.
     * @param idEnterprise Id of {@link Enterprise} to which this {@link OVFPackage} belongs.
     * @param idRepository Id of the {@link Repository} to which the {@link OVFPackage} belongs.
     * @return {@link OVFPackageInstanceStatusDto } queried.
     * @throws AppsLibraryCommandException .
     */
    OVFPackageInstanceStatusDto getOVFPackageInstanceStatus(UserSession userSession,
        String idOVFPackageName, final Integer nameOVFPackageList, Integer idEnterprise,
        Integer idRepository) throws AppsLibraryCommandException;

    /**
     * Refresh the current status of a {@link OVFPackage} in the DB.
     * 
     * @param userSession Data from the current user.
     * @param idsOvfInstance Name of the item to refresh.
     * @param idEnterprise Id of {@link Enterprise} to which this {@link OVFPackage} belongs.
     * @param idRepository Id of the {@link Repository} to which the {@link OVFPackage} belongs.
     * @return {@link OVFPackageInstanceStatusDto } queried.
     * @throws AppsLibraryCommandException .
     */
    OVFPackageInstanceStatusDto refreshOVFPackageInstanceStatus(UserSession userSession,
        String idsOvfInstance, Integer idEnterprise, Integer idRepository)
        throws AppsLibraryCommandException;
}
