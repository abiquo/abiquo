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

package com.abiquo.server.core.scheduler;

import java.lang.reflect.InvocationTargetException;

import javax.persistence.EntityManager;

import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import com.abiquo.server.core.common.persistence.DefaultDAOTestBase;
import com.abiquo.server.core.common.persistence.TestDataAccessManager;
import com.abiquo.server.core.infrastructure.Datacenter;
import com.abiquo.server.core.infrastructure.DatacenterGenerator;
import com.abiquo.server.core.infrastructure.Machine;
import com.abiquo.server.core.infrastructure.MachineGenerator;
import com.abiquo.server.core.infrastructure.Rack;
import com.softwarementors.bzngine.engines.jpa.test.configuration.EntityManagerFactoryForTesting;
import com.softwarementors.bzngine.entities.test.PersistentInstanceTester;

public class MachineLoadRuleDAOTest extends DefaultDAOTestBase<MachineLoadRuleDAO, MachineLoadRule>
{
    private DatacenterGenerator datacenterGenerator;

    private MachineGenerator machineGenerator;

    @Override
    @BeforeMethod
    protected void methodSetUp()
    {
        super.methodSetUp();
        datacenterGenerator = new DatacenterGenerator(getSeed());
        machineGenerator = new MachineGenerator(getSeed());
    }

    @Override
    protected MachineLoadRuleDAO createDao(final EntityManager entityManager)
    {
        return new MachineLoadRuleDAO(entityManager);
    }

    @Override
    protected PersistentInstanceTester<MachineLoadRule> createEntityInstanceGenerator()
    {
        return new MachineLoadRuleGenerator(getSeed());
    }

    @Override
    protected EntityManagerFactoryForTesting getFactory()
    {
        return TestDataAccessManager.getFactory();
    }

    @Override
    public MachineLoadRuleGenerator eg()
    {
        return (MachineLoadRuleGenerator) super.eg();
    }

    @Test
    public void test_findMachineLoadRulesByRack() throws IllegalAccessException,
        InvocationTargetException, NoSuchMethodException
    {
        Datacenter datacenter = datacenterGenerator.createUniqueInstance();
        Rack rack = datacenter.createRack("bRack_1", 2, 4094, 2, 10);
        Machine machine = machineGenerator.createMachine(datacenter, rack);
        MachineLoadRule mclr1 = eg().createInstance(rack);
        MachineLoadRule mclr2 = eg().createInstance(machine);
        ds().persistAll(datacenter, rack, machine, mclr1, mclr2);

        MachineLoadRuleDAO dao = createDaoForRollbackTransaction();
        Assert.assertEquals(dao.findByRack(rack).size(), 1);
    }

    @Test
    public void test_findMachineLoadRulesByRackIncludingMachineRules()
        throws IllegalAccessException, InvocationTargetException, NoSuchMethodException
    {
        Datacenter datacenter = datacenterGenerator.createUniqueInstance();
        Rack rack = datacenter.createRack("bRack_1", 2, 4094, 2, 10);
        Machine machine = machineGenerator.createMachine(datacenter, rack);
        MachineLoadRule mclr1 = eg().createInstance(rack);
        MachineLoadRule mclr2 = eg().createInstance(machine);
        ds().persistAll(datacenter, rack, machine, mclr1, mclr2);

        MachineLoadRuleDAO dao = createDaoForRollbackTransaction();
        Assert.assertEquals(dao.findByRack(rack, true).size(), 2);
    }
}
