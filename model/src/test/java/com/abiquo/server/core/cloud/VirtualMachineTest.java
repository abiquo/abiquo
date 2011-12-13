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

import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import com.abiquo.server.core.appslibrary.VirtualMachineTemplate;
import com.abiquo.server.core.appslibrary.VirtualMachineTemplateGenerator;
import com.abiquo.server.core.common.DefaultEntityTestBase;
import com.abiquo.server.core.enterprise.Enterprise;
import com.abiquo.server.core.enterprise.EnterpriseGenerator;
import com.softwarementors.bzngine.entities.test.InstanceTester;

public class VirtualMachineTest extends DefaultEntityTestBase<VirtualMachine>
{
    private VirtualMachineTemplateGenerator virtualImageGenerator;

    private EnterpriseGenerator enterpriseGenerator;

    @Override
    protected InstanceTester<VirtualMachine> createEntityInstanceGenerator()
    {
        return new VirtualMachineGenerator(getSeed());
    }

    @Override
    @BeforeMethod
    protected void methodSetUp()
    {
        super.methodSetUp();
        virtualImageGenerator = new VirtualMachineTemplateGenerator(getSeed());
        enterpriseGenerator = new EnterpriseGenerator(getSeed());
    }

    @Test
    public void testIsNotChefEnabled()
    {
        VirtualMachine nochef = eg().createUniqueInstance();
        assertFalse(nochef.isChefEnabled());

        Enterprise enterprise = enterpriseGenerator.createChefInstance();
        VirtualMachineTemplate image = virtualImageGenerator.createInstance(enterprise);
        image.setChefEnabled(true);

        VirtualMachine chef =
            ((VirtualMachineGenerator) eg()).createInstance(image, enterprise, "Test");
        assertTrue(chef.isChefEnabled());
    }
}
