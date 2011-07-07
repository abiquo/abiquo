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

    public AbiquoLinkBuilder(ServerMessageContext context)
    {
        super(context);
    }

    public RESTLink buildRestLink(Class< ? > resource, String rel, Map<String, String> params)
    {
        
        List<SyndLink> links = setResource(resource).rel(rel).pathParams(params).build(null);
        SyndLink first = links.get(0);
        return new RESTLink(first);
    }

    public RESTLink buildRestLink(Class< ? > resource, String subResource, String rel,
        Map<String, String> params)
    {
        List<SyndLink> links =
            setResource(resource).rel(rel).pathParams(params).subResource(subResource).build(null);
        return new RESTLink(links.get(0));
    }

    public RESTLink buildActionLink(Class< ? > resource, String subResource, String title,
        Map<String, String> params)
    {
        List<SyndLink> links =
            setResource(resource).rel("action").pathParams(params).subResource(subResource).build(
                null);
        SyndLink first = links.get(0);
        RESTLink link = new RESTLink(first);
        link.setTitle(title);
        return link;
    }

    public AbiquoLinkBuilder pathParams(Map<String, String> params)
    {
        if (params != null && !params.isEmpty())
        {
            for (Map.Entry<String, String> entry : params.entrySet())
            {
//                if (!pathParams.containsKey(entry.getKey()))
//                {
                    pathParam(entry.getKey(), entry.getValue());
//                }
            }
        }
        return this;
    }

    @Override
    public AbiquoLinkBuilder rel(String rel)
    {
        return (AbiquoLinkBuilder) super.rel(rel);
    }

    public AbiquoLinkBuilder setResource(Class< ? > resource)
    {
        return (AbiquoLinkBuilder) super.resource(resource);
    }

    public static AbiquoLinkBuilder createBuilder(LinkBuilders linkProcessor)
    {
        return (AbiquoLinkBuilder) linkProcessor.createSingleLinkBuilder().relativize(false);
    }
}
