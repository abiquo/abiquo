package com.abiquo.server.core.enterprise;

import javax.persistence.EntityManager;

import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import com.abiquo.server.core.common.persistence.DefaultDAOTestBase;
import com.abiquo.server.core.common.persistence.TestDataAccessManager;
import com.softwarementors.bzngine.engines.jpa.test.configuration.EntityManagerFactoryForTesting;
import com.softwarementors.bzngine.entities.test.PersistentInstanceTester;

public class ApprovalManagerDAOTest extends DefaultDAOTestBase<ApprovalManagerDAO, ApprovalManager>
{

    @BeforeMethod
    protected void methodSetUp()
    {
        super.methodSetUp();
        
        // FIXME: Remember to add all entities that have to be removed during tearDown in the method:
        // com.abiquo.server.core.common.persistence.TestDataAccessManager.initializePersistentInstanceRemovalSupport
    }

    @Override
    protected ApprovalManagerDAO createDao(EntityManager entityManager)
    {
        return new ApprovalManagerDAO(entityManager);
    }

    @Override
    protected PersistentInstanceTester<ApprovalManager> createEntityInstanceGenerator()
    {
        return new ApprovalManagerGenerator(getSeed());
    }

    @Override
    protected EntityManagerFactoryForTesting getFactory()
    {
        return TestDataAccessManager.getFactory();
    }

    @Override
    public ApprovalManagerGenerator eg()
    {
        return (ApprovalManagerGenerator) super.eg();
    }

    
}
