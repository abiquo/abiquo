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

package com.abiquo.api.spring.security.onetimetoken.token;

import org.springframework.security.GrantedAuthority;
import org.springframework.security.providers.AbstractAuthenticationToken;

/**
 * One time token. Expendable token.
 * 
 * @author ssedano
 * @version 0.1
 */
public class OneTimeTokenToken extends AbstractAuthenticationToken
{
    private Object principal;

    /**
     * The token.
     */
    private String token;

    public String getToken()
    {
        return token;
    }

    public void setToken(String token)
    {
        this.token = token;
    }

    public OneTimeTokenToken(String token, Object principa, GrantedAuthority[] authorities)
    {
        super(authorities);
        this.token = token;
        super.setAuthenticated(true);
        this.principal = principa;
    }

    public OneTimeTokenToken(String token)
    {
        super(null);
        this.token = token;
        setAuthenticated(false);
    }

    public OneTimeTokenToken()
    {
        this.setAuthenticated(false);
    }

    @Override
    public Object getCredentials()
    {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Object getDetails()
    {
        // TODO Auto-generated method stub
        return super.getDetails();
    }

    @Override
    public Object getPrincipal()
    {
        // TODO Auto-generated method stub
        return this.principal;
    }

    @Override
    public void setAuthenticated(boolean isAuthenticated) throws IllegalArgumentException
    {
        if (isAuthenticated)
        {
            throw new IllegalArgumentException("Cannot set this token to trusted - use constructor containing GrantedAuthority[]s instead");
        }

        super.setAuthenticated(false);
    }
}
