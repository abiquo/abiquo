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

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import org.apache.wink.client.ClientResponse;
import org.apache.wink.client.Resource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.abiquo.abiserver.commands.stub.AbstractAPIStub;
import com.abiquo.abiserver.config.AbiConfigManager;
import com.abiquo.abiserver.pojo.result.BasicResult;
import com.abiquo.abiserver.pojo.result.DataResult;
import com.abiquo.abiserver.pojo.virtualimage.Category;
import com.abiquo.abiserver.pojo.virtualimage.Icon;
import com.abiquo.abiserver.pojo.virtualimage.OVFPackage;
import com.abiquo.abiserver.pojo.virtualimage.OVFPackageInstanceStatus;
import com.abiquo.abiserver.pojo.virtualimage.OVFPackageList;
import com.abiquo.appliancemanager.transport.TemplateStateDto;
import com.abiquo.appliancemanager.transport.TemplatesStateDto;
import com.abiquo.model.enumerator.DiskFormatType;
import com.abiquo.model.rest.RESTLink;
import com.abiquo.model.transport.error.ErrorDto;
import com.abiquo.model.transport.error.ErrorsDto;
import com.abiquo.ovfmanager.ovf.section.DiskFormat;
import com.abiquo.server.core.appslibrary.CategoriesDto;
import com.abiquo.server.core.appslibrary.CategoryDto;
import com.abiquo.server.core.appslibrary.DiskFormatTypeDto;
import com.abiquo.server.core.appslibrary.DiskFormatTypesDto;
import com.abiquo.server.core.appslibrary.IconDto;
import com.abiquo.server.core.appslibrary.IconsDto;
import com.abiquo.server.core.appslibrary.TemplateDefinitionDto;
import com.abiquo.server.core.appslibrary.TemplateDefinitionListDto;
import com.abiquo.server.core.appslibrary.TemplateDefinitionListsDto;
import com.abiquo.server.core.appslibrary.TemplateDefinitionsDto;

public class AppsLibraryStubImpl extends AbstractAPIStub implements AppsLibraryStub
{

    private static final Logger logger = LoggerFactory.getLogger(AppsLibraryStubImpl.class);

    public static final String ENTERPRISES_PATH = "admin/enterprises";

    public static final String TEMPLATE_DEFINITION_PATH = "appslib/templateDefinitions";

    public static final String TEMPLATE_DEFINITION_LISTS_PATH = "appslib/templateDefinitionList";

    @Override
    public DataResult<Icon> createIcon(final IconDto icon)
    {
        DataResult<Icon> result = new DataResult<Icon>();

        String uri = createIconsLink();

        ClientResponse response = post(uri, icon);

        if (response.getStatusCode() / 200 == 1)
        {
            result.setSuccess(Boolean.TRUE);
            result.setData(createFlexIconObject(response.getEntity(IconDto.class)));
        }
        else
        {
            populateErrors(response, result, "createIcon");
        }

        return result;
    }

    private Category createFlexCategoryObject(final CategoryDto dto)
    {
        Category category = new Category();
        return category.toPojo(dto);
    }

    @Override
    public DataResult<OVFPackageList> createTemplateDefinitionListFromOVFIndexUrl(
        final Integer idEnterprise, final String ovfindexURL)
    {
        DataResult<OVFPackageList> result = new DataResult<OVFPackageList>();

        String uri = createTemplateDefinitionListsLink(idEnterprise.toString());

        // Resource resource = createResourceOVFPackageLists(idEnterprise);
        // resource.queryParam("ovfindexURL", ovfpackageListURL);

        ClientResponse response =
            resource(uri).accept(MediaType.APPLICATION_XML).contentType(MediaType.TEXT_PLAIN).post(
                ovfindexURL);
        final Integer httpStatus = response.getStatusCode();

        if (httpStatus / 200 != 1)
        {
            populateErrors(response, result, "createOVFPackageList");
        }
        else
        {
            result.setSuccess(Boolean.TRUE);
            result.setData(createFlexOVFPackageListObject(response
                .getEntity(TemplateDefinitionListDto.class)));
        }

        return result;
    }

