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

package com.abiquo.abiserver.commands.test.data;

import static org.junit.Assert.fail;

import com.abiquo.abiserver.pojo.authentication.Login;
import com.abiquo.abiserver.pojo.authentication.LoginResult;
import com.abiquo.abiserver.pojo.authentication.UserSession;
import com.abiquo.abiserver.pojo.result.DataResult;
import com.abiquo.abiserver.services.flex.LoginService;

public class LoginProvider
{
    private static UserSession user = null;

    /**
     * Private class constructor
     */
    private LoginProvider()
    {
        // Private Constructor
    }

    /**
     * Method that log in abiCloud
     */
    @SuppressWarnings("unchecked")
    public static void doLogin()
    {
        final LoginService loginServices = new LoginService();
        final Login login = new Login();
        login.setUser("admin");
        login.setPassword("c69a39bd64ffb77ea7ee3369dce742f3");
        login.setLocale("en_EN");

        final DataResult<LoginResult> dataResult = (DataResult<LoginResult>) loginServices.login(login);

        if (dataResult.getSuccess())
        {
            user = dataResult.getData().getSession();
        }
        else
        {
            fail("Invalid Login information");
        }
    }

    /**
     * Method to logout from abiCloud
     */
    public static void doLogout()
    {
        final LoginService loginService = new LoginService();
        loginService.logout(user);
    }

    public static UserSession getUser()
    {
        return user;
    }

}
