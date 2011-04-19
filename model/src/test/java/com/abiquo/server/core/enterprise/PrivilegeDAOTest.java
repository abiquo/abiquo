package com.abiquo.server.core.enterprise;

import java.util.List;

import javax.persistence.EntityManager;

import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import com.abiquo.server.core.common.persistence.DefaultDAOTestBase;
import com.abiquo.server.core.common.persistence.TestDataAccessManager;
import com.softwarementors.bzngine.engines.jpa.test.configuration.EntityManagerFactoryForTesting;
import com.softwarementors.bzngine.entities.test.PersistentInstanceTester;

public class PrivilegeDAOTest extends DefaultDAOTestBase<PrivilegeDAO, Privilege>
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
    protected PrivilegeDAO createDao(final EntityManager entityManager)
    {
        return new PrivilegeDAO(entityManager);
    }

    @Override
    protected PersistentInstanceTester<Privilege> createEntityInstanceGenerator()
    {
        return new PrivilegeGenerator(getSeed());
    }

    @Override
    protected EntityManagerFactoryForTesting getFactory()
    {
        return TestDataAccessManager.getFactory();
    }

    @Override
    public PrivilegeGenerator eg()
    {
        return (PrivilegeGenerator) super.eg();
    }

    @Test
    public void test_findAllPrivileges()
    {
        PrivilegeDAO dao = createDaoForReadWriteTransaction();
        Privilege priv = eg().createUniqueInstance();
        dao.persist(priv);
        List<Privilege> privs = dao.findAll();

        eg().assertAllPropertiesEqual(privs.iterator().next(), priv);
    }

}
