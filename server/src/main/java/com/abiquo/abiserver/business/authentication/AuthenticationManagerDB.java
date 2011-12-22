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

package com.abiquo.abiserver.business.authentication;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.abiquo.abiserver.abicloudws.AbiCloudConstants;
import com.abiquo.abiserver.business.hibernate.pojohb.user.UserHB;
import com.abiquo.abiserver.config.AbiConfig;
import com.abiquo.abiserver.config.AbiConfigManager;
import com.abiquo.abiserver.persistence.hibernate.HibernateUtil;
import com.abiquo.abiserver.pojo.authentication.Login;
import com.abiquo.abiserver.pojo.authentication.LoginResult;
import com.abiquo.abiserver.pojo.authentication.UserSession;
import com.abiquo.abiserver.pojo.result.BasicResult;
import com.abiquo.abiserver.pojo.result.DataResult;
import com.abiquo.util.ErrorManager;
import com.abiquo.util.resources.ResourceManager;

/**
 * This Authentication Manager provides authentication services, with a Data Base backend.
 * 
 * @author Oliver
 * @deprecated From now on we should log in (authenticat) against the API!
 *             {@link AuthenticationManagerApi}
 */
@Deprecated
public class AuthenticationManagerDB implements IAuthenticationManager
{
    private static final Logger logger = LoggerFactory.getLogger(AuthenticationManagerDB.class);

    private final AbiConfig abiConfig = AbiConfigManager.getInstance().getAbiConfig();

    private static final ResourceManager resourceManger =
        new ResourceManager(AuthenticationManagerDB.class);

    private final ErrorManager errorManager = ErrorManager
        .getInstance(AbiCloudConstants.ERROR_PREFIX);

    @Override
    @SuppressWarnings("unchecked")
    public DataResult<LoginResult> doLogin(final Login login)
    {
        DataResult<LoginResult> dataResult = new DataResult<LoginResult>();

        Session session = null;
        Transaction transaction = null;

        try
        {
            session = HibernateUtil.getSession();

            transaction = session.beginTransaction();

            // Get the user from the appropiate source
            UserHB userHB = null;
            if (StringUtils.isBlank(login.getAuthToken()))
            {
                userHB = getUser(login, session);
            }
            else
            {
                userHB = getUserUsingToken(login, session);
            }

            if (userHB != null)
            {
                // User exists. Check if it is active
                if (userHB.getActive() == 1)
                {
                    // User exists in database and is active.

                    // Looking for all existing active sessions of this user, ordered by when were
                    // created
                    ArrayList<UserSession> oldUserSessions =
                        (ArrayList<UserSession>) session.createCriteria(UserSession.class)
                            .add(Restrictions.eq("user", login.getUser()))
                            .addOrder(Order.desc("key")).list();
                    Date currentTime = new Date();

                    // We erase old expired sessions
                    for (UserSession existingSession : oldUserSessions)
                    {
                        if (currentTime.after(existingSession.getExpireDate()))
                        {
                            session.delete(existingSession);
                        }
                    }

                    // Creating the user session
                    UserSession userSession = new UserSession();
                    userSession.setUser(userHB.getUser());
                    userSession.setKey(String.valueOf(Calendar.getInstance().getTimeInMillis()));
                    userSession.setLocale(login.getLocale());

                    userSession.setUserIdDb(userHB.getIdUser());
                    userSession.setEnterpriseName(userHB.getEnterpriseHB().getName());

                    int sessionTimeout = abiConfig.getSessionTimeout();
                    long expireMilis = new Date().getTime() + sessionTimeout * 60 * 1000;
                    Date expireDate = new Date(expireMilis);
                    userSession.setExpireDate(expireDate);

                    // Saving in Data Base the created User Session
                    session.save(userSession);

                    // Generating the login result, with the user who has logged in and his session
                    LoginResult loginResult = new LoginResult();
                    loginResult.setSession(userSession);
                    loginResult.setUser(userHB.toPojo());

                    // Generating the DataResult
                    dataResult.setSuccess(true);
                    dataResult.setMessage(AuthenticationManagerDB.resourceManger
                        .getMessage("doLogin.success"));
                    dataResult.setData(loginResult);
                }
                else
                {
                    // User is not active. Generating the DataResult
                    errorManager.reportError(resourceManger, dataResult, "doLogin.userInActive");
                }
            }
            else
            {
                // User not exists in database or bad credentials. Generating the DataResult
                errorManager.reportError(resourceManger, dataResult,
                    "doLogin.passwordUserIncorrect");

                dataResult.setResultCode(BasicResult.USER_INVALID);
            }

            transaction.commit();

        }
        catch (Exception e)
        {
            if (transaction != null && transaction.isActive())
            {
                transaction.rollback();
            }

            errorManager.reportError(resourceManger, dataResult, "doLogin.exception", e);
        }

        return dataResult;
    }

