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

package com.abiquo.server.core.infrastructure;

import java.util.List;

import org.testng.Assert;

import com.abiquo.server.core.cloud.VirtualMachine;
import com.abiquo.server.core.cloud.VirtualMachineGenerator;
import com.abiquo.server.core.common.DefaultEntityGenerator;
import com.softwarementors.commons.test.SeedGenerator;

public class VirtualMachineForHAGenerator extends DefaultEntityGenerator<VirtualMachineForHA>
{
    VirtualMachineGenerator virtualMachineGenerator;

    MachineGenerator machineGenerator;

    public VirtualMachineForHAGenerator(SeedGenerator seed)
    {
        super(seed);

        virtualMachineGenerator = new VirtualMachineGenerator(seed);
        machineGenerator = new MachineGenerator(seed);
    }

    @Override
    public VirtualMachineForHA createUniqueInstance()
    {
        VirtualMachineForHA virtualMachineForHA = new VirtualMachineForHA();

        VirtualMachine virtualMachine = virtualMachineGenerator.createUniqueInstance();
        virtualMachineForHA.setVirtualMachine(virtualMachine);

        Machine machine = machineGenerator.createUniqueInstance();
        virtualMachineForHA.setMachine(machine);

        return virtualMachineForHA;
    }

    @Override
    public void addAuxiliaryEntitiesToPersist(VirtualMachineForHA entity,
        List<Object> entitiesToPersist)
    {
        super.addAuxiliaryEntitiesToPersist(entity, entitiesToPersist);

        VirtualMachine virtualMachine = entity.getVirtualMachine();
        virtualMachineGenerator.addAuxiliaryEntitiesToPersist(virtualMachine, entitiesToPersist);
        entitiesToPersist.add(virtualMachine);

        Machine machine = entity.getMachine();
        machineGenerator.addAuxiliaryEntitiesToPersist(machine, entitiesToPersist);
        entitiesToPersist.add(machine);
    }

    @Override
    public void assertAllPropertiesEqual(VirtualMachineForHA arg0, VirtualMachineForHA arg1)
    {
        Assert.assertEquals(arg0.getVirtualMachine().getId(), arg1.getVirtualMachine().getId());
        Assert.assertEquals(arg0.getMachine().getId(), arg1.getMachine().getId());
    }
}
