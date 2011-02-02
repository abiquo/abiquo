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

package com.abiquo.abiserver.business.hibernate.pojohb.workload.engine;

import org.testng.annotations.Test;

import com.abiquo.abiserver.persistence.DataAccessTestBase;

@Test
public class PhysicalmachineRuleFinderTest extends DataAccessTestBase
{
//    @Test
//    public void test_chooseHigherPrecedenceMachineLoadRules() {
//        Session session = createSessionInTransaction();
//        
//        DatacenterHB datacenter1 = TestEntityGenerationUtils.createDatacenter("datacenter1");
//        DatacenterHB datacenter2 = TestEntityGenerationUtils.createDatacenter("datacenter2");
//        DatacenterHB unusedDatacenter = TestEntityGenerationUtils.createDatacenter("unusedDatacenter");
//        RackHB rack1_1 = TestEntityGenerationUtils.createRack(datacenter1, "rack1_1");
//        RackHB rack1_2 = TestEntityGenerationUtils.createRack(datacenter1, "rack1_2");
//        RackHB rack2_1 = TestEntityGenerationUtils.createRack(datacenter2, "rack2_1");
//        RackHB unusedRack = TestEntityGenerationUtils.createRack(unusedDatacenter, "rack1_2_noMachine");
//        PhysicalmachineHB machine1_1_1 = TestEntityGenerationUtils.createMachine(rack1_1, "machine1_1_1");
//        PhysicalmachineHB machine1_1_2 = TestEntityGenerationUtils.createMachine(rack1_1, "machine1_1_2");
//        PhysicalmachineHB machine1_1_3 = TestEntityGenerationUtils.createMachine(rack1_2, "machine1_1_3");
//        PhysicalmachineHB machine1_1_4 = TestEntityGenerationUtils.createMachine(unusedRack, "machine1_1_3");
//        PhysicalmachineHB machine2_1_1 = TestEntityGenerationUtils.createMachine(rack2_1, "machine1_1_3");
//        PhysicalmachineHB unusedMachine = TestEntityGenerationUtils.createMachine(unusedRack, "unusedMachine");
//        List<PhysicalmachineHB> candidateMachines = new ArrayList<PhysicalmachineHB>();
//
//        MachineLoadRuleHB rule1 = new MachineLoadRuleHB(datacenter1, rack1_1, machine1_1_1);
//        MachineLoadRuleHB rule2 = new MachineLoadRuleHB(datacenter1, rack1_1, null);
//        MachineLoadRuleHB rule3 = new MachineLoadRuleHB(datacenter1, null, null);
//        MachineLoadRuleHB rule4 = new MachineLoadRuleHB(datacenter2, rack2_1, null);
//        SessionUtils.saveAndFlush(session, datacenter1, datacenter2, unusedDatacenter, rack1_1, rack1_2, rack2_1, unusedRack, 
//            machine1_1_1, machine1_1_2, machine1_1_3, machine1_1_4, machine2_1_1, unusedMachine, rule1, rule2, rule3, rule4);
//        
//        List<MachineLoadRuleHB> rules = new ArrayList<MachineLoadRuleHB>();
//        Collections.addAll( rules, rule1, rule2, rule3, rule4 );
//        
//        candidateMachines.add(machine2_1_1);
//        candidateMachines.add(machine1_1_1);
//        candidateMachines.add(machine1_1_2);
//        candidateMachines.add(machine1_1_3);
//        candidateMachines.add(machine1_1_4);
//
//        PhysicalmachineRuleFinder finder = new PhysicalmachineRuleFinder(session, candidateMachines);
//        Map<PhysicalmachineHB, List<MachineLoadRuleHB>> result = finder.chooseHigherPrecedenceMachineLoadRules(candidateMachines, rules);
//        // Test a machine that has direct rules at machine, rack and datacenter level: only machine level ones must be selected
//        AssertUtils.assertContainsAllAndOnly(result.get(machine1_1_1), rule1);
//        // Test a machine that has indirect rules at rack and datacenter level: only rack level ones must be selected
//        AssertUtils.assertContainsAllAndOnly(result.get(machine1_1_2), rule2);
//        // Test a machine that has indirect rules at datacenter level: only datacenter level ones must be selected
//        AssertUtils.assertContainsAllAndOnly(result.get(machine1_1_3), rule3);
//        // Test a machine that has no rules -direct or indirect
//        Assert.assertTrue(result.get(machine1_1_4).isEmpty());
//        // Test a machine that is simply not a candidate
//        Assert.assertNull(result.get(unusedMachine));
//        // Test a machine that hangs from a different datacenter/rack
//        AssertUtils.assertContainsAllAndOnly(result.get(machine2_1_1), rule4);
//    }
//    
//    @Test
//    public void test_findCandidateMachineLoadRules() {
//        Session session = createSessionInTransaction();
//        
//        DatacenterHB datacenter1 = TestEntityGenerationUtils.createDatacenter("datacenter1");
//        DatacenterHB datacenter2 = TestEntityGenerationUtils.createDatacenter("datacenter2");
//        DatacenterHB unusedDatacenter = TestEntityGenerationUtils.createDatacenter("unusedDatacenter");
//        RackHB rack1_1 = TestEntityGenerationUtils.createRack(datacenter1, "rack1_1");
//        RackHB rack1_2 = TestEntityGenerationUtils.createRack(datacenter1, "rack1_2");
//        RackHB rack2_1 = TestEntityGenerationUtils.createRack(datacenter2, "rack2_1");
//        RackHB unusedRack = TestEntityGenerationUtils.createRack(unusedDatacenter, "rack1_2_noMachine");
//        PhysicalmachineHB machine1_1_1 = TestEntityGenerationUtils.createMachine(rack1_1, "machine1_1_1");
//        PhysicalmachineHB machine1_1_2 = TestEntityGenerationUtils.createMachine(rack1_1, "machine1_1_2");
//        PhysicalmachineHB machine1_1_3 = TestEntityGenerationUtils.createMachine(rack1_2, "machine1_1_3");
//        PhysicalmachineHB machine1_1_4 = TestEntityGenerationUtils.createMachine(unusedRack, "machine1_1_3");
//        PhysicalmachineHB machine2_1_1 = TestEntityGenerationUtils.createMachine(rack2_1, "machine1_1_3");
//        PhysicalmachineHB unusedMachine = TestEntityGenerationUtils.createMachine(unusedRack, "unusedMachine");
//        MachineLoadRuleHB rule1 = new MachineLoadRuleHB(datacenter1, rack1_1, machine1_1_1);
//        MachineLoadRuleHB rule2 = new MachineLoadRuleHB(datacenter1, rack1_1, null);
//        MachineLoadRuleHB rule3 = new MachineLoadRuleHB(datacenter1, null, null);
//        MachineLoadRuleHB rule4 = new MachineLoadRuleHB(datacenter2, rack2_1, null);
//        SessionUtils.saveAndFlush(session, datacenter1, datacenter2, unusedDatacenter, rack1_1, rack1_2, rack2_1, unusedRack, 
//            machine1_1_1, machine1_1_2, machine1_1_3, machine1_1_4, machine2_1_1, unusedMachine, rule1, rule2, rule3, rule4);
//        
//        List<PhysicalmachineHB> candidateMachines = new ArrayList<PhysicalmachineHB>();
//        Collections.addAll( candidateMachines, machine1_1_1);
//
//        PhysicalmachineRuleFinder finder = new PhysicalmachineRuleFinder(session, candidateMachines);
//        List<MachineLoadRuleHB> result = finder.findCandidateMachineLoadRules(session, candidateMachines);
//        AssertUtils.assertContainsAllAndOnly(result, rule1, rule2, rule3);
//        
//        candidateMachines.clear();
//        candidateMachines.add(machine2_1_1);
//        result = finder.findCandidateMachineLoadRules(session, candidateMachines);
//        AssertUtils.assertContainsAllAndOnly(result, rule4);
//
//        candidateMachines.clear();
//        candidateMachines.add(unusedMachine);
//        Assert.assertTrue( finder.findCandidateMachineLoadRules(session, candidateMachines).isEmpty() );
//
//        candidateMachines.clear();
//        Collections.addAll( candidateMachines, machine1_1_1, machine2_1_1);
//        result = finder.findCandidateMachineLoadRules(session, candidateMachines);
//        AssertUtils.assertContainsAllAndOnly(result, rule1, rule2, rule3, rule4);
//    }
}
