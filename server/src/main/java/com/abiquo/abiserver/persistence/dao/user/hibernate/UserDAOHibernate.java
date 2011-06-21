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

package com.abiquo.abiserver.persistence.dao.user.hibernate;

import java.util.ArrayList;
import java.util.List;

import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.criterion.Restrictions;

import com.abiquo.abiserver.business.hibernate.pojohb.user.UserHB;
import com.abiquo.abiserver.persistence.dao.user.UserDAO;
import com.abiquo.abiserver.persistence.hibernate.HibernateDAO;
import com.abiquo.abiserver.persistence.hibernate.HibernateDAOFactory;
import com.abiquo.server.core.enterprise.User.AuthType;

/**
 * * Class that implements the extra DAO functions for the
 * {@link com.abiquo.abiserver.persistence.dao.user.UserDAO} interface
 * 
 * @author jdevesa@abiquo.com
 */
public class UserDAOHibernate extends HibernateDAO<UserHB, Integer> implements UserDAO
{

    /** Named queries */
    private static final String GET_USER_BY_USER_NAME = "GET_USER_BY_USER_NAME";

    private static final String GET_USERS_BY_PRIVILEGE = "GET_USERS_BY_PRIVILEGE";

    private static final String GET_USERS_BY_ROLE_DESC = "GET_USERS_BY_ROLE_DESC";

    /*
     * (non-Javadoc)
     * @see com.abiquo.abiserver.persistence.dao.user.UserDAO#getUserByUserName(java.lang.String)
     */
    @Override
    @Deprecated
    public UserHB getUserByUserName(final String username)
    {
        return getUserByLoginAuth(username, AuthType.ABIQUO.name());
    }

    /**
     * @see com.abiquo.abiserver.persistence.dao.user.UserDAO#getUserByLoginAuth(java.lang.String,
     *      com.abiquo.server.core.enterprise.User.AuthType)
     */
    @Override
    public UserHB getUserByLoginAuth(final String username, String authType)
    {
        UserHB requestedUser = new UserHB();

        if (authType == null)
        {
            authType = AuthType.ABIQUO.name();
        }
        Session session = HibernateDAOFactory.getSessionFactory().getCurrentSession();
        Query userQuery = session.getNamedQuery(GET_USER_BY_USER_NAME);
        userQuery.setString("username", username);
        userQuery.setString("authType", authType);
        requestedUser = (UserHB) userQuery.uniqueResult();
        return requestedUser;
    }

    @Override
    public String getEmailByUserName(final String username)
    {
        return getEmailByUserName(username, AuthType.ABIQUO.name());
    }

    /**
     * @see com.abiquo.abiserver.persistence.dao.user.UserDAO#getEmailByUserName(java.lang.String,
     *      java.lang.String)
     */
    @Override
    public String getEmailByUserName(final String username, final String authType)
    {
        return getUserByLoginAuth(username, authType).getEmail();
    }

    @SuppressWarnings("unchecked")
    @Override
    public List<UserHB> getUsersByUserPrivileges(final String privilege, final Integer enterprise)
    {
        List<UserHB> requestedUser = new ArrayList<UserHB>();

        Session session = HibernateDAOFactory.getSessionFactory().getCurrentSession();
        Query userQuery = session.getNamedQuery(GET_USERS_BY_PRIVILEGE);
        userQuery.setString("privilege", privilege);
        userQuery.setInteger("enterprise", enterprise);
        requestedUser = userQuery.list();

        return requestedUser;
    }

    @Override
    public UserHB findUserHBByName(final String name)
    {
        return findUserHBByName(name, AuthType.ABIQUO.name());
    }

    @Override
    public UserHB findUserHBByName(final String name, final String authType)
    {
        return (UserHB) getSession().createCriteria(UserHB.class)
            .add(Restrictions.eq("user", name))
            .add(Restrictions.eq("authType", authType != null ? authType : AuthType.ABIQUO.name()))
            .uniqueResult();
    }

    @Override
    public UserHB findUserHBById(final Integer id)
    {
        return findUserHBById(id, AuthType.ABIQUO.name());
    }

    @Override
    public UserHB findUserHBById(final Integer id, String authType)
    {
        UserHB requestedUser = new UserHB();

        if (authType == null)
        {
            authType = AuthType.ABIQUO.name();
        }
        Session session = HibernateDAOFactory.getSessionFactory().getCurrentSession();
        return (UserHB) session.createCriteria(UserHB.class).add(Restrictions.eq("id", id))
            .add(Restrictions.eq("authType", authType != null ? authType : AuthType.ABIQUO.name()))
            .uniqueResult();

        // getNamedQuery(GET_USER_BY_USER_NAME);
        // userQuery.setInteger("id", id);
        // userQuery.setString("authType", authType);

        // return (UserHB) getSession().createCriteria(UserHB.class).add(Restrictions.eq("id", id))
        // .add(Restrictions.eq("authType", authType != null ? authType : AuthType.ABIQUO.name()))
        // .uniqueResult();
    }

    @SuppressWarnings("unchecked")
    @Override
    public List<UserHB> getUsersByUserRol(final String role, final Integer enterprise)
    {
        List<UserHB> requestedUser = new ArrayList<UserHB>();

        Session session = HibernateDAOFactory.getSessionFactory().getCurrentSession();
        Query userQuery = session.getNamedQuery(GET_USERS_BY_ROLE_DESC);
        // userQuery.setString("roleDescription", role);
        // userQuery.setInteger("enterprise", enterprise);
        requestedUser = userQuery.list();

        return requestedUser;

    }
}
