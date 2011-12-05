package com.abiquo.server.core.enterprise;

import javax.persistence.EntityManager;

import org.testng.annotations.BeforeMethod;

import com.abiquo.server.core.common.persistence.DefaultDAOTestBase;
import com.abiquo.server.core.common.persistence.TestDataAccessManager;
import com.softwarementors.bzngine.engines.jpa.test.configuration.EntityManagerFactoryForTesting;
import com.softwarementors.bzngine.entities.test.PersistentInstanceTester;

public class EventDAOTest extends DefaultDAOTestBase<EventDAO, Event>
{

    @BeforeMethod
    protected void methodSetUp()
    {
        super.methodSetUp();
        
        // FIXME: Remember to add all entities that have to be removed during tearDown in the method:
        // com.abiquo.server.core.common.persistence.TestDataAccessManager.initializePersistentInstanceRemovalSupport
    }

    @Override
    protected EventDAO createDao(EntityManager entityManager)
    {
        return new EventDAO(entityManager);
    }

    @Override
    protected PersistentInstanceTester<Event> createEntityInstanceGenerator()
    {
        return new EventGenerator(getSeed());
    }

    @Override
    protected EntityManagerFactoryForTesting getFactory()
    {
        return TestDataAccessManager.getFactory();
    }

    @Override
    public EventGenerator eg()
    {
        return (EventGenerator) super.eg();
    }

    
}
