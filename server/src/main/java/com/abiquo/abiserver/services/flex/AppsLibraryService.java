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

import com.abiquo.abiserver.appslibrary.stub.AppsLibraryStub;
import com.abiquo.abiserver.appslibrary.stub.AppsLibraryStubImpl;
import com.abiquo.abiserver.business.BusinessDelegateProxy;
import com.abiquo.abiserver.business.UserSessionException;
import com.abiquo.abiserver.business.hibernate.pojohb.virtualimage.CategoryHB;
import com.abiquo.abiserver.business.hibernate.pojohb.virtualimage.IconHB;
import com.abiquo.abiserver.business.hibernate.pojohb.virtualimage.RepositoryHB;
import com.abiquo.abiserver.business.hibernate.pojohb.virtualimage.VirtualimageHB;
import com.abiquo.abiserver.commands.AppsLibraryCommand;
import com.abiquo.abiserver.commands.impl.AppsLibraryCommandImpl;
import com.abiquo.abiserver.commands.stub.APIStubFactory;
import com.abiquo.abiserver.exception.AppsLibraryCommandException;
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
        try
        {
            appsLibraryCommand =
                (AppsLibraryCommand) Thread.currentThread().getContextClassLoader().loadClass(
                    "com.abiquo.abiserver.commands.impl.AppsLibraryPremiumCommandImpl")
                    .newInstance();
        }
        catch (Exception e)
        {
            appsLibraryCommand = new AppsLibraryCommandImpl();
        }
    }

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

    public DataResult<Repository> getDatacenterRepository(final UserSession userSession,
        final Integer idDatacenter, final Integer idEnterprise) // TODO idEnterpise is not used
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
    public DataResult<Category> createCategory(final UserSession userSession,
        final Integer idEnterprise, final String categoryName)
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

    public BasicResult deleteCategory(final UserSession userSession, final Integer idCategory)
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

    public DataResult<List<Category>> getCategories(final UserSession userSession,
        final Integer idEnterprise)
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
    public DataResult<Icon> createIcon(final UserSession userSession, final Integer idEnterprise,
        final Icon icon)
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

    public BasicResult editIcon(final UserSession userSession, final Icon icon)
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

    public BasicResult deleteIcon(final UserSession userSession, final Integer idIcon)
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

    public DataResult<List<Icon>> getIcons(final UserSession userSession, final Integer idEnterprise)
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
        final UserSession userSession, final Integer idEnterprise, final Integer idRepo,
        final Integer idCategory, final Integer idHypervisorType)
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
    public DataResult<List<VirtualImage>> getVirtualImageByCategory(final UserSession userSession,
        final Integer idEnterprise, final Integer idRepo, final Integer idCategory)
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

    /** List. */
    public DataResult<List<String>> getOVFPackageListName(final UserSession userSession,
        final Integer idEnterprise)
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

    public DataResult<OVFPackageList> getOVFPackageList(final UserSession userSession,
        final Integer idEnterprise, final String nameOVFPackageList)
    {

        DataResult<OVFPackageList> result = new DataResult<OVFPackageList>();

        AppsLibraryStub proxy =
            APIStubFactory.getInstance(userSession, new AppsLibraryStubImpl(userSession),
                AppsLibraryStub.class);
        try
        {

            OVFPackageListDto packagListsDto =
                proxy.getOVFPackageList(idEnterprise, nameOVFPackageList);

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

    public DataResult<OVFPackageList> createOVFPackageList(final UserSession userSession,
        final Integer idEnterprise, final String ovfpackageListURL)
    {

        DataResult<OVFPackageList> result = new DataResult<OVFPackageList>();

        AppsLibraryStub proxy =
            APIStubFactory.getInstance(userSession, new AppsLibraryStubImpl(userSession),
                AppsLibraryStub.class);
        OVFPackageListDto packagListsDto =
            proxy.createOVFPackageList(idEnterprise, ovfpackageListURL);
        try
        {

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

    public DataResult<OVFPackageList> refreshOVFPackageList(final UserSession userSession,
        final Integer idEnterprise, final String nameOvfpackageList)
    {

        DataResult<OVFPackageList> result = new DataResult<OVFPackageList>();

        AppsLibraryStub proxy =
            APIStubFactory.getInstance(userSession, new AppsLibraryStubImpl(userSession),
                AppsLibraryStub.class);
        OVFPackageListDto packagListsDto =
            proxy.refreshOVFPackageList(idEnterprise, nameOvfpackageList);
        try
        {

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

    public BasicResult deleteOVFPackageList(final UserSession userSession,
        final Integer idEnterprise, final String nameOvfpackageList)
    {
        BasicResult result = new BasicResult();

        AppsLibraryStub proxy =
            APIStubFactory.getInstance(userSession, new AppsLibraryStubImpl(userSession),
                AppsLibraryStub.class);
        try
        {
            proxy.deleteOVFPackageList(idEnterprise, nameOvfpackageList);

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

    /** DC specific status. */
    public DataResult<List<OVFPackageInstanceStatus>> getOVFPackageListStatus(
        final UserSession userSession, final String nameOVFPackageList, final Integer idEnterprise,
        final Integer idRepository)
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

    public BasicResult startDownloadOVFPackage(final UserSession userSession,
        final List<String> idsOvfpackageIn, final Integer idEnterprise, final Integer idRepository)
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

    public DataResult<OVFPackageInstanceStatus> cancelDownloadOVFPackage(
        final UserSession userSession, final String idOvfpackage, final Integer idEnterprise,
        final Integer idRepository)
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
        final UserSession userSession, final List<String> idsOvfpackageIn,
        final Integer idEnterprise, final Integer idRepository)
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

    protected OVFPackage transform(final OVFPackageDto packDto)
    {
        OVFPackage pack = new OVFPackage();
        if (packDto.getName() != null)
        {
            pack.setCategory(packDto.getName());
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

    protected OVFPackageList transform(final OVFPackageListDto listDto)
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

    protected OVFPackageInstanceStatus transform(final OVFPackageInstanceStatusDto statusDto)
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

    protected List<OVFPackageInstanceStatus> transform(
        final OVFPackageInstanceStatusListDto statusListDto)
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

    /**
     * Refreshes the instance status of the image.
     * 
     * @param userSession current user.
     * @param idsOvfpackageIn Name of the item to refresh.
     * @param idEnterprise Id of {@link Enterprise} to which this {@link OVFPackage} belongs.
     * @param idRepository Id of the {@link Repository} to which the {@link OVFPackage} belongs.
     * @return DataResult<OVFPackageInstanceStatus>
     */
    public DataResult<OVFPackageInstanceStatus> refreshOVFPackageInstanceStatus(
        final UserSession userSession, final String idsOvfpackageIn, final Integer idEnterprise,
        final Integer idRepository)
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
                result.setMessage(result.getData().getError());
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
