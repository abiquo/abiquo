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

import java.util.Collection;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;

import org.apache.commons.lang.StringUtils;
import org.hibernate.Criteria;
import org.hibernate.Query;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.ProjectionList;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.springframework.stereotype.Repository;

import com.abiquo.server.core.cloud.VirtualAppliance;
import com.abiquo.server.core.common.persistence.DefaultDAOBase;
import com.abiquo.server.core.util.PagedList;

@Repository("jpaIpPoolManagementDAO")
public class IpPoolManagementDAO extends DefaultDAOBase<Integer, IpPoolManagement>
{

    private final static String GET_NETWORK_POOL_PURCHASED_BY_ENTERPRISE = "SELECT ip "//
        + "FROM com.abiquo.server.core.infrastructure.Datacenter dc "//
        + "INNER JOIN dc.network net, "//
        + "com.abiquo.server.core.infrastructure.network.VLANNetwork vlan "//
        + "INNER JOIN vlan.configuration.dhcp dhcp, "//
        + "com.abiquo.server.core.infrastructure.network.IpPoolManagement ip "//
        // + "LEFT JOIN join ip.virtualMachine vm "//
        + "LEFT JOIN ip.virtualAppliance vapp, "//
        + "com.abiquo.server.core.cloud.VirtualDatacenter vdc "//
        + "where net.id = vlan.network.id "//
        + "and dhcp.id = ip.dhcp.id "//
        + "and dc.id = vdc.datacenter.id "//
        + "and vdc.enterprise.id = :enterpriseId "//
        + "and ip.virtualDatacenter.id = vdc.id ";

    private final static String GET_IPPOOLMANAGEMENT_ASSIGNED_TO_DIFFERENT_VM_AND_DIFFERENT_FROM_NOT_DEPLOYED_SQL =
        "SELECT * " //
            + "FROM ip_pool_management ip, " // 
            + "rasd_management rasd " //
            + "JOIN virtualmachine vm " //
            + "ON vm.idVM = rasd.idVM "
            + "WHERE rasd.idManagement = ip.idManagement " //
            + "AND rasd.idVM != :idVM " //
            + "AND ip.vlan_network_id = :idVlanNetwork " //
            + "AND vm.state != 'NOT_DEPLOYED'"; //

    private static Criterion equalMac(String mac)
    {
        assert !StringUtils.isEmpty(mac);

        return Restrictions.eq(IpPoolManagement.MAC_PROPERTY, mac);
    }

    public IpPoolManagementDAO()
    {
        super(IpPoolManagement.class);
    }

    public IpPoolManagementDAO(EntityManager entityManager)
    {
        super(IpPoolManagement.class, entityManager);
    }

    public boolean existsAnyWithMac(String mac)
    {
        assert !StringUtils.isEmpty(mac);

        return this.existsAnyByCriterions(equalMac(mac));
    }

    public List<IpPoolManagement> findByVirtualMachine(final Integer virtualMachineId)
    {
        Criteria criteria = getSession().createCriteria(IpPoolManagement.class);

        Criterion onVM = Restrictions.eq(IpPoolManagement.ID_VM_PROPERTY, virtualMachineId);

        criteria.add(onVM);

        List<IpPoolManagement> result = getResultList(criteria);

        return result;

    }

    /**
     * Find all the IpPoolManagement created by a vLAN
     * 
     * @param vlanId
     * @return
     */
    public List<IpPoolManagement> findByVLAN(final Integer vlanId, Integer page,
        final Integer numElem)
    {
        int totalResults = 0;
        if (numElem != -1)
        {
            TypedQuery<IpPoolManagement> queryCount =
                getEntityManager().createNamedQuery("IP_POOL_MANAGEMENT.BY_VLAN",
                    IpPoolManagement.class);
            queryCount.setParameter("vlan_id", vlanId);

            // Check if the page requested is bigger than the last one
            totalResults = queryCount.getResultList().size();
            if ((totalResults / numElem) < page)
            {
                page = totalResults / numElem;
            }
        }

        TypedQuery<IpPoolManagement> query =
            getEntityManager().createNamedQuery("IP_POOL_MANAGEMENT.BY_VLAN",
                IpPoolManagement.class);
        query.setParameter("vlan_id", vlanId);

        if (numElem != -1)
        {
            query.setFirstResult(page * numElem);
            query.setMaxResults(numElem);
        }

        List<IpPoolManagement> result = query.getResultList();
        if (totalResults == 0)
        {
            totalResults = result.size();
        }

        PagedList<IpPoolManagement> ipList = new PagedList<IpPoolManagement>(result);
        ipList.setTotalResults(totalResults);
        ipList.setPageSize(numElem);
        ipList.setCurrentPage(page);

        return ipList;
    }

