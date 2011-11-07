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

package com.abiquo.server.core.appslibrary;

import java.util.List;

import com.abiquo.model.enumerator.DiskFormatType;
import com.abiquo.server.core.common.DefaultEntityGenerator;
import com.abiquo.server.core.enterprise.Enterprise;
import com.abiquo.server.core.enterprise.EnterpriseGenerator;
import com.abiquo.server.core.infrastructure.Datacenter;
import com.abiquo.server.core.infrastructure.DatacenterGenerator;
import com.abiquo.server.core.infrastructure.Repository;
import com.abiquo.server.core.infrastructure.RepositoryGenerator;
import com.abiquo.server.core.infrastructure.storage.VolumeManagementGenerator;
import com.softwarementors.commons.test.SeedGenerator;
import com.softwarementors.commons.testng.AssertEx;

public class VirtualImageGenerator extends DefaultEntityGenerator<VirtualImage>
{
    private EnterpriseGenerator enterpriseGenerator;

    private RepositoryGenerator repositoryGenerator;

    private CategoryGenerator categoryGenerator;

    private IconGenerator iconGenerator;

    private DatacenterGenerator datacenterGenerator;

    private VolumeManagementGenerator volumeGenerator;;

    public VirtualImageGenerator(final SeedGenerator seed)
    {
        super(seed);
        enterpriseGenerator = new EnterpriseGenerator(seed);
        repositoryGenerator = new RepositoryGenerator(seed);
        categoryGenerator = new CategoryGenerator(seed);
        iconGenerator = new IconGenerator(seed);
        datacenterGenerator = new DatacenterGenerator(seed);
    }

    @Override
    public void assertAllPropertiesEqual(final VirtualImage img1, final VirtualImage img2)
    {
        // Properties
        AssertEx.assertPropertiesEqualSilent(img1, img2, VirtualImage.DISKFORMAT_TYPE_PROPERTY,
            VirtualImage.NAME_PROPERTY, VirtualImage.STATEFUL_PROPERTY,
            VirtualImage.CPU_REQUIRED_PROPERTY, VirtualImage.PATH_PROPERTY,
            VirtualImage.OVFID_PROPERTY, VirtualImage.RAM_REQUIRED_PROPERTY,
            VirtualImage.HD_REQUIRED_PROPERTY, VirtualImage.DISK_FILE_SIZE_PROPERTY,
            VirtualImage.DESCRIPTION_PROPERTY, VirtualImage.SHARED_PROPERTY,
            VirtualImage.COST_CODE_PROPERTY);

        // Required relationships
        enterpriseGenerator.assertAllPropertiesEqual(img1.getEnterprise(), img2.getEnterprise());
        categoryGenerator.assertAllPropertiesEqual(img1.getCategory(), img2.getCategory());

        // Optional relationships
        if (img1.getRepository() != null)
        {
            repositoryGenerator
                .assertAllPropertiesEqual(img1.getRepository(), img2.getRepository());
        }
        if (img1.getIcon() != null)
        {
            iconGenerator.assertAllPropertiesEqual(img1.getIcon(), img2.getIcon());
        }
        if (img1.getMaster() != null)
        {
            assertAllPropertiesEqual(img1.getMaster(), img2.getMaster());
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
        Datacenter datacenter = datacenterGenerator.createUniqueInstance();
        return createInstance(enterprise, datacenter);
    }

    public VirtualImage createInstance(final Enterprise enterprise, final Datacenter datacenter)
    {
        Repository repository = repositoryGenerator.createInstance(datacenter);
        return createInstance(enterprise, repository);
    }

    public VirtualImage createInstance(final Enterprise enterprise, final Repository repository)
    {
        Category category = categoryGenerator.createUniqueInstance();
        return createInstance(enterprise, repository, category);
    }

    public VirtualImage createInstance(final Enterprise enterprise, final Repository repository,
        final Category category)
    {
        final String name =
            newString(nextSeed(), VirtualImage.NAME_LENGTH_MIN, VirtualImage.NAME_LENGTH_MAX);

        return createInstance(enterprise, repository, 0, 0, 0, name, category);
    }

    public VirtualImage createInstance(final Enterprise enterprise, final Repository repository,
        final int cpuRequired, final int ramRequired, final long hdRequired, final String name)
    {
        Category category = categoryGenerator.createUniqueInstance();
        return createInstance(enterprise, repository, 0, 0, 0, name, category);
    }

    public VirtualImage createInstance(final Enterprise enterprise, final Repository repository,
        final int cpuRequired, final int ramRequired, final long hdRequired, final String name,
        final Category category)
    {
        Long diskFileSize = newBigDecimal(nextSeed()).longValue();
        final String pathName =
            newString(nextSeed(), VirtualImage.PATH_LENGTH_MIN, VirtualImage.PATH_LENGTH_MAX);
        String ovfid =
            newString(nextSeed(), VirtualImage.OVFID_LENGTH_MIN, VirtualImage.OVFID_LENGTH_MAX);
        String creationUser =
            newString(nextSeed(), VirtualImage.CREATION_USER_LENGTH_MIN,
                VirtualImage.CREATION_USER_LENGTH_MAX);

        VirtualImage vimage =
            new VirtualImage(enterprise, name, DiskFormatType.RAW, pathName, diskFileSize, category);

        vimage.setRepository(repository);
        vimage.setCpuRequired(cpuRequired);
        vimage.setRamRequired(ramRequired);
        vimage.setHdRequiredInBytes(hdRequired);
        vimage.setOvfid(ovfid);
        vimage.setCreationUser(creationUser);

        return vimage;
    }

    public VirtualImage createSlaveImage(final VirtualImage master)
    {
        VirtualImage slave =
            createInstance(master.getEnterprise(), master.getRepository(), master.getCategory());
        slave.setMaster(master);
        return slave;
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
