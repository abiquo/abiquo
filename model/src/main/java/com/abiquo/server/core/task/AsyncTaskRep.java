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

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.Transaction;

@Component
public class AsyncTaskRep
{
    @Autowired
    protected static JedisPool jedisPool;

    @Autowired
    protected TaskDAO taskDao;

    @Autowired
    protected JobDAO jobDao;

    public Task save(Task task)
    {
        Jedis jedis = jedisPool.getResource();
        Transaction transaction = jedis.multi();
        boolean discard = true;

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
            discard = false;
        }
        finally
        {
            if (discard)
            {
                transaction.discard();
            }

            jedisPool.returnResource(jedis);
        }

        return task;
    }

    public Job save(Job job)
    {
        Jedis jedis = jedisPool.getResource();
        Transaction transaction = jedis.multi();
        boolean discard = true;

        try
        {
            jobDao.save(job, transaction);
            transaction.exec();
            discard = false;
        }
        finally
        {
            if (discard)
            {
                transaction.discard();
            }

            jedisPool.returnResource(jedis);
        }

        return job;
    }

    public void delete(Task task)
    {
        Jedis jedis = jedisPool.getResource();
        Transaction transaction = jedis.multi();
        boolean discard = true;

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
            discard = false;
        }
        finally
        {
            if (discard)
            {
                transaction.discard();
            }

            jedisPool.returnResource(jedis);
        }
    }

    public Task findTask(String taskId)
    {
        Jedis jedis = jedisPool.getResource();

        try
        {
            Task task = taskDao.findById(taskId, jedis);

            if (task != null)
            {
                task.getJobs().addAll(
                    jobDao.findJobs(taskDao.getTaskJobsKey(task.getIdAsString()), jedis));
            }

            return task;
        }
        finally
        {
            jedisPool.returnResource(jedis);
        }
    }

    public Job findJob(String jobId)
    {
        Jedis jedis = jedisPool.getResource();

        try
        {
            return jobDao.findById(jobId, jedis);
        }
        finally
        {
            jedisPool.returnResource(jedis);
        }
    }
}
