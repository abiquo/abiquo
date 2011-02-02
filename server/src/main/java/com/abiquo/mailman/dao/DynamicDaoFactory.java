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

import java.lang.reflect.Constructor;
import java.util.Hashtable;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This class is a Factory of Direct Access Objects. The main characteristic of this class is: <br>
 * 1.- Is a Singleton Class <br>
 * 2.- Can create the DAO object from the class passed as parameter <br>
 * 3.- Can add a Cache Wrapper to the DAO, if the class that implements the wrapper is passed as
 * parameter <br>
 * 4.- It implements a DAO Object Pool, allowing one and only one instance of the DAO class <br>
 * <br>
 * It is recomended to implement this class as part of the Application Scope classpath (system
 * classpath or application-ear classhpath). This guarantees that the cached information is coherent
 * within the application.
 * 
 * @author Diego Parrilla
 * @date 04-dic-2009
 * @version $Revision: 1.2 $
 */
public class DynamicDaoFactory
{
    /**
     * The Link to weblogic logger
     */
    private static final Logger debug = LoggerFactory.getLogger(DynamicDaoFactory.class.getName());

    /**
     * @clientCardinality 1
     */
    private static DynamicDaoFactory instance_ = new DynamicDaoFactory();

    /**
     * @clientCardinality 1
     */
    private CacheDispatcher dispatcher_;

    /**
     * @clientCardinality 1
     */
    private CacheReceiver receiver_;

    private Hashtable daos_ = new Hashtable();

    /**
     * Constructor for DynamicDaoFactory. It must be private.
     */
    private DynamicDaoFactory()
    {
        // Initializes the Cache Classes
        CacheDispatcherTestImpl obj = new CacheDispatcherTestImpl();
        this.setDispatcher(obj);
        this.setReceiver(obj);
    }

    /**
     * Returns the singleton instance of the DynamicDoFactory class
     */
    public static DynamicDaoFactory getInstance()
    {
        return instance_;
    }

    /**
     * Implements the logic to return the DAO of the class passed as an argument, plus the class
     * Cache that wraps the DAO. If it is the first time that the DAO is referenced, then creates
     * the objects (CacheDAO and DAO). Otherwise, retrieves the CachedDAO object from the pool.
     * 
     * @param classDao - the class (NOT the interface) that implements the CacheDAO
     * @param dao - the class (NOT the interface) that implements the DAO
     * @return an object with the Cache DAO requested
     * @exception ClassNotFoundException - Cannot find the class of the DAO
     */
    public Object getDao(Class cache, Class dao) throws Exception
    {
        Object result = null;

        if (dao == null)
        {
            throw new ClassNotFoundException("Null argument as DAO");
        }

        String daoName = dao.getName();

        if ((result = daos_.get(daoName)) == null)
        {
            Object daoObject = dao.newInstance();
            debug.debug("Created DAO " + daoObject.getClass().getName());
            if (cache != null)
            {
                Constructor[] con = cache.getConstructors();
                Object[] args = new Object[3];
                args[0] = dispatcher_;
                args[1] = receiver_;
                args[2] = daoObject;

                Constructor constructor = con[0];

                if (con != null)
                {
                    result = constructor.newInstance(args);
                    debug.debug("Created CacheDAO " + cache.getName());
                }
            }
            else
            {
                result = daoObject;
            }

            daos_.put(daoName, result);
        }

        return result;
    }

    /**
     * Implements the logic to return the DAO of the class passed as an argument. It does not
     * implement any caching strategy. If it is the first time that the DAO is referenced, then
     * creates the object. Otherwise, retrieves the DAO object from the pool.
     * 
     * @param dao - the class (NOT the interface) that implements the DAO
     * @return an object with the DAO requested
     * @exception ClassNotFoundException - Cannot find the class of the DAO
     */
    public Object getDao(Class dao) throws Exception
    {
        return this.getDao(null, dao);
    }

    /**
     * Pass as an argument the object that implements the logic to inform of the changes in the
     * local context of the cache to the rest of the world
     * 
     * @param obj - an object that implements the interface CacheDispatcher
     */
    public void setDispatcher(CacheDispatcher obj)
    {
        dispatcher_ = obj;
    }

    /**
     * Pass as an argument the object that implements the logic to listen for changes in the cached
     * content outside of the context of the cache.
     * 
     * @param obj - an object that implements the interface CacheReceiver
     */
    public void setReceiver(CacheReceiver obj)
    {
        receiver_ = obj;
    }
}
