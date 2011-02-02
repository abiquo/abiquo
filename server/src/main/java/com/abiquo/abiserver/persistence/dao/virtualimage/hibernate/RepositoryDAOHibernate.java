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

package com.abiquo.abiserver.persistence.dao.virtualimage.hibernate;

import org.hibernate.Query;
import org.hibernate.Session;

import com.abiquo.abiserver.business.hibernate.pojohb.virtualimage.RepositoryHB;
import com.abiquo.abiserver.exception.PersistenceException;
import com.abiquo.abiserver.persistence.dao.virtualimage.RepositoryDAO;
import com.abiquo.abiserver.persistence.hibernate.HibernateDAO;
import com.abiquo.abiserver.persistence.hibernate.HibernateDAOFactory;

/**
 * Class that implements the extra DAO functions for the
 * {@link com.abiquo.abiserver.persistence.dao.virtualimage.RepositoryDAO} interface
 * 
 * @author jdevesa@abiquo.com, apuig
 */
public class RepositoryDAOHibernate extends HibernateDAO<RepositoryHB, Integer> implements
    RepositoryDAO
{

    // XXX same (change the mapping class) as DCRepositoryDAO on project 'model'
    private final static String QUERY_GET_BY_DC =
        "FROM com.abiquo.abiserver.business.hibernate.pojohb.virtualimage.RepositoryHB WHERE "
            + "datacenter.idDataCenter = :idDatacenter";// AND enterprise.idEnterprise =

    // :idEnterprise";

    private final static String QUERY_GET_BY_URL =
        "FROM com.abiquo.abiserver.business.hibernate.pojohb.virtualimage.RepositoryHB WHERE "
            + "url = :repositoryLocation";// AND enterprise.idEnterprise = :idEnterprise";

    private final String REPOSITORY_GET_BY_DATACENTER = "REPOSITORY_GET_BY_DATACENTER";

    @Override
    public RepositoryHB findByDatacenter(Integer idDatacenter)
    {
        return (RepositoryHB) getSession().createQuery(QUERY_GET_BY_DC)
        // .setParameter("idEnterprise", idEnterprise)
            .setParameter("idDatacenter", idDatacenter).uniqueResult();
    }

    @Override
    public RepositoryHB findByLocation(String repositoryLocation)
    {
        return (RepositoryHB) getSession().createQuery(QUERY_GET_BY_URL)
        // .setParameter("idEnterprise", idEnterprise)
            .setParameter("repositoryLocation", repositoryLocation).uniqueResult();
    }

    @Override
    public RepositoryHB findByDatacenterDAO(Integer datacenterId) throws PersistenceException
    {
        RepositoryHB repository;

        Session session = HibernateDAOFactory.getSessionFactory().getCurrentSession();
        Query query = session.createQuery(QUERY_GET_BY_DC);
        query.setInteger("idDatacenter", datacenterId);

        repository = (RepositoryHB) query.uniqueResult();

        return repository;
    }
}
