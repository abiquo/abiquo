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

package com.abiquo.api.services.cloud;

import static com.abiquo.api.common.UriTestResolver.resolveVirtualMachineDeployURI;
import static org.testng.Assert.assertEquals;

import org.apache.wink.client.ClientResponse;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import com.abiquo.api.resources.AbstractJpaGeneratorIT;
import com.abiquo.server.core.cloud.VirtualAppliance;
import com.abiquo.server.core.cloud.VirtualDatacenter;
import com.abiquo.server.core.cloud.VirtualMachine;
import com.abiquo.server.core.cloud.VirtualMachineDeployDto;
import com.abiquo.server.core.common.EnvironmentGenerator;

/**
 * Integration tests to verify virtual machine locking logic.
 * 
 * @author Ignasi Barrera
 */
public class VirtualMachineLockIT extends AbstractJpaGeneratorIT
{
    private EnvironmentGenerator env;

    private VirtualDatacenter vdc;

    private VirtualAppliance vapp;

    private VirtualMachine vm;

    @BeforeMethod
    @Override
    public void setup()
    {
        super.setup();

        env = new EnvironmentGenerator(seed);
        env.generateEnterprise();
        env.generateInfrastructure();
        env.generateVirtualDatacenter();
        env.generateNotAllocatedVirtualMachine();

        setup(env.getEnvironment().toArray());

        vdc = env.get(VirtualDatacenter.class);
        vapp = env.get(VirtualAppliance.class);
        vm = env.get(VirtualMachine.class);
    }

    @Test
    public void testDeploy()
    {
        VirtualMachineDeployDto dto = new VirtualMachineDeployDto();
        dto.setForceEnterpriseSoftLimits(false);

        String uri = resolveVirtualMachineDeployURI(vdc.getId(), vapp.getId(), vm.getId());

        ClientResponse response =
            post(uri, dto, EnvironmentGenerator.SYSADMIN, EnvironmentGenerator.SYSADMIN);

        assertEquals(response.getStatusCode(), 202);
    }
}
