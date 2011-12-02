package com.abiquo.server.core.infrastructure.network;

import javax.persistence.EntityManager;

import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import com.abiquo.server.core.common.persistence.DefaultDAOTestBase;
import com.abiquo.server.core.common.persistence.TestDataAccessManager;
import com.softwarementors.bzngine.engines.jpa.test.configuration.EntityManagerFactoryForTesting;
import com.softwarementors.bzngine.entities.test.PersistentInstanceTester;

public class DhcpOptionDAOTest extends DefaultDAOTestBase<DhcpOptionDAO, DhcpOption>
{

    @BeforeMethod
    protected void methodSetUp()
    {
        super.methodSetUp();
        
        // FIXME: Remember to add all entities that have to be removed during tearDown in the method:
        // com.abiquo.server.core.common.persistence.TestDataAccessManager.initializePersistentInstanceRemovalSupport
    }

    @Override
    protected DhcpOptionDAO createDao(EntityManager entityManager)
    {
        return new DhcpOptionDAO(entityManager);
    }

    @Override
    protected PersistentInstanceTester<DhcpOption> createEntityInstanceGenerator()
    {
        return new DhcpOptionGenerator(getSeed());
    }

    @Override
    protected EntityManagerFactoryForTesting getFactory()
    {
        return TestDataAccessManager.getFactory();
    }

    @Override
    public DhcpOptionGenerator eg()
    {
        return (DhcpOptionGenerator) super.eg();
    }

    
}
