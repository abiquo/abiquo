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

import java.util.ArrayList;
import java.util.List;

import org.hibernate.Query;

import com.abiquo.abiserver.business.hibernate.pojohb.infrastructure.PhysicalmachineHB;
import com.abiquo.abiserver.business.hibernate.pojohb.user.EnterpriseHB;
import com.abiquo.abiserver.business.hibernate.pojohb.workload.EnterpriseExclusionRuleHB;
import com.abiquo.abiserver.persistence.SessionUtils;
import com.abiquo.abiserver.persistence.dao.workload.EnterpriseExclusionRuleDAO;
import com.abiquo.abiserver.persistence.hibernate.HibernateDAO;

/**
 * TODO query on the hbm.xml file
 */
public class EnterpriseExclusionRuleDAOHibernate extends
    HibernateDAO<EnterpriseExclusionRuleHB, Integer> implements EnterpriseExclusionRuleDAO
{
    /**
     * Returns machines having VMs, none of which belongs to enterprises excluded from hosting VMs
     * from the specified enterprise.
     */
    @Override
    @SuppressWarnings("unchecked")
    public List<PhysicalmachineHB> findMachinesWithVMsFromExcludedEnterprises(
        final EnterpriseHB enterprise)
    {
        assert enterprise != null;

        final Query query =
            getSession()
                .createQuery(
                    "SELECT DISTINCT vm.hypervisor.physicalMachine FROM VirtualmachineHB vm WHERE "
                        + " (vm.enterpriseHB IN( SELECT rule.enterprise2 FROM EnterpriseExclusionRuleHB rule WHERE rule.enterprise1 = :enterprise )) "
                        + " OR "
                        + " (vm.enterpriseHB IN( SELECT rule.enterprise1 FROM EnterpriseExclusionRuleHB rule WHERE rule.enterprise2 = :enterprise )) ");
        query.setParameter("enterprise", enterprise);
        final List<PhysicalmachineHB> result = query.list();

        return result;
    }

    /**
     * Returns the list of enterprises that can't be co-located with an enterprise.
     */
    @Override
    public List<EnterpriseHB> findExcludedEnterprises(final EnterpriseHB enterprise)
    {
        assert enterprise != null;

        /*
         * Find all rules of which the enterprise is part first.
         */
        final List<EnterpriseExclusionRuleHB> rules =
            SessionUtils
                .list(getSession(), EnterpriseExclusionRuleHB.class,
                    "SELECT obj FROM EnterpriseExclusionRuleHB obj " + "WHERE "
                        + "  obj.enterprise1 = ? OR " + "  obj.enterprise2 = ?", enterprise,
                    enterprise);
        final List<EnterpriseHB> result = new ArrayList<EnterpriseHB>();
        for (final EnterpriseExclusionRuleHB rule : rules)
        {
            // If the enterprise is enterprise1, then enterprise2 is the enterprise that can't be
            // co-located
            if (rule.getEnterprise1() == enterprise)
            {
                result.add(rule.getEnterprise2());
            }
            // ...else, then enterprise1 is the enterprise that can't be co-located
            else
            {
                result.add(rule.getEnterprise1());
            }
        }
        return result;
    }
}
