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
package com.abiquo.server.core.infrastructure.storage;

import java.util.List;

import com.abiquo.model.enumerator.RemoteServiceType;
import com.abiquo.model.enumerator.StorageTechnologyType;
import com.abiquo.server.core.common.GenericEntityGenerator;
import com.abiquo.server.core.infrastructure.RemoteService;
import com.abiquo.server.core.infrastructure.RemoteServiceGenerator;
import com.softwarementors.commons.test.SeedGenerator;
import com.softwarementors.commons.testng.AssertEx;

public class StoragePoolGenerator extends GenericEntityGenerator<StoragePool>
{
    RemoteServiceGenerator remoteServiceGenerator;

    public StoragePoolGenerator(SeedGenerator seed)
    {
        super(seed);
        remoteServiceGenerator = new RemoteServiceGenerator(seed);
    }

    @Override
    public void assertAllPropertiesEqual(StoragePool pool1, StoragePool pool2)
    {
        AssertEx.assertPropertiesEqualSilent(pool1, pool2, StoragePool.HOST_PORT_PROPERTY,
            StoragePool.URL_MANAGEMENT_PROPERTY, StoragePool.NAME_PROPERTY,
            StoragePool.TYPE_PROPERTY, StoragePool.HOST_IP_PROPERTY);

        // FIXME: change this.
//        remoteServiceGenerator.assertAllPropertiesEqual(pool1.getRemoteService(), pool2
//            .getRemoteService());
              
    }

    @Override
    public StoragePool createUniqueInstance()
    {
        RemoteService remoteService =
            remoteServiceGenerator.createInstance(RemoteServiceType.STORAGE_SYSTEM_MONITOR);

        return createInstance(remoteService);
    }

    public StoragePool createInstance(RemoteService remoteService)
    {
        // Random values
        String id = newString(nextSeed(), StoragePool.ID_LENGTH_MIN, StoragePool.ID_LENGTH_MAX);
        String name =
            newString(nextSeed(), StoragePool.NAME_LENGTH_MIN, StoragePool.NAME_LENGTH_MAX);
        String url =
            newString(nextSeed(), StoragePool.URL_MANAGEMENT_LENGTH_MIN,
                StoragePool.URL_MANAGEMENT_LENGTH_MAX);
        String hostIp =
            newString(nextSeed(), StoragePool.HOST_IP_LENGTH_MIN, StoragePool.HOST_IP_LENGTH_MAX);
        int hostPort = nextSeed();
        StorageTechnologyType type = newEnum(StorageTechnologyType.class, nextSeed());

        StoragePool pool = new StoragePool(name, url, type, hostIp, hostPort, remoteService);
        pool.setId(id);

        return pool;
    }

    @Override
    public void addAuxiliaryEntitiesToPersist(StoragePool entity, List<Object> entitiesToPersist)
    {
    	// FIXME: change this.
//        if (entity.getRemoteService() != null)
//        {
//            remoteServiceGenerator.addAuxiliaryEntitiesToPersist(entity.getRemoteService(),
//                entitiesToPersist);
//            entitiesToPersist.add(entity.getRemoteService());
//        }

        super.addAuxiliaryEntitiesToPersist(entity, entitiesToPersist);
    }

}
