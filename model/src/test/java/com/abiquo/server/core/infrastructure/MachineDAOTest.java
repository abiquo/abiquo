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
 * Boston, MA 02111-1307, USA. */

package com.abiquo.server.core.infrastructure;

import java.lang.reflect.InvocationTargetException;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.OptimisticLockException;

import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import com.abiquo.server.core.common.persistence.DefaultDAOTestBase;
import com.abiquo.server.core.infrastructure.Machine.State;
import com.softwarementors.bzngine.engines.jpa.EntityManagerHelper;
import com.softwarementors.bzngine.entities.test.PersistentInstanceTester;

public class MachineDAOTest extends DefaultDAOTestBase<MachineDAO, Machine>
{

    @BeforeMethod
    protected void methodSetUp()
    {
        super.methodSetUp();
    }

    @Override
    protected MachineDAO createDao(EntityManager arg0)
    {
        return new MachineDAO(arg0);
    }

    @Override
    protected PersistentInstanceTester<Machine> createEntityInstanceGenerator()
    {
        return new MachineGenerator(getSeed());
    }

    @Test
    public void test_findMachines() throws IllegalAccessException, InvocationTargetException,
        NoSuchMethodException
    {
        DatacenterGenerator datacenterGenerator = new DatacenterGenerator(getSeed());
        MachineGenerator machineGenerator = new MachineGenerator(getSeed());

        Datacenter datacenter1 = datacenterGenerator.createUniqueInstance();
        Datacenter datacenter2 = datacenterGenerator.createUniqueInstance();
        Machine machine2_1 = machineGenerator.createMachine(datacenter2);
        machine2_1.setName("bbb");
        Machine machine2_2 = machineGenerator.createMachine(datacenter2);
        machine2_2.setName("aaa");
        ds().persistAll(datacenter1, datacenter2, machine2_1, machine2_2);

        MachineDAO dao = createDaoForRollbackTransaction();
        List<Machine> machines = dao.findMachines(reload(dao, datacenter1));
        Assert.assertTrue(machines.isEmpty());

        machines = dao.findMachines(reload(dao, datacenter2));
        assertEqualsPropertyForList(Machine.NAME_PROPERTY, machines, "aaa", "bbb");
    }

    @Test
    public void test_findRackMachines()
    {
        DatacenterGenerator datacenterGenerator = new DatacenterGenerator(getSeed());
        MachineGenerator machineGenerator = new MachineGenerator(getSeed());

        Datacenter datacenter1 = datacenterGenerator.createUniqueInstance();
        Datacenter datacenter2 = datacenterGenerator.createUniqueInstance();
        Rack rack1_1 = datacenter1.createRack("Rack1_1", 2, 4094, 2, 10);
        Rack rack2_1 = datacenter2.createRack("Rack2_1", 2, 4094, 2, 10);
        Rack rack2_2 = datacenter2.createRack("Rack2_2", 2, 4094, 2, 10);
        Machine machine1_1 = machineGenerator.createMachine(datacenter1);
        Machine machine2_2 = machineGenerator.createMachine(datacenter2);
        Machine machine2_3 = machineGenerator.createMachine(datacenter2);
        machine1_1.setRack(rack1_1);
        machine2_2.setRack(rack2_1);
        machine2_3.setRack(rack2_1);
        ds().persistAll(datacenter1, datacenter2, rack1_1, rack2_1, rack2_2, machine1_1,
            machine2_2, machine2_3);

        MachineDAO dao = createDaoForRollbackTransaction();
        Assert.assertEquals(dao.findRackMachines(reload(dao, rack1_1)).size(), 1);
        Assert.assertEquals(dao.findRackMachines(reload(dao, rack2_1)).size(), 2);
        Assert.assertEquals(dao.findRackMachines(reload(dao, rack2_2)).size(), 0);
    }

