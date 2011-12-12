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

package com.abiquo.server.core.infrastructure.storage;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.EntityManager;

import org.hibernate.Session;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import com.abiquo.model.enumerator.StorageTechnologyType;
import com.abiquo.server.core.cloud.VirtualDatacenter;
import com.abiquo.server.core.cloud.VirtualDatacenterGenerator;
import com.abiquo.server.core.cloud.VirtualMachine;
import com.abiquo.server.core.cloud.VirtualMachineGenerator;
import com.abiquo.server.core.common.persistence.DefaultDAOTestBase;
import com.abiquo.server.core.common.persistence.TestDataAccessManager;
import com.abiquo.server.core.infrastructure.management.Rasd;
import com.abiquo.server.core.infrastructure.management.RasdManagement;
import com.abiquo.server.core.infrastructure.management.RasdManagementDAO;
import com.softwarementors.bzngine.engines.jpa.test.configuration.EntityManagerFactoryForTesting;
import com.softwarementors.bzngine.entities.test.PersistentInstanceTester;

public class VolumeManagementDAOTest extends
    DefaultDAOTestBase<VolumeManagementDAO, VolumeManagement>
{
    private VirtualDatacenterGenerator vdcGenerator;

    private StoragePoolGenerator poolGenerator;

    private VirtualMachineGenerator vmGenerator;

    @Override
    @BeforeMethod
    protected void methodSetUp()
    {
        super.methodSetUp();
        vdcGenerator = new VirtualDatacenterGenerator(getSeed());
        poolGenerator = new StoragePoolGenerator(getSeed());
        vmGenerator = new VirtualMachineGenerator(getSeed());
    }

    @Override
    protected VolumeManagementDAO createDao(final EntityManager entityManager)
    {
        return new VolumeManagementDAO(entityManager);
    }

    @Override
    protected PersistentInstanceTester<VolumeManagement> createEntityInstanceGenerator()
    {
        return new VolumeManagementGenerator(getSeed());
    }

    @Override
    protected EntityManagerFactoryForTesting getFactory()
    {
        return TestDataAccessManager.getFactory();
    }

    @Override
    public VolumeManagementGenerator eg()
    {
        return (VolumeManagementGenerator) super.eg();
    }

    @Test
    public void testGetVolumesByPool()
    {
        VolumeManagement volume = eg().createUniqueInstance();

        List<Object> entitiesToPersist = new ArrayList<Object>();
        eg().addAuxiliaryEntitiesToPersist(volume, entitiesToPersist);
        persistAll(ds(), entitiesToPersist, volume);

        VolumeManagementDAO dao = createDaoForRollbackTransaction();

        List<VolumeManagement> results = dao.getVolumesByPool(volume.getStoragePool());

        assertSize(results, 1);
        assertAllEntityPropertiesEqual(results.iterator().next(), volume);
    }

    @Test
    public void testGetVolumesByVirtualDatacenter()
    {
        // Test without filtering
        VolumeManagement volume = eg().createUniqueInstance();

        List<Object> entitiesToPersist = new ArrayList<Object>();
        eg().addAuxiliaryEntitiesToPersist(volume, entitiesToPersist);
        persistAll(ds(), entitiesToPersist, volume);

        VolumeManagementDAO dao = createDaoForRollbackTransaction();

        List<VolumeManagement> results =
            dao.getVolumesByVirtualDatacenter(volume.getVirtualDatacenter());

        assertSize(results, 1);
        assertAllEntityPropertiesEqual(results.iterator().next(), volume);
    }

    @Test
    public void testGetStatefulCandidatesWithoutVolumes()
    {
        VirtualDatacenter vdc = vdcGenerator.createUniqueInstance();

        List<Object> entitiesToPersist = new ArrayList<Object>();
        vdcGenerator.addAuxiliaryEntitiesToPersist(vdc, entitiesToPersist);
        persistAll(ds(), entitiesToPersist, vdc);

        VolumeManagementDAO dao = createDaoForRollbackTransaction();

        List<VolumeManagement> results = dao.getStatefulCandidates(vdc);

        assertEmpty(results);
    }

    @Test
    public void testGetStatefulCandidatesWithAssociatedState()
    {
        VolumeManagement volume = eg().createUniqueInstance();
        VirtualMachine vm =
            vmGenerator.createInstance(volume.getVirtualDatacenter().getEnterprise());

        volume.attach(RasdManagement.FIRST_ATTACHMENT_SEQUENCE, vm);

        List<Object> entitiesToPersist = new ArrayList<Object>();
        eg().addAuxiliaryEntitiesToPersist(volume, entitiesToPersist);
        persistAll(ds(), entitiesToPersist, volume);

        VolumeManagementDAO dao = createDaoForRollbackTransaction();

        List<VolumeManagement> results = dao.getStatefulCandidates(volume.getVirtualDatacenter());

        assertEmpty(results);
    }

    @Test
    public void testGetStatefulCandidatesWithDifferentVirtualDatacenter()
    {
        VirtualDatacenter vdc = vdcGenerator.createUniqueInstance();
        VirtualDatacenter other =
            vdcGenerator.createInstance(vdc.getDatacenter(), vdc.getEnterprise());
        VolumeManagement volume = eg().createInstance(vdc);

        List<Object> entitiesToPersist = new ArrayList<Object>();
        eg().addAuxiliaryEntitiesToPersist(volume, entitiesToPersist);
        persistAll(ds(), entitiesToPersist, volume, other);

        VolumeManagementDAO dao = createDaoForRollbackTransaction();

        List<VolumeManagement> results = dao.getStatefulCandidates(other);

        assertEmpty(results);
    }

    @Test
    public void testGetStatefulCandidatesWithNormalPool()
    {
        StoragePool pool = poolGenerator.createUniqueInstance();
        VolumeManagement volume = eg().createInstance(pool);

        pool.getDevice().setStorageTechnology(StorageTechnologyType.NEXENTA);

        List<Object> entitiesToPersist = new ArrayList<Object>();
        eg().addAuxiliaryEntitiesToPersist(volume, entitiesToPersist);
        persistAll(ds(), entitiesToPersist, volume);

        VolumeManagementDAO dao = createDaoForRollbackTransaction();

        List<VolumeManagement> results = dao.getStatefulCandidates(volume.getVirtualDatacenter());

        assertEmpty(results);
    }

    @Test
    public void testGetStatefulCandidatesInISCSIPool()
    {
        StoragePool pool = poolGenerator.createUniqueInstance();
        VolumeManagement volume = eg().createInstance(pool);

        pool.getDevice().setStorageTechnology(StorageTechnologyType.GENERIC_ISCSI);

        List<Object> entitiesToPersist = new ArrayList<Object>();
        eg().addAuxiliaryEntitiesToPersist(volume, entitiesToPersist);
        persistAll(ds(), entitiesToPersist, volume);

        VolumeManagementDAO dao = createDaoForRollbackTransaction();

        List<VolumeManagement> results = dao.getStatefulCandidates(volume.getVirtualDatacenter());

        assertSize(results, 1);
        assertAllEntityPropertiesEqual(results.iterator().next(), volume);
    }

    @Test
    public void testGetStatefulCandidatesInISCSIPoolWithMultipleVolumes()
    {
        StoragePool pool = poolGenerator.createUniqueInstance();
        VolumeManagement volume1 = eg().createInstance(pool);
        VolumeManagement volume2 = eg().createInstance(pool, volume1.getVirtualDatacenter());

        pool.getDevice().setStorageTechnology(StorageTechnologyType.GENERIC_ISCSI);

        List<Object> entitiesToPersist = new ArrayList<Object>();
        eg().addAuxiliaryEntitiesToPersist(volume1, entitiesToPersist);
        persistAll(ds(), entitiesToPersist, volume1, volume2.getRasd(), volume2);

        VolumeManagementDAO dao = createDaoForRollbackTransaction();

        List<VolumeManagement> results = dao.getStatefulCandidates(volume1.getVirtualDatacenter());

        assertSize(results, 2);
    }

    @Test
    public void testGetVolumesByRasd()
    {
        // Test without filtering
        VolumeManagement volume = eg().createUniqueInstance();

        List<Object> entitiesToPersist = new ArrayList<Object>();
        eg().addAuxiliaryEntitiesToPersist(volume, entitiesToPersist);
        persistAll(ds(), entitiesToPersist, volume);
        Rasd rasd = volume.getRasd();
        VolumeManagementDAO dao = createDaoForRollbackTransaction();

        VolumeManagement vol = dao.getVolumeByRasd(rasd);

        eg().assertAllPropertiesEqual(vol, volume);
    }
    
    /**
     * Create five machines, three with the temporal value set, and two without the temporal values
     * set. Check the default behaviour (check {@link VirtualMachine} entity filters) is to return
     * only the ones without the temporal values.
     */
    @Test
    public void findAllWithNotTemporalFilters()
    {
        createFiveVolumesWithTemporalAndNotTemporalValueSetAndPersistThem();
        
        VolumeManagementDAO dao = createDaoForRollbackTransaction();

        List<VolumeManagement> all = dao.findAll();
        assertEquals(all.size(), 2);
    }
    
    /**
     * Create five resources, three with the temporal value set, and two without the temporal values
     * set. Disable the filter {@link RasdManagement.NOT_TEMP}.
     * Check the behaviour ({@link RasdManagement} entity filters) is to return
     * all the resources.
     * 
     * Whatever happens, enable the filter
     */
    @Test
    public void findAllWithoutFilters()
    {
        createFiveVolumesWithTemporalAndNotTemporalValueSetAndPersistThem();
        VolumeManagementDAO dao = createDaoForRollbackTransaction();
        
        try
        {
            ((Session) dao.getEntityManager().getDelegate()).disableFilter(VolumeManagement.NOT_TEMP);
            List<VolumeManagement> all = dao.findAll();
            assertEquals(all.size(), 5);
        }
        finally
        {
            ((Session) dao.getEntityManager().getDelegate()).enableFilter(VolumeManagement.NOT_TEMP);
        }
    }

    /**
     * Create five resources, three with the temporal value set, and two without the temporal values
     * set. Disable the filter {@link RasdManagement.NOT_TEMP} and enable the {@link RasdManagement.ONLY_TEMP} one.
     * Check the behaviour ({@link RasdManagement} entity filters) is to return
     * only the ones with the temporal values.
     * 
     * Whatever happens, enable the filter
     */
    @Test
    public void findAllOnlyTempFilters()
    {
        createFiveVolumesWithTemporalAndNotTemporalValueSetAndPersistThem();
        VolumeManagementDAO dao = createDaoForRollbackTransaction();
        
        try
        {
            ((Session) dao.getEntityManager().getDelegate()).disableFilter(VolumeManagement.NOT_TEMP);
            ((Session) dao.getEntityManager().getDelegate()).enableFilter(VolumeManagement.ONLY_TEMP);
            List<VolumeManagement> all = dao.findAll();
            assertEquals(all.size(), 3);
        }
        finally
        {
            ((Session) dao.getEntityManager().getDelegate()).enableFilter(VolumeManagement.NOT_TEMP);
            ((Session) dao.getEntityManager().getDelegate()).disableFilter(VolumeManagement.ONLY_TEMP);
        }
    }
    
    /**
     * Create five resources, three with the temporal value set, and two without the temporal values
     * set.
     */
    private void createFiveVolumesWithTemporalAndNotTemporalValueSetAndPersistThem()
    {
        VolumeManagement rm1 = eg().createUniqueInstance();
        rm1.setTemporal(23);
        List<Object> resourceList = new ArrayList<Object>();
        eg().addAuxiliaryEntitiesToPersist(rm1, resourceList);

        VolumeManagement rm2 = eg().createUniqueInstance();
        rm2.setTemporal(24);
        eg().addAuxiliaryEntitiesToPersist(rm2, resourceList);

        VolumeManagement rm3 = eg().createUniqueInstance();
        rm3.setTemporal(35);
        eg().addAuxiliaryEntitiesToPersist(rm3, resourceList);

        VolumeManagement rm4 = eg().createUniqueInstance();
        eg().addAuxiliaryEntitiesToPersist(rm4, resourceList);

        VolumeManagement rm5 = eg().createUniqueInstance();
        eg().addAuxiliaryEntitiesToPersist(rm5, resourceList);

        persistAll(ds(), resourceList, rm1, rm2, rm3, rm4, rm5);
    }
}
