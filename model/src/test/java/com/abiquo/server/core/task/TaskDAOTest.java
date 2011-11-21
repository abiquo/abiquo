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

import java.util.UUID;

import org.testng.annotations.Test;

import redis.clients.jedis.Transaction;

import com.abiquo.server.core.task.Task.TaskType;

public class TaskDAOTest extends RedisDAOTestBase
{
    protected TaskDAO dao = new TaskDAO();

    @Test
    public void test_save()
    {
        Task task = createUniqueTask();
        save(task);

        Task fromDb = dao.findById(task.getTaskId(), jedis);
        assertSameTask(task, fromDb);
    }

    @Test
    public void test_saveWithNullFields()
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
    public void test_delete()
    {
        Task task = createUniqueTask();
        save(task);
        delete(task);

        Task fromDb = dao.findById(task.getTaskId(), jedis);
        assertNull(fromDb);
    }

    @Test
    public void test_deleteNonInsertedTask()
    {
        Task task = createUniqueTask();
        delete(task);

        Task fromDb = dao.findById(task.getTaskId(), jedis);
        assertNull(fromDb);
    }

    protected void save(Task task)
    {
        Transaction transaction = getTransaction();

        try
        {
            dao.save(task, transaction);
            transaction.exec();
        }
        catch (RuntimeException e)
        {
            transaction.discard();
            throw e;
        }
    }

    protected void delete(Task task)
    {
        Transaction transaction = getTransaction();

        try
        {
            dao.delete(task, transaction);
            transaction.exec();
        }
        catch (RuntimeException e)
        {
            transaction.discard();
            throw e;
        }
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
            save(task);
            fail();
        }
        catch (RuntimeException e)
        {
            assertTrue(true);
        }
    }
}
