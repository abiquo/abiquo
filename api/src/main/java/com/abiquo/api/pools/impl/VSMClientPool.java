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

import com.abiquo.vsm.client.VSMClient;

/**
 * {@link GenericObjectPool} for VSMClient.
 * 
 * @author enric.ruiz@abiquo.com
 */
@Component(value = "vSMClientPool")
public class VSMClientPool extends GenericObjectPool
{
    public VSMClientPool()
    {
        super(new PoolableVSMClientFactory());
    }

    public VSMClient borrowObject(String uri) throws Exception
    {
        VSMClient client = (VSMClient) super.borrowObject();
        return client.initialize(uri);
    }

    private static class PoolableVSMClientFactory extends BasePoolableObjectFactory
    {
        @Override
        public Object makeObject() throws Exception
        {
            return new VSMClient();
        }
    }
}
