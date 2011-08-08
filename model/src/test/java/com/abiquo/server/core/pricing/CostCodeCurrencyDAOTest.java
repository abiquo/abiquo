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

public class CostCodeCurrencyDAOTest extends
    DefaultDAOTestBase<CostCodeCurrencyDAO, CostCodeCurrency>
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
    protected CostCodeCurrencyDAO createDao(final EntityManager entityManager)
    {
        return new CostCodeCurrencyDAO(entityManager);
    }

    @Override
    protected PersistentInstanceTester<CostCodeCurrency> createEntityInstanceGenerator()
    {
        return new CostCodeCurrencyGenerator(getSeed());
    }

    @Override
    protected EntityManagerFactoryForTesting getFactory()
    {
        return TestDataAccessManager.getFactory();
    }

    @Override
    public CostCodeCurrencyGenerator eg()
    {
        return (CostCodeCurrencyGenerator) super.eg();
    }

    @Test
    public void findCostCodeCurrencies()
    {
        CostCodeCurrency c1 = eg().createUniqueInstance();
        CostCodeCurrency c2 = eg().createUniqueInstance();

        ds().persistAll(c1.getCurrency(), c1.getCostCode(), c1, c2.getCurrency(), c2.getCostCode(),
            c2);

        CostCodeCurrencyDAO dao = createDaoForRollbackTransaction();

        Collection<CostCodeCurrency> ccs = dao.find(null, null, false, 0, 25, c1.getCostCode());
        AssertEx.assertSize(ccs, 1);
        ccs = dao.find(null, null, false, 0, 25, c2.getCostCode());
        AssertEx.assertSize(ccs, 1);

    }
}
