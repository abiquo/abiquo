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

    RoleGenerator roleGenerator;

    public RoleLdapGenerator(final SeedGenerator seed)
    {
        super(seed);
        roleGenerator = new RoleGenerator(seed);
    }

    @Override
    public void assertAllPropertiesEqual(final RoleLdap obj1, final RoleLdap obj2)
    {
        AssertEx.assertPropertiesEqualSilent(obj1, obj2);
        roleGenerator.assertAllPropertiesEqual(obj1.getRole(), obj2.getRole());
    }

    @Override
    public RoleLdap createUniqueInstance()
    {
        String role_ldap =
            newString(nextSeed(), RoleLdap.ROLE_LDAP_LENGTH_MIN, RoleLdap.ROLE_LDAP_LENGTH_MAX);

        RoleLdap roleLdap = new RoleLdap(role_ldap, roleGenerator.createUniqueInstance());

        return roleLdap;
    }

    public RoleLdap createUniqueInstance(final String name)
    {
        RoleLdap roleLdap = new RoleLdap(name, roleGenerator.createUniqueInstance());

        return roleLdap;
    }

    public RoleLdap createInstance(final Role role)
    {
        String role_ldap =
            newString(nextSeed(), RoleLdap.ROLE_LDAP_LENGTH_MIN, RoleLdap.ROLE_LDAP_LENGTH_MAX);

        RoleLdap roleLdap = new RoleLdap(role_ldap, role);

        return roleLdap;
    }

    public RoleLdap createInstance(final String name)
    {
        RoleLdap roleLdap = new RoleLdap(name, roleGenerator.createUniqueInstance());

        return roleLdap;
    }

    @Override
    public void addAuxiliaryEntitiesToPersist(final RoleLdap entity,
        final List<Object> entitiesToPersist)
    {
        super.addAuxiliaryEntitiesToPersist(entity, entitiesToPersist);

        Role role = entity.getRole();
        roleGenerator.addAuxiliaryEntitiesToPersist(role, entitiesToPersist);
        entitiesToPersist.add(role);

    }

}
