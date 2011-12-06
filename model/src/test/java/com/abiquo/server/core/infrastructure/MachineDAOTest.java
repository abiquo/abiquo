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
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import javax.persistence.EntityManager;
import javax.persistence.OptimisticLockException;
import javax.validation.ConstraintViolationException;

import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import com.abiquo.model.enumerator.HypervisorType;
import com.abiquo.model.enumerator.MachineState;
import com.abiquo.server.core.cloud.Hypervisor;
import com.abiquo.server.core.cloud.HypervisorGenerator;
import com.abiquo.server.core.cloud.VirtualDatacenter;
import com.abiquo.server.core.cloud.VirtualDatacenterGenerator;
import com.abiquo.server.core.common.persistence.DefaultDAOTestBase;
import com.abiquo.server.core.enterprise.Enterprise;
import com.abiquo.server.core.enterprise.EnterpriseGenerator;
import com.softwarementors.bzngine.engines.jpa.EntityManagerHelper;
import com.softwarementors.bzngine.entities.test.PersistentInstanceTester;

public class MachineDAOTest extends DefaultDAOTestBase<MachineDAO, Machine>
{
    private HypervisorGenerator hypervisorGenerator;

    @Override
    @BeforeMethod
    protected void methodSetUp()
    {
        super.methodSetUp();
        hypervisorGenerator = new HypervisorGenerator(getSeed());
    }

    @Override
    protected MachineDAO createDao(final EntityManager arg0)
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
        HypervisorGenerator hypervisorGenerator = new HypervisorGenerator(getSeed());

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
        Machine machine9 = machineGenerator.createMachine(datacenter);
        Machine machine10 = machineGenerator.createMachine(datacenter);
        Machine machine11 = machineGenerator.createMachine(datacenter);

        machine1.setState(MachineState.DISABLED_FOR_HA);
        machine2.setState(MachineState.HA_IN_PROGRESS);
        machine3.setState(MachineState.HALTED);
        machine4.setState(MachineState.MANAGED);
        machine5.setState(MachineState.NOT_MANAGED);
        machine6.setState(MachineState.PROVISIONED);
        machine7.setState(MachineState.STOPPED);
        machine8.setState(MachineState.UNLICENSED);
        machine9.setState(MachineState.MANAGED);
        machine10.setState(MachineState.MANAGED);
        machine11.setState(MachineState.MANAGED);

        machine1.setRack(rack);
        machine2.setRack(rack);
        machine3.setRack(rack);
        machine4.setRack(rack);
        machine5.setRack(rack);
        machine6.setRack(rack);
        machine7.setRack(rack);
        machine8.setRack(rack);
        machine9.setRack(rack);
        machine10.setRack(rack);
        machine11.setRack(rack);

        machine9.setIpmiIP("10.60.1.205");
        machine9.setIpmiUser("earl.hickey");
        machine9.setIpmiPassword("karma");

        machine10.setIpmiIP("10.60.1.205");
        machine10.setIpmiUser("earl.hickey");

        machine11.setIpmiIP("10.60.1.205");
        machine11.setIpmiUser("earl.hickey");
        machine11.setIpmiPassword("karma");

        Hypervisor hyp1 = hypervisorGenerator.createUniqueInstance();
        hyp1.setType(HypervisorType.VMX_04);
        hyp1.setMachine(machine1);
        // machine1.setHypervisor(hyp1);

        Hypervisor hyp2 = hypervisorGenerator.createUniqueInstance();
        hyp2.setType(HypervisorType.VMX_04);
        hyp2.setMachine(machine2);
        // machine2.setHypervisor(hyp2);

        Hypervisor hyp3 = hypervisorGenerator.createUniqueInstance();
        hyp3.setType(HypervisorType.VMX_04);
        hyp3.setMachine(machine3);
        // machine3.setHypervisor(hyp3);

        Hypervisor hyp4 = hypervisorGenerator.createUniqueInstance();
        hyp4.setType(HypervisorType.VMX_04);
        hyp4.setMachine(machine4);
        // machine4.setHypervisor(hyp4);

        Hypervisor hyp5 = hypervisorGenerator.createUniqueInstance();
        hyp5.setType(HypervisorType.VMX_04);
        hyp5.setMachine(machine5);
        // machine5.setHypervisor(hyp5);

