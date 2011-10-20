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

package com.abiquo.server.core.enterprise;

import javax.persistence.EntityManager;

import org.hibernate.Query;
import org.springframework.stereotype.Repository;

import com.abiquo.server.core.common.persistence.DefaultDAOBase;

/**
 * @author ssedano
 */
@Repository("jpaOneTimeTokeSessionDAO")
public class OneTimeTokenSessionDAO extends DefaultDAOBase<Integer, OneTimeTokenSession>
{
    public OneTimeTokenSessionDAO()
    {
        super(OneTimeTokenSession.class);
    }

    public OneTimeTokenSessionDAO(EntityManager entityManager)
    {
        super(OneTimeTokenSession.class, entityManager);
    }

    /**
     * HQL to consume tokens.
     */
    private static String CONSUME_TOKEN = "delete " + OneTimeTokenSession.class.getSimpleName()
        + " where token = :token";

    /**
     * Consume the given token. Which actually deletes the token. Returns the number of rows
     * affected by the update. Ideally only 1.
     * 
     * @param token token to be consumed.
     * @return number of rows affected.
     */
    public int consumeToken(String token)
    {
        Query query = this.getSession().createQuery(CONSUME_TOKEN);
        query.setString("token", token);
        return query.executeUpdate();
    }

}
