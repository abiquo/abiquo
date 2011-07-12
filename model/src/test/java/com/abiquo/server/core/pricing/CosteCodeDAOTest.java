package com.abiquo.server.core.pricing;

import javax.persistence.EntityManager;

import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import com.abiquo.server.core.common.persistence.DefaultDAOTestBase;
import com.abiquo.server.core.common.persistence.TestDataAccessManager;
import com.softwarementors.bzngine.engines.jpa.test.configuration.EntityManagerFactoryForTesting;
import com.softwarementors.bzngine.entities.test.PersistentInstanceTester;

public class CosteCodeDAOTest extends DefaultDAOTestBase<CosteCodeDAO, CosteCode>
{

    @BeforeMethod
    protected void methodSetUp()
    {
        super.methodSetUp();
        
        // FIXME: Remember to add all entities that have to be removed during tearDown in the method:
        // com.abiquo.server.core.common.persistence.TestDataAccessManager.initializePersistentInstanceRemovalSupport
    }

    @Override
    protected CosteCodeDAO createDao(EntityManager entityManager)
    {
        return new CosteCodeDAO(entityManager);
    }

    @Override
    protected PersistentInstanceTester<CosteCode> createEntityInstanceGenerator()
    {
        return new CosteCodeGenerator(getSeed());
    }

    @Override
    protected EntityManagerFactoryForTesting getFactory()
    {
        return TestDataAccessManager.getFactory();
    }

    @Override
    public CosteCodeGenerator eg()
    {
        return (CosteCodeGenerator) super.eg();
    }

    
}