    @Override
    public BasicResult doLogout(final UserSession userSession)
    {
        BasicResult basicResult = new BasicResult();

        Session session = null;
        Transaction transaction = null;

        try
        {
            session = HibernateUtil.getSession();
            transaction = session.beginTransaction();

            // Deleting the user session
            UserSession previousSession =
                (UserSession) session.createCriteria(UserSession.class)
                    .add(Restrictions.eq("user", userSession.getUser()))
                    .add(Restrictions.eq("key", userSession.getKey())).uniqueResult();

            if (previousSession != null)
            {
                session.delete(previousSession.getClass().getSimpleName(), previousSession);
            }

            basicResult.setSuccess(true);
            basicResult.setMessage(AuthenticationManagerDB.resourceManger
                .getMessage("doLogout.success"));

            transaction.commit();
        }
        catch (Exception e)
        {
            if (transaction != null && transaction.isActive())
            {
                transaction.rollback();
            }

            errorManager.reportError(resourceManger, basicResult, "doLogout", e);
        }

        return basicResult;
    }

    @Override
    public BasicResult checkSession(final UserSession userSession)
    {
        BasicResult checkSessionResult = new BasicResult();

        Session session = null;
        Transaction transaction = null;

        UserSession sessionToCheck = null;

        try
        {
            session = HibernateUtil.getSession();
            transaction = session.beginTransaction();

            sessionToCheck =
                (UserSession) HibernateUtil.getSession().createCriteria(UserSession.class)
                    .add(Restrictions.eq("user", userSession.getUser()))
                    .add(Restrictions.eq("key", userSession.getKey())).uniqueResult();

            if (sessionToCheck == null)
            {
                // The session does not exist, so is not valid
                checkSessionResult.setResultCode(BasicResult.SESSION_INVALID);
                logger.trace("Invalod session. Please login again");
            }
            else
            {
                // Checking if the session has expired
                Date currentDate = new Date();
                if (currentDate.before(sessionToCheck.getExpireDate()))
                {
                    // The session is valid updating the expire Date
                    int sessionTimeout = abiConfig.getSessionTimeout();
                    long expireMilis = new Date().getTime() + sessionTimeout * 60 * 1000;
                    Date expireDate = new Date(expireMilis);
                    sessionToCheck.setExpireDate(expireDate);

                    session.update(sessionToCheck);

                    checkSessionResult.setSuccess(true);
                    checkSessionResult.setMessage(AuthenticationManagerDB.resourceManger
                        .getMessage("checkSession.success"));
                }
                else
                {
                    // The session has time out. Deleting the session from Data Base
                    session.delete(sessionToCheck);

                    checkSessionResult.setResultCode(BasicResult.SESSION_TIMEOUT);
                    logger.trace("Session expired. Please login again");
                }

            }

            transaction.commit();
        }
        catch (Exception e)
        {
            if (transaction != null && transaction.isActive())
            {
                transaction.rollback();
            }

            logger.trace("Unexpected error while checking the user session", e);
        }

        return checkSessionResult;
    }

    @Override
    public boolean isLoggedIn(final String username)
    {
        List<UserSession> sessions = findAllSessions(username);
        return sessions != null && !sessions.isEmpty();
    }

    @SuppressWarnings("unchecked")
    @Override
    public List<UserSession> findAllSessions(final String username)
    {
        Session session = null;
        Transaction transaction = null;
        List<UserSession> sessions = null;

        try
        {
            session = HibernateUtil.getSession();
            transaction = session.beginTransaction();

            sessions =
                session.createCriteria(UserSession.class).add(Restrictions.eq("user", username))
                    .list();

            transaction.commit();
        }
        catch (Exception ex)
        {
            if (transaction != null && transaction.isActive())
            {
                transaction.rollback();
            }

            BasicResult checkSessionResult = new BasicResult();
            checkSessionResult.setSuccess(false);

            errorManager.reportError(resourceManger, checkSessionResult, "checkSession.exception",
                ex);
        }

        return sessions;
    }

    /**
     * gets a user from DB given the credentials.
     * 
     * @param login The user credentials.
     * @param session The Hibernate session.
     * @return The user.
     */
    private UserHB getUser(final Login login, final Session session)
    {
        // Checking if a user exists for the given credentials (remeber to check that the user
        String customQuery = "SELECT * FROM user WHERE user= :user AND password= :password";

        Query query = session.createSQLQuery(customQuery).addEntity(UserHB.class);
        query.setString("user", login.getUser());
        query.setString("password", login.getPassword());
        return (UserHB) query.uniqueResult();
    }

    /**
     * Gets the user from DB using the authentication Token information.
     * 
     * @param login The object with the token information.
     * @param session The Hibernate session.
     * @return The user.
     */
    private UserHB getUserUsingToken(final Login login, final Session session) throws Exception
    {
        // Get token data
        String[] token = TokenUtils.getTokenFields(login.getAuthToken());
        String tokenUser = TokenUtils.getTokenUser(token);
        long tokenExpiration = TokenUtils.getTokenExpiration(token);
        String tokenSignature = TokenUtils.getTokenSignature(token);

        // Check token expiration
        if (TokenUtils.isTokenExpired(tokenExpiration))
        {
            throw new Exception("Authentication token has expired.");
        }

        // Get the token user from db
        String customQuery = "SELECT * FROM user WHERE user= :user";
        Query query = session.createSQLQuery(customQuery).addEntity(UserHB.class);
        query.setString("user", tokenUser);
        UserHB userHB = (UserHB) query.uniqueResult();

        if (userHB != null)
        {
            // Validate credentials with the token
            String signature =
                TokenUtils.makeTokenSignature(tokenExpiration, userHB.getUser(),
                    userHB.getPassword());

            if (!signature.equals(tokenSignature))
            {
                return null;
            }
        }

        return userHB;
    }

}
