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

import static org.testng.Assert.assertEquals;

import org.springframework.security.GrantedAuthority;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import com.abiquo.api.common.AbstractGeneratorTest;
import com.abiquo.server.core.enterprise.Privilege;
import com.abiquo.server.core.enterprise.Role;
import com.abiquo.server.core.enterprise.User;

/**
 * unit tests for the {@link AbiquoUserDetailsService} class.
 * 
 * @author aprete
 */
public class AbiquoUserDetailsServiceTest extends AbstractGeneratorTest
{
    private Privilege p1;

    private Privilege p2;

    private Privilege p3;

    private AbiquoUserDetailsService userDetailsService;

    @BeforeMethod
    public void setUp() throws Exception
    {
        userDetailsService = new AbiquoUserDetailsService();

        p1 = privilegeGenerator.createUniqueInstance();
        p2 = privilegeGenerator.createUniqueInstance();
        p3 = privilegeGenerator.createUniqueInstance();

        setup(p1, p2, p3);
    }

    // @Test
    // public void testLoadUserAuthoritiesWithMultipleRoles() throws Exception
    // {
    // Role role = roleGenerator.createInstance(p1, p2);
    // User user = userGenerator.createInstance(role);
    // setup(role, user.getEnterprise(), user);
    //
    // GrantedAuthority[] privileges = userDetailsService.loadUserAuthorities(user);
    // assertEquals(privileges.length, 2);
    // }
    //
    // @Test
    // public void testLoadUserAuthoritiesWithSingleRole() throws Exception
    // {
    // Role role = roleGenerator.createInstance(p3);
    // User user = userGenerator.createInstance(role);
    // setup(role, user.getEnterprise(), user);
    //
    // GrantedAuthority[] privileges = userDetailsService.loadUserAuthorities(user);
    // assertEquals(privileges.length, 1);
    // }

    @Test
    public void testLoadUserAuthoritiesWithoutRoles() throws Exception
    {
        Role role = roleGenerator.createInstance();
        User user = userGenerator.createInstance(role);
        setup(role, user.getEnterprise(), user);

        GrantedAuthority[] privileges = userDetailsService.loadUserAuthorities(user);
        assertEquals(privileges.length, 0);
    }
}
