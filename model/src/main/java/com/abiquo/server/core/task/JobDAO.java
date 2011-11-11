package com.abiquo.server.core.task;

import static com.abiquo.model.redis.RedisEntityUtils.getEntityKey;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.Transaction;

import com.abiquo.model.redis.KeyMaker;
import com.abiquo.model.redis.RedisDAOBase;
import com.abiquo.model.redis.RedisEntityUtils;
import com.abiquo.server.core.task.Job.JobState;
import com.abiquo.server.core.task.Job.JobType;

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
