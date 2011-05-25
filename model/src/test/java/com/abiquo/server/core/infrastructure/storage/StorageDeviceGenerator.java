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

import com.abiquo.model.enumerator.StorageTechnologyType;
import com.abiquo.server.core.common.DefaultEntityGenerator;
import com.abiquo.server.core.infrastructure.Datacenter;
import com.abiquo.server.core.infrastructure.DatacenterGenerator;
import com.softwarementors.commons.test.SeedGenerator;
import com.softwarementors.commons.testng.AssertEx;

public class StorageDeviceGenerator extends DefaultEntityGenerator<StorageDevice>
{
    private DatacenterGenerator datacenterGenerator;

    public StorageDeviceGenerator(final SeedGenerator seed)
    {
        super(seed);
        datacenterGenerator = new DatacenterGenerator(seed);
    }

    @Override
    public void assertAllPropertiesEqual(final StorageDevice obj1, final StorageDevice obj2)
    {
        AssertEx.assertPropertiesEqualSilent(obj1, obj2, StorageDevice.MANAGEMENT_PORT_PROPERTY,
            StorageDevice.NAME_PROPERTY, StorageDevice.ISCSI_IP_PROPERTY,
            StorageDevice.STORAGE_TECHNOLOGY_PROPERTY, StorageDevice.MANAGEMENT_IP_PROPERTY,
            StorageDevice.ISCSI_PORT_PROPERTY);

        datacenterGenerator.assertAllPropertiesEqual(obj1.getDatacenter(), obj2.getDatacenter());
    }

    @Override
    public StorageDevice createUniqueInstance()
    {
        Datacenter datacenter = datacenterGenerator.createUniqueInstance();
        return createInstance(datacenter);
    }

    public StorageDevice createInstance(final StorageTechnologyType storageTechnology)
    {
        Datacenter datacenter = datacenterGenerator.createUniqueInstance();
        return createInstance(datacenter, storageTechnology);
    }

    public StorageDevice createInstance(final Datacenter datacenter)
    {
        return createInstance(datacenter, StorageTechnologyType.NETAPP);
    }

    public StorageDevice createInstance(final Datacenter datacenter,
        final StorageTechnologyType storageTechnology)
    {
        StorageDevice device = new StorageDevice();

        device.setDatacenter(datacenter);
        device.setIscsiIp("192.168.1.1");
        device.setIscsiPort(80);
        device.setManagementIp("102.168.1.2");
        device.setManagementPort(8080);
        device.setName(newString(nextSeed(), StorageDevice.NAME_LENGTH_MIN,
            StorageDevice.NAME_LENGTH_MAX));
        device.setStorageTechnology(storageTechnology);

        return device;
    }

    @Override
    public void addAuxiliaryEntitiesToPersist(final StorageDevice entity,
        final List<Object> entitiesToPersist)
    {
        super.addAuxiliaryEntitiesToPersist(entity, entitiesToPersist);

        Datacenter datacenter = entity.getDatacenter();
        datacenterGenerator.addAuxiliaryEntitiesToPersist(datacenter, entitiesToPersist);
        entitiesToPersist.add(datacenter);
    }

}
