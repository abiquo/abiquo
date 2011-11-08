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
import com.abiquo.server.core.appslibrary.OVFPackage;
import com.abiquo.server.core.appslibrary.OVFPackageList;
import com.abiquo.server.core.appslibrary.OVFPackageRep;
import com.abiquo.server.core.enterprise.Enterprise;
import com.abiquo.tracer.ComponentType;
import com.abiquo.tracer.EventType;
import com.abiquo.tracer.SeverityType;

@Service
public class OVFPackageListService extends DefaultApiServiceWithApplianceManagerClient
{

    private final static Logger LOGGER = LoggerFactory.getLogger(OVFPackageListService.class);

    @Autowired
    protected AppsLibraryDAO appsLibraryDao;

    @Autowired
    protected OVFPackageRep repo;

    @Autowired
    protected OVFPackageService ovfPackageService;

    public OVFPackageListService()
    {
    }

    public OVFPackageListService(final EntityManager em)
    {
        repo = new OVFPackageRep(em);
        entService = new EnterpriseService(em);
        appsLibraryDao = new AppsLibraryDAO(em);
        ovfPackageService = new OVFPackageService(em);
    }

    @Transactional(readOnly = true, propagation = Propagation.REQUIRED)
    public List<OVFPackageList> getOVFPackageLists()
    {
        return repo.getAllOVFPackageLists();
    }

    @Transactional(readOnly = false, propagation = Propagation.REQUIRED)
    public OVFPackageList addOVFPackageList(final OVFPackageList ovfPackageList,
        final Integer idEnterprise)
    {
        final String name = ovfPackageList.getName();

        OVFPackageList prevlist = null;
        Enterprise ent = entService.getEnterprise(idEnterprise);
        prevlist = repo.findOVFPackageListByNameAndEnterprise(name, ent);

        if (prevlist != null) // TODO name unique on BBDD
        {
            addConflictErrors(APIError.OVF_PACKAGE_LIST_NAME_ALREADY_EXIST);
            flushErrors();
        }

        AppsLibrary appsLibrary = appsLibraryDao.findByEnterprise(ent);

        ovfPackageList.setAppsLibrary(appsLibrary);

        repo.persistList(ovfPackageList);

        for (OVFPackage ovfPackage : ovfPackageList.getOvfPackages())
        {
            ovfPackage.addToOvfPackageLists(ovfPackageList);
            ovfPackageService.addOVFPackage(ovfPackage, idEnterprise);
        }

        repo.updateList(ovfPackageList);

        return ovfPackageList;
    }

    @Transactional(readOnly = false, propagation = Propagation.REQUIRED)
    public OVFPackageList addOVFPackageList(final String repositorySpaceURL,
        final Integer idEnterprise)
    {
        // Enterprise ent = entRepo.findById(idEnterprise);
        // AppsLibrary appsLib = appsLibraryDao.findByEnterprise(ent);
        OVFPackageList ovfPackageList =
            obtainOVFPackageListFromRepositorySpaceLocation(repositorySpaceURL);

        return addOVFPackageList(ovfPackageList, idEnterprise);
    }

    @Transactional(readOnly = false, propagation = Propagation.REQUIRED)
    public OVFPackageList getOVFPackageList(final Integer id)
    {
        OVFPackageList ovfPackageList = repo.getOVFPackageList(id);
        if (ovfPackageList == null)
        {
            addNotFoundErrors(APIError.NON_EXISTENT_OVF_PACKAGE_LIST);
            flushErrors();
        }
        Hibernate.initialize(ovfPackageList.getOvfPackages());
        return ovfPackageList;
    }

    @Transactional(readOnly = false, propagation = Propagation.REQUIRED)
    public OVFPackageInstancesStateDto getOVFPackageListInstanceStatus(final Integer id,
        final Integer datacenterId, final Integer enterpriseId)
    {
        checkEnterpriseAndDatacenter(enterpriseId, datacenterId);

        final OVFPackageList ovfPackageList = getOVFPackageList(id);
        final ApplianceManagerResourceStubImpl amClient = getApplianceManagerClient(datacenterId);
        final OVFPackageInstancesStateDto stateList = new OVFPackageInstancesStateDto();

        for (OVFPackage ovfPack : ovfPackageList.getOvfPackages())
        {
            try
            {
                stateList.add(amClient.getCurrentOVFPackageInstanceStatus(String
                    .valueOf(enterpriseId), ovfPack.getUrl()));
            }
            catch (Exception e)
            {
                OVFPackageInstanceStateDto error = new OVFPackageInstanceStateDto();
                error.setOvfId(ovfPack.getUrl());
                error.setStatus(OVFStatusEnumType.ERROR);
                error.setErrorCause(e.toString());

                stateList.add(error);

                LOGGER.error("Can not obtain the status of the list.{}", e);
            }
        }

        return stateList;
    }

