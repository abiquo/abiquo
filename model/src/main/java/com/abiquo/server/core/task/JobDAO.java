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
import com.abiquo.server.core.task.Job.JobState;
import com.abiquo.server.core.task.Job.JobType;

/**
 * This base class provides Redis-persistence logic for {@link Job} entity. <h3>Instance to persist</h3>
 * 
 * <pre>
 * job.id = 0
 * job.type = RESET
 * job.state = PENDING
 * job.rollbackState = PENDING
 * job.description = ""
 * job.parentTaskId = 1
 * job.timestamp = 123456789
 * job.data = { "one":"somenicedata" }
 * </pre>
 * 
 * <h3>Redis structure</h3>
 * 
 * <pre>
 * HMSET Job:0 "id" "0" "type" "RESET" "state" "PENDING" "rollbackState" "PENDING "description" "parentTaskId" "1" "timestamp" "123456789" "data" "Job:0:data"
 * HMSET Job:0:data "one" "somenicedata"
 * </pre>
 * 
 * @author eruiz@abiquo.com
 */
@Component
public class JobDAO extends RedisDAOBase<Job>
{
    protected final KeyMaker keyMaker = new KeyMaker(Job.class);

    public Job findById(final String jobId, Jedis jedis)
    {
        return find(getEntityKey(Job.class, jobId), jedis);
    }

    public List<Job> findJobs(final String jobsKey, Jedis jedis)
    {
        List<Job> jobs = new LinkedList<Job>();

        for (String jobKey : jedis.lrange(jobsKey, 0, -1))
        {
            jobs.add(find(jobKey, jedis));
        }

        return jobs;
    }

    protected Job find(final String jobKey, Jedis jedis)
    {
        Map<String, String> hashed = jedis.hgetAll(jobKey);

        if (hashed.isEmpty())
        {
            return null;
        }

        Job job = new Job();

        job.setId(hashed.get("id"));
        job.setType(JobType.valueOf(hashed.get("type")));
        job.setState(JobState.valueOf(hashed.get("state")));
        job.setRollbackState(JobState.valueOf(hashed.get("rollbackState")));
        job.setDescription(hashed.get("description"));
        job.setTimestamp(Long.parseLong(hashed.get("timestamp")));
        job.setParentTaskId(hashed.get("parentTaskId"));

        Map<String, String> data = jedis.hgetAll(getJobDataKey(job.getIdAsString()));

        if (data != null && !data.isEmpty())
        {
            job.getData().putAll(data);
        }

        return job;
    }

    protected void updateTimestamp(final Job job, Transaction transaction)
    {
        String entityKey = job.getEntityKey();

        transaction.hdel(entityKey, "timestamp");
        transaction.hset(entityKey, "timestamp", String.valueOf(RedisEntityUtils.getUnixtime()));
    }

    @Override
    public void delete(Job job, Transaction transaction)
    {
        transaction.del(job.getEntityKey());
        transaction.del(getJobDataKey(job.getIdAsString()));
    }

    @Override
    public void save(Job job, Transaction transaction)
    {
        // Clear to persist
        delete(job, transaction);

        // Hash plain fields
        Map<String, String> hashed = new HashMap<String, String>();
        hashed.put("id", job.getId());
        hashed.put("type", job.getType().name());
        hashed.put("state", job.getState().name());
        hashed.put("rollbackState", job.getRollbackState().name());
        hashed.put("description", job.getDescription());
        hashed.put("parentTaskId", job.getParentTaskId());
        hashed.put("timestamp", String.valueOf(RedisEntityUtils.getUnixtime()));

        // Hash extra data map
        String jobDataKey = getJobDataKey(job.getIdAsString());
        hashed.put("data", jobDataKey);

        // Persist
        transaction.hmset(job.getEntityKey(), hashed);

        if (!job.getData().isEmpty())
        {
            transaction.hmset(jobDataKey, job.getData());
        }
    }

    private String getJobDataKey(final String jobId)
    {
        return keyMaker.make(jobId, "data");
    }
}
