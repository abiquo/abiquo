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

package com.abiquo.server.core.infrastructure.storage;

import javax.persistence.EntityManager;

import org.testng.annotations.BeforeMethod;

import com.abiquo.server.core.common.persistence.DefaultDAOTestBase;
import com.abiquo.server.core.common.persistence.TestDataAccessManager;
import com.abiquo.server.core.infrastructure.DatacenterGenerator;
import com.softwarementors.bzngine.engines.jpa.test.configuration.EntityManagerFactoryForTesting;
import com.softwarementors.bzngine.entities.test.PersistentInstanceTester;

public class TierDAOTest extends DefaultDAOTestBase<TierDAO, Tier>
{

    private DatacenterGenerator datacenterGenerator;

    private TierGenerator tierGenerator;

    @Override
    @BeforeMethod
    protected void methodSetUp()
    {
        super.methodSetUp();
    }

    @Override
    protected TierDAO createDao(final EntityManager entityManager)
    {
        return new TierDAO(entityManager);
    }

    @Override
    protected PersistentInstanceTester<Tier> createEntityInstanceGenerator()
    {
        return new TierGenerator(getSeed());
    }

    @Override
    protected EntityManagerFactoryForTesting getFactory()
    {
        return TestDataAccessManager.getFactory();
    }

    @Override
    public TierGenerator eg()
    {
        return (TierGenerator) super.eg();
    }

    // @Test
    // public void testGetEnabledTiersByDatacenter()
    // {
    // datacenterGenerator = new DatacenterGenerator(getSeed());
    // tierGenerator = new TierGenerator(getSeed());
    //
    // Datacenter datacenter1 = datacenterGenerator.createUniqueInstance();
    // Tier tier1 = tierGenerator.createInstance(datacenter1, "tier1");
    // Tier tier2 = tierGenerator.createInstance(datacenter1, "tier2");
    // tier2.setEnabled(false);
    //
    // ds().persistAll(datacenter1, tier1, tier2);
    // TierDAO dao = createDaoForRollbackTransaction();
    //
    // Assert.assertEquals(dao.getEnableTiersByDatacenter(datacenter1.getId()).size(), 1);
    //
    // }

}
