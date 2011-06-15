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

import org.hibernate.Criteria;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.Restrictions;
import org.springframework.stereotype.Repository;

import com.abiquo.server.core.common.persistence.DefaultDAOBase;

/**
 * This class provides access to DB in order to query for {@link RoleLdap}.
 * 
 * @author ssedano
 */
@Repository("jpaLdapRoleDAO")
public class RoleLdapDAO extends DefaultDAOBase<Integer, RoleLdap>
{
    /**
     * Constructor.
     */
    public RoleLdapDAO()
    {
        super(RoleLdap.class);
    }

    /**
     * Constructor.
     * 
     * @param entityManager entitimanager.
     */
    public RoleLdapDAO(EntityManager entityManager)
    {
        super(RoleLdap.class, entityManager);
    }

    /**
     * {@link Role} that match <b>exactly</b> with type.
     * 
     * @param type name of the <code>LdapRoleDAO</code>
     * @return <code>LdapRoleDAO</code>s which type mathes name
     */
    public RoleLdap findByType(String type)
    {
        if (type == null)
        {
            return null;
        }
        // If at some point a single ldapRole will map more than one role, the implementation of
        // this function must change.
        RoleLdap role = (RoleLdap) createCriteria(type).uniqueResult();

        return role;
    }

    /**
     * @param type name.
     * @return Criteria that matches type.
     */
    private Criterion sameType(String type)
    {
        return Restrictions.eq("ldapRole", type);
    }

    private Criteria createCriteria(String type)
    {

        Criteria criteria = createCriteria();
        if (type != null)
        {
            criteria.add(sameType(type));
        }
        return criteria;
    }
}
