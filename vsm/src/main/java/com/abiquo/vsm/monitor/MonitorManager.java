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

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.abiquo.vsm.VSMManager;
import com.abiquo.vsm.events.VMEventType;
import com.abiquo.vsm.exception.MonitorException;
import com.abiquo.vsm.model.PhysicalMachine;
import com.abiquo.vsm.model.VirtualMachine;
import com.abiquo.vsm.model.VirtualMachinesCache;
import com.abiquo.vsm.monitor.Monitor.Type;
import com.abiquo.vsm.monitor.esxi.ESXiMonitor;
import com.abiquo.vsm.monitor.hyperv.HyperVMonitor;
import com.abiquo.vsm.monitor.libvirt.KVMMonitor;
import com.abiquo.vsm.monitor.libvirt.XenMonitor;
import com.abiquo.vsm.monitor.vbox.VirtualBoxMonitor;
import com.abiquo.vsm.monitor.xenserver.XenServerMonitor;
import com.abiquo.vsm.redis.dao.RedisDao;
import com.abiquo.vsm.redis.dao.RedisDaoFactory;

/**
 * Manages physical machine monitor life cycle.
 * 
 * @author ibarrera
 */
public class MonitorManager
{
    /** The logger. */
    private static final Logger LOGGER = LoggerFactory.getLogger(MonitorManager.class);

    /** The list of available monitors, indexed by type. */
    protected Map<Type, Class< ? extends AbstractMonitor>> monitorClasses;

    /** The monitors that are running. */
    private Map<Type, List<AbstractMonitor>> runningMonitors;

    /** The dao used to access stored data. */
    protected RedisDao dao;

    /**
     * Creates the <code>MonitorManager</code> and registers all the monitors.
     */
    public MonitorManager()
    {
        super();
        monitorClasses = new HashMap<Type, Class< ? extends AbstractMonitor>>();
        runningMonitors = new HashMap<Type, List<AbstractMonitor>>();
        dao = RedisDaoFactory.getInstance();

        // Register monitors
        registerMonitor(KVMMonitor.class);
        registerMonitor(XenMonitor.class);
        registerMonitor(ESXiMonitor.class);
        registerMonitor(HyperVMonitor.class);
        registerMonitor(XenServerMonitor.class);
        registerMonitor(VirtualBoxMonitor.class);
    }

    /**
     * Start monitoring the target physical machine.
     * <p>
     * Creates the monitor or reuses an existing one to monitor the target physical machine.
     * 
     * @param physicalMachineAddress The address of the physical machine to monitor.
     * @param type The hypervisor type of the physical machine.
     * @param username The user name used to connect to the physical machine.
     * @param password The password used to connect to the physical machine.
     * @return The physical machine details.
     * @throws MonitorException If the monitor cannot be created.
     */
    public PhysicalMachine monitor(String physicalMachineAddress, Type type, String username,
        String password) throws MonitorException
    {
        PhysicalMachine pm = null;

        try
        {
            pm = dao.findPhysicalMachineByAddress(physicalMachineAddress);

            if (pm == null)
            {
                VirtualMachinesCache cache = new VirtualMachinesCache();
                dao.save(cache);

                pm = new PhysicalMachine();
                pm.setAddress(physicalMachineAddress);
                pm.setVirtualMachines(cache);
            }

            pm.setUsername(username);
            pm.setPassword(password);
            pm.setType(type.name());

            dao.save(pm);
        }
        catch (Exception ex)
        {
            throw new MonitorException("The physical machine at " + physicalMachineAddress
                + " could not be added to the list of monitored machines", ex);
        }

        try
        {
            // Create and start the monitor
            createAndStartMonitor(physicalMachineAddress, type, username, password);
        }
        catch (MonitorException e)
        {
            dao.delete(pm);
            throw e;
        }

        return pm;
    }

