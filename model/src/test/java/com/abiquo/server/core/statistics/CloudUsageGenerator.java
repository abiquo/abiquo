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

package com.abiquo.server.core.statistics;

import java.util.List;

import com.abiquo.server.core.common.DefaultEntityGenerator;
import com.softwarementors.commons.test.SeedGenerator;
import com.softwarementors.commons.testng.AssertEx;

public class CloudUsageGenerator extends DefaultEntityGenerator<CloudUsage>
{

    public CloudUsageGenerator(SeedGenerator seed)
    {
        super(seed);

    }

    @Override
    public void assertAllPropertiesEqual(CloudUsage obj1, CloudUsage obj2)
    {
        AssertEx.assertPropertiesEqualSilent(obj1, obj2, CloudUsage.V_STORAGE_TOTAL_PROPERTY,
            CloudUsage.V_MEMORY_RESERVED_PROPERTY, CloudUsage.STORAGE_TOTAL_PROPERTY,
            CloudUsage.V_MEMORY_USED_PROPERTY, CloudUsage.V_CPU_TOTAL_PROPERTY,
            CloudUsage.PUBLIC_I_PS_USED_PROPERTY, CloudUsage.PUBLIC_I_PS_RESERVED_PROPERTY,
            CloudUsage.STORAGE_USED_PROPERTY, CloudUsage.SERVERS_TOTAL_PROPERTY,
            CloudUsage.STORAGE_RESERVED_PROPERTY, CloudUsage.PUBLIC_I_PS_TOTAL_PROPERTY,
            CloudUsage.V_STORAGE_USED_PROPERTY, CloudUsage.VLAN_USED_PROPERTY,
            CloudUsage.NUM_VDC_CREATED_PROPERTY, CloudUsage.NUM_ENTERPRISES_CREATED_PROPERTY,
            CloudUsage.V_STORAGE_RESERVED_PROPERTY, CloudUsage.V_MEMORY_TOTAL_PROPERTY,
            CloudUsage.V_CPU_USED_PROPERTY, CloudUsage.V_CPU_RESERVED_PROPERTY,
            CloudUsage.V_MACHINES_TOTAL_PROPERTY, CloudUsage.SERVERS_RUNNING_PROPERTY,
            CloudUsage.NUM_USERS_CREATED_PROPERTY, CloudUsage.V_MACHINES_RUNNING_PROPERTY);
    }

    @Override
    public CloudUsage createUniqueInstance()
    {
        // FIXME: Write here how to create the pojo

        CloudUsage cloudUsage = new CloudUsage();

        return cloudUsage;
    }

    @Override
    public void addAuxiliaryEntitiesToPersist(CloudUsage entity, List<Object> entitiesToPersist)
    {
        super.addAuxiliaryEntitiesToPersist(entity, entitiesToPersist);

    }

}
