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

import java.util.ArrayList;
import java.util.List;

import javax.persistence.EntityManager;

import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import com.abiquo.model.enumerator.DiskFormatType;
import com.abiquo.server.core.cloud.Hypervisor;
import com.abiquo.server.core.cloud.HypervisorGenerator;
import com.abiquo.server.core.cloud.VirtualImageConversionGenerator;
import com.abiquo.server.core.cloud.VirtualMachine;
import com.abiquo.server.core.cloud.VirtualMachineGenerator;
import com.abiquo.server.core.common.persistence.DefaultDAOTestBase;
import com.abiquo.server.core.common.persistence.TestDataAccessManager;
import com.abiquo.server.core.enterprise.Enterprise;
import com.abiquo.server.core.enterprise.EnterpriseGenerator;
import com.abiquo.server.core.enterprise.Role;
import com.abiquo.server.core.enterprise.RoleGenerator;
import com.abiquo.server.core.enterprise.User;
import com.abiquo.server.core.enterprise.UserGenerator;
import com.abiquo.server.core.infrastructure.Datacenter;
import com.abiquo.server.core.infrastructure.DatacenterGenerator;
import com.abiquo.server.core.infrastructure.Machine;
import com.abiquo.server.core.infrastructure.MachineGenerator;
import com.softwarementors.bzngine.engines.jpa.test.configuration.EntityManagerFactoryForTesting;
import com.softwarementors.bzngine.entities.test.PersistentInstanceTester;

