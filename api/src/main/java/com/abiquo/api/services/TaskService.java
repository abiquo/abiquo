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

package com.abiquo.api.services;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.abiquo.api.exceptions.APIError;
import com.abiquo.server.core.task.AsyncTaskRep;
import com.abiquo.server.core.task.Task;
import com.abiquo.server.core.task.enums.TaskOwnerType;
import com.abiquo.tracer.ComponentType;
import com.abiquo.tracer.EventType;
import com.abiquo.tracer.SeverityType;

@Component
public class TaskService extends DefaultApiService
{
    private final static Logger LOGGER = LoggerFactory.getLogger(TaskService.class);

    @Autowired
    private AsyncTaskRep repo;

    public List<Task> findTasks(final TaskOwnerType type, final String ownerId)
    {
        List<Task> tasks = null;

        try
        {
            tasks = repo.findTasksByOwnerId(type, ownerId);
        }
        catch (Exception e)
        {
            tracer.log(SeverityType.CRITICAL, ComponentType.VIRTUAL_MACHINE, EventType.VM_TASK,
                "redis.error.user");

            tracer.systemError(SeverityType.CRITICAL, ComponentType.VIRTUAL_MACHINE,
                EventType.VM_TASK, e, "redis.persistTaskError", e.getMessage());

            addServiceUnavailableErrors(APIError.REDIS_CONNECTION_FAILED);
            flushErrors();
        }

        return tasks;
    }

    public Task findTask(final String taskId)
    {
        Task task = null;

        try
        {
            task = repo.findTask(taskId);
        }
        catch (Exception e)
        {
            tracer.log(SeverityType.CRITICAL, ComponentType.VIRTUAL_MACHINE, EventType.VM_TASK,
                "redis.error.user");

            tracer.systemError(SeverityType.CRITICAL, ComponentType.VIRTUAL_MACHINE,
                EventType.VM_TASK, e, "redis.listTasksError", e.getMessage());

            addServiceUnavailableErrors(APIError.REDIS_CONNECTION_FAILED);
            flushErrors();
        }

        if (task == null)
        {
            LOGGER.error("Error retrieving the task: {} does not exist", taskId);
            addNotFoundErrors(APIError.NON_EXISTENT_TASK);
            flushErrors();
        }

        return task;
    }

    public Task findTask(final String ownerId, final String taskId)
    {
        Task task = null;

        try
        {
            task = repo.findTask(taskId);
        }
        catch (Exception e)
        {
            tracer.log(SeverityType.CRITICAL, ComponentType.VIRTUAL_MACHINE, EventType.VM_TASK,
                "redis.error.user");

            tracer.systemError(SeverityType.CRITICAL, ComponentType.VIRTUAL_MACHINE,
                EventType.VM_TASK, e, "redis.listTasksError", e.getMessage());

            addServiceUnavailableErrors(APIError.REDIS_CONNECTION_FAILED);
            flushErrors();
        }

        if (task == null)
        {
            LOGGER.error("Error retrieving the task: {} does not exist", taskId);
            addNotFoundErrors(APIError.NON_EXISTENT_TASK);
            flushErrors();
        }

        if (!ownerId.equals(task.getOwnerId()))
        {
            LOGGER.error(String.format(
                "Error retrieving the task: %s. Owner %s not match task owner %s", taskId, ownerId,
                task.getOwnerId()));

            addNotFoundErrors(APIError.NON_EXISTENT_TASK);
            flushErrors();
        }

        return task;
    }

    public Task addTask(final Task task)
    {
        return repo.save(task);
    }

    public void deleteTask(final Task task)
    {
        repo.delete(task);
    }
}
