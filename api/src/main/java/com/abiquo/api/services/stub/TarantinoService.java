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

package com.abiquo.api.services.stub;

import java.io.IOException;

import javax.persistence.EntityManager;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.abiquo.api.exceptions.APIError;
import com.abiquo.api.services.DefaultApiService;
import com.abiquo.api.services.RemoteServiceService;
import com.abiquo.commons.amqp.impl.tarantino.TarantinoRequestProducer;
import com.abiquo.commons.amqp.impl.tarantino.domain.builder.VirtualMachineDescriptionBuilder;
import com.abiquo.commons.amqp.impl.tarantino.domain.dto.DatacenterTasks;
import com.abiquo.model.enumerator.HypervisorType;
import com.abiquo.model.enumerator.RemoteServiceType;
import com.abiquo.server.core.cloud.VirtualMachine;
import com.abiquo.server.core.infrastructure.Datacenter;
import com.abiquo.server.core.infrastructure.RemoteService;
import com.abiquo.tracer.ComponentType;
import com.abiquo.tracer.EventType;
import com.abiquo.tracer.SeverityType;

/**
 * Utility methods to send jobs to Tarantino.
 * 
 * @author Ignasi Barrera
 */
@Service
public class TarantinoService extends DefaultApiService
{
    @Autowired
    private RemoteServiceService remoteServiceService;

    @Autowired
    private VsmServiceStub vsm;

    @Autowired
    private TarantinoJobCreator jobCreator;

    public TarantinoService()
    {

    }

    public TarantinoService(final EntityManager em)
    {
        remoteServiceService = new RemoteServiceService(em);
        jobCreator = new TarantinoJobCreator(em);
        vsm = new VsmServiceStub();
    }

    /**
     * Creates and sends a reconfigure operation.
     * 
     * @param vm The virtual machine to reconfigure.
     * @param originalConfig The original configuration for the virtual machine.
     * @param newConfig The new configuration for the virtual machine.
     * @return The identifier of the reconfigure task.
     */
    @Transactional(readOnly = true, propagation = Propagation.REQUIRED)
    public String reconfigureVirtualMachine(final VirtualMachine vm,
        final VirtualMachineDescriptionBuilder originalConfig,
        final VirtualMachineDescriptionBuilder newConfig)
    {
        Datacenter datacenter = vm.getHypervisor().getMachine().getDatacenter();
        ignoreVSMEventsIfNecessary(datacenter, vm);

        DatacenterTasks reconfigureTask = jobCreator.reconfigureTask(vm, originalConfig, newConfig);
        send(datacenter, reconfigureTask, EventType.VM_RECONFIGURE);

        return reconfigureTask.getId();
    }

    /**
     * Send the given datacenter tasks.
     * 
     * @param datacenter The datacenter where the tasks will be sent to.
     * @param tasks The tasks to send.
     * @param event The event associated to the task (power on, reconfigure, etc).
     */
    private void send(final Datacenter datacenter, final DatacenterTasks tasks,
        final EventType event)
    {
        // FIXME use the datacenter UUID
        TarantinoRequestProducer producer = new TarantinoRequestProducer(datacenter.getName());

        try
        {
            producer.openChannel();
            producer.publish(tasks);
        }
        catch (Exception ex)
        {
            tracer.log(SeverityType.CRITICAL, ComponentType.VIRTUAL_MACHINE, event,
                APIError.GENERIC_OPERATION_ERROR.getMessage());

            tracer.systemError(SeverityType.CRITICAL, ComponentType.VIRTUAL_MACHINE, event,
                "Failed to enqueue task in Tarantino. Rabbitmq might be "
                    + "down or not configured. The error message was " + ex.getMessage(), ex);

            addNotFoundErrors(APIError.GENERIC_OPERATION_ERROR);
            flushErrors();
        }
        finally
        {
            closeProducerChannel(producer, event);
        }

        tracer.log(SeverityType.INFO, ComponentType.VIRTUAL_MACHINE, event,
            "Task enqueued successfully to Tarantinio");
    }

    /**
     * Close the producer channel.
     * 
     * @param producer The channel to close.
     * @param event The event being processed (power on, reconfigure, etc).
     */
    private void closeProducerChannel(final TarantinoRequestProducer producer, final EventType event)
    {
        try
        {
            producer.closeChannel();
        }
        catch (IOException ex)
        {
            tracer.log(SeverityType.CRITICAL, ComponentType.VIRTUAL_MACHINE, event,
                APIError.GENERIC_OPERATION_ERROR.getMessage());

            tracer.systemError(SeverityType.CRITICAL, ComponentType.VIRTUAL_MACHINE, event,
                "Error closing the producer channel with error: " + ex.getMessage(), ex);

        }
    }

    /**
     * Unsubscribe from the VSM if a reconfigure task is sent to a XEN or KVM.
     * <p>
     * Since reconfigure tasks in those hypervisors may undefine the domain and redefine it again,
     * the unsubscription is performed to ignore all DESTROY and CREATE events that may arrive
     * because of that process.
     * 
     * @param datacenter The datacenter where the tasks are performed.
     * @param vm The virtual machine to unsubscribe.
     */
    private void ignoreVSMEventsIfNecessary(final Datacenter datacenter, final VirtualMachine vm)
    {
        HypervisorType type = vm.getHypervisor().getType();

        if (type == HypervisorType.XEN_3 || type == HypervisorType.KVM)
        {
            RemoteService vsmRS =
                remoteServiceService.getRemoteService(datacenter.getId(),
                    RemoteServiceType.VIRTUAL_SYSTEM_MONITOR);

            vsm.unsubscribe(vsmRS, vm);
        }
    }
}
