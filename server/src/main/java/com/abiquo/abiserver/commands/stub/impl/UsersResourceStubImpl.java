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
import java.util.Map;

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
import com.abiquo.abiserver.pojo.user.Role;
import com.abiquo.abiserver.pojo.user.User;
import com.abiquo.abiserver.pojo.user.UserListOptions;
import com.abiquo.abiserver.pojo.user.UserListResult;
import com.abiquo.model.rest.RESTLink;
import com.abiquo.server.core.enterprise.EnterpriseDto;
import com.abiquo.server.core.enterprise.RoleDto;
import com.abiquo.server.core.enterprise.UserDto;
import com.abiquo.server.core.enterprise.UsersDto;

public class UsersResourceStubImpl extends AbstractAPIStub implements UsersResourceStub
{

    @Override
    public DataResult<User> createUser(User user)
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
    public BasicResult updateUser(User user)
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

    public BasicResult deleteUser(User user)
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

    public DataResult<UserListResult> getUsers(UserListOptions userListOptions)
    {
        DataResult<UserListResult> dataResult = new DataResult<UserListResult>();
        UserListResult userListResult = new UserListResult();

        String enterpriseWildcard = "_";
        if (userListOptions.getByEnterprise() != null)
        {
            enterpriseWildcard = String.valueOf(userListOptions.getByEnterprise().getId());
        }

        UserHB currentUser = getCurrentUser();
        if (currentUser.getRoleHB().getType() == com.abiquo.server.core.enterprise.Role.Type.ENTERPRISE_ADMIN)
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
            createUsersLink(enterpriseWildcard, userListOptions.getOffset(), userListOptions
                .getLength());
        uri = UriHelper.appendQueryParamsToPath(uri, queryParams, false);

        ClientResponse response = get(uri);
        if (response.getStatusCode() == 200)
        {
            UsersDto usersDto = response.getEntity(UsersDto.class);
            Collection<User> users = new ArrayList<User>();
            Map<String, EnterpriseDto> catchedEnterprises = new HashMap<String, EnterpriseDto>();
            Map<String, RoleDto> catchedRoles = new HashMap<String, RoleDto>();

            for (UserDto dto : usersDto.getCollection())
            {
                RoleDto role = getRole(dto.searchLink("role").getHref(), catchedRoles);
                EnterpriseDto enterprise =
                    getEnterprise(dto.searchLink("enterprise").getHref(), catchedEnterprises);

                users.add(User.create(dto, Enterprise.create(enterprise), Role.create(role)));
            }

            Integer total =
                usersDto.getTotalSize() != null ? usersDto.getTotalSize() : usersDto
                    .getCollection().size();

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

    private RoleDto getRole(String roleUri, Map<String, RoleDto> cache)
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

    private EnterpriseDto getEnterprise(String enterpriseUri, Map<String, EnterpriseDto> cache)
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

    private UserDto fromUserToDto(User user)
    {
        UserDto newUser =
            new UserDto(user.getName(), user.getSurname(), user.getEmail(), user.getUser(), user
                .getPass(), user.getLocale(), user.getDescription());

        newUser.setActive(user.getActive());
        newUser.addLink(new RESTLink("role", createRoleLink(user.getRole().getId())));
        newUser.addLink(new RESTLink("enterprise", createEnterpriseLink(user.getEnterprise()
            .getId())));

        if (!ArrayUtils.isEmpty(user.getAvailableVirtualDatacenters()))
        {
            newUser.setAvailableVirtualDatacenters(StringUtils.join(user
                .getAvailableVirtualDatacenters(), ","));
        }
        else
        {
            // see all virtual datacenters, required for user modification
            newUser.setAvailableVirtualDatacenters(null);
        }

        return newUser;
    }
}
