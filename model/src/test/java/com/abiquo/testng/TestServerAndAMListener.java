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
import java.io.IOException;
import java.io.RandomAccessFile;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.mortbay.jetty.Handler;
import org.mortbay.jetty.Server;
import org.mortbay.jetty.handler.DefaultHandler;
import org.mortbay.jetty.handler.HandlerList;
import org.mortbay.jetty.handler.ResourceHandler;
import org.mortbay.jetty.nio.SelectChannelConnector;
import org.mortbay.jetty.webapp.WebAppContext;
import org.testng.ISuite;

public class TestServerAndAMListener extends TestServerListener
{

    /** appliance manager remote service configuration */
    protected static final String AM_WEBAPP_DIR = "am.webapp.dir";

    protected static final String AM_WEBAPP_CONTEXT = "am.webapp.context";

    protected static final String AM_WEBAPP_CONTEXT_DEFAULT = "/am";

    /** remote repository file server configuration */
    protected Server rsServer;

    public static final int RS_FILE_SERVER_PORT = 8282;

    private final static String fileServerPath = getParameter("rs.basedir")+"/src/test/resources/";

    private final static String diskFilePath = getParameter("rs.basedir")+"/src/test/resources/testovf/diskFile.vmdk";

    /** Should be the same of on the References size on the ''src/test/resources/description.ovf'' */
    private final static Long diskFileSize = 1024 * 1024 * 10l;

    protected final static String ovfId = String.format(
        "http://localhost:%d/testovf/description.ovf", RS_FILE_SERVER_PORT);

    protected final static String REPO_PATH = getParameter(
        "abiquo.appliancemanager.localRepositoryPath", "/tmp/testrepo");

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
            createRepository();
            
            server.start();
            LOGGER.info("Test server started with am.");

            startRemoteServiceServer();

            
        }
        catch (Exception ex)
        {
            throw new RuntimeException("Could not start test server with am", ex);
        }
    }

    public void onFinish(final ISuite suite)
    {
        super.onFinish(suite);

        try
        {
            tearDownFileServer();

            cleanupRepository();
        }
        catch (Exception e)
        {
            throw new RuntimeException("can tear down RS", e);
        }
    }

    private static void cleanupRepository() throws IOException
    {
        File vmrepo = new File(REPO_PATH);
        if (vmrepo.exists())
        {
            FileUtils.deleteDirectory(vmrepo);
        }
    }

    private static void createRepository() throws IOException
    {
        File vmrepo = new File(REPO_PATH);
        if (vmrepo.exists())
        {
            FileUtils.deleteDirectory(vmrepo);
        }
        
        vmrepo = new File(REPO_PATH);
        vmrepo.mkdirs();

        new File(FilenameUtils.concat(REPO_PATH, ".abiquo_repository")).createNewFile();
    }

    private void startRemoteServiceServer() throws Exception
    {
        rsServer = new Server();
        SelectChannelConnector connector = new SelectChannelConnector();
        connector.setPort(RS_FILE_SERVER_PORT);
        rsServer.addConnector(connector);

        ResourceHandler resource_handler = new ResourceHandler();
        // resource_handler.setDirectoriesListed(true);
        // resource_handler.setWelcomeFiles(new String[] {"index.html"});
        resource_handler.setResourceBase(fileServerPath);

        HandlerList handlers = new HandlerList();
        handlers.setHandlers(new Handler[] {resource_handler, new DefaultHandler()});
        rsServer.setHandler(handlers);
        rsServer.start();

        createDiskFile();
    }

    private void tearDownFileServer() throws Exception
    {
        if (rsServer != null)
        {
            rsServer.stop();
        }
        deleteDiskFile();
    }

    /**
     * creates the referenced file in testovf/description.ovf
     */
    private void createDiskFile() throws IOException
    {
        System.err.println("*********************"+diskFilePath);
        File diskFile = new File(diskFilePath);
        RandomAccessFile f = new RandomAccessFile(diskFile, "rw");
        f.setLength(diskFileSize);
    }

    private void deleteDiskFile() throws Exception
    {
        File diskFile = new File(diskFilePath);

        final String errorCause =
            String.format("Can not delete the disk file at [%s]", diskFilePath);

        if (diskFile.exists())
        {
            if (!diskFile.delete())
            {
                throw new Exception(errorCause);
            }
        }
        else
        {
            throw new Exception(errorCause);
        }
    }

    private static String getRelativeContextPath(String relpath)
    {
        return FilenameUtils.normalize(relpath);
    }

}
