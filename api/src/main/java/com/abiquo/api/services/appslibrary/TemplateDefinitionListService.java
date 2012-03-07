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

package com.abiquo.api.services.appslibrary;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.SocketTimeoutException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import javax.persistence.EntityManager;

import org.apache.commons.lang.StringUtils;
import org.hibernate.Hibernate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.abiquo.api.exceptions.APIError;
import com.abiquo.api.services.DefaultApiService;
import com.abiquo.api.services.EnterpriseService;
import com.abiquo.api.services.stub.AMServiceStub;
import com.abiquo.api.tracer.TracerLogger;
import com.abiquo.appliancemanager.repositoryspace.OVFDescription;
import com.abiquo.appliancemanager.repositoryspace.RepositorySpace;
import com.abiquo.appliancemanager.transport.TemplatesStateDto;
import com.abiquo.model.transport.error.CommonError;
import com.abiquo.ovfmanager.ovf.exceptions.XMLException;
import com.abiquo.server.core.appslibrary.AppsLibrary;
import com.abiquo.server.core.appslibrary.AppsLibraryDAO;
import com.abiquo.server.core.appslibrary.TemplateDefinition;
import com.abiquo.server.core.appslibrary.TemplateDefinitionList;
import com.abiquo.server.core.appslibrary.TemplateDefinitionRep;
import com.abiquo.server.core.enterprise.Enterprise;
import com.abiquo.tracer.ComponentType;
import com.abiquo.tracer.EventType;
import com.abiquo.tracer.SeverityType;

@Service
public class TemplateDefinitionListService extends DefaultApiService
{

    private final static Logger LOGGER = LoggerFactory
        .getLogger(TemplateDefinitionListService.class);

    @Autowired
    protected AppsLibraryDAO appsLibraryDao;

    @Autowired
    protected TemplateDefinitionRep repo;

    @Autowired
    protected TemplateDefinitionService templateDefinitionService;

    @Autowired
    private EnterpriseService enterpriseService;

    @Autowired
    private AMServiceStub amService;

    @Autowired
    protected TracerLogger tracer;

    public TemplateDefinitionListService()
    {
    }

    public TemplateDefinitionListService(final EntityManager em)
    {
        repo = new TemplateDefinitionRep(em);
        enterpriseService = new EnterpriseService(em);
        appsLibraryDao = new AppsLibraryDAO(em);
        templateDefinitionService = new TemplateDefinitionService(em);
        tracer = new TracerLogger();
        amService = new AMServiceStub();
    }

    @Transactional(readOnly = true, propagation = Propagation.REQUIRED)
    public List<TemplateDefinitionList> getOVFPackageLists()
    {
        return repo.getTemplateDefinitionLists();
    }

    @Transactional(readOnly = false, propagation = Propagation.REQUIRED)
    public TemplateDefinitionList addTemplateDefinitionList(
        final TemplateDefinitionList templateDefList, final Integer idEnterprise)
    {
        final String name = templateDefList.getName();

        Enterprise ent = enterpriseService.getEnterprise(idEnterprise);
        AppsLibrary appsLib = appsLibraryDao.findByEnterpriseOrInitialize(ent);

        templateDefList.setAppsLibrary(appsLib);

        validate(templateDefList);

        for (TemplateDefinition templateDef : templateDefList.getTemplateDefinitions())
        {
            templateDef.setAppsLibrary(appsLib);
            // validate(templateDef);
        }

        TemplateDefinitionList prevlist = null;
        prevlist = repo.findTemplateDefinitionListByNameAndEnterprise(name, ent);

        if (prevlist != null)
        {
            addConflictErrors(APIError.TEMPLATE_DEFINITION_LIST_NAME_ALREADY_EXIST);
            flushErrors();
        }

        repo.persistTemplateDefinitionList(templateDefList);

        for (TemplateDefinition templateDef : templateDefList.getTemplateDefinitions())
        {
            if (templateDef.isValid())
            {// TemplateDefinition tDef =
                templateDefinitionService.addTemplateDefinition(templateDef, idEnterprise);

                templateDef.addToTemplateDefinitionLists(templateDefList);
            }
            else
            {
                templateDef.getTemplateDefinitionLists().clear();

                if (tracer != null)
                {
                    tracer.log(SeverityType.WARNING, ComponentType.APPLIANCE_MANAGER,
                        EventType.TEMPLATE_DEFINITION_LIST_MODIFIED,
                        "templateDefinition.createError", templateDef.getName(),
                        validationErrors(templateDef.getValidationErrors()));
                }
            }
        }

        // templateDefList.setTemplateDefinitions(correctTemplates);
        repo.updateTemplateDefinitionList(templateDefList);

        return templateDefList;
    }

