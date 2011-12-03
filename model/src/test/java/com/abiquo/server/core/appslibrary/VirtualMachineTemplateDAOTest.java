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
import javax.persistence.NoResultException;

import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import com.abiquo.model.enumerator.ConversionState;
import com.abiquo.model.enumerator.DiskFormatType;
import com.abiquo.model.enumerator.HypervisorType;
import com.abiquo.server.core.cloud.VirtualImageConversionGenerator;
import com.abiquo.server.core.common.persistence.DefaultDAOTestBase;
import com.abiquo.server.core.common.persistence.TestDataAccessManager;
import com.abiquo.server.core.enterprise.Enterprise;
import com.abiquo.server.core.enterprise.EnterpriseGenerator;
import com.abiquo.server.core.infrastructure.Datacenter;
import com.abiquo.server.core.infrastructure.DatacenterGenerator;
import com.abiquo.server.core.infrastructure.Repository;
import com.abiquo.server.core.infrastructure.RepositoryGenerator;
import com.abiquo.server.core.infrastructure.storage.VolumeManagement;
import com.abiquo.server.core.infrastructure.storage.VolumeManagementGenerator;
import com.softwarementors.bzngine.engines.jpa.test.configuration.EntityManagerFactoryForTesting;
import com.softwarementors.bzngine.entities.test.PersistentInstanceTester;

public class VirtualMachineTemplateDAOTest extends DefaultDAOTestBase<VirtualMachineTemplateDAO, VirtualMachineTemplate>
{
    private EnterpriseGenerator enterpriseGenerator;

    private RepositoryGenerator repositoryGenerator;

    private VirtualImageConversionGenerator conversionGenerator;

    private VolumeManagementGenerator volumeGenerator;

    private DatacenterGenerator datacenterGenerator;

    private CategoryGenerator categoryGenerator;

    @Override
    @BeforeMethod
    protected void methodSetUp()
    {
        super.methodSetUp();
        enterpriseGenerator = new EnterpriseGenerator(getSeed());
        repositoryGenerator = new RepositoryGenerator(getSeed());
        conversionGenerator = new VirtualImageConversionGenerator(getSeed());
        volumeGenerator = new VolumeManagementGenerator(getSeed());
        datacenterGenerator = new DatacenterGenerator(getSeed());
        categoryGenerator = new CategoryGenerator(getSeed());
    }

    @Override
    protected VirtualMachineTemplateDAO createDao(final EntityManager entityManager)
    {
        return new VirtualMachineTemplateDAO(entityManager);
    }

    @Override
    protected PersistentInstanceTester<VirtualMachineTemplate> createEntityInstanceGenerator()
    {
        return new VirtualMachineTemplateGenerator(getSeed());
    }

    @Override
    protected EntityManagerFactoryForTesting getFactory()
    {
        return TestDataAccessManager.getFactory();
    }

    @Override
    public VirtualMachineTemplateGenerator eg()
    {
        return (VirtualMachineTemplateGenerator) super.eg();
    }

    @Test
    public void testFindVirtualMachineTemplatesByEnterprise()
    {
        Enterprise ent = enterpriseGenerator.createUniqueInstance();
        VirtualMachineTemplate vi = eg().createInstance(ent);

        List<Object> entitiesToPersist = new ArrayList<Object>();
        eg().addAuxiliaryEntitiesToPersist(vi, entitiesToPersist);
        persistAll(ds(), entitiesToPersist, vi);

        VirtualMachineTemplateDAO dao = createDaoForRollbackTransaction();
        List<VirtualMachineTemplate> templates = dao.findByEnterprise(ent);

        assertEquals(templates.size(), 1);
    }

