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

import com.abiquo.server.core.cloud.NodeVirtualImage;
import com.abiquo.server.core.cloud.NodeVirtualImageGenerator;
import com.abiquo.server.core.cloud.VirtualAppliance;
import com.abiquo.server.core.cloud.VirtualApplianceGenerator;
import com.abiquo.server.core.common.DefaultEntityGenerator;
import com.abiquo.server.core.infrastructure.Datastore;
import com.abiquo.server.core.infrastructure.DatastoreGenerator;
import com.abiquo.server.core.infrastructure.management.RasdManagementGenerator;
import com.softwarementors.commons.test.SeedGenerator;
import com.softwarementors.commons.testng.AssertEx;

public class DiskManagementGenerator extends DefaultEntityGenerator<DiskManagement>
{
    private RasdManagementGenerator rasdmGenerator;

    private VirtualApplianceGenerator vappGenerator;

    private NodeVirtualImageGenerator nviGenerator;

    private DatastoreGenerator dsGenerator;

    public DiskManagementGenerator(final SeedGenerator seed)
    {
        super(seed);
        rasdmGenerator = new RasdManagementGenerator(seed);
        vappGenerator = new VirtualApplianceGenerator(seed);
        dsGenerator = new DatastoreGenerator(seed);
        nviGenerator = new NodeVirtualImageGenerator(seed);
    }

    @Override
    public void assertAllPropertiesEqual(final DiskManagement obj1, final DiskManagement obj2)
    {
        AssertEx.assertPropertiesEqualSilent(obj1, obj2);

        rasdmGenerator.assertAllPropertiesEqual(obj1, obj2);
    }

    @Override
    public DiskManagement createUniqueInstance()
    {
        NodeVirtualImage nvi = nviGenerator.createUniqueInstance();
        VirtualAppliance vapp = vappGenerator.createUniqueInstance();
        vapp.addToNodeVirtualImages(nvi);
        return new DiskManagement(vapp.getVirtualDatacenter(),
            vapp,
            nvi.getVirtualMachine(),
            dsGenerator.createUniqueInstance(),
            10000L);

    }

    @Override
    public void addAuxiliaryEntitiesToPersist(final DiskManagement entity,
        final List<Object> entitiesToPersist)
    {
        super.addAuxiliaryEntitiesToPersist(entity, entitiesToPersist);

        Datastore ds = entity.getDatastore();
        dsGenerator.addAuxiliaryEntitiesToPersist(ds, entitiesToPersist);
        entitiesToPersist.add(ds);

        rasdmGenerator.addAuxiliaryEntitiesToPersist(entity, entitiesToPersist);
    }

}
