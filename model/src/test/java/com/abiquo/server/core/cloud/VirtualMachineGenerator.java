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

import java.util.List;
import java.util.Random;
import java.util.UUID;

import com.abiquo.server.core.common.DefaultEntityGenerator;
import com.abiquo.server.core.enterprise.Enterprise;
import com.abiquo.server.core.enterprise.EnterpriseGenerator;
import com.abiquo.server.core.enterprise.User;
import com.abiquo.server.core.enterprise.UserGenerator;
import com.softwarementors.commons.test.SeedGenerator;
import com.softwarementors.commons.testng.AssertEx;

public class VirtualMachineGenerator extends DefaultEntityGenerator<VirtualMachine>
{

    HypervisorGenerator hypervisorGenerator;

    EnterpriseGenerator enterpriseGenerator;

    UserGenerator userGenerator;

    VirtualImageGenerator vImageGenerator;

    public VirtualMachineGenerator(SeedGenerator seed)
    {
        super(seed);

        hypervisorGenerator = new HypervisorGenerator(seed);
        enterpriseGenerator = new EnterpriseGenerator(seed);
        userGenerator = new UserGenerator(seed);
        vImageGenerator = new VirtualImageGenerator(seed);
    }

    @Override
    public void assertAllPropertiesEqual(VirtualMachine obj1, VirtualMachine obj2)
    {
        AssertEx.assertPropertiesEqualSilent(obj1, obj2, VirtualMachine.NAME_PROPERTY,
            VirtualMachine.DESCRIPTION_PROPERTY, VirtualMachine.RAM_PROPERTY,
            VirtualMachine.CPU_PROPERTY, VirtualMachine.HD_PROPERTY,
            VirtualMachine.VDRP_PORT_PROPERTY, VirtualMachine.VDRP_IP_PROPERTY,
            VirtualMachine.HIGH_DISPONIBILITY_PROPERTY, VirtualMachine.ID_TYPE_PROPERTY);

        vImageGenerator.assertAllPropertiesEqual(obj1.getVirtualImage(), obj2.getVirtualImage());
    }

    @Override
    public VirtualMachine createUniqueInstance()
    {
        Hypervisor hypervisor = hypervisorGenerator.createUniqueInstance();

        return createInstance(hypervisor);
    }

    public VirtualMachine createInstance(Hypervisor hypervisor)
    {
        Enterprise enterprise = enterpriseGenerator.createUniqueInstance();
        User user = userGenerator.createInstance(enterprise);

        VirtualImage vimage = vImageGenerator.createInstance(enterprise);

        return new VirtualMachine("" + new Random().nextInt(),
            enterprise,
            user,
            hypervisor,
            vimage,
            UUID.randomUUID(),
            0);
    }

    public VirtualMachine createInstance(VirtualImage vimage)
    {
        Enterprise enterprise = enterpriseGenerator.createUniqueInstance();
        User user = userGenerator.createInstance(enterprise);
        Hypervisor hypervisor = hypervisorGenerator.createUniqueInstance();

        return new VirtualMachine("" + new Random().nextInt(),
            enterprise,
            user,
            hypervisor,
            vimage,
            UUID.randomUUID(),
            0);
    }

    public VirtualMachine createInstance(Enterprise enterprise)
    {
        User user = userGenerator.createInstance(enterprise);
        VirtualImage vi = vImageGenerator.createInstance(enterprise);

        return new VirtualMachine("" + new Random().nextInt(),
            enterprise,
            user,
            vi,
            UUID.randomUUID(),
            0);
    }

    public VirtualMachine createInstance(VirtualImage vimage, Enterprise enterprise,
        Hypervisor hypervisor, String name)
    {
        User user = userGenerator.createInstance(enterprise);

        VirtualMachine virtualMachine =
            new VirtualMachine("" + new Random().nextInt(),
                enterprise,
                user,
                vimage,
                UUID.randomUUID(),
                0);

        virtualMachine.setName(name);
        virtualMachine.setHypervisor(hypervisor);

        // by default set the virtual image requirements
        virtualMachine.setCpu(vimage.getCpuRequired());
        virtualMachine.setRam(vimage.getRamRequired());
        virtualMachine.setHdInBytes((int) vimage.getHdRequiredInBytes());

        return virtualMachine;
    }

    public VirtualMachine createInstance(VirtualImage vimage, Enterprise enterprise, String name)
    {
        User user = userGenerator.createInstance(enterprise);

        VirtualMachine virtualMachine =
            new VirtualMachine("" + new Random().nextInt(),
                enterprise,
                user,
                vimage,
                UUID.randomUUID(),
                0);
        // Hypervisor hypervisor = hypervisorGenerator.createUniqueInstance();

        virtualMachine.setName(name);

        // by default set the virtual image requirements
        virtualMachine.setCpu(vimage.getCpuRequired());
        virtualMachine.setRam(vimage.getRamRequired());
        virtualMachine.setHdInBytes((int) vimage.getHdRequiredInBytes());
        virtualMachine.setState(State.NOT_DEPLOYED);

        return virtualMachine;
    }

    @Override
    public void addAuxiliaryEntitiesToPersist(VirtualMachine entity, List<Object> entitiesToPersist)
    {
        super.addAuxiliaryEntitiesToPersist(entity, entitiesToPersist);

        Hypervisor hypervisor = entity.getHypervisor();
        hypervisorGenerator.addAuxiliaryEntitiesToPersist(hypervisor, entitiesToPersist);
        entitiesToPersist.add(hypervisor);

        VirtualImage vimage = entity.getVirtualImage();
        vImageGenerator.addAuxiliaryEntitiesToPersist(vimage, entitiesToPersist);
        entitiesToPersist.add(vimage);

        Enterprise enterprise = entity.getEnterprise();
        enterpriseGenerator.addAuxiliaryEntitiesToPersist(enterprise, entitiesToPersist);
        entitiesToPersist.add(enterprise);

        User user = entity.getUser();
        userGenerator.addAuxiliaryEntitiesToPersist(user, entitiesToPersist);
        entitiesToPersist.add(user);
    }

}
