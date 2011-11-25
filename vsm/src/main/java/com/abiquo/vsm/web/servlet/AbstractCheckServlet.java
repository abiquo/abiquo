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
package com.abiquo.vsm.web.servlet;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Base implementation of the Check Servlet.
 * <p>
 * Each Remote Service in the platform must implement its own <code>CheckServlet</code> to let
 * consumers test its availability.
 * 
 * @author ibarrera
 */
public abstract class AbstractCheckServlet extends HttpServlet
{
    /** The logger. */
    private static final Logger LOGGER = LoggerFactory.getLogger(AbstractCheckServlet.class);

    /** Serial UID. */
    private static final long serialVersionUID = 1L;

    /**
     * Performs a check to validate Remote Service status.
     * 
     * @return A boolean indicating the status of the Remote Service.
     * @throws Exception If check operation fails or the Remote Service is not available.
     */
    protected abstract boolean check(final HttpServletRequest req, final HttpServletResponse resp)
        throws Exception;

    @Override
    protected void doGet(final HttpServletRequest req, final HttpServletResponse resp)
        throws ServletException, IOException
    {
        try
        {
            if (check(req, resp))
            {
                success(req, resp);
            }
            else
            {
                fail(req, resp);
            }
        }
        catch (Exception ex)
        {
            LOGGER.warn("Check operation failed");
            fail(req, resp, ex.getMessage());
        }
    }

    /**
     * Returns a {@link HttpServletResponse#SC_OK} HTTP code indicating that the Remote Service is
     * available.
     * 
     * @param req The request.
     * @param resp The Response.
     */
    protected void success(final HttpServletRequest req, final HttpServletResponse resp)
    {
        resp.setStatus(HttpServletResponse.SC_OK);
    }

    /**
     * Returns a {@link HttpServletResponse#SC_SERVICE_UNAVAILABLE} HTTP code indicating that the
     * Remote Service is not available.
     * 
     * @param req The request.
     * @param resp The Response.
     * @throws If error code cannot be sent.
     */
    protected void fail(final HttpServletRequest req, final HttpServletResponse resp)
        throws IOException
    {
        resp.sendError(HttpServletResponse.SC_SERVICE_UNAVAILABLE);
    }

    /**
     * Returns a {@link HttpServletResponse#SC_SERVICE_UNAVAILABLE} HTTP code indicating that the
     * Remote Service is not available.
     * 
     * @param req The request.
     * @param resp The Response.
     * @param msg The details of the check failure.
     * @throws If error code cannot be sent.
     */
    protected void fail(final HttpServletRequest req, final HttpServletResponse resp,
        final String msg) throws IOException
    {
        resp.sendError(HttpServletResponse.SC_SERVICE_UNAVAILABLE, msg);
    }

    @Override
    protected void doPost(final HttpServletRequest req, final HttpServletResponse resp)
        throws ServletException, IOException
    {
        doGet(req, resp);
    }
}
