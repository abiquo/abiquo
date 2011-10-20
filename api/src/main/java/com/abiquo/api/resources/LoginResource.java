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
import javax.ws.rs.Path;
import javax.ws.rs.core.Context;

import org.apache.wink.common.annotations.Workspace;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import com.abiquo.api.services.UserService;
import com.abiquo.api.util.IRESTBuilder;
import com.abiquo.server.core.enterprise.User;
import com.abiquo.server.core.enterprise.UserDto;

/**
 * Resource accessible by anyone to authenticate against Abiquo API.
 * 
 * @author ssedano
 */
@Path(LoginResource.LOGIN_PATH)
@Controller
@Workspace(workspaceTitle = "Abiquo login workspace", collectionTitle = "Login")
public class LoginResource extends AbstractResource
{
    @Autowired
    private UserService userService;

    public static final String LOGIN_PATH = "/login";

    /**
     * Returns the current user with its credentials if any.
     * 
     * @return current user.
     * @throws Exception UserDto
     */
    @GET
    public UserDto getUserByName(@Context final IRESTBuilder restBuilder) throws Exception
    {
        User user = userService.getCurrentUser();
        UserDto userDto = UserResource.createTransferObject(user);
        userDto.setIdEnterprise(user.getEnterprise().getId());
        userDto.setIdRole(user.getRole().getId());
        addLinks(restBuilder, userDto, userDto.getIdEnterprise(), userDto.getIdRole());
        return userDto;
    }

    /**
     * Add links so we can navigate between data.
     * 
     * @param restBuilder builder.
     * @param userDto user.
     * @param enterpriseId id.
     * @param roleId id.
     * @return UserDto
     */
    private UserDto addLinks(final IRESTBuilder restBuilder, final UserDto userDto,
        final int enterpriseId, final int roleId)
    {
        userDto.addLinks(restBuilder.buildUserLinks(enterpriseId, roleId, userDto));

        return userDto;
    }
}
