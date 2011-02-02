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

import com.abiquo.server.core.cloud.VirtualDatacenter;
import com.abiquo.server.core.common.persistence.DefaultDAOBase;
import com.abiquo.server.core.util.PagedList;

@Repository("jpaUserDAO")
public class UserDAO extends DefaultDAOBase<Integer, User>
{
    public UserDAO()
    {
        super(User.class);
    }

    public UserDAO(EntityManager entityManager)
    {
        super(User.class, entityManager);
    }

    public static Criterion sameEnterprise(Enterprise enterprise)
    {
        return Restrictions.eq(User.ENTERPRISE_PROPERTY, enterprise);
    }

    public static Criterion sameId(Integer userId)
    {
        return Restrictions.eq(User.ID_PROPERTY, userId);
    }

    public static Criterion sameNick(String nick)
    {
        return Restrictions.eq(User.NICK_PROPERTY, nick);
    }

    private Criterion filterBy(String filter)
    {
        Disjunction filterDisjunction = Restrictions.disjunction();

        filterDisjunction.add(Restrictions.like(User.NAME_PROPERTY, '%' + filter + '%'));
        filterDisjunction.add(Restrictions.like(User.SURNAME_PROPERTY, '%' + filter + '%'));
        filterDisjunction.add(Restrictions.like(User.EMAIL_PROPERTY, '%' + filter + '%'));
        filterDisjunction.add(Restrictions.like(User.NICK_PROPERTY, '%' + filter + '%'));

        return filterDisjunction;
    }

    public Collection<User> findByEnterprise(Enterprise enterprise)
    {
        return find(enterprise, null, VirtualDatacenter.NAME_PROPERTY, false);
    }

    public User findByEnterprise(Integer userId, Enterprise enterprise)
    {
        Criteria criteria = createCriteria(sameId(userId), sameEnterprise(enterprise));
        criteria.addOrder(Order.asc(VirtualDatacenter.NAME_PROPERTY));

        return (User) criteria.uniqueResult();
    }

    public Collection<User> find(Enterprise enterprise, String filter, String orderBy, boolean desc)
    {
        return find(enterprise, filter, orderBy, desc, false, 0, 25);
    }

    public Collection<User> find(Enterprise enterprise, String filter, String orderBy,
        boolean desc, boolean connected, Integer offset, Integer numResults)
    {
        Criteria criteria = createCriteria(enterprise, filter, orderBy, desc, connected);

        Long total = count(criteria);

        criteria = createCriteria(enterprise, filter, orderBy, desc, connected);

        criteria.setFirstResult(offset * numResults);
        criteria.setMaxResults(numResults);

        List<User> result = getResultList(criteria);

        PagedList<User> page = new PagedList<User>();
        page.addAll(result);
        page.setCurrentPage(offset);
        page.setPageSize(numResults);
        page.setTotalResults(total.intValue());

        return page;
    }

    private Criteria createCriteria(Enterprise enterprise, String filter, String orderBy,
        boolean desc, boolean connected)
    {
        Criteria criteria = createCriteria();

        if (enterprise != null)
        {
            criteria.add(sameEnterprise(enterprise));
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

    public boolean existAnyUserWithNick(String nick)
    {
        return existsAnyByCriterions(sameNick(nick));
    }

    public boolean existAnyOtherUserWithNick(User user, String nick)
    {
        return existsAnyOtherByCriterions(user, sameNick(nick));
    }
}
