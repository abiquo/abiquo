package com.abiquo.server.core.cloud.stateful;

import javax.persistence.EntityManager;

import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import com.abiquo.server.core.common.persistence.DefaultDAOTestBase;
import com.abiquo.server.core.common.persistence.TestDataAccessManager;
import com.softwarementors.bzngine.engines.jpa.test.configuration.EntityManagerFactoryForTesting;
import com.softwarementors.bzngine.entities.test.PersistentInstanceTester;

public class DiskStatefulConversionDAOTest extends DefaultDAOTestBase<DiskStatefulConversionDAO, DiskStatefulConversion>
{

    @BeforeMethod
    protected void methodSetUp()
    {
        super.methodSetUp();
        
        // FIXME: Remember to add all entities that have to be removed during tearDown in the method:
        // com.abiquo.server.core.common.persistence.TestDataAccessManager.initializePersistentInstanceRemovalSupport
    }

    @Override
    protected DiskStatefulConversionDAO createDao(EntityManager entityManager)
    {
        return new DiskStatefulConversionDAO(entityManager);
    }

    @Override
    protected PersistentInstanceTester<DiskStatefulConversion> createEntityInstanceGenerator()
    {
        return new DiskStatefulConversionGenerator(getSeed());
    }

    @Override
    protected EntityManagerFactoryForTesting getFactory()
    {
        return TestDataAccessManager.getFactory();
    }

    @Override
    public DiskStatefulConversionGenerator eg()
    {
        return (DiskStatefulConversionGenerator) super.eg();
    }

    
}
