package com.abiquo.server.core.enterprise;

import javax.persistence.EntityManager;

import org.testng.annotations.BeforeMethod;

import com.abiquo.server.core.common.persistence.DefaultDAOTestBase;
import com.abiquo.server.core.common.persistence.TestDataAccessManager;
import com.softwarementors.bzngine.engines.jpa.test.configuration.EntityManagerFactoryForTesting;
import com.softwarementors.bzngine.entities.test.PersistentInstanceTester;

public class RoleLdapDAOTest extends DefaultDAOTestBase<RoleLdapDAO, RoleLdap>
{

    @Override
    @BeforeMethod
    protected void methodSetUp()
    {
        super.methodSetUp();
    }

    @Override
    protected RoleLdapDAO createDao(final EntityManager entityManager)
    {
        return new RoleLdapDAO(entityManager);
    }

    @Override
    protected PersistentInstanceTester<RoleLdap> createEntityInstanceGenerator()
    {
        return new RoleLdapGenerator(getSeed());
    }

    @Override
    protected EntityManagerFactoryForTesting getFactory()
    {
        return TestDataAccessManager.getFactory();
    }

    @Override
    public RoleLdapGenerator eg()
    {
        return (RoleLdapGenerator) super.eg();
    }

}
