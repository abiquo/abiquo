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

import com.abiquo.model.enumerator.HypervisorType;
import com.abiquo.server.core.common.DefaultEntityWithLimitsGenerator;
import com.abiquo.server.core.enterprise.Enterprise;
import com.abiquo.server.core.enterprise.EnterpriseGenerator;
import com.abiquo.server.core.infrastructure.Datacenter;
import com.abiquo.server.core.infrastructure.DatacenterGenerator;
import com.abiquo.server.core.infrastructure.RemoteServiceGenerator;
import com.abiquo.server.core.infrastructure.network.Network;
import com.abiquo.server.core.infrastructure.network.NetworkGenerator;
import com.softwarementors.commons.test.SeedGenerator;
import com.softwarementors.commons.testng.AssertEx;

public class VirtualDatacenterGenerator extends DefaultEntityWithLimitsGenerator<VirtualDatacenter>
{
    RemoteServiceGenerator rsGenerator;

    DatacenterGenerator datacenterGenerator;

    EnterpriseGenerator enterpriseGenerator;

    NetworkGenerator networkGenerator;

    public VirtualDatacenterGenerator(final SeedGenerator seed)
    {
        super(seed);

        datacenterGenerator = new DatacenterGenerator(seed);
        enterpriseGenerator = new EnterpriseGenerator(seed);
        networkGenerator = new NetworkGenerator(seed);
        rsGenerator = new RemoteServiceGenerator(seed);
    }

    @Override
    public void assertAllPropertiesEqual(final VirtualDatacenter obj1, final VirtualDatacenter obj2)
    {
        AssertEx.assertPropertiesEqualSilent(obj1, obj2, VirtualDatacenter.NAME_PROPERTY);
    }

    @Override
    public VirtualDatacenter createUniqueInstance()
    {
        Enterprise enterprise = enterpriseGenerator.createUniqueInstance();
        return createInstance(enterprise);
    }

    public VirtualDatacenter createInstance(final Enterprise enterprise)
    {
        Datacenter datacenter = datacenterGenerator.createUniqueInstance();
        return createInstance(datacenter, enterprise);
    }

    public VirtualDatacenter createInstance(final Datacenter datacenter)
    {
        Enterprise enterprise = enterpriseGenerator.createUniqueInstance();
        return createInstance(datacenter, enterprise);
    }

    public VirtualDatacenter createInstance(final Datacenter datacenter,
        final Enterprise enterprise, final HypervisorType htype, final String name)
    {
        Network network = networkGenerator.createUniqueInstance();
        VirtualDatacenter virtualDatacenter =
            new VirtualDatacenter(enterprise, datacenter, network, htype, name);

        setDefaultLimits(virtualDatacenter);

        return virtualDatacenter;
    }

    public VirtualDatacenter createInstance(final Datacenter datacenter,
        final Enterprise enterprise, final HypervisorType htype)
    {
        final String name =
            newString(nextSeed(), VirtualDatacenter.NAME_LENGTH_MIN,
                VirtualDatacenter.NAME_LENGTH_MAX);

        return createInstance(datacenter, enterprise, htype, name);
    }

    public VirtualDatacenter createInstance(final Datacenter datacenter, final Enterprise enterprise)
    {
        return createInstance(datacenter, enterprise, HypervisorType.VMX_04);
    }

    @Override
    public void addAuxiliaryEntitiesToPersist(final VirtualDatacenter entity,
        final List<Object> entitiesToPersist)
    {
        super.addAuxiliaryEntitiesToPersist(entity, entitiesToPersist);

        Datacenter datacenter = entity.getDatacenter();
        datacenterGenerator.addAuxiliaryEntitiesToPersist(datacenter, entitiesToPersist);
        entitiesToPersist.add(datacenter);

        Enterprise enterprise = entity.getEnterprise();
        enterpriseGenerator.addAuxiliaryEntitiesToPersist(enterprise, entitiesToPersist);
        entitiesToPersist.add(enterprise);

        Network network = entity.getNetwork();
        networkGenerator.addAuxiliaryEntitiesToPersist(network, entitiesToPersist);
        entitiesToPersist.add(network);

    }

}
