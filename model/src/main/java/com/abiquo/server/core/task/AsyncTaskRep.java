package com.abiquo.server.core.task;

import org.apache.commons.pool.impl.GenericObjectPool;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

import com.abiquo.model.redis.Transaction;

@Component
public class AsyncTaskRep
{
    // TODO singleton
    protected static JedisPool pool = new JedisPool(new GenericObjectPool.Config(), "localhost");

    @Autowired
    protected TaskDAO taskDao;

    @Autowired
    protected JobDAO jobDao;

    public Task save(Task task)
    {
        Jedis jedis = pool.getResource();
        Transaction transaction = (Transaction) jedis.multi();

        try
        {
            // Persist referenced Jobs
            for (Job job : task.getJobs())
            {
                jobDao.save(job, transaction);
            }

            // Persist task
            taskDao.save(task, transaction);

            transaction.exec();
        }
        finally
        {
            transaction.discardIfNeeded();
            pool.returnResource(jedis);
        }

        return task;
    }

    public Job save(Job job)
    {
        Jedis jedis = pool.getResource();
        Transaction transaction = (Transaction) jedis.multi();

        try
        {
            jobDao.save(job, transaction);
            transaction.exec();
        }
        finally
        {
            transaction.discardIfNeeded();
            pool.returnResource(jedis);
        }

        return job;
    }

    public void delete(Task task)
    {
        Jedis jedis = pool.getResource();
        Transaction transaction = (Transaction) jedis.multi();

        try
        {
            // Delete referenced jobs
            for (Job job : task.getJobs())
            {
                jobDao.delete(job, transaction);
            }

            // Delete task
            taskDao.delete(task, transaction);

            transaction.exec();
        }
        finally
        {
            transaction.discardIfNeeded();
            pool.returnResource(jedis);
        }
    }

    public Task findTask(String taskId)
    {
        Jedis jedis = pool.getResource();

        try
        {
            Task task = taskDao.findById(taskId, jedis);
            task.getJobs().addAll(
                jobDao.findJobs(taskDao.getTaskJobsKey(task.getIdAsString()), jedis));

            return task;
        }
        finally
        {
            pool.returnResource(jedis);
        }
    }

    public Job findJob(String jobId)
    {
        Jedis jedis = pool.getResource();

        try
        {
            return jobDao.findById(jobId, jedis);
        }
        finally
        {
            pool.returnResource(jedis);
        }
    }
}
