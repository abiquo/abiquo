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

import java.util.List;

import javax.persistence.EntityManager;

import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.Restrictions;
import org.springframework.stereotype.Repository;

import com.abiquo.server.core.common.persistence.DefaultDAOBase;

@Repository("jpaRoleLdapDAO")
public class RoleLdapDAO extends DefaultDAOBase<Integer, RoleLdap>
{
    public RoleLdapDAO()
    {
        super(RoleLdap.class);
    }

    public RoleLdapDAO(final EntityManager entityManager)
    {
        super(RoleLdap.class, entityManager);
    }

    public RoleLdap findByRoleLdap(final String roleLdap)
    {
        List<RoleLdap> rls = findByCriterions(sameRoleLdap(roleLdap));
        if (rls == null || rls.isEmpty() || rls.size() > 1)
        {
            // TODO: throw error (more than 1 results)
        }
        return rls.get(0);
    }

    private Criterion sameRoleLdap(final String roleLdap)
    {
        return Restrictions.eq(RoleLdap.ROLE_LDAP_PROPERTY, roleLdap);
    }

}
