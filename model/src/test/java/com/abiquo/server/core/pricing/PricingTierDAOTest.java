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

package com.abiquo.server.core.pricing;

import java.util.Collection;

import javax.persistence.EntityManager;

import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import com.abiquo.server.core.common.persistence.DefaultDAOTestBase;
import com.abiquo.server.core.common.persistence.TestDataAccessManager;
import com.softwarementors.bzngine.engines.jpa.test.configuration.EntityManagerFactoryForTesting;
import com.softwarementors.bzngine.entities.test.PersistentInstanceTester;
import com.softwarementors.commons.testng.AssertEx;

public class PricingTierDAOTest extends DefaultDAOTestBase<PricingTierDAO, PricingTier>
{

    @Override
    @BeforeMethod
    protected void methodSetUp()
    {
        super.methodSetUp();

        // FIXME: Remember to add all entities that have to be removed during tearDown in the
        // method:
        // com.abiquo.server.core.common.persistence.TestDataAccessManager.initializePersistentInstanceRemovalSupport
    }

    @Override
    protected PricingTierDAO createDao(final EntityManager entityManager)
    {
        return new PricingTierDAO(entityManager);
    }

    @Override
    protected PersistentInstanceTester<PricingTier> createEntityInstanceGenerator()
    {
        return new PricingTierGenerator(getSeed());
    }

    @Override
    protected EntityManagerFactoryForTesting getFactory()
    {
        return TestDataAccessManager.getFactory();
    }

    @Override
    public PricingTierGenerator eg()
    {
        return (PricingTierGenerator) super.eg();
    }

    @Test
    public void findPricingTiers()
    {
        PricingTier pt1 = eg().createUniqueInstance();
        PricingTier pt2 = eg().createUniqueInstance();

        ds().persistAll(pt1.getTier(), pt1.getPricingTemplate(), pt1, pt2.getTier(),
            pt2.getPricingTemplate(), pt2);

        PricingTierDAO dao = createDaoForRollbackTransaction();

        Collection<PricingTier> ptts = dao.findPricingTiers(pt1.getPricingTemplate());
        AssertEx.assertSize(ptts, 1);
        ptts = dao.findPricingTiers(pt2.getPricingTemplate());
        AssertEx.assertSize(ptts, 1);

    }

    @Test
    public void existAnyOtherWithTier()
    {
        PricingTier pt1 = eg().createUniqueInstance();
        PricingTier pt2 = eg().createUniqueInstance();

        ds().persistAll(pt1.getTier(), pt1.getPricingTemplate(), pt1, pt2.getTier(),
            pt2.getPricingTemplate(), pt2);

        PricingTierDAO dao = createDaoForRollbackTransaction();

        assertFalse(dao.existAnyOtherWithTier(pt1, pt1.getTier(), pt1.getPricingTemplate()));

        assertFalse(dao.existAnyOtherWithTier(pt1, pt1.getTier(), pt2.getPricingTemplate()));

    }

}
