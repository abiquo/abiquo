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

package com.abiquo.abiserver.persistence.dao.workload;

import java.util.List;

import com.abiquo.abiserver.business.hibernate.pojohb.workload.FitPolicyRuleHB;
import com.abiquo.abiserver.persistence.DAO;

public interface FitPolicyRuleDAO extends DAO<FitPolicyRuleHB, Integer>
{
    /**
     * Gets the global {@link FitPolicyRuleHB}.
     * 
     * @return The global <code>FitPolicyRuleHB</code>.
     */
    public FitPolicyRuleHB getGlobalFitPolicy();

    /**
     * Gets the global {@link FitPolicyRuleHB}.
     * 
     * @return The global <code>FitPolicyRuleHB</code>.
     */
    public List<FitPolicyRuleHB> getDatacenterFitPolicies();

    /**
     * Returns the {@link FitPolicyRuleHB} that applies to the given datacenter.
     * 
     * @param idDatacenter The id of the datacenter.
     * @return The <code>FitPolicyRuleHB</code> that applies to the given datacenter.
     */
    public FitPolicyRuleHB getFitPolicyForDatacenter(final Integer idDatacenter);

    /**
     * Delete the rules for the given detacenter.
     * 
     * @param idDatacenter The id of the datacenter.
     */
    public void deleteRulesForDatacenter(Integer idDatacenter);
}
