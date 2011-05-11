package com.abiquo.server.core.cloud.stateful;

import javax.persistence.EntityManager;

import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import com.abiquo.server.core.common.persistence.DefaultDAOTestBase;
import com.abiquo.server.core.common.persistence.TestDataAccessManager;
import com.softwarementors.bzngine.engines.jpa.test.configuration.EntityManagerFactoryForTesting;
import com.softwarementors.bzngine.entities.test.PersistentInstanceTester;

public class NodeVirtualImageStatefulConversionDAOTest extends DefaultDAOTestBase<NodeVirtualImageStatefulConversionDAO, NodeVirtualImageStatefulConversion>
{

    @BeforeMethod
    protected void methodSetUp()
    {
        super.methodSetUp();
        
        // FIXME: Remember to add all entities that have to be removed during tearDown in the method:
        // com.abiquo.server.core.common.persistence.TestDataAccessManager.initializePersistentInstanceRemovalSupport
    }

    @Override
    protected NodeVirtualImageStatefulConversionDAO createDao(EntityManager entityManager)
    {
        return new NodeVirtualImageStatefulConversionDAO(entityManager);
    }

    @Override
    protected PersistentInstanceTester<NodeVirtualImageStatefulConversion> createEntityInstanceGenerator()
    {
        return new NodeVirtualImageStatefulConversionGenerator(getSeed());
    }

    @Override
    protected EntityManagerFactoryForTesting getFactory()
    {
        return TestDataAccessManager.getFactory();
    }

    @Override
    public NodeVirtualImageStatefulConversionGenerator eg()
    {
        return (NodeVirtualImageStatefulConversionGenerator) super.eg();
    }

    
}
