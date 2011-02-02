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

import com.abiquo.virtualfactory.exception.HypervisorException;
import com.abiquo.virtualfactory.exception.VirtualMachineException;
import com.abiquo.virtualfactory.model.config.VirtualMachineConfiguration;

/**
 * The Interface IHypervisor.
 */
public interface IHypervisor
{

    // TODO: required default constructor !!!

    /**
     * Connects to the hypervisor.
     * 
     * @param address the url
     * @throws HypervisorException
     */
    public void connect(URL address) throws HypervisorException;

    /**
     * Disconnects from the hypervisor. Currently it's just implemented for the Libvirt based
     * Hypervisors
     * 
     * @param address the url
     * @throws HypervisorException
     */
    public void disconnect() throws HypervisorException;

    /**
     * Gets the address.
     * 
     * @return the address
     */
    public URL getAddress();

    /**
     * Logins in with the user && password.
     * 
     * @param user the user
     * @param password the password
     */
    public void login(String user, String password);

    /**
     * Logs out the hypervisor user
     */
    public void logout();

    /**
     * Creates a new virtual machine.
     * 
     * @param config the config
     * @return the abs virtual machine
     * @throws VirtualMachineException the virtual machine exception
     */
    public AbsVirtualMachine createMachine(VirtualMachineConfiguration config)
        throws VirtualMachineException;

    /**
     * Returns the Hypervisor this class is wrapping.
     * 
     * @return the hypervisor type
     */
    public String getHypervisorType();

    /**
     * Initializes the hypervisor
     * 
     * @param address The hypervisor address
     * @param user the admin user
     * @param password the admin password
     * @throws If initialization fails
     */
    public void init(URL address, String user, String password) throws HypervisorException;

    /**
     * Gets the virtual machine from the hypervisor
     * 
     * @param virtualMachineConfig the virtual machine configuraiton
     * @return the virtual machine
     * @throws HypervisorException
     */
    public AbsVirtualMachine getMachine(VirtualMachineConfiguration virtualMachineConfig)
        throws HypervisorException;

}
