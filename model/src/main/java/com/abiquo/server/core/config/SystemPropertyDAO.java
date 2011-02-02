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
package com.abiquo.server.core.config;

import java.util.List;

import javax.persistence.EntityManager;

import org.hibernate.Criteria;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.springframework.stereotype.Repository;

import com.abiquo.server.core.common.persistence.DefaultDAOBase;

@Repository("jpaSystemPropertyDAO")
public class SystemPropertyDAO extends DefaultDAOBase<Integer, SystemProperty>
{
    public SystemPropertyDAO()
    {
        super(SystemProperty.class);
    }

    public SystemPropertyDAO(EntityManager entityManager)
    {
        super(SystemProperty.class, entityManager);
    }

    public boolean existsAnyWithName(String name)
    {
        return existsAnyByCriterions(equalsName(name));
    }

    public boolean existsAnyOtherWithName(SystemProperty systemProperty, String name)
    {
        return this.existsAnyOtherByCriterions(systemProperty, equalsName(name));
    }

    public SystemProperty findByName(String name)
    {
        Criteria criteria = createCriteria(equalsName(name));
        criteria.addOrder(Order.asc(SystemProperty.NAME_PROPERTY));
        List<SystemProperty> result = getResultList(criteria);
        return result.isEmpty() ? null : result.get(0);
    }

    public List<SystemProperty> findByComponent(String component)
    {
        Criteria criteria = createCriteria(equalsComponent(component));
        criteria.addOrder(Order.asc(SystemProperty.NAME_PROPERTY));
        return getResultList(criteria);
    }

    private Criterion equalsName(String name)
    {
        return Restrictions.eq(SystemProperty.NAME_PROPERTY, name);
    }

    private Criterion equalsComponent(String component)
    {
        String prefix = component + ".";
        return Restrictions.like(SystemProperty.NAME_PROPERTY, prefix, MatchMode.START);
    }

}
