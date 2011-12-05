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

import com.abiquo.server.core.cloud.VirtualApplianceGenerator;
import com.abiquo.server.core.cloud.VirtualDatacenter;
import com.abiquo.server.core.cloud.VirtualDatacenterGenerator;
import com.abiquo.server.core.cloud.VirtualMachineGenerator;
import com.abiquo.server.core.common.DefaultEntityGenerator;
import com.abiquo.server.core.infrastructure.management.RasdManagementGenerator;
import com.softwarementors.commons.test.SeedGenerator;
import com.softwarementors.commons.testng.AssertEx;

public class IpPoolManagementGenerator extends DefaultEntityGenerator<IpPoolManagement>
{

    private VLANNetworkGenerator vlanNetworkGenerator;

    private RasdManagementGenerator rasdmGenerator;

    private VirtualDatacenterGenerator vdcGenerator;

    private VirtualApplianceGenerator vappGenerator;

    private VirtualMachineGenerator vmGenerator;

    public IpPoolManagementGenerator(final SeedGenerator seed)
    {
        super(seed);

        vlanNetworkGenerator = new VLANNetworkGenerator(seed);
        vdcGenerator = new VirtualDatacenterGenerator(seed);
        rasdmGenerator = new RasdManagementGenerator(seed);
        vappGenerator = new VirtualApplianceGenerator(seed);
        vmGenerator = new VirtualMachineGenerator(seed);
    }

    @Override
    public void assertAllPropertiesEqual(final IpPoolManagement obj1, final IpPoolManagement obj2)
    {
        AssertEx.assertPropertiesEqualSilent(obj1, obj2, IpPoolManagement.NAME_PROPERTY,
            IpPoolManagement.MAC_PROPERTY, IpPoolManagement.CONFIGURATION_GATEWAY_PROPERTY,
            IpPoolManagement.QUARANTINE_PROPERTY, IpPoolManagement.IP_PROPERTY);

        vlanNetworkGenerator.assertAllPropertiesEqual(obj1.getVlanNetwork(), obj2.getVlanNetwork());
        rasdmGenerator.assertAllPropertiesEqual(obj1, obj2);
    }

    @Override
    public IpPoolManagement createUniqueInstance()
    {
        VirtualDatacenter vdc = vdcGenerator.createUniqueInstance();
        VLANNetwork vlan = vlanNetworkGenerator.createUniqueInstance();
        return createInstance(vdc, vlan);
    }

    public IpPoolManagement createInstance(final VirtualDatacenter vdc, final Network network)
    {
        VLANNetwork vLANNetwork = vlanNetworkGenerator.createInstance(network);
        return createInstance(vdc, vLANNetwork);
    }

    private IpPoolManagement createInstance(final VirtualDatacenter vdc, final VLANNetwork vlan)
    {
        String mac = newString(nextSeed(), 0, 255);
        String name = newString(nextSeed(), 0, 255);
        String ip = newString(nextSeed(), 0, 255);
        String networkName = newString(nextSeed(), 0, 255);

        IpPoolManagement ipPoolManagement =
            new IpPoolManagement(vlan, mac, name, ip, networkName, IpPoolManagement.Type.PRIVATE);

        ipPoolManagement.setVirtualDatacenter(vdc);

        return ipPoolManagement;
    }

    public IpPoolManagement createInstance(final VirtualDatacenter vdc, final VLANNetwork vlan,
        final String IPAddress)
    {
        String mac = newString(nextSeed(), 0, 255);
        String name = newString(nextSeed(), 0, 255);
        String ip = IPAddress;
        String networkName = newString(nextSeed(), 0, 255);

        IpPoolManagement ipPoolManagement =
            new IpPoolManagement(vlan, mac, name, ip, networkName, IpPoolManagement.Type.PRIVATE);

        ipPoolManagement.setVirtualDatacenter(vdc);

        return ipPoolManagement;
    }

    @Override
    public void addAuxiliaryEntitiesToPersist(final IpPoolManagement entity,
        final List<Object> entitiesToPersist)
    {
        super.addAuxiliaryEntitiesToPersist(entity, entitiesToPersist);

        VLANNetwork vlanNetwork = entity.getVlanNetwork();
        vlanNetworkGenerator.addAuxiliaryEntitiesToPersist(vlanNetwork, entitiesToPersist);
        entitiesToPersist.add(vlanNetwork);

        rasdmGenerator.addAuxiliaryEntitiesToPersist(entity, entitiesToPersist);
    }

}
