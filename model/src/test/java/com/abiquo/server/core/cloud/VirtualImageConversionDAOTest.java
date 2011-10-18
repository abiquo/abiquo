package com.abiquo.server.core.cloud;

import javax.persistence.EntityManager;

import org.testng.annotations.BeforeMethod;

import com.abiquo.server.core.common.persistence.DefaultDAOTestBase;
import com.abiquo.server.core.common.persistence.TestDataAccessManager;
import com.softwarementors.bzngine.engines.jpa.test.configuration.EntityManagerFactoryForTesting;
import com.softwarementors.bzngine.entities.test.PersistentInstanceTester;

public class VirtualImageConversionDAOTest extends
    DefaultDAOTestBase<VirtualImageConversionDAO, VirtualImageConversion>
{

    private VirtualImageGenerator virtualImageGenerator;

    @Override
    @BeforeMethod
    protected void methodSetUp()
    {
        super.methodSetUp();
        this.virtualImageGenerator = new VirtualImageGenerator(getSeed());
    }

    @Override
    protected VirtualImageConversionDAO createDao(final EntityManager entityManager)
    {
        return new VirtualImageConversionDAO(entityManager);
    }

    @Override
    protected PersistentInstanceTester<VirtualImageConversion> createEntityInstanceGenerator()
    {
        return new VirtualImageConversionGenerator(getSeed());
    }

    @Override
    protected EntityManagerFactoryForTesting getFactory()
    {
        return TestDataAccessManager.getFactory();
    }

    @Override
    public VirtualImageConversionGenerator eg()
    {
        return (VirtualImageConversionGenerator) super.eg();
    }

}
