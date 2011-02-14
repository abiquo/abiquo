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

package com.abiquo.abiserver.persistence.dao.virtualappliance.hibernate;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Collection;

import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.Session;

import com.abiquo.abiserver.business.hibernate.pojohb.virtualappliance.VirtualDataCenterHB;
import com.abiquo.abiserver.exception.PersistenceException;
import com.abiquo.abiserver.persistence.DAOFactory;
import com.abiquo.abiserver.persistence.dao.virtualappliance.VirtualDataCenterDAO;
import com.abiquo.abiserver.persistence.hibernate.HibernateDAO;
import com.abiquo.abiserver.persistence.hibernate.HibernateDAOFactory;

/**
 * Class that implements the extra DAO functions for the
 * {@link com.abiquo.abiserver.persistence.dao.virtualappliance.VirtualDataCenterDAO} interface
 * 
 * @author jdevesa@abiquo.com
 */
@SuppressWarnings("unchecked")
public class VirtualDataCenterDAOHibernate extends HibernateDAO<VirtualDataCenterHB, Integer>
    implements VirtualDataCenterDAO
{

    private static final String VIRTUAL_DATACENTER_GET_BY_NETWORK =
        "VIRTUAL_DATACENTER.GET_BY_NETWORK";

    private static final String VIRTUAL_DATACENTER_GET_BY_VAPP = "VIRTUAL_DATACENTER_GET_BY_VAPP";

    private static final String VIRTUAL_DATACENTERS_BY_ENTERPRISE =
        "VIRTUAL_DATACENTER_BY_ENTERPRISE";

    private static final String VIRTUAL_DATACENTERS_BY_ENTERPRISE_AND_DATACENTER =
        "VIRTUAL_DATACENTER_BY_ENTERPRISE_AND_DATACENTER";

    private static final String SUM_VOLUMES_RESOURCES =
        "select sum(r.limitResource) from rasd r, rasd_management rm where r.instanceID = rm.idResource and rm.idResourceType = '8' and rm.idVirtualDatacenter = :virtualDatacenterId";

    private static final String COUNT_PUBLIC_IP_RESOURCES =
        "select count(*) from ip_pool_management ipm, network_configuration nc, vlan_network vn, datacenter dc, rasd_management rm "
            + " where ipm.dhcp_service_id=nc.dhcp_service_id and vn.network_configuration_id = nc.network_configuration_id and vn.network_id = dc.network_id and rm.idManagement = ipm.idManagement "
            + " and ipm.mac is not null " //
            + " and rm.idVirtualDataCenter = :virtualDatacenterId";

    @Override
    public VirtualDataCenterHB findByIdNamed(Integer id)
    {
        return (VirtualDataCenterHB) getSession().get("VirtualDataCenterHB", id);
    }

    @Override
    public VirtualDataCenterHB getVirtualDatacenterFromNetworkId(Integer idNetwork)
        throws PersistenceException
    {
        VirtualDataCenterHB vdcHB;

        try
        {
            Session session = HibernateDAOFactory.getSessionFactory().getCurrentSession();
            Query query = session.getNamedQuery(VIRTUAL_DATACENTER_GET_BY_NETWORK);
            query.setInteger("network_id", idNetwork);
            vdcHB = (VirtualDataCenterHB) query.uniqueResult();
        }
        catch (HibernateException he)
        {
            throw new PersistenceException(he.getMessage(), he);
        }

        return vdcHB;
    }

    @Override
    public VirtualDataCenterHB getVirtualDatacenterFromVirtualAppliance(Integer vappId)
        throws PersistenceException
    {
        try
        {
            Session session = HibernateDAOFactory.getSessionFactory().getCurrentSession();
            Query query = session.getNamedQuery(VIRTUAL_DATACENTER_GET_BY_VAPP);
            query.setInteger("vappId", vappId);
            return (VirtualDataCenterHB) query.uniqueResult();
        }
        catch (HibernateException he)
        {
            throw new PersistenceException(he.getMessage(), he);
        }

    }

    @Override
    public Collection<VirtualDataCenterHB> getVirtualDatacentersFromEnterprise(Integer enterpriseId)
    {
        Session session = HibernateDAOFactory.getSessionFactory().getCurrentSession();
        return session.getNamedQuery(VIRTUAL_DATACENTERS_BY_ENTERPRISE)
            .setParameter("enterpriseId", enterpriseId).list();
    }

    @Override
    public Collection<VirtualDataCenterHB> getVirtualDatacentersFromEnterpriseAndDatacenter(
        Integer enterpriseId, Integer datacenterId)
    {
        Session session = HibernateDAOFactory.getSessionFactory().getCurrentSession();
        return session.getNamedQuery(VIRTUAL_DATACENTERS_BY_ENTERPRISE_AND_DATACENTER)
            .setParameter("enterpriseId", enterpriseId).setParameter("datacenterId", datacenterId)
            .list();
    }

    private final static Long MB_TO_BYTES = 1024l * 1024l;

    public long getCurrentStorageAllocated(int virtualDatacenterId)
    {
        Session session = HibernateDAOFactory.getSessionFactory().getCurrentSession();

        BigDecimal storage =
            (BigDecimal) session.createSQLQuery(SUM_VOLUMES_RESOURCES)
                .setParameter("virtualDatacenterId", virtualDatacenterId).uniqueResult();

        return storage == null ? 0 : storage.longValue() * MB_TO_BYTES;
    }

    public long getCurrentPublicIpAllocated(int virtualDatacenterId)
    {
        Session session = HibernateDAOFactory.getSessionFactory().getCurrentSession();

        BigInteger publicIps =
            (BigInteger) session.createSQLQuery(COUNT_PUBLIC_IP_RESOURCES)
                .setParameter("virtualDatacenterId", virtualDatacenterId).uniqueResult();

        return publicIps == null ? 0 : publicIps.longValue();

    }

    public long getCurrentVlanAllocated(int virtualDatacenterId)
    {
        final DAOFactory daoF = HibernateDAOFactory.instance();

        Integer vlanCount =
            daoF.getVlanNetworkDAO().findByVirtualDatacenter(virtualDatacenterId).size();

        return vlanCount == null ? 0 : vlanCount;
    }
}
