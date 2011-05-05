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

import static com.abiquo.api.util.URIResolver.buildPath;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.persistence.EntityManager;
import javax.ws.rs.core.MultivaluedMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.abiquo.api.exceptions.APIError;
import com.abiquo.api.resources.EnterpriseResource;
import com.abiquo.api.resources.EnterprisesResource;
import com.abiquo.api.resources.config.PrivilegeResource;
import com.abiquo.api.resources.config.PrivilegesResource;
import com.abiquo.api.tracer.TracerLogger;
import com.abiquo.api.util.URIResolver;
import com.abiquo.model.rest.RESTLink;
import com.abiquo.server.core.enterprise.Enterprise;
import com.abiquo.server.core.enterprise.EnterpriseRep;
import com.abiquo.server.core.enterprise.Privilege;
import com.abiquo.server.core.enterprise.Role;
import com.abiquo.server.core.enterprise.RoleDto;
import com.abiquo.tracer.ComponentType;
import com.abiquo.tracer.EventType;
import com.abiquo.tracer.SeverityType;

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

    @Transactional(readOnly = false, propagation = Propagation.REQUIRED)
    public Role addRole(final RoleDto dto)
    {
        Role role;

        LOGGER.debug("Getting enterprise link");
        RESTLink enterpriseId = dto.searchLink(EnterpriseResource.ENTERPRISE);

        if (enterpriseId == null)
        {
            LOGGER.debug("Creating role without enterprise");
            role = addRole(dto, null);
        }
        else
        {
            Enterprise enterprise = findEnterprise(dto);

            if (enterprise == null)
            {
                addNotFoundErrors(APIError.NON_EXISTENT_ENTERPRISE);
                flushErrors();
            }

            LOGGER.debug("Creating role with enterprise");
            role = addRole(dto, enterprise);
        }

        tracer.log(SeverityType.INFO, ComponentType.ROLE, EventType.ROLE_CREATED, "Created role "
            + role.getName());
        return role;
    }

    private Enterprise findEnterprise(final RoleDto dto)
    {
        return enterpriseRep.findById(getEnterpriseId(dto));
    }

    private Integer getEnterpriseId(final RoleDto dto)
    {
        LOGGER.debug("Getting enterprise link from role");
        RESTLink enterprise = dto.searchLink(EnterpriseResource.ENTERPRISE);

        if (enterprise != null)
        {

            String buildPath =
                buildPath(EnterprisesResource.ENTERPRISES_PATH, EnterpriseResource.ENTERPRISE_PARAM);
            MultivaluedMap<String, String> enterpriseValues =
                URIResolver.resolveFromURI(buildPath, enterprise.getHref());

            if (enterpriseValues == null
                || !enterpriseValues.containsKey(EnterpriseResource.ENTERPRISE))
            {
                addNotFoundErrors(APIError.ROLE_PARAM_NOT_FOUND);
                flushErrors();
            }

            Integer roleId =
                Integer.valueOf(enterpriseValues.getFirst(EnterpriseResource.ENTERPRISE));
            return roleId;
        }

        LOGGER.debug("Role without enterprise link");
        return null;
    }

    private List<Integer> getPrivilegeIds(final RoleDto dto)
    {
        List<Integer> idList = new ArrayList<Integer>();
        LOGGER.debug("Getting privileges links from role");
        for (RESTLink rsl : dto.getLinks())
        {
            if (rsl.getRel().contains(PrivilegeResource.PRIVILEGE))
            {
                String buildPath =
                    buildPath(PrivilegesResource.PRIVILEGES_PATH, PrivilegeResource.PRIVILEGE_PARAM);
                MultivaluedMap<String, String> privilegeValues =
                    URIResolver.resolveFromURI(buildPath, rsl.getHref());
                if (privilegeValues == null
                    || !privilegeValues.containsKey(PrivilegeResource.PRIVILEGE))
                {
                    addNotFoundErrors(APIError.PRIVILEGE_PARAM_NOT_FOUND);
                    flushErrors();
                }
                idList.add(Integer.valueOf(privilegeValues.getFirst(PrivilegeResource.PRIVILEGE)));
            }
        }

        return idList;
    }

    public Role addRole(final RoleDto dto, final Enterprise enterprise)
    {

        Role role = new Role(dto.getName(), enterprise);

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
        Role role = enterpriseRep.findRoleById(id);
        if (role == null)
        {
            addNotFoundErrors(APIError.NON_EXISTENT_ROLE);
            flushErrors();
        }
        return role;
    }

    @Transactional(readOnly = false, propagation = Propagation.REQUIRED)
    public Role modifyRole(final Integer roleId, final RoleDto dto)
    {
        Role old = getRole(roleId);
        if (old == null)
        {
            addNotFoundErrors(APIError.NON_EXISTENT_ROLE);
            flushErrors();
        }

        if (old.isBlocked())
        {
            addConflictErrors(APIError.NON_MODIFICABLE_ROLE);
            flushErrors();
        }

        old.setName(dto.getName());

        if (!old.isValid())
        {
            addValidationErrors(old.getValidationErrors());
            flushErrors();
        }

        LOGGER.debug("Setting enterprise");
        Integer entId = getEnterpriseId(dto);
        if (entId != null)
        {
            Enterprise ent = enterpriseRep.findById(entId);
            if (ent == null)
            {
                addNotFoundErrors(APIError.NON_EXISTENT_ENTERPRISE);
                flushErrors();
            }
            old.setEnterprise(ent);
        }

        old.setPrivileges(new ArrayList<Privilege>());
        if (dto.getLinks() != null)
        {
            LOGGER.debug("Setting privileges");
            for (Integer pId : getPrivilegeIds(dto))
            {
                Privilege p = enterpriseRep.findPrivilegeById(pId);
                if (p == null)
                {
                    addNotFoundErrors(APIError.NON_EXISTENT_PRIVILEGE);
                    flushErrors();
                }
                old.addPrivilege(p);
            }
            tracer.log(SeverityType.INFO, ComponentType.ROLE, EventType.ROLE_PRIVILEGES_MODIFY,
                "Modifying privileges from role " + old.getName());
        }

        enterpriseRep.updateRole(old);

        tracer.log(SeverityType.INFO, ComponentType.ROLE, EventType.ROLE_MODIFY, "Updated role "
            + old.getName());

        return old;
    }

    @Transactional(readOnly = false, propagation = Propagation.REQUIRED)
    public void removeRole(final Integer id)
    {
        Role role = getRole(id);
        enterpriseRep.deleteRole(role);

        tracer.log(SeverityType.INFO, ComponentType.ROLE, EventType.ROLE_DELETED, "Deleted role "
            + role.getName());
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

}
