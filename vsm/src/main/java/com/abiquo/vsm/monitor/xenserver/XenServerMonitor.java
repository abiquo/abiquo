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
package com.abiquo.vsm.monitor.xenserver;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.abiquo.vsm.events.VMEvent;
import com.abiquo.vsm.events.VMEventType;
import com.abiquo.vsm.exception.MonitorException;
import com.abiquo.vsm.model.PhysicalMachine;
import com.abiquo.vsm.monitor.AbstractMonitor;
import com.abiquo.vsm.monitor.Monitor;
import com.abiquo.vsm.monitor.Monitor.Type;
import com.xensource.xenapi.Connection;
import com.xensource.xenapi.Event;
import com.xensource.xenapi.Types;
import com.xensource.xenapi.VM;
import com.xensource.xenapi.Types.EventsLost;
import com.xensource.xenapi.Types.VmOperations;
import com.xensource.xenapi.Types.VmPowerState;

/**
 * The XenServer monitor.
 * 
 * @author destevez
 */
@Monitor(type = Type.XENSERVER)
public class XenServerMonitor extends AbstractMonitor
{
    /** The logger. */
    private final static Logger LOGGER = LoggerFactory.getLogger(XenServerMonitor.class);

    /** Maximum number of machines this monitor can manage. */
    public static final int MAX_MONITORED_MACHINES = 1;

    /** The list of active pollers. */
    private List<XenServerSubscriber> subscribers;

    /**
     * Creates the <code>XenServerMonitor</code>.
     */
    public XenServerMonitor()
    {
        subscribers = new LinkedList<XenServerSubscriber>();
    }

    @Override
    public int getMaxNumberOfHypervisors()
    {
        return MAX_MONITORED_MACHINES;
    }

    @Override
    public void start()
    {
        LOGGER.debug("Starting XenServer monitor");
        // Do nothing. The addPhysicalMachine does all the work.
    }

    @Override
    public void shutdown()
    {
        LOGGER.debug("Shutting down XenServer monitor");
        // Do nothing. The removePhysicalMachine does all the work.
    }

    @Override
    public void addPhysicalMachine(String physicalMachineAddress) throws MonitorException
    {
        super.addPhysicalMachine(physicalMachineAddress);

        // Start monitoring the physical machine in a separate thread
        XenServerSubscriber subscriber = new XenServerSubscriber(physicalMachineAddress);
        subscriber.start();

        // Save the poller in the list of pollers, to be able to stop it when needed
        subscribers.add(subscriber);
    }

    @Override
    public void removePhysicalMachine(String physicalMachineAddress) throws MonitorException
    {
        super.removePhysicalMachine(physicalMachineAddress);

        // Set the stop flag, to make the poller thread terminate
        for (XenServerSubscriber subscriber : subscribers)
        {
            if (subscriber.physicalMachineAddress.equals(physicalMachineAddress))
            {
                subscriber.mustUnsubscribe = true;
            }
        }
    }

    @Override
    public void publishState(String physicalMachineAddress, String virtualMachineName)
        throws MonitorException
    {
        super.publishState(physicalMachineAddress, virtualMachineName);

        Types.VmPowerState powerState = null;
        Connection connection = null;

        // Connect to the hypervisor
        PhysicalMachine pm = getPhysicalMachine(physicalMachineAddress);

        try
        {
            connection =
                XenServerConnector.connect(pm.getAddress(), pm.getUsername(), pm.getPassword());

            Iterator<VM> vmsIterator = VM.getByNameLabel(connection, virtualMachineName).iterator();
            if (vmsIterator.hasNext())
            {
                powerState = vmsIterator.next().getPowerState(connection);
            }
            else
            {
                // VM Not found
                powerState = Types.VmPowerState.UNRECOGNIZED;
                LOGGER.error("Virtual machine {} was not found", virtualMachineName);
            }
        }
        catch (Exception ex)
        {
            throw new MonitorException("Could not get the state of the virtual machine: "
                + virtualMachineName + " on " + physicalMachineAddress, ex);
        }
        finally
        {
            if (connection != null)
            {
                XenServerConnector.disconnect(connection);
            }
        }

        this.notify(new VMEvent(XenServerUtils.translateEvent(powerState),
            physicalMachineAddress,
            virtualMachineName));
    }

