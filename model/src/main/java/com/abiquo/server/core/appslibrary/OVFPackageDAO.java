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

@Repository("jpaOVFPackageDAO")
public class OVFPackageDAO extends DefaultDAOBase<Integer, OVFPackage>
{

    private final static String FIND_BY_ENTERPRISE = "SELECT ovf FROM OVFPackage ovf " //
        + "WHERE ovf.appsLibrary.enterprise.id = :enterpriseId ";

    public OVFPackageDAO()
    {
        super(OVFPackage.class);
    }

    public OVFPackageDAO(final EntityManager entityManager)
    {
        super(OVFPackage.class, entityManager);
    }

    public OVFPackage findByUrl(final String url)
    {
        return findUniqueByProperty(OVFPackage.URL_PROPERTY, url);
    }

    public List<OVFPackage> findByEnterprise(final Integer enterpriseId)
    {
        Query query = getSession().createQuery(FIND_BY_ENTERPRISE);
        query.setParameter("enterpriseId", enterpriseId);

        return query.list();
    }

    public List<OVFPackage> findByAppsLibrary(final AppsLibrary appsLibrary)
    {
        Criteria criteria = createCriteria(sameAppsLibrary(appsLibrary));
        criteria.addOrder(Order.asc(OVFPackage.NAME_PROPERTY));

        return criteria.list();
    }

    private static Criterion sameAppsLibrary(final AppsLibrary appsLibrary)
    {
        return Restrictions.eq(OVFPackage.APPS_LIBRARY_PROPERTY, appsLibrary);
    }
}
