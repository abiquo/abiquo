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

import com.abiquo.model.enumerator.RemoteServiceType;
import com.abiquo.server.core.common.DefaultEntityGenerator;
import com.softwarementors.commons.test.SeedGenerator;
import com.softwarementors.commons.testng.AssertEx;

public class RemoteServiceGenerator extends DefaultEntityGenerator<RemoteService>
{

    DatacenterGenerator datacenterGenerator;

    public RemoteServiceGenerator(SeedGenerator seed)
    {
        super(seed);

        datacenterGenerator = new DatacenterGenerator(seed);

    }

    @Override
    public void assertAllPropertiesEqual(RemoteService obj1, RemoteService obj2)
    {
        AssertEx.assertPropertiesEqualSilent(obj1, obj2, RemoteService.URI_PROPERTY,
            RemoteService.TYPE_PROPERTY, RemoteService.STATUS_PROPERTY);
    }

    @Override
    public RemoteService createUniqueInstance()
    {
        return createInstance(RemoteServiceType.VIRTUAL_FACTORY);
    }

    public RemoteService createInstance(RemoteServiceType type)
    {
        Datacenter datacenter = datacenterGenerator.createUniqueInstance();

        return createInstance(type, datacenter);
    }

    public RemoteService createInstance(RemoteServiceType type, Datacenter datacenter)
    {
        return datacenter.createRemoteService(type, "http://localhost:8080/fooo", 1);
    }

    @Override
    public void addAuxiliaryEntitiesToPersist(RemoteService entity, List<Object> entitiesToPersist)
    {
        super.addAuxiliaryEntitiesToPersist(entity, entitiesToPersist);

        Datacenter datacenter = entity.getDatacenter();
        datacenterGenerator.addAuxiliaryEntitiesToPersist(datacenter, entitiesToPersist);
        entitiesToPersist.add(datacenter);

    }

}
