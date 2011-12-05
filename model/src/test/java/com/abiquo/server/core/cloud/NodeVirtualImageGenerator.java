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

import com.abiquo.server.core.appslibrary.VirtualMachineTemplate;
import com.abiquo.server.core.appslibrary.VirtualMachineTemplateGenerator;
import com.abiquo.server.core.common.DefaultEntityGenerator;
import com.abiquo.server.core.enterprise.User;
import com.abiquo.server.core.enterprise.UserGenerator;
import com.softwarementors.commons.test.SeedGenerator;
import com.softwarementors.commons.testng.AssertEx;

public class NodeVirtualImageGenerator extends DefaultEntityGenerator<NodeVirtualImage>
{
    private VirtualApplianceGenerator vappGenerator;

    private VirtualMachineGenerator vmGenerator;

    private VirtualMachineTemplateGenerator imageGenerator;

    private UserGenerator userGenerator;

    public NodeVirtualImageGenerator(final SeedGenerator seed)
    {
        super(seed);
        vappGenerator = new VirtualApplianceGenerator(seed);
        vmGenerator = new VirtualMachineGenerator(seed);
        imageGenerator = new VirtualMachineTemplateGenerator(seed);
        userGenerator = new UserGenerator(seed);
    }

    @Override
    public void assertAllPropertiesEqual(final NodeVirtualImage node1, final NodeVirtualImage node2)
    {
        AssertEx.assertPropertiesEqualSilent(node1, node2, Node.MODIFIED_PROPERTY,
            Node.NAME_PROPERTY);

        vappGenerator.assertAllPropertiesEqual(node1.getVirtualAppliance(),
            node2.getVirtualAppliance());
        vmGenerator.assertAllPropertiesEqual(node1.getVirtualMachine(), node2.getVirtualMachine());
        imageGenerator.assertAllPropertiesEqual(node1.getVirtualImage(), node2.getVirtualImage());
    }

    @Override
    public NodeVirtualImage createUniqueInstance()
    {
        VirtualAppliance vapp = vappGenerator.createUniqueInstance();
        return createInstance(vapp);
    }

    public NodeVirtualImage createInstance(final VirtualMachine vm)
    {
        VirtualAppliance vapp =
            vappGenerator.createInstance(vm.getEnterprise(), vm.getHypervisor().getMachine()
                .getDatacenter());
        return createInstance(vapp, vm);
    }

    public NodeVirtualImage createInstance(final VirtualMachineTemplate image)
    {
        VirtualAppliance vapp =
            vappGenerator.createInstance(image.getEnterprise(), image.getRepository()
                .getDatacenter());
        User user = userGenerator.createInstance(image.getEnterprise());

        return createInstance(vapp, user, image);
    }

    public NodeVirtualImage createInstance(final VirtualAppliance vapp)
    {
        User user = userGenerator.createInstance(vapp.getEnterprise());
        return createInstance(vapp, user);
    }

    public NodeVirtualImage createInstance(final VirtualAppliance vapp, final User user)
    {
        VirtualMachineTemplate image =
            imageGenerator.createInstance(vapp.getEnterprise(), vapp.getVirtualDatacenter()
                .getDatacenter());
        return createInstance(vapp, user, image);
    }

    public NodeVirtualImage createInstance(final VirtualAppliance vapp, final User user,
        final VirtualMachineTemplate image)
    {
        VirtualMachine vm = vmGenerator.createInstance(image, vapp.getEnterprise(), user, "TestVM");
        return createInstance(vapp, vm, image);
    }

    public NodeVirtualImage createInstance(final VirtualAppliance virtualAppliance,
        final VirtualMachine vMachine)
    {
        return createInstance(virtualAppliance, vMachine, vMachine.getVirtualMachineTemplate());
    }

    public NodeVirtualImage createInstance(final VirtualAppliance virtualAppliance,
        final VirtualMachine vMachine, final VirtualMachineTemplate vImage)
    {
        String name = newString(nextSeed(), Node.NAME_LENGTH_MIN, Node.NAME_LENGTH_MAX);
        return new NodeVirtualImage(name, virtualAppliance, vImage, vMachine);
    }

    @Override
    public void addAuxiliaryEntitiesToPersist(final NodeVirtualImage entity,
        final List<Object> entitiesToPersist)
    {
        super.addAuxiliaryEntitiesToPersist(entity, entitiesToPersist);

        VirtualAppliance vapp = entity.getVirtualAppliance();
        vappGenerator.addAuxiliaryEntitiesToPersist(vapp, entitiesToPersist);
        entitiesToPersist.add(vapp);

        VirtualMachine vm = entity.getVirtualMachine();
        vmGenerator.addAuxiliaryEntitiesToPersist(vm, entitiesToPersist);
        entitiesToPersist.add(vm);
    }

}