public class VirtualImageConversionDAOTest extends
    DefaultDAOTestBase<VirtualImageConversionDAO, VirtualImageConversion>
{
    private VirtualMachineTemplateGenerator virtualImageGenerator;

    private EnterpriseGenerator enterpriseGenerator;

    private VirtualMachineGenerator virtualMachineGenerator;

    private HypervisorGenerator hypervisorGenerator;

    private MachineGenerator machineGenerator;

    private DatacenterGenerator datacenterGenerator;

    private UserGenerator userGenerator;

    private RoleGenerator roleGenerator;

    @Override
    @BeforeMethod
    protected void methodSetUp()
    {
        super.methodSetUp();
        userGenerator = new UserGenerator(getSeed());
        virtualImageGenerator = new VirtualMachineTemplateGenerator(getSeed());
        enterpriseGenerator = new EnterpriseGenerator(getSeed());
        virtualMachineGenerator = new VirtualMachineGenerator(getSeed());
        hypervisorGenerator = new HypervisorGenerator(getSeed());
        machineGenerator = new MachineGenerator(getSeed());
        datacenterGenerator = new DatacenterGenerator(getSeed());
        roleGenerator = new RoleGenerator(getSeed());
    }

    @Override
    protected VirtualImageConversionDAO createDao(final EntityManager entityManager)
    {
        return new VirtualImageConversionDAO(entityManager);
    }

    @Override
    protected PersistentInstanceTester<VirtualImageConversion> createEntityInstanceGenerator()
    {
        return new VirtualImageConversionGenerator(getSeed());
    }

    @Override
    protected EntityManagerFactoryForTesting getFactory()
    {
        return TestDataAccessManager.getFactory();
    }

    @Override
    public VirtualImageConversionGenerator eg()
    {
        return (VirtualImageConversionGenerator) super.eg();
    }

    @Test(enabled = false)
    public void getUnbundledConversionTest()
    {
        Datacenter datacenter = datacenterGenerator.createUniqueInstance();
        Machine machine = machineGenerator.createMachine(datacenter);
        Enterprise enterprise = enterpriseGenerator.createUniqueInstance();
        Role role = roleGenerator.createInstanceSysAdmin();
        User user = userGenerator.createInstance(enterprise, role);
        VirtualMachineTemplate image =
            virtualImageGenerator.createVirtualMachineTemplateWithConversions(enterprise);
        Hypervisor hypervisor = hypervisorGenerator.createInstance(machine);
        VirtualMachine virtualMachine =
            virtualMachineGenerator.createInstance(image, enterprise, hypervisor, user, "name");
        VirtualImageConversion imageConversion =
            eg().createInstance(image, DiskFormatType.VDI_SPARSE);

        List<Object> entitiesToSetup = new ArrayList<Object>();
        entitiesToSetup.add(datacenter);
        entitiesToSetup.add(enterprise);
        entitiesToSetup.add(role);
        entitiesToSetup.add(user);
        entitiesToSetup.add(machine);
        entitiesToSetup.add(hypervisor);
        entitiesToSetup.add(image);
        entitiesToSetup.add(virtualMachine);

        entitiesToSetup.add(imageConversion);

        ds().persistAll(entitiesToSetup.toArray());
        VirtualImageConversionDAO dao = createDaoForReadWriteTransaction();
        dao.getUnbundledConversion(image, virtualMachine.getHypervisor().getType().baseFormat);
        Assert.assertEquals(DiskFormatType.VDI_SPARSE, virtualMachine.getVirtualMachineTemplate()
            .getDiskFormatType());
    }

    @Test
    public void testIsConverted()
    {
        VirtualImageConversion imageConversion = eg().createUniqueInstance();
        List<Object> entitiesToPersist = new ArrayList<Object>();
        eg().addAuxiliaryEntitiesToPersist(imageConversion, entitiesToPersist);
        persistAll(ds(), entitiesToPersist, imageConversion);

        VirtualImageConversionDAO dao = createDaoForRollbackTransaction();

        assertTrue(dao.isConverted(imageConversion.getVirtualMachineTemplate(),
            imageConversion.getTargetType()));
    }

    @Test
    public void testIsNotConverted()
    {
        VirtualImageConversion imageConversion = eg().createUniqueInstance();

        List<Object> entitiesToPersist = new ArrayList<Object>();
        eg().addAuxiliaryEntitiesToPersist(imageConversion, entitiesToPersist);
        persistAll(ds(), entitiesToPersist);

        VirtualImageConversionDAO dao = createDaoForRollbackTransaction();

        assertFalse(dao.isConverted(imageConversion.getVirtualMachineTemplate(),
            imageConversion.getTargetType()));
    }

    @Test
    public void testExistsDuplicatedConversions()
    {
        VirtualMachineTemplate template = virtualImageGenerator.createUniqueInstance();

        VirtualImageConversion imageConversion =
            eg().createInstance(template, DiskFormatType.VHD_FLAT, DiskFormatType.VMDK_FLAT);

        List<Object> entitiesToPersist = new ArrayList<Object>();

        eg().addAuxiliaryEntitiesToPersist(imageConversion, entitiesToPersist);
        entitiesToPersist.add(imageConversion);
        ds().persistAll(entitiesToPersist.toArray());

        VirtualImageConversionDAO dao = createDaoForRollbackTransaction();

        assertTrue(dao.existDuplicatedConversion(imageConversion));
    }

    @Test
    public void testNotExistsDuplicatedConversionsDiferentTemplate()
    {
        VirtualMachineTemplate template = virtualImageGenerator.createUniqueInstance();
        VirtualMachineTemplate template2 = virtualImageGenerator.createUniqueInstance();

        VirtualImageConversion imageConversion =
            eg().createInstance(template, DiskFormatType.VHD_FLAT, DiskFormatType.VMDK_FLAT);

        // same format diferents template
        VirtualImageConversion imageConversionNotDuplicated =
            eg().createInstance(template2, DiskFormatType.VHD_FLAT, DiskFormatType.VMDK_FLAT);

        List<Object> entitiesToPersist = new ArrayList<Object>();

        eg().addAuxiliaryEntitiesToPersist(imageConversion, entitiesToPersist);
        eg().addAuxiliaryEntitiesToPersist(imageConversionNotDuplicated, entitiesToPersist);
        entitiesToPersist.add(imageConversion);
        ds().persistAll(entitiesToPersist.toArray());

        VirtualImageConversionDAO dao = createDaoForRollbackTransaction();

        assertFalse(dao.existDuplicatedConversion(imageConversionNotDuplicated));
    }

    @Test
    public void testNotExistsDuplicatedConversionsDiferentFormat()
    {
        VirtualMachineTemplate template = virtualImageGenerator.createUniqueInstance();

        VirtualImageConversion imageConversion =
            eg().createInstance(template, DiskFormatType.VHD_FLAT, DiskFormatType.VMDK_FLAT);

        // same format diferents template
        VirtualImageConversion imageConversionNotDuplicated =
            eg().createInstance(template, DiskFormatType.VDI_FLAT, DiskFormatType.VMDK_FLAT);

        List<Object> entitiesToPersist = new ArrayList<Object>();

        eg().addAuxiliaryEntitiesToPersist(imageConversion, entitiesToPersist);
        entitiesToPersist.add(imageConversion);
        ds().persistAll(entitiesToPersist.toArray());

        VirtualImageConversionDAO dao = createDaoForRollbackTransaction();

        assertFalse(dao.existDuplicatedConversion(imageConversionNotDuplicated));
    }
}
