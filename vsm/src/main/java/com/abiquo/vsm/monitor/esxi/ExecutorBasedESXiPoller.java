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
package com.abiquo.vsm.monitor.esxi;

import static com.abiquo.vsm.events.VMEventType.CREATED;
import static com.abiquo.vsm.events.VMEventType.DESTROYED;
import static com.abiquo.vsm.events.VMEventType.UNKNOWN;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.HashSet;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.abiquo.vsm.events.VMEvent;
import com.abiquo.vsm.events.VMEventType;
import com.abiquo.vsm.exception.MonitorException;
import com.abiquo.vsm.model.PhysicalMachine;
import com.abiquo.vsm.model.VirtualMachinesCache;
import com.abiquo.vsm.monitor.AbstractMonitor;
import com.abiquo.vsm.monitor.Monitor;
import com.abiquo.vsm.monitor.Monitor.Type;
import com.abiquo.vsm.monitor.executor.AbstractTask;
import com.abiquo.vsm.monitor.executor.PeriodicalExecutor;
import com.vmware.vim25.ObjectContent;
import com.vmware.vim25.VirtualMachineConfigInfo;

/**
 * The ESXi monitor.
 * 
 * @author ibarrera
 */
@Monitor(type = Type.VMX_04)
public class ExecutorBasedESXiPoller extends AbstractMonitor
{
    /** The logger. */
    private final static Logger LOGGER = LoggerFactory.getLogger(ExecutorBasedESXiPoller.class);

    /** Maximum number of machines this monitor can manage. */
    public static final int MAX_MONITORED_MACHINES = 1;

    /** HyperV WMI based connector */
    private ESXiConnector esx;

    // Polling stuff

    /** The executor */
    private PeriodicalExecutor executor;

    /** The poller */
    private Poller poller;

    /**
     * Creates the ESXi monitor.
     */
    public ExecutorBasedESXiPoller()
    {
        int pollingInterval =
            Integer.valueOf(System.getProperty("abiquo.vsm.esx.pollinginterval", "5000"));

        esx = new ESXiConnector();
        poller = new Poller();
        executor = createExecutor(poller, pollingInterval);
    }

    @Override
    public int getMaxNumberOfHypervisors()
    {
        return MAX_MONITORED_MACHINES;
    }

    @Override
    public void shutdown()
    {
        String physicalmachines = StringUtils.join(monitoredMachines, ", ");
        LOGGER.debug("Stopping ESXi monitor {} for: {}", uuid, physicalmachines);

        executor.stop();
    }

    @Override
    public void start()
    {
        LOGGER.debug("Starting ESXi monitor {}", uuid);
        executor.start();
    }

    @Override
    public void publishState(final String physicalMachineAddress, final String virtualMachineName)
        throws MonitorException
    {
        super.publishState(physicalMachineAddress, virtualMachineName);

        // Connect to the hypervisor
        PhysicalMachine pm = getPhysicalMachine(physicalMachineAddress);
        esx.connect(physicalMachineAddress, pm.getUsername(), pm.getPassword());

        // Get concrete virtual machine state
        VMEventType state = VMEventType.UNKNOWN;
        try
        {
            state = esx.getState(virtualMachineName);
        }
        finally
        {
            esx.disconnect();
        }

        // Publish the event
        this.notify(new VMEvent(state, physicalMachineAddress, virtualMachineName));
    }

    /**
     * Creates and initializes the {@link PeriodicalExecutor}.
     * 
     * @param poller The poller to be executed.
     * @param pollInterval The polling execution interval.
     * @return The PeriodicalExecutor.
     */
    private PeriodicalExecutor createExecutor(final Poller poller, final int pollInterval)
    {
        LOGGER.debug("Creating new ESX monitor with polling interval: {} ms", pollInterval);

        return new PeriodicalExecutor(poller, pollInterval)
        {
            @Override
            public void executionFailure(final Throwable t)
            {
                LOGGER.trace("An unexpected error occured while monitoring physical machine", t);
            }
        };
    }