    @Test
    public void testFindVirtualMachineTemplatesByEnterpriseAndRepository()
    {
        Enterprise ent = enterpriseGenerator.createUniqueInstance();
        Repository repo = repositoryGenerator.createUniqueInstance();
        VirtualMachineTemplate vi = eg().createInstance(ent, repo);

        List<Object> entitiesToPersist = new ArrayList<Object>();
        eg().addAuxiliaryEntitiesToPersist(vi, entitiesToPersist);
        persistAll(ds(), entitiesToPersist, vi);

        VirtualMachineTemplateDAO dao = createDaoForRollbackTransaction();
        List<VirtualMachineTemplate> templatess = dao.findByEnterpriseAndRepository(ent, repo);

        assertEquals(templatess.size(), 1);
    }

    @Test
    public void testFindVirtualMachineTemplateByName()
    {
        VirtualMachineTemplate vi = eg().createUniqueInstance();

        List<Object> entitiesToPersist = new ArrayList<Object>();
        eg().addAuxiliaryEntitiesToPersist(vi, entitiesToPersist);
        persistAll(ds(), entitiesToPersist, vi);

        VirtualMachineTemplateDAO dao = createDaoForRollbackTransaction();
        VirtualMachineTemplate template = dao.findByName(vi.getName());
        assertNotNull(template);

        template = dao.findByName("UNEXISTING");
        assertNull(template);
    }

    @Test
    public void testFindVirtualMachineTemplateByPath()
    {
        VirtualMachineTemplate vi = eg().createUniqueInstance();

        List<Object> entitiesToPersist = new ArrayList<Object>();
        eg().addAuxiliaryEntitiesToPersist(vi, entitiesToPersist);
        persistAll(ds(), entitiesToPersist, vi);

        VirtualMachineTemplateDAO dao = createDaoForRollbackTransaction();
        VirtualMachineTemplate template = dao.findByPath(vi.getEnterprise(), vi.getRepository(), vi.getPath());
        assertNotNull(template);

        try
        {
            dao.findByPath(vi.getEnterprise(), vi.getRepository(), "UNEXISTING");
            fail("findByPath should have failed");
        }
        catch (NoResultException ex)
        {
            // Test succeeds
        }
    }

    @Test
    public void testExistsWithSamePath()
    {
        VirtualMachineTemplate vi = eg().createUniqueInstance();

        List<Object> entitiesToPersist = new ArrayList<Object>();
        eg().addAuxiliaryEntitiesToPersist(vi, entitiesToPersist);
        persistAll(ds(), entitiesToPersist, vi);

        VirtualMachineTemplateDAO dao = createDaoForRollbackTransaction();
        boolean exists =
            dao.existWithSamePath(vi.getEnterprise(), vi.getRepository(), vi.getPath());
        assertTrue(exists);

        exists = dao.existWithSamePath(vi.getEnterprise(), vi.getRepository(), "UNEXISTING");
        assertFalse(exists);
    }

    /** Virtual Machine Template hypervisor compatible. */

    @Test
    public void testCompatibles_NoCompatible_NoConversions()
    {
        Enterprise ent = enterpriseGenerator.createUniqueInstance();
        Repository repo = repositoryGenerator.createUniqueInstance();
        VirtualMachineTemplate vi1 =
            eg().createInstance(ent, repo, DiskFormatType.VMDK_FLAT, "compatible-vmx");

        List<Object> entitiesToPersist = new ArrayList<Object>();
        eg().addAuxiliaryEntitiesToPersist(vi1, entitiesToPersist);
        persistAll(ds(), entitiesToPersist, vi1);

        VirtualMachineTemplateDAO dao = createDaoForRollbackTransaction();

        List<VirtualMachineTemplate> compatiblesVbox = dao.findBy(ent, repo, null, HypervisorType.VBOX);

        assertEquals(compatiblesVbox.size(), 0);
    }

