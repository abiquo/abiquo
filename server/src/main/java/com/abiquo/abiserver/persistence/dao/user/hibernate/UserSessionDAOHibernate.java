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