    /**
     * Creates and starts a new monitor, wihtout persisting it in the Redis database.
     * <p>
     * this method will be invoked by the {@link VSMManager} in the startup process to reload the
     * existing monitors.
     * 
     * @param physicalMachineAddress The address of the physical machine to monitor.
     * @param type The hypervisor type of the physical machine.
     * @param username The user name used to connect to the physical machine.
     * @param password The password used to connect to the physical machine.
     * @throws MonitorException If the monitor cannot be created.
     */
    public void createAndStartMonitor(String physicalMachineAddress, Type type, String username,
        String password) throws MonitorException
    {
        LOGGER.info("Start monitoring {} of type {}", physicalMachineAddress, type.name());

        // Get the monitor
        AbstractMonitor monitor = findAvailableMonitor(type);

        // Create and start a new one if necessary
        if (monitor == null)
        {
            monitor = createMonitor(type);
            monitor.start();

            // Once the monitor has started without errors, add it to the list
            // of running monitors
            addRunningMonitor(monitor, type);
        }

        // Start monitoring the target machine
        monitor.addPhysicalMachine(physicalMachineAddress);
    }

    /**
     * Stop monitoring the target physical machine.
     * 
     * @param physicalMachineAddress The address of the physical machine to stop monitoring.
     * @param type The hypervisor type of the physical machine.
     * @throws MonitorException If the shutdown operation cannot be performed.
     */
    public void shutdown(String physicalMachineAddress, Type type) throws MonitorException
    {
        AbstractMonitor monitor = findRunningMonitor(physicalMachineAddress, type);

        if (monitor != null)
        {
            LOGGER.info("Shutting down monitor for: {}", physicalMachineAddress);

            monitor.removePhysicalMachine(physicalMachineAddress);
            removeRunningMonitor(monitor, type);

            // Remove machine from db
            PhysicalMachine pm = getPhysicalMachine(physicalMachineAddress);
            dao.delete(pm);
        }
        else
        {
            LOGGER.info("No running monitor found for: {}", physicalMachineAddress);
        }
    }

    /**
     * Shutdown all running monitors.
     * 
     * @throws MonitorException If the shutdown operation fails.
     */
    public void stopAllMonitors()
    {
        for (List<AbstractMonitor> monitors : runningMonitors.values())
        {
            for (AbstractMonitor monitor : monitors)
            {
                monitor.shutdown();
            }
        }
    }

    /**
     * Add a subscription for a virtual machine events.
     * 
     * @param physicalMachineAddress The address of the physical machine where the virtual machine
     *            is deployed.
     * @param type The hypervisor type of the physical machine.
     * @param virtualMachineName The virtual machine to subscribe to.
     * @return The subscription details.
     * @throws MonitorException If the subscription cannot be performed.
     */
    public VirtualMachine subscribe(String physicalMachineAddress, Type type,
        String virtualMachineName) throws MonitorException
    {
        LOGGER.info("Subscribing to {} on {}", virtualMachineName, physicalMachineAddress);

        PhysicalMachine pm = getPhysicalMachine(physicalMachineAddress);

        if (dao.findVirtualMachineByName(virtualMachineName) != null)
        {
            throw new MonitorException("Virtual machine " + virtualMachineName
                + " already persisted.");
        }

        VirtualMachine vm = new VirtualMachine();
        vm.setName(virtualMachineName);
        vm.setPhysicalMachine(pm);
        vm.setLastKnownState(VMEventType.UNKNOWN.name());

        dao.save(vm);

        return vm;
    }

    /**
     * Remove a subscription for a virtual machine.
     * 
     * @param physicalMachineAddress The address of the physical machine where the virtual machine
     *            is deployed.
     * @param type The hypervisor type of the physical machine.
     * @param virtualMachineName The virtual machine to unsubscribe from.
     * @throws MonitorException If the unsubscription cannot be performed.
     */
    public void unsubscribe(String physicalMachineAddress, Type type, String virtualMachineName)
        throws MonitorException
    {
        LOGGER.info("Unsubscribing from {} on {}", virtualMachineName, physicalMachineAddress);

        VirtualMachine vm = dao.findVirtualMachineByName(virtualMachineName);

        if (vm == null)
        {
            throw new MonitorException("There is no subscription for virtual machine "
                + virtualMachineName);
        }

        dao.delete(vm);
    }

    /**
     * Get the state of the given virtual machine.
     * 
     * @param physicalMachineAddress The address of the physical machine where the virtual machine
     *            is deployed.
     * @param type The hypervisor type of the physical machine.
     * @param virtualMachineName The virtual machine to get the state from.
     * @throws MonitorException If the state cannot be retrieved.
     */
    public void getState(String physicalMachineAddress, Type type, String virtualMachineName)
        throws MonitorException
    {
        AbstractMonitor monitor = findRunningMonitor(physicalMachineAddress, type);

        if (monitor != null)
        {
            LOGGER.info("Getting state of virtual machine {} on {}", virtualMachineName,
                physicalMachineAddress);

            monitor.publishState(physicalMachineAddress, virtualMachineName);
        }
        else
        {
            throw new MonitorException(String.format("Machine %s doesn't have a running monitor",
                physicalMachineAddress));
        }
    }

