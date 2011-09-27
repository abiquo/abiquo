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

import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.Session;

import com.abiquo.abiserver.business.hibernate.pojohb.networking.IpPoolManagementHB;
import com.abiquo.abiserver.exception.PersistenceException;
import com.abiquo.abiserver.persistence.dao.networking.IpPoolManagementDAO;
import com.abiquo.abiserver.persistence.hibernate.HibernateDAO;
import com.abiquo.abiserver.persistence.hibernate.HibernateDAOFactory;

/**
 * * Class that implements the extra DAO functions for the
 * {@link com.abiquo.abiserver.persistence.dao.networking.IpPoolManagementDAO} interface
 * 
 * @author jdevesa@abiquo.com
 */
public class IpPoolManagementDAOHibernate extends HibernateDAO<IpPoolManagementHB, Integer>
    implements IpPoolManagementDAO
{

    private static final String IP_POOL_GET_PRIVATE_NICS_BY_VIRTUALMACHINE =
        "IP_POOL_GET_PRIVATE_NICS_BY_VIRTUALMACHINE";

    @SuppressWarnings("unchecked")
    @Override
    public List<IpPoolManagementHB> getPrivateNICsByVirtualMachine(final Integer virtualMachineId)
        throws PersistenceException
    {
        try
        {
            Session session = HibernateDAOFactory.getSessionFactory().getCurrentSession();
            Query query = session.getNamedQuery(IP_POOL_GET_PRIVATE_NICS_BY_VIRTUALMACHINE);
            query.setInteger("vmId", virtualMachineId);

            return query.list();
        }
        catch (HibernateException e)
        {
            throw new PersistenceException(e.getMessage(), e);
        }
    }

    protected String createOrderByQuery(final String query, final String orderBy, final Boolean asc)
    {
        StringBuilder queryString = new StringBuilder(query);

        queryString.append(" order by ");
        if (orderBy.equalsIgnoreCase("ip"))
        {
            queryString
                .append(" cast(substring(ip.ip, 1, locate('.', ip.ip) - 1) as integer), cast(substring(ip.ip, locate('.', ip.ip) + 1, locate('.', ip.ip, locate('.', ip.ip) + 1) - locate('.', ip.ip) - 1) as integer), cast(substring(ip.ip, locate('.', ip.ip, locate('.', ip.ip) + 1) + 1, locate('.', ip.ip, locate('.', ip.ip, locate('.', ip.ip) + 1) + 1) - locate('.', ip.ip, locate('.', ip.ip) +  1) - 1) as integer), cast(substring(ip.ip, locate('.', ip.ip, locate('.', ip.ip, locate('.', ip.ip) + 1) + 1) + 1, 3) as integer) ");

            // set asc or desc
            if (asc)
            {
                queryString.append("asc");
            }
            else
            {
                queryString.append("desc");
            }
        }
        else if (orderBy.equalsIgnoreCase("quarantine"))
        {
            queryString.append("ip.quarantine ");

            // set asc or desc
            if (asc)
            {
                queryString.append("asc");
            }
            else
            {
                queryString.append("desc");
            }
        }
        else if (orderBy.equalsIgnoreCase("mac"))
        {
            queryString.append("ip.mac ");

            // set asc or desc
            if (asc)
            {
                queryString.append("asc");
            }
            else
            {
                queryString.append("desc");
            }
        }
        else if (orderBy.equalsIgnoreCase("vlanNetworkName"))
        {
            queryString.append("ip.vlanNetworkName ");

            // set asc or desc
            if (asc)
            {
                queryString.append("asc");
            }
            else
            {
                queryString.append("desc");
            }
        }
        else if (orderBy.equalsIgnoreCase("virtualApplianceName"))
        {
            queryString.append("vapp.name "); // Table Alias must be used to avoid OrderBy not
                                              // showing null values (ABICLOUD-703)

            // set asc or desc
            if (asc)
            {
                queryString.append("asc");
            }
            else
            {
                queryString.append("desc");
            }
        }
        else if (orderBy.equalsIgnoreCase("virtualMachineName"))
        {
            queryString.append("vm.name "); // Table Alias must be used to avoid OrderBy not showing
                                            // null values (ABICLOUD-703)

            // set asc or desc
            if (asc)
            {
                queryString.append("asc");
            }
            else
            {
                queryString.append("desc");
            }
        }
        else if (orderBy.equalsIgnoreCase("enterpriseName"))
        {
            queryString.append("ent.name ");

            // set asc or desc
            if (asc)
            {
                queryString.append("asc");
            }
            else
            {
                queryString.append("desc");
            }
        }
        else
        {
            // order by IP by default
            queryString
                .append(" cast(substring(ip.ip, 1, locate('.', ip.ip) - 1) as integer), cast(substring(ip.ip, locate('.', ip.ip) + 1, locate('.', ip.ip, locate('.', ip.ip) + 1) - locate('.', ip.ip) - 1) as integer), cast(substring(ip.ip, locate('.', ip.ip, locate('.', ip.ip) + 1) + 1, locate('.', ip.ip, locate('.', ip.ip, locate('.', ip.ip) + 1) + 1) - locate('.', ip.ip, locate('.', ip.ip) +  1) - 1) as integer), cast(substring(ip.ip, locate('.', ip.ip, locate('.', ip.ip, locate('.', ip.ip) + 1) + 1) + 1, 3) as integer) asc");

        }

        return queryString.toString();
    }

}
