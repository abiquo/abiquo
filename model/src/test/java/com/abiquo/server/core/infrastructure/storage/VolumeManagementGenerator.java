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

import com.abiquo.server.core.appslibrary.VirtualMachineTemplate;
import com.abiquo.server.core.appslibrary.VirtualMachineTemplateGenerator;
import com.abiquo.server.core.cloud.VirtualDatacenter;
import com.abiquo.server.core.cloud.VirtualDatacenterGenerator;
import com.abiquo.server.core.common.DefaultEntityGenerator;
import com.abiquo.server.core.enterprise.Enterprise;
import com.abiquo.server.core.infrastructure.Datacenter;
import com.abiquo.server.core.infrastructure.management.Rasd;
import com.abiquo.server.core.infrastructure.management.RasdManagementGenerator;
import com.softwarementors.commons.test.SeedGenerator;
import com.softwarementors.commons.testng.AssertEx;

public class VolumeManagementGenerator extends DefaultEntityGenerator<VolumeManagement>
{
    private StoragePoolGenerator poolGenerator;

    private RasdManagementGenerator rasdmGenerator;

    private VirtualDatacenterGenerator vdcGenerator;

    private VirtualMachineTemplateGenerator imageGenerator;

    public VolumeManagementGenerator(final SeedGenerator seed)
    {
        super(seed);
        poolGenerator = new StoragePoolGenerator(seed);
        rasdmGenerator = new RasdManagementGenerator(seed);
        vdcGenerator = new VirtualDatacenterGenerator(seed);
        imageGenerator = new VirtualMachineTemplateGenerator(seed);
    }

    @Override
    public void assertAllPropertiesEqual(final VolumeManagement obj1, final VolumeManagement obj2)
    {
        AssertEx.assertPropertiesEqualSilent(obj1, obj2, VolumeManagement.ID_SCSI_PROPERTY,
            VolumeManagement.STATE_PROPERTY, VolumeManagement.USED_SIZE_PROPERTY);

        poolGenerator.assertAllPropertiesEqual(obj1.getStoragePool(), obj2.getStoragePool());
        rasdmGenerator.assertAllPropertiesEqual(obj1, obj2);

        // Optional properties
        if (obj1.getVirtualMachineTemplate() != null)
        {
            imageGenerator.assertAllPropertiesEqual(obj1.getVirtualMachineTemplate(), obj2.getVirtualMachineTemplate());
        }
    }

    @Override
    public VolumeManagement createUniqueInstance()
    {
        String name =
            newString(nextSeed(), Rasd.ELEMENT_NAME_LENGTH_MIN, Rasd.ELEMENT_NAME_LENGTH_MAX);

        return createInstance(name);
    }

    public VolumeManagement createInstance(final String name)
    {
        VirtualDatacenter vdc = vdcGenerator.createUniqueInstance();
        StoragePool pool = poolGenerator.createUniqueInstance();
        return createInstance(name, vdc, pool);
    }

    public VolumeManagement createInstance(final StoragePool pool)
    {
        VirtualDatacenter vdc = vdcGenerator.createInstance(pool.getDevice().getDatacenter());
        return createInstance(pool, vdc);
    }

    public VolumeManagement createInstance(final Datacenter datacenter)
    {
        VirtualDatacenter vdc = vdcGenerator.createInstance(datacenter);
        return createInstance(vdc);
    }

    public VolumeManagement createInstance(final Datacenter datacenter, final Enterprise enterprise)
    {
        VirtualDatacenter vdc = vdcGenerator.createInstance(datacenter, enterprise);
        return createInstance(vdc);
    }

    public VolumeManagement createInstance(final VirtualDatacenter vdc)
    {
        StoragePool pool = poolGenerator.createInstance(vdc.getDatacenter());
        return createInstance(pool, vdc);
    }

    public VolumeManagement createInstance(final StoragePool pool, final VirtualDatacenter vdc,
        final String idIscsi)
    {
        String name =
            newString(nextSeed(), Rasd.ELEMENT_NAME_LENGTH_MIN, Rasd.ELEMENT_NAME_LENGTH_MAX);
        String uuid = UUID.randomUUID().toString();
        long sizeInMB = nextSeed();
        return new VolumeManagement(uuid, name, sizeInMB, idIscsi, pool, vdc);
    }

    public VolumeManagement createInstance(final StoragePool pool, final VirtualDatacenter vdc)
    {
        String name =
            newString(nextSeed(), Rasd.ELEMENT_NAME_LENGTH_MIN, Rasd.ELEMENT_NAME_LENGTH_MAX);

        return createInstance(name, vdc, pool);
    }

    public VolumeManagement createInstance(final String name, final VirtualDatacenter vdc,
        final StoragePool pool)
    {
        String uuid = UUID.randomUUID().toString();
        long sizeInMB = nextSeed();
        String idSCSI = "ip-10.60.1.26:3260-iscsi-iqn.2001-04.com.acme-lun-" + nextSeed();

        return new VolumeManagement(uuid, name, sizeInMB, idSCSI, pool, vdc);
    }

    public VolumeManagement createStatefulInstance()
    {
        VolumeManagement volume = createUniqueInstance();
        return addStatefulImageToVolume(volume, volume.getStoragePool().getDevice().getDatacenter());
    }

    public VolumeManagement createStatefulInstance(final Datacenter datacenter)
    {
        VolumeManagement volume = createInstance(datacenter);
        return addStatefulImageToVolume(volume, datacenter);
    }

    private VolumeManagement addStatefulImageToVolume(final VolumeManagement volume,
        final Datacenter datacenter)
    {
        VirtualMachineTemplate image = imageGenerator.createInstance(datacenter);
        volume.setVirtualMachineTemplate(image);
        return volume;
    }

    @Override
    public void addAuxiliaryEntitiesToPersist(final VolumeManagement entity,
        final List<Object> entitiesToPersist)
    {
        super.addAuxiliaryEntitiesToPersist(entity, entitiesToPersist);

        StoragePool storagePool = entity.getStoragePool();
        poolGenerator.addAuxiliaryEntitiesToPersist(storagePool, entitiesToPersist);
        entitiesToPersist.add(storagePool);

        rasdmGenerator.addAuxiliaryEntitiesToPersist(entity, entitiesToPersist);

        if (entity.getVirtualMachineTemplate() != null)
        {
            VirtualMachineTemplate virtualImage = entity.getVirtualMachineTemplate();
            imageGenerator.addAuxiliaryEntitiesToPersist(virtualImage, entitiesToPersist);
            entitiesToPersist.add(virtualImage);
        }
    }

}
