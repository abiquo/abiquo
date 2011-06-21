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
package com.abiquo.api.web.filters;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;

import org.springframework.util.StringUtils;

/**
 * {@link HttpServletRequest} that ignores the trailing '/'.
 * 
 * @author scastro
 */
public class SecuredURLRequest extends HttpServletRequestWrapper
{
    public SecuredURLRequest(final HttpServletRequest request)
    {
        super(request);
    }

    @Override
    public String getPathInfo()
    {
        return removeLastSlash(super.getPathInfo());
    }

    @Override
    public String getRequestURI()
    {
        // Only remove the trailing slash if we don't have a PathInfo
        String pathInfo = super.getPathInfo();
        if (!StringUtils.hasText(pathInfo))
        {
            return removeLastSlash(super.getRequestURI());
        }

        return super.getRequestURI();
    }

    private String removeLastSlash(final String str)
    {
        if (StringUtils.hasText(str) && str.endsWith("/") && str.length() != 1)
        {
            return str.substring(0, str.length() - 1);
        }
        return str;
    }

}
