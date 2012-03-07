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

package com.abiquo.nodecollector.resource;

import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.QueryParam;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import com.abiquo.nodecollector.exception.BadRequestException;
import com.abiquo.nodecollector.exception.NodecollectorException;
import com.abiquo.nodecollector.exception.ServiceUnavailableException;
import com.abiquo.nodecollector.service.StonithService;

/**
 * @author eruiz@abiquo.com
 */
@Path(StonithResource.STONITH_PATH)
@Controller
public class StonithResource
{
    private static final Logger LOGGER = LoggerFactory.getLogger(StonithResource.class);

    public static final String STONITH_PATH = "stonith";

    @Autowired
    private StonithService service;

    @GET
    @Path("/up")
    public void isStonithUp(@QueryParam("host") final String host,
        @QueryParam("port") final Integer port, @QueryParam("user") final String user,
        @QueryParam("password") final String password) throws NodecollectorException
    {
        validateQueryParams(host, port, user, password);

        if (!service.isStonithUp(host, port, user, password))
        {
            throw new ServiceUnavailableException("The stonith service is down.");
        }
    }

    @POST
    public void shootTheOtherNodeInTheHead(@QueryParam("host") final String host,
        @QueryParam("port") final Integer port, @QueryParam("user") final String user,
        @QueryParam("password") final String password) throws NodecollectorException
    {
        validateQueryParams(host, port, user, password);

        if (!service.shootTheOtherNodeInTheHead(host, port, user, password))
        {
            throw new ServiceUnavailableException("Unable to power off the remote node.");
        }
    }

    protected void validateQueryParams(final String host, final Integer port, final String user,
        final String password) throws NodecollectorException
    {
        if (host == null || user == null || password == null)
        {
            throw new BadRequestException("Bad request, invalid query parameters.");
        }

        if (host.isEmpty())
        {
            throw new BadRequestException("Bad request, invalid query parameters.");
        }
    }
}
