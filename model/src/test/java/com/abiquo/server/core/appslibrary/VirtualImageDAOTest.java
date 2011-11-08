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

public class VirtualImageDAOTest extends DefaultDAOTestBase<VirtualImageDAO, VirtualImage>
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
    protected VirtualImageDAO createDao(final EntityManager entityManager)
    {
        return new VirtualImageDAO(entityManager);
    }

    @Override
    protected PersistentInstanceTester<VirtualImage> createEntityInstanceGenerator()
    {
        return new VirtualImageGenerator(getSeed());
    }

    @Override
    protected EntityManagerFactoryForTesting getFactory()
    {
        return TestDataAccessManager.getFactory();
    }

    @Override
    public VirtualImageGenerator eg()
    {
        return (VirtualImageGenerator) super.eg();
    }

    @Test
    public void testFindVirtualImagesByEnterprise()
    {
        Enterprise ent = enterpriseGenerator.createUniqueInstance();
        VirtualImage vi = eg().createInstance(ent);

        List<Object> entitiesToPersist = new ArrayList<Object>();
        eg().addAuxiliaryEntitiesToPersist(vi, entitiesToPersist);
        persistAll(ds(), entitiesToPersist, vi);

        VirtualImageDAO dao = createDaoForRollbackTransaction();
        List<VirtualImage> images = dao.findByEnterprise(ent);

        assertEquals(images.size(), 1);
    }

    @Test
    public void testFindVirtualImagesByEnterpriseAndRepository()
    {
        Enterprise ent = enterpriseGenerator.createUniqueInstance();
        Repository repo = repositoryGenerator.createUniqueInstance();
        VirtualImage vi = eg().createInstance(ent, repo);

        List<Object> entitiesToPersist = new ArrayList<Object>();
        eg().addAuxiliaryEntitiesToPersist(vi, entitiesToPersist);
        persistAll(ds(), entitiesToPersist, vi);

        VirtualImageDAO dao = createDaoForRollbackTransaction();
        List<VirtualImage> images = dao.findByEnterpriseAndRepository(ent, repo);

        assertEquals(images.size(), 1);
    }

    @Test
    public void testFindVirtualImageByName()
    {
        VirtualImage vi = eg().createUniqueInstance();

        List<Object> entitiesToPersist = new ArrayList<Object>();
        eg().addAuxiliaryEntitiesToPersist(vi, entitiesToPersist);
        persistAll(ds(), entitiesToPersist, vi);

        VirtualImageDAO dao = createDaoForRollbackTransaction();
        VirtualImage image = dao.findByName(vi.getName());
        assertNotNull(image);

        image = dao.findByName("UNEXISTING");
        assertNull(image);
    }

    @Test
    public void testFindVirtualImageByPath()
    {
        VirtualImage vi = eg().createUniqueInstance();

        List<Object> entitiesToPersist = new ArrayList<Object>();
        eg().addAuxiliaryEntitiesToPersist(vi, entitiesToPersist);
        persistAll(ds(), entitiesToPersist, vi);

        VirtualImageDAO dao = createDaoForRollbackTransaction();
        VirtualImage image = dao.findByPath(vi.getEnterprise(), vi.getRepository(), vi.getPath());
        assertNotNull(image);

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
        VirtualImage vi = eg().createUniqueInstance();

        List<Object> entitiesToPersist = new ArrayList<Object>();
        eg().addAuxiliaryEntitiesToPersist(vi, entitiesToPersist);
        persistAll(ds(), entitiesToPersist, vi);

        VirtualImageDAO dao = createDaoForRollbackTransaction();
        boolean exists =
            dao.existWithSamePath(vi.getEnterprise(), vi.getRepository(), vi.getPath());
        assertTrue(exists);

        exists = dao.existWithSamePath(vi.getEnterprise(), vi.getRepository(), "UNEXISTING");
        assertFalse(exists);
    }


    /** Virtual Image hypervisor compatible. */

    @Test
    public void testCompatibles_NoCompatible_NoConversions()
    {
        Enterprise ent = enterpriseGenerator.createUniqueInstance();
        Repository repo = repositoryGenerator.createUniqueInstance();
        VirtualImage vi1 =
            eg().createInstance(ent, repo, DiskFormatType.VMDK_FLAT, "compatible-vmx");

        List<Object> entitiesToPersist = new ArrayList<Object>();
        eg().addAuxiliaryEntitiesToPersist(vi1, entitiesToPersist);
        persistAll(ds(), entitiesToPersist, vi1);

        VirtualImageDAO dao = createDaoForRollbackTransaction();

        List<VirtualImage> compatiblesVbox = dao.findBy(ent, repo, null, HypervisorType.VBOX);

        assertEquals(compatiblesVbox.size(), 0);
    }

    @Test
    public void testCompatibles_NoCompatible_ConversionCompatible()
    {
        Enterprise ent = enterpriseGenerator.createUniqueInstance();
        Repository repo = repositoryGenerator.createUniqueInstance();
        VirtualImage vi1 =
            eg().createInstance(ent, repo, DiskFormatType.VMDK_FLAT, "compatible-vmx");

        VirtualImageConversion conversion1 =
            conversionGenerator.createInstance(vi1, DiskFormatType.RAW);
        VirtualImageConversion conversion2 =
            conversionGenerator.createInstance(vi1, DiskFormatType.VDI_FLAT);// <-- compatible

        List<Object> entitiesToPersist = new ArrayList<Object>();
        eg().addAuxiliaryEntitiesToPersist(vi1, entitiesToPersist);
        persistAll(ds(), entitiesToPersist, vi1, conversion1, conversion2);

        VirtualImageDAO dao = createDaoForRollbackTransaction();

        List<VirtualImage> compatiblesVbox = dao.findBy(ent, repo, null, HypervisorType.VBOX);

        assertEquals(compatiblesVbox.size(), 1);
        assertEquals(compatiblesVbox.get(0).getName(), "compatible-vmx");
    }

    @Test
    public void testCompatibles_NoCompatible_ConversionCompatible_notFinished()
    {
        Enterprise ent = enterpriseGenerator.createUniqueInstance();
        Repository repo = repositoryGenerator.createUniqueInstance();
        VirtualImage vi1 =
            eg().createInstance(ent, repo, DiskFormatType.VMDK_FLAT, "compatible-vmx");

        VirtualImageConversion conversion1 =
            conversionGenerator.createInstance(vi1, DiskFormatType.RAW);
        VirtualImageConversion conversion2 =
            conversionGenerator.createInstance(vi1, DiskFormatType.VDI_FLAT);// <-- compatible
        conversion2.setState(ConversionState.ENQUEUED);

        List<Object> entitiesToPersist = new ArrayList<Object>();
        eg().addAuxiliaryEntitiesToPersist(vi1, entitiesToPersist);
        persistAll(ds(), entitiesToPersist, vi1, conversion1, conversion2);

        VirtualImageDAO dao = createDaoForRollbackTransaction();

        List<VirtualImage> compatiblesVbox = dao.findBy(ent, repo, null, HypervisorType.VBOX);

        assertEquals(compatiblesVbox.size(), 0);
    }

    @Test
    public void testCompatibles_NoCompatible_ConversionNoCompatible()
    {
        Enterprise ent = enterpriseGenerator.createUniqueInstance();
        Repository repo = repositoryGenerator.createUniqueInstance();
        VirtualImage vi1 =
            eg().createInstance(ent, repo, DiskFormatType.VMDK_FLAT, "compatible-vmx");

        VirtualImageConversion conversion1 =
            conversionGenerator.createInstance(vi1, DiskFormatType.RAW);
        VirtualImageConversion conversion2 =
            conversionGenerator.createInstance(vi1, DiskFormatType.VMDK_STREAM_OPTIMIZED);

        List<Object> entitiesToPersist = new ArrayList<Object>();
        eg().addAuxiliaryEntitiesToPersist(vi1, entitiesToPersist);
        persistAll(ds(), entitiesToPersist, vi1, conversion1, conversion2);

        VirtualImageDAO dao = createDaoForRollbackTransaction();

        List<VirtualImage> compatiblesVbox = dao.findBy(ent, repo, null, HypervisorType.VBOX);

        assertEquals(compatiblesVbox.size(), 0);
    }

    @Test
    public void testCompatibles_Compatible_ConversionCompatible()
    {
        Enterprise ent = enterpriseGenerator.createUniqueInstance();
        Repository repo = repositoryGenerator.createUniqueInstance();
        VirtualImage vi1 =
            eg().createInstance(ent, repo, DiskFormatType.VDI_SPARSE, "compatible-vmx");

        VirtualImageConversion conversion1 =
            conversionGenerator.createInstance(vi1, DiskFormatType.VDI_FLAT);

        List<Object> entitiesToPersist = new ArrayList<Object>();
        eg().addAuxiliaryEntitiesToPersist(vi1, entitiesToPersist);
        persistAll(ds(), entitiesToPersist, vi1, conversion1);

        VirtualImageDAO dao = createDaoForRollbackTransaction();

        List<VirtualImage> compatiblesVbox = dao.findBy(ent, repo, null, HypervisorType.VBOX);

        assertEquals(compatiblesVbox.size(), 1);
        assertEquals(compatiblesVbox.get(0).getName(), "compatible-vmx");
    }

    @Test
    public void testCompatibles_Compatible_ConversionCompatible_notFinished()
    {
        Enterprise ent = enterpriseGenerator.createUniqueInstance();
        Repository repo = repositoryGenerator.createUniqueInstance();
        VirtualImage vi1 =
            eg().createInstance(ent, repo, DiskFormatType.VDI_SPARSE, "compatible-vmx");

        VirtualImageConversion conversion1 =
            conversionGenerator.createInstance(vi1, DiskFormatType.VDI_FLAT);
        conversion1.setState(ConversionState.ENQUEUED);

        List<Object> entitiesToPersist = new ArrayList<Object>();
        eg().addAuxiliaryEntitiesToPersist(vi1, entitiesToPersist);
        persistAll(ds(), entitiesToPersist, vi1, conversion1);

        VirtualImageDAO dao = createDaoForRollbackTransaction();

        List<VirtualImage> compatiblesVbox = dao.findBy(ent, repo, null, HypervisorType.VBOX);

        assertEquals(compatiblesVbox.size(), 1);
        assertEquals(compatiblesVbox.get(0).getName(), "compatible-vmx");
    }

    @Test
    public void testCompatibles_Compatible_NoConversions()
    {
        Enterprise ent = enterpriseGenerator.createUniqueInstance();
        Repository repo = repositoryGenerator.createUniqueInstance();
        VirtualImage vi1 =
            eg().createInstance(ent, repo, DiskFormatType.VDI_FLAT, "compatible-vbox");

        List<Object> entitiesToPersist = new ArrayList<Object>();
        eg().addAuxiliaryEntitiesToPersist(vi1, entitiesToPersist);
        persistAll(ds(), entitiesToPersist, vi1, vi1);

        VirtualImageDAO dao = createDaoForRollbackTransaction();

        List<VirtualImage> compatiblesVbox = dao.findBy(ent, repo, null, HypervisorType.VBOX);

        assertEquals(compatiblesVbox.size(), 1);
        assertEquals(compatiblesVbox.get(0).getName(), "compatible-vbox");
    }

    @Test
    public void testCompatibles_Compatible_ConversionNoCompatible()
    {
        Enterprise ent = enterpriseGenerator.createUniqueInstance();
        Repository repo = repositoryGenerator.createUniqueInstance();
        VirtualImage vi1 =
            eg().createInstance(ent, repo, DiskFormatType.VDI_FLAT, "compatible-vbox");

        VirtualImageConversion conversion1 =
            conversionGenerator.createInstance(vi1, DiskFormatType.VMDK_STREAM_OPTIMIZED);

        List<Object> entitiesToPersist = new ArrayList<Object>();
        eg().addAuxiliaryEntitiesToPersist(vi1, entitiesToPersist);
        persistAll(ds(), entitiesToPersist, vi1, conversion1);

        VirtualImageDAO dao = createDaoForRollbackTransaction();

        List<VirtualImage> compatiblesVbox = dao.findBy(ent, repo, null, HypervisorType.VBOX);

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

        VirtualImageDAO dao = createDaoForRollbackTransaction();
        List<VirtualImage> images = dao.findStatefuls();
        assertEquals(images.size(), 1);
    }

    @Test
    public void testFindStatefulsWithoutResults()
    {
        VirtualImage vi = eg().createUniqueInstance();

        List<Object> entitiesToPersist = new ArrayList<Object>();
        eg().addAuxiliaryEntitiesToPersist(vi, entitiesToPersist);
        persistAll(ds(), entitiesToPersist, vi);

        VirtualImageDAO dao = createDaoForRollbackTransaction();
        List<VirtualImage> images = dao.findStatefuls();
        assertEquals(images.size(), 0);
    }

    @Test
    public void testFindStatefulsByDatacenter()
    {
        VolumeManagement statefulVolume = volumeGenerator.createStatefulInstance();

        List<Object> entitiesToPersist = new ArrayList<Object>();
        volumeGenerator.addAuxiliaryEntitiesToPersist(statefulVolume, entitiesToPersist);
        persistAll(ds(), entitiesToPersist, statefulVolume);

        Datacenter datacenter = statefulVolume.getStoragePool().getDevice().getDatacenter();

        VirtualImageDAO dao = createDaoForRollbackTransaction();
        List<VirtualImage> images = dao.findStatefulsByDatacenter(datacenter);
        assertEquals(images.size(), 1);
    }

    @Test
    public void testFindStatefulsByDatacenterWithoutResults()
    {
        VolumeManagement statefulVolume = volumeGenerator.createStatefulInstance();
        Datacenter anotherDatacenter = datacenterGenerator.createUniqueInstance();

        List<Object> entitiesToPersist = new ArrayList<Object>();
        volumeGenerator.addAuxiliaryEntitiesToPersist(statefulVolume, entitiesToPersist);
        persistAll(ds(), entitiesToPersist, statefulVolume, anotherDatacenter);

        VirtualImageDAO dao = createDaoForRollbackTransaction();
        List<VirtualImage> images = dao.findStatefulsByDatacenter(anotherDatacenter);
        assertEquals(images.size(), 0);
    }

    @Test
    public void testFindStatefulsByCategoryAndDatacenter()
    {
        VolumeManagement statefulVolume = volumeGenerator.createStatefulInstance();

        List<Object> entitiesToPersist = new ArrayList<Object>();
        volumeGenerator.addAuxiliaryEntitiesToPersist(statefulVolume, entitiesToPersist);
        persistAll(ds(), entitiesToPersist, statefulVolume);

        Datacenter datacenter = statefulVolume.getStoragePool().getDevice().getDatacenter();
        Category category = statefulVolume.getVirtualImage().getCategory();

        VirtualImageDAO dao = createDaoForRollbackTransaction();
        List<VirtualImage> images = dao.findStatefulsByCategoryAndDatacenter(category, datacenter);
        assertEquals(images.size(), 1);
    }

    @Test
    public void testFindStatefulsByCategoryAndDatacenterWithDifferentDatacenter()
    {
        VolumeManagement statefulVolume = volumeGenerator.createStatefulInstance();
        Datacenter anotherDatacenter = datacenterGenerator.createUniqueInstance();

        List<Object> entitiesToPersist = new ArrayList<Object>();
        volumeGenerator.addAuxiliaryEntitiesToPersist(statefulVolume, entitiesToPersist);
        persistAll(ds(), entitiesToPersist, statefulVolume, anotherDatacenter);

        Category category = statefulVolume.getVirtualImage().getCategory();

        VirtualImageDAO dao = createDaoForRollbackTransaction();
        List<VirtualImage> images =
            dao.findStatefulsByCategoryAndDatacenter(category, anotherDatacenter);
        assertEquals(images.size(), 0);
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

        VirtualImageDAO dao = createDaoForRollbackTransaction();
        List<VirtualImage> images =
            dao.findStatefulsByCategoryAndDatacenter(anotherCategory, datacenter);
        assertEquals(images.size(), 0);
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

        VirtualImageDAO dao = createDaoForRollbackTransaction();
        List<VirtualImage> images =
            dao.findStatefulsByCategoryAndDatacenter(anotherCategory, anotherDatacenter);
        assertEquals(images.size(), 0);
    }
}
