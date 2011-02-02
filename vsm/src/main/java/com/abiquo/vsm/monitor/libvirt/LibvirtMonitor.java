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

package com.abiquo.vsm.monitor.libvirt;

import java.net.MalformedURLException;
import java.net.URL;

import org.libvirt.Connect;
import org.libvirt.Domain;
import org.libvirt.LibvirtException;
import org.libvirt.DomainInfo.DomainState;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.abiquo.vsm.events.VMEvent;
import com.abiquo.vsm.events.VMEventType;
import com.abiquo.vsm.exception.MonitorException;
import com.abiquo.vsm.model.PhysicalMachine;
import com.abiquo.vsm.monitor.AbstractMonitor;

/**
 * The abstract libvirt monitor.
 * 
 * @author eruiz@abiquo.com
 */
public abstract class LibvirtMonitor extends AbstractMonitor
{
    /** The logger. */
    private final static Logger LOGGER = LoggerFactory.getLogger(LibvirtMonitor.class);

    @Override
    public int getMaxNumberOfHypervisors()
    {
        return 1;
    }

    @Override
    public void shutdown()
    {
        // Intentionally empty
    }

    @Override
    public void start()
    {
        // Intentionally empty
    }

    @Override
    public void publishState(String physicalMachineAddress, String virtualMachineName)
        throws MonitorException
    {
        super.publishState(physicalMachineAddress, virtualMachineName);

        // Connect to the hypervisor
        PhysicalMachine pm = getPhysicalMachine(physicalMachineAddress);
        Connect conn = connect(pm.getAddress());
        Domain dom = null;

        try
        {
            // Get concrete virtual machine state
            dom = conn.domainLookupByName(virtualMachineName);
            VMEventType state = translateEvent(dom.getInfo().state);

            // Publish the state
            notify(new VMEvent(state, physicalMachineAddress, virtualMachineName));
        }
        catch (LibvirtException e)
        {
            throw new MonitorException("Could not get the state of virtual machine: "
                + virtualMachineName, e);
        }
        finally
        {
            disconnect(conn, dom);
        }
    }

    /**
     * Creates a connection to the libvirt daemon of a physical machine.
     * 
     * @param address The address of the physical machine.
     * @return A new connection instance.
     * @throws MonitorException When the address is invalid or if is impossible to connect with the
     *             libvirt daemon.
     */
    private Connect connect(final String address) throws MonitorException
    {
        try
        {
            URL url = new URL(address);
            return new Connect(buildHypervisorURL(url), false);
        }
        catch (MalformedURLException e)
        {
            throw new MonitorException("Invalid physical machine address " + address, e);
        }
        catch (LibvirtException e)
        {
            throw new MonitorException("Unable to connect with " + address, e);
        }
    }

    /**
     * Closes a connection to libvirt freeing first the used domain.
     * 
     * @param conn The connection to close.
     * @param dom The domain to free.
     */
    private void disconnect(Connect conn, Domain dom)
    {
        try
        {
            if (dom != null)
            {
                dom.free();
            }

            if (conn != null)
            {
                conn.close();
            }
        }
        catch (LibvirtException e)
        {
            LOGGER.error("Unable to close the connection to libvirt", e);
        }
    }

    /**
     * Translates an libvirt event code into an {@link VMEventType}.
     * 
     * @param state Event code to translate.
     * @return Translated event.
     */
    private VMEventType translateEvent(final DomainState state)
    {
        VMEventType current = VMEventType.UNKNOWN;

        switch (state)
        {
            case VIR_DOMAIN_RUNNING:
                current = VMEventType.POWER_ON;
                break;

            case VIR_DOMAIN_PAUSED:
                current = VMEventType.PAUSED;
                break;

            case VIR_DOMAIN_SHUTDOWN:
                current = VMEventType.POWER_OFF;
                break;

            case VIR_DOMAIN_CRASHED:
                current = VMEventType.UNKNOWN;
                break;

            case VIR_DOMAIN_SHUTOFF:
                current = VMEventType.POWER_OFF;
                break;

            case VIR_DOMAIN_BLOCKED:
                current = VMEventType.POWER_ON;
                break;

            case VIR_DOMAIN_NOSTATE:
                current = VMEventType.UNKNOWN;

            default:
                break;
        }

        return current;
    }

    /**
     * Builds a specific libvirt connection URI by each hypervisor.
     * 
     * @param url The physical machine address, only host will be used.
     * @return The specific libvirt connection URI.
     */
    public abstract String buildHypervisorURL(final URL url);
}
