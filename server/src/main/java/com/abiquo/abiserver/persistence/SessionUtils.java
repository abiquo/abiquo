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

package com.abiquo.abiserver.persistence;

import java.io.Serializable;
import java.util.List;

import org.apache.cxf.common.util.StringUtils;
import org.hibernate.Query;
import org.hibernate.Session;

public class SessionUtils
{

    public static void saveAndFlush(Session session, Object... entities)
    {
        assert session != null;
        assert entities != null;

        for (Object entity : entities)
        {
            assert entity != null;
            session.save(entity);
        }
        session.flush();
    }

    @SuppressWarnings("unchecked")
    public static <T> T uniqueResult(Session session, Class<T> cls, String queryString,
        Object... params)
    {
        assert cls != null;
        return (T) uniqueResult(session, queryString, params);
    }

    public static Object uniqueResult(Session session, String queryString, Object... params)
    {
        assert session != null;
        assert !StringUtils.isEmpty(queryString);
        assert params != null;

        Query query = createQueryWithParameters(session, queryString, params);
        return query.uniqueResult();
    }

    public static List< ? > list(Session session, String queryString, Object... params)
    {
        assert session != null;
        assert !StringUtils.isEmpty(queryString);
        assert params != null;

        Query query = createQueryWithParameters(session, queryString, params);
        return query.list();
    }

    @SuppressWarnings("unchecked")
    public static <T> List<T> list(Session session, Class<T> cls, String queryString,
        Object... params)
    {
        assert cls != null;

        return (List<T>) list(session, queryString, params);
    }

    private static Query createQueryWithParameters(Session session, String queryString,
        Object... params)
    {
        Query query = session.createQuery(queryString);
        for (int i = 0; i < params.length; i++)
        {
            Object param = params[i];
            assert param != null;
            query.setParameter(i, param);
        }
        return query;
    }

    public static <T> boolean entityExists(Session session, Class<T> cls, Serializable id)
    {
        assert session != null;
        assert cls != null;
        assert id != null;

        return session.get(cls, id) != null;
    }
}
