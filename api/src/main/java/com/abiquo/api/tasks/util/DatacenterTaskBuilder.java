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

package com.abiquo.api.tasks.util;

import java.security.InvalidParameterException;
import java.util.Map;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.WordUtils;

import com.abiquo.commons.amqp.impl.tarantino.domain.DiskSnapshot;
import com.abiquo.commons.amqp.impl.tarantino.domain.HypervisorConnection;
import com.abiquo.commons.amqp.impl.tarantino.domain.StateTransition;
import com.abiquo.commons.amqp.impl.tarantino.domain.VirtualMachineDefinition;
import com.abiquo.commons.amqp.impl.tarantino.domain.dto.DatacenterTasks;
import com.abiquo.commons.amqp.impl.tarantino.domain.operations.ApplyVirtualMachineStateOp;
import com.abiquo.commons.amqp.impl.tarantino.domain.operations.ReconfigureVirtualMachineOp;
import com.abiquo.commons.amqp.impl.tarantino.domain.operations.RefreshVirtualMachineResourcesOp;
import com.abiquo.commons.amqp.impl.tarantino.domain.operations.SnapshotVirtualMachineOp;
import com.abiquo.server.core.cloud.VirtualMachineStateTransition;
import com.abiquo.server.core.task.Job;
import com.abiquo.server.core.task.Job.JobType;
import com.abiquo.server.core.task.Task;
import com.abiquo.server.core.task.enums.TaskType;

/**
 * Builder with a fluent interface to simplify the creation of {@link DatacenterTasks} and
 * {@link Task}.
 * 
 * @author serafin.sedano@abiquo.com
 * @author enric.ruiz@abiquo.com
 */
public class DatacenterTaskBuilder
{
    protected VirtualMachineDefinition definition;

    protected HypervisorConnection hypervisor;

    protected Task asyncTask;

    protected DatacenterTasks tarantinoTask;

    public DatacenterTaskBuilder(final VirtualMachineDefinition definition,
        final HypervisorConnection hypervisor, final String userId)
    {
        init(definition, hypervisor, userId);
    }

    public DatacenterTaskBuilder(final String userId)
    {
        init(null, null, userId);
    }

    /**
     * Initializes and reset the builder.
     * 
     * @param definition {@link VirtualMachineDefinition} to use
     * @param hypervisor {@link HypervisorConnection} to use
     * @return The {@link DatacenterTaskBuilder} self
     */
    public DatacenterTaskBuilder init(final VirtualMachineDefinition definition,
        final HypervisorConnection hypervisor, final String userId)
    {
        this.tarantinoTask = new DatacenterTasks();
        this.tarantinoTask.setDependent(Boolean.TRUE);

        this.asyncTask = new Task();
        this.asyncTask.setTaskId(this.tarantinoTask.getId());
        this.asyncTask.setUserId(userId);

        this.definition = definition;
        this.hypervisor = hypervisor;

        return this;
    }

    /**
     * End builder-method to get the {@link DatacenterTasks} for Tarantino.
     * 
     * @return The {@link DatacenterTasks} for Tarantino
     */
    public DatacenterTasks buildTarantinoTask()
    {
        return this.tarantinoTask;
    }

    /**
     * End builder-method to get the {@link Task} for Redis persistence.
     * 
     * @return The {@link Task} for Redis persistence
     */
    public Task buildAsyncTask(final String ownerId, final TaskType taskType)
    {
        if (StringUtils.isBlank(ownerId) || taskType == null)
        {
            throw new InvalidParameterException();
        }

        this.asyncTask.setOwnerId(ownerId);
        this.asyncTask.setType(taskType);

        for (Job job : this.asyncTask.getJobs())
        {
            String jobName = format(job.getType().name(), false);
            String taskName = format(this.asyncTask.getType().name(), true);
            String ownerName = format(this.asyncTask.getType().getOwnerType().name(), false);

            job.setDescription(String.format("%s task's %s on %s with id %s", taskName, jobName,
                ownerName, ownerId));
        }

        return this.asyncTask;
    }

