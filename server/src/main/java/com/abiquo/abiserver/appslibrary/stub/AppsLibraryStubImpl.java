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
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.core.Response.Status.Family;

import org.apache.commons.lang.StringUtils;
import org.apache.wink.client.ClientResponse;
import org.apache.wink.client.Resource;
import org.jclouds.abiquo.domain.enterprise.TemplateDefinitionList;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.abiquo.abiserver.commands.stub.AbstractAPIStub;
import com.abiquo.abiserver.config.AbiConfigManager;
import com.abiquo.abiserver.persistence.DAOFactory;
import com.abiquo.abiserver.persistence.hibernate.HibernateDAOFactory;
import com.abiquo.abiserver.pojo.result.BasicResult;
import com.abiquo.abiserver.pojo.result.DataResult;
import com.abiquo.abiserver.pojo.virtualimage.Category;
import com.abiquo.abiserver.pojo.virtualimage.OVFPackage;
import com.abiquo.abiserver.pojo.virtualimage.OVFPackageInstanceStatus;
import com.abiquo.abiserver.pojo.virtualimage.OVFPackageList;
import com.abiquo.appliancemanager.client.AMClient;
import com.abiquo.appliancemanager.client.AMClientException;
import com.abiquo.appliancemanager.transport.TemplateStateDto;
import com.abiquo.appliancemanager.transport.TemplatesStateDto;
import com.abiquo.model.enumerator.DiskFormatType;
import com.abiquo.model.rest.RESTLink;
import com.abiquo.model.transport.LinksDto;
import com.abiquo.model.transport.error.ErrorDto;
import com.abiquo.model.transport.error.ErrorsDto;
import com.abiquo.server.core.appslibrary.CategoriesDto;
import com.abiquo.server.core.appslibrary.CategoryDto;
import com.abiquo.server.core.appslibrary.DiskFormatTypeDto;
import com.abiquo.server.core.appslibrary.DiskFormatTypesDto;
import com.abiquo.server.core.appslibrary.TemplateDefinitionDto;
import com.abiquo.server.core.appslibrary.TemplateDefinitionListDto;
import com.abiquo.server.core.appslibrary.TemplateDefinitionsDto;

public class AppsLibraryStubImpl extends AbstractAPIStub implements AppsLibraryStub
{

    private static final Logger logger = LoggerFactory.getLogger(AppsLibraryStubImpl.class);

    public static final String ENTERPRISES_PATH = "admin/enterprises";

    public static final String TEMPLATE_DEFINITION_PATH = "appslib/templateDefinitions";

    public static final String TEMPLATE_DEFINITION_LISTS_PATH = "appslib/templateDefinitionList";

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
            resource(uri, TemplateDefinitionListDto.MEDIA_TYPE).contentType(MediaType.TEXT_PLAIN)
                .post(ovfindexURL);

