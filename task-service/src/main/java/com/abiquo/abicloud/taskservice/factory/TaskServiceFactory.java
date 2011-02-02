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
package com.abiquo.abicloud.taskservice.factory;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.abiquo.abicloud.taskservice.TaskService;
import com.abiquo.abicloud.taskservice.exception.TaskServiceException;
import com.abiquo.abicloud.taskservice.impl.executor.ExecutorTaskService;
import com.abiquo.abicloud.taskservice.impl.quartz.QuartzTaskService;

/**
 * Factory to access the {@link TaskService}.
 * <p>
 * If Quartz library is found in classpath, this factory tries to load by a
 * {@link QuartzTaskService} implementation. Otherwise, a {@link ExecutorTaskService} implementation
 * is returned.
 * 
 * @author ibarrera
 */
public class TaskServiceFactory
{
    /**
     * The logger.
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(TaskServiceFactory.class);

    /**
     * The singleton instance of the {@link TaskService}.
     */
    private static TaskService service;

    /**
     * Gets the {@link TaskService}.
     * <p>
     * If Quartz library is found in classpath, this factory tries to load by a
     * {@link QuartzTaskService} implementation. Otherwise, a {@link ExecutorTaskService}
     * implementation is returned.
     * 
     * @return The <code>TaskService</code>.
     * @throws TaskServiceException If the service cannot be initialized.
     */
    public static TaskService getService() throws TaskServiceException
    {
        if (service == null)
        {
            try
            {
                // Check if Quartz library is in classpath
                ClassLoader cl = Thread.currentThread().getContextClassLoader();
                cl.loadClass("org.quartz.Scheduler");

                LOGGER.info("Loading Task Service: {}", QuartzTaskService.class.getName());
                service = new QuartzTaskService();
            }
            catch (ClassNotFoundException ex)
            {
                // Quartz library not found
                LOGGER.info("Loading Task Service: {}", ExecutorTaskService.class.getName());
                service = new ExecutorTaskService();
            }
        }

        return service;
    }
}
