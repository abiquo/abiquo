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

/**
 * abiCloud  community version
 * cloud management application for hybrid clouds
 * Copyright (C) 2008-2010 - Soluciones Grid SL
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

import java.util.Collection;
import java.util.List;

import com.abiquo.server.core.common.DefaultEntityGenerator;
import com.softwarementors.commons.test.SeedGenerator;
import com.softwarementors.commons.testng.AssertEx;

public class DatastoreGenerator extends DefaultEntityGenerator<Datastore>
{

    private MachineGenerator machineGenerator;

    public DatastoreGenerator(SeedGenerator seed)
    {
        super(seed);
        this.machineGenerator = new MachineGenerator(seed);
    }

    @Override
    public void assertAllPropertiesEqual(Datastore obj1, Datastore obj2)
    {
        AssertEx.assertPropertiesEqualSilent(obj1, obj2, Datastore.NAME_PROPERTY,
            Datastore.DIRECTORY_PROPERTY, Datastore.ROOT_PATH_PROPERTY);
    }

    @Override
    public Datastore createUniqueInstance()
    {
        Machine machine = machineGenerator.createMachineIntoRack();
        return createInstance(machine);
    }

    public Datastore createInstance(Machine machine)
    {
        String rootPath = newString(nextSeed(), 1, Integer.MAX_VALUE);
        String name = newString(nextSeed(), 1, Integer.MAX_VALUE);
        String directory = newString(nextSeed(), 1, Integer.MAX_VALUE);

        Datastore datastore = new Datastore(machine, name, rootPath, directory);

        return datastore;
    }

    @Override
    public void addAuxiliaryEntitiesToPersist(Datastore entity, List<Object> entitiesToPersist)
    {
        super.addAuxiliaryEntitiesToPersist(entity, entitiesToPersist);
        Collection<Machine> machines = entity.getMachines();
        for (Machine machine : machines)
        {
            this.machineGenerator.addAuxiliaryEntitiesToPersist(machine, entitiesToPersist);
            entitiesToPersist.add(machine);
        }
    }

}
