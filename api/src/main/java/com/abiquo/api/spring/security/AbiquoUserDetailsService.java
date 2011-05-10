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

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.DataRetrievalFailureException;
import org.springframework.security.GrantedAuthority;
import org.springframework.security.GrantedAuthorityImpl;
import org.springframework.security.userdetails.UserDetails;
import org.springframework.security.userdetails.UserDetailsService;
import org.springframework.security.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.abiquo.server.core.enterprise.EnterpriseRep;
import com.abiquo.server.core.enterprise.User;

/**
 * User details service to load user information from database using the Abiquo persistende layer.
 * 
 * @author ibarrera
 */
@Service("userDetailsService")
@Transactional(readOnly = true, propagation = Propagation.REQUIRED)
public class AbiquoUserDetailsService implements UserDetailsService
{
    /** The default role prefix to use. */
    protected static final String DEFAULT_ROLE_PREFIX = "ROLE_";

    /** The default role prefix to use. */
    protected static final String DEFAULT_ROLE = "ROLE_ABIQUO";

    /** The Enterprise DAO repository. */
    @Autowired
    protected EnterpriseRep enterpriseRep;

    @Override
    public UserDetails loadUserByUsername(final String username) throws UsernameNotFoundException,
        DataAccessException
    {
        User user = null;
        try
        {
            user = enterpriseRep.getUserByUserName(username);
        }
        catch (Exception ex)
        {
            throw new DataRetrievalFailureException("Could not load user information", ex);
        }

        if (user == null)
        {
            throw new UsernameNotFoundException("Invalid credentials");
        }

        // Set user information
        AbiquoUserDetails userDetails = new AbiquoUserDetails();
        userDetails.setUserId(user.getId());
        userDetails.setUsername(user.getNick());
        userDetails.setPassword(user.getPassword());
        userDetails.setActive(user.getActive() == 1);
        userDetails.setEnterpriseId(user.getEnterprise().getId());
        userDetails.setEnterpriseName(user.getEnterprise().getName());
        userDetails.setAuthType(user.getAuthType().name());

        // Set user authorities
        GrantedAuthority[] authorities = loadUserAuthorities(user);
        userDetails.setAuthorities(authorities);

        return userDetails;
    }

    /**
     * Load the granted authorities for the authenticated user.
     * 
     * @param user The authenticated user.
     * @return An array with the granted authorities.
     */
    protected GrantedAuthority[] loadUserAuthorities(final User user)
    {
        String role = DEFAULT_ROLE_PREFIX + user.getRole().getType();
        if (DEFAULT_ROLE != null)
        {
            return new GrantedAuthority[] {new GrantedAuthorityImpl(role),
            new GrantedAuthorityImpl(DEFAULT_ROLE)};
        }
        return new GrantedAuthority[] {new GrantedAuthorityImpl(role)};
    }

}
