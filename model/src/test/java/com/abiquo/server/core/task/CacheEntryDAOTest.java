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

import static org.testng.Assert.assertNull;

import org.testng.annotations.Test;

import redis.clients.jedis.Transaction;

import com.abiquo.model.util.redis.RedisDAOTestBase;

public class CacheEntryDAOTest extends RedisDAOTestBase
{
    protected CacheEntryDAO dao = new CacheEntryDAO();

    protected CacheEntryGenerator generator = new CacheEntryGenerator();

    @Test
    public void test_save()
    {
        CacheEntry entry = generator.createUniqueInstance();
        save(entry);

        CacheEntry fromDb = dao.findById(entry.getId(), jedis);
        generator.assertSameEntry(entry, fromDb);
    }

    @Test
    public void test_delete()
    {
        CacheEntry entry = generator.createUniqueInstance();
        save(entry);
        delete(entry);

        CacheEntry fromDb = dao.findById(entry.getId(), jedis);
        assertNull(fromDb);
    }

    @Test
    public void test_deleteNonInsertedJob()
    {
        CacheEntry entry = generator.createUniqueInstance();
        delete(entry);

        CacheEntry fromDb = dao.findById(entry.getId(), jedis);
        assertNull(fromDb);
    }

    protected void save(CacheEntry entry)
    {
        Transaction transaction = getTransaction();

        try
        {
            dao.save(entry, transaction);
            transaction.exec();
        }
        catch (RuntimeException e)
        {
            transaction.discard();
            throw e;
        }
    }

    protected void delete(CacheEntry entry)
    {
        Transaction transaction = getTransaction();

        try
        {
            dao.delete(entry, transaction);
            transaction.exec();
        }
        catch (RuntimeException e)
        {
            transaction.discard();
            throw e;
        }
    }
}
