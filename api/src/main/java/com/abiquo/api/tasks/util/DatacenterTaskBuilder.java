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

import org.apache.commons.lang.StringUtils;

import com.abiquo.commons.amqp.impl.tarantino.domain.DiskSnapshot;
import com.abiquo.commons.amqp.impl.tarantino.domain.HypervisorConnection;
import com.abiquo.commons.amqp.impl.tarantino.domain.StateTransition;
import com.abiquo.commons.amqp.impl.tarantino.domain.VirtualMachineDefinition;
import com.abiquo.commons.amqp.impl.tarantino.domain.dto.DatacenterTasks;
import com.abiquo.commons.amqp.impl.tarantino.domain.operations.ApplyVirtualMachineStateOp;
import com.abiquo.commons.amqp.impl.tarantino.domain.operations.ReconfigureVirtualMachineOp;
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

    public DatacenterTaskBuilder(VirtualMachineDefinition definition,
        HypervisorConnection hypervisor)
    {
        init(definition, hypervisor);
    }

    /**
     * Initializes and reset the builder.
     * 
     * @param definition {@link VirtualMachineDefinition} to use
     * @param hypervisor {@link HypervisorConnection} to use
     * @return The {@link DatacenterTaskBuilder} self
     */
    public DatacenterTaskBuilder init(VirtualMachineDefinition definition,
        HypervisorConnection hypervisor)
    {
        this.tarantinoTask = new DatacenterTasks();

        this.asyncTask = new Task();
        this.asyncTask.setTaskId(this.tarantinoTask.getId());

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

        return this.asyncTask;
    }

    /**
     * Add new {@link VirtualMachineStateTransition}. This method must be used to add transitions
     * that only needs {@link VirtualMachineDefinition} and {@link HypervisorConnection} to be
     * created.
     * 
     * @param transition The transition type to create
     * @return The {@link DatacenterTaskBuilder} self
     */
    public DatacenterTaskBuilder add(VirtualMachineStateTransition transition)
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
                this.asyncTask.getJobs().add(createRedisJob(job.getId(), JobType.POWER_ON));

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
     * @param newDefition The virtualmachine redefinition
     * @return The {@link DatacenterTaskBuilder} self
     */
    public DatacenterTaskBuilder addReconfigure(VirtualMachineDefinition newDefition)
    {
        ReconfigureVirtualMachineOp job = new ReconfigureVirtualMachineOp();
        job.setVirtualMachine(definition);
        job.setHypervisorConnection(hypervisor);
        job.setNewVirtualMachine(newDefition);

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
    public DatacenterTaskBuilder addSnapshot(DiskSnapshot destinationDisk)
    {
        SnapshotVirtualMachineOp job = new SnapshotVirtualMachineOp();
        job.setVirtualMachine(definition);
        job.setHypervisorConnection(hypervisor);
        job.setDiskSnapshot(destinationDisk);

        this.tarantinoTask.addDatacenterJob(job);
        this.asyncTask.getJobs().add(createRedisJob(job.getId(), JobType.SNAPSHOT));

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
    protected StateTransition toCommonsTransition(VirtualMachineStateTransition transition)
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
                throw new InvalidParameterException();
        }
    }
}
