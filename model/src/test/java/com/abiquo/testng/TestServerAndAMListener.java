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

import java.io.File;

import org.apache.commons.io.FilenameUtils;
import org.mortbay.jetty.Server;
import org.mortbay.jetty.webapp.WebAppContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.ISuite;
import org.testng.ISuiteListener;

public class TestServerAndAMListener extends TestServerListener
{

    protected static final String AM_WEBAPP_DIR = "am.webapp.dir";

    protected static final String AM_WEBAPP_CONTEXT = "am.webapp.context";

    protected static final String AM_WEBAPP_CONTEXT_DEFAULT = "/am";

    @Override
    public void onStart(final ISuite suite)
    {
        final String amcontextPath = getRelativeContextPath(getParameter(AM_WEBAPP_DIR));
        LOGGER.info("Starting test server with am using contextpath {}", amcontextPath);
        LOGGER.info(getParameter(WEBAPP_DIR));

        int port = Integer.valueOf(getParameter(WEBAPP_PORT, DEFAULT_SERVER_PORT));
        server = new Server(port);

        WebAppContext webappapi = new WebAppContext();
        webappapi.setContextPath(getParameter(WEBAPP_CONTEXT));
        webappapi.setWar(getParameter(WEBAPP_DIR));
        webappapi.setServer(server);

        WebAppContext webappam = new WebAppContext();
        webappam.setContextPath(getParameter(AM_WEBAPP_CONTEXT, AM_WEBAPP_CONTEXT_DEFAULT));
        webappam.setWar(amcontextPath);
        webappam.setServer(server);

        server.setHandlers(new WebAppContext[] {webappapi, webappam});

        try
        {
            server.start();
            server.join();

            LOGGER.info("Test server started with am.");
        }
        catch (Exception ex)
        {
            throw new RuntimeException("Could not start test server with am", ex);
        }
    }

    private static String getRelativeContextPath(String relpath)
    {
        return FilenameUtils.normalize(relpath);
    }

}
