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
import com.abiquo.server.core.infrastructure.storage.VolumeManagement;
import com.softwarementors.commons.test.SeedGenerator;
import com.softwarementors.commons.testng.AssertEx;

public class VirtualMachineTemplateGenerator extends DefaultEntityGenerator<VirtualMachineTemplate>
{
    private final EnterpriseGenerator enterpriseGenerator;

    private final RepositoryGenerator repositoryGenerator;

    private final CategoryGenerator categoryGenerator;

    private final DatacenterGenerator datacenterGenerator;

    public VirtualMachineTemplateGenerator(final SeedGenerator seed)
    {
        super(seed);
        enterpriseGenerator = new EnterpriseGenerator(seed);
        repositoryGenerator = new RepositoryGenerator(seed);
        categoryGenerator = new CategoryGenerator(seed);
        datacenterGenerator = new DatacenterGenerator(seed);
    }

    @Override
    public void assertAllPropertiesEqual(final VirtualMachineTemplate img1,
        final VirtualMachineTemplate img2)
    {
        // Properties
        AssertEx.assertPropertiesEqualSilent(img1, img2,
            VirtualMachineTemplate.DISKFORMAT_TYPE_PROPERTY, VirtualMachineTemplate.NAME_PROPERTY,
            VirtualMachineTemplate.STATEFUL_PROPERTY, VirtualMachineTemplate.CPU_REQUIRED_PROPERTY,
            VirtualMachineTemplate.PATH_PROPERTY, VirtualMachineTemplate.OVFID_PROPERTY,
            VirtualMachineTemplate.RAM_REQUIRED_PROPERTY,
            VirtualMachineTemplate.HD_REQUIRED_PROPERTY,
            VirtualMachineTemplate.DISK_FILE_SIZE_PROPERTY,
            VirtualMachineTemplate.DESCRIPTION_PROPERTY, VirtualMachineTemplate.SHARED_PROPERTY,
            VirtualMachineTemplate.COST_CODE_PROPERTY, VirtualMachineTemplate.ICON_URL_PROPERTY);

        // Required relationships
        enterpriseGenerator.assertAllPropertiesEqual(img1.getEnterprise(), img2.getEnterprise());
        categoryGenerator.assertAllPropertiesEqual(img1.getCategory(), img2.getCategory());

        // Optional relationships
        if (img1.getRepository() != null)
        {
            repositoryGenerator
                .assertAllPropertiesEqual(img1.getRepository(), img2.getRepository());
        }
        if (img1.getMaster() != null)
        {
            assertAllPropertiesEqual(img1.getMaster(), img2.getMaster());
        }
    }

    @Override
    public VirtualMachineTemplate createUniqueInstance()
    {
        Enterprise enterprise = enterpriseGenerator.createUniqueInstance();
        return createInstance(enterprise);
    }

    public VirtualMachineTemplate createInstance(final Enterprise enterprise)
    {
        Datacenter datacenter = datacenterGenerator.createUniqueInstance();
        return createInstance(enterprise, datacenter);
    }

    public VirtualMachineTemplate createInstance(final Datacenter datacenter)
    {
        Enterprise enterprise = enterpriseGenerator.createUniqueInstance();
        return createInstance(enterprise, datacenter);
    }

    public VirtualMachineTemplate createInstance(final Enterprise enterprise,
        final Datacenter datacenter)
    {
        Repository repository = repositoryGenerator.createInstance(datacenter);
        return createInstance(enterprise, repository);
    }

    public VirtualMachineTemplate createInstance(final Enterprise enterprise,
        final Repository repository)
    {
        Category category = categoryGenerator.createUniqueInstance();
        return createInstance(enterprise, repository, category);
    }

    public VirtualMachineTemplate createInstance(final Enterprise enterprise,
        final Repository repository, final DiskFormatType baseType, final String name)
    {
        Category category = categoryGenerator.createUniqueInstance();

        return createInstance(enterprise, repository, 0, 0, 0, name, category, baseType);
    }

    public VirtualMachineTemplate createInstance(final Enterprise enterprise,
        final Repository repository, final Category category)
    {
        final String name =
            newString(nextSeed(), VirtualMachineTemplate.NAME_LENGTH_MIN,
                VirtualMachineTemplate.NAME_LENGTH_MAX);

        return createInstance(enterprise, repository, 0, 0, 0, name, category, DiskFormatType.RAW);
    }

    public VirtualMachineTemplate createInstance(final Enterprise enterprise,
        final Repository repository, final int cpuRequired, final int ramRequired,
        final long hdRequired, final String name)
    {
        Category category = categoryGenerator.createUniqueInstance();
        return createInstance(enterprise, repository, cpuRequired, ramRequired, hdRequired, name,
            category, DiskFormatType.RAW);
    }

