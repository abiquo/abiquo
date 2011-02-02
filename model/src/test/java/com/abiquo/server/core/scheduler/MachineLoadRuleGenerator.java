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

package com.abiquo.server.core.scheduler;

import java.util.List;
import java.util.Random;

import com.abiquo.server.core.common.DefaultEntityGenerator;
import com.abiquo.server.core.infrastructure.DatacenterGenerator;
import com.abiquo.server.core.infrastructure.Machine;
import com.abiquo.server.core.infrastructure.MachineGenerator;
import com.abiquo.server.core.infrastructure.RackGenerator;
import com.softwarementors.commons.test.SeedGenerator;
import com.softwarementors.commons.testng.AssertEx;

public class MachineLoadRuleGenerator extends DefaultEntityGenerator<MachineLoadRule>
{

    MachineGenerator machineGen;

    DatacenterGenerator datacenterGen;

    RackGenerator rackGen;

    public MachineLoadRuleGenerator(SeedGenerator seed)
    {
        super(seed);
        machineGen = new MachineGenerator(seed);
        datacenterGen = new DatacenterGenerator(seed);
        rackGen = new RackGenerator(seed);

    }

    @Override
    public void assertAllPropertiesEqual(MachineLoadRule obj1, MachineLoadRule obj2)
    {
        AssertEx.assertPropertiesEqualSilent(obj1, obj2,
            MachineLoadRule.CPU_LOAD_PERCENTAGE_PROPERTY,
            MachineLoadRule.RAM_LOAD_PERCENTAGE_PROPERTY);
    }

    @Override
    public MachineLoadRule createUniqueInstance()
    {

        // Machine machine = machineGen.createUniqueInstance();

        int cpuLoadPercentage = new Random(nextSeed()).nextInt(100);
        int ramLoadPercentage = new Random(nextSeed()).nextInt(100);

        // created rule applies to machines
        MachineLoadRule machineLoadRule = new MachineLoadRule(cpuLoadPercentage, ramLoadPercentage);

        return machineLoadRule;
    }

    public MachineLoadRule createInstance(Machine machine)
    {
        MachineLoadRule machineLoadRule = createUniqueInstance();

        machineLoadRule.setMachine(machine);

        return machineLoadRule;
    }

    @Override
    public void addAuxiliaryEntitiesToPersist(MachineLoadRule entity, List<Object> entitiesToPersist)
    {

        super.addAuxiliaryEntitiesToPersist(entity, entitiesToPersist);

        Machine machine = entity.getMachine();
        if (machine != null)
        {
            machineGen.addAuxiliaryEntitiesToPersist(machine, entitiesToPersist);
            entitiesToPersist.add(machine);
        }
        // else
        // {
        // Rack rack = entity.getRack();
        //
        // if (rack != null)
        // {
        // rackGen.addAuxiliaryEntitiesToPersist(rack, entitiesToPersist);
        // entitiesToPersist.add(rack);
        // }
        // else
        // {
        // Datacenter datacenter = entity.getDatacenter();
        // // if no machine and no rack then a datacenter exist
        // datacenterGen.addAuxiliaryEntitiesToPersist(datacenter, entitiesToPersist);
        // entitiesToPersist.add(datacenter);
        //
        // }// no rack
        // }// no machien
    }
}
