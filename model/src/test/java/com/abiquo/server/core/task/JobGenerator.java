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

import com.abiquo.server.core.task.Job.JobType;

public class JobGenerator
{
    public Job createUniqueInstance()
    {
        Job job = new Job();

        job.setId(UUID.randomUUID().toString());
        job.setDescription("Random description");
        job.setType(JobType.CONFIGURE);
        job.setParentTaskId(UUID.randomUUID().toString());

        return job;
    }

    public void assertSameJob(final Job one, final Job other)
    {
        assertEquals(one.getId(), other.getId());
        assertEquals(one.getEntityKey(), other.getEntityKey());
        assertEquals(one.getType(), other.getType());
        assertEquals(one.getDescription(), other.getDescription());
        assertEquals(one.getState(), other.getState());
        assertEquals(one.getRollbackState(), other.getRollbackState());
        assertEquals(one.getParentTaskId(), other.getParentTaskId());
    }
}
