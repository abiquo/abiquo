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

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.abiquo.abiserver.business.hibernate.pojohb.workload.FitPolicyRuleHB;
import com.abiquo.abiserver.persistence.dao.workload.FitPolicyRuleDAO;
import com.abiquo.abiserver.persistence.hibernate.HibernateDAO;

public class FitPolicyRuleDAOHibernate extends HibernateDAO<FitPolicyRuleHB, Integer> implements
    FitPolicyRuleDAO
{
    /** The logger. */
    private final static Logger LOGGER = LoggerFactory.getLogger(FitPolicyRuleDAOHibernate.class);

    private static final String GLOBAL_FITPOLICY_RULE_QUERY = "WORKLOAD.GLOBAL_FITPOLICY_RULE";

    private static final String DATACENTER_FITPOLICY_RULES = "WORKLOAD.DATACENTER_FITPOLICY_RULES";

    @Override
    public FitPolicyRuleHB getGlobalFitPolicy()
    {
        return findUniqueByNamedQuery(GLOBAL_FITPOLICY_RULE_QUERY);
    }

    @Override
    public List<FitPolicyRuleHB> getDatacenterFitPolicies()
    {
        return findByNamedQuery(DATACENTER_FITPOLICY_RULES);
    }

    @Override
    public FitPolicyRuleHB getFitPolicyForDatacenter(final Integer idDatacenter)
    {
        List<FitPolicyRuleHB> rules = findByProperty("datacenter.idDataCenter", idDatacenter);
        return rules == null || rules.isEmpty() ? null : rules.get(0);
    }

    @Override
    public void deleteRulesForDatacenter(Integer idDatacenter)
    {
        LOGGER.info("Deleting allocation rules for datacenter {}", idDatacenter);

        List<FitPolicyRuleHB> rules = findByProperty("datacenter.idDataCenter", idDatacenter);

        for (FitPolicyRuleHB rule : rules)
        {
            makeTransient(rule);
        }
    }
}
