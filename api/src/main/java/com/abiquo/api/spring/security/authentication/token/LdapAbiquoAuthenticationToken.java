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

package com.abiquo.api.spring.security.authentication.token;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.naming.directory.DirContext;

import org.springframework.security.Authentication;
import org.springframework.security.GrantedAuthority;
import org.springframework.security.GrantedAuthorityImpl;
import org.springframework.security.providers.UsernamePasswordAuthenticationToken;

public class LdapAbiquoAuthenticationToken extends UsernamePasswordAuthenticationToken
{
    /**
     * 
     */
    private static final long serialVersionUID = -2237530505841363367L;

    /**
     * 
     */
    private final Authentication auth;

    transient private DirContext context;

    private final List<GrantedAuthority> authorities = new ArrayList<GrantedAuthority>();

    /**
     * Construct a new LdapAuthenticationToken, using an existing Authentication object and granting
     * all users a default authority.
     * 
     * @param auth
     * @param defaultAuthority
     */
    public LdapAbiquoAuthenticationToken(Authentication auth, GrantedAuthority defaultAuthority)
    {
        super(auth.getPrincipal(), auth.getCredentials());
        this.auth = auth;
        if (auth.getAuthorities() != null)
        {
            this.authorities.addAll(Arrays.asList(auth.getAuthorities()));
        }
        if (defaultAuthority != null)
        {
            this.authorities.add(defaultAuthority);
        }
        super.setAuthenticated(true);
    }

    /**
     * Construct a new LdapAuthenticationToken, using an existing Authentication object and granting
     * all users a default authority.
     * 
     * @param auth
     * @param defaultAuthority
     */
    public LdapAbiquoAuthenticationToken(Authentication auth, String defaultAuthority)
    {
        this(auth, new GrantedAuthorityImpl(defaultAuthority));
    }

    public LdapAbiquoAuthenticationToken(Authentication auth)
    {
        super(auth.getPrincipal(), auth.getCredentials());
        this.auth = auth;
    }

    @Override
    public GrantedAuthority[] getAuthorities()
    {
        GrantedAuthority[] authoritiesArray = this.authorities.toArray(new GrantedAuthority[0]);
        return authoritiesArray;
    }

    public void addAuthority(GrantedAuthority authority)
    {
        this.authorities.add(authority);
    }

    @Override
    public Object getCredentials()
    {
        return auth.getCredentials();
    }

    @Override
    public Object getPrincipal()
    {
        return auth.getPrincipal();
    }

    /**
     * Retrieve the LDAP context attached to this user's authentication object.
     * 
     * @return the LDAP context
     */
    public DirContext getContext()
    {
        return context;
    }

    /**
     * Attach an LDAP context to this user's authentication object.
     * 
     * @param context the LDAP context
     */
    public void setContext(DirContext context)
    {
        this.context = context;
    }

}
