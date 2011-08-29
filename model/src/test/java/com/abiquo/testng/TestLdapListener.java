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

import static com.abiquo.testng.TestConfig.DEFAULT_LDAP_PORT;
import static com.abiquo.testng.TestConfig.DEFAULT_LDIF_DIRECTORY;
import static com.abiquo.testng.TestConfig.DEFAULT_WORKING_DIRECTORY;
import static com.abiquo.testng.TestConfig.getParameter;

import java.io.File;
import java.util.List;

import org.apache.directory.server.configuration.ApacheDS;
import org.apache.directory.server.constants.ServerDNConstants;
import org.apache.directory.server.core.DefaultDirectoryService;
import org.apache.directory.server.core.factory.JdbmPartitionFactory;
import org.apache.directory.server.core.partition.Partition;
import org.apache.directory.server.core.partition.impl.btree.jdbm.JdbmPartition;
import org.apache.directory.server.ldap.LdapServer;
import org.apache.directory.server.protocol.shared.transport.TcpTransport;
import org.apache.directory.shared.ldap.constants.SchemaConstants;
import org.apache.directory.shared.ldap.exception.LdapInvalidDnException;
import org.apache.directory.shared.ldap.schema.ldif.extractor.SchemaLdifExtractor;
import org.apache.directory.shared.ldap.schema.ldif.extractor.impl.DefaultSchemaLdifExtractor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.ISuite;
import org.testng.ISuiteListener;

/**
 * Starts and stops an ApacheDS server for integration tests.
 * 
 * @author ssedano
 */
public class TestLdapListener implements ISuiteListener
{
    private static final Logger LOGGER = LoggerFactory.getLogger(TestLdapListener.class);

    private static final String LDAP_LDIF_DIR = "ldap.ldif.dir";

    private static final String LDAP_WORKING_DIR = "ldap.working.dir";

    private static final String LDAP_PORT = "ldap.port";

    private DefaultDirectoryService service;

    private LdapServer ldapService;

    private ApacheDS apacheDs;

    @Override
    public void onStart(final ISuite suite)
    {
        LOGGER.info("Starting apacheDS server...");
        int port = Integer.valueOf(getParameter(LDAP_PORT, DEFAULT_LDAP_PORT));
        long start = System.currentTimeMillis();
        try
        {
            JdbmPartition partition = getDefaultPartition();
            reset(new File(getParameter(LDAP_WORKING_DIR, DEFAULT_WORKING_DIRECTORY)));
            service.addPartition(partition);
            configureLdapServer(port);
            configureLdifDirectory(new File(getParameter(LDAP_LDIF_DIR, DEFAULT_LDIF_DIRECTORY)));
            apacheDs.startup();
        }
        catch (Exception ex)
        {
            throw new RuntimeException("Could not start test server", ex);
        }

        LOGGER.info("ApacheDS started in {} milliseconds", System.currentTimeMillis() - start);
    }

    private JdbmPartition getDefaultPartition() throws LdapInvalidDnException
    {
        JdbmPartition partition = new JdbmPartition();

        partition.setId("springframework.org");
        partition.setSuffix("dc=springframework,dc=org");
        return partition;
    }

    public void configureLdapServer(final int port) throws Exception
    {
        ldapService = new LdapServer();
        ldapService.setTransports(new TcpTransport(port));
        ldapService.setDirectoryService(service);

        apacheDs = new ApacheDS(ldapService);
    }

    public void reset(final File workingDirectory) throws Exception
    {
        SchemaLdifExtractor extractor = new DefaultSchemaLdifExtractor(workingDirectory);
        extractor.extractOrCopy(true);

        service = new DefaultDirectoryService();
        service.setWorkingDirectory(workingDirectory);
        service.getChangeLog().setEnabled(false);
        service.setSystemPartition(createSystemPartition(service, workingDirectory));
    }

    public void addPartitions(final List<JdbmPartition> partitions) throws Exception
    {
        for (Partition partition : partitions)
        {
            service.addPartition(partition);
        }
    }

    public void configureLdifDirectory(final File ldifDirectory)
    {
        apacheDs.setLdifDirectory(ldifDirectory);
    }

    private JdbmPartition createSystemPartition(final DefaultDirectoryService service,
        final File workingDirectory) throws Exception
    {
        JdbmPartitionFactory partitionFactory = new JdbmPartitionFactory();
        JdbmPartition systemPartition =
            partitionFactory.createPartition("system", ServerDNConstants.SYSTEM_DN, 500,
                new File(workingDirectory, "system"));
        partitionFactory.addIndex(systemPartition, SchemaConstants.OBJECT_CLASS_AT, 100);

        systemPartition.setSchemaManager(service.getSchemaManager());
        return systemPartition;
    }

    @Override
    public void onFinish(final ISuite suite)
    {
        LOGGER.info("Stopping apacheDS server...");
        long start = System.currentTimeMillis();
        try
        {
            apacheDs.shutdown();
        }
        catch (Exception ex)
        {
            throw new RuntimeException("Could not stop apacheDS server", ex);
        }

        LOGGER.info("ApacheDS stopped in {} milliseconds", System.currentTimeMillis() - start);
    }

}
