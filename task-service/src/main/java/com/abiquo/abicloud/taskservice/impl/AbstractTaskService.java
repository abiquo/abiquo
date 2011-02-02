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
package com.abiquo.abicloud.taskservice.impl;

import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.abiquo.abicloud.taskservice.TaskService;
import com.abiquo.abicloud.taskservice.exception.TaskServiceException;
import com.abiquo.abicloud.taskservice.model.Task;
import com.abiquo.abicloud.taskservice.utils.AnnotationUtils;

/**
 * Base class with common {@link TaskService} functionality.
 * 
 * @author ibarrera
 */
public abstract class AbstractTaskService implements TaskService
{
    /**
     * The logger.
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(AbstractTaskService.class);

    /**
     * Packages to scan to find tasks.
     */
    public static final String DEFAULT_SCAN_PACKAGE = "com.abiquo";

    @Override
    public void scheduleAll() throws TaskServiceException
    {
        LOGGER.info("Running Task discovery...");

        // Find all task classes
        Set<Class< ? >> taskClasses = null;
        try
        {
            taskClasses =
                AnnotationUtils.findAnnotatedClasses(Task.class, DEFAULT_SCAN_PACKAGE, true);
        }
        catch (Exception ex)
        {
            throw new TaskServiceException("Could not find task classes", ex);
        }

        // Schedule task classes
        if (taskClasses != null && !taskClasses.isEmpty())
        {
            LOGGER.info("Found {} tasks to be scheduled", taskClasses.size());

            for (Class< ? > taskClass : taskClasses)
            {
                schedule(taskClass);
            }
        }
    }
}