    /**
     * Find a running monitor with free slots for the given hypervisor type. Return
     * <code>null</code> if no monitors are available.
     * 
     * @param type The type of the hypervisor to monitor.
     * @return A monitor with available slots, or <code>null</code> if none is available.
     */
    protected AbstractMonitor findAvailableMonitor(Type type)
    {
        List< ? extends AbstractMonitor> candidates = runningMonitors.get(type);

        if (candidates != null)
        {
            for (AbstractMonitor monitor : candidates)
            {
                if (monitor.hasAvailableSlots())
                {
                    return monitor;
                }
            }
        }

        return null;
    }

    /**
     * Find a running monitor that monitors the given physical machine.
     * 
     * @param physicalMachineAddress The address of the physical machine monitored by the monitor to
     *            find.
     * @param type The type of the hypervisor to monitor.
     * @return The monitor that monitors the given physical machine.
     */
    protected AbstractMonitor findRunningMonitor(String physicalMachineAddress, Type type)
    {
        List< ? extends AbstractMonitor> candidates = runningMonitors.get(type);

        if (candidates != null)
        {
            for (AbstractMonitor monitor : candidates)
            {
                if (monitor.monitors(physicalMachineAddress))
                {
                    return monitor;
                }
            }
        }

        return null;
    }

    /**
     * Instantiates a monitor for the given type of hypervisor.
     * 
     * @param type The type of the hypervisor to monitor.
     * @return The monitor.
     * @throws MonitorException If the monitor cannot be created.
     */
    protected AbstractMonitor createMonitor(Type type) throws MonitorException
    {
        try
        {
            Class< ? extends AbstractMonitor> monitorClass = monitorClasses.get(type);
            if (monitorClass != null)
            {
                LOGGER.info("Creating a new monitor of type {}", type.name());

                return monitorClass.newInstance();
            }

            throw new MonitorException("There is no monitor defined of type: " + type.name());
        }
        catch (Exception ex)
        {
            throw new MonitorException("There is no monitor defined of type: " + type.name(), ex);
        }

    }

    /**
     * Adds the given monitor class to the list of available monitors.
     * 
     * @param monitorClass The monitor class to add.
     */
    protected void registerMonitor(Class< ? extends AbstractMonitor> monitorClass)
    {
        Monitor config = monitorClass.getAnnotation(Monitor.class);

        if (config == null)
        {
            LOGGER.warn("Ignoring monitor: {}", monitorClass.getName());
        }
        else
        {
            LOGGER.info("Adding {} monitor: {}", config.type(), monitorClass.getName());
            monitorClasses.put(config.type(), monitorClass);
        }
    }

    /**
     * Adds the given monitor to the list of running monitors.
     * 
     * @param monitor The monitor to add.
     * @param type The type of the hypervisor that the monitor monitors.
     */
    private void addRunningMonitor(AbstractMonitor monitor, Type type)
    {
        List<AbstractMonitor> currentMonitors = runningMonitors.get(type);
        if (currentMonitors == null)
        {
            currentMonitors = new LinkedList<AbstractMonitor>();
        }

        currentMonitors.add(monitor);
        runningMonitors.put(type, currentMonitors);
    }

    /**
     * Removes the given monitor from the list of running monitors.
     * 
     * @param monitor The monitor to remove.
     * @param type The type of the hypervisor that the monitor monitors.
     */
    private void removeRunningMonitor(AbstractMonitor monitor, Type type)
    {
        List<AbstractMonitor> currentMonitors = runningMonitors.get(type);

        if (currentMonitors != null && currentMonitors.contains(monitor)
            && monitor.getMonitoredMachines().isEmpty())
        {
            currentMonitors.remove(monitor);
            runningMonitors.put(type, currentMonitors);
        }
    }

    /**
     * Get the given physical machine from the database.
     * 
     * @param physicalMachineAddress The address of the physical machine.
     * @return The physical machine.
     * @throws MonitorException If the physical machine is not found.
     */
    private PhysicalMachine getPhysicalMachine(String physicalMachineAddress)
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
