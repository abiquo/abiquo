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

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.Authentication;
import org.springframework.security.ConfigAttributeDefinition;
import org.springframework.security.context.SecurityContextHolder;
import org.springframework.security.intercept.web.DefaultFilterInvocationDefinitionSource;
import org.springframework.security.intercept.web.FilterSecurityInterceptor;
import org.springframework.security.vote.AccessDecisionVoter;
import org.springframework.security.vote.AffirmativeBased;
import org.springframework.stereotype.Service;

import com.abiquo.model.rest.RESTLink;

@Service
public class URLAuthenticator
{

    @Autowired
    private AffirmativeBased accessManager;

    @Autowired
    private FilterSecurityInterceptor filterSecurityInterceptor;

    private enum methods
    {
        GET, POST, PUT, DELETE, OPTIONS
    };

    /**
     * Return only links allowed for logged user
     * 
     * @param links to check
     * @return links allowed for logged user
     */
    public List<RESTLink> checkAuthLinks(final List<RESTLink> links, final String baseUri)
    {
        List<RESTLink> authslinks = null;
        if (links != null)
        {
            authslinks = new ArrayList<RESTLink>();

            for (RESTLink link : links)
            {
                if (checkPermissions(new StringBuffer(link.getHref()), baseUri))
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
    public boolean checkPermissions(final StringBuffer url, final String baseUri)
    {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        DefaultFilterInvocationDefinitionSource fids =
            (DefaultFilterInvocationDefinitionSource) filterSecurityInterceptor
                .getObjectDefinitionSource();
        Iterator iter = accessManager.getDecisionVoters().iterator();
        String path = parse(url, baseUri);
        if (StringUtils.isBlank(path))
        {
            return Boolean.TRUE;
        }
        for (methods m : methods.values())
        {
            ConfigAttributeDefinition config = fids.lookupAttributes(path, m.name());
            if (config != null)
            {
                INNER_LOOP: while (iter.hasNext())
                {
                    AccessDecisionVoter voter = (AccessDecisionVoter) iter.next();
                    int result = voter.vote(auth, new Object(), config);
                    switch (result)
                    {
                        case AccessDecisionVoter.ACCESS_GRANTED:
                            return Boolean.TRUE;

                        case AccessDecisionVoter.ACCESS_DENIED:
                        default:
                            continue INNER_LOOP;
                    }
                }
            }
        }
        return Boolean.FALSE;
    }

    private static String parse(final StringBuffer url, final String baseUri)
    {
        return StringUtils.substringAfterLast(url.toString(), baseUri);

        // StringBuffer api = new StringBuffer("api");
        // int i = url.lastIndexOf(api.toString());
        // if (i > -1)
        // {
        // return new StringBuffer(url.substring(i + api.length()));
        // }
        // return url;
    }
}
