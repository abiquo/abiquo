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

import org.testng.Assert;
import org.testng.annotations.Test;

import com.abiquo.server.core.cloud.Hypervisor;
import com.abiquo.server.core.cloud.HypervisorGenerator;
import com.abiquo.server.core.common.persistence.DefaultDAOTestBase;
import com.abiquo.server.core.infrastructure.Machine.State;
import com.softwarementors.bzngine.entities.test.PersistentInstanceTester;

public class RackDAOTest extends DefaultDAOTestBase<RackDAO, Rack>
{

    @Override
    protected RackDAO createDao(final EntityManager arg0)
    {
        return new RackDAO(arg0);
    }

    @Override
    protected PersistentInstanceTester<Rack> createEntityInstanceGenerator()
    {
        return new RackGenerator(getSeed());
    }

    @Test
    public void test_findRacks() throws IllegalAccessException, InvocationTargetException,
        NoSuchMethodException
    {
        DatacenterGenerator generator = new DatacenterGenerator(getSeed());

        Datacenter datacenter1 = generator.createUniqueInstance();
        Datacenter datacenter2 = generator.createUniqueInstance();
        Rack rack2_1 = datacenter2.createRack("bRack_1", 2, 4094, 2, 10);
        Rack rack2_2 = datacenter2.createRack("aRack_2", 2, 4094, 2, 10);
        ds().persistAll(datacenter1, datacenter2, rack2_1, rack2_2);

        RackDAO dao = createDaoForRollbackTransaction();
        Assert.assertEquals(dao.findRacks(reload(dao, datacenter1)).size(), 0);
        List<Rack> result = dao.findRacks(reload(dao, datacenter2));
        assertEqualsPropertyForList(Rack.NAME_PROPERTY, result, "aRack_2", "bRack_1");
    }

    @Test
    public void test_existsAnyWithDatacenterAndName()
    {
        DatacenterGenerator generator = new DatacenterGenerator(getSeed());

        Datacenter datacenter1 = generator.createUniqueInstance();
        Datacenter datacenter2 = generator.createUniqueInstance();
        Rack rack1_1 = datacenter1.createRack("Rack 1", 2, 4094, 2, 10);
        Rack rack2_1 = datacenter2.createRack("Rack 1", 2, 4094, 2, 10);
        Rack rack2_2 = datacenter2.createRack("Rack 2", 2, 4094, 2, 10);
        ds().persistAll(datacenter1, datacenter2, rack1_1, rack2_1, rack2_2);

        Assert.assertTrue(createDaoForRollbackTransaction().existsAnyWithDatacenterAndName(
            datacenter1, "Rack 1"));
        Assert.assertFalse(createDaoForRollbackTransaction().existsAnyWithDatacenterAndName(
            datacenter1, "Rack 2"));
        Assert.assertFalse(createDaoForRollbackTransaction().existsAnyWithDatacenterAndName(
            datacenter1, "Rack 3"));
        Assert.assertTrue(createDaoForRollbackTransaction().existsAnyWithDatacenterAndName(
            datacenter2, "Rack 1"));
        Assert.assertTrue(createDaoForRollbackTransaction().existsAnyWithDatacenterAndName(
            datacenter2, "Rack 2"));
        Assert.assertFalse(createDaoForRollbackTransaction().existsAnyWithDatacenterAndName(
            datacenter2, "Rack 3"));
    }

    @Test
    public void test_existsAnyOtherWithDatacenterAndName()
    {
        DatacenterGenerator generator = new DatacenterGenerator(getSeed());

        Datacenter datacenter1 = generator.createUniqueInstance();
        Datacenter datacenter2 = generator.createUniqueInstance();
        Rack rack1_1 = datacenter1.createRack("Rack 1", 2, 4094, 2, 10);
        Rack rack2_1 = datacenter2.createRack("Rack 1", 2, 4094, 2, 10);
        Rack rack2_2 = datacenter2.createRack("Rack 2", 2, 4094, 2, 10);
        ds().persistAll(datacenter1, datacenter2, rack1_1, rack2_1, rack2_2);

        Assert.assertFalse(createDaoForRollbackTransaction().existsAnyOtherWithDatacenterAndName(
            rack1_1, "Rack 1"));
        Assert.assertFalse(createDaoForRollbackTransaction().existsAnyOtherWithDatacenterAndName(
            rack1_1, "Rack 2"));
        Assert.assertFalse(createDaoForRollbackTransaction().existsAnyOtherWithDatacenterAndName(
            rack1_1, "Rack 3"));

        Assert.assertFalse(createDaoForRollbackTransaction().existsAnyOtherWithDatacenterAndName(
            rack2_1, "Rack 1"));
        Assert.assertTrue(createDaoForRollbackTransaction().existsAnyOtherWithDatacenterAndName(
            rack2_1, "Rack 2"));
        Assert.assertFalse(createDaoForRollbackTransaction().existsAnyOtherWithDatacenterAndName(
            rack2_1, "Rack 3"));

        Assert.assertTrue(createDaoForRollbackTransaction().existsAnyOtherWithDatacenterAndName(
            rack2_2, "Rack 1"));
        Assert.assertFalse(createDaoForRollbackTransaction().existsAnyOtherWithDatacenterAndName(
            rack2_2, "Rack 2"));
        Assert.assertFalse(createDaoForRollbackTransaction().existsAnyOtherWithDatacenterAndName(
            rack2_2, "Rack 3"));
    }

