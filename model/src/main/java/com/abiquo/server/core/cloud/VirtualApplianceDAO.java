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

package com.abiquo.server.core.cloud;

import java.util.List;

import javax.persistence.EntityManager;

import org.hibernate.Criteria;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.springframework.stereotype.Repository;

import com.abiquo.server.core.common.persistence.DefaultDAOBase;
import com.abiquo.server.core.enterprise.Enterprise;
import com.softwarementors.bzngine.entities.PersistentEntity;

@Repository("jpaVirtualApplianceDAO")
public class VirtualApplianceDAO extends DefaultDAOBase<Integer, VirtualAppliance>
{
    private static Criterion sameEnterprise(final Enterprise enterprise)
    {
        return Restrictions.eq(VirtualAppliance.ENTERPRISE_PROPERTY, enterprise);
    }

    private static Criterion sameVirtualDatacenter(final VirtualDatacenter virtualDatacenter)
    {
        return Restrictions.eq(VirtualAppliance.VIRTUAL_DATACENTER_PROPERTY, virtualDatacenter);
    }

    public VirtualApplianceDAO()
    {
        super(VirtualAppliance.class);
    }

    public VirtualApplianceDAO(final EntityManager entityManager)
    {
        super(VirtualAppliance.class, entityManager);
    }

    public List<VirtualAppliance> findByEnterprise(final Enterprise enterprise)
    {
        Criteria criteria = createCriteria(sameEnterprise(enterprise));
        criteria.addOrder(Order.asc(VirtualAppliance.NAME_PROPERTY));

        return criteria.list();
    }

    public VirtualAppliance findById(final VirtualDatacenter vdc, final Integer vappId)
    {
        Criteria criteria = createCriteria(sameVirtualDatacenter(vdc));
        criteria.add(Restrictions.eq(PersistentEntity.ID_PROPERTY, vappId));

        return (VirtualAppliance) criteria.uniqueResult();
    }

    public VirtualAppliance findByName(final String name)
    {
        return findUniqueByProperty(VirtualAppliance.NAME_PROPERTY, name);
    }

    public List<VirtualAppliance> findByVirtualDatacenter(final VirtualDatacenter virtualDatacenter)
    {
        Criteria criteria = createCriteria(sameVirtualDatacenter(virtualDatacenter));
        criteria.addOrder(Order.asc(VirtualAppliance.NAME_PROPERTY));

        return criteria.list();
    }
}