        if (response.getStatusType().getFamily() != Family.SUCCESSFUL)
        {
            populateErrors(response, result, "createOVFPackageList");
            result.setMessage(result.getMessage().concat("\n " + ovfindexURL));
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

        if (response.getStatusType().getFamily() != Family.SUCCESSFUL)
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

        ClientResponse response =
            resource(uri, TemplatesStateDto.MEDIA_TYPE).queryParam("datacenterId", datacenterId)
                .get();

        if (response.getStatusType().getFamily() == Family.SUCCESSFUL)
        {
            result.setSuccess(Boolean.TRUE);
            result.setData(createFlexOVFPackageListObject(response
                .getEntity(TemplatesStateDto.class)));
        }
        else
        {
            populateErrors(response, result, "getOVFPackageList");
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

        if (templateDefinitionId == null) // uploading
        {
            final String amUrl;
            DAOFactory factory = HibernateDAOFactory.instance();
            factory.beginConnection();
            try
            {
                amUrl =
                    factory
                        .getRemoteServiceDAO()
                        .getRemoteServiceUriByType(
                            datacenterId,
                            com.abiquo.abiserver.business.hibernate.pojohb.service.RemoteServiceType.APPLIANCE_MANAGER);
            }
            finally
            {
                factory.endConnection();
            }

            // XXX direct server->am communication
            final AMClient amClient = new AMClient().initialize(amUrl, false);

            TemplateStateDto uploadState;
            try
            {
                uploadState = amClient.getTemplateStatus(idEnterprise, templateDefinitionUrl);
                result.setSuccess(Boolean.TRUE);
                result.setData(createFlexOVFPackageListObject(uploadState));
                return result;
            }
            catch (AMClientException e)
            {
                result.setSuccess(Boolean.FALSE);
                result.setMessage(e.getMessage());
                return result;
            }
        }

        final String uri =
            createTemplateStateLink(String.valueOf(idEnterprise), String
                .valueOf(templateDefinitionId));

        ClientResponse response =
            resource(uri, TemplateStateDto.MEDIA_TYPE).queryParam("datacenterId", datacenterId)
                .get();

        if (response.getStatusType().getFamily() == Family.SUCCESSFUL)
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
        StringBuffer error = new StringBuffer();
        for (String templateDefUrl : templateDefinitionUrls)
        {

            BasicResult partRes =
                installTemplateDefinitionInDatacenter(templateDefUrl, idEnterprise, datacenterId);
            if (!partRes.getSuccess())
            {
                error.append("\n").append(templateDefUrl).append(" : ")
                    .append(partRes.getMessage());
            }
        }

        BasicResult result = new BasicResult();
        result.setSuccess(true);
        String totalError = error.toString();
        if (!StringUtils.isEmpty(totalError))
        {
            result.setSuccess(false);
            result.setMessage(totalError);
        }

        return result;
    }

    private BasicResult installTemplateDefinitionInDatacenter(final String templateDefinitionUrl,
        final Integer idEnterprise, final Integer datacenterId)
    {
        final Integer templateDefinitionId =
            getTemplateDefinitionIdByUrl(templateDefinitionUrl, idEnterprise);

        final String uri =
            createTemplateDefinitionInstallLink(String.valueOf(idEnterprise), String
                .valueOf(templateDefinitionId));

        Resource resource = resource(uri, MediaType.TEXT_PLAIN);
        ClientResponse response = resource.post(String.valueOf(datacenterId));
        // TODO post use the the provided mediatype both for mediatype and accepttype
        // ClientResponse response = post(uri, String.valueOf(ovfPackageId), MediaType.TEXT_PLAIN);
        BasicResult result = new BasicResult();
        result.setSuccess(true);
        if (response.getStatusType().getFamily() != Family.SUCCESSFUL)
        {
            logger.error("Can't install TemplateDefinition {} in dc {}", templateDefinitionUrl,
                datacenterId);
            try
            {
                populateErrors(response, result, "installTemplateDefinitionInDatacenter");
            }
            catch (Exception e)
            {
                result.setMessage(response.getEntity(String.class));
            }

            result.setSuccess(false);
        }
        return result;
    }

    @Override
    public DataResult<OVFPackageInstanceStatus> uninstallTemplateDefinitionInDatacenter(
        final String templateDefinitionUrl, final Integer idEnterprise, final Integer datacenterId)
    {

        DataResult<OVFPackageInstanceStatus> result = new DataResult<OVFPackageInstanceStatus>();
        final Integer templateDefinitionId =
            getTemplateDefinitionIdByUrl(templateDefinitionUrl, idEnterprise);

        final String uri =
            createTemplateDefinitionUninstallLink(String.valueOf(idEnterprise), String
                .valueOf(templateDefinitionId));

        Resource resource = resource(uri, MediaType.TEXT_PLAIN);
        ClientResponse response = resource.post(String.valueOf(datacenterId));
        // TODO post use the the provided mediatype both for mediatype and accepttype
        // ClientResponse response = post(uri, String.valueOf(ovfPackageId), MediaType.TEXT_PLAIN);

        if (response.getStatusType().getFamily() != Family.SUCCESSFUL)
        {
            logger.error("Can't install TemplateDefinition {} in dc {}", templateDefinitionUrl,
                datacenterId);
            populateErrors(response, result, "uninstallTemplateDefinitionInDatacenter");
            // error cause will be shown with getOVFPackageState
        }

        OVFPackageInstanceStatus status = new OVFPackageInstanceStatus();
        status.setStatus("NOT_DOWNLOAD");
        status.setUrl(templateDefinitionUrl);

        result.setSuccess(true);
        result.setData(status);
        return result;
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

        ClientResponse response = get(uri, TemplateDefinitionListDto.MEDIA_TYPE);

        if (response.getStatusType().getFamily() == Family.SUCCESSFUL)
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
        Collection<TemplateDefinitionList> packageLists = getTemplateDefinitionLists(idEnterprise);

        for (TemplateDefinitionList list : packageLists)
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

        Collection<TemplateDefinitionList> listsDto = new ArrayList<TemplateDefinitionList>();
        try
        {
            listsDto =
                getApiClient().getAdministrationService().getEnterprise(idEnterprise)
                    .listTemplateDefinitionLists();
        }
        catch (Exception ex)
        {
            populateErrors(ex, result, "getTemplateDefinitionListNames");
            return result;
        }
        finally
        {
            releaseApiClient();
        }

        if (listsDto == null || listsDto.size() == 0)
        {
            DataResult<OVFPackageList> defaultList = addDefaultTemplateDefinitionList(idEnterprise);
            if (defaultList == null || !defaultList.getSuccess())
            {
                result.setSuccess(Boolean.FALSE);
                String message =
                    defaultList != null ? defaultList.getMessage()
                        : "Cannot add default respository : "
                            + AbiConfigManager.getInstance().getAbiConfig()
                                .getDefaultTemplateRepository();
                ;
                result.setMessage(message);

                return result;
            }
            templateDefListNames.add(defaultList.getData().getName());
        }
        for (TemplateDefinitionList list : listsDto)
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
            logger.debug("There is no default remote repository space defined");
            return null;
        }
        else
        {
            logger.debug("Adding default remote repository space at [{}]",
                defaultTemplateRepository);
            DataResult<OVFPackageList> list =
                createTemplateDefinitionListFromOVFIndexUrl(idEnterprise, defaultTemplateRepository);
            return list;

        }
    }

