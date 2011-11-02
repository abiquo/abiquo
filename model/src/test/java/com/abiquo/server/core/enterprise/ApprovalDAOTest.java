package com.abiquo.server.core.enterprise;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.EntityManager;

import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import com.abiquo.server.core.common.persistence.DefaultDAOTestBase;
import com.abiquo.server.core.common.persistence.TestDataAccessManager;
import com.softwarementors.bzngine.engines.jpa.test.configuration.EntityManagerFactoryForTesting;
import com.softwarementors.bzngine.entities.test.PersistentInstanceTester;

public class ApprovalDAOTest extends DefaultDAOTestBase<ApprovalDAO, Approval>
{

    @Override
    @BeforeMethod
    protected void methodSetUp()
    {
        super.methodSetUp();

        // FIXME: Remember to add all entities that have to be removed during tearDown in the
        // method:
        // com.abiquo.server.core.common.persistence.TestDataAccessManager.initializePersistentInstanceRemovalSupport
    }

    @Override
    protected ApprovalDAO createDao(final EntityManager entityManager)
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

    @Test
    public void testGetApprovals()
    {
        Approval approval1 = eg().createUniqueInstance();
        Approval approval2 = eg().createUniqueInstance();

        List<Object> entitiesToPersist = new ArrayList<Object>();
        eg().addAuxiliaryEntitiesToPersist(approval1, entitiesToPersist);
        persistAll(ds(), entitiesToPersist, approval1, approval2);

        ApprovalDAO dao = createDaoForRollbackTransaction();

        List<Approval> results = dao.findAll();

        assertSize(results, 2);
        assertAllEntityPropertiesEqual(results.iterator().next(), approval1);
        assertAllEntityPropertiesEqual(results.iterator().next(), approval2);
    }

    @Test
    public void testGetApprovalByToken()
    {
        Approval approval = eg().createUniqueInstance();

        List<Object> entitiesToPersist = new ArrayList<Object>();
        eg().addAuxiliaryEntitiesToPersist(approval, entitiesToPersist);
        persistAll(ds(), entitiesToPersist, approval);

        ApprovalDAO dao = createDaoForRollbackTransaction();

        Approval result = dao.findByToken(approval.getToken());

        assertNotNull(result);
        assertAllEntityPropertiesEqual(result, approval);
    }

    @Test
    public void testGetApprovalById()
    {
        Approval approval = eg().createUniqueInstance();

        List<Object> entitiesToPersist = new ArrayList<Object>();
        eg().addAuxiliaryEntitiesToPersist(approval, entitiesToPersist);
        persistAll(ds(), entitiesToPersist, approval);

        ApprovalDAO dao = createDaoForRollbackTransaction();

        Approval result = dao.findById(approval.getId());

        assertNotNull(result);
        assertAllEntityPropertiesEqual(result, approval);
    }

    @Test
    public void testGetApprovalByRequest()
    {
        Approval approval = eg().createUniqueInstance();

        List<Object> entitiesToPersist = new ArrayList<Object>();
        eg().addAuxiliaryEntitiesToPersist(approval, entitiesToPersist);
        persistAll(ds(), entitiesToPersist, approval);

        ApprovalDAO dao = createDaoForRollbackTransaction();

        Approval result = dao.findByRequest(approval.getRequest());

        assertNotNull(result);
        assertAllEntityPropertiesEqual(result, approval);
    }
}
