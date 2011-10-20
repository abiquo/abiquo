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
import com.abiquo.server.core.config.Category;
import com.abiquo.server.core.config.CategoryGenerator;
import com.abiquo.server.core.config.Icon;
import com.abiquo.server.core.config.IconGenerator;
import com.abiquo.server.core.enterprise.Enterprise;
import com.abiquo.server.core.enterprise.EnterpriseGenerator;
import com.abiquo.server.core.infrastructure.Repository;
import com.abiquo.server.core.infrastructure.RepositoryGenerator;
import com.softwarementors.commons.test.SeedGenerator;
import com.softwarementors.commons.testng.AssertEx;

public class VirtualImageGenerator extends DefaultEntityGenerator<VirtualImage>
{
    private EnterpriseGenerator enterpriseGenerator;

    private RepositoryGenerator repositoryGenerator;

    private CategoryGenerator categoryGenerator;

    private IconGenerator iconGenerator;

    public VirtualImageGenerator(final SeedGenerator seed)
    {
        super(seed);
        enterpriseGenerator = new EnterpriseGenerator(seed);
        repositoryGenerator = new RepositoryGenerator(seed);
        categoryGenerator = new CategoryGenerator(seed);
        iconGenerator = new IconGenerator(seed);
    }

    @Override
    public void assertAllPropertiesEqual(final VirtualImage obj1, final VirtualImage obj2)
    {
        AssertEx.assertPropertiesEqualSilent(obj1, obj2, VirtualImage.DISKFORMAT_TYPE_PROPERTY,
            VirtualImage.NAME_PROPERTY, VirtualImage.STATEFUL_PROPERTY,
            VirtualImage.CPU_REQUIRED_PROPERTY, VirtualImage.PATH_NAME_PROPERTY,
            VirtualImage.OVFID_PROPERTY, VirtualImage.RAM_REQUIRED_PROPERTY,
            VirtualImage.HD_REQUIRED_PROPERTY, VirtualImage.DISK_FILE_SIZE_PROPERTY,
            VirtualImage.DESCRIPTION_PROPERTY);

        categoryGenerator.assertAllPropertiesEqual(obj1.getCategory(), obj2.getCategory());

        if (obj1.getRepository() != null)
        {
            repositoryGenerator
                .assertAllPropertiesEqual(obj1.getRepository(), obj2.getRepository());
        }
        if (obj1.getIcon() != null)
        {
            iconGenerator.assertAllPropertiesEqual(obj1.getIcon(), obj2.getIcon());
        }
        if (obj1.getMaster() != null)
        {
            assertAllPropertiesEqual(obj1.getMaster(), obj2.getMaster());
        }
    }

    @Override
    public VirtualImage createUniqueInstance()
    {
        Enterprise enterprise = enterpriseGenerator.createUniqueInstance();
        return createInstance(enterprise);
    }

    public VirtualImage createInstance(final Enterprise enterprise)
    {
        final String name =
            newString(nextSeed(), VirtualImage.NAME_LENGTH_MIN, VirtualImage.NAME_LENGTH_MAX);
        Repository repository = repositoryGenerator.createUniqueInstance();

        return createInstance(enterprise, repository, 0, 0, 0, name);
    }

    public VirtualImage createInstance(final Enterprise enterprise, final Repository repository,
        final int cpuRequired, final int ramRequired, final long hdRequired, final String name)
    {
        Category category = categoryGenerator.createUniqueInstance();

        Long diskFileSize = newBigDecimal(nextSeed()).longValue();
        final String pathName =
            newString(nextSeed(), VirtualImage.PATH_NAME_LENGTH_MIN,
                VirtualImage.PATH_NAME_LENGTH_MAX);
        String ovfid =
            newString(nextSeed(), VirtualImage.OVFID_LENGTH_MIN, VirtualImage.OVFID_LENGTH_MAX);

        VirtualImage vimage =
            new VirtualImage(enterprise, name, DiskFormatType.RAW, pathName, diskFileSize, category);

        vimage.setRepository(repository);
        vimage.setCpuRequired(cpuRequired);
        vimage.setRamRequired(ramRequired);
        vimage.setHdRequiredInBytes(hdRequired);
        vimage.setOvfid(ovfid);

        return vimage;
    }

    public VirtualImage createImageWithConversions(final Enterprise enterprise)
    {
        VirtualImage image = createInstance(enterprise);
        VirtualImageConversion conversion =
            new VirtualImageConversion(image, DiskFormatType.RAW, newString(nextSeed(), 0, 255));
        image.addConversion(conversion);

        return image;
    }

    @Override
    public void addAuxiliaryEntitiesToPersist(final VirtualImage entity,
        final List<Object> entitiesToPersist)
    {
        super.addAuxiliaryEntitiesToPersist(entity, entitiesToPersist);

        Enterprise enterprise = entity.getEnterprise();
        enterpriseGenerator.addAuxiliaryEntitiesToPersist(enterprise, entitiesToPersist);
        entitiesToPersist.add(enterprise);

        Category category = entity.getCategory();
        categoryGenerator.addAuxiliaryEntitiesToPersist(category, entitiesToPersist);
        entitiesToPersist.add(category);

        if (entity.getRepository() != null)
        {
            Repository repository = entity.getRepository();
            repositoryGenerator.addAuxiliaryEntitiesToPersist(repository, entitiesToPersist);
            entitiesToPersist.add(repository);
        }

        if (entity.getMaster() != null)
        {
            VirtualImage master = entity.getMaster();
            // Take care of recursion here
            addAuxiliaryEntitiesToPersist(master, entitiesToPersist);
            entitiesToPersist.add(master);
        }

        if (entity.getIcon() != null)
        {
            Icon icon = entity.getIcon();
            iconGenerator.addAuxiliaryEntitiesToPersist(icon, entitiesToPersist);
            entitiesToPersist.add(icon);
        }
    }

}
