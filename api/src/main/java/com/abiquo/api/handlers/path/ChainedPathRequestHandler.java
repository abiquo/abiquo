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

package com.abiquo.api.handlers.path;

import java.util.List;
import java.util.Properties;

import org.apache.wink.server.handlers.HandlersChain;
import org.apache.wink.server.handlers.MessageContext;
import org.apache.wink.server.handlers.RequestHandler;

/**
 * Handler with a list of handlers. This handlers check if the request uri path matches with the
 * method <code>matches</code> of the handlers in the list. The first handler who match will be who
 * handle the request.
 * 
 * @author scastro
 */
public class ChainedPathRequestHandler implements RequestHandler
{
    private List<PathConstrainedRequestHandler> handlerChain;

    public ChainedPathRequestHandler(final List<PathConstrainedRequestHandler> handlers)
    {
        this.handlerChain = handlers;
    }

    @Override
    public void init(final Properties props)
    {

    }

    /**
     * @see org.apache.wink.server.handlers.RequestHandler#handleRequest(org.apache.wink.server.handlers.MessageContext,
     *      org.apache.wink.server.handlers.HandlersChain)
     */
    @Override
    public void handleRequest(final MessageContext context, final HandlersChain chain)
        throws Throwable
    {
        String path = context.getUriInfo().getPath();
        for (PathConstrainedRequestHandler handler : handlerChain)
        {
            if (handler.appliesTo(path))
            {
                handler.handleRequest(context, chain);
                // Only the first handler to match will be executed
                return;
            }
        }
        // if no handler match we must continue
        chain.doChain(context);
    }
}
