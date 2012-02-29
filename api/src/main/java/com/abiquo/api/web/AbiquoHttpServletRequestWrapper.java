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
package com.abiquo.api.web;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;

/**
 * Since the class {@link VersionCheckerFilter} needs to change the headers
 * "Accept" and "Content-type" in some requests, we need a class to override
 * the value of these headers.
 * 
 * @author jdevesa@abiquo.com
 */
public class AbiquoHttpServletRequestWrapper extends HttpServletRequestWrapper
{
    /**
     * Map that will store the modified values.
     */
    private Map<String, String> headerMap;

    /**
     * Default constructor. It calls the 'super' method and it 
     * initializes the hash map.
     * 
     * @param request instance of the {@link HttpServlerRequest}
     */
    public AbiquoHttpServletRequestWrapper(HttpServletRequest request)
    {
        super(request);
        headerMap = new HashMap<String, String>();
    }
    
    /* (non-Javadoc)
     * @see javax.servlet.http.HttpServletRequestWrapper#getHeader(java.lang.String)
     */
    public String getHeader(final String key)
    {
        String value;
        if ((value = headerMap.get(key)) != null)
        {
            return value;
        }
        else
        {
            return ((HttpServletRequest)getRequest()).getHeader(key);
        }
    }
    
    /**
     * Set a new value for a header.
     * 
     * @param key header key
     * @param value header value.
     */
    public void setHeader(final String key, final String value)
    {
        headerMap.put(key, value);
    }

}
