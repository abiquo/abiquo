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
import java.util.List;

import javax.persistence.EntityManager;

import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import com.abiquo.server.core.cloud.VirtualMachine;
import com.abiquo.server.core.cloud.VirtualMachineGenerator;
import com.abiquo.server.core.common.persistence.DefaultDAOTestBase;
import com.softwarementors.bzngine.entities.test.PersistentInstanceTester;

public class ChefRecipeDAOTest extends DefaultDAOTestBase<ChefRecipeDAO, ChefRecipe>
{
    protected VirtualMachineGenerator vmGenerator;

    @Override
    @BeforeMethod
    protected void methodSetUp()
    {
        super.methodSetUp();
        vmGenerator = new VirtualMachineGenerator(getSeed());
    }

    @Override
    protected ChefRecipeDAO createDao(final EntityManager entityManager)
    {
        return new ChefRecipeDAO(entityManager);
    }

    @Override
    protected PersistentInstanceTester<ChefRecipe> createEntityInstanceGenerator()
    {
        return new ChefRecipeGenerator(getSeed());
    }

    @Override
    public ChefRecipeGenerator eg()
    {
        return (ChefRecipeGenerator) super.eg();
    }

    @Test
    public void testFindByVirtualMachine()
    {
        VirtualMachine vm = vmGenerator.createUniqueInstance();
        ChefRecipe recipe = eg().createInstance(vm);

        List<Object> entitiesToPersist = new ArrayList<Object>();
        eg().addAuxiliaryEntitiesToPersist(recipe, entitiesToPersist);
        persistAll(ds(), entitiesToPersist, recipe);

        ChefRecipeDAO dao = createDaoForRollbackTransaction();

        List<ChefRecipe> chefRecipes = dao.findByVirtualMachine(vm);
        assertEquals(chefRecipes.size(), 1);
    }
}
