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

package com.abiquo.api.services;

import static com.abiquo.api.util.URIResolver.buildPath;

import java.util.Collection;
import java.util.Collections;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.persistence.EntityManager;
import javax.ws.rs.core.MultivaluedMap;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.AccessDeniedException;
import org.springframework.security.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.abiquo.api.exceptions.APIError;
import com.abiquo.api.resources.EnterpriseResource;
import com.abiquo.api.resources.EnterprisesResource;
import com.abiquo.api.resources.RoleResource;
import com.abiquo.api.resources.RolesResource;
import com.abiquo.api.spring.security.AbiquoUserDetails;
import com.abiquo.api.util.URIResolver;
import com.abiquo.model.rest.RESTLink;
import com.abiquo.server.core.enterprise.Enterprise;
import com.abiquo.server.core.enterprise.EnterpriseRep;
import com.abiquo.server.core.enterprise.Role;
import com.abiquo.server.core.enterprise.User;
import com.abiquo.server.core.enterprise.User.AuthType;
import com.abiquo.server.core.enterprise.UserDto;

@Service
@Transactional(readOnly = true)
public class UserService extends DefaultApiService
{

    @Autowired
    EnterpriseRep repo;

    public UserService()
    {

    }

    // use this to initialize it for tests
    public UserService(final EntityManager em)
    {
        repo = new EnterpriseRep(em);
    }

    /**
     * Based on the spring authentication context.
     * 
     * @see SecurityContextHolder
     */
    public User getCurrentUser()
    {
        if (SecurityContextHolder.getContext().getAuthentication().getPrincipal() instanceof AbiquoUserDetails)
        {
            AbiquoUserDetails details =
                (AbiquoUserDetails) SecurityContextHolder.getContext().getAuthentication()
                    .getPrincipal();

            AuthType authType =
                AuthType.valueOf(details.getAuthType() != null ? details.getAuthType()
                    : AuthType.ABIQUO.name());
            return repo.getUserByAuth(details.getUsername(), authType);
        }
        else
        { // Backward compatibility and bzngine

            String userName = SecurityContextHolder.getContext().getAuthentication().getName();

            return repo.getUserByAuth(userName, AuthType.ABIQUO);
        }
    }

    // TODO: Remove unused method
    // public Collection<User> getUsers()
    // {
    // return repo.findAllUsers();
    // }

    // TODO: Remove Unused method
    // public Collection<User> getUsersByEnterprise(final Integer enterpriseId)
    // {
    // return repo.findUsersByEnterprise(findEnterprise(enterpriseId));
    // }

    public Collection<User> getUsersByEnterprise(final String enterpriseId, final String filter,
        final String order, final boolean desc)
    {
        return getUsersByEnterprise(enterpriseId, filter, order, desc, false, 0, 25);
    }

    public Collection<User> getUsersByEnterprise(final String enterpriseId, final String filter,
        String order, final boolean desc, final boolean connected, final Integer page,
        final Integer numResults)
    {
        Enterprise enterprise = null;
        User user = getCurrentUser();

        if (!enterpriseId.equals("_"))
        {
            enterprise = findEnterprise(Integer.valueOf(enterpriseId));

            // [ABICLOUDPREMIUM-1310] Cloud admin can view all. Enterprise admin and users can only
            // view their enterprise: check that the provided id corresponds to their enterprise,
            // and fail if the id is invalid
            checkCurrentEnterprise(enterprise);
        }
        else
        {
            // [ABICLOUDPREMIUM-1310] Cloud admin can view all. Enterprise admin and users can only
            // view their enterprise, so force it if necessary. Here we won't fail, because no id
            // was provided in the request
            if (user.getRole().getType() != Role.Type.SYS_ADMIN)
            {
                enterprise = user.getEnterprise();
            }
        }

        // [ABICLOUDPREMIUM-1310] If all the checks are valid, we still need to restrict to the
        // current user if the role of the requestes is a standard user
        if (user.getRole().getType() == Role.Type.USER)
        {
            return Collections.singletonList(user);
        }

        if (StringUtils.isEmpty(order))
        {
            order = User.NAME_PROPERTY;
        }

        return repo.findUsersByEnterprise(enterprise, filter, order, desc, connected, page,
            numResults);
    }

