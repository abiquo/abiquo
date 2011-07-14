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

import java.util.Collection;
import java.util.List;

import javax.persistence.EntityManager;

import org.hibernate.Criteria;
import org.hibernate.Query;
import org.hibernate.criterion.Restrictions;
import org.springframework.stereotype.Repository;

import com.abiquo.server.core.common.persistence.DefaultDAOBase;
import com.abiquo.server.core.infrastructure.Datacenter;
import com.abiquo.server.core.infrastructure.Machine;

@Repository("jpaMachineLoadRuleDAO")
public class MachineLoadRuleDAO extends DefaultDAOBase<Integer, MachineLoadRule>
{
    public MachineLoadRuleDAO()
    {
        super(MachineLoadRule.class);
    }

    public MachineLoadRuleDAO(EntityManager entityManager)
    {
        super(MachineLoadRule.class, entityManager);
    }

    private final static String CANDIDATE_MACHINE_RULES =
        "SELECT DISTINCT obj FROM com.abiquo.server.core.scheduler.MachineLoadRule obj WHERE "
            + "obj.machine IN (:machines) OR "
            + // Find rules from machines
            "obj.rack IN (SELECT obj2.rack FROM com.abiquo.server.core.infrastructure.Machine obj2 WHERE obj2 IN (:machines)) OR "
            + // Find rules from racks hosting the machines
            "obj.datacenter IN (SELECT obj3.datacenter FROM com.abiquo.server.core.infrastructure.Machine obj3 WHERE obj3 IN (:machines)) ";

    @SuppressWarnings("unchecked")
    public List<MachineLoadRule> findCandidateMachineLoadRules(
        Collection<Machine> firstPassCandidateMachines)
    {
        Query query = getSession().createQuery(CANDIDATE_MACHINE_RULES);
        query.setParameterList("machines", firstPassCandidateMachines);

        return query.list();
    }

    public List<MachineLoadRule> getRulesForDatacenter(final Integer idDatacenter)
    {
        Criteria crit = createNestedCriteria(MachineLoadRule.DATACENTER_PROPERTY);
        crit.add(Restrictions.eq(Datacenter.ID_PROPERTY, idDatacenter));
        return getResultList(crit);
    }

}
