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

package com.abiquo.abiserver.services.flex;

import java.util.List;

import com.abiquo.abiserver.appslibrary.stub.AppsLibraryStub;
import com.abiquo.abiserver.appslibrary.stub.AppsLibraryStubImpl;
import com.abiquo.abiserver.business.BusinessDelegateProxy;
import com.abiquo.abiserver.business.UserSessionException;
import com.abiquo.abiserver.commands.AppsLibraryCommand;
import com.abiquo.abiserver.commands.impl.AppsLibraryCommandImpl;
import com.abiquo.abiserver.commands.stub.APIStubFactory;
import com.abiquo.abiserver.commands.stub.DatacenterRepositoryResourceStub;
import com.abiquo.abiserver.commands.stub.VirtualImageResourceStub;
import com.abiquo.abiserver.commands.stub.impl.DatacenterRepositoryResourceStubImpl;
import com.abiquo.abiserver.commands.stub.impl.VirtualImageResourceStubImpl;
import com.abiquo.abiserver.exception.AppsLibraryCommandException;
import com.abiquo.abiserver.persistence.DAOFactory;
import com.abiquo.abiserver.persistence.hibernate.HibernateDAOFactory;
import com.abiquo.abiserver.pojo.authentication.UserSession;
import com.abiquo.abiserver.pojo.result.BasicResult;
import com.abiquo.abiserver.pojo.result.DataResult;
import com.abiquo.abiserver.pojo.virtualimage.Category;
import com.abiquo.abiserver.pojo.virtualimage.DiskFormatType;
import com.abiquo.abiserver.pojo.virtualimage.Icon;
import com.abiquo.abiserver.pojo.virtualimage.OVFPackageInstanceStatus;
import com.abiquo.abiserver.pojo.virtualimage.OVFPackageList;
import com.abiquo.abiserver.pojo.virtualimage.Repository;
import com.abiquo.abiserver.pojo.virtualimage.VirtualImage;
import com.abiquo.server.core.appslibrary.CategoryDto;
import com.abiquo.server.core.appslibrary.IconDto;

public class AppsLibraryService
{
    private AppsLibraryCommand appsLibraryCommand;

    private final static boolean REPOSITORY_SYNCHRONIZE = true;

    private final static boolean REPOSITORY_INCLUDE_USAGE = true;

    public AppsLibraryService()
    {
        try
        {

            appsLibraryCommand =
                (AppsLibraryCommand) Thread.currentThread().getContextClassLoader()
                    .loadClass("com.abiquo.abiserver.commands.impl.AppsLibraryPremiumCommandImpl")
                    .newInstance();
        }
        catch (Exception e)
        {
            appsLibraryCommand = new AppsLibraryCommandImpl();
        }
    }

    public DataResult<Repository> getDatacenterRepository(final UserSession userSession,
        final Integer idDatacenter, final Integer idEnterprise) // TODO idEnterpise is not used
    {
        DatacenterRepositoryResourceStub dcRepoStub =
            APIStubFactory.getInstance(userSession, new DatacenterRepositoryResourceStubImpl(),
                DatacenterRepositoryResourceStub.class);

        // refresh content and get
        return dcRepoStub.getRepository(idDatacenter, idEnterprise, REPOSITORY_SYNCHRONIZE,
            REPOSITORY_INCLUDE_USAGE);
    }

    /** Virtual images */

    /**
     * @param idRepo, if 0, indicate stateful images
     * @param idCategory, if 0 indicate return all the categories
     */
    public DataResult<List<VirtualImage>> getVirtualImageByCategoryAndHypervisorCompatible(
        final UserSession userSession, final Integer idEnterprise, final Integer idRepo,
        final Integer idCategory, final Integer idHypervisorType)
    {
        final VirtualImageResourceStub vimageStub =
            APIStubFactory.getInstance(userSession, new VirtualImageResourceStubImpl(),
                VirtualImageResourceStub.class);

        // idRepo == 0 --> stateful
        final Integer datacenterId = idRepo == 0 ? null : getDatacenterIdByRepository(idRepo);
        // idCategory == 0 indicate return all the categories
        final Integer categoryId = idCategory == 0 ? null : idCategory;

        return vimageStub.getVirtualImageByCategoryAndHypervisorCompatible(idEnterprise,
            datacenterId, categoryId, idHypervisorType);
    }

