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
 * Boston, MA 02111-1307, USA. */

package com.abiquo.server.core.infrastructure;

import java.util.List;

import org.testng.Assert;

import com.abiquo.model.enumerator.MachineState;
import com.abiquo.server.core.common.DefaultEntityGenerator;
import com.abiquo.server.core.enterprise.Enterprise;
import com.softwarementors.commons.test.SeedGenerator;

public class MachineGenerator extends DefaultEntityGenerator<Machine>
{

    private DatacenterGenerator datacenterGenerator;

    private RackGenerator rackGenerator;

    public MachineGenerator(final SeedGenerator seed)
    {
        super(seed);
        this.datacenterGenerator = new DatacenterGenerator(seed);
        this.rackGenerator = new RackGenerator(seed);
    }

    @Override
    public void assertAllPropertiesEqual(final Machine arg0, final Machine arg1)
    {
        Assert.assertEquals(arg0.getName(), arg1.getName());
    }

    @Override
    public Machine createUniqueInstance()
    {
        Datacenter datacenter = this.datacenterGenerator.createUniqueInstance();
        return createMachine(datacenter);
    }

    public Machine createMachineIntoRack()
    {
        Datacenter datacenter = this.datacenterGenerator.createUniqueInstance();
        Rack rack = rackGenerator.createInstance(datacenter);

        return createMachine(datacenter, rack);
    }

    public Machine createReservedMachine(final Enterprise enterprise)
    {
        Machine machine = createMachineIntoRack();
        machine.setEnterprise(enterprise);
        return machine;
    }

    @Override
    public void addAuxiliaryEntitiesToPersist(final Machine entity,
        final List<Object> entitiesToPersist)
    {
        if (entity.getRack() != null)
        {
            rackGenerator.addAuxiliaryEntitiesToPersist(entity.getRack(), entitiesToPersist);
            entitiesToPersist.add(entity.getRack());
        }
        else
        {
            Datacenter datacenter = entity.getDatacenter();
            this.datacenterGenerator.addAuxiliaryEntitiesToPersist(datacenter, entitiesToPersist);
            entitiesToPersist.add(datacenter);
        }

        super.addAuxiliaryEntitiesToPersist(entity, entitiesToPersist);
    }

    public Machine createMachine(final Datacenter datacenter)
    {
        int seed = nextSeed();

        final String name = newString(seed, Machine.NAME_LENGTH_MIN, Machine.NAME_LENGTH_MAX);
        Machine machine = createMachine(datacenter, name);

        return machine;
    }

    public Machine createMachine(final Datacenter datacenter, final Rack rack)
    {
        Machine machine = createMachine(datacenter);
        machine.setRack(rack);

        return machine;
    }

    public Machine createMachine(final Datacenter datacenter, final String name)
    {
        int seed = nextSeed();

        int virtualRamInMb = seed * 10 + 1;
        int virtualRamUsedInMb = seed * 30 + 1;
        int realCpuThreads = seed + 1;
        int virtualCpusPerThread = 1;
        int currentCpusInUse = seed + 3 + 1;
        MachineState state = newEnum(MachineState.class, seed);
        final String description =
            newString(seed, Machine.DESCRIPTION_LENGTH_MIN, Machine.DESCRIPTION_LENGTH_MAX);

        String virtualSwitch = newString(seed, 1, 255);

        Machine machine =
            datacenter.createMachine(name, description, virtualRamInMb, virtualRamUsedInMb,
                realCpuThreads, currentCpusInUse, virtualCpusPerThread, state, virtualSwitch);
        return machine;
    }

}
