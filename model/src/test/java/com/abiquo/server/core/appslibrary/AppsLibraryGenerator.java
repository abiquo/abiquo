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
import com.softwarementors.bzngine.entities.PersistentEntity;
import com.softwarementors.commons.test.SeedGenerator;
import com.softwarementors.commons.testng.AssertEx;

public class AppsLibraryGenerator extends DefaultEntityGenerator<AppsLibrary>
{

    EnterpriseGenerator enterpriseGenerator;

    public AppsLibraryGenerator(final SeedGenerator seed)
    {
        super(seed);

        enterpriseGenerator = new EnterpriseGenerator(seed);

    }

    @Override
    public void assertAllPropertiesEqual(final AppsLibrary obj1, final AppsLibrary obj2)
    {
        AssertEx.assertPropertiesEqualSilent(obj1, obj2, PersistentEntity.ID_PROPERTY);
    }

    @Override
    public AppsLibrary createUniqueInstance()
    {
        AppsLibrary appsLibrary = new AppsLibrary();

        Enterprise n1 = enterpriseGenerator.createUniqueInstance();
        appsLibrary.setEnterprise(n1);

        return appsLibrary;
    }

    public AppsLibrary createUniqueInstance(final Enterprise n1)
    {
        AppsLibrary appsLibrary = new AppsLibrary();
        appsLibrary.setEnterprise(n1);
        return appsLibrary;
    }
    
    @Override
    public void addAuxiliaryEntitiesToPersist(final AppsLibrary entity,
        final List<Object> entitiesToPersist)
    {
        super.addAuxiliaryEntitiesToPersist(entity, entitiesToPersist);

        Enterprise n1 = entity.getEnterprise();
        enterpriseGenerator.addAuxiliaryEntitiesToPersist(n1, entitiesToPersist);
        entitiesToPersist.add(n1);

    }

}
