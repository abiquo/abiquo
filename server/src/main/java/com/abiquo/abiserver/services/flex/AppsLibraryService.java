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

import java.util.LinkedList;
import java.util.List;

import com.abiquo.abiserver.business.BusinessDelegateProxy;
import com.abiquo.abiserver.business.UserSessionException;
import com.abiquo.abiserver.business.hibernate.pojohb.virtualimage.CategoryHB;
import com.abiquo.abiserver.business.hibernate.pojohb.virtualimage.IconHB;
import com.abiquo.abiserver.business.hibernate.pojohb.virtualimage.RepositoryHB;
import com.abiquo.abiserver.business.hibernate.pojohb.virtualimage.VirtualimageHB;
import com.abiquo.abiserver.commands.AppsLibraryCommand;
import com.abiquo.abiserver.exception.AppsLibraryCommandException;
import com.abiquo.abiserver.listener.ProxyContextLoaderListener;
import com.abiquo.abiserver.pojo.authentication.UserSession;
import com.abiquo.abiserver.pojo.result.BasicResult;
import com.abiquo.abiserver.pojo.result.DataResult;
import com.abiquo.abiserver.pojo.virtualimage.Category;
import com.abiquo.abiserver.pojo.virtualimage.DiskFormatType;
import com.abiquo.abiserver.pojo.virtualimage.Icon;
import com.abiquo.abiserver.pojo.virtualimage.OVFPackage;
import com.abiquo.abiserver.pojo.virtualimage.OVFPackageInstanceStatus;
import com.abiquo.abiserver.pojo.virtualimage.OVFPackageList;
import com.abiquo.abiserver.pojo.virtualimage.Repository;
import com.abiquo.abiserver.pojo.virtualimage.VirtualImage;
import com.abiquo.appliancemanager.transport.OVFPackageInstanceStatusDto;
import com.abiquo.appliancemanager.transport.OVFPackageInstanceStatusListDto;
import com.abiquo.ovfmanager.ovf.section.DiskFormat;
import com.abiquo.server.core.appslibrary.OVFPackageDto;
import com.abiquo.server.core.appslibrary.OVFPackageListDto;

public class AppsLibraryService
{
    private AppsLibraryCommand appsLibraryCommand;

    public AppsLibraryService()
    {
        appsLibraryCommand = ProxyContextLoaderListener.getCtx().getBean(AppsLibraryCommand.class);
    }

    public DataResult<List<DiskFormatType>> getDiskFormatTypes(UserSession userSession)
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

