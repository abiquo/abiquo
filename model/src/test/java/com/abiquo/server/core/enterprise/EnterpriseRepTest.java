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

package com.abiquo.server.core.enterprise;

import java.lang.reflect.InvocationTargetException;
import java.util.List;

import javax.persistence.EntityManager;

import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import com.abiquo.server.core.cloud.VirtualImage;
import com.abiquo.server.core.cloud.VirtualImageGenerator;
import com.abiquo.server.core.common.Limit;
import com.abiquo.server.core.common.persistence.DefaultJpaDataAccessTestBase;
import com.softwarementors.bzngine.engines.jpa.EntityManagerHelper;
import com.softwarementors.commons.testng.AssertEx;

public class EnterpriseRepTest extends DefaultJpaDataAccessTestBase
{

    private EnterpriseGenerator eg;

    private VirtualImageGenerator virtualImageGenerator;

    private EnterpriseGenerator eg()
    {
        return this.eg;
    }

    @Override
    @BeforeMethod
    public void methodSetUp()
    {
        super.methodSetUp();
        this.eg = new EnterpriseGenerator(getSeed());
        this.virtualImageGenerator = new VirtualImageGenerator(getSeed());
    }

    @Test
    public void test_findById()
    {
        Enterprise enterprise = eg().createUniqueInstance();
        ds().persistAll(enterprise);

        EnterpriseRep rep =
            new EnterpriseRep(ds().createEntityManagerAndBeginRollbackTransaction());
        Assert.assertNotNull(rep.findById(enterprise.getId()));
    }

    @Test
    public void test_findByNameAnywhere() throws IllegalAccessException, InvocationTargetException,
        NoSuchMethodException
    {
        Enterprise enterprise = eg().createInstance("axy");
        Enterprise enterprise2 = eg().createInstance("xyz");
        ds().persistAll(enterprise, enterprise2);

        EnterpriseRep rep =
            new EnterpriseRep(ds().createEntityManagerAndBeginRollbackTransaction());
        List<Enterprise> result = rep.findByNameAnywhere("xy");
        AssertEx.assertEqualsPropertyForListNullable("name", result, "axy", "xyz");
    }

    @Test
    public void test_insert()
    {
        EntityManager em = ds().createEntityManagerAndBeginReadWriteTransaction();
        EnterpriseRep rep = new EnterpriseRep(em);
        Enterprise enterprise = eg().createUniqueInstance();
        rep.insert(enterprise);
        EntityManagerHelper.commitAndClose(em);

        Assert.assertTrue(ds().canFind(enterprise));
    }

    // CODE_COVERAGE: wrong report by Emma
    @Test
    public void test_insert_checksDuplicatedName()
    {
        Enterprise enterprise = eg().createInstance("abc");
        ds().persistAll(enterprise);

        EntityManager em = ds().createEntityManagerAndBeginReadWriteTransaction();
        EnterpriseRep rep = new EnterpriseRep(em);
        try
        {
            rep.insert(eg().createInstance("abc"));
            fail(); // /CLVER
        }
        catch (AssertionError e)
        {
            Assert.assertEquals(e.getMessage(), EnterpriseRep.BUG_INSERT_NAME_MUST_BE_UNIQUE);
        }
    }

    @Test
    public void test_update()
    {
        Enterprise enterprise = eg().createUniqueInstance();
        ds().persistAll(enterprise);

        EntityManager em = ds().createEntityManagerAndBeginReadWriteTransaction();
        EnterpriseRep rep = new EnterpriseRep(em);
        Enterprise enterpriseB = rep.findById(enterprise.getId());
        enterpriseB.setCpuCountLimits(new Limit(99L, 123L));
        rep.update(enterpriseB);
        EntityManagerHelper.commitAndClose(em);

        EnterpriseRep rep2 =
            new EnterpriseRep(ds().createEntityManagerAndBeginRollbackTransaction());
        Enterprise enterpriseC = rep2.findById(enterpriseB.getId());
        Assert.assertEquals(enterpriseC.getCpuCountSoftLimit(), (Long) 99L);
        Assert.assertEquals(enterpriseC.getCpuCountHardLimit(), (Long) 123L);
    }

    // CODE_COVERAGE: wrong report by Emma
    @Test
    public void test_update_checksDuplicatedName()
    {
        Enterprise enterprise = eg().createInstance("abc");
        Enterprise enterprise2 = eg().createInstance("efg");
        ds().persistAll(enterprise, enterprise2);

        EntityManager em = ds().createEntityManagerAndBeginReadWriteTransaction();
        EnterpriseRep rep = new EnterpriseRep(em);
        Enterprise enterprise2B = rep.findById(enterprise2.getId());
        try
        {
            enterprise2B.setName("abc");
            rep.update(enterprise2B);
            fail(); // /CLVER
        }
        catch (AssertionError e)
        {
            Assert.assertEquals(e.getMessage(), EnterpriseRep.BUG_UPDATE_NAME_MUST_BE_UNIQUE);
        }
    }

    @Test
    public void test_existsAnyOtherWithName()
    {
        Enterprise enterprise = eg().createInstance("axy");
        Enterprise enterprise2 = eg().createInstance("name2");
        ds().persistAll(enterprise, enterprise2);

        EntityManager em = ds().createEntityManagerAndBeginReadWriteTransaction();
        EnterpriseRep rep = new EnterpriseRep(em);
        Enterprise enterpriseB = rep.findById(enterprise.getId());
        Assert.assertFalse(rep.existsAnyOtherWithName(enterpriseB, "INEXISTENT_NAME"));
        Assert.assertFalse(rep.existsAnyOtherWithName(enterpriseB, "axy"));
        Assert.assertTrue(rep.existsAnyOtherWithName(enterpriseB, "name2"));
    }

    @Test
    public void test_existsAnyWithName()
    {
        Enterprise enterprise = eg().createInstance("axy");
        ds().persistAll(enterprise);

        EntityManager em = ds().createEntityManagerAndBeginReadWriteTransaction();
        EnterpriseRep rep = new EnterpriseRep(em);
        Assert.assertFalse(rep.existsAnyWithName("INEXISTENT_NAME"));
        Assert.assertTrue(rep.existsAnyWithName("axy"));
        Assert.assertTrue(rep.existsAnyWithName("AXY"));
    }

    @Test
    public void deleteEnterpriseWithImagesAndConversions()
    {
        Enterprise enterprise = eg().createUniqueInstance();
        VirtualImage image = virtualImageGenerator.createImageWithConversions(enterprise);
        ds().persistAll(enterprise, image.getCategory(), image);

        int enterpriseId = enterprise.getId();

        EntityManager em = ds().createEntityManagerAndBeginReadWriteTransaction();
        EnterpriseRep rep = new EnterpriseRep(em);

        Enterprise deleted = rep.findById(enterpriseId);
        try
        {
            rep.delete(deleted);
        }
        catch (Exception e)
        {
            fail(e.getMessage());
        }
        EntityManagerHelper.commitAndClose(em);
        rep = new EnterpriseRep(ds().createEntityManagerAndBeginRollbackTransaction());
        deleted = rep.findById(enterpriseId);
        Assert.assertNull(deleted);
    }
}
