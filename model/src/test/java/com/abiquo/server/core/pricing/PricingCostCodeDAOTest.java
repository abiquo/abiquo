package com.abiquo.server.core.pricing;

import javax.persistence.EntityManager;

import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import com.abiquo.server.core.common.persistence.DefaultDAOTestBase;
import com.abiquo.server.core.common.persistence.TestDataAccessManager;
import com.softwarementors.bzngine.engines.jpa.test.configuration.EntityManagerFactoryForTesting;
import com.softwarementors.bzngine.entities.test.PersistentInstanceTester;

public class PricingCostCodeDAOTest extends DefaultDAOTestBase<PricingCostCodeDAO, PricingCostCode>
{

    @BeforeMethod
    protected void methodSetUp()
    {
        super.methodSetUp();
        
        // FIXME: Remember to add all entities that have to be removed during tearDown in the method:
        // com.abiquo.server.core.common.persistence.TestDataAccessManager.initializePersistentInstanceRemovalSupport
    }

    @Override
    protected PricingCostCodeDAO createDao(EntityManager entityManager)
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

    
}
