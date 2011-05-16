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

package com.abiquo.server.core.infrastructure;

import java.lang.reflect.InvocationTargetException;
import java.util.List;

import javax.persistence.EntityManager;

import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import com.abiquo.server.core.cloud.Hypervisor;
import com.abiquo.server.core.cloud.HypervisorGenerator;
import com.abiquo.server.core.common.Lists;
import com.abiquo.server.core.common.persistence.DefaultJpaDataAccessTestBase;
import com.softwarementors.bzngine.engines.jpa.EntityManagerHelper;
import com.softwarementors.bzngine.engines.jpa.test.EntityManagerFactoryTestSupport;
import com.softwarementors.commons.testng.AssertEx;

public class InfrastructureRepTest extends DefaultJpaDataAccessTestBase
{

    private DatacenterGenerator eg;

    private MachineGenerator machineGenerator;

    HypervisorGenerator hypervisorGenerator;

    private DatastoreGenerator datastoreGenerator;

    private DatacenterGenerator eg()
    {
        return this.eg;
    }

    @Override
    @BeforeMethod
    public void methodSetUp()
    {
        super.methodSetUp();
        this.eg = new DatacenterGenerator(getSeed());
        this.machineGenerator = new MachineGenerator(getSeed());
        this.hypervisorGenerator = new HypervisorGenerator(getSeed());
        this.datastoreGenerator = new DatastoreGenerator(getSeed());
    }

    @Test
    public void test_findDatacenterById()
    {
        Datacenter datacenter = eg().createUniqueInstance();
        ds().persistAll(datacenter);

        InfrastructureRep rep =
            new InfrastructureRep(ds().createEntityManagerAndBeginRollbackTransaction());
        Assert.assertNotNull(rep.findById(datacenter.getId()));
    }

    @Test
    public void test_findAllDatacenters() throws IllegalAccessException, InvocationTargetException,
        NoSuchMethodException
    {
        Datacenter datacenter = eg().createInstance("axy");
        Datacenter datacenter2 = eg().createInstance("xyz");
        ds().persistAll(datacenter, datacenter2);

        InfrastructureRep rep =
            new InfrastructureRep(ds().createEntityManagerAndBeginRollbackTransaction());
        List<Datacenter> result = Lists.createSortedList(rep.findAll(), Datacenter.ORDER_BY_NAME);
        AssertEx
            .assertEqualsPropertyForListNullable(Datacenter.NAME_PROPERTY, result, "axy", "xyz");
    }

    @Test
    public void test_insert()
    {
        Datacenter datacenter = eg().createUniqueInstance();
        EntityManager em = ds().createEntityManagerAndBeginReadWriteTransaction();
        InfrastructureRep rep = new InfrastructureRep(em);
        rep.insert(datacenter);
        EntityManagerHelper.commitAndClose(em);

        Assert.assertTrue(ds().canFind(datacenter));
    }

    @Test
    public void test_insert_withDuplicatedName()
    {
        Datacenter datacenter = eg().createInstance("axy");
        ds().persistAll(datacenter);

        EntityManager em = ds().createEntityManagerAndBeginReadWriteTransaction();
        InfrastructureRep rep = new InfrastructureRep(em);
        Datacenter datacenter2 = eg().createInstance("axy");
        try
        {
            rep.insert(datacenter2);
            fail(); // /CLVER
        }
        catch (AssertionError e)
        {
            Assert.assertEquals(e.getMessage(), InfrastructureRep.BUG_INSERT_NAME_MUST_BE_UNIQUE);

        }
    }

    @Test
    public void test_update()
    {
        Datacenter datacenter = eg().createInstance("axy");
        ds().persistAll(datacenter);

        EntityManager em = ds().createEntityManagerAndBeginReadWriteTransaction();
        InfrastructureRep rep = new InfrastructureRep(em);
        Datacenter datacenterB = rep.findById(datacenter.getId());
        datacenterB.setName("new name");
        rep.update(datacenterB);
        EntityManagerHelper.commitAndClose(em);

        Datacenter datacenterC = ds().loadForRollback(datacenterB);
        Assert.assertEquals(datacenterC.getName(), "new name");
    }