    @Override
    public BasicResult deleteTemplateDefinitionList(final Integer idEnterprise,
        final String templateDefinitionListName)
    {

        BasicResult result = new BasicResult();
        final Integer templateDefinitionListId =
            getTemplateDefinitionListIdFromName(idEnterprise, templateDefinitionListName);

        String uri =
            createTemplateDefinitionListLink(idEnterprise.toString(), templateDefinitionListId
                .toString());
        ClientResponse response = delete(uri);

        final Integer httpStatus = response.getStatusCode();

        if (httpStatus / 200 != 1)
        {
            populateErrors(response, result, "deleteOVFPackageList");
        }
        else
        {
            result.setSuccess(Boolean.TRUE);
        }
        return result;
    }

    @Override
    public DataResult<List<OVFPackageInstanceStatus>> getTemplatesState(
        final String templateDefinitionListName, final Integer idEnterprise,
        final Integer datacenterId)
    {
        final DataResult<List<OVFPackageInstanceStatus>> result =
            new DataResult<List<OVFPackageInstanceStatus>>();

        final Integer listId =
            getTemplateDefinitionListIdFromName(idEnterprise, templateDefinitionListName);
        final String uri =
            createTemplateStateFromListLink(idEnterprise.toString(), String.valueOf(listId));

        ClientResponse response = resource(uri).queryParam("datacenterId", datacenterId).get();

        if (response.getStatusCode() / 200 == 1)
        {
            result.setSuccess(Boolean.TRUE);
            result.setData(createFlexOVFPackageListObject(response
                .getEntity(TemplatesStateDto.class)));
        }
        else
        {
            populateErrors(response, result, "getOVFPackageList");
            // TODO getOVFPackageListStatus messages
        }

        return result;
    }

    @Override
    public DataResult<List<OVFPackageInstanceStatus>> getTemplatesState(final List<String> ovfUrls,
        final Integer idEnterprise, final Integer datacenterId)
    {
        final DataResult<List<OVFPackageInstanceStatus>> result =
            new DataResult<List<OVFPackageInstanceStatus>>();
        final List<OVFPackageInstanceStatus> list = new LinkedList<OVFPackageInstanceStatus>();
        for (String templateDefinitionUrl : ovfUrls)
        {
            list.add(getTemplateState(templateDefinitionUrl, idEnterprise, datacenterId).getData());
        }

        result.setSuccess(Boolean.TRUE);
        result.setData(list);
        return result;
    }

    @Override
    public DataResult<OVFPackageInstanceStatus> getTemplateState(
        final String templateDefinitionUrl, final Integer idEnterprise, final Integer datacenterId)
    {
        final DataResult<OVFPackageInstanceStatus> result =
            new DataResult<OVFPackageInstanceStatus>();

        final Integer templateDefinitionId =
            getTemplateDefinitionIdByUrl(templateDefinitionUrl, idEnterprise);

        final String uri =
            createTemplateStateLink(String.valueOf(idEnterprise), String
                .valueOf(templateDefinitionId));

        ClientResponse response = resource(uri).queryParam("datacenterId", datacenterId).get();

        if (response.getStatusCode() / 200 == 1)
        {
            result.setSuccess(Boolean.TRUE);
            result.setData(createFlexOVFPackageListObject(response
                .getEntity(TemplateStateDto.class)));
        }
        else
        {
            populateErrors(response, result, "getOVFPackageList");
            // TODO getOVFPackageStatus messages
        }

        return result;
    }

    @Override
    public BasicResult installTemplateDefinitionsInDatacenter(
        final List<String> templateDefinitionUrls, final Integer idEnterprise,
        final Integer datacenterId)
    {
        for (String templateDefUrl : templateDefinitionUrls)
        {
            installTemplateDefinitionInDatacenter(templateDefUrl, idEnterprise, datacenterId);
        }

        BasicResult result = new BasicResult();
        result.setSuccess(true);
        return result;
    }

