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

import junit.framework.Assert;

import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import com.abiquo.server.core.appslibrary.Category;
import com.abiquo.server.core.appslibrary.CategoryGenerator;
import com.abiquo.server.core.appslibrary.VirtualImage;
import com.abiquo.server.core.appslibrary.VirtualImageGenerator;
import com.abiquo.server.core.common.persistence.DefaultDAOTestBase;
import com.abiquo.server.core.common.persistence.TestDataAccessManager;
import com.abiquo.server.core.enterprise.Enterprise;
import com.abiquo.server.core.enterprise.EnterpriseGenerator;
import com.abiquo.server.core.enterprise.Privilege;
import com.softwarementors.bzngine.engines.jpa.test.configuration.EntityManagerFactoryForTesting;
import com.softwarementors.bzngine.entities.test.PersistentInstanceTester;

public class NodeVirtualImageDAOTest extends
    DefaultDAOTestBase<NodeVirtualImageDAO, NodeVirtualImage>
{
    private VirtualMachineGenerator virtualMachineGenerator;

    private VirtualApplianceGenerator virtualApplianceGenerator;

    private VirtualAppliance vapp;

    private VirtualMachine vmachine;

    private VirtualImageGenerator virtualImageGenerator;

    private VirtualImage vimage;

    private CategoryGenerator categoryGenerator;

    @Override
    @BeforeMethod
    protected void methodSetUp()
    {
        super.methodSetUp();

        virtualMachineGenerator = new VirtualMachineGenerator(getSeed());
        virtualApplianceGenerator = new VirtualApplianceGenerator(getSeed());
        virtualImageGenerator = new VirtualImageGenerator(getSeed());
        categoryGenerator = new CategoryGenerator(getSeed());

        vimage = virtualImageGenerator.createUniqueInstance();
        vapp = virtualApplianceGenerator.createUniqueInstance();
        vmachine = virtualMachineGenerator.createInstance(vimage);

        saveVirtualAppliance(vapp);
        // List<Object> entitiesToSetup = new ArrayList<Object>();
        //
        // entitiesToSetup.add(vimage.getCategory());
        // entitiesToSetup.add(vimage);
        // entitiesToSetup.add(vmachine);
        // ds().persistAll(entitiesToSetup.toArray());
        // saveVirtualImage(vimage);
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
    public void findByEnterprise()
    {
        EnterpriseGenerator enterpriseGenerator = new EnterpriseGenerator(getSeed());
        Enterprise enterprise = enterpriseGenerator.createUniqueInstance();
        Enterprise enterprise2 = enterpriseGenerator.createUniqueInstance();

        VirtualMachineGenerator vMachineGenerator = new VirtualMachineGenerator(getSeed());
        VirtualMachine vm1 = vMachineGenerator.createInstance(enterprise);
        VirtualMachine vm2 = vMachineGenerator.createInstance(enterprise);
        VirtualMachine vm3 = vMachineGenerator.createInstance(enterprise2);

        VirtualDatacenterGenerator vdcGenerator = new VirtualDatacenterGenerator(getSeed());
        VirtualDatacenter vdc = vdcGenerator.createInstance(enterprise);
        VirtualDatacenter vdc2 = vdcGenerator.createInstance(enterprise2);

        VirtualApplianceGenerator vApplianceGenerator = new VirtualApplianceGenerator(getSeed());
        VirtualAppliance vAppliance = vApplianceGenerator.createInstance(vdc);
        VirtualAppliance vAppliance2 = vApplianceGenerator.createInstance(vdc2);

        NodeVirtualImageGenerator nodeVImageGenerator = new NodeVirtualImageGenerator(getSeed());
        NodeVirtualImage nvi = nodeVImageGenerator.createInstance(vAppliance, vm1);
        NodeVirtualImage nvi2 = nodeVImageGenerator.createInstance(vAppliance, vm2);
        NodeVirtualImage nvi3 = nodeVImageGenerator.createInstance(vAppliance2, vm3);

        vm1.getVirtualImage().getRepository()
            .setDatacenter(vm1.getHypervisor().getMachine().getRack().getDatacenter());
        vm2.getVirtualImage().getRepository()
            .setDatacenter(vm2.getHypervisor().getMachine().getRack().getDatacenter());
        vm3.getVirtualImage().getRepository()
            .setDatacenter(vm3.getHypervisor().getMachine().getRack().getDatacenter());

        // ds().persistAll(enterprise, enterprise2, vm1.getUser().getRole(), vm1.getUser(),
        // vm1.getHypervisor().getMachine().getDatacenter(),
        // vm1.getHypervisor().getMachine().getRack().getDatacenter(),
        // vm1.getHypervisor().getMachine().getRack(), vm1.getHypervisor().getMachine(),
        // vm1.getHypervisor(), vm1.getVirtualImage(), vm1, vm2.getUser().getRole(),
        // vm2.getUser(), vm2.getHypervisor().getMachine().getRack().getDatacenter(),
        // vm2.getHypervisor().getMachine().getRack(),
        // vm2.getHypervisor().getMachine().getDatacenter(), vm2.getHypervisor().getMachine(),
        // vm2.getHypervisor(), vm2.getVirtualImage(), vm2, vm3.getUser().getRole(),
        // vm3.getUser(), vm3.getHypervisor().getMachine().getRack().getDatacenter(),
        // vm3.getHypervisor().getMachine().getRack(),
        // vm3.getHypervisor().getMachine().getDatacenter(), vm3.getHypervisor().getMachine(),
        // vm3.getHypervisor(), vm3.getVirtualImage(), vm3, vdc.getNetwork(), vdc.getDatacenter(),
        // vdc, vdc2.getNetwork(), vdc2.getDatacenter(), vdc2, vAppliance, vAppliance2, nvi, nvi2,
        // nvi3);

        List<Object> entitiesToSetup = new ArrayList<Object>();

        entitiesToSetup.add(enterprise);
        entitiesToSetup.add(enterprise2);
        entitiesToSetup.add(vdc.getNetwork());
        entitiesToSetup.add(vdc.getDatacenter());
        entitiesToSetup.add(vdc);
        entitiesToSetup.add(vdc2.getNetwork());
        entitiesToSetup.add(vdc2.getDatacenter());
        entitiesToSetup.add(vdc2);
        entitiesToSetup.add(vAppliance);
        entitiesToSetup.add(vAppliance2);

        for (Privilege p : vm1.getUser().getRole().getPrivileges())
        {
            entitiesToSetup.add(p);
        }

        entitiesToSetup.add(vm1.getUser().getRole());
        entitiesToSetup.add(vm1.getUser());
        entitiesToSetup.add(vm1.getHypervisor().getMachine().getRack().getDatacenter());
        entitiesToSetup.add(vm1.getHypervisor().getMachine().getRack());
        entitiesToSetup.add(vm1.getHypervisor().getMachine());
        entitiesToSetup.add(vm1.getHypervisor());
        entitiesToSetup.add(vm1.getVirtualImage().getRepository());
        entitiesToSetup.add(vm1.getVirtualImage().getCategory());
        entitiesToSetup.add(vm1.getVirtualImage());
        entitiesToSetup.add(vm1);
        entitiesToSetup.add(nvi);

        for (Privilege p : vm2.getUser().getRole().getPrivileges())
        {
            entitiesToSetup.add(p);
        }

        entitiesToSetup.add(vm2.getUser().getRole());
        entitiesToSetup.add(vm2.getUser());
        entitiesToSetup.add(vm2.getHypervisor().getMachine().getRack().getDatacenter());
        entitiesToSetup.add(vm2.getHypervisor().getMachine().getRack());
        entitiesToSetup.add(vm2.getHypervisor().getMachine());
        entitiesToSetup.add(vm2.getHypervisor());
        entitiesToSetup.add(vm2.getVirtualImage().getRepository());
        entitiesToSetup.add(vm2.getVirtualImage().getCategory());
        entitiesToSetup.add(vm2.getVirtualImage());
        entitiesToSetup.add(vm2);
        entitiesToSetup.add(nvi2);

        for (Privilege p : vm3.getUser().getRole().getPrivileges())
        {
            entitiesToSetup.add(p);
        }

        entitiesToSetup.add(vm3.getUser().getRole());
        entitiesToSetup.add(vm3.getUser());
        entitiesToSetup.add(vm3.getHypervisor().getMachine().getRack().getDatacenter());
        entitiesToSetup.add(vm3.getHypervisor().getMachine().getRack());
        entitiesToSetup.add(vm3.getHypervisor().getMachine());
        entitiesToSetup.add(vm3.getHypervisor());
        entitiesToSetup.add(vm3.getVirtualImage().getRepository());
        entitiesToSetup.add(vm3.getVirtualImage().getCategory());
        entitiesToSetup.add(vm3.getVirtualImage());
        entitiesToSetup.add(vm3);
        entitiesToSetup.add(nvi3);

        ds().persistAll(entitiesToSetup.toArray());

        NodeVirtualImageDAO dao = createDaoForRollbackTransaction();
        List<NodeVirtualImage> list = dao.findByEnterprise(enterprise);
        Assert.assertEquals(list.size(), 2);
    }

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
        NodeVirtualImage nodeVirtualImage = eg().createInstance(vapp, vmachine, vimage);
        ds().persistAll(nodeVirtualImage);

        NodeVirtualImageDAO nodeVirtualImageDAO = createDaoForRollbackTransaction();

        NodeVirtualImage result = nodeVirtualImageDAO.findByVirtualMachine(vmachine);

        assertNotNull(result);
        assertEquals(result.getId(), nodeVirtualImage.getId());
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

    private void saveCategory(final Category category)
    {
        List<Object> entitiesToPersist = new ArrayList<Object>();
        categoryGenerator.addAuxiliaryEntitiesToPersist(category, entitiesToPersist);
        persistAll(ds(), entitiesToPersist, category);
    }
}
