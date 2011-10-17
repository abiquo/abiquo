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
    ENTRPRISE_ADMINISTER_ALL,

    PHYS_DC_ENUMERATE,

    USERS_MANAGE_OTHER_ENTERPRISES,

    USERS_MANAGE_ROLES,

    USERS_MANAGE_ROLES_OTHER_ENTERPRISES,

    USERS_MANAGE_SYSTEM_ROLES,

    USERS_MANAGE_USERS,

    USERS_VIEW,

    USERS_VIEW_PRIVILEGES,

    USERS_PROHIBIT_VDC_RESTRICTION,

    USERS_MANAGE_LDAP_GROUP,

    VDC_ENUMERATE,

    PRICING_VIEW,
    
    PRICING_MANAGE,

    ENTERPRISE_ENUMERATE;
}
