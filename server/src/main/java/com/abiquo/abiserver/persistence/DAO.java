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
import java.util.Map;

/**
 * Extended functionality for the DAO pattern.
 * 
 * @param <T> The type of the object managed by this DAO.
 * @author ibarrera
 */
public interface DAO<T, ID extends Serializable> extends Crudable<T, ID>
{
    /**
     * Executes a Named Query.
     * 
     * @param name The name of the query.
     * @return The results of the query.
     */
    public List<T> findByNamedQuery(final String name);

    /**
     * Executes a Named Query.
     * 
     * @param name The name of the query.
     * @param params The query parameters.
     * @return The results of the query.
     */
    public List<T> findByNamedQuery(final String name, final Map<String, ? > params);

    /**
     * Executes a Named Query.
     * 
     * @param name The name of the query.
     * @return The unique result
     */
    public T findUniqueByNamedQuery(final String name);

    /**
     * Executes a Named Query
     * 
     * @param name The name of the query.
     * @param params The query parameters.
     * @return The unique result
     */
    public T findUniqueByNamedQuery(final String name, final Map<String, ? > params);

    /**
     * Executes a Named Query.
     * <p>
     * This method does not support collections as a query parameter, since collection parameters
     * always have to be named.
     * 
     * @param name The name of the query.
     * @param params The query parameters.
     * @return The results of the query.
     */
    public List<T> findByNamedQuery(final String name, final Object... params);

    /**
     * Find objects by a given property and value.
     * 
     * @param property The property used to filter.
     * @param value The value of the property.
     * @return The results of the filter.
     */
    public List<T> findByProperty(final String property, final Object value);

    /**
     * Find objects by a given property and value.
     * 
     * @param property The property used to filter.
     * @param value The value of the property.
     * @return The results of the filter.
     */
    public T findUniqueByProperty(final String property, final Object value);

    /**
     * Find objects by a given properties and values.
     * 
     * @param properties The properties used to filter.
     * @param values The values of the properties.
     * @return The results of the filter.
     */
    public List<T> findByProperties(final String[] properties, final Object[] values);

    /**
     * Find objects by a given properties and values.
     * 
     * @param properties The properties used to filter.
     * @param values The values of the properties.
     * @return The results of the filter.
     */
    public T findUniqueByProperties(final String[] properties, final Object[] values);

}
