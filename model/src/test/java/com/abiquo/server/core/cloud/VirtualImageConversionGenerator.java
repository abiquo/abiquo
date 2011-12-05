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

package com.abiquo.server.core.cloud;

import java.util.List;

import com.abiquo.model.enumerator.ConversionState;
import com.abiquo.model.enumerator.DiskFormatType;
import com.abiquo.server.core.appslibrary.VirtualMachineTemplate;
import com.abiquo.server.core.appslibrary.VirtualImageConversion;
import com.abiquo.server.core.appslibrary.VirtualMachineTemplateGenerator;
import com.abiquo.server.core.common.DefaultEntityGenerator;
import com.softwarementors.commons.test.SeedGenerator;
import com.softwarementors.commons.testng.AssertEx;

public class VirtualImageConversionGenerator extends DefaultEntityGenerator<VirtualImageConversion>
{
    private VirtualMachineTemplateGenerator virtualImageGenerator;

    public VirtualImageConversionGenerator(final SeedGenerator seed)
    {
        super(seed);
        virtualImageGenerator = new VirtualMachineTemplateGenerator(seed);
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
        VirtualMachineTemplate virtualImage = virtualImageGenerator.createUniqueInstance();

        VirtualImageConversion virtualImageConversion =
            new VirtualImageConversion(virtualImage, DiskFormatType.UNKNOWN, newString(nextSeed(),
                VirtualImageConversion.TARGET_PATH_LENGTH_MIN,
                VirtualImageConversion.TARGET_PATH_LENGTH_MAX));

        return virtualImageConversion;
    }

    /** FINISHED */
    public VirtualImageConversion createInstance(final VirtualMachineTemplate vimage,
        final DiskFormatType targetFormat)
    {
        VirtualImageConversion virtualImageConversion =
            new VirtualImageConversion(vimage, targetFormat, newString(nextSeed(),
                VirtualImageConversion.TARGET_PATH_LENGTH_MIN,
                VirtualImageConversion.TARGET_PATH_LENGTH_MAX));

        virtualImageConversion.setState(ConversionState.FINISHED);
        return virtualImageConversion;
    }

    @Override
    public void addAuxiliaryEntitiesToPersist(final VirtualImageConversion entity,
        final List<Object> entitiesToPersist)
    {
        super.addAuxiliaryEntitiesToPersist(entity, entitiesToPersist);

        VirtualMachineTemplate virtualImage = entity.getVirtualMachineTemplate();
        virtualImageGenerator.addAuxiliaryEntitiesToPersist(virtualImage, entitiesToPersist);
        entitiesToPersist.add(virtualImage);
    }

}
