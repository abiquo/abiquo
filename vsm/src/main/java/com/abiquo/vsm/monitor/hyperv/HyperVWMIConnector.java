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
package com.abiquo.vsm.monitor.hyperv;

import java.net.MalformedURLException;
import java.net.URL;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.abiquo.vsm.exception.MonitorException;
import com.abiquo.vsm.monitor.hyperv.util.HyperVConstants;
import com.hyper9.jwbem.SWbemLocator;
import com.hyper9.jwbem.SWbemObjectSet;
import com.hyper9.jwbem.SWbemServices;
import com.hyper9.jwbem.msvm.virtualsystem.MsvmComputerSystem;

/**
 * WMI Connector for Hyper-V monitoring tasks.
 * 
 * @author ibarrera
 */
public class HyperVWMIConnector
{
    /** The logger. */
    private final static Logger LOGGER = LoggerFactory.getLogger(HyperVWMIConnector.class);

    /** The SWbem service for the virtualization namespace. */
    private SWbemServices virtService;

    /**
     * Connects to the hypervisor.
     * 
     * @param physicalMachineAddress The address of the hypervisor.
     * @param username The user name used to connect to the hypervisor.
     * @param password The password used to connect to the hypervisor.
     * @throws MonitorException If connection fails.
     */
    public void connect(String physicalMachineAddress, String username, String password)
        throws MonitorException
    {
        LOGGER.trace("Connecting to HyperV host: {}", physicalMachineAddress);

        try
        {
            URL url = new URL(physicalMachineAddress);

            SWbemLocator loc = new SWbemLocator();
            virtService =
                loc.connect(url.getHost(), "127.0.0.1", HyperVConstants.VIRTUALIZATION_NS,
                    username, password);
        }
        catch (MalformedURLException ex)
        {
            throw new MonitorException("Invalid connection URI: " + physicalMachineAddress, ex);
        }
        catch (Exception ex)
        {
            throw new MonitorException("Could not connect to HyperV host: "
                + physicalMachineAddress, ex);
        }
    }

    /**
     * Get the state of the specified virtual machine.
     * 
     * @param virtualMachineName The name of the virtual machine.
     * @return The virtual machine state.
     * @throws MonitorException If an error occurs retrieving machine state.
     */
    public int getState(String virtualMachineName) throws MonitorException
    {
        try
        {
            String format = "SELECT * FROM Msvm_ComputerSystem WHERE ElementName='%s'";
            String query = String.format(format, virtualMachineName);

            SWbemObjectSet<MsvmComputerSystem> compObjectSet =
                virtService.execQuery(query, MsvmComputerSystem.class);
            MsvmComputerSystem virtualMachine = compObjectSet.iterator().next();

            return virtualMachine.getEnabledState();
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
    public Iterable<MsvmComputerSystem> getAllVMs() throws MonitorException
    {
        try
        {
            return virtService.execQuery("SELECT * FROM Msvm_ComputerSystem",
                MsvmComputerSystem.class);
        }
        catch (Exception ex)
        {
            throw new MonitorException("Could not get the list of virtual machines", ex);
        }
    }

    /**
     * Disconnects from the hypervisor.
     * 
     * @param physicalMachineAddress The hypervisor address.
     */
    public void disconnect(String physicalMachineAddress)
    {
        LOGGER.trace("Disconnecting to HyperV host: {}", physicalMachineAddress);
        virtService.getLocator().disconnect();
    }

}
