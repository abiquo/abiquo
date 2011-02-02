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

import java.net.URI;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.core.Response.ResponseBuilder;

import org.apache.wink.common.internal.ResponseImpl.ResponseBuilderImpl;
import org.apache.wink.server.handlers.MessageContext;
import org.apache.wink.server.internal.handlers.CheckLocationHeaderHandler;
import org.apache.wink.server.internal.handlers.SearchResult;
import org.apache.wink.server.utils.LinkBuilders;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.context.support.WebApplicationContextUtils;

import com.abiquo.api.util.IRESTBuilder;
import com.abiquo.api.util.RESTLinkBuildersImpl;
import com.abiquo.model.transport.SingleResourceTransportDto;
import com.google.common.collect.Iterables;

public class RESTHandler extends CheckLocationHeaderHandler
{
    protected static Class REST_BUILDER_INTERFACE = IRESTBuilder.class;

    public void handleRequest(MessageContext context)
    {
        LinkBuilders builder = new RESTLinkBuildersImpl(context);
        context.getAttributes().remove(LinkBuilders.class.getName());
        context.setAttribute(LinkBuilders.class, builder);

        SearchResult searchResult = context.getAttribute(SearchResult.class);
        Object[] parameters = searchResult.getInvocationParameters();
        Collection<Object> newParameters = new ArrayList<Object>();
        if (parameters != null && parameters.length > 0)
        {
            for (Object parameter : parameters)
            {
                if (parameter instanceof LinkBuilders)
                {
                    newParameters.add(builder);
                }
                else
                {
                    newParameters.add(parameter);
                }
            }
        }
        searchResult.setInvocationParameters(newParameters
            .toArray(new Object[newParameters.size()]));

        context.setAttribute(SearchResult.class, searchResult);
        createRESTBuilder(context, builder);
    }

    @Override
    public void handleResponse(MessageContext context) throws Throwable
    {
        if (context.getResponseStatusCode() == HttpServletResponse.SC_OK
            && RequestMethod.valueOf(context.getRequest().getMethod()) == RequestMethod.POST)
        {
            context.setResponseStatusCode(HttpServletResponse.SC_CREATED);
            Object entity = context.getResponseEntity();

            if (entity instanceof SingleResourceTransportDto)
            {
                SingleResourceTransportDto resource = (SingleResourceTransportDto) entity;

                ResponseBuilder builder = new ResponseBuilderImpl();
                builder.location(new URI(resource.getEditLink().getHref()));
                builder.entity(resource);
                builder.status(HttpServletResponse.SC_CREATED);

                context.setResponseEntity(builder.build());
            }
        }

        super.handleRequest(context);
    }

    @SuppressWarnings("unchecked")
    protected void createRESTBuilder(MessageContext context, LinkBuilders linksProcessor)
    {
        ServletContext servletContext = context.getAttribute(ServletContext.class);
        Map<String, IRESTBuilder> beans =
            WebApplicationContextUtils.getWebApplicationContext(servletContext).getBeansOfType(
                IRESTBuilder.class);

        IRESTBuilder builder = Iterables.get(beans.values(), 0);

        context.setAttribute(REST_BUILDER_INTERFACE, builder.injectProcessor(linksProcessor));
    }
}