    @Test
    public void test_update_withDuplicatedName()
    {
        Datacenter datacenter = eg().createInstance("axy");
        Datacenter datacenter2 = eg().createInstance("abc");
        ds().persistAll(datacenter, datacenter2);

        EntityManager em = ds().createEntityManagerAndBeginReadWriteTransaction();
        InfrastructureRep rep = new InfrastructureRep(em);
        Datacenter datacenterB = rep.findById(datacenter.getId());
        datacenterB.setName("abc");
        try
        {
            rep.update(datacenterB);
            fail();
        }
        catch (AssertionError e)
        {
            Assert.assertEquals(e.getMessage(), InfrastructureRep.BUG_UPDATE_NAME_MUST_BE_UNIQUE);
        }
    }

    @Test
    public void test_existsAnyWithName()
    {
        Datacenter datacenter = eg().createInstance("axy");
        ds().persistAll(datacenter);

        EntityManager em = ds().createEntityManagerAndBeginRollbackTransaction();
        InfrastructureRep rep = new InfrastructureRep(em);
        Assert.assertTrue(rep.existsAnyDatacenterWithName("axy"));
        Assert.assertFalse(rep.existsAnyDatacenterWithName("INEXISTENT_NAME"));
    }

    @Test
    public void test_existsAnyOtherWithName()
    {
        Datacenter datacenter = eg().createInstance("axy");
        Datacenter datacenter2 = eg().createInstance("ABC");
        ds().persistAll(datacenter, datacenter2);

        EntityManager em = ds().createEntityManagerAndBeginRollbackTransaction();
        InfrastructureRep rep = new InfrastructureRep(em);
        Datacenter datacenterB = rep.findById(datacenter.getId());
        Assert.assertFalse(rep.existsAnyOtherWithName(datacenterB, "axy"));
        Assert.assertTrue(rep.existsAnyOtherWithName(datacenterB, "ABC"));
        Assert.assertFalse(rep.existsAnyOtherWithName(datacenterB, "INEXISTENT_NAME"));
    }

    @Test
    public void test_findRacks() throws IllegalAccessException, InvocationTargetException,
        NoSuchMethodException
    {
        Datacenter datacenter = eg().createInstance("axy");
        Datacenter datacenter2 = eg().createInstance("ABC");
        Rack rack2_1 = datacenter2.createRack("rack2_1", 2, 4094, 2, 10);
        Rack rack2_2 = datacenter2.createRack("rack2_2", 2, 4094, 2, 10);
        ds().persistAll(datacenter, datacenter2, rack2_1, rack2_2);

        EntityManager em = ds().createEntityManagerAndBeginRollbackTransaction();
        InfrastructureRep rep = new InfrastructureRep(em);

        Datacenter datacenterB = rep.findById(datacenter.getId());
        Assert.assertTrue(rep.findRacks(datacenterB).isEmpty());

        Datacenter datacenter2B = rep.findById(datacenter2.getId());
        List<Rack> racks = rep.findRacks(datacenter2B);
        assertEqualsPropertyForList(Rack.NAME_PROPERTY, racks, "rack2_1", "rack2_2");
    }

    @Test
    public void test_findMachines() throws IllegalAccessException, InvocationTargetException,
        NoSuchMethodException
    {
        Datacenter datacenter = eg().createInstance("axy");
        Datacenter datacenter2 = eg().createInstance("ABC");
        Machine machine2_1 = this.machineGenerator.createMachine(datacenter2);
        machine2_1.setName("bbb");
        Machine machine2_2 = this.machineGenerator.createMachine(datacenter2);
        machine2_2.setName("aaa");
        ds().persistAll(datacenter, datacenter2, machine2_1, machine2_2);

        EntityManager em = ds().createEntityManagerAndBeginRollbackTransaction();
        InfrastructureRep rep = new InfrastructureRep(em);

        Datacenter datacenterB = rep.findById(datacenter.getId());
        Assert.assertTrue(rep.findMachines(datacenterB).isEmpty());

        Datacenter datacenter2B = rep.findById(datacenter2.getId());
        List<Machine> machines = rep.findMachines(datacenter2B);
        assertEqualsPropertyForList(Machine.NAME_PROPERTY, machines, "aaa", "bbb");
    }

