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

import java.nio.charset.Charset;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Collection;
import java.util.Collections;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.persistence.EntityManager;
import javax.ws.rs.core.MultivaluedMap;

import org.apache.commons.codec.binary.Hex;
import org.apache.commons.lang.StringUtils;
import org.hibernate.Hibernate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.AccessDeniedException;
import org.springframework.security.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.abiquo.api.config.ConfigService;
import com.abiquo.api.exceptions.APIError;
import com.abiquo.api.exceptions.ConflictException;
import com.abiquo.api.resources.EnterpriseResource;
import com.abiquo.api.resources.EnterprisesResource;
import com.abiquo.api.resources.RoleResource;
import com.abiquo.api.resources.RolesResource;
import com.abiquo.api.spring.security.AbiquoUserDetails;
import com.abiquo.api.spring.security.SecurityService;
import com.abiquo.api.util.URIResolver;
import com.abiquo.model.enumerator.Privileges;
import com.abiquo.model.rest.RESTLink;
import com.abiquo.server.core.enterprise.Enterprise;
import com.abiquo.server.core.enterprise.EnterpriseRep;
import com.abiquo.server.core.enterprise.Privilege;
import com.abiquo.server.core.enterprise.Role;
import com.abiquo.server.core.enterprise.User;
import com.abiquo.server.core.enterprise.UserDto;
import com.abiquo.server.core.enterprise.User.AuthType;
import com.abiquo.tracer.ComponentType;
import com.abiquo.tracer.EventType;
import com.abiquo.tracer.SeverityType;

@Service
@Transactional(readOnly = true)
public class UserService extends DefaultApiService
{

    private static final Logger LOGGER = LoggerFactory.getLogger(UserService.class);

    @Autowired
    EnterpriseRep repo;

    @Autowired
    SecurityService securityService;

    public UserService()
    {

    }

    // use this to initialize it for tests
    public UserService(final EntityManager em)
    {
        repo = new EnterpriseRep(em);
        securityService = new SecurityService();
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
            checkCurrentEnterpriseForUsers(enterprise);
        }
        else
        {
            // [ABICLOUDPREMIUM-1310] Cloud admin can view all. Enterprise admin and users can only
            // view their enterprise, so force it if necessary. Here we won't fail, because no id
            // was provided in the request
            // if (user.getRole().getType() != Role.Type.SYS_ADMIN)
            if (!securityService.isCloudAdmin())
            {
                enterprise = user.getEnterprise();
            }
        }

        // [ABICLOUDPREMIUM-1310] If all the checks are valid, we still need to restrict to the
        // current user if the role of the requestes is a standard user
        // if (user.getRole().getType() == Role.Type.USER)

        // [ROLES & PRIVILEGES] User response depends on current user's privileges)

        if (!securityService.hasPrivilege(Privileges.USERS_VIEW))
        {
            return Collections.singletonList(user);
        }

        if (StringUtils.isEmpty(order))
        {
            order = User.NAME_PROPERTY;
        }

        Collection<User> users =
            repo
                .findUsersByEnterprise(enterprise, filter, order, desc, connected, page, numResults);

        // Refresh all entities to avioid lazys
        for (User u : users)
        {
            Hibernate.initialize(u.getEnterprise());
            Hibernate.initialize(u.getRole());

            for (Privilege p : u.getRole().getPrivileges())
            {
                Hibernate.initialize(p);
            }
        }

        return users;
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

        if (dto.getPassword() == null || dto.getPassword().isEmpty())
        {
            addValidationErrors(APIError.USER_PASSWORD_IS_NECESSARY);
            flushErrors();
        }
        if (dto.getNick() == null || dto.getNick().isEmpty())
        {
            addValidationErrors(APIError.USER_NICK_IS_NECESSARY);
            flushErrors();
        }
        if (dto.getName() == null || dto.getName().isEmpty())
        {
            addValidationErrors(APIError.USER_NAME_IS_NECESSARY);
            flushErrors();
        }

