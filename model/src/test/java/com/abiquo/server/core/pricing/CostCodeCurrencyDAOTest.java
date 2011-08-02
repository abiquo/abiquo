package com.abiquo.server.core.pricing;

import javax.persistence.EntityManager;

import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import com.abiquo.server.core.common.persistence.DefaultDAOTestBase;
import com.abiquo.server.core.common.persistence.TestDataAccessManager;
import com.softwarementors.bzngine.engines.jpa.test.configuration.EntityManagerFactoryForTesting;
import com.softwarementors.bzngine.entities.test.PersistentInstanceTester;

public class CostCodeCurrencyDAOTest extends DefaultDAOTestBase<CostCodeCurrencyDAO, CostCodeCurrency>
{

    @BeforeMethod
    protected void methodSetUp()
    {
        super.methodSetUp();
        
        // FIXME: Remember to add all entities that have to be removed during tearDown in the method:
        // com.abiquo.server.core.common.persistence.TestDataAccessManager.initializePersistentInstanceRemovalSupport
    }

    @Override
    protected CostCodeCurrencyDAO createDao(EntityManager entityManager)
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

    
}
