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
