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

import static com.abiquo.model.redis.RedisEntityUtils.getEntityKey;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Component;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.Transaction;

import com.abiquo.model.redis.KeyMaker;
import com.abiquo.model.redis.RedisDAOBase;
import com.abiquo.model.redis.RedisEntityUtils;
import com.abiquo.server.core.task.enums.TaskOwnerType;
import com.abiquo.server.core.task.enums.TaskState;
import com.abiquo.server.core.task.enums.TaskType;

/**
 * This base class provides Redis-persistence logic for {@link Task} entity. <h3>Instance to persist
 * </h3>
 * 
 * <pre>
 * task.ownerId = A
 * task.taskId = 0
 * task.userId = user0
 * task.type = DEPLOY
 * task.state = STARTED
 * task.timestamp = 123456789
 * task.jobs = [Job0, Job1]
 * 
 * <pre>
 * <h3>Redis structure</h3>
 * 
 * <pre>
 * HMSET Task:0 "ownerId" "A" "taskId" "0" "userId" "user0" "type" "DEPLOY" "state" "STARTED" "timestamp" "12346789" "jobs" "Task:0:jobs"
 * RPUSH Task:0:jobs Jobs:0
 * RPUSH Task:0:jobs Jobs:1
 * LPUSH Owner:VirtualMachine:A Task:0
 * 
 * <pre>
 * @author eruiz@abiquo.com
 */
@Component
public class TaskDAO extends RedisDAOBase<Task>
{
    protected final KeyMaker keyMaker = new KeyMaker(Task.class);

    public Task findById(final String taskId, Jedis jedis)
    {
        return find(getEntityKey(Task.class, taskId), jedis);
    }

    public List<Task> findByOwnerId(final TaskOwnerType type, final String ownerId, Jedis jedis)
    {
        List<Task> tasks = new LinkedList<Task>();
        String ownerKey = getOwnerTaskEntityKey(type, ownerId);

        for (String taskKey : jedis.lrange(ownerKey, 0, -1))
        {
            Task task = find(taskKey, jedis);

            if (task != null)
            {
                tasks.add(task);
            }
        }

        return tasks;
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
        task.setState(TaskState.valueOf(hashed.get("state")));
        task.setTimestamp(Long.parseLong(hashed.get("timestamp")));

        return task;
    }

    @Override
    public void delete(Task task, Transaction transaction)
    {
        // Build keys
        String taskJobsKey = getTaskJobsEntityKey(task.getIdAsString());
        String ownerTaskKey = getOwnerTaskEntityKey(task);

        // Delete
        transaction.del(task.getEntityKey());
        transaction.del(taskJobsKey);

        // Remove task from Owner index
        transaction.lrem(ownerTaskKey, 0, task.getEntityKey());
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
        hashed.put("state", task.getState().name());
        hashed.put("timestamp", String.valueOf(RedisEntityUtils.getUnixtime()));

        // Hash job collection
        String taskJobsKey = getTaskJobsEntityKey(task.getIdAsString());
        hashed.put("jobs", taskJobsKey);

        // Persist
        transaction.hmset(task.getEntityKey(), hashed);

        for (Job job : task.getJobs())
        {
            transaction.rpush(taskJobsKey, job.getEntityKey());
        }

        // Add task to Owner index
        transaction.lpush(getOwnerTaskEntityKey(task), task.getEntityKey());
    }

    public String getTaskJobsEntityKey(final String taskId)
    {
        return keyMaker.make(taskId, "jobs");
    }

    protected String getOwnerTaskEntityKey(final Task task)
    {
        return getOwnerTaskEntityKey(task.getType().getOwnerType(), task.getOwnerId());
    }

    protected String getOwnerTaskEntityKey(final TaskOwnerType type, final String ownerId)
    {
        return String.format("Owner:%s:%s", type.getName(), ownerId);
    }
}
