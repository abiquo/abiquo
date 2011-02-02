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
package com.abiquo.abicloud.taskservice.impl.executor;

import static com.abiquo.abicloud.taskservice.utils.TaskUtils.getName;
import static com.abiquo.abicloud.taskservice.utils.TaskUtils.validateTask;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.abiquo.abicloud.taskservice.TaskService;
import com.abiquo.abicloud.taskservice.exception.TaskServiceException;
import com.abiquo.abicloud.taskservice.impl.AbstractTaskService;
import com.abiquo.abicloud.taskservice.model.Task;

/**
 * Executor implementation of the {@link TaskService}.
 * 
 * @author ibarrera
 */
public class ExecutorTaskService extends AbstractTaskService
{
    /**
     * The logger.
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(ExecutorTaskService.class);

    /**
     * The default size of the thread pool used to schedule tasks.
     */
    private static final int DEFAULT_POOL_SIZE = 5;

    /**
     * The task executor.
     */
    private ScheduledExecutorService scheduler;

    /**
     * All scheduled tasks.
     */
    private Map<String, Future< ? >> scheduledTasks = new HashMap<String, Future< ? >>();

    /**
     * Creates a new {@link ExecutorTaskService}.
     */
    public ExecutorTaskService()
    {
        this(DEFAULT_POOL_SIZE);
    }

    /**
     * Creates a new {@link ExecutorTaskService} using a thread pool of the specified size.
     * 
     * @param poolSize The size of the thread pool used to schedule tasks.
     */
    public ExecutorTaskService(final int poolSize)
    {
        super();
        scheduler = Executors.newScheduledThreadPool(poolSize);
        LOGGER.info("{} started", this.getClass().getSimpleName());
    }

    @Override
    public void schedule(final Class< ? > taskClass) throws TaskServiceException
    {
        // Check if is a valid task class
        validateTask(taskClass);

        // Read task configuration
        Task taskConfig = taskClass.getAnnotation(Task.class);
        String taskName = getName(taskClass, taskConfig.name(), "Task");

        LOGGER.info("Adding task {} to {}", taskName, this.getClass().getSimpleName());

        // Schedule task
        ExecutorTask executorTask = new ExecutorTask(taskClass);

        ScheduledFuture< ? > scheduledTask =
            scheduler.scheduleWithFixedDelay(executorTask, taskConfig.startDelay(), taskConfig
                .interval(), taskConfig.timeUnit());

        scheduledTasks.put(taskName, scheduledTask);
    }

    @Override
    public void unschedule(final Class< ? > taskClass) throws TaskServiceException
    {
        // Check if is a valid task class
        validateTask(taskClass);

        // Read task configuration
        Task taskConfig = taskClass.getAnnotation(Task.class);
        String taskName = getName(taskClass, taskConfig.name(), "Task");

        LOGGER.info("Removing task {} from {}", taskName, this.getClass().getSimpleName());

        // Unschedule task
        ScheduledFuture< ? > scheduledTask = (ScheduledFuture< ? >) scheduledTasks.get(taskName);

        if (scheduledTask == null)
        {
            throw new TaskServiceException("Task " + taskClass.getName() + " is not scheduled");
        }

        scheduledTask.cancel(false);
    }

    @Override
    public void shutdown() throws TaskServiceException
    {
        LOGGER.info("Shutting down {}", this.getClass().getSimpleName());
        scheduler.shutdown();
    }
}