    /**
     * Performs synchronous polling calls to get the state of the monitored virtual machines.
     * 
     * @author destevez
     */
    private class XenServerSubscriber extends Thread
    {
        /** The address of the machine being monitored. */
        private String physicalMachineAddress;

        /** Boolean indicating if the thread must be stopped. */
        private boolean mustUnsubscribe;

        /** The type of the events to subscribe to. */
        private Set<String> eventClasses;

        /**
         * Creates a new <code>XenServerPoller</code>
         * 
         * @param physicalMachineAddress The address of the machine to monitor.
         */
        public XenServerSubscriber(String physicalMachineAddress)
        {
            super();
            this.physicalMachineAddress = physicalMachineAddress;
            this.mustUnsubscribe = false;

            // Capture only VM events
            this.eventClasses = new HashSet<String>();
            this.eventClasses.add("vm");
        }

        @Override
        public void run()
        {
            Connection connection = connectAndSubscribe();

            if (connection != null)
            {
                // Start listening for events
                LOGGER.trace("Listening to events at: {}", physicalMachineAddress);

                watchEvents(connection);
            }

            // Unregister the event listening
            unsubscribeAndClose(connection);

            // Remove the poller from the list of pollers to allow the garbage
            // collector delete it from memory
            subscribers.remove(this);
        }

        /**
         * Subscribes to events in the physical machine.
         * 
         * @return The connection object to the physical machine.
         */
        private Connection connectAndSubscribe()
        {
            try
            {
                // Get the physical machine details
                PhysicalMachine pm = getPhysicalMachine(physicalMachineAddress);

                // Connect to the target physical machine
                Connection connection =
                    XenServerConnector.connect(physicalMachineAddress, pm.getUsername(), pm
                        .getPassword());

                // Subscribe to the events of the configured type
                Event.register(connection, eventClasses);

                return connection;
            }
            catch (Exception ex)
            {
                LOGGER.error("Could not subscribe to events at: " + physicalMachineAddress, ex);
            }

            return null;
        }

        /**
         * Unsubscribes from events and closes the given connection.
         * 
         * @param connection The connection to free.
         */
        private void unsubscribeAndClose(Connection connection)
        {
            try
            {
                // Unsubscribe from events
                Event.unregister(connection, eventClasses);
            }
            catch (Exception ex)
            {
                LOGGER.error("Could unsubscribe from events at: " + physicalMachineAddress, ex);
            }

            try
            {
                // Logout and close connection
                XenServerConnector.disconnect(connection);
            }
            catch (Exception ex)
            {
                LOGGER.error("Could not disconnect from: " + physicalMachineAddress, ex);
            }
        }

        /**
         * Notify the event only if the thread is not marked to be stopped.
         * 
         * @param vm The virtual machine where the event was fired.
         * @param type The type of the event to notify.
         */
        private void notifyStateChange(VM.Record vm, VMEventType type)
        {
            String msg =
                String.format("VM %s: nameLabel=%s, uuid=%s, state =%s", type.name(), vm.nameLabel,
                    vm.uuid, vm.powerState);

            LOGGER.trace(msg);

            // Notify only if the poller is not marked to be stopped
            if (!mustUnsubscribe)
            {
                VMEvent event = new VMEvent(type, physicalMachineAddress, vm.nameLabel);
                XenServerMonitor.this.notify(event);
            }
        }

