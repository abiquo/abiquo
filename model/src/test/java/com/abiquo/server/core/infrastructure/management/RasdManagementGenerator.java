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

package com.abiquo.server.core.infrastructure.management;

import java.util.List;

import com.abiquo.server.core.cloud.VirtualAppliance;
import com.abiquo.server.core.cloud.VirtualApplianceGenerator;
import com.abiquo.server.core.cloud.VirtualDatacenter;
import com.abiquo.server.core.cloud.VirtualDatacenterGenerator;
import com.abiquo.server.core.cloud.VirtualMachine;
import com.abiquo.server.core.cloud.VirtualMachineGenerator;
import com.abiquo.server.core.common.DefaultEntityGenerator;
import com.softwarementors.commons.test.SeedGenerator;
import com.softwarementors.commons.testng.AssertEx;

public class RasdManagementGenerator extends DefaultEntityGenerator<RasdManagement>
{
    private RasdGenerator rasdGenerator;

    private VirtualDatacenterGenerator vdcGenerator;

    private VirtualApplianceGenerator vappGenerator;

    private VirtualMachineGenerator vmGenerator;

    public RasdManagementGenerator(final SeedGenerator seed)
    {
        super(seed);
        rasdGenerator = new RasdGenerator(seed);
        vdcGenerator = new VirtualDatacenterGenerator(seed);
        vappGenerator = new VirtualApplianceGenerator(seed);
        vmGenerator = new VirtualMachineGenerator(seed);
    }

    @Override
    public void assertAllPropertiesEqual(final RasdManagement obj1, final RasdManagement obj2)
    {
        AssertEx.assertPropertiesEqualSilent(obj1, obj2, RasdManagement.ID_RESOURCE_TYPE_PROPERTY);

        if (obj1.getVirtualDatacenter() != null || obj2.getVirtualDatacenter() != null)
        {
            vdcGenerator.assertAllPropertiesEqual(obj1.getVirtualDatacenter(),
                obj2.getVirtualDatacenter());
        }

        if (obj1.getVirtualAppliance() != null || obj2.getVirtualAppliance() != null)
        {
            vappGenerator.assertAllPropertiesEqual(obj1.getVirtualAppliance(),
                obj2.getVirtualAppliance());
        }

        if (obj1.getVirtualMachine() != null || obj2.getVirtualMachine() != null)
        {
            vmGenerator
                .assertAllPropertiesEqual(obj1.getVirtualMachine(), obj2.getVirtualMachine());
        }
    }

    @Override
    public RasdManagement createUniqueInstance()
    {
        String idResource = newString(nextSeed(), 0, 255);
        return createInstance(idResource);
    }

    public RasdManagement createInstance(final String idResource)
    {
        VirtualDatacenter vdc = vdcGenerator.createUniqueInstance();
        return createInstance(idResource, vdc);
    }

    public RasdManagement createInstance(final String idResource, final VirtualDatacenter vdc)
    {
        RasdManagement rasdManagement = new RasdManagement(idResource);
        Rasd rasd = rasdGenerator.createInstance(Integer.valueOf(idResource));

        rasdManagement.setRasd(rasd);
        rasdManagement.setVirtualDatacenter(vdc);

        return rasdManagement;
    }

    @Override
    public void addAuxiliaryEntitiesToPersist(final RasdManagement entity,
        final List<Object> entitiesToPersist)
    {
        super.addAuxiliaryEntitiesToPersist(entity, entitiesToPersist);

        Rasd rasd = entity.getRasd();
        if (rasd != null)
        {
            rasdGenerator.addAuxiliaryEntitiesToPersist(rasd, entitiesToPersist);
            entitiesToPersist.add(rasd);
        }

        VirtualDatacenter vdc = entity.getVirtualDatacenter();
        if (vdc != null)
        {
            vdcGenerator.addAuxiliaryEntitiesToPersist(vdc, entitiesToPersist);
            entitiesToPersist.add(vdc);
        }

        VirtualAppliance vapp = entity.getVirtualAppliance();
        if (vapp != null)
        {
            vappGenerator.addAuxiliaryEntitiesToPersist(vapp, entitiesToPersist);
            entitiesToPersist.add(vapp);
        }

        VirtualMachine vm = entity.getVirtualMachine();
        if (vm != null)
        {
            vmGenerator.addAuxiliaryEntitiesToPersist(vm, entitiesToPersist);
            entitiesToPersist.add(vm);
        }
    }
}