    @Test
    public void testCompatibles_NoCompatible_ConversionCompatible()
    {
        Enterprise ent = enterpriseGenerator.createUniqueInstance();
        Repository repo = repositoryGenerator.createUniqueInstance();
        VirtualMachineTemplate vi1 =
            eg().createInstance(ent, repo, DiskFormatType.VMDK_FLAT, "compatible-vmx");

        VirtualImageConversion conversion1 =
            conversionGenerator.createInstance(vi1, DiskFormatType.RAW);
        VirtualImageConversion conversion2 =
            conversionGenerator.createInstance(vi1, DiskFormatType.VDI_FLAT);// <-- compatible

        List<Object> entitiesToPersist = new ArrayList<Object>();
        eg().addAuxiliaryEntitiesToPersist(vi1, entitiesToPersist);
        persistAll(ds(), entitiesToPersist, vi1, conversion1, conversion2);

        VirtualMachineTemplateDAO dao = createDaoForRollbackTransaction();

        List<VirtualMachineTemplate> compatiblesVbox = dao.findBy(ent, repo, null, HypervisorType.VBOX);

        assertEquals(compatiblesVbox.size(), 1);
        assertEquals(compatiblesVbox.get(0).getName(), "compatible-vmx");
    }

    @Test
    public void testCompatibles_NoCompatible_ConversionCompatible_notFinished()
    {
        Enterprise ent = enterpriseGenerator.createUniqueInstance();
        Repository repo = repositoryGenerator.createUniqueInstance();
        VirtualMachineTemplate vi1 =
            eg().createInstance(ent, repo, DiskFormatType.VMDK_FLAT, "compatible-vmx");

        VirtualImageConversion conversion1 =
            conversionGenerator.createInstance(vi1, DiskFormatType.RAW);
        VirtualImageConversion conversion2 =
            conversionGenerator.createInstance(vi1, DiskFormatType.VDI_FLAT);// <-- compatible
        conversion2.setState(ConversionState.ENQUEUED);

        List<Object> entitiesToPersist = new ArrayList<Object>();
        eg().addAuxiliaryEntitiesToPersist(vi1, entitiesToPersist);
        persistAll(ds(), entitiesToPersist, vi1, conversion1, conversion2);

        VirtualMachineTemplateDAO dao = createDaoForRollbackTransaction();

        List<VirtualMachineTemplate> compatiblesVbox = dao.findBy(ent, repo, null, HypervisorType.VBOX);

        assertEquals(compatiblesVbox.size(), 0);
    }

    @Test
    public void testCompatibles_NoCompatible_ConversionNoCompatible()
    {
        Enterprise ent = enterpriseGenerator.createUniqueInstance();
        Repository repo = repositoryGenerator.createUniqueInstance();
        VirtualMachineTemplate vi1 =
            eg().createInstance(ent, repo, DiskFormatType.VMDK_FLAT, "compatible-vmx");

        VirtualImageConversion conversion1 =
            conversionGenerator.createInstance(vi1, DiskFormatType.RAW);
        VirtualImageConversion conversion2 =
            conversionGenerator.createInstance(vi1, DiskFormatType.VMDK_STREAM_OPTIMIZED);

        List<Object> entitiesToPersist = new ArrayList<Object>();
        eg().addAuxiliaryEntitiesToPersist(vi1, entitiesToPersist);
        persistAll(ds(), entitiesToPersist, vi1, conversion1, conversion2);

        VirtualMachineTemplateDAO dao = createDaoForRollbackTransaction();

        List<VirtualMachineTemplate> compatiblesVbox = dao.findBy(ent, repo, null, HypervisorType.VBOX);

        assertEquals(compatiblesVbox.size(), 0);
    }

    @Test
    public void testCompatibles_Compatible_ConversionCompatible()
    {
        Enterprise ent = enterpriseGenerator.createUniqueInstance();
        Repository repo = repositoryGenerator.createUniqueInstance();
        VirtualMachineTemplate vi1 =
            eg().createInstance(ent, repo, DiskFormatType.VDI_SPARSE, "compatible-vmx");

        VirtualImageConversion conversion1 =
            conversionGenerator.createInstance(vi1, DiskFormatType.VDI_FLAT);

        List<Object> entitiesToPersist = new ArrayList<Object>();
        eg().addAuxiliaryEntitiesToPersist(vi1, entitiesToPersist);
        persistAll(ds(), entitiesToPersist, vi1, conversion1);

        VirtualMachineTemplateDAO dao = createDaoForRollbackTransaction();

        List<VirtualMachineTemplate> compatiblesVbox = dao.findBy(ent, repo, null, HypervisorType.VBOX);

        assertEquals(compatiblesVbox.size(), 1);
        assertEquals(compatiblesVbox.get(0).getName(), "compatible-vmx");
    }