    @Transactional(readOnly = false, propagation = Propagation.REQUIRED)
    public User addUser(final UserDto dto, final Integer enterpriseId)
    {
        Role role = findRole(dto);
        return addUser(dto, enterpriseId, role);
    }

    public User addUser(final UserDto dto, final Integer enterpriseId, final Role role)
    {
        Enterprise enterprise = findEnterprise(enterpriseId);

        checkEnterpriseAdminCredentials(enterprise);

        User user =
            enterprise.createUser(role, dto.getName(), dto.getSurname(), dto.getEmail(),
                dto.getNick(), dto.getPassword(), dto.getLocale());
        user.setActive(dto.isActive() ? 1 : 0);
        user.setDescription(dto.getDescription());
        user.setAvailableVirtualDatacenters(dto.getAvailableVirtualDatacenters());

        if (!user.isValid())
        {
            addValidationErrors(user.getValidationErrors());
            flushErrors();
        }
        if (repo.existAnyUserWithNickAndAuth(user.getNick(), AuthType.ABIQUO))
        {
            addConflictErrors(APIError.USER_DUPLICATED_NICK);
            flushErrors();
        }
        if (!emailIsValid(user.getEmail()))
        {
            addValidationErrors(APIError.EMAIL_IS_INVALID);
            flushErrors();
        }

        repo.insertUser(user);

        return user;
    }

    public User getUser(final Integer id)
    {
        User user = repo.findUserById(id);

        if (user == null)
        {
            addNotFoundErrors(APIError.USER_NON_EXISTENT);
            flushErrors();
        }

        checkUserCredentialsForSelfUser(user, user.getEnterprise());

        return user;
    }

    @Transactional(readOnly = false, propagation = Propagation.REQUIRED)
    public User modifyUser(final Integer userId, final UserDto user)
    {
        User old = repo.findUserById(userId);
        if (old == null)
        {
            addNotFoundErrors(APIError.USER_NON_EXISTENT);
            flushErrors();
        }

        checkUserCredentialsForSelfUser(old, old.getEnterprise());

        // Cloud Admins should only be editable by other Cloud Admins
        if (old.getRole().getType() == Role.Type.SYS_ADMIN
            && getCurrentUser().getRole().getType() != Role.Type.SYS_ADMIN)
        {
            addConflictErrors(APIError.NOT_ENOUGH_PRIVILEGES);
            flushErrors();
        }

        old.setActive(user.isActive() ? 1 : 0);
        old.setEmail(user.getEmail());
        old.setLocale(user.getLocale());
        old.setName(user.getName());
        old.setPassword(user.getPassword());
        old.setSurname(user.getSurname());
        old.setNick(user.getNick());
        old.setDescription(user.getDescription());

        if (user.getAvailableVirtualDatacenters() != null)
        {
            old.setAvailableVirtualDatacenters(user.getAvailableVirtualDatacenters());
        }

        if (!emailIsValid(user.getEmail()))
        {
            addValidationErrors(APIError.EMAIL_IS_INVALID);
            flushErrors();
        }
        if (user.searchLink(RoleResource.ROLE) != null)
        {
            old.setRole(findRole(user));
        }
        if (user.searchLink(EnterpriseResource.ENTERPRISE) != null)
        {
            old.setEnterprise(findEnterprise(getEnterpriseID(user)));
        }

        if (!old.isValid())
        {
            addValidationErrors(old.getValidationErrors());
            flushErrors();
        }
        if (repo.existAnyOtherUserWithNick(old, old.getNick()))
        {
            addConflictErrors(APIError.USER_DUPLICATED_NICK);
            flushErrors();
        }

        return updateUser(old);
    }

    public User updateUser(final User user)
    {
        repo.updateUser(user);

        return user;
    }

    @Transactional(readOnly = false, propagation = Propagation.REQUIRED)
    public void removeUser(final Integer id)
    {
        User user = getUser(id);

        checkEnterpriseAdminCredentials(user.getEnterprise());

        // Cloud Admins should only be editable by other Cloud Admins
        if (user.getRole().getType() == Role.Type.SYS_ADMIN
            && getCurrentUser().getRole().getType() != Role.Type.SYS_ADMIN)
        {
            addForbiddenErrors(APIError.NOT_ENOUGH_PRIVILEGES);
            flushErrors();
        }

        repo.removeUser(user);
    }

