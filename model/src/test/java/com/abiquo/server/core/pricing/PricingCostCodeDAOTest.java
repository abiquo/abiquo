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

public class PricingCostCodeDAOTest extends DefaultDAOTestBase<PricingCostCodeDAO, PricingCostCode>
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
    protected PricingCostCodeDAO createDao(final EntityManager entityManager)
    {
        return new PricingCostCodeDAO(entityManager);
    }

    @Override
    protected PersistentInstanceTester<PricingCostCode> createEntityInstanceGenerator()
    {
        return new PricingCostCodeGenerator(getSeed());
    }

    @Override
    protected EntityManagerFactoryForTesting getFactory()
    {
        return TestDataAccessManager.getFactory();
    }

    @Override
    public PricingCostCodeGenerator eg()
    {
        return (PricingCostCodeGenerator) super.eg();
    }

    @Test
    public void findPricingCostCodes()
    {
        PricingCostCode pc1 = eg().createUniqueInstance();
        PricingCostCode pc2 = eg().createUniqueInstance();

        ds().persistAll(pc1.getCostCode(), pc1.getPricingTemplate().getCurrency(),
            pc1.getPricingTemplate(), pc1, pc2.getCostCode(),
            pc2.getPricingTemplate().getCurrency(), pc2.getPricingTemplate(), pc2);

        PricingCostCodeDAO dao = createDaoForRollbackTransaction();

        Collection<PricingCostCode> pccs = dao.findPricingCostCodes(pc1.getPricingTemplate());
        AssertEx.assertSize(pccs, 1);
        pccs = dao.findPricingCostCodes(pc2.getPricingTemplate());
        AssertEx.assertSize(pccs, 1);

    }

    @Test
    public void existAnyOtherWithCostCode()
    {
        PricingCostCode pc1 = eg().createUniqueInstance();
        PricingCostCode pc2 = eg().createUniqueInstance();

        ds().persistAll(pc1.getCostCode(), pc1.getPricingTemplate().getCurrency(),
            pc1.getPricingTemplate(), pc1, pc2.getCostCode(),
            pc2.getPricingTemplate().getCurrency(), pc2.getPricingTemplate(), pc2);

        PricingCostCodeDAO dao = createDaoForReadWriteTransaction();

        assertFalse(dao.existAnyOtherWithCostCode(pc1, pc1.getCostCode(), pc1.getPricingTemplate()));

        assertFalse(dao.existAnyOtherWithCostCode(pc1, pc1.getCostCode(), pc2.getPricingTemplate()));

    }

}
