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

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.abiquo.api.exceptions.APIError;
import com.abiquo.api.exceptions.NotFoundException;
import com.abiquo.server.core.enterprise.EnterpriseRep;
import com.abiquo.server.core.enterprise.Role;
import com.abiquo.server.core.enterprise.RoleDto;

@Service
@Transactional(readOnly = true)
public class RoleService extends DefaultApiService
{
    @Autowired
    EnterpriseRep enterpriseRep;

    public Collection<Role> getRoles()
    {
        return enterpriseRep.findAllRoles();
    }

    @Transactional(readOnly = false, propagation = Propagation.REQUIRED)
    public Role addRole(RoleDto dto)
    {
        Role role =
            new Role(dto.getType(), dto.getShortDescription(), dto.getLargeDescription(), dto
                .getSecurityLevel());

        if (!role.isValid())
        {
            addValidationErrors(role.getValidationErrors());
            flushErrors();
        }

        enterpriseRep.insertRole(role);
        return role;
    }

    public Role getRole(Integer id)
    {
        return enterpriseRep.findRoleById(id);
    }

    @Transactional(readOnly = false, propagation = Propagation.REQUIRED)
    public Role modifyRole(Integer roleId, RoleDto dto)
    {
        Role old = getRole(roleId);
        if (old == null)
        {
            throw new NotFoundException(APIError.NON_EXISTENT_ROLE);
        }

        old.setShortDescription(dto.getShortDescription());
        old.setLargeDescription(dto.getLargeDescription());
        old.setSecurityLevel(dto.getSecurityLevel());

        if (!old.isValid())
        {
            addValidationErrors(old.getValidationErrors());
            flushErrors();
        }

        enterpriseRep.updateRole(old);
        return old;
    }

    @Transactional(readOnly = false, propagation = Propagation.REQUIRED)
    public void removeRole(Integer id)
    {
        Role role = getRole(id);
        if (role == null)
        {
            throw new NotFoundException(APIError.NON_EXISTENT_ROLE);
        }
        enterpriseRep.deleteRole(role);
    }
}
