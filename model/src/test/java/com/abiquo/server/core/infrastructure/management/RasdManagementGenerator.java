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

package com.abiquo.server.core.infrastructure.management;

import java.util.List;

import com.abiquo.server.core.cloud.VirtualDatacenter;
import com.abiquo.server.core.cloud.VirtualDatacenterGenerator;
import com.abiquo.server.core.common.DefaultEntityGenerator;
import com.softwarementors.commons.test.SeedGenerator;
import com.softwarementors.commons.testng.AssertEx;

public class RasdManagementGenerator extends DefaultEntityGenerator<RasdManagement>

{

    VirtualDatacenterGenerator vdcGen;

    // VirtualApplianceGenerator virtualApplianceGenerator;

    public RasdManagementGenerator(SeedGenerator seed)
    {
        super(seed);
        vdcGen = new VirtualDatacenterGenerator(seed);
        // virtualApplianceGenerator = new VirtualApplianceGenerator(seed);

    }

    @Override
    public void assertAllPropertiesEqual(RasdManagement obj1, RasdManagement obj2)
    {
        AssertEx.assertPropertiesEqualSilent(obj1, obj2, RasdManagement.RASDRAW_PROPERTY,
            RasdManagement.ID_RESOURCE_TYPE_PROPERTY, RasdManagement.VIRTUAL_APPLIANCE_PROPERTY,
            RasdManagement.VIRTUAL_MACHINE_PROPERTY);
    }

    @Override
    public RasdManagement createUniqueInstance()
    {
        String idResource = newString(nextSeed(), 0, 255); // TODO generate resources with sense -->
                                                           // storage and network
        return createInstance(idResource);
    }

    public RasdManagement createInstance(String idResource)
    {

        RasdManagement rasd = new RasdManagement(idResource);
        rasd.setVirtualDatacenter(vdcGen.createUniqueInstance());

        return rasd;
    }

    @Override
    public void addAuxiliaryEntitiesToPersist(RasdManagement entity, List<Object> entitiesToPersist)
    {
        super.addAuxiliaryEntitiesToPersist(entity, entitiesToPersist);

        VirtualDatacenter vdc = entity.getVirtualDatacenter();

        vdcGen.addAuxiliaryEntitiesToPersist(vdc, entitiesToPersist);
        entitiesToPersist.add(vdc);

        // TODO if virtualdatacenter and other relations not null

        // VirtualAppliance virtualAppliance = entity.getVirtualAppliance();
        //
        // virtualApplianceGenerator
        // .addAuxiliaryEntitiesToPersist(virtualAppliance, entitiesToPersist);
        // entitiesToPersist.add(virtualAppliance);
    }

}
