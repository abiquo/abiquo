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

/**
 * 
 */
package com.abiquo.api.spring.security;

import static com.abiquo.api.common.Assert.assertSize;
import static org.testng.Assert.assertTrue;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.context.SecurityContextHolder;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.support.DependencyInjectionTestExecutionListener;
import org.springframework.test.context.testng.AbstractTestNGSpringContextTests;
import org.testng.annotations.Test;

import com.abiquo.api.common.BasicUserAuthentication;
import com.abiquo.api.common.SysadminAuthentication;
import com.abiquo.model.rest.RESTLink;

/**
 * @author scastro
 */
@TestExecutionListeners(DependencyInjectionTestExecutionListener.class)
@ContextConfiguration("classpath:springresources/security-url-test-beans.xml")
public class SecuredURLProcessingTest extends AbstractTestNGSpringContextTests
{
    @Autowired
    private URLAuthenticator urlAuthenticator;

    private void loginAsSysAdmin()
    {
        SecurityContextHolder.getContext().setAuthentication(new SysadminAuthentication());
    }

    private void loginAsBasicUser()
    {
        SecurityContextHolder.getContext().setAuthentication(new BasicUserAuthentication());
    }

    @Test
    public void testPermissions()
    {
        loginAsSysAdmin();
        assertTrue(urlAuthenticator.checkPermissions("http://localhost:80/api/admin/datacenters"));
    }

    @Test
    public void testLinksPermissions()
    {
        loginAsSysAdmin();
        List<RESTLink> links = new ArrayList<RESTLink>();
        // allowed links
        links.add(new RESTLink("", "http://localhost:80/api/"));
        links.add(new RESTLink("", "http://localhost:80/api/admin/datacenters"));
        links.add(new RESTLink("", "http://localhost:80/api/admin/enterprises"));
        // non allowed links
        links.add(new RESTLink("", "http://localhost:80/api/admin/undefined"));
        assertSize(urlAuthenticator.checkAuthLinks(links), 3);
    }

    @Test
    public void testLinksPermissionsForUserWithoutPermissions()
    {
        loginAsBasicUser();
        List<RESTLink> links = new ArrayList<RESTLink>();
        links.add(new RESTLink("", "http://localhost:80/api/admin/datacenters"));
        links.add(new RESTLink("", "http://localhost:80/api/admin/enterprises"));
        links.add(new RESTLink("", "http://localhost:80/api/admin/undefined"));
        links.add(new RESTLink("", "http://localhost:80/api/admin/undefined"));
        assertSize(urlAuthenticator.checkAuthLinks(links), 0);
    }
}
