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

package com.abiquo.scheduler;

import static junit.framework.Assert.assertNotNull;
import static junit.framework.Assert.assertNotSame;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.testng.Assert;

import com.abiquo.server.core.enterprise.Enterprise;
import com.abiquo.server.core.enterprise.EnterpriseRep;
import com.abiquo.server.core.infrastructure.Datacenter;
import com.abiquo.server.core.infrastructure.DatacenterRep;
import com.abiquo.server.core.infrastructure.Machine;
import com.abiquo.server.core.infrastructure.Rack;
import com.abiquo.server.core.scheduler.EnterpriseExclusionRule;
import com.abiquo.server.core.scheduler.EnterpriseExclusionRuleDAO;
import com.abiquo.server.core.scheduler.FitPolicyRule;
import com.abiquo.server.core.scheduler.FitPolicyRule.FitPolicy;
import com.abiquo.server.core.scheduler.FitPolicyRuleDAO;
import com.abiquo.server.core.scheduler.MachineLoadRule;
import com.abiquo.server.core.scheduler.MachineLoadRuleDAO;

@Component
@Transactional
public class PopulateRules extends PopulateConstants
{
    @Autowired
    DatacenterRep dcRep;

    @Autowired
    EnterpriseRep enterRep;

    @Autowired
    EnterpriseExclusionRuleDAO exclusionDao;

    @Autowired
    FitPolicyRuleDAO fitDao;

    @Autowired
    MachineLoadRuleDAO loadDao;

    // EnterpriseExclusionRuleGenerator exclusionGen;

    /**
     * <ul>
     * <li>rule.fit.d1:PERFORM/PROGRESS # rule.fit:default:PROGRESS
     * <li>rule.exclusion.e1:e2
     * <li>rule.reserved.e1:m1
     * <li>rule.load.m1:100,100
     * <li>rule.load.r1:100,100
     * <li>rule.load.d1:100,100
     * </ul>
     */
    public void createRule(String rule)
    {
        String[] frg = rule.split(DELIMITER_ENTITIES);

        Assert.assertTrue(frg.length == 3, "Invalid rule declaration " + rule);
        Assert.assertTrue(frg[0].equalsIgnoreCase(DEC_RULE), "Invalid rule declaration " + rule);

        String ruleType = frg[1];

        String[] frags = frg[2].split(DELIMITER_DEFINITION);
        Assert.assertTrue(frags.length == 2, "Invalid rule declaration " + rule);

        if (ruleType.equalsIgnoreCase(RULE_FIT))
        {
            createFit(frags[0], frags[1]);
        }
        else if (ruleType.equalsIgnoreCase(RULE_EXCLUSION))
        {
            createAffinity(frags[0], frags[1]);
        }
        else if (ruleType.equalsIgnoreCase(RULE_RESERVED))
        {
            createReserved(frags[0], frags[1]);
        }
        else if (ruleType.equalsIgnoreCase(RULE_LOAD))
        {
            String entity = frags[0];
            frags = frags[1].split(DELIMITER_ATTRIBUTES);
            createLoad(entity, Integer.parseInt(frags[0]), Integer.parseInt(frags[1]));
        }
        else
        {
            throw new PopulateException("Invalid rule definition " + rule);
        }
    }

    private void createAffinity(String e1, String e2)
    {
        assertNotSame(e1, e2);

        Enterprise enter1 = enterRep.findByName(e1);
        Enterprise enter2 = enterRep.findByName(e2);

        assertNotNull("Enterprise not found " + e1, enter1);
        assertNotNull("Enterprise not found " + e2, enter2);

        EnterpriseExclusionRule exclusionRule = new EnterpriseExclusionRule(enter1, enter2);
        exclusionDao.persist(exclusionRule);
    }

    private void createReserved(String enterprise, String machine)
    {
        Enterprise enterp = enterRep.findByName(enterprise);
        assertNotNull("Enterprise not found " + enterprise, enterp);

        Machine mach = dcRep.findMachineByName(machine);
        assertNotNull("Machine not found " + machine, mach);

        mach.setEnterprise(enterp);

        dcRep.updateMachine(mach);
    }

    /**
     * fit.d1:PERFORM/PROGRESS // fit.default:PROGRESS
     */
    private void createFit(String datacenterName, String fitPolicy)
    {
        FitPolicyRule fitrule;

        FitPolicy fit = FitPolicy.valueOf(fitPolicy);

        if (datacenterName.equalsIgnoreCase("default"))
        {
            fitrule = new FitPolicyRule(fit);
        }
        else
        {
            Datacenter dc = dcRep.findByName(datacenterName);

            assertNotNull("Datacenter not found " + datacenterName, dc);

            fitrule = new FitPolicyRule(dc, fit);
        }

        fitDao.persist(fitrule);
    }

    private void createLoad(String entity, int cpu, int ram)
    {
        MachineLoadRule mlr;

        if (entity.startsWith("d"))
        {
            Datacenter dc = dcRep.findByName(entity);

            assertNotNull("Datacenter not found " + entity, dc);

            mlr = new MachineLoadRule(dc, cpu, ram);
        }
        else if (entity.startsWith("r"))
        {
            Rack rack = dcRep.findRackByName(entity);

            assertNotNull("Rack not found " + entity, rack);

            mlr = new MachineLoadRule(rack, cpu, ram);
        }
        else if (entity.startsWith("m"))
        {
            Machine machine = dcRep.findMachineByName(entity);

            assertNotNull("Machine not found " + entity, machine);

            mlr = new MachineLoadRule(machine, cpu, ram);
        }
        else
        {
            throw new IllegalArgumentException("Invalid load rule entity : " + entity);
        }

        loadDao.persist(mlr);
    }
}
