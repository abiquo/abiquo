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

package com.abiquo.api.services;

import java.util.ArrayList;
import java.util.Collection;

import javax.persistence.EntityManager;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.abiquo.api.exceptions.APIError;
import com.abiquo.api.tracer.TracerLogger;
import com.abiquo.server.core.enterprise.Enterprise;
import com.abiquo.server.core.enterprise.EnterpriseRep;
import com.abiquo.server.core.enterprise.Privilege;
import com.abiquo.server.core.enterprise.Role;
import com.abiquo.server.core.enterprise.RoleLdap;

@Service
@Transactional(readOnly = true)
public class RoleService extends DefaultApiService
{
    private static final Logger LOGGER = LoggerFactory.getLogger(RoleService.class);

    @Autowired
    EnterpriseRep enterpriseRep;

    public RoleService()
    {

    }

    // use this to initialize it for tests
    public RoleService(final EntityManager em)
    {
        enterpriseRep = new EnterpriseRep(em);
        tracer = new TracerLogger();
    }

    public Collection<Role> getRoles()
    {
        return enterpriseRep.findAllRoles();
    }

    public Role getRole(final Integer id)
    {
        Role role = enterpriseRep.findRoleById(id);
        if (role == null)
        {
            addNotFoundErrors(APIError.NON_EXISTENT_ROLE);
            flushErrors();
        }
        return role;
    }

    public RoleLdap getRoleLdap(final Integer id)
    {
        RoleLdap roleLdap = enterpriseRep.findRoleLdapById(id);
        if (roleLdap == null)
        {
            addNotFoundErrors(APIError.NON_EXISTENT_ROLELDAP);
            flushErrors();
        }
        return roleLdap;
    }

    private Enterprise findEnterprise(final Integer enterpriseId)
    {
        Enterprise enterprise = enterpriseRep.findById(enterpriseId);
        if (enterprise == null)
        {
            addNotFoundErrors(APIError.NON_EXISTENT_ENTERPRISE);
            flushErrors();
        }
        return enterprise;
    }

    public Collection<Role> getRolesByEnterprise(final int enterpriseId, final String filter,
        final String order, final boolean desc)
    {
        return getRolesByEnterprise(enterpriseId, filter, order, desc, 0, 25);
    }

    public Collection<Role> getRolesByEnterprise(final int enterpriseId, final String filter,
        final String order, final boolean desc, final Integer page, final Integer numResults)
    {

        Enterprise enterprise = null;
        if (enterpriseId != 0)
        {
            enterprise = findEnterprise(enterpriseId);
        }
        return enterpriseRep.findRolesByEnterprise(enterprise, filter, order, desc, 0, 25);
    }

    public Collection<Role> getRolesWithEqualsOrLessPrivileges(final Role role,
        final Collection<Role> roles)
    {
        Collection<Role> result = new ArrayList<Role>();

        if (role.getPrivileges() != null && !role.getPrivileges().isEmpty() && roles != null
            && !roles.isEmpty())
        {
            for (Role r : roles)
            {
                if (r.getPrivileges() != null && !r.getPrivileges().isEmpty())
                {
                    if (hasSameOrLessPrivileges(role.getPrivileges(), r.getPrivileges()))
                    {
                        result.add(r);
                    }
                }
                else
                {
                    result.add(r);
                }
            }
        }

        return result;
    }

    public boolean hasSameOrLessPrivileges(final Collection<Privilege> original,
        final Collection<Privilege> toCompare)
    {
        boolean hasSameOrLessPrivileges = true;
        if (original != null && !original.isEmpty() && toCompare != null && !toCompare.isEmpty())
        {
            if (original.size() >= toCompare.size())
            {

                for (Privilege tcp : toCompare)
                {
                    boolean hasThisPrivilege = false;
                    for (Privilege p : original)
                    {
                        if (tcp.getName().equals(p.getName()))
                        {
                            hasThisPrivilege = true;
                            break;
                        }
                    }
                    if (!hasThisPrivilege)
                    {
                        hasSameOrLessPrivileges = false;
                        break;
                    }
                }
            }
            else
            {
                hasSameOrLessPrivileges = false;
            }
        }

        return hasSameOrLessPrivileges;
    }

    public void checkHasSameOrLessPrivileges(final Collection<Privilege> original,
        final Collection<Privilege> toCompare)
    {
        if (!hasSameOrLessPrivileges(original, toCompare))
        {
            addConflictErrors(APIError.HAS_NOT_ENOUGH_PRIVILEGE);
            flushErrors();
        }
    }

}
