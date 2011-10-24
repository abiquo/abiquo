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

package com.abiquo.testng;

import static com.abiquo.testng.TestConfig.DEFAULT_SERVER_PORT;
import static com.abiquo.testng.TestConfig.getParameter;

import org.mortbay.jetty.Handler;
import org.mortbay.jetty.Server;
import org.mortbay.jetty.handler.DefaultHandler;
import org.mortbay.jetty.handler.HandlerList;
import org.mortbay.jetty.handler.ResourceHandler;
import org.mortbay.jetty.nio.SelectChannelConnector;
import org.mortbay.jetty.webapp.WebAppContext;
import org.testng.ISuite;

public class TestServerAndOVFListener extends TestServerListener
{

    /** remote repository file server configuration */
    protected Server rsServer;

    public static final int RS_FILE_SERVER_PORT = 7979;

    private final static String fileServerPath =
        getParameter("rs.basedir") + "/src/test/resources/";

    @Override
    public void onStart(final ISuite suite)
    {
        LOGGER.info(getParameter(WEBAPP_DIR));

        int port = Integer.valueOf(getParameter(WEBAPP_PORT, DEFAULT_SERVER_PORT));
        server = new Server(port);

        WebAppContext webappapi = new WebAppContext();
        webappapi.setContextPath(getParameter(WEBAPP_CONTEXT));
        webappapi.setWar(getParameter(WEBAPP_DIR));
        webappapi.setServer(server);

        server.setHandlers(new WebAppContext[] {webappapi});

        try
        {
            server.start();
            LOGGER.info("Test server started.");

            startRemoteServiceServer();

        }
        catch (Exception ex)
        {
            throw new RuntimeException("Could not start test server", ex);
        }
    }

    public void onFinish(final ISuite suite)
    {
        super.onFinish(suite);
    }

    private void startRemoteServiceServer() throws Exception
    {
        rsServer = new Server();
        SelectChannelConnector connector = new SelectChannelConnector();
        connector.setPort(RS_FILE_SERVER_PORT);
        rsServer.addConnector(connector);

        ResourceHandler resource_handler = new ResourceHandler();
        resource_handler.setResourceBase(fileServerPath);

        HandlerList handlers = new HandlerList();
        handlers.setHandlers(new Handler[] {resource_handler, new DefaultHandler()});
        rsServer.setHandler(handlers);
        rsServer.start();
    }

}
