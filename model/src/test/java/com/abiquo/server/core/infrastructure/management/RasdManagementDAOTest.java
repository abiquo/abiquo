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

package com.abiquo.server.core.infrastructure.management;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.persistence.EntityManager;

import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import com.abiquo.server.core.cloud.VirtualMachine;
import com.abiquo.server.core.cloud.VirtualMachineGenerator;
import com.abiquo.server.core.common.persistence.DefaultDAOTestBase;
import com.abiquo.server.core.common.persistence.TestDataAccessManager;
import com.abiquo.server.core.infrastructure.storage.VolumeManagement;
import com.softwarementors.bzngine.engines.jpa.test.configuration.EntityManagerFactoryForTesting;
import com.softwarementors.bzngine.entities.test.PersistentInstanceTester;

public class RasdManagementDAOTest extends DefaultDAOTestBase<RasdManagementDAO, RasdManagement>
{
    private VirtualMachineGenerator vmGenerator;

    @Override
    @BeforeMethod
    protected void methodSetUp()
    {
        super.methodSetUp();
        vmGenerator = new VirtualMachineGenerator(getSeed());
    }

    @Override
    protected RasdManagementDAO createDao(final EntityManager entityManager)
    {
        return new RasdManagementDAO(entityManager);
    }

    @Override
    protected PersistentInstanceTester<RasdManagement> createEntityInstanceGenerator()
    {
        return new RasdManagementGenerator(getSeed());
    }

    @Override
    protected EntityManagerFactoryForTesting getFactory()
    {
        return TestDataAccessManager.getFactory();
    }

    @Override
    public RasdManagementGenerator eg()
    {
        return (RasdManagementGenerator) super.eg();
    }

    @Test
    public void testFindByVirtualMachine()
    {
        RasdManagement rasdManagement = eg().createUniqueInstance();
        VirtualMachine vm = vmGenerator.createUniqueInstance();
        rasdManagement.setVirtualMachine(vm);

        List<Object> entitiesToPersist = new ArrayList<Object>();
        eg().addAuxiliaryEntitiesToPersist(rasdManagement, entitiesToPersist);
        persistAll(ds(), entitiesToPersist, rasdManagement);

        RasdManagementDAO dao = createDaoForRollbackTransaction();

        Collection<RasdManagement> results = dao.findByVirtualMachine(vm);

        assertEquals(results.size(), 1);
        eg().assertAllPropertiesEqual(results.iterator().next(), rasdManagement);
    }

    @Test
    public void testFindByVirtualDatacenterAndResourceType()
    {
        RasdManagement rasdManagement = eg().createInstance(VolumeManagement.DISCRIMINATOR);

        List<Object> entitiesToPersist = new ArrayList<Object>();
        eg().addAuxiliaryEntitiesToPersist(rasdManagement, entitiesToPersist);
        persistAll(ds(), entitiesToPersist, rasdManagement);

        RasdManagementDAO dao = createDaoForRollbackTransaction();

        Collection<RasdManagement> results =
            dao.findByVirtualDatacenterAndResourceType(rasdManagement.getVirtualDatacenter(),
                VolumeManagement.DISCRIMINATOR);

        assertEquals(results.size(), 1);
        eg().assertAllPropertiesEqual(results.iterator().next(), rasdManagement);
    }
}
