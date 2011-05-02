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

import java.util.List;

import javax.naming.directory.DirContext;
import javax.naming.directory.SearchControls;
import javax.naming.ldap.InitialLdapContext;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ldap.core.DirContextAdapter;
import org.springframework.ldap.core.DirContextOperations;
import org.springframework.ldap.core.LdapEntryIdentification;
import org.springframework.ldap.core.LdapEntryIdentificationContextMapper;
import org.springframework.ldap.core.LdapTemplate;
import org.springframework.security.Authentication;
import org.springframework.security.BadCredentialsException;
import org.springframework.security.ldap.DefaultSpringSecurityContextSource;
import org.springframework.security.ldap.populator.DefaultLdapAuthoritiesPopulator;
import org.springframework.security.providers.UsernamePasswordAuthenticationToken;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import com.abiquo.api.common.AbstractGeneratorTest;
import com.abiquo.api.spring.security.authentication.providers.ldap.LdapAbiquoAuthenticationProvider;

public class SpringSecurityAuthenticationTest extends AbstractGeneratorTest
{
    @Autowired
    private LdapAbiquoAuthenticationProvider authProvider;

    /**
     * In case ldap embedded didn't shut down properly, delete the temporal folder: <br>
     * <code>rm -rf /tmp/apacheds-spring-security</code>
     */
    @BeforeMethod
    public void setUp()
    {
        Assert.assertNotNull(authProvider);
    }

    /**
     * Although it shuts down the embedded server, this process may take a while. Should it be
     * performed after method?
     */
    @AfterClass
    public void tearDown()
    {

    }

    @Test(enabled = false, dependsOnMethods = {"testSpringSecurityAuthenticationLdapPasswordFail"})
    public void testSpringSecurityAuthenticationLdapPassword()
    {

        // uid=rod,ou=people,dc=springframework,dc=org
        String login = "rod";
        String password = "koala";
        UsernamePasswordAuthenticationToken token =
            new UsernamePasswordAuthenticationToken(login, password);
        Authentication authentication = authProvider.authenticate(token);
        authentication.getAuthorities();
        Assert.assertTrue(authentication.isAuthenticated());

    }

    @Test(enabled = false)
    public void testSpringSecurityAuthenticationADPassword()
    {
        String login = "rod";
        String password = "koala";
        UsernamePasswordAuthenticationToken token =
            new UsernamePasswordAuthenticationToken(login, password);
        Authentication authentication = authProvider.authenticate(token);
        authentication.getAuthorities();
        Assert.assertTrue(authentication.isAuthenticated());
    }

    @Test(enabled = false, expectedExceptions = {BadCredentialsException.class})
    public void testSpringSecurityAuthenticationLdapPasswordFail()
    {
        // uid=rod,ou=people,dc=springframework,dc=org
        String login = "rod";
        String password = "koalakoala";
        UsernamePasswordAuthenticationToken token =
            new UsernamePasswordAuthenticationToken(login, password);

        Authentication authentication = authProvider.authenticate(token);

    }

    @Test(enabled = false)
    public void testSpringSecurityAuthenticationLdap()
    {
        LdapAbiquoAuthenticationProvider auth =
            (LdapAbiquoAuthenticationProvider) applicationContext.getBean("ldapAuthProvider");
        Assert.assertNotNull(auth);

        DefaultLdapAuthoritiesPopulator ldapAuthorities =
            (DefaultLdapAuthoritiesPopulator) applicationContext.getBean("ldapAuthorities");

        DefaultSpringSecurityContextSource contextSource =
            (DefaultSpringSecurityContextSource) applicationContext.getBean("contextSource");

        LdapTemplate ldapTemplate = new LdapTemplate(contextSource);
        // ldapTemplate.authenticate("", "(sAMAccountName=Administrator)", "Windowssucks0!");
        // LdapAbiquoAuthenticatorImpl ldapAuthenticator =
        // (LdapAbiquoAuthenticatorImpl) applicationContext.getBean("ldapAuthenticator");

        try
        {

            SearchControls search = new SearchControls();

            // Specify the attributes to return
            String returnedAtts[] = {"sn", "givenName", "mail"};
            search.setReturningAttributes(returnedAtts);

            // Specify the search scope
            search.setSearchScope(SearchControls.SUBTREE_SCOPE);

            // specify the LDAP search filter
            String searchFilter = "(&(objectClass=user)(mail=*))";

            // Specify the Base for the search
            String searchBase = "dc=bcn,dc=abiquo,dc=com";

            // NamingEnumeration answer = ldapTemplate.search(searchBase, searchFilter, search);

            List<LdapEntryIdentification> l =
                ldapTemplate.search("", "(uid=rod)", search,
                    new LdapEntryIdentificationContextMapper());

            for (LdapEntryIdentification n : l)
            {

                DirContext lContext =
                    contextSource.getContext(n.getAbsoluteDn().toCompactString(), "koala");
                // lContext.getAttributes("rod");
                InitialLdapContext ldapContext = (InitialLdapContext) lContext;
                DirContextOperations authAdapter = new DirContextAdapter();
                authAdapter.addAttributeValue("ldapContext", ldapContext);
                ldapAuthorities.getGrantedAuthorities(authAdapter, "rod");
                ldapAuthorities.getGroupMembershipRoles(
                    "cn=Rod Johnson,ou=people,dc=springframework,dc=org", "rod");
                ldapAuthorities.getGroupMembershipRoles(n.getAbsoluteDn().toCompactString(), "rod");
            }
            List ll =
                ldapTemplate.search("", "sAMAccountName=rod", search,
                    new LdapEntryIdentificationContextMapper());

        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }
}
