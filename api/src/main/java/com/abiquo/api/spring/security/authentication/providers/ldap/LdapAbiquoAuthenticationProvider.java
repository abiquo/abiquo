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

package com.abiquo.api.spring.security.authentication.providers.ldap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.support.MessageSourceAccessor;
import org.springframework.ldap.AuthenticationException;
import org.springframework.ldap.core.DirContextOperations;
import org.springframework.security.Authentication;
import org.springframework.security.BadCredentialsException;
import org.springframework.security.GrantedAuthority;
import org.springframework.security.SpringSecurityMessageSource;
import org.springframework.security.ldap.LdapAuthoritiesPopulator;
import org.springframework.security.ldap.SpringSecurityLdapTemplate;
import org.springframework.security.providers.AuthenticationProvider;
import org.springframework.security.providers.UsernamePasswordAuthenticationToken;
import org.springframework.security.providers.ldap.LdapAuthenticator;
import org.springframework.security.userdetails.UserDetails;
import org.springframework.security.userdetails.ldap.UserDetailsContextMapper;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

import com.abiquo.api.spring.security.authentication.populator.LdapAbiquoAuthoritiesPopulator;
import com.abiquo.api.spring.security.authentication.token.LdapAbiquoAuthenticationToken;

/**
 * Custom provider. This class controls the Authentication against LDAP/AD.
 * 
 * @author ssedano
 */
/**
 * @author ssedano
 */
public class LdapAbiquoAuthenticationProvider implements AuthenticationProvider
{
    protected MessageSourceAccessor messages = SpringSecurityMessageSource.getAccessor();

    /**
     * logger.
     */
    private static final Logger logger = LoggerFactory
        .getLogger(LdapAbiquoAuthenticationProvider.class);

    /**
     * Constructor.
     * 
     * @param authenticator an implementation of a {@link LdapAuthenticator} LdapAuthenticator.
     */
    public LdapAbiquoAuthenticationProvider(LdapAuthenticator authenticator)
    {
        this(authenticator, null);
    }

    /**
     * @param authenticator an implementation of a {@link LdapAuthenticator} LdapAuthenticator.
     * @param authoritiesPopulator an implementation of a {@link LdapAuthoritiesPopulator}
     *            LdapAuthenticator.
     */
    public LdapAbiquoAuthenticationProvider(LdapAuthenticator authenticator,
        LdapAuthoritiesPopulator authoritiesPopulator)
    {
        setAuthenticator(authenticator);
        setAuthoritiesPopulator(authoritiesPopulator);
    }

    /**
     * Custom authenticator.
     */
    private LdapAuthenticator authenticator;

    /**
     * Custom authoritiesPopulator.
     */
    private LdapAuthoritiesPopulator authoritiesPopulator;

    /**
     * Implementation of UserDetailsContextMapper.
     */
    private UserDetailsContextMapper userDetailsContextMapper;

    /**
     * @return UserDetailsContextMapper Implementation of UserDetailsContextMapper.
     */
    public UserDetailsContextMapper getUserDetailsContextMapper()
    {
        return userDetailsContextMapper;
    }

    /**
     * @param userDetailsContextMapper Implementation of UserDetailsContextMapper.
     */
    public void setUserDetailsContextMapper(UserDetailsContextMapper userDetailsContextMapper)
    {
        this.userDetailsContextMapper = userDetailsContextMapper;
    }

    /**
     * @return Implementation of UserDetailsContextMapper.LdapAuthoritiesPopulator
     */
    public LdapAuthoritiesPopulator getAuthoritiesPopulator()
    {
        return authoritiesPopulator;
    }

    /**
     * @param authoritiesPopulator Implementation of authoritiesPopulator. void
     */
    public void setAuthoritiesPopulator(LdapAuthoritiesPopulator authoritiesPopulator)
    {
        this.authoritiesPopulator = authoritiesPopulator;
    }

    /**
     * This class assures that GrantedAuthority will never be null, but empty.
     * 
     * @author ssedano
     */
    private static class NullAuthoritiesPopulator implements LdapAuthoritiesPopulator
    {
        /**
         * @see org.springframework.security.ldap.LdapAuthoritiesPopulator#getGrantedAuthorities(org.springframework.ldap.core.DirContextOperations,
         *      java.lang.String)
         */
        public GrantedAuthority[] getGrantedAuthorities(DirContextOperations userDetails,
            String username)
        {
            return new GrantedAuthority[0];
        }
    }

