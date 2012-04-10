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
import java.util.HashMap;
import java.util.Map;

import javax.persistence.EntityManager;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.abiquo.api.exceptions.APIError;
import com.abiquo.api.exceptions.APIException;
import com.abiquo.api.exceptions.NotFoundException;
import com.abiquo.api.services.DefaultApiService;
import com.abiquo.api.services.InfrastructureService;
import com.abiquo.api.services.RemoteServiceService;
import com.abiquo.api.services.TaskService;
import com.abiquo.api.services.UserService;
import com.abiquo.api.tasks.util.DatacenterTaskBuilder;
import com.abiquo.api.tracer.TracerLogger;
import com.abiquo.api.util.snapshot.SnapshotUtils;
import com.abiquo.api.util.snapshot.SnapshotUtils.SnapshotType;
import com.abiquo.commons.amqp.impl.tarantino.TarantinoRequestProducer;
import com.abiquo.commons.amqp.impl.tarantino.domain.DiskSnapshot;
import com.abiquo.commons.amqp.impl.tarantino.domain.HypervisorConnection;
import com.abiquo.commons.amqp.impl.tarantino.domain.VirtualMachineDefinition;
import com.abiquo.commons.amqp.impl.tarantino.domain.builder.VirtualMachineDescriptionBuilder;
import com.abiquo.commons.amqp.impl.tarantino.domain.dto.DatacenterTasks;
import com.abiquo.model.enumerator.RemoteServiceType;
import com.abiquo.server.core.appslibrary.VirtualMachineTemplate;
import com.abiquo.server.core.cloud.Hypervisor;
import com.abiquo.server.core.cloud.VirtualAppliance;
import com.abiquo.server.core.cloud.VirtualMachine;
import com.abiquo.server.core.cloud.VirtualMachineState;
import com.abiquo.server.core.cloud.VirtualMachineStateTransition;
import com.abiquo.server.core.infrastructure.Datacenter;
import com.abiquo.server.core.infrastructure.RemoteService;
import com.abiquo.server.core.task.Task;
import com.abiquo.server.core.task.enums.TaskType;
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

    /** The logger object **/
    private final static Logger logger = LoggerFactory.getLogger(TarantinoService.class);

    @Autowired
    protected RemoteServiceService remoteServiceService;

    @Autowired
    protected VsmServiceStub vsm;

    @Autowired
    protected TarantinoJobCreator jobCreator;

    @Autowired
    protected UserService userService;

    @Autowired
    protected TaskService taskService;

    @Autowired
    protected InfrastructureService infrastructureService;

    public TarantinoService()
    {

    }

    public TarantinoService(final EntityManager em)
    {
        this.tracer = new TracerLogger(); // TODO super(em)

        remoteServiceService = new RemoteServiceService(em);
        jobCreator = new TarantinoJobCreator(em);
        vsm = new VsmServiceStub();
        taskService = new TaskService();
    }

    /**
     * Persist the {@link Task} for progress tracking and send the given {@link DatacenterTasks} to
     * tarantino. Steps:
     * <ol>
     * <li>Persist the {@link Task} to redis</li>
     * <li>Send {@link DatacenterTasks} to Tarantino using AMQP</li>
     * <li>If can not connect to AMQP broker, the {@link Task} is deleted from redis</li>
     * </ol>
     * 
     * @param datacenter The {@link Datacenter} where the tasks will be sent to.
     * @param task The {@link Task} to persist.
     * @param tasks The {@link DatacenterTasks} to send.
     * @param eventType The {@link EventType} associated to the task (power on, reconfigure, etc).
     */
    protected void enqueueTask(final Datacenter datacenter, final Task task,
        final DatacenterTasks tasks, final EventType eventType)
    {
        try
        {
            taskService.addTask(task);
        }
        catch (RuntimeException e)
        {
            tracer.log(SeverityType.CRITICAL, ComponentType.VIRTUAL_MACHINE, eventType,
                "redis.error.user");

            tracer.systemError(SeverityType.CRITICAL, ComponentType.VIRTUAL_MACHINE, eventType, e,
                "redis.persistTaskError", e.getMessage());

            addServiceUnavailableErrors(APIError.REDIS_CONNECTION_FAILED);
            flushErrors();
        }

        TarantinoRequestProducer producer = getTarantinoProducer(datacenter);

        try
        {
            producer.openChannel();
            producer.publish(tasks);
        }
        catch (Exception e)
        {
            tracer.log(SeverityType.CRITICAL, ComponentType.VIRTUAL_MACHINE, eventType,
                APIError.RABBITMQ_CONNECTION_FAILED.getMessage());

            tracer.systemError(SeverityType.CRITICAL, ComponentType.VIRTUAL_MACHINE, eventType, e,
                "tarantino.sendError", e.getMessage());

            try
            {
                // Delete redis stored task
                taskService.deleteTask(task);
            }
            catch (RuntimeException r)
            {
                tracer.log(SeverityType.CRITICAL, ComponentType.VIRTUAL_MACHINE, eventType,
                    "redis.error.user");

                tracer.systemError(SeverityType.CRITICAL, ComponentType.VIRTUAL_MACHINE, eventType,
                    r, "redis.deleteTaskError", r.getMessage());
            }

            addServiceUnavailableErrors(APIError.RABBITMQ_CONNECTION_FAILED);
            flushErrors();
        }
        finally
        {
            closeProducerChannel(producer, eventType);
        }

        tracer.log(SeverityType.INFO, ComponentType.VIRTUAL_MACHINE, eventType,
            "tarantino.taskEnqueued");
    }

    /**
     * Unsubscribe the {@link VirtualMachine} from VSM and call to
     * {@link TarantinoService#enqueueTask(Datacenter, Task, DatacenterTasks, EventType)}.
     * 
     * @param virtualMachine The {@link VirtualMachine} to instance.
     * @param redisTask {@link Task} to persist
     * @param tarantinoTask {@link DatacenterTasks} to send
     * @return The {@link Task} UUID for progress tracking
     */
    protected String unsubscribeVirtualMachineAndEnqueueTask(final VirtualMachine virtualMachine,
        final Task redisTask, final DatacenterTasks tarantinoTask, final EventType eventType)
    {
        // Unsubscribe the virtual machine to prevent unlock
        Datacenter datacenter = virtualMachine.getHypervisor().getMachine().getDatacenter();
        RemoteService service = remoteServiceService.getVSMRemoteService(datacenter);
        if (vsm.isVirtualMachineSubscribed(service, virtualMachine.getName()))
        {
            logger.debug("Unsubscribing virtual machine {} from VSM", virtualMachine.getName());
            vsm.unsubscribe(service, virtualMachine);
            logger.debug("Virtual machine {} unsubscribed from VSM", virtualMachine.getName());
        }
        else
        {
            // The machine must be subscribed
            logger
                .error(
                    "Unsubscribing virtual machine {} from VSM: Error: the virtual machine was not subscribed. Was the Subscription deleted manually outside Abiquo?",
                    virtualMachine.getName());
        }

        try
        {
            // Add Redis task for progress tracking and send the tarantino task
            logger.debug("Enqueuing task for virtual machine {}", virtualMachine.getName());
            enqueueTask(datacenter, redisTask, tarantinoTask, eventType);
            logger.debug("Task for virtual machine {} enqueued", virtualMachine.getName());
        }
        catch (APIException e)
        {
            // Restore the virtual machine subscription on error
            logger.debug("Subscribing virtual machine {} to VSM due an send error.",
                virtualMachine.getName());
            vsm.subscribe(service, virtualMachine);
            logger.debug("Virtual machine {} subscribed to VSM", virtualMachine.getName());
            throw e;
        }

        return tarantinoTask.getId();
    }

    private TarantinoRequestProducer getTarantinoProducer(final Datacenter datacenter)
    {
        final String datacenterQueueId = datacenter.getUuid();
        if (StringUtils.isEmpty(datacenterQueueId))
        {
            tracer.systemError(SeverityType.CRITICAL, ComponentType.VIRTUAL_MACHINE,
                EventType.VM_UNKNOWN, null, "tarantino.sendError",
                APIError.DATACENTER_QUEUE_NOT_CONFIGURED.getMessage());

            addNotFoundErrors(APIError.DATACENTER_QUEUE_NOT_CONFIGURED);
            flushErrors();
        }

        return new TarantinoRequestProducer(datacenterQueueId);
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

            tracer.systemError(SeverityType.CRITICAL, ComponentType.VIRTUAL_MACHINE, event, ex,
                "tarantino.closeProducer", ex.getMessage());
        }
    }

    /**
     * Creates and sends a deploy operation.
     * 
     * @param virtualMachine The virtual machine to reconfigure.
     * @param originalConfig The original configuration for the virtual machine.
     * @param newConfig The new configuration for the virtual machine.
     * @return The identifier of the reconfigure task.
     */
    @Transactional(readOnly = true, propagation = Propagation.REQUIRED)
    public String deployVirtualMachine(final VirtualMachine virtualMachine,
        final VirtualMachineDescriptionBuilder virtualMachineDesciptionBuilder)
    {
        Datacenter datacenter = virtualMachine.getHypervisor().getMachine().getDatacenter();
        try
        {

            HypervisorConnection conn =
                jobCreator.hypervisorConnectionConfiguration(virtualMachine.getHypervisor());
            DatacenterTaskBuilder builder =
                new DatacenterTaskBuilder(virtualMachineDesciptionBuilder.build(),
                    conn,
                    userService.getCurrentUser().getNick());

            DatacenterTasks deployTask =
                builder.add(VirtualMachineStateTransition.CONFIGURE)
                    .add(VirtualMachineStateTransition.POWERON).buildTarantinoTask();

            enqueueTask(datacenter,
                builder.buildAsyncTask(String.valueOf(virtualMachine.getId()), TaskType.DEPLOY),
                deployTask, EventType.VM_DEPLOY);

            return deployTask.getId();
        }
        catch (RuntimeException e)
        {
            logger.error("Error enqueuing the deploy task dto to the virtual factory with error ",
                e);

            tracer.log(SeverityType.CRITICAL, ComponentType.VIRTUAL_MACHINE, EventType.VM_DEPLOY,
                APIError.GENERIC_OPERATION_ERROR.getMessage());

            // For the Admin to know all errors
            tracer.systemLog(SeverityType.CRITICAL, ComponentType.VIRTUAL_MACHINE,
                EventType.VM_DEPLOY, "tarantino.deployVMError", e.getMessage());

            // There is no point in continue
            addUnexpectedErrors(APIError.GENERIC_OPERATION_ERROR);
            flushErrors();
        }
        return null;
    }

    /**
     * Creates and sends a reconfigure operation. Unsubscribe el VirtualMachine
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

        RemoteService vsmRS =
            remoteServiceService.getRemoteService(datacenter.getId(),
                RemoteServiceType.VIRTUAL_SYSTEM_MONITOR);

        if (vsm.isVirtualMachineSubscribed(vsmRS, vm.getName()))
        {
            logger.debug("Unsubscribing virtual machine {} from VSM", vm.getName());
            vsm.unsubscribe(vsmRS, vm);
            logger.debug("Virtual machine {} unsubscribed from VSM", vm.getName());
        }
        else
        {
            // The machine must be subscribed
            logger
                .error(
                    "Unsubscribing virtual machine {} from VSM: Error: the virtual machine was not subscribed. Was the Subscription deleted manually outside Abiquo?",
                    vm.getName());
        }

        try
        {
            HypervisorConnection conn =
                jobCreator.hypervisorConnectionConfiguration(vm.getHypervisor());
            DatacenterTaskBuilder builder =
                new DatacenterTaskBuilder(originalConfig.build(), conn, userService
                    .getCurrentUser().getNick());

            DatacenterTasks reconfigTask =
                builder.addReconfigure(newConfig.build()).buildTarantinoTask();

            enqueueTask(datacenter,
                builder.buildAsyncTask(String.valueOf(vm.getId()), TaskType.RECONFIGURE),
                builder.buildTarantinoTask(), EventType.VM_RECONFIGURE);

            return reconfigTask.getId();

        }
        catch (NotFoundException e)
        {
            logger
                .debug("Error enqueuing the reconfiguretask dto to the virtual factory with error: "
                    + e.getMessage() + " unmonitoring the machine: " + vm.getName());

            vsm.subscribe(vsmRS, vm);
            throw e;
        }
        catch (RuntimeException e)
        {
            logger
                .error("Error enqueuing the reconfigure task dto to the virtual factory with error: "
                    + e.getMessage());

            tracer.log(SeverityType.CRITICAL, ComponentType.VIRTUAL_MACHINE,
                EventType.VM_RECONFIGURE, APIError.GENERIC_OPERATION_ERROR.getMessage());

            // For the Admin to know all errors
            tracer.systemLog(SeverityType.CRITICAL, ComponentType.VIRTUAL_MACHINE,
                EventType.VM_RECONFIGURE, "tarantino.reconfigureVMError", e.getMessage());

            // We need to re subscribe to the machine
            vsm.subscribe(vsmRS, vm);

            // There is no point in continue
            addUnexpectedErrors(APIError.GENERIC_OPERATION_ERROR);
            flushErrors();
        }
        return null;
    }

    /**
     * Sends a Deploy operation originated by HA move
     * 
     * @param virtualMachine
     * @param virtualMachineDesciptionBuilder
     * @param originalVMState
     * @return
     */
    @Transactional(readOnly = true, propagation = Propagation.REQUIRED)
    public String deployVirtualMachineHA(final VirtualMachine virtualMachine,
        final VirtualMachineDescriptionBuilder virtualMachineDesciptionBuilder,
        final boolean originalVMStateON, final Map<String, String> extraData)
    {
        Datacenter datacenter = virtualMachine.getHypervisor().getMachine().getDatacenter();

        try
        {
            HypervisorConnection conn =
                jobCreator.hypervisorConnectionConfiguration(virtualMachine.getHypervisor());
            DatacenterTaskBuilder builder =
                new DatacenterTaskBuilder(virtualMachineDesciptionBuilder.build(), conn, "admin");
            // XXX: This should be system user

            DatacenterTasks deployTask = null;

            if (originalVMStateON)
            {
                deployTask =
                    builder.add(VirtualMachineStateTransition.CONFIGURE, extraData)
                        .add(VirtualMachineStateTransition.POWERON).buildTarantinoTask();
            }
            else
            {
                deployTask =
                    builder.add(VirtualMachineStateTransition.CONFIGURE, extraData)
                        .buildTarantinoTask();
            }

            enqueueTask(datacenter,
                builder.buildAsyncTask(String.valueOf(virtualMachine.getId()), TaskType.HA_DEPLOY),
                deployTask, EventType.VM_MOVING_BY_HA);

            return deployTask.getId();
        }
        catch (NotFoundException e)
        {
            // No need to unsuscribe the machine
            // logger.debug("Error enqueuing the HA deploy task dto to Tarantino with error: "
            // + e.getMessage() + " unmonitoring the machine: " + virtualMachine.getName());
            // vsm.unsubscribe(remoteService, virtualMachine);

            throw e;
        }
        catch (RuntimeException e)
        {
            logger
                .error("Error enqueuing the HA deploy task dto to the virtual factory with error: "
                    + e.getMessage());

            tracer.log(SeverityType.CRITICAL, ComponentType.VIRTUAL_MACHINE,
                EventType.VM_MOVING_BY_HA, APIError.GENERIC_OPERATION_ERROR.getMessage());

            // For the Admin to know all errors
            tracer.systemLog(SeverityType.CRITICAL, ComponentType.VIRTUAL_MACHINE,
                EventType.VM_MOVING_BY_HA, "tarantino.deployVMError", e.getMessage());

            // No need to unsuscribe the machine
            // logger.debug("Error enqueuing the deploy task dto to Tarantino with error: "
            // + e.getMessage() + " unmonitoring the machine: " + virtualMachine.getName());
            // vsm.unsubscribe(remoteService, virtualMachine);

            // There is no point in continue
            addUnexpectedErrors(APIError.GENERIC_OPERATION_ERROR);
            flushErrors();
        }
        return null;
    }

    /**
     * Undeploys VM after a Re-enable HA operation
     * 
     * @param virtualMachine
     * @param virtualMachineDesciptionBuilder
     * @param currentState
     * @param sourceHypervisor VM is undeployed from this hypervisor, not the one we have in DB
     * @return
     */
    @Transactional(readOnly = true, propagation = Propagation.REQUIRED)
    public String undeployVirtualMachineHA(final VirtualMachine virtualMachine,
        final VirtualMachineDescriptionBuilder virtualMachineDesciptionBuilder,
        final VirtualMachineState currentState, final Hypervisor originalHypervisor)
    {

        Map<String, String> extraData = new HashMap<String, String>();
        extraData.put("isHA", Boolean.TRUE.toString());
        try
        {
            // VM is undeployed from this hypervisor, not the one we have in DB
            HypervisorConnection conn =
                jobCreator.hypervisorConnectionConfiguration(originalHypervisor);
            DatacenterTaskBuilder builder =
                new DatacenterTaskBuilder(virtualMachineDesciptionBuilder.build(), conn,
                /* userService.getCurrentUser().getNick() */"admin");
            // XXX: This should be system user

            if (VirtualMachineState.ON.equals(currentState))
            {
                builder.add(VirtualMachineStateTransition.POWEROFF);
            }

            DatacenterTasks tarantinoTask =
                builder.add(VirtualMachineStateTransition.DECONFIGURE, extraData)
                    .buildTarantinoTask();

            Task redisTask =
                builder
                    .buildAsyncTask(String.valueOf(virtualMachine.getId()), TaskType.HA_UNDEPLOY);

            Datacenter datacenter = virtualMachine.getHypervisor().getMachine().getDatacenter();

            // Add Redis task for progress tracking and send the tarantino task
            logger.debug("Enqueuing task for virtual machine {}", virtualMachine.getName());
            enqueueTask(datacenter, redisTask, tarantinoTask, EventType.VM_UNDEPLOY);
            logger.debug("Task for virtual machine {} enqueued", virtualMachine.getName());

            return tarantinoTask.getId();
        }
        catch (NotFoundException e)
        {
            throw e;
        }
        catch (RuntimeException e)
        {
            logger
                .error("Error enqueuing the undeploy task dto to the virtual factory with error: "
                    + e.getMessage());

            tracer.log(SeverityType.CRITICAL, ComponentType.VIRTUAL_MACHINE, EventType.VM_UNDEPLOY,
                APIError.GENERIC_OPERATION_ERROR.getMessage());

            // For the Admin to know all errors
            tracer.systemLog(SeverityType.CRITICAL, ComponentType.VIRTUAL_MACHINE,
                EventType.VM_UNDEPLOY, "tarantino.undeployVMError", e.getMessage());

            // There is no point in continue
            addUnexpectedErrors(APIError.GENERIC_OPERATION_ERROR);
            flushErrors();
        }
        return null;

    }

    /**
     * Creates and sends a deploy operation.
     * 
     * @param virtualMachine The virtual machine to reconfigure.
     * @param originalConfig The original configuration for the virtual machine.
     * @param newConfig The new configuration for the virtual machine.
     * @return The identifier of the reconfigure task.
     */
    @Transactional(readOnly = true, propagation = Propagation.REQUIRED)
    public String undeployVirtualMachine(final VirtualMachine virtualMachine,
        final VirtualMachineDescriptionBuilder virtualMachineDesciptionBuilder,
        final VirtualMachineState currentState)
    {

        try
        {
            HypervisorConnection conn =
                jobCreator.hypervisorConnectionConfiguration(virtualMachine.getHypervisor());
            DatacenterTaskBuilder builder =
                new DatacenterTaskBuilder(virtualMachineDesciptionBuilder.build(),
                    conn,
                    userService.getCurrentUser().getNick());

            if (mustPowerOffToUndeploy(currentState))
            {
                builder.add(VirtualMachineStateTransition.POWEROFF);
            }

            DatacenterTasks tarantinoTask =
                builder.add(VirtualMachineStateTransition.DECONFIGURE).buildTarantinoTask();

            Task redisTask =
                builder.buildAsyncTask(String.valueOf(virtualMachine.getId()), TaskType.UNDEPLOY);

            unsubscribeVirtualMachineAndEnqueueTask(virtualMachine, redisTask, tarantinoTask,
                EventType.VM_UNDEPLOY);

            return tarantinoTask.getId();
        }
        catch (NotFoundException e)
        {
            throw e;
        }
        catch (RuntimeException e)
        {
            logger
                .error("Error enqueuing the undeploy task dto to the virtual factory with error: "
                    + e.getMessage());

            tracer.log(SeverityType.CRITICAL, ComponentType.VIRTUAL_MACHINE, EventType.VM_UNDEPLOY,
                APIError.GENERIC_OPERATION_ERROR.getMessage());

            // For the Admin to know all errors
            tracer.systemLog(SeverityType.CRITICAL, ComponentType.VIRTUAL_MACHINE,
                EventType.VM_UNDEPLOY, "tarantino.undeployVMError", e.getMessage());

            // There is no point in continue
            addUnexpectedErrors(APIError.GENERIC_OPERATION_ERROR);
            flushErrors();
        }
        return null;
    }

    /**
     * Creates and sends a deploy operation. <br>
     * Also adds the flag for deletion of the {@link VirtualMachine}.
     * 
     * @param virtualMachine The virtual machine to reconfigure.
     * @param originalConfig The original configuration for the virtual machine.
     * @param newConfig The new configuration for the virtual machine.
     * @return The identifier of the reconfigure task.
     */
    @Transactional(readOnly = true, propagation = Propagation.REQUIRED)
    public String undeployVirtualMachineAndDelete(final VirtualMachine virtualMachine,
        final VirtualMachineDescriptionBuilder virtualMachineDesciptionBuilder,
        final VirtualMachineState currentState)
    {
        Datacenter datacenter = virtualMachine.getHypervisor().getMachine().getDatacenter();

        try
        {
            HypervisorConnection conn =
                jobCreator.hypervisorConnectionConfiguration(virtualMachine.getHypervisor());
            DatacenterTaskBuilder builder =
                new DatacenterTaskBuilder(virtualMachineDesciptionBuilder.build(),
                    conn,
                    userService.getCurrentUser().getNick());

            if (mustPowerOffToUndeploy(currentState))
            {
                builder.add(VirtualMachineStateTransition.POWEROFF);
            }
            // This method must delete the virtual machine in the handler

            Map<String, String> extraData = new HashMap<String, String>();
            extraData.put("delete", Boolean.TRUE.toString());

            DatacenterTasks tarantinoTask =
                builder.add(VirtualMachineStateTransition.DECONFIGURE, extraData)
                    .buildTarantinoTask();

            Task redisTask =
                builder.buildAsyncTask(String.valueOf(virtualMachine.getId()), TaskType.UNDEPLOY);

            unsubscribeVirtualMachineAndEnqueueTask(virtualMachine, redisTask, tarantinoTask,
                EventType.VM_UNDEPLOY);

            return tarantinoTask.getId();

        }
        catch (NotFoundException e)
        {
            throw e;
        }
        catch (RuntimeException e)
        {
            logger
                .error("Error enqueuing the undeploy task dto to the virtual factory with error: "
                    + e.getMessage());

            tracer.log(SeverityType.CRITICAL, ComponentType.VIRTUAL_MACHINE, EventType.VM_UNDEPLOY,
                APIError.GENERIC_OPERATION_ERROR.getMessage());

            // For the Admin to know all errors
            tracer.systemLog(SeverityType.CRITICAL, ComponentType.VIRTUAL_MACHINE,
                EventType.VM_UNDEPLOY, "tarantino.undeployVMError", e.getMessage());

            // There is no point in continue
            addUnexpectedErrors(APIError.GENERIC_OPERATION_ERROR);
            flushErrors();
        }
        return null;
    }

    protected boolean mustPowerOffToUndeploy(final VirtualMachineState currentState)
    {
        return VirtualMachineState.ON.equals(currentState)
            || VirtualMachineState.PAUSED.equals(currentState);
    }

    /**
     * Creates and sends a deploy operation.
     * 
     * @param virtualMachine The virtual machine to reconfigure.
     * @param originalConfig The original configuration for the virtual machine.
     * @param newConfig The new configuration for the virtual machine.
     * @return The identifier of the reconfigure task.
     */
    @Transactional(readOnly = true, propagation = Propagation.REQUIRED)
    public String applyVirtualMachineState(final VirtualMachine virtualMachine,
        final VirtualMachineDescriptionBuilder virtualMachineDesciptionBuilder,
        final VirtualMachineStateTransition machineStateTransition)
    {
        Datacenter datacenter = virtualMachine.getHypervisor().getMachine().getDatacenter();
        // ignoreVSMEventsIfNecessary(datacenter, virtualMachine);

        // Some hypervisors may keep the virtual machine in ON state during the reset operation.
        // This means that the VSM will not notify any state change event. To prevent this, we
        // invalidate the last known state for the virtual machine in the VSM to force it to notify
        // the next state change event.
        if (machineStateTransition == VirtualMachineStateTransition.RESET)
        {
            invalidateLastKnownState(virtualMachine);
        }

        try
        {
            HypervisorConnection conn =
                jobCreator.hypervisorConnectionConfiguration(virtualMachine.getHypervisor());
            DatacenterTaskBuilder builder =
                new DatacenterTaskBuilder(virtualMachineDesciptionBuilder.build(),
                    conn,
                    userService.getCurrentUser().getNick());

            DatacenterTasks deployTask = builder.add(machineStateTransition).buildTarantinoTask();

            enqueueTask(datacenter, builder.buildAsyncTask(String.valueOf(virtualMachine.getId()),
                getTaskTypeFromTransition(machineStateTransition)), deployTask, EventType.VM_STATE);

            return deployTask.getId();
        }
        catch (NotFoundException e)
        {
            // We need to unsuscribe the machine
            logger
                .debug("Error enqueuing the state change task dto to the virtual factory with error: "
                    + e.getMessage() + " machine: " + virtualMachine.getName());

            // If we invalidated the last known state and something fails, ask VSM for the state of
            // the virtual machine to recover it.
            if (machineStateTransition == VirtualMachineStateTransition.RESET)
            {
                refreshState(virtualMachine);
            }

            throw e;
        }
        catch (RuntimeException e)
        {
            logger
                .error("Error enqueuing the state change task dto to the virtual factory with error: "
                    + e.getMessage());

            tracer.log(SeverityType.CRITICAL, ComponentType.VIRTUAL_MACHINE, EventType.VM_STATE,
                APIError.GENERIC_OPERATION_ERROR.getMessage());

            // For the Admin to know all errors
            tracer.systemLog(SeverityType.CRITICAL, ComponentType.VIRTUAL_MACHINE,
                EventType.VM_DEPLOY, "tarantino.applyChangesVMError", e.getMessage());

            // If we invalidated the last known state and something fails, ask VSM for the state of
            // the virtual machine to recover it.
            if (machineStateTransition == VirtualMachineStateTransition.RESET)
            {
                refreshState(virtualMachine);
            }

            // There is no point in continue
            addUnexpectedErrors(APIError.GENERIC_OPERATION_ERROR);
            flushErrors();
        }
        return null;
    }

    /**
     * Wrapper of
     * {@link TarantinoService#snapshotVirtualMachine(VirtualAppliance, VirtualMachine, VirtualMachineState, String, String, String)}
     * that uses {@link SnapshotUtils#formatSnapshotPath(VirtualMachineTemplate)} and
     * {@link SnapshotUtils#formatSnapshotFilename(VirtualMachineTemplate)} to build the instance
     * path and filename.
     * 
     * @param virtualAppliance The {@link VirtualAppliance} where the {@link VirtualMachine} is
     *            located.
     * @param virtualMachine The {@link VirtualMachine} to instance.
     * @param originalState The original {@link VirtualMachineState}.
     * @param snapshotName The final name of the {@link VirtualMachineTemplate}.
     * @return The {@link Task} UUID for progress tracking
     */
    @Transactional(readOnly = true, propagation = Propagation.REQUIRED)
    public String snapshotVirtualMachine(final VirtualAppliance virtualAppliance,
        final VirtualMachine virtualMachine, final VirtualMachineState originalState,
        final String snapshotName)
    {
        VirtualMachineTemplate template = virtualMachine.getVirtualMachineTemplate();

        return snapshotVirtualMachine(virtualAppliance, virtualMachine, originalState,
            snapshotName, SnapshotUtils.formatSnapshotPath(template),
            SnapshotUtils.formatSnapshotFilename(template));
    }

    /**
     * Builds a {@link DiskSnapshot} from a {@link VirtualMachine} and call to
     * {@link TarantinoService#createAndSendVirtualMachineInstanceOperations(VirtualAppliance, VirtualMachine, VirtualMachineState, DiskSnapshot)}
     * to start the instance process.
     * 
     * @param virtualAppliance The {@link VirtualAppliance} where the {@link VirtualMachine} is
     *            located.
     * @param virtualMachine The {@link VirtualMachine} to instance.
     * @param originalState The original {@link VirtualMachineState}.
     * @param snapshotName The final name of the {@link VirtualMachineTemplate}.
     * @param snapshotPath Path where the instance will be created.
     * @param snapshotFilename Filename of the instance.
     * @return The {@link Task} UUID for progress tracking
     */
    @Transactional(readOnly = true, propagation = Propagation.REQUIRED)
    public String snapshotVirtualMachine(final VirtualAppliance virtualAppliance,
        final VirtualMachine virtualMachine, final VirtualMachineState originalState,
        final String snapshotName, final String snapshotPath, final String snapshotFilename)
    {
        Datacenter datacenter = virtualMachine.getHypervisor().getMachine().getDatacenter();

        DiskSnapshot destinationDisk = new DiskSnapshot();
        destinationDisk.setRepository(infrastructureService.getRepository(datacenter).getUrl());
        destinationDisk.setPath(snapshotPath);
        destinationDisk.setSnapshotFilename(snapshotFilename);
        destinationDisk.setName(snapshotName);
        destinationDisk.setRepositoryManagerAddress(remoteServiceService.getAMRemoteService(
            datacenter).getUri());

        return createAndSendVirtualMachineInstanceOperations(virtualAppliance, virtualMachine,
            originalState, destinationDisk);
    }

    /**
     * Refresh the resources of the given virtual machine.
     * 
     * @param virtualMachine The virtual machine.
     * @return The {@link Task} UUID for progress tracking
     */
    @Transactional(readOnly = true, propagation = Propagation.REQUIRED)
    public String refreshVirtualMachineResources(final VirtualMachine virtualMachine,
        final VirtualAppliance virtualAppliance)
    {
        Datacenter datacenter = virtualMachine.getHypervisor().getMachine().getDatacenter();

        // Build the job sequence
        VirtualMachineDescriptionBuilder definitionBuilder =
            jobCreator.toTarantinoDto(virtualMachine, virtualAppliance);
        VirtualMachineDefinition definition = definitionBuilder.build();

        HypervisorConnection connection =
            jobCreator.hypervisorConnectionConfiguration(virtualMachine.getHypervisor());

        DatacenterTaskBuilder builder =
            new DatacenterTaskBuilder(definition, connection, userService.getCurrentUser()
                .getNick());
        builder.addRefreshResources();

        // Build redis task
        Task redisTask =
            builder.buildAsyncTask(String.valueOf(virtualMachine.getId()), TaskType.REFRESH);

        // Build tarantino task
        DatacenterTasks tarantinoTask = builder.buildTarantinoTask();

        // Add Redis task for progress tracking and send the tarantino task
        return unsubscribeVirtualMachineAndEnqueueTask(virtualMachine, redisTask, tarantinoTask,
            EventType.VM_REFRESH_RESOURCES);
    }

    /**
     * Creates and sends the DTOs for an instance of type {@link SnapshotType#FROM_STATEFUL_DISK}.
     * 
     * @param virtualAppliance The {@link VirtualAppliance} where the {@link VirtualMachine} is
     *            located.
     * @param virtualMachine The {@link VirtualMachine} to instance.
     * @param originalState The original {@link VirtualMachineState}.
     * @param snapshotName The final name of the {@link VirtualMachineTemplate}.
     * @return The {@link Task} UUID for progress tracking
     */
    public String instanceStatefulVirtualMachine(final VirtualAppliance virtualAppliance,
        final VirtualMachine virtualMachine, final VirtualMachineState originalState,
        final String snapshotName)
    {
        logger.debug("Unsupported instance type {} on community version.",
            SnapshotType.FROM_STATEFUL_DISK.name());
        addConflictErrors(APIError.STATUS_CONFLICT);
        flushErrors();

        return null;
    }

    /**
     * Creates and sends the DTOs for an instance operation.
     * 
     * @param virtualAppliance The {@link VirtualAppliance} where the {@link VirtualMachine} is
     *            located.
     * @param virtualMachine The {@link VirtualMachine} to instance.
     * @param originalState The original {@link VirtualMachineState}.
     * @param destinationDisk The destination {@link DiskSnapshot}.
     * @return The {@link Task} UUID for progress tracking
     */
    private String createAndSendVirtualMachineInstanceOperations(
        final VirtualAppliance virtualAppliance, final VirtualMachine virtualMachine,
        final VirtualMachineState originalState, final DiskSnapshot destinationDisk)
    {
        // Build the job sequence
        VirtualMachineDescriptionBuilder definitionBuilder =
            jobCreator.toTarantinoDto(virtualMachine, virtualAppliance);

        VirtualMachineDefinition definition = definitionBuilder.build();

        HypervisorConnection connection =
            jobCreator.hypervisorConnectionConfiguration(virtualMachine.getHypervisor());

        DatacenterTaskBuilder builder =
            new DatacenterTaskBuilder(definition, connection, userService.getCurrentUser()
                .getNick());

        if (SnapshotUtils.mustPowerOffToSnapshot(originalState))
        {
            logger.debug("Instance of virtual machine {} requires a power off",
                virtualMachine.getName());

            builder.add(VirtualMachineStateTransition.POWEROFF);
            builder.addSnapshot(destinationDisk);
            builder.add(VirtualMachineStateTransition.POWERON);
        }
        else
        {
            builder.addSnapshot(destinationDisk);
        }

        // Build redis task
        Task redisTask =
            builder.buildAsyncTask(String.valueOf(virtualMachine.getId()), TaskType.SNAPSHOT);

        // Build tarantino task
        DatacenterTasks tarantinoTask = builder.buildTarantinoTask();

        return unsubscribeVirtualMachineAndEnqueueTask(virtualMachine, redisTask, tarantinoTask,
            EventType.VM_INSTANCE);
    }

    public String changeVirtualMachineStateWhileStatefulInstance(
        final VirtualAppliance virtualAppliance, final VirtualMachine virtualMachine,
        final Map<String, String> data, final VirtualMachineStateTransition transition,
        final String creationUser, final boolean unsubscribe)
    {
        return null;
    }

    /**
     * Return the {@link TaskType} that is related to this {@link VirtualMachineStateTransition}. <br>
     * <br>
     * Null if empty.
     * 
     * @param machineStateTransition the current.
     * @return JobType
     */
    private TaskType getTaskTypeFromTransition(
        final VirtualMachineStateTransition machineStateTransition)
    {
        switch (machineStateTransition)
        {
            case CONFIGURE:
            {
                return TaskType.DEPLOY;
            }
            case DECONFIGURE:
            {
                return TaskType.UNDEPLOY;
            }
            case POWEROFF:
            {
                return TaskType.POWER_OFF;
            }
            case POWERON:
            {
                return TaskType.POWER_ON;
            }
            case PAUSE:
            {
                return TaskType.PAUSE;
            }
            case RESUME:
            {
                return TaskType.RESET;
            }
            case SNAPSHOT:
            {
                return TaskType.SNAPSHOT;
            }
            case RECONFIGURE:
            {
                return TaskType.RECONFIGURE;
            }
            case RESET:
            {
                return TaskType.RESET;
            }
            default:
            {
                logger.error("Error unknown transition: " + machineStateTransition);
            }
                return null;
        }
    }

    /**
     * Invalidates the last known state of the given virtual machine in the VSM.
     * <p>
     * This method will force the VSM to notify the state of the virtual machine the next time an
     * event is produced in a virtual machine.
     * 
     * @param virtualMachine The virtual machine.
     */
    protected void invalidateLastKnownState(final VirtualMachine virtualMachine)
    {
        Datacenter datacenter = virtualMachine.getHypervisor().getMachine().getDatacenter();
        RemoteService service = remoteServiceService.getVSMRemoteService(datacenter);

        logger.debug("Invalidating last known state for virtual machine {} in VSM",
            virtualMachine.getName());
        vsm.invalidateLastKnownVirtualMachineState(service, virtualMachine);
        logger.debug("State for virtual machine {} invalidated in VSM", virtualMachine.getName());
    }

    /**
     * Refreshes the state of the given virtual machine in the VSM.
     * <p>
     * This method will force the VSM to notify the state of the virtual machine.
     * 
     * @param virtualMachine The virtual machine.
     */
    protected void refreshState(final VirtualMachine virtualMachine)
    {
        Datacenter datacenter = virtualMachine.getHypervisor().getMachine().getDatacenter();
        RemoteService service = remoteServiceService.getVSMRemoteService(datacenter);

        logger.debug("Refreshing state for virtual machine {} in VSM", virtualMachine.getName());
        vsm.refreshVirtualMachineState(service, virtualMachine);
        logger.debug("State for virtual machine {} refreshed in VSM", virtualMachine.getName());
    }
}
