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

package com.abiquo.abiserver.persistence.dao.user.hibernate;

import org.hibernate.Query;
import org.hibernate.Session;

import com.abiquo.abiserver.business.hibernate.pojohb.user.RoleHB;
import com.abiquo.abiserver.persistence.dao.user.RoleDAO;
import com.abiquo.abiserver.persistence.hibernate.HibernateDAO;
import com.abiquo.abiserver.persistence.hibernate.HibernateDAOFactory;

/**
 * * Class that implements the extra DAO functions for the
 * {@link com.abiquo.abiserver.persistence.dao.user.RoleDAO} interface
 * 
 * @author jdevesa@abiquo.com
 */
public class RoleDAOHibernate extends HibernateDAO<RoleHB, Integer> implements RoleDAO
{

    private final static String GET_ROLE_BY_NAME = "GET_ROLE_BY_NAME";

    @Override
    public RoleHB getRoleByName(final String name)
    {
        RoleHB requestedRol = new RoleHB();

        Session session = HibernateDAOFactory.getSessionFactory().getCurrentSession();
        Query userQuery = session.getNamedQuery(GET_ROLE_BY_NAME);
        userQuery.setString("name", name);
        requestedRol = (RoleHB) userQuery.uniqueResult();

        return requestedRol;
    }

}
