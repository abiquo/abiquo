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
package com.abiquo.abiserver.persistence.dao.authorization.hibernate;

import java.util.Date;
import java.util.Random;

import org.springframework.security.providers.encoding.Md5PasswordEncoder;

import com.abiquo.abiserver.business.hibernate.pojohb.authorization.OneTimeTokenSessionHB;
import com.abiquo.abiserver.persistence.dao.authorization.OneTimeTokenSessionDAO;
import com.abiquo.abiserver.persistence.hibernate.HibernateDAO;

/**
 * Hibernate DAO for the {@link OneTimeTokenSessionHB} class.
 * 
 * @author ibarrera
 */
public class OneTimeTokenSessionDAOHibernate extends HibernateDAO<OneTimeTokenSessionHB, Integer>
    implements OneTimeTokenSessionDAO
{

    @Override
    public OneTimeTokenSessionHB generateToken()
    {
        OneTimeTokenSessionHB oneTimeTokenSession = new OneTimeTokenSessionHB();
        oneTimeTokenSession.setToken(generateRandomToken());
        makePersistent(oneTimeTokenSession);
        return oneTimeTokenSession;
    }

    /**
     * Generates a pseudorandom string to be used as a token.
     * 
     * @return string.
     */
    private String generateRandomToken()
    {
        Md5PasswordEncoder enconde = new Md5PasswordEncoder();
        enconde.setEncodeHashAsBase64(true);

        Long rawPass = new Date().getTime() * new Random().nextLong();
        return enconde.encodePassword(rawPass.toString(), null);
    }

}
