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

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

import org.hibernate.HibernateException;
import org.hibernate.NonUniqueResultException;
import org.hibernate.Query;
import org.hibernate.Session;

import com.abiquo.abiserver.business.hibernate.pojohb.infrastructure.DatacenterHB;
import com.abiquo.abiserver.business.hibernate.pojohb.infrastructure.RackHB;
import com.abiquo.abiserver.exception.PersistenceException;
import com.abiquo.abiserver.persistence.dao.infrastructure.DataCenterDAO;
import com.abiquo.abiserver.persistence.hibernate.HibernateDAO;
import com.abiquo.abiserver.persistence.hibernate.HibernateDAOFactory;

/**
 * * Class that implements the extra DAO functions for the
 * {@link com.abiquo.abiserver.persistence.dao.infrastructure.DataCenterDAO} interface
 * 
 * @author jdevesa@abiquo.com
 */
public class DataCenterDAOHibernate extends HibernateDAO<DatacenterHB, Integer> implements
    DataCenterDAO
{

    private static final String DATACENTER_GET_NUMBER_OF_VIRTUAL_DATACENTERS =
        "DATACENTER.GET_NUMBER_OF_VIRTUAL_DATACENTERS";

    private static final String DATACENTER_GET_BY_PRIVATE_NETWORK_ID =
        "DATACENTER.GET_BY_PRIVATE_NETWORK_ID";

    private static final String DATACENTER_GET_BY_PUBLIC_NETWORK_ID =
        "DATACENTER.GET_BY_PUBLIC_NETWORK_ID";

    private final static String ALL_IDS = "GET_ALL_DATACENTER_IDS";

    private static final String GET_DATACENTER_BY_NAME = "DATACENTER.GET_BY_NAME";

    private final static String GET_ALLOWED_DATACENTERS = "GET_ALLOWED_DATACENTERS";

    private final static String GET_RACKS_BY_DATACENTER = "DATACENTER.GET_RACKS_BY_DATACENTER";

    private static final String SUM_STORAGE_RESOURCES =
        "select sum(r.limitResource) from volume_management vm, storage_pool sp, remote_service rs, rasd_management rm, virtualdatacenter vdc, rasd r "
            + " where vm.idStorage = sp.idStorage and sp.idRemoteService = rs.idRemoteService and vm.idManagement = rm.idManagement and rm.idVirtualDataCenter = vdc.idVirtualDataCenter and rm.idResource= r.instanceID "
            + " and rs.idDatacenter = :datacenterId and vdc.idEnterprise=:enterpriseId";

    private static final String COUNT_IP_RESOURCES =
        "select count(*) from ip_pool_management ipm, network_configuration nc, vlan_network vn, datacenter dc, rasd_management rm, virtualdatacenter vdc "
            + " where ipm.dhcp_service_id=nc.dhcp_service_id and vn.network_configuration_id = nc.network_configuration_id and vn.network_id = dc.network_id and rm.idManagement = ipm.idManagement "
            + " and ipm.mac is not null "
            + " and rm.idVirtualDataCenter = vdc.idVirtualDataCenter "
            + " and dc.idDataCenter = :datacenterId and vdc.idEnterprise = :enterpriseId";

    //
    // private static final String COUNT_VLAN_RESOURCES =
    // "select count(*) from vlan_network vn, datacenter dc, virtualdatacenter vdc "
    // + " where vn.network_id= dc.network_id and vdc.networktypeID = vn.network_id "
    // + " and dc.idDataCenter = :datacenterId  and vdc.idEnterprise = :enterpriseId";

    @Override
    public Long getNumberVirtualDatacentersByDatacenter(final Integer idDatacenter)
    {

        Long numberOfVirtualDatacenters;

        final Session session = HibernateDAOFactory.getSessionFactory().getCurrentSession();
        final Query pmQuery = session.getNamedQuery(DATACENTER_GET_NUMBER_OF_VIRTUAL_DATACENTERS);
        pmQuery.setInteger("idDatacenter", idDatacenter);
        numberOfVirtualDatacenters = (Long) pmQuery.uniqueResult();

        return numberOfVirtualDatacenters;
    }

    @Override
    public void updateUsedResourcesByDatacenter(final Integer idDatacenter)
    {
        final Session session = HibernateDAOFactory.getSessionFactory().getCurrentSession();
        final String update =
            "update physicalmachine p, "
                + "(SELECT hy.idPhysicalMachine, IFNULL(SUM(vm.ram),0) ram, IFNULL(SUM(vm.cpu),0) cpu, IFNULL(SUM(vm.hd),0) hd "
                + "FROM virtualmachine vm right join hypervisor hy on vm.idHypervisor = hy.id, "
                + "physicalmachine pm, rack r "
                + "WHERE (vm.state is null or vm.state != 'NOT_DEPLOYED') AND pm.idPhysicalMachine = hy.idPhysicalMachine "
                + "AND pm.idRack = r.idRack AND r.idDatacenter = :idDatacenter "
                + "group by hy.idPhysicalMachine) x "
                + "set p.ramused = x.ram, p.cpuused = x.cpu, p.hdused = x.hd where p.idPhysicalMachine = x.idPhysicalMachine ";
        final Query pmQuery = session.createSQLQuery(update);
        pmQuery.setInteger("idDatacenter", idDatacenter);
        pmQuery.executeUpdate();
    }

    @Override
    public DatacenterHB getDatacenterWhereThePrivateNetworkStays(final Integer networkId)
        throws PersistenceException
    {
        DatacenterHB datacenter;

        try
        {
            final Session session = HibernateDAOFactory.getSessionFactory().getCurrentSession();
            final Query query = session.getNamedQuery(DATACENTER_GET_BY_PRIVATE_NETWORK_ID);
            query.setInteger("networkId", networkId);
            datacenter = (DatacenterHB) query.uniqueResult();
        }
        catch (final HibernateException he)
        {
            throw new PersistenceException(he.getMessage(), he);
        }

        return datacenter;
    }

    @Override
    public DatacenterHB getDatacenterWhereThePublicNetworkStays(final Integer idNetwork)
        throws PersistenceException
    {
        DatacenterHB datacenter;

        try
        {
            final Session session = HibernateDAOFactory.getSessionFactory().getCurrentSession();
            final Query query = session.getNamedQuery(DATACENTER_GET_BY_PUBLIC_NETWORK_ID);
            query.setInteger("networkId", idNetwork);
            datacenter = (DatacenterHB) query.uniqueResult();
        }
        catch (final HibernateException he)
        {
            throw new PersistenceException(he.getMessage(), he);
        }

        return datacenter;
    }

    @Override
    public List<Integer> findAllIds()
    {
        return HibernateDAOFactory.getSessionFactory().getCurrentSession().getNamedQuery(ALL_IDS)
            .list();
    }

    @Override
    public DatacenterHB findByName(final String name) throws PersistenceException
    {
        DatacenterHB datacenter;

        try
        {
            final Session session = HibernateDAOFactory.getSessionFactory().getCurrentSession();
            final Query query = session.getNamedQuery(GET_DATACENTER_BY_NAME);
            query.setString("name", name);
            datacenter = (DatacenterHB) query.uniqueResult();
        }
        catch (final NonUniqueResultException e)
        {
            // No datacenter found
            return null;
        }
        catch (final HibernateException he)
        {
            throw new PersistenceException(he.getMessage(), he);
        }

        return datacenter;
    }

    @Override
    @SuppressWarnings("unchecked")
    public List<DatacenterHB> getAllowedDatacenters(final int idEnterprise)

    {
        final Session session = HibernateDAOFactory.getSessionFactory().getCurrentSession();
        final Query query = session.getNamedQuery(GET_ALLOWED_DATACENTERS);
        query.setInteger("idEnterprise", idEnterprise);

        return query.list();
    }

    @SuppressWarnings("unchecked")
    @Override
    public ArrayList<RackHB> getRacks(Integer datacenterId, String filters)
    {
        final Session session = HibernateDAOFactory.getSessionFactory().getCurrentSession();
        final Query query = session.getNamedQuery(GET_RACKS_BY_DATACENTER);
        query.setInteger("idDatacenter", datacenterId);
        query.setString("filterLike", (filters == null || filters.isEmpty()) ? "%" : "%" + filters
            + "%");

        return (ArrayList<RackHB>) query.list();
    }

    private final static Long MB_TO_BYTES = 1024l * 1024l;

    @Override
    public long getCurrentStorageAllocated(int idEnterprise, int idDatacenter)
    {
        Session session = HibernateDAOFactory.getSessionFactory().getCurrentSession();

        BigDecimal storage =
            (BigDecimal) session.createSQLQuery(SUM_STORAGE_RESOURCES)
                .setParameter("datacenterId", idDatacenter)
                .setParameter("enterpriseId", idEnterprise).uniqueResult();

        return storage == null ? 0 : storage.longValue() * MB_TO_BYTES;
    }

    @Override
    public long getCurrentPublicIpAllocated(int idEnterprise, int idDatacenter)
    {
        Session session = HibernateDAOFactory.getSessionFactory().getCurrentSession();

        BigInteger publicIps =
            (BigInteger) session.createSQLQuery(COUNT_IP_RESOURCES)
                .setParameter("datacenterId", idDatacenter)
                .setParameter("enterpriseId", idEnterprise).uniqueResult();

        return publicIps == null ? 0 : publicIps.longValue();
    }

}
