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

import com.abiquo.model.enumerator.Privileges;
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
    public boolean hasPrivilege(final Privileges privilege)
    {
        return AuthorityUtils.userHasAuthority(AbiquoUserDetailsService.DEFAULT_ROLE_PREFIX
            + privilege.name());
    }

    public void requirePrivilege(final Privileges privilege)
    {
        if (!hasPrivilege(privilege))
        {
            throw new AccessDeniedException("Missing privilege " + privilege.name());
        }
    }

    public boolean hasPrivilege(final Privileges privilege, final User user)
    {
        if (user.getRole().getPrivileges() != null)
        {
            for (Privilege p : user.getRole().getPrivileges())
            {
                if (p.getName().equals(privilege.name()))
                {
                    return true;
                }
            }
        }
        return false;
    }

    public boolean canManageOtherEnterprises()
    {
        return hasPrivilege(Privileges.USERS_MANAGE_OTHER_ENTERPRISES);
    }

    public boolean canManageOtherUsers()
    {
        return hasPrivilege(Privileges.USERS_MANAGE_USERS);
    }

    public boolean canManageOtherEnterprises(final User user)
    {
        return hasPrivilege(Privileges.USERS_MANAGE_OTHER_ENTERPRISES, user);
    }

    public boolean canManageOtherUsers(final User user)
    {
        return hasPrivilege(Privileges.USERS_MANAGE_USERS, user);
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

    public static Privileges[] getAllPrivileges()
    {
        return Privileges.values();
    }

}
