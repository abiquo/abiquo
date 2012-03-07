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

package com.abiquo.server.core.cloud.stateful;

import java.util.List;

import com.abiquo.server.core.cloud.VirtualAppliance;
import com.abiquo.server.core.cloud.VirtualApplianceGenerator;
import com.abiquo.server.core.common.DefaultEntityGenerator;
import com.abiquo.server.core.enterprise.User;
import com.abiquo.server.core.enterprise.UserGenerator;
import com.softwarementors.commons.test.SeedGenerator;

public class VirtualApplianceStatefulConversionGenerator extends
    DefaultEntityGenerator<VirtualApplianceStatefulConversion>
{
    private UserGenerator userGenerator;

    private VirtualApplianceGenerator virtualApplianceGenerator;

    public VirtualApplianceStatefulConversionGenerator(final SeedGenerator seed)
    {
        super(seed);

        userGenerator = new UserGenerator(seed);
        virtualApplianceGenerator = new VirtualApplianceGenerator(seed);
    }

    @Override
    public void assertAllPropertiesEqual(final VirtualApplianceStatefulConversion obj1,
        final VirtualApplianceStatefulConversion obj2)
    {
        userGenerator.assertAllPropertiesEqual(obj1.getUser(), obj2.getUser());

        virtualApplianceGenerator.assertAllPropertiesEqual(obj1.getVirtualAppliance(),
            obj2.getVirtualAppliance());
    }

    @Override
    public VirtualApplianceStatefulConversion createUniqueInstance()
    {
        User user = userGenerator.createUniqueInstance();
        VirtualAppliance virtualAppliance = virtualApplianceGenerator.createUniqueInstance();

        VirtualApplianceStatefulConversion virtualApplianceStatefulConversion =
            new VirtualApplianceStatefulConversion(user, virtualAppliance);

        return virtualApplianceStatefulConversion;
    }

    public VirtualApplianceStatefulConversion createInstance(final User user,
        final VirtualAppliance virtualAppliance)
    {
        VirtualApplianceStatefulConversion virtualApplianceStatefulConversion =
            new VirtualApplianceStatefulConversion(user, virtualAppliance);

        return virtualApplianceStatefulConversion;
    }

    @Override
    public void addAuxiliaryEntitiesToPersist(final VirtualApplianceStatefulConversion entity,
        final List<Object> entitiesToPersist)
    {
        super.addAuxiliaryEntitiesToPersist(entity, entitiesToPersist);

        User user = entity.getUser();
        userGenerator.addAuxiliaryEntitiesToPersist(user, entitiesToPersist);
        entitiesToPersist.add(user);

        VirtualAppliance virtualAppliance = entity.getVirtualAppliance();
        virtualApplianceGenerator
            .addAuxiliaryEntitiesToPersist(virtualAppliance, entitiesToPersist);
        entitiesToPersist.add(virtualAppliance);
    }
}
