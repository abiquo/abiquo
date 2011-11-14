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

import java.util.Collection;

import javax.ws.rs.DELETE;
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
import com.abiquo.api.resources.cloud.VirtualMachinesResource;
import com.abiquo.api.services.EnterpriseService;
import com.abiquo.api.services.UserService;
import com.abiquo.api.services.cloud.VirtualMachineService;
import com.abiquo.api.spring.security.SecurityService;
import com.abiquo.api.util.IRESTBuilder;
import com.abiquo.model.enumerator.Privileges;
import com.abiquo.server.core.cloud.VirtualMachine;
import com.abiquo.server.core.cloud.VirtualMachinesDto;
import com.abiquo.server.core.enterprise.Enterprise;
import com.abiquo.server.core.enterprise.EnterpriseDto;
import com.abiquo.server.core.enterprise.User;
import com.abiquo.server.core.enterprise.UserDto;
import com.abiquo.server.core.enterprise.UserWithRoleDto;

@Parent(UsersResource.class)
@Path(UserResource.USER_PARAM)
@Controller
public class UserResource extends AbstractResource
{
    public static final String USER = "user";

    public static final String USER_PARAM = "{" + USER + "}";

    public static final String USER_ACTION_GET_VIRTUALMACHINES_PATH = "action/virtualmachines";

    @Autowired
    UserService service;

    @Autowired
    EnterpriseService enterpriseService;

    @Autowired
    VirtualMachineService vmService;

    @Autowired
    SecurityService securityService;

    @GET
    public UserDto getUser(
        @PathParam(EnterpriseResource.ENTERPRISE) final String enterpriseIdOrWildcard,
        @PathParam(USER) final Integer userId, @Context final IRESTBuilder restBuilder)
        throws Exception
    {
        if (!securityService.hasPrivilege(Privileges.USERS_VIEW))
        {
            User currentUser = service.getCurrentUser();
            if (currentUser.getId().equals(userId))
            {
                User user = service.getUser(userId);
                return createTransferObject(user, restBuilder);
            }
            else
            {
                // throws access denied exception
                securityService.requirePrivilege(Privileges.USERS_VIEW);
            }

        }

        if (!enterpriseIdOrWildcard.equals("_"))
        {
            validatePathParameters(Integer.valueOf(enterpriseIdOrWildcard), userId);
        }

        User user = service.getUser(userId);
        return createTransferObject(user, restBuilder);
    }

    @PUT
    public UserDto modifyUser(
        @PathParam(EnterpriseResource.ENTERPRISE) final String enterpriseIdOrWildcard,
        @PathParam(USER) final Integer userId, final UserDto user,
        @Context final IRESTBuilder restBuilder) throws Exception
    {
        if (!enterpriseIdOrWildcard.equals("_"))
        {
            validatePathParameters(Integer.valueOf(enterpriseIdOrWildcard), userId);
        }

        User u = service.modifyUser(userId, user);

        return createTransferObject(u, restBuilder);
    }

    @DELETE
    public void deleteUser(@PathParam(EnterpriseResource.ENTERPRISE) final Integer enterpriseId,
        @PathParam(USER) final Integer userId)
    {
        validatePathParameters(enterpriseId, userId);
        service.removeUser(userId);
    }

    @GET
    @Path(UserResource.USER_ACTION_GET_VIRTUALMACHINES_PATH)
    public VirtualMachinesDto getVirtualMachines(
        @PathParam(EnterpriseResource.ENTERPRISE) final Integer enterpriseId,
        @PathParam(UserResource.USER) final Integer userId, @Context final IRESTBuilder restBuilder)
        throws Exception
    {

        Enterprise enterprise = enterpriseService.getEnterprise(enterpriseId);

        User user = service.findUserByEnterprise(userId, enterprise);

        Collection<VirtualMachine> vms = vmService.findVirtualMachinesByUser(enterprise, user);

        return VirtualMachinesResource.createAdminTransferObjects(vms, restBuilder);
    }

    private static UserDto addLinks(final IRESTBuilder restBuilder, final UserDto user,
        final Integer enterpriseId, final Integer roleId)
    {
        user.setLinks(restBuilder.buildUserLinks(enterpriseId, roleId, user));
        return user;
    }

    private static UserWithRoleDto addLinks(final IRESTBuilder restBuilder,
        final UserWithRoleDto user, final Integer enterpriseId, final Integer roleId)
    {
        user.setLinks(restBuilder.buildUserLinks(enterpriseId, roleId, user));
        return user;
    }

    public static UserWithRoleDto createUsersTransferObjectWithRole(final User user,
        final IRESTBuilder restBuilder) throws Exception
    {
        UserWithRoleDto u = createUserTransferObjectWithRole(user, restBuilder);

        u = addLinks(restBuilder, u, user.getEnterprise().getId(), user.getRole().getId());

        return u;
    }

    public static UserDto createTransferObject(final User user, final IRESTBuilder restBuilder)
        throws Exception
    {
        UserDto u = createTransferObject(user);

        u = addLinks(restBuilder, u, user.getEnterprise().getId(), user.getRole().getId());

        return u;
    }

    public static UserDto createTransferObject(final User user)
    {
        UserDto u = new UserDto();

        u.setId(user.getId());
        u.setActive(user.getActive());
        u.setEmail(user.getEmail());
        u.setLocale(user.getLocale());
        u.setName(user.getName());
        u.setPassword(user.getPassword());
        u.setSurname(user.getSurname());
        u.setNick(user.getNick());
        u.setDescription(user.getDescription());
        u.setAvailableVirtualDatacenters(user.getAvailableVirtualDatacenters());
        u.setAuthType(user.getAuthType().name());

        return u;
    }

    public static UserWithRoleDto createUserTransferObjectWithRole(final User user,
        final IRESTBuilder restBuilder) throws Exception
    {
        UserWithRoleDto u = new UserWithRoleDto();

        u.setId(user.getId());
        u.setActive(user.getActive());
        u.setEmail(user.getEmail());
        u.setLocale(user.getLocale());
        u.setName(user.getName());
        u.setPassword(user.getPassword());
        u.setSurname(user.getSurname());
        u.setNick(user.getNick());
        u.setDescription(user.getDescription());
        u.setAvailableVirtualDatacenters(user.getAvailableVirtualDatacenters());
        u.setAuthType(user.getAuthType().name());

        EnterpriseDto e =
            EnterpriseResource.createTransferObject(user.getEnterprise(), restBuilder);
        u.setEnterprise(e);

        return u;
    }

    private void validatePathParameters(final Integer enterpriseId, final Integer userId)
        throws NotFoundException
    {
        if (!service.isAssignedTo(enterpriseId, userId))
        {
            throw new NotFoundException(APIError.NOT_ASSIGNED_USER_ENTERPRISE);
        }
    }
}
