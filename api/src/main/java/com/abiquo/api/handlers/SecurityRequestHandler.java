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

package com.abiquo.api.handlers;

import java.util.Properties;

import org.apache.wink.server.handlers.HandlersChain;
import org.apache.wink.server.handlers.MessageContext;
import org.apache.wink.server.handlers.RequestHandler;

import com.abiquo.api.services.UserService;
import com.abiquo.api.spring.BeanLoader;

public class SecurityRequestHandler implements RequestHandler
{
    private UserService userService;

    private static SecurityRequestHandler instance;

    @Override
    public void init(final Properties props)
    {
        instance = this;
    }

    @Override
    public void handleRequest(final MessageContext context, final HandlersChain chain)
        throws Throwable
    {
        // To override
    }

    public static SecurityRequestHandler getInstance()
    {
        return instance;
    }

    public UserService getUserService()
    {
        if (userService == null)
        {
            userService = BeanLoader.getInstance().getBean(UserService.class);
        }
        return userService;
    }

    public void setUserService(final UserService userService)
    {
        this.userService = userService;
    }

}