    @Transactional(readOnly = false, propagation = Propagation.REQUIRED)
    public OVFPackageList updateOVFPackageList(final Integer idEnterprise, final Integer idList)
    {
        OVFPackageList oldList = repo.getOVFPackageList(idList);

        if (oldList == null)
        {
            addNotFoundErrors(APIError.NON_EXISTENT_OVF_PACKAGE_LIST);
            flushErrors();
        }
        final String listUrl = oldList.getUrl();
        repo.updateList(oldList);

        OVFPackageList newList = obtainOVFPackageListFromRepositorySpaceLocation(listUrl);
        return addOVFPackageList(newList, idEnterprise);
    }

    @Transactional(readOnly = true, propagation = Propagation.REQUIRED)
    public List<OVFPackageList> getOVFPackageListsByEnterprise(final Integer idEnterprise)
    {

        Enterprise ent = entService.getEnterprise(idEnterprise);
        if (ent == null)
        {
            addNotFoundErrors(APIError.NON_EXISTENT_ENTERPRISE);
            flushErrors();
        }

        List<OVFPackageList> ovfPackageList = new ArrayList<OVFPackageList>();
        ovfPackageList = repo.getOVFPackageListsByEnterprise(idEnterprise);
        return ovfPackageList;
    }

    @Transactional(readOnly = false, propagation = Propagation.REQUIRED)
    public OVFPackageList modifyOVFPackageList(final Integer ovfPackageListId,
        final OVFPackageList ovfPackageList, final Integer idEnterprise)
    {
        OVFPackageList old = repo.getOVFPackageList(ovfPackageListId);

        if (old == null)
        {
            addNotFoundErrors(APIError.NON_EXISTENT_OVF_PACKAGE_LIST);
            flushErrors();
        }

        // TODO - Apply changes and compare etags
        old.setName(ovfPackageList.getName());
        old.setOvfPackages(ovfPackageList.getOvfPackages());

        Enterprise ent = entService.getEnterprise(idEnterprise);
        AppsLibrary appsLib = appsLibraryDao.findByEnterprise(ent);
        old.setAppsLibrary(appsLib);
        repo.updateList(old);

        tracer.log(SeverityType.INFO, ComponentType.WORKLOAD, EventType.OVF_PACKAGES_LIST_MODIFIED,
            "OVFPackage list " + ovfPackageList.getName() + " updated");
        return old;
    }

    @Transactional(readOnly = false, propagation = Propagation.REQUIRED)
    public void removeOVFPackageList(final Integer id)
    {
        OVFPackageList ovfPackageList = repo.getOVFPackageList(id);

        if (ovfPackageList == null)
        {
            addNotFoundErrors(APIError.NON_EXISTENT_OVF_PACKAGE_LIST);
            flushErrors();
        }

        tracer.log(SeverityType.INFO, ComponentType.WORKLOAD, EventType.OVF_PACKAGES_LIST_DELETED,
            "Removing ovf package list " + ovfPackageList.getName());

        repo.removeOVFPackageList(ovfPackageList);

    }

    private OVFPackageList obtainOVFPackageListFromRepositorySpaceLocation(String repositorySpaceURL)
    {
        if (!repositorySpaceURL.endsWith("/ovfindex.xml"))
        {
            if (repositorySpaceURL.endsWith("/"))
            {
                repositorySpaceURL += "ovfindex.xml";
            }
            else
            {
                repositorySpaceURL += "/ovfindex.xml";
            }
        }

        OVFPackageList list = new OVFPackageList();

        RepositorySpace repo = null;

        try
        {
            repo = RepositorySpaceXML.getInstance().obtainRepositorySpace(repositorySpaceURL);
        }
        catch (XMLException e)
        {
            final String cause =
                String.format("Can not find the RepositorySpace at [%s]", repositorySpaceURL);
            LOGGER.debug(cause);
            addValidationErrors(APIError.INVALID_OVF_INDEX_XML);
            flushErrors();
        }
        catch (MalformedURLException e)
        {
            final String cause =
                String.format("Invalid repository space identifier : [%s]", repositorySpaceURL);
            LOGGER.debug(cause);
            addNotFoundErrors(APIError.NON_EXISTENT_REPOSITORY_SPACE);
            flushErrors();
        }

        catch (IOException e)
        {
            final String cause =
                String.format("Can not open a connection to : [%s]", repositorySpaceURL);
            LOGGER.debug(cause);
            addNotFoundErrors(APIError.NON_EXISTENT_REPOSITORY_SPACE);
            flushErrors();
        }

        final String baseRepositorySpaceURL =
            repositorySpaceURL.substring(0, repositorySpaceURL.length() - "ovfindex.xml".length());

        for (OVFDescription description : repo.getOVFDescription())
        {

            OVFPackage pack =
                ovfPackageService.ovfPackageFromOvfDescription(description, baseRepositorySpaceURL);

            list.getOvfPackages().add(pack);

        }

        list.setName(repo.getRepositoryName());
        list.setUrl(repositorySpaceURL);

        return list;
    }

}
