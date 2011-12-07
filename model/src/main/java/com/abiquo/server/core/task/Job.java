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

import java.util.HashMap;
import java.util.Map;

import com.abiquo.model.redis.RedisEntityBase;

public class Job extends RedisEntityBase
{
    public enum JobState
    {
        PENDING, STARTED, DONE, FAILED, ROLLBACK_PENDING, ROLLBACK_STARTED, ROLLBACK_DONE

    };

    public enum JobType
    {
        CONFIGURE, DECONFIGURE, RECONFIGURE, POWER_ON, POWER_OFF, PAUSE, RESUME, RESET, SNAPSHOT
    }

    protected String id;

    protected JobType type;

    protected JobState state;

    protected JobState rollbackState;

    protected String description;

    protected String parentTaskId;

    protected long timestamp;

    protected Map<String, String> data;

    public Job()
    {
        this.data = new HashMap<String, String>();

        this.state = JobState.PENDING;
        this.rollbackState = JobState.PENDING;
    }

    @Override
    protected String getIdAsString()
    {
        return getId();
    }

    public String getId()
    {
        return id;
    }

    public void setId(String id)
    {
        this.id = id;
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

    public Map<String, String> getData()
    {
        return this.data;
    }

    public long getTimestamp()
    {
        return timestamp;
    }

    public void setTimestamp(long timestamp)
    {
        this.timestamp = timestamp;
    }

    public String getParentTaskId()
    {
        return parentTaskId;
    }

    public void setParentTaskId(String parentTaskId)
    {
        this.parentTaskId = parentTaskId;
    }

    @Override
    public String toString()
    {
        return String.format(
            "Job id: '%s' Job type: '%s' Task id: '%s' State: '%s' Rollback state: '%s'", getId(),
            getType().name(), getParentTaskId(), getState().name(), getRollbackState().name());
    }
}
