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

import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.Session;

import com.abiquo.abiserver.business.hibernate.pojohb.networking.NetworkHB;
import com.abiquo.abiserver.business.hibernate.pojohb.networking.VlanNetworkHB;
import com.abiquo.abiserver.exception.PersistenceException;
import com.abiquo.abiserver.persistence.dao.networking.NetworkDAO;
import com.abiquo.abiserver.persistence.hibernate.HibernateDAO;
import com.abiquo.abiserver.persistence.hibernate.HibernateDAOFactory;

/**
 * Class that implements the extra DAO functions for the
 * {@link com.abiquo.abiserver.persistence.dao.networking.PrivateNetworkDAO} interface
 * 
 * @author jdevesa@abiquo.com
 */
public class NetworkDAOHibernate extends HibernateDAO<NetworkHB, Integer> implements NetworkDAO
{

    private static final String GET_VLAN_WITH_NAME_BY_NETWORK_ID =
        "GET_VLAN_WITH_NAME_BY_NETWORK_ID";

    @Override
    public VlanNetworkHB findVlanWithName(Integer networkId, String vlanNetworkName)
        throws PersistenceException
    {
        VlanNetworkHB namedVLAN;

        try
        {
            Session session = HibernateDAOFactory.getSessionFactory().getCurrentSession();
            Query query = session.getNamedQuery(GET_VLAN_WITH_NAME_BY_NETWORK_ID);
            query.setInteger("network_id", networkId);
            query.setString("vlan_network_name", vlanNetworkName);

            namedVLAN = (VlanNetworkHB) query.uniqueResult();

        }
        catch (HibernateException e)
        {
            throw new PersistenceException(e.getMessage(), e);
        }

        return namedVLAN;
    }

    @Override
    public NetworkHB findByVirtualDatacenter(Integer vdcId)
    {
        Session session = HibernateDAOFactory.getSessionFactory().getCurrentSession();

        return (NetworkHB) session
            .createQuery(
                "select vdc.network from com.abiquo.abiserver.business.hibernate.pojohb.virtualappliance.VirtualDataCenterHB vdc where vdc.idVirtualDataCenter = :id")
            .setParameter("id", vdcId).uniqueResult();
    }
}
