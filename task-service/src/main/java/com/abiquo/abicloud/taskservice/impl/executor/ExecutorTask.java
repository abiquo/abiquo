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

import static com.abiquo.abicloud.taskservice.utils.TaskUtils.getTaskMethod;

import java.lang.reflect.Method;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.abiquo.abicloud.taskservice.exception.TaskServiceException;

/**
 * A generic executor task.
 * 
 * @author ibarrera
 */
public class ExecutorTask implements Runnable
{
    /**
     * The logger.
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(ExecutorTask.class);

    /**
     * The target task to execute.
     */
    private Class<?> targetClass;

    /**
     * The target method to execute.
     */
    private Method targetMethod;

    /**
     * Creates a new {@link ExecutorTask} for the given task class.
     * 
     * @throws TaskServiceException If task cannot be created.
     */
    public ExecutorTask(final Class< ? > taskClass) throws TaskServiceException
    {
        super();

        // Get task class and task method to execute
        this.targetClass = taskClass;
        this.targetMethod = getTaskMethod(taskClass);

        // Ensure that Task class can be instantiated
        try
        {
            this.targetClass.newInstance();
        }
        catch (Exception ex)
        {
            throw new TaskServiceException("Could not instantiate task class: "
                + taskClass.getName(), ex);
        }
    }

    @Override
    public void run()
    {
        try
        {
            Object targetTask = targetClass.newInstance();
            targetMethod.invoke(targetTask);
        }
        catch (Exception ex)
        {
            // TODO: Task exception handling
            LOGGER.error("An error occured while executing task {}", targetClass.getSimpleName());
        }
    }

}
