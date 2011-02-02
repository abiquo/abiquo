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
import static org.testng.Assert.assertNull;

import java.util.Collections;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.testng.annotations.Test;

import com.abiquo.api.persistence.impl.OVFPackageDAO;
import com.abiquo.server.core.appslibrary.OVFPackage;

@Test(enabled = true)
public class OVFPackageDAOTest extends AbstractDAOTest
{
    @Autowired
    OVFPackageDAO ovfPackageDao;

    @Override
    protected List<String> data()
    {
        // return Collections.singletonList("/data/ovfpackage.xml");
        return Collections.singletonList("/data/ovfpackagelist.xml");
    }

    @Test(enabled = false)
    public void saveOVFPackage() throws Exception
    {
        OVFPackage ovfPackage = new OVFPackage();

        // ovfPackage.setOvfPackageLists(ovfPackageLists)
        ovfPackage.setName("ovfPackage_new");
        ovfPackage.setDescription("description_new");
        ovfPackage.setUrl("url_new");

        ovfPackageDao.makePersistent(ovfPackage);

        OVFPackage retrieved = ovfPackageDao.findById(4);

        // assertEquals(1, datacenterDao.findById(1).getId().intValue());
        assertEquals(retrieved.getName(), "ovfPackage_new");
        assertEquals(retrieved.getDescription(), "description_new");
        assertEquals(retrieved.getUrl(), "url_new");
    }

    @Test(enabled = false)
    public void deleteOVFPackage() throws Exception
    {
        OVFPackage pack = ovfPackageDao.findById(1);
        assertNotNull(pack);

        ovfPackageDao.makeTransient(pack);

        OVFPackage deleted = ovfPackageDao.findById(1);

        assertNull(deleted);
    }

    @Override
    protected String compareMethod()
    {
        return "getName";
    }

    @Override
    protected JpaDAO getDao()
    {
        return ovfPackageDao;
    }

    @Override
    protected Object getPersistentObject()
    {
        OVFPackage ovfPackage = new OVFPackage();

        // ovfPackage.setDatacenter(datacenterDao.findById(1));
        ovfPackage.setName("ovfPackage_5");
        ovfPackage.setDescription("description_5");
        ovfPackage.setUrl("large_description_5");
        return ovfPackage;
    }
}
