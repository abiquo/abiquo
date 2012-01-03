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

package com.abiquo.server.core.enterprise;

import java.util.Collection;

import javax.persistence.EntityManager;

import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.Restrictions;
import org.springframework.stereotype.Repository;

import com.abiquo.server.core.common.persistence.DefaultDAOBase;
import com.abiquo.server.core.infrastructure.Datacenter;
import com.softwarementors.bzngine.entities.PersistentEntity;

@Repository("jpaDatacenterLimitsDAO")
public class DatacenterLimitsDAO extends DefaultDAOBase<Integer, DatacenterLimits>
{
    public DatacenterLimitsDAO()
    {
        super(DatacenterLimits.class);
    }

    public DatacenterLimitsDAO(final EntityManager entityManager)
    {
        super(DatacenterLimits.class, entityManager);
    }

    private Criterion sameEnterprise(final Enterprise enterprise)
    {
        return Restrictions.eq(DatacenterLimits.ENTERPRISE_PROPERTY, enterprise);
    }

    private Criterion sameDatacenter(final Datacenter datacenter)
    {
        return Restrictions.eq(DatacenterLimits.DATACENTER_PROPERTY, datacenter);
    }

    private Criterion sameId(final Integer id)
    {
        return Restrictions.eq(PersistentEntity.ID_PROPERTY, id);
    }

    public Collection<DatacenterLimits> findByDatacenter(final Datacenter datacenter)
    {
        return findByCriterions(sameDatacenter(datacenter));
    }

    public Collection<DatacenterLimits> findByEnterprise(final Enterprise enterprise)
    {
        return findByCriterions(sameEnterprise(enterprise),
            Restrictions.isNotNull(DatacenterLimits.DATACENTER_PROPERTY));
    }

    public DatacenterLimits findByEnterpriseAndDatacenter(final Enterprise enterprise,
        final Datacenter datacenter)
    {
        return (DatacenterLimits) createCriteria(sameEnterprise(enterprise),
            sameDatacenter(datacenter)).uniqueResult();
    }

    public DatacenterLimits findByEnterpriseAndIdentifier(final Enterprise enterprise,
        final Integer limitId)
    {
        return (DatacenterLimits) createCriteria(sameEnterprise(enterprise), sameId(limitId))
            .uniqueResult();
    }

}
