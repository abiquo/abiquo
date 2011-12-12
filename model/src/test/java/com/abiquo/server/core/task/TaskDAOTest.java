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

import java.util.List;

import org.testng.annotations.Test;

import redis.clients.jedis.Transaction;

import com.abiquo.model.util.redis.RedisDAOTestBase;
import com.abiquo.server.core.task.enums.TaskOwnerType;

public class TaskDAOTest extends RedisDAOTestBase
{
    protected TaskDAO dao = new TaskDAO();

    protected TaskGenerator generator = new TaskGenerator();

    @Test
    public void test_save()
    {
        Task task = generator.createUniqueInstance();
        save(task);

        Task fromDb = dao.findById(task.getTaskId(), jedis);
        generator.assertSameTask(task, fromDb);
    }

    @Test
    public void test_saveWithNullFields()
    {
        Task task = generator.createUniqueInstance();
        task.setTaskId(null);
        expectRuntimeOnInsertNullField(task);

        task = generator.createUniqueInstance();
        task.setOwnerId(null);
        expectRuntimeOnInsertNullField(task);

        task = generator.createUniqueInstance();
        task.setType(null);
        expectRuntimeOnInsertNullField(task);

        task = generator.createUniqueInstance();
        task.setUserId(null);
        expectRuntimeOnInsertNullField(task);

        task = generator.createUniqueInstance();
        task.setState(null);
        expectRuntimeOnInsertNullField(task);
    }

    @Test
    public void test_delete()
    {
        Task task = generator.createUniqueInstance();
        save(task);
        delete(task);

        Task fromDb = dao.findById(task.getTaskId(), jedis);
        assertNull(fromDb);
    }

    @Test
    public void test_deleteNonInsertedTask()
    {
        Task task = generator.createUniqueInstance();
        delete(task);

        Task fromDb = dao.findById(task.getTaskId(), jedis);
        assertNull(fromDb);
    }

    @Test
    public void test_finByOwnerId()
    {
        Task task0 = generator.createUniqueInstance();
        Task task1 = generator.createUniqueInstance();

        task0.setOwnerId("A");
        task1.setOwnerId("A");

        Task task2 = generator.createUniqueInstance();
        task2.setOwnerId("B");

        Task task3 = generator.createUniqueInstance();
        task3.setOwnerId("C");

        save(task0);
        save(task1);
        save(task2);
        save(task3);

        List<Task> tasks = dao.findByOwnerId(TaskOwnerType.VIRTUAL_MACHINE, "A", jedis);
        assertEquals(tasks.size(), 2);
        assertEquals(tasks.get(0).getTaskId(), task1.getTaskId());
        assertEquals(tasks.get(1).getTaskId(), task0.getTaskId());

        tasks = dao.findByOwnerId(TaskOwnerType.VIRTUAL_MACHINE, "B", jedis);
        assertEquals(tasks.size(), 1);
        assertEquals(tasks.get(0).getTaskId(), task2.getTaskId());

        tasks = dao.findByOwnerId(TaskOwnerType.VIRTUAL_MACHINE, "C", jedis);
        assertEquals(tasks.size(), 1);
        assertEquals(tasks.get(0).getTaskId(), task3.getTaskId());
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