    @Test
    public void testCompatibles_Compatible_ConversionCompatible_notFinished()
    {
        Enterprise ent = enterpriseGenerator.createUniqueInstance();
        Repository repo = repositoryGenerator.createUniqueInstance();
        VirtualMachineTemplate vi1 =
            eg().createInstance(ent, repo, DiskFormatType.VDI_SPARSE, "compatible-vmx");

        VirtualImageConversion conversion1 =
            conversionGenerator.createInstance(vi1, DiskFormatType.VDI_FLAT);
        conversion1.setState(ConversionState.ENQUEUED);

        List<Object> entitiesToPersist = new ArrayList<Object>();
        eg().addAuxiliaryEntitiesToPersist(vi1, entitiesToPersist);
        persistAll(ds(), entitiesToPersist, vi1, conversion1);

        VirtualMachineTemplateDAO dao = createDaoForRollbackTransaction();

        List<VirtualMachineTemplate> compatiblesVbox = dao.findBy(ent, repo, null, HypervisorType.VBOX);

        assertEquals(compatiblesVbox.size(), 1);
        assertEquals(compatiblesVbox.get(0).getName(), "compatible-vmx");
    }

    @Test
    public void testCompatibles_Compatible_NoConversions()
    {
        Enterprise ent = enterpriseGenerator.createUniqueInstance();
        Repository repo = repositoryGenerator.createUniqueInstance();
        VirtualMachineTemplate vi1 =
            eg().createInstance(ent, repo, DiskFormatType.VDI_FLAT, "compatible-vbox");

        List<Object> entitiesToPersist = new ArrayList<Object>();
        eg().addAuxiliaryEntitiesToPersist(vi1, entitiesToPersist);
        persistAll(ds(), entitiesToPersist, vi1, vi1);

        VirtualMachineTemplateDAO dao = createDaoForRollbackTransaction();

        List<VirtualMachineTemplate> compatiblesVbox = dao.findBy(ent, repo, null, HypervisorType.VBOX);

        assertEquals(compatiblesVbox.size(), 1);
        assertEquals(compatiblesVbox.get(0).getName(), "compatible-vbox");
    }

    @Test
    public void testCompatibles_Compatible_ConversionNoCompatible()
    {
        Enterprise ent = enterpriseGenerator.createUniqueInstance();
        Repository repo = repositoryGenerator.createUniqueInstance();
        VirtualMachineTemplate vi1 =
            eg().createInstance(ent, repo, DiskFormatType.VDI_FLAT, "compatible-vbox");

        VirtualImageConversion conversion1 =
            conversionGenerator.createInstance(vi1, DiskFormatType.VMDK_STREAM_OPTIMIZED);

        List<Object> entitiesToPersist = new ArrayList<Object>();
        eg().addAuxiliaryEntitiesToPersist(vi1, entitiesToPersist);
        persistAll(ds(), entitiesToPersist, vi1, conversion1);

        VirtualMachineTemplateDAO dao = createDaoForRollbackTransaction();

        List<VirtualMachineTemplate> compatiblesVbox = dao.findBy(ent, repo, null, HypervisorType.VBOX);

        assertEquals(compatiblesVbox.size(), 1);
        assertEquals(compatiblesVbox.get(0).getName(), "compatible-vbox");
    }

