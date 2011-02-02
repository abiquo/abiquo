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

package com.abiquo.api.persistence.impl;

import java.util.List;

import org.springframework.stereotype.Repository;

import com.abiquo.api.persistence.JpaDAO;
import com.abiquo.server.core.appslibrary.OVFPackageList;

@Repository
public class OVFPackageListDAO extends JpaDAO<OVFPackageList, Integer>
{
    private static final String FIND_BY_DATACENTER =
        "from OVFPackageList where appsLibrary.enterprise.id = :idEnterprise";

    private static final String FIND_BY_NAME =
        "from OVFPackageList where name = :name AND appsLibrary.enterprise.id = :idEnterprise";

    @Override
    protected Class<OVFPackageList> getPersistentClass()
    {
        return OVFPackageList.class;
    }

    public List<OVFPackageList> findByEnterprise(final Integer idEnterprise)
    {
        return entityManager.createQuery(FIND_BY_DATACENTER).setParameter("idEnterprise",
            idEnterprise).getResultList();
    }

    public OVFPackageList findByName(final String name, final Integer idEnterprise)
    {
        return (OVFPackageList) entityManager.createQuery(FIND_BY_NAME).setParameter("name", name)
            .setParameter("idEnterprise", idEnterprise).getSingleResult();
    }

}
