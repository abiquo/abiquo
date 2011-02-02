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
package com.abiquo.abicloud.taskservice.impl.quartz;

import static com.abiquo.abicloud.taskservice.utils.TaskUtils.getTaskMethod;

import java.lang.reflect.Method;

import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import com.abiquo.abicloud.taskservice.exception.TaskServiceException;

/**
 * Generic implementation of the {@link Job} interface to allow executing any class method as a
 * periodical task.
 * 
 * @author ibarrera
 */
public class QuartzTask implements Job
{
    /**
     * Attibute used to store target task class name.
     */
    public static final String TASK_CLASS_ATTRIBUTE = "taskClass";

    @Override
    public void execute(final JobExecutionContext context) throws JobExecutionException
    {
        // Get the target task class
        Class< ? > taskClass =
            (Class< ? >) context.getJobDetail().getJobDataMap().get(TASK_CLASS_ATTRIBUTE);

        Object targetTask = null;
        Method targetMethod = null;

        // Find task method to execute
        try
        {
            targetMethod = getTaskMethod(taskClass);
        }
        catch (TaskServiceException ex)
        {
            throw new JobExecutionException("Could not get task method", ex);
        }

        // Instantiate task object
        try
        {
            targetTask = taskClass.newInstance();
        }
        catch (Exception ex)
        {
            throw new JobExecutionException("Could not instantiate task class: "
                + taskClass.getName(), ex);
        }

        // Execute task method
        try
        {
            targetMethod.invoke(targetTask);
        }
        catch (Exception ex)
        {
            throw new JobExecutionException("Could not execute task: "
                + targetTask.getClass().getName(), ex);
        }
    }

}
