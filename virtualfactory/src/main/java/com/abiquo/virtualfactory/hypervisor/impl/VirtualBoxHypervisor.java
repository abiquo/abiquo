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

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Map;

import javax.xml.ws.BindingProvider;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.virtualbox_4_0.IConsole;
import org.virtualbox_4_0.ISession;
import org.virtualbox_4_0.IVirtualBox;
import org.virtualbox_4_0.VirtualBoxManager;
import org.virtualbox_4_0.jaxws.VboxPortType;

import com.abiquo.virtualfactory.constants.MessageValues;
import com.abiquo.virtualfactory.exception.HypervisorException;
import com.abiquo.virtualfactory.exception.VirtualMachineException;
import com.abiquo.virtualfactory.machine.impl.VirtualBoxMachine;
import com.abiquo.virtualfactory.model.AbsVirtualMachine;
import com.abiquo.virtualfactory.model.IHypervisor;
import com.abiquo.virtualfactory.model.State;
import com.abiquo.virtualfactory.model.config.VirtualMachineConfiguration;
import com.sun.xml.ws.developer.JAXWSProperties;

/**
 * The Class VirtualBoxHypervisor represents the VirtualBox hypervisor.
 */
public class VirtualBoxHypervisor implements IHypervisor
{

    /** The Constant logger. */
    private final static Logger logger =
        LoggerFactory.getLogger(VirtualBoxHypervisor.class.getName());

    /** The url. */
    private URL url;

    /** The mgr. */
    private VirtualBoxManager mgr;

    /** The vbox. */
    private IVirtualBox vbox;

    /** The admin user */
    private String user;

    /** The admin password */
    private String password;

    private int vboxport = 18083;

    /**
     * virtual box specific function to VirtualBoxMachine.
     * 
     * @return the session
     */
    public ISession getSession()
    {
        return mgr.getSessionObject();
    }

    /**
     * virtual box specific function to VirtualBoxMachine.
     * 
     * @return the virtual box
     */
    public IVirtualBox getVirtualBox()
    {
        return vbox;
    }

    /**
     * Gets the console object
     * 
     * @return the console
     */
    public IConsole getConsole()
    {
        IConsole console = getSession().getConsole();
        return console;

    }

    /*
     * (non-Javadoc)
     * @see com.abiquo.abicloud.model.IHypervisor#connect(java.net.URL)
     */
    public void connect(final URL url)
    {
        this.url = url;
        URL vboxURL = null;

        try
        {
            vboxURL = new URL(url.getProtocol(), url.getHost(), vboxport, url.getFile());
        }
        catch (MalformedURLException e)
        {
            logger.error("Malformed URL for connecting to VirtualBox", e);
        }

        logger.info("Connecting to the VirtualBox Hypervisor: {}", url.toExternalForm());
        logger.debug("Initializating WebSessionManager...");
        mgr = VirtualBoxManager.createInstance(null);
        logger.debug("Logging into Session");
        mgr.connect(vboxURL.toExternalForm(), user, password);
        logger.debug("Getting session Object");
        vbox = mgr.getVBox();
        VboxPortType service = vbox.getRemoteWSPort();
        BindingProvider bindingProvider = (BindingProvider) service;
        Map<String, Object> requestContext = bindingProvider.getRequestContext();
        requestContext.put(JAXWSProperties.REQUEST_TIMEOUT, 0);
        requestContext.put(JAXWSProperties.CONNECT_TIMEOUT, 0);
    }

    /**
     * Reconnects to the hypervisors
     */
    public void reconnect()
    {
        if (this.mgr == null)
        {
            connect(this.url);
        }
    }

    /*
     * (non-Javadoc)
     * @see com.abiquo.abicloud.model.IHypervisor#logout()
     */
    public void logout()
    {
        mgr.disconnect();
        mgr.cleanup();

        logger.info("Logged out form VirtualBox at address:" + url.toString());

        vbox = null;
        mgr = null;
    }

    /*
     * (non-Javadoc)
     * @see com.abiquo.abicloud.model.IHypervisor#login(java.lang.String, java.lang.String)
     */
    public void login(final String user, final String password)
    {
        this.user = user;
        this.password = password;
    }

    /*
     * (non-Javadoc)
     * @see com.abiquo.abicloud.model.IHypervisor#createMachine(com.abiquo.abicloud
     * .model.VirtualMachineConfiguration)
     */
    public AbsVirtualMachine createMachine(final VirtualMachineConfiguration config)
        throws VirtualMachineException
    {
        return new VirtualBoxMachine(config);
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
     * @see com.abiquo.abicloud.model.IHypervisor#getHypervisorType()
     */
    public String getHypervisorType()
    {
        return "vbox";
    }

    @Override
    public void init(final URL url, final String user, final String password)
        throws HypervisorException
    {
        logger.debug("VirtualBox Hypervisor initialized");
        login(user, password);

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

    @Override
    public void disconnect() throws HypervisorException
    {
        this.logout();
    }

    @Override
    public AbsVirtualMachine getMachine(final VirtualMachineConfiguration virtualMachineConfig)
        throws HypervisorException
    {
        init(this.url, this.user, this.password);
        connect(this.url);
        try
        {
            vbox.findMachine(virtualMachineConfig.getMachineName());
        }
        catch (Exception e)
        {
            try
            {
                vbox.findMachine(virtualMachineConfig.getMachineId().toString());
            }
            catch (Exception e1)
            {
                logger.debug(MessageValues.VM_NOT_FOUND + virtualMachineConfig.getMachineName());
                disconnect();
                return null;
            }
        }
        AbsVirtualMachine vm;
        try
        {
            virtualMachineConfig.setHypervisor(this);
            vm = createMachine(virtualMachineConfig);
            vm.setState(State.DEPLOYED);
        }
        catch (VirtualMachineException e)
        {
            disconnect();
            throw new HypervisorException(e);
        }
        disconnect();
        return vm;

    }

    /**
     * Gets the Virtual Box Manager
     * 
     * @return
     */
    public VirtualBoxManager getVboxManager()
    {
        return this.mgr;
    }

}
