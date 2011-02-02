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
package com.abiquo.vsm.monitor.vbox;

import java.net.MalformedURLException;
import java.net.URL;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.virtualbox_4_0.IMachine;
import org.virtualbox_4_0.IVirtualBox;
import org.virtualbox_4_0.MachineState;
import org.virtualbox_4_0.VirtualBoxManager;

import com.abiquo.vsm.events.VMEventType;
import com.abiquo.vsm.exception.MonitorException;

/**
 * VirtualBox connector
 * 
 * @author pnavarro
 */
public class VirtualBoxConnector
{
    /** The logger. */
    private final static Logger LOGGER = LoggerFactory.getLogger(VirtualBoxConnector.class);

    private VirtualBoxManager mgr;

    private IVirtualBox vbox;

    /** VirtualBox default port **/
    private int vboxport = 18083;

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

        URL vboxAddress = null;
        URL pmAddress;
        try
        {
            pmAddress = new URL(physicalMachineAddress);
            vboxAddress =
                new URL(pmAddress.getProtocol(), pmAddress.getHost(), vboxport, pmAddress.getFile());
        }
        catch (MalformedURLException e)
        {
            LOGGER.error("An internal error was found when handling URLS, ", e);
        }
        mgr = VirtualBoxManager.createInstance(null);
        LOGGER.trace("Logging into Session");
        mgr.connect(vboxAddress.toExternalForm(), username, password);
        vbox = mgr.getVBox();
    }

    /**
     * Disconnects from the hypervisor.
     * 
     * @param physicalMachineAddress The hypervisor address.
     */
    public void disconnect(String physicalMachineAddress)
    {
        LOGGER.trace("Disconnecting to HyperV host: {}", physicalMachineAddress);
        mgr.disconnect();
    }

    /**
     * Get the state of the specified virtual machine.
     * 
     * @param virtualMachineName The name of the virtual machine.
     * @return The virtual machine state.
     * @throws MonitorException If an error occurs retrieving machine state.
     */
    public VMEventType getState(String virtualMachineName) throws MonitorException
    {
        try
        {
            VMEventType state = VMEventType.UNKNOWN;
            IMachine machine = vbox.findMachine(virtualMachineName);
            MachineState actualState = machine.getState();
            return tranlateEvent(actualState);
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
    public Iterable<IMachine> getAllVMs() throws MonitorException
    {
        try
        {
            return vbox.getMachines();
        }
        catch (Exception ex)
        {
            throw new MonitorException("Could not get the list of virtual machines", ex);
        }
    }

    /**
     * Translate the events from VirtualBox to our event
     * 
     * @param actualState the actual state
     * @return the VSM event type
     */
    public static VMEventType tranlateEvent(MachineState actualState)
    {
        VMEventType state = VMEventType.UNKNOWN;
        switch (actualState)
        {
            case Aborted:
                state = VMEventType.POWER_OFF;
                break;
            case Paused:
                state = VMEventType.PAUSED;
                break;
            case Running:
                state = VMEventType.POWER_ON;
                break;
            case PoweredOff:
                state = VMEventType.POWER_OFF;
                break;
            case Restoring:
                state = VMEventType.RESUMED;
                break;
            case Starting:
                state = VMEventType.POWER_ON;
                break;
            case Stopping:
                state = VMEventType.POWER_OFF;
                break;
            default:
                state = VMEventType.POWER_OFF;
                break;
        }
        return state;
    }

}
