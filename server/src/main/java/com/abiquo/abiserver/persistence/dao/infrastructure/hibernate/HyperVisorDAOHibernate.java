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

import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.Session;

import com.abiquo.abiserver.business.hibernate.pojohb.infrastructure.DatacenterHB;
import com.abiquo.abiserver.business.hibernate.pojohb.infrastructure.HypervisorHB;
import com.abiquo.abiserver.exception.PersistenceException;
import com.abiquo.abiserver.persistence.dao.infrastructure.HyperVisorDAO;
import com.abiquo.abiserver.persistence.hibernate.HibernateDAO;
import com.abiquo.abiserver.persistence.hibernate.HibernateDAOFactory;
import com.abiquo.model.enumerator.HypervisorType;

/**
 * * Class that implements the extra DAO functions for the
 * {@link com.abiquo.abiserver.persistence.dao.infrastructure.HyperVisorDAO} interface
 * 
 * @author jdevesa@abiquo.com
 */
public class HyperVisorDAOHibernate extends HibernateDAO<HypervisorHB, Integer> implements
    HyperVisorDAO
{
    private final static String GET_HYPERTECH_FROM_DATACENTER = "GET_HYPERTECH_FROM_DATACENTER";

    private final static String GET_VDRP = "GET_VDRP";

    private static final String GET_HYPER_FROM_PHYSICALMACHINE =
        "HYPERVISOR.GET_HYPER_FROM_PHYSICALMACHINE";

    @Override
    public List<HypervisorType> getHypervisorsTypeByDataCenter(DatacenterHB datacenter)
        throws PersistenceException
    {
        Session session = HibernateDAOFactory.getSessionFactory().getCurrentSession();

        try
        {
            Query query = session.getNamedQuery(GET_HYPERTECH_FROM_DATACENTER);
            query.setString("idDataCenter", String.valueOf(datacenter.getIdDataCenter()));

            return query.list();

        }
        catch (HibernateException e)
        {
            throw new PersistenceException(e.getMessage(), e);
        }
    }

    @Override
    public List<Integer> getUsedPortsFromDB(Integer idHypervisor, Integer idPhysicalMachine)
    {

        Session session = HibernateDAOFactory.getSessionFactory().getCurrentSession();
        Query query = session.getNamedQuery(GET_VDRP);
        query.setInteger("idHyper", idHypervisor);
        query.setInteger("idPM", idPhysicalMachine);

        return query.list();
    }

    @Override
    public HypervisorHB getHypervisorFromPhysicalMachine(Integer pmId)
    {
        Session session = HibernateDAOFactory.getSessionFactory().getCurrentSession();

        Query query = session.getNamedQuery(GET_HYPER_FROM_PHYSICALMACHINE);
        query.setInteger("pmId", pmId);

        return (HypervisorHB) query.uniqueResult();
    }

}
