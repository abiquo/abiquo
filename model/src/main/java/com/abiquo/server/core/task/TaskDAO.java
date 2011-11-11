package com.abiquo.server.core.task;

import static com.abiquo.model.redis.RedisEntityUtils.getEntityKey;

import java.util.HashMap;
import java.util.Map;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.Transaction;

import com.abiquo.model.redis.KeyMaker;
import com.abiquo.model.redis.RedisDAOBase;
import com.abiquo.model.redis.RedisEntityUtils;
import com.abiquo.server.core.task.Task.TaskType;

public class TaskDAO extends RedisDAOBase<Task>
{
    protected final KeyMaker keyMaker = new KeyMaker(Task.class);

    public Task findById(final String taskId, Jedis jedis)
    {
        return find(getEntityKey(Task.class, taskId), jedis);
    }

    protected Task find(final String taskKey, Jedis jedis)
    {
        Map<String, String> hashed = jedis.hgetAll(taskKey);

        if (hashed.isEmpty())
        {
            return null;
        }

        Task task = new Task();

        task.setOwnerId(hashed.get("ownerId"));
        task.setTaskId(hashed.get("taskId"));
        task.setUserId(hashed.get("userId"));
        task.setType(TaskType.valueOf(hashed.get("type")));
        task.setTimestamp(Long.parseLong(hashed.get("timestamp")));

        return task;
    }

    @Override
    public void delete(Task task, Transaction transaction)
    {
        // Build keys
        String taskJobsKey = getTaskJobsKey(task.getIdAsString());

        // Delete
        transaction.del(task.getEntityKey());
        transaction.del(taskJobsKey);
    }

    @Override
    public void save(Task task, Transaction transaction)
    {
        // Clear to persist
        delete(task, transaction);

        // Hash plain fields
        Map<String, String> hashed = new HashMap<String, String>();
        hashed.put("ownerId", task.getOwnerId());
        hashed.put("taskId", task.getTaskId());
        hashed.put("userId", task.getUserId());
        hashed.put("type", task.getType().name());
        hashed.put("timestamp", String.valueOf(RedisEntityUtils.getUnixtime()));

        // Hash job collection
        String taskJobsKey = getTaskJobsKey(task.getIdAsString());
        hashed.put("jobs", taskJobsKey);

        // Persist
        transaction.hmset(task.getEntityKey(), hashed);

        for (Job job : task.getJobs())
        {
            transaction.rpush(taskJobsKey, job.getEntityKey());
        }
    }

    public String getTaskJobsKey(final String taskId)
    {
        return keyMaker.make(taskId, "jobs");
    }
}
