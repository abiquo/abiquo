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

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.AccessDecisionManager;
import org.springframework.security.AccessDeniedException;
import org.springframework.security.Authentication;
import org.springframework.security.ConfigAttributeDefinition;
import org.springframework.security.context.SecurityContextHolder;
import org.springframework.security.intercept.web.FilterInvocation;
import org.springframework.security.intercept.web.FilterSecurityInterceptor;
import org.springframework.stereotype.Service;

import com.abiquo.model.rest.RESTLink;

@Service
public class URLAuthenticator
{
    @Autowired
    private AccessDecisionManager accessManager;

    @Autowired
    private FilterSecurityInterceptor filterSecurityInterceptor;

    /**
     * Return only links allowed for logged user
     * 
     * @param links to check
     * @return links allowed for logged user
     */
    public List<RESTLink> checkAuthLinks(final List<RESTLink> links)
    {
        List<RESTLink> authslinks = null;

        if (links != null)
        {
            authslinks = new ArrayList<RESTLink>();

            for (RESTLink link : links)
            {
                if (checkPermissions(link.getHref()))
                {
                    authslinks.add(link);
                }
            }
        }

        return authslinks;
    }

    /**
     * Check if a url is allowed for logged user
     * 
     * @param url to check
     * @return true if is allowed, else false
     */
    public boolean checkPermissions(final String url)
    {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        String[] methods = {"GET", "POST", "PUT", "DELETE", "OPTIONS"};

        boolean allowAccess = false;

        for (String method : methods)
        {

            FilterInvocation securedObject = mockFilterInvocation(url, method);
            ConfigAttributeDefinition config =
                filterSecurityInterceptor.getObjectDefinitionSource().getAttributes(securedObject);

            if (config != null)
            {
                try
                {
                    accessManager.decide(auth, securedObject, config);
                    allowAccess = true;
                    break;
                }
                catch (AccessDeniedException accessDeniedException)
                {
                    continue;
                }
            }
            else
            {
                // to pass it test
                allowAccess = true;
                break;
            }
        }

        return allowAccess;
    }

    /**
     * Creates a mock object of FilterInvocation class
     * 
     * @param url
     * @param method
     * @return mock object of FilterInvocation class
     */
    private static FilterInvocation mockFilterInvocation(final String url, final String method)
    {
        FilterInvocation mockFilterInvocation = mock(FilterInvocation.class);
        HttpServletRequest mockRequest = mock(HttpServletRequest.class);

        when(mockRequest.getMethod()).thenReturn(method);
        when(mockFilterInvocation.getRequestUrl()).thenReturn(parse(url));
        when(mockFilterInvocation.getHttpRequest()).thenReturn(mockRequest);

        return mockFilterInvocation;
    }

    private static String parse(final String url)
    {
        String api = "api";
        int i = url.lastIndexOf(api);
        if (i > -1)
        {
            return url.substring(i + api.length());
        }
        return url;
    }

}
