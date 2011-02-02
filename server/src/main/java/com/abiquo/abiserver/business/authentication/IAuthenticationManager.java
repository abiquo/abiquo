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

import java.util.List;

import com.abiquo.abiserver.pojo.authentication.Login;
import com.abiquo.abiserver.pojo.authentication.LoginResult;
import com.abiquo.abiserver.pojo.authentication.UserSession;
import com.abiquo.abiserver.pojo.result.BasicResult;
import com.abiquo.abiserver.pojo.result.DataResult;

/**
 * These interface defines the methods that a class that wants to provide Authentication features
 * must implement
 * 
 * @author Oliver
 */

public interface IAuthenticationManager
{

    /**
     * Performs a Login action
     * 
     * @param login
     * @return A DataResult object with a LoginResult object containing the user who has logged in
     *         and his session. If there was a problem with the login process, the DataResult will
     *         containing the information with the problem
     */
    public DataResult<LoginResult> doLogin(Login login);

    /**
     * Performs a standard Logout action, destroying the session
     * 
     * @param session to destroy
     * @return A BasicResult object with success = true if the logout action had success
     */
    public BasicResult doLogout(UserSession session);

    /**
     * Checks if a session object is still valid
     * 
     * @param session to check
     * @return A BasicResult containing the result of the check session
     */
    public BasicResult checkSession(UserSession session);

    /**
     * Check if a user is logged in.
     * 
     * @param username The name of the user to check.
     * @return A boolean indicating if the specified user is logged in.
     */
    public boolean isLoggedIn(final String username);

    /**
     * Find all sessions for the specified user.
     * 
     * @param username The username to check.
     * @return The current sessions of the specified user.
     */
    public List<UserSession> findAllSessions(final String username);
}
