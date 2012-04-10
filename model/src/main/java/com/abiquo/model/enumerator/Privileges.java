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

package com.abiquo.model.enumerator;

/**
 * Existing privileges in the platform
 * 
 * @author dlopez
 */
public enum Privileges
{
    AUTHENTICATED,

    // ENTERPRISE
    ENTERPRISE_ADMINISTER_ALL,

    ENTERPRISE_ENUMERATE,

    // INFRASTRUCTURE
    PHYS_DC_ENUMERATE,

    PHYS_DC_ALLOW_MODIFY_NETWORK,

    PHYS_DC_RETRIEVE_DETAILS,

    // USERS
    USERS_MANAGE_OTHER_ENTERPRISES,

    USERS_MANAGE_ROLES,

    USERS_MANAGE_ROLES_OTHER_ENTERPRISES,

    USERS_MANAGE_SYSTEM_ROLES,

    USERS_MANAGE_USERS,

    USERS_VIEW,

    USERS_VIEW_PRIVILEGES,

    USERS_PROHIBIT_VDC_RESTRICTION,

    USERS_MANAGE_LDAP_GROUP,

    // VDCS
    VDC_ENUMERATE,

    VDC_MANAGE_VAPP,

    // VAPP
    VAPP_CUSTOMISE_SETTINGS,

    VAPP_DEPLOY_UNDEPLOY,

    VAPP_ASSIGN_NETWORK,

    VAPP_ASSIGN_VOLUME,

    VAPP_PERFORM_ACTIONS,

    VAPP_CREATE_STATEFUL,

    VAPP_CREATE_INSTANCE,

    // PRICING
    PRICING_VIEW,

    PRICING_MANAGE,

    // EVENTS
    EVENTLOG_VIEW_ENTERPRISE,

    // GLOBAL CATEGORIES
    APPLIB_MANAGE_GLOBAL_CATEGORIES;

    public static Privileges[] simpleRole()
    {
        Privileges[] p =
            {VDC_ENUMERATE, VDC_MANAGE_VAPP, VAPP_CUSTOMISE_SETTINGS, VAPP_DEPLOY_UNDEPLOY,
            VAPP_ASSIGN_NETWORK, VAPP_ASSIGN_VOLUME, VAPP_PERFORM_ACTIONS, VAPP_CREATE_INSTANCE,
            VAPP_CREATE_STATEFUL, EVENTLOG_VIEW_ENTERPRISE};
        return p;
    }
}