    protected VirtualMachineTemplate createInstance(final Enterprise enterprise,
        final Repository repository, final int cpuRequired, final int ramRequired,
        final long hdRequired, final String name, final Category category,
        final DiskFormatType diskFormat)
    {
        Long diskFileSize = newBigDecimal(nextSeed()).longValue();
        final String pathName =
            newString(nextSeed(), VirtualMachineTemplate.PATH_LENGTH_MIN,
                VirtualMachineTemplate.PATH_LENGTH_MAX);
        String ovfid =
            newString(nextSeed(), VirtualMachineTemplate.OVFID_LENGTH_MIN,
                VirtualMachineTemplate.OVFID_LENGTH_MAX);
        String creationUser =
            newString(nextSeed(), VirtualMachineTemplate.CREATION_USER_LENGTH_MIN,
                VirtualMachineTemplate.CREATION_USER_LENGTH_MAX);

        String iconUrl = "http://validiconurl.com/icon.jpg";
        VirtualMachineTemplate vtemplate =
            new VirtualMachineTemplate(enterprise,
                name,
                diskFormat,
                pathName,
                diskFileSize,
                category,
                "dummyuser");

        vtemplate.setRepository(repository);
        vtemplate.setCpuRequired(cpuRequired);
        vtemplate.setRamRequired(ramRequired);
        vtemplate.setHdRequiredInBytes(hdRequired);
        vtemplate.setOvfid(ovfid);
        vtemplate.setCreationUser(creationUser);
        vtemplate.setIconUrl(iconUrl);

        return vtemplate;
    }

    public VirtualMachineTemplate createInstanceGenericISCSI(final Enterprise enterprise,
        final Datacenter datacenter, final VolumeManagement volman)
    {
        Repository repository = repositoryGenerator.createInstance(datacenter);
        Category category = categoryGenerator.createUniqueInstance();

        final String name =
            newString(nextSeed(), VirtualMachineTemplate.NAME_LENGTH_MIN,
                VirtualMachineTemplate.NAME_LENGTH_MAX);
        Long diskFileSize = newBigDecimal(nextSeed()).longValue();
        final String pathName =
            newString(nextSeed(), VirtualMachineTemplate.PATH_LENGTH_MIN,
                VirtualMachineTemplate.PATH_LENGTH_MAX);
        String ovfid =
            newString(nextSeed(), VirtualMachineTemplate.OVFID_LENGTH_MIN,
                VirtualMachineTemplate.OVFID_LENGTH_MAX);
        String creationUser =
            newString(nextSeed(), VirtualMachineTemplate.CREATION_USER_LENGTH_MIN,
                VirtualMachineTemplate.CREATION_USER_LENGTH_MAX);

        VirtualMachineTemplate vi =
            new VirtualMachineTemplate(enterprise,
                name,
                DiskFormatType.RAW,
                pathName,
                diskFileSize,
                category,
                "dummyuser",
                volman);

        vi.setRepository(repository);
        vi.setCpuRequired(0);
        vi.setRamRequired(0);
        vi.setHdRequiredInBytes(0);
        vi.setOvfid(ovfid);
        vi.setCreationUser(creationUser);

        return vi;
    }

    public VirtualMachineTemplate createSlaveVirtualMachineTemplate(
        final VirtualMachineTemplate master)
    {
        VirtualMachineTemplate slave =
            createInstance(master.getEnterprise(), master.getRepository(), master.getCategory());
        slave.setMaster(master);
        return slave;
    }

    public VirtualMachineTemplate createSlavePersistent(final VirtualMachineTemplate master,
        final VolumeManagement volman)
    {
        VirtualMachineTemplate slave =
            createInstanceGenericISCSI(master.getEnterprise(), master.getRepository()
                .getDatacenter(), volman);
        slave.setMaster(master);
        slave.setCategory(master.getCategory());
        slave.setRepository(master.getRepository());
        return slave;
    }

    public VirtualMachineTemplate createVirtualMachineTemplateWithConversions(
        final Enterprise enterprise)
    {
        VirtualMachineTemplate template = createInstance(enterprise);
        VirtualImageConversion conversion =
            new VirtualImageConversion(template, DiskFormatType.RAW, newString(nextSeed(), 0, 255));
        template.addConversion(conversion);

        return template;
    }

    @Override
    public void addAuxiliaryEntitiesToPersist(final VirtualMachineTemplate entity,
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
            VirtualMachineTemplate master = entity.getMaster();
            // Take care of recursion here
            addAuxiliaryEntitiesToPersist(master, entitiesToPersist);
            entitiesToPersist.add(master);
        }
    }

}
