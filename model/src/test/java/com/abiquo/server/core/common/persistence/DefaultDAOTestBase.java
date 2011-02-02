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

package com.abiquo.server.core.common.persistence;

import java.io.Serializable;

import javax.persistence.EntityManager;

import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;

import com.abiquo.server.core.common.DefaultEntityBase;
import com.softwarementors.bzngine.dao.JpaDaoBase;
import com.softwarementors.bzngine.dao.hibernate.test.JpaHibernateDaoTestBase;
import com.softwarementors.bzngine.engines.jpa.EntityManagerHelper;
import com.softwarementors.bzngine.engines.jpa.test.configuration.EntityManagerFactoryForTesting;
import com.softwarementors.bzngine.entities.PersistentEntity;

// TODO extebds GenericDAOTestBase
public abstract class DefaultDAOTestBase<DAO extends DefaultDAOBase<Integer, T>, T extends DefaultEntityBase>
    extends JpaHibernateDaoTestBase<DAO, T, Integer>
{

    private HibernateSessionAllocationChecker sessionChecker;

    @Override
    @BeforeMethod
    protected void methodSetUp()
    {
        this.sessionChecker = new HibernateSessionAllocationChecker(getFactory());

        super.methodSetUp();
    }

    @Override
    @AfterMethod
    protected void methodTearDown()
    {
        super.methodTearDown();

        long outstandingSessions = this.sessionChecker.getOutstandingSessions();
        assert outstandingSessions == 0;
    }

    /*
     * protected boolean isContraintViolation(SQLException e) { return e instanceof
     * MySQLIntegrityConstraintViolationException; }
     */

    protected static <T extends PersistentEntity<K>, K extends Serializable> T reload(
        JpaDaoBase< ? , ? > dao, T entity)
    {
        assert dao != null;
        assert entity != null;

        T result = reload(dao.getEntityManager(), entity);
        return result;
    }

    @SuppressWarnings("unchecked")
    protected static <T extends PersistentEntity<K>, K extends Serializable> T reload(
        EntityManager entityManager, T entity)
    {
        assert entityManager != null;
        assert entityManager.isOpen();
        assert EntityManagerHelper.isInTransaction(entityManager);
        assert entity != null;

        T result = (T) entityManager.find(entity.getClass(), entity.getId());
        return result;
    }

    @Override
    protected EntityManagerFactoryForTesting getFactory()
    {
        return TestDataAccessManager.getFactory();
    }

}
