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

import com.abiquo.abiserver.business.hibernate.pojohb.networking.DHCPServiceHB;
import com.abiquo.abiserver.business.hibernate.pojohb.networking.IpPoolManagementHB;
import com.abiquo.abiserver.exception.PersistenceException;
import com.abiquo.abiserver.persistence.dao.networking.DHCPServiceDAO;
import com.abiquo.abiserver.persistence.hibernate.HibernateDAO;
import com.abiquo.abiserver.persistence.hibernate.HibernateDAOFactory;

/**
 * Class that implements the extra DAO functions for the
 * {@link com.abiquo.abiserver.persistence.dao.networking.DHCPServiceDAO} interface
 * 
 * @author jdevesa@abiquo.com
 */
public class DHCPServiceDAOHibernate extends HibernateDAO<DHCPServiceHB, Integer> implements
    DHCPServiceDAO
{

    /**
     * Named Queries.
     */
    private static String DHCP_SERVICE_GET_AVAILABLE_IP_MANAGEMENT =
        "DHCP_SERVICE_GET_AVAILABLE_IP_MANAGEMENT";

    @Override
    public IpPoolManagementHB getNextAvailableIp(Integer dhcpServiceId, String gateway)
        throws PersistenceException
    {
        try
        {
            Session session = HibernateDAOFactory.getSessionFactory().getCurrentSession();
            Query query = session.getNamedQuery(DHCP_SERVICE_GET_AVAILABLE_IP_MANAGEMENT);
            query.setInteger("dhcpId", dhcpServiceId);
            query.setString("gateway", gateway);

            if (query.list().size() > 0)
            {
                return (IpPoolManagementHB) query.list().get(0);
            }
            else
            {
                return null;
            }
        }
        catch (HibernateException e)
        {
            throw new PersistenceException(e.getMessage(), e);
        }

    }

}
