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
import java.util.UUID;

import com.abiquo.server.core.appslibrary.VirtualImage;
import com.abiquo.server.core.appslibrary.VirtualImageGenerator;
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

    public VirtualMachineGenerator(final SeedGenerator seed)
    {
        super(seed);

        hypervisorGenerator = new HypervisorGenerator(seed);
        enterpriseGenerator = new EnterpriseGenerator(seed);
        userGenerator = new UserGenerator(seed);
        vImageGenerator = new VirtualImageGenerator(seed);
    }

    @Override
    public void assertAllPropertiesEqual(final VirtualMachine obj1, final VirtualMachine obj2)
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

    public VirtualMachine createInstance(final Hypervisor hypervisor)
    {
        Enterprise enterprise = enterpriseGenerator.createUniqueInstance();
        VirtualImage vimage = vImageGenerator.createInstance(enterprise);
        String name =
            newString(nextSeed(), VirtualMachine.NAME_LENGTH_MIN, VirtualMachine.NAME_LENGTH_MAX);

        return createInstance(vimage, enterprise, hypervisor, name);
    }

    public VirtualMachine createInstance(final VirtualImage vimage)
    {
        Enterprise enterprise = enterpriseGenerator.createUniqueInstance();
        Hypervisor hypervisor = hypervisorGenerator.createUniqueInstance();
        String name =
            newString(nextSeed(), VirtualMachine.NAME_LENGTH_MIN, VirtualMachine.NAME_LENGTH_MAX);

        return createInstance(vimage, enterprise, hypervisor, name);
    }

    public VirtualMachine createInstance(final Enterprise enterprise)
    {
        User user = userGenerator.createInstance(enterprise);
        return createInstance(enterprise, user);
    }

    public VirtualMachine createInstance(final Enterprise enterprise, final User user)
    {
        VirtualImage vimage = vImageGenerator.createInstance(enterprise);
        Hypervisor hypervisor = hypervisorGenerator.createUniqueInstance();
        String name =
            newString(nextSeed(), VirtualMachine.NAME_LENGTH_MIN, VirtualMachine.NAME_LENGTH_MAX);

        return createInstance(vimage, enterprise, hypervisor, user, name);
    }

    public VirtualMachine createInstance(final VirtualImage vimage, final Enterprise enterprise,
        final Hypervisor hypervisor, final String name)
    {
        User user = userGenerator.createInstance(enterprise);
        return createInstance(vimage, enterprise, hypervisor, user, name);
    }

    public VirtualMachine createInstance(final VirtualImage vimage, final Enterprise enterprise,
        final Hypervisor hypervisor, final User user, final String name)
    {
        VirtualMachine virtualMachine =
            new VirtualMachine(name, enterprise, user, vimage, UUID.randomUUID(), 0);

        virtualMachine.setHypervisor(hypervisor);

        // by default set the virtual image requirements
        virtualMachine.setCpu(vimage.getCpuRequired());
        virtualMachine.setRam(vimage.getRamRequired());
        virtualMachine.setHdInBytes((int) vimage.getHdRequiredInBytes());

        return virtualMachine;
    }

    public VirtualMachine createInstance(final VirtualImage vimage, final Enterprise enterprise,
        final User user, final String name)
    {
        VirtualMachine virtualMachine =
            new VirtualMachine(name, enterprise, user, vimage, UUID.randomUUID(), 0);
        // Hypervisor hypervisor = hypervisorGenerator.createUniqueInstance();

        virtualMachine.setName(name);

        // by default set the virtual image requirements
        virtualMachine.setCpu(vimage.getCpuRequired());
        virtualMachine.setRam(vimage.getRamRequired());
        virtualMachine.setHdInBytes((int) vimage.getHdRequiredInBytes());
        virtualMachine.setState(VirtualMachineState.ALLOCATED);

        return virtualMachine;
    }

    public VirtualMachine createInstance(final VirtualImage vimage, final Enterprise enterprise,
        final String name)
    {
        User user = userGenerator.createInstance(enterprise);

        VirtualMachine virtualMachine =
            new VirtualMachine(name, enterprise, user, vimage, UUID.randomUUID(), 0);
        // Hypervisor hypervisor = hypervisorGenerator.createUniqueInstance();

        virtualMachine.setName(name);

        // by default set the virtual image requirements
        virtualMachine.setCpu(vimage.getCpuRequired());
        virtualMachine.setRam(vimage.getRamRequired());
        virtualMachine.setHdInBytes((int) vimage.getHdRequiredInBytes());
        virtualMachine.setState(VirtualMachineState.ALLOCATED);

        return virtualMachine;
    }

    @Override
    public void addAuxiliaryEntitiesToPersist(final VirtualMachine entity,
        final List<Object> entitiesToPersist)
    {
        super.addAuxiliaryEntitiesToPersist(entity, entitiesToPersist);

        Hypervisor hypervisor = entity.getHypervisor();
        hypervisorGenerator.addAuxiliaryEntitiesToPersist(hypervisor, entitiesToPersist);
        entitiesToPersist.add(hypervisor);

        Enterprise enterprise = entity.getEnterprise();
        enterpriseGenerator.addAuxiliaryEntitiesToPersist(enterprise, entitiesToPersist);
        entitiesToPersist.add(enterprise);

        VirtualImage vimage = entity.getVirtualImage();
        vImageGenerator.addAuxiliaryEntitiesToPersist(vimage, entitiesToPersist);
        entitiesToPersist.add(vimage);

        User user = entity.getUser();
        userGenerator.addAuxiliaryEntitiesToPersist(user, entitiesToPersist);
        entitiesToPersist.add(user);
    }

}
