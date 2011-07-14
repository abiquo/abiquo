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

import java.util.Date;

import com.abiquo.abiserver.persistence.dao.user.UserSessionDAO;
import com.abiquo.abiserver.persistence.hibernate.HibernateDAO;
import com.abiquo.abiserver.pojo.authentication.UserSession;

public class UserSessionDAOHibernate extends HibernateDAO<UserSession, Integer> implements
    UserSessionDAO
{
    /**
     * Deletes all sessions older than a Date.
     * 
     * @param name username.
     * @param date limit.
     * @return number of deleted objects.
     */
    public int deleteUserSessionsOlderThan(String name, Date date)
    {
        return getSession().createQuery(
            "delete from " + UserSession.class.getSimpleName()
                + " where user = ? and expireDate < ?").setString(0, name).setDate(1, new Date())
            .executeUpdate();

    }

    /**
     * Deletes all sessions from user that matches the key.
     * 
     * @param name username.
     * @param key key.
     * @return int. Number of deleted objects.
     */
    public int deleteAllUserSessions(String name, String key)
    {
        return getSession().createQuery(
            "delete from " + UserSession.class.getSimpleName() + " where user = ? and key = ?")
            .setString(0, name).setString(1, key).executeUpdate();
    }

    /**
     * Obtains the current session for the user with name and key. Null if no session is in DB.
     * 
     * @param name username.
     * @param key key.
     * @return current session if any. Null otherwise. UserSession
     */
    public UserSession getCurrentUserSession(String name, String key)
    {
        return findUniqueByProperties(new String[] {"user", "key"}, new String[] {name, key});
    }
}
