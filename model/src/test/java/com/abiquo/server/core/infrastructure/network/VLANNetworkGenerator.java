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
package com.abiquo.server.core.infrastructure.network;

import java.util.List;

import com.abiquo.model.enumerator.NetworkType;
import com.abiquo.server.core.cloud.VirtualDatacenter;
import com.abiquo.server.core.common.DefaultEntityGenerator;
import com.abiquo.server.core.infrastructure.RemoteService;
import com.softwarementors.bzngine.entities.PersistentEntity;
import com.softwarementors.commons.test.SeedGenerator;
import com.softwarementors.commons.testng.AssertEx;

public class VLANNetworkGenerator extends DefaultEntityGenerator<VLANNetwork>
{
    private NetworkGenerator networkGenerator;

    private NetworkConfigurationGenerator configuratorGenerator;

    public VLANNetworkGenerator(final SeedGenerator seed)
    {
        super(seed);
        this.networkGenerator = new NetworkGenerator(seed);
        this.configuratorGenerator = new NetworkConfigurationGenerator(seed);
    }

    @Override
    public void assertAllPropertiesEqual(final VLANNetwork obj1, final VLANNetwork obj2)
    {
        AssertEx.assertPropertiesEqualSilent(obj1, obj2, PersistentEntity.ID_PROPERTY,
            VLANNetwork.NAME_PROPERTY);
    }

    @Override
    public VLANNetwork createUniqueInstance()
    {
        Network network = this.networkGenerator.createUniqueInstance();
        return createInstance(network);
    }

    public VLANNetwork createInstance(final Network network)
    {
        NetworkConfiguration configuration = this.configuratorGenerator.createUniqueInstance();

        final String name =
            newString(nextSeed(), VirtualDatacenter.NAME_LENGTH_MIN,
                VirtualDatacenter.NAME_LENGTH_MAX);

        return new VLANNetwork(name, network, NetworkType.INTERNAL, configuration);
    }

    public VLANNetwork createInstance(final Network network, final RemoteService rsDHCP)
    {
        NetworkConfiguration configuration = this.configuratorGenerator.createInstance(rsDHCP);

        final String name =
            newString(nextSeed(), VirtualDatacenter.NAME_LENGTH_MIN,
                VirtualDatacenter.NAME_LENGTH_MAX);

        return new VLANNetwork(name, network, NetworkType.INTERNAL, configuration);
    }

    public VLANNetwork createInstance(final Network network, final RemoteService rsDHCP,
        final String netmask)
    {
        NetworkConfiguration configuration =
            this.configuratorGenerator.createInstance(rsDHCP, netmask);

        final String name =
            newString(nextSeed(), VirtualDatacenter.NAME_LENGTH_MIN,
                VirtualDatacenter.NAME_LENGTH_MAX);

        return new VLANNetwork(name, network, NetworkType.INTERNAL, configuration);
    }

    public VLANNetwork createInstance(final Network network, final String name)
    {
        NetworkConfiguration configuration = this.configuratorGenerator.createUniqueInstance();

        return new VLANNetwork(name, network, NetworkType.INTERNAL, configuration);
    }

    @Override
    public void addAuxiliaryEntitiesToPersist(final VLANNetwork entity,
        final List<Object> entitiesToPersist)
    {
        super.addAuxiliaryEntitiesToPersist(entity, entitiesToPersist);

        Network network = entity.getNetwork();
        this.networkGenerator.addAuxiliaryEntitiesToPersist(network, entitiesToPersist);
        entitiesToPersist.add(network);

        NetworkConfiguration configuration = entity.getConfiguration();
        this.configuratorGenerator.addAuxiliaryEntitiesToPersist(configuration, entitiesToPersist);
        entitiesToPersist.add(configuration);
    }
}
