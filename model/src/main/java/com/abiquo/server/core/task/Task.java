package com.abiquo.server.core.task;

import java.util.LinkedList;
import java.util.List;

import com.abiquo.model.redis.RedisEntityBase;

public class Task extends RedisEntityBase
{
    public enum TaskType
    {
        DEPLOY, UNDEPLOY, RECONFIGURE, POWER_ON, POWER_OFF, PAUSE, RESUME, RESET, SNAPSHOT
    }

    protected String ownerId;

    protected String taskId;

    protected String userId;

    protected TaskType type;

    protected long timestamp;

    protected List<Job> jobs;

    public Task()
    {
        this.jobs = new LinkedList<Job>();
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

    public List<Job> getJobs()
    {
        return jobs;
    }

    public String getUserId()
    {
        return userId;
    }

    public void setUserId(String userId)
    {
        this.userId = userId;
    }

    public long getTimestamp()
    {
        return timestamp;
    }

    public void setTimestamp(long timestamp)
    {
        this.timestamp = timestamp;
    }

    public TaskType getType()
    {
        return type;
    }

    public void setType(TaskType type)
    {
        this.type = type;
    }

    @Override
    protected String getIdAsString()
    {
        return getTaskId();
    }
}
