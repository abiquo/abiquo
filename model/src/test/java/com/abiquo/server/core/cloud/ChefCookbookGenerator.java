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

import com.abiquo.server.core.cloud.chef.ChefCookbook;
import com.abiquo.server.core.common.DefaultEntityGenerator;
import com.softwarementors.commons.test.SeedGenerator;
import com.softwarementors.commons.testng.AssertEx;

public class ChefCookbookGenerator extends DefaultEntityGenerator<ChefCookbook>
{

    VirtualMachineGenerator virtualMachineGenerator;

    public ChefCookbookGenerator(final SeedGenerator seed)
    {
        super(seed);

        virtualMachineGenerator = new VirtualMachineGenerator(seed);

    }

    @Override
    public void assertAllPropertiesEqual(final ChefCookbook obj1, final ChefCookbook obj2)
    {
        AssertEx.assertPropertiesEqualSilent(obj1, obj2, ChefCookbook.COOKBOOK_PROPERTY);
    }

    @Override
    public ChefCookbook createUniqueInstance()
    {
        VirtualMachine virtualMachine = virtualMachineGenerator.createUniqueInstance();

        String version = newString(nextSeed(), 0, 5);
        String cookbook = newString(nextSeed(), 0, 255);
        ChefCookbook chefCookbook = new ChefCookbook(virtualMachine, cookbook, version);

        return chefCookbook;
    }

    public ChefCookbook createInstance(final VirtualMachine virtualMachine, final String cookbook,
        final String version)
    {
        ChefCookbook chefCookbook = new ChefCookbook(virtualMachine, cookbook, version);

        return chefCookbook;
    }

    public ChefCookbook createInstanceWithoutVirtualMachine()
    {
        String version = newString(nextSeed(), 0, 5);
        String cookbook = newString(nextSeed(), 0, 255);
        ChefCookbook chefCookbook = new ChefCookbook(null, cookbook, version);
        chefCookbook.setVersion(version);

        return chefCookbook;
    }

    @Override
    public void addAuxiliaryEntitiesToPersist(final ChefCookbook entity,
        final List<Object> entitiesToPersist)
    {
        super.addAuxiliaryEntitiesToPersist(entity, entitiesToPersist);

        VirtualMachine virtualMachine = entity.getVirtualmachine();
        virtualMachineGenerator.addAuxiliaryEntitiesToPersist(virtualMachine, entitiesToPersist);
        entitiesToPersist.add(virtualMachine);

    }

}
