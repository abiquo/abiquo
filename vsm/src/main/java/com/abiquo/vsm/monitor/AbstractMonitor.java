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
package com.abiquo.vsm.monitor;

import java.io.IOException;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.abiquo.vsm.VSMManager;
import com.abiquo.vsm.events.VMEvent;
import com.abiquo.vsm.events.VMEventType;
import com.abiquo.vsm.exception.MonitorException;
import com.abiquo.vsm.model.PhysicalMachine;
import com.abiquo.vsm.model.VirtualMachine;
import com.abiquo.vsm.redis.dao.RedisDao;
import com.abiquo.vsm.redis.dao.RedisDaoFactory;
import com.abiquo.vsm.redis.pubsub.RedisPublisher;

/**
 * Base class for all monitor implementations.
 * 
 * @author ibarrera
 */
public abstract class AbstractMonitor
{
    /** The logger. */
    private static final Logger LOGGER = LoggerFactory.getLogger(AbstractMonitor.class);

    /** Event notifier used to push events to Redis. */
    private RedisPublisher redisPublisher;

    /** List of monitored physical machine addresses. */
    protected List<String> monitoredMachines;

    /** The dao used to access stored data. */
    private RedisDao dao;

    /**
     * Creates the monitor.
     */
    public AbstractMonitor()
    {
        String redisHost = VSMManager.getInstance().getRedisHost();
        int redisPort = VSMManager.getInstance().getRedisPort();

        redisPublisher = new RedisPublisher(redisHost, redisPort);
        monitoredMachines = Collections.synchronizedList(new LinkedList<String>());
        dao = RedisDaoFactory.getInstance();
    }

    /**
     * Start monitoring the target physical machine.
     */
    public abstract void start();

    /**
     * Stop monitoring the target physical machine and shutdown the monitor.
     */
    public abstract void shutdown();

    /**
     * Publish the current state of the given virtual machine.
     * 
     * @param physicalMachineAddress The monitored physical machine where the virtual machine is
     *            deployed.
     * @param virtualMachineName The name of the virtual machine.
     * @throws MonitorException If an error occurs while getting the state of the virtual machine.
     */
    public void publishState(String physicalMachineAddress, String virtualMachineName)
        throws MonitorException
    {
        // Update the last known state to UNKNOWN in order to force the event notification
        VirtualMachine virtualMachine = dao.findVirtualMachineByName(virtualMachineName);

        if (virtualMachine != null)
        {
            virtualMachine.setLastKnownState(VMEventType.UNKNOWN.name());
            dao.save(virtualMachine);
        }
    }

    /**
     * Return the maximum number of hypervisors that can be monitored by this monitor instance.
     * 
     * @return The maximum number of hypervisors that can be monitored by this monitor instance.
     */
    public abstract int getMaxNumberOfHypervisors();

    /**
     * Check if there are available slots to monitor more hypervisors.
     * 
     * @return Boolean indicating if there are available slots to monitor more hypervisors.
     */
    public boolean hasAvailableSlots()
    {
        synchronized (monitoredMachines)
        {
            int max = getMaxNumberOfHypervisors();
            return (max == 0) ? true : max > monitoredMachines.size();
        }
    }

    /**
     * Add a physical machine to be monitored.
     * 
     * @param physicalMachineAddress The physical machine address to be monitored.
     * @throws MonitorException If the physical machine can not be added.
     */
    public void addPhysicalMachine(String physicalMachineAddress) throws MonitorException
    {
        synchronized (monitoredMachines)
        {
            monitoredMachines.add(physicalMachineAddress);
        }

        LOGGER.debug("Added {} to the list of monitored machines", physicalMachineAddress);
    }

    /**
     * Remove a physical machine from the monitored machines list.
     * 
     * @param physicalMachineAddress The physical machine to remove from the monitored machines
     *            list.
     * @throws MonitorException If the physical machine can not be removed.
     */
    public void removePhysicalMachine(String physicalMachineAddress) throws MonitorException
    {
        synchronized (monitoredMachines)
        {
            monitoredMachines.remove(physicalMachineAddress);

            LOGGER.debug("Removed {} from the list of monitored machines", physicalMachineAddress);

            if (monitoredMachines.isEmpty())
            {
                LOGGER.info("There are no more machines to monitor. Shutting down.");
                shutdown();
            }
        }
    }

    /**
     * Check if the current monitor is monitoring the given physical machine.
     * 
     * @param physicalMachineAddress The physical machine.
     * @return Boolean indicating if the current monitor is monitoring the given physical machine.
     */
    public boolean monitors(String physicalMachineAddress)
    {
        synchronized (monitoredMachines)
        {
            return monitoredMachines.contains(physicalMachineAddress);
        }
    }

    /**
     * Notifies an event that has been fired in a virtual machine.
     * 
     * @param event The event to propagate.
     */
    public void notify(VMEvent event)
    {
        try
        {
            LOGGER.trace("Received event: {}", event.toString());
            redisPublisher.publishEvent(event);
        }
        catch (IOException ex)
        {
            LOGGER.error("Unable to notify event: " + event.toString(), ex);
        }
    }

    /**
     * Gets the monitored machine list
     * 
     * @return the list of monitored machines
     */
    public List<String> getMonitoredMachines()
    {
        return monitoredMachines;
    }

    /**
     * Get the given physical machine from the database.
     * 
     * @param physicalMachineAddress The address of the physical machine.
     * @return The physical machine.
     * @throws MonitorException If the physical machine is not found.
     */
    protected PhysicalMachine getPhysicalMachine(String physicalMachineAddress)
        throws MonitorException
    {
        PhysicalMachine pm = dao.findPhysicalMachineByAddress(physicalMachineAddress);

        if (pm == null)
        {
            throw new MonitorException("The physical machine at " + physicalMachineAddress
                + " is not being monitored");
        }

        return pm;
    }

}