    /**
     * Performs synchronous polling calls to get the state of the monitored virtual machines.
     * 
     * @author ibarrera
     * @author Enric Ruiz
     */
    private class Poller extends AbstractTask
    {
        @Override
        public void execute() throws Exception
        {
            // It is important to synchronize the monitoredMachines list to avoid errors if a
            // machine is added or removed while iterating the list
            synchronized (monitoredMachines)
            {
                for (String address : monitoredMachines)
                {
                    LOGGER.trace("Getting information from: {}", address);

                    PhysicalMachine pm = getPhysicalMachine(address);
                    VirtualMachinesCache cache = pm.getVirtualMachines();

                    esx.connect(address, pm.getUsername(), pm.getPassword());

                    try
                    {
                        // Virtual machines running in the current poller execution
                        Set<String> current = new HashSet<String>();

                        LOGGER.trace("Getting information from the current VMs...");

                        ObjectContent[] vms = esx.getAllVMs();

                        if (vms != null)
                        {
                            for (ObjectContent vm : vms)
                            {
                                VirtualMachineConfigInfo vmConfig =
                                    esx.getVMConfigFromObjectContent(vm);

                                if (vmConfig == null)
                                {
                                    continue;
                                }

                                current.add(decodeURLRawString(vmConfig.getName()));
                            }
                        }

                        // Virtual machines running in last poller execution
                        Set<String> cached = new HashSet<String>(cache.getCache());

                        // Virtual machines that remains cached and running
                        Set<String> intersection = new HashSet<String>(cached);
                        intersection.retainAll(current);

                        // Created virtual machines since last execution
                        Set<String> created = new HashSet<String>(current);
                        created.removeAll(cached);

                        // Removed virtual machines since last execution
                        Set<String> removed = new HashSet<String>(cached);
                        removed.removeAll(current);

                        // Clear the cache
                        cache.getCache().clear();

                        // Propagate events and rebuild the cache
                        for (String virtualMachine : intersection)
                        {
                            if (!isBeingMigrated(virtualMachine))
                            {
                                VMEventType state = esx.getState(virtualMachine);

                                LOGGER.debug("Found {} in state {}", virtualMachine, state.name());
                                notifyState(state, virtualMachine, address);
                                cache.getCache().add(virtualMachine);
                            }
                        }

                        for (String virtualMachine : created)
                        {
                            if (!isBeingMigrated(virtualMachine))
                            {
                                LOGGER.debug("Created {} at {}", virtualMachine, address);
                                notifyState(CREATED, virtualMachine, address);
                                cache.getCache().add(virtualMachine);
                            }
                        }

                        for (String virtualMachine : removed)
                        {
                            if (!isBeingMigrated(virtualMachine))
                            {
                                if (!existInVCenter(virtualMachine))
                                {
                                    LOGGER.debug("Removed {} from {}", virtualMachine, address);
                                    notifyState(DESTROYED, virtualMachine, address);
                                }
                                else
                                {
                                    // In order to check it in next poller execution
                                    cache.getCache().add(virtualMachine);
                                }
                            }
                            else
                            {
                                // In order to check it in next poller execution
                                cache.getCache().add(virtualMachine);
                            }
                        }
                    }
                    finally
                    {
                        esx.disconnect();
                    }
                }
            }
        }
    }

    /**
     * Notifies the given {@link VMEventType} state for the given virtual machine and machine
     * address.
     * 
     * @param state the state to notify
     * @param virtualMachineName the virtual machine name to query about
     * @param machineAddress the physical machine address where virtual machine is running
     */

    private void notifyState(final VMEventType state, final String virtualMachineName,
        final String machineAddress) throws MonitorException
    {
        if (!state.equals(UNKNOWN))
        {
            VMEvent event = new VMEvent(state, machineAddress, virtualMachineName);
            ExecutorBasedESXiPoller.this.notify(event);
        }
    }

    /**
     * Checks through vCenter if a virtual machine is being migrated.
     * 
     * @param virtualMachine the virtual machine name to query about
     * @return true if the virtual machine is being migrated, otherwise false.
     */
    private boolean isBeingMigrated(final String virtualMachine) throws MonitorException
    {
        return esx.isManagedByVCenter() ? esx.virtualMachineIsBeingMigrated(virtualMachine) : false;
    }

    /**
     * Checks if a virtual machine exists in the associated vCenter
     * 
     * @param virtualMachine the virtual machine name to query about
     * @return true if the virtual machine is running in a monitored host, otherwise false.
     */
    private boolean existInVCenter(final String virtualMachine) throws MonitorException
    {
        return esx.isManagedByVCenter() ? esx.existVirtualMachineInVCenter(virtualMachine) : false;
    }

    protected String decodeURLRawString(final String value)
    {
        try
        {
            return URLDecoder.decode(value, "UTF-8");
        }
        catch (UnsupportedEncodingException e)
        {
            LOGGER.error("Can not decode {} from URL raw encoding. {}", value, e);
            return value;
        }
    }
}
