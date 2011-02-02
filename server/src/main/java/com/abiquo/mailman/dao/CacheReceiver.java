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

package com.abiquo.mailman.dao;

/**
 * Interface that defines the behaviour that should implement the class that will receive the
 * message from caches from different application scopes, and how should interact with the local
 * caching implementation.
 * 
 * @author Diego Parrilla
 * @date 04-dic-2009
 * @version $Revision: 1.2 $
 */
public interface CacheReceiver
{
    /**
     * Register the Cache DAO object in the list of objects to be notified when a change in the
     * cache from a different context occurs.
     * 
     * @param className - a String with the name of the class that implements the cache
     * @param obj - the object that implements the cache
     */
    void addListener(String className, CacheListener obj);

    /**
     * Clear the content of the cache described by the class name and the primary key. This is a
     * fine grane clearing.
     * 
     * @param className - a String with the name of the class registered in the implementation of
     *            the interface
     * @param pk - an Object with the key into the cache
     */
    void clear(String className, Object pk);

    /**
     * Clear the content of the cache described by the class name.
     * 
     * @param className - a String with the name of the class registered in the implementation of
     *            the interface
     */
    void clear(String className);
} // end CacheReceiver
