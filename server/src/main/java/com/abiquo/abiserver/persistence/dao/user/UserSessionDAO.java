package com.abiquo.abiserver.persistence.dao.user;

import java.util.Date;

import com.abiquo.abiserver.persistence.DAO;
import com.abiquo.abiserver.pojo.authentication.UserSession;

/**
 * Specific interface to work with the {@link com.abiquo.abiserver.pojo.authentication.UserSession}
 * Exposes all the methods that this entity will need to interact with the data source. UserSession
 * is the entity that keeps session in DB.
 * 
 * @author serafin.sedano@abiquo.com
 */
public interface UserSessionDAO extends DAO<UserSession, Integer>
{
    /**
     * Deletes all sessions older than a Date.
     * 
     * @param name username.
     * @param date limit.
     * @return number of deleted objects. int
     */
    public int deleteUserSessionsOlderThan(String name, Date date);

    /**
     * Deletes all sessions from user that matches the key.
     * 
     * @param name username.
     * @param key key.
     * @return int. Number of deleted objects.
     */
    public int deleteAllUserSessions(String name, String key);

    /**
     * Obtains the current session for the user with name if matches with key. Null if no session is
     * in DB.
     * 
     * @param name username.
     * @param key key.
     * @return current session if any. Null otherwise. UserSession
     */
    public UserSession getCurrentUserSession(String name, String key);
}
