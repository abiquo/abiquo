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

package com.abiquo.abiserver.persistence;

import java.io.Serializable;
import java.util.List;

import com.abiquo.abiserver.exception.PersistenceException;

/**
 * This interface provides the methods to implement a DAO pattern for CRUD(Create, Read, Update and
 * Delete) functionality. Each entity of the DataBase should have a class which implements its
 * methods in order to access to it.
 * 
 * @author abiquo
 */
public interface Crudable<T, ID extends Serializable>
{
    /**
     * Looks for an persistent object which a given id.
     * 
     * @param id identifier of the object.
     * @return Entity we want to get
     */
    T findById(ID id);

    /**
     * Looks for a list of persistent objects in Database.
     * 
     * @return Whole list of entities.
     */
    List<T> findAll() throws PersistenceException;

    /**
     * Stores an entity in database.
     * 
     * @param entity Entity to store.
     * @return he persisted entity.
     */
    T makePersistent(T entity) throws PersistenceException;

    /**
     * Stores an entity in database.
     * 
     * @param entityName The name of the entity.
     * @param entity The entity to store.
     * @return The persisted entity.
     * @throws PersistenceException If there is an error persisting the entity.
     */
    T makePersistent(String entityName, T entity) throws PersistenceException;

    /**
     * Deletes an entity from database.
     * 
     * @param entity Entity to delete
     */
    void makeTransient(T entity) throws PersistenceException;

    /**
     * Deletes an entity in database.
     * 
     * @param entityName The name of the entity.
     * @param entity The entity to delete.
     * @throws PersistenceException If there is an error deleting the entity.
     */
    void makeTransient(String entityName, T entity) throws PersistenceException;

    /**
     * Merges an entity.
     * 
     * @param entity entity to merge.
     * @return the updated entity.
     * @throws PersistenceException If an error.
     */
    T merge(T entity) throws PersistenceException;

    /**
     * Merges an entity.
     * 
     * @param entity entity to merge.
     * @param entity The entity to store.
     * @return the updated entity.
     * @throws PersistenceException If an error.
     */
    T merge(String entityName, T entity) throws PersistenceException;

    /**
     * Refreshes an entity. It means to actually retrieve the entity from DB.
     * 
     * @param entity entity to refresh.
     * @return the updated entity.
     * @throws PersistenceException If an error.
     */

    void refresh(T entity) throws PersistenceException;

}