    /**
     * We return an {@link UsernamePasswordAuthenticationToken} should this function admits that
     * very same type¿?
     * 
     * @see org.springframework.security.providers.AuthenticationProvider#authenticate(org.springframework.security.Authentication)
     */
    public Authentication authenticate(Authentication authentication)
        throws AuthenticationException
    {
        // UsernamePasswordAuthenticationToken authToken =
        // (UsernamePasswordAuthenticationToken) authentication;
        String username = authentication.getName();

        if (!StringUtils.hasLength(username))
        {
            throw new BadCredentialsException("LdapAuthenticationProvider.emptyUsername",
                "Empty Username");
        }

        String password = (String) authentication.getCredentials();
        Assert.notNull(password, "Null password was supplied in authentication token");

        if (password.length() == 0)
        {
            logger.debug("Rejecting empty password for user " + username);
            throw new BadCredentialsException("LdapAuthenticationProvider.emptyPassword",
                "Empty Password");
        }

        // Authenticate, using the passed-in credentials.
        DirContextOperations authAdapter = authenticator.authenticate(authentication);

        // Creating an LdapAuthenticationToken (rather than using the existing Authentication
        // object) allows us to add the already-created LDAP context for our app to use later.
        // LdapAbiquoAuthenticationToken ldapAuth = new
        // LdapAbiquoAuthenticationToken(authentication);
        // DirContextOperations ldapContext =
        // (DirContextOperations) authAdapter.getObjectAttribute("ldapContext");
        //
        // if (ldapContext != null)
        // {
        // ldapAuth.setContext(ldapContext);
        // }

        SpringSecurityLdapTemplate ldapTemplate =
            (SpringSecurityLdapTemplate) authAdapter.getObjectAttribute("ldapTemplate");
        if (ldapTemplate != null)
        {
            ((LdapAbiquoAuthoritiesPopulator) this.getAuthoritiesPopulator())
                .setLdapTemplate(ldapTemplate);
        }

        GrantedAuthority[] extraAuthorities = loadUserAuthorities(authAdapter, username, password);

        if (extraAuthorities == null || extraAuthorities.length == 0)
        {
            // No Abiquo roles therefore the login fails
            throw new BadCredentialsException(messages.getMessage(
                "BindAuthenticator.badCredentials", "Bad credentials"));
        }
        UserDetails user =
            userDetailsContextMapper.mapUserFromContext(authAdapter, username, extraAuthorities);

        Authentication logged = createSuccessfulAuthentication(authentication, user);

        return logged;
    }

    /**
     * Although this function always returns an instance of UsernamePasswordAuthenticationToken, the
     * method could be overriden or modified to return almost any token.
     * 
     * @param authentication token.
     * @param user details.
     * @return Authentication UsernamePasswordAuthenticationToken.
     */
    protected Authentication createSuccessfulAuthentication(Authentication authentication,
        UserDetails user)
    {
        Object password = authentication.getCredentials();

        UsernamePasswordAuthenticationToken token =
            new UsernamePasswordAuthenticationToken(user, password, user.getAuthorities());
        token.setDetails(user);
        return token;
    }

    /**
     * Load the authorities.
     * 
     * @param userData details.
     * @param username login.
     * @param password password.
     * @return GrantedAuthority[].
     */
    protected GrantedAuthority[] loadUserAuthorities(DirContextOperations userData,
        String username, String password)
    {
        return getAuthoritiesPopulator().getGrantedAuthorities(userData, username);
    }

    /**
     * UsernamePasswordAuthenticationToken LdapAbiquoAuthenticationToken
     * 
     * @see org.springframework.security.providers.AuthenticationProvider#supports(java.lang.Class)
     */
    public boolean supports(Class clazz)
    {
        return (UsernamePasswordAuthenticationToken.class.isAssignableFrom(clazz) || LdapAbiquoAuthenticationToken.class
            .isAssignableFrom(clazz));
    }

    /**
     * @return LdapAuthenticator.
     */
    public LdapAuthenticator getAuthenticator()
    {
        return authenticator;
    }

    /**
     * @param authenticator authenticator.
     */
    public void setAuthenticator(LdapAuthenticator authenticator)
    {
        this.authenticator = authenticator;
    }

}
