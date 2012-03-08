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

import org.apache.commons.pool.BasePoolableObjectFactory;
import org.apache.commons.pool.impl.GenericObjectPool;
import org.springframework.stereotype.Component;

import com.abiquo.appliancemanager.client.AMClient;

/**
 * {@link GenericObjectPool} for {@link AMClient}.
 */
@Component
public class AMClientPool extends GenericObjectPool
{

    public AMClientPool()
    {
        super(new PoolableAMClientFactory());
    }

    /**
     * @param configTimeout, only for am request that will require some repository filesystem action
     */
    public AMClient borrowObject(final String uri, final boolean withTimeout) throws Exception
    {
        AMClient client = (AMClient) super.borrowObject();
        return client.initialize(uri, withTimeout);
    }

    private static class PoolableAMClientFactory extends BasePoolableObjectFactory
    {
        @Override
        public Object makeObject() throws Exception
        {
            return new AMClient();
        }
    }
}
