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

/**
 * 
 */
package com.abiquo.abiserver.persistence.dao.networking.hibernate;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.Session;

import com.abiquo.abiserver.abicloudws.AbiCloudConstants;
import com.abiquo.abiserver.business.hibernate.pojohb.networking.IpPoolManagementHB;
import com.abiquo.abiserver.business.hibernate.pojohb.networking.VlanNetworkHB;
import com.abiquo.abiserver.exception.PersistenceException;
import com.abiquo.abiserver.persistence.dao.networking.VlanNetworkDAO;
import com.abiquo.abiserver.persistence.hibernate.HibernateDAO;
import com.abiquo.abiserver.persistence.hibernate.HibernateDAOFactory;

/**
 * Class that implements the extra DAO functions for the
 * {@link com.abiquo.abiserver.persistence.dao.networking.VlanNetworkDAO} interface
 * 
 * @author jdevesa@abiquo.com
 */
public class VlanNetworkDAOHibernate extends HibernateDAO<VlanNetworkHB, Integer> implements
    VlanNetworkDAO
{

    // Named queries
    private static String GET_DEFAULT_VLAN_BY_NETWORK_ID = "GET_DEFAULT_VLAN_BY_NETWORK_ID";

    private static String GET_NUMBER_IPS_USED_BY_VLAN = "GET_NUMBER_IPS_USED_BY_VLAN";

    private static String GET_NUMBER_VDCS_USED_BY_VLAN = "GET_NUMBER_VDCS_USED_BY_VLAN";

    private static String IS_PUBLIC_VLAN = "IS_PUBLIC_VLAN";

    private static String GET_NUMBER_VLAN_BY_ENTERPRISE = "GET_NUMBER_VLAN_BY_ENTERPRISE";

    private static String VLAN_BY_ENTERPRISE_AND_DATACENTER = "VLAN_BY_ENTERPRISE_AND_DATACENTER";

    private static String VLAN_BY_VDC = "VLAN_BY_VDC";

    private static String GET_PRIVATE_VLANS_BY_DATACENTER = "GET_PRIVATE_VLANS_BY_DATACENTER";

    private static String GET_MAX_FREE_TAG = "GET_MAX_FREE_TAG";

    @Override
    public Integer getFreeVLANTag(final Integer idRack)
    {
        Integer freeVlanTag = 0;

        final Session session = HibernateDAOFactory.getSessionFactory().getCurrentSession();
        final Query query = session.getNamedQuery(GET_MAX_FREE_TAG);
        query.setInteger("idRack", idRack);

        final Integer latestVlanTagUsed = (Integer) query.uniqueResult();

        if (latestVlanTagUsed != null)
        {
            freeVlanTag = latestVlanTagUsed + 1;
            if (freeVlanTag > AbiCloudConstants.VLAN_MAX)
            {
                return null; // "The VLAN tag limitation has been reached ";
            }

        }
        else
        {
            freeVlanTag = 3;
        }

        if (freeVlanTag == 2)
        {
            if (freeVlanTag == AbiCloudConstants.VLAN_MAX)
            {
                return null; // "The VLAN tag limitation has been reached ";
            }

            freeVlanTag++;
        }

        return freeVlanTag;
    }

    @Override
    public VlanNetworkHB getDefaultVLAN(final Integer idNetwork) throws PersistenceException
    {
        VlanNetworkHB defaultVLAN;

        try
        {
            final Session session = HibernateDAOFactory.getSessionFactory().getCurrentSession();
            final Query query = session.getNamedQuery(GET_DEFAULT_VLAN_BY_NETWORK_ID);
            query.setInteger("network_id", idNetwork);

            defaultVLAN = (VlanNetworkHB) query.uniqueResult();

        }
        catch (final HibernateException e)
        {
            throw new PersistenceException(e.getMessage(), e);
        }

        return defaultVLAN;
    }

    @Override
    public Long howManyUsedIPs(final Integer vlanNetworkId) throws PersistenceException
    {
        try
        {
            final Session session = HibernateDAOFactory.getSessionFactory().getCurrentSession();
            final Query query = session.getNamedQuery(GET_NUMBER_IPS_USED_BY_VLAN);
            query.setInteger("vlan_network_id", vlanNetworkId);
            return (Long) query.uniqueResult();
        }
        catch (final HibernateException e)
        {
            throw new PersistenceException(e.getMessage(), e);
        }
    }

    @Override
    public Long howManyVDCs(final Integer vlanNetworkId) throws PersistenceException
    {
        try
        {
            final Session session = HibernateDAOFactory.getSessionFactory().getCurrentSession();
            final Query query = session.getNamedQuery(GET_NUMBER_VDCS_USED_BY_VLAN);
            query.setInteger("vlan_network_id", vlanNetworkId);
            return (Long) query.uniqueResult();
        }
        catch (final HibernateException e)
        {
            throw new PersistenceException(e.getMessage(), e);
        }
    }

    @Override
    public Boolean isPrivateVLAN(final Integer vlanNetworkId) throws PersistenceException
    {
        try
        {
            final Session session = HibernateDAOFactory.getSessionFactory().getCurrentSession();
            final Query query = session.getNamedQuery(IS_PUBLIC_VLAN);
            query.setInteger("vlan_network_id", vlanNetworkId);
            return (Long) query.uniqueResult() == 0L;
        }
        catch (final HibernateException e)
        {
            throw new PersistenceException(e.getMessage(), e);
        }
    }

    @Override
    public List<VlanNetworkHB> findByEnterprise(final Integer idEnterprise)
    {
        final Map<String, Integer> params = new HashMap<String, Integer>();
        params.put("idEnterprise", idEnterprise);
        return findByNamedQuery(GET_NUMBER_VLAN_BY_ENTERPRISE, params);
    }

    @Override
    public List<VlanNetworkHB> findByEnterpriseAndDatacenter(final Integer idEnterpirse,
        final Integer idDatacenter)
    {
        final Map<String, Integer> params = new HashMap<String, Integer>();
        params.put("idEnterprise", idEnterpirse);
        params.put("idDatacenter", idDatacenter);
        return findByNamedQuery(VLAN_BY_ENTERPRISE_AND_DATACENTER, params);
    }

    @Override
    public List<VlanNetworkHB> findByVirtualDatacenter(final Integer idVirtualDatacenter)
    {
        final Map<String, Integer> params = new HashMap<String, Integer>();
        params.put("idVirtualDatacenter", idVirtualDatacenter);
        return findByNamedQuery(VLAN_BY_VDC, params);
    }

    @SuppressWarnings("unchecked")
    @Override
    public List<VlanNetworkHB> findPrivateVLANsByDatacenter(final Integer idDatacenter)
        throws PersistenceException
    {
        try
        {
            Session session = HibernateDAOFactory.getSessionFactory().getCurrentSession();
            Query query = session.getNamedQuery(GET_PRIVATE_VLANS_BY_DATACENTER);
            query.setInteger("datacenterId", idDatacenter);

            return query.list();
        }
        catch (final HibernateException e)
        {
            throw new PersistenceException(e.getMessage(), e);
        }
    }

    /**
     * Named Queries.
     */
    private static String VLAN_GET_AVAILABLE_IP_MANAGEMENT = "VLAN_GET_AVAILABLE_IP_MANAGEMENT";

    @Override
    public IpPoolManagementHB getNextAvailableIp(final Integer vlanNetworkId, final String gateway)
        throws PersistenceException
    {
        try
        {
            Session session = HibernateDAOFactory.getSessionFactory().getCurrentSession();
            Query query = session.getNamedQuery(VLAN_GET_AVAILABLE_IP_MANAGEMENT);
            query.setInteger("vlanNetworkId", vlanNetworkId);
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
