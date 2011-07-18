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
package com.abiquo.nodecollector.utils;

import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.abiquo.nodecollector.constants.MessageValues;
import com.abiquo.nodecollector.exception.UnprovisionedException;

/**
 * Small class to define the ping provisioning.
 * 
 * @author jdevesa@abiquo.com
 */
public class ProvisioningUtils
{

    /** Adding logger. */
    private static final Logger LOG = LoggerFactory.getLogger(ProvisioningUtils.class);

    /**
     * Checks if the IP address is well formed and pings the machine to see if it is running.
     * 
     * @param ip ip address of the machine.
     * @throws InvalidIPAddressException if the IP address is not well formed.
     * @throws UnprovisionedException if the machine doesn't respond the pign.
     * @throws InvalidIPAddressException
     */
    public static void provisioningCheck(final String ip) throws UnprovisionedException
    {
        /** Milliseconds to wait before fail the ping attempt. TODO should be configurable ¿ */
        Integer PING_TIMEOUT = 3000;

        // Check if the provided IP is reachable (on a given timeout).
        try
        {
            InetAddress address = InetAddress.getByName(ip);

            // By default XenServer filters port 7 (Echo), so InetAddress.isReachable() calls
            // will fail. A connection to port 80 (where XenServer API listens) is attempted too.
            if (!address.isReachable(PING_TIMEOUT) && !isReachable(address, 80, PING_TIMEOUT))
            {
                throw new UnprovisionedException(MessageValues.UNP_EXCP);
            }
        }
        catch (IOException e)
        {
            LOG.warn("Can not execute the ping to [{}] cuased by: ", ip, e.getMessage());

            // the only information available is the physical machine state
            throw new UnprovisionedException(MessageValues.UNP_EXCP);
        }
    }

    /**
     * Tests if a TCP connection can be established to the specified address and port.
     * 
     * @param ip The address where to connect.
     * @param port The port where to connect.
     * @param timeout The timeout interval.
     * @return A boolean indicating if the connection could be established.
     */
    public static boolean isReachable(final InetAddress ip, final int port, final int timeout)
    {
        Socket socket = new Socket();

        try
        {
            socket.connect(new InetSocketAddress(ip, port), timeout);
            return true;
        }
        catch (IOException ex)
        {
            return false;
        }
        finally
        {
            if (!socket.isClosed())
            {
                try
                {
                    socket.close();
                }
                catch (IOException e)
                {
                    LOG.warn("Could not close socket connection to {}:{}", ip, port);
                }
            }
        }
    }
}