    @Test
    public void test_findRacksWithHAEnabled() throws IllegalAccessException,
        InvocationTargetException, NoSuchMethodException
    {
        DatacenterGenerator generator = new DatacenterGenerator(getSeed());

        Datacenter datacenter = generator.createUniqueInstance();

        Rack rack1 = datacenter.createRack("Rack_1", 2, 4094, 2, 10);
        Rack rack2 = datacenter.createRack("Rack_2", 2, 4094, 2, 10);
        Rack rack3 = datacenter.createRack("Rack_3", 2, 4094, 2, 10);

        rack1.setHaEnabled(true);
        rack2.setHaEnabled(false);
        rack3.setHaEnabled(true);

        ds().persistAll(datacenter, rack1, rack2, rack3);

        RackDAO dao = createDaoForRollbackTransaction();

        List<Rack> result = dao.findRacksWithHAEnabled(reload(dao, datacenter));

        Assert.assertEquals(result.size(), 2);
    }

    /**
     * Returns any machine that is in the rack in MANAGED.
     * 
     * @param rackId rack.
     * @return Machine
     */
    @Test
    public void getRandomMachinesToShutDownFromRack()
    {

        DatacenterGenerator generator = new DatacenterGenerator(getSeed());
        UcsRackGenerator ucsRackGenerator = new UcsRackGenerator(getSeed());
        MachineGenerator machineGenerator = new MachineGenerator(getSeed());
        HypervisorGenerator hypervisorGenerator = new HypervisorGenerator(getSeed());
        Datacenter datacenter = generator.createUniqueInstance();

        UcsRack ucsRack = ucsRackGenerator.createInstance(datacenter);
        ds().persistAll(datacenter, ucsRack);
        for (int i = 0; i < 7; i++)
        {
            Machine machine = machineGenerator.createUniqueInstance();
            machine.setState(State.MANAGED);
            machine.setRack(ucsRack);
            machine.setDatacenter(datacenter);
            machine.setBelongsToManagedRack(Boolean.TRUE);

            Hypervisor visor = hypervisorGenerator.createUniqueInstance();
            visor.setMachine(machine);
            ds().persistAll(machine, visor);
        }

        List<Machine> machines =
            createDaoForRollbackTransaction().getRandomMachinesToShutDownFromRack(ucsRack.getId(),
                4);
        Assert.assertEquals(machines.size(), 4);
    }

    /**
     * Returns any machine that is in the rack in MANAGED.
     * 
     * @param rackId rack.
     * @return Machine
     */
    @Test
    public void getRandomMachinesToPoweOnFromRack()
    {

        DatacenterGenerator generator = new DatacenterGenerator(getSeed());
        UcsRackGenerator ucsRackGenerator = new UcsRackGenerator(getSeed());
        MachineGenerator machineGenerator = new MachineGenerator(getSeed());
        HypervisorGenerator hypervisorGenerator = new HypervisorGenerator(getSeed());
        Datacenter datacenter = generator.createUniqueInstance();

        UcsRack ucsRack = ucsRackGenerator.createInstance(datacenter);
        ds().persistAll(datacenter, ucsRack);
        for (int i = 0; i < 7; i++)
        {
            Machine machine = machineGenerator.createUniqueInstance();
            machine.setState(State.HALTED_FOR_SAVE);
            machine.setRack(ucsRack);
            machine.setBelongsToManagedRack(Boolean.TRUE);

            machine.setDatacenter(datacenter);
            Hypervisor visor = hypervisorGenerator.createUniqueInstance();
            visor.setMachine(machine);
            ds().persistAll(machine, visor);
        }
        Machine machine = machineGenerator.createUniqueInstance();
        machine.setState(State.MANAGED);
        machine.setRack(ucsRack);
        machine.setBelongsToManagedRack(Boolean.TRUE);

        machine.setDatacenter(datacenter);
        Hypervisor visor = hypervisorGenerator.createUniqueInstance();
        visor.setMachine(machine);
        ds().persistAll(machine, visor);
        List<Machine> machines =
            createDaoForRollbackTransaction().getRandomMachinesToStartFromRack(ucsRack.getId(), 4);
        Assert.assertEquals(machines.size(), 4);
    }
}
