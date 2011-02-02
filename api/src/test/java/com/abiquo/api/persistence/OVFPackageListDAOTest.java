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

package com.abiquo.api.persistence;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;
import static org.testng.Assert.assertTrue;

import java.util.Collections;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.testng.annotations.Test;

import com.abiquo.api.persistence.impl.AppsLibraryDAO;
import com.abiquo.api.persistence.impl.OVFPackageListDAO;
import com.abiquo.server.core.appslibrary.AppsLibrary;
import com.abiquo.server.core.appslibrary.OVFPackage;
import com.abiquo.server.core.appslibrary.OVFPackageList;

@Test(enabled = true)
public class OVFPackageListDAOTest extends AbstractDAOTest
{
    @Autowired
    OVFPackageListDAO ovfPackageListDao;

    @Autowired
    AppsLibraryDAO appsLibraryDao;

    @Override
    protected List<String> data()
    {
        return Collections.singletonList("/data/ovfpackagelist.xml");
    }

    @Test(enabled = false)
    public void saveOVFPackageList() throws Exception
    {
        AppsLibrary appsLibrary = appsLibraryDao.findByEnterprise(1);
        OVFPackageList ovfPackageList = new OVFPackageList();

        // ovfPackageList.setOvfPackageListLists(ovfPackageListLists)
        ovfPackageList.setName("ovfPackageList_new");
        ovfPackageList.setAppsLibrary(appsLibrary);

        ovfPackageListDao.makePersistent(ovfPackageList);

        OVFPackageList retrieved = ovfPackageListDao.findById(4);

        assertEquals(retrieved.getName(), "ovfPackageList_new");
        assertEquals(String.valueOf(retrieved.getAppsLibrary().getId()), appsLibrary.getId()
            .toString());
        assertEquals(String.valueOf(retrieved.getAppsLibrary().getEnterprise().getId()), "1");
    }

    @Test(enabled = false)
    public void listOVFPackageList() throws Exception
    {
        // Tests list, add and remove from packageList
        OVFPackageList retrieved = ovfPackageListDao.findById(1);
        List<OVFPackage> packList = retrieved.getOvfPackages();

        assertNotNull(packList);
        assertTrue(packList.size() == 3);

        OVFPackage ovfPackage = new OVFPackage();

        // ovfPackage.setOvfPackageLists(ovfPackageLists)
        ovfPackage.setName("ovfPackage_new");
        ovfPackage.setDescription("description_new");
        ovfPackage.setUrl("url_new");

        packList.add(ovfPackage);
        ovfPackageListDao.makePersistent(retrieved);

        OVFPackageList retrieved2 = ovfPackageListDao.findById(1);
        List<OVFPackage> packList2 = retrieved2.getOvfPackages();

        assertNotNull(packList2);
        assertTrue(packList2.size() == 4);
    }

    @Override
    protected String compareMethod()
    {
        return "getName";
    }

    @Override
    protected JpaDAO getDao()
    {
        return ovfPackageListDao;
    }

    @Override
    protected Object getPersistentObject()
    {
        OVFPackageList ovfPackageList = new OVFPackageList();
        AppsLibrary appsLibrary = appsLibraryDao.findByEnterprise(1);

        ovfPackageList.setAppsLibrary(appsLibrary);
        ovfPackageList.setName("ovfPackageList_5");
        // ovfPackageList.setOvfPackages(ovfPackages);
        return ovfPackageList;
    }
}
