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

package com.abiquo.abiserver.persistence.dao.workload.hibernate;

import junit.framework.Assert;

import org.hibernate.Session;
import org.testng.annotations.Test;

import com.abiquo.abiserver.business.hibernate.pojohb.infrastructure.DatacenterHB;
import com.abiquo.abiserver.business.hibernate.pojohb.infrastructure.HypervisorHB;
import com.abiquo.abiserver.business.hibernate.pojohb.infrastructure.PhysicalmachineHB;
import com.abiquo.abiserver.business.hibernate.pojohb.infrastructure.RackHB;
import com.abiquo.abiserver.business.hibernate.pojohb.user.EnterpriseHB;
import com.abiquo.abiserver.business.hibernate.pojohb.virtualappliance.VirtualmachineHB;
import com.abiquo.abiserver.business.hibernate.pojohb.workload.EnterpriseExclusionRuleHB;
import com.abiquo.abiserver.exception.PersistenceException;
import com.abiquo.abiserver.persistence.AssertUtils;
import com.abiquo.abiserver.persistence.DataAccessTestBase;
import com.abiquo.abiserver.persistence.SessionUtils;
import com.abiquo.abiserver.persistence.TestDAOHelper;
import com.abiquo.abiserver.persistence.TestEntityGenerationUtils;
import com.abiquo.abiserver.persistence.dao.workload.EnterpriseExclusionRuleDAO;
import com.abiquo.model.enumerator.HypervisorType;

@Test
public class EnterpriseExclusionRuleDAOTest extends DataAccessTestBase
{
    public EnterpriseExclusionRuleDAOTest()
    {

    }

    @Test
    public void test_findExcludedEnterprises()
    {
        Session session = createSessionInTransaction();

        EnterpriseHB enterprise1 = TestEntityGenerationUtils.createEnterprise("enterprise1");
        EnterpriseHB enterprise2 = TestEntityGenerationUtils.createEnterprise("enterprise2");
        EnterpriseHB enterprise3 = TestEntityGenerationUtils.createEnterprise("enterprise3");
        EnterpriseHB enterprise4 = TestEntityGenerationUtils.createEnterprise("enterprise4");
        EnterpriseExclusionRuleHB rule1 = new EnterpriseExclusionRuleHB(enterprise1, enterprise2);
        EnterpriseExclusionRuleHB rule2 = new EnterpriseExclusionRuleHB(enterprise3, enterprise1);

        SessionUtils.saveAndFlush(session, enterprise1, enterprise2, enterprise3, enterprise4,
            rule1, rule2);

        EnterpriseExclusionRuleDAO dao = TestDAOHelper.createEnterpriseExclusionRuleDAO(session);
        AssertUtils.assertContainsAllAndOnly(dao.findExcludedEnterprises(enterprise1), enterprise2,
            enterprise3);
        AssertUtils.assertContainsAllAndOnly(dao.findExcludedEnterprises(enterprise2), enterprise1);
        AssertUtils.assertContainsAllAndOnly(dao.findExcludedEnterprises(enterprise3), enterprise1);
        AssertUtils.assertContainsAllAndOnly(dao.findExcludedEnterprises(enterprise4));
    }

    // Execute a useless query that at least proves the query is well formed, independently of
    // whether it does what we want or not
    @Test
    public void test_findMachinesWithVMsFromExcludedEnterprises_queryIsValidJQL()
    {
        Session session = createSessionInTransaction();

        EnterpriseHB enterprise1 = TestEntityGenerationUtils.createEnterprise("enterprise1");
        SessionUtils.saveAndFlush(session, enterprise1);

        EnterpriseExclusionRuleDAO dao = TestDAOHelper.createEnterpriseExclusionRuleDAO(session);
        Assert.assertTrue(dao.findMachinesWithVMsFromExcludedEnterprises(enterprise1).isEmpty());
    }

