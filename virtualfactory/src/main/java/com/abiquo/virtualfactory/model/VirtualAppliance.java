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

package com.abiquo.virtualfactory.model;

import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.abiquo.virtualfactory.exception.VirtualMachineException;
import com.abiquo.virtualfactory.model.config.VirtualMachineConfiguration;

/**
 * This class represents a Virtual Appliance.
 */
public class VirtualAppliance
{

    /** The logger */
    private final static Logger logger = LoggerFactory.getLogger(VirtualAppliance.class);

    /** The machines. */
    private Collection<AbsVirtualMachine> machines;

    /** The virtual appliance id. */
    private String virtualApplianceId;

    /** The virtual appliance state */
    private State state;

    /**
     * Instantiates a new virtual appliance.
     */
    public VirtualAppliance()
    {
        machines = new ArrayList<AbsVirtualMachine>();
        state = State.NOT_DEPLOYED;
    }

    /**
     * Gets the virtual appliance state
     * 
     * @return the state
     */
    public State getState()
    {
        return state;
    }

    /**
     * Sets the virtual appliance state
     * 
     * @param state the state to set
     */
    public void setState(State state)
    {
        this.state = state;
    }

    /**
     * Gets the Virtual Appliance Id.
     * 
     * @return the Virtual Appliance Id
     */
    public String getVirtualApplianceId()
    {
        return virtualApplianceId;
    }

    /**
     * Sets the Virtual Appliance Id.
     * 
     * @param virtualApplianceId the new Virtual Appliance Id
     */
    public void setVirtualApplianceId(String virtualApplianceId)
    {
        this.virtualApplianceId = virtualApplianceId;
    }

    /**
     * Adds a virtual machine.
     * 
     * @param type the virtual machine type
     * @param address the hypervisor address where the machine shall be deployed
     * @param config the configuration object with other parameters
     * @param user TODO
     * @param password TODO
     * @return the virtual machine added
     * @throws VirtualMachineException the virtual machine exception
     */
    public AbsVirtualMachine addMachine(String type, URL address,
        VirtualMachineConfiguration config, String user, String password)
        throws VirtualMachineException
    {
        AbsVirtualMachine newMachine =
            VirtualSystemModel.getModel().createVirtualMachine(type, address, config, user,
                password);
        machines.add(newMachine);
        return newMachine;
    }

    /**
     * Gets the machines list.
     * 
     * @return the machines list
     */
    public Collection<AbsVirtualMachine> getMachines()
    {
        return machines;
    }

    /**
     * Cleans the cancelled machines
     */
    public void cleanCancelledMachines()
    {
        List<AbsVirtualMachine> machinesToAnalyze = new ArrayList<AbsVirtualMachine>();
        machinesToAnalyze.addAll(machines);
        for (AbsVirtualMachine machine : machinesToAnalyze)
        {
            if (machine.getState().compareTo(State.CANCELLED) == 0)
            {
                UUID machineId = machine.getConfiguration().getMachineId();
                machines.remove(machine);
            }
        }

    }

}
