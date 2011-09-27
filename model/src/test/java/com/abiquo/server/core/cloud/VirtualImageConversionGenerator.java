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

import com.abiquo.model.enumerator.DiskFormatType;
import com.abiquo.server.core.common.DefaultEntityGenerator;
import com.abiquo.server.core.enterprise.Enterprise;
import com.abiquo.server.core.enterprise.EnterpriseGenerator;
import com.softwarementors.bzngine.entities.PersistentEntity;
import com.softwarementors.bzngine.entities.PersistentVersionedEntityBase;
import com.softwarementors.commons.test.SeedGenerator;
import com.softwarementors.commons.testng.AssertEx;

public class VirtualImageConversionGenerator extends DefaultEntityGenerator<VirtualImageConversion>
{
    private VirtualImageGenerator virtualImageGenerator;

    private EnterpriseGenerator enterpriseGenerator;

    public VirtualImageConversionGenerator(final SeedGenerator seed)
    {
        super(seed);
        virtualImageGenerator = new VirtualImageGenerator(seed);
        enterpriseGenerator = new EnterpriseGenerator(seed);
    }

    @Override
    public void assertAllPropertiesEqual(final VirtualImageConversion obj1,
        final VirtualImageConversion obj2)
    {
        AssertEx.assertPropertiesEqualSilent(obj1, obj2, PersistentEntity.ID_PROPERTY,
            PersistentVersionedEntityBase.VERSION_PROPERTY);
    }

    @Override
    public VirtualImageConversion createUniqueInstance()
    {

        Enterprise enterprise = enterpriseGenerator.createUniqueInstance();
        VirtualImage i = virtualImageGenerator.createInstance(enterprise);

        VirtualImageConversion img =
            new VirtualImageConversion(i, DiskFormatType.VMDK_FLAT, "[DEFAULT]");
        img.setSize(i.getDiskFileSize());
        return img;
    }

    public VirtualImageConversion create(final VirtualImage image,
        final DiskFormatType targetFormat, final String targetPath)
    {

        VirtualImageConversion img = new VirtualImageConversion(image, targetFormat, targetPath);
        img.setSize(image.getDiskFileSize());
        return img;
    }

    public VirtualImageConversion create(final VirtualImage image,
        final DiskFormatType targetFormat, final String targetPath,
        final DiskFormatType sourceFormat, final String sourcePath)
    {

        VirtualImageConversion img = new VirtualImageConversion(image, targetFormat, targetPath);
        img.setSourceType(sourceFormat);
        img.setSourcePath(sourcePath);
        img.setSize(image.getDiskFileSize());
        return img;
    }

    @Override
    public void addAuxiliaryEntitiesToPersist(final VirtualImageConversion entity,
        final List<Object> entitiesToPersist)
    {
        super.addAuxiliaryEntitiesToPersist(entity, entitiesToPersist);

        VirtualImage image = entity.getImage();

        entitiesToPersist.add(image);
        Enterprise enterprise = entity.getImage().getEnterprise();
        enterpriseGenerator.addAuxiliaryEntitiesToPersist(enterprise, entitiesToPersist);
        entitiesToPersist.add(enterprise);

    }

}
