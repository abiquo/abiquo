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

package com.abiquo.server.core.appslibrary;

import java.util.List;

import javax.persistence.EntityManager;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.abiquo.server.core.common.DefaultRepBase;
import com.abiquo.server.core.config.Category;
import com.abiquo.server.core.config.CategoryDAO;
import com.abiquo.server.core.config.Icon;
import com.abiquo.server.core.config.IconDAO;
import com.abiquo.server.core.enterprise.Enterprise;

@Repository
public class OVFPackageRep extends DefaultRepBase
{

    @Autowired
    OVFPackageDAO dao;

    @Autowired
    OVFPackageListDAO listDao;

    @Autowired
    AppsLibraryDAO appsLibraryDao;

    @Autowired
    CategoryDAO categoryDao;

    @Autowired
    IconDAO iconDao;

    public OVFPackageRep()
    {

    }

    public OVFPackageRep(final EntityManager em)
    {
        assert entityManager != null;
        assert entityManager.isOpen();

        this.entityManager = em;

        dao = new OVFPackageDAO(em);
        listDao = new OVFPackageListDAO(em);
        appsLibraryDao = new AppsLibraryDAO(em);
        categoryDao = new CategoryDAO(em);
        iconDao = new IconDAO(em);
    }

    public List<OVFPackage> getOVFPackagesByEnterprise(final Integer idEnterprise)
    {
        return dao.findByEnterprise(idEnterprise);
    }

    public OVFPackage addOVFPackage(final OVFPackage ovfPackage, final Enterprise enterprise)
    {
        AppsLibrary appsLib = appsLibraryDao.findByEnterprise(enterprise);

        Category category = categoryDao.findById(ovfPackage.getCategory().getId());
        if (category == null)
        {
            categoryDao.persist(ovfPackage.getCategory());
        }

        ovfPackage.setAppsLibrary(appsLib);
        dao.persist(ovfPackage);
        dao.flush();

        return ovfPackage;
    }

    public OVFPackage getOVFPackage(final Integer id)
    {
        return dao.findById(id);
    }

    public OVFPackage modifyOVFPackage(final Integer ovfPackageId, final OVFPackage ovfPackage,
        final Enterprise enterprise)
    {
        OVFPackage old = dao.findById(ovfPackageId);

        // TODO - Apply changes and compare etags
        old.setName(ovfPackage.getName());
        old.setDescription(ovfPackage.getDescription());

        AppsLibrary appsLib = appsLibraryDao.findByEnterprise(enterprise);
        ovfPackage.setAppsLibrary(appsLib);
        old.setId(ovfPackageId);
        old.setCategory(ovfPackage.getCategory());
        old.setType(ovfPackage.getType());
        old.setIcon(ovfPackage.getIcon());
        old.setProductName(ovfPackage.getProductName());
        old.setProductUrl(ovfPackage.getProductUrl());
        old.setProductVendor(ovfPackage.getProductVendor());
        old.setProductVersion(ovfPackage.getProductVersion());
        old.setUrl(ovfPackage.getUrl());
        old.setOvfPackageLists(ovfPackage.getOvfPackageLists());

        update(old);

        return old;
    }

    public void update(final OVFPackage old)
    {
        dao.flush();

    }

    public void removeOVFPackage(final Integer id)
    {
        OVFPackage ovfPackage = dao.findById(id);

        // manually remove lists associated
        // As OVFPackage<->OVFPackageLists is a Many-to-many relation, the delete operation
        // must be done manually for the dependant (not owner) side in the relation: OVFPackage in
        // this case
        List<OVFPackageList> lists = ovfPackage.getOvfPackageLists();
        for (OVFPackageList ovfPackageList : lists)
        {
            ovfPackageList.getOvfPackages().remove(ovfPackage);
            listDao.flush();
        }

        dao.remove(ovfPackage);
    }

    public void removeOVFPackageList(final OVFPackageList ovfPackageList)
    {
        for (OVFPackage ovf : ovfPackageList.getOvfPackages())
        {
            if (ovf.getOvfPackageLists().size() == 1)
            {
                dao.remove(ovf);
            }
            else
            {
                ovf.getOvfPackageLists().remove(ovfPackageList);
            }
        }
        listDao.remove(ovfPackageList);

    }

    public List<OVFPackageList> getAllOVFPackageLists()
    {
        return listDao.findAll();
    }

    public OVFPackageList findOVFPackageListByNameAndEnterprise(final String name,
        final Enterprise ent)
    {

        return listDao.findByNameAndEnterprise(name, ent);
    }

    public void persistList(final OVFPackageList ovfPackageList)
    {
        listDao.persist(ovfPackageList);
    }

    public void updateList(final OVFPackageList ovfPackageList)
    {
        listDao.flush();
    }

    public OVFPackageList getOVFPackageList(final Integer id)
    {
        return listDao.findById(id);
    }

    public List<OVFPackageList> getOVFPackageListsByEnterprise(final Integer idEnterprise)
    {
        return listDao.findByEnterprise(idEnterprise);

    }
}
