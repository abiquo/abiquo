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

package com.abiquo.api.eventing;

import static org.testng.Assert.assertEquals;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import javax.persistence.EntityManager;

import com.abiquo.api.common.AbstractUnitTest;
import com.abiquo.commons.amqp.impl.vsm.domain.VirtualSystemEvent;
import com.abiquo.server.core.cloud.Hypervisor;
import com.abiquo.server.core.cloud.NodeVirtualImage;
import com.abiquo.server.core.cloud.VirtualAppliance;
import com.abiquo.server.core.cloud.VirtualMachine;
import com.abiquo.server.core.enterprise.Enterprise;
import com.abiquo.server.core.infrastructure.Datastore;
import com.abiquo.server.core.infrastructure.DatastoreDAO;
import com.abiquo.server.core.infrastructure.Machine;
import com.softwarementors.bzngine.engines.jpa.EntityManagerHelper;

/**
 * @author eruiz
 */
public abstract class VSMEventProcessorTestBase extends AbstractUnitTest
{
    protected abstract VSMEventProcessor getEventingProcessor(EntityManager em);

    protected void assertStage(VirtualMachineStage stage)
    {
        // List of entities to persist
        List<Object> entitiesToPersist = new ArrayList<Object>();

        // Create enterprise
        Enterprise enterprise = enterpriseGenerator.createUniqueInstance();

        enterpriseGenerator.addAuxiliaryEntitiesToPersist(enterprise, entitiesToPersist);
        entitiesToPersist.add(enterprise);

        // Create hypervisor
        Hypervisor hypervisor = hypervisorGenerator.createUniqueInstance();
        // hypervisor.setMachine(datastore.getMachines().get(0));
        hypervisor.getMachine().setEnterprise(enterprise);
        hypervisor.getMachine().setHypervisor(hypervisor);

        Datastore datastore = datastoreGenerator.createInstance(hypervisor.getMachine());
        datastore.setDatastoreUUID(UUID.randomUUID().toString()); // TODO: UUID should be added to
                                                                  // 'datastoreGenerator' ??
        datastoreGenerator.addAuxiliaryEntitiesToPersist(datastore, entitiesToPersist);
        entitiesToPersist.add(datastore);

        hypervisorGenerator.addAuxiliaryEntitiesToPersist(hypervisor, entitiesToPersist);
        entitiesToPersist.add(hypervisor);

        // Create virtual appliance
        VirtualAppliance vapp = virtualApplianceGenerator.createUniqueInstance();
        virtualApplianceGenerator.addAuxiliaryEntitiesToPersist(vapp, entitiesToPersist);
        entitiesToPersist.add(vapp);

        VirtualMachine vm = vmGenerator.createInstance(hypervisor);

        vm.setHypervisor(hypervisor);
        vm.setEnterprise(enterprise);
        vm.setName(stage.getName());
        vm.setState(stage.getState());
        vm.getVirtualImage().setIdCategory(null); // TODO remove
        vm.setDatastore(datastore);

        vmGenerator.addAuxiliaryEntitiesToPersist(vm, entitiesToPersist);
        entitiesToPersist.add(vm);

        NodeVirtualImage node = nodeVirtualImageGenerator.createInstance(vapp, vm);
        nodeVirtualImageGenerator.addAuxiliaryEntitiesToPersist(node, entitiesToPersist);
        entitiesToPersist.add(node);

        vapp.addToNodeVirtualImages(node);

        // Persist all entities
        setup(entitiesToPersist.toArray());

        // Testing stuff
        EntityManager manager = getEntityManagerWithAnActiveTransaction();
        VSMEventProcessor processor = getEventingProcessor(manager);

        try
        {
            if (stage.getEvent() != null)
            {
                VirtualMachine foundVM = processor.vmRepo.findByName(stage.getName());

                processor.onEvent(buildEvent(stage.getEvent(), foundVM.getName(), foundVM
                    .getHypervisor().getMachine().getName(), foundVM.getHypervisor().getType()
                    .name()));

                foundVM = processor.vmRepo.findByName(stage.getName());

                assertEquals(foundVM.getState(), stage.getExpected());
            }
        }
        finally
        {
            EntityManagerHelper.commit(manager);
        }
    }

