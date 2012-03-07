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

import static org.testng.Assert.assertEquals;

import java.util.List;

import com.abiquo.server.core.common.DefaultEntityGenerator;
import com.softwarementors.commons.test.SeedGenerator;
import com.softwarementors.commons.testng.AssertEx;

public class VirtualDatacenterResourcesGenerator extends
    DefaultEntityGenerator<VirtualDatacenterResources>
{

    public VirtualDatacenterResourcesGenerator(SeedGenerator seed)
    {
        super(seed);

    }

    @Override
    public void assertAllPropertiesEqual(VirtualDatacenterResources obj1,
        VirtualDatacenterResources obj2)
    {
        AssertEx.assertPropertiesEqualSilent(obj1, obj2,
            VirtualDatacenterResources.MEMORY_RESERVED_PROPERTY,
            VirtualDatacenterResources.VDC_NAME_PROPERTY,
            VirtualDatacenterResources.VOL_CREATED_PROPERTY,
            VirtualDatacenterResources.VLAN_RESERVED_PROPERTY,
            VirtualDatacenterResources.PUBLIC_I_PS_USED_PROPERTY,
            VirtualDatacenterResources.PUBLIC_I_PS_RESERVED_PROPERTY,
            VirtualDatacenterResources.LOCAL_STORAGE_RESERVED_PROPERTY,
            VirtualDatacenterResources.EXT_STORAGE_USED_PROPERTY,
            VirtualDatacenterResources.VOL_ATTACHED_PROPERTY,
            VirtualDatacenterResources.VLAN_USED_PROPERTY,
            VirtualDatacenterResources.EXT_STORAGE_RESERVED_PROPERTY,
            VirtualDatacenterResources.LOCAL_STORAGE_USED_PROPERTY,
            VirtualDatacenterResources.V_CPU_USED_PROPERTY,
            VirtualDatacenterResources.V_CPU_RESERVED_PROPERTY,
            VirtualDatacenterResources.VM_CREATED_PROPERTY,
            VirtualDatacenterResources.VOL_ASSOCIATED_PROPERTY,
            VirtualDatacenterResources.VM_ACTIVE_PROPERTY,
            VirtualDatacenterResources.MEMORY_USED_PROPERTY,
            VirtualDatacenterResources.ID_ENTERPRISE_PROPERTY);
    }

    @Override
    public VirtualDatacenterResources createUniqueInstance()
    {
        // FIXME: Write here how to create the pojo
        VirtualDatacenterResources virtualDatacenterResources = new VirtualDatacenterResources();

        return virtualDatacenterResources;
    }

    @Override
    public void addAuxiliaryEntitiesToPersist(VirtualDatacenterResources entity,
        List<Object> entitiesToPersist)
    {
        super.addAuxiliaryEntitiesToPersist(entity, entitiesToPersist);

    }

}
