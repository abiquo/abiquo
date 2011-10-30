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

import static com.abiquo.testng.TestConfig.getParameter;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;

import org.mortbay.jetty.Handler;
import org.mortbay.jetty.Server;
import org.mortbay.jetty.handler.DefaultHandler;
import org.mortbay.jetty.handler.HandlerList;
import org.mortbay.jetty.handler.ResourceHandler;
import org.mortbay.jetty.nio.SelectChannelConnector;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.ISuite;
import org.testng.ISuiteListener;

/**
 * Set up a remote repository to act as an HTTP server for content in resouces/testovf folder.
 */
public class OVFRemoteRepositoryListener implements ISuiteListener
{

    protected static final Logger LOGGER = LoggerFactory
        .getLogger(OVFRemoteRepositoryListener.class);

    /** ##### ##### ##### ##### ##### */

    public static final int RS_FILE_SERVER_PORT = 7979;

    public final static String ovfId = String.format("http://localhost:%d/testovf/description.ovf",
        RS_FILE_SERVER_PORT);

    // ERRORS
    public final static String ovf_invalidUrl = ovfId + "NOT-FOUND-OVF";

    public final static String ovf_notFound = String.format(
        "http://localhost:%d/testovf/errors/ovfId_notFound.ovf", RS_FILE_SERVER_PORT);

    public final static String ovf_fileNotAllowed = String.format(
        "http://localhost:%d/testovf/errors/fileNotAllowed.ovf", RS_FILE_SERVER_PORT);

    public final static String ovf_fileNotFound = String.format(
        "http://localhost:%d/testovf/errors/fileNotFound.ovf", RS_FILE_SERVER_PORT);

    public final static String ovf_invalidDiskFormat = String.format(
        "http://localhost:%d/testovf/errors/invalidDiskFormat.ovf", RS_FILE_SERVER_PORT);

    public final static String ovf_malformed = String.format(
        "http://localhost:%d/testovf/errors/malformed.ovf", RS_FILE_SERVER_PORT);

    public final static String ovf_multiDisk = String.format(
        "http://localhost:%d/testovf/errors/multiDisk.ovf", RS_FILE_SERVER_PORT);

    /** ##### ##### ##### ##### ##### */

    /** remote repository file server configuration */
    protected Server rsServer;

    private final static String fileServerPath = getParameter("rs.basedir")
        + "/src/test/resources/";

    private final static String diskFilePath = getParameter("rs.basedir")
        + "/src/test/resources/testovf/diskFile.vmdk";

    /** Should be the same of on the References size on the ''src/test/resources/description.ovf'' */
    private final static Long diskFileSize = 1024 * 1024 * 10l;

    @Override
    public void onStart(final ISuite suite)
    {
        try
        {
            startRemoteServiceServer();
            LOGGER.info("Test server for ovfindex started.");

            createDiskFile();

        }
        catch (Exception ex)
        {
            throw new RuntimeException("Could not start test server for ovfindex", ex);
        }
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

    @Override
    public void onFinish(final ISuite suite)
    {
        LOGGER.info("Stopping test server for ovfindex...");

        try
        {
            if (rsServer != null)
            {
                rsServer.stop();
            }
            LOGGER.info("Test server for ovfindex stoped.");

            deleteDiskFile();
        }
        catch (Exception ex)
        {
            throw new RuntimeException("Could not stop test server for ovfindex", ex);
        }
    }

    /**
     * creates the referenced file in testovf/description.ovf
     */
    private void createDiskFile() throws IOException
    {
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

}
