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
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.abiquo.vsm.exception.MonitorException;
import com.xensource.xenapi.APIVersion;
import com.xensource.xenapi.Connection;
import com.xensource.xenapi.Session;
import com.xensource.xenapi.Types;
import com.xensource.xenapi.Types.VmOperations;
import com.xensource.xenapi.VM;

/**
 * XenServer API connector.
 * 
 * @author destevez
 */
/**
 * @author Ignasi Barrera
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

    /** The connection to the hypervisor. */
    private Connection connection;

    /**
     * Connects to the hypervisor.
     * 
     * @param physicalMachineAddress The address of the hypervisor.
     * @param username The user name used to connect to the hypervisor.
     * @param password The password used to connect to the hypervisor.
     * @throws MonitorException If connection fails.
     */
    public void connect(final String physicalMachineAddress, final String user,
        final String password) throws MonitorException
    {
        try
        {
            URL url = new URL(physicalMachineAddress);
            URL connectionURL =
                new URL(url.getProtocol() + "://" + url.getHost() + ":" + DEFAULT_API_PORT);

            LOGGER.trace("Connecting to XenServer host: {}", connectionURL.toString());

            connection = new Connection(connectionURL);

            Session session =
                Session.loginWithPassword(connection, user, password, APIVersion.latest()
                    .toString());

            LOGGER.trace("Connected with Session id {}", session.getUuid(connection));
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
     * @throws MonitorException If disconnection fails.
     */
    public void disconnect() throws MonitorException
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

    /**
     * Get the state of the specified virtual machine.
     * 
     * @param virtualMachineName The name of the virtual machine.
     * @return The virtual machine state.
     * @throws MonitorException If an error occurs retrieving machine state.
     */
    public Types.VmPowerState getState(final String virtualMachineName) throws MonitorException
    {
        try
        {
            Types.VmPowerState powerState = null;

            Iterator<VM> vmsIterator = VM.getByNameLabel(connection, virtualMachineName).iterator();
            if (vmsIterator.hasNext())
            {
                powerState = vmsIterator.next().getPowerState(connection);
            }
            else
            {
                // VM Not found
                powerState = Types.VmPowerState.UNRECOGNIZED;
                LOGGER.error("Virtual machine {} was not found", virtualMachineName);
            }

            return powerState;
        }
        catch (Exception ex)
        {
            throw new MonitorException("Could not get the state of virtual machine: "
                + virtualMachineName, ex);
        }
    }

    /**
     * Get the information of all virtual machines in the target physical machine.
     * 
     * @return The information of all virtual machines in the target physical machine.
     * @throws MonitorException If the list of virtual machine information cannot be obtained.
     */
    public List<VM.Record> getAllVMs() throws MonitorException
    {
        try
        {
            List<VM.Record> vms = new ArrayList<VM.Record>();
            Map<VM, VM.Record> retrievedVMs = VM.getAllRecords(getConnection());

            for (VM.Record vm : retrievedVMs.values())
            {
                if (!vm.isControlDomain && !vm.isATemplate)
                {
                    vms.add(vm);
                }
            }

            return vms;
        }
        catch (Exception ex)
        {
            throw new MonitorException("Could not get the list of virtual machines", ex);
        }
    }

    /**
     * Check if the current virtual machine is being rebooted.
     * 
     * @param vm The virtual machine to check.
     * @return Boolean indicating if the current virtual machine is being rebooted.
     */
    public boolean isBeingRebooted(final VM.Record vm) throws MonitorException
    {
        try
        {
            Collection<VmOperations> ops = vm.currentOperations.values();

            for (VmOperations op : ops)
            {
                if (op == VmOperations.HARD_REBOOT || op == VmOperations.CLEAN_REBOOT
                    || op == VmOperations.POWER_STATE_RESET)
                {
                    return true;
                }
            }

            return false;
        }
        catch (Exception ex)
        {
            throw new MonitorException("Could not get current operations of the virtual machine",
                ex);
        }
    }

    public Connection getConnection()
    {
        return connection;
    }

}
