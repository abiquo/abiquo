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
package com.abiquo.abicloud.taskservice.impl.test;

import junit.framework.TestCase;

import org.junit.Test;

import com.abiquo.abicloud.taskservice.TaskService;
import com.abiquo.abicloud.taskservice.factory.TaskServiceFactory;
import com.abiquo.abicloud.taskservice.impl.quartz.QuartzTaskService;
import com.abiquo.abicloud.taskservice.impl.quartz.test.CronTask;
import com.abiquo.abicloud.taskservice.test.LogTask;

/**
 * Unit Tests for the {@link QuartzTaskService}.
 * 
 * @author ibarrera
 */
public class AbstractTaskServiceTest extends TestCase
{
    /**
     * The task service to test.
     */
    private TaskService taskService;

    @Override
    protected void setUp() throws Exception
    {
        taskService = TaskServiceFactory.getService();
    }

    @Override
    protected void tearDown() throws Exception
    {
        taskService.unschedule(LogTask.class);
        taskService.unschedule(CronTask.class);
    }

    /**
     * Tests task scheduling.
     * 
     * @throws Exception If task scheduling fails.
     */
    @Test
    public void test0scheduleAll() throws Exception
    {
        taskService.scheduleAll();
        Thread.sleep(5000);

        assertTrue(LogTask.executionCount > 0);
        assertTrue(CronTask.executionCount > 0);
    }

}
