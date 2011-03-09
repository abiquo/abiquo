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
package com.abiquo.api.spring.security;

import org.springframework.security.GrantedAuthority;
import org.springframework.security.userdetails.UserDetails;

/**
 * Stores teh details of an authenticated user.
 * 
 * @author ibarrera
 */
public class AbiquoUserDetails implements UserDetails
{
    /** Serial UID. */
    private static final long serialVersionUID = 1L;

    /** The id of the authenticated user. */
    private Integer userId;

    /** The name of the authenticated user. */
    private String username;

    /** The password of the authenticated user. */
    private String password;

    /** The activation flag of the authenticated user. */
    private boolean active;

    /** The id of the enterprise of the authenticated user. */
    private Integer enterpriseId;

    /** The name of the enterprise of the authenticated user. */
    private String enterpriseName;

    /** The granted authorities for the authenticated user. */
    private GrantedAuthority[] authorities;

    // Implementation of UserDetails interface

    @Override
    public GrantedAuthority[] getAuthorities()
    {
        return authorities;
    }

    @Override
    public String getPassword()
    {
        return password;
    }

    @Override
    public String getUsername()
    {
        return username;
    }

    @Override
    public boolean isAccountNonExpired()
    {
        return active;
    }

    @Override
    public boolean isAccountNonLocked()
    {
        return active;
    }

    @Override
    public boolean isCredentialsNonExpired()
    {
        return active;
    }

    @Override
    public boolean isEnabled()
    {
        return active;
    }

    // Getters and setters

    public Integer getUserId()
    {
        return userId;
    }

    public void setUserId(final Integer userId)
    {
        this.userId = userId;
    }

    public boolean isActive()
    {
        return active;
    }

    public void setActive(final boolean active)
    {
        this.active = active;
    }

    public Integer getEnterpriseId()
    {
        return enterpriseId;
    }

    public void setEnterpriseId(final Integer enterpriseId)
    {
        this.enterpriseId = enterpriseId;
    }

    public String getEnterpriseName()
    {
        return enterpriseName;
    }

    public void setEnterpriseName(final String enterpriseName)
    {
        this.enterpriseName = enterpriseName;
    }

    public void setUsername(final String username)
    {
        this.username = username;
    }

    public void setPassword(final String password)
    {
        this.password = password;
    }

    public void setAuthorities(final GrantedAuthority[] authorities)
    {
        this.authorities = authorities;
    }

}
