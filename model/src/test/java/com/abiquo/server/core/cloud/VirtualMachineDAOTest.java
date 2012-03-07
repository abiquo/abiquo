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

import org.hibernate.Session;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import com.abiquo.server.core.appslibrary.VirtualMachineTemplate;
import com.abiquo.server.core.common.persistence.DefaultDAOTestBase;
import com.softwarementors.bzngine.entities.test.PersistentInstanceTester;

@Test
public class VirtualMachineDAOTest extends DefaultDAOTestBase<VirtualMachineDAO, VirtualMachine>
{

    @Override
    @BeforeMethod
    protected void methodSetUp()
    {
        super.methodSetUp();
    }

    @Override
    protected VirtualMachineDAO createDao(final EntityManager entityManager)
    {
        return new VirtualMachineDAO(entityManager);
    }

    @Override
    protected PersistentInstanceTester<VirtualMachine> createEntityInstanceGenerator()
    {
        return new VirtualMachineGenerator(getSeed());
    }

    @Override
    public VirtualMachineGenerator eg()
    {
        return (VirtualMachineGenerator) super.eg();
    }

    /**
     * Create five machines, three with the temporal value set, and two without the temporal values
     * set. Check the default behaviour (check {@link VirtualMachine} entity filters) is to return
     * only the ones without the temporal values.
     */
    @Test
    public void findAllWithNotTemporalFilters()
    {
        createFiveMachinesWithTemporalAndNotTemporalValueSetAndPersistThem();

        VirtualMachineDAO dao = createDaoForRollbackTransaction();

        List<VirtualMachine> all = dao.findAll();
        assertEquals(all.size(), 2);
    }

    /**
     * Create five machines with same Virtual Machine Template and retrieve the list of Virtual
     * Machines using the Virtual Machine Template related. Check if the size list value is correct.
     */
    @Test
    public void findAllByVirtualMachineTemplate()
    {
        Integer virtualMachineTemplateId =
            createFiveMachinesWithSameVirtualMachineTemplateAndPersistThem();

        VirtualMachineDAO dao = createDaoForRollbackTransaction();

        List<VirtualMachine> all = dao.findByVirtualMachineTemplate(virtualMachineTemplateId);
        assertEquals(all.size(), 5);
    }

    /**
     * Create five machines with same Virtual Machine Template and check if true value is retrieved
     * when querying if this Virtual Machine Template has any Virtual Machine using it.
     */
    @Test
    public void checkIfVirtualMachineTemplateIsBeingUsedByVirtualMachines()
    {
        Integer virtualMachineTemplateId =
            createFiveMachinesWithSameVirtualMachineTemplateAndPersistThem();

        VirtualMachineDAO dao = createDaoForRollbackTransaction();

        boolean result = dao.hasVirtualMachineTemplate(virtualMachineTemplateId);
        assertEquals(result, true);
    }

    /**
     * Create five machines, three with the temporal value set, and two without the temporal values
     * set. Disable the filter {@VirtualMachine.NOT_TEMP}. Check the
     * behaviour (check {@link VirtualMachine} entity filters) is to return all the machines.
     * Whatever happens, enable the filter
     */
    @Test
    public void findAllWithoutFilters()
    {
        createFiveMachinesWithTemporalAndNotTemporalValueSetAndPersistThem();
        VirtualMachineDAO dao = createDaoForRollbackTransaction();

        try
        {
            ((Session) dao.getEntityManager().getDelegate()).disableFilter(VirtualMachine.NOT_TEMP);
            List<VirtualMachine> all = dao.findAll();
            assertEquals(all.size(), 5);
        }
        finally
        {
            ((Session) dao.getEntityManager().getDelegate()).enableFilter(VirtualMachine.NOT_TEMP);
        }
    }

