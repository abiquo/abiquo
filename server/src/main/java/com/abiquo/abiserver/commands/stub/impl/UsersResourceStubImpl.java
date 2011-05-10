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

package com.abiquo.abiserver.commands.stub.impl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.wink.client.ClientResponse;
import org.apache.wink.common.internal.utils.UriHelper;

import com.abiquo.abiserver.business.hibernate.pojohb.user.UserHB;
import com.abiquo.abiserver.commands.stub.AbstractAPIStub;
import com.abiquo.abiserver.commands.stub.UsersResourceStub;
import com.abiquo.abiserver.pojo.result.BasicResult;
import com.abiquo.abiserver.pojo.result.DataResult;
import com.abiquo.abiserver.pojo.user.Enterprise;
import com.abiquo.abiserver.pojo.user.Privilege;
import com.abiquo.abiserver.pojo.user.Role;
import com.abiquo.abiserver.pojo.user.RoleListOptions;
import com.abiquo.abiserver.pojo.user.RoleListResult;
import com.abiquo.abiserver.pojo.user.User;
import com.abiquo.abiserver.pojo.user.UserListOptions;
import com.abiquo.abiserver.pojo.user.UserListResult;
import com.abiquo.abiserver.security.SecurityService;
import com.abiquo.model.rest.RESTLink;
import com.abiquo.server.core.enterprise.EnterpriseDto;
import com.abiquo.server.core.enterprise.PrivilegeDto;
import com.abiquo.server.core.enterprise.PrivilegesDto;
import com.abiquo.server.core.enterprise.RoleDto;
import com.abiquo.server.core.enterprise.RolesDto;
import com.abiquo.server.core.enterprise.UserDto;
import com.abiquo.server.core.enterprise.UsersDto;

public class UsersResourceStubImpl extends AbstractAPIStub implements UsersResourceStub
{

    @Override
    public DataResult<User> createUser(final User user)
    {
        DataResult<User> result = new DataResult<User>();

        UserDto dto = fromUserToDto(user);

        ClientResponse response =
            post(createUsersLink(String.valueOf(user.getEnterprise().getId())), dto);
        if (response.getStatusCode() == 201)
        {
            UserDto responseDto = response.getEntity(UserDto.class);

            User newUser = User.create(responseDto, user.getEnterprise(), user.getRole());
            result.setSuccess(true);
            result.setData(newUser);
        }
        else
        {
            populateErrors(response, result, "createUser");
        }

        return result;
    }

    @Override
    public BasicResult updateUser(final User user)
    {
        BasicResult result = new BasicResult();

        UserDto dto = fromUserToDto(user);
        ClientResponse response = put(createUserLink("_", user.getId()), dto);

        if (response.getStatusCode() == 200)
        {
            result.setSuccess(true);
        }
        else
        {
            populateErrors(response, result, "updateUser");
        }

        return result;
    }

    @Override
    public BasicResult deleteUser(final User user)
    {
        BasicResult result = new BasicResult();

        ClientResponse response =
            delete(createUserLink(user.getEnterprise().getId(), user.getId()));
        if (response.getStatusCode() == 204)
        {
            result.setSuccess(true);
        }
        else
        {
            populateErrors(response, result, "deleteUser");
        }

        return result;
    }

