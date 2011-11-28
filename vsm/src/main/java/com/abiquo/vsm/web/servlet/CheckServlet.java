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

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.abiquo.vsm.VSMManager;

/**
 * Performs specific Virtual System Monitor checks.
 * 
 * @author ibarrera
 */
public class CheckServlet extends AbstractCheckServlet
{
    /** Serial UID. */
    private static final long serialVersionUID = 1L;

    @Override
    protected boolean check(final HttpServletRequest req, final HttpServletResponse resp)
        throws Exception
    {
        // Read configuration
        VSMManager vsmManager = VSMManager.getInstance();

        if (!vsmManager.isRedisRunning())
        {
            req.setAttribute("redis", "Redis is down");
        }
        if (!vsmManager.isRabbitMQRunning())
        {
            req.setAttribute("rabbit", "RabbitMQ is down, check RabbitMQ and restart VSM");
        }

        return VSMManager.getInstance().checkSystem();
    }

    @Override
    protected void fail(HttpServletRequest req, HttpServletResponse resp) throws IOException
    {
        String redis = (String) req.getAttribute("redis");
        String rabbit = (String) req.getAttribute("rabbit");

        String msg =
            String.format("Configuration errors:\n\t%s\n\t%s", redis == null ? "" : redis,
                rabbit == null ? "" : rabbit);

        fail(req, resp, msg);
    }

}