    private String format(final String name, final boolean capitalize)
    {
        String formatted = name.toLowerCase();

        if (capitalize)
        {
            formatted = WordUtils.capitalize(formatted);
        }

        return formatted.replace("_", " ");
    }

    /**
     * Add new {@link VirtualMachineStateTransition}. This method must be used to add transitions
     * that only needs {@link VirtualMachineDefinition} and {@link HypervisorConnection} to be
     * created.
     * 
     * @param transition The transition type to create
     * @return The {@link DatacenterTaskBuilder} self
     */
    public DatacenterTaskBuilder add(final VirtualMachineStateTransition transition)
    {
        return add(this.definition, this.hypervisor, transition, null);
    }

    /**
     * Add new {@link VirtualMachineStateTransition}. This method must be used to add transitions
     * that only needs {@link VirtualMachineDefinition} and {@link HypervisorConnection} to be
     * created.
     * 
     * @param transition The transition type to create
     * @param extraData map with extra data to add to the job.
     * @return The {@link DatacenterTaskBuilder} self
     */
    public DatacenterTaskBuilder add(final VirtualMachineStateTransition transition,
        final Map<String, String> extraData)
    {
        return add(this.definition, this.hypervisor, transition, extraData);
    }

    /**
     * Add new {@link VirtualMachineStateTransition}. This method must be used to add transitions
     * that only needs {@link VirtualMachineDefinition} and {@link HypervisorConnection} to be
     * created.
     * 
     * @param transition The transition type to create
     * @param extraData map to add to the job.
     * @return The {@link DatacenterTaskBuilder} self
     */
    protected DatacenterTaskBuilder add(final VirtualMachineDefinition definition,
        final HypervisorConnection hypervisor, final VirtualMachineStateTransition transition,
        final Map<String, String> extraData)
    {
        switch (transition)
        {
            case PAUSE:
            case POWEROFF:
            case POWERON:
            case RESET:
            case RESUME:
            case CONFIGURE:
            case DECONFIGURE:
                ApplyVirtualMachineStateOp job = new ApplyVirtualMachineStateOp();
                job.setVirtualMachine(definition);
                job.setHypervisorConnection(hypervisor);
                job.setTransaction(toCommonsTransition(transition));

                this.tarantinoTask.addDatacenterJob(job);
                Job redisJob =
                    createRedisJob(job.getId(), this.getTaskTypeFromTransition(transition));
                if (extraData != null)
                {
                    redisJob.getData().putAll(extraData);
                }
                this.asyncTask.getJobs().add(redisJob);

                break;

            case ALLOCATE:
            case DEALLOCATE:
            case SNAPSHOT:
            case RECONFIGURE:
                throw new InvalidParameterException();
        }

        return this;
    }

    /**
     * Adds a new {@link ReconfigureVirtualMachineOp} to the Jobs collection.
     * 
     * @param newDefinition The virtualmachine redefinition
     * @return The {@link DatacenterTaskBuilder} self
     */
    public DatacenterTaskBuilder addReconfigure(final VirtualMachineDefinition newDefinition)
    {
        ReconfigureVirtualMachineOp job = new ReconfigureVirtualMachineOp();
        job.setVirtualMachine(definition);
        job.setHypervisorConnection(hypervisor);
        job.setNewVirtualMachine(newDefinition);

        this.tarantinoTask.addDatacenterJob(job);
        this.asyncTask.getJobs().add(createRedisJob(job.getId(), JobType.RECONFIGURE));

        return this;
    }

