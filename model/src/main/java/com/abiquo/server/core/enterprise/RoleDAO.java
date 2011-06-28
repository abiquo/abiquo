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
import java.util.List;

import javax.persistence.EntityManager;

import org.apache.commons.lang.StringUtils;
import org.hibernate.Criteria;
import org.hibernate.Query;
import org.hibernate.criterion.Conjunction;
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

    public static Criterion sameEnterpriseOrNull(final Enterprise enterprise)
    {
        Disjunction filterDisjunction = Restrictions.disjunction();

        filterDisjunction.add(Restrictions.eq(Role.ENTERPRISE_PROPERTY, enterprise));
        filterDisjunction.add(Restrictions.isNull(Role.ENTERPRISE_PROPERTY));

        return filterDisjunction;
    }

    public static Criterion sameEnterprise(final Enterprise enterprise)
    {
        Disjunction filterDisjunction = Restrictions.disjunction();

        filterDisjunction.add(Restrictions.eq(Role.ENTERPRISE_PROPERTY, enterprise));

        return filterDisjunction;
    }

    public static Criterion genericRole()
    {
        return Restrictions.isNull(Role.ENTERPRISE_PROPERTY);

    }

    private Criterion filterBy(final String filter)
    {
        Disjunction filterDisjunction = Restrictions.disjunction();

        filterDisjunction.add(Restrictions.like(Role.NAME_PROPERTY, '%' + filter + '%'));

        return filterDisjunction;
    }

    private Criterion filterExactlyBy(final String filter)
    {
        Conjunction filterConjunction = Restrictions.conjunction();

        filterConjunction.add(Restrictions.like(Role.NAME_PROPERTY, filter));

        return filterConjunction;
    }

    public Collection<Role> find(final Enterprise enterprise, final String filter,
        final String orderBy, final boolean desc)
    {
        return find(enterprise, filter, orderBy, desc, 0, 25);
    }

    public Collection<Role> find(final Enterprise enterprise, final String filter,
        final String orderBy, final boolean desc, final Integer offset, final Integer numResults)
    {
        return find(enterprise, filter, orderBy, desc, offset, numResults, false);
    }

    public Collection<Role> find(final Enterprise enterprise, final String filter,
        final String orderBy, final boolean desc, final Integer offset, final Integer numResults,
        final boolean discardNullEnterprises)
    {
        Criteria criteria =
            createCriteria(enterprise, filter, orderBy, desc, discardNullEnterprises);

        Long total = count(criteria);

        criteria = createCriteria(enterprise, filter, orderBy, desc, discardNullEnterprises);

        criteria.setFirstResult(offset * numResults);
        criteria.setMaxResults(numResults);

        List<Role> result = getResultList(criteria);

        PagedList<Role> page = new PagedList<Role>();
        page.addAll(result);
        page.setCurrentElement(offset);
        page.setPageSize(numResults);
        page.setTotalResults(total.intValue());

        return page;
    }

    public Collection<Role> findExactly(final Enterprise enterprise, final String filter,
        final String orderBy, final boolean desc, final Integer offset, final Integer numResults,
        final boolean discardNullEnterprises)
    {
        Criteria criteria =
            createCriteriaExactly(enterprise, filter, orderBy, desc, discardNullEnterprises);

        Long total = count(criteria);

        criteria = createCriteriaExactly(enterprise, filter, orderBy, desc, discardNullEnterprises);

        criteria.setFirstResult(offset * numResults);
        criteria.setMaxResults(numResults);

        List<Role> result = getResultList(criteria);

        PagedList<Role> page = new PagedList<Role>();
        page.addAll(result);
        page.setCurrentElement(offset);
        page.setPageSize(numResults);
        page.setTotalResults(total.intValue());

        return page;
    }

    private Criteria createCriteria(final Enterprise enterprise, final String filter,
        final String orderBy, final boolean desc, final boolean discardNullEnterprises)
    {
        Criteria criteria = createCriteria();

        if (enterprise != null)
        {
            if (discardNullEnterprises)
            {
                criteria.add(sameEnterprise(enterprise));
            }
            else
            {
                criteria.add(sameEnterpriseOrNull(enterprise));
            }
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
            criteria.addOrder(Order.asc(Role.NAME_PROPERTY));
        }

        return criteria;
    }

    public List<Privilege> findPrivilegesByIdRole(final Integer idRole)
    {
        Query query = getSession().createQuery(QUERY_PRIVILEGES_FROM_ROLE);
        query.setInteger("idRole", idRole);

        List<Privilege> privileges = query.list();
        return privileges;
    }

    private final static String QUERY_PRIVILEGES_FROM_ROLE = //
        "  SELECT r.privileges FROM " + //
            "com.abiquo.server.core.enterprise.Role r " + //
            "WHERE r.id = :idRole";

    private Criteria createCriteriaExactly(final Enterprise enterprise, final String filter,
        final String orderBy, final boolean desc, final boolean discardNullEnterprises)
    {
        Criteria criteria = createCriteria();

        if (enterprise != null)
        {
            if (discardNullEnterprises)
            {
                criteria.add(sameEnterprise(enterprise));
            }
            else
            {
                criteria.add(sameEnterpriseOrNull(enterprise));
            }

        }
        else
        {
            criteria.add(genericRole());
        }

        if (!StringUtils.isEmpty(filter))
        {
            criteria.add(filterExactlyBy(filter));
        }

        if (!StringUtils.isEmpty(orderBy))
        {
            Order order = Order.asc(orderBy);
            if (desc)
            {
                order = Order.desc(orderBy);
            }
            criteria.addOrder(order);
            criteria.addOrder(Order.asc(Role.NAME_PROPERTY));
        }

        return criteria;
    }
}
