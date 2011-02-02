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
import com.abiquo.abiserver.business.hibernate.pojohb.networking.VlanNetworkHB;
import com.abiquo.abiserver.business.hibernate.pojohb.user.EnterpriseHB;
import com.abiquo.abiserver.exception.PersistenceException;
import com.abiquo.abiserver.networking.IPAddress;
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

    /** Named queries */
    private static final String IP_POOL_MANAGEMENT_GET_IPADDRESS_FROM_MAC =
        "IP_POOL_MANAGEMENT_GET_IPADDRESS_FROM_MAC";

    private static final String IP_POOL_GET_NETWORK_POOL_BY_VDC = "IP_POOL_GET_NETWORK_POOL_BY_VDC";

    private static final String IP_POOL_GET_NETWORK_POOL_BY_VLAN =
        "IP_POOL_GET_NETWORK_POOL_BY_VLAN";

    private static final String IP_POOL_GET_PRIVATE_NICS_BY_VIRTUALMACHINE =
        "IP_POOL_GET_PRIVATE_NICS_BY_VIRTUALMACHINE";

    private static final String IP_POOL_GET_NETWORK_POOL_AVAILABLE_BY_VLAN =
        "IP_POOL_GET_NETWORK_POOL_AVAILABLE_BY_VLAN";

    private static final String IP_POOL_GET_PRIVATE_IP_BY_VLAN = "IP_POOL_GET_PRIVATE_IP_BY_VLAN";

    private static final String IP_POOL_GET_NETWORK_POOL_BY_ENTERPRISE =
        "IP_POOL_GET_NETWORK_POOL_BY_ENTERPRISE";

    private static final String IP_POOL_GET_ENTERPRISES_WITH_NETWORK_BY_DATACENTER =
        "IP_POOL_GET_ENTERPRISES_WITH_NETWORK_BY_DATACENTER";

    private static final String IP_POOL_GET_VLAN_BY_IP_POOL_MANAGEMENT =
        "IP_POOL_GET_VLAN_BY_IP_POOL_MANAGEMENT";

    private static final String IP_POOL_GET_BY_VLAN = "IP_POOL.GET_BY_VLAN";

    private static final String IP_POOL_GET_BY_VIRTUAL_MACHIE = "IP_POOL.GET_BY_VIRTUAL_MACHIE";

    @Override
    public List<IpPoolManagementHB> findByVirtualMachine(Integer idVm)
    {
        Session session = HibernateDAOFactory.getSessionFactory().getCurrentSession();
        Query query = session.getNamedQuery(IP_POOL_GET_BY_VIRTUAL_MACHIE);

        query.setInteger("idVm", idVm);

        return query.list();
    }

    @Override
    public boolean isVlanAssignedToDifferentVM(Integer idVm, Integer vlan_network_id)
    {
        Session session = HibernateDAOFactory.getSessionFactory().getCurrentSession();
        Query query = session.getNamedQuery(IP_POOL_GET_BY_VLAN);

        query.setInteger("idVm", idVm);
        query.setInteger("vlan_network_id", vlan_network_id);

        List<IpPoolManagementHB> ippoolList = (List<IpPoolManagementHB>) query.list();
        if (ippoolList.isEmpty())
        {
            return false;
        }
        return true;
    }

    @Override
    public boolean existingMACAddress(String MACaddress) throws PersistenceException
    {
        try
        {
            Session session = HibernateDAOFactory.getSessionFactory().getCurrentSession();
            Query query = session.getNamedQuery(IP_POOL_MANAGEMENT_GET_IPADDRESS_FROM_MAC);
            query.setString("mac", MACaddress);
            if (query.uniqueResult() == null)
            {
                return false;
            }
            else
            {
                return true;
            }
        }
        catch (HibernateException e)
        {
            throw new PersistenceException(e.getMessage(), e);
        }
    }

    @SuppressWarnings("unchecked")
    @Override
    public List<EnterpriseHB> getEnterprisesWithNetworksByDatacenter(Integer datacenterId,
        Integer offset, Integer numElem, String filterLike) throws PersistenceException
    {
        try
        {
            Session session = HibernateDAOFactory.getSessionFactory().getCurrentSession();
            Query query = session.getNamedQuery(IP_POOL_GET_ENTERPRISES_WITH_NETWORK_BY_DATACENTER);

            query.setInteger("datacenterId", datacenterId);
            query.setString("filterLike", (filterLike.isEmpty()) ? "%" : "%" + filterLike + "%");
            query.setFirstResult(offset);
            if (numElem != null)
            {
                query.setMaxResults(numElem);
            }

            return query.list();
        }
        catch (HibernateException e)
        {
            throw new PersistenceException(e.getMessage(), e);
        }
    }

    @Override
    public IpPoolManagementHB getIpPoolManagementByVLANandIP(Integer vlanId, IPAddress requestedIP)
        throws PersistenceException
    {
        try
        {
            Session session = HibernateDAOFactory.getSessionFactory().getCurrentSession();
            Query query = session.getNamedQuery(IP_POOL_GET_PRIVATE_IP_BY_VLAN);
            query.setInteger("vlanId", vlanId);
            query.setString("ip", requestedIP.toString());

            return (IpPoolManagementHB) query.uniqueResult();
        }
        catch (HibernateException e)
        {
            throw new PersistenceException(e.getMessage(), e);
        }
    }

    @SuppressWarnings("unchecked")
    @Override
    public List<IpPoolManagementHB> getNetworkPoolAvailableByVLAN(Integer vlanId, Integer offset,
        Integer numElem, String filterLike) throws PersistenceException
    {
        List<IpPoolManagementHB> listOfPools;

        try
        {
            Session session = HibernateDAOFactory.getSessionFactory().getCurrentSession();
            Query query = session.getNamedQuery(IP_POOL_GET_NETWORK_POOL_AVAILABLE_BY_VLAN);
            query.setInteger("vlanId", vlanId);
            query.setString("filterLike", (filterLike.isEmpty()) ? "%" : "%" + filterLike + "%");

            query.setFirstResult(offset);
            if (numElem != null)
            {
                query.setMaxResults(numElem);
            }

            listOfPools = query.list();
        }
        catch (HibernateException e)
        {
            throw new PersistenceException(e.getMessage(), e);
        }

        return listOfPools;
    }

    @SuppressWarnings("unchecked")
    @Override
    public List<IpPoolManagementHB> getNetworkPoolByEnterprise(Integer enterpriseId,
        Integer offset, Integer numElem, String filterLike, String orderBy, Boolean asc)
        throws PersistenceException
    {
        try
        {
            Session session = HibernateDAOFactory.getSessionFactory().getCurrentSession();
            Query query = session.getNamedQuery(IP_POOL_GET_NETWORK_POOL_BY_ENTERPRISE);
            if (orderBy != null)
            {
                String newQuery = createOrderByQuery(query.getQueryString(), orderBy, asc);
                query = session.createQuery(newQuery);
            }
            query.setInteger("enterpriseId", enterpriseId);
            query.setString("filterLike", (filterLike.isEmpty()) ? "%" : "%" + filterLike + "%");
            query.setFirstResult(offset);
            if (numElem != null)
            {
                query.setMaxResults(numElem);
            }

            return query.list();
        }
        catch (HibernateException e)
        {
            throw new PersistenceException(e.getMessage(), e);
        }
    }

    @SuppressWarnings("unchecked")
    @Override
    public List<IpPoolManagementHB> getNetworkPoolByVDC(Integer vdcId, Integer offset,
        Integer numElem, String filterLike, String orderBy, Boolean asc)
        throws PersistenceException
    {
        List<IpPoolManagementHB> listOfPools;

        try
        {
            Session session = HibernateDAOFactory.getSessionFactory().getCurrentSession();
            Query query = session.getNamedQuery(IP_POOL_GET_NETWORK_POOL_BY_VDC);
            if (orderBy != null)
            {
                String newQuery = createOrderByQuery(query.getQueryString(), orderBy, asc);
                query = session.createQuery(newQuery);
            }
            query.setInteger("vdcId", vdcId);
            query.setString("filterLike", (filterLike.isEmpty()) ? "%" : "%" + filterLike + "%");

            query.setFirstResult(offset);
            if (numElem != null)
            {
                query.setMaxResults(numElem);
            }

            listOfPools = query.list();
        }
        catch (HibernateException e)
        {
            throw new PersistenceException(e.getMessage(), e);
        }

        return listOfPools;
    }

    @SuppressWarnings("unchecked")
    @Override
    public List<IpPoolManagementHB> getNetworkPoolByVLAN(Integer vlanId, Integer offset,
        Integer numElem, String filterLike, String orderBy, Boolean asc)
        throws PersistenceException
    {
        List<IpPoolManagementHB> listOfPools;

        try
        {
            Session session = HibernateDAOFactory.getSessionFactory().getCurrentSession();
            Query query = session.getNamedQuery(IP_POOL_GET_NETWORK_POOL_BY_VLAN);
            if (orderBy != null)
            {
                String newQuery = createOrderByQuery(query.getQueryString(), orderBy, asc);
                query = session.createQuery(newQuery);
            }
            query.setInteger("vlanId", vlanId);
            query.setString("filterLike", (filterLike.isEmpty()) ? "%" : "%" + filterLike + "%");

            query.setFirstResult(offset);
            if (numElem != null)
            {
                query.setMaxResults(numElem);
            }

            listOfPools = query.list();
        }
        catch (HibernateException e)
        {
            throw new PersistenceException(e.getMessage(), e);
        }

        return listOfPools;
    }

    @Override
    public Integer getNumberEnterprisesWithNetworkPoolByDatacenter(Integer datacenterId,
        String filterLike) throws PersistenceException
    {
        try
        {
            Session session = HibernateDAOFactory.getSessionFactory().getCurrentSession();
            Query query = session.getNamedQuery(IP_POOL_GET_ENTERPRISES_WITH_NETWORK_BY_DATACENTER);
            query.setInteger("datacenterId", datacenterId);
            query.setString("filterLike", (filterLike.isEmpty()) ? "%" : "%" + filterLike + "%");

            return query.list().size();
        }
        catch (HibernateException e)
        {
            throw new PersistenceException(e.getMessage(), e);
        }
    }

    @Override
    public Integer getNumberNetworkPoolAvailableByVLAN(Integer vlanId, String filterLike)
        throws PersistenceException
    {
        try
        {
            Session session = HibernateDAOFactory.getSessionFactory().getCurrentSession();
            Query query = session.getNamedQuery(IP_POOL_GET_NETWORK_POOL_AVAILABLE_BY_VLAN);
            query.setInteger("vlanId", vlanId);
            query.setString("filterLike", (filterLike.isEmpty()) ? "%" : "%" + filterLike + "%");

            return query.list().size();
        }
        catch (HibernateException e)
        {
            throw new PersistenceException(e.getMessage(), e);
        }
    }

    @Override
    public Integer getNumberNetworkPoolByEnterprise(Integer enterpriseId, String filterLike)
        throws PersistenceException
    {
        try
        {
            Session session = HibernateDAOFactory.getSessionFactory().getCurrentSession();
            Query query = session.getNamedQuery(IP_POOL_GET_NETWORK_POOL_BY_ENTERPRISE);
            query.setInteger("enterpriseId", enterpriseId);
            query.setString("filterLike", (filterLike.isEmpty()) ? "%" : "%" + filterLike + "%");

            return query.list().size();
        }
        catch (HibernateException e)
        {
            throw new PersistenceException(e.getMessage(), e);
        }
    }

    @Override
    public Integer getNumberNetworkPoolByVDC(Integer vdcId, String filterLike)
        throws PersistenceException
    {
        try
        {
            Session session = HibernateDAOFactory.getSessionFactory().getCurrentSession();
            Query query = session.getNamedQuery(IP_POOL_GET_NETWORK_POOL_BY_VDC);
            query.setInteger("vdcId", vdcId);
            query.setString("filterLike", (filterLike.isEmpty()) ? "%" : "%" + filterLike + "%");

            return query.list().size();
        }
        catch (HibernateException e)
        {
            throw new PersistenceException(e.getMessage(), e);
        }
    }

    @Override
    public Integer getNumberNetworkPoolByVLAN(Integer vlanId, String filterLike)
        throws PersistenceException
    {
        try
        {
            Session session = HibernateDAOFactory.getSessionFactory().getCurrentSession();
            Query query = session.getNamedQuery(IP_POOL_GET_NETWORK_POOL_BY_VLAN);
            query.setInteger("vlanId", vlanId);
            query.setString("filterLike", (filterLike.isEmpty()) ? "%" : "%" + filterLike + "%");

            return query.list().size();
        }
        catch (HibernateException e)
        {
            throw new PersistenceException(e.getMessage(), e);
        }
    }

    @SuppressWarnings("unchecked")
    @Override
    public List<IpPoolManagementHB> getPrivateNICsByVirtualMachine(Integer virtualMachineId)
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

    @Override
    public VlanNetworkHB getVlanByIpPoolManagement(Integer idManagement)
        throws PersistenceException
    {
        try
        {
            Session session = HibernateDAOFactory.getSessionFactory().getCurrentSession();
            Query query = session.getNamedQuery(IP_POOL_GET_VLAN_BY_IP_POOL_MANAGEMENT);
            query.setInteger("id_pool", idManagement);

            return (VlanNetworkHB) query.uniqueResult();
        }
        catch (HibernateException e)
        {
            throw new PersistenceException(e.getMessage(), e);
        }
    }

    protected String createOrderByQuery(String query, String orderBy, Boolean asc)
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
            queryString .append(" cast(substring(ip.ip, 1, locate('.', ip.ip) - 1) as integer), cast(substring(ip.ip, locate('.', ip.ip) + 1, locate('.', ip.ip, locate('.', ip.ip) + 1) - locate('.', ip.ip) - 1) as integer), cast(substring(ip.ip, locate('.', ip.ip, locate('.', ip.ip) + 1) + 1, locate('.', ip.ip, locate('.', ip.ip, locate('.', ip.ip) + 1) + 1) - locate('.', ip.ip, locate('.', ip.ip) +  1) - 1) as integer), cast(substring(ip.ip, locate('.', ip.ip, locate('.', ip.ip, locate('.', ip.ip) + 1) + 1) + 1, 3) as integer) asc");

        }

        return queryString.toString();
    }

}
