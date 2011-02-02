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
 * This interface defines the behaviour of the class that should inform to the rest of the caches in
 * other application contexts. This class should be used by the classes that extends AbstractCache
 * to broadcast the information.
 * 
 * @author Diego Parrilla
 * @date 04-dic-2009
 * @version $Revision: 1.2 $
 */
public interface CacheDispatcher
{
    /**
     * Inform that the content of the cache described by the class name and the primary key must be
     * cleared. This is a fine grane clearing.
     * 
     * @param className - a String with the name of the class registered in the implementation of
     *            the interface
     * @param pk - an Object with the key into the cache
     */
    void publish(String className, Object pk);

    /**
     * Inform that the content of the cache described by the class name.
     * 
     * @param className - a String with the name of the class registered in the implementation of
     *            the interface
     */
    void publish(String className);
} // end CacheDispatcher
