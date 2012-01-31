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

import javax.annotation.PostConstruct;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.Authentication;
import org.springframework.security.ConfigAttributeDefinition;
import org.springframework.security.context.SecurityContextHolder;
import org.springframework.security.intercept.web.DefaultFilterInvocationDefinitionSource;
import org.springframework.security.intercept.web.FilterSecurityInterceptor;
import org.springframework.security.vote.AccessDecisionVoter;
import org.springframework.security.vote.AffirmativeBased;
import org.springframework.security.vote.RoleVoter;
import org.springframework.stereotype.Service;

import com.abiquo.model.rest.RESTLink;

/**
 * This class eliminates from the dto the links that the user who made the request have no
 * permission to see.
 * 
 * @author sergi.castro@abiquo.com
 * @author serafin.sedano@abiquo.com
 * @author ignasi.barrera@abiquo.com
 */
@Service
public class URLAuthenticator
{

    @Autowired
    private AffirmativeBased accessManager;

    private RoleVoter roleVoter;

    @Autowired
    private FilterSecurityInterceptor filterSecurityInterceptor;

    private enum methods
    {
        GET, POST, PUT, DELETE, OPTIONS
    };

    @PostConstruct
    private void setRoleVoter()
    {
        for (Object o : accessManager.getDecisionVoters())
        {
            if (o instanceof RoleVoter)
            {
                roleVoter = (RoleVoter) o;
                break;
            }
        }
    }

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
     * Check if a url is allowed for logged user. If there is no roleVoter then always show the
     * links.
     * 
     * @param url to check
     * @return true if is allowed, else false
     */
    public boolean checkPermissions(final StringBuffer url, final String baseUri)
    {

        if (roleVoter == null) // No role based security
        {
            return Boolean.TRUE;
        }
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        DefaultFilterInvocationDefinitionSource fids =
            (DefaultFilterInvocationDefinitionSource) filterSecurityInterceptor
                .getObjectDefinitionSource();

        String path = parse(url, baseUri);
        if (StringUtils.isBlank(path)) // The uri is not Abiquo
        {
            return Boolean.TRUE;
        }
        OUTER_LOOP: for (methods m : methods.values())
        {
            ConfigAttributeDefinition config = fids.lookupAttributes(path, m.name());
            if (config != null)
            {
                int result = roleVoter.vote(auth, new Object(), config);
                switch (result)
                {
                    case AccessDecisionVoter.ACCESS_GRANTED:
                        return Boolean.TRUE;

                    case AccessDecisionVoter.ACCESS_DENIED:
                    default:
                        continue OUTER_LOOP;
                }
            }
        }
        return Boolean.FALSE;
    }

    private static String parse(final StringBuffer url, final String baseUri)
    {
        return StringUtils.substringAfterLast(url.toString(), baseUri);
    }
}
