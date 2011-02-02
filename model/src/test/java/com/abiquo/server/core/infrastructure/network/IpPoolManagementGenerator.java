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
import com.softwarementors.commons.test.SeedGenerator;
import com.softwarementors.commons.testng.AssertEx;

public class IpPoolManagementGenerator extends DefaultEntityGenerator<IpPoolManagement>
{

    VLANNetworkGenerator vLANNetworkGenerator;

    DhcpGenerator dhcpGenerator;

    VirtualDatacenterGenerator vdcGenerator;

    public IpPoolManagementGenerator(SeedGenerator seed)
    {
        super(seed);

        vLANNetworkGenerator = new VLANNetworkGenerator(seed);
        dhcpGenerator = new DhcpGenerator(seed);
        vdcGenerator = new VirtualDatacenterGenerator(seed);
    }

    @Override
    public void assertAllPropertiesEqual(IpPoolManagement obj1, IpPoolManagement obj2)
    {
        AssertEx.assertPropertiesEqualSilent(obj1, obj2, IpPoolManagement.NAME_PROPERTY,
            IpPoolManagement.MAC_PROPERTY, IpPoolManagement.CONFIGURATION_GATEWAY_PROPERTY,
            IpPoolManagement.QUARANTINE_PROPERTY, IpPoolManagement.IP_PROPERTY);
    }

    @Override
    public IpPoolManagement createUniqueInstance()
    {
        VirtualDatacenter vdc = vdcGenerator.createUniqueInstance();
        VLANNetwork vlan = vLANNetworkGenerator.createUniqueInstance();
        return createInstance(vdc, vlan);
    }

    public IpPoolManagement createInstance(VirtualDatacenter vdc, Network network)
    {
        VLANNetwork vLANNetwork = vLANNetworkGenerator.createInstance(network);
        return createInstance(vdc, vLANNetwork);
    }

    private IpPoolManagement createInstance(VirtualDatacenter vdc, VLANNetwork vlan)
    {
        String mac = newString(nextSeed(), 0, 255);
        String name = newString(nextSeed(), 0, 255);
        String ip = newString(nextSeed(), 0, 255);
        String networkName = newString(nextSeed(), 0, 255);

        IpPoolManagement ipPoolManagement =
            new IpPoolManagement(vlan.getConfiguration().getDhcp(),
                vlan,
                mac,
                name,
                ip,
                networkName);

        ipPoolManagement.setVirtualDatacenter(vdc);

        return ipPoolManagement;
    }
    
    public IpPoolManagement createInstance(VirtualDatacenter vdc, VLANNetwork vlan, String IPAddress)
    {
        String mac = newString(nextSeed(), 0, 255);
        String name = newString(nextSeed(), 0, 255);
        String ip = IPAddress;
        String networkName = newString(nextSeed(), 0, 255);

        IpPoolManagement ipPoolManagement =
            new IpPoolManagement(vlan.getConfiguration().getDhcp(),
                vlan,
                mac,
                name,
                ip,
                networkName);

        ipPoolManagement.setVirtualDatacenter(vdc);

        return ipPoolManagement;
    }

    @Override
    public void addAuxiliaryEntitiesToPersist(IpPoolManagement entity,
        List<Object> entitiesToPersist)
    {
        super.addAuxiliaryEntitiesToPersist(entity, entitiesToPersist);

        VLANNetwork vLANNetwork = entity.getVlanNetwork();
        vLANNetworkGenerator.addAuxiliaryEntitiesToPersist(vLANNetwork, entitiesToPersist);
        entitiesToPersist.add(vLANNetwork);

        Dhcp dhcp = entity.getDhcp();
        dhcpGenerator.addAuxiliaryEntitiesToPersist(dhcp, entitiesToPersist);
        entitiesToPersist.add(dhcp);

        VirtualDatacenter vdc = entity.getVirtualDatacenter();
        vdcGenerator.addAuxiliaryEntitiesToPersist(vdc, entitiesToPersist);
        entitiesToPersist.add(vdc);
    }

}
