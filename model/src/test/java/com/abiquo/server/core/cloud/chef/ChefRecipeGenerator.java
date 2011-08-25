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

package com.abiquo.server.core.cloud.chef;

import java.util.List;

import com.abiquo.server.core.cloud.VirtualMachine;
import com.abiquo.server.core.cloud.VirtualMachineGenerator;
import com.abiquo.server.core.common.DefaultEntityGenerator;
import com.softwarementors.commons.test.SeedGenerator;
import com.softwarementors.commons.testng.AssertEx;

public class ChefRecipeGenerator extends DefaultEntityGenerator<ChefRecipe>
{
    protected VirtualMachineGenerator virtualMachineGenerator;

    public ChefRecipeGenerator(final SeedGenerator seed)
    {
        super(seed);
        virtualMachineGenerator = new VirtualMachineGenerator(seed);
    }

    @Override
    public void assertAllPropertiesEqual(final ChefRecipe obj1, final ChefRecipe obj2)
    {
        AssertEx.assertPropertiesEqualSilent(obj1, obj2, ChefRecipe.NAME_PROPERTY,
            ChefRecipe.DESCRIPTION_PROPERTY);

        virtualMachineGenerator.assertAllPropertiesEqual(obj1.getVirtualMachine(),
            obj2.getVirtualMachine());
    }

    @Override
    public ChefRecipe createUniqueInstance()
    {
        VirtualMachine virtualMachine = virtualMachineGenerator.createUniqueInstance();
        return createInstance(virtualMachine);
    }

    public ChefRecipe createInstanceWithoutVirtualMachine()
    {
        return createInstance(null);
    }

    public ChefRecipe createInstance(final VirtualMachine virtualMachine)
    {
        String name = newString(nextSeed(), ChefRecipe.NAME_LENGTH_MIN, ChefRecipe.NAME_LENGTH_MAX);
        return createInstance(name, virtualMachine);
    }

    public ChefRecipe createInstance(final String name, final VirtualMachine virtualMachine)
    {
        String description =
            newString(nextSeed(), ChefRecipe.DESCRIPTION_LENGTH_MIN,
                ChefRecipe.DESCRIPTION_LENGTH_MAX);

        ChefRecipe recipe = new ChefRecipe(name, description);
        recipe.setVirtualMachine(virtualMachine);

        return recipe;
    }

    @Override
    public void addAuxiliaryEntitiesToPersist(final ChefRecipe entity,
        final List<Object> entitiesToPersist)
    {
        super.addAuxiliaryEntitiesToPersist(entity, entitiesToPersist);

        VirtualMachine virtualMachine = entity.getVirtualMachine();
        if (virtualMachine != null)
        {
            virtualMachineGenerator
                .addAuxiliaryEntitiesToPersist(virtualMachine, entitiesToPersist);
            entitiesToPersist.add(virtualMachine);
        }
    }

}
