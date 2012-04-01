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

import javax.validation.constraints.Min;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
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
import com.abiquo.model.enumerator.Privileges;
import com.abiquo.server.core.enterprise.Role;
import com.abiquo.server.core.enterprise.RolesDto;
import com.abiquo.server.core.enterprise.User;
import com.abiquo.server.core.util.PagedList;

/**
 * @author scastro
 * @wiki Roles Resource offers the functionality of managing the platform roles in a logical way.
 */
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

    /**
     * Return all roles of an enterprise
     * 
     * @title Retrieve a list of Roles
     * @param enterpriseId identifier of the enterprise
     * @param filter
     * @param orderBy
     * @param desc
     * @param page
     * @param numResults
     * @param restBuilder a Context-injected object to create the links of the Dto
     * @return a {RolesDto} object with all roles from an enterprise
     * @throws Exception
     */
    @GET
    @Produces(RolesDto.MEDIA_TYPE)
    public RolesDto getRoles(
        @QueryParam(EnterpriseResource.ENTERPRISE_AS_PARAM) @DefaultValue("0") @Min(0) final Integer enterpriseId,
        @QueryParam(FILTER) @DefaultValue("") final String filter,
        @QueryParam(BY) @DefaultValue("") final String orderBy,
        @QueryParam(ASC) @DefaultValue("") final boolean desc,
        @QueryParam(START_WITH) @DefaultValue("0") @Min(0) final Integer page,
        @QueryParam(LIMIT) @DefaultValue(DEFAULT_PAGE_LENGTH_STRING) final Integer numResults,
        @Context final IRESTBuilder restBuilder) throws Exception
    {
        Collection<Role> all =
            service.getRolesByEnterprise(enterpriseId, filter, orderBy, desc, page, numResults);
        RolesDto roles = new RolesDto();

        // Can only get my role
        if (!securityService.hasPrivilege(Privileges.USERS_VIEW_PRIVILEGES)
            && !securityService.hasPrivilege(Privileges.USERS_MANAGE_ROLES)
            && !securityService.hasPrivilege(Privileges.USERS_VIEW))
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
                    roles.setTotalSize(roles.getCollection().size());
                }
            }

            return roles;
        }

        // Can get all roles
        if (all != null && !all.isEmpty() && all instanceof PagedList< ? >)
        {
            PagedList<Role> list = (PagedList<Role>) all;

            Collection<Role> allowedRoles =
                service.getRolesWithEqualsOrLessPrivileges(userService.getCurrentUser().getRole(),
                    all);

            for (Role r : allowedRoles)
            {
                roles.add(createTransferObject(r, restBuilder));
            }
            roles.setLinks(restBuilder
                .buildPaggingLinks(uriInfo.getAbsolutePath().toString(), list));
            roles.setTotalSize(list.getTotalResults());
        }

        return roles;
    }
}
