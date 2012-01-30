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

import com.abiquo.model.transport.SingleResourceTransportDto;
import com.abiquo.server.core.task.enums.TaskState;
import com.abiquo.server.core.task.enums.TaskType;

@XmlRootElement(name = "task")
public class TaskDto extends SingleResourceTransportDto
{
    private static final long serialVersionUID = 3587883337292250623L;

    protected String ownerId;

    protected String taskId;

    protected String userId;

    protected TaskType type;

    protected long timestamp;

    protected TaskState state;

    protected JobsDto jobs;

    public TaskDto()
    {
    }

    public JobsDto getJobs()
    {
        return jobs;
    }

    public void setJobs(JobsDto jobs)
    {
        this.jobs = jobs;
    }

    public String getOwnerId()
    {
        return ownerId;
    }

    public void setOwnerId(String ownerId)
    {
        this.ownerId = ownerId;
    }

    public String getTaskId()
    {
        return taskId;
    }

    public void setTaskId(String taskId)
    {
        this.taskId = taskId;
    }

    public String getUserId()
    {
        return userId;
    }

    public void setUserId(String userId)
    {
        this.userId = userId;
    }

    public TaskType getType()
    {
        return type;
    }

    public void setType(TaskType type)
    {
        this.type = type;
    }

    public long getTimestamp()
    {
        return timestamp;
    }

    public void setTimestamp(long timestamp)
    {
        this.timestamp = timestamp;
    }

    public TaskState getState()
    {
        return state;
    }

    public void setState(TaskState state)
    {
        this.state = state;
    }
}
