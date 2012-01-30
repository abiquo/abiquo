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

import com.abiquo.server.core.common.DefaultEntityGenerator;
import com.abiquo.server.core.enterprise.Enterprise;
import com.abiquo.server.core.enterprise.EnterpriseGenerator;
import com.abiquo.server.core.infrastructure.Datacenter;
import com.softwarementors.commons.test.SeedGenerator;
import com.softwarementors.commons.testng.AssertEx;

public class VirtualApplianceGenerator extends DefaultEntityGenerator<VirtualAppliance>
{
    private EnterpriseGenerator enterpriseGenerator;

    private VirtualDatacenterGenerator virtualDatacenterGenerator;

    public VirtualApplianceGenerator(final SeedGenerator seed)
    {
        super(seed);

        enterpriseGenerator = new EnterpriseGenerator(seed);
        virtualDatacenterGenerator = new VirtualDatacenterGenerator(seed);
    }

    @Override
    public void assertAllPropertiesEqual(final VirtualAppliance obj1, final VirtualAppliance obj2)
    {
        AssertEx.assertPropertiesEqualSilent(obj1, obj2, VirtualAppliance.NAME_PROPERTY,
            VirtualAppliance.NODECONNECTIONS_PROPERTY, VirtualAppliance.PUBLIC_APP_PROPERTY,
            VirtualAppliance.HIGH_DISPONIBILITY_PROPERTY, VirtualAppliance.ERROR_PROPERTY,
            VirtualAppliance.STATE_PROPERTY);
    }

    @Override
    public VirtualAppliance createUniqueInstance()
    {
        Enterprise enterprise = enterpriseGenerator.createUniqueInstance();
        return createInstance(enterprise);
    }

    public VirtualAppliance createInstance(final Enterprise enterprise)
    {
        String name = newString(nextSeed(), 0, 255);
        VirtualApplianceState state = VirtualApplianceState.NOT_DEPLOYED;
        VirtualDatacenter virtualDatacenter = virtualDatacenterGenerator.createInstance(enterprise);

        VirtualAppliance virtualAppliance =
            new VirtualAppliance(enterprise, virtualDatacenter, name, state);

        return virtualAppliance;
    }

    public VirtualAppliance createInstance(final Enterprise enterprise, final Datacenter datacenter)
    {
        String name = newString(nextSeed(), 0, 255);
        VirtualApplianceState state = VirtualApplianceState.NOT_DEPLOYED;
        VirtualDatacenter virtualDatacenter =
            virtualDatacenterGenerator.createInstance(datacenter, enterprise);

        VirtualAppliance virtualAppliance =
            new VirtualAppliance(enterprise, virtualDatacenter, name, state);

        return virtualAppliance;
    }

    public VirtualAppliance createInstance(final VirtualDatacenter virtualDatacenter)
    {
        String name = newString(nextSeed(), 0, 255);
        VirtualApplianceState state = newEnum(VirtualApplianceState.class, nextSeed());
        Enterprise enterprise = virtualDatacenter.getEnterprise();

        VirtualAppliance virtualAppliance =
            new VirtualAppliance(enterprise, virtualDatacenter, name, state);

        return virtualAppliance;
    }

    public VirtualAppliance createInstance(final VirtualDatacenter virtualDatacenter,
        final String vappName)
    {
        VirtualApplianceState state = newEnum(VirtualApplianceState.class, nextSeed());
        Enterprise enterprise = virtualDatacenter.getEnterprise();

        VirtualAppliance virtualAppliance =
            new VirtualAppliance(enterprise, virtualDatacenter, vappName, state);

        return virtualAppliance;
    }

    @Override
    public void addAuxiliaryEntitiesToPersist(final VirtualAppliance entity,
        final List<Object> entitiesToPersist)
    {
        super.addAuxiliaryEntitiesToPersist(entity, entitiesToPersist);

        Enterprise enterprise = entity.getEnterprise();
        enterpriseGenerator.addAuxiliaryEntitiesToPersist(enterprise, entitiesToPersist);
        entitiesToPersist.add(enterprise);

        VirtualDatacenter virtualDatacenter = entity.getVirtualDatacenter();
        virtualDatacenterGenerator.addAuxiliaryEntitiesToPersist(virtualDatacenter,
            entitiesToPersist);
        entitiesToPersist.add(virtualDatacenter);

        // TODO ??? add the node virtual image ???

    }

}
