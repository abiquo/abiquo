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

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.List;

import javax.persistence.EntityManager;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.abiquo.server.core.common.DefaultRepBase;
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

        old.setCategory(ovfPackage.getCategory());
        old.setType(ovfPackage.getType());
        old.setIcon(ovfPackage.getIcon());
        old.setProductName(ovfPackage.getProductName());
        old.setProductUrl(ovfPackage.getProductUrl());
        old.setProductVendor(ovfPackage.getProductVendor());
        old.setProductVersion(ovfPackage.getProductVersion());
        old.setUrl(ovfPackage.getUrl());
        old.setOvfPackageLists(ovfPackage.getOvfPackageLists());

        dao.persist(old);

        return old;
    }

    public void removeOVFPackage(final Integer id)
    {
        OVFPackage ovfPackage = dao.findById(id);

        // manually remove lists associated
        // As OVFPackage<->OVFPackageLists is a Many-to-many relation, the delete operation
        // must be done manually for the dependant (not owner) side in the relation: OVFPackage in
        // this case
        List<OVFPackageList> lists = ovfPackage.getOvfPackageLists();
        for (Object element : lists)
        {
            OVFPackageList ovfPackageList = (OVFPackageList) element;
            ovfPackageList.getOvfPackages().remove(ovfPackage);
            listDao.persist(ovfPackageList);
        }

        dao.persist(ovfPackage);
    }

    public Icon findByIconPathOrCreateNew(final String iconPath)
    {
        if (iconPath == null)
        {
            return null;
        }

        Icon icon;

        icon = iconDao.findByPath(iconPath);

        if (icon == null)
        {
            icon = new Icon();
            icon.setName("unname"); // TODO
            icon.setPath(iconPath);

            iconDao.persist(icon);
            iconDao.flush();
        }

        return icon;
    }

    public Category findByCategoryNameOrCreateNew(final String categoryName)
    {
        if (categoryName == null || categoryName.isEmpty())
        {
            return categoryDao.findDefault();
        }

        Category cat = categoryDao.findByName(categoryName);

        if (cat == null)
        {
            cat = new Category();
            cat.setName(categoryName);
            cat.setIsDefault(0);
            cat.setIsErasable(1);
            categoryDao.persist(cat);
        }

        return cat;
    }

}
