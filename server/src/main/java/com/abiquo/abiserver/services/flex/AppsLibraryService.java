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
import com.abiquo.abiserver.commands.stub.APIStubFactory;
import com.abiquo.abiserver.commands.stub.DatacenterRepositoryResourceStub;
import com.abiquo.abiserver.commands.stub.VirtualMachineTemplateResourceStub;
import com.abiquo.abiserver.commands.stub.impl.DatacenterRepositoryResourceStubImpl;
import com.abiquo.abiserver.commands.stub.impl.VirtualMachineTemplateResourceStubImpl;
import com.abiquo.abiserver.pojo.authentication.UserSession;
import com.abiquo.abiserver.pojo.result.BasicResult;
import com.abiquo.abiserver.pojo.result.DataResult;
import com.abiquo.abiserver.pojo.virtualimage.Category;
import com.abiquo.abiserver.pojo.virtualimage.Icon;
import com.abiquo.abiserver.pojo.virtualimage.OVFPackageInstanceStatus;
import com.abiquo.abiserver.pojo.virtualimage.OVFPackageList;
import com.abiquo.abiserver.pojo.virtualimage.Repository;
import com.abiquo.abiserver.pojo.virtualimage.VirtualImage;
import com.abiquo.server.core.appslibrary.CategoryDto;
import com.abiquo.server.core.appslibrary.IconDto;

public class AppsLibraryService
{

    private final static boolean REPOSITORY_INCLUDE_USAGE = true;

    public AppsLibraryService()
    {
    }

    public DataResult<Repository> getDatacenterRepository(final UserSession userSession,
        final Integer idDatacenter, final Integer idEnterprise, final Boolean refresh)
    {
        // TODO idEnterpise is not used
        DatacenterRepositoryResourceStub dcRepoStub =
            APIStubFactory.getInstance(userSession, new DatacenterRepositoryResourceStubImpl(),
                DatacenterRepositoryResourceStub.class);

        // refresh content and get
        return dcRepoStub.getRepository(idDatacenter, idEnterprise, refresh,
            REPOSITORY_INCLUDE_USAGE);
    }

    /** Virtual images */

    /**
     * @param idRepo, if 0, indicate stateful images
     * @param idCategory, if 0 indicate return all the categories
     */
    public DataResult<List<VirtualImage>> getVirtualImageByCategoryAndHypervisorCompatible(
        final UserSession userSession, final Integer idEnterprise, final Integer idRepo,
        final String categoryName, final String hypervisorTypeName, final Integer idDatacenter)
    {
        final VirtualMachineTemplateResourceStub vimageStub =
            APIStubFactory.getInstance(userSession, new VirtualMachineTemplateResourceStubImpl(),
                VirtualMachineTemplateResourceStub.class);

        // idRepo == 0 --> stateful
        final Boolean includeStateful = idRepo == null || idRepo == 0;

        DataResult<List<VirtualImage>> listImages =
            vimageStub.getVirtualMachineTemplateByCategoryAndHypervisorCompatible(idEnterprise,
                idDatacenter, categoryName, hypervisorTypeName, includeStateful);

        return fixVirtaulImageRepositroyAndEnterprise(listImages, idEnterprise, idRepo);
    }

    /**
     * @param idRepo, if 0, indicate stateful images
     * @param idCategory, if 0 indicate return all the categories
     */
    public DataResult<List<VirtualImage>> getVirtualImageByCategory(final UserSession userSession,
        final Integer idEnterprise, final Integer idRepo, final String categoryName,
        final Integer idDatacenter)
    {
        final VirtualMachineTemplateResourceStub vimageStub =
            APIStubFactory.getInstance(userSession, new VirtualMachineTemplateResourceStubImpl(),
                VirtualMachineTemplateResourceStub.class);

        // idRepo == 0 --> stateful
        final Integer datacenterId = idRepo == 0 ? null : idDatacenter;

        DataResult<List<VirtualImage>> listImages =
            vimageStub
                .getVirtualMachineTemplateByCategory(idEnterprise, datacenterId, categoryName);

        return fixVirtaulImageRepositroyAndEnterprise(listImages, idEnterprise, idRepo);
    }

    private DataResult<List<VirtualImage>> fixVirtaulImageRepositroyAndEnterprise(
        final DataResult<List<VirtualImage>> images, final Integer idEnterprise,
        final Integer idRepository)
    {
        for (VirtualImage vimage : images.getData())
        {
            vimage.setIdEnterprise(idEnterprise);
            vimage.getRepository().setId(idRepository);
        }

        return images;
    }

