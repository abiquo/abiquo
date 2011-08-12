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

import org.testng.annotations.Test;

import com.abiquo.server.core.common.DefaultEntityTestBase;
import com.softwarementors.bzngine.entities.test.InstanceTester;

public class IpPoolManagementTest extends DefaultEntityTestBase<IpPoolManagement>
{

    @Override
    protected InstanceTester<IpPoolManagement> createEntityInstanceGenerator()
    {
        return new IpPoolManagementGenerator(getSeed());
    }

    @Test(enabled = false)
    public void testIpType()
    {
        IpPoolManagement ip = createUniqueEntity();

        assertTrue(ip.isPrivateIp());
        assertFalse(ip.isPublicIp());

        ip.setType(IpPoolManagement.Type.PUBLIC);
        assertFalse(ip.isPrivateIp());
        assertTrue(ip.isPublicIp());
    }
}