    // XXX not used on the AppsLibraryCommand
    private List<TemplateDefinitionList> getTemplateDefinitionLists(final Integer idEnterprise)
    {

        // String uri = createTemplateDefinitionListsLink(idEnterprise.toString());
        List<TemplateDefinitionList> templateList = null;

        try
        {
            templateList =
                getApiClient().getAdministrationService().getEnterprise(idEnterprise)
                    .listTemplateDefinitionLists();
        }
        catch (Exception ex)
        {
            populateErrors(ex, new BasicResult(), "getTemplateDefinitionLists");
        }
        finally
        {
            releaseApiClient();
        }

        return templateList;
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

            result.setSuccess(Boolean.TRUE);
            result.setData(createFlexOVFPackageListObject(list));
        }
        catch (WebApplicationException e)
        {
            result.setSuccess(Boolean.FALSE);

            Response response = e.getResponse();
            result.setMessage(response != null ? String.valueOf(response.getEntity())
                : "Request fails");
        }

        return result;
    }

    private TemplateDefinitionListDto refreshTemplateDefintionListFromRepository(
        final Integer idEnterprise, final Integer idList)
    {

        String uri = createTemplateDefinitionListLink(idEnterprise.toString(), idList.toString());
        ClientResponse response =
            put(uri, null, TemplateDefinitionListDto.MEDIA_TYPE, MediaType.TEXT_PLAIN);

        if (response.getStatusType().getFamily() != Family.SUCCESSFUL)
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
        ClientResponse response = get(uri, TemplateDefinitionsDto.MEDIA_TYPE);

        if (response.getStatusType().getFamily() != Family.SUCCESSFUL)
        {
            throw new WebApplicationException(response(response));
        }

        return response.getEntity(TemplateDefinitionsDto.class);
    }

    @Deprecated
    @Override
    public DataResult<List<String>> getIcons(final Integer idEnterprise)
    {
        DataResult<List<String>> result = new DataResult<List<String>>();

        return result;

    }

    @Override
    public DataResult<List<com.abiquo.abiserver.pojo.virtualimage.DiskFormatType>> getDiskFormatTypes()
    {

        DataResult<List<com.abiquo.abiserver.pojo.virtualimage.DiskFormatType>> result =
            new DataResult<List<com.abiquo.abiserver.pojo.virtualimage.DiskFormatType>>();

        final String uri = createDiskFormatTypesLink();

        ClientResponse response = get(uri, DiskFormatTypesDto.MEDIA_TYPE);

        if (response.getStatusType().getFamily() == Family.SUCCESSFUL)
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
        RESTLink categoryLink = getLink("category", packDto.getLinks());
        if (categoryLink != null)
        {
            pack.setCategory(categoryLink.getTitle());
        }
        else
        {
            pack.setCategory("Others");
        }
        pack.setDescription(packDto.getDescription());
        pack.setDiskFormat(String.valueOf(packDto.getDiskFormatType()));
        pack.setDiskSizeMb(packDto.getDiskFileSize());
        pack.setIconUrl(packDto.getIconUrl());

        pack.setIdOVFPackage(packDto.getId());
        pack.setName(packDto.getName());
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
        ClientResponse response = get(uri, TemplateDefinitionsDto.MEDIA_TYPE);

        if (response.getStatusType().getFamily() != Family.SUCCESSFUL)
        {
            throw new WebApplicationException(response(response));
        }

        return response.getEntity(TemplateDefinitionsDto.class);
    }

    /**
     * CATEGORY
     */

    @Override
    public DataResult<List<Category>> getCategories(final Integer idEnterprise)
    {

        DataResult<List<Category>> result = new DataResult<List<Category>>();

        String uri = createCategoriesLink();
        if (idEnterprise != null)
        {
            uri = uri + "?idEnterprise=" + String.valueOf(idEnterprise);
        }

        ClientResponse response = get(uri, CategoriesDto.MEDIA_TYPE);

        if (response.getStatusType().getFamily() == Family.SUCCESSFUL)
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
    public DataResult<Category> createCategory(final CategoryDto categoryDto,
        final Integer idEnterprise)
    {
        DataResult<Category> result = new DataResult<Category>();

        if (idEnterprise != 0)
        {
            RESTLink link = new RESTLink("enterprise", createEnterpriseLink(idEnterprise));
            link.setType(LinksDto.MEDIA_TYPE);
            categoryDto.addLink(link);
        }
        String uri = createCategoriesLink();

        ClientResponse response = post(uri, categoryDto);

        if (response.getStatusType().getFamily() == Family.SUCCESSFUL)
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

        if (response.getStatusType().getFamily() == Family.SUCCESSFUL)
        {
            result.setSuccess(Boolean.TRUE);
        }
        else
        {
            populateErrors(response, result, "deleteCategory");
        }
        return result;
    }

    private RESTLink getLink(final String rel, final List<RESTLink> links)
    {
        for (RESTLink link : links)
        {
            if (link.getRel().equalsIgnoreCase(rel))
            {
                return link;
            }
        }
        return null; // TODO check error. i guess could be null
    }
}
