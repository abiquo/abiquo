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

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNull;
import static org.testng.Assert.assertTrue;
import static org.testng.Assert.fail;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;

import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

import com.abiquo.server.core.task.Job.JobType;
import com.abiquo.server.core.task.Task.TaskType;

@Test(groups = "redisaccess")
public class AsyncTaskRepTest
{
    AsyncTaskRep repo = new AsyncTaskRep();

    @BeforeTest
    public void testSetUp() throws Exception
    {
        Field jobDaoField = AsyncTaskRep.class.getDeclaredField("jobDao");
        Field taskDaoField = AsyncTaskRep.class.getDeclaredField("taskDao");
        Field repoField = AsyncTaskRep.class.getDeclaredField("jedisPool");

        jobDaoField.setAccessible(true);
        taskDaoField.setAccessible(true);
        repoField.setAccessible(true);

        jobDaoField.set(repo, JobDAO.class.newInstance());
        taskDaoField.set(repo, TaskDAO.class.newInstance());

        JedisPoolConfig jedisPoolConfig = new JedisPoolConfig();
        jedisPoolConfig.setTestOnBorrow(true);

        repoField.set(repo, new JedisPool(jedisPoolConfig, "localhost"));
    }

    @Test
    public void test_saveTask()
    {
        Task task = createUniqueTask();
        repo.save(task);

        Task fromDb = repo.findTask(task.getTaskId());
        assertSameTask(task, fromDb);
    }

    @Test
    public void test_findTaskByJobId()
    {
        Task task = createUniqueTask();
        Job j0 = createUniqueJob();
        Job j1 = createUniqueJob();
        Job j2 = createUniqueJob();

        task.getJobs().add(j0);
        task.getJobs().add(j1);
        task.getJobs().add(j2);

        repo.save(task);

        assertSameTask(repo.findTaskByJobId(j0.getId()), task);
        assertSameTask(repo.findTaskByJobId(j1.getId()), task);
        assertSameTask(repo.findTaskByJobId(j2.getId()), task);
        assertNull(repo.findTaskByJobId("blabla"), null);
    }

    @Test
    public void test_saveTaskWithNullFields()
    {
        Task task = createUniqueTask();
        task.setTaskId(null);
        expectRuntimeOnInsertNullField(task);

        task = createUniqueTask();
        task.setOwnerId(null);
        expectRuntimeOnInsertNullField(task);

        task = createUniqueTask();
        task.setType(null);
        expectRuntimeOnInsertNullField(task);

        task = createUniqueTask();
        task.setUserId(null);
        expectRuntimeOnInsertNullField(task);
    }

    @Test
    public void test_deleteTask()
    {
        Task task = createUniqueTask();
        repo.save(task);
        repo.delete(task);

        Task fromDb = repo.findTask(task.getTaskId());
        assertNull(fromDb);
    }

    @Test
    public void test_deleteTaskWithJobs()
    {
        Task task = createUniqueTask();
        Job j0 = createUniqueJob();
        Job j1 = createUniqueJob();
        Job j2 = createUniqueJob();

        task.getJobs().add(j0);
        task.getJobs().add(j1);
        task.getJobs().add(j2);

        repo.save(task);
        repo.delete(task);

        Task fromDb = repo.findTask(task.getTaskId());
        assertNull(fromDb);

        Job j0FromDb = repo.findJob(j0.getId());
        assertNull(j0FromDb);

        Job j1FromDb = repo.findJob(j1.getId());
        assertNull(j1FromDb);

        Job j2FromDb = repo.findJob(j2.getId());
        assertNull(j2FromDb);
    }

    @Test
    public void test_saveJob()
    {
        Job job = createUniqueJob();
        repo.save(job);

        Job fromDb = repo.findJob(job.getId());
        assertSameJob(job, fromDb);
    }

    @Test
    public void test_saveJobData()
    {
        Map<String, String> data = new HashMap<String, String>();
        data.put("DummyKey0", "A");
        data.put("DummyKey1", "B");

        Job job = createUniqueJob();
        job.getData().putAll(data);

        repo.save(job);

        Job fromDb = repo.findJob(job.getId());
        Map<String, String> dataFromDb = fromDb.getData();

        assertEquals(dataFromDb.get("DummyKey0"), "A");
        assertEquals(dataFromDb.get("DummyKey1"), "B");
    }

    @Test
    public void test_saveJobWithNullFields()
    {
        Job job = createUniqueJob();
        job.setId(null);
        expectRuntimeOnInsertNullField(job);

        job = createUniqueJob();
        job.setDescription(null);
        expectRuntimeOnInsertNullField(job);

        job = createUniqueJob();
        job.setState(null);
        expectRuntimeOnInsertNullField(job);

        job = createUniqueJob();
        job.setRollbackState(null);
        expectRuntimeOnInsertNullField(job);

        job = createUniqueJob();
        job.setType(null);
        expectRuntimeOnInsertNullField(job);
    }

    protected Task createUniqueTask()
    {
        Task task = new Task();

        task.setOwnerId(UUID.randomUUID().toString());
        task.setTaskId(UUID.randomUUID().toString());
        task.setUserId(UUID.randomUUID().toString());
        task.setType(TaskType.POWER_ON);

        return task;
    }

    protected void assertSameTask(final Task one, final Task other)
    {
        assertEquals(one.getTaskId(), other.getTaskId());
        assertEquals(one.getOwnerId(), other.getOwnerId());
        assertEquals(one.getUserId(), other.getUserId());
        assertEquals(one.getType(), other.getType());
    }

    protected void expectRuntimeOnInsertNullField(Task task)
    {
        try
        {
            repo.save(task);
            fail();
        }
        catch (RuntimeException e)
        {
            assertTrue(true);
        }
    }

    protected Job createUniqueJob()
    {
        Job job = new Job();

        job.setId(UUID.randomUUID().toString());
        job.setDescription("blablablabla");
        job.setType(JobType.CONFIGURE);
        job.setParentTaskId(UUID.randomUUID().toString());

        return job;
    }

    protected void assertSameJob(final Job one, final Job other)
    {
        assertEquals(one.getId(), other.getId());
        assertEquals(one.getEntityKey(), other.getEntityKey());
        assertEquals(one.getType(), other.getType());
        assertEquals(one.getDescription(), other.getDescription());
        assertEquals(one.getState(), other.getState());
        assertEquals(one.getRollbackState(), other.getRollbackState());
    }

    protected void expectRuntimeOnInsertNullField(Job job)
    {
        try
        {
            repo.save(job);
            fail();
        }
        catch (RuntimeException e)
        {
            assertTrue(true);
        }
    }
}
