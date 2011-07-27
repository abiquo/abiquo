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

import com.abiquo.model.enumerator.RemoteServiceType;
import com.abiquo.server.core.common.DefaultEntityGenerator;
import com.abiquo.server.core.infrastructure.RemoteService;
import com.abiquo.server.core.infrastructure.RemoteServiceGenerator;
import com.softwarementors.commons.test.SeedGenerator;
import com.softwarementors.commons.testng.AssertEx;

public class DhcpGenerator extends DefaultEntityGenerator<Dhcp>
{

    RemoteServiceGenerator remoteServiceGenerator;

    public DhcpGenerator(SeedGenerator seed)
    {
        super(seed);

        remoteServiceGenerator = new RemoteServiceGenerator(seed);
    }

    @Override
    public void assertAllPropertiesEqual(Dhcp obj1, Dhcp obj2)
    {
        AssertEx.assertPropertiesEqualSilent(obj1, obj2, Dhcp.ID_PROPERTY);
    }

    @Override
    public Dhcp createUniqueInstance()
    {
        RemoteService remoteService =
            remoteServiceGenerator.createInstance(RemoteServiceType.DHCP_SERVICE);

        Dhcp dhcp = new Dhcp(remoteService);

        return dhcp;
    }

    /**
     * Create the dhcp service form an already created RemoteService
     * 
     * @param dhcpService remote service created
     * @return the generate DHCP.
     */
    public Dhcp createInstance(RemoteService dhcpService)
    {
        return new Dhcp(dhcpService);
    }

    @Override
    public void addAuxiliaryEntitiesToPersist(Dhcp entity, List<Object> entitiesToPersist)
    {
        super.addAuxiliaryEntitiesToPersist(entity, entitiesToPersist);

        RemoteService remoteService = entity.getRemoteService();
        remoteServiceGenerator.addAuxiliaryEntitiesToPersist(remoteService, entitiesToPersist);
        entitiesToPersist.add(remoteService);

    }

}
