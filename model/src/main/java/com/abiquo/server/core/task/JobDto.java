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

import javax.xml.bind.annotation.XmlRootElement;

import com.abiquo.server.core.task.Job.JobState;
import com.abiquo.server.core.task.Job.JobType;

@XmlRootElement(name = "job")
public class JobDto
{
    protected String id;

    protected String parentTaskId;

    protected JobType type;

    protected JobState state;

    protected JobState rollbackState;

    protected String description;

    protected long timestamp;

    public String getId()
    {
        return id;
    }

    public void setId(String id)
    {
        this.id = id;
    }

    public String getParentTaskId()
    {
        return parentTaskId;
    }

    public void setParentTaskId(String parentTaskId)
    {
        this.parentTaskId = parentTaskId;
    }

    public JobType getType()
    {
        return type;
    }

    public void setType(JobType type)
    {
        this.type = type;
    }

    public JobState getState()
    {
        return state;
    }

    public void setState(JobState state)
    {
        this.state = state;
    }

    public JobState getRollbackState()
    {
        return rollbackState;
    }

    public void setRollbackState(JobState rollbackState)
    {
        this.rollbackState = rollbackState;
    }

    public String getDescription()
    {
        return description;
    }

    public void setDescription(String description)
    {
        this.description = description;
    }

    public long getTimestamp()
    {
        return timestamp;
    }

    public void setTimestamp(long timestamp)
    {
        this.timestamp = timestamp;
    }
}
