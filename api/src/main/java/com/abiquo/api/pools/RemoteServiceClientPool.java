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

package com.abiquo.api.pools;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.pool.impl.GenericObjectPool;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.abiquo.api.exceptions.APIError;
import com.abiquo.api.pools.impl.VSMClientPool;
import com.abiquo.api.services.DefaultApiService;
import com.abiquo.server.core.infrastructure.RemoteService;

/**
 * Global remote service client pool.
 * <p>
 * This class provides generic access to remote service clients and allows to configure the
 * connection pools for each one.
 * 
 * @author Ignasi Barrera
 * @param <T> The type of the remote service client.
 */
@Service
public class RemoteServiceClientPool extends DefaultApiService
{
    /** The logger. */
    private final static Logger LOGGER = LoggerFactory.getLogger(RemoteServiceClientPool.class);

    /**
     * A map of remote service client pools holding single connection pools for each remote service
     * in each datacenter.
     */
    private Map<String, Pool< ? >> pools = new HashMap<String, Pool< ? >>();

    /**
     * Get a remote service client from the pool to connect to the given remote service.
     * 
     * @param remoteService The target remote service.
     * @return The remote service client used to connect to the remote service.
     */
    public Object getClientFor(final RemoteService remoteService)
    {
        Pool< ? > pool = getPoolForRemoteService(remoteService);
        Object client = null;
        try
        {
            client = pool.borrowObject();
        }
        catch (Exception ex)
        {
            LOGGER.error(APIError.REMOTE_SERVICE_ERROR_BORROWING.getMessage(), ex);
            addUnexpectedErrors(APIError.REMOTE_SERVICE_ERROR_BORROWING);
            flushErrors();
        }
        return client;
    }

    /**
     * Returns a remote service client to the pool.
     * 
     * @param remoteService The target remote service.
     * @param client The remote service client to return to the pool.
     */
    public void releaseClientFor(final RemoteService remoteService, final Object client)
    {
        Pool< ? > pool = getPoolForRemoteService(remoteService);
        try
        {
            pool.returnObject(client);
        }
        catch (Exception ex)
        {
            LOGGER.trace("Unable to return remote service client for " + remoteService.getUri()
                + " to the pool", ex);
        }
    }

    /**
     * Get the remote service client pool for the given remote service.
     * <p>
     * This method will create a new pool if none exists.
     * 
     * @param remoteService The remote service to get the client pool for.
     * @return The remote service client pool for the given remote service.
     */
    protected Pool< ? > getPoolForRemoteService(final RemoteService remoteService)
    {
        Pool< ? > pool = pools.get(remoteService.getUri());
        if (pool == null)
        {
            pool = createClientPoolFor(remoteService);
        }
        return pool;
    }

    /**
     * Create a new remote service client pool for the given remote service.
     * 
     * @param remoteService The remote service to create the client pool for.
     * @return The remote service client pool for the given remote service.
     */
    protected Pool< ? > createClientPoolFor(final RemoteService remoteService)
    {
        Pool< ? > pool = null;
        switch (remoteService.getType())
        {
            case VIRTUAL_SYSTEM_MONITOR:
                pool = new VSMClientPool(remoteService);
                break;
            default:
                LOGGER.error(APIError.REMOTE_SERVICE_NON_POOLABLE.getMessage());
                addUnexpectedErrors(APIError.REMOTE_SERVICE_NON_POOLABLE);
                flushErrors();
                break;
        }
        return pool;
    }

    /**
     * The pool object itself.
     * <p>
     * This class will be the responsible for managing the objects in the pool.
     * 
     * @author Ignasi Barrera
     * @param <T> The type of the objects to be added to the pool.
     */
    public static class Pool<T> extends GenericObjectPool
    {
        /**
         * Creates the pool for clients connecting to the given remote service.
         * 
         * @param remoteService The target remote service.
         */
        public Pool(final PoolableRemoteServiceClientFactory factory)
        {
            super(factory);
        }
    }

}
