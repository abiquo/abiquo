package com.abiquo.server.core.task;

import java.util.HashMap;
import java.util.Map;

import com.abiquo.model.redis.RedisEntityBase;

public class Job extends RedisEntityBase
{
    public enum JobState
    {
        PENDING, STARTED, DONE, FAILED, SKIPPED

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

    protected long timestamp;

    protected Map<String, String> data;

    public Job()
    {
        this.data = new HashMap<String, String>();

        this.state = JobState.PENDING;
        this.rollbackState = JobState.PENDING;
        this.description = "asss";
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

    @Override
    protected String getIdAsString()
    {
        return getId();
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
