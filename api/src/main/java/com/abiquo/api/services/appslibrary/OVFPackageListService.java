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

import java.util.List;

import javax.persistence.PersistenceException;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.abiquo.api.exceptions.APIError;
import com.abiquo.api.exceptions.APIException;
import com.abiquo.api.persistence.impl.AppsLibraryDAO;
import com.abiquo.api.persistence.impl.OVFPackageListDAO;
import com.abiquo.api.services.DefaultApiService;
import com.abiquo.appliancemanager.repositoryspace.OVFDescription;
import com.abiquo.appliancemanager.repositoryspace.RepositorySpace;
import com.abiquo.ovfmanager.ovf.exceptions.XMLException;
import com.abiquo.server.core.appslibrary.AppsLibrary;
import com.abiquo.server.core.appslibrary.OVFPackage;
import com.abiquo.server.core.appslibrary.OVFPackageList;

@Service
@Transactional(readOnly = true)
public class OVFPackageListService extends DefaultApiService
{
    @Autowired
    OVFPackageListDAO dao;

    @Autowired
    AppsLibraryDAO appsLibraryDao;

    @Autowired
    OVFPackageService ovfPackageService;

    public List<OVFPackageList> getOVFPackageLists()
    {
        return dao.findAll();
    }

    @Transactional(readOnly = false, propagation = Propagation.REQUIRED)
    public OVFPackageList addOVFPackageList(OVFPackageList ovfPackageList, Integer idEnterprise)
    {
        final String name = ovfPackageList.getName();

        OVFPackageList prevlist = null;

        try
        {
            prevlist = dao.findByName(name, idEnterprise);
        }
        catch (PersistenceException e)
        {

        }

        if (prevlist != null) // TODO name unique on BBDD
        {
            addConflictErrors(APIError.OVF_PACKAGE_LIST_NAME_ALREADY_EXIST);
            flushErrors();
        }

        AppsLibrary appsLibrary = appsLibraryDao.findByEnterprise(idEnterprise);

        ovfPackageList.setAppsLibrary(appsLibrary);

        for (OVFPackage ovfPackage : ovfPackageList.getOvfPackages())
        {
            ovfPackageService.addOVFPackage(ovfPackage, idEnterprise);
        }

        dao.makePersistent(ovfPackageList);

        return ovfPackageList;
    }

    @Transactional(readOnly = false, propagation = Propagation.REQUIRED)
    public OVFPackageList addOVFPackageList(String repositorySpaceURL, Integer idEnterprise)
    {
        AppsLibrary appsLib = appsLibraryDao.findByEnterprise(idEnterprise); // TODO remove

        OVFPackageList ovfPackageList =
            obtainOVFPackageListFromRepositorySpaceLocation(repositorySpaceURL);

        return addOVFPackageList(ovfPackageList, idEnterprise);
    }

    public OVFPackageList getOVFPackageList(Integer id)
    {
        return dao.findById(id);
    }

    @Transactional(readOnly = false, propagation = Propagation.REQUIRED)
    public OVFPackageList updateOVFPackageList(final Integer idEnterprise, final Integer idList)
    {
        OVFPackageList oldList = dao.findById(idList);

        final String listUrl = oldList.getUrl();
        if (listUrl == null)
        {
            // it can not be updated
            return oldList;
        }

        dao.makeTransient(oldList);

        OVFPackageList newList = obtainOVFPackageListFromRepositorySpaceLocation(listUrl);
        return addOVFPackageList(newList, idEnterprise);
    }

    public List<OVFPackageList> getOVFPackageListsByEnterprise(Integer idEnterprise)
    {

        AppsLibrary appsLib = appsLibraryDao.findByEnterprise(idEnterprise); // TODO remove

        return dao.findByEnterprise(idEnterprise);
    }

    @Transactional(readOnly = false, propagation = Propagation.REQUIRED)
    public OVFPackageList modifyOVFPackageList(Integer ovfPackageListId,
        OVFPackageList ovfPackageList, Integer idEnterprise)
    {
        OVFPackageList old = dao.findById(ovfPackageListId);

        // TODO - Apply changes and compare etags
        old.setName(ovfPackageList.getName());
        old.setOvfPackages(ovfPackageList.getOvfPackages());

        AppsLibrary appsLib = appsLibraryDao.findByEnterprise(idEnterprise);
        old.setAppsLibrary(appsLib);

        return dao.makePersistent(old);
    }

    @Transactional(readOnly = false, propagation = Propagation.REQUIRED)
    public void removeOVFPackageList(Integer id)
    {
        OVFPackageList ovfPackageList = dao.findById(id);
        dao.makeTransient(ovfPackageList);
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

        RepositorySpace repo;

        try
        {
            repo = RepositorySpaceXML.getInstance().obtainRepositorySpace(repositorySpaceURL);
        }
        catch (XMLException e)
        {
            // TODO
            final String cause =
                String.format("Can not find the RepositorySpace at [%s]", repositorySpaceURL);
            final Response response = Response.status(Status.NOT_FOUND).entity(cause).build();
            throw new WebApplicationException(response);
        }

        final String baseRepositorySpaceURL =
            repositorySpaceURL.substring(0, repositorySpaceURL.length() - "ovfindex.xml".length());

        for (OVFDescription description : repo.getOVFDescription())
        {
            try
            {
                OVFPackage pack =
                    ovfPackageService.ovfPackageFromOvfDescription(description,
                        baseRepositorySpaceURL);

                list.getOvfPackages().add(pack);
            }
            catch (Exception e)
            {
                // TODO can not add the ovfpackage
            }
        }

        list.setName(repo.getRepositoryName());
        list.setUrl(repositorySpaceURL);

        return list;
    }
}
