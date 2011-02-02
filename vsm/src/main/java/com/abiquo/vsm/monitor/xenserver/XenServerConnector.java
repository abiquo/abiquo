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
package com.abiquo.vsm.monitor.xenserver;

import java.net.URL;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.abiquo.vsm.exception.MonitorException;
import com.xensource.xenapi.APIVersion;
import com.xensource.xenapi.Connection;
import com.xensource.xenapi.Session;

/**
 * XenServer API connector.
 * 
 * @author destevez
 */
public class XenServerConnector
{
    /**
     * The default port where XenApi listens.This port is forced since the client sends an invalid
     * port. Changes must be made to the client to avoid this.
     */
    public static final int DEFAULT_API_PORT = 80;

    /** The Constant logger. */
    private final static Logger LOGGER = LoggerFactory.getLogger(XenServerConnector.class);

    /**
     * Connects to the hypervisor.
     * 
     * @param physicalMachineAddress The address of the hypervisor.
     * @param username The user name used to connect to the hypervisor.
     * @param password The password used to connect to the hypervisor.
     * @return The connection object.
     * @throws MonitorException If connection fails.
     */
    public static Connection connect(String physicalMachineAddress, String user, String password)
        throws MonitorException
    {
        try
        {
            // FIXME: Force the connection port. Client must be changed to send the right port.
            URL url = new URL(physicalMachineAddress);
            URL connectionURL =
                new URL(url.getProtocol() + "://" + url.getHost() + ":" + DEFAULT_API_PORT);

            LOGGER.trace("Connecting to XenServer host: {}", connectionURL.toString());

            Connection connection = new Connection(connectionURL);

            Session session =
                Session.loginWithPassword(connection, user, password, APIVersion.latest()
                    .toString());

            LOGGER.trace("Connected with Session id {}", session.getUuid(connection));

            return connection;
        }
        catch (Exception ex)
        {
            throw new MonitorException("Could not connect to XenServer host: "
                + physicalMachineAddress, ex);
        }
    }

    /**
     * Disconnects from the XenServer host.
     * 
     * @param connection The connection object.
     * @throws MonitorException If disconnection fails.
     */
    public static void disconnect(Connection connection) throws MonitorException
    {
        LOGGER.trace("Disconnecting from XenServer [{}]", connection.getSessionReference());

        try
        {
            // Logout from hypervisor and end connection
            Session.logout(connection);
            connection.dispose();
            connection = null;
        }
        catch (Exception ex)
        {
            throw new MonitorException("Could not disconnect from XenServer Hypervisor", ex);
        }
    }

}
