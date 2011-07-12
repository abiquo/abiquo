package com.abiquo.server.core.cloud;

import javax.persistence.EntityManager;

import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import com.abiquo.server.core.common.persistence.DefaultDAOTestBase;
import com.abiquo.server.core.common.persistence.TestDataAccessManager;
import com.softwarementors.bzngine.engines.jpa.test.configuration.EntityManagerFactoryForTesting;
import com.softwarementors.bzngine.entities.test.PersistentInstanceTester;

public class ChefCookbookDAOTest extends DefaultDAOTestBase<ChefCookbookDAO, ChefCookbook>
{

    @BeforeMethod
    protected void methodSetUp()
    {
        super.methodSetUp();
        
        // FIXME: Remember to add all entities that have to be removed during tearDown in the method:
        // com.abiquo.server.core.common.persistence.TestDataAccessManager.initializePersistentInstanceRemovalSupport
    }

    @Override
    protected ChefCookbookDAO createDao(EntityManager entityManager)
    {
        return new ChefCookbookDAO(entityManager);
    }

    @Override
    protected PersistentInstanceTester<ChefCookbook> createEntityInstanceGenerator()
    {
        return new ChefCookbookGenerator(getSeed());
    }

    @Override
    protected EntityManagerFactoryForTesting getFactory()
    {
        return TestDataAccessManager.getFactory();
    }

    @Override
    public ChefCookbookGenerator eg()
    {
        return (ChefCookbookGenerator) super.eg();
    }

    
}
