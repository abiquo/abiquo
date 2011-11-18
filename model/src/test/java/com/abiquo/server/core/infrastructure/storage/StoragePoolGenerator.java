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
import java.util.UUID;

import com.abiquo.server.core.common.GenericEntityGenerator;
import com.abiquo.server.core.infrastructure.Datacenter;
import com.softwarementors.commons.test.SeedGenerator;
import com.softwarementors.commons.testng.AssertEx;

public class StoragePoolGenerator extends GenericEntityGenerator<StoragePool>
{
    private TierGenerator tierGenerator;

    private StorageDeviceGenerator deviceGenerator;

    public StoragePoolGenerator(final SeedGenerator seed)
    {
        super(seed);
        tierGenerator = new TierGenerator(seed);
        deviceGenerator = new StorageDeviceGenerator(seed);
    }

    @Override
    public void assertAllPropertiesEqual(final StoragePool obj1, final StoragePool obj2)
    {
        AssertEx.assertPropertiesEqualSilent(obj1, obj2, StoragePool.NAME_PROPERTY,
            StoragePool.AVAILABLE_SIZE_IN_MB_PROPERTY, StoragePool.TOTAL_SIZE_IN_MB_PROPERTY,
            StoragePool.USED_SIZE_IN_MB_PROPERTY, StoragePool.ENABLED_PROPERTY);

        tierGenerator.assertAllPropertiesEqual(obj1.getTier(), obj2.getTier());
        deviceGenerator.assertAllPropertiesEqual(obj1.getDevice(), obj2.getDevice());
    }

    @Override
    public StoragePool createUniqueInstance()
    {
        Tier tier = tierGenerator.createUniqueInstance();
        return createInstance(tier);
    }

    public StoragePool createInstance(final StorageDevice device)
    {
        Tier tier = tierGenerator.createInstance(device.getDatacenter());
        return createInstance(device, tier);
    }

    public StoragePool createInstance(final Datacenter datacenter)
    {
        StorageDevice device = deviceGenerator.createInstance(datacenter);
        return createInstance(device);
    }

    public StoragePool createInstance(final Tier tier)
    {
        StorageDevice device = deviceGenerator.createInstance(tier.getDatacenter());
        return createInstance(device, tier);
    }

    public StoragePool createInstance(final StorageDevice device, final Tier tier)
    {
        StoragePool storagePool = new StoragePool();

        storagePool.setTier(tier);
        storagePool.setDevice(device);
        storagePool.setIdStorage(UUID.randomUUID().toString());
        storagePool.setName(newString(nextSeed(), StoragePool.NAME_LENGTH_MIN,
            StoragePool.NAME_LENGTH_MAX));
        storagePool.setAvailableSizeInMb(nextSeed());
        storagePool.setTotalSizeInMb(nextSeed());
        storagePool.setUsedSizeInMb(nextSeed());

        return storagePool;
    }

    @Override
    public void addAuxiliaryEntitiesToPersist(final StoragePool entity,
        final List<Object> entitiesToPersist)
    {
        super.addAuxiliaryEntitiesToPersist(entity, entitiesToPersist);

        Tier tier = entity.getTier();
        tierGenerator.addAuxiliaryEntitiesToPersist(tier, entitiesToPersist);
        entitiesToPersist.add(tier);

        StorageDevice device = entity.getDevice();
        deviceGenerator.addAuxiliaryEntitiesToPersist(device, entitiesToPersist);
        entitiesToPersist.add(device);
    }

}
