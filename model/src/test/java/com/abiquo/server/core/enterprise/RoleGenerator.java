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

import com.abiquo.model.enumerator.Privileges;
import com.abiquo.server.core.common.DefaultEntityGenerator;
import com.softwarementors.commons.test.SeedGenerator;
import com.softwarementors.commons.testng.AssertEx;

public class RoleGenerator extends DefaultEntityGenerator<Role>
{
    PrivilegeGenerator privilegeGenerator;

    EnterpriseGenerator enterpriseGenerator;

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
        Privilege p1 = new Privilege(Privileges.USERS_MANAGE_OTHER_ENTERPRISES);
        Privilege p3 = new Privilege(Privileges.USERS_VIEW);
        Privilege p4 = new Privilege(Privileges.USERS_VIEW_PRIVILEGES);
        Privilege p5 = new Privilege(Privileges.USERS_MANAGE_USERS);
        Privilege p6 = new Privilege(Privileges.USERS_MANAGE_ROLES_OTHER_ENTERPRISES);
        Privilege p7 = new Privilege(Privileges.USERS_MANAGE_SYSTEM_ROLES);
        Privilege p8 = new Privilege(Privileges.USERS_PROHIBIT_VDC_RESTRICTION);
        Privilege p9 = new Privilege(Privileges.USERS_MANAGE_LDAP_GROUP);
        Privilege p10 = new Privilege(Privileges.ENTERPRISE_ADMINISTER_ALL);
        Privilege p11 = new Privilege(Privileges.VDC_ENUMERATE);
        Privilege p12 = new Privilege(Privileges.USERS_MANAGE_ROLES);
        return createInstance(p1, p3, p4, p5, p6, p7, p8, p9, p10, p11, p12);
    }

    public Role createInstanceSysAdmin(final String name)
    {
        Privilege p1 = new Privilege(Privileges.USERS_MANAGE_OTHER_ENTERPRISES);
        Privilege p3 = new Privilege(Privileges.USERS_VIEW);
        Privilege p4 = new Privilege(Privileges.USERS_VIEW_PRIVILEGES);
        Privilege p5 = new Privilege(Privileges.USERS_MANAGE_USERS);
        Privilege p6 = new Privilege(Privileges.USERS_MANAGE_ROLES_OTHER_ENTERPRISES);
        Privilege p7 = new Privilege(Privileges.USERS_MANAGE_SYSTEM_ROLES);
        Privilege p8 = new Privilege(Privileges.USERS_PROHIBIT_VDC_RESTRICTION);
        Privilege p9 = new Privilege(Privileges.USERS_MANAGE_LDAP_GROUP);
        Privilege p10 = new Privilege(Privileges.ENTERPRISE_ADMINISTER_ALL);
        Privilege p11 = new Privilege(Privileges.VDC_ENUMERATE);
        return createInstance(name, p1, p3, p4, p5, p6, p7, p8, p9, p10, p11);
    }

    public Role createInstanceEnterprisAdmin()
    {
        Privilege p3 = new Privilege(Privileges.USERS_VIEW);
        Privilege p4 = new Privilege(Privileges.USERS_VIEW_PRIVILEGES);
        Privilege p5 = new Privilege(Privileges.USERS_MANAGE_USERS);
        Privilege p8 = new Privilege(Privileges.USERS_PROHIBIT_VDC_RESTRICTION);
        Privilege p11 = new Privilege(Privileges.VDC_ENUMERATE);
        return createInstance(p3, p4, p5, p8, p11);
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
    /*
     * private Privilege[] createPrivileges(final List<String> strings) { Privilege[] created = new
     * Privilege[strings.size()]; for (int i = 0; i < strings.size(); i++) { created[i] = new
     * Privilege(strings.get(i)); } return created; } public static List<String> getAllPrivileges()
     * { return getAllPrivileges(""); } public static List<String> getAllPrivileges(final String
     * prefix) { List<String> privileges = new ArrayList<String>(); privileges.add(prefix +
     * Permissions.ENTERPRISE_ENUMERATE); privileges.add(prefix +
     * Permissions.ENTERPRISE_ADMINISTER_ALL); privileges.add(prefix +
     * Permissions.ENTERPRISE_RESOURCE_SUMMARY_ENT); privileges.add(prefix +
     * Permissions.PHYS_DC_ENUMERATE); privileges.add(prefix +
     * Permissions.PHYS_DC_RETRIEVE_RESOURCE_USAGE); privileges.add(prefix +
     * Permissions.PHYS_DC_MANAGE); privileges.add(prefix + Permissions.PHYS_DC_RETRIEVE_DETAILS);
     * privileges.add(prefix + Permissions.PHYS_DC_ALLOW_MODIFY_SERVERS); privileges.add(prefix +
     * Permissions.PHYS_DC_ALLOW_MODIFY_NETWORK); privileges.add(prefix +
     * Permissions.PHYS_DC_ALLOW_MODIFY_STORAGE); privileges.add(prefix +
     * Permissions.PHYS_DC_ALLOW_MODIFY_ALLOCATION); privileges.add(prefix +
     * Permissions.VDC_ENUMERATE); privileges.add(prefix + Permissions.VDC_MANAGE);
     * privileges.add(prefix + Permissions.VDC_MANAGE_VAPP); privileges.add(prefix +
     * Permissions.VDC_MANAGE_NETWORK); privileges.add(prefix + Permissions.VDC_MANAGE_STORAGE);
     * privileges.add(prefix + Permissions.VAPP_CUSTOMISE_SETTINGS); privileges.add(prefix +
     * Permissions.VAPP_DEPLOY_UNDEPLOY); privileges.add(prefix + Permissions.VAPP_ASSIGN_NETWORK);
     * privileges.add(prefix + Permissions.VAPP_ASSIGN_VOLUME); privileges.add(prefix +
     * Permissions.VAPP_PERFORM_ACTIONS); privileges.add(prefix + Permissions.VAPP_CREATE_STATEFUL);
     * privileges.add(prefix + Permissions.VAPP_CREATE_INSTANCE); privileges.add(prefix +
     * Permissions.APPLIB_VIEW); privileges.add(prefix + Permissions.APPLIB_ALLOW_MODIFY);
     * privileges.add(prefix + Permissions.APPLIB_UPLOAD_IMAGE); privileges.add(prefix +
     * Permissions.APPLIB_MANAGE_REPOSITORY); privileges.add(prefix +
     * Permissions.APPLIB_DOWNLOAD_IMAGE); privileges.add(prefix +
     * Permissions.APPLIB_MANAGE_CATEGORIES); privileges.add(prefix + Permissions.USERS_VIEW);
     * privileges.add(prefix + Permissions.USERS_MANAGE_ENTERPRISE); privileges.add(prefix +
     * Permissions.USERS_MANAGE_USERS); privileges.add(prefix +
     * Permissions.USERS_MANAGE_OTHER_ENTERPRISES); privileges.add(prefix +
     * Permissions.USERS_PROHIBIT_VDC_RESTRICTION); privileges.add(prefix +
     * Permissions.USERS_VIEW_PRIVILEGES); privileges.add(prefix + Permissions.USERS_MANAGE_ROLES);
     * privileges.add(prefix + Permissions.USERS_MANAGE_ROLES_OTHER_ENTERPRISES);
     * privileges.add(prefix + Permissions.USERS_MANAGE_SYSTEM_ROLES); privileges.add(prefix +
     * Permissions.USERS_MANAGE_LDAP_GROUP); privileges.add(prefix +
     * Permissions.USERS_ENUMERATE_CONNECTED); privileges.add(prefix +
     * Permissions.USERS_DEFINE_AS_MANAGER); privileges.add(prefix + Permissions.SYSCONFIG_VIEW);
     * privileges.add(prefix + Permissions.SYSCONFIG_ALLOW_MODIFY); privileges.add(prefix +
     * Permissions.EVENTLOG_VIEW_ENTERPRISE); privileges.add(prefix +
     * Permissions.EVENTLOG_VIEW_ALL); privileges.add(prefix + Permissions.APPLIB_VM_COST_CODE);
     * privileges.add(prefix + Permissions.USERS_MANAGE_ENTERPRISE_BRANDING); privileges.add(prefix
     * + Permissions.SYSCONFIG_SHOW_REPORTS); return privileges; }
     */
}
