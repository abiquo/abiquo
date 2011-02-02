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

package com.abiquo.virtualfactory.hypervisor.impl;

import java.net.URL;

import org.libvirt.Connect;
import org.libvirt.Domain;
import org.libvirt.LibvirtException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.abiquo.virtualfactory.constants.MessageValues;
import com.abiquo.virtualfactory.exception.HypervisorException;
import com.abiquo.virtualfactory.exception.VirtualMachineException;
import com.abiquo.virtualfactory.machine.impl.LibvirtMachine;
import com.abiquo.virtualfactory.model.AbsVirtualMachine;
import com.abiquo.virtualfactory.model.IHypervisor;
import com.abiquo.virtualfactory.model.State;
import com.abiquo.virtualfactory.model.config.VirtualMachineConfiguration;

/**
 * The Class KVMHypervisor represents the KVM hypervisor.
 * 
 * @author Marc Morata Fité
 * @author Zeus Gomez
 */

public abstract class AbsLibvirtHypervisor implements IHypervisor
{
    /** The Constant logger. */
    protected final static Logger logger =
        LoggerFactory.getLogger(AbsLibvirtHypervisor.class.getName());

    /** The original url. **/
    protected URL url;

    private String user;

    private String password;

    public abstract String getHypervisorUrl(URL url);

    public abstract String getHypervisorType();

    public void init(URL url, String user, String password) throws HypervisorException
    {
        this.url = url;
        this.user = user;
        this.password = password;
    }

    public void connect(final URL url) throws HypervisorException
    {
        // Do nothing, the connection logic is in LibvirtMachine
    }

    public void disconnect() throws HypervisorException
    {
        // Do nothing, the connection logic is in LibvirtMachine
    }

    /*
     * (non-Javadoc)
     * @see com.abiquo.abicloud.model.IHypervisor#createMachine(com.abiquo.abicloud
     * .model.VirtualMachineConfiguration)
     */
    public AbsVirtualMachine createMachine(final VirtualMachineConfiguration config)
        throws VirtualMachineException
    {

        return new LibvirtMachine(config);
    }

    /*
     * (non-Javadoc)
     * @see com.abiquo.abicloud.model.IHypervisor#getAddress()
     */
    public URL getAddress()
    {
        return url;
    }

    /*
     * (non-Javadoc)
     * @see com.abiquo.abicloud.model.IHypervisor#logout()
     */
    public void logout()
    {
        // Do nothing, the connection logic is in LibvirtMachine
    }

    /*
     * (non-Javadoc)
     * @see com.abiquo.abicloud.model.IHypervisor#login(java.lang.String, java.lang.String)
     */
    public void login(final String user, final String password)
    {
        // Do nothing, the connection logic is in LibvirtMachine
    }

    /**
     * Gets the user
     * 
     * @return the user
     */
    public String getUser()
    {
        return user;
    }

    /**
     * Gets the password
     * 
     * @return the password
     */
    public String getPassword()
    {
        return password;
    }

    public AbsVirtualMachine getMachine(VirtualMachineConfiguration virtualMachineConfig)
        throws HypervisorException
    {
        Connect conn = null;
        Domain dom = null;

        try
        {
            conn = connect(conn);
            dom = conn.domainLookupByName(virtualMachineConfig.getMachineName());
            virtualMachineConfig.setHypervisor(this);
            AbsVirtualMachine vm = createMachine(virtualMachineConfig);
            vm.setState(State.DEPLOYED);
            return vm;
        }
        catch (Exception e)
        {
            logger.debug(MessageValues.VM_NOT_FOUND + virtualMachineConfig.getMachineName());
            return null;
        }
        finally
        {
            try
            {
                disconnect(conn, dom);
            }
            catch (LibvirtException e)
            {
                logger.error("An error was occurred disconnecting from the libvirt hypervisor", e);
            }

        }
    }

    private Connect connect(Connect conn) throws LibvirtException
    {
        if (conn != null)
        {
            if (conn.isConnected())
            {
                logger
                    .error("Trying to instance a connection already connected. Something is wrong in the code!.");
                return conn;
            }
        }

        conn = new Connect(getHypervisorUrl(getAddress()));

        return conn;
    }

    /**
     * TODO: Refactor to use the same methods define in LibvirtMachine to create and free
     * connections
     * 
     * @param conn
     * @param doms
     * @return
     * @throws LibvirtException
     */
    private Connect disconnect(Connect conn, Domain... doms) throws LibvirtException
    {
        for (Domain dom : doms)
        {
            if (dom != null)
            {
                dom.free();
            }
        }

        if (conn != null)
        {
            conn.close();
        }

        return conn;
    }

}
