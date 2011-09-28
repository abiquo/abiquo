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

package com.abiquo.server.core.appslibrary;

import java.util.List;

import javax.persistence.EntityManager;

import org.hibernate.Criteria;
import org.hibernate.Query;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.springframework.stereotype.Repository;

import com.abiquo.server.core.common.persistence.DefaultDAOBase;
import com.abiquo.server.core.enterprise.Enterprise;

@Repository("jpaOVFPackageListDAO")
public class OVFPackageListDAO extends DefaultDAOBase<Integer, OVFPackageList>
{
    private final static String FIND_BY_ENTERPRISE = "SELECT ovflist FROM OVFPackageList ovflist " //
        + "WHERE ovflist.appsLibrary.enterprise.id = :enterpriseId ";

    private final static String FIND_BY_NAME_AND_ENTERPRISE =
        "SELECT ovflist FROM OVFPackageList ovflist " //
            + "WHERE ovflist.appsLibrary.enterprise.id = :enterpriseId and ovflist.name = :nameEnt";

    public OVFPackageListDAO()
    {
        super(OVFPackageList.class);
    }

    public OVFPackageListDAO(final EntityManager entityManager)
    {
        super(OVFPackageList.class, entityManager);
    }

    public List<OVFPackageList> findByEnterprise(final Integer enterpriseId)
    {
        Query query = getSession().createQuery(FIND_BY_ENTERPRISE);
        query.setParameter("enterpriseId", enterpriseId);

        return query.list();
    }

    public OVFPackageList findByNameAndEnterprise(final String nameEnt, final Enterprise ent)
    {
        Query query = getSession().createQuery(FIND_BY_NAME_AND_ENTERPRISE);
        query.setParameter("enterpriseId", ent.getId());
        query.setParameter("nameEnt", nameEnt);

        return (OVFPackageList) query.uniqueResult();
    }

    public List<OVFPackage> findByName(final String name)
    {
        Criteria criteria = createCriteria(sameName(name));
        criteria.addOrder(Order.asc(OVFPackageList.NAME_PROPERTY));

        return criteria.list();
    }

    private static Criterion sameName(final String name)
    {
        return Restrictions.eq(OVFPackageList.NAME_PROPERTY, name);
    }
}
