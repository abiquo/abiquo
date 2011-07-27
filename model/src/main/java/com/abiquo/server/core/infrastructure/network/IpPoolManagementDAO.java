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
import com.abiquo.server.core.cloud.VirtualMachine;
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
            + "ON vm.idVM = rasd.idVM " + "WHERE rasd.idManagement = ip.idManagement " //
            + "AND rasd.idVM != :idVM " //
            + "AND ip.vlan_network_id = :idVlanNetwork " //
            + "AND vm.state != 'NOT_DEPLOYED'"; //

    public static final String BY_ENT = " SELECT ip FROM IpPoolManagement ip "
        + " left join ip.virtualMachine vm " + " left join ip.virtualAppliance vapp, "
        + " NetworkConfiguration nc, " + " VirtualDatacenter vdc, " + " VLANNetwork vn, "
        + " Enterprise ent " + " WHERE ip.dhcp.id = nc.dhcp.id "
        + " AND nc.id = vn.configuration.id " + " AND vn.network.id = vdc.network.id"
        + " AND vdc.enterprise.id = ent.id" + " AND ent.id = :ent_id " + " AND "
        + "( ip.ip like :filterLike " + " OR ip.mac like :filterLike "
        + " OR ip.vlanNetwork.name like :filterLike " + " OR vapp.name like :filterLike "
        + " OR vm.name like :filterLike " + ")";

    public static final String BY_VDC = " SELECT ip FROM IpPoolManagement ip "
        + " left join ip.virtualMachine vm " + " left join ip.virtualAppliance vapp, "
        + " NetworkConfiguration nc, " + " VirtualDatacenter vdc, " + " VLANNetwork vn "
        + " WHERE ip.dhcp.id = nc.dhcp.id " + " AND nc.id = vn.configuration.id "
        + " AND vn.network.id = vdc.network.id" + " AND vdc.id = :vdc_id AND"
        + "( ip.ip like :filterLike " + " OR ip.mac like :filterLike "
        + " OR ip.vlanNetwork.name like :filterLike " + " OR vapp.name like :filterLike "
        + " OR vm.name like :filterLike " + ")";

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

    public List<IpPoolManagement> findByVirtualMachine(final VirtualMachine virtualMachine)
    {
        Criteria criteria = getSession().createCriteria(IpPoolManagement.class);

        Criterion onVM = Restrictions.eq(IpPoolManagement.VIRTUAL_MACHINE_PROPERTY, virtualMachine);

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
    public List<IpPoolManagement> findByVLAN(final Integer vlanId, Integer firstElement,
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
        }

        TypedQuery<IpPoolManagement> query =
            getEntityManager().createNamedQuery("IP_POOL_MANAGEMENT.BY_VLAN",
                IpPoolManagement.class);
        query.setParameter("vlan_id", vlanId);
        if (firstElement >= totalResults)
            firstElement = totalResults - numElem;
        if (numElem != -1)
        {

            query.setFirstResult(firstElement);
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
        ipList.setCurrentElement(firstElement);

        return ipList;
    }

    public List<IpPoolManagement> findByVdc(final Integer vdcId, Integer firstElem,
        final Integer numElem, final String has, final IpPoolManagement.OrderByEnum orderby,
        final Boolean asc)
    {
        // Get the query that counts the total results.
        Query finalQuery = getSession().createQuery(BY_VDC + " " + defineOrderBy(orderby, asc));
        finalQuery.setParameter("vdc_id", vdcId);
        finalQuery.setParameter("filterLike", (has.isEmpty()) ? "%" : "%" + has + "%");

        // Check if the page requested is bigger than the last one
        Integer totalResults = finalQuery.list().size();

        if (firstElem >= totalResults)
            firstElem = totalResults - numElem;
        finalQuery.setFirstResult(firstElem);
        finalQuery.setMaxResults(numElem);

        PagedList<IpPoolManagement> ipList = new PagedList<IpPoolManagement>(finalQuery.list());
        ipList.setTotalResults(totalResults);
        ipList.setPageSize(numElem);
        ipList.setCurrentElement(firstElem);

        return ipList;
    }

    public List<IpPoolManagement> findByEnterprise(Integer entId, Integer firstElem,
        final Integer numElem, final String has, final IpPoolManagement.OrderByEnum orderby,
        final Boolean asc)
    {
        // Get the query that counts the total results.
        Query finalQuery = getSession().createQuery(BY_ENT + " " + defineOrderBy(orderby, asc));
        finalQuery.setParameter("ent_id", entId);
        finalQuery.setParameter("filterLike", (has.isEmpty()) ? "%" : "%" + has + "%");

        // Check if the page requested is bigger than the last one
        Integer totalResults = finalQuery.list().size();
        if (firstElem >= totalResults)
            firstElem = totalResults - numElem;

        // Get the list of elements
        finalQuery.setFirstResult(firstElem);
        finalQuery.setMaxResults(numElem);

        PagedList<IpPoolManagement> ipList = new PagedList<IpPoolManagement>(finalQuery.list());
        ipList.setTotalResults(totalResults);
        ipList.setPageSize(numElem);
        ipList.setCurrentElement(firstElem);

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

    private String defineOrderBy(IpPoolManagement.OrderByEnum orderBy, final Boolean asc)
    {

        StringBuilder queryString = new StringBuilder();

        queryString.append(" order by ");
        switch (orderBy)
        {
            case IP:
            {
                queryString
                    .append(" cast(substring(ip.ip, 1, locate('.', ip.ip) - 1) as integer), cast(substring(ip.ip, locate('.', ip.ip) + 1, locate('.', ip.ip, locate('.', ip.ip) + 1) - locate('.', ip.ip) - 1) as integer), cast(substring(ip.ip, locate('.', ip.ip, locate('.', ip.ip) + 1) + 1, locate('.', ip.ip, locate('.', ip.ip, locate('.', ip.ip) + 1) + 1) - locate('.', ip.ip, locate('.', ip.ip) +  1) - 1) as integer), cast(substring(ip.ip, locate('.', ip.ip, locate('.', ip.ip, locate('.', ip.ip) + 1) + 1) + 1, 3) as integer) ");
                break;

            }
            case QUARANTINE:
            {
                queryString.append("ip.quarantine ");
                break;
            }
            case MAC:
            {
                queryString.append("ip.mac ");
                break;
            }
            case VLAN:
            {
                queryString.append("ip.vlanNetwork.name ");
                break;
            }
            case VIRTUALDATACENTER:
            {
                queryString.append("ip.virtualDatacenter.name ");
                break;
            }
            case VIRTUALMACHINE:
            {
                queryString.append("vm.name ");
                break;
            }
            case VIRTUALAPPLIANCE:
            {
                queryString.append("vapp.name ");
                break;
            }
            case LEASE:
            {
                queryString.append("ip.name ");
            }
        }

        if (asc)
        {
            queryString.append("asc");
        }
        else
        {
            queryString.append("desc");
        }

        return queryString.toString();
    }

}
