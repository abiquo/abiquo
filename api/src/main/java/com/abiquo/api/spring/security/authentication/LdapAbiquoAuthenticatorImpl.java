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

package com.abiquo.api.spring.security.authentication;

import java.util.Iterator;

import javax.naming.Context;
import javax.naming.directory.DirContext;
import javax.naming.ldap.InitialLdapContext;

import org.springframework.dao.DataAccessException;
import org.springframework.ldap.NamingException;
import org.springframework.ldap.core.ContextSource;
import org.springframework.ldap.core.DirContextOperations;
import org.springframework.ldap.core.DistinguishedName;
import org.springframework.security.Authentication;
import org.springframework.security.BadCredentialsException;
import org.springframework.security.ldap.DefaultSpringSecurityContextSource;
import org.springframework.security.ldap.SpringSecurityContextSource;
import org.springframework.security.ldap.SpringSecurityLdapTemplate;
import org.springframework.security.providers.UsernamePasswordAuthenticationToken;
import org.springframework.security.providers.ldap.LdapAuthenticator;
import org.springframework.security.providers.ldap.authenticator.BindAuthenticator;
import org.springframework.util.Assert;

/**
 * Custom Spring Security LDAP authenticator which tries to bind to an LDAP server using the
 * passed-in credentials; does <strong>not</strong> require "master" credentials for an initial bind
 * prior to searching for the passed-in username.
 * 
 * @author ssedano
 */

public class LdapAbiquoAuthenticatorImpl extends BindAuthenticator
{
    /**
     * Constructor.
     * 
     * @param contextSource context.
     */
    public LdapAbiquoAuthenticatorImpl(SpringSecurityContextSource contextSource)
    {
        super(contextSource);
    }

    /**
     * ContextFactory.
     */
    private DefaultSpringSecurityContextSource contextFactory;

    /**
     *Roles Prefix.
     */
    private String principalPrefix = "";

    /**
     * Binds the <code>username</code> to the server with the <code>userDn</code> using
     * <code>password</code>.
     * 
     * @param userDn Distinguished name.
     * @param username login.
     * @param password password.
     * @return DirContextOperations binded context.
     */
    private DirContextOperations bindWithDn(String userDn, String username, String password)
    {
        SpringSecurityLdapTemplate template =
            new SpringSecurityLdapTemplate(new BindWithSpecificDnContextSource((SpringSecurityContextSource) getContextSource(),
                userDn,
                password));

        try
        {

            DirContextOperations user = template.retrieveEntry(userDn, getUserAttributes());
            // We pass the ldapTemplate binded to the dn in order to make searches later in the
            // application (AD workaround)
            user.addAttributeValue("ldapTemplate", template);
            return user;

        }
        catch (BadCredentialsException e)
        {
            // This will be thrown if an invalid user name is used and the method may
            // be called multiple times to try different names, so we trap the exception
            // unless a subclass wishes to implement more specialized behaviour.
            handleBindException(userDn, username, e.getCause());
        }

        return null;
    }

    /**
     * Binds the <code>username</code> to the server with the <code>userDn</code> using
     * <code>password</code>.
     * 
     * @param userDn Distinguished name.
     * @param username login.
     * @param password password.
     * @return DirContextOperations binded context.
     */
    private DirContextOperations bindWithDomainName(String username, String password)
    {
        try
        { // If this fails the login is not admitted by the server
            this.contextFactory.getContext(username, password);

            // Work Around to allow domain\login and login@full.domain
            String login = activeDirectoryToLdapLogin(username);
            // cn={0},CN=Users is the default search in AD
            return bindWithDn("cn=" + login + ",CN=Users", login, password);
        }
        catch (Exception e)
        {
            // This will be thrown if an invalid user name is used and the method may
            // be called multiple times to try different names, so we trap the exception
            // unless a subclass wishes to implement more specialized behaviour.
            handleBindException(this.contextFactory.getBaseLdapPathAsString(), username, e
                .getCause());
        }

        return null;
    }

    /**
     * Active Directory allow login in
     * <ul>
     * <li>login</li>
     * <li>domain\login</li >
     * <li>login@full.domain.dn</li>
     * </ul>
     * To provide this compatibility we must get rid of <b>domain\</b> and <b>@full.domain.dn</b>.
     * 
     * @param username login.
     * @return login.
     */
    private String activeDirectoryToLdapLogin(String username)
    {
        String login = username;
        int index = 0;

        if ((index = username.indexOf("\\")) != -1)
            login = username.substring(index + 1);
        else if ((index = username.indexOf("@")) != -1)
            login = username.substring(0, index);
        else
            login = username;
        return login;
    }

    /**
     * Implementació de ContextSource <code>bind</code>ed to a Distinguished name.
     * 
     * @author ssedano
     */
    private class BindWithSpecificDnContextSource implements ContextSource
    {
        /**
         * Context.
         */
        private final SpringSecurityContextSource ctxFactory;

