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

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.testng.annotations.Test;

import com.abiquo.server.core.cloud.chef.RunlistElement.RunlistElementOrder;
import com.abiquo.server.core.common.DefaultEntityTestBase;
import com.softwarementors.bzngine.entities.test.InstanceTester;

public class RunlistElementTest extends DefaultEntityTestBase<RunlistElement>
{

    @Override
    protected InstanceTester<RunlistElement> createEntityInstanceGenerator()
    {
        return new RunlistElementGenerator(getSeed());
    }

    @Test
    public void testIsCookBook()
    {
        RunlistElement recipe = createUniqueEntity();

        recipe.setName("recipe[]");
        assertFalse(recipe.isCookbook());

        recipe.setName("recipe[:]");
        assertTrue(recipe.isCookbook());

        recipe.setName("recipe[testcookbook]");
        assertTrue(recipe.isCookbook());

        recipe.setName("recipe[::]");
        assertFalse(recipe.isCookbook());

        recipe.setName("recipe[test::recipe]");
        assertFalse(recipe.isCookbook());
    }

    @Test
    public void testSortRecipes()
    {
        RunlistElement r1 = createUniqueEntity();
        RunlistElement r2 = createUniqueEntity();

        r1.setName("recipe[AAtestrecipe]");
        r2.setName("recipe[testrecipe::recipe]");

        List<RunlistElement> recipes = new ArrayList<RunlistElement>();
        recipes.add(r2);
        recipes.add(r1);

        Collections.sort(recipes, RunlistElementOrder.BY_NAME);

        assertEquals(recipes.get(0).getName(), r1.getName());
        assertEquals(recipes.get(1).getName(), r2.getName());
    }
}