    @Test
    public void test_findMachinesWithVMsFromExcludedEnterprises()
    {
        Session session = createSessionInTransaction();

        DatacenterHB datacenter = TestEntityGenerationUtils.createDatacenter("datacenter1");
        RackHB rack = TestEntityGenerationUtils.createRack(datacenter, "rack1");
        PhysicalmachineHB machine = TestEntityGenerationUtils.createMachine(rack, "machine1");
        HypervisorHB hypervisor =
            TestEntityGenerationUtils.createHypervisor("hypervisorDesc", machine,
                HypervisorType.VBOX);

        EnterpriseHB enterprise = TestEntityGenerationUtils.createEnterprise("enterprise1");
        EnterpriseHB enterprise2 = TestEntityGenerationUtils.createEnterprise("enterprise2");
        EnterpriseHB enterprise3 = TestEntityGenerationUtils.createEnterprise("enterprise3");
        SessionUtils.saveAndFlush(session, datacenter, rack, machine, hypervisor, enterprise,
            enterprise2, enterprise3);

        // We have a machine that might fit, but...it has no VMs => not returned
        EnterpriseExclusionRuleDAO dao = TestDAOHelper.createEnterpriseExclusionRuleDAO(session);
        Assert.assertTrue(dao.findMachinesWithVMsFromExcludedEnterprises(enterprise).isEmpty());

        // We have a machine with a VM, but does not belong to 'excluded' enterprise
        VirtualmachineHB vm = TestEntityGenerationUtils.createVirtualmachine("vm1");
        vm.setHypervisor(hypervisor);
        vm.setEnterpriseHB(enterprise2);
        SessionUtils.saveAndFlush(session, vm);
        Assert.assertTrue(dao.findMachinesWithVMsFromExcludedEnterprises(enterprise).isEmpty());

        // We have a machine with a VM, but belongs to 'excluded' enterprise: return it
        EnterpriseExclusionRuleHB rule = new EnterpriseExclusionRuleHB(enterprise, enterprise2);
        SessionUtils.saveAndFlush(session, rule);
        AssertUtils.assertContainsAllAndOnly(dao
            .findMachinesWithVMsFromExcludedEnterprises(enterprise), machine);

        // We add another machine, associated to a non-excluded enterprise: do not return it
        PhysicalmachineHB machine2 = TestEntityGenerationUtils.createMachine(rack, "machine2");
        VirtualmachineHB vm2 = TestEntityGenerationUtils.createVirtualmachine("vm2");
        HypervisorHB hypervisor2 =
            TestEntityGenerationUtils.createHypervisor("hypervisor2Desc", machine2,
                HypervisorType.VMX_04);
        vm2.setHypervisor(hypervisor2);
        vm2.setEnterpriseHB(enterprise3);
        SessionUtils.saveAndFlush(session, machine2, hypervisor2, vm2);
        AssertUtils.assertContainsAllAndOnly(dao
            .findMachinesWithVMsFromExcludedEnterprises(enterprise), machine);

        // Exclude enterprise to which machine2 belongs: then it will be one of the forbidden
        // machines
        EnterpriseExclusionRuleHB rule2 = new EnterpriseExclusionRuleHB(enterprise3, enterprise);
        SessionUtils.saveAndFlush(session, rule2);
        AssertUtils.assertContainsAllAndOnly(dao
            .findMachinesWithVMsFromExcludedEnterprises(enterprise), machine, machine2);

        // Remove VM deployed in machine2: it will not appear in query
        session.delete(vm);
        session.flush();
        AssertUtils.assertContainsAllAndOnly(dao
            .findMachinesWithVMsFromExcludedEnterprises(enterprise), machine2);
    }

    /** This test makes completely sure we have everything well worked out */
    @Test
    public void test_save() throws PersistenceException
    {
        Session session = createSessionInTransaction();
        EnterpriseHB enterprise1 = TestEntityGenerationUtils.createEnterprise("enterprise1");
        EnterpriseHB enterprise2 = TestEntityGenerationUtils.createEnterprise("enterprise2");
        EnterpriseExclusionRuleHB rule = new EnterpriseExclusionRuleHB(enterprise1, enterprise2);

        EnterpriseExclusionRuleDAO dao = TestDAOHelper.createEnterpriseExclusionRuleDAO(session);
        SessionUtils.saveAndFlush(session, enterprise1, enterprise2);
        dao.makePersistent(rule);
        session.flush();

        Assert.assertTrue(SessionUtils.entityExists(session, EnterpriseExclusionRuleHB.class, rule
            .getId()));
    }

}
