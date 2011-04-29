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

import java.util.Collection;

import javax.persistence.EntityManager;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.abiquo.api.exceptions.APIError;
import com.abiquo.api.exceptions.NotFoundException;
import com.abiquo.server.core.enterprise.Enterprise;
import com.abiquo.server.core.enterprise.EnterpriseRep;
import com.abiquo.server.core.enterprise.Role;
import com.abiquo.server.core.enterprise.RoleDto;

@Service
@Transactional(readOnly = true)
public class RoleService extends DefaultApiService
{
    @Autowired
    EnterpriseRep enterpriseRep;

    public RoleService()
    {

    }

    // use this to initialize it for tests
    public RoleService(final EntityManager em)
    {
        enterpriseRep = new EnterpriseRep(em);
    }

    public Collection<Role> getRoles()
    {
        return enterpriseRep.findAllRoles();
    }

    @Transactional(readOnly = false, propagation = Propagation.REQUIRED)
    public Role addRole(final RoleDto dto)
    {

        Role role = new Role(dto.getName());

        if (!role.isValid())
        {
            addValidationErrors(role.getValidationErrors());
            flushErrors();
        }

        enterpriseRep.insertRole(role);
        return role;
    }

    public Role getRole(final Integer id)
    {
        return enterpriseRep.findRoleById(id);
    }

    @Transactional(readOnly = false, propagation = Propagation.REQUIRED)
    public Role modifyRole(final Integer roleId, final RoleDto dto)
    {
        Role old = getRole(roleId);
        if (old == null)
        {
            throw new NotFoundException(APIError.NON_EXISTENT_ROLE);
        }

        if (old.isBlocked())
        {
            throw new NotFoundException(APIError.NON_MODIFICABLE_ROLE);
        }

        old.setName(dto.getName());

        if (!old.isValid())
        {
            addValidationErrors(old.getValidationErrors());
            flushErrors();
        }

        enterpriseRep.updateRole(old);
        return old;
    }

    @Transactional(readOnly = false, propagation = Propagation.REQUIRED)
    public void removeRole(final Integer id)
    {
        Role role = getRole(id);
        if (role == null)
        {
            throw new NotFoundException(APIError.NON_EXISTENT_ROLE);
        }
        enterpriseRep.deleteRole(role);
    }

    private Enterprise findEnterprise(final Integer enterpriseId)
    {
        Enterprise enterprise = enterpriseRep.findById(enterpriseId);
        if (enterprise == null)
        {
            throw new NotFoundException(APIError.NON_EXISTENT_ENTERPRISE);
        }
        return enterprise;
    }

    public Collection<Role> getRolesByEnterprise(final int enterpriseId, final String filter,
        final String order, final boolean desc, final boolean connected, final Integer page,
        final Integer numResults)
    {

        Enterprise enterprise = null;
        if (enterpriseId != 0)
        {
            enterprise = findEnterprise(Integer.valueOf(enterpriseId));
        }
        return enterpriseRep.findRolesByEnterprise(enterprise, filter, order, desc, 0, 25);
    }
}
