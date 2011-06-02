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

package com.abiquo.abiserver.security;

import org.springframework.stereotype.Service;

import com.abiquo.abiserver.pojo.user.Privilege;
import com.abiquo.abiserver.pojo.user.Role;

/**
 * Security Service to check user privileges
 * 
 * @author aprete
 */

@Service
public class SecurityService
{
    public static final String DEFAULT_ROLE_PREFIX = "ROLE_";

    public static final String OTHER_ENTERPRISES_PRIVILEGE = "USERS_MANAGE_OTHER_ENTERPRISES";

    public static final String USERS_MANAGE_USERS = "USERS_MANAGE_USERS";

    public static final String USERS_MANAGE_ENTERPRISE_BRANDING =
        "USERS_MANAGE_ENTERPRISE_BRANDING";

    public static boolean hasPrivilege(final String privilege, final Role role)
    {
        if (role.getPrivileges() != null)
        {
            for (Privilege p : role.getPrivileges())
            {
                if (p.getName().equals(privilege))
                {
                    return true;
                }
            }
        }
        return false;
    }

    public static boolean canManageOtherEnterprises(final Role role)
    {
        return hasPrivilege(OTHER_ENTERPRISES_PRIVILEGE, role);
    }

    public static boolean canManageOtherUsers(final Role role)
    {
        return hasPrivilege(USERS_MANAGE_USERS, role);
    }

    public static boolean isCloudAdmin(final Role role)
    {
        return canManageOtherEnterprises(role);
    }

    public static boolean isEnterpriseAdmin(final Role role)
    {
        return !canManageOtherEnterprises(role) && canManageOtherUsers(role);
    }

    public static boolean isStandardUser(final Role role)
    {
        return !canManageOtherEnterprises(role) && !canManageOtherUsers(role);
    }

    public boolean hasPrivilegeForEnterprise(final String privilege, final Integer idEnterprise,
        final Role role)
    {

        // Get current user information
        // AbiquoUserDetails userDetails =
        // (AbiquoUserDetails) SecurityContextHolder.getContext().getAuthentication()
        // .getPrincipal();
        //
        // if (userDetails.getEnterpriseId() == idEnterprise)
        // {
        // return AuthorityUtils.userHasAuthority(privilege);
        // }

        return false;
    }

}
