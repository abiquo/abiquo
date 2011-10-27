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

import java.util.ArrayList;
import java.util.List;

import org.apache.wink.server.handlers.HandlersFactory;
import org.apache.wink.server.handlers.RequestHandler;
import org.apache.wink.server.handlers.ResponseHandler;

public class RESTHandlerFactory extends HandlersFactory
{
    @Override
    public List< ? extends RequestHandler> getRequestHandlers()
    {
        List<RequestHandler> listOfHandlers = new ArrayList<RequestHandler>();

        // Check the input constraints
        listOfHandlers.add(new InputParamConstraintHandler());

        // Injects the IRESTLinkBuilder object to all the methods.
        listOfHandlers.add(new RESTHandler());

        return listOfHandlers;
    }

    @Override
    public List< ? extends ResponseHandler> getResponseHandlers()
    {
        List<ResponseHandler> listOfHandlers = new ArrayList<ResponseHandler>();

        listOfHandlers.add(new RESTHandler());

        // Check dto links permissions
        listOfHandlers.add(new CheckLinksPermissionsHandler());

        return listOfHandlers;
    }

}
