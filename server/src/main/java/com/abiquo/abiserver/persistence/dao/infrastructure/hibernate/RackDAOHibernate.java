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

import java.util.ArrayList;
import java.util.List;

import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.Session;

import com.abiquo.abiserver.business.hibernate.pojohb.infrastructure.PhysicalmachineHB;
import com.abiquo.abiserver.business.hibernate.pojohb.infrastructure.RackHB;
import com.abiquo.abiserver.exception.PersistenceException;
import com.abiquo.abiserver.persistence.dao.infrastructure.RackDAO;
import com.abiquo.abiserver.persistence.hibernate.HibernateDAO;
import com.abiquo.abiserver.persistence.hibernate.HibernateDAOFactory;

/**
 * Class that implements the extra DAO functions for the
 * {@link com.abiquo.abiserver.persistence.dao.infrastructure.RackDAO} interface
 * 
 * @author jdevesa@abiquo.com
 */
public class RackDAOHibernate extends HibernateDAO<RackHB, Integer> implements RackDAO
{

    private final static String FIND_BY_MIN_VLANS = "RACKS.FIND_BY_MIN_VLANS";
    private final static String GET_PHYSICAL_MACHINES_BY_RACK = "RACKS.GET_PHYSICAL_MACHINE_BY_RACK";

    private static String GET_LOWEST_VLANIDMAX_VALUE =
        "RACK.GET_LOWEST_VLANIDMAX_VALUE";

    @Override
    public List<Integer> getRackIdByMinVlanCount(Integer vlan_vdc,
        Integer idVApp)
    {
        Session session = HibernateDAOFactory.getSessionFactory().getCurrentSession();
        Query query = session.getNamedQuery(FIND_BY_MIN_VLANS);

        query.setInteger("vlan_vdc", vlan_vdc);
        query.setInteger("idVApp", idVApp);

        return (List<Integer>) query.list();
    }

    @SuppressWarnings("unchecked")
    @Override
    public List<PhysicalmachineHB> getPhysicalMachines(Integer rackId, String filters)
    {
        try
        {
            final Session session = HibernateDAOFactory.getSessionFactory().getCurrentSession();
            final Query query = session.getNamedQuery(GET_PHYSICAL_MACHINES_BY_RACK);
            query.setInteger("idRack", rackId);
//            query.setString("filterLike", (filters == null || filters.isEmpty()) ? "%" : "%" + filters + "%");

            return (ArrayList<PhysicalmachineHB>) query.list();
        }
        catch (HibernateException he)
        {
            throw new PersistenceException(he.getMessage());
        }
    }

    @Override
    public Integer getLowestVlanIdMax(Integer datacenterId) throws PersistenceException
    {
        try
        {
            final Session session = HibernateDAOFactory.getSessionFactory().getCurrentSession();
            final Query query = session.getNamedQuery(GET_LOWEST_VLANIDMAX_VALUE);
            query.setInteger("datacenterId", datacenterId);
            if (query.uniqueResult() != null)
            {
                return (Integer) query.uniqueResult();
            }
            else
            {
                return null;
            }

        }
        catch (final HibernateException e)
        {
            throw new PersistenceException(e.getMessage(), e);
        }
    }
}
