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

package com.abiquo.api.eventing;

import java.util.HashMap;
import java.util.Map;

import javax.persistence.EntityManager;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.abiquo.api.tracer.TracerLogger;
import com.abiquo.commons.amqp.impl.vsm.VSMCallback;
import com.abiquo.commons.amqp.impl.vsm.domain.VirtualSystemEvent;
import com.abiquo.server.core.cloud.VirtualMachine;
import com.abiquo.server.core.cloud.VirtualMachineRep;
import com.abiquo.server.core.cloud.VirtualMachineState;
import com.abiquo.tracer.ComponentType;
import com.abiquo.tracer.EventType;
import com.abiquo.tracer.SeverityType;
import com.abiquo.vsm.events.VMEventType;

/**
 * This listener {@link VSMCallback} receives the events from the all Virtual System Monitors in
 * each datacenter and updates the state of virtual machines and virtual appliances in database.
 * 
 * @author eruiz@abiquo.com
 */
@Service
public class VSMEventProcessor implements VSMCallback
{
    private final static Logger LOGGER = LoggerFactory.getLogger(VSMEventProcessor.class);

    @Autowired
    protected VirtualMachineRep vmRepo;

    @Autowired
    protected TracerLogger tracer;

    /** Event to virtual machine state translations */
    protected final Map<VMEventType, VirtualMachineState> stateByEvent =
        new HashMap<VMEventType, VirtualMachineState>()
        {
            {
                put(VMEventType.POWER_OFF, VirtualMachineState.OFF);
                put(VMEventType.POWER_ON, VirtualMachineState.ON);
                put(VMEventType.PAUSED, VirtualMachineState.PAUSED);
                put(VMEventType.RESUMED, VirtualMachineState.ON);
                put(VMEventType.DESTROYED, VirtualMachineState.NOT_ALLOCATED);
                put(VMEventType.MOVED, VirtualMachineState.LOCKED); // TODO Move to enterprise
            }
        };

    /** Event to trace-event virtual machine translations */
    protected final Map<VMEventType, EventType> traceEventByEvent =
        new HashMap<VMEventType, EventType>()
        {
            {
                put(VMEventType.POWER_OFF, EventType.VM_POWEROFF);
                put(VMEventType.POWER_ON, EventType.VM_POWERON);
                put(VMEventType.PAUSED, EventType.VM_PAUSED);
                put(VMEventType.RESUMED, EventType.VM_RESUMED);
                put(VMEventType.DESTROYED, EventType.VM_DESTROY);
                put(VMEventType.MOVED, EventType.VM_MOVED);
            }
        };

    /**
     * Constructor for test purposes only.
     * 
     * @param em The entity manager to use.
     */
    public VSMEventProcessor(EntityManager em)
    {
        this.vmRepo = new VirtualMachineRep(em);
        this.tracer = new TracerLogger();
    }

    public VSMEventProcessor()
    {
    }

    @Override
    @Transactional(readOnly = false, propagation = Propagation.REQUIRED)
    public void onEvent(final VirtualSystemEvent notification)
    {
        LOGGER.debug("New notification received, {}.", notification.toString());

        // Check if notification contains a valid event
        VMEventType event = eventFromString(notification.getEventType());

        if (stateByEvent.get(event) == null)
        {
            LOGGER.error("Unknown event {}. Just ignoring it.", notification.getEventType());
            return;
        }

        // Update virtual machine state
        VirtualMachine machine = vmRepo.findByName(notification.getVirtualSystemId());

        if (machine != null)
        {
            vmRepo.update(updateMachineState(machine, notification));
        }
    }

    /**
     * Process the given notification and if it affects to the virtual machine, updates the state of
     * a virtual machine instance.
     * 
     * @param machine The instance to update.
     * @param notification The notification.
     * @return The virtual machine instance.
     */
    protected VirtualMachine updateMachineState(VirtualMachine machine,
        final VirtualSystemEvent notification)
    {
        return processEvent(machine, eventFromString(notification.getEventType()), notification);
    }

    /**
     * Process the given notification and if it affects to the virtual machine, updates the state of
     * a virtual machine instance.
     * 
     * @param machine The instance to update.
     * @param event The event notified.
     * @param notification The complete notification.
     * @return The virtual machine instance.
     */
    protected VirtualMachine processEvent(VirtualMachine machine, VMEventType event,
        VirtualSystemEvent notification)
    {
        switch (event)
        {
            case PAUSED:
            case POWER_OFF:
            case POWER_ON:
            case RESUMED:
            case SAVED:
            case DESTROYED:
                onVMDestroyedEvent(machine, event, notification);
                break;

            default:
                LOGGER.warn("Ignoring {} event.", event);
                break;
        }

        return machine;
    }

    protected void logAndTraceVirtualMachineStateUpdated(final VirtualMachine machine,
        final VMEventType event, final VirtualSystemEvent notification)
    {
        String message =
            String.format("Processed %s event in machine %s, the current machine state is %s.",
                event.name(), machine.getName(), machine.getState().name());

        traceVirtualMachineStateUpdated(notification, message);
        LOGGER.debug(message);
    }

    /**
     * Publish an INFO system log to tracer system with VIRTUAL_MACHINE component type.
     * 
     * @param notification The received notification.
     * @param message The message to publish
     */
    protected void traceVirtualMachineStateUpdated(final VirtualSystemEvent notification,
        final String message)
    {
        VMEventType event = eventFromString(notification.getEventType());

        if (traceEventByEvent.containsKey(event))
        {
            tracer.systemLog(SeverityType.INFO, ComponentType.VIRTUAL_MACHINE,
                traceEventByEvent.get(event), message);
        }
    }

    protected VMEventType eventFromString(final String name)
    {
        try
        {
            return VMEventType.valueOf(name);
        }
        catch (IllegalArgumentException e)
        {
            return null;
        }
    }

    /**
     * Fires on Virtual Machine Destroyed event detection. - Sets VM state to NOT_ALLOCATED -
     * Resources ARE freed
     * 
     * @param vm
     */
    protected void onVMDestroyedEvent(VirtualMachine vMachine, final VMEventType event,
        final VirtualSystemEvent notification)
    {
        vMachine.setState(stateByEvent.get(event));
        logAndTraceVirtualMachineStateUpdated(vMachine, event, notification);

        // TODO: Resources are freed?
        // onDeleteNode(session, nodeVi.toPojoHB()
    }
}
