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

package com.abiquo.api.util;

import java.util.List;
import java.util.Map;

import org.apache.wink.common.model.synd.SyndLink;
import org.apache.wink.server.internal.handlers.ServerMessageContext;
import org.apache.wink.server.internal.utils.SingleLinkBuilderImpl;
import org.apache.wink.server.utils.LinkBuilders;

import com.abiquo.model.rest.RESTLink;

public class AbiquoLinkBuilder extends SingleLinkBuilderImpl
{

    public AbiquoLinkBuilder(final ServerMessageContext context)
    {
        super(context);
    }

    public RESTLink buildRestLink(final Class< ? > resource, final String rel,
        final Map<String, String> params)
    {

        List<SyndLink> links = setResource(resource).rel(rel).pathParams(params).build(null);
        SyndLink first = links.get(0);
        return new RESTLink(first);
    }

    public RESTLink buildRestLink(final Class< ? > resource, final String subResource,
        final String rel, final String title, final Map<String, String> params)
    {
        RESTLink link = buildRestLink(resource, subResource, rel, params);
        link.setTitle(title);

        return link;

    }

    public RESTLink buildRestLink(final Class< ? > resource, final String subResource,
        final String rel, final Map<String, String> params)
    {
        List<SyndLink> links =
            setResource(resource).rel(rel).pathParams(params).subResource(subResource).build(null);
        return new RESTLink(links.get(0));
    }

    // TODO this method should desapear since action links are not defined this way anymore. Check:
    // http://wiki.abiquo.com/display/Abiquo/API+links+and+MIME+types
    public RESTLink buildActionLink(final Class< ? > resource, final String subResource,
        final String title, final Map<String, String> params)
    {
        List<SyndLink> links =
            setResource(resource).rel("action").pathParams(params).subResource(subResource)
                .build(null);
        SyndLink first = links.get(0);
        RESTLink link = new RESTLink(first);
        link.setTitle(title);
        return link;
    }

    public AbiquoLinkBuilder pathParams(final Map<String, String> params)
    {
        if (params != null && !params.isEmpty())
        {
            for (Map.Entry<String, String> entry : params.entrySet())
            {
                pathParam(entry.getKey(), entry.getValue());
            }
        }
        return this;
    }

    @Override
    public AbiquoLinkBuilder rel(final String rel)
    {
        return (AbiquoLinkBuilder) super.rel(rel);
    }

    public AbiquoLinkBuilder setResource(final Class< ? > resource)
    {
        return (AbiquoLinkBuilder) super.resource(resource);
    }

    public static AbiquoLinkBuilder createBuilder(final LinkBuilders linkProcessor)
    {
        return (AbiquoLinkBuilder) linkProcessor.createSingleLinkBuilder().relativize(false);
    }
}
