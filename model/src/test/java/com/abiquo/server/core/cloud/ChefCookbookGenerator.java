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

import com.abiquo.server.core.common.DefaultEntityGenerator;
import com.softwarementors.commons.test.SeedGenerator;
import com.softwarementors.commons.testng.AssertEx;

public class ChefCookbookGenerator extends DefaultEntityGenerator<ChefCookbook>
{

    VirtualMachineGenerator virtualMachineGenerator;

    public ChefCookbookGenerator(SeedGenerator seed)
    {
        super(seed);

        virtualMachineGenerator = new VirtualMachineGenerator(seed);

    }

    @Override
    public void assertAllPropertiesEqual(ChefCookbook obj1, ChefCookbook obj2)
    {
        AssertEx.assertPropertiesEqualSilent(obj1, obj2, ChefCookbook.COOKBOOK_PROPERTY);
    }

    @Override
    public ChefCookbook createUniqueInstance()
    {
        // FIXME: Write here how to create the pojo
        VirtualMachine virtualMachine = virtualMachineGenerator.createUniqueInstance();

        String cookbook = newString(nextSeed(), 0, 255);
        ChefCookbook chefCookbook = new ChefCookbook(virtualMachine, cookbook);

        return chefCookbook;
    }

    @Override
    public void addAuxiliaryEntitiesToPersist(ChefCookbook entity, List<Object> entitiesToPersist)
    {
        super.addAuxiliaryEntitiesToPersist(entity, entitiesToPersist);

        VirtualMachine virtualMachine = entity.getVirtualmachine();
        virtualMachineGenerator.addAuxiliaryEntitiesToPersist(virtualMachine, entitiesToPersist);
        entitiesToPersist.add(virtualMachine);

    }

}