    /**
     * Adds a new {@link SnapshotVirtualMachineOp} to the Jobs collection.
     * 
     * @param destinationDisk The destination disk for the snapshot
     * @return The {@link DatacenterTaskBuilder} self
     */
    public DatacenterTaskBuilder addSnapshot(final DiskSnapshot destinationDisk)
    {
        SnapshotVirtualMachineOp job = new SnapshotVirtualMachineOp();
        job.setVirtualMachine(definition);
        job.setHypervisorConnection(hypervisor);
        job.setDiskSnapshot(destinationDisk);

        this.tarantinoTask.addDatacenterJob(job);

        Job redisJob = createRedisJob(job.getId(), JobType.SNAPSHOT);
        redisJob.getData().put("name", destinationDisk.getName());
        redisJob.getData().put("path",
            FilenameUtils.concat(destinationDisk.getPath(), destinationDisk.getSnapshotFilename()));

        this.asyncTask.getJobs().add(redisJob);

        return this;
    }

    /**
     * Adds a new {@link SnapshotVirtualMachineOp} to the Jobs collection.
     * 
     * @param destinationDisk The destination disk for the snapshot
     * @return The {@link DatacenterTaskBuilder} self
     */
    public DatacenterTaskBuilder addRefreshResources()
    {
        RefreshVirtualMachineResourcesOp job = new RefreshVirtualMachineResourcesOp();
        job.setVirtualMachine(definition);
        job.setHypervisorConnection(hypervisor);

        this.tarantinoTask.addDatacenterJob(job);
        this.asyncTask.getJobs().add(createRedisJob(job.getId(), JobType.REFRESH));

        return this;
    }

    /**
     * Encapsulates the {@link Job} creation.
     * 
     * @param jobId The job ID to set
     * @param jobType The job type
     * @return A new instance of {@link Job}
     */
    protected Job createRedisJob(final String jobId, final JobType jobType)
    {
        Job job = new Job();

        job.setId(jobId);
        job.setParentTaskId(this.asyncTask.getTaskId());
        job.setType(jobType);

        return job;
    }

    /**
     * Translate a {@link VirtualMachineStateTransition} to {@link StateTransition}
     * 
     * @param transition The transition to translate
     * @return The {@link StateTransition} translation
     */
    protected StateTransition toCommonsTransition(final VirtualMachineStateTransition transition)
    {
        switch (transition)
        {
            case ALLOCATE:
                return StateTransition.ALLOCATE;

            case CONFIGURE:
                return StateTransition.CONFIGURE;

            case DEALLOCATE:
                return StateTransition.DEALLOCATE;

            case DECONFIGURE:
                return StateTransition.DECONFIGURE;

            case PAUSE:
                return StateTransition.PAUSE;

            case POWEROFF:
                return StateTransition.POWEROFF;

            case POWERON:
                return StateTransition.POWERON;

            case RECONFIGURE:
                return StateTransition.RECONFIGURE;

            case RESET:
                return StateTransition.RESET;

            case RESUME:
                return StateTransition.RESUME;

            case SNAPSHOT:
                return StateTransition.SNAPSHOT;

            default:
                throw new InvalidParameterException("Error unknown transition: " + transition);
        }
    }

    /**
     * Return the {@link JobType} that is related to this {@link VirtualMachineStateTransition}. <br>
     * <br>
     * Null if empty.
     * 
     * @param machineStateTransition the current.
     * @return JobType
     */
    public JobType getTaskTypeFromTransition(
        final VirtualMachineStateTransition machineStateTransition)
    {
        switch (machineStateTransition)
        {
            case CONFIGURE:
            {
                return JobType.CONFIGURE;
            }
            case DECONFIGURE:
            {
                return JobType.DECONFIGURE;
            }
            case POWEROFF:
            {
                return JobType.POWER_OFF;
            }
            case POWERON:
            {
                return JobType.POWER_ON;
            }
            case PAUSE:
            {
                return JobType.PAUSE;
            }
            case RESUME:
            {
                return JobType.RESET;
            }
            case SNAPSHOT:
            {
                return JobType.SNAPSHOT;
            }
            case RECONFIGURE:
            {
                return JobType.RECONFIGURE;
            }
            case RESET:
            {
                return JobType.RESET;
            }
            default:
            {
                throw new InvalidParameterException("Error unknown transition: "
                    + machineStateTransition);
            }
        }
    }
}