        /**
         * Listens for events at the target host and processes them.
         * 
         * @param connection The connection to the target host.
         */
        private void watchEvents(Connection connection)
        {
            // Machines we are watching at this execution to avoid sending repeated events
            Map<String, VmPowerState> vmsChangingState = new HashMap<String, VmPowerState>();
            Map<String, VmOperations> vmsOperationFiltered = new HashMap<String, VmOperations>();

            while (!mustUnsubscribe)
            {
                try
                {
                    Set<Event.Record> events = Event.next(connection);

                    LOGGER.trace("Received Events Package with {} events", events.size());

                    for (Iterator<Event.Record> iterator = events.iterator(); iterator.hasNext();)
                    {
                        Event.Record record = iterator.next();
                        VM.Record vm = (VM.Record) record.snapshot;

                        if (vm.currentOperations.values().size() == 1)
                        {
                            // An Operation was made over this VM
                            VmOperations currentOperation =
                                vm.currentOperations.values().iterator().next();

                            // Avoid sending repeated operation
                            if (!vmsOperationFiltered.containsKey(vm.nameLabel)
                                || !vmsOperationFiltered.get(vm.nameLabel).equals(currentOperation))
                            {
                                // We are ignoring repeated operations on the same VirtualMachine
                                // New operation detected on VM
                                if (currentOperation.equals(VmOperations.PROVISION))
                                {
                                    notifyStateChange(vm, VMEventType.CREATED);
                                }
                                else if (currentOperation.equals(VmOperations.DESTROY))
                                {
                                    notifyStateChange(vm, VMEventType.DESTROYED);
                                }
                            }

                            vmsOperationFiltered.remove(vm.nameLabel);
                            vmsOperationFiltered.put(vm.nameLabel, currentOperation);
                        }

                        // XenServerAPI sends repeated events, we keep a list with VMs current
                        // states and check when a change is made
                        if (vmsChangingState.containsKey(vm.nameLabel))
                        {
                            VmPowerState currentState = vmsChangingState.get(vm.nameLabel);

                            // Checks for new state
                            if (currentState != vm.powerState)
                            {
                                if (vm.powerState != Types.VmPowerState.UNRECOGNIZED)
                                {
                                    switch (currentState)
                                    {
                                        case HALTED:
                                            if (vm.powerState == Types.VmPowerState.RUNNING)
                                            {
                                                notifyStateChange(vm, VMEventType.POWER_ON);
                                            }
                                            break;
                                        case PAUSED:
                                        case SUSPENDED:
                                            if (vm.powerState == Types.VmPowerState.RUNNING)
                                            {
                                                notifyStateChange(vm, VMEventType.RESUMED);
                                            }
                                            break;
                                        case RUNNING:
                                            switch (vm.powerState)
                                            {
                                                case HALTED:
                                                    notifyStateChange(vm, VMEventType.POWER_OFF);
                                                    break;
                                                case PAUSED:
                                                case SUSPENDED:
                                                    notifyStateChange(vm, VMEventType.PAUSED);
                                                    break;
                                                default:
                                                    break;
                                            }
                                            break;

                                        default:
                                            LOGGER.trace(
                                                "Ignoring state change for virtual machine: {}",
                                                vm.nameLabel);
                                            break;
                                    }
                                }
                                else
                                {
                                    LOGGER.trace(
                                        "Unrecognized or unknown state for virtual machine: {}",
                                        vm.nameLabel);
                                }

                                // Remove from watch List
                                vmsChangingState.remove(vm.nameLabel);
                            }

                            // Ignore if state has not changed
                        }
                        else
                        {
                            // New VM to watch for state changes
                            if (vm.powerState != Types.VmPowerState.UNRECOGNIZED)
                            {
                                vmsChangingState.put(vm.nameLabel, vm.powerState);
                            }
                            else
                            {
                                LOGGER.trace(
                                    "State unrecognized or unknown for virtual machine: {}",
                                    vm.nameLabel);
                            }
                        }
                    }
                }
                catch (EventsLost ex)
                {
                    LOGGER.trace("There were lost events for physical machine: {}. "
                        + "This may be due to a slow event consumer. Trying to re-register...",
                        physicalMachineAddress);

                    try
                    {
                        // When an EventsLost exception occurs, client must re-register to
                        // continue receiving events. See official documentation:
                        // http://docs.vmd.citrix.com/XenServer/5.6.0fp1/1.0/en_gb/sdk.html#subscribing_to_and_listening_for_events
                        Event.unregister(connection, eventClasses);
                        Event.register(connection, eventClasses);
                    }
                    catch (Exception e)
                    {
                        LOGGER
                            .error(
                                "Could not re-register to events on physical machine: {}. Shutting down the monitor.",
                                physicalMachineAddress);

                        mustUnsubscribe = true;
                    }
                }
                catch (Exception ex)
                {
                    LOGGER.trace("Could not get the events for physical machine: {}",
                        physicalMachineAddress);
                }
            }
        }
    }
}
