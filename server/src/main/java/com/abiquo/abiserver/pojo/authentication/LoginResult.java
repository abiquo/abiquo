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

package com.abiquo.abiserver.pojo.authentication;

import java.util.ArrayList;

import com.abiquo.abiserver.pojo.authorization.Resource;
import com.abiquo.abiserver.pojo.user.User;

/**
 * This class defines the three objects that the client needs after performin a successful login
 * action
 * 
 * @author Oliver
 */
public class LoginResult
{
    private UserSession session;

    private User user;

    private ArrayList<Resource> clientResources;

    public LoginResult()
    {
        session = new UserSession();
        user = new User();
        clientResources = new ArrayList<Resource>();
    }

    public UserSession getSession()
    {
        return session;
    }

    public void setSession(UserSession session)
    {
        this.session = session;
    }

    public User getUser()
    {
        return user;
    }

    public void setUser(User user)
    {
        this.user = user;
    }

    public ArrayList<Resource> getClientResources()
    {
        return clientResources;
    }

    public void setClientResources(ArrayList<Resource> clientResources)
    {
        this.clientResources = clientResources;
    }

}
