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

import java.util.List;
import java.util.Properties;

import org.apache.wink.server.handlers.HandlersChain;
import org.apache.wink.server.handlers.MessageContext;
import org.apache.wink.server.handlers.RequestHandler;
import org.springframework.security.GrantedAuthority;
import org.springframework.security.context.SecurityContextHolder;

import com.abiquo.api.services.UserService;
import com.abiquo.api.spring.BeanLoader;
import com.abiquo.api.spring.security.AbiquoUserDetails;
import com.abiquo.server.core.enterprise.User.AuthType;

/**
 * Handler with a list of handlers. This handlers check if the request uri path matches with the
 * method <code>matches</code> of the handlers in the list. The first handler who match will be who
 * handle the request.
 * 
 * @author scastro
 */
public class SecurityPathRequestHandler implements RequestHandler
{
    private UserService userService;

    private List<SecurityPathRequestHandler> pathHandlers;

    public SecurityPathRequestHandler()
    {
        // Must be unused. Exists only to be overrided
    }

    public SecurityPathRequestHandler(final List<SecurityPathRequestHandler> handlers)
    {
        pathHandlers = handlers;
    }

    @Override
    public void init(final Properties props)
    {
    }

    /**
     * Check if the path matches with the handler waits
     * 
     * @param path request uri path
     * @return true if matches, else false
     */
    public boolean matches(final String path)
    {
        return false;
    }

    /**
     * @see org.apache.wink.server.handlers.RequestHandler#handleRequest(org.apache.wink.server.handlers.MessageContext,
     *      org.apache.wink.server.handlers.HandlersChain)
     */
    @Override
    public void handleRequest(final MessageContext context, final HandlersChain chain)
        throws Throwable
    {
        String path = context.getUriInfo().getPath();
        for (SecurityPathRequestHandler handler : pathHandlers)
        {
            if (handler.matches(path))
            {
                handler.handleRequest(context, chain);
                return;
            }
        }
        // if no handler match we must continue
        chain.doChain(context);
    }

    /**
     * Get ther user login info
     * 
     * @return array with the userName, the authentication type and the privileges string array of
     *         the user login
     */
    protected Object[] getCurrentLoginInfo()
    {
        String authtype = "";
        String username = "";
        String[] privileges = null;
        if (SecurityContextHolder.getContext().getAuthentication().getPrincipal() instanceof AbiquoUserDetails)
        {
            AbiquoUserDetails details =
                (AbiquoUserDetails) SecurityContextHolder.getContext().getAuthentication()
                    .getPrincipal();

            AuthType authType =
                AuthType.valueOf(details.getAuthType() != null ? details.getAuthType()
                    : AuthType.ABIQUO.name());
            authtype = authType.name();
            username = details.getUsername();
            GrantedAuthority[] autorities = details.getAuthorities();
            privileges = new String[autorities.length];
            for (int i = 0; i < autorities.length; i++)
            {
                privileges[i] = autorities[i].getAuthority().replaceFirst("ROLE_", "");
            }
        }
        else
        { // Backward compatibility and bzngine
            username = SecurityContextHolder.getContext().getAuthentication().getName();
            throw new RuntimeException("The authentication was not an AbiquoUserDetails but "
                + SecurityContextHolder.getContext().getAuthentication().getClass()
                    .getCanonicalName());
        }

        return new Object[] {username, authtype, privileges};
    }

    // Service getters and setters
    public UserService getUserService()
    {
        if (userService == null)
        {
            userService = BeanLoader.getInstance().getBean(UserService.class);
        }
        return userService;
    }
}