    @Test
    public void test_findRackById()
    {
        InfrastructureRep rep =
            new InfrastructureRep(ds().createEntityManagerAndBeginRollbackTransaction());
        Assert.assertNull(rep.findRackById(new Integer(-5)));

        Datacenter datacenter = eg().createUniqueInstance();
        Rack rack = datacenter.createRack("a rack", 2, 4094, 2, 10);
        ds().persistAll(datacenter, rack);

        rep = new InfrastructureRep(ds().createEntityManagerAndBeginRollbackTransaction());
        Assert.assertNotNull(rep.findRackById(rack.getId()));
    }

    @Test
    public void test_findRackMachines() throws IllegalAccessException, InvocationTargetException,
        NoSuchMethodException
    {
        Datacenter datacenter = eg().createUniqueInstance();
        Rack rack1 = datacenter.createRack("a rack", 2, 4094, 2, 10);
        ds().persistAll(datacenter, rack1);

        InfrastructureRep rep =
            new InfrastructureRep(ds().createEntityManagerAndBeginRollbackTransaction());
        Rack rack1B = rep.findRackById(rack1.getId());
        Assert.assertTrue(rep.findRackMachines(rack1B).isEmpty());

        // ************************************
        Datacenter datacenter2 = eg().createUniqueInstance();
        Rack rack2_1 = datacenter2.createRack("a rack", 2, 4094, 2, 10);
        Rack rack2_2 = datacenter2.createRack("a rack 2", 2, 4094, 2, 10);
        Machine machine2_1_1 = this.machineGenerator.createMachine(datacenter2);
        machine2_1_1.setName("bbb");
        machine2_1_1.setRack(rack2_1);
        Machine machine2_1_2 = this.machineGenerator.createMachine(datacenter2);
        machine2_1_2.setName("aaa");
        machine2_1_2.setRack(rack2_1);
        ds().persistAll(datacenter2, rack2_1, rack2_2, machine2_1_1, machine2_1_2);

        InfrastructureRep repB =
            new InfrastructureRep(ds().createEntityManagerAndBeginRollbackTransaction());
        rack1B = repB.findRackById(rack1.getId());
        Rack rack2_1B = repB.findRackById(rack2_1.getId());
        Rack rack2_2B = repB.findRackById(rack2_2.getId());

        Assert.assertTrue(repB.findRackMachines(rack1B).isEmpty());
        AssertEx.assertEqualsPropertyForListNullable(Rack.NAME_PROPERTY,
            repB.findRackMachines(rack2_1B), "aaa", "bbb");
        Assert.assertTrue(repB.findRackMachines(rack2_2B).isEmpty());
    }

    @Test
    public void test_existsAnyRackWithName()
    {
        Datacenter datacenter1 = eg().createUniqueInstance();
        Datacenter datacenter2 = eg().createUniqueInstance();
        Rack rack1 = datacenter1.createRack("rack1", 2, 4094, 2, 10);
        ds().persistAll(datacenter1, datacenter2, rack1);

        InfrastructureRep rep =
            new InfrastructureRep(ds().createEntityManagerAndBeginRollbackTransaction());
        Datacenter datacenter1B = rep.findById(datacenter1.getId());
        Datacenter datacenter2B = rep.findById(datacenter2.getId());
        Assert.assertTrue(rep.existsAnyRackWithName(datacenter1B, "rack1"));
        Assert.assertFalse(rep.existsAnyRackWithName(datacenter1B, "rack INEXISTENT"));
        Assert.assertFalse(rep.existsAnyRackWithName(datacenter2B, "rack1"));
        Assert.assertFalse(rep.existsAnyRackWithName(datacenter2B, "rack INEXISTENT"));
    }

    @Test
    public void test_insertRack()
    {
        Datacenter datacenter1 = eg().createUniqueInstance();
        ds().persistAll(datacenter1);

        InfrastructureRep rep =
            new InfrastructureRep(ds().createEntityManagerAndBeginReadWriteTransaction());
        Datacenter datacenter1B = rep.findById(datacenter1.getId());
        Rack rack1 = datacenter1B.createRack("rack1", 2, 4094, 2, 10);
        rep.insertRack(rack1);

        Rack rack1B = rep.findRackById(rack1.getId());

        Assert.assertNotNull(rack1B);
    }

