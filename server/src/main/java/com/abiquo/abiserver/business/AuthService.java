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

package com.abiquo.abiserver.business;

import java.util.List;

import com.abiquo.abiserver.business.authentication.AuthenticationManagerApi;
import com.abiquo.abiserver.business.authentication.AuthenticationManagerDB;
import com.abiquo.abiserver.business.authentication.IAuthenticationManager;
import com.abiquo.abiserver.business.authorization.IAuthorizationManager;
import com.abiquo.abiserver.config.AbiConfigManager;
import com.abiquo.abiserver.pojo.authentication.Login;
import com.abiquo.abiserver.pojo.authentication.LoginResult;
import com.abiquo.abiserver.pojo.authentication.UserSession;
import com.abiquo.abiserver.pojo.result.BasicResult;
import com.abiquo.abiserver.pojo.result.DataResult;

/**
 * This class provides authentication and authorization features, making a bridge between a class
 * that needs these features, and the classes that provides them.
 * 
 * @author Oliver
 */

public class AuthService
{

    // Singleton class
    private static AuthService instance;

    // Object that provides the authentication features
    private IAuthenticationManager authenticationManager;

    // Object that provides the authorization features
    private IAuthorizationManager authorizationManager;

    private AuthService()
    {
        // TODO Instantiate convenient authentication and authorization managers
        String securityMode = AbiConfigManager.getInstance().getAbiConfig().getAbiquoSecurityMode();

        // FIXME DB is deprecated we should always use API authentication
        if ("ldap".equals(securityMode))
        {
            authenticationManager = new AuthenticationManagerApi();
        }
        else
        {
            authenticationManager = new AuthenticationManagerDB();
        }
    }

    public static AuthService getInstance()
    {
        if (instance == null)
        {
            instance = new AuthService();
        }

        return instance;
    }

    /**
     * Calls the Authentication Manager to perform a controlled login.
     * 
     * @param login
     * @return A Session object with session information if the login process had success Null if
     *         login action was unsuccessful
     */
    public DataResult<LoginResult> doLogin(final Login login)
    {
        return authenticationManager.doLogin(login);
    }

    /**
     * Calls the Authentication Manager to perform a controlled logout
     * 
     * @param session The session that wants to logout from the server
     * @return
     */
    public BasicResult doLogout(final UserSession session)
    {
        return authenticationManager.doLogout(session);
    }

    /**
     * Calls the Authentication Manager to check if a session is valid
     * 
     * @param session The session to check
     * @return true if a session is still valid
     */
    public BasicResult checkSession(final UserSession session)
    {
        return authenticationManager.checkSession(session);
    }

    /**
     * Calls the Authorization Manager to check if this method can be used without a session
     * 
     * @param methodName
     * @return true if this method can be used without start a session
     */
    public boolean doAuthorization(final String methodName)
    {
        return authorizationManager.checkAuthorization(null, methodName);
    }

    /**
     * Find all sessions for the specified user.
     * 
     * @param username The username to check.
     * @return The current sessions of the specified user.
     */
    public List<UserSession> findAllSessions(final String username)
    {
        return authenticationManager.findAllSessions(username);
    }

    /**
     * Check if a user is logged in.
     * 
     * @param username The name of the user to check.
     * @return A boolean indicating if the specified user is logged in.
     */
    public boolean isLoggedIn(final String username)
    {
        return authenticationManager.isLoggedIn(username);
    }

    /**
     * Calls the Authorization Manager to check if this session is authorized to use this method
     * 
     * @param session
     * @param methodName
     * @return true if this session is authorized to use this method
     */
    public boolean doAuthorization(final UserSession session, final String methodName)
    {
        // TODO return authorizationManager.checkAuthorization(session, methodName);
        return true;
    }

    /**
     * Checks that the user has the appropiate permissions to perform the requested operation.
     * 
     * @param session The User session.
     * @param methodName The requested operation.
     * @throws UserSessionException If user does not have the appropiate permissions.
     */
    public void checkUserPermissions(final UserSession session, final String methodName)
        throws UserSessionException
    {
        BasicResult checkResult = checkSession(session);
        if (!checkResult.getSuccess())
        {
            throw new UserSessionException(checkResult);
        }

        boolean isAuthorized = AuthService.getInstance().doAuthorization(session, methodName);
        if (!isAuthorized)
        {
            checkResult.setSuccess(false);
            checkResult.setResultCode(BasicResult.NOT_AUTHORIZED);

            throw new UserSessionException(checkResult);
        }
    }

}
