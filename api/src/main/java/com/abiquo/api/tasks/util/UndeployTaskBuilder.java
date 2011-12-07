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

import com.abiquo.commons.amqp.impl.tarantino.domain.HypervisorConnection;
import com.abiquo.commons.amqp.impl.tarantino.domain.builder.VirtualMachineDescriptionBuilder;
import com.abiquo.commons.amqp.impl.tarantino.domain.dto.DatacenterTasks;
import com.abiquo.commons.amqp.impl.tarantino.domain.operations.ApplyVirtualMachineStateOp;
import com.abiquo.server.core.cloud.Hypervisor;
import com.abiquo.server.core.cloud.VirtualMachine;
import com.abiquo.server.core.cloud.VirtualMachineState;
import com.abiquo.server.core.cloud.VirtualMachineStateTransition;
import com.abiquo.server.core.task.Job;
import com.abiquo.server.core.task.Job.JobType;
import com.abiquo.server.core.task.Task;
import com.abiquo.server.core.task.enums.TaskType;

public class UndeployTaskBuilder
{
    protected VirtualMachine virtualMachine;

    protected VirtualMachineDescriptionBuilder builder;

    public static UndeployTaskBuilder newInstance()
    {
        return new UndeployTaskBuilder();
    }

    public DatacenterTasks buildTarantino()
    {
        DatacenterTasks tasks = new DatacenterTasks();

        tasks.setId(virtualMachine.getUuid());
        tasks.setDependent(true);

        HypervisorConnection hypervisorConnection =
            hypervisorConnectionConfiguration(virtualMachine.getHypervisor());

        ApplyVirtualMachineStateOp state =
            applyStateVirtualMachineConfiguration(virtualMachine, tasks, builder,
                hypervisorConnection, VirtualMachineStateTransition.POWEROFF);
        state.setId(tasks.getId() + ".poweroff");

        tasks.getJobs().add(state);

        ApplyVirtualMachineStateOp configuration =
            applyStateVirtualMachineConfiguration(virtualMachine, tasks, builder,
                hypervisorConnection, VirtualMachineStateTransition.DECONFIGURE);
        configuration.setId(tasks.getId() + ".deconfigure");

        tasks.getJobs().add(configuration);

        return tasks;
    }

    public Task buildRedis()
    {
        Task task = new Task();

        task.setTaskId(virtualMachine.getUuid());
        task.setOwnerId(virtualMachine.getId().toString());
        task.setType(TaskType.UNDEPLOY);

        Job job0 = new Job();
        job0.setType(JobType.POWER_OFF);

        Job job1 = new Job();
        job1.setType(JobType.DECONFIGURE);

        task.getJobs().add(job0);
        task.getJobs().add(job1);

        return task;
    }

    public UndeployTaskBuilder with(final VirtualMachine virtualMachine)
    {
        this.virtualMachine = virtualMachine;
        return this;
    }

    public UndeployTaskBuilder with(final VirtualMachineDescriptionBuilder builder)
    {
        this.builder = builder;
        return this;
    }

    public static void main(final String[] args)
    {
        UndeployTaskBuilder builder = new UndeployTaskBuilder();

        UndeployTaskBuilder.newInstance().with(new VirtualMachine())
            .with(new VirtualMachineDescriptionBuilder());

        builder.buildRedis();
        builder.buildTarantino();

    }

    /**
     * Creates a undeploy task. The Job id identifies this job and is neede to create the ids of the
     * items. It is hyerarchic so Task 1 and its job would be 1.1, another 1.2 <br>
     * <br>
     * If it is ON we shutdown the virtual machine.
     * 
     * @param virtualMachine The virtual machine to reconfigure.
     * @param builder The original configuration for the virtual machine.
     * @param currentState State of the {@link VirtualMachine} at the start of the undeploy. The
     *            state of this {@link VirtualMachine} at this point is
     *            {@link VirtualMachineState#LOCKED}.
     * @return The reconfigure task.
     */
    // public Task undeployTask(final VirtualMachine virtualMachine,
    // final VirtualMachineDescriptionBuilder builder, final VirtualMachineState currentState)
    // {
    // DatacenterTasks tarantinoTask =
    // this.undeployTarantinoTask(virtualMachine, builder, currentState);
    // return addTask(tarantinoTask, virtualMachine.getId(), TaskType.UNDEPLOY, virtualMachine
    // .getUser().getId());
    // }

    public HypervisorConnection hypervisorConnectionConfiguration(final Hypervisor hypervisor)
    {
        HypervisorConnection hypervisorConnection = new HypervisorConnection();
        hypervisorConnection.setHypervisorType(HypervisorConnection.HypervisorType
            .valueOf(hypervisor.getType().name()));
        // XXX Dummy implementation
        // hypervisorConnection.setHypervisorType(HypervisorConnection.HypervisorType.TEST);
        hypervisorConnection.setIp(hypervisor.getIp());
        hypervisorConnection.setLoginPassword(hypervisor.getPassword());
        hypervisorConnection.setLoginUser(hypervisor.getUser());
        return hypervisorConnection;
    }

    public ApplyVirtualMachineStateOp applyStateVirtualMachineConfiguration(
        final VirtualMachine virtualMachine, final DatacenterTasks deployTask,
        final VirtualMachineDescriptionBuilder vmDesc,
        final HypervisorConnection hypervisorConnection,
        final VirtualMachineStateTransition stateTransition)
    {
        ApplyVirtualMachineStateOp stateJob = new ApplyVirtualMachineStateOp();
        stateJob.setVirtualMachine(vmDesc.build(virtualMachine.getUuid()));
        stateJob.setHypervisorConnection(hypervisorConnection);
        stateJob.setTransaction(com.abiquo.commons.amqp.impl.tarantino.domain.StateTransition
            .fromValue(stateTransition.name()));
        stateJob.setId(deployTask.getId() + "." + virtualMachine.getUuid());
        return stateJob;
    }

    /**
     * Adds a task to redis. This task become available for all the resources implementing
     * {@link AbstractResourceWithTasks}.
     * 
     * @param virtualMachine
     * @param deployTask void
     * @return
     */
    // private Task addTask(final DatacenterTasks datacenterTask, final Integer ownerId,
    // final TaskType taskType, final Integer userId)
    // {
    // Task task = new Task();
    // task.setOwnerId(String.valueOf(ownerId));
    // task.setTaskId(datacenterTask.getId());
    // task.setTimestamp(Calendar.getInstance().getTimeInMillis());
    // task.setType(taskType);
    // task.setUserId(String.valueOf(userId));
    //
    // for (BaseJob j : datacenterTask.getJobs())
    // {
    // Job job = new Job();
    // job.setParentTaskId(datacenterTask.getId());
    // job.setTimestamp(Calendar.getInstance().getTimeInMillis());
    // job.setId(j.getId());
    // // FIXME
    // job.setType(JobType.DECONFIGURE);
    // task.getJobs().add(job);
    // }
    // return task;
    // }
}
