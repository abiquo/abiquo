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

import java.util.Hashtable;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This class implements the logic to cache the information for the DynamicDaoFactory. It implements
 * the CacheListener interface, and uses classes that implements the CacheReceiver and
 * CacheDispatcher interfaces
 * 
 * @author Diego Parrilla
 * @date 04-dic-2009
 * @version $Revision: 1.2 $
 */
public class AbstractCache implements CacheListener
{
    /**
     * The Link to logger
     */
    protected static final Logger debug = LoggerFactory.getLogger("AbstractCache");

    /**
     * @label refresh
     * @clientCardinality 1
     */
    private CacheDispatcher dispatcher_;

    private Hashtable cached_ = new Hashtable();

    /**
     * @directed
     * @associates <{ColleagueBranchesDao}>
     * @label decorates
     * @clientCardinality 1
     */
    private Object decorated_;

    private String daoClassName_;

    /**
     * Constructor for AbstractCache. It must be created through this constructor.
     * 
     * @param dispatcher - an object that implements the CacheDispatcher interface
     * @param receiver - an object that implements the CacheReceiver interface
     * @param dao - an object that implements the DAO wrapped by this object
     */
    public AbstractCache(CacheDispatcher dispatcher, CacheReceiver receiver, Object dao)
    {
        decorated_ = dao;
        dispatcher_ = dispatcher;
        daoClassName_ = decorated_.getClass().getName();
        receiver.addListener(daoClassName_, this);
        debug.debug("Registered Listener in the CacheReceiver for " + daoClassName_);
    }

    /**
     * This method is synchronized to avoid concurrency problems. I don't know if it helps or not...
     * 
     * @see es.amplia.marco.util.dao.CacheListener#clear(Object)
     */
    public synchronized void clear(Object pk)
    {
        cached_.remove(pk);
        debug.debug("Removed from Cache " + daoClassName_ + ":" + (String) pk);
    }

    /**
     * This method is synchronized to avoid concurrency problems. I don't know if it helps or not...
     * 
     * @see es.amplia.marco.util.dao.CacheListener#clearAll()
     */
    public synchronized void clearAll()
    {
        cached_.clear();
        debug.debug("Removed all from Cache " + daoClassName_);
    }

    /**
     * Reference to the DAO decorated by this decorator class. This object implements the class that
     * should implement the DAO interface in itself.
     * 
     * @return a direct reference to the DAO
     */
    protected Object getDecorated()
    {
        return this.decorated_;
    }

    /**
     * Restore the object linked to the key from the cache.
     * 
     * @param key - an object that identifies the values to be retrieved from the cache
     * @return an object with the value cached if exists, or null if it does not exists in the cache
     */
    protected Object get(Object key)
    {
        debug.debug("Retrieved from cache " + daoClassName_ + ":" + (String) key);
        return this.cached_.get(key);
    }

    /**
     * Inform that the content of the cache described by primary key must be cleared. This is a fine
     * grane clearing.
     * 
     * @param pk - an Object with the key into the cache
     */
    protected void publish(Object pk)
    {
        dispatcher_.publish(daoClassName_, pk);
        debug.debug("Published " + daoClassName_ + ":" + (String) pk);
    }

    /**
     * Inform that the content of the cache must be cleared.
     * 
     * @param pk - an Object with the key into the cache
     */
    protected void publish()
    {
        dispatcher_.publish(daoClassName_);
        debug.debug("Published " + daoClassName_);
    }

    /**
     * Store the object identified by the key into the cache
     * 
     * @param key - object that identifies the value into the cache
     * @param value - object to be stored in cache
     */
    protected void put(Object key, Object value)
    {
        this.cached_.put(key, value);
        debug.debug("Stored in cache " + daoClassName_ + ":" + (String) key);
    }
}
