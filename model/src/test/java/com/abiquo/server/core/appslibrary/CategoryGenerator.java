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

package com.abiquo.server.core.appslibrary;

import java.util.List;

import com.abiquo.server.core.common.DefaultEntityGenerator;
import com.abiquo.server.core.enterprise.Enterprise;
import com.abiquo.server.core.enterprise.EnterpriseGenerator;
import com.softwarementors.commons.test.SeedGenerator;
import com.softwarementors.commons.testng.AssertEx;

public class CategoryGenerator extends DefaultEntityGenerator<Category>
{

    private final EnterpriseGenerator enterpriseGenerator;

    public CategoryGenerator(final SeedGenerator seed)
    {
        super(seed);
        enterpriseGenerator = new EnterpriseGenerator(seed);
    }

    @Override
    public void assertAllPropertiesEqual(final Category obj1, final Category obj2)
    {
        AssertEx.assertPropertiesEqualSilent(obj1, obj2, Category.NAME_PROPERTY,
            Category.DEFAULT_PROPERTY, Category.ERASABLE_PROPERTY);
        if (obj1.getEnterprise() != null)
        {
            enterpriseGenerator
                .assertAllPropertiesEqual(obj1.getEnterprise(), obj2.getEnterprise());
        }
    }

    @Override
    public Category createUniqueInstance()
    {
        String name = newString(nextSeed(), Category.NAME_LENGTH_MIN, Category.NAME_LENGTH_MAX);
        return new Category(name);
    }

    public Category createDefaultInstance()
    {
        String name = newString(nextSeed(), Category.NAME_LENGTH_MIN, Category.NAME_LENGTH_MAX);
        return Category.defaultCategory(name);
    }

    @Override
    public void addAuxiliaryEntitiesToPersist(final Category entity,
        final List<Object> entitiesToPersist)
    {
        super.addAuxiliaryEntitiesToPersist(entity, entitiesToPersist);
        if (entity.getEnterprise() != null)
        {
            Enterprise enterprise = entity.getEnterprise();
            enterpriseGenerator.addAuxiliaryEntitiesToPersist(enterprise, entitiesToPersist);
            entitiesToPersist.add(enterprise);
        }
    }
}
