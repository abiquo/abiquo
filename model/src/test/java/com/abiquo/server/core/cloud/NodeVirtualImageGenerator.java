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

import com.abiquo.server.core.appslibrary.VirtualImage;
import com.abiquo.server.core.appslibrary.VirtualImageGenerator;
import com.abiquo.server.core.common.DefaultEntityGenerator;
import com.softwarementors.commons.test.SeedGenerator;
import com.softwarementors.commons.testng.AssertEx;

public class NodeVirtualImageGenerator extends DefaultEntityGenerator<NodeVirtualImage>
{
    private VirtualApplianceGenerator vApplianceGenerator;

    private VirtualMachineGenerator vMachineGenerator;

    private VirtualImageGenerator vImageGenerator;

    public NodeVirtualImageGenerator(final SeedGenerator seed)
    {
        super(seed);
        vApplianceGenerator = new VirtualApplianceGenerator(seed);
        vMachineGenerator = new VirtualMachineGenerator(seed);
        vImageGenerator = new VirtualImageGenerator(seed);
    }

    @Override
    public void assertAllPropertiesEqual(final NodeVirtualImage obj1, final NodeVirtualImage obj2)
    {
        AssertEx
            .assertPropertiesEqualSilent(obj1, obj2, Node.MODIFIED_PROPERTY, Node.NAME_PROPERTY);

        vApplianceGenerator.assertAllPropertiesEqual(obj1.getVirtualAppliance(), obj2
            .getVirtualAppliance());
        vMachineGenerator.assertAllPropertiesEqual(obj1.getVirtualMachine(), obj2
            .getVirtualMachine());
        vImageGenerator.assertAllPropertiesEqual(obj1.getVirtualImage(), obj2.getVirtualImage());
    }

    @Override
    public NodeVirtualImage createUniqueInstance()
    {
        VirtualAppliance vAppliance = vApplianceGenerator.createUniqueInstance();
        VirtualImage vImage = vImageGenerator.createUniqueInstance();
        VirtualMachine vMachine = vMachineGenerator.createInstance(vImage);

        return new NodeVirtualImage("" + new Random().nextInt(), vAppliance, vImage, vMachine);
    }

    public NodeVirtualImage createInstance(final VirtualMachine vMachine)
    {
        VirtualAppliance vAppliance = vApplianceGenerator.createUniqueInstance();
        return createInstance(vAppliance, vMachine);
    }

    public NodeVirtualImage createInstance(final VirtualAppliance virtualAppliance,
        final VirtualImage vimage)
    {
        VirtualMachine vMachine = vMachineGenerator.createInstance(vimage);

        return new NodeVirtualImage("" + new Random().nextInt(), virtualAppliance, vimage, vMachine);
    }

    public NodeVirtualImage createInstance(final VirtualAppliance virtualAppliance,
        final VirtualMachine vMachine)
    {
        return new NodeVirtualImage("" + new Random().nextInt(), virtualAppliance, vMachine
            .getVirtualImage(), vMachine);
    }

    public NodeVirtualImage createInstance(final VirtualAppliance virtualAppliance,
        final VirtualMachine vMachine, final VirtualImage vImage)
    {
        return new NodeVirtualImage("" + new Random().nextInt(), virtualAppliance, vImage, vMachine);
    }

    @Override
    public void addAuxiliaryEntitiesToPersist(final NodeVirtualImage entity,
        final List<Object> entitiesToPersist)
    {
        super.addAuxiliaryEntitiesToPersist(entity, entitiesToPersist);

        VirtualAppliance vAppliance = entity.getVirtualAppliance();
        VirtualMachine vMachine = entity.getVirtualMachine();
        // VirtualImage vImage = entity.getVirtualImage();

        vApplianceGenerator.addAuxiliaryEntitiesToPersist(vAppliance, entitiesToPersist);
        entitiesToPersist.add(vAppliance);

        vMachineGenerator.addAuxiliaryEntitiesToPersist(vMachine, entitiesToPersist);
        entitiesToPersist.add(vMachine);

        // The virtualMachine generator already saves the VirtualImage
        // vImageGenerator.addAuxiliaryEntitiesToPersist(vImage, entitiesToPersist);
        // entitiesToPersist.add(vImage);
    }

}
