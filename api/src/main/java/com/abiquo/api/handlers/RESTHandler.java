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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.context.support.WebApplicationContextUtils;

import com.abiquo.api.util.AbiquoLinkBuildersFactory;
import com.abiquo.api.util.IRESTBuilder;
import com.abiquo.model.transport.AcceptedRequestDto;
import com.abiquo.model.transport.MovedPermanentlyDto;
import com.abiquo.model.transport.SeeOtherDto;
import com.abiquo.model.transport.SingleResourceTransportDto;
import com.abiquo.model.transport.WrapperDto;
import com.google.common.collect.Iterables;

public class RESTHandler extends CheckLocationHeaderHandler
{
    protected Class<IRESTBuilder> REST_BUILDER_INTERFACE = IRESTBuilder.class;

    private static final Logger LOGGER = LoggerFactory.getLogger(RESTHandler.class);

    @Override
    public void handleRequest(final MessageContext context)
    {
        LinkBuilders builder = new AbiquoLinkBuildersFactory(context);
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

        searchResult
            .setInvocationParameters(newParameters.toArray(new Object[newParameters.size()]));

        context.setAttribute(SearchResult.class, searchResult);
        createRESTBuilder(context, builder);
    }

    @Override
    public void handleResponse(final MessageContext context) throws Throwable
    {
        if (context.getResponseStatusCode() == HttpServletResponse.SC_OK
            && context.getResponseEntity() != null
            && context.getResponseEntity() instanceof SeeOtherDto)
        {
            SeeOtherDto dto = (SeeOtherDto) context.getResponseEntity();

            ResponseBuilder builder = new ResponseBuilderImpl();
            builder.location(new URI(dto.getLocation()));
            builder.status(HttpServletResponse.SC_SEE_OTHER);

            context.setResponseStatusCode(HttpServletResponse.SC_SEE_OTHER);
            context.setResponseEntity(builder.build());
        }
        // If the entity is the appropriate we return a 202
        else if (context.getResponseStatusCode() == HttpServletResponse.SC_OK
            && context.getResponseEntity() != null
            && context.getResponseEntity() instanceof AcceptedRequestDto)
        {
            context.setResponseStatusCode(HttpServletResponse.SC_ACCEPTED);
        }
        else if (context.getResponseStatusCode() == HttpServletResponse.SC_OK
            && context.getResponseEntity() != null
            && context.getResponseEntity() instanceof MovedPermanentlyDto)
        {
            context.setResponseStatusCode(HttpServletResponse.SC_MOVED_PERMANENTLY);
            ResponseBuilder builder = new ResponseBuilderImpl();
            builder.location(new URI(((MovedPermanentlyDto) context.getResponseEntity())
                .getLocation().getHref()));
            builder.entity(context.getResponseEntity());
            builder.status(HttpServletResponse.SC_MOVED_PERMANENTLY);
            context.setResponseEntity(builder.build());
        }
        else if (context.getResponseStatusCode() == HttpServletResponse.SC_OK
            && RequestMethod.valueOf(context.getRequest().getMethod()) == RequestMethod.POST)
        {
            context.setResponseStatusCode(HttpServletResponse.SC_CREATED);
            Object entity = context.getResponseEntity();

            if (entity instanceof SingleResourceTransportDto)
            {
                SingleResourceTransportDto resource = (SingleResourceTransportDto) entity;

                ResponseBuilder builder = new ResponseBuilderImpl();
                if (!(entity instanceof WrapperDto))
                {
                    if (resource.getEditLink() != null)
                    {
                        builder.location(new URI(resource.getEditLink().getHref()));
                    }
                    else
                    {
                        LOGGER.warn("The object returned by the POST "
                            + "operation does not have an edit link");
                    }
                }
                builder.entity(resource);
                builder.status(HttpServletResponse.SC_CREATED);

                context.setResponseEntity(builder.build());
            }
        }

        super.handleRequest(context);
    }

    protected void createRESTBuilder(final MessageContext context, final LinkBuilders linksProcessor)
    {
        ServletContext servletContext = context.getAttribute(ServletContext.class);
        Map<String, IRESTBuilder> beans =
            WebApplicationContextUtils.getWebApplicationContext(servletContext).getBeansOfType(
                IRESTBuilder.class);

        IRESTBuilder builder = Iterables.get(beans.values(), 0);
        context.setAttribute(REST_BUILDER_INTERFACE, builder.injectProcessor(linksProcessor));
    }
}
