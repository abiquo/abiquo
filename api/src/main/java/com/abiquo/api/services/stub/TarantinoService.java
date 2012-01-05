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
import com.abiquo.model.enumerator.HypervisorType;
import com.abiquo.model.enumerator.RemoteServiceType;
import com.abiquo.server.core.appslibrary.VirtualMachineTemplate;
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
    private RemoteServiceService remoteServiceService;

    @Autowired
    private VsmServiceStub vsm;

    @Autowired
    private TarantinoJobCreator jobCreator;

    @Autowired
    private UserService userService;

    @Autowired
    private TaskService taskService;

    @Autowired
    private InfrastructureService infrastructureService;

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
    private void enqueueTask(final Datacenter datacenter, final Task task,
        final DatacenterTasks tasks, final EventType eventType)
    {
        try
        {
            taskService.addTask(task);
        }
        catch (RuntimeException e)
        {
            tracer.log(SeverityType.CRITICAL, ComponentType.VIRTUAL_MACHINE, eventType,
                APIError.GENERIC_OPERATION_ERROR.getMessage());

            tracer.systemError(SeverityType.CRITICAL, ComponentType.VIRTUAL_MACHINE, eventType, e,
                "redis.persistTaskError", e.getMessage());

            addNotFoundErrors(APIError.GENERIC_OPERATION_ERROR);
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
                APIError.GENERIC_OPERATION_ERROR.getMessage());

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
                    APIError.GENERIC_OPERATION_ERROR.getMessage());

                tracer.systemError(SeverityType.CRITICAL, ComponentType.VIRTUAL_MACHINE, eventType,
                    r, "redis.deleteTaskError", r.getMessage());
            }

            addNotFoundErrors(APIError.GENERIC_OPERATION_ERROR);
            flushErrors();
        }
        finally
        {
            closeProducerChannel(producer, eventType);
        }

        tracer.log(SeverityType.INFO, ComponentType.VIRTUAL_MACHINE, eventType,
            "tarantino.taskEnqueued");
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
            logger.error("Error enqueuing the deploy task dto to Tarantino with error: "
                + e.getMessage());

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

        vsm.unsubscribe(vsmRS, vm);

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
            logger.debug("Error enqueuing the reconfiguretask dto to Tarantino with error: "
                + e.getMessage() + " unmonitoring the machine: " + vm.getName());

            vsm.subscribe(vsmRS, vm);
            throw e;
        }
        catch (RuntimeException e)
        {
            logger.error("Error enqueuing the reconfigure task dto to Tarantino with error: "
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
        final boolean originalVMStateON)
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

            DatacenterTasks deployTask = null;

            if (originalVMStateON)
            {
                deployTask =
                    builder.add(VirtualMachineStateTransition.CONFIGURE)
                        .add(VirtualMachineStateTransition.POWERON).buildTarantinoTask();
            }
            else
            {
                deployTask =
                    builder.add(VirtualMachineStateTransition.CONFIGURE).buildTarantinoTask();
            }

            enqueueTask(datacenter, builder.buildAsyncTask(String.valueOf(virtualMachine.getId()),
                TaskType.HIGH_AVAILABILITY), deployTask, EventType.VM_MOVING_BY_HA);

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
            logger.error("Error enqueuing the HA deploy task dto to Tarantino with error: "
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
        Datacenter datacenter = virtualMachine.getHypervisor().getMachine().getDatacenter();

        try
        {
            HypervisorConnection conn =
                jobCreator.hypervisorConnectionConfiguration(virtualMachine.getHypervisor());
            DatacenterTaskBuilder builder =
                new DatacenterTaskBuilder(virtualMachineDesciptionBuilder.build(),
                    conn,
                    userService.getCurrentUser().getNick());

            if (VirtualMachineState.ON.equals(currentState))
            {
                builder.add(VirtualMachineStateTransition.POWEROFF);
            }
            DatacenterTasks deployTask =
                builder.add(VirtualMachineStateTransition.DECONFIGURE).buildTarantinoTask();

            enqueueTask(datacenter,
                builder.buildAsyncTask(String.valueOf(virtualMachine.getId()), TaskType.UNDEPLOY),
                deployTask, EventType.VM_UNDEPLOY);

            return deployTask.getId();
        }
        catch (NotFoundException e)
        {
            throw e;
        }
        catch (RuntimeException e)
        {
            logger.error("Error enqueuing the undeploy task dto to Tarantino with error: "
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

            if (VirtualMachineState.ON.equals(currentState))
            {
                builder.add(VirtualMachineStateTransition.POWEROFF);
            }
            // This method must delete the virtual machine in the handler

            Map<String, String> extraData = new HashMap<String, String>();
            extraData.put("delete", Boolean.TRUE.toString());
            DatacenterTasks deployTask =
                builder.add(VirtualMachineStateTransition.DECONFIGURE, extraData)
                    .buildTarantinoTask();

            enqueueTask(datacenter,
                builder.buildAsyncTask(String.valueOf(virtualMachine.getId()), TaskType.UNDEPLOY),
                deployTask, EventType.VM_UNDEPLOY);

            return deployTask.getId();
        }
        catch (NotFoundException e)
        {
            throw e;
        }
        catch (RuntimeException e)
        {
            logger.error("Error enqueuing the undeploy task dto to Tarantino with error: "
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
    public String applyVirtualMachineState(final VirtualMachine virtualMachine,
        final VirtualMachineDescriptionBuilder virtualMachineDesciptionBuilder,
        final VirtualMachineStateTransition machineStateTransition)
    {
        Datacenter datacenter = virtualMachine.getHypervisor().getMachine().getDatacenter();
        // ignoreVSMEventsIfNecessary(datacenter, virtualMachine);

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
            logger.debug("Error enqueuing the state change task dto to Tarantino with error: "
                + e.getMessage() + " machine: " + virtualMachine.getName());

            throw e;
        }
        catch (RuntimeException e)
        {
            logger.error("Error enqueuing the state change task dto to Tarantino with error: "
                + e.getMessage());

            tracer.log(SeverityType.CRITICAL, ComponentType.VIRTUAL_MACHINE, EventType.VM_STATE,
                APIError.GENERIC_OPERATION_ERROR.getMessage());

            // For the Admin to know all errors
            tracer.systemLog(SeverityType.CRITICAL, ComponentType.VIRTUAL_MACHINE,
                EventType.VM_DEPLOY, "tarantino.applyChangesVMError", e.getMessage());

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
        final String snapshotName, final SnapshotType type)
    {
        VirtualMachineTemplate template = virtualMachine.getVirtualMachineTemplate();

        return snapshotVirtualMachine(virtualAppliance, virtualMachine, originalState,
            snapshotName, SnapshotUtils.formatSnapshotPath(template),
            SnapshotUtils.formatSnapshotFilename(template));
    }

    /**
     * Builds a {@link DiskSnapshot} from a {@link VirtualMachine} and call to
     * {@link TarantinoService#createAndSendVirtualMachineInstance(VirtualAppliance, VirtualMachine, VirtualMachineState, DiskSnapshot)}
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

        return createAndSendVirtualMachineInstance(virtualAppliance, virtualMachine, originalState,
            destinationDisk);
    }

    /**
     * Creates and sends an instance operation.
     * 
     * @param virtualAppliance The {@link VirtualAppliance} where the {@link VirtualMachine} is
     *            located.
     * @param virtualMachine The {@link VirtualMachine} to instance.
     * @param originalState The original {@link VirtualMachineState}.
     * @param destinationDisk The destination {@link DiskSnapshot}.
     * @return The {@link Task} UUID for progress tracking
     */
    private String createAndSendVirtualMachineInstance(final VirtualAppliance virtualAppliance,
        final VirtualMachine virtualMachine, final VirtualMachineState originalState,
        final DiskSnapshot destinationDisk)
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

        // Unsubscribe the virtual machine to prevent unlock
        Datacenter datacenter = virtualMachine.getHypervisor().getMachine().getDatacenter();
        RemoteService service = remoteServiceService.getVSMRemoteService(datacenter);

        logger.debug("Unsubscribing virtual machine {} from VSM", virtualMachine.getName());
        vsm.unsubscribe(service, virtualMachine);
        logger.debug("Virtual machine {} unsubscribed from VSM", virtualMachine.getName());

        try
        {
            // Add Redis task for progress tracking and send the tarantino task
            logger
                .debug("Enqueuing instance task for virtual machine {}", virtualMachine.getName());
            enqueueTask(datacenter, redisTask, tarantinoTask, EventType.VM_INSTANCE);
            logger.debug("Instance task for virtual machine {} enqueued", virtualMachine.getName());
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

}