    @Test
    public void test_insertRack_withDuplicatedName()
    {
        Datacenter datacenter1 = eg().createUniqueInstance();
        Rack rack1 = datacenter1.createRack("sameRackName", 2, 4094, 2, 10);
        ds().persistAll(datacenter1, rack1);

        InfrastructureRep rep =
            new InfrastructureRep(ds().createEntityManagerAndBeginReadWriteTransaction());
        Datacenter datacenter1B = rep.findById(datacenter1.getId());

        Rack rack2 = datacenter1B.createRack("sameRackName", 2, 4094, 2, 10);
        try
        {
            rep.insertRack(rack2);
            fail();
        }
        catch (AssertionError ae)
        {
            Assert.assertEquals(ae.getMessage(), InfrastructureRep.BUG_INSERT_RACK_NAME_MUST_BE_UNIQUE);
        }
    }

    @Test
    public void test_existsAnyOtherRackWithName()
    {
        Datacenter datacenter1 = eg().createUniqueInstance();
        Rack rack1_1 = datacenter1.createRack("rack1", 2, 4094, 2, 10);
        Rack rack1_2 = datacenter1.createRack("rack2", 2, 4094, 2, 10);
        Datacenter datacenter2 = eg().createUniqueInstance();
        Rack rack2_1 = datacenter2.createRack("rack3", 2, 4094, 2, 10);
        ds().persistAll(datacenter1, datacenter2, rack1_1, rack1_2, rack2_1);

        InfrastructureRep rep =
            new InfrastructureRep(ds().createEntityManagerAndBeginRollbackTransaction());
        Rack rack1_1B = rep.findRackById(rack1_1.getId());
        Assert.assertFalse(rep.existsAnyOtherRackWithName(rack1_1B, "rack1"));
        Assert.assertTrue(rep.existsAnyOtherRackWithName(rack1_1B, "rack2"));
        Assert.assertFalse(rep.existsAnyOtherRackWithName(rack1_1B, "rack3"));
        Assert.assertFalse(rep.existsAnyOtherRackWithName(rack1_1B, "INEXISTENT_RACK"));
    }

    @Test
    public void test_updateRack()
    {
        Datacenter datacenter1 = eg().createUniqueInstance();
        Rack rack1_1 = datacenter1.createRack("rack1", 2, 4094, 2, 10);
        ds().persistAll(datacenter1, rack1_1);

        EntityManager em = ds().createEntityManagerAndBeginReadWriteTransaction();
        InfrastructureRep rep = new InfrastructureRep(em);
        Rack rack1_1B = rep.findRackById(rack1_1.getId());
        rack1_1B.setName("newName");
        rep.updateRack(rack1_1B);
        EntityManagerHelper.commitAndClose(em);

        Rack rack1_1C = ds().loadForRollback(rack1_1B);
        Assert.assertEquals(rack1_1C.getName(), "newName");
    }

    @Test
    public void test_updateRack_withDuplicatedName()
    {
        Datacenter datacenter1 = eg().createUniqueInstance();
        Rack rack1_1 = datacenter1.createRack("rack1", 2, 4094, 2, 10);
        Rack rack1_2 = datacenter1.createRack("rack2", 2, 4094, 2, 10);
        ds().persistAll(datacenter1, rack1_1, rack1_2);

        EntityManager em = ds().createEntityManagerAndBeginRollbackTransaction();
        InfrastructureRep rep = new InfrastructureRep(em);
        Rack rack1_1B = rep.findRackById(rack1_1.getId());
        rack1_1B.setName("rack2");
        try
        {
            rep.updateRack(rack1_1B);
            fail();
        }
        catch (AssertionError e)
        {
            Assert.assertEquals(e.getMessage(), InfrastructureRep.BUG_UPDATE_RACK_NAME_MUST_BE_UNIQUE);
        }
    }