    @Test
    public void test_existsAnyWithDatacenterAndName()
    {
        DatacenterGenerator generator = new DatacenterGenerator(getSeed());
        MachineGenerator machineGenerator = new MachineGenerator(getSeed());

        Datacenter datacenter1 = generator.createUniqueInstance();
        Datacenter datacenter2 = generator.createUniqueInstance();
        Machine machine1_1 = machineGenerator.createMachine(datacenter1);
        machine1_1.setName("Name 1");
        Machine machine2_1 = machineGenerator.createMachine(datacenter2);
        machine2_1.setName("Name 1");
        Machine machine2_2 = machineGenerator.createMachine(datacenter2);
        machine2_2.setName("Name 2");
        ds().persistAll(datacenter1, datacenter2, machine1_1, machine2_1, machine2_2);

        Assert.assertTrue(createDaoForRollbackTransaction().existsAnyWithDatacenterAndName(
            datacenter1, "Name 1"));
        Assert.assertFalse(createDaoForRollbackTransaction().existsAnyWithDatacenterAndName(
            datacenter1, "Name 2"));
        Assert.assertFalse(createDaoForRollbackTransaction().existsAnyWithDatacenterAndName(
            datacenter1, "Name 3"));
        Assert.assertTrue(createDaoForRollbackTransaction().existsAnyWithDatacenterAndName(
            datacenter2, "Name 1"));
        Assert.assertTrue(createDaoForRollbackTransaction().existsAnyWithDatacenterAndName(
            datacenter2, "Name 2"));
        Assert.assertFalse(createDaoForRollbackTransaction().existsAnyWithDatacenterAndName(
            datacenter2, "Name 3"));
    }

    @Test
    public void test_existsAnyOtherWithDatacenterAndName()
    {
        DatacenterGenerator generator = new DatacenterGenerator(getSeed());
        MachineGenerator machineGenerator = new MachineGenerator(getSeed());

        Datacenter datacenter1 = generator.createUniqueInstance();
        Datacenter datacenter2 = generator.createUniqueInstance();
        Machine machine1_1 = machineGenerator.createMachine(datacenter1);
        machine1_1.setName("Name 1");
        Machine machine2_1 = machineGenerator.createMachine(datacenter2);
        machine2_1.setName("Name 1");
        Machine machine2_2 = machineGenerator.createMachine(datacenter2);
        machine2_2.setName("Name 2");
        ds().persistAll(datacenter1, datacenter2, machine1_1, machine2_1, machine2_2);

        Assert.assertFalse(createDaoForRollbackTransaction().existsAnyOtherWithDatacenterAndName(
            machine1_1, "Name 1"));
        Assert.assertFalse(createDaoForRollbackTransaction().existsAnyOtherWithDatacenterAndName(
            machine1_1, "Name 2"));
        Assert.assertFalse(createDaoForRollbackTransaction().existsAnyOtherWithDatacenterAndName(
            machine1_1, "Name 3"));

        Assert.assertFalse(createDaoForRollbackTransaction().existsAnyOtherWithDatacenterAndName(
            machine2_1, "Name 1"));
        Assert.assertTrue(createDaoForRollbackTransaction().existsAnyOtherWithDatacenterAndName(
            machine2_1, "Name 2"));
        Assert.assertFalse(createDaoForRollbackTransaction().existsAnyOtherWithDatacenterAndName(
            machine2_1, "Name 3"));

        Assert.assertTrue(createDaoForRollbackTransaction().existsAnyOtherWithDatacenterAndName(
            machine2_2, "Name 1"));
        Assert.assertFalse(createDaoForRollbackTransaction().existsAnyOtherWithDatacenterAndName(
            machine2_2, "Name 2"));
        Assert.assertFalse(createDaoForRollbackTransaction().existsAnyOtherWithDatacenterAndName(
            machine2_2, "Name 3"));
    }

    // CODE_COVERAGE: wrong report by Emma
    @Test
    public void test_concurrentDeletion()
    {
        Machine machine = createUniqueEntity();
        ds().persistAll(machine.getDatacenter(), machine);

        MachineDAO dao1 = createDaoForReadWriteTransaction();
        MachineDAO dao2 = createDaoForReadWriteTransaction();

        Machine machineB = dao1.findById(machine.getId());
        Machine machineC = dao2.findById(machine.getId());
        dao1.remove(machineB);
        EntityManagerHelper.commitAndClose(dao1.getEntityManager());

        machineC.setName("New name");
        try
        {
            dao2.flush();
            fail(); // /CLVER
        }
        catch (OptimisticLockException e)
        {
            // We expect this to happen
        }
    }