    private String validationErrors(final Set<CommonError> errors)
    {
        StringBuilder sbuilder = new StringBuilder();
        for (CommonError error : errors)
        {
            sbuilder.append(String.format("%s - %s\n", error.getCode(), error.getMessage()));
        }

        return sbuilder.toString();

    }

    @Transactional(readOnly = false, propagation = Propagation.REQUIRED)
    public TemplateDefinitionList addTemplateDefinitionList(final String ovfindexURL,
        final Integer idEnterprise)
    {
        TemplateDefinitionList ovfPackageList =
            obtainTemplateDefinitionListFromOVFIndexUrl(ovfindexURL);

        return addTemplateDefinitionList(ovfPackageList, idEnterprise);
    }

    @Transactional(readOnly = false, propagation = Propagation.REQUIRED)
    public TemplateDefinitionList getTemplateDefinitionList(final Integer id,
        final Integer idEnterprise)
    {
        enterpriseService.getEnterprise(idEnterprise); // check can view

        TemplateDefinitionList templateDefinitionList = repo.getTemplateDefinitionList(id);
        if (templateDefinitionList == null)
        {
            addNotFoundErrors(APIError.NON_EXISTENT_TEMPLATE_DEFINITION_LIST);
            flushErrors();
        }
        Hibernate.initialize(templateDefinitionList.getTemplateDefinitions());
        return templateDefinitionList;
    }

    @Transactional(readOnly = false, propagation = Propagation.REQUIRED)
    public TemplatesStateDto getTemplateListStatus(final Integer id, final Integer datacenterId,
        final Integer enterpriseId)
    {
        final TemplateDefinitionList templateDefinitionList =
            getTemplateDefinitionList(id, enterpriseId);

        return amService.getTemplatesState(datacenterId, enterpriseId,
            getListIds(templateDefinitionList));
    }

    private String[] getListIds(final TemplateDefinitionList list)
    {
        ArrayList<String> ids = new ArrayList<String>();
        for (TemplateDefinition tdef : list.getTemplateDefinitions())
        {
            ids.add(tdef.getUrl());
        }
        return ids.toArray(new String[ids.size()]);
    }

    @Transactional(readOnly = false, propagation = Propagation.REQUIRED)
    public TemplateDefinitionList refreshTemplateDefinitionList(final Integer idEnterprise,
        final Integer idList)
    {
        enterpriseService.getEnterprise(idEnterprise); // check can view

        TemplateDefinitionList oldList = repo.getTemplateDefinitionList(idList);

        if (oldList == null)
        {
            addNotFoundErrors(APIError.NON_EXISTENT_TEMPLATE_DEFINITION_LIST);
            flushErrors();
        }

        final String listUrl = oldList.getUrl();
        if (StringUtils.isEmpty(listUrl))
        {
            addConflictErrors(APIError.TEMPLATE_DEFINITION_LIST_REFRESH_NO_URL);
            flushErrors();
        }

        TemplateDefinitionList newList = obtainTemplateDefinitionListFromOVFIndexUrl(listUrl);
        removeTemplateDefinitionList(idList, true, idEnterprise);
        return addTemplateDefinitionList(newList, idEnterprise);
    }

    @Transactional(readOnly = true, propagation = Propagation.REQUIRED)
    public List<TemplateDefinitionList> getTemplateDefinitionListsByEnterprise(
        final Integer idEnterprise) throws SocketTimeoutException
    {
        enterpriseService.getEnterprise(idEnterprise);

        List<TemplateDefinitionList> ovfPackageList = new ArrayList<TemplateDefinitionList>();
        ovfPackageList = repo.getTemplateDefinitionListsByEnterprise(idEnterprise);
        return ovfPackageList;
    }