        User user =
            enterprise.createUser(role, dto.getName(), dto.getSurname(), dto.getEmail(), dto
                .getNick(), encrypt(dto.getPassword()), dto.getLocale());
        user.setActive(dto.isActive() ? 1 : 0);
        user.setDescription(dto.getDescription());
        validate(user);
        if (!securityService.hasPrivilege(Privileges.USERS_PROHIBIT_VDC_RESTRICTION, user)
            && !StringUtils.isBlank(dto.getAvailableVirtualDatacenters()))
        {
            user.setAvailableVirtualDatacenters(dto.getAvailableVirtualDatacenters());
        }

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

        tracer
            .log(SeverityType.INFO, ComponentType.USER, EventType.USER_CREATE, "user.created", user
                .getName(), enterprise.getName(), user.getName(), user.getSurname(), user.getRole());

        return user;
    }

    public User getUser(final Integer id)
    {

        return getUser(id, false);
    }

    public User getUser(final Integer id, final Boolean skipCredentials)
    {
        User user = repo.findUserById(id);

        if (user == null)
        {
            addNotFoundErrors(APIError.USER_NON_EXISTENT);
            flushErrors();
        }

        if (!skipCredentials)
        {
            checkUserCredentialsForSelfUser(user, user.getEnterprise());
        }

        return user;
    }

    @Transactional(readOnly = false, propagation = Propagation.REQUIRED)
    public User modifyUser(final Integer userId, final UserDto user)
    {
        if (!securityService.hasPrivilege(Privileges.USERS_MANAGE_USERS)
            && !securityService.hasPrivilege(Privileges.USERS_MANAGE_OTHER_ENTERPRISES))
        {
            if (!getCurrentUser().getId().equals(userId))
            {
                securityService.requirePrivilege(Privileges.USERS_MANAGE_USERS);
            }
        }

        User old = repo.findUserById(userId);
        if (old == null)
        {
            addNotFoundErrors(APIError.USER_NON_EXISTENT);
            flushErrors();
        }

        checkUserCredentialsForSelfUser(old, old.getEnterprise());

        if (securityService.canManageOtherEnterprises(old)
            && !securityService.canManageOtherEnterprises())
        {
            addConflictErrors(APIError.NOT_ENOUGH_PRIVILEGES);
            flushErrors();
        }

        old.setActive(user.isActive() ? 1 : 0);
        old.setEmail(user.getEmail());
        old.setLocale(user.getLocale());
        old.setName(user.getName());
        if (!StringUtils.isEmpty(user.getPassword()))
        {
            // Password must only be updated if it is provided
            old.setPassword(encrypt(user.getPassword()));
        }
        old.setSurname(user.getSurname());
        if (!old.getNick().equalsIgnoreCase(user.getNick()))
        {
            addConflictErrors(APIError.USER_NICK_CANNOT_BE_CHANGED);
            flushErrors();
        }
        old.setDescription(user.getDescription());

        if (!securityService.hasPrivilege(Privileges.USERS_PROHIBIT_VDC_RESTRICTION, old))
        {
            if (StringUtils.isBlank(user.getAvailableVirtualDatacenters()))
            {
                user.setAvailableVirtualDatacenters(null);
            }

            old.setAvailableVirtualDatacenters(user.getAvailableVirtualDatacenters());
        }

        if (!emailIsValid(user.getEmail()))
        {
            addValidationErrors(APIError.EMAIL_IS_INVALID);
            flushErrors();
        }

        String authMode = ConfigService.getSecurityMode();
        if (user.searchLink(RoleResource.ROLE) != null)
        {

            Role newRole = findRole(user);
            if (authMode.equalsIgnoreCase(User.AuthType.LDAP.toString()))
            {
                if (!old.getRole().getId().equals(newRole.getId()))
                {

                    // In ldap mode it is not possible to edit user's role
                    throw new ConflictException(APIError.NOT_EDIT_USER_ROLE_LDAP_MODE);
                }
            }
            old.setRole(newRole);
        }

        Enterprise newEnt = null;
        if (user.searchLink(EnterpriseResource.ENTERPRISE) != null)
        {
            newEnt = findEnterprise(getEnterpriseID(user));
        }

        if (securityService.hasPrivilege(Privileges.USERS_MANAGE_OTHER_ENTERPRISES))
        {
            if (user.searchLink(EnterpriseResource.ENTERPRISE) != null)
            {
                old.setEnterprise(newEnt);
            }
        }
        else if (securityService.hasPrivilege(Privileges.ENTERPRISE_ADMINISTER_ALL))
        {
            if (getCurrentUser().getId().equals(user.getId()))
            {
                if (user.searchLink(EnterpriseResource.ENTERPRISE) != null)
                {
                    old.setEnterprise(newEnt);
                }
            }
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

        updateUser(old);

        tracer.log(SeverityType.INFO, ComponentType.USER, EventType.USER_MODIFY, "user.modified",
            old.getName(), old.getEnterprise().getName(), old.getName(), old.getSurname(), old
                .getRole());

        return old;
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

        // user can not delete himself
        User logged = getCurrentUser();
        if (logged.getId() == user.getId())
        {
            addConflictErrors(APIError.USER_DELETING_HIMSELF);
            flushErrors();
        }

        checkEnterpriseAdminCredentials(user.getEnterprise());

        // Cloud Admins should only be editable by other Cloud Admins
        // if (user.getRole().getType() == Role.Type.SYS_ADMIN
        // && getCurrentUser().getRole().getType() != Role.Type.SYS_ADMIN)
        if (securityService.canManageOtherEnterprises(user)
            && !securityService.canManageOtherEnterprises())
        {
            addForbiddenErrors(APIError.NOT_ENOUGH_PRIVILEGES);
            flushErrors();
        }

        repo.removeUser(user);

        tracer.log(SeverityType.INFO, ComponentType.USER, EventType.USER_DELETE, "user.deleted",
            user.getName(), user.getEnterprise().getName(), user.getName(), user.getSurname(), user
                .getRole());
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
        Role role = repo.findRoleById(getRoleId(dto));
        if (role == null)
        {
            addNotFoundErrors(APIError.NON_EXISTENT_ROLE);
            flushErrors();
        }
        return role;
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
        boolean sameEnterprise = enterprise.getId().equals(user.getEnterprise().getId());
        // Role.Type role = user.getRole().getType();
        //
        // if ((role == Role.Type.ENTERPRISE_ADMIN && !enterprise.equals(user.getEnterprise()))
        // || role == Role.Type.USER)

        if (securityService.isEnterpriseAdmin() && !sameEnterprise
            || securityService.isStandardUser())
        {
            throw new AccessDeniedException("");
        }
    }

    public String enterpriseWithBlockedRoles(final Enterprise enterprise)
    {
        Collection<User> users = repo.findUsersByEnterprise(enterprise);
        for (User user : users)
        {
            if (user.getRole().isBlocked())
            {
                return user.getRole().getName().toString();
            }
        }
        return "";
    }

    private void checkUserCredentialsForSelfUser(final User selfUser, final Enterprise enterprise)
    {
        User user = getCurrentUser();
        boolean sameEnterprise = enterprise.getId().equals(user.getEnterprise().getId());
        boolean sameUser = user.getId().equals(selfUser.getId());

        // Role.Type role = user.getRole().getType();
        //
        // if ((role == Role.Type.ENTERPRISE_ADMIN && !enterprise.equals(user.getEnterprise()))
        // || (role == Role.Type.USER && user.getId() != selfUser.getId()))

        if (securityService.isEnterpriseAdmin() && !sameEnterprise
            || securityService.isStandardUser() && !sameUser)
        {
            throw new AccessDeniedException("");
        }
    }

    public void checkCurrentEnterprise(final Enterprise enterprise)
    {
        User user = getCurrentUser();
        boolean sameEnterprise = enterprise.getId().equals(user.getEnterprise().getId());

        // Role.Type role = user.getRole().getType();
        // if ((role == Role.Type.ENTERPRISE_ADMIN || role == Role.Type.USER) && !sameEnterprise)
        if (!sameEnterprise
            && !securityService.hasPrivilege(Privileges.USERS_MANAGE_OTHER_ENTERPRISES)
            && !securityService.hasPrivilege(Privileges.USERS_MANAGE_ROLES_OTHER_ENTERPRISES)
            && !securityService.hasPrivilege(Privileges.ENTERPRISE_ENUMERATE)
            && !securityService.hasPrivilege(Privileges.ENTERPRISE_ADMINISTER_ALL)
            && !securityService.hasPrivilege(Privileges.PHYS_DC_ENUMERATE))
        {
            throw new AccessDeniedException("Missing privilege to get info from other enterprises");
        }
    }

    public void checkCurrentEnterpriseForUsers(final Enterprise enterprise)
    {
        User user = getCurrentUser();
        boolean sameEnterprise = enterprise.getId().equals(user.getEnterprise().getId());

        if (!sameEnterprise
            && !securityService.hasPrivilege(Privileges.USERS_MANAGE_OTHER_ENTERPRISES))
        {
            throw new AccessDeniedException("Missing privilege to get info from other enterprises");
        }
    }

    public void checkCurrentEnterpriseForPostMethods(final Enterprise enterprise)
    {
        User user = getCurrentUser();
        boolean sameEnterprise = enterprise.getId().equals(user.getEnterprise().getId());

        if (!sameEnterprise && !securityService.hasPrivilege(Privileges.ENTERPRISE_ADMINISTER_ALL))
        {
            throw new AccessDeniedException("Missing privilege to manage info from other enterprises");
        }
    }

    /**
     * Retrieves the user by nick in the DB. This method assumes that the login is unique.
     */
    public User getUserByLogin(final String login)
    {
        return repo.getUserByUserName(login);
    }

    private Boolean emailIsValid(final String email)
    {
        if (email != null && !email.isEmpty())
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
        else
        {
            return true;
        }
    }

    private String encrypt(final String toEncrypt)
    {
        MessageDigest messageDigest = null;
        try
        {
            messageDigest = MessageDigest.getInstance("MD5");
        }
        catch (NoSuchAlgorithmException e)
        {
            LOGGER.debug("cannot get the instance of messageDigest", e);
            // revise if the method is called from other method
            addUnexpectedErrors(APIError.STATUS_BAD_REQUEST);
            flushErrors();
        }
        messageDigest.reset();
        messageDigest.update(toEncrypt.getBytes(Charset.forName("UTF8")));
        final byte[] resultByte = messageDigest.digest();
        return new String(Hex.encodeHex(resultByte));
    }

    public SecurityService getSecurityService()
    {
        return securityService;
    }

    /**
     * Check if a user has permissions to use the given virtual datacenter
     * 
     * @param username nick of the given User
     * @param authtype authentication type of the given User
     * @param privileges array of strings with all privileges names from the given User role
     * @param idVdc identifier from virtual datacenter to check
     * @return True if user is allowed to user the given virtual datacenter
     */
    public boolean isUserAllowedToUseVirtualDatacenter(final String username,
        final String authtype, final String[] privileges, final Integer idVdc)
    {
        return repo.isUserAllowedToUseVirtualDatacenter(username, authtype, privileges, idVdc);
    }

    /**
     * Check if a user has permissions to use or see the given enterprise
     * 
     * @param username nick of the given User
     * @param authtype authentication type of the given User
     * @param privileges array of strings with all privileges names from the given User role
     * @param idEnteprise identifier from enterprise to check
     * @return True if user is allowed to use or see the given enterprise
     */
    public boolean isUserAllowedToEnterprise(final String username, final String authtype,
        final String[] privileges, final Integer idEnterprise)
    {
        return repo.isUserAllowedToEnterprise(username, authtype, privileges, idEnterprise);
    }
}
