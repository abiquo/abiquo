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
import java.util.Random;

import com.abiquo.server.core.cloud.VirtualImage;
import com.abiquo.server.core.cloud.VirtualImageGenerator;
import com.abiquo.server.core.common.DefaultEntityGenerator;
import com.softwarementors.commons.test.SeedGenerator;
import com.softwarementors.commons.testng.AssertEx;

public class VolumeManagementGenerator extends DefaultEntityGenerator<VolumeManagement>
{

    // TODO extends RasdGenerator and use the 'super'

    VirtualImageGenerator vImageGen;

    StoragePoolGenerator storagePoolGen;

    // VirtualApplianceGenerator vAppGen;

    public VolumeManagementGenerator(SeedGenerator seed)
    {
        super(seed);

        vImageGen = new VirtualImageGenerator(seed);
        storagePoolGen = new StoragePoolGenerator(seed);
        // vAppGen = new VirtualApplianceGenerator(seed);
    }

    @Override
    public void assertAllPropertiesEqual(VolumeManagement obj1, VolumeManagement obj2)
    {
        AssertEx.assertPropertiesEqualSilent(obj1, obj2, VolumeManagement.ID_SCSI_PROPERTY,
            VolumeManagement.STATE_PROPERTY, VolumeManagement.USED_SIZE_PROPERTY); // TODO add assoc
        // properitess
    }

    @Override
    public VolumeManagement createUniqueInstance()
    {
        StoragePool storagePool = storagePoolGen.createUniqueInstance();
        VirtualImage vimage = vImageGen.createUniqueInstance();

        // VirtualAppliance virtualAppliance = vAppGen.createUniqueInstance();

        VolumeManagement volumeManagement = new VolumeManagement(storagePool, vimage, "" + new Random().nextInt());

        return volumeManagement;
    }

    @Override
    public void addAuxiliaryEntitiesToPersist(VolumeManagement entity,
        List<Object> entitiesToPersist)
    {
        super.addAuxiliaryEntitiesToPersist(entity, entitiesToPersist);

        VirtualImage vimage = entity.getVirtualImage();
        // VirtualAppliance virtualApp = entity.getVirtualAppliance();
        StoragePool storagePool = entity.getStoragePool();

        vImageGen.addAuxiliaryEntitiesToPersist(vimage, entitiesToPersist);
        entitiesToPersist.add(vimage);

        // vAppGen.addAuxiliaryEntitiesToPersist(virtualApp, entitiesToPersist);
        // entitiesToPersist.add(virtualApp);

        storagePoolGen.addAuxiliaryEntitiesToPersist(storagePool, entitiesToPersist);
        entitiesToPersist.add(storagePool);
    }

}