    @Transactional(readOnly = false, propagation = Propagation.REQUIRED)
    public TemplateDefinitionList updateTemplateDefinitionList(final Integer templateDefListId,
        final TemplateDefinitionList templateDefList, final Integer idEnterprise)
    {
        Enterprise ent = enterpriseService.getEnterprise(idEnterprise);
        TemplateDefinitionList old = repo.getTemplateDefinitionList(templateDefListId);

        if (old == null)
        {
            addNotFoundErrors(APIError.NON_EXISTENT_TEMPLATE_DEFINITION_LIST);
            flushErrors();
        }

        old.setName(templateDefList.getName());
        old.setTemplateDefinitions(templateDefList.getTemplateDefinitions());

        AppsLibrary appsLib = appsLibraryDao.findByEnterprise(ent);
        old.setAppsLibrary(appsLib);

        validate(old);

        for (TemplateDefinition templateDef : old.getTemplateDefinitions())
        {
            validate(templateDef);
        }

        repo.updateTemplateDefinitionList(old);

        tracer.log(SeverityType.INFO, ComponentType.WORKLOAD,
            EventType.TEMPLATE_DEFINITION_LIST_MODIFIED, "templateDefinitionList.updated",
            templateDefList.getName());
        return old;
    }

    @Transactional(readOnly = false, propagation = Propagation.REQUIRED)
    public void removeTemplateDefinitionList(final Integer id, final boolean refresh,
        final Integer idEnterprise)
    {
        enterpriseService.getEnterprise(idEnterprise);

        TemplateDefinitionList templateDefList = repo.getTemplateDefinitionList(id);

        if (templateDefList == null)
        {
            addNotFoundErrors(APIError.NON_EXISTENT_TEMPLATE_DEFINITION_LIST);
            flushErrors();
        }

        if (!refresh)
        {
            tracer.log(SeverityType.INFO, ComponentType.WORKLOAD,
                EventType.TEMPLATE_DEFINITION_LIST_DELETED, "templateDefinitionList.deleted",
                templateDefList.getName());
        }
        repo.removeTemplateDefinitionList(templateDefList);
    }

    private TemplateDefinitionList obtainTemplateDefinitionListFromOVFIndexUrl(String ovfindexUrl)
    {

        if (!ovfindexUrl.endsWith("/ovfindex.xml"))
        {
            if (ovfindexUrl.endsWith("/"))
            {
                ovfindexUrl += "ovfindex.xml";
            }
            else
            {
                String suffix = ovfindexUrl.endsWith(".xml") ? "" : "/ovfindex.xml";
                ovfindexUrl += suffix;
            }
        }

        TemplateDefinitionList list = new TemplateDefinitionList();

        RepositorySpace repo = null;

        try
        {
            RepositorySpaceXML ovfindexXML = RepositorySpaceXML.getInstance();
            repo = ovfindexXML.obtainRepositorySpace(ovfindexUrl);
        }
        catch (XMLException e)
        {
            final String cause =
                String.format("Can not find the RepositorySpace at [%s]", ovfindexUrl);
            LOGGER.debug(cause);
            addValidationErrors(APIError.INVALID_OVF_INDEX_XML);
            flushErrors();
        }
        catch (MalformedURLException e)
        {
            final String cause =
                String.format("Invalid repository space identifier : [%s]", ovfindexUrl);
            LOGGER.debug(cause);
            addNotFoundErrors(APIError.NON_EXISTENT_REPOSITORY_SPACE);
            flushErrors();
        }
        catch (IOException e)
        {
            final String cause = String.format("Can not open a connection to : [%s]", ovfindexUrl);
            LOGGER.debug(cause);
            addNotFoundErrors(APIError.NON_EXISTENT_REPOSITORY_SPACE);
            flushErrors();
        }

        String baseRepositorySpaceURL = "";
        if (ovfindexUrl.lastIndexOf('/') != -1)
        {
            baseRepositorySpaceURL = ovfindexUrl.substring(0, ovfindexUrl.lastIndexOf('/'));
        }
        for (OVFDescription description : repo.getOVFDescription())
        {

            TemplateDefinition pack =
                templateDefinitionService.transformToTemplateDefinition(description,
                    baseRepositorySpaceURL);

            list.getTemplateDefinitions().add(pack);

        }

        list.setName(repo.getRepositoryName());
        list.setUrl(ovfindexUrl);

        return list;
    }

}