    /**
     * @param idRepo, if 0, indicate stateful images
     * @param idCategory, if 0 indicate return all the categories
     */
    public DataResult<List<VirtualImage>> getVirtualImageByCategory(final UserSession userSession,
        final Integer idEnterprise, final Integer idRepo, final Integer idCategory)
    {
        final VirtualImageResourceStub vimageStub =
            APIStubFactory.getInstance(userSession, new VirtualImageResourceStubImpl(),
                VirtualImageResourceStub.class);

        // idRepo == 0 --> stateful
        final Integer datacenterId = idRepo == 0 ? null : getDatacenterIdByRepository(idRepo);
        // idCategory == 0 indicate return all the categories
        final Integer categoryId = idCategory == 0 ? null : idCategory;

        return vimageStub.getVirtualImageByCategory(idEnterprise, datacenterId, categoryId);
    }

    /** List. */
    public DataResult<List<String>> getOVFPackageListName(final UserSession userSession,
        final Integer idEnterprise)
    {

        return proxyStub(userSession).getOVFPackageListName(idEnterprise);
    }

    public DataResult<OVFPackageList> getOVFPackageList(final UserSession userSession,
        final Integer idEnterprise, final String nameOVFPackageList)
    {
        return proxyStub(userSession).getOVFPackageList(idEnterprise, nameOVFPackageList);
    }

    public DataResult<OVFPackageList> createOVFPackageList(final UserSession userSession,
        final Integer idEnterprise, final String ovfpackageListURL)
    {
        return proxyStub(userSession).createOVFPackageList(idEnterprise, ovfpackageListURL);
    }

    public DataResult<OVFPackageList> refreshOVFPackageList(final UserSession userSession,
        final Integer idEnterprise, final String nameOvfpackageList)
    {

        return proxyStub(userSession).refreshOVFPackageList(idEnterprise, nameOvfpackageList);
    }

    public BasicResult deleteOVFPackageList(final UserSession userSession,
        final Integer idEnterprise, final String nameOvfpackageList)
    {
        return proxyStub(userSession).deleteOVFPackageList(idEnterprise, nameOvfpackageList);
    }

    /** DC specific status. */
    public DataResult<List<OVFPackageInstanceStatus>> getOVFPackageListStatus(
        final UserSession userSession, final String nameOVFPackageList, final Integer idEnterprise,
        final Integer idRepository)
    {

        final Integer datacenterId = getDatacenterIdByRepository(idRepository);

        return proxyStub(userSession).getOVFPackageListState(nameOVFPackageList, idEnterprise,
            datacenterId);
    }

    public DataResult<List<OVFPackageInstanceStatus>> refreshOVFPackageStatus(
        final UserSession userSession, final List<String> ovfUrlsIn, final Integer idEnterprise,
        final Integer idRepository)
    {

        final Integer datacenterId = getDatacenterIdByRepository(idRepository);
        final List<String> ovfUrls = ovfUrlsIn; // XXX cast to arraylist

        return proxyStub(userSession).getOVFPackagesState(ovfUrls, idEnterprise, datacenterId);

    }

    public DataResult<OVFPackageInstanceStatus> refreshOVFPackageInstanceStatus(
        final UserSession userSession, final String ovfUrl, final Integer idEnterprise,
        final Integer idRepository)
    {
        final Integer datacenterId = getDatacenterIdByRepository(idRepository);

        return proxyStub(userSession).getOVFPackageState(ovfUrl, idEnterprise, datacenterId);
    }

    public BasicResult startDownloadOVFPackage(final UserSession userSession,
        final List<String> idsOvfpackageIn, final Integer idEnterprise, final Integer idRepository)
    {
        final List<String> ovfUrls = idsOvfpackageIn; // XXX cast to arraylist

        final Integer datacenterId = getDatacenterIdByRepository(idRepository);

        return proxyStub(userSession).installOVFPackagesInDatacenter(ovfUrls, idEnterprise,
            datacenterId);
    }

    public DataResult<OVFPackageInstanceStatus> cancelDownloadOVFPackage(
        final UserSession userSession, final String ovfUrl, final Integer idEnterprise,
        final Integer idRepository)
    {
        final Integer datacenterId = getDatacenterIdByRepository(idRepository);

        return proxyStub(userSession).uninstallOVFPackageInDatacenter(ovfUrl, idEnterprise,
            datacenterId);
    }

    /**
     * ################################# #################################
     */

    /**
     * TODO user Format, Icon and Category ResourceStub
     */

