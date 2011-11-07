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

    private VolumeManagementGenerator volumeGenerator;

    private DatacenterGenerator datacenterGenerator;

    @Override
    @BeforeMethod
    protected void methodSetUp()
    {
        super.methodSetUp();
        enterpriseGenerator = new EnterpriseGenerator(getSeed());
        repositoryGenerator = new RepositoryGenerator(getSeed());
        volumeGenerator = new VolumeManagementGenerator(getSeed());
        datacenterGenerator = new DatacenterGenerator(getSeed());
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
}
