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

package com.abiquo.api.persistence;

import java.io.Serializable;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.PersistenceException;

/**
 * Basic CRUD implementations for a JpaDAO entity.
 * 
 * @deprecated This class is deprecated, DAO objects must be in the model package
 */
public abstract class JpaDAO<T, ID extends Serializable> implements Crudable<T, ID>
{
    protected EntityManager entityManager;

    @PersistenceContext
    public void setEntityManager(EntityManager entityManager)
    {
        this.entityManager = entityManager;
    }

    /**
     * @see com.abiquo.abicloud.abiserver.persistence.Crudable#findAll()
     */
    @SuppressWarnings("unchecked")
    public List<T> findAll() throws PersistenceException
    {
        try
        {
            return entityManager.createQuery("from " + getPersistentClass().getName())
                .getResultList();
        }
        catch (Exception e)
        {
            // FIXME: Error message
            throw new PersistenceException(e.getMessage());
        }
    }

    /**
     * @see com.abiquo.abicloud.abiserver.persistence.Crudable#findById(java.io.Serializable)
     */
    public T findById(ID id) throws PersistenceException
    {
        try
        {
            T entity = entityManager.find(getPersistentClass(), id);
            return entity;
        }
        catch (Exception e)
        {
            // FIXME: Error message
            throw new PersistenceException(e.getMessage());
        }
    }

    /**
     * @see com.abiquo.abicloud.abiserver.persistence.Crudable#makePersistent(java.lang.Object)
     */
    public T makePersistent(T entity) throws PersistenceException
    {
        try
        {
            entityManager.persist(entity);
            return entity;
        }
        catch (Exception e)
        {
            // FIXME: Error message
            throw new PersistenceException(e.getMessage());
        }
    }

    /**
     * @see com.abiquo.abicloud.abiserver.persistence.Crudable#makeTransient(java.lang.Object)
     */
    public void makeTransient(T entity) throws PersistenceException
    {
        try
        {
            entityManager.remove(entity);
        }
        catch (Exception e)
        {
            // FIXME: Error message
            throw new PersistenceException(e.getMessage());
        }
    }

    /**
     * @return the persistentClass
     */
    protected abstract Class<T> getPersistentClass();
}
