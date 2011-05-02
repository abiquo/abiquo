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

package com.abiquo.api.spring.security.authentication.populator;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import javax.naming.directory.SearchControls;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ldap.core.ContextSource;
import org.springframework.ldap.core.DirContextOperations;
import org.springframework.security.GrantedAuthority;
import org.springframework.security.GrantedAuthorityImpl;
import org.springframework.security.ldap.LdapAuthoritiesPopulator;
import org.springframework.security.ldap.SpringSecurityLdapTemplate;
import org.springframework.security.ldap.populator.DefaultLdapAuthoritiesPopulator;
import org.springframework.util.Assert;

import com.abiquo.api.spring.security.authentication.LdapAbiquoAuthenticatorImpl;
import com.abiquo.server.core.enterprise.EnterpriseRep;
import com.abiquo.server.core.enterprise.LdapRole;

/**
 * Custom implementation in order to match LDAP/AD roles with Abiquo roles.
 * 
 * @author ssedano
 */
/**
 * @author ssedano
 */
public class LdapAbiquoAuthoritiesPopulator implements LdapAuthoritiesPopulator
{
    private static final Log logger = LogFactory.getLog(DefaultLdapAuthoritiesPopulator.class);

    // ~ Instance fields
    // ================================================================================================

    /**
     * A default role which will be assigned to all authenticated users if set <br>
     * Defaults to <code>ROLE_ABIQUO</code>.
     */
    private GrantedAuthority defaultRole;

    /**
     * Template to make searches. In AD it needs to be passed from the
     * {@link LdapAbiquoAuthenticatorImpl} if it does not support anonymous.
     */
    private SpringSecurityLdapTemplate ldapTemplate;

    /**
     * @return template.
     */
    public SpringSecurityLdapTemplate getLdapTemplate()
    {
        return ldapTemplate;
    }

    public void setLdapTemplate(SpringSecurityLdapTemplate ldapTemplate)
    {
        this.ldapTemplate = ldapTemplate;
    }

    /**
     * Controls used to determine whether group searches should be performed over the full sub-tree
     * from the base DN. Modified by searchSubTree property
     */
    private final SearchControls searchControls = new SearchControls();

    /**
     * The ID of the attribute which contains the role name for a group
     */
    private String groupRoleAttribute = "cn";

    /**
     * The base DN from which the search for group membership should be performed
     */
    private String groupSearchBase;

    /**
     * The pattern to be used for the user search. {0} is the user's DN
     */
    private String groupSearchFilter = "(member={0})";

    /**
     * Attributes of the User's LDAP Object that contain role name information.
     */

    // private String[] userRoleAttributes = null;
    private String rolePrefix;

    public String getRolePrefix()
    {
        return rolePrefix;
    }

    private boolean convertToUpperCase = true;

    // ~ Constructors
    // ===================================================================================================

    /**
     * Constructor for group search scenarios. <tt>userRoleAttributes</tt> may still be set as a
     * property.
     * 
     * @param contextSource supplies the contexts used to search for user roles.
     * @param groupSearchBase if this is an empty string the search will be performed from the root
     *            DN of the context factory.
     */
    public LdapAbiquoAuthoritiesPopulator(ContextSource contextSource, String groupSearchBase)
    {
        ldapTemplate = new SpringSecurityLdapTemplate(contextSource);
        ldapTemplate.setSearchControls(searchControls);
        ldapTemplate.setIgnorePartialResultException(true);
        setGroupSearchBase(groupSearchBase);
    }

    // ~ Methods
    // ========================================================================================================

    /**
     * Obtains the authorities for the user who's directory entry is represented by the supplied
     * LdapUserDetails object. <br>
     * This function also applies the default ROLE_ABIQUO which every single user logged successfuly
     * in Abiquo has.
     * 
     * @param user the user who's authorities are required
     * @return the set of Abiquo roles granted to the user.
     */
    @Override
    public final GrantedAuthority[] getGrantedAuthorities(DirContextOperations user, String username)
    {
        String userDn = user.getNameInNamespace();
        ldapTemplate.setIgnorePartialResultException(true);
        if (logger.isDebugEnabled())
        {
            logger.debug("Getting authorities for user " + userDn);
        }

        Set roles = getGroupMembershipRoles(userDn, username);

        Set<GrantedAuthority> abiquoRoles = getAdditionalRoles(user, username, roles);

        if (defaultRole != null)
        {
            abiquoRoles.add(defaultRole);
        }
        return abiquoRoles.toArray(new GrantedAuthority[abiquoRoles.size()]);
    }