    private void installTemplateDefinitionInDatacenter(final String templateDefinitionUrl,
        final Integer idEnterprise, final Integer datacenterId)
    {
        final Integer templateDefinitionId =
            getTemplateDefinitionIdByUrl(templateDefinitionUrl, idEnterprise);

        final String uri =
            createTemplateDefinitionInstallLink(String.valueOf(idEnterprise), String
                .valueOf(templateDefinitionId));

        Resource resource = resource(uri).contentType(MediaType.TEXT_PLAIN);
        ClientResponse response = resource.post(String.valueOf(datacenterId));
        // TODO post use the the provided mediatype both for mediatype and accepttype
        // ClientResponse response = post(uri, String.valueOf(ovfPackageId), MediaType.TEXT_PLAIN);

        if (response.getStatusCode() / 200 != 1)
        {
            logger.error("Can't install TemplateDefinition {} in dc {}", templateDefinitionUrl,
                datacenterId);
            // error cause will be shown with getOVFPackageState
        }
    }

    @Override
    public DataResult<OVFPackageInstanceStatus> uninstallTemplateDefinitionInDatacenter(
        final String templateDefinitionUrl, final Integer idEnterprise, final Integer datacenterId)
    {

        final Integer templateDefinitionId =
            getTemplateDefinitionIdByUrl(templateDefinitionUrl, idEnterprise);

        final String uri =
            createTemplateDefinitionUninstallLink(String.valueOf(idEnterprise), String
                .valueOf(templateDefinitionId));

        Resource resource = resource(uri).contentType(MediaType.TEXT_PLAIN);
        ClientResponse response = resource.post(String.valueOf(datacenterId));
        // TODO post use the the provided mediatype both for mediatype and accepttype
        // ClientResponse response = post(uri, String.valueOf(ovfPackageId), MediaType.TEXT_PLAIN);

        if (response.getStatusCode() / 200 != 1)
        {
            logger.error("Can't install TemplateDefinition {} in dc {}", templateDefinitionUrl,
                datacenterId);
            // error cause will be shown with getOVFPackageState
        }

        return new DataResult<OVFPackageInstanceStatus>(); // TODO no content
    }

    @Override
    public DataResult<OVFPackageList> getTemplateDefinitionList(final Integer idEnterprise,
        final String templateDefinitionListName)
    {

        DataResult<OVFPackageList> result = new DataResult<OVFPackageList>();
        final Integer templateDefListId =
            getTemplateDefinitionListIdFromName(idEnterprise, templateDefinitionListName);
        String uri =
            createTemplateDefinitionListLink(idEnterprise.toString(), templateDefListId.toString());

        ClientResponse response = get(uri);

        if (response.getStatusCode() / 200 == 1)
        {
            result.setSuccess(Boolean.TRUE);
            result.setData(createFlexOVFPackageListObject(response
                .getEntity(TemplateDefinitionListDto.class)));
        }
        else
        {
            populateErrors(response, result, "getOVFPackageList");
        }

        return result;
    }

    private Integer getTemplateDefinitionListIdFromName(final Integer idEnterprise,
        final String templateDefinitionListName)
    {
        TemplateDefinitionListsDto packageLists = getTemplateDefinitionLists(idEnterprise);

        for (TemplateDefinitionListDto list : packageLists.getCollection())
        {
            final String listName = list.getName();
            if (templateDefinitionListName.equalsIgnoreCase(listName))
            {
                return list.getId();
            }
        }

        final String cause =
            String.format("Can not locat TemplateDescriptionList named [%s] for enterprise [%s]",
                templateDefinitionListName, idEnterprise);
        final Response response = Response.status(Status.NOT_FOUND).entity(cause).build();
        throw new WebApplicationException(response);
    }

    @Override
    public DataResult<List<String>> getTemplateDefinitionListNames(final Integer idEnterprise)
    {
        DataResult<List<String>> result = new DataResult<List<String>>();

        List<String> templateDefListNames = new LinkedList<String>();

        TemplateDefinitionListsDto listsDto = new TemplateDefinitionListsDto();
        try
        {
            listsDto = getTemplateDefinitionLists(idEnterprise);
        }
        catch (WebApplicationException e)
        {
            result.setSuccess(Boolean.FALSE);
            result.setMessage(e.getMessage());
            return result;
        }

        if (listsDto == null || listsDto.getCollection().size() == 0)
        {
            DataResult<OVFPackageList> defaultList = addDefaultTemplateDefinitionList(idEnterprise);
            if (defaultList == null)
            {
                result.setSuccess(Boolean.FALSE);
                return result;
            }
            templateDefListNames.add(defaultList.getData().getName());
        }
        for (TemplateDefinitionListDto list : listsDto.getCollection())
        {
            templateDefListNames.add(list.getName());
        }
        result.setSuccess(Boolean.TRUE);
        result.setData(templateDefListNames);

        return result;
    }