    @Override
    public DataResult<UserListResult> getUsers(final UserListOptions userListOptions)
    {
        DataResult<UserListResult> dataResult = new DataResult<UserListResult>();
        UserListResult userListResult = new UserListResult();

        String enterpriseWildcard = "_";
        if (userListOptions.getByEnterprise() != null)
        {
            enterpriseWildcard = String.valueOf(userListOptions.getByEnterprise().getId());
        }

        UserHB currentUser = getCurrentUser();

        if (SecurityService.isEnterpriseAdmin(currentUser.getRoleHB().toPojo()))
        {
            enterpriseWildcard = String.valueOf(currentUser.getEnterpriseHB().getIdEnterprise());
        }

        boolean desc = !userListOptions.getAsc();
        String orderBy = userListOptions.getOrderBy();
        if (orderBy.equals("user"))
        {
            orderBy = "nick";
        }
        Map<String, String[]> queryParams = new HashMap<String, String[]>();
        if (!StringUtils.isEmpty(userListOptions.getFilter()))
        {
            queryParams.put("filter", new String[] {userListOptions.getFilter()});
        }
        queryParams.put("orderBy", new String[] {orderBy});
        queryParams.put("desc", new String[] {String.valueOf(desc)});

        if (userListOptions.getLoggedOnly() != null)
        {
            queryParams.put("connected", new String[] {userListOptions.getLoggedOnly().toString()});
        }

        String uri =
            createUsersLink(enterpriseWildcard, userListOptions.getOffset(),
                userListOptions.getLength());
        uri = UriHelper.appendQueryParamsToPath(uri, queryParams, false);

        ClientResponse response = get(uri);
        if (response.getStatusCode() == 200)
        {
            UsersDto usersDto = response.getEntity(UsersDto.class);
            Collection<User> users = new ArrayList<User>();
            Collection<User> normalUsers = new ArrayList<User>();
            Map<String, EnterpriseDto> catchedEnterprises = new HashMap<String, EnterpriseDto>();
            Map<String, RoleDto> catchedRoles = new HashMap<String, RoleDto>();
            Map<String, Set<Privilege>> catchedPrivileges = new HashMap<String, Set<Privilege>>();

            for (UserDto dto : usersDto.getCollection())
            {
                RoleDto role = getRole(dto.searchLink("role").getHref(), catchedRoles);
                EnterpriseDto enterprise =
                    getEnterprise(dto.searchLink("enterprise").getHref(), catchedEnterprises);

                RESTLink enterpriseLink = role.searchLink("enterprise");
                EnterpriseDto enterpriseRole = null;
                Enterprise entRole = null;
                if (enterpriseLink != null)
                {
                    enterpriseRole = getEnterprise(enterpriseLink.getHref(), catchedEnterprises);
                    entRole = Enterprise.create(enterpriseRole);
                }

                RESTLink privilegesLink = role.searchLink("action", "privileges");
                Set<Privilege> privileges = new HashSet<Privilege>();
                if (privilegesLink != null)
                {
                    privileges = getPrivileges(privilegesLink.getHref(), catchedPrivileges);
                }

                if (SecurityService.isStandardUser(currentUser.getRoleHB().toPojo())
                    && orderBy.equalsIgnoreCase("role"))
                {
                    normalUsers.add(User.create(dto, Enterprise.create(enterprise),
                        Role.create(role, entRole, privileges)));
                }
                else
                {
                    users.add(User.create(dto, Enterprise.create(enterprise),
                        Role.create(role, entRole, privileges)));
                }
            }
            Collection<User> usersWithoutVDC = new ArrayList<User>();
            Integer total =
                usersDto.getTotalSize() != null ? usersDto.getTotalSize() : usersDto
                    .getCollection().size();

            for (User user : normalUsers)
            {
                if (user.getAvailableVirtualDatacenters().length == 0)
                {
                    usersWithoutVDC.add(user);
                }
            }
            normalUsers.removeAll(usersWithoutVDC);
            if (orderBy.equalsIgnoreCase("role") && !desc)
            {
                usersWithoutVDC.addAll(normalUsers);
                usersWithoutVDC.addAll(users);
                users = usersWithoutVDC;
            }
            else
            {
                users.addAll(normalUsers);
                users.addAll(usersWithoutVDC);
            }
            userListResult.setTotalUsers(total);
            userListResult.setUsersList(users);

            dataResult.setData(userListResult);
            dataResult.setSuccess(true);
        }
        else
        {
            populateErrors(response, dataResult, "getUsers");
        }

        return dataResult;
    }

    private Set<Privilege> getPrivileges(final String privilegesUri,
        final Map<String, Set<Privilege>> cache)
    {
        Set<Privilege> privileges = new HashSet<Privilege>();
        if (!cache.containsKey(privilegesUri))
        {
            PrivilegesDto ps =
                get(privilegesUri, UsersResourceStubImpl.FLAT_MEDIA_TYPE).getEntity(
                    PrivilegesDto.class);
            if (ps.getCollection() != null)
            {
                for (PrivilegeDto p : ps.getCollection())
                {
                    privileges.add(Privilege.create(p));
                }
            }
            cache.put(privilegesUri, privileges);
        }
        else
        {
            privileges = cache.get(privilegesUri);
        }
        return privileges;
    }

    private RoleDto getRole(final String roleUri, final Map<String, RoleDto> cache)
    {
        RoleDto dto = null;
        if (!cache.containsKey(roleUri))
        {
            dto = get(roleUri).getEntity(RoleDto.class);
            cache.put(roleUri, dto);
        }
        else
        {
            dto = cache.get(roleUri);
        }
        return dto;
    }

