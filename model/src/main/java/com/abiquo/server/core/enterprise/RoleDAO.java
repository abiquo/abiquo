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
import java.util.Date;
import java.util.List;

import javax.persistence.EntityManager;

import org.apache.commons.lang.StringUtils;
import org.hibernate.Criteria;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.Disjunction;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.springframework.stereotype.Repository;

import com.abiquo.server.core.common.persistence.DefaultDAOBase;
import com.abiquo.server.core.util.PagedList;

@Repository("jpaRoleDAO")
public class RoleDAO extends DefaultDAOBase<Integer, Role>
{
    public RoleDAO()
    {
        super(Role.class);
    }

    public RoleDAO(final EntityManager entityManager)
    {
        super(Role.class, entityManager);
    }

    public static Criterion sameEnterprise(final Enterprise enterprise)
    {
        return Restrictions.eq(Role.ENTERPRISE_PROPERTY, enterprise);
    }

    public static Criterion genericRole()
    {
        return Restrictions.eq(Role.ENTERPRISE_PROPERTY, null);
    }

    private Criterion filterBy(final String filter)
    {
        Disjunction filterDisjunction = Restrictions.disjunction();

        filterDisjunction.add(Restrictions.like(Role.NAME_PROPERTY, '%' + filter + '%'));

        return filterDisjunction;
    }

    public Collection<Role> find(final Enterprise enterprise, final String filter,
        final String orderBy, final boolean desc)
    {
        return find(enterprise, filter, orderBy, desc, false, 0, 25);
    }

    public Collection<Role> find(final Enterprise enterprise, final String filter,
        final String orderBy, final boolean desc, final boolean connected, final Integer offset,
        final Integer numResults)
    {
        Criteria criteria = createCriteria(enterprise, filter, orderBy, desc, connected);

        Long total = count(criteria);

        criteria = createCriteria(enterprise, filter, orderBy, desc, connected);

        criteria.setFirstResult(offset * numResults);
        criteria.setMaxResults(numResults);

        List<Role> result = getResultList(criteria);

        PagedList<Role> page = new PagedList<Role>();
        page.addAll(result);
        page.setCurrentPage(offset);
        page.setPageSize(numResults);
        page.setTotalResults(total.intValue());

        return page;
    }

    private Criteria createCriteria(final Enterprise enterprise, final String filter,
        final String orderBy, final boolean desc, final boolean connected)
    {
        Criteria criteria = createCriteria();

        if (enterprise != null)
        {
            criteria.add(sameEnterprise(enterprise));
        }
        else
        {
            criteria.add(genericRole());
        }

        if (!StringUtils.isEmpty(filter))
        {
            criteria.add(filterBy(filter));
        }

        if (!StringUtils.isEmpty(orderBy))
        {
            Order order = Order.asc(orderBy);
            if (desc)
            {
                order = Order.desc(orderBy);
            }
            criteria.addOrder(order);
        }

        if (connected)
        {
            criteria.createCriteria("sessions").add(Restrictions.gt("expireDate", new Date()));
            criteria.setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY);
        }
        return criteria;
    }
}