    /**
     * Authorities which are tied to the user in LDAP/AD.
     * 
     * @param userDn Distinguished name.
     * @param username login.
     * @return Only the Abiquo roles.
     */
    public Set getGroupMembershipRoles(String userDn, String username)
    {
        Set authorities = new HashSet();

        if (getGroupSearchBase() == null)
        {
            return authorities;
        }

        if (logger.isDebugEnabled())
        {
            logger.debug("Searching for roles for user '" + username + "', DN = " + "'" + userDn
                + "', with filter " + groupSearchFilter + " in search base '"
                + getGroupSearchBase() + "'");
        }

        Set userRoles =
            ldapTemplate.searchForSingleAttributeValues(getGroupSearchBase(), groupSearchFilter,
                new String[] {userDn, username}, groupRoleAttribute);

        if (logger.isDebugEnabled())
        {
            logger.debug("Roles from search: " + userRoles);
        }

        Iterator it = userRoles.iterator();

        while (it.hasNext())
        {
            String role = (String) it.next();

            if (convertToUpperCase)
            {
                role = role.toUpperCase();
            }

            authorities.add(new GrantedAuthorityImpl(rolePrefix + role));
        }

        return authorities;
    }

    /**
     * @return ContextSource.
     */
    protected ContextSource getContextSource()
    {
        return ldapTemplate.getContextSource();
    }

    /**
     * Set the group search base (name to search under)
     * 
     * @param groupSearchBase if this is an empty string the search will be performed from the root
     *            DN of the context factory.
     */
    private void setGroupSearchBase(String groupSearchBase)
    {
        Assert.notNull(groupSearchBase,
            "The groupSearchBase (name to search under), must not be null.");
        this.groupSearchBase = groupSearchBase;
        if (groupSearchBase.length() == 0)
        {
            logger
                .info("groupSearchBase is empty. Searches will be performed from the context source base");
        }
    }

    /**
     * @return groupSearchBase
     */
    protected String getGroupSearchBase()
    {
        return groupSearchBase;
    }

    public void setConvertToUpperCase(boolean convertToUpperCase)
    {
        this.convertToUpperCase = convertToUpperCase;
    }

    /**
     * The default role which will be assigned to all users.
     * 
     * @param defaultRole the role name, including any desired prefix.
     */
    public void setDefaultRole(String defaultRole)
    {
        Assert.notNull(defaultRole, "The defaultRole property cannot be set to null");
        this.defaultRole = new GrantedAuthorityImpl(defaultRole);
    }

    public void setGroupRoleAttribute(String groupRoleAttribute)
    {
        Assert.notNull(groupRoleAttribute, "groupRoleAttribute must not be null");
        this.groupRoleAttribute = groupRoleAttribute;
    }

    public void setGroupSearchFilter(String groupSearchFilter)
    {
        Assert.notNull(groupSearchFilter, "groupSearchFilter must not be null");
        this.groupSearchFilter = groupSearchFilter;
    }

    /**
     * Sets the prefix which will be prepended to the values loaded from the directory. Defaults to
     * "ROLE_" for compatibility with <tt>RoleVoter/tt>.
     */
    public void setRolePrefix(String rolePrefix)
    {
        Assert.notNull(rolePrefix, "rolePrefix must not be null");
        this.rolePrefix = rolePrefix;
    }

    /**
     * If set to true, a subtree scope search will be performed. If false a single-level search is
     * used.
     * 
     * @param searchSubtree set to true to enable searching of the entire tree below the
     *            <tt>groupSearchBase</tt>.
     */
    public void setSearchSubtree(boolean searchSubtree)
    {
        int searchScope =
            searchSubtree ? SearchControls.SUBTREE_SCOPE : SearchControls.ONELEVEL_SCOPE;
        searchControls.setSearchScope(searchScope);
    }

    @Autowired
    private EnterpriseRep enterpriseRep;

    /**
     * Maps Ldap roles to Abiquo roles. Sould mapping be providen in the installation? <br>
     * 
     * @param roles LDAP roles.
     * @see org.springframework.security.ldap.populator.DefaultLdapAuthoritiesPopulator#getAdditionalRoles(org.springframework.ldap.core.DirContextOperations,
     *      java.lang.String)
     */
    protected Set<GrantedAuthority> getAdditionalRoles(DirContextOperations user, String username,
        Set roles)
    {
        Set<GrantedAuthority> abiquoRoles = new HashSet<GrantedAuthority>();

        for (Object o : roles)
        {
            GrantedAuthority rol = (GrantedAuthority) o;
            String type = rol.getAuthority();
            // We add this prefix previously.
            if (type.startsWith(rolePrefix))
            {
                type = type.replaceFirst(rolePrefix, "");
            }
            LdapRole ldapRole = enterpriseRep.findLdapRoleByType(type);

            if (ldapRole != null && ldapRole.getRole() != null)
            {
                abiquoRoles.add(new GrantedAuthorityImpl(rolePrefix
                    + ldapRole.getRole().getType().name()));

                // We need this role in the context just in case authorities given to this user had
                // changed since last login
                user.addAttributeValue("ldapRole", ldapRole);
            }
            // FIXME : This break should be removed if in later versions users can be mapped to more
            // than one role. Rest of the code is ready to multi role users.
            // One LDAP/AD user must be mapped to no more than one LDAP/AD role (1.8 requirement)
            break;
        }

        return abiquoRoles;
    }
}
