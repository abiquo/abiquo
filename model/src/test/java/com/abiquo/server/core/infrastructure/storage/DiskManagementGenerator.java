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

package com.abiquo.server.core.infrastructure.storage;

import java.util.List;

import com.abiquo.server.core.cloud.VirtualMachine;
import com.abiquo.server.core.cloud.VirtualMachineGenerator;
import com.abiquo.server.core.common.DefaultEntityGenerator;
import com.abiquo.server.core.infrastructure.DatastoreGenerator;
import com.abiquo.server.core.infrastructure.management.RasdManagement;
import com.abiquo.server.core.infrastructure.management.RasdManagementGenerator;
import com.softwarementors.commons.test.SeedGenerator;

public class DiskManagementGenerator extends DefaultEntityGenerator<DiskManagement>
{
    private RasdManagementGenerator rasdmGenerator;

    private DatastoreGenerator datastoreGenerator;

    private VirtualMachineGenerator vmGenerator;

    public DiskManagementGenerator(final SeedGenerator seed)
    {
        super(seed);
        rasdmGenerator = new RasdManagementGenerator(seed);
        datastoreGenerator = new DatastoreGenerator(seed);
        vmGenerator = new VirtualMachineGenerator(seed);
    }

    @Override
    public void assertAllPropertiesEqual(final DiskManagement disk1, final DiskManagement disk2)
    {
        rasdmGenerator.assertAllPropertiesEqual(disk1, disk2);

        // Optional properties
        if (disk1.getDatastore() != null)
        {
            datastoreGenerator.assertAllPropertiesEqual(disk1.getDatastore(), disk2.getDatastore());
        }
    }

    @Override
    public DiskManagement createUniqueInstance()
    {
        VirtualMachine vm = vmGenerator.createUniqueInstance();
        return createInstance(vm);
    }

    public DiskManagement createInstance(final VirtualMachine vm)
    {
        return new DiskManagement(vm, (long) nextSeed(), RasdManagement.FIRST_ATTACHMENT_SEQUENCE
            + nextSeed());
    }

    @Override
    public void addAuxiliaryEntitiesToPersist(final DiskManagement entity,
        final List<Object> entitiesToPersist)
    {
        super.addAuxiliaryEntitiesToPersist(entity, entitiesToPersist);
        rasdmGenerator.addAuxiliaryEntitiesToPersist(entity, entitiesToPersist);
    }

}