        Hypervisor hyp6 = hypervisorGenerator.createUniqueInstance();
        hyp6.setType(HypervisorType.VMX_04);
        hyp6.setMachine(machine6);
        // machine6.setHypervisor(hyp6);

        Hypervisor hyp7 = hypervisorGenerator.createUniqueInstance();
        hyp7.setType(HypervisorType.VMX_04);
        hyp7.setMachine(machine7);
        // machine7.setHypervisor(hyp7);

        Hypervisor hyp8 = hypervisorGenerator.createUniqueInstance();
        hyp8.setType(HypervisorType.VMX_04);
        hyp8.setMachine(machine8);
        // machine8.setHypervisor(hyp8);

        Hypervisor hyp9 = hypervisorGenerator.createUniqueInstance();
        hyp9.setType(HypervisorType.VMX_04);
        hyp9.setMachine(machine9);
        // machine9.setHypervisor(hyp9);

        Hypervisor hyp10 = hypervisorGenerator.createUniqueInstance();
        hyp10.setType(HypervisorType.VMX_04);
        hyp10.setMachine(machine10);
        // machine10.setHypervisor(hyp10);

        Hypervisor hyp11 = hypervisorGenerator.createUniqueInstance();
        hyp11.setType(HypervisorType.XENSERVER);
        hyp11.setMachine(machine11);
        // machine11.setHypervisor(hyp11);

        ds().persistAll(datacenter, rack, machine1, machine2, machine3, machine4, machine5,
            machine6, machine7, machine8, machine9, machine10, machine11, hyp1, hyp2, hyp3, hyp4,
            hyp5, hyp6, hyp7, hyp8, hyp9, hyp10, hyp11);

        MachineDAO dao = createDaoForRollbackTransaction();

