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
 * Boston, MA 02111-1307, USA. */

package com.abiquo.server.core.infrastructure;

import javax.persistence.EntityManager;

import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import com.abiquo.server.core.common.DefaultEntityCurrentUsed;
import com.abiquo.server.core.common.persistence.DefaultDAOTestBase;
import com.abiquo.server.core.enterprise.Enterprise;
import com.abiquo.server.core.enterprise.EnterpriseGenerator;
import com.softwarementors.bzngine.entities.test.PersistentInstanceTester;

public class DatacenterDAOTest extends DefaultDAOTestBase<DatacenterDAO, Datacenter>
{

    private RackGenerator rackGenerator;

    private EnterpriseGenerator enterpriseGenerator;

    @Override
    @BeforeMethod
    protected void methodSetUp()
    {
        super.methodSetUp();
        this.rackGenerator = new RackGenerator(getSeed());
        this.enterpriseGenerator = new EnterpriseGenerator(getSeed());
    }

    @Override
    protected DatacenterDAO createDao(final EntityManager arg0)
    {
        return new DatacenterDAO(arg0);
    }

    @Override
    protected PersistentInstanceTester<Datacenter> createEntityInstanceGenerator()
    {
        return new DatacenterGenerator(getSeed());
    }

    @Test
    public void test_datacenterDeletionDeletesRack()
    {
        Datacenter datacenter = eg().createUniqueInstance();
        Rack rack1 = this.rackGenerator.createInstance(datacenter);
        Rack rack2 = this.rackGenerator.createInstance(datacenter);

        ds().persistAll(datacenter);

        ds().remove(datacenter);

        Assert.assertFalse(ds().canFind(rack1));
        Assert.assertFalse(ds().canFind(rack2));
    }

    @Test
    public void test_existsAnyWithName()
    {
        Assert.assertFalse(createDaoForRollbackTransaction().existsAnyWithName("INEXISTENT_NAME"));

        Datacenter datacenter = createUniqueEntity();
        datacenter.setName("A name");
        ds().persistAll(datacenter);

        Assert.assertTrue(createDaoForRollbackTransaction().existsAnyWithName("A name"));
    }

    @Test
    public void test_existsAnyOtherWithName()
    {
        Datacenter datacenter = createUniqueEntity();
        datacenter.setName("A name");
        Datacenter datacenter2 = createUniqueEntity();
        datacenter2.setName("Name 2");
        ds().persistAll(datacenter, datacenter2);

        DatacenterDAO dao = createDaoForRollbackTransaction();
        datacenter = dao.findById(datacenter.getId());
        Assert.assertFalse(dao.existsAnyOtherWithName(datacenter, "INEXISTENT_NAME"));
        Assert.assertFalse(dao.existsAnyOtherWithName(datacenter, "A name"));
        Assert.assertTrue(dao.existsAnyOtherWithName(datacenter, "Name 2"));
    }

    /**
     * Should generate actual data to check its validity
     */
    @Test(enabled = false)
    public void test_getUsedResources()
    {
        Enterprise enterprise = this.enterpriseGenerator.createInstance("Enterprise Name");
        Datacenter datacenter = createUniqueEntity();

        datacenter.setName("Datacenter name");
        ds().persistAll(datacenter, enterprise);

        DatacenterDAO dao = createDaoWithNoTransaction();

        DefaultEntityCurrentUsed expectedUsed = new DefaultEntityCurrentUsed(0, 0, 0);
        DefaultEntityCurrentUsed currentUsed =
            dao.getCurrentResourcesAllocated(datacenter.getId(), enterprise.getId());
        Assert.assertEquals(currentUsed.getCpu(), expectedUsed.getCpu());
        Assert.assertEquals(currentUsed.getRamInMb(), expectedUsed.getRamInMb());
        Assert.assertEquals(currentUsed.getHdInMb(), expectedUsed.getHdInMb());
        Assert.assertEquals(currentUsed.getPublicIp(), expectedUsed.getPublicIp());
        Assert.assertEquals(currentUsed.getVlanCount(), expectedUsed.getVlanCount());

    }

}
