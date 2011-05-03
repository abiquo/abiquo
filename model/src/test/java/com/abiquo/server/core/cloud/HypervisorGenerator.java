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

import static org.testng.Assert.assertEquals;

import java.util.List;

import com.abiquo.model.enumerator.HypervisorType;
import com.abiquo.server.core.common.DefaultEntityGenerator;
import com.abiquo.server.core.enterprise.Enterprise;
import com.abiquo.server.core.infrastructure.Machine;
import com.abiquo.server.core.infrastructure.MachineGenerator;
import com.softwarementors.commons.test.SeedGenerator;

public class HypervisorGenerator extends DefaultEntityGenerator<Hypervisor>
{
    private MachineGenerator machineGenerator;

    public HypervisorGenerator(SeedGenerator seed)
    {
        super(seed);
        this.machineGenerator = new MachineGenerator(seed);
    }

    @Override
    public void assertAllPropertiesEqual(Hypervisor obj1, Hypervisor obj2)
    {
        assertEquals(obj1.getId(), obj2.getId());
        assertEquals(obj1.getType(), obj2.getType());
        assertEquals(obj1.getIp(), obj2.getIp());
        assertEquals(obj1.getIpService(), obj2.getIpService());
        assertEquals(obj1.getPort(), obj2.getPort());
        assertEquals(obj1.getUser(), obj2.getUser());
        assertEquals(obj1.getPassword(), obj2.getPassword());
    }

    @Override
    public Hypervisor createUniqueInstance()
    {
        Machine machine = machineGenerator.createMachineIntoRack();

        return createInstance(machine);
    }

    public Hypervisor createInstance(Machine machine)
    {
        HypervisorType type = newEnum(HypervisorType.class, nextSeed());
        return createInstance(machine, type);
    }
    
    public Hypervisor createReservedHypervisor(Enterprise enterprise)
    {
        Machine machine = machineGenerator.createReservedMachine(enterprise);
        
        return createInstance(machine);
    }

    public Hypervisor createInstance(Machine machine, HypervisorType type)
    {
        String ip = newString(nextSeed(), 0, 39);
        String ipService = newString(nextSeed(), 0, 39);
        int port = nextSeed();

        String user = newString(nextSeed(), 0, 255);
        String password = newString(nextSeed(), 0, 255);

        return machine.createHypervisor(type, ip, ipService, port, user, password);
    }

    @Override
    public void addAuxiliaryEntitiesToPersist(Hypervisor entity, List<Object> entitiesToPersist)
    {
        super.addAuxiliaryEntitiesToPersist(entity, entitiesToPersist);
        Machine machine = entity.getMachine();
        this.machineGenerator.addAuxiliaryEntitiesToPersist(machine, entitiesToPersist);
        entitiesToPersist.add(machine);
    }

}
