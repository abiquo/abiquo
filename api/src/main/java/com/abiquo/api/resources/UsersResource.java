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

import static com.abiquo.api.resources.UserResource.createTransferObject;
import static com.abiquo.api.resources.UserResource.createTransferObjectWithRole;
import static com.abiquo.api.resources.UserResource.createUsersTransferObjectWithRole;

import java.util.Collection;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.UriInfo;

import org.apache.wink.common.annotations.Parent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import com.abiquo.api.config.ConfigService;
import com.abiquo.api.exceptions.APIError;
import com.abiquo.api.exceptions.ConflictException;
import com.abiquo.api.services.UserService;
import com.abiquo.api.spring.security.SecurityService;
import com.abiquo.api.util.IRESTBuilder;
import com.abiquo.model.transport.LinksDto;
import com.abiquo.server.core.enterprise.User;
import com.abiquo.server.core.enterprise.UserDto;
import com.abiquo.server.core.enterprise.UserWithRoleDto;
import com.abiquo.server.core.enterprise.UsersDto;
import com.abiquo.server.core.enterprise.UsersWithRolesDto;
import com.abiquo.server.core.util.PagedList;

/**
 * @wiki The User Resource offers the functionality of managing the users of an enterprise in a
 *       logical way.
 */
@Parent(EnterpriseResource.class)
@Path(UsersResource.USERS_PATH)
@Controller
public class UsersResource extends AbstractResource
{
    public static final String USERS_PATH = "users";

    @Autowired
    private UserService service;

    @Context
    UriInfo uriInfo;

    @Autowired
    SecurityService securityService;

    /**
     * Returns the users of an enterprise.
     * 
     * @title Retrieve a list of users
     * @param enterpriseId identifier of the enterprise
     * @param filter
     * @param orderBy
     * @param desc
     * @param connected
     * @param page
     * @param numResults
     * @param restBuilder a Context-injected object to create the links of the Dto
     * @return a {UsersDto} object with all user retrived from the enterprise
     * @throws Exception
     */
    @GET
    @Produces({UsersDto.MEDIA_TYPE, LinksDto.MEDIA_TYPE})
    public UsersDto getUsers(@PathParam(EnterpriseResource.ENTERPRISE) final String enterpriseId,
        @QueryParam("filter") final String filter, @QueryParam("orderBy") final String orderBy,
        @QueryParam("desc") final boolean desc, @QueryParam("connected") final boolean connected,
        @QueryParam("page") Integer page, @QueryParam("numResults") Integer numResults,
        @Context final IRESTBuilder restBuilder) throws Exception
    {
        if (page == null)
        {
            page = 0;
        }

        if (numResults == null)
        {
            numResults = DEFAULT_PAGE_LENGTH;
        }

        Collection<User> all =
            service.getUsersByEnterprise(enterpriseId, filter, orderBy, desc, connected, page,
                numResults);
        UsersDto users = new UsersDto();

        // Can get all users
        if (all != null && !all.isEmpty())
        {
            for (User u : all)
            {
                users.add(createTransferObject(u, restBuilder));
            }

            if (all instanceof PagedList< ? >)
            {
                PagedList<User> list = (PagedList<User>) all;
                users.setLinks(restBuilder.buildPaggingLinks(uriInfo.getAbsolutePath().toString(),
                    list));
                users.setTotalSize(list.getTotalResults());
            }
        }

        return users;
    }

    /**
     * Returns the users with own roles of an enterprise.
     * 
     * @title Retrive a list of users with own roles
     * @param enterpriseId identifier of the enterprise
     * @param filter
     * @param orderBy
     * @param desc
     * @param connected
     * @param page
     * @param numResults
     * @param restBuilder a Context-injected object to create the links of the Dto
     * @return a {UsersWithRolesDto} object with all user and his own role retrived from the
     *         enterprise
     * @throws Exception
     */
    @GET
    @Produces(UsersWithRolesDto.MEDIA_TYPE)
    public UsersWithRolesDto getUsersWithRoles(
        @PathParam(EnterpriseResource.ENTERPRISE) final String enterpriseId,
        @QueryParam("filter") final String filter, @QueryParam("orderBy") final String orderBy,
        @QueryParam("desc") final boolean desc, @QueryParam("connected") final boolean connected,
        @QueryParam("page") Integer page, @QueryParam("numResults") Integer numResults,
        @Context final IRESTBuilder restBuilder) throws Exception
    {
        if (page == null)
        {
            page = 0;
        }

        if (numResults == null)
        {
            numResults = DEFAULT_PAGE_LENGTH;
        }

        Collection<User> all =
            service.getUsersByEnterprise(enterpriseId, filter, orderBy, desc, connected, page,
                numResults);
        UsersWithRolesDto users = new UsersWithRolesDto();

        // Can get all users
        if (all != null && !all.isEmpty())
        {
            for (User u : all)
            {
                UserWithRoleDto uDto = createUsersTransferObjectWithRole(u, restBuilder);
                uDto.setRole(RoleResource.createTransferWithPrivilegesObject(u.getRole(),
                    restBuilder));
                users.add(uDto);
            }

            if (all instanceof PagedList< ? >)
            {
                PagedList<User> list = (PagedList<User>) all;
                users.setLinks(restBuilder.buildPaggingLinks(uriInfo.getAbsolutePath().toString(),
                    list));
                users.setTotalSize(list.getTotalResults());
            }
        }

        return users;
    }

    /**
     * Creates a user and returns it after creation
     * 
     * @title Create a new user
     * @wiki When creating a new user, the password must be provided in plain text.
     * @param enterpriseId identifier of the enterprise
     * @param user user to create
     * @param restBuilder a Context-injected object to create the links of the Dto
     * @return a {UserDto} object with the created user
     * @throws Exception
     */
    @POST
    @Consumes(UserDto.MEDIA_TYPE)
    @Produces(UserDto.MEDIA_TYPE)
    public UserDto postUser(@PathParam(EnterpriseResource.ENTERPRISE) final Integer enterpriseId,
        final UserDto user, @Context final IRESTBuilder restBuilder) throws Exception
    {

        String authMode = ConfigService.getSecurityMode();

        if (authMode.equalsIgnoreCase(User.AuthType.LDAP.toString()))
        {
            // In ldap mode it is not possible to create user
            throw new ConflictException(APIError.NOT_USER_CREACION_LDAP_MODE);
        }
        User u = service.addUser(user, enterpriseId);

        return createTransferObject(u, restBuilder);
    }
}
