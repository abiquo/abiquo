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
import java.util.ArrayList;
import java.util.List;

import javax.persistence.EntityManager;

import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import com.abiquo.server.core.common.persistence.DefaultJpaDataAccessTestBase;
import com.abiquo.server.core.enterprise.EnterpriseGenerator;
import com.abiquo.server.core.infrastructure.Datacenter;
import com.abiquo.server.core.infrastructure.DatacenterGenerator;
import com.abiquo.server.core.infrastructure.Machine;
import com.abiquo.server.core.infrastructure.MachineGenerator;
import com.abiquo.server.core.infrastructure.Rack;
import com.abiquo.server.core.infrastructure.RackGenerator;
import com.softwarementors.bzngine.engines.jpa.EntityManagerHelper;

public class RulesRepTest extends DefaultJpaDataAccessTestBase
{
    FitPolicyRuleGenerator fitPolicyRuleGenerator;

    MachineLoadRuleGenerator machineLoadRuleGenerator;

    EnterpriseExclusionRuleGenerator enterpriseExclusionRuleGenerator;

    DatacenterGenerator datacenterGenerator;

    RackGenerator rackGenerator;

    MachineGenerator machineGenerator;

    EnterpriseGenerator enterpriseGenerator;

    @Override
    @BeforeMethod
    public void methodSetUp()
    {
        super.methodSetUp();
        this.fitPolicyRuleGenerator = new FitPolicyRuleGenerator(getSeed());
        this.machineLoadRuleGenerator = new MachineLoadRuleGenerator(getSeed());
        this.enterpriseExclusionRuleGenerator = new EnterpriseExclusionRuleGenerator(getSeed());
        this.datacenterGenerator = new DatacenterGenerator(getSeed());
        this.rackGenerator = new RackGenerator(getSeed());
        this.machineGenerator = new MachineGenerator(getSeed());
        this.enterpriseGenerator = new EnterpriseGenerator(getSeed());
    }

    @Test
    public void test_findAllMachineLoadRules() throws IllegalAccessException,
        InvocationTargetException, NoSuchMethodException
    {
        Datacenter dt = datacenterGenerator.createUniqueInstance();
        Rack rack = rackGenerator.createInstance(dt);
        Machine machine = machineGenerator.createMachine(dt, rack);

        MachineLoadRule dcRule = machineLoadRuleGenerator.createInstance(dt);
        MachineLoadRule rackrule = machineLoadRuleGenerator.createInstance(rack);
        MachineLoadRule machineRule = machineLoadRuleGenerator.createInstance(machine);

        ds().persistAll(dt, rack, machine, dcRule, rackrule, machineRule);

        RulesRep rep = new RulesRep(ds().createEntityManagerAndBeginRollbackTransaction());

        List<MachineLoadRule> result = rep.findAllMachineLoadRules();
        assertNotNull(result);
        assertEquals(result.size(), 3);
    }

    @Test
    public void test_deleteMachineLoadRule()
    {

        Datacenter dt = datacenterGenerator.createUniqueInstance();
        Rack rack = rackGenerator.createInstance(dt);
        Machine machine = machineGenerator.createMachine(dt, rack);

        MachineLoadRule dcRule = machineLoadRuleGenerator.createInstance(dt);
        MachineLoadRule rackrule = machineLoadRuleGenerator.createInstance(rack);
        MachineLoadRule machineRule = machineLoadRuleGenerator.createInstance(machine);

        ds().persistAll(dt, rack, machine, dcRule, rackrule, machineRule);

        EntityManager em = ds().createEntityManagerAndBeginReadWriteTransaction();

        RulesRep rep = new RulesRep(em);
        MachineLoadRule mlr1 = rep.findMachineLoadRuleById(machineRule.getId());
        MachineLoadRule mlr2 = rep.findMachineLoadRuleById(rackrule.getId());

        try
        {
            rep.deleteMachineLoadRule(mlr1);
            rep.deleteMachineLoadRule(mlr2);
            List<MachineLoadRule> result = rep.findAllMachineLoadRules();
            assertNotNull(result);
            assertEquals(result.size(), 1);
            EntityManagerHelper.commitAndClose(em);
        }
        catch (Exception e)
        {
            e.printStackTrace();
            fail();
        }
    }

