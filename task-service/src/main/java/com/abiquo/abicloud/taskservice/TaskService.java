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
package com.abiquo.abicloud.taskservice;

import com.abiquo.abicloud.taskservice.exception.TaskServiceException;
import com.abiquo.abicloud.taskservice.model.Task;

/**
 * The TaskService.
 * <p>
 * Provides generic functionality to schedule and manage periodical tasks.
 * 
 * @author ibarrera
 */
public interface TaskService
{
    /**
     * Schedules a new task.
     * 
     * @param taskClass The task to schedule.
     * @throws TaskServiceException If task cannot be scheduled.
     */
    public void schedule(final Class< ? > taskClass) throws TaskServiceException;

    /**
     * Finds and schedules all task classes.
     * <p>
     * This method scans all classes annotated with {@link Task} annotation and schedules them.
     * 
     * @throws TaskServiceException If tasks cannot be found and scheduled.
     */
    public void scheduleAll() throws TaskServiceException;

    /**
     * Removes a task from the {@link TaskService}.
     * 
     * @param taskClass The task to unschedule.
     * @throws TaskServiceException If task cannot be unscheduled.
     */
    public void unschedule(final Class< ? > taskClass) throws TaskServiceException;

    /**
     * Shuts down the service and stops all tasks.
     * 
     * @throws TaskServiceException If an error occurs qhile shutting down the service.
     */
    public void shutdown() throws TaskServiceException;
}
