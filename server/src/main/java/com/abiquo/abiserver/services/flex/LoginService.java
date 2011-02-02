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

package com.abiquo.abiserver.services.flex;

import com.abiquo.abiserver.business.BusinessDelegateProxy;
import com.abiquo.abiserver.business.UserSessionException;
import com.abiquo.abiserver.commands.LoginCommand;
import com.abiquo.abiserver.commands.impl.LoginCommandImpl;
import com.abiquo.abiserver.pojo.authentication.Login;
import com.abiquo.abiserver.pojo.authentication.UserSession;
import com.abiquo.abiserver.pojo.result.BasicResult;

/**
 * This class defines all services related to login and logout processes
 * 
 * @author Oliver
 */

public class LoginService
{

    private final LoginCommand loginCommand;

    public LoginService()
    {
        loginCommand = new LoginCommandImpl();
    }

    protected LoginCommand proxyCommand(UserSession userSession)
    {
        return BusinessDelegateProxy.getInstance(userSession, loginCommand, LoginCommand.class);
    }

    /**
     * Performs a login action against the Authentication Manager
     * 
     * @param loginData
     * @return If login was successfully, a DataResult<LoginResult> object, contain the user's
     *         session, data and client resources If not, a BasicResult object, with the error
     *         message
     */

    public BasicResult login(final Login loginData)
    {
        return loginCommand.login(loginData);
    }

    public BasicResult login(final String token)
    {
        return null;
    }

    /**
     * Performs a logout action over a session
     * 
     * @param session
     * @return
     */
    public BasicResult logout(final UserSession session)
    {

        LoginCommand command = proxyCommand(session);
        BasicResult br = new BasicResult();

        try
        {
            br = command.logout(session);
        }
        catch (Exception e)
        {
            // It doesn't matter the usersessionException. It's a logout!
            br.setSuccess(Boolean.TRUE);
        }

        return br;

    }

}