    @Test
    public void testFindStatefuls()
    {
        VolumeManagement statefulVolume = volumeGenerator.createStatefulInstance();

        List<Object> entitiesToPersist = new ArrayList<Object>();
        volumeGenerator.addAuxiliaryEntitiesToPersist(statefulVolume, entitiesToPersist);
        persistAll(ds(), entitiesToPersist, statefulVolume);

        VirtualMachineTemplateDAO dao = createDaoForRollbackTransaction();
        List<VirtualMachineTemplate> templates = dao.findStatefuls();
        assertEquals(templates.size(), 1);
    }

    @Test
    public void testFindStatefulsWithoutResults()
    {
        VirtualMachineTemplate vi = eg().createUniqueInstance();

        List<Object> entitiesToPersist = new ArrayList<Object>();
        eg().addAuxiliaryEntitiesToPersist(vi, entitiesToPersist);
        persistAll(ds(), entitiesToPersist, vi);

        VirtualMachineTemplateDAO dao = createDaoForRollbackTransaction();
        List<VirtualMachineTemplate> templates = dao.findStatefuls();
        assertEquals(templates.size(), 0);
    }

    @Test
    public void testFindStatefulsByDatacenter()
    {
        VolumeManagement statefulVolume = volumeGenerator.createStatefulInstance();

        List<Object> entitiesToPersist = new ArrayList<Object>();
        volumeGenerator.addAuxiliaryEntitiesToPersist(statefulVolume, entitiesToPersist);
        persistAll(ds(), entitiesToPersist, statefulVolume);

        Datacenter datacenter = statefulVolume.getStoragePool().getDevice().getDatacenter();

        VirtualMachineTemplateDAO dao = createDaoForRollbackTransaction();
        List<VirtualMachineTemplate> templatess = dao.findStatefulsByDatacenter(datacenter);
        assertEquals(templatess.size(), 1);
    }

    @Test
    public void testFindStatefulsByDatacenterWithoutResults()
    {
        VolumeManagement statefulVolume = volumeGenerator.createStatefulInstance();
        Datacenter anotherDatacenter = datacenterGenerator.createUniqueInstance();

        List<Object> entitiesToPersist = new ArrayList<Object>();
        volumeGenerator.addAuxiliaryEntitiesToPersist(statefulVolume, entitiesToPersist);
        persistAll(ds(), entitiesToPersist, statefulVolume, anotherDatacenter);

        VirtualMachineTemplateDAO dao = createDaoForRollbackTransaction();
        List<VirtualMachineTemplate> templates = dao.findStatefulsByDatacenter(anotherDatacenter);
        assertEquals(templates.size(), 0);
    }

    @Test
    public void testFindStatefulsByCategoryAndDatacenter()
    {
        VolumeManagement statefulVolume = volumeGenerator.createStatefulInstance();

        List<Object> entitiesToPersist = new ArrayList<Object>();
        volumeGenerator.addAuxiliaryEntitiesToPersist(statefulVolume, entitiesToPersist);
        persistAll(ds(), entitiesToPersist, statefulVolume);

        Datacenter datacenter = statefulVolume.getStoragePool().getDevice().getDatacenter();
        Category category = statefulVolume.getVirtualMachineTemplate().getCategory();

        VirtualMachineTemplateDAO dao = createDaoForRollbackTransaction();
        List<VirtualMachineTemplate> templates = dao.findStatefulsByCategoryAndDatacenter(category, datacenter);
        assertEquals(templates.size(), 1);
    }

    @Test
    public void testFindStatefulsByCategoryAndDatacenterWithDifferentDatacenter()
    {
        VolumeManagement statefulVolume = volumeGenerator.createStatefulInstance();
        Datacenter anotherDatacenter = datacenterGenerator.createUniqueInstance();

        List<Object> entitiesToPersist = new ArrayList<Object>();
        volumeGenerator.addAuxiliaryEntitiesToPersist(statefulVolume, entitiesToPersist);
        persistAll(ds(), entitiesToPersist, statefulVolume, anotherDatacenter);

        Category category = statefulVolume.getVirtualMachineTemplate().getCategory();

        VirtualMachineTemplateDAO dao = createDaoForRollbackTransaction();
        List<VirtualMachineTemplate> templates =
            dao.findStatefulsByCategoryAndDatacenter(category, anotherDatacenter);
        assertEquals(templates.size(), 0);
    }

