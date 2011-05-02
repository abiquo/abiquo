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

package com.abiquo.api.spring.security;

import javax.naming.NamingException;
import javax.naming.directory.Attributes;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ldap.core.DirContextAdapter;
import org.springframework.ldap.core.DirContextOperations;
import org.springframework.security.GrantedAuthority;
import org.springframework.security.GrantedAuthorityImpl;
import org.springframework.security.userdetails.UserDetails;
import org.springframework.security.userdetails.ldap.UserDetailsContextMapper;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import com.abiquo.server.core.enterprise.Enterprise;
import com.abiquo.server.core.enterprise.EnterpriseRep;
import com.abiquo.server.core.enterprise.LdapRole;
import com.abiquo.server.core.enterprise.User;

/**
 * Implementation fo the interface to load data from LDAP/Active Directory to our platform. And
 * viceversa.
 * 
 * @author ssedano
 */

public class AbiquoUserDetailsContextMapper implements UserDetailsContextMapper
{
    /**
     * A default role which will be assigned to all authenticated users if set <br>
     * Defaults to <code>ROLE_ABIQUO</code>.
     */
    private GrantedAuthority defaultRole;

    /** The default role prefix to use. */
    private String rolePrefix;

    /**
     * The default role prefix to use. By convention ROLE_
     * 
     * @return String
     */
    public String getRolePrefix()
    {
        return rolePrefix;
    }

    /**
     * The default role prefix to use. Defined in XML.
     * 
     * @param rolePrefix void
     */
    public void setRolePrefix(String rolePrefix)
    {
        this.rolePrefix = rolePrefix;
    }

    /**
     * The Enterprise DAO repository. Only the method which access this rep declares
     * transactionality.
     */
    @Autowired
    protected EnterpriseRep enterpriseRep;

    /**
     * This implementation tries to lookup the user in Abiquo database and if it does not exists
     * creates a user and registers it. The data of that brand new user is retrieved from the
     * LDAP/Active Directory.
     * 
     * @see org.springframework.security.userdetails.ldap.UserDetailsContextMapper#mapUserFromContext(org.springframework.ldap.core.DirContextOperations,
     *      java.lang.String, org.springframework.security.GrantedAuthority[])
     */
    @Override
    @Transactional(readOnly = false, propagation = Propagation.REQUIRED)
    public UserDetails mapUserFromContext(DirContextOperations ctx, String username,
        GrantedAuthority[] authority)
    {
        AbiquoUserDetails userDetails = new AbiquoUserDetails();

        String login = username; // activeDirectoryToLdapLogin(username);
        User user = enterpriseRep.getUserByUserName(login);
        Attributes attributes = ctx.getAttributes();
        try
        {
            if (user != null)
            {
                // the roles might change in LDAP
                GrantedAuthority role = authority[0];
                if (!role.getAuthority().equalsIgnoreCase(user.getRole().getType().name()))
                {
                    LdapRole ldapRole;

                    ldapRole = (LdapRole) attributes.get("ldapRole").get();
                    user.setRole(ldapRole.getRole());
                    enterpriseRep.updateUser(user);
                }

                userDetailsFromUser(userDetails, user);
            }
            else
            {
                // We register a new User
                User u = addUser(login, attributes);
                userDetailsFromUser(userDetails, u);
            }

        }
        catch (NamingException e)
        {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return userDetails;
    }

    /**
     * Inserts a new Abiquo User in the DB imported from LDAP/Active Directory.
     * 
     * @param username user login.
     * @param attributes
     * @return
     * @throws NamingException User
     */
    private User addUser(String username, Attributes attributes) throws NamingException
    {
        LdapRole ldapRole = (LdapRole) attributes.get("ldapRole").get();
        String name = (String) attributes.get("givenName").get();
        String surname = (String) attributes.get("sn").get();
        String email = (String) attributes.get("mail").get();

        Enterprise enterprise = addEnterprise(attributes);

        User u =
            new User(enterprise, ldapRole.getRole(), name, surname, email, username, null, "en_US");
        u.setActive(1);
        u.setDescription((String) attributes.get("description").get());
        enterpriseRep.insertUser(u);
        return u;
    }

    /**
     * There can't be users with no enterprise associated.
     * 
     * @param attributes context attributes from LDAP/AD.
     * @return Brand new enterprise or an existing enterprise that matches by name.
     * @throws NamingException Enterprise
     */
    private Enterprise addEnterprise(Attributes attributes) throws NamingException
    {
        String enterpriseName = (String) attributes.get("company").get();
        Enterprise e = enterpriseRep.findByName(enterpriseName);
        if (e == null)
        {
            e = new Enterprise(enterpriseName, 0, 0, 0, 0, 0, 0);
            enterpriseRep.insert(e);
        }
        return e;
    }

    /**
     * Completes the {@link AbiquoUserDetails} with a {@link User} which already was in Abiquo.
     * 
     * @param userDetails instance of {@link AbiquoUserDetails}.
     * @param user AbiquoUser.
     */
    private void userDetailsFromUser(AbiquoUserDetails userDetails, User user)
    {
        userDetails.setUserId(user.getId());
        userDetails.setUsername(user.getNick());
        userDetails.setPassword(user.getPassword());
        userDetails.setActive(user.getActive() == 1);

        userDetails.setEnterpriseId(user.getEnterprise().getId());
        userDetails.setEnterpriseName(user.getEnterprise().getName());

        // Set user authorities
        GrantedAuthority[] authorities = loadUserAuthorities(user);
        userDetails.setAuthorities(authorities);
    }

    /**
     * Load the granted authorities for the authenticated user.
     * 
     * @param user The authenticated user.
     * @return An array with the granted authorities.
     */
    protected GrantedAuthority[] loadUserAuthorities(final User user)
    {
        String role = rolePrefix + user.getRole().getType();
        if (defaultRole != null)
        {
            return new GrantedAuthority[] {new GrantedAuthorityImpl(role), defaultRole};
        }
        return new GrantedAuthority[] {new GrantedAuthorityImpl(role)};
    }

    /**
     * The default role which will be assigned to all users. Defaults to <code>ROLE_ABIQUO</code>.
     * 
     * @param defaultRole the role name, including any desired prefix.
     */
    public void setDefaultRole(String defaultRole)
    {
        Assert.notNull(defaultRole, "The defaultRole property cannot be set to null");
        this.defaultRole = new GrantedAuthorityImpl(defaultRole);
    }

    /**
     * We are not creating users nor modifying them in the LDAP/Active Directory
     * 
     * @see org.springframework.security.userdetails.ldap.UserDetailsContextMapper#mapUserToContext(org.springframework.security.userdetails.UserDetails,
     *      org.springframework.ldap.core.DirContextAdapter)
     */
    @Override
    public void mapUserToContext(UserDetails user, DirContextAdapter ctx)
    {
        // We are not doing anything to bind data from Abiquo to LDAP/AD.

    }

}
