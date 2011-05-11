package com.abiquo.server.core.cloud.stateful;

import javax.persistence.EntityManager;

import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import com.abiquo.server.core.common.persistence.DefaultDAOTestBase;
import com.abiquo.server.core.common.persistence.TestDataAccessManager;
import com.softwarementors.bzngine.engines.jpa.test.configuration.EntityManagerFactoryForTesting;
import com.softwarementors.bzngine.entities.test.PersistentInstanceTester;

public class VirtualApplicanceStatefulConversionDAOTest extends DefaultDAOTestBase<VirtualApplicanceStatefulConversionDAO, VirtualApplicanceStatefulConversion>
{

    @BeforeMethod
    protected void methodSetUp()
    {
        super.methodSetUp();
        
        // FIXME: Remember to add all entities that have to be removed during tearDown in the method:
        // com.abiquo.server.core.common.persistence.TestDataAccessManager.initializePersistentInstanceRemovalSupport
    }

    @Override
    protected VirtualApplicanceStatefulConversionDAO createDao(EntityManager entityManager)
    {
        return new VirtualApplicanceStatefulConversionDAO(entityManager);
    }

    @Override
    protected PersistentInstanceTester<VirtualApplicanceStatefulConversion> createEntityInstanceGenerator()
    {
        return new VirtualApplicanceStatefulConversionGenerator(getSeed());
    }

    @Override
    protected EntityManagerFactoryForTesting getFactory()
    {
        return TestDataAccessManager.getFactory();
    }

    @Override
    public VirtualApplicanceStatefulConversionGenerator eg()
    {
        return (VirtualApplicanceStatefulConversionGenerator) super.eg();
    }

    
}
