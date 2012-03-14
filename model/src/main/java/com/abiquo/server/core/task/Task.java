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

import java.util.LinkedList;
import java.util.List;

import com.abiquo.model.redis.RedisEntityBase;
import com.abiquo.server.core.task.enums.TaskState;
import com.abiquo.server.core.task.enums.TaskType;

public class Task extends RedisEntityBase
{
    protected String ownerId;

    protected String taskId;

    protected String userId;

    protected TaskType type;

    protected long timestamp;

    protected TaskState state;

    protected List<Job> jobs;

    public Task()
    {
        this.jobs = new LinkedList<Job>();
        this.state = TaskState.PENDING;
    }

    @Override
    protected String getIdAsString()
    {
        return getTaskId();
    }

    public String getOwnerId()
    {
        return ownerId;
    }

    public void setOwnerId(final String ownerId)
    {
        this.ownerId = ownerId;
    }

    public String getTaskId()
    {
        return taskId;
    }

    public void setTaskId(final String taskId)
    {
        this.taskId = taskId;
    }

    public List<Job> getJobs()
    {
        return jobs;
    }

    public void setJobs(final List<Job> jobs)
    {
        this.jobs = jobs;
    }

    public String getUserId()
    {
        return userId;
    }

    public void setUserId(final String userId)
    {
        this.userId = userId;
    }

    public long getTimestamp()
    {
        return timestamp;
    }

    public void setTimestamp(final long timestamp)
    {
        this.timestamp = timestamp;
    }

    public TaskType getType()
    {
        return type;
    }

    public void setType(final TaskType type)
    {
        this.type = type;
    }

    public TaskState getState()
    {
        return state;
    }

    public void setState(final TaskState state)
    {
        this.state = state;
    }

    public boolean isAborted()
    {
        return this.getState() == TaskState.ABORTED;
    }

    public boolean isFinished()
    {
        return this.getState() == TaskState.ABORTED
            || this.getState() == TaskState.FINISHED_SUCCESSFULLY
            || this.getState() == TaskState.FINISHED_UNSUCCESSFULLY;
    }

    @Override
    public String toString()
    {
        return String.format(
            "Task id: '%s' Task type: '%s' Owner id: '%s' Owner type: '%s' State: '%s'",
            getTaskId(), getType().name(), getOwnerId(), getType().getOwnerType().name(),
            getState().name());
    }
}
