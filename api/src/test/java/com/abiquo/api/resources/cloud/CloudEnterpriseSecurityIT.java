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

package com.abiquo.api.resources.cloud;

import java.util.ArrayList;
import java.util.List;

import org.testng.annotations.BeforeMethod;

import com.abiquo.api.resources.AbstractJpaGeneratorIT;
import com.abiquo.model.enumerator.Privileges;
import com.abiquo.server.core.cloud.VirtualAppliance;
import com.abiquo.server.core.cloud.VirtualDatacenter;
import com.abiquo.server.core.common.DefaultEntityBase;
import com.abiquo.server.core.common.EnvironmentGenerator;
import com.abiquo.server.core.enterprise.Enterprise;
import com.abiquo.server.core.enterprise.Privilege;
import com.abiquo.server.core.enterprise.Role;
import com.abiquo.server.core.enterprise.User;

public class CloudEnterpriseSecurityIT extends AbstractJpaGeneratorIT
{
    private final String USER = "simpleuser";

    private EnvironmentGenerator environment;

    private VirtualDatacenter vdc;

    private VirtualAppliance vapp;

    @BeforeMethod
    public void setupEnvironment()
    {
        // Generate the environment
        environment = new EnvironmentGenerator(seed);
        environment.generateEnterprise();
        environment.generateInfrastructure();
        environment.generateVirtualDatacenter();

        setup(environment.getEnvironment().toArray());

        // Get the entities we'll need from the environment
        vdc = environment.get(VirtualDatacenter.class);
        vapp = environment.get(VirtualAppliance.class);
    }

    @BeforeMethod
    public void setupSimpleUser()
    {
        Enterprise ent = enterpriseGenerator.createInstanceNoLimits();
        Role role = roleGenerator.createInstance(Privileges.simpleRole());
        User user = userGenerator.createInstance(ent, role, USER, USER);

        List<DefaultEntityBase> entitiesToSetup = new ArrayList<DefaultEntityBase>();
        entitiesToSetup.add(ent);
        for (Privilege p : role.getPrivileges())
        {
            entitiesToSetup.add(p);
        }
        entitiesToSetup.add(role);
        entitiesToSetup.add(user);
        setup(entitiesToSetup.toArray());

    }

}
