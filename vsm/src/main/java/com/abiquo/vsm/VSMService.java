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
package com.abiquo.vsm;

import com.abiquo.vsm.exception.MonitorException;
import com.abiquo.vsm.exception.VSMException;
import com.abiquo.vsm.model.PhysicalMachine;
import com.abiquo.vsm.model.VirtualMachine;
import com.abiquo.vsm.monitor.MonitorManager;
import com.abiquo.vsm.monitor.Monitor.Type;

/**
 * Entry point to the VSM business logic.
 * 
 * @author ibarrera
 */
public class VSMService
{
    /** The singleton instance of the service. */
    private static VSMService instance;

    /** The Monitor manager. */
    protected MonitorManager monitorManager;

    /**
     * The <code>VSMService</code> factory method.
     * 
     * @return The singleton instance of the <code>VSMService</code>.
     */
    public static VSMService getInstance()
    {
        if (instance == null)
        {
            instance = new VSMService();
        }

        return instance;
    }

    /**
     * Creates the VSM service.
     */
    protected VSMService()
    {
        monitorManager = new MonitorManager();
    }

    public void getState(String physicalMachineAddress, String type, String virtualMachineName)
    {
        try
        {
            monitorManager.getState(physicalMachineAddress, Type.valueOf(type), virtualMachineName);
        }
        catch (MonitorException ex)
        {
            throw new VSMException("Could not get the state of virtual machine "
                + virtualMachineName + " on " + physicalMachineAddress, ex);
        }
    }

    public PhysicalMachine monitor(String physicalMachineAddress, String type, String username,
        String password)
    {
        try
        {
            return monitorManager.monitor(physicalMachineAddress, Type.valueOf(type), username,
                password);
        }
        catch (MonitorException ex)
        {
            throw new VSMException("Could not start monitoring " + physicalMachineAddress, ex);
        }
    }

    public void createAndStartMonitor(String physicalMachineAddress, String type, String username,
        String password)
    {
        try
        {
            monitorManager.createAndStartMonitor(physicalMachineAddress, Type.valueOf(type),
                username, password);
        }
        catch (MonitorException ex)
        {
            throw new VSMException("Could not start monitoring " + physicalMachineAddress, ex);
        }
    }

    public void shutdown(String physicalMachineAddress, String type)
    {
        try
        {
            monitorManager.shutdown(physicalMachineAddress, Type.valueOf(type));
        }
        catch (MonitorException ex)
        {
            throw new VSMException("Could not shutdown monitor for " + physicalMachineAddress, ex);
        }
    }

    public void stopAllMonitors()
    {
        monitorManager.stopAllMonitors();
    }

    public VirtualMachine subscribe(String physicalMachineAddress, String type,
        String virtualMachineName)
    {
        try
        {
            return monitorManager.subscribe(physicalMachineAddress, Type.valueOf(type),
                virtualMachineName);
        }
        catch (MonitorException ex)
        {
            throw new VSMException("Could not subscribe to " + virtualMachineName, ex);
        }
    }

    public void unsubscribe(String physicalMachineAddress, String type, String virtualMachineName)
    {
        try
        {
            monitorManager.unsubscribe(physicalMachineAddress, Type.valueOf(type),
                virtualMachineName);
        }
        catch (MonitorException ex)
        {
            throw new VSMException("Could not unssubscribe from " + virtualMachineName, ex);
        }
    }

}
