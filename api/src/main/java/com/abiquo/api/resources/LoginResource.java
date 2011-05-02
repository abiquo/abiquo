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
    public UserDto getUserByName(@Context IRESTBuilder restBuilder) throws Exception
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
    private UserDto addLinks(IRESTBuilder restBuilder, UserDto userDto, int enterpriseId, int roleId)
    {
        userDto.addLinks(restBuilder.buildUserLinks(enterpriseId, roleId, userDto));

        return userDto;
    }
}
