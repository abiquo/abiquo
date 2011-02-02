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
package com.abiquo.abicloud.taskservice.impl.quartz.config;

import java.util.Properties;

import com.abiquo.abicloud.taskservice.TaskService;

/**
 * In Memory configuration of the {@link TaskService}.
 * <p>
 * This class is used to provide hard-coded configuration in order to avoid users altering task
 * scheduling.
 * 
 * @author ibarrera
 */
public class InMemoryConfiguration implements QuartzConfiguration
{

    @Override
    public Properties getConfiguration()
    {
        Properties prop = new Properties();

        prop.put("org.quartz.jobStore.class", "org.quartz.simpl.RAMJobStore");
        prop.put("org.quartz.threadPool.class", "org.quartz.simpl.SimpleThreadPool");
        prop.put("org.quartz.threadPool.threadCount", "1");
        prop.put("org.quartz.scheduler.instanceId", "1");
        prop.put("org.quartz.scheduler.instanceName", "InMemoryTaskService");
        prop.put("org.quartz.scheduler.rmi.export", "false");
        prop.put("org.quartz.scheduler.rmi.proxy", "false");

        return prop;
    }

}
