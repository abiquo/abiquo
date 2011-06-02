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
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.UriInfo;

import org.apache.wink.common.annotations.Workspace;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import com.abiquo.api.services.RoleService;
import com.abiquo.api.services.UserService;
import com.abiquo.api.spring.security.SecurityService;
import com.abiquo.api.util.IRESTBuilder;
import com.abiquo.server.core.enterprise.Role;
import com.abiquo.server.core.enterprise.RolesDto;
import com.abiquo.server.core.enterprise.User;
import com.abiquo.server.core.util.PagedList;

@Path(RolesResource.ROLES_PATH)
@Controller
@Workspace(workspaceTitle = "Abiquo administration workspace", collectionTitle = "Roles")
public class RolesResource extends AbstractResource
{
    private static final Logger LOGGER = LoggerFactory.getLogger(RolesResource.class);

    public static final String ROLES_PATH = "admin/roles";

    @Autowired
    private RoleService service;

    @Autowired
    private UserService userService;

    @Autowired
    private SecurityService securityService;

    @Context
    UriInfo uriInfo;

    @GET
    public RolesDto getRoles(@QueryParam("idEnterprise") final String enterpriseId,
        @QueryParam("filter") final String filter, @QueryParam("orderBy") final String orderBy,
        @QueryParam("desc") final boolean desc, @QueryParam("page") Integer page,
        @QueryParam("numResults") Integer numResults, @Context final IRESTBuilder restBuilder)
        throws Exception
    {
        if (page == null)
        {
            page = 0;
        }

        if (numResults == null)
        {
            numResults = DEFAULT_PAGE_LENGTH;
        }

        int entId = 0;
        if (enterpriseId != null)
        {
            entId = Integer.valueOf(enterpriseId);
        }

        Collection<Role> all =
            service.getRolesByEnterprise(entId, filter, orderBy, desc, page, numResults);
        RolesDto roles = new RolesDto();

        // Can only get my role
        if (!securityService.hasPrivilege(SecurityService.USERS_VIEW_PRIVILEGES)
            && !securityService.hasPrivilege(SecurityService.USERS_MANAGE_ROLES)
            && !securityService.hasPrivilege(SecurityService.USERS_VIEW))
        {
            User currentUser = userService.getCurrentUser();
            if (all != null && !all.isEmpty())
            {
                for (Role r : all)
                {
                    if (currentUser.getRole().getId().equals(r.getId()))
                    {
                        roles.add(createTransferObject(r, restBuilder));
                        break;
                    }
                }

                if (all instanceof PagedList< ? >)
                {
                    PagedList<Role> list = (PagedList<Role>) all;
                    roles.setLinks(restBuilder.buildPaggingLinks(uriInfo.getAbsolutePath()
                        .toString(), list));
                    roles.setTotalSize(list.getTotalResults());
                }
            }

            return roles;
        }

        // Can get all roles
        if (all != null && !all.isEmpty())
        {
            for (Role r : all)
            {
                roles.add(createTransferObject(r, restBuilder));
            }

            if (all instanceof PagedList< ? >)
            {
                PagedList<Role> list = (PagedList<Role>) all;
                roles.setLinks(restBuilder.buildPaggingLinks(uriInfo.getAbsolutePath().toString(),
                    list));
                roles.setTotalSize(list.getTotalResults());
            }
        }

        return roles;
    }

}
