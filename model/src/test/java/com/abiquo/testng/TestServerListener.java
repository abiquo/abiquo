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

import org.mortbay.jetty.Server;
import org.mortbay.jetty.webapp.WebAppContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.ISuite;
import org.testng.ISuiteListener;

/**
 * Starts and stops a Jetty server for integration tests.
 * 
 * @author ibarrera
 */
public class TestServerListener implements ISuiteListener
{
    private static final Logger LOGGER = LoggerFactory.getLogger(TestServerListener.class);

    private static final String WEBAPP_DIR = "webapp.dir";

    private static final String WEBAPP_CONTEXT = "webapp.context";

    private static final String WEBAPP_PORT = "webapp.port";

    private Server server;

    @Override
    public void onStart(final ISuite suite)
    {
        LOGGER.info("Starting test server...");
        long start = System.currentTimeMillis();
        int port = Integer.valueOf(getParameter(WEBAPP_PORT, DEFAULT_SERVER_PORT));
        server = new Server(port);

        WebAppContext webapp = new WebAppContext();
        webapp.setContextPath(getParameter(WEBAPP_CONTEXT));
        webapp.setWar(getParameter(WEBAPP_DIR));
        webapp.setServer(server);

        server.setHandler(webapp);

        try
        {
            server.start();
            LOGGER.info("Test server started.");
        }
        catch (Exception ex)
        {
            throw new RuntimeException("Could not start test server", ex);
        }

        LOGGER.info("Server started in {} milliseconds", System.currentTimeMillis() - start);
    }

    @Override
    public void onFinish(final ISuite suite)
    {
        LOGGER.info("Stopping test server...");
        long start = System.currentTimeMillis();
        try
        {
            server.stop();
            LOGGER.info("Test server stoped.");
        }
        catch (Exception ex)
        {
            throw new RuntimeException("Could not stop test server", ex);
        }
        LOGGER.info("Server stopped in {} milliseconds", System.currentTimeMillis() - start);
    }

}