    @Test
    public void test_existsAnyMachineWithName()
    {
        Datacenter datacenter1 = eg().createUniqueInstance();
        Machine machine1_1 = this.machineGenerator.createMachine(datacenter1, "machine1");
        Machine machine1_2 = this.machineGenerator.createMachine(datacenter1, "machine2");
        Datacenter datacenter2 = eg().createUniqueInstance();
        Machine machine2_1 = this.machineGenerator.createMachine(datacenter2, "machine3");
        ds().persistAll(datacenter1, datacenter2, machine1_1, machine1_2, machine2_1);

        InfrastructureRep rep =
            new InfrastructureRep(ds().createEntityManagerAndBeginRollbackTransaction());
        Machine machine1_1B = rep.findMachineById(machine1_1.getId());
        Assert.assertFalse(rep.existsAnyOtherMachineWithName(machine1_1B, "machine1"));
        Assert.assertTrue(rep.existsAnyOtherMachineWithName(machine1_1B, "machine2"));
        Assert.assertFalse(rep.existsAnyOtherMachineWithName(machine1_1B, "machine3"));
        Assert.assertFalse(rep.existsAnyOtherMachineWithName(machine1_1B, "INEXISTENT_RACK"));
    }

    @Test
    public void test_existsAnyOtherMachineWithName()
    {
        Datacenter datacenter1 = eg().createUniqueInstance();
        Machine machine1_1 = this.machineGenerator.createMachine(datacenter1, "machine1");
        Machine machine1_2 = this.machineGenerator.createMachine(datacenter1, "machine2");
        Datacenter datacenter2 = eg().createUniqueInstance();
        Machine machine2_1 = this.machineGenerator.createMachine(datacenter2, "machine3");
        ds().persistAll(datacenter1, datacenter2, machine1_1, machine1_2, machine2_1);

        InfrastructureRep rep =
            new InfrastructureRep(ds().createEntityManagerAndBeginRollbackTransaction());
        Machine machine1_1B = rep.findMachineById(machine1_1.getId());
        Assert.assertFalse(rep.existsAnyOtherMachineWithName(machine1_1B, "machine1"));
        Assert.assertTrue(rep.existsAnyOtherMachineWithName(machine1_1B, "machine2"));
        Assert.assertFalse(rep.existsAnyOtherMachineWithName(machine1_1B, "machine3"));
        Assert.assertFalse(rep.existsAnyOtherMachineWithName(machine1_1B, "INEXISTENT_RACK"));
    }

    @Test
    public void test_findMachineById()
    {
        Datacenter datacenter1 = eg().createUniqueInstance();
        Machine machine1_1 = this.machineGenerator.createMachine(datacenter1, "machine1");
        ds().persistAll(datacenter1, machine1_1);

        InfrastructureRep rep =
            new InfrastructureRep(ds().createEntityManagerAndBeginRollbackTransaction());
        Assert.assertNotNull(rep.findMachineById(machine1_1.getId()));
    }

    @Test
    public void test_insertMachine()
    {
        Datacenter datacenter1 = eg().createUniqueInstance();
        ds().persistAll(datacenter1);

        InfrastructureRep rep =
            new InfrastructureRep(ds().createEntityManagerAndBeginReadWriteTransaction());
        Datacenter datacenter1B = rep.findById(datacenter1.getId());
        Machine machine1 = this.machineGenerator.createMachine(datacenter1B, "machine1");
        rep.insertMachine(machine1);

        Machine machine1B = rep.findMachineById(machine1.getId());

        Assert.assertNotNull(machine1B);
    }

    @Test
    public void test_insertMachine_withDuplicatedName()
    {
        Datacenter datacenter1 = eg().createUniqueInstance();
        Machine machine1 = this.machineGenerator.createMachine(datacenter1, "sameMachineName");
        ds().persistAll(datacenter1, machine1);

        InfrastructureRep rep =
            new InfrastructureRep(ds().createEntityManagerAndBeginReadWriteTransaction());
        Datacenter datacenter1B = rep.findById(datacenter1.getId());

        Machine machine2 = this.machineGenerator.createMachine(datacenter1B, "sameMachineName");
        try
        {
            rep.insertMachine(machine2);
            fail();
        }
        catch (AssertionError ae)
        {
            Assert.assertEquals(ae.getMessage(),
                InfrastructureRep.BUG_INSERT_MACHINE_NAME_MUST_BE_UNIQUE);
        }
    }

