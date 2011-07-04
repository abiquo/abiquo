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

package com.abiquo.server.core.cloud.stateful;

import java.util.Date;
import java.util.List;

import com.abiquo.model.enumerator.ConversionState;
import com.abiquo.server.core.common.DefaultEntityGenerator;
import com.abiquo.server.core.infrastructure.storage.VolumeManagement;
import com.abiquo.server.core.infrastructure.storage.VolumeManagementGenerator;
import com.softwarementors.commons.test.SeedGenerator;
import com.softwarementors.commons.testng.AssertEx;

public class DiskStatefulConversionGenerator extends DefaultEntityGenerator<DiskStatefulConversion>
{
    private VolumeManagementGenerator volumeManagementGenerator;

    public DiskStatefulConversionGenerator(final SeedGenerator seed)
    {
        super(seed);

        volumeManagementGenerator = new VolumeManagementGenerator(seed);
    }

    @Override
    public void assertAllPropertiesEqual(final DiskStatefulConversion obj1,
        final DiskStatefulConversion obj2)
    {
        AssertEx.assertPropertiesEqualSilent(obj1, obj2,
            DiskStatefulConversion.IMAGE_PATH_PROPERTY, DiskStatefulConversion.ID_PROPERTY,
            DiskStatefulConversion.STATE_PROPERTY);

        volumeManagementGenerator.assertAllPropertiesEqual(obj1.getVolume(), obj2.getVolume());
    }

    @Override
    public DiskStatefulConversion createUniqueInstance()
    {
        VolumeManagement volume = volumeManagementGenerator.createUniqueInstance();
        return createInstance(volume);
    }

    public DiskStatefulConversion createInstance(final VolumeManagement volume)
    {
        String imagePath =
            newString(nextSeed(), DiskStatefulConversion.IMAGE_PATH_LENGTH_MIN,
                DiskStatefulConversion.IMAGE_PATH_LENGTH_MAX);

        ConversionState state = newEnum(ConversionState.class, nextSeed());
        Date timestamp = newDateTime(nextSeed()).toDate();

        return new DiskStatefulConversion(imagePath, volume, state, timestamp);
    }

    @Override
    public void addAuxiliaryEntitiesToPersist(final DiskStatefulConversion entity,
        final List<Object> entitiesToPersist)
    {
        super.addAuxiliaryEntitiesToPersist(entity, entitiesToPersist);

        VolumeManagement volume = entity.getVolume();
        volumeManagementGenerator.addAuxiliaryEntitiesToPersist(volume, entitiesToPersist);
        entitiesToPersist.add(volume);
    }

}