    /**
     * ################################# #################################
     */
    public DataResult<List<DiskFormatType>> getDiskFormatTypes(final UserSession userSession)
    {
        DataResult<List<DiskFormatType>> result = new DataResult<List<DiskFormatType>>();

        AppsLibraryCommand proxyService = proxyService(userSession);
        try
        {
            List<DiskFormatType> disksFormat = proxyService.getDiskFormatTypes(userSession);

            result.setData(disksFormat);
            result.setSuccess(true);
        }
        catch (UserSessionException e)
        {
            result.setSuccess(false);
            result.setMessage(e.getMessage());
            result.setResultCode(e.getResult().getResultCode());
        }

        return result;
    }

    // todo con idRepo

    /** Icon */
    public DataResult<Icon> createIcon(final UserSession userSession, final Icon icon)
    {
        IconDto dto = new IconDto();
        dto.setName(icon.getName());
        dto.setPath(icon.getPath());

        return proxyStub(userSession).createIcon(dto);
    }

    public BasicResult editIcon(final UserSession userSession, final Icon icon)
    {
        return proxyStub(userSession).editIcon(icon);
    }

    public BasicResult deleteIcon(final UserSession userSession, final Integer idIcon)
    {
        return proxyStub(userSession).deleteIcon(idIcon);
    }

    public DataResult<List<Icon>> getIcons(final UserSession userSession)
    {

        return proxyStub(userSession).getIcons();
    }

    /**
     * ################################# #################################
     */

    /**
     * TODO user VirtualImageResourceStub
     */

    /**
     * ################################# #################################
     */

    public BasicResult editVirtualImage(final UserSession userSession, final VirtualImage vimage)
    {
        BasicResult result = new BasicResult();

        AppsLibraryCommand proxyService = proxyService(userSession);
        try
        {
            proxyService.editVirtualImage(userSession, vimage.toPojoHB());

            result.setSuccess(true);
        }
        catch (AppsLibraryCommandException e)
        {
            result.setSuccess(false);
            result.setMessage(e.getMessage());
        }
        catch (UserSessionException e)
        {
            result.setSuccess(false);
            result.setMessage(e.getMessage());
            result.setResultCode(e.getResult().getResultCode());
        }

        return result;
    }

    public BasicResult deleteVirtualImage(final UserSession userSession,
        final Integer idVirtualImage)
    {
        BasicResult result = new BasicResult();

        AppsLibraryCommand proxyService = proxyService(userSession);
        try
        {
            proxyService.deleteVirtualImage(userSession, idVirtualImage);

            result.setSuccess(true);
        }
        catch (AppsLibraryCommandException e)
        {
            result.setSuccess(false);
            result.setMessage(e.getMessage());
        }
        catch (UserSessionException e)
        {
            result.setSuccess(false);
            result.setMessage(e.getMessage());
            result.setResultCode(e.getResult().getResultCode());
        }

        return result;
    }

    /**
     * The Stub we will use to connect to API
     */
    protected AppsLibraryStub proxyStub(final UserSession userSession)
    {
        return APIStubFactory.getInstance(userSession, new AppsLibraryStubImpl(),
            AppsLibraryStub.class);
    }

    /**
     * Returns a proxied {@link AppsLibraryCommand}.
     * 
     * @param userSession The user session used by the proxy.
     * @return The proxied service.
     */
    private AppsLibraryCommand proxyService(final UserSession userSession)
    {
        return BusinessDelegateProxy.getInstance(userSession, appsLibraryCommand,
            AppsLibraryCommand.class);
    }

    private Integer getDatacenterIdByRepository(final Integer idRepository)
    {
        Integer idDatacenter;

        final DAOFactory daoF = HibernateDAOFactory.instance();

        daoF.beginConnection();

        idDatacenter =
            daoF.getRepositoryDAO().findById(idRepository).getDatacenter().getIdDataCenter();

        daoF.endConnection();

        return idDatacenter;
    }

    /**
     * CATEGORY
     */

    public DataResult<List<Category>> getCategories(final UserSession userSession)
    {
        return proxyStub(userSession).getCategories();
    }

    public DataResult<Category> createCategory(final UserSession userSession,
        final Integer idEnterprise, final String categoryName)
    {
        CategoryDto categoryDto = new CategoryDto();
        categoryDto.setName(categoryName);
        categoryDto.setDefaultCategory(false);
        categoryDto.setErasable(true);
        return proxyStub(userSession).createCategory(categoryDto);
    }

    public BasicResult deleteCategory(final UserSession userSession, final Integer idCategory)
    {
        return proxyStub(userSession).deleteCategory(idCategory);
    }

}