    // CODE_COVERAGE: wrong report by Emma
    @Test
    public void test_concurrenUpdate()
    {
        Machine machine = createUniqueEntity();
        ds().persistAll(machine.getDatacenter(), machine);

        MachineDAO dao1 = createDaoForReadWriteTransaction();
        MachineDAO dao2 = createDaoForReadWriteTransaction();

        Machine machineB = dao1.findById(machine.getId());
        Machine machineC = dao2.findById(machine.getId());
        machineB.setName("New name 1");
        EntityManagerHelper.commitAndClose(dao1.getEntityManager());

        machineC.setName("New name 2");
        try
        {
            dao2.flush();
            fail(); // / CLVER
        }
        catch (OptimisticLockException e)
        {
            // We expect this to happen
        }
    }

    @Test
    public void test_deleteRackMachines()
    {
        MachineGenerator machineGenerator = new MachineGenerator(getSeed());

        Datacenter datacenter = new Datacenter("Datacenter name", "Datacenter location");
        Rack rack1 = datacenter.createRack("Rack 1", 2, 4094, 2, 10);
        Rack rack2 = datacenter.createRack("Rack 2", 2, 4094, 2, 10);
        Machine machine1_1 = machineGenerator.createMachine(datacenter);
        machine1_1.setRack(rack1);
        Machine machine2_1 = machineGenerator.createMachine(datacenter);
        machine2_1.setRack(rack2);
        Machine machine2_2 = machineGenerator.createMachine(datacenter);
        machine2_2.setRack(rack2);
        Machine machine3 = machineGenerator.createMachine(datacenter);

        ds().persistAll(datacenter, rack1, rack2, machine1_1, machine2_1, machine2_2, machine3);

        MachineDAO dao = createDaoForReadWriteTransaction();
        Assert.assertEquals(dao.deleteRackMachines(reload(dao, rack1)), 1);
        EntityManagerHelper.commitAndClose(dao.getEntityManager());

        Assert.assertFalse(ds().canFind(machine1_1));
        Assert.assertTrue(ds().canFind(machine2_1));
        Assert.assertTrue(ds().canFind(machine2_2));
        Assert.assertTrue(ds().canFind(machine3));

        dao = createDaoForReadWriteTransaction();
        Assert.assertEquals(dao.deleteRackMachines(reload(dao, rack2)), 2);
        EntityManagerHelper.commitAndClose(dao.getEntityManager());
        Assert.assertFalse(ds().canFind(machine1_1));
        Assert.assertFalse(ds().canFind(machine2_1));
        Assert.assertFalse(ds().canFind(machine2_2));
        Assert.assertTrue(ds().canFind(machine3));
    }

    @Test
    public void test_findRackEnabledMachines()
    {
        DatacenterGenerator datacenterGenerator = new DatacenterGenerator(getSeed());
        MachineGenerator machineGenerator = new MachineGenerator(getSeed());

        Datacenter datacenter = datacenterGenerator.createUniqueInstance();

        Rack rack = datacenter.createRack("Rack", 2, 4094, 2, 10);

        Machine machine1 = machineGenerator.createMachine(datacenter);
        Machine machine2 = machineGenerator.createMachine(datacenter);
        Machine machine3 = machineGenerator.createMachine(datacenter);
        Machine machine4 = machineGenerator.createMachine(datacenter);
        Machine machine5 = machineGenerator.createMachine(datacenter);
        Machine machine6 = machineGenerator.createMachine(datacenter);
        Machine machine7 = machineGenerator.createMachine(datacenter);
        Machine machine8 = machineGenerator.createMachine(datacenter);

        machine1.setState(State.DISABLED_FOR_HA);
        machine2.setState(State.HA_IN_PROGRESS);
        machine3.setState(State.HALTED);
        machine4.setState(State.MANAGED);
        machine5.setState(State.NOT_MANAGED);
        machine6.setState(State.PROVISIONED);
        machine7.setState(State.STOPPED);
        machine8.setState(State.UNLICENSED);

        machine1.setRack(rack);
        machine2.setRack(rack);
        machine2.setRack(rack);
        machine4.setRack(rack);
        machine5.setRack(rack);
        machine6.setRack(rack);
        machine7.setRack(rack);
        machine8.setRack(rack);

        ds().persistAll(datacenter, rack, machine1, machine2, machine3, machine4, machine5,
            machine6, machine7, machine8);

        MachineDAO dao = createDaoForRollbackTransaction();

        Assert.assertEquals(State.values().length, 8);
        Assert.assertEquals(dao.findRackEnabledForHAMachines(reload(dao, rack)).size(), 1);
    }
}