    @Test
    public void testFindStatefulsByCategoryAndDatacenterWithDifferentCategory()
    {
        VolumeManagement statefulVolume = volumeGenerator.createStatefulInstance();
        Category anotherCategory = categoryGenerator.createUniqueInstance();

        List<Object> entitiesToPersist = new ArrayList<Object>();
        volumeGenerator.addAuxiliaryEntitiesToPersist(statefulVolume, entitiesToPersist);
        persistAll(ds(), entitiesToPersist, statefulVolume, anotherCategory);

        Datacenter datacenter = statefulVolume.getStoragePool().getDevice().getDatacenter();

        VirtualMachineTemplateDAO dao = createDaoForRollbackTransaction();
        List<VirtualMachineTemplate> templates =
            dao.findStatefulsByCategoryAndDatacenter(anotherCategory, datacenter);
        assertEquals(templates.size(), 0);
    }

    @Test
    public void testFindStatefulsByCategoryAndDatacenterWithDifferentCategoryAndDatacenter()
    {
        VolumeManagement statefulVolume = volumeGenerator.createStatefulInstance();
        Category anotherCategory = categoryGenerator.createUniqueInstance();
        Datacenter anotherDatacenter = datacenterGenerator.createUniqueInstance();

        List<Object> entitiesToPersist = new ArrayList<Object>();
        volumeGenerator.addAuxiliaryEntitiesToPersist(statefulVolume, entitiesToPersist);
        persistAll(ds(), entitiesToPersist, statefulVolume, anotherCategory, anotherDatacenter);

        VirtualMachineTemplateDAO dao = createDaoForRollbackTransaction();
        List<VirtualMachineTemplate> templates =
            dao.findStatefulsByCategoryAndDatacenter(anotherCategory, anotherDatacenter);
        assertEquals(templates.size(), 0);
    }

    @Test
    public void testCheckAMasterVirtualMachineTemplateIsMaster()
    {
        VirtualMachineTemplate vi = eg().createUniqueInstance();
        VirtualMachineTemplate slave = eg().createSlaveVirtualMachineTemplate(vi);

        List<Object> entitiesToPersist = new ArrayList<Object>();
        eg().addAuxiliaryEntitiesToPersist(vi, entitiesToPersist);
        persistAll(ds(), entitiesToPersist, vi, slave);

        VirtualMachineTemplateDAO dao = createDaoForRollbackTransaction();

        assertTrue(dao.isMaster(vi));
    }

    @Test
    public void testCheckVirtualMachineTemplateIsMaster()
    {
        VirtualMachineTemplate vi = eg().createUniqueInstance();

        List<Object> entitiesToPersist = new ArrayList<Object>();
        eg().addAuxiliaryEntitiesToPersist(vi, entitiesToPersist);
        persistAll(ds(), entitiesToPersist, vi);

        VirtualMachineTemplateDAO dao = createDaoForRollbackTransaction();

        assertFalse(dao.isMaster(vi));
    }

    @Test
    public void testCheckSlaveVirtualMachineTemplateIsMaster()
    {
        VirtualMachineTemplate vi = eg().createUniqueInstance();
        VirtualMachineTemplate slave = eg().createSlaveVirtualMachineTemplate(vi);

        List<Object> entitiesToPersist = new ArrayList<Object>();
        eg().addAuxiliaryEntitiesToPersist(vi, entitiesToPersist);
        persistAll(ds(), entitiesToPersist, vi, slave);

        VirtualMachineTemplateDAO dao = createDaoForRollbackTransaction();

        assertFalse(dao.isMaster(slave));
    }
}
