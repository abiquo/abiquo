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
 * @author Diego Parrilla
 * @date 04-dic-2009
 * @version $Revision: 1.2 $
 */
public class CacheDispatcherTestImpl implements CacheDispatcher, CacheReceiver
{
    /**
     * The Link to weblogic logger
     */
    private static final Logger debug =
        LoggerFactory.getLogger(CacheDispatcherTestImpl.class.getName());

    private Hashtable registry_ = new Hashtable();

    /**
     * Constructor for CacheDispatcherTestImpl.
     */
    public CacheDispatcherTestImpl()
    {
        super();
    }

    /**
     * @see es.amplia.marco.util.startup.helper.CacheReceiver#addListener(String, Object)
     */
    public void addListener(String className, CacheListener obj)
    {
        registry_.put(className, obj);

        debug.debug("Registrado en el CacheReceiver el objeto de " + className);
    }

    /**
     * @see es.amplia.marco.util.startup.helper.CacheReceiver#clear(String, Object)
     */
    public void clear(String className, Object pk)
    {
        debug.debug("Recibiendo el mensaje de la red... " + className + ":" + pk);

        CacheListener obj = (CacheListener) registry_.get(className);

        if (obj != null)
        {
            obj.clear(pk);
        }
    }

    /**
     * @see es.amplia.marco.util.startup.helper.CacheReceiver#clear(String)
     */
    public void clear(String className)
    {
        debug.debug("Recibiendo el mensaje de la red... " + className);

        CacheListener obj = (CacheListener) registry_.get(className);

        if (obj != null)
        {
            obj.clearAll();
        }
    }

    /**
     * @see es.amplia.marco.util.startup.helper.CacheDispatcher#publishClear(String, Object)
     */
    public void publish(String className, Object pk)
    {
        // Aquí va el código que envía el mensaje a la red.
        debug.debug("Enviando el mensaje a red... " + className + ":" + pk);
    }

    /**
     * @see es.amplia.marco.util.startup.helper.CacheDispatcher#publishClear(String)
     */
    public void publish(String className)
    {
        // Aquí va el código que envía el mensaje a la red.
        debug.debug("Enviando el mensaje a red... " + className);
    }
}
