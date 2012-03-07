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

package com.abiquo.api.pools.impl;

import org.apache.commons.pool.impl.GenericObjectPool;

import com.abiquo.api.pools.PoolableRemoteServiceClientFactory;
import com.abiquo.api.pools.RemoteServiceClientPool.Pool;
import com.abiquo.server.core.infrastructure.RemoteService;
import com.abiquo.vsm.client.VSMClient;

/**
 * {@link GenericObjectPool} for VSMClient.
 * 
 * @author enric.ruiz@abiquo.com
 */
public class VSMClientPool extends Pool<VSMClient>
{
    public VSMClientPool(final RemoteService remoteService)
    {
        super(new PoolableVSMClientFactory(remoteService));
    }

    private static class PoolableVSMClientFactory extends PoolableRemoteServiceClientFactory
    {
        public PoolableVSMClientFactory(final RemoteService remoteService)
        {
            super(remoteService);
        }

        @Override
        public Object makeObject() throws Exception
        {
            VSMClient client = new VSMClient();
            client.initialize(remoteService.getUri());
            return client;
        }
    }
}