        Assert.assertEquals(MachineState.values().length, 8);
        Assert.assertEquals(dao.findRackEnabledForHAMachines(reload(dao, rack)).size(), 1);
    }

    @Test
    public void test_reallocateReserved()
    {
        final String sharedDsUid = "xaredUUID";

        HypervisorGenerator hGenerator = new HypervisorGenerator(getSeed());
        MachineGenerator machineGenerator = new MachineGenerator(getSeed());
        VirtualDatacenterGenerator vdcGenerator = new VirtualDatacenterGenerator(getSeed());
        EnterpriseGenerator eGenerator = new EnterpriseGenerator(getSeed());
        DatastoreGenerator dsGenerator = new DatastoreGenerator(getSeed());

        Enterprise e = eGenerator.createInstanceNoLimits("someEnterprise");
        e.setIsReservationRestricted(true);

        Datacenter datacenter = new Datacenter("Datacenter name", "Datacenter location");
        Datacenter datacenter2 = new Datacenter("Datacenter name2", "Datacenter location2");

        Rack rack1 = datacenter.createRack("Rack 1", 2, 4094, 2, 10);
        Rack rack2 = datacenter.createRack("Rack 2", 2, 4094, 2, 10);

        Machine machine1_1 = machineGenerator.createMachine(datacenter, rack1);
        Machine machine1_2 = machineGenerator.createMachine(datacenter, rack1);
        Machine machine2_1 = machineGenerator.createMachine(datacenter, rack2);
        Machine machine2_2 = machineGenerator.createMachine(datacenter, rack2);

        Hypervisor h11 = hGenerator.createInstance(machine1_1, HypervisorType.VMX_04);
        Hypervisor h12 = hGenerator.createInstance(machine1_2, HypervisorType.VMX_04);
        Hypervisor h21 = hGenerator.createInstance(machine2_1, HypervisorType.VMX_04);
        Hypervisor h22 = hGenerator.createInstance(machine2_2, HypervisorType.VMX_04);

        // all machines are reserved by the enterprise
        machine1_1.setEnterprise(e);
        machine1_2.setEnterprise(e);
        machine2_1.setEnterprise(e);
        machine2_2.setEnterprise(e);

        machine1_1.setState(MachineState.MANAGED);
        machine1_2.setState(MachineState.MANAGED);
        machine2_1.setState(MachineState.MANAGED);
        machine2_2.setState(MachineState.MANAGED);

        Datastore ds11 = dsGenerator.createInstance(machine1_1);
        Datastore ds12 = dsGenerator.createInstance(machine1_2);
        Datastore ds21 = dsGenerator.createInstance(machine2_1);
        Datastore ds22 = dsGenerator.createInstance(machine2_2);
        ds11.setDatastoreUUID(sharedDsUid);
        ds12.setDatastoreUUID(sharedDsUid);
        ds21.setDatastoreUUID(sharedDsUid);
        ds22.setDatastoreUUID(sharedDsUid);
        ds11.setEnabled(true);
        ds11.setSize(10);
        ds11.setUsedSize(0);
        ds12.setEnabled(true);
        ds12.setSize(10);
        ds12.setUsedSize(0);
        ds21.setEnabled(true);
        ds21.setSize(10);
        ds21.setUsedSize(0);
        ds22.setEnabled(true);
        ds22.setSize(10);
        ds22.setUsedSize(0);

        VirtualDatacenter vdc1 = vdcGenerator.createInstance(datacenter, e, HypervisorType.VMX_04);
        VirtualDatacenter vdc2 = vdcGenerator.createInstance(datacenter, e, HypervisorType.VMX_04);
        VirtualDatacenter vdc3 = vdcGenerator.createInstance(datacenter, e, HypervisorType.VMX_04);

        ds().persistAll(e, datacenter, datacenter2, rack1, rack2, machine1_1, machine1_2,
            machine2_1, machine2_2, h11, h12, h21, h22, ds11, ds12, ds21, ds22, vdc1, vdc2, vdc3);

        MachineDAO dao = createDaoForReadWriteTransaction();

        List<Machine> candidates2 =
            dao.findFirstCandidateMachinesReservedRestrictedHAExclude(rack1.getId(), vdc1.getId(),
                e, h11.getId());

        Assert.assertEquals(candidates2.size(), 1);
        Assert.assertEquals(candidates2.get(0).getId(), machine1_2.getId());

        List<Machine> candidates =
            dao.findCandidateMachines(rack1.getId(), vdc1.getId(), e, sharedDsUid, h11.getId());

        Assert.assertEquals(candidates.size(), 1);
        Assert.assertEquals(candidates.get(0).getId(), machine1_2.getId());

        EntityManagerHelper.commitAndClose(dao.getEntityManager());
    }

    @Test
    public void test_reallocateNoReserved()
    {
        final String sharedDsUid = "xaredUUID";

        HypervisorGenerator hGenerator = new HypervisorGenerator(getSeed());
        MachineGenerator machineGenerator = new MachineGenerator(getSeed());
        VirtualDatacenterGenerator vdcGenerator = new VirtualDatacenterGenerator(getSeed());
        EnterpriseGenerator eGenerator = new EnterpriseGenerator(getSeed());
        DatastoreGenerator dsGenerator = new DatastoreGenerator(getSeed());

        Enterprise e = eGenerator.createInstanceNoLimits("someEnterprise");
        e.setIsReservationRestricted(false);

        Datacenter datacenter = new Datacenter("Datacenter name", "Datacenter location");
        Datacenter datacenter2 = new Datacenter("Datacenter name2", "Datacenter location2");

        Rack rack1 = datacenter.createRack("Rack 1", 2, 4094, 2, 10);
        Rack rack2 = datacenter.createRack("Rack 2", 2, 4094, 2, 10);

        Machine machine1_1 = machineGenerator.createMachine(datacenter, rack1);
        Machine machine1_2 = machineGenerator.createMachine(datacenter, rack1);
        Machine machine2_1 = machineGenerator.createMachine(datacenter, rack2);
        Machine machine2_2 = machineGenerator.createMachine(datacenter, rack2);

        Hypervisor h11 = hGenerator.createInstance(machine1_1, HypervisorType.VMX_04);
        Hypervisor h12 = hGenerator.createInstance(machine1_2, HypervisorType.VMX_04);
        Hypervisor h21 = hGenerator.createInstance(machine2_1, HypervisorType.VMX_04);
        Hypervisor h22 = hGenerator.createInstance(machine2_2, HypervisorType.VMX_04);

        // ANY machines are reserved by the enterprise
        machine1_1.setEnterprise(null);
        machine1_2.setEnterprise(null);
        machine2_1.setEnterprise(null);
        machine2_2.setEnterprise(null);

        machine1_1.setState(MachineState.MANAGED);
        machine1_2.setState(MachineState.MANAGED);
        machine2_1.setState(MachineState.MANAGED);
        machine2_2.setState(MachineState.MANAGED);

        Datastore ds11 = dsGenerator.createInstance(machine1_1);
        Datastore ds12 = dsGenerator.createInstance(machine1_2);
        Datastore ds21 = dsGenerator.createInstance(machine2_1);
        Datastore ds22 = dsGenerator.createInstance(machine2_2);
        ds11.setDatastoreUUID(sharedDsUid);
        ds12.setDatastoreUUID(sharedDsUid);
        ds21.setDatastoreUUID(sharedDsUid);
        ds22.setDatastoreUUID(sharedDsUid);

        ds11.setEnabled(true);
        ds11.setSize(10);
        ds11.setUsedSize(0);
        ds12.setEnabled(true);
        ds12.setSize(10);
        ds12.setUsedSize(0);
        ds21.setEnabled(true);
        ds21.setSize(10);
        ds21.setUsedSize(0);
        ds22.setEnabled(true);
        ds22.setSize(10);
        ds22.setUsedSize(0);

        VirtualDatacenter vdc1 = vdcGenerator.createInstance(datacenter, e, HypervisorType.VMX_04);
        VirtualDatacenter vdc2 = vdcGenerator.createInstance(datacenter, e, HypervisorType.VMX_04);
        VirtualDatacenter vdc3 = vdcGenerator.createInstance(datacenter, e, HypervisorType.VMX_04);

        ds().persistAll(e, datacenter, datacenter2, rack1, rack2, machine1_1, machine1_2,
            machine2_1, machine2_2, h11, h12, h21, h22, ds11, ds12, ds21, ds22, vdc1, vdc2, vdc3);

        MachineDAO dao = createDaoForReadWriteTransaction();

        List<Machine> candidates =
            dao.findCandidateMachines(rack1.getId(), vdc1.getId(), e, sharedDsUid, h11.getId());

        Assert.assertEquals(candidates.size(), 1);
        Assert.assertEquals(candidates.get(0).getId(), machine1_2.getId());

        EntityManagerHelper.commitAndClose(dao.getEntityManager());
    }

    @Test
    public void test_findbyIP()
    {
        Hypervisor hypervisor = hypervisorGenerator.createUniqueInstance();

        List<Object> entitiesToPersist = new ArrayList<Object>();
        hypervisorGenerator.addAuxiliaryEntitiesToPersist(hypervisor, entitiesToPersist);
        persistAll(ds(), entitiesToPersist, hypervisor);

        Datacenter datacenter = hypervisor.getMachine().getDatacenter();

        MachineDAO dao = createDaoForRollbackTransaction();

        Machine result = dao.findByIp(datacenter, hypervisor.getIp());
        assertNotNull(result);
    }

    @Test
    public void test_findbyIP_notFound()
    {
        Hypervisor hypervisor = hypervisorGenerator.createUniqueInstance();

        List<Object> entitiesToPersist = new ArrayList<Object>();
        hypervisorGenerator.addAuxiliaryEntitiesToPersist(hypervisor, entitiesToPersist);
        persistAll(ds(), entitiesToPersist, hypervisor);

        Datacenter datacenter = hypervisor.getMachine().getDatacenter();

        MachineDAO dao = createDaoForRollbackTransaction();

        Machine result = dao.findByIp(datacenter, "NOT_EXISTING_IP");
        assertNull(result);
    }

    public void testLargeSwitchName()
    {
        MachineGenerator machineGenerator = new MachineGenerator(getSeed());
        EnterpriseGenerator eGenerator = new EnterpriseGenerator(getSeed());
        Enterprise e = eGenerator.createInstanceNoLimits("someEnterprise");

        Datacenter datacenter = new Datacenter("Datacenter name", "Datacenter location");

        Rack rack1 = datacenter.createRack("Rack 1", 2, 4094, 2, 10);
        ds().persistAll(datacenter, rack1, e);

        Machine machine = machineGenerator.createMachine(datacenter, rack1);

        machine.setEnterprise(e);

        machine.setState(MachineState.MANAGED);
        machine.setVirtualSwitch(new BigInteger(1000, new Random()).toString(32));
        MachineDAO dao = createDaoForReadWriteTransaction();
        dao.persist(machine);
        EntityManagerHelper.commitAndClose(dao.getEntityManager());
    }

    @Test(expectedExceptions = {ConstraintViolationException.class})
    public void testLargeSwitchNameFail()
    {
        Machine machine = this.createUniqueEntity();
        machine.setVirtualSwitch(new BigInteger(4444, new Random()).toString(32));
        ds().persistAll(machine);
    }
}
