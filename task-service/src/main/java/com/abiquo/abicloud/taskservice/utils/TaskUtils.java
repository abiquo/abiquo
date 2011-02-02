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
package com.abiquo.abicloud.taskservice.utils;

import java.lang.reflect.Method;

import com.abiquo.abicloud.taskservice.exception.TaskServiceException;
import com.abiquo.abicloud.taskservice.model.Task;
import com.abiquo.abicloud.taskservice.model.TaskMethod;

/**
 * Utility method to work with periodical tasks.
 * 
 * @author ibarrera
 */
public class TaskUtils
{
    /**
     * Validates if a class can be scheduled.
     * 
     * @param taskClass The class to validate.
     * @throws TaskServiceException If the class cannot be scheduled.
     */
    public static void validateTask(final Class< ? > taskClass) throws TaskServiceException
    {
        // Find task annotation and
        if (!taskClass.isAnnotationPresent(Task.class))
        {
            throw new TaskServiceException("Only classes with Task annotation can be proxied");
        }

        int methodCount = 0;
        for (Method method : taskClass.getMethods())
        {
            if (method.isAnnotationPresent(TaskMethod.class))
            {
                methodCount++;
            }
        }

        if (methodCount != 1)
        {
            throw new TaskServiceException("Task class must have one (and only one) method annotated with TaskMethod annotation");
        }
    }

    /**
     * Gets the method to execute.
     * 
     * @param taskClass The class containing the task method.
     * @return The task method to execute.
     * @throws TaskServiceException If task method is not found.
     */
    public static Method getTaskMethod(final Class< ? > taskClass) throws TaskServiceException
    {
        for (Method method : taskClass.getMethods())
        {
            if (method.isAnnotationPresent(TaskMethod.class))
            {
                return method;
            }
        }

        throw new TaskServiceException("Task class must have one (and only one) method annotated with TaskMethod annotation");
    }

    /**
     * Gets the descriptive name for the task.
     * 
     * @param taskConfig The task configuration.
     * @param defaultName The default name.
     * @param suffix The suffix.
     */
    public static String getName(final Class< ? > taskClass, final String defaultName,
        final String suffix)
    {
        if ("".equals(defaultName))
        {
            return taskClass.getSimpleName() + suffix;
        }
        else
        {
            return defaultName;
        }
    }
}