    private DataResult<OVFPackageList> addDefaultTemplateDefinitionList(final Integer idEnterprise)
    {
        String defaultTemplateRepository =
            AbiConfigManager.getInstance().getAbiConfig().getDefaultTemplateRepository();
        if (defaultTemplateRepository == null || defaultTemplateRepository.isEmpty())
        {
            logger.debug("There aren't any default repository space defined");
            return null;
        }
        else
        {
            logger.debug("Adding default repository space at [{}]", defaultTemplateRepository);
            DataResult<OVFPackageList> list =
                createTemplateDefinitionListFromOVFIndexUrl(idEnterprise, defaultTemplateRepository);
            return list;

        }
    }

    // XXX not used on the AppsLibraryCommand
    private TemplateDefinitionListsDto getTemplateDefinitionLists(final Integer idEnterprise)
    {

        String uri = createTemplateDefinitionListsLink(idEnterprise.toString());
        ClientResponse response = get(uri);

        final Integer httpStatus = response.getStatusCode();
        if (httpStatus != 200)
        {
            throw new WebApplicationException(response(response));
        }

        return response.getEntity(TemplateDefinitionListsDto.class);
    }

    @Override
    public DataResult<OVFPackageList> refreshTemplateDefinitionListFromRepository(
        final Integer idEnterprise, final String templateDefintionListName)
    {
        DataResult<OVFPackageList> result = new DataResult<OVFPackageList>();

        final Integer idList =
            getTemplateDefinitionListIdFromName(idEnterprise, templateDefintionListName);
        TemplateDefinitionListDto list = new TemplateDefinitionListDto();
        try
        {
            list = refreshTemplateDefintionListFromRepository(idEnterprise, idList);
        }
        catch (WebApplicationException e)
        {
            result.setSuccess(Boolean.FALSE);
            result.setMessage(e.getMessage());
        }
        result.setSuccess(Boolean.TRUE);
        result.setData(createFlexOVFPackageListObject(list));

        return result;

    }

    private TemplateDefinitionListDto refreshTemplateDefintionListFromRepository(
        final Integer idEnterprise, final Integer idList)
    {

        String uri = createTemplateDefinitionListLink(idEnterprise.toString(), idList.toString());
        ClientResponse response =
            resource(uri).accept(MediaType.APPLICATION_XML).contentType(MediaType.TEXT_PLAIN).put(
                null);

        final Integer httpStatus = response.getStatusCode();
        if (httpStatus != 200)
        {
            throw new WebApplicationException(response(response));
        }

        return response.getEntity(TemplateDefinitionListDto.class);
    }

    @Deprecated
    // TODO remove
    private static Response response(final ClientResponse response)
    {
        String cause = new String();
        try
        {
            ErrorsDto errors = response.getEntity(ErrorsDto.class);
            for (ErrorDto e : errors.getCollection())
            {
                cause = cause.concat(e.getMessage());
            }
        }
        catch (Exception e)
        {
            cause = response.getEntity(String.class);
        }

        return Response.status(response.getStatusCode()).entity(cause).build();
    }

    @Override
    public TemplateDefinitionsDto getTemplateDefinitions(final Integer idEnterprise,
        final String templateDefinitionListName)
    {
        final Integer templateDefinitionListId =
            getTemplateDefinitionListIdFromName(idEnterprise, templateDefinitionListName);

        String uri =
            createTemplateDefinitionLink(idEnterprise.toString(), templateDefinitionListId
                .toString());
        ClientResponse response = get(uri);

        final Integer httpStatus = response.getStatusCode();

        if (httpStatus != 200)
        {
            throw new WebApplicationException(response(response));
        }

        return response.getEntity(TemplateDefinitionsDto.class);
    }

    @Override
    public BasicResult deleteIcon(final Integer idIcon)
    {
        BasicResult result = new BasicResult();

        final String uri = createIconLink(idIcon);

        ClientResponse response = delete(uri);

        if (response.getStatusCode() / 200 == 1)
        {
            result.setSuccess(Boolean.TRUE);
        }
        else
        {
            populateErrors(response, result, "deleteIcon");
        }
        return result;
    }

