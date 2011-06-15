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

package com.abiquo.server.core.enterprise;

import java.util.List;

import com.abiquo.server.core.common.DefaultEntityGenerator;
import com.softwarementors.commons.test.SeedGenerator;
import com.softwarementors.commons.testng.AssertEx;

public class RoleLdapGenerator extends DefaultEntityGenerator<RoleLdap>
{

    private RoleGenerator roleGenerator = null;

    public RoleLdapGenerator(SeedGenerator seed)
    {
        super(seed);
        roleGenerator = new RoleGenerator(seed);
    }

    @Override
    public void assertAllPropertiesEqual(RoleLdap obj1, RoleLdap obj2)
    {
        AssertEx.assertPropertiesEqualSilent(obj1, obj2);
        roleGenerator.assertAllPropertiesEqual(obj1.getRole(), obj2.getRole());
    }

    @Override
    public RoleLdap createUniqueInstance()
    {
        // FIXME: Write here how to create the pojo

        return createInstance(newString(nextSeed(), 1, 128));
    }

    public RoleLdap createUniqueInstance(String type)
    {
        return createInstance(type);
    }

    private RoleLdap createInstance(String type)
    {
        RoleLdap ldapRole = new RoleLdap(type, roleGenerator.createUniqueInstance());

        return ldapRole;
    }

    @Override
    public void addAuxiliaryEntitiesToPersist(RoleLdap entity, List<Object> entitiesToPersist)
    {
        super.addAuxiliaryEntitiesToPersist(entity, entitiesToPersist);

        Role role = entity.getRole();
        roleGenerator.addAuxiliaryEntitiesToPersist(role, entitiesToPersist);
        entitiesToPersist.add(role);

    }

}
