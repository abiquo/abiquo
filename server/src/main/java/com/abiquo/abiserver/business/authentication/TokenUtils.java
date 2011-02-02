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

package com.abiquo.abiserver.business.authentication;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.codec.digest.DigestUtils;

/**
 * Utility methods to manipulate authentication tokens.
 * 
 * @author ibarrera
 */
public class TokenUtils
{
    /**
     * Creates the authentication token signature.
     * 
     * @param tokenExpiryTime The token expiration time.
     * @param username The token user name.
     * @param password The token password.
     * @param key The token key.
     * @return The token.
     */
    public static String makeTokenSignature(final long tokenExpiryTime, final String username,
        final String password)
    {
        return DigestUtils.md5Hex(username + ":" + tokenExpiryTime + ":" + password
            + ":abiquo-auth-key");
    }

    /**
     * Returns an array containing each token field
     * <ul>
     * <li>[0] => The token signature.</li>
     * <li>[1] => The token user name.</li>
     * <li>[2] => The token expiration time.</li>
     * </ul>
     * 
     * @param token The token.
     * @return The token fields.
     * @throws Exception
     */
    public static String[] getTokenFields(final String token) throws Exception
    {
        String base64Token = token;
        for (int j = 0; j < base64Token.length() % 4; j++)
        {
            base64Token = base64Token + "=";
        }

        if (!Base64.isArrayByteBase64(base64Token.getBytes()))
        {
            throw new Exception("Token is not Base64 encoded");
        }

        String decodedToken = new String(Base64.decodeBase64(base64Token.getBytes()));
        return decodedToken.split(":");
    }

    /**
     * Checks if the token is expired.
     * 
     * @param tokenExpiryTime The token expiration time.
     * @return A boolean indicating if the token is expired.
     */
    public static boolean isTokenExpired(final long tokenExpiryTime)
    {
        return tokenExpiryTime < System.currentTimeMillis();
    }

    /**
     * Get the user from the token.
     * 
     * @param token The token.
     * @return The user.
     */
    public static String getTokenUser(final String[] token)
    {
        return token[0];
    }

    /**
     * Get the expiration from the token.
     * 
     * @param token The token.
     * @return The expiration.
     */
    public static long getTokenExpiration(final String[] token)
    {
        return new Long(token[1]).longValue();
    }

    /**
     * Get the signature from the token.
     * 
     * @param token The token.
     * @return The signature.
     */
    public static String getTokenSignature(final String[] token)
    {
        return token[2];
    }

}
