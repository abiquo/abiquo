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

package com.abiquo.api.services.infrastructure;

import javax.persistence.EntityManager;

import org.springframework.security.context.SecurityContextHolder;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import com.abiquo.api.common.AbstractUnitTest;
import com.abiquo.api.common.SysadminAuthentication;
import com.abiquo.api.exceptions.APIError;
import com.abiquo.api.exceptions.NotFoundException;
import com.abiquo.api.services.MachineService;
import com.abiquo.api.services.cloud.VirtualMachineService;
import com.abiquo.api.services.stub.VsmServiceStubMock;
import com.abiquo.model.enumerator.FitPolicy;
import com.abiquo.server.core.cloud.Hypervisor;
import com.abiquo.server.core.cloud.VirtualAppliance;
import com.abiquo.server.core.cloud.VirtualDatacenter;
import com.abiquo.server.core.cloud.VirtualMachine;
import com.abiquo.server.core.cloud.VirtualMachineState;
import com.abiquo.server.core.common.EnvironmentGenerator;
import com.abiquo.server.core.infrastructure.Datacenter;
import com.abiquo.server.core.infrastructure.Machine;
import com.abiquo.server.core.scheduler.FitPolicyRule;
import com.softwarementors.bzngine.engines.jpa.EntityManagerHelper;

public class MachineServiceTest extends AbstractUnitTest
{
    @Override
    @AfterMethod
    public void tearDown()
    {

        environment.getEnvironment().clear();
        super.tearDown();
    }

    EnvironmentGenerator environment = new EnvironmentGenerator(seed);

    @Override
    @BeforeMethod
    public void setup()
    {
        environment.generateEnterprise();
        environment.generateInfrastructure();
        environment.generateVirtualDatacenter();
        SecurityContextHolder.getContext().setAuthentication(new SysadminAuthentication());
    }

    @Test
    public void testDeleteMachineWithVirtualMachineManagedDeployed()
    {
        Datacenter datacenter = environment.get(Datacenter.class);
        environment.generateAllocatedVirtualMachine();
        setup(environment.getEnvironment().toArray());
        FitPolicyRule f = new FitPolicyRule(datacenter, FitPolicy.PERFORMANCE);
        setup(f);
        VirtualMachine vmManaged = environment.get(VirtualMachine.class);

        Hypervisor hypervisor = environment.get(Hypervisor.class);
        Machine machine = hypervisor.getMachine();

        EntityManager em = getEntityManager();
        EntityManagerHelper.beginReadWriteTransaction(em);

        MachineService service = new MachineService(em);
        service.setVsm(new VsmServiceStubMock()); // Must use the mocked VSM
        service.removeMachine(machine.getId());

        EntityManagerHelper.commit(em);

        EntityManagerHelper.beginRollbackTransaction(em);
        service = new MachineService(em);

        try
        {
            service.getMachine(machine.getId());
        }
        catch (NotFoundException ex)
        {
            org.testng.Assert.assertEquals(ex.getErrors().iterator().next().getMessage(),
                APIError.NON_EXISTENT_MACHINE.getMessage());
        }

        VirtualMachineService vmService = new VirtualMachineService(em);

        VirtualDatacenter virtualDatacenter = environment.get(VirtualDatacenter.class);
        VirtualAppliance virtualAppliance = environment.get(VirtualAppliance.class);

        VirtualMachine virtualMachine =
            vmService.getVirtualMachine(virtualDatacenter.getId(), virtualAppliance.getId(),
                vmManaged.getId());

        org.testng.Assert.assertNull(virtualMachine.getHypervisor());
        org.testng.Assert.assertNull(virtualMachine.getDatastore());
        org.testng.Assert
            .assertEquals(virtualMachine.getState(), VirtualMachineState.NOT_ALLOCATED);

    }

    @Test
    public void testDeleteMachineWithVirtualMachineNotManagedDeployed()
    {
        Datacenter datacenter = environment.get(Datacenter.class);
        environment.generateNotAllocatedVirtualMachine();
        setup(environment.getEnvironment().toArray());
        FitPolicyRule f = new FitPolicyRule(datacenter, FitPolicy.PERFORMANCE);
        setup(f);
        VirtualMachine vmNotManaged = environment.get(VirtualMachine.class);

        Hypervisor hypervisor = environment.get(Hypervisor.class);
        Machine machine = hypervisor.getMachine();

        EntityManager em = getEntityManager();
        EntityManagerHelper.beginReadWriteTransaction(em);

        MachineService service = new MachineService(em);
        service.setVsm(new VsmServiceStubMock()); // Must use the mocked VSM
        service.removeMachine(machine.getId());

        EntityManagerHelper.commit(em);

        EntityManagerHelper.beginRollbackTransaction(em);
        service = new MachineService(em);

        try
        {
            service.getMachine(machine.getId());
        }
        catch (NotFoundException ex)
        {
            org.testng.Assert.assertEquals(ex.getErrors().iterator().next().getMessage(),
                APIError.NON_EXISTENT_MACHINE.getMessage());
        }

        VirtualMachineService vmService = new VirtualMachineService(em);

        VirtualDatacenter virtualDatacenter = environment.get(VirtualDatacenter.class);
        VirtualAppliance virtualAppliance = environment.get(VirtualAppliance.class);

        try
        {
            vmService.getVirtualMachine(virtualDatacenter.getId(), virtualAppliance.getId(),
                vmNotManaged.getId());
        }
        catch (NotFoundException ex)
        {
            org.testng.Assert.assertEquals(ex.getErrors().iterator().next().getMessage(),
                APIError.NON_EXISTENT_VIRTUALMACHINE.getMessage());
        }

    }
}
