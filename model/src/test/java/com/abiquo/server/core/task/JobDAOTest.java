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

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.testng.annotations.Test;

import redis.clients.jedis.Transaction;

import com.abiquo.server.core.task.Job.JobType;

public class JobDAOTest extends RedisDAOTestBase
{
    protected JobDAO dao = new JobDAO();

    @Test
    public void test_save()
    {
        Job job = createUniqueJob();
        save(job);

        Job fromDb = dao.findById(job.getId(), jedis);
        assertSameJob(job, fromDb);
    }

    @Test
    public void test_saveWithNullFields()
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

        job = createUniqueJob();
        job.setParentTaskId(null);
        expectRuntimeOnInsertNullField(job);
    }

    @Test
    public void test_saveData()
    {
        Map<String, String> data = new HashMap<String, String>();
        data.put("DummyKey0", "A");
        data.put("DummyKey1", "B");

        Job job = createUniqueJob();
        job.getData().putAll(data);

        save(job);

        Job fromDb = dao.findById(job.getId(), jedis);
        Map<String, String> dataFromDb = fromDb.getData();

        assertEquals(dataFromDb.get("DummyKey0"), "A");
        assertEquals(dataFromDb.get("DummyKey1"), "B");
    }

    @Test
    public void test_delete()
    {
        Job job = createUniqueJob();
        save(job);
        delete(job);

        Job fromDb = dao.findById(job.getId(), jedis);
        assertNull(fromDb);
    }

    @Test
    public void test_deleteNonInsertedJob()
    {
        Job job = createUniqueJob();
        delete(job);

        Job fromDb = dao.findById(job.getId(), jedis);
        assertNull(fromDb);
    }

    protected void save(Job job)
    {
        Transaction transaction = getTransaction();

        try
        {
            dao.save(job, transaction);
            transaction.exec();
        }
        catch (RuntimeException e)
        {
            transaction.discard();
            throw e;
        }
    }

    protected void delete(Job job)
    {
        Transaction transaction = getTransaction();

        try
        {
            dao.delete(job, transaction);
            transaction.exec();
        }
        catch (RuntimeException e)
        {
            transaction.discard();
            throw e;
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
        assertEquals(one.getParentTaskId(), other.getParentTaskId());
    }

    protected void expectRuntimeOnInsertNullField(Job job)
    {
        try
        {
            save(job);
            fail();
        }
        catch (RuntimeException e)
        {
            assertTrue(true);
        }
    }
}
