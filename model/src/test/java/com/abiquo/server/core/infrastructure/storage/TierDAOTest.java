package com.abiquo.server.core.infrastructure.storage;

import javax.persistence.EntityManager;

import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import com.abiquo.server.core.common.persistence.DefaultDAOTestBase;
import com.abiquo.server.core.common.persistence.TestDataAccessManager;
import com.softwarementors.bzngine.engines.jpa.test.configuration.EntityManagerFactoryForTesting;
import com.softwarementors.bzngine.entities.test.PersistentInstanceTester;

public class TierDAOTest extends DefaultDAOTestBase<TierDAO, Tier>
{

    @BeforeMethod
    protected void methodSetUp()
    {
        super.methodSetUp();
        
        // FIXME: Remember to add all entities that have to be removed during tearDown in the method:
        // com.abiquo.server.core.common.persistence.TestDataAccessManager.initializePersistentInstanceRemovalSupport
    }

    @Override
    protected TierDAO createDao(EntityManager entityManager)
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

    
}
