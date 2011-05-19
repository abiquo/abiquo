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

package com.abiquo.api.spring.security.provider;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.security.Authentication;
import org.springframework.security.AuthenticationException;
import org.springframework.security.BadCredentialsException;
import org.springframework.security.GrantedAuthority;
import org.springframework.security.GrantedAuthorityImpl;
import org.springframework.security.providers.AuthenticationProvider;
import org.springframework.security.providers.UsernamePasswordAuthenticationToken;
import org.springframework.util.Assert;

import com.abiquo.api.spring.security.onetimetoken.service.OneTimeTokenDetailsService;
import com.abiquo.server.core.enterprise.User.AuthType;

public class OneTimeTokenAuthenticationProvier implements InitializingBean, AuthenticationProvider
{
    /**
     * Service to authenticate with a token.
     */
    private OneTimeTokenDetailsService oneTimeTokenDetailsService;

    public OneTimeTokenDetailsService getOneTimeTokenDetailsService()
    {
        return oneTimeTokenDetailsService;
    }

    public void setOneTimeTokenDetailsService(OneTimeTokenDetailsService oneTimeTokenDetailsService)
    {
        this.oneTimeTokenDetailsService = oneTimeTokenDetailsService;
    }

    /**
     * @see org.springframework.security.providers.AuthenticationProvider#authenticate(org.springframework.security.Authentication)
     */
    @Override
    public Authentication authenticate(Authentication authentication)
        throws AuthenticationException
    {
        if (!supports(authentication.getClass()))
        {
            return null;
        }

        boolean success = this.getOneTimeTokenDetailsService().checkToken(authentication);

        if (success)
        {
            UsernamePasswordAuthenticationToken auth =
                new UsernamePasswordAuthenticationToken(null,
                    authentication.getCredentials(),
                    new GrantedAuthority[] {new GrantedAuthorityImpl(AuthType.ONE_TIME.name())});

            return auth;
        }
        throw new BadCredentialsException("Token invalied");
    }

    /**
     * @see org.springframework.security.providers.AuthenticationProvider#supports(java.lang.Class)
     */
    @Override
    public boolean supports(Class authentication)
    {
        return UsernamePasswordAuthenticationToken.class.isAssignableFrom(authentication);
    }

    /**
     * @see org.springframework.beans.factory.InitializingBean#afterPropertiesSet()
     */
    @Override
    public void afterPropertiesSet() throws Exception
    {
        Assert.notNull(this.oneTimeTokenDetailsService, "A userDetailsService must be set");
    }

}