    @Override
    public BasicResult editIcon(final Icon icon)
    {
        DataResult<Icon> result = new DataResult<Icon>();

        final String uri = createIconLink(icon.getId());

        IconDto iconDto = new IconDto();

        iconDto.setId(icon.getId());
        iconDto.setName(icon.getName());
        iconDto.setPath(icon.getPath());

        ClientResponse response = put(uri, iconDto);

        if (response.getStatusCode() / 200 == 1)
        {
            result.setSuccess(Boolean.TRUE);
            result.setData(createFlexIconObject(response.getEntity(IconDto.class)));
        }
        else
        {
            populateErrors(response, result, "editIcon");
        }
        return result;

    }

    @Override
    public DataResult<List<Icon>> getIcons()
    {

        DataResult<List<Icon>> result = new DataResult<List<Icon>>();

        final String uri = createIconsLink();

        ClientResponse response = get(uri);

        if (response.getStatusCode() / 200 == 1)
        {
            result.setSuccess(Boolean.TRUE);
            IconsDto icons = response.getEntity(IconsDto.class);
            List<Icon> listIcon = new ArrayList<Icon>();
            for (IconDto icon : icons.getCollection())
            {
                listIcon.add(createFlexIconObject(icon));
            }
            result.setData(listIcon);
        }
        else
        {
            populateErrors(response, result, "getIcons");
        }

        return result;

    }

    public static Icon createFlexIconObject(final IconDto iconDto)
    {
        Icon icon = new Icon();
        icon.setName(iconDto.getName());
        icon.setPath(iconDto.getPath());
        icon.setId(iconDto.getId());
        return icon;
    }

    @Override
    public DataResult<List<com.abiquo.abiserver.pojo.virtualimage.DiskFormatType>> getDiskFormatTypes()
    {

        DataResult<List<com.abiquo.abiserver.pojo.virtualimage.DiskFormatType>> result =
            new DataResult<List<com.abiquo.abiserver.pojo.virtualimage.DiskFormatType>>();

        final String uri = createDiskFormatTypesLink();

        ClientResponse response = get(uri);

        if (response.getStatusCode() / 200 == 1)
        {
            result.setSuccess(Boolean.TRUE);
            DiskFormatTypesDto diskFormatTypes = response.getEntity(DiskFormatTypesDto.class);

            List<com.abiquo.abiserver.pojo.virtualimage.DiskFormatType> listDiskFormatType =
                new ArrayList<com.abiquo.abiserver.pojo.virtualimage.DiskFormatType>();
            for (DiskFormatTypeDto diskFormatType : diskFormatTypes.getCollection())
            {
                listDiskFormatType.add(createFlexDiskFormatTypeObject(diskFormatType));
            }
            result.setData(listDiskFormatType);
        }
        else
        {
            populateErrors(response, result, "getDiskFormatTypes");
        }

        return result;

    }

    public static com.abiquo.abiserver.pojo.virtualimage.DiskFormatType createFlexDiskFormatTypeObject(
        final DiskFormatTypeDto diskFormatTypeDto)
    {
        return new com.abiquo.abiserver.pojo.virtualimage.DiskFormatType(DiskFormatType
            .fromId(diskFormatTypeDto.getId()));
    }

    protected OVFPackageList createFlexOVFPackageListObject(final TemplateDefinitionListDto listDto)
    {
        OVFPackageList list = new OVFPackageList();
        list.setName(listDto.getName());
        list.setUrl(listDto.getUrl());

        List<OVFPackage> packs = new LinkedList<OVFPackage>();

        if (listDto.getTemplateDefinitions() != null)
        {
            for (TemplateDefinitionDto packDto : listDto.getTemplateDefinitions().getCollection())
            {
                packs.add(createFlexOVFPackageObject(packDto));
            }
        }

        list.setOvfpackages(packs);
        return list;
    }

    protected OVFPackage createFlexOVFPackageObject(final TemplateDefinitionDto packDto)
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
        pack.setDiskFormat(DiskFormat.fromValue(packDto.getDiskFormatType()).name());
        pack.setDiskSizeMb(packDto.getDiskFileSize());
        RESTLink iconLink = packDto.searchLink("icon");
        if (iconLink != null)
        {
            pack.setIconUrl(iconLink.getTitle());
        }

