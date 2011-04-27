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

package com.abiquo.api.spring.security;

import org.springframework.security.context.SecurityContextHolder;
import org.springframework.security.util.AuthorityUtils;
import org.springframework.stereotype.Service;

/**
 * Security Service to check user privileges
 * 
 * @author aprete
 */

@Service
public class SecurityService
{
    public static final String OTHER_ENTERPRISES_PRIVILEGE = "USERS_MANAGE_OTHER_ENTERPRISES";

    public static final String OTHER_USERS_PRIVILEGE = "USERS_MANAGE_OTHER_USERS";

    /*
     * public static final String ROLES_PRIVILEGE = "USERS_MANAGE_ROLES"; public static final String
     * OTHER_ENTERPRISES_ROLES_PRIVILEGE = "USERS_MANAGE_ROLES_OTHER_ENTERPRISES"; public static
     * final String SYSTEM_ROLES_PRIVILEGE = "USERS_MANAGE_SYSTEM_ROLES";
     */

    public boolean hasPrivilege(final String privilege)
    {
        return AuthorityUtils.userHasAuthority(privilege);
    }

    public boolean canManageOtherEnterprises()
    {
        return hasPrivilege(OTHER_ENTERPRISES_PRIVILEGE);
    }

    public boolean canManageOtherUsers()
    {
        return hasPrivilege(OTHER_USERS_PRIVILEGE);
    }

    public boolean hasPrivilegeForEnterprise(final String privilege, final Integer idEnterprise)
    {

        // Get current user information
        AbiquoUserDetails userDetails =
            (AbiquoUserDetails) SecurityContextHolder.getContext().getAuthentication()
                .getPrincipal();

        if (userDetails.getEnterpriseId() == idEnterprise)
        {
            return AuthorityUtils.userHasAuthority(privilege);
        }

        return false;
    }

}
