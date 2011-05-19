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

import java.util.Date;
import java.util.Random;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.providers.encoding.Md5PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.abiquo.server.core.enterprise.EnterpriseRep;

/**
 * Provides the basic functionalities to operate one time token.
 * 
 * @author ssedano
 */
@Service
@Transactional(readOnly = true)
public class OneTimeTokenService extends DefaultApiService
{
    private Logger LOGGER = LoggerFactory.getLogger(OneTimeTokenService.class);

    @Autowired
    EnterpriseRep repo;

    /**
     * Generates a valid and persisted one time token to be used as an authentication header in http
     * petitions.
     */
    @Transactional(readOnly = false)
    public void generateOneTimeToken()
    {
        LOGGER.debug("generateOneTimeToken: generating token... ");
        String token = generateToken();

        repo.persistToken(token);
        LOGGER.debug("generateOneTimeToken: token generated!");
    }

    /**
     * Generates a pseudorandom string to be used as a token.
     * 
     * @return string.
     */
    private String generateToken()
    {
        Md5PasswordEncoder enconde = new Md5PasswordEncoder();

        enconde.setEncodeHashAsBase64(true);
        Long rawPass = new Date().getTime() * new Random().nextLong();
        String token = enconde.encodePassword(rawPass.toString(), null);
        return token;
    }
}
