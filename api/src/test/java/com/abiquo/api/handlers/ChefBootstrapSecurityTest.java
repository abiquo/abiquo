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

import static com.abiquo.api.common.UriTestResolver.resolveVirtualMachineBootstrapURI;
import static com.abiquo.api.common.UriTestResolver.resolveVirtualMachineURI;
import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertTrue;

import org.springframework.security.context.SecurityContextHolder;
import org.springframework.security.providers.UsernamePasswordAuthenticationToken;
import org.testng.annotations.Test;

import com.abiquo.api.common.UriTestResolver;
import com.abiquo.api.handlers.path.ChefBootstrapSecurityRequestHandler;
import com.abiquo.api.spring.security.onetimetoken.token.OneTimeTokenToken;
import com.abiquo.testng.TestConfig;

@Test(groups = TestConfig.BASIC_UNIT_TESTS)
public class ChefBootstrapSecurityTest
{
    @Test(groups = TestConfig.BASIC_UNIT_TESTS)
    public void testChefSecurityHandlerAppliesToToken()
    {
        OneTimeTokenToken auth = new OneTimeTokenToken();
        SecurityContextHolder.getContext().setAuthentication(auth);

        String uri = apiRelative(resolveVirtualMachineBootstrapURI(1, 1, 1));
        ChefBootstrapSecurityRequestHandler handler = new ChefBootstrapSecurityRequestHandler();
        assertTrue(handler.appliesTo(uri));
    }

    @Test(groups = TestConfig.BASIC_UNIT_TESTS)
    public void testChefSecurityHandlerDoesNotApplyToToken()
    {
        UsernamePasswordAuthenticationToken auth =
            new UsernamePasswordAuthenticationToken("user", "pass");
        SecurityContextHolder.getContext().setAuthentication(auth);

        String uri = apiRelative(resolveVirtualMachineBootstrapURI(1, 1, 1));
        ChefBootstrapSecurityRequestHandler handler = new ChefBootstrapSecurityRequestHandler();
        assertFalse(handler.appliesTo(uri));
    }

    @Test(groups = TestConfig.BASIC_UNIT_TESTS)
    public void testChefSecurityHandlerDoesNotApplyToOtherURIs()
    {
        OneTimeTokenToken auth = new OneTimeTokenToken();
        SecurityContextHolder.getContext().setAuthentication(auth);

        String uri = apiRelative(resolveVirtualMachineURI(1, 1, 1));
        ChefBootstrapSecurityRequestHandler handler = new ChefBootstrapSecurityRequestHandler();
        assertFalse(handler.appliesTo(uri));
    }

    private static String apiRelative(final String uri)
    {
        return uri.replace(UriTestResolver.API_URI + "/", "");
    }
}