        /**
         * DistinguishedName.
         */
        DistinguishedName userDn;

        /**
         * password
         */
        private final String password;

        /**
         * Constructor.
         * 
         * @param ctxFactory context.
         * @param userDn DistinguishedName.
         * @param password password.
         */
        public BindWithSpecificDnContextSource(SpringSecurityContextSource ctxFactory,
            String userDn, String password)
        {
            this.ctxFactory = ctxFactory;
            this.userDn = new DistinguishedName(userDn);
            this.userDn.prepend(ctxFactory.getBaseLdapPath());
            contextFactory.setReferral(Context.REFERRAL);
            this.password = password;
        }

        /**
         * @see org.springframework.ldap.core.ContextSource#getReadOnlyContext()
         */
        public DirContext getReadOnlyContext() throws DataAccessException
        {
            return ctxFactory.getReadWriteContext(userDn.toString(), password);
        }

        /**
         * @see org.springframework.ldap.core.ContextSource#getReadWriteContext()
         */
        public DirContext getReadWriteContext() throws DataAccessException
        {
            return getReadOnlyContext();
        }

        /**
         * @see org.springframework.ldap.core.ContextSource#getContext(java.lang.String,
         *      java.lang.String)
         */
        @Override
        public DirContext getContext(String principal, String credentials) throws NamingException
        {
            return contextFactory.getContext(principal, credentials);
        }
    }

    /**
     * @see org.springframework.security.providers.ldap.authenticator.BindAuthenticator#authenticate(org.springframework.security.Authentication)
     */
    @Override
    public DirContextOperations authenticate(Authentication authentication)
    {
        DirContextOperations user = null;
        Assert.isInstanceOf(UsernamePasswordAuthenticationToken.class, authentication,
            "Can only process UsernamePasswordAuthenticationToken objects");

        String username = authentication.getName();
        String password = (String) authentication.getCredentials();
        if (principalPrefix != null && !"".equals(principalPrefix))
        {
            username = principalPrefix.concat("\\").concat(username);
        }
        // If DN patterns are configured, try authenticating with them directly
        Iterator dns = getUserDns(username).iterator();

        while (dns.hasNext() && user == null)
        {
            String dn = (String) dns.next();
            user = bindWithDn(dn, username, password);
            if (user != null)
            {
                break;
            }
        }

        // Admit domain\\username and username@full.domain.dn
        if (user == null)
        {
            user = bindWithDomainName(username, password);
        }

        // Otherwise use the configured locator to find the user
        // and authenticate with the returned DN.
        // With AD which does not support anonymous binding this will fail!
        if (user == null && getUserSearch() != null)
        {
            DirContextOperations userFromSearch = getUserSearch().searchForUser(username);
            user = bindWithDn(userFromSearch.getDn().toString(), username, password);
        }

        if (user == null)
        {
            throw new BadCredentialsException(messages.getMessage(
                "BindAuthenticator.badCredentials", "Bad credentials"));
        }
        return user;
    }

    /**
     * Since the InitialLdapContext that's stored as a property of an LdapAuthenticationToken is
     * transient (because it isn't Serializable), we need some way to recreate the
     * InitialLdapContext if it's null (e.g., if the LdapAuthenticationToken has been serialized and
     * deserialized). This is that mechanism.
     * 
     * @param authenticator the LdapAuthenticator instance from your application's context
     * @param auth the LdapAuthenticationToken in which to recreate the InitialLdapContext
     * @return
     */
    static public InitialLdapContext recreateLdapContext(LdapAuthenticator authenticator,
        Authentication auth)
    {
        DirContextOperations authAdapter = authenticator.authenticate(auth);
        InitialLdapContext context =
            (InitialLdapContext) authAdapter.getObjectAttribute("ldapContext");
        // auth.setContext(context);
        return context;
    }

    public DefaultSpringSecurityContextSource getContextFactory()
    {
        return contextFactory;
    }

    /**
     * Set the context factory to use for generating a new LDAP context.
     * 
     * @param contextFactory
     */
    public void setContextFactory(DefaultSpringSecurityContextSource contextFactory)
    {
        this.contextFactory = contextFactory;
    }

    public String getPrincipalPrefix()
    {
        return principalPrefix;
    }

    /**
     * Set the string to be prepended to all principal names prior to attempting authentication
     * against the LDAP server. (For example, if the Active Directory wants the domain-name-plus
     * backslash prepended, use this.)
     * 
     * @param principalPrefix
     */
    public void setPrincipalPrefix(String principalPrefix)
    {
        if (principalPrefix != null)
        {
            this.principalPrefix = principalPrefix;
        }
        else
        {
            this.principalPrefix = "";
        }

    }

}
