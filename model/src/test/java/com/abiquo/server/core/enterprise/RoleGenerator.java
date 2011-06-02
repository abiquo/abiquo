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

import java.util.Collection;
import java.util.List;

import com.abiquo.server.core.common.DefaultEntityGenerator;
import com.softwarementors.commons.test.SeedGenerator;
import com.softwarementors.commons.testng.AssertEx;

public class RoleGenerator extends DefaultEntityGenerator<Role>
{
    PrivilegeGenerator privilegeGenerator;

    EnterpriseGenerator enterpriseGenerator;

    public static final String ENTRPRISE_ADMINISTER_ALL = "ENTRPRISE_ADMINISTER_ALL";

    public static final String USERS_MANAGE_OTHER_ENTERPRISES = "USERS_MANAGE_OTHER_ENTERPRISES";

    public static final String USERS_MANAGE_OTHER_USERS = "USERS_MANAGE_OTHER_USERS";

    public static final String USERS_VIEW = "USERS_VIEW";

    public static final String USERS_VIEW_PRIVILEGES = "USERS_VIEW_PRIVILEGES";

    public static final String USERS_MANAGE_LDAP_GROUP = "USERS_MANAGE_LDAP_GROUP";

    public static final String USERS_MANAGE_USERS = "USERS_MANAGE_USERS";

    public static final String USERS_MANAGE_ROLES = "USERS_MANAGE_ROLES";

    public static final String USERS_MANAGE_ROLES_OTHER_ENTERPRISES =
        "USERS_MANAGE_ROLES_OTHER_ENTERPRISES";

    public static final String USERS_MANAGE_SYSTEM_ROLES = "USERS_MANAGE_SYSTEM_ROLES";

    public static final String USERS_PROHIBIT_VDC_RESTRICTION = "USERS_PROHIBIT_VDC_RESTRICTION";

    public static final String VDC_ENUMERATE = "VDC_ENUMERATE";

    public RoleGenerator(final SeedGenerator seed)
    {
        super(seed);

        enterpriseGenerator = new EnterpriseGenerator(seed);

        privilegeGenerator = new PrivilegeGenerator(seed);
    }

    @Override
    public void assertAllPropertiesEqual(final Role obj1, final Role obj2)
    {
        AssertEx.assertPropertiesEqualSilent(obj1, obj2, Role.NAME_PROPERTY, Role.BLOCKED_PROPERTY,
            Role.ENTERPRISE_PROPERTY);
    }

    @Override
    public Role createUniqueInstance()
    {
        return createInstanceSysAdmin();
    }

    public Role createInstanceSysAdmin()
    {
        Privilege p1 = new Privilege(USERS_MANAGE_OTHER_ENTERPRISES);
        Privilege p2 = new Privilege(USERS_MANAGE_OTHER_USERS);
        Privilege p3 = new Privilege(USERS_VIEW);
        Privilege p4 = new Privilege(USERS_VIEW_PRIVILEGES);
        Privilege p5 = new Privilege(USERS_MANAGE_USERS);
        Privilege p6 = new Privilege(USERS_MANAGE_ROLES_OTHER_ENTERPRISES);
        Privilege p7 = new Privilege(USERS_MANAGE_SYSTEM_ROLES);
        Privilege p8 = new Privilege(USERS_PROHIBIT_VDC_RESTRICTION);
        Privilege p9 = new Privilege(USERS_MANAGE_LDAP_GROUP);
        Privilege p10 = new Privilege(ENTRPRISE_ADMINISTER_ALL);
        Privilege p11 = new Privilege(VDC_ENUMERATE);
        Privilege p12 = new Privilege(USERS_MANAGE_ROLES);
        return createInstance(p1, p2, p3, p4, p5, p6, p7, p8, p9, p10, p11, p12);
    }

    public Role createInstanceSysAdmin(final String name)
    {
        Privilege p1 = new Privilege(USERS_MANAGE_OTHER_ENTERPRISES);
        Privilege p2 = new Privilege(USERS_MANAGE_OTHER_USERS);
        Privilege p3 = new Privilege(USERS_VIEW);
        Privilege p4 = new Privilege(USERS_VIEW_PRIVILEGES);
        Privilege p5 = new Privilege(USERS_MANAGE_USERS);
        Privilege p6 = new Privilege(USERS_MANAGE_ROLES_OTHER_ENTERPRISES);
        Privilege p7 = new Privilege(USERS_MANAGE_SYSTEM_ROLES);
        Privilege p8 = new Privilege(USERS_PROHIBIT_VDC_RESTRICTION);
        Privilege p9 = new Privilege(USERS_MANAGE_LDAP_GROUP);
        Privilege p10 = new Privilege(ENTRPRISE_ADMINISTER_ALL);
        Privilege p11 = new Privilege(VDC_ENUMERATE);
        return createInstance(name, p1, p2, p3, p4, p5, p6, p7, p8, p9, p10, p11);
    }

    public Role createInstanceEnterprisAdmin()
    {
        Privilege p2 = new Privilege(USERS_MANAGE_OTHER_USERS);
        Privilege p3 = new Privilege(USERS_VIEW);
        Privilege p4 = new Privilege(USERS_VIEW_PRIVILEGES);
        Privilege p5 = new Privilege(USERS_MANAGE_USERS);
        Privilege p8 = new Privilege(USERS_PROHIBIT_VDC_RESTRICTION);
        Privilege p11 = new Privilege(VDC_ENUMERATE);
        return createInstance(p2, p3, p4, p5, p8, p11);
    }

    public Role createInstance(final Enterprise enterprise)
    {
        String name = newString(nextSeed(), Role.NAME_LENGTH_MIN, Role.NAME_LENGTH_MAX);

        Role role = new Role(name, enterprise);

        return role;
    }

    public Role createInstance()
    {
        String name = newString(nextSeed(), Role.NAME_LENGTH_MIN, Role.NAME_LENGTH_MAX);

        Role role = new Role(name);

        return role;
    }

    public Role createInstance(final String name)
    {

        Role role = new Role(name);

        return role;
    }

    public Role createInstanceBlocked()
    {
        Role role = createInstance();
        role.setBlocked(true);
        return role;
    }

    public Role createInstanceBlocked(final Privilege... privileges)
    {
        Role role = createInstanceBlocked();
        for (Privilege p : privileges)
        {
            role.addPrivilege(p);
        }
        return role;
    }

    public Role createInstance(final Privilege... privileges)
    {
        Role role = createInstance();
        for (Privilege p : privileges)
        {
            role.addPrivilege(p);
        }
        return role;
    }

    public Role createInstance(final String name, final Privilege... privileges)
    {
        Role role = createInstance(name);
        for (Privilege p : privileges)
        {
            role.addPrivilege(p);
        }
        return role;
    }

    public Role createInstanceEnterprise()
    {
        Enterprise enterprise = enterpriseGenerator.createUniqueInstance();

        return createInstance(enterprise);
    }

    public Role createInstance(final String name, final Enterprise enterprise)
    {

        Role role = new Role(name, enterprise);

        return role;
    }

    @Override
    public void addAuxiliaryEntitiesToPersist(final Role entity,
        final List<Object> entitiesToPersist)
    {
        super.addAuxiliaryEntitiesToPersist(entity, entitiesToPersist);

        Collection<Privilege> privileges = entity.getPrivileges();
        for (Privilege privilege : privileges)
        {
            privilegeGenerator.addAuxiliaryEntitiesToPersist(privilege, entitiesToPersist);
            entitiesToPersist.add(privilege);
        }

    }
}
