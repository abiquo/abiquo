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

import com.abiquo.model.enumerator.VirtualMachineState;
import com.abiquo.server.core.common.DefaultEntityGenerator;
import com.abiquo.server.core.enterprise.Enterprise;
import com.abiquo.server.core.enterprise.EnterpriseGenerator;
import com.softwarementors.commons.test.SeedGenerator;
import com.softwarementors.commons.testng.AssertEx;

public class VirtualApplianceGenerator extends DefaultEntityGenerator<VirtualAppliance>
{

    EnterpriseGenerator enterpriseGenerator;

    VirtualDatacenterGenerator virtualDatacenterGenerator;

    // NodeVirtualImageGenerator nodeGenerator;

    public VirtualApplianceGenerator(SeedGenerator seed)
    {
        super(seed);

        enterpriseGenerator = new EnterpriseGenerator(seed);

        virtualDatacenterGenerator = new VirtualDatacenterGenerator(seed);

        // nodeGenerator = new NodeVirtualImageGenerator(seed);
    }

    @Override
    public void assertAllPropertiesEqual(VirtualAppliance obj1, VirtualAppliance obj2)
    {
        AssertEx.assertPropertiesEqualSilent(obj1, obj2, VirtualAppliance.NAME_PROPERTY,
            VirtualAppliance.NODECONNECTIONS_PROPERTY, VirtualAppliance.PUBLIC_APP_PROPERTY,
            VirtualAppliance.HIGH_DISPONIBILITY_PROPERTY, VirtualAppliance.ERROR_PROPERTY,
            VirtualAppliance.SUB_STATE_PROPERTY, VirtualAppliance.STATE_PROPERTY);
    }

    @Override
    public VirtualAppliance createUniqueInstance()
    {
        String name = newString(nextSeed(), 0, 255);
        VirtualMachineState state = newEnum(VirtualMachineState.class, nextSeed());
        Enterprise enterprise = enterpriseGenerator.createUniqueInstance();
        VirtualDatacenter virtualDatacenter = virtualDatacenterGenerator.createInstance(enterprise);

        VirtualAppliance virtualAppliance =
            new VirtualAppliance(enterprise, virtualDatacenter, name, state, state);

        return virtualAppliance;
    }

    public VirtualAppliance createInstance(VirtualDatacenter virtualDatacenter)
    {
        String name = newString(nextSeed(), 0, 255);
        VirtualMachineState state = newEnum(VirtualMachineState.class, nextSeed());
        Enterprise enterprise = virtualDatacenter.getEnterprise();

        VirtualAppliance virtualAppliance =
            new VirtualAppliance(enterprise, virtualDatacenter, name, state, state);

        return virtualAppliance;
    }

    public VirtualAppliance createInstance(VirtualDatacenter virtualDatacenter, String vappName)
    {
        VirtualMachineState state = newEnum(VirtualMachineState.class, nextSeed());
        Enterprise enterprise = virtualDatacenter.getEnterprise();

        VirtualAppliance virtualAppliance =
            new VirtualAppliance(enterprise, virtualDatacenter, vappName, state, state);

        return virtualAppliance;
    }

    // public VirtualAppliance createInstance(List<VirtualImage> virtualImages)
    // {
    // VirtualAppliance vapp = createUniqueInstance();
    //
    // for (VirtualImage vimage : virtualImages)
    // {
    // NodeVirtualImage node = nodeGenerator.createInstance(vapp, vimage);
    //
    // vapp.addToNodeVirtualImages(node);
    // }
    //
    // return vapp;
    // }

    @Override
    public void addAuxiliaryEntitiesToPersist(VirtualAppliance entity,
        List<Object> entitiesToPersist)
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
