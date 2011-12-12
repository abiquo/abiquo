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
import static org.testng.Assert.assertNotNull;
import static org.testng.Assert.assertNull;
import static org.testng.Assert.assertTrue;
import static org.testng.Assert.fail;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.testng.annotations.Test;

import com.abiquo.model.util.redis.RedisRepoTestBase;
import com.abiquo.server.core.task.enums.TaskOwnerType;

public class AsyncTaskRepTest extends RedisRepoTestBase
{
    protected TaskGenerator taskGenerator = new TaskGenerator();

    protected JobGenerator jobGenerator = new JobGenerator();

    @Test
    public void test_saveTask()
    {
        Task task = taskGenerator.createUniqueInstance();
        repo.save(task);

        Task fromDb = repo.findTask(task.getTaskId());
        taskGenerator.assertSameTask(task, fromDb);
    }

    @Test
    public void test_saveTaskWithJobs()
    {
        Task task = taskGenerator.createUniqueInstance();
        Job j0 = jobGenerator.createUniqueInstance();
        Job j1 = jobGenerator.createUniqueInstance();
        Job j2 = jobGenerator.createUniqueInstance();

        task.getJobs().add(j0);
        task.getJobs().add(j1);
        task.getJobs().add(j2);

        task = repo.save(task);

        assertNotNull(task);
        assertEquals(task.getJobs().size(), 3);

        task = repo.save(task);
        assertEquals(task.getJobs().size(), 3);
    }

    @Test
    public void test_findTaskByJobId()
    {
        Task task = taskGenerator.createUniqueInstance();
        Job j0 = jobGenerator.createUniqueInstance();
        Job j1 = jobGenerator.createUniqueInstance();
        Job j2 = jobGenerator.createUniqueInstance();

        task.getJobs().add(j0);
        task.getJobs().add(j1);
        task.getJobs().add(j2);

        repo.save(task);

        taskGenerator.assertSameTask(repo.findTaskByJobId(j0.getId()), task);
        taskGenerator.assertSameTask(repo.findTaskByJobId(j1.getId()), task);
        taskGenerator.assertSameTask(repo.findTaskByJobId(j2.getId()), task);
        assertNull(repo.findTaskByJobId("blabla"), null);
    }

    @Test
    public void test_saveTaskWithNullFields()
    {
        Task task = taskGenerator.createUniqueInstance();
        task.setTaskId(null);
        expectRuntimeOnInsertNullField(task);

        task = taskGenerator.createUniqueInstance();
        task.setOwnerId(null);
        expectRuntimeOnInsertNullField(task);

        task = taskGenerator.createUniqueInstance();
        task.setType(null);
        expectRuntimeOnInsertNullField(task);

        task = taskGenerator.createUniqueInstance();
        task.setUserId(null);
        expectRuntimeOnInsertNullField(task);

        task = taskGenerator.createUniqueInstance();
        task.setState(null);
        expectRuntimeOnInsertNullField(task);
    }

    @Test
    public void test_deleteTask()
    {
        Task task = taskGenerator.createUniqueInstance();
        repo.save(task);
        repo.delete(task);

        Task fromDb = repo.findTask(task.getTaskId());
        assertNull(fromDb);
    }

    @Test
    public void test_deleteTaskWithJobs()
    {
        Task task = taskGenerator.createUniqueInstance();
        Job j0 = jobGenerator.createUniqueInstance();
        Job j1 = jobGenerator.createUniqueInstance();
        Job j2 = jobGenerator.createUniqueInstance();

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
        Job job = jobGenerator.createUniqueInstance();
        repo.save(job);

        Job fromDb = repo.findJob(job.getId());
        jobGenerator.assertSameJob(job, fromDb);
    }

    @Test
    public void test_saveJobData()
    {
        Map<String, String> data = new HashMap<String, String>();
        data.put("DummyKey0", "A");
        data.put("DummyKey1", "B");

        Job job = jobGenerator.createUniqueInstance();
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
        Job job = jobGenerator.createUniqueInstance();
        job.setId(null);
        expectRuntimeOnInsertNullField(job);

        job = jobGenerator.createUniqueInstance();
        job.setDescription(null);
        expectRuntimeOnInsertNullField(job);

        job = jobGenerator.createUniqueInstance();
        job.setState(null);
        expectRuntimeOnInsertNullField(job);

        job = jobGenerator.createUniqueInstance();
        job.setRollbackState(null);
        expectRuntimeOnInsertNullField(job);

        job = jobGenerator.createUniqueInstance();
        job.setType(null);
        expectRuntimeOnInsertNullField(job);
    }

    @Test
    public void test_finTaskByOwnerId()
    {
        Task task0 = taskGenerator.createUniqueInstance();
        Task task1 = taskGenerator.createUniqueInstance();

        task0.setOwnerId("A");
        task1.setOwnerId("A");

        Task task2 = taskGenerator.createUniqueInstance();
        task2.setOwnerId("B");

        Task task3 = taskGenerator.createUniqueInstance();
        task3.setOwnerId("C");

        repo.save(task0);
        repo.save(task1);
        repo.save(task2);
        repo.save(task3);

        List<Task> tasks = repo.findTasksByOwnerId(TaskOwnerType.VIRTUAL_MACHINE, "A");
        assertEquals(tasks.size(), 2);
        assertEquals(tasks.get(0).getTaskId(), task1.getTaskId());
        assertEquals(tasks.get(1).getTaskId(), task0.getTaskId());

        tasks = repo.findTasksByOwnerId(TaskOwnerType.VIRTUAL_MACHINE, "B");
        assertEquals(tasks.size(), 1);
        assertEquals(tasks.get(0).getTaskId(), task2.getTaskId());

        tasks = repo.findTasksByOwnerId(TaskOwnerType.VIRTUAL_MACHINE, "C");
        assertEquals(tasks.size(), 1);
        assertEquals(tasks.get(0).getTaskId(), task3.getTaskId());
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