    public List<IpPoolManagement> findByVdc(final Integer vdcId, Integer page, final Integer numElem)
    {
        // Get the query that counts the total results.
        TypedQuery<IpPoolManagement> queryCount =
            getEntityManager()
                .createNamedQuery("IP_POOL_MANAGEMENT.BY_VDC", IpPoolManagement.class);
        queryCount.setParameter("vdc_id", vdcId);

        // Check if the page requested is bigger than the last one
        Integer totalResults = queryCount.getResultList().size();
        if ((totalResults / numElem) < page)
        {
            page = totalResults / numElem;
        }

        // Get the list of elements
        TypedQuery<IpPoolManagement> query =
            getEntityManager()
                .createNamedQuery("IP_POOL_MANAGEMENT.BY_VDC", IpPoolManagement.class);
        query.setParameter("vdc_id", vdcId);
        query.setFirstResult(page * numElem);
        query.setMaxResults(numElem);

        PagedList<IpPoolManagement> ipList = new PagedList<IpPoolManagement>(query.getResultList());
        ipList.setTotalResults(totalResults);
        ipList.setPageSize(numElem);
        ipList.setCurrentPage(page);

        return ipList;
    }

    public List<IpPoolManagement> findByEnterprise(Integer entId, Integer page,
        final Integer numElem)
    {
        // Get the query that counts the total results.
        TypedQuery<IpPoolManagement> queryCount =
            getEntityManager()
                .createNamedQuery("IP_POOL_MANAGEMENT.BY_ENT", IpPoolManagement.class);
        queryCount.setParameter("ent_id", entId);

        // Check if the page requested is bigger than the last one
        Integer totalResults = queryCount.getResultList().size();
        if ((totalResults / numElem) < page)
        {
            page = totalResults / numElem;
        }

        // Get the list of elements
        TypedQuery<IpPoolManagement> query =
            getEntityManager()
                .createNamedQuery("IP_POOL_MANAGEMENT.BY_ENT", IpPoolManagement.class);
        query.setParameter("ent_id", entId);
        query.setFirstResult(page * numElem);
        query.setMaxResults(numElem);

        PagedList<IpPoolManagement> ipList = new PagedList<IpPoolManagement>(query.getResultList());
        ipList.setTotalResults(totalResults);
        ipList.setPageSize(numElem);
        ipList.setCurrentPage(page);

        return ipList;
    }

    @SuppressWarnings("unchecked")
    public Collection<String> getAllMacs()
    {
        Criteria criteria = getSession().createCriteria(IpPoolManagement.class);
        ProjectionList projList = Projections.projectionList();
        projList.add(Projections.property(IpPoolManagement.MAC_PROPERTY));

        criteria.setProjection(projList);
        return criteria.list();
    }

    public List<IpPoolManagement> getNetworkPoolPurchasedByEnterprise(final Integer enterpriseId)
    {
        Query query = getSession().createQuery(GET_NETWORK_POOL_PURCHASED_BY_ENTERPRISE);
        query.setParameter("enterpriseId", enterpriseId);

        return query.list();
    }

    public Boolean isVlanAssignedToDifferentVM(final Integer virtualMachineId,
        final VLANNetwork vlanNetwork)
    {
        List<IpPoolManagement> ippoolList;
        Query query =
            getSession().createSQLQuery(
                GET_IPPOOLMANAGEMENT_ASSIGNED_TO_DIFFERENT_VM_AND_DIFFERENT_FROM_NOT_DEPLOYED_SQL);
        query.setParameter("idVlanNetwork", vlanNetwork.getId());
        query.setParameter("idVM", virtualMachineId);
        ippoolList = query.list();

        if (ippoolList.isEmpty())
        {
            return false;
        }
        return true;
    }

    public List<IpPoolManagement> findByVirtualAppliance(VirtualAppliance vapp)
    {
        Criterion onVapp = Restrictions.eq(IpPoolManagement.VIRTUAL_APPLIANCE_PROPERTY, vapp);
        Criteria criteria = getSession().createCriteria(IpPoolManagement.class).add(onVapp);
        List<IpPoolManagement> result = getResultList(criteria);

        return result;
    }

}
