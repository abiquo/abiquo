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

import java.util.ArrayList;
import java.util.List;

import javax.persistence.EntityManager;

import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import com.abiquo.server.core.common.persistence.DefaultDAOTestBase;
import com.abiquo.server.core.common.persistence.TestDataAccessManager;
import com.softwarementors.bzngine.engines.jpa.test.configuration.EntityManagerFactoryForTesting;
import com.softwarementors.bzngine.entities.test.PersistentInstanceTester;

public class NodeVirtualImageDAOTest extends
    DefaultDAOTestBase<NodeVirtualImageDAO, NodeVirtualImage>
{
    private VirtualMachineGenerator virtualMachineGenerator;

    private VirtualApplianceGenerator virtualApplianceGenerator;

    private VirtualAppliance vapp;

    private VirtualMachine vmachine;

    @Override
    @BeforeMethod
    protected void methodSetUp()
    {
        super.methodSetUp();

        virtualMachineGenerator = new VirtualMachineGenerator(getSeed());
        virtualApplianceGenerator = new VirtualApplianceGenerator(getSeed());

        vapp = virtualApplianceGenerator.createUniqueInstance();
        vmachine = virtualMachineGenerator.createUniqueInstance();

        saveVirtualAppliance(vapp);
        saveVirtualMachine(vmachine);
    }

    @Override
    protected NodeVirtualImageDAO createDao(final EntityManager entityManager)
    {
        return new NodeVirtualImageDAO(entityManager);
    }

    @Override
    protected PersistentInstanceTester<NodeVirtualImage> createEntityInstanceGenerator()
    {
        return new NodeVirtualImageGenerator(getSeed());
    }

    @Override
    protected EntityManagerFactoryForTesting getFactory()
    {
        return TestDataAccessManager.getFactory();
    }

    @Override
    public NodeVirtualImageGenerator eg()
    {
        return (NodeVirtualImageGenerator) super.eg();
    }

    @Test
    public void testFindVirtualAppliance()
    {
        NodeVirtualImage nodeVirtualImage = eg().createInstance(vapp, vmachine);
        ds().persistAll(nodeVirtualImage);

        NodeVirtualImageDAO nodeVirtualImageDAO = createDaoForRollbackTransaction();

        VirtualAppliance result = nodeVirtualImageDAO.findVirtualAppliance(vmachine);

        assertNotNull(result);
        virtualApplianceGenerator.assertAllPropertiesEqual(result, vapp);
    }

    @Test
    public void testFindVirtualApplianceWithUnexistentVirtualMachine()
    {
        NodeVirtualImage nodeVirtualImage = eg().createInstance(vapp, vmachine);
        ds().persistAll(nodeVirtualImage);

        NodeVirtualImageDAO nodeVirtualImageDAO = createDaoForRollbackTransaction();

        VirtualMachine vmachineUnexisting = virtualMachineGenerator.createUniqueInstance();
        VirtualAppliance result = nodeVirtualImageDAO.findVirtualAppliance(vmachineUnexisting);

        assertNull(result);
    }

    @Test
    public void testFindByVirtualMachine()
    {
        NodeVirtualImage nodeVirtualImage = eg().createInstance(vapp, vmachine);
        ds().persistAll(nodeVirtualImage);

        NodeVirtualImageDAO nodeVirtualImageDAO = createDaoForRollbackTransaction();

        NodeVirtualImage result = nodeVirtualImageDAO.findByVirtualMachine(vmachine);

        assertNotNull(result);
        assertAllEntityPropertiesEqual(result, nodeVirtualImage);
    }

    @Test
    public void testFindByUnexistingVirtualMachine()
    {
        NodeVirtualImage nodeVirtualImage = eg().createInstance(vapp, vmachine);
        ds().persistAll(nodeVirtualImage);

        NodeVirtualImageDAO nodeVirtualImageDAO = createDaoForRollbackTransaction();

        VirtualMachine vmachineUnexisting = virtualMachineGenerator.createUniqueInstance();
        NodeVirtualImage result = nodeVirtualImageDAO.findByVirtualMachine(vmachineUnexisting);

        assertNull(result);
    }

    @Test
    public void testFindByVirtualImage()
    {
        NodeVirtualImage nvi1 = eg().createInstance(vapp, vmachine);
        NodeVirtualImage nvi2 = eg().createInstance(vapp, vmachine);
        NodeVirtualImage nvi3 = eg().createInstance(vapp, vmachine);

        ds().persistAll(nvi1, nvi2, nvi3);

        NodeVirtualImageDAO nodeVirtualImageDAO = createDaoForRollbackTransaction();

        List<NodeVirtualImage> result =
            nodeVirtualImageDAO.findByVirtualImage(vmachine.getVirtualImage());

        assertNotNull(result);
        assertEquals(result.size(), 3);
    }

    private void saveVirtualAppliance(final VirtualAppliance vapp)
    {
        List<Object> entitiesToPersist = new ArrayList<Object>();
        virtualApplianceGenerator.addAuxiliaryEntitiesToPersist(vapp, entitiesToPersist);
        persistAll(ds(), entitiesToPersist, vapp);
    }

    private void saveVirtualMachine(final VirtualMachine virtualMachine)
    {
        List<Object> entitiesToPersist = new ArrayList<Object>();
        virtualMachineGenerator.addAuxiliaryEntitiesToPersist(virtualMachine, entitiesToPersist);
        persistAll(ds(), entitiesToPersist, virtualMachine);
    }
}
