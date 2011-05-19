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

package com.abiquo.api.spring.security.onetimetoken.filter;

import java.io.IOException;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.security.Authentication;
import org.springframework.security.AuthenticationException;
import org.springframework.security.AuthenticationManager;
import org.springframework.security.context.SecurityContextHolder;
import org.springframework.security.ui.AuthenticationDetailsSource;
import org.springframework.security.ui.SpringSecurityFilter;
import org.springframework.security.ui.WebAuthenticationDetailsSource;
import org.springframework.util.Assert;

import com.abiquo.api.spring.security.onetimetoken.token.OneTimeTokenToken;

public class OneTimeTokenFilter extends SpringSecurityFilter implements InitializingBean
{
    /**
     * In case we decide to use a specific charset.
     */
    private String credentialsCharset = "UTF-8";

    public String getCredentialsCharset()
    {
        return credentialsCharset;
    }

    public void setCredentialsCharset(String credentialsCharset)
    {
        this.credentialsCharset = credentialsCharset;
    }

    /**
     * Details of the authentication.
     */
    private AuthenticationDetailsSource authenticationDetailsSource =
        new WebAuthenticationDetailsSource();

    private AuthenticationManager authenticationManager;

    public AuthenticationManager getAuthenticationManager()
    {
        return authenticationManager;
    }

    public void setAuthenticationManager(AuthenticationManager authenticationManager)
    {
        this.authenticationManager = authenticationManager;
    }

    /**
     * @see org.springframework.security.ui.SpringSecurityFilter#getOrder()
     */
    @Override
    public int getOrder()
    {
        return 0;
    }

    /**
     * @see org.springframework.security.ui.SpringSecurityFilter#doFilterHttp(javax.servlet.http.HttpServletRequest,
     *      javax.servlet.http.HttpServletResponse, javax.servlet.FilterChain)
     */
    @Override
    protected void doFilterHttp(HttpServletRequest request, HttpServletResponse response,
        FilterChain chain) throws IOException, ServletException
    {

        String header = request.getHeader("Authorization");

        if (logger.isDebugEnabled())
        {
            logger.debug("Authorization header: " + header);
        }
        // one time tokens starts with OneTime
        if ((header != null) && header.startsWith("OneTime "))
        {
            String base64Token = header.substring(8);

            OneTimeTokenToken authRequest = new OneTimeTokenToken(base64Token);
            authRequest.setDetails(authenticationDetailsSource.buildDetails(request));

            Authentication authResult;

            try
            {
                authResult = authenticationManager.authenticate(authRequest);
            }
            catch (AuthenticationException failed)
            {
                // Authentication failed
                if (logger.isDebugEnabled())
                {
                    logger.debug("Authentication request for chef:  failed: " + failed.toString());
                }

                SecurityContextHolder.getContext().setAuthentication(null);

                chain.doFilter(request, response);

                return;
            }

            // Authentication success
            if (logger.isDebugEnabled())
            {
                logger.debug("Authentication success: " + authResult.toString());
            }

            SecurityContextHolder.getContext().setAuthentication(authResult);

        }

        chain.doFilter(request, response);
    }

    /**
     * @see org.springframework.beans.factory.InitializingBean#afterPropertiesSet()
     */
    @Override
    public void afterPropertiesSet() throws Exception
    {
        Assert.notNull(authenticationManager, "authenticationManager can't be null!");
    }

}
