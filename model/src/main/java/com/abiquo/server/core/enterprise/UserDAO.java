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
import com.abiquo.server.core.enterprise.User.AuthType;
import com.abiquo.server.core.util.PagedList;

@Repository("jpaUserDAO")
public class UserDAO extends DefaultDAOBase<Integer, User>
{
    public UserDAO()
    {
        super(User.class);
    }

    public UserDAO(final EntityManager entityManager)
    {
        super(User.class, entityManager);
    }

    public static Criterion sameEnterprise(final Enterprise enterprise)
    {
        return Restrictions.eq(User.ENTERPRISE_PROPERTY, enterprise);
    }

    public static Criterion sameId(final Integer userId)
    {
        return Restrictions.eq(User.ID_PROPERTY, userId);
    }

    public static Criterion sameNick(final String nick)
    {
        return Restrictions.eq(User.NICK_PROPERTY, nick);
    }

    public static Criterion sameRole(final Role role)
    {
        return Restrictions.eq(User.ROLE_PROPERTY, role);
    }

    private Criterion filterBy(final String filter)
    {
        Disjunction filterDisjunction = Restrictions.disjunction();

        filterDisjunction.add(Restrictions.like(User.NAME_PROPERTY, '%' + filter + '%'));
        filterDisjunction.add(Restrictions.like(User.SURNAME_PROPERTY, '%' + filter + '%'));
        filterDisjunction.add(Restrictions.like(User.EMAIL_PROPERTY, '%' + filter + '%'));
        filterDisjunction.add(Restrictions.like(User.NICK_PROPERTY, '%' + filter + '%'));

        return filterDisjunction;
    }

    public Collection<User> findByRole(final Role role)
    {
        return find(null, role, null, User.ID_PROPERTY, false, false, 0, 25);
    }

    public Collection<User> findByEnterprise(final Enterprise enterprise)
    {
        return find(enterprise, null, VirtualDatacenter.NAME_PROPERTY, false);
    }

    public User findByEnterprise(final Integer userId, final Enterprise enterprise)
    {
        Criteria criteria = createCriteria(sameId(userId), sameEnterprise(enterprise));
        criteria.addOrder(Order.asc(VirtualDatacenter.NAME_PROPERTY));

        return (User) criteria.uniqueResult();
    }

    public Collection<User> find(final Enterprise enterprise, final String filter,
        final String orderBy, final boolean desc)
    {
        return find(enterprise, null, filter, orderBy, desc, false, 0, 25);
    }

    public Collection<User> find(final Enterprise enterprise, final Role role, final String filter,
        final String orderBy, final boolean desc, final boolean connected, final Integer offset,
        final Integer numResults)
    {
        Criteria criteria = createCriteria(enterprise, role, filter, orderBy, desc, connected);

        Long total = count(criteria);

        criteria = createCriteria(enterprise, role, filter, orderBy, desc, connected);

        criteria.setFirstResult(offset * numResults);
        criteria.setMaxResults(numResults);

        List<User> result = getResultList(criteria);

        PagedList<User> page = new PagedList<User>();
        page.addAll(result);
        page.setCurrentElement(offset);
        page.setPageSize(numResults);
        page.setTotalResults(total.intValue());

        return page;
    }

    private Criteria createCriteria(final Enterprise enterprise, final Role role,
        final String filter, final String orderBy, final boolean desc, final boolean connected)
    {
        Criteria criteria = createCriteria();

        if (enterprise != null)
        {
            criteria.add(sameEnterprise(enterprise));
        }

        if (role != null)
        {
            criteria.add(sameRole(role));
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

    public boolean existAnyUserWithNick(final String nick)
    {
        return existsAnyByCriterions(sameNick(nick));
    }

    public boolean existAnyOtherUserWithNick(final User user, final String nick)
    {
        return existsAnyOtherByCriterions(user, sameNick(nick));
    }

    /**
     * Returns a User with nick {login} that is login to DB.
     * 
     * @param login that must match.
     * @return User.
     */
    public User getAbiquoUserByLogin(String login)
    {
        Criteria criteria = createCriteria();
        criteria.add(sameNick(login));

        criteria.add(Restrictions.eq("authType", User.AuthType.ABIQUO));

        return (User) criteria.uniqueResult();
    }

    /**
     * eturns a User with nick {login} that is login to [authType].
     * 
     * @param login that must match.
     * @param authType a {@link User.AuthType} value.
     * @return User .
     */
    public User getUserByAuth(String login, AuthType authType)
    {
        Criteria criteria = createCriteria();
        criteria.add(sameNick(login));

        criteria.add(Restrictions.eq("authType", authType));

        return (User) criteria.uniqueResult();
    }

    /**
     * Same AuthType?.
     * 
     * @param authType AuthType.a {@link User.AuthType} value.
     * @return Criterion
     */
    public static Criterion sameAuthType(AuthType authType)
    {
        return Restrictions.eq("authType", authType);
    }

    /**
     * Look up in the DB for a user with login == nick and authType == authType.
     * 
     * @param nick login.
     * @param authType a {@link User.AuthType} value.
     * @return boolean true if exists, false otherwise.
     */
    public boolean existAnyUserWithNickAndAuth(String nick, AuthType authType)
    {
        return existsAnyByCriterions(sameNick(nick), sameAuthType(authType));
    }
    
    public boolean existAnyUserWithRole(final Role role)
    {
        return existsAnyByCriterions(sameRole(role));
    }
}
