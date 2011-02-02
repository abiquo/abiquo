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

package com.abiquo.server.core.infrastructure;

import java.util.List;

import com.abiquo.server.core.common.DefaultEntityGenerator;
import com.softwarementors.commons.test.SeedGenerator;
import com.softwarementors.commons.testng.AssertEx;

public class RepositoryGenerator extends DefaultEntityGenerator<Repository>
{

    DatacenterGenerator datacenterGenerator;

    public RepositoryGenerator(SeedGenerator seed)
    {
        super(seed);

        datacenterGenerator = new DatacenterGenerator(seed);
    }

    @Override
    public void assertAllPropertiesEqual(Repository obj1, Repository obj2)
    {
        AssertEx.assertPropertiesEqualSilent(obj1, obj2, Repository.NAME_PROPERTY,
            Repository.URL_PROPERTY);
    }

    @Override
    public Repository createUniqueInstance()
    {
        Datacenter datacenter = datacenterGenerator.createUniqueInstance();

        return createInstance(datacenter);
    }

    public Repository createInstance(Datacenter datacenter)
    {
        final String url =
            newString(nextSeed(), Repository.URL_LENGTH_MIN, Repository.URL_LENGTH_MAX);

        Repository repository = new Repository(datacenter, url);

        return repository;

    }

    @Override
    public void addAuxiliaryEntitiesToPersist(Repository entity, List<Object> entitiesToPersist)
    {
        super.addAuxiliaryEntitiesToPersist(entity, entitiesToPersist);

        Datacenter datacenter = entity.getDatacenter();
        datacenterGenerator.addAuxiliaryEntitiesToPersist(datacenter, entitiesToPersist);

        entitiesToPersist.add(datacenter);
    }

}