    protected void assertStageAndDestroyed(VirtualMachineStage stage)
    {
        // List of entities to persist
        List<Object> entitiesToPersist = new ArrayList<Object>();

        // Create enterprise
        Enterprise enterprise = enterpriseGenerator.createUniqueInstance();

        enterpriseGenerator.addAuxiliaryEntitiesToPersist(enterprise, entitiesToPersist);
        entitiesToPersist.add(enterprise);

        // Create hypervisor
        Hypervisor hypervisor = hypervisorGenerator.createUniqueInstance();
        // hypervisor.setMachine(datastore.getMachines().get(0));
        hypervisor.getMachine().setEnterprise(enterprise);
        hypervisor.getMachine().setHypervisor(hypervisor);

        hypervisorGenerator.addAuxiliaryEntitiesToPersist(hypervisor, entitiesToPersist);
        entitiesToPersist.add(hypervisor);

        // Create virtual appliance
        VirtualAppliance vapp = virtualApplianceGenerator.createUniqueInstance();
        virtualApplianceGenerator.addAuxiliaryEntitiesToPersist(vapp, entitiesToPersist);
        entitiesToPersist.add(vapp);

        VirtualMachine vm = vmGenerator.createInstance(hypervisor);

        vm.setHypervisor(hypervisor);
        vm.setEnterprise(enterprise);
        vm.setName(stage.getName());
        vm.setState(stage.getState());
        vm.setHdInBytes(1000000);
        vm.setCpu(2);
        vm.setRam(1000);
        vm.getVirtualImage().setIdCategory(null); // TODO remove

        Datastore datastore = datastoreGenerator.createInstance(hypervisor.getMachine());
        datastore.setDatastoreUUID(UUID.randomUUID().toString()); // TODO: UUID should be added to
                                                                  // 'datastoreGenerator' ??

        // Resources
        // TODO (should be filled with an actual VM Deploy)
        datastore.setUsedSize(vm.getHdInBytes());
        hypervisor.getMachine().setVirtualCpusUsed(vm.getCpu());
        hypervisor.getMachine().setVirtualRamUsedInMb(vm.getRam());
        // Resources

        datastoreGenerator.addAuxiliaryEntitiesToPersist(datastore, entitiesToPersist);
        entitiesToPersist.add(datastore);

        vm.setDatastore(datastore);

        vmGenerator.addAuxiliaryEntitiesToPersist(vm, entitiesToPersist);
        entitiesToPersist.add(vm);

        NodeVirtualImage node = nodeVirtualImageGenerator.createInstance(vapp, vm);
        nodeVirtualImageGenerator.addAuxiliaryEntitiesToPersist(node, entitiesToPersist);
        entitiesToPersist.add(node);

        vapp.addToNodeVirtualImages(node);


        // Persist all entities
        setup(entitiesToPersist.toArray());

        // Testing stuff
        EntityManager manager = getEntityManagerWithAnActiveTransaction();
        VSMEventProcessor processor = getEventingProcessor(manager);

        try
        {
            if (stage.getEvent() != null)
            {
                VirtualMachine foundVM = processor.vmRepo.findByName(stage.getName());

                processor.onEvent(buildEvent(stage.getEvent(), foundVM.getName(), foundVM
                    .getHypervisor().getMachine().getName(), foundVM.getHypervisor().getType()
                    .name()));

                foundVM = processor.vmRepo.findByName(stage.getName());


                // state is as NOT_ALLOCATED as expected
                assertEquals(foundVM.getState(), stage.getExpected());

                // Resources are freed
                assertEquals(datastore.getUsedSize(), 0);
                assertEquals(hypervisor.getMachine().getVirtualCpusUsed().intValue(), 0);
                assertEquals(hypervisor.getMachine().getVirtualRamUsedInMb().intValue(), 0);
            }
        }
        finally
        {
            EntityManagerHelper.commit(manager);
        }
    }

    protected VirtualSystemEvent buildEvent(String event, String virtualMachine,
        String machineAddress, String machineType)
    {
        VirtualSystemEvent notification = new VirtualSystemEvent();

        notification.setEventType(event);
        notification.setVirtualSystemAddress(machineAddress);
        notification.setVirtualSystemId(virtualMachine);
        notification.setVirtualSystemType(machineType);

        return notification;
    }
}
