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

import javax.persistence.EntityManager;

import org.hibernate.Criteria;
import org.hibernate.criterion.Criterion;
import java.util.Collection;
import java.util.List;

import javax.persistence.EntityManager;

import org.apache.commons.lang.StringUtils;
import org.hibernate.Criteria;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.Disjunction;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.springframework.stereotype.Repository;
import com.abiquo.server.core.util.PagedList;

import com.abiquo.server.core.common.persistence.DefaultDAOBase;

/**
 * This class provides access to DB in order to query for {@link RoleLdap}.
 * 
 * @author ssedano
 */

@Repository("jpaLdapRoleDAO")
public class RoleLdapDAO extends DefaultDAOBase<Integer, RoleLdap>
{
    public RoleLdapDAO()
    {
        super(RoleLdap.class);
    }

    /**
     * Constructor.
     * 
     * @param entityManager entitimanager.
     */
    public RoleLdapDAO(final EntityManager entityManager)
    {
        super(RoleLdap.class, entityManager);
    }

    /**
     * {@link Role} that match <b>exactly</b> with type.
     * 
     * @param type name of the <code>LdapRoleDAO</code>
     * @return <code>LdapRoleDAO</code>s which type mathes name
     */
    public RoleLdap findByType(String type)
    {
        if (type == null)
        {
            return null;
        }
        // If at some point a single ldapRole will map more than one role, the implementation of
        // this function must change.
        RoleLdap role = (RoleLdap) createCriteria(type).uniqueResult();

        return role;
    }

    /**
     * @param type name.
     * @return Criteria that matches type.
     */
    private Criterion sameType(String type)
    {
        return Restrictions.eq("ldapRole", type);
    }

    private Criteria createCriteria(String type)
    {

        Criteria criteria = createCriteria();
        if (type != null)
        {
            criteria.add(sameType(type));
        }
        return criteria;
    }

    // Criterions

    private Criterion sameRole(final Role role)
    {
        return Restrictions.eq(RoleLdap.ROLE_PROPERTY, role);
    }

    private Criterion sameRoleLdap(final String roleLdap)
    {
        return Restrictions.eq(RoleLdap.ROLE_LDAP_PROPERTY, roleLdap);
    }

    public List<RoleLdap> findByRoleLdap(final String roleLdap)
    {
        return findByCriterions(sameRoleLdap(roleLdap));
    }

    public Collection<RoleLdap> findByRole(final Role role)
    {
        return findByCriterions(sameRole(role));
    }

    public Collection<RoleLdap> find(final String filter, final String orderBy, final boolean desc,
        final Integer offset, final Integer numResults)
    {
        Criteria criteria = createCriteria(filter, orderBy, desc);

        Long total = count(criteria);

        criteria = createCriteria(filter, orderBy, desc);

        criteria.setFirstResult(offset * numResults);
        criteria.setMaxResults(numResults);

        List<RoleLdap> result = getResultList(criteria);

        PagedList<RoleLdap> page = new PagedList<RoleLdap>();
        page.addAll(result);
        page.setCurrentElement(offset);
        page.setPageSize(numResults);
        page.setTotalResults(total.intValue());

        return page;
    }

    private Criteria createCriteria(final String filter, final String orderBy, final boolean desc)
    {
        Criteria criteria = createCriteria();

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

        return criteria;
    }

    private Criterion filterBy(final String filter)
    {
        Disjunction filterDisjunction = Restrictions.disjunction();

        filterDisjunction.add(Restrictions.like(RoleLdap.ROLE_LDAP_PROPERTY, '%' + filter + '%'));

        return filterDisjunction;
    }

    public boolean existAnyRoleLdapWithRole(final Role role)
    {
        return existsAnyByCriterions(sameRole(role));
    }
}
