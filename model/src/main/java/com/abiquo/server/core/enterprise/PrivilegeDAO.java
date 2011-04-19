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

import org.hibernate.Query;
import org.springframework.stereotype.Repository;

import com.abiquo.server.core.common.persistence.DefaultDAOBase;

@Repository("jpaPrivilegeDAO")
public class PrivilegeDAO extends DefaultDAOBase<Integer, Privilege>
{
    public PrivilegeDAO()
    {
        super(Privilege.class);
    }

    public PrivilegeDAO(final EntityManager entityManager)
    {
        super(Privilege.class, entityManager);
    }

    @Override
    public List<Privilege> findAll()
    {
        return createCriteria().list();
    }

    public List<Privilege> findByRole(final Integer idRole)
    {
        // TODO Use criteria when role has got priveleges list property

        Query query =
            getSession().createQuery(
                "select p from roles_privileges rp left outer join privileges p"
                    + "on rp.idPrivilege = p.idPrivilege where rp.idRole = :idRole");

        query.setInteger("idRole", idRole);
        List<Privilege> privileges = query.list();
        return privileges;
    }

    // TODO create function to modify privileges for a role when exist priveleges list property
}
