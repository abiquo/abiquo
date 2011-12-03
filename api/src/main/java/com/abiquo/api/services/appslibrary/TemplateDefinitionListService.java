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
import java.util.ArrayList;
import java.util.List;

import javax.persistence.EntityManager;

import org.hibernate.Hibernate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.abiquo.api.exceptions.APIError;
import com.abiquo.api.services.EnterpriseService;
import com.abiquo.appliancemanager.client.ApplianceManagerResourceStubImpl;
import com.abiquo.appliancemanager.repositoryspace.OVFDescription;
import com.abiquo.appliancemanager.repositoryspace.RepositorySpace;
import com.abiquo.appliancemanager.transport.OVFPackageInstanceStateDto;
import com.abiquo.appliancemanager.transport.OVFPackageInstancesStateDto;
import com.abiquo.appliancemanager.transport.OVFStatusEnumType;
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
public class TemplateDefinitionListService extends DefaultApiServiceWithApplianceManagerClient
{

    private final static Logger LOGGER = LoggerFactory
        .getLogger(TemplateDefinitionListService.class);

    @Autowired
    protected AppsLibraryDAO appsLibraryDao;

    @Autowired
    protected TemplateDefinitionRep repo;

    @Autowired
    protected TemplateDefinitionService templateDefinitionService;

    public TemplateDefinitionListService()
    {
    }

    public TemplateDefinitionListService(final EntityManager em)
    {
        repo = new TemplateDefinitionRep(em);
        entService = new EnterpriseService(em);
        appsLibraryDao = new AppsLibraryDAO(em);
        templateDefinitionService = new TemplateDefinitionService(em);
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

        TemplateDefinitionList prevlist = null;
        Enterprise ent = entService.getEnterprise(idEnterprise);
        prevlist = repo.findTemplateDefinitionListByNameAndEnterprise(name, ent);

        if (prevlist != null) // TODO name unique on BBDD
        {
            addConflictErrors(APIError.TEMPLATE_DEFINITION_LIST_NAME_ALREADY_EXIST);
            flushErrors();
        }

        AppsLibrary appsLibrary = appsLibraryDao.findByEnterprise(ent);

        templateDefList.setAppsLibrary(appsLibrary);

        repo.persistTemplateDefinitionList(templateDefList);

        for (TemplateDefinition templateDefs : templateDefList.getTemplateDefinitions())
        {
            templateDefs.addToTemplateDefinitionLists(templateDefList);
            templateDefinitionService.addTemplateDefinition(templateDefs, idEnterprise);
        }

        repo.updateTemplateDefinitionList(templateDefList);

        return templateDefList;
    }

    @Transactional(readOnly = false, propagation = Propagation.REQUIRED)
    public TemplateDefinitionList addTemplateDefinitionList(final String ovfindexURL,
        final Integer idEnterprise)
    {
        // Enterprise ent = entRepo.findById(idEnterprise);
        // AppsLibrary appsLib = appsLibraryDao.findByEnterprise(ent);
        TemplateDefinitionList ovfPackageList =
            obtainTemplateDefinitionListFromOVFIndexUrl(ovfindexURL);

        return addTemplateDefinitionList(ovfPackageList, idEnterprise);
    }

    @Transactional(readOnly = false, propagation = Propagation.REQUIRED)
    public TemplateDefinitionList getTemplateDefinitionList(final Integer id)
    {
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
    public OVFPackageInstancesStateDto getTemplateListStatus(final Integer id,
        final Integer datacenterId, final Integer enterpriseId)
    {
        checkEnterpriseAndDatacenter(enterpriseId, datacenterId);

        final TemplateDefinitionList templateDefinitionList = getTemplateDefinitionList(id);
        final ApplianceManagerResourceStubImpl amClient = getApplianceManagerClient(datacenterId);
        final OVFPackageInstancesStateDto stateList = new OVFPackageInstancesStateDto();

        for (TemplateDefinition templateDef : templateDefinitionList.getTemplateDefinitions())
        {
            try
            {
                stateList.add(amClient.getTemplateStatus(String.valueOf(enterpriseId),
                    templateDef.getUrl()));
            }
            catch (Exception e)
            {
                OVFPackageInstanceStateDto error = new OVFPackageInstanceStateDto();
                error.setOvfId(templateDef.getUrl());
                error.setStatus(OVFStatusEnumType.ERROR);
                error.setErrorCause(e.toString());

                stateList.add(error);

                LOGGER.error("Can not obtain the status of the list.{}", e);
            }
        }

        return stateList;
    }

    @Transactional(readOnly = false, propagation = Propagation.REQUIRED)
    public TemplateDefinitionList updateTemplateDefinitionList(final Integer idEnterprise,
        final Integer idList)
    {
        TemplateDefinitionList oldList = repo.getTemplateDefinitionList(idList);

        if (oldList == null)
        {
            addNotFoundErrors(APIError.NON_EXISTENT_TEMPLATE_DEFINITION_LIST);
            flushErrors();
        }
        final String listUrl = oldList.getUrl();

        TemplateDefinitionList newList = obtainTemplateDefinitionListFromOVFIndexUrl(listUrl);
        updateTemplateDefinitionList(idList, newList, idEnterprise);
        return newList;
    }

    @Transactional(readOnly = true, propagation = Propagation.REQUIRED)
    public List<TemplateDefinitionList> getTemplateDefinitionListsByEnterprise(
        final Integer idEnterprise)
    {

        Enterprise ent = entService.getEnterprise(idEnterprise);
        if (ent == null)
        {
            addNotFoundErrors(APIError.NON_EXISTENT_ENTERPRISE);
            flushErrors();
        }

        List<TemplateDefinitionList> ovfPackageList = new ArrayList<TemplateDefinitionList>();
        ovfPackageList = repo.getTemplateDefinitionListsByEnterprise(idEnterprise);
        return ovfPackageList;
    }

    @Transactional(readOnly = false, propagation = Propagation.REQUIRED)
    public TemplateDefinitionList updateTemplateDefinitionList(final Integer templateDefListId,
        final TemplateDefinitionList templateDefList, final Integer idEnterprise)
    {
        TemplateDefinitionList old = repo.getTemplateDefinitionList(templateDefListId);

        if (old == null)
        {
            addNotFoundErrors(APIError.NON_EXISTENT_TEMPLATE_DEFINITION_LIST);
            flushErrors();
        }

        // TODO - Apply changes and compare etags
        old.setName(templateDefList.getName());
        old.setTemplateDefinitions(templateDefList.getTemplateDefinitions());

        Enterprise ent = entService.getEnterprise(idEnterprise);
        AppsLibrary appsLib = appsLibraryDao.findByEnterprise(ent);
        old.setAppsLibrary(appsLib);
        repo.updateTemplateDefinitionList(old);

        tracer.log(SeverityType.INFO, ComponentType.WORKLOAD,
            EventType.TEMPLATE_DEFINITION_LIST_MODIFIED, "TemplateDefinitionList "
                + templateDefList.getName() + " updated");
        return old;
    }

    @Transactional(readOnly = false, propagation = Propagation.REQUIRED)
    public void removeTemplateDefinitionList(final Integer id)
    {
        TemplateDefinitionList templateDefList = repo.getTemplateDefinitionList(id);

        if (templateDefList == null)
        {
            addNotFoundErrors(APIError.NON_EXISTENT_TEMPLATE_DEFINITION_LIST);
            flushErrors();
        }

        tracer.log(SeverityType.INFO, ComponentType.WORKLOAD,
            EventType.TEMPLATE_DEFINITION_LIST_DELETED, "Removing ovf package list "
                + templateDefList.getName());

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
