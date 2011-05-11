package com.abiquo.server.core.cloud.stateful;

import java.util.Date;
import java.util.List;

import com.abiquo.server.core.cloud.State;
import com.abiquo.server.core.common.DefaultEntityGenerator;
import com.abiquo.server.core.infrastructure.storage.VolumeManagement;
import com.abiquo.server.core.infrastructure.storage.VolumeManagementGenerator;
import com.softwarementors.commons.test.SeedGenerator;
import com.softwarementors.commons.testng.AssertEx;

public class DiskStatefulConversionGenerator extends DefaultEntityGenerator<DiskStatefulConversion>
{
    VolumeManagementGenerator volumeManagementGenerator;

    public DiskStatefulConversionGenerator(final SeedGenerator seed)
    {
        super(seed);

        volumeManagementGenerator = new VolumeManagementGenerator(seed);
    }

    @Override
    public void assertAllPropertiesEqual(final DiskStatefulConversion obj1,
        final DiskStatefulConversion obj2)
    {
        AssertEx.assertPropertiesEqualSilent(obj1, obj2, DiskStatefulConversion.TIMESTAMP_PROPERTY,
            DiskStatefulConversion.IMAGE_PATH_PROPERTY, DiskStatefulConversion.ID_PROPERTY,
            DiskStatefulConversion.STATE_PROPERTY, DiskStatefulConversion.MANAGEMENT_PROPERTY);
    }

    @Override
    public DiskStatefulConversion createUniqueInstance()
    {
        String imagePath = newString(nextSeed(), 0, 255);
        VolumeManagement volume = volumeManagementGenerator.createUniqueInstance();
        State state = newEnum(State.class, nextSeed());
        Date timestamp = newDateTime(nextSeed()).toDate();

        DiskStatefulConversion diskStatefulConversion =
            new DiskStatefulConversion(imagePath, volume, state, timestamp);

        return diskStatefulConversion;
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
