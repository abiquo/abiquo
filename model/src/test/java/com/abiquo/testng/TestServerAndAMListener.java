package com.abiquo.testng;

import static com.abiquo.testng.TestConfig.DEFAULT_SERVER_PORT;
import static com.abiquo.testng.TestConfig.getParameter;

import org.mortbay.jetty.Server;
import org.mortbay.jetty.webapp.WebAppContext;
import org.testng.ISuite;

public class TestServerAndAMListener extends TestServerListener
{

    protected static final String AM_WEBAPP_DIR = "am.webapp.dir";

    protected static final String AM_WEBAPP_CONTEXT = "am.webapp.context";

    @Override
    public void onStart(final ISuite suite)
    {

        LOGGER.info("Starting test server with am...");

        int port = Integer.valueOf(getParameter(WEBAPP_PORT, DEFAULT_SERVER_PORT));
        server = new Server(port);

        WebAppContext webapp = new WebAppContext();
        webapp.setContextPath(getParameter(WEBAPP_CONTEXT));
        webapp.setWar(getParameter(WEBAPP_DIR));
        webapp.setServer(server);

        WebAppContext webappam = new WebAppContext();
        webappam.setContextPath(getParameter(AM_WEBAPP_CONTEXT));
        webappam.setWar(getParameter(AM_WEBAPP_DIR));
        webappam.setServer(server);

        server.setHandlers(new WebAppContext[] {webapp, webappam});

        try
        {
            server.start();
            LOGGER.info("Test server started with am.");
        }
        catch (Exception ex)
        {
            throw new RuntimeException("Could not start test server with am", ex);
        }
    }

}
