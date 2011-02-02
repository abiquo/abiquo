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

package com.abiquo.server.core.infrastructure.network;

import java.util.List;

import com.abiquo.server.core.cloud.VirtualDatacenter;
import com.abiquo.server.core.cloud.VirtualDatacenterGenerator;
import com.abiquo.server.core.common.DefaultEntityGenerator;
import com.abiquo.server.core.infrastructure.Rack;
import com.abiquo.server.core.infrastructure.RackGenerator;
import com.softwarementors.commons.test.SeedGenerator;

public class NetworkAssignmentGenerator extends DefaultEntityGenerator<NetworkAssignment>
{
    private RackGenerator rackGen;

    private VLANNetworkGenerator vlanGen;

    private VirtualDatacenterGenerator vdcGen;

    public NetworkAssignmentGenerator(SeedGenerator seed)
    {
        super(seed);

        rackGen = new RackGenerator(seed);
        vlanGen = new VLANNetworkGenerator(seed);
        vdcGen = new VirtualDatacenterGenerator(seed);
    }

    @Override
    public void assertAllPropertiesEqual(NetworkAssignment obj1, NetworkAssignment obj2)
    {
        rackGen.assertAllPropertiesEqual(obj1.getRack(), obj2.getRack());
        vlanGen.assertAllPropertiesEqual(obj1.getVlanNetwork(), obj2.getVlanNetwork());
        vdcGen.assertAllPropertiesEqual(obj1.getVirtualDatacenter(), obj2.getVirtualDatacenter());
    }

    @Override
    public NetworkAssignment createUniqueInstance()
    {
        VirtualDatacenter vdc = vdcGen.createUniqueInstance();
        Rack rack = rackGen.createInstance(vdc.getDatacenter());
        VLANNetwork vlan = vlanGen.createInstance(vdc.getDatacenter().getNetwork());

        NetworkAssignment networkAssignment = new NetworkAssignment(vdc, rack, vlan);

        return networkAssignment;
    }
    
    public NetworkAssignment createInstance(VirtualDatacenter vdc, Rack rack, VLANNetwork vlan)
    {
        NetworkAssignment networkAssignment = new NetworkAssignment(vdc, rack, vlan);

        return networkAssignment;
    }

    @Override
    public void addAuxiliaryEntitiesToPersist(NetworkAssignment entity,
        List<Object> entitiesToPersist)
    {
        super.addAuxiliaryEntitiesToPersist(entity, entitiesToPersist);

        VirtualDatacenter vdc = entity.getVirtualDatacenter();
        vdcGen.addAuxiliaryEntitiesToPersist(vdc, entitiesToPersist);
        entitiesToPersist.add(vdc);

        Rack rack = entity.getRack();
        rackGen.addAuxiliaryEntitiesToPersist(rack, entitiesToPersist);
        entitiesToPersist.add(rack);

        VLANNetwork vlan = entity.getVlanNetwork();
        vlanGen.addAuxiliaryEntitiesToPersist(vlan, entitiesToPersist);
        entitiesToPersist.add(vlan);
    }
}
