package com.abiquo.server.core.cloud;

import java.util.List;

import com.abiquo.model.enumerator.DiskFormatType;
import com.abiquo.server.core.common.DefaultEntityGenerator;
import com.softwarementors.commons.test.SeedGenerator;
import com.softwarementors.commons.testng.AssertEx;

public class VirtualImageConversionGenerator extends DefaultEntityGenerator<VirtualImageConversion>
{

    VirtualImageGenerator virtualImageGenerator;

    public VirtualImageConversionGenerator(final SeedGenerator seed)
    {
        super(seed);

        virtualImageGenerator = new VirtualImageGenerator(seed);

    }

    @Override
    public void assertAllPropertiesEqual(final VirtualImageConversion obj1,
        final VirtualImageConversion obj2)
    {
        AssertEx.assertPropertiesEqualSilent(obj1, obj2,
            VirtualImageConversion.SOURCE_TYPE_PROPERTY,
            VirtualImageConversion.TARGET_TYPE_PROPERTY,
            VirtualImageConversion.SOURCE_PATH_PROPERTY,
            VirtualImageConversion.TARGET_PATH_PROPERTY, VirtualImageConversion.STATE_PROPERTY,
            VirtualImageConversion.SIZE_PROPERTY);
    }

    @Override
    public VirtualImageConversion createUniqueInstance()
    {
        // FIXME: Write here how to create the pojo

        VirtualImage virtualImage = virtualImageGenerator.createUniqueInstance();

        VirtualImageConversion virtualImageConversion =
            new VirtualImageConversion(virtualImage, DiskFormatType.UNKNOWN, newString(nextSeed(),
                VirtualImageConversion.TARGET_PATH_LENGTH_MIN,
                VirtualImageConversion.TARGET_PATH_LENGTH_MAX));

        return virtualImageConversion;
    }

    @Override
    public void addAuxiliaryEntitiesToPersist(final VirtualImageConversion entity,
        final List<Object> entitiesToPersist)
    {
        super.addAuxiliaryEntitiesToPersist(entity, entitiesToPersist);

        VirtualImage virtualImage = entity.getVirtualImage();
        virtualImageGenerator.addAuxiliaryEntitiesToPersist(virtualImage, entitiesToPersist);
        entitiesToPersist.add(virtualImage);

    }

}