    @Test
    public void test_findAllEnterpriseExclusionRules() throws IllegalAccessException,
        InvocationTargetException, NoSuchMethodException
    {

        EnterpriseExclusionRule eeRule1 = enterpriseExclusionRuleGenerator.createUniqueInstance();
        EnterpriseExclusionRule eeRule2 = enterpriseExclusionRuleGenerator.createUniqueInstance();
        EnterpriseExclusionRule eeRule3 = enterpriseExclusionRuleGenerator.createUniqueInstance();

        List<Object> allToPersist = new ArrayList<Object>();
        enterpriseExclusionRuleGenerator.addAuxiliaryEntitiesToPersist(eeRule1, allToPersist);
        enterpriseExclusionRuleGenerator.addAuxiliaryEntitiesToPersist(eeRule2, allToPersist);
        enterpriseExclusionRuleGenerator.addAuxiliaryEntitiesToPersist(eeRule3, allToPersist);

        persistAll(ds(), allToPersist, eeRule1, eeRule2, eeRule3);

        RulesRep rep = new RulesRep(ds().createEntityManagerAndBeginRollbackTransaction());

        List<EnterpriseExclusionRule> result = rep.findAllEnterpriseExclusionRules();
        assertNotNull(result);
        assertEquals(result.size(), 3);

    }

    @Test
    public void test_deleteEnterpriseExclusionRule()
    {
        EnterpriseExclusionRule eeRule1 = enterpriseExclusionRuleGenerator.createUniqueInstance();

        List<Object> allToPersist = new ArrayList<Object>();
        enterpriseExclusionRuleGenerator.addAuxiliaryEntitiesToPersist(eeRule1, allToPersist);

        persistAll(ds(), allToPersist, eeRule1);

        EntityManager em = ds().createEntityManagerAndBeginReadWriteTransaction();

        RulesRep rep = new RulesRep(em);
        EnterpriseExclusionRule eeRule = rep.findEnterpriseExclusionRuleById(eeRule1.getId());

        try
        {
            rep.deleteEnterpriseExclusionRule(eeRule);
            List<EnterpriseExclusionRule> result = rep.findAllEnterpriseExclusionRules();
            assertNotNull(result);
            assertEquals(result.size(), 0);
            EntityManagerHelper.commitAndClose(em);
        }
        catch (Exception e)
        {
            e.printStackTrace();
            fail();
        }

    }

    @Test
    public void test_findAllFitPolicyRules() throws IllegalAccessException,
        InvocationTargetException, NoSuchMethodException
    {

        FitPolicyRule fpr1 = fitPolicyRuleGenerator.createUniqueInstance();
        FitPolicyRule fpr2 = fitPolicyRuleGenerator.createUniqueInstance();

        List<Object> allToPersist = new ArrayList<Object>();
        fitPolicyRuleGenerator.addAuxiliaryEntitiesToPersist(fpr1, allToPersist);
        fitPolicyRuleGenerator.addAuxiliaryEntitiesToPersist(fpr2, allToPersist);

        persistAll(ds(), allToPersist, fpr1, fpr2);

        EntityManager em = ds().createEntityManagerAndBeginReadWriteTransaction();

        RulesRep rep = new RulesRep(ds().createEntityManagerAndBeginRollbackTransaction());

        List<FitPolicyRule> result = rep.findAllFitPolicyRules();
        assertNotNull(result);
        assertEquals(result.size(), 2);

    }

