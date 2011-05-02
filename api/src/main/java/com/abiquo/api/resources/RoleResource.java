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

package com.abiquo.api.resources;

import javax.ws.rs.GET;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.Context;

import org.apache.wink.common.annotations.Parent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import com.abiquo.api.exceptions.APIError;
import com.abiquo.api.exceptions.NotFoundException;
import com.abiquo.api.resources.config.PrivilegesResource;
import com.abiquo.api.services.RoleService;
import com.abiquo.api.transformer.ModelTransformer;
import com.abiquo.api.util.IRESTBuilder;
import com.abiquo.server.core.cloud.VirtualMachineDto;
import com.abiquo.server.core.cloud.VirtualMachinesDto;
import com.abiquo.server.core.enterprise.PrivilegesDto;
import com.abiquo.server.core.enterprise.Role;
import com.abiquo.server.core.enterprise.RoleDto;

@Parent(RolesResource.class)
@Path(RoleResource.ROLE_PARAM)
@Controller
public class RoleResource extends AbstractResource
{
    public static final String ROLE = "role";

    public static final String ENTERPRISE = "enterprise";

    public static final String ROLE_PARAM = "{" + ROLE + "}";


    public static final String ENTERPRISE_PARAM = "{" + ENTERPRISE + "}";

    public static final String ROLE_ACTION_GET_PRIVILEGES = "action/privileges";


    @Autowired
    RoleService service;

    @GET
    public RoleDto getRole(@PathParam(ROLE) final Integer roleId,
        @Context final IRESTBuilder restBuilder) throws Exception
    {
        Role role = service.getRole(roleId);

        if (role == null)
        {
            throw new NotFoundException(APIError.NON_EXISTENT_ROLE);
        }

        return createTransferObject(role, restBuilder);
    }

    @PUT
    // Not supported yet
    public RoleDto modifyRole(final RoleDto role, @PathParam(ROLE) final Integer roleId,
        @Context final IRESTBuilder restBuilder) throws Exception
    {
        Role r = service.modifyRole(roleId, role);

        return createTransferObject(r, restBuilder);
    }

    // @DELETE
    // Not supported yet
    public void deleteRole(@PathParam(ROLE) final Integer roleId)
    {
        service.removeRole(roleId);
    }

    /**
     * Retrieves the list Of Virtual machines defined into an enterprise.
     * 
     * @param enterpriseId identifier of the enterprise
     * @param restBuilder {@linnk IRESTBuilder} object injected by context
     * @return the {@link VirtualMachinesDto} object. A {@link VirtualMachineDto} wrapper.
     * @throws Exception
     */
    @GET
    @Path(RoleResource.ROLE_ACTION_GET_PRIVILEGES)
    public PrivilegesDto getPrivileges(@PathParam(RoleResource.ROLE) final Integer roleId,
        @Context final IRESTBuilder restBuilder) throws Exception
    {

        Role role = service.getRole(roleId);

        if (role == null)
        {
            throw new NotFoundException(APIError.NON_EXISTENT_ROLE);
        }

        return PrivilegesResource.createAdminTransferObjects(role.getPrivileges(), restBuilder);

    }

    private static RoleDto addLinks(final IRESTBuilder restBuilder, final RoleDto role,
        final Integer enterpriseId)
    {
        role.setLinks(restBuilder.buildRoleLinks(enterpriseId, role));
        return role;
    }

    public static RoleDto addLinks(final IRESTBuilder restBuilder, final RoleDto role)
    {
        role.setLinks(restBuilder.buildRoleLinks(role));
        return role;
    }

    public static RoleDto createTransferObject(final Role role, final IRESTBuilder restBuilder)
        throws Exception
    {
        RoleDto dto = ModelTransformer.transportFromPersistence(RoleDto.class, role);
        if (role.getEnterprise() != null)
        {
            dto = addLinks(restBuilder, dto, role.getEnterprise().getId());
        }
        else
        {
            dto = addLinks(restBuilder, dto);
        }
        return dto;
    }

    public static Role createPersistenceObject(final RoleDto role) throws Exception
    {
        return ModelTransformer.persistenceFromTransport(Role.class, role);
    }
}
