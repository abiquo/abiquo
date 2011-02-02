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

import static com.abiquo.api.resources.RoleResource.createTransferObject;

import java.util.Collection;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.core.Context;

import org.apache.wink.common.annotations.Workspace;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import com.abiquo.api.services.RoleService;
import com.abiquo.api.util.IRESTBuilder;
import com.abiquo.server.core.enterprise.Role;
import com.abiquo.server.core.enterprise.RoleDto;
import com.abiquo.server.core.enterprise.RolesDto;

@Path(RolesResource.ROLES_PATH)
@Controller
@Workspace(workspaceTitle = "Abiquo administration workspace", collectionTitle = "Roles")
public class RolesResource extends AbstractResource
{
    public static final String ROLES_PATH = "admin/roles";

    @Autowired
    private RoleService service;

    @GET
    public RolesDto getRoles(@Context IRESTBuilder restBuilder) throws Exception
    {
        Collection<Role> all = service.getRoles();
        RolesDto roles = new RolesDto();

        if (all != null && !all.isEmpty())
        {
            for (Role r : all)
            {
                roles.add(createTransferObject(r, restBuilder));
            }
        }

        return roles;
    }

    // @POST
    // Not supported yet
    public RoleDto postRole(RoleDto role, @Context IRESTBuilder restBuilder) throws Exception
    {
        Role r = service.addRole(role);

        return createTransferObject(r, restBuilder);
    }
}