    @Test
    public void test_updateMachine()
    {
        Datacenter datacenter1 = eg().createUniqueInstance();
        Machine machine1_1 = this.machineGenerator.createMachine(datacenter1, "machine1");
        ds().persistAll(datacenter1, machine1_1);

        EntityManager em = ds().createEntityManagerAndBeginReadWriteTransaction();
        InfrastructureRep rep = new InfrastructureRep(em);
        Machine machine1_1B = rep.findMachineById(machine1_1.getId());
        machine1_1B.setName("newName");
        rep.updateMachine(machine1_1B);
        EntityManagerHelper.commitAndClose(em);

        Machine machine1_1C = ds().loadForRollback(machine1_1B);
        Assert.assertEquals(machine1_1C.getName(), "newName");
    }

    @Test
    public void test_updateMachine_withDuplicatedName()
    {
        Datacenter datacenter1 = eg().createUniqueInstance();
        Machine machine1_1 = this.machineGenerator.createMachine(datacenter1, "machine1");
        Machine machine1_2 = this.machineGenerator.createMachine(datacenter1, "machine2");
        ds().persistAll(datacenter1, machine1_1, machine1_2);

        EntityManager em = ds().createEntityManagerAndBeginRollbackTransaction();
        InfrastructureRep rep = new InfrastructureRep(em);
        Machine machine1_1B = rep.findMachineById(machine1_1.getId());
        machine1_1B.setName("machine2");
        try
        {
            rep.updateMachine(machine1_1B);
            fail();
        }
        catch (AssertionError e)
        {
            Assert.assertEquals(e.getMessage(),
                InfrastructureRep.BUG_UPDATE_MACHINE_NAME_MUST_BE_UNIQUE);
        }
    }

    @Test
    public void createHypervisor()
    {
        Machine machine = persistMachine();

        EntityManager em = ds().createEntityManagerAndBeginReadWriteTransaction();
        InfrastructureRep rep = new InfrastructureRep(em);

        Machine machine_2 = rep.findMachineById(machine.getId());

        Hypervisor hypervisor = hypervisorGenerator.createInstance(machine_2);
        rep.insertHypervisor(hypervisor);

        EntityManagerHelper.commitAndClose(em);

        rep = new InfrastructureRep(ds().createEntityManagerAndBeginRollbackTransaction());
        Machine machine_3 = rep.findMachineById(machine.getId());
        assertNotNull(machine_3.getHypervisor());
    }

    @Test
    public void findDatastoreById()
    {
        Datastore datastore = datastoreGenerator.createUniqueInstance();
        ds().persistAll(datastore);

        EntityManager em = ds().createEntityManagerAndBeginRollbackTransaction();
        InfrastructureRep rep = new InfrastructureRep(em);

        assertNotNull(rep.findDatastoreById(datastore.getId()));
    }

    @Test
    public void createDatastoreWithMachine()
    {
        Machine machine = persistMachine();

        EntityManager em = ds().createEntityManagerAndBeginReadWriteTransaction();
        InfrastructureRep rep = new InfrastructureRep(em);

        Machine machine_2 = rep.findMachineById(machine.getId());

        Datastore datastore = new Datastore(machine_2, "dsName", "rootPath", "dsDirectory");

        rep.insertDatastore(datastore);

        EntityManagerHelper.commitAndClose(em);
        rep = new InfrastructureRep(ds().createEntityManagerAndBeginRollbackTransaction());

        Machine machine_3 = rep.findMachineById(machine.getId());
        assertSize(machine_3.getDatastores(), 1);

        Datastore datastore_2 = rep.findDatastoreById(datastore.getId());
        assertNotNull(datastore_2);
        assertSize(datastore_2.getMachines(), 1);
    }

    @Test
    public void updateDatastoreWithMachine()
    {
        Machine machine = persistMachine();
        EntityManager em = ds().createEntityManagerAndBeginReadWriteTransaction();
        InfrastructureRep rep = new InfrastructureRep(em);

        Machine machine_2 = rep.findMachineById(machine.getId());

        Datastore datastore = new Datastore(machine_2, "dsName", "rootPath", "dsDirectory");

        rep.insertDatastore(datastore);

        EntityManagerHelper.commitAndClose(em);
        em = ds().createEntityManagerAndBeginReadWriteTransaction();
        rep = new InfrastructureRep(em);

        Datastore datastore_2 = rep.findDatastoreById(datastore.getId());
        datastore_2.setName("dsName_2");

        rep.updateDatastore(datastore_2);
        EntityManagerHelper.commitAndClose(em);

        rep = new InfrastructureRep(ds().createEntityManagerAndBeginRollbackTransaction());
        Datastore datastore_3 = rep.findDatastoreById(datastore.getId());

        assertEquals(datastore_3.getName(), "dsName_2");
    }

