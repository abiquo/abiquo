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
import java.util.ArrayList;
import java.util.List;

import javax.persistence.EntityManager;

import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import com.abiquo.server.core.appslibrary.Category;
import com.abiquo.server.core.cloud.VirtualMachine;
import com.abiquo.server.core.cloud.VirtualMachineGenerator;
import com.abiquo.server.core.common.persistence.DefaultDAOTestBase;
import com.softwarementors.bzngine.entities.test.PersistentInstanceTester;

public class RackDAOTest extends DefaultDAOTestBase<RackDAO, Rack>
{
    private DatacenterGenerator datacenterGenerator;

    private VirtualMachineGenerator vmgenerator;

    @BeforeMethod
    @Override
    protected void methodSetUp()
    {
        super.methodSetUp();
        datacenterGenerator = new DatacenterGenerator(getSeed());
        vmgenerator = new VirtualMachineGenerator(getSeed());
    }

    @Override
    protected RackDAO createDao(final EntityManager entityManager)
    {
        return new RackDAO(entityManager);
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
        Datacenter datacenter1 = datacenterGenerator.createUniqueInstance();
        Datacenter datacenter2 = datacenterGenerator.createUniqueInstance();
        Rack rack2_1 = datacenter2.createRack("bRack_1", 2, 4094, 2, 10);
        Rack rack2_2 = datacenter2.createRack("aRack_2", 2, 4094, 2, 10);
        ds().persistAll(datacenter1, datacenter2, rack2_1, rack2_2);

        RackDAO dao = createDaoForRollbackTransaction();
        Assert.assertEquals(dao.findRacks(reload(dao, datacenter1)).size(), 0);
        List<Rack> result = dao.findRacks(reload(dao, datacenter2));
        assertEqualsPropertyForList(Rack.NAME_PROPERTY, result, "aRack_2", "bRack_1");
    }

    public void test_findfilteredRacks() throws IllegalAccessException, InvocationTargetException,
        NoSuchMethodException
    {
        DatacenterGenerator generator = new DatacenterGenerator(getSeed());

        Datacenter datacenter1 = generator.createUniqueInstance();
        Datacenter datacenter2 = generator.createUniqueInstance();
        Rack rack2_1 = datacenter2.createRack("bRack_1", 2, 4094, 2, 10);
        Rack rack2_2 = datacenter2.createRack("aRack_2", 2, 4094, 2, 10);
        Rack rack2_3 = datacenter2.createRack("cRack_3_filter", 2, 4094, 2, 10);
        ds().persistAll(datacenter1, datacenter2, rack2_1, rack2_2, rack2_3);

        RackDAO dao = createDaoForRollbackTransaction();
        List<Rack> racks1 = dao.findRacks(reload(dao, datacenter1));
        List<Rack> racks2 = dao.findRacks(reload(dao, datacenter2));
        List<Rack> racks2filter = dao.findRacks(reload(dao, datacenter2), "filter");
        Assert.assertEquals(racks1.size(), 0);
        Assert.assertEquals(racks2.size(), 3);
        Assert.assertEquals(racks2filter.size(), 1);
        assertEqualsPropertyForList(Rack.NAME_PROPERTY, racks2, "aRack_2", "bRack_1",
            "cRack_3_filter");
        assertEqualsPropertyForList(Rack.NAME_PROPERTY, racks2filter, "cRack_3_filter");
    }

    @Test
    public void test_existsAnyWithDatacenterAndName()
    {
        Datacenter datacenter1 = datacenterGenerator.createUniqueInstance();
        Datacenter datacenter2 = datacenterGenerator.createUniqueInstance();
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
        Datacenter datacenter1 = datacenterGenerator.createUniqueInstance();
        Datacenter datacenter2 = datacenterGenerator.createUniqueInstance();
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
        Datacenter datacenter = datacenterGenerator.createUniqueInstance();

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

    @Test
    public void test_findUsedVrdpPorts()
    {
        VirtualMachine vm1 = vmgenerator.createUniqueInstance();
        VirtualMachine vm2 =
            vmgenerator.createInstance(vm1.getVirtualImage(), vm1.getEnterprise(),
                vm1.getHypervisor(), vm1.getUser(), "test");

        // FIXME: Fix virtual image fields until we have the changes in the VirtualImage API
        Category category = new Category("test-category");
        category.setIsDefault(0);
        category.setIsErasable(0);
        ds().persistAll(category);
        vm1.getVirtualImage().setIdCategory(category.getId());

        List<Object> entitiesToPersist = new ArrayList<Object>();
        vmgenerator.addAuxiliaryEntitiesToPersist(vm1, entitiesToPersist);
        persistAll(ds(), entitiesToPersist, vm1, vm2);

        Rack rack = vm1.getHypervisor().getMachine().getRack();

        RackDAO dao = createDaoForRollbackTransaction();
        List<Integer> usedPorts = dao.findUsedVrdpPorts(rack);

        assertEquals(usedPorts.size(), 2);
    }
}
