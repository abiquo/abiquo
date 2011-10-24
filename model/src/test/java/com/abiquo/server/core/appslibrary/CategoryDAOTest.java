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

package com.abiquo.server.core.appslibrary;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;

import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import com.abiquo.server.core.common.persistence.DefaultDAOTestBase;
import com.abiquo.server.core.common.persistence.TestDataAccessManager;
import com.softwarementors.bzngine.engines.jpa.test.configuration.EntityManagerFactoryForTesting;
import com.softwarementors.bzngine.entities.test.PersistentInstanceTester;

public class CategoryDAOTest extends DefaultDAOTestBase<CategoryDAO, Category>
{
    @Override
    @BeforeMethod
    protected void methodSetUp()
    {
        super.methodSetUp();
    }

    @Override
    protected CategoryDAO createDao(final EntityManager entityManager)
    {
        return new CategoryDAO(entityManager);
    }

    @Override
    protected PersistentInstanceTester<Category> createEntityInstanceGenerator()
    {
        return new CategoryGenerator(getSeed());
    }

    @Override
    protected EntityManagerFactoryForTesting getFactory()
    {
        return TestDataAccessManager.getFactory();
    }

    @Override
    public CategoryGenerator eg()
    {
        return (CategoryGenerator) super.eg();
    }

    @Test
    public void testFindDefault()
    {
        Category category = eg().createDefaultInstance();
        ds().persistAll(category);

        CategoryDAO dao = createDaoForRollbackTransaction();
        Category defaultCategory = dao.findDefault();

        assertNotNull(defaultCategory);
        assertTrue(defaultCategory.isDefaultCategory());
        assertEquals(defaultCategory.getName(), category.getName());
    }

    @Test(expectedExceptions = NoResultException.class)
    public void testGetUnexistingDefault()
    {
        Category category = eg().createUniqueInstance();
        ds().persistAll(category);

        CategoryDAO dao = createDaoForRollbackTransaction();
        dao.findDefault();
    }

    @Test
    public void testFindByName()
    {
        Category category = eg().createUniqueInstance();
        ds().persistAll(category);

        CategoryDAO dao = createDaoForRollbackTransaction();

        Category result = dao.findByName(category.getName());
        assertNotNull(result);
        assertEquals(result.getName(), category.getName());

        result = dao.findByName(category.getName() + "UNEXISTING");
        assertNull(result);
    }

    @Test
    public void testExistCategoryWithSameName()
    {
        Category category = eg().createUniqueInstance();
        ds().persistAll(category);

        CategoryDAO dao = createDaoForRollbackTransaction();
        boolean result = dao.existsAnyWithName(category.getName());
        assertTrue(result);

        result = dao.existsAnyWithName(category.getName() + "UNEXISTING");
        assertFalse(result);
    }

}