    /** List. */
    public DataResult<List<String>> getOVFPackageListName(final UserSession userSession,
        final Integer idEnterprise)
    {

        return proxyStub(userSession).getTemplateDefinitionListNames(idEnterprise);
    }

    public DataResult<OVFPackageList> getOVFPackageList(final UserSession userSession,
        final Integer idEnterprise, final String nameOVFPackageList)
    {
        return proxyStub(userSession).getTemplateDefinitionList(idEnterprise, nameOVFPackageList);
    }

    public DataResult<OVFPackageList> createOVFPackageList(final UserSession userSession,
        final Integer idEnterprise, final String ovfpackageListURL)
    {
        return proxyStub(userSession).createTemplateDefinitionListFromOVFIndexUrl(idEnterprise,
            ovfpackageListURL);
    }

    public DataResult<OVFPackageList> refreshOVFPackageList(final UserSession userSession,
        final Integer idEnterprise, final String nameOvfpackageList)
    {

        return proxyStub(userSession).refreshTemplateDefinitionListFromRepository(idEnterprise,
            nameOvfpackageList);
    }

    public BasicResult deleteOVFPackageList(final UserSession userSession,
        final Integer idEnterprise, final String nameOvfpackageList)
    {
        return proxyStub(userSession)
            .deleteTemplateDefinitionList(idEnterprise, nameOvfpackageList);
    }

    /** DC specific status. */
    public DataResult<List<OVFPackageInstanceStatus>> getOVFPackageListStatus(
        final UserSession userSession, final String nameOVFPackageList, final Integer idEnterprise,
        final Integer idDatacenter)
    {
        return proxyStub(userSession).getTemplatesState(nameOVFPackageList, idEnterprise,
            idDatacenter);
    }

    public DataResult<List<OVFPackageInstanceStatus>> refreshOVFPackageStatus(
        final UserSession userSession, final List<String> ovfUrlsIn, final Integer idEnterprise,
        final Integer idDatacenter)
    {
        final List<String> ovfUrls = ovfUrlsIn; // XXX cast to arraylist

        return proxyStub(userSession).getTemplatesState(ovfUrls, idEnterprise, idDatacenter);

    }

    public DataResult<OVFPackageInstanceStatus> refreshOVFPackageInstanceStatus(
        final UserSession userSession, final String ovfUrl, final Integer idEnterprise,
        final Integer idDatacenter)
    {
        return proxyStub(userSession).getTemplateState(ovfUrl, idEnterprise, idDatacenter);
    }

    public BasicResult startDownloadOVFPackage(final UserSession userSession,
        final List<String> idsOvfpackageIn, final Integer idEnterprise, final Integer idDatacenter)
    {
        final List<String> ovfUrls = idsOvfpackageIn; // XXX cast to arraylist

        return proxyStub(userSession).installTemplateDefinitionsInDatacenter(ovfUrls, idEnterprise,
            idDatacenter);
    }

    public DataResult<OVFPackageInstanceStatus> cancelDownloadOVFPackage(
        final UserSession userSession, final String ovfUrl, final Integer idEnterprise,
        final Integer idDatacenter)
    {
        return proxyStub(userSession).uninstallTemplateDefinitionInDatacenter(ovfUrl, idEnterprise,
            idDatacenter);
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
    public DataResult<List<com.abiquo.abiserver.pojo.virtualimage.DiskFormatType>> getDiskFormatTypes(
        final UserSession userSession)
    {

        return proxyStub(userSession).getDiskFormatTypes();
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

    public BasicResult editVirtualImage(final UserSession userSession, final Integer idEnterprise,
        final Integer idDatacenter, final VirtualImage vimage)
    {

        final VirtualMachineTemplateResourceStub vimageStub =
            APIStubFactory.getInstance(userSession, new VirtualMachineTemplateResourceStubImpl(),
                VirtualMachineTemplateResourceStub.class);

        return vimageStub.editVirtualImage(idEnterprise, idDatacenter, vimage);
    }

    public BasicResult deleteVirtualImage(final UserSession userSession,
        final Integer idEnterprise, final Integer idDatacenter, final Integer idVirtualImage)
    {

        final VirtualMachineTemplateResourceStub vimageStub =
            APIStubFactory.getInstance(userSession, new VirtualMachineTemplateResourceStubImpl(),
                VirtualMachineTemplateResourceStub.class);

        return vimageStub.deleteVirtualMachineTemplate(idEnterprise, idDatacenter, idVirtualImage);

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
     * CATEGORY
     */

    public DataResult<List<Category>> getCategories(final UserSession userSession)
    {
        return proxyStub(userSession).getCategories();
    }

    public DataResult<Category> createCategory(final UserSession userSession,
        final String categoryName)
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
