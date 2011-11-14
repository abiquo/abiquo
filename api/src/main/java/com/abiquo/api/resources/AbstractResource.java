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

import static com.abiquo.api.util.URIResolver.buildPath;
import static com.abiquo.api.util.URIResolver.resolveFromURI;

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
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.ResponseBuilder;

import org.apache.wink.common.internal.ResponseImpl.ResponseBuilderImpl;
import org.apache.wink.server.handlers.MessageContext;

import com.abiquo.api.exceptions.APIError;
import com.abiquo.api.exceptions.NotFoundException;
import com.abiquo.model.rest.RESTLink;

public abstract class AbstractResource
{
    public static final String FLAT_MEDIA_TYPE = "application/flat+xml";

    public static final String LINK_MEDIA_TYPE = "application/link+xml";

    public static final String START_WITH = "startwith";

    public static final String BY = "by";

    public static final String FILTER = "has";

    public static final String LIMIT = "limit";

    public static final String ASC = "asc";

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
    public Response options(@Context final MessageContext context)
    {
        final ResponseBuilder builder = new ResponseBuilderImpl();

        final String methodsAllowed = getMethodsAllowed();

        builder.header("Allow", methodsAllowed);

        return builder.build();
    }

    protected String getMethodsAllowed()
    {
        final Collection<String> allowed = new LinkedHashSet<String>();
        for (final Method method : this.getClass().getMethods())
        {
            for (final Annotation anno : method.getAnnotations())
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

    /**
     * Extracts an Id from the link with the given rel.
     * 
     * @param link where the id.
     * @param path the resource.
     * @param param the parameter.
     * @param key the rel.
     * @param error what do we output.
     * @return Integer
     */
    protected Integer getLinkId(final RESTLink link, final String path, final String param,
        final String key, final APIError error)
    {
        if (link == null)
        {
            throw new NotFoundException(error);
        }

        final String buildPath = buildPath(path, param);
        final MultivaluedMap<String, String> values = resolveFromURI(buildPath, link.getHref());

        if (values == null || !values.containsKey(key))
        {
            throw new NotFoundException(error);
        }

        return Integer.valueOf(values.getFirst(key));
    }
    
}
