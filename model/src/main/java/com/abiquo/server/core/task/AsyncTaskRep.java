package com.abiquo.server.core.task;

import org.apache.commons.pool.impl.GenericObjectPool;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.Transaction;

import com.abiquo.server.core.task.Job.JobState;

@Component
public class AsyncTaskRep
{
    // TODO singleton
    protected static JedisPool pool = new JedisPool(new GenericObjectPool.Config(), "localhost");

    @Autowired
    protected TaskDAO taskDao;

    @Autowired
    protected JobDAO jobDao;

    public Task insert(Task task)
    {
        Transaction transaction = pool.getResource().multi();

        // Persist referenced Jobs
        for (Job job : task.getJobs())
        {
            jobDao.insert(job, transaction);
        }

        // Persist task
        taskDao.insert(task, transaction);

        transaction.exec();

        return task;
    }

    public void delete(Task task)
    {
        Transaction transaction = pool.getResource().multi();

        // Delete referenced jobs
        for (Job job : task.getJobs())
        {
            jobDao.delete(job, transaction);
        }

        // Delete task
        taskDao.delete(task, transaction);

        transaction.exec();
    }

    public Task findTask(String taskId)
    {
        Jedis jedis = pool.getResource();

        Task task = taskDao.findById(taskId, jedis);
        task.getJobs().addAll(jobDao.findJobs(taskDao.getTaskJobsKey(task.getIdAsString()), jedis));

        return task;
    }

    public Job findJob(String jobId)
    {
        return jobDao.findById(jobId, pool.getResource());
    }

    public void updateJobState(Job job, JobState state)
    {
        Transaction transaction = pool.getResource().multi();
        jobDao.updateJobState(job, state, transaction);
        transaction.exec();
    }

    public void updateJobRollbackState(Job job, JobState state)
    {
        Transaction transaction = pool.getResource().multi();
        jobDao.updateJobState(job, state, transaction);
        transaction.exec();
    }
}
