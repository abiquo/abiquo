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

import java.util.List;

import javax.persistence.EntityManager;

import org.hibernate.Criteria;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.Restrictions;
import org.springframework.stereotype.Repository;

import com.abiquo.server.core.common.persistence.DefaultDAOBase;
import com.abiquo.server.core.infrastructure.Datacenter;

@Repository("jpaFitPolicyRuleDAO")
public class FitPolicyRuleDAO extends DefaultDAOBase<Integer, FitPolicyRule>
{
    public FitPolicyRuleDAO()
    {
        super(FitPolicyRule.class);
    }

    public FitPolicyRuleDAO(EntityManager entityManager)
    {
        super(FitPolicyRule.class, entityManager);
    }

    /**
     * Gets the global {@link FitPolicyRuleHB}.
     * 
     * @return The global <code>FitPolicyRuleHB</code>.
     */
    public FitPolicyRule getGlobalFitPolicy()
    {
        Criteria criteria = getSession().createCriteria(FitPolicyRule.class);
        Criterion noDatacenter = Restrictions.isNull(FitPolicyRule.DATACENTER_PROPERTY);
        criteria.add(noDatacenter);

        FitPolicyRule result = getSingleResult(criteria);

        return result;
    }

    /**
     * Gets the global {@link FitPolicyRuleHB}.
     * 
     * @return The global <code>FitPolicyRuleHB</code>.
     */
    public List<FitPolicyRule> getDatacenterFitPolicies()
    {
        Criteria criteria = getSession().createCriteria(FitPolicyRule.class);
        Criterion anyDatacenter = Restrictions.isNotNull(FitPolicyRule.DATACENTER_PROPERTY);
        criteria.add(anyDatacenter);

        // criteria.addOrder(Order.asc(FitPolicyRule.ID_PROPERTY));

        List<FitPolicyRule> result = getResultList(criteria);

        return result;

    }

    /**
     * Returns the {@link FitPolicyRuleHB} that applies to the given datacenter.
     * 
     * @param idDatacenter The id of the datacenter.
     * @return The <code>FitPolicyRuleHB</code> that applies to the given datacenter.
     */
    public FitPolicyRule getFitPolicyForDatacenter(final Integer idDatacenter)
    {
        Criteria crit = createNestedCriteria(FitPolicyRule.DATACENTER_PROPERTY);
        crit.add(Restrictions.eq(Datacenter.ID_PROPERTY, idDatacenter));

        return (FitPolicyRule) crit.uniqueResult();
    }
}