    /**
     * Create five machines, three with the temporal value set, and two without the temporal values
     * set. Disable the filter {@VirtualMachine.NOT_TEMP} and enable the
     * {@VirtualMachine.ONLY_TEMP} one. Check the behaviour (check
     * {@link VirtualMachine} entity filters) is to return only the ones with the temporal values.
     * Whatever happens, enable the filter
     */
    @Test
    public void findAllOnlyTempFilters()
    {
        createFiveMachinesWithTemporalAndNotTemporalValueSetAndPersistThem();
        VirtualMachineDAO dao = createDaoForRollbackTransaction();

        try
        {
            ((Session) dao.getEntityManager().getDelegate()).disableFilter(VirtualMachine.NOT_TEMP);
            ((Session) dao.getEntityManager().getDelegate()).enableFilter(VirtualMachine.ONLY_TEMP);
            List<VirtualMachine> all = dao.findAll();
            assertEquals(all.size(), 3);
        }
        finally
        {
            ((Session) dao.getEntityManager().getDelegate()).enableFilter(VirtualMachine.NOT_TEMP);
            ((Session) dao.getEntityManager().getDelegate())
                .disableFilter(VirtualMachine.ONLY_TEMP);
        }
    }

    @Test
    public void findVirtualMachinesNotAllocatedCompatibleHypervisor()
    {
        VirtualMachine vm = eg().createUniqueInstance();
        // used for posterior search
        Hypervisor hyp = vm.getHypervisor();
        vm.setHypervisor(null);

        vm.setState(VirtualMachineState.NOT_ALLOCATED);
        List<Object> vmlist = new ArrayList<Object>();
        eg().addAuxiliaryEntitiesToPersist(vm, vmlist);
        vmlist.add(vm);

        ds().persistAll(vmlist.toArray());

        VirtualMachineDAO dao = createDaoForRollbackTransaction();
        List<VirtualMachine> vms = dao.findVirtualMachinesNotAllocatedCompatibleHypervisor(hyp);
        assertEquals(vms.size(), 1);
    }

    /**
     * Create five machines, three with the temporal value set, and two without the temporal values
     * set.
     */
    private void createFiveMachinesWithTemporalAndNotTemporalValueSetAndPersistThem()
    {
        VirtualMachine vm1 = eg().createUniqueInstance();
        vm1.setTemporal(23);
        List<Object> vmlist = new ArrayList<Object>();
        eg().addAuxiliaryEntitiesToPersist(vm1, vmlist);

        VirtualMachine vm2 = eg().createUniqueInstance();
        vm2.setTemporal(24);
        eg().addAuxiliaryEntitiesToPersist(vm2, vmlist);

        VirtualMachine vm3 = eg().createUniqueInstance();
        vm3.setTemporal(35);
        eg().addAuxiliaryEntitiesToPersist(vm3, vmlist);

        VirtualMachine vm4 = eg().createUniqueInstance();
        eg().addAuxiliaryEntitiesToPersist(vm4, vmlist);

        VirtualMachine vm5 = eg().createUniqueInstance();
        eg().addAuxiliaryEntitiesToPersist(vm5, vmlist);

        persistAll(ds(), vmlist, vm1, vm2, vm3, vm4, vm5);
    }

    private Integer createFiveMachinesWithSameVirtualMachineTemplateAndPersistThem()
    {
        List<Object> vmlist = new ArrayList<Object>();

        VirtualMachine vm1 = eg().createUniqueInstance();
        eg().addAuxiliaryEntitiesToPersist(vm1, vmlist);

        VirtualMachineTemplate vmt = vm1.getVirtualMachineTemplate();

        VirtualMachine vm2 = eg().createUniqueInstance();
        vm2.setVirtualMachineTemplate(vmt);
        eg().addAuxiliaryEntitiesToPersist(vm2, vmlist);

        VirtualMachine vm3 = eg().createUniqueInstance();
        vm3.setVirtualMachineTemplate(vmt);
        eg().addAuxiliaryEntitiesToPersist(vm3, vmlist);

        VirtualMachine vm4 = eg().createUniqueInstance();
        vm4.setVirtualMachineTemplate(vmt);
        eg().addAuxiliaryEntitiesToPersist(vm4, vmlist);

        VirtualMachine vm5 = eg().createUniqueInstance();
        vm5.setVirtualMachineTemplate(vmt);
        eg().addAuxiliaryEntitiesToPersist(vm5, vmlist);

        persistAll(ds(), vmlist, vm1, vm2, vm3, vm4, vm5);

        return vmt.getId();
    }
}
