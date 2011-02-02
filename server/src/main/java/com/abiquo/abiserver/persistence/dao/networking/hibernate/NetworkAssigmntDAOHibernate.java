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

package com.abiquo.abiserver.persistence.dao.networking.hibernate;

import java.util.List;

import org.hibernate.Query;
import org.hibernate.Session;

import com.abiquo.abiserver.business.hibernate.pojohb.networking.NetworkAssignmentHB;
import com.abiquo.abiserver.persistence.dao.networking.NetworkAssigmntDAO;
import com.abiquo.abiserver.persistence.hibernate.HibernateDAO;
import com.abiquo.abiserver.persistence.hibernate.HibernateDAOFactory;

public class NetworkAssigmntDAOHibernate extends HibernateDAO<NetworkAssignmentHB, Integer>
    implements NetworkAssigmntDAO
{

    private static final String GET_BY_VDC = "NET_ASSIG.GET_BY_VDC";

    private static final String GET_BY_VLAN = "NET_ASSIG.GET_BY_VLAN";

    public List<NetworkAssignmentHB> findByVirtualDatacenter(final Integer idVirtualDataCenter)
    {
        Session session = HibernateDAOFactory.getSessionFactory().getCurrentSession();
        Query query = session.getNamedQuery(GET_BY_VDC);
        query.setInteger("idVirtualDataCenter", idVirtualDataCenter);

        return query.list();
    }

    public NetworkAssignmentHB findByVlan(final Integer vlanNetworkId)
    {
        Session session = HibernateDAOFactory.getSessionFactory().getCurrentSession();
        Query query = session.getNamedQuery(GET_BY_VLAN);
        query.setInteger("vlanNetworkId", vlanNetworkId);

        return (NetworkAssignmentHB) query.uniqueResult();
    }
}
