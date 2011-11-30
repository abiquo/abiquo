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

package com.abiquo.api.resources;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashSet;

import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.OPTIONS;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.ResponseBuilder;

import org.apache.wink.common.internal.ResponseImpl.ResponseBuilderImpl;
import org.apache.wink.server.handlers.MessageContext;

public abstract class AbstractResource
{
    public static final String FLAT_MEDIA_TYPE = "application/flat+xml";

    public static final String LINK_MEDIA_TYPE = "application/link+xml";

    public static final String START_WITH = "startwith";

    public static final String BY = "by";

    public static final String FILTER = "has";

    public static final String LIMIT = "limit";

    public static final String ASC = "asc";
    
    public static final String TYPE = "type";

    public static final Integer DEFAULT_PAGE_LENGTH = 25;

    public static final String DEFAULT_PAGE_LENGTH_STRING = "25";

    private static Collection<Class> REST = new ArrayList<Class>()
    {
        {
            add(GET.class);
            add(POST.class);
            add(PUT.class);
            add(DELETE.class);
            add(OPTIONS.class);
        }
    };

    @OPTIONS
    public Response options(@Context MessageContext context)
    {
        ResponseBuilder builder = new ResponseBuilderImpl();

        String methodsAllowed = getMethodsAllowed();

        builder.header("Allow", methodsAllowed);

        return builder.build();
    }

    protected String getMethodsAllowed()
    {
        Collection<String> allowed = new LinkedHashSet<String>();
        for (Method method : this.getClass().getMethods())
        {
            for (Annotation anno : method.getAnnotations())
            {
                if (REST.contains(anno.annotationType()))
                {
                    allowed.add(anno.annotationType().getSimpleName());
                    break;
                }
            }
        }
        return allowed.toString();
    }

}
