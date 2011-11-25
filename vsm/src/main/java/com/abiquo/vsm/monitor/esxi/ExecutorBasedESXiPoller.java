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
import com.abiquo.vsm.redis.dao.RedisDao;
import com.abiquo.vsm.redis.dao.RedisDaoFactory;
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

    /** The dao used to access stored data. */
    private RedisDao dao;

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
        esx = new ESXiConnector();
        dao = RedisDaoFactory.getInstance();
        poller = new Poller();
        executor = createExecutor(poller, Poller.POLLING_INTERVAL);
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
        LOGGER.debug("Stopping ESXi monitor for: {}", physicalmachines);

        executor.stop();
    }

    @Override
    public void start()
    {
        LOGGER.debug("Starting ESXi monitor");

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
            esx.disconnect(physicalMachineAddress);
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
     */
    private class Poller extends AbstractTask
    {
        /** The polling interval. */
        public static final int POLLING_INTERVAL = 5000;

        @Override
        public void execute() throws Exception
        {
            // It is important to synchronize the monitoredMachines list to avoid errors if a
            // machine is added or removed while iterating the list
            synchronized (monitoredMachines)
            {
                for (String physicalMachineAddress : monitoredMachines)
                {
                    LOGGER.trace("Getting information from: {}", physicalMachineAddress);

                    PhysicalMachine pm = getPhysicalMachine(physicalMachineAddress);
                    VirtualMachinesCache cache = pm.getVirtualMachines();

                    esx.connect(physicalMachineAddress, pm.getUsername(), pm.getPassword());

                    // Current VMs in the hypervisor. Used to detect CREATE and DESTROY events
                    Set<String> currentVMs = new HashSet<String>();

                    try
                    {
                        LOGGER.trace("Getting information from the current VMS...");

                        // Get states
                        ObjectContent[] vms = esx.getAllVMs();

                        for (ObjectContent vm : vms)
                        {
                            VirtualMachineConfigInfo vmConfig =
                                esx.getVMConfigFromObjectContent(vm);

                            if (vmConfig == null)
                            {
                                continue;
                            }

                            // Save the VM in the list of current VMs
                            String vmName = vmConfig.getName();
                            currentVMs.add(vmName);

                            // Get the new state of the VM
                            VMEventType state = esx.getStateForObject(vm);
                            LOGGER.trace("Found VM {} in state {}", vmName, state.name());

                            VMEvent event = new VMEvent(state, physicalMachineAddress, vmName);

                            // Propagate the event. RedisSubscriber will decide if it must be
                            // notified, based on subscription information
                            ExecutorBasedESXiPoller.this.notify(event);
                        }

                        if (LOGGER.isTraceEnabled())
                        {
                            String cacheStr = StringUtils.join(cache.getCache(), ", ");
                            LOGGER.trace(
                                "Cache for Machine {} before generating CREATE/DESTROY is: {}",
                                physicalMachineAddress, cacheStr);
                        }

                        // Propagate create and destroy events
                        propagateCreateAndDestroyEvents(pm, currentVMs);

                        // Update the physical machine with the current machines in the hypervisor
                        cache.getCache().clear();
                        cache.getCache().addAll(currentVMs);

                        if (LOGGER.isTraceEnabled())
                        {
                            String cacheStr = StringUtils.join(cache.getCache(), ", ");
                            LOGGER.trace(
                                "Cache for Machine {} after generating CREATE/DESTROY is: {}",
                                physicalMachineAddress, cacheStr);
                        }
                    }
                    finally
                    {
                        esx.disconnect(physicalMachineAddress);
                    }
                }
            }
        }

        /**
         * Propagates events for each created and destroyed virtual machine.
         * 
         * @param pm The physical machine being monitored.
         * @param currentVMs The current virtual machines in the hypervisor.
         */
        private void propagateCreateAndDestroyEvents(final PhysicalMachine pm,
            final Set<String> currentVMs)
        {
            // Propagate DESTROY events
            Set<String> removedVMs = pm.getVirtualMachines().getCache();
            removedVMs.removeAll(currentVMs);

            for (String removed : removedVMs)
            {
                VMEvent event = new VMEvent(VMEventType.DESTROYED, pm.getAddress(), removed);

                LOGGER.trace("Removed VM {} from {}", removed, pm.getAddress());

                ExecutorBasedESXiPoller.this.notify(event);
            }

            // Propagate CREATE events
            Set<String> createdVMs = new HashSet<String>(currentVMs);
            createdVMs.removeAll(pm.getVirtualMachines().getCache());

            for (String created : createdVMs)
            {
                VMEvent event = new VMEvent(VMEventType.CREATED, pm.getAddress(), created);

                LOGGER.trace("New VM {} at {}", created, pm.getAddress());

                ExecutorBasedESXiPoller.this.notify(event);
            }
        }
    }

}
