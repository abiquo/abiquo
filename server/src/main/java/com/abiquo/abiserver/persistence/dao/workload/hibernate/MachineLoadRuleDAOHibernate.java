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

import java.util.Collection;
import java.util.List;

import org.hibernate.Query;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.abiquo.abiserver.business.hibernate.pojohb.infrastructure.PhysicalmachineHB;
import com.abiquo.abiserver.business.hibernate.pojohb.workload.MachineLoadRuleHB;
import com.abiquo.abiserver.persistence.dao.workload.MachineLoadRuleDAO;
import com.abiquo.abiserver.persistence.hibernate.HibernateDAO;

public class MachineLoadRuleDAOHibernate extends HibernateDAO<MachineLoadRuleHB, Integer> implements
    MachineLoadRuleDAO
{
    /** The logger. */
    private final static Logger LOGGER = LoggerFactory.getLogger(MachineLoadRuleDAOHibernate.class);

    private final static String FIST_PASS_CANDIDATE_RULES =
        "MACHINE_LOAD_RULE.FIST_PASS_CANDIDATE_RULES";

    @Override
    public List<MachineLoadRuleHB> getRulesForDatacenter(final Integer idDatacenter)
    {
        return findByProperty("datacenter.idDataCenter", idDatacenter);
    }

    @Override
    public List<MachineLoadRuleHB> getRulesForRack(final Integer idRack)
    {
        return findByProperty("rack.idRack", idRack);
    }

    @Override
    public List<MachineLoadRuleHB> getRulesForMachine(final Integer idPhysicalMachine)
    {
        return findByProperty("machine.idPhysicalMachine", idPhysicalMachine);
    }

    @Override
    public void deleteRulesForDatacenter(final Integer idDatacenter)
    {
        LOGGER.info("Deleting allocation rules for datacenter {}", idDatacenter);

        List<MachineLoadRuleHB> datacenterRules = getRulesForDatacenter(idDatacenter);
        deleteRules(datacenterRules);
    }

    @Override
    public void deleteRulesForRack(final Integer idRack)
    {
        LOGGER.info("Deleting allocation rules for rack {}", idRack);

        List<MachineLoadRuleHB> rackRules = getRulesForRack(idRack);
        deleteRules(rackRules);
    }

    @Override
    public void deleteRulesForMachine(final Integer idPhysicalMachine)
    {
        LOGGER.info("Deleting allocation rules for physical machine {}", idPhysicalMachine);

        List<MachineLoadRuleHB> machineRules = getRulesForMachine(idPhysicalMachine);
        deleteRules(machineRules);
    }

    @Override
    @SuppressWarnings("unchecked")
    public List<MachineLoadRuleHB> findCandidateMachineRules(
        Collection<PhysicalmachineHB> firstPassCandidateMachines)
    {
        Query query = getSession().getNamedQuery(FIST_PASS_CANDIDATE_RULES);

        query.setParameterList("machines", firstPassCandidateMachines);
        List<MachineLoadRuleHB> candidateMachineLoadRules = query.list();

        return candidateMachineLoadRules;
    }

    /**
     * Delete the given list of rules.
     * 
     * @param rules The rules to delete.
     */
    private void deleteRules(List<MachineLoadRuleHB> rules)
    {
        for (MachineLoadRuleHB rule : rules)
        {
            makeTransient(rule);
        }
    }
}
