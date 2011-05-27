package com.abiquo.server.core.infrastructure;

import javax.persistence.EntityManager;

import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import com.abiquo.server.core.common.persistence.DefaultDAOTestBase;
import com.abiquo.server.core.common.persistence.TestDataAccessManager;
import com.softwarementors.bzngine.engines.jpa.test.configuration.EntityManagerFactoryForTesting;
import com.softwarementors.bzngine.entities.test.PersistentInstanceTester;

public class UcsRackDAOTest extends DefaultDAOTestBase<UcsRackDAO, UcsRack>
{

    @BeforeMethod
    protected void methodSetUp()
    {
        super.methodSetUp();
        
        // FIXME: Remember to add all entities that have to be removed during tearDown in the method:
        // com.abiquo.server.core.common.persistence.TestDataAccessManager.initializePersistentInstanceRemovalSupport
    }

    @Override
    protected UcsRackDAO createDao(EntityManager entityManager)
    {
        return new UcsRackDAO(entityManager);
    }

    @Override
    protected PersistentInstanceTester<UcsRack> createEntityInstanceGenerator()
    {
        return new UcsRackGenerator(getSeed());
    }

    @Override
    protected EntityManagerFactoryForTesting getFactory()
    {
        return TestDataAccessManager.getFactory();
    }

    @Override
    public UcsRackGenerator eg()
    {
        return (UcsRackGenerator) super.eg();
    }

    
}
