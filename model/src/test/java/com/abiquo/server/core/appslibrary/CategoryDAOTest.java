package com.abiquo.server.core.appslibrary;

import javax.persistence.EntityManager;

import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import com.abiquo.server.core.common.persistence.DefaultDAOTestBase;
import com.abiquo.server.core.common.persistence.TestDataAccessManager;
import com.softwarementors.bzngine.engines.jpa.test.configuration.EntityManagerFactoryForTesting;
import com.softwarementors.bzngine.entities.test.PersistentInstanceTester;

public class CategoryDAOTest extends DefaultDAOTestBase<CategoryDAO, Category>
{

    @BeforeMethod
    protected void methodSetUp()
    {
        super.methodSetUp();
        
        // FIXME: Remember to add all entities that have to be removed during tearDown in the method:
        // com.abiquo.server.core.common.persistence.TestDataAccessManager.initializePersistentInstanceRemovalSupport
    }

    @Override
    protected CategoryDAO createDao(EntityManager entityManager)
    {
        return new CategoryDAO(entityManager);
    }

    @Override
    protected PersistentInstanceTester<Category> createEntityInstanceGenerator()
    {
        return new CategoryGenerator(getSeed());
    }

    @Override
    protected EntityManagerFactoryForTesting getFactory()
    {
        return TestDataAccessManager.getFactory();
    }

    @Override
    public CategoryGenerator eg()
    {
        return (CategoryGenerator) super.eg();
    }

    
}
