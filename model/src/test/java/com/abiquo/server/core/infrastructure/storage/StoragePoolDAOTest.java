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
package com.abiquo.server.core.infrastructure.storage;

import javax.persistence.EntityManager;

import org.testng.annotations.Test;

import com.abiquo.server.core.common.persistence.GenericDAOTestBase;
import com.softwarementors.bzngine.engines.jpa.EntityManagerHelper;
import com.softwarementors.bzngine.entities.test.PersistentInstanceTester;

public class StoragePoolDAOTest extends GenericDAOTestBase<String, StoragePoolDAO, StoragePool>
{
    @Override
    protected StoragePoolDAO createDao(EntityManager entityManager)
    {
        return new StoragePoolDAO(entityManager);
    }

    @Override
    protected PersistentInstanceTester<StoragePool> createEntityInstanceGenerator()
    {
        return new StoragePoolGenerator(getSeed());
    }

    @Test(enabled = false)
    public void test_findByRemoteService()
    {
//        StoragePool pool = createUniqueEntity();
//        ds().persistAll(pool.getRemoteService().getDatacenter(), pool.getRemoteService(), pool);
//
//        StoragePoolDAO dao = createDaoForRollbackTransaction();
//
//        assertSize(dao.findByRemoteService(pool.getRemoteService().getId()), 1);
//        assertSize(dao.findByRemoteService(Integer.MAX_VALUE), 0);
    }

    @Test(enabled = false)
    public void test_findByDatacenter()
    {
//        StoragePool pool = createUniqueEntity();
//        ds().persistAll(pool.getRemoteService().getDatacenter(), pool.getRemoteService(), pool);
//
//        StoragePoolDAO dao = createDaoForRollbackTransaction();
//
//        assertSize(dao.findByDatacenter(pool.getRemoteService().getDatacenter().getId()), 1);
//        assertSize(dao.findByRemoteService(Integer.MAX_VALUE), 0);
    }

    @Test(enabled = false)
    public void test_updateStoragePool()
    {
//        StoragePool randomPool = createUniqueEntity();
//        ds().persistAll(randomPool.getRemoteService().getDatacenter(),
//            randomPool.getRemoteService(), randomPool);
//
//        EntityManager em = ds().createEntityManagerAndBeginReadWriteTransaction();
//        StoragePoolDAO dao = new StoragePoolDAO(em);
//
//        StoragePool poolToUpdate = dao.findExistingById(randomPool.getId());
//        poolToUpdate.setName("Updated name");
//        EntityManagerHelper.commitAndClose(em);
//
//        StoragePool poolFromDB = ds().loadForRollback(poolToUpdate);
//        assertEquals(poolFromDB.getName(), "Updated name");
    }
}
