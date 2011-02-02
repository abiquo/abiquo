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

package com.abiquo.abiserver.persistence.dao.infrastructure.hibernate;

import java.util.List;

import org.hibernate.Query;
import org.hibernate.Session;

import com.abiquo.abiserver.business.hibernate.pojohb.service.RemoteServiceHB;
import com.abiquo.abiserver.business.hibernate.pojohb.service.RemoteServiceType;
import com.abiquo.abiserver.persistence.dao.infrastructure.RemoteServiceDAO;
import com.abiquo.abiserver.persistence.hibernate.HibernateDAO;
import com.abiquo.abiserver.persistence.hibernate.HibernateDAOFactory;

/**
 * * Class that implements the extra DAO functions for the
 * {@link com.abiquo.abiserver.persistence.dao.infrastructure.DataCenterDAO} interface
 * 
 * @author jdevesa@abiquo.com
 */
public class RemoteServiceDAOHibernate extends HibernateDAO<RemoteServiceHB, Integer> implements
    RemoteServiceDAO
{

    protected static final String REMOTE_SERVICE_GET_REMOTE_SERVICES_BY_DATACENTER =
        "REMOTE_SERVICE.GET_REMOTE_SERVICES_BY_DATACENTER";

    protected static final String REMOTE_SERVICE_GET_REMOTE_SERVICES_BY_TYPE =
        "REMOTE_SERVICE.GET_REMOTE_SERVICES_BY_TYPE";

    protected static final String REMOTE_SERVICE_GET_REMOTE_SERVICES_BY_URL =
        "REMOTE_SERVICE.GET_REMOTE_SERVICES_BY_URL";

    protected static final String REMOTE_SERVICE_GET_REMOTE_SERVICES_LOCATION_BY_TYPE =
        "REMOTE_SERVICE.GET_REMOTE_SERVICES_LOCATION_BY_TYPE";

    @Override
    public List<RemoteServiceHB> getAllRemoteServices(final Integer idDataCenter)
    {
        Session session = HibernateDAOFactory.getSessionFactory().getCurrentSession();

        Query pmQuery = session.getNamedQuery(REMOTE_SERVICE_GET_REMOTE_SERVICES_BY_DATACENTER);
        pmQuery.setInteger("idDataCenter", idDataCenter);

        return pmQuery.list();
    }

    @Override
    public List<RemoteServiceHB> getRemoteServicesByType(final Integer idDataCenter,
        final RemoteServiceType remoteServiceType)
    {
        Session session = HibernateDAOFactory.getSessionFactory().getCurrentSession();

        Query pmQuery = session.getNamedQuery(REMOTE_SERVICE_GET_REMOTE_SERVICES_BY_TYPE);
        pmQuery.setInteger("idDataCenter", idDataCenter);
        pmQuery.setParameter("remoteServiceType", remoteServiceType);

        return pmQuery.list();
    }

    public String getRemoteServiceUriByType(final Integer idDatacenter,
        final RemoteServiceType remoteServiceType)
    {
        Session session = HibernateDAOFactory.getSessionFactory().getCurrentSession();

        Query pmQuery = session.getNamedQuery(REMOTE_SERVICE_GET_REMOTE_SERVICES_LOCATION_BY_TYPE);
        pmQuery.setInteger("idDataCenter", idDatacenter);
        pmQuery.setParameter("remoteServiceType", remoteServiceType);

        return (String) pmQuery.uniqueResult();
    }

    @Override
    public List<RemoteServiceHB> getRemoteServicesByUrl(String uri)
    {
        Session session = HibernateDAOFactory.getSessionFactory().getCurrentSession();

        Query pmQuery = session.getNamedQuery(REMOTE_SERVICE_GET_REMOTE_SERVICES_BY_URL);
        pmQuery.setString("uri", uri);

        return pmQuery.list();
    }
}
