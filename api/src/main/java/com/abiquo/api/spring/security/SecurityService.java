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

import org.springframework.security.AccessDeniedException;
import org.springframework.security.context.SecurityContextHolder;
import org.springframework.security.util.AuthorityUtils;
import org.springframework.stereotype.Service;

import com.abiquo.server.core.enterprise.Privilege;
import com.abiquo.server.core.enterprise.User;

/**
 * Security Service to check user privileges
 * 
 * @author aprete
 */

@Service
public class SecurityService
{
    public static final String DRP = AbiquoUserDetailsService.DEFAULT_ROLE_PREFIX;

    public static final String ENTRPRISE_ADMINISTER_ALL = DRP + "ENTRPRISE_ADMINISTER_ALL";

    public static final String USERS_MANAGE_OTHER_ENTERPRISES = DRP
        + "USERS_MANAGE_OTHER_ENTERPRISES";

    public static final String USERS_MANAGE_OTHER_USERS = DRP + "USERS_MANAGE_OTHER_USERS";

    public static final String USERS_MANAGE_ROLES_OTHER_ENTERPRISES = DRP
        + "USERS_MANAGE_ROLES_OTHER_ENTERPRISES";

    public static final String USERS_MANAGE_SYSTEM_ROLES = DRP + "USERS_MANAGE_SYSTEM_ROLES";

    public static final String USERS_MANAGE_USERS_PRIV = "USERS_MANAGE_USERS";

    public static final String USERS_MANAGE_USERS = DRP + USERS_MANAGE_USERS_PRIV;

    public static final String USERS_VIEW = DRP + "USERS_VIEW";

    public static final String USERS_VIEW_PRIVILEGES = DRP + "USERS_VIEW_PRIVILEGES";

    public static final String USERS_PROHIBIT_VDC_RESTRICTION = DRP
        + "USERS_PROHIBIT_VDC_RESTRICTION";

    public static final String USERS_MANAGE_LDAP_GROUP = DRP + "USERS_MANAGE_LDAP_GROUP";

    public boolean hasPrivilege(final String privilege)
    {
        return AuthorityUtils.userHasAuthority(privilege);
    }

    public void requirePrivilege(final String privilege)
    {
        if (!hasPrivilege(privilege))
        {
            throw new AccessDeniedException("Missing privilege " + privilege);
        }
    }

    public boolean hasPrivilege(final String privilege, final User user)
    {
        if (user.getRole().getPrivileges() != null)
        {
            for (Privilege p : user.getRole().getPrivileges())
            {
                if (p.getName().equals(privilege))
                {
                    return true;
                }
            }
        }
        return false;
    }

    public boolean canManageOtherEnterprises()
    {
        return hasPrivilege(USERS_MANAGE_OTHER_ENTERPRISES);
    }

    public boolean canManageOtherUsers()
    {
        return hasPrivilege(USERS_MANAGE_OTHER_USERS);
    }

    public boolean canManageOtherEnterprises(final User user)
    {
        return hasPrivilege(USERS_MANAGE_OTHER_ENTERPRISES, user);
    }

    public boolean canManageOtherUsers(final User user)
    {
        return hasPrivilege(USERS_MANAGE_OTHER_USERS, user);
    }

    public boolean isCloudAdmin()
    {
        return canManageOtherEnterprises();
    }

    public boolean isEnterpriseAdmin()
    {
        return !canManageOtherEnterprises() && canManageOtherUsers();
    }

    public boolean isStandardUser()
    {
        return !canManageOtherEnterprises() && !canManageOtherUsers();
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

    public static String[] getAllPrivileges()
    {
        return new String[] {ENTRPRISE_ADMINISTER_ALL, USERS_MANAGE_OTHER_ENTERPRISES,
        USERS_MANAGE_OTHER_USERS, USERS_MANAGE_ROLES_OTHER_ENTERPRISES, USERS_MANAGE_SYSTEM_ROLES,
        USERS_MANAGE_USERS, USERS_VIEW, USERS_VIEW_PRIVILEGES, USERS_PROHIBIT_VDC_RESTRICTION,
        USERS_MANAGE_LDAP_GROUP};
    }

}