    public boolean isAssignedTo(final Integer enterpriseId, final Integer userId)
    {
        User user = getUser(userId);

        return user != null && user.getEnterprise().getId().equals(enterpriseId);
    }

    private Enterprise findEnterprise(final Integer enterpriseId)
    {
        Enterprise enterprise = repo.findById(enterpriseId);
        if (enterprise == null)
        {
            addNotFoundErrors(APIError.NON_EXISTENT_ENTERPRISE);
            flushErrors();
        }
        return enterprise;
    }

    public User findUserByEnterprise(final Integer userId, final Enterprise enterprise)
    {
        User user = repo.findUserByEnterprise(userId, enterprise);
        if (user == null)
        {
            addNotFoundErrors(APIError.USER_NON_EXISTENT);
            flushErrors();
        }
        return user;
    }

    private Role findRole(final UserDto dto)
    {
        return repo.findRoleById(getRoleId(dto));
    }

    private Integer getRoleId(final UserDto user)
    {
        RESTLink role = user.searchLink(RoleResource.ROLE);

        if (role == null)
        {
            addValidationErrors(APIError.MISSING_ROLE_LINK);
            flushErrors();
        }

        String buildPath = buildPath(RolesResource.ROLES_PATH, RoleResource.ROLE_PARAM);
        MultivaluedMap<String, String> roleValues =
            URIResolver.resolveFromURI(buildPath, role.getHref());

        if (roleValues == null || !roleValues.containsKey(RoleResource.ROLE))
        {
            addNotFoundErrors(APIError.ROLE_PARAM_NOT_FOUND);
            flushErrors();
        }

        Integer roleId = Integer.valueOf(roleValues.getFirst(RoleResource.ROLE));
        return roleId;
    }

    private Integer getEnterpriseID(final UserDto user)
    {
        RESTLink ent = user.searchLink(EnterpriseResource.ENTERPRISE);

        String buildPath =
            buildPath(EnterprisesResource.ENTERPRISES_PATH, EnterpriseResource.ENTERPRISE_PARAM);
        MultivaluedMap<String, String> values =
            URIResolver.resolveFromURI(buildPath, ent.getHref());

        Integer entId = Integer.valueOf(values.getFirst(EnterpriseResource.ENTERPRISE));
        return entId;
    }

    public void checkEnterpriseAdminCredentials(final Enterprise enterprise)
    {
        User user = getCurrentUser();
        Role.Type role = user.getRole().getType();

        if ((role == Role.Type.ENTERPRISE_ADMIN && !enterprise.equals(user.getEnterprise()))
            || role == Role.Type.USER)
        {
            throw new AccessDeniedException("");
        }
    }

    private void checkUserCredentialsForSelfUser(final User selfUser, final Enterprise enterprise)
    {
        User user = getCurrentUser();
        Role.Type role = user.getRole().getType();

        if ((role == Role.Type.ENTERPRISE_ADMIN && !enterprise.equals(user.getEnterprise()))
            || (role == Role.Type.USER && user.getId() != selfUser.getId()))
        {
            throw new AccessDeniedException("");
        }
    }

    public void checkCurrentEnterprise(final Enterprise enterprise)
    {
        User user = getCurrentUser();
        Role.Type role = user.getRole().getType();
        boolean sameEnterprise = enterprise.equals(user.getEnterprise());

        if ((role == Role.Type.ENTERPRISE_ADMIN || role == Role.Type.USER) && !sameEnterprise)
        {
            throw new AccessDeniedException("");
        }
    }

    private Boolean emailIsValid(final String email)
    {
        final Pattern pattern;
        final Matcher matchers;
        final String EMAIL_PATTERN =
            "[a-z0-9A-Z!#$%&'*+/=?^_`{|}~-]+(?:\\.[a-z0-9A-Z!#$%&'*+/=?^_`{|}~-]+)*@"
                + "(?:[a-z0-9A-Z](?:[a-z0-9A-Z-]*[a-z0-9A-Z])?\\.)+[a-z0-9A-Z](?:[a-z0-9A-Z-]*[a-z0-9A-Z])?";
        pattern = Pattern.compile(EMAIL_PATTERN);
        matchers = pattern.matcher(email);
        return matchers.matches();
    }
}
