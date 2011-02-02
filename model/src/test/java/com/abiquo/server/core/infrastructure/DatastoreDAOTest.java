/**
 * Abiquo community edition
 * cloud management application for hybrid clouds
 * Copyright (C) 2008-2010 - Abiquo Holdings S.L.
 *
 * This application is free software; you can redistribute it and/or
 * modify it under the terms of the GNU LESSER GENERAL PUBLIC
 * LICENSE as published by the Free Software Foundation under
 * version 3 of the License
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * LESSER GENERAL PUBLIC LICENSE v.3 for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the
 * Free Software Foundation, Inc., 59 Temple Place - Suite 330,
 * Boston, MA 02111-1307, USA.
 */

/**
 * abiCloud  community version
 * cloud management application for hybrid clouds
 * Copyright (C) 2008-2010 - Soluciones Grid SL
 *
 * This application is free software; you can redistribute it and/or
 * modify it under the terms of the GNU LESSER GENERAL PUBLIC
 * LICENSE as published by the Free Software Foundation under
 * version 3 of the License
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * LESSER GENERAL PUBLIC LICENSE v.3 for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the
 * Free Software Foundation, Inc., 59 Temple Place - Suite 330,
 * Boston, MA 02111-1307, USA.
 */

package com.abiquo.server.core.infrastructure;

import java.util.List;

import javax.persistence.EntityManager;

import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import com.abiquo.server.core.common.persistence.DefaultDAOTestBase;
import com.softwarementors.bzngine.entities.test.PersistentInstanceTester;
import com.softwarementors.commons.testng.AssertEx;

public class DatastoreDAOTest extends DefaultDAOTestBase<DatastoreDAO, Datastore>
{

    @BeforeMethod
    protected void methodSetUp()
    {
        super.methodSetUp();
    }

    @Override
    protected DatastoreDAO createDao(EntityManager entityManager)
    {
        return new DatastoreDAO(entityManager);
    }

    @Override
    protected PersistentInstanceTester<Datastore> createEntityInstanceGenerator()
    {
        return new DatastoreGenerator(getSeed());
    }

    @Override
    public DatastoreGenerator eg()
    {
        return (DatastoreGenerator) super.eg();
    }

    @Test
    public void test_existsAnyWithName()
    {
        Assert.assertFalse(createDaoForRollbackTransaction().existsAnyWithName(
            "INEXISTENT_PROPERTY"));

        Datastore entity = createUniqueEntity();
        entity.setName("A property");
        ds().persistAll(entity);

        Assert.assertTrue(createDaoForRollbackTransaction().existsAnyWithName("A property"));
    }

    @Test
    public void test_existsAnyOtherWithName()
    {
        Datastore entity = createUniqueEntity();
        entity.setName("A property");
        Datastore entity2 = createUniqueEntity();
        entity2.setName("Property 2");
        ds().persistAll(entity, entity2);

        DatastoreDAO dao = createDaoForRollbackTransaction();
        entity = dao.findById(entity.getId());
        Assert.assertFalse(dao.existsAnyOtherWithName(entity, "INEXISTENT_PROPERTY"));
        Assert.assertFalse(dao.existsAnyOtherWithName(entity, "A property"));
        Assert.assertTrue(dao.existsAnyOtherWithName(entity, "Property 2"));
    }

    @Test
    public void test_existsAnyWithDirectory()
    {
        Assert.assertFalse(createDaoForRollbackTransaction().existsAnyWithDirectory(
            "INEXISTENT_PROPERTY"));

        Datastore entity = createUniqueEntity();
        entity.setDirectory("A property");
        ds().persistAll(entity);

        Assert.assertTrue(createDaoForRollbackTransaction().existsAnyWithDirectory("A property"));
    }

    @Test
    public void test_existsAnyOtherWithDirectory()
    {
        Datastore entity = createUniqueEntity();
        entity.setDirectory("A property");
        Datastore entity2 = createUniqueEntity();
        entity2.setDirectory("Property 2");
        ds().persistAll(entity, entity2);

        DatastoreDAO dao = createDaoForRollbackTransaction();
        entity = dao.findById(entity.getId());
        Assert.assertFalse(dao.existsAnyOtherWithDirectory(entity, "INEXISTENT_PROPERTY"));
        Assert.assertFalse(dao.existsAnyOtherWithDirectory(entity, "A property"));
        Assert.assertTrue(dao.existsAnyOtherWithDirectory(entity, "Property 2"));
    }

    @Test
    public void findMachineDatastores()
    {
        DatacenterGenerator datacenterGenerator = new DatacenterGenerator(getSeed());
        Datacenter datacenter = datacenterGenerator.createUniqueInstance();

        MachineGenerator machineGenerator = new MachineGenerator(getSeed());
        Machine machine = machineGenerator.createMachine(datacenter);

        Datastore entity = eg().createInstance(machine);
        ds().persistAll(datacenter, machine, entity);

        EntityManager em = ds().createEntityManagerAndBeginRollbackTransaction();
        DatastoreDAO dao = createDao(em);
        MachineDAO machineDao = new MachineDAO(em);

        Machine machine_2 = machineDao.findById(machine.getId());

        List<Datastore> datastores = dao.findMachineDatastores(machine_2);
        AssertEx.assertSize(datastores, 1);
    }

    @Override
    public void test_isManaged()
    {
        // FIXME: This is one of the default tests that don't work because they don't take into
        // account NM relationships
    }

    @Override
    public void test_isManaged_preconditionEntityManagerMustBeOpen()
    {
        // FIXME: This is one of the default tests that don't work because they don't take into
        // account NM relationships
    }

    @Override
    public void test_remove()
    {
        // FIXME: This is one of the default tests that don't work because they don't take into
        // account NM relationships
    }
}
