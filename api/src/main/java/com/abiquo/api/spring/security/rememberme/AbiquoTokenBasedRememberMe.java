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

package com.abiquo.api.spring.security.rememberme;

import java.util.Arrays;
import java.util.Date;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.aop.framework.Advised;
import org.springframework.aop.support.AopUtils;
import org.springframework.security.Authentication;
import org.springframework.security.ui.rememberme.InvalidCookieException;
import org.springframework.security.ui.rememberme.TokenBasedRememberMeServices;
import org.springframework.security.userdetails.UserDetails;
import org.springframework.util.StringUtils;

import com.abiquo.api.spring.security.AbiquoUserDetails;
import com.abiquo.api.spring.security.AbiquoUserDetailsService;
import com.abiquo.server.core.enterprise.User.AuthType;

/**
 * Since Almost all code is done by Ben Alex, all credits go to him. This Class needs an
 * {@link AbiquoUserDetailsService}.
 * 
 * @author abiquo
 * @version 0.1
 */
public class AbiquoTokenBasedRememberMe extends TokenBasedRememberMeServices
{
    /**
     * @see org.springframework.security.ui.rememberme.TokenBasedRememberMeServices#processAutoLoginCookie(java.lang.String[],
     *      javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
     */
    @Override
    public UserDetails processAutoLoginCookie(String[] cookieTokens, HttpServletRequest request,
        HttpServletResponse response)
    {

        if (cookieTokens.length != 4)
        {
            throw new InvalidCookieException("Cookie token did not contain " + 4
                + " tokens, but contained '" + Arrays.asList(cookieTokens) + "'");
        }

        long tokenExpiryTime;

        try
        {
            tokenExpiryTime = new Long(cookieTokens[1]).longValue();
        }
        catch (NumberFormatException nfe)
        {
            throw new InvalidCookieException("Cookie token[1] did not contain a valid number (contained '"
                + cookieTokens[1] + "')");
        }

        if (isTokenExpired(tokenExpiryTime))
        {
            throw new InvalidCookieException("Cookie token[1] has expired (expired on '"
                + new Date(tokenExpiryTime) + "'; current time is '" + new Date() + "')");
        }
        synchronized (this) // Concurrency might change the value of the authType before the read.
        {
            // Since UserDetails is an Abiquo Implementation we set the authType
            try
            {
                AuthType authType = AuthType.valueOf(cookieTokens[3]);
                getTargetObject(getUserDetailsService(), AbiquoUserDetailsService.class)
                    .setAuthType(authType);
            }
            catch (ClassCastException e)
            {
                throw new InvalidCookieException("UserDetailsService must be an  AbiquoUserDetailsService'");
            }
            catch (Exception nfe)
            {
                throw new InvalidCookieException("Cookie token[3] did not contain a valid AuthType (contained '"
                    + cookieTokens[3] + "')");
            }

            // Check the user exists.
            // Defer lookup until after expiry time checked, to possibly avoid expensive database
            // call.
            UserDetails userDetails = getUserDetailsService().loadUserByUsername(cookieTokens[0]);

            // Check signature of token matches remaining details.
            // Must do this after user lookup, as we need the DAO-derived password.
            // If efficiency was a major issue, just add in a UserCache implementation,
            // but recall that this method is usually only called once per HttpSession - if the
            // token is
            // valid,
            // it will cause SecurityContextHolder population, whilst if invalid, will cause the
            // cookie
            // to be cancelled.
            String expectedTokenSignature =
                makeTokenSignature(tokenExpiryTime, userDetails.getUsername(),
                    userDetails.getPassword());

            if (!expectedTokenSignature.equals(cookieTokens[2]))
            {
                throw new InvalidCookieException("Cookie token[2] contained signature '"
                    + cookieTokens[2] + "' but expected '" + expectedTokenSignature + "'");
            }

            return userDetails;
        }
    }

    /**
     * Return the actual class at runtime in case of Proxy.
     * 
     * @param <T> Type.
     * @param proxy object.
     * @param targetClass actual class.
     * @return runtime instance.
     * @throws Exception T
     */
    protected <T> T getTargetObject(Object proxy, Class<T> targetClass) throws Exception
    {
        while (AopUtils.isJdkDynamicProxy(proxy))
        {
            return (T) ((Advised) proxy).getTargetSource().getTarget();
        }
        return (T) proxy; // expected to be cglib proxy then, which is simply a specialized
                          // class
    }

    /**
     * @see org.springframework.security.ui.rememberme.TokenBasedRememberMeServices#onLoginSuccess(javax.servlet.http.HttpServletRequest,
     *      javax.servlet.http.HttpServletResponse, org.springframework.security.Authentication)
     */
    @Override
    public void onLoginSuccess(HttpServletRequest request, HttpServletResponse response,
        Authentication successfulAuthentication)
    {
        String authType = retrieveAuthType(successfulAuthentication);
        String username = retrieveUserName(successfulAuthentication);
        String password = retrievePassword(successfulAuthentication);

        // If unable to find a username and password, just abort as TokenBasedRememberMeServices is
        // unable to construct a valid token in this case.
        if (!StringUtils.hasLength(username)
            || (!StringUtils.hasLength(password) && AuthType.ABIQUO.name().equalsIgnoreCase(
                authType)) || !StringUtils.hasLength(authType))
        {
            return;
        }

        int tokenLifetime = calculateLoginLifetime(request, successfulAuthentication);
        long expiryTime = System.currentTimeMillis() + 1000L * tokenLifetime;

        String signatureValue = makeTokenSignature(expiryTime, username, password);

        setCookie(new String[] {username, Long.toString(expiryTime), signatureValue + authType},
            tokenLifetime, request, response);

        if (logger.isDebugEnabled())
        {
            logger.debug("Added remember-me cookie for user '" + username + "', expiry: '"
                + new Date(expiryTime) + "'");
        }
    }

    /**
     * The mode of authentication.
     * 
     * @param authentication object.
     * @return String value of {@link AuthType }.
     */
    protected String retrieveAuthType(Authentication authentication)
    {
        if (isInstanceOfAbiquoUserDetails(authentication))
        {
            return ((AbiquoUserDetails) authentication.getPrincipal()).getAuthType();
        }
        else
        {
            return null;
        }
    }

    /**
     * Actual {@link AbiquoUserDetails.} or not.
     * 
     * @param authentication login.
     * @return true if is an instance of {@link AbiquoUserDetails }. False otherwise.
     */
    private boolean isInstanceOfAbiquoUserDetails(Authentication authentication)
    {
        return authentication.getPrincipal() instanceof AbiquoUserDetails;
    }
}
