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
 * Boston, MA 02111-1307, USA. */
package com.abiquo.server.core.infrastructure;

import java.util.List;

import com.abiquo.server.core.common.DefaultEntityGenerator;
import com.abiquo.server.core.infrastructure.network.Network;
import com.abiquo.server.core.infrastructure.network.NetworkGenerator;
import com.softwarementors.commons.test.SeedGenerator;
import com.softwarementors.commons.testng.AssertEx;

public class DatacenterGenerator extends DefaultEntityGenerator<Datacenter>
{

    private NetworkGenerator networkGenerator;

    public DatacenterGenerator(SeedGenerator seed)
    {
        super(seed);

        networkGenerator = new NetworkGenerator(seed);
    }

    @Override
    public void assertAllPropertiesEqual(Datacenter arg0, Datacenter arg1)
    {
        AssertEx.assertPropertiesEqualSilent(arg0, arg1, Datacenter.NAME_PROPERTY,
            Datacenter.LOCATION_PROPERTY, Datacenter.ID_PROPERTY);

        networkGenerator.assertAllPropertiesEqual(arg0.getNetwork(), arg1.getNetwork());
    }

    @Override
    public Datacenter createUniqueInstance()
    {
        int seed = nextSeed();
        final String name = newString(seed, Datacenter.NAME_LENGTH_MIN, Datacenter.NAME_LENGTH_MAX);

        return createInstance(name);
    }

    public Datacenter createInstance(String name)
    {
        int seed = nextSeed();
        final String situation =
            newString(seed, Datacenter.LOCATION_LENGTH_MIN, Datacenter.LOCATION_LENGTH_MAX);

        Datacenter dc = new Datacenter(name, situation);
        dc.setNetwork(networkGenerator.createUniqueInstance());

        return dc;
    }

    @Override
    public void addAuxiliaryEntitiesToPersist(Datacenter entity, List<Object> entitiesToPersist)
    {
        Network network = entity.getNetwork();

        if (network != null)
        {
            networkGenerator.addAuxiliaryEntitiesToPersist(network, entitiesToPersist);
            entitiesToPersist.add(network);
        }

        super.addAuxiliaryEntitiesToPersist(entity, entitiesToPersist);

    }
}
