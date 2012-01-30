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

package com.abiquo.server.core.task;

import static org.testng.Assert.assertEquals;

import java.util.UUID;

import com.abiquo.server.core.task.enums.TaskState;
import com.abiquo.server.core.task.enums.TaskType;

public class TaskGenerator
{
    public Task createUniqueInstance()
    {
        Task task = new Task();

        task.setOwnerId(UUID.randomUUID().toString());
        task.setTaskId(UUID.randomUUID().toString());
        task.setUserId(UUID.randomUUID().toString());
        task.setType(TaskType.POWER_ON);
        task.setState(TaskState.STARTED);

        return task;
    }

    public void assertSameTask(final Task one, final Task other)
    {
        assertEquals(one.getTaskId(), other.getTaskId());
        assertEquals(one.getOwnerId(), other.getOwnerId());
        assertEquals(one.getUserId(), other.getUserId());
        assertEquals(one.getType(), other.getType());
        assertEquals(one.getState(), other.getState());
    }
}
