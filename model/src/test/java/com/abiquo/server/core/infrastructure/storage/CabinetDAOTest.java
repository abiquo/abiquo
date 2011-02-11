package com.abiquo.server.core.infrastructure.storage;

import javax.persistence.EntityManager;

import org.testng.annotations.BeforeMethod;

import com.abiquo.server.core.common.persistence.DefaultDAOTestBase;
import com.abiquo.server.core.common.persistence.TestDataAccessManager;
import com.softwarementors.bzngine.engines.jpa.test.configuration.EntityManagerFactoryForTesting;
import com.softwarementors.bzngine.entities.test.PersistentInstanceTester;

public class CabinetDAOTest extends DefaultDAOTestBase<CabinetDAO, Cabinet>
{

    @BeforeMethod
    protected void methodSetUp()
    {
        super.methodSetUp();
        
        // FIXME: Remember to add all entities that have to be removed during tearDown in the method:
        // com.abiquo.server.core.common.persistence.TestDataAccessManager.initializePersistentInstanceRemovalSupport
    }

    @Override
    protected CabinetDAO createDao(EntityManager entityManager)
    {
        return new CabinetDAO(entityManager);
    }

    @Override
    protected PersistentInstanceTester<Cabinet> createEntityInstanceGenerator()
    {
        return new CabinetGenerator(getSeed());
    }

    @Override
    protected EntityManagerFactoryForTesting getFactory()
    {
        return TestDataAccessManager.getFactory();
    }

    @Override
    public CabinetGenerator eg()
    {
        return (CabinetGenerator) super.eg();
    }

    
}