    public DataResult<Repository> getDatacenterRepository(UserSession userSession,
        Integer idDatacenter, Integer idEnterprise) // TODO idEnterpise is not used
    {
        DataResult<Repository> result = new DataResult<Repository>();

        AppsLibraryCommand proxyService = proxyService(userSession);
        try
        {
            RepositoryHB repoHb =
                proxyService.getDatacenterRepository(userSession, idDatacenter, idEnterprise);

            result.setData(repoHb.toPojo());
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

    // todo con idRepo

    /** Category */
    public DataResult<Category> createCategory(UserSession userSession, Integer idEnterprise,
        String categoryName)
    {
        DataResult<Category> result = new DataResult<Category>();

        AppsLibraryCommand proxyService = proxyService(userSession);
        try
        {
            CategoryHB categoryHb =
                proxyService.createCategory(userSession, idEnterprise, categoryName);

            result.setData(categoryHb.toPojo());
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

    public BasicResult deleteCategory(UserSession userSession, Integer idCategory)
    {
        BasicResult result = new BasicResult();

        AppsLibraryCommand proxyService = proxyService(userSession);
        try
        {
            proxyService.deleteCategory(userSession, idCategory);

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

    public DataResult<List<Category>> getCategories(UserSession userSession, Integer idEnterprise)
    {
        DataResult<List<Category>> result = new DataResult<List<Category>>();

        AppsLibraryCommand proxyService = proxyService(userSession);
        try
        {
            List<CategoryHB> categoriesHb = proxyService.getCategories(userSession, idEnterprise);
            List<Category> categories = new LinkedList<Category>();

            for (CategoryHB catHb : categoriesHb)
            {
                categories.add(catHb.toPojo());
            }

            result.setData(categories);
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

    /** Icon */
    public DataResult<Icon> createIcon(UserSession userSession, Integer idEnterprise, Icon icon)
    {
        DataResult<Icon> result = new DataResult<Icon>();

        AppsLibraryCommand proxyService = proxyService(userSession);
        try
        {
            IconHB iconHb = proxyService.createIcon(userSession, idEnterprise, icon.toPojoHB());

            result.setData(iconHb.toPojo());
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

    public BasicResult editIcon(UserSession userSession, Icon icon)
    {
        BasicResult result = new BasicResult();

        AppsLibraryCommand proxyService = proxyService(userSession);
        try
        {
            proxyService.editIcon(userSession, icon.toPojoHB());

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

    public BasicResult deleteIcon(UserSession userSession, Integer idIcon)
    {
        BasicResult result = new BasicResult();

        AppsLibraryCommand proxyService = proxyService(userSession);
        try
        {
            proxyService.deleteIcon(userSession, idIcon);

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

    public DataResult<List<Icon>> getIcons(UserSession userSession, Integer idEnterprise)
    {
        DataResult<List<Icon>> result = new DataResult<List<Icon>>();

        AppsLibraryCommand proxyService = proxyService(userSession);
        try
        {

            List<IconHB> iconsHb = proxyService.getIcons(userSession, idEnterprise);
            List<Icon> icons = new LinkedList<Icon>();

            for (IconHB iHb : iconsHb)
            {
                icons.add(iHb.toPojo());

            }

            result.setData(icons);
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

    /** Virtual images */

    /**
     * @param idRepo, if 0, indicate stateful images
     * @param idCategory, if 0 indicate return all the categories
     */
    public DataResult<List<VirtualImage>> getVirtualImageByCategoryAndHypervisorCompatible(
        UserSession userSession, Integer idEnterprise, Integer idRepo, Integer idCategory,
        Integer idHypervisorType)
    {
        DataResult<List<VirtualImage>> result = new DataResult<List<VirtualImage>>();

        AppsLibraryCommand proxyService = proxyService(userSession);
        try
        {
            List<VirtualImage> vimages =
                proxyService.getVirtualImageByCategoryAndHypervisorCompatible(userSession,
                    idEnterprise, idRepo, idCategory, idHypervisorType);

            result.setData(vimages);
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
     * @param idRepo, if 0, indicate stateful images
     * @param idCategory, if 0 indicate return all the categories
     */
    public DataResult<List<VirtualImage>> getVirtualImageByCategory(UserSession userSession,
        Integer idEnterprise, Integer idRepo, Integer idCategory)
    {
        DataResult<List<VirtualImage>> result = new DataResult<List<VirtualImage>>();

        AppsLibraryCommand proxyService = proxyService(userSession);
        try
        {

            List<VirtualimageHB> vimagesHb =
                proxyService.getVirtualImageByCategory(userSession, idEnterprise, idRepo,
                    idCategory);
            List<VirtualImage> vimages = new LinkedList<VirtualImage>();

            for (VirtualimageHB viHb : vimagesHb)
            {
                vimages.add(viHb.toPojo());

            }

            result.setData(vimages);
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

    public BasicResult editVirtualImage(UserSession userSession, VirtualImage vimage)
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

    public BasicResult deleteVirtualImage(UserSession userSession, Integer idVirtualImage)
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

    /** List. */
    public DataResult<List<String>> getOVFPackageListName(UserSession userSession,
        Integer idEnterprise)
    {

        DataResult<List<String>> result = new DataResult<List<String>>();

        AppsLibraryCommand proxyService = proxyService(userSession);
        try
        {

            List<String> packagListNames =
                proxyService.getOVFPackageListName(userSession, idEnterprise);

            result.setData(packagListNames);
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

    public DataResult<OVFPackageList> getOVFPackageList(UserSession userSession,
        Integer idEnterprise, String nameOVFPackageList)
    {

        DataResult<OVFPackageList> result = new DataResult<OVFPackageList>();

        AppsLibraryCommand proxyService = proxyService(userSession);
        try
        {

            OVFPackageListDto packagListsDto =
                proxyService.getOVFPackageList(userSession, idEnterprise, nameOVFPackageList);

            result.setData(transform(packagListsDto));
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

    public DataResult<OVFPackageList> createOVFPackageList(UserSession userSession,
        Integer idEnterprise, String ovfpackageListURL)
    {

        DataResult<OVFPackageList> result = new DataResult<OVFPackageList>();

        AppsLibraryCommand proxyService = proxyService(userSession);
        try
        {

            OVFPackageListDto packagListsDto =
                proxyService.createOVFPackageList(userSession, idEnterprise, ovfpackageListURL);

            OVFPackageList packagList = transform(packagListsDto);
            packagList.setUrl(ovfpackageListURL); // XXX

            result.setData(packagList);
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

    public DataResult<OVFPackageList> refreshOVFPackageList(UserSession userSession,
        Integer idEnterprise, String nameOvfpackageList)
    {

        DataResult<OVFPackageList> result = new DataResult<OVFPackageList>();

        AppsLibraryCommand proxyService = proxyService(userSession);
        try
        {

            OVFPackageListDto packagListsDto =
                proxyService.refreshOVFPackageList(userSession, idEnterprise, nameOvfpackageList);

            result.setData(transform(packagListsDto));
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

    public BasicResult deleteOVFPackageList(UserSession userSession, Integer idEnterprise,
        String nameOvfpackageList)
    {
        BasicResult result = new BasicResult();

        AppsLibraryCommand proxyService = proxyService(userSession);
        try
        {
            proxyService.deleteOVFPackageList(userSession, idEnterprise, nameOvfpackageList);

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

    /** DC specific status. */
    public DataResult<List<OVFPackageInstanceStatus>> getOVFPackageListStatus(
        UserSession userSession, String nameOVFPackageList, Integer idEnterprise,
        Integer idRepository)
    {

        DataResult<List<OVFPackageInstanceStatus>> result =
            new DataResult<List<OVFPackageInstanceStatus>>();

        AppsLibraryCommand proxyService = proxyService(userSession);
        try
        {

            OVFPackageInstanceStatusListDto statusListDto =
                proxyService.getOVFPackageListStatus(userSession, nameOVFPackageList, idEnterprise,
                    idRepository);

            List<OVFPackageInstanceStatus> statusList = transform(statusListDto);

            result.setData(statusList);
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

    public BasicResult startDownloadOVFPackage(UserSession userSession,
        List<String> idsOvfpackageIn, Integer idEnterprise, Integer idRepository)
    {
        List<String> idsOvfpackage = idsOvfpackageIn; // XXX cast to arraylist

        BasicResult result = new BasicResult();

        AppsLibraryCommand proxyService = proxyService(userSession);
        try
        {
            proxyService.startDownloadOVFPackage(userSession, idsOvfpackage, idEnterprise,
                idRepository);

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

    public DataResult<OVFPackageInstanceStatus> cancelDownloadOVFPackage(UserSession userSession,
        String idOvfpackage, Integer idEnterprise, Integer idRepository)
    {

        DataResult<OVFPackageInstanceStatus> result = new DataResult<OVFPackageInstanceStatus>();

        AppsLibraryCommand proxyService = proxyService(userSession);
        try
        {
            OVFPackageInstanceStatusDto statusDto =
                proxyService.cancelDownloadOVFPackage(userSession, idOvfpackage, idEnterprise,
                    idRepository);

            result.setData(transform(statusDto));
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

    public DataResult<List<OVFPackageInstanceStatus>> refreshOVFPackageStatus(
        UserSession userSession, List<String> idsOvfpackageIn, Integer idEnterprise,
        Integer idRepository)
    {

        List<String> idsOvfpackage = idsOvfpackageIn; // XXX cast to arraylist

        DataResult<List<OVFPackageInstanceStatus>> result =
            new DataResult<List<OVFPackageInstanceStatus>>();

        AppsLibraryCommand proxyService = proxyService(userSession);
        try
        {
            OVFPackageInstanceStatusListDto statusListDto =
                proxyService.refreshOVFPackageStatus(userSession, idsOvfpackage, idEnterprise,
                    idRepository);

            result.setData(transform(statusListDto));
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

    protected OVFPackage transform(OVFPackageDto packDto)
    {
        OVFPackage pack = new OVFPackage();
        if (packDto.getCategoryName() != null)
        {
            pack.setCategory(packDto.getCategoryName());
        }
        else
        {
            pack.setCategory("Others");
        }
        pack.setDescription(packDto.getDescription());
        pack.setDiskFormat(DiskFormat.fromValue(packDto.getDiskFormatTypeUri()).name());
        pack.setDiskSizeMb(packDto.getDiskSizeMb());
        pack.setIconUrl(packDto.getIconPath());
        pack.setIdOVFPackage(packDto.getId());
        pack.setName(packDto.getProductName()); // XXX duplicated name
        pack.setProductName(packDto.getProductName());
        pack.setProductUrl(packDto.getProductUrl());
        pack.setProductVendor(packDto.getProductVendor());
        pack.setProductVersion(packDto.getProductVersion());
        pack.setUrl(packDto.getUrl());

        return pack;
    }

    protected OVFPackageList transform(OVFPackageListDto listDto)
    {
        OVFPackageList list = new OVFPackageList();
        list.setName(listDto.getName());
        list.setUrl("unused URL"); // TODO missing URL

        List<OVFPackage> packs = new LinkedList<OVFPackage>();

        if (listDto.getOvfPackages() != null)
        {
            for (OVFPackageDto packDto : listDto.getOvfPackages())
            {
                packs.add(transform(packDto));
            }
        }

        list.setOvfpackages(packs);
        return list;
    }

    protected OVFPackageInstanceStatus transform(OVFPackageInstanceStatusDto statusDto)
    {
        OVFPackageInstanceStatus status = new OVFPackageInstanceStatus();

        status.setStatus(statusDto.getOvfPackageStatus().name());
        status.setUrl(statusDto.getOvfId());

        status.setError(statusDto.getErrorCause());

        if (statusDto.getProgress() != null)
        {
            status.setProgress(statusDto.getProgress().floatValue());
        }

        return status;
    }

    protected List<OVFPackageInstanceStatus> transform(OVFPackageInstanceStatusListDto statusListDto)
    {
        List<OVFPackageInstanceStatus> statusList = new LinkedList<OVFPackageInstanceStatus>();

        for (OVFPackageInstanceStatusDto statusDto : statusListDto.getOvfPackageInstancesStatus())
        {
            statusList.add(transform(statusDto));
        }

        return statusList;
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

    public DataResult<OVFPackageInstanceStatus> refreshOVFPackageInstanceStatus(
        UserSession userSession, String idsOvfpackageIn, Integer idEnterprise, Integer idRepository)
    {

        String idsOvfpackage = idsOvfpackageIn; // XXX cast to arraylist

        DataResult<OVFPackageInstanceStatus> result = new DataResult<OVFPackageInstanceStatus>();

        AppsLibraryCommand proxyService = proxyService(userSession);
        try
        {
            OVFPackageInstanceStatusDto statusListDto =
                proxyService.refreshOVFPackageInstanceStatus(userSession, idsOvfpackage,
                    idEnterprise, idRepository);

            result.setData(transform(statusListDto));
            result.setSuccess(true);
            if (result.getData().getError() != null && !"".equals(result.getData().getError()))
            {
                result.setSuccess(false);
            }
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
}
