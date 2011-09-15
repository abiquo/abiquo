package com.abiquo.server.core.enterprise;

import javax.persistence.EntityManager;

import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import com.abiquo.server.core.common.persistence.DefaultDAOTestBase;
import com.abiquo.server.core.common.persistence.TestDataAccessManager;
import com.softwarementors.bzngine.engines.jpa.test.configuration.EntityManagerFactoryForTesting;
import com.softwarementors.bzngine.entities.test.PersistentInstanceTester;

public class ApprovalDAOTest extends DefaultDAOTestBase<ApprovalDAO, Approval>
{

    @BeforeMethod
    protected void methodSetUp()
    {
        super.methodSetUp();
        
        // FIXME: Remember to add all entities that have to be removed during tearDown in the method:
        // com.abiquo.server.core.common.persistence.TestDataAccessManager.initializePersistentInstanceRemovalSupport
    }

    @Override
    protected ApprovalDAO createDao(EntityManager entityManager)
    {
        return new ApprovalDAO(entityManager);
    }

    @Override
    protected PersistentInstanceTester<Approval> createEntityInstanceGenerator()
    {
        return new ApprovalGenerator(getSeed());
    }

    @Override
    protected EntityManagerFactoryForTesting getFactory()
    {
        return TestDataAccessManager.getFactory();
    }

    @Override
    public ApprovalGenerator eg()
    {
        return (ApprovalGenerator) super.eg();
    }

    
}