        pack.setIdOVFPackage(packDto.getId());
        pack.setName(packDto.getProductName()); // XXX duplicated name
        pack.setProductName(packDto.getProductName());
        pack.setProductUrl(packDto.getProductUrl());
        pack.setProductVendor(packDto.getProductVendor());
        pack.setProductVersion(packDto.getProductVersion());
        pack.setUrl(packDto.getUrl());

        return pack;
    }

    private List<OVFPackageInstanceStatus> createFlexOVFPackageListObject(
        final TemplatesStateDto entity)
    {
        List<OVFPackageInstanceStatus> statusList = new LinkedList<OVFPackageInstanceStatus>();

        for (TemplateStateDto statusDto : entity.getCollection())
        {
            statusList.add(createFlexOVFPackageListObject(statusDto));
        }

        return statusList;
    }

    protected OVFPackageInstanceStatus createFlexOVFPackageListObject(
        final TemplateStateDto statusDto)
    {
        OVFPackageInstanceStatus status = new OVFPackageInstanceStatus();

        status.setStatus(statusDto.getStatus().name());
        status.setUrl(statusDto.getOvfId());

        status.setError(statusDto.getErrorCause());

        if (statusDto.getDownloadingProgress() != null)
        {
            status.setProgress(statusDto.getDownloadingProgress().floatValue());
        }

        return status;
    }

    // TODO the client should use IDS for OVFPackages .... then this code MUST die
    private Integer getTemplateDefinitionIdByUrl(final String templateDefinitionUrl,
        final Integer idEnterprise)
    {
        List<TemplateDefinitionDto> ovfs = getTemplateDefinitions(idEnterprise).getCollection();

        for (TemplateDefinitionDto ovf : ovfs)
        {
            if (templateDefinitionUrl.equalsIgnoreCase(ovf.getUrl()))
            {
                return ovf.getId();
            }
        }
        return null; // TODO FAIL ... see the TODO above, if there is a related bug fix it first
    }

    private TemplateDefinitionsDto getTemplateDefinitions(final Integer idEnterprise)
    {
        String uri = createTemplateDefinitionsLink(idEnterprise.toString());
        ClientResponse response = get(uri);

        final Integer httpStatus = response.getStatusCode();

        if (httpStatus != 200)
        {
            throw new WebApplicationException(response(response));
        }

        return response.getEntity(TemplateDefinitionsDto.class);
    }

    /**
     * CATEGORY
     */

    @Override
    public DataResult<List<Category>> getCategories()
    {

        DataResult<List<Category>> result = new DataResult<List<Category>>();

        final String uri = createCategoriesLink();

        ClientResponse response = get(uri);

        if (response.getStatusCode() == 200)
        {
            result.setSuccess(Boolean.TRUE);
            CategoriesDto categoriesDto = response.getEntity(CategoriesDto.class);
            List<Category> listCategory = new ArrayList<Category>();
            for (CategoryDto category : categoriesDto.getCollection())
            {
                listCategory.add(createFlexCategoryObject(category));
            }
            result.setData(listCategory);
        }
        else
        {
            populateErrors(response, result, "getIcons");
        }

        return result;

    }

    @Override
    public DataResult<Category> createCategory(final CategoryDto categoryDto)
    {
        DataResult<Category> result = new DataResult<Category>();

        String uri = createCategoriesLink();

        ClientResponse response = post(uri, categoryDto);

        if (response.getStatusCode() == 201)
        {
            result.setSuccess(Boolean.TRUE);
            result.setData(createFlexCategoryObject(response.getEntity(CategoryDto.class)));
        }
        else
        {
            populateErrors(response, result, "createCategory");
        }

        return result;
    }

    @Override
    public BasicResult deleteCategory(final Integer idCategory)
    {
        BasicResult result = new BasicResult();

        final String uri = createCategoryLink(idCategory);

        ClientResponse response = delete(uri);

        if (response.getStatusCode() == 200)
        {
            result.setSuccess(Boolean.TRUE);
        }
        else
        {
            populateErrors(response, result, "deleteCategory");
        }
        return result;
    }
}