    @Test
    public void test_deleteFitPolicyRule()
    {
        FitPolicyRule fpr1 = fitPolicyRuleGenerator.createUniqueInstance();
        FitPolicyRule fpr2 = fitPolicyRuleGenerator.createUniqueInstance();

        List<Object> allToPersist = new ArrayList<Object>();
        fitPolicyRuleGenerator.addAuxiliaryEntitiesToPersist(fpr1, allToPersist);
        fitPolicyRuleGenerator.addAuxiliaryEntitiesToPersist(fpr2, allToPersist);

        persistAll(ds(), allToPersist, fpr1, fpr2);

        EntityManager em = ds().createEntityManagerAndBeginReadWriteTransaction();

        RulesRep rep = new RulesRep(em);
        FitPolicyRule fitPolicyRule = rep.findFitPolicyRuleById(fpr1.getId());

        try
        {
            rep.deleteFitPolicyRule(fitPolicyRule);
            List<FitPolicyRule> result = rep.findAllFitPolicyRules();
            assertNotNull(result);
            assertEquals(result.size(), 1);
            EntityManagerHelper.commitAndClose(em);
        }
        catch (Exception e)
        {
            e.printStackTrace();
            fail();
        }

    }

    @Test
    public void test_getMachineLoadForDatacenter()
    {
        Datacenter dt = datacenterGenerator.createUniqueInstance();
        MachineLoadRule mlr1 = machineLoadRuleGenerator.createInstance(dt);
        MachineLoadRule mlr2 = machineLoadRuleGenerator.createInstance(dt);

        ds().persistAll(dt, mlr1, mlr2);

        EntityManager em = ds().createEntityManagerAndBeginReadWriteTransaction();

        RulesRep rep = new RulesRep(em);

        List<MachineLoadRule> result = rep.getMachineLoadForDatacenter(dt.getId());

        assertNotNull(result);
        assertEquals(result.size(), 2);

    }

    @Test
    public void test_getFitPolicyForDatacenter()
    {
        FitPolicyRule fpr = fitPolicyRuleGenerator.createUniqueInstance();

        List<Object> allToPersist = new ArrayList<Object>();
        fitPolicyRuleGenerator.addAuxiliaryEntitiesToPersist(fpr, allToPersist);

        persistAll(ds(), allToPersist, fpr);

        Integer datacenterId = fpr.getDatacenter().getId();

        EntityManager em = ds().createEntityManagerAndBeginReadWriteTransaction();

        RulesRep rep = new RulesRep(em);

        FitPolicyRule result = rep.getFitPolicyForDatacenter(datacenterId);

        assertNotNull(result);
        assertEquals(result.getDatacenter().getId(), fpr.getDatacenter().getId());

    }

    @Test
    public void test_getGlobalRules()
    {
        FitPolicyRule fpr = fitPolicyRuleGenerator.createGlobalFitPolicyInstance();

        EnterpriseExclusionRule eeRule1 = enterpriseExclusionRuleGenerator.createUniqueInstance();
        EnterpriseExclusionRule eeRule2 = enterpriseExclusionRuleGenerator.createUniqueInstance();

        List<Object> allToPersist = new ArrayList<Object>();

        enterpriseExclusionRuleGenerator.addAuxiliaryEntitiesToPersist(eeRule1, allToPersist);
        enterpriseExclusionRuleGenerator.addAuxiliaryEntitiesToPersist(eeRule2, allToPersist);

        persistAll(ds(), allToPersist, eeRule1, eeRule2, fpr);

        EntityManager em = ds().createEntityManagerAndBeginReadWriteTransaction();

        RulesRep rep = new RulesRep(em);

        List<PersistentRule> result = rep.getGlobalRules();

        assertNotNull(result);
        assertEquals(result.size(), 3);

    }

    @Test
    public void test_getGlobalFitPolicy()
    {
        FitPolicyRule fpr = fitPolicyRuleGenerator.createGlobalFitPolicyInstance();
        List<Object> allToPersist = new ArrayList<Object>();

        persistAll(ds(), allToPersist, fpr);

        EntityManager em = ds().createEntityManagerAndBeginReadWriteTransaction();
        RulesRep rep = new RulesRep(em);
        FitPolicyRule result = rep.getGlobalFitPolicy();
        assertNotNull(result);
    }

}
