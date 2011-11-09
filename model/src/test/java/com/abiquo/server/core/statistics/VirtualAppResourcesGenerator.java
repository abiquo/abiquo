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

public class VirtualAppResourcesGenerator extends DefaultEntityGenerator<VirtualAppResources>
{

    public VirtualAppResourcesGenerator(SeedGenerator seed)
    {
        super(seed);

    }

    @Override
    public void assertAllPropertiesEqual(VirtualAppResources obj1, VirtualAppResources obj2)
    {
        AssertEx.assertPropertiesEqualSilent(obj1, obj2, VirtualAppResources.VDC_NAME_PROPERTY,
            VirtualAppResources.VAPP_NAME_PROPERTY, VirtualAppResources.VOL_ATTACHED_PROPERTY,
            VirtualAppResources.VM_CREATED_PROPERTY, VirtualAppResources.VOL_ASSOCIATED_PROPERTY,
            VirtualAppResources.VM_ACTIVE_PROPERTY, VirtualAppResources.ID_ENTERPRISE_PROPERTY);
    }

    @Override
    public VirtualAppResources createUniqueInstance()
    {
        // FIXME: Write here how to create the pojo

        VirtualAppResources virtualAppResources = new VirtualAppResources();

        return virtualAppResources;
    }

    @Override
    public void addAuxiliaryEntitiesToPersist(VirtualAppResources entity,
        List<Object> entitiesToPersist)
    {
        super.addAuxiliaryEntitiesToPersist(entity, entitiesToPersist);

    }

}
