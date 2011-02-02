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

package com.abiquo.api.common;

import java.util.HashMap;
import java.util.Map;

import org.mortbay.jetty.Server;
import org.mortbay.jetty.servlet.Context;
import org.mortbay.jetty.servlet.FilterHolder;
import org.mortbay.jetty.servlet.ServletHolder;
import org.springframework.orm.jpa.support.OpenEntityManagerInViewFilter;
import org.springframework.web.context.ContextLoaderListener;

import com.abiquo.api.util.AbiquoRestServlet;

public class Jetty
{
    private static Server server;

    protected static String contextConfigLocation =
        "classpath:META-INF/server/wink-core-context.xml, "
            + "classpath:springresources/applicationContext-test.xml";

    public static void start() throws Exception
    {
        start(contextConfigLocation);
    }

    public static void start(String contextConfig) throws Exception
    {
        server = new Server(9009);
        Context contextHandler = new Context(server, "/api", Context.SESSIONS);

        Map<String, String> initParameters = new HashMap<String, String>();
        initParameters.put("contextConfigLocation", contextConfig);

        contextHandler.setInitParams(initParameters);
        contextHandler.addEventListener(new ContextLoaderListener());

        ServletHolder servletHolder = new ServletHolder(new AbiquoRestServlet());
        contextHandler.addServlet(servletHolder, "/*");

        FilterHolder filterHolder = new FilterHolder(new OpenEntityManagerInViewFilter());
        contextHandler.addFilter(filterHolder, "/*", 1);

        server.start();
    }

    public static void stop() throws Exception
    {
        if (server != null)
        {
            server.stop();
        }
    }
}