    private EnterpriseDto getEnterprise(final String enterpriseUri,
        final Map<String, EnterpriseDto> cache)
    {
        EnterpriseDto dto = null;
        if (!cache.containsKey(enterpriseUri))
        {
            dto = get(enterpriseUri).getEntity(EnterpriseDto.class);
            cache.put(enterpriseUri, dto);
        }
        else
        {
            dto = cache.get(enterpriseUri);
        }
        return dto;
    }

    private UserDto fromUserToDto(final User user)
    {
        UserDto newUser =
            new UserDto(user.getName(),
                user.getSurname(),
                user.getEmail(),
                user.getUser(),
                user.getPass(),
                user.getLocale(),
                user.getDescription());

        newUser.setActive(user.getActive());
        newUser.addLink(new RESTLink("role", createRoleLink(user.getRole().getId())));
        newUser.addLink(new RESTLink("enterprise", createEnterpriseLink(user.getEnterprise()
            .getId())));

        if (!ArrayUtils.isEmpty(user.getAvailableVirtualDatacenters()))
        {
            newUser.setAvailableVirtualDatacenters(StringUtils.join(
                user.getAvailableVirtualDatacenters(), ","));
        }
        else
        {
            // see all virtual datacenters, required for user modification
            newUser.setAvailableVirtualDatacenters(null);
        }

        return newUser;
    }

    @Override
    public DataResult<Role> getRole(final int roleId)
    {
        DataResult<Role> result = new DataResult<Role>();

        String uri = createRoleLink(roleId);

        ClientResponse response = get(uri);

        if (response.getStatusCode() == 200)
        {
            result.setSuccess(true);

            Role role = getRole(response);

            result.setData(role);
        }
        else
        {
            populateErrors(response, result, "getRole");
        }

        return result;
    }

    protected Role getRole(final ClientResponse response)
    {
        RoleDto role = response.getEntity(RoleDto.class);

        return getRole(role, true);
    }

    protected Role getRole(final RoleDto role, final boolean getPrivileges)
    {

        RESTLink enterpriseLink = role.searchLink("enterprise");
        EnterpriseDto enterpriseRole = null;
        Enterprise entRole = null;
        if (enterpriseLink != null)
        {
            enterpriseRole =
                getEnterprise(enterpriseLink.getHref(), new HashMap<String, EnterpriseDto>());
            entRole = Enterprise.create(enterpriseRole);
        }
        Set<Privilege> privileges = new HashSet<Privilege>();

        if (getPrivileges)
        {
            RESTLink privilegesLink = role.searchLink("action", "privileges");

            if (privilegesLink != null)
            {
                privileges =
                    getPrivileges(privilegesLink.getHref(), new HashMap<String, Set<Privilege>>());
            }
        }
        return Role.create(role, entRole, privileges);
    }

    @Override
    public DataResult<RoleListResult> getRoles(final RoleListOptions roleListOptions)
    {
        DataResult<RoleListResult> dataResult = new DataResult<RoleListResult>();
        RoleListResult roleListResult = new RoleListResult();

        String enterpriseId = null;
        if (roleListOptions.getIdEnterprise() != 0)
        {
            enterpriseId = String.valueOf(roleListOptions.getIdEnterprise());
        }

        boolean desc = !roleListOptions.getAsc();
        String orderBy = roleListOptions.getOrderBy();

        Map<String, String[]> queryParams = new HashMap<String, String[]>();
        if (!StringUtils.isEmpty(roleListOptions.getFilter()))
        {
            queryParams.put("filter", new String[] {roleListOptions.getFilter()});
        }
        queryParams.put("orderBy", new String[] {orderBy});
        queryParams.put("desc", new String[] {String.valueOf(desc)});

        String uri =
            createRolesLink(enterpriseId, roleListOptions.getOffset(), roleListOptions.getLength());
        uri = UriHelper.appendQueryParamsToPath(uri, queryParams, false);

        ClientResponse response = get(uri);
        if (response.getStatusCode() == 200)
        {
            RolesDto rolesDto = response.getEntity(RolesDto.class);
            Collection<Role> roles = new ArrayList<Role>();
            for (RoleDto dto : rolesDto.getCollection())
            {
                roles.add(getRole(dto, false));

            }

            Integer total =
                rolesDto.getTotalSize() != null ? rolesDto.getTotalSize() : rolesDto
                    .getCollection().size();

            roleListResult.setTotalRoles(total);
            roleListResult.setRolesList(roles);

            dataResult.setData(roleListResult);
            dataResult.setSuccess(true);
        }
        else
        {
            populateErrors(response, dataResult, "getRoles");
        }

        return dataResult;
    }
}
