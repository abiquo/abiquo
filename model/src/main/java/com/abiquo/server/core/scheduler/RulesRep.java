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

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.persistence.EntityManager;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.abiquo.server.core.common.DefaultRepBase;
import com.abiquo.server.core.enterprise.EnterpriseDto;

@Repository
public class RulesRep extends DefaultRepBase
{

    @Autowired
    private EnterpriseExclusionRuleDAO enterpriseExclusionRuleDao;

    @Autowired
    private FitPolicyRuleDAO fitPolicyRuleDao;

    @Autowired
    private MachineLoadRuleDAO machineLoadRuleDao;

    public RulesRep()
    {

    }

    public RulesRep(final EntityManager entityManager)
    {
        this.entityManager = entityManager;

        this.enterpriseExclusionRuleDao = new EnterpriseExclusionRuleDAO(entityManager);
        this.fitPolicyRuleDao = new FitPolicyRuleDAO(entityManager);
        this.machineLoadRuleDao = new MachineLoadRuleDAO(entityManager);
    }

    /**
     * @return List<PersistentRule>
     */
    public List<PersistentRule> getGlobalRules()
    {
        List<PersistentRule> rules = new ArrayList<PersistentRule>();

        // Get global rules
        FitPolicyRule globalFitPolicyRule = fitPolicyRuleDao.getGlobalFitPolicy();
        List<EnterpriseExclusionRule> enterpriseExclusionRules =
            enterpriseExclusionRuleDao.findAll();

        rules.add(globalFitPolicyRule);
        rules.addAll(enterpriseExclusionRules);
        return rules;
    }

    /**
     * @param idDatacenter
     * @return List<PersistentRule>
     */
    public List<PersistentRule> getDatacenterRules(final Integer idDatacenter)
    {
        List<PersistentRule> rules = new ArrayList<PersistentRule>();

        // Get datacenter rules
        FitPolicyRule fitPolicyRule = fitPolicyRuleDao.getFitPolicyForDatacenter(idDatacenter);
        List<MachineLoadRule> machineRules = machineLoadRuleDao.getRulesForDatacenter(idDatacenter);

        rules.add(fitPolicyRule);
        rules.addAll(machineRules);

        return rules;
    }

    public List<EnterpriseExclusionRule> findAllEnterpriseExclusionRules()
    {
        return enterpriseExclusionRuleDao.findAll();
    }

    public EnterpriseExclusionRule findEnterpriseExclusionRuleById(final Integer id)
    {
        return this.enterpriseExclusionRuleDao.findById(id);
    }

    public void deleteEnterpriseExclusionRule(final EnterpriseExclusionRule enterpriseExclusionRule)
    {
        if (enterpriseExclusionRule != null)
        {
            enterpriseExclusionRuleDao.remove(enterpriseExclusionRule);
            enterpriseExclusionRuleDao.flush();
        }

    }

    public EnterpriseExclusionRule insertEnterpriseExclusionRule(final EnterpriseExclusionRule rule)
    {
        enterpriseExclusionRuleDao.persist(rule);
        enterpriseExclusionRuleDao.flush();
        return rule;
    }

    public List<FitPolicyRule> findAllFitPolicyRules()
    {
        return fitPolicyRuleDao.findAll();
    }

    public FitPolicyRule findFitPolicyRuleById(final Integer id)
    {
        return this.fitPolicyRuleDao.findById(id);
    }

    public FitPolicyRule insertFitPolicyRule(final FitPolicyRule rule)
    {
        fitPolicyRuleDao.persist(rule);
        fitPolicyRuleDao.flush();
        return rule;
    }

    public List<MachineLoadRule> findAllMachineLoadRules()
    {
        return machineLoadRuleDao.findAll();
    }

    public MachineLoadRule findMachineLoadRuleById(final Integer id)
    {
        return this.machineLoadRuleDao.findById(id);
    }

    public List<MachineLoadRule> findMachineLoadRuleByMachine(final Integer id)
    {
        return this.machineLoadRuleDao.findByMachine(id);
    }

    public void deleteMachineLoadRule(final MachineLoadRule machineLoadRule)
    {
        if (machineLoadRule != null)
        {
            machineLoadRuleDao.remove(machineLoadRule);
            machineLoadRuleDao.flush();
        }
    }

    public MachineLoadRule insertMachineLoadRule(final MachineLoadRule rule)
    {
        machineLoadRuleDao.persist(rule);
        machineLoadRuleDao.flush();
        return rule;
    }

    public Set<EnterpriseDto> findEnterprises(final EnterpriseExclusionRule eeR)
    {
        Set<EnterpriseDto> enterprises = new HashSet<EnterpriseDto>();

        for (EnterpriseDto enterprise : findEnterprises(eeR))
        {
            enterprises.add(enterprise);
        }

        return enterprises;
    }

    public void deleteFitPolicyRule(final FitPolicyRule fitPolicyRule)
    {
        if (fitPolicyRule != null)
        {
            fitPolicyRuleDao.remove(fitPolicyRule);
            fitPolicyRuleDao.flush();
        }

    }

    public FitPolicyRule getFitPolicyForDatacenter(final Integer datacenterId)
    {
        FitPolicyRule fpr = fitPolicyRuleDao.getFitPolicyForDatacenter(datacenterId);
        return fpr;
    }

    public FitPolicyRule getGlobalFitPolicy()
    {
        FitPolicyRule fpr = fitPolicyRuleDao.getGlobalFitPolicy();
        return fpr;
    }

    public List<MachineLoadRule> getMachineLoadForDatacenter(final Integer datacenterId)
    {
        List<MachineLoadRule> mlrList = machineLoadRuleDao.getRulesForDatacenter(datacenterId);
        return mlrList;
    }

}