    @Test
    public void createOrUpdateDatastoreWithDuplicatedName()
    {
        Machine machine = persistMachine();
        DuplicatedDatastoreTest test =
            new DuplicatedDatastoreTest(ds(), datastoreGenerator, machine)
            {
                @Override
                public Datastore getDuplicatedDatastoreToInsert(Machine machine)
                {
                    return new Datastore(machine, datastore.getName(), "rootPath", "dsDirectory");
                }

                @Override
                public Datastore getDuplicatedDatastoreToUpdate(Datastore duplicatedDatastore)
                {
                    duplicatedDatastore.setName(this.datastore.getName());
                    return duplicatedDatastore;
                }
            };
        test.assertInsertError();
        test.assertUpdateError();
    }

    @Test
    public void createOrUpdateDatastoreWithDuplicatedDirectory()
    {
        Machine machine = persistMachine();
        DuplicatedDatastoreTest test =
            new DuplicatedDatastoreTest(ds(), datastoreGenerator, machine)
            {
                @Override
                public Datastore getDuplicatedDatastoreToInsert(Machine machine)
                {
                    return new Datastore(machine, "dsName", "rootPath", datastore.getDirectory());
                }

                @Override
                public Datastore getDuplicatedDatastoreToUpdate(Datastore duplicatedDatastore)
                {
                    duplicatedDatastore.setDirectory(this.datastore.getDirectory());
                    return duplicatedDatastore;
                }
            };
        test.assertInsertError();
        test.assertUpdateError();
    }

    @Test
    public void test_delete()
    {
        // throw new RuntimeException("");
    }

    @Test
    public void test_deleteMachine()
    {

    }

    @Test
    public void test_deleteRack()
    {

    }

    private Machine persistMachine()
    {
        Datacenter datacenter = eg().createUniqueInstance();
        Machine machine = machineGenerator.createMachine(datacenter);
        ds().persistAll(datacenter, machine);
        return machine;
    }

    private abstract static class DuplicatedDatastoreTest
    {
        protected final EntityManagerFactoryTestSupport ds;

        protected final DatastoreGenerator datastoreGenerator;

        protected final Machine machine;

        protected Datastore datastore;

        public DuplicatedDatastoreTest(EntityManagerFactoryTestSupport ds,
            DatastoreGenerator datastoreGenerator, Machine machine)
        {
            this.ds = ds;
            this.datastoreGenerator = datastoreGenerator;
            this.machine = machine;
            this.datastore = datastoreGenerator.createInstance(machine);
            ds.persistAll(datastore);
        }

        public abstract Datastore getDuplicatedDatastoreToInsert(Machine machine);

        public abstract Datastore getDuplicatedDatastoreToUpdate(Datastore datastore);

        public void assertInsertError()
        {
            EntityManager em = ds.createEntityManagerAndBeginReadWriteTransaction();
            InfrastructureRep rep = new InfrastructureRep(em);
            Machine machine_2 = rep.findMachineById(machine.getId());

            try
            {
                rep.insertDatastore(getDuplicatedDatastoreToInsert(machine_2));
                fail();
            }
            catch (AssertionError e)
            {
                assertTrue(e.getMessage().startsWith("ASSERT - datastore duplicated"));
            }
        }

        public void assertUpdateError()
        {
            Datastore datastore_2 = datastoreGenerator.createInstance(machine);
            ds.persistAll(datastore_2);

            EntityManager em = ds.createEntityManagerAndBeginReadWriteTransaction();
            InfrastructureRep rep = new InfrastructureRep(em);
            Datastore datastore_3 = rep.findDatastoreById(datastore_2.getId());

            try
            {
                rep.updateDatastore(getDuplicatedDatastoreToUpdate(datastore_3));
                fail();
            }
            catch (AssertionError e)
            {
                assertTrue(e.getMessage().startsWith("ASSERT - datastore duplicated"));
            }
        }
    }

}
