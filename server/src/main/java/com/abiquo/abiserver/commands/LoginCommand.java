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
package com.abiquo.abiserver.commands;

import com.abiquo.abiserver.pojo.authentication.Login;
import com.abiquo.abiserver.pojo.authentication.LoginResult;
import com.abiquo.abiserver.pojo.authentication.UserSession;
import com.abiquo.abiserver.pojo.result.BasicResult;
import com.abiquo.abiserver.pojo.result.DataResult;

public interface LoginCommand
{

    /**
     * Performs a Login action. Uses Proxy to be able to access to Authentication Manager
     * 
     * @param loginData necessary data to perform a login action
     * @return a DataResult object, containing a LoginResult with the user's session, information
     *         and client resources
     */
    @SuppressWarnings("unchecked")
    public abstract DataResult<LoginResult> login(Login loginData);

    /**
     * Performs a Logout action. Uses the Proxy to be able to access to Authentication Manager
     * 
     * @param session The session that we want to logout from the server
     * @return
     */
    public abstract BasicResult logout(UserSession session);

}
