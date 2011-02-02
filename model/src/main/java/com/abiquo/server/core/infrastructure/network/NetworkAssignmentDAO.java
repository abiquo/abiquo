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

package com.abiquo.server.core.infrastructure.network;

import java.util.List;

import javax.persistence.EntityManager;

import org.hibernate.Criteria;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.Restrictions;
import org.springframework.stereotype.Repository;

import com.abiquo.server.core.cloud.VirtualDatacenter;
import com.abiquo.server.core.common.persistence.DefaultDAOBase;

@Repository("jpaNetworkAssignmentDAO")
public class NetworkAssignmentDAO extends DefaultDAOBase<Integer, NetworkAssignment>
{
    public NetworkAssignmentDAO()
    {
        super(NetworkAssignment.class);
    }

    public NetworkAssignmentDAO(EntityManager entityManager)
    {
        super(NetworkAssignment.class, entityManager);
    }

    public List<NetworkAssignment> findByVirtualDatacenter(VirtualDatacenter virtualDatacenter)
    {
        Criteria criteria = getSession().createCriteria(NetworkAssignment.class);

        Criterion onVdc =
            Restrictions.eq(NetworkAssignment.VIRTUAL_DATACENTER_PROPERTY, virtualDatacenter);

        criteria.add(onVdc);
        // criteria.addOrder(Order.asc(VirtualDatacenter.NAME_PROPERTY));

        List<NetworkAssignment> result = getResultList(criteria);

        return result;
    }

    public NetworkAssignment findByVlanNetwork(VLANNetwork vlanNetwork)
    {
        Criteria criteria = getSession().createCriteria(NetworkAssignment.class);

        Criterion onVlanNetwork =
            Restrictions.eq(NetworkAssignment.VLAN_NETWORK_PROPERTY, vlanNetwork);

        criteria.add(onVlanNetwork);

        List<NetworkAssignment> hbs = getResultList(criteria);

        if (hbs.size() > 0)
        {
            return hbs.get(0);
        }
        else
        {
            return null;
        }

    }
}
