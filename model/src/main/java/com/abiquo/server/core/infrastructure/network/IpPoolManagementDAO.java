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

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.persistence.EntityManager;

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
import com.abiquo.server.core.infrastructure.management.RasdManagement;
import com.abiquo.server.core.infrastructure.network.IpPoolManagement.OrderByEnum;
import com.abiquo.server.core.util.PagedList;
import com.softwarementors.bzngine.entities.PersistentEntity;

@Repository("jpaIpPoolManagementDAO")
@SuppressWarnings("unchecked")
public class IpPoolManagementDAO extends DefaultDAOBase<Integer, IpPoolManagement>
{

    // public static final String BY_DATACENTER = " SELECT ip FROM "
    // + "Datacenter dc INNER JOIN dc.network net, VLANNetwork vlan "
    // + "INNER JOIN vlan.configuration conf INNER JOIN conf.dhcp dhcp, "
    // + "IpPoolManagement ip LEFT JOIN ip.virtualMachine vm LEFT JOIN ip.virtualAppliance vapp "
    // + "LEFT JOIN ip.virtualDatacenter vdc JOIN vdc.enterprise ent "
    // + "WHERE net.id = vlan.network.id AND dhcp.id = ip.dhcp.id AND dc.id = :datacenter_id";

    public static final String BY_DATACENTER =
        " SELECT ip FROM "
            + "Datacenter dc INNER JOIN dc.network net, VLANNetwork vlan "
            + "INNER JOIN vlan.configuration conf INNER JOIN conf.dhcp dhcp, "
            + "IpPoolManagement ip LEFT JOIN ip.virtualMachine vm LEFT JOIN ip.virtualAppliance vapp "
            + "LEFT JOIN ip.virtualDatacenter vdc LEFT JOIN vlan.enterprise ent "
            + "WHERE net.id = vlan.network.id AND dhcp.id = ip.dhcp.id AND dc.id = :datacenter_id AND "
            + "( ip.ip LIKE :filterLike OR ip.mac LIKE :filterLike OR ip.networkName LIKE :filterLike OR "
            + " vm.name like :filterLike OR vapp.name LIKE :filterLike OR ent.name LIKE :filterLike )";

    public static final String BY_DEFAULT_VLAN_USED_BY_ANY_VDC =
        " SELECT ip FROM  virtualdatacenter vdc, ip_pool_management ip where "
            + "vdc.default_vlan_network_id=ip.vlan_network_id and vdc.default_vlan_network_id=:vlan_id";

    public static final String BY_ENT = " SELECT ip FROM IpPoolManagement ip "
        + " left join ip.virtualMachine vm " + " left join ip.virtualAppliance vapp, "
        + " NetworkConfiguration nc, " + " VirtualDatacenter vdc, " + " VLANNetwork vn, "
        + " Enterprise ent " + " WHERE ip.dhcp.id = nc.dhcp.id "
        + " AND nc.id = vn.configuration.id " + " AND vn.network.id = vdc.network.id"
        + " AND vdc.enterprise.id = ent.id" + " AND ent.id = :ent_id " + " AND "
        + "( ip.ip like :filterLike " + " OR ip.mac like :filterLike "
        + " OR ip.vlanNetwork.name like :filterLike " + " OR vapp.name like :filterLike "
        + " OR vm.name like :filterLike " + ")";

    public static final String BY_IP_PURCHASED = " SELECT ip FROM "
        + "Datacenter dc INNER JOIN dc.network net, VLANNetwork vlan "
        + "INNER JOIN vlan.configuration conf INNER JOIN conf.dhcp dhcp, "
        + "IpPoolManagement ip LEFT JOIN ip.virtualMachine vm LEFT JOIN ip.virtualAppliance vapp, "
        + "VirtualDatacenter vdc LEFT JOIN vdc.enterprise ent "
        + "WHERE net.id = vlan.network.id AND dhcp.id = ip.dhcp.id AND vdc.id = :vdc_id AND "
        + "vdc.datacenter.id = dc.id AND ip.id = :ip_id AND "
        + "ip.available = 1 AND vlan.enterprise is null AND ip.virtualDatacenter.id = :vdc_id";

    public static final String BY_IP_TO_PURCHASE = " SELECT ip FROM "
        + "Datacenter dc INNER JOIN dc.network net, VLANNetwork vlan "
        + "INNER JOIN vlan.configuration conf INNER JOIN conf.dhcp dhcp, "
        + "IpPoolManagement ip LEFT JOIN ip.virtualMachine vm LEFT JOIN ip.virtualAppliance vapp, "
        + "VirtualDatacenter vdc LEFT JOIN vdc.enterprise ent "
        + "WHERE net.id = vlan.network.id AND dhcp.id = ip.dhcp.id AND vdc.id = :vdc_id AND "
        + "vdc.datacenter.id = dc.id AND ip.id = :ip_id AND "
        + "ip.available = 1 AND vlan.enterprise is null AND ip.virtualDatacenter is null";

    public static final String BY_NETWORK = " SELECT ip FROM IpPoolManagement ip "
        + " left join ip.virtualMachine vm " + " left join ip.virtualAppliance vapp, "
        + " NetworkConfiguration nc, " + " VLANNetwork vn " + " WHERE ip.dhcp.id = nc.dhcp.id "
        + " AND nc.id = vn.configuration.id " + " AND vn.id = :vlan_id "
        + " AND vn.network.id = :net_id AND " + "( ip.ip like :filterLike "
        + " OR ip.mac like :filterLike " + " OR ip.vlanNetwork.name like :filterLike "
        + " OR vapp.name like :filterLike " + " OR vm.name like :filterLike " + ")";

    public static final String BY_PUBLIC_VLAN =
        " SELECT ip FROM "
            + "Datacenter dc INNER JOIN dc.network net, VLANNetwork vlan "
            + "INNER JOIN vlan.configuration conf INNER JOIN conf.dhcp dhcp, "
            + "IpPoolManagement ip LEFT JOIN ip.virtualDatacenter vdc LEFT JOIN ip.virtualAppliance vapp "
            + "LEFT JOIN ip.virtualMachine vm LEFT JOIN vdc.enterprise ent "
            + "WHERE net.id = vlan.network.id AND dhcp.id = ip.dhcp.id AND dc.id = :datacenter_id AND "
            + "vlan.id = :vlan_id AND"
            + "( ip.ip LIKE :filterLike OR ip.mac LIKE :filterLike OR ip.networkName LIKE :filterLike OR "
            + " vm.name like :filterLike OR vapp.name LIKE :filterLike OR ent.name LIKE :filterLike )";

    public static final String BY_VDC = " SELECT ip FROM IpPoolManagement ip "
        + " left join ip.virtualMachine vm " + " left join ip.virtualAppliance vapp, "
        + " NetworkConfiguration nc, " + " VirtualDatacenter vdc, " + " VLANNetwork vn "
        + " WHERE ip.dhcp.id = nc.dhcp.id " + " AND nc.id = vn.configuration.id "
        + " AND vn.network.id = vdc.network.id" + " AND vdc.id = :vdc_id AND"
        + "( ip.ip like :filterLike " + " OR ip.mac like :filterLike "
        + " OR ip.vlanNetwork.name like :filterLike " + " OR vapp.name like :filterLike "
        + " OR vm.name like :filterLike " + ")";

    public static final String BY_VDC_PURCHASED =
        " SELECT ip FROM "
            + "Datacenter dc INNER JOIN dc.network net, VLANNetwork vlan "
            + "INNER JOIN vlan.configuration conf INNER JOIN conf.dhcp dhcp, "
            + "IpPoolManagement ip LEFT JOIN ip.virtualMachine vm LEFT JOIN ip.virtualAppliance vapp, "
            + "VirtualDatacenter vdc LEFT JOIN vdc.enterprise ent "
            + "WHERE net.id = vlan.network.id AND dhcp.id = ip.dhcp.id AND vdc.id = :vdc_id AND "
            + "vdc.datacenter.id = dc.id AND "
            + "ip.available = 1 AND vlan.enterprise is null AND ip.virtualDatacenter.id = :vdc_id AND "
            + "( ip.ip LIKE :filterLike OR ip.mac LIKE :filterLike OR ip.networkName LIKE :filterLike OR "
            + " vm.name like :filterLike OR vapp.name LIKE :filterLike OR ent.name LIKE :filterLike )";

    public static final String BY_VDC_TO_PURCHASE =
        " SELECT ip FROM "
            + "Datacenter dc INNER JOIN dc.network net, VLANNetwork vlan "
            + "INNER JOIN vlan.configuration conf INNER JOIN conf.dhcp dhcp, "
            + "IpPoolManagement ip LEFT JOIN ip.virtualMachine vm LEFT JOIN ip.virtualAppliance vapp, "
            + "VirtualDatacenter vdc LEFT JOIN vdc.enterprise ent "
            + "WHERE net.id = vlan.network.id AND dhcp.id = ip.dhcp.id AND vdc.id = :vdc_id AND "
            + "vdc.datacenter.id = dc.id AND "
            + "ip.available = 1 AND ip.quarantine = 0  AND vlan.enterprise is null AND ip.virtualDatacenter is null AND "
            + "( ip.ip LIKE :filterLike OR ip.mac LIKE :filterLike OR ip.networkName LIKE :filterLike OR "
            + " vm.name like :filterLike OR vapp.name LIKE :filterLike OR ent.name LIKE :filterLike )";

    public static final String BY_VIRTUAL_MACHINE = "SELECT ip "
        + "FROM IpPoolManagement ip INNER JOIN ip.virtualMachine vm " + "WHERE vm.id = :vm_id "
        + "ORDER BY ip.rasd.configurationName";

    public static final String BY_VLAN = " SELECT ip FROM IpPoolManagement ip "
        + " left join ip.virtualMachine vm " + " left join ip.virtualAppliance vapp, "
        + " NetworkConfiguration nc, " + " VirtualDatacenter vdc, " + " VLANNetwork vn "
        + " WHERE ip.dhcp.id = nc.dhcp.id " + " AND nc.id = vn.configuration.id "
        + " AND vn.id = :vlan_id " + " AND vn.network.id = vdc.network.id"
        + " AND vdc.id = :vdc_id AND" + "( ip.ip like :filterLike "
        + " OR ip.mac like :filterLike " + " OR ip.vlanNetwork.name like :filterLike "
        + " OR vapp.name like :filterLike " + " OR vm.name like :filterLike " + ")";

    public static final String BY_EXTERNAL_VLAN = "SELECT ip FROM IpPoolManagement ip "
        + " left join ip.virtualMachine vm " + " left join ip.virtualAppliance vapp "
        + " left join ip.virtualDatacenter vdc, " + " NetworkConfiguration nc, "
        + " VLANNetwork vn " + " join vn.enterprise ent, " + " DatacenterLimits dcl"
        + " WHERE ip.dhcp.id = nc.dhcp.id " + " AND nc.id = vn.configuration.id "
        + " AND vn.id = :vlan_id " + " AND ent.id = :ent_id AND "
        + " dcl.enterprise.id = ent.id AND " + " ip.available = 1 AND "
        + " dcl.id = :dc_limit_id AND " + "( ip.ip like :filterLike "
        + " OR ip.mac like :filterLike " + " OR ip.vlanNetwork.name like :filterLike "
        + " OR vapp.name like :filterLike " + " OR vm.name like :filterLike " + ")";

    public static final String BY_VLAN_USED_BY_ANY_VDC =
        " SELECT ip FROM ip_pool_management ip  , rasd_management rasd, virtualdatacenter vdc "
            + "  WHERE ip.idManagement= rasd.idManagement and rasd.idVirtualDatacenter "
            + "= vdc.idVirtualDatacenter and ip.vlan_network_id =:vlan_id";

    public static final String BY_VLAN_USED_BY_ANY_VM = " SELECT ip FROM IpPoolManagement ip "
        + " left join ip.virtualMachine vm " + " left join ip.virtualAppliance vapp, "
        + " NetworkConfiguration nc, " + " VLANNetwork vn " + " WHERE ip.dhcp.id = nc.dhcp.id "
        + " AND nc.id = vn.configuration.id " + " AND vn.id = :vlan_id "
        + " AND ( ip.ip like :filterLike " + " OR ip.mac like :filterLike "
        + " OR ip.vlanNetwork.name like :filterLike " + " OR vapp.name like :filterLike "
        + " OR vm.name like :filterLike " + ")";

    public static final String BY_VLAN_WITHOUT_USED_IPS = " SELECT ip FROM IpPoolManagement ip "
        + " left join ip.virtualMachine vm " + " left join ip.virtualAppliance vapp, "
        + " NetworkConfiguration nc, " + " VirtualDatacenter vdc, " + " VLANNetwork vn "
        + " WHERE ip.dhcp.id = nc.dhcp.id " + " AND nc.id = vn.configuration.id "
        + " AND vn.id = :vlan_id " + " AND vn.network.id = vdc.network.id"
        + " AND vdc.id = :vdc_id " + " AND vm is null AND " + "( ip.ip like :filterLike "
        + " OR ip.mac like :filterLike " + " OR ip.vlanNetwork.name like :filterLike "
        + " OR vapp.name like :filterLike " + " OR vm.name like :filterLike " + ")";

    private final static String GET_IPPOOLMANAGEMENT_ASSIGNED_TO_DIFFERENT_VM_AND_DIFFERENT_FROM_NOT_DEPLOYED_SQL =
        "SELECT * " //
            + "FROM ip_pool_management ip, " //
            + "rasd_management rasd " //
            + "JOIN virtualmachine vm " //
            + "ON vm.idVM = rasd.idVM " + "WHERE rasd.idManagement = ip.idManagement " //
            + "AND rasd.idVM != :idVM " //
            + "AND ip.vlan_network_id = :idVlanNetwork " //
            + "AND vm.state != 'NOT_DEPLOYED'"; //

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
        + "and vdc.enterprise.id = :enterpriseId "
        + "and ip.virtualDatacenter.id = vdc.id "
        + "and vlan.type = 'PUBLIC'";

    private static Criterion equalMac(final String mac)
    {
        assert !StringUtils.isEmpty(mac);

        return Restrictions.eq(IpPoolManagement.MAC_PROPERTY, mac);
    }

    public IpPoolManagementDAO()
    {
        super(IpPoolManagement.class);
    }

    public IpPoolManagementDAO(final EntityManager entityManager)
    {
        super(IpPoolManagement.class, entityManager);
    }

    public boolean existsAnyWithMac(final String mac)
    {
        assert !StringUtils.isEmpty(mac);

        return this.existsAnyByCriterions(equalMac(mac));
    }

    public List<IpPoolManagement> findExternalIpsByVlan(final Integer entId,
        final Integer datacenterLimitId, final Integer vlanId, Integer startwith,
        final Integer limit, final String filter, final OrderByEnum orderByEnum,
        final Boolean descOrAsc, final Boolean onlyAvailable)
    {
        // TODO Auto-generated method stub
        Query finalQuery =
            getSession().createQuery(
                BY_EXTERNAL_VLAN + " " + defineOnlyAvailableFilter(onlyAvailable)
                    + defineOrderBy(orderByEnum, descOrAsc));
        finalQuery.setParameter("ent_id", entId);
        finalQuery.setParameter("dc_limit_id", datacenterLimitId);
        finalQuery.setParameter("vlan_id", vlanId);
        finalQuery.setParameter("filterLike", filter == null || filter.isEmpty() ? "%" : "%"
            + filter + "%");

        // Check if the page requested is bigger than the last one
        Integer totalResults = finalQuery.list().size();

        if (limit != null)
        {
            finalQuery.setMaxResults(limit);
        }

        if (startwith >= totalResults)
        {
            startwith = totalResults - limit;
        }
        finalQuery.setFirstResult(startwith);
        finalQuery.setMaxResults(limit);

        PagedList<IpPoolManagement> ipList = new PagedList<IpPoolManagement>(finalQuery.list());
        ipList.setTotalResults(totalResults);
        ipList.setPageSize(limit);
        ipList.setCurrentElement(startwith);

        return ipList;
    }

    public List<IpPoolManagement> findFreeIpsByVlan(final VLANNetwork vlan)
    {
        Criterion freeIps = Restrictions.eq(IpPoolManagement.VLAN_NETWORK_PROPERTY, vlan);
        Criteria criteria = getSession().createCriteria(IpPoolManagement.class).add(freeIps);
        criteria.add(Restrictions.isNull(IpPoolManagement.MAC_PROPERTY));
        return criteria.list();
    }

    /**
     * Return a single {@link IpPoolManagement}
     * 
     * @param vlan {@link VLANNetwork} oject which the Ip should belong to.
     * @param ipId identifier of the Ip.
     * @return the found object.
     */
    public IpPoolManagement findIp(final VLANNetwork vlan, final Integer ipId)
    {
        Criteria criteria = getSession().createCriteria(IpPoolManagement.class);
        Criterion vlanEqual = Restrictions.eq(IpPoolManagement.VLAN_NETWORK_PROPERTY, vlan);
        Criterion ipEqual = Restrictions.eq(PersistentEntity.ID_PROPERTY, ipId);

        criteria.add(vlanEqual).add(ipEqual);

        return (IpPoolManagement) criteria.uniqueResult();
    }

    public List<IpPoolManagement> findIpsByEnterprise(final Integer entId, Integer firstElem,
        final Integer numElem, final String has, final IpPoolManagement.OrderByEnum orderby,
        final Boolean asc)
    {
        // Get the query that counts the total results.
        Query finalQuery = getSession().createQuery(BY_ENT + " " + defineOrderBy(orderby, asc));
        finalQuery.setParameter("ent_id", entId);
        finalQuery.setParameter("filterLike", has.isEmpty() ? "%" : "%" + has + "%");

        // Check if the page requested is bigger than the last one
        Integer totalResults = finalQuery.list().size();
        if (firstElem >= totalResults)
        {
            firstElem = totalResults - numElem;
        }

        // Get the list of elements
        finalQuery.setFirstResult(firstElem);
        finalQuery.setMaxResults(numElem);

        PagedList<IpPoolManagement> ipList = new PagedList<IpPoolManagement>(finalQuery.list());
        ipList.setTotalResults(totalResults);
        ipList.setPageSize(numElem);
        ipList.setCurrentElement(firstElem);

        return ipList;
    }

    /**
     * Return all the IPs from a VLAN.
     * 
     * @param network {@link Network} network entity that stores all the VLANs
     * @param vlanId identifier of the VLAN to search into.
     * @return all the {@link IpPoolManagement} ips.
     */
    public List<IpPoolManagement> findIpsByNetwork(final Network network, final Integer vlanId)
    {
        return findIpsByNetwork(network, vlanId, null);
    }

    /**
     * Return all the IPs from a VLAN filtered by a string
     * 
     * @param network {@link Network} network entity that stores all the VLANs
     * @param vlanId identifier of the VLAN to search into.
     * @param has to filter the search
     * @return all the {@link IpPoolManagement} ips.
     */
    public List<IpPoolManagement> findIpsByNetwork(final Network network, final Integer vlanId,
        final String has)
    {
        return findIpsByNetwork(network, vlanId, has, 0, null);
    }

    /**
     * Return all the IPs from a VLAN filtered by a string and saying how many elements do you want
     * and the first element to retrieve
     * 
     * @param network {@link Network} network entity that stores all the VLANs
     * @param vlanId identifier of the VLAN to search into.
     * @param has to filter the search
     * @param firstElem firstelement to retrieve.
     * @param numeElem to retrieve.
     * @return all the {@link IpPoolManagement} ips.
     */
    public List<IpPoolManagement> findIpsByNetwork(final Network network, final Integer vlanId,
        final String has, Integer firstElem, final Integer numElem)
    {
        Query finalQuery = getSession().createQuery(BY_NETWORK);
        finalQuery.setParameter("net_id", network.getId());
        finalQuery.setParameter("vlan_id", vlanId);
        finalQuery.setParameter("filterLike", has == null || has.isEmpty() ? "%" : "%" + has + "%");

        // Check if the page requested is bigger than the last one
        Integer totalResults = finalQuery.list().size();

        if (numElem != null)
        {
            finalQuery.setMaxResults(numElem);
        }

        if (firstElem >= totalResults)
        {
            firstElem = totalResults - 1;
            finalQuery.setMaxResults(1);
        }
        finalQuery.setFirstResult(firstElem);

        PagedList<IpPoolManagement> ipList = new PagedList<IpPoolManagement>(finalQuery.list());
        ipList.setTotalResults(totalResults);
        ipList.setPageSize(numElem);
        ipList.setCurrentElement(firstElem);

        return ipList;
    }

    /**
     * Return the {@link PagedList} entity with the Ips by VLAN.
     * 
     * @param vdcId virtual datacenter id
     * @param vlanId vlan id
     * @return list of used IpPoolManagement.
     */
    public List<IpPoolManagement> findIpsByPrivateVLAN(final Integer vdcId, final Integer vlanId)
    {

        Query finalQuery = getSession().createQuery(BY_VLAN);
        finalQuery.setParameter("vdc_id", vdcId);
        finalQuery.setParameter("vlan_id", vlanId);
        finalQuery.setParameter("filterLike", "%");

        Integer totalResults = finalQuery.list().size();

        PagedList<IpPoolManagement> ipList = new PagedList<IpPoolManagement>(finalQuery.list());
        ipList.setTotalResults(totalResults);

        return ipList;

    }

    /**
     * Find all the IpPoolManagement created and available by a vLAN with filter options
     * 
     * @param vdcId identifier of the virtual datacenter.
     * @param vlanId identifier of the vlan.
     * @param firstElem first element to retrieve.
     * @param numElem number of elements to retrieve.
     * @param has filter %like%
     * @param orderby ordering filter. {@see IpPoolManagement.OrderByEnum}
     * @param asc ordering filter, ascending = true, descending = false.
     * @return List of IP addresses that pass the filter.
     */
    public List<IpPoolManagement> findIpsByPrivateVLANAvailableFiltered(final Integer vdcId,
        final Integer vlanId, Integer firstElem, final Integer numElem, final String has,
        final IpPoolManagement.OrderByEnum orderby, final Boolean asc)
    {
        // Get the query that counts the total results.
        Query finalQuery =
            getSession().createQuery(
                BY_VLAN + " " + defineFilterAvailable() + defineOrderBy(orderby, asc));
        finalQuery.setParameter("vdc_id", vdcId);
        finalQuery.setParameter("vlan_id", vlanId);
        finalQuery.setParameter("filterLike", has.isEmpty() ? "%" : "%" + has + "%");

        // Check if the page requested is bigger than the last one
        Integer totalResults = finalQuery.list().size();

        if (firstElem >= totalResults)
        {
            firstElem = totalResults - numElem;
        }
        finalQuery.setFirstResult(firstElem);
        finalQuery.setMaxResults(numElem);

        PagedList<IpPoolManagement> ipList = new PagedList<IpPoolManagement>(finalQuery.list());
        ipList.setTotalResults(totalResults);
        ipList.setPageSize(numElem);
        ipList.setCurrentElement(firstElem);

        return ipList;
    }

    /**
     * Find all the IpPoolManagement created by a vLAN with filter options
     * 
     * @param vdcId identifier of the virtual datacenter.
     * @param vlanId identifier of the vlan.
     * @param firstElem first element to retrieve.
     * @param numElem number of elements to retrieve.
     * @param has filter %like%
     * @param orderby ordering filter. {@see IpPoolManagement.OrderByEnum}
     * @param asc ordering filter, ascending = true, descending = false.
     * @return List of IP addresses that pass the filter.
     */
    public List<IpPoolManagement> findIpsByPrivateVLANFiltered(final Integer vdcId,
        final Integer vlanId, Integer firstElem, final Integer numElem, final String has,
        final IpPoolManagement.OrderByEnum orderby, final Boolean asc, final Boolean freeIps)
    {
        // Get the query that counts the total results.
        Query finalQuery;
        if (!freeIps)
        {
            finalQuery = getSession().createQuery(BY_VLAN + " " + defineOrderBy(orderby, asc));
        }
        else
        {
            finalQuery =
                getSession().createQuery(
                    BY_VLAN_WITHOUT_USED_IPS + " " + defineFilterAvailable()
                        + defineOrderBy(orderby, asc));
        }
        finalQuery.setParameter("vdc_id", vdcId);
        finalQuery.setParameter("vlan_id", vlanId);
        finalQuery.setParameter("filterLike", has.isEmpty() ? "%" : "%" + has + "%");

        // Check if the page requested is bigger than the last one
        Integer totalResults = finalQuery.list().size();

        if (firstElem >= totalResults)
        {
            firstElem = totalResults - numElem;
        }
        finalQuery.setFirstResult(firstElem);
        finalQuery.setMaxResults(numElem);

        PagedList<IpPoolManagement> ipList = new PagedList<IpPoolManagement>(finalQuery.list());
        ipList.setTotalResults(totalResults);
        ipList.setPageSize(numElem);
        ipList.setCurrentElement(firstElem);

        return ipList;
    }

    public List<IpPoolManagement> findIpsByVdc(final Integer vdcId, Integer firstElem,
        final Integer numElem, final String has, final IpPoolManagement.OrderByEnum orderby,
        final Boolean asc)
    {
        // Get the query that counts the total results.
        Query finalQuery = getSession().createQuery(BY_VDC + " " + defineOrderBy(orderby, asc));
        finalQuery.setParameter("vdc_id", vdcId);
        finalQuery.setParameter("filterLike", has.isEmpty() ? "%" : "%" + has + "%");

        // Check if the page requested is bigger than the last one
        Integer totalResults = finalQuery.list().size();

        if (firstElem == null)
        {
            firstElem = 0;
        }

        if (firstElem >= totalResults)
        {
            firstElem = totalResults - numElem;
        }
        finalQuery.setFirstResult(firstElem);
        finalQuery.setMaxResults(numElem);

        PagedList<IpPoolManagement> ipList = new PagedList<IpPoolManagement>(finalQuery.list());
        ipList.setTotalResults(totalResults);
        ipList.setPageSize(numElem);
        ipList.setCurrentElement(firstElem);

        return ipList;
    }

    public List<IpPoolManagement> findIpsByVirtualAppliance(final VirtualAppliance vapp)
    {
        Criterion onVapp = Restrictions.eq(RasdManagement.VIRTUAL_APPLIANCE_PROPERTY, vapp);
        Criteria criteria = getSession().createCriteria(IpPoolManagement.class).add(onVapp);
        List<IpPoolManagement> result = getResultList(criteria);

        return result;
    }

    public List<IpPoolManagement> findIpsByVirtualMachine(final VirtualMachine virtualMachine)
    {
        Query query = getSession().createQuery(BY_VIRTUAL_MACHINE);
        query.setParameter("vm_id", virtualMachine.getId());

        List<IpPoolManagement> ips = query.list();
        return ips;

    }

    public List<IpPoolManagement> findIpsByVirtualMachineWithConfigurationId(
        final VirtualMachine vm, final Integer vmConfigId)
    {
        List<IpPoolManagement> ips = findIpsByVirtualMachine(vm);
        List<IpPoolManagement> resultIps = new ArrayList<IpPoolManagement>();
        for (IpPoolManagement ip : ips)
        {
            if (ip.getVlanNetwork().getConfiguration().getId().equals(vmConfigId))
            {
                resultIps.add(ip);
            }
        }
        return resultIps;
    }

    public List<IpPoolManagement> findIpsByVlan(final VLANNetwork vlan)
    {
        return findByCriterions(Restrictions.eq(IpPoolManagement.VLAN_NETWORK_PROPERTY, vlan));
    }

    public IpPoolManagement findPublicIpPurchasedByVirtualDatacenter(final Integer vdcId,
        final Integer ipId)
    {
        Query finalQuery = getSession().createQuery(BY_IP_PURCHASED);
        finalQuery.setParameter("vdc_id", vdcId);
        finalQuery.setParameter("ip_id", ipId);

        return (IpPoolManagement) finalQuery.uniqueResult();
    }

    /**
     * Return all the public IPS defined into a Datacenter.
     * 
     * @param datacenterId identifier of the datacenter.
     * @param startwith first element to retrieve.
     * @param limit number of elements to retrieve.
     * @param filter filter query.
     * @param orderByEnum the way we order the query.
     * @param descOrAsc if the order is ascendant or descendant.
     * @return the list of matching {@link IpPoolManagement} object.
     */
    public List<IpPoolManagement> findPublicIpsByDatacenter(final Integer datacenterId,
        Integer startwith, final Integer limit, final String filter, final OrderByEnum orderByEnum,
        final Boolean descOrAsc)
    {
        Query finalQuery =
            getSession().createQuery(BY_DATACENTER + " " + defineOrderBy(orderByEnum, descOrAsc));
        finalQuery.setParameter("datacenter_id", datacenterId);
        finalQuery.setParameter("filterLike", filter == null || filter.isEmpty() ? "%" : "%"
            + filter + "%");

        // Check if the page requested is bigger than the last one
        Integer totalResults = finalQuery.list().size();

        if (limit != null)
        {
            finalQuery.setMaxResults(limit);
        }

        if (startwith >= totalResults)
        {
            startwith = totalResults - limit;
        }
        finalQuery.setFirstResult(startwith);
        finalQuery.setMaxResults(limit);

        PagedList<IpPoolManagement> ipList = new PagedList<IpPoolManagement>(finalQuery.list());
        ipList.setTotalResults(totalResults);
        ipList.setPageSize(limit);
        ipList.setCurrentElement(startwith);

        return ipList;
    }

    public List<IpPoolManagement> findPublicIpsByVlan(final Integer datacenterId,
        final Integer vlanId, Integer startwith, final Integer limit, final String filter,
        final OrderByEnum orderByEnum, final Boolean descOrAsc, final Boolean all)
    {
        Query finalQuery =
            getSession().createQuery(
                BY_PUBLIC_VLAN + " " + defineAllFilter(all) + " "
                    + defineOrderBy(orderByEnum, descOrAsc));
        finalQuery.setParameter("datacenter_id", datacenterId);
        finalQuery.setParameter("vlan_id", vlanId);
        finalQuery.setParameter("filterLike", filter == null || filter.isEmpty() ? "%" : "%"
            + filter + "%");

        // Check if the page requested is bigger than the last one
        Integer totalResults = finalQuery.list().size();

        if (limit != null)
        {
            finalQuery.setMaxResults(limit);
        }

        if (startwith >= totalResults)
        {
            startwith = totalResults - limit;
        }
        finalQuery.setFirstResult(startwith);
        finalQuery.setMaxResults(limit);

        PagedList<IpPoolManagement> ipList = new PagedList<IpPoolManagement>(finalQuery.list());
        ipList.setTotalResults(totalResults);
        ipList.setPageSize(limit);
        ipList.setCurrentElement(startwith);

        return ipList;
    }

    public List<IpPoolManagement> findpublicIpsPurchasedByVirtualDatacenter(final Integer vdcId,
        final Boolean onlyAvailabe, Integer startwith, final Integer limit, final String filter,
        final OrderByEnum orderByEnum, final Boolean descOrAsc)
    {
        Query finalQuery =
            getSession().createQuery(
                BY_VDC_PURCHASED + " " + defineOnlyAvailableFilter(onlyAvailabe)
                    + defineOrderBy(orderByEnum, descOrAsc));
        finalQuery.setParameter("vdc_id", vdcId);
        finalQuery.setParameter("filterLike", filter == null || filter.isEmpty() ? "%" : "%"
            + filter + "%");

        // Check if the page requested is bigger than the last one
        Integer totalResults = finalQuery.list().size();

        if (limit != null)
        {
            finalQuery.setMaxResults(limit);
        }

        if (startwith >= totalResults)
        {
            startwith = totalResults - limit;
        }
        finalQuery.setFirstResult(startwith);
        finalQuery.setMaxResults(limit);

        PagedList<IpPoolManagement> ipList = new PagedList<IpPoolManagement>(finalQuery.list());
        ipList.setTotalResults(totalResults);
        ipList.setPageSize(limit);
        ipList.setCurrentElement(startwith);

        return ipList;

    }

    /**
     * Return the list of IPs purchased by an enterprise in a public VLAN. Any IP in a public VLAN
     * with VirtualDatacenter not null
     * 
     * @param vlan network to search into.
     * @return the list of with IPs purchased.
     */
    public List<IpPoolManagement> findPublicIpsPurchasedByVlan(final VLANNetwork vlan)
    {
        return findByCriterions(Restrictions.eq(IpPoolManagement.VLAN_NETWORK_PROPERTY, vlan),
            Restrictions.isNotNull(RasdManagement.VIRTUAL_DATACENTER_PROPERTY));
    }

    public List<IpPoolManagement> findpublicIpsToPurchaseByVirtualDatacenter(final Integer vdcId,
        Integer startwith, final Integer limit, final String filter, final OrderByEnum orderByEnum,
        final Boolean descOrAsc)
    {
        Query finalQuery =
            getSession().createQuery(
                BY_VDC_TO_PURCHASE + " " + defineOrderBy(orderByEnum, descOrAsc));
        finalQuery.setParameter("vdc_id", vdcId);
        finalQuery.setParameter("filterLike", filter == null || filter.isEmpty() ? "%" : "%"
            + filter + "%");

        // Check if the page requested is bigger than the last one
        Integer totalResults = finalQuery.list().size();

        if (limit != null)
        {
            finalQuery.setMaxResults(limit);
        }

        if (startwith >= totalResults)
        {
            startwith = totalResults - limit;
        }
        finalQuery.setFirstResult(startwith);
        finalQuery.setMaxResults(limit);

        PagedList<IpPoolManagement> ipList = new PagedList<IpPoolManagement>(finalQuery.list());
        ipList.setTotalResults(totalResults);
        ipList.setPageSize(limit);
        ipList.setCurrentElement(startwith);

        return ipList;
    }

    public IpPoolManagement findPublicIpToPurchaseByVirtualDatacenter(final Integer vdcId,
        final Integer ipId)
    {
        Query finalQuery = getSession().createQuery(BY_IP_TO_PURCHASE);
        finalQuery.setParameter("vdc_id", vdcId);
        finalQuery.setParameter("ip_id", ipId);

        return (IpPoolManagement) finalQuery.uniqueResult();
    }

    public List<IpPoolManagement> findUsedIpsByPrivateVLAN(final Integer vlanId)
    {
        Query finalQuery =
            getSession().createQuery(BY_VLAN_USED_BY_ANY_VM + " " + defineFilterUsed());
        finalQuery.setParameter("vlan_id", vlanId);
        finalQuery.setParameter("filterLike", "%");

        Integer totalResults = finalQuery.list().size();

        PagedList<IpPoolManagement> ipList = new PagedList<IpPoolManagement>(finalQuery.list());
        ipList.setTotalResults(totalResults);

        return ipList;
    }

    /**
     * Return the {@link PagedList} entity with the used Ips by VLAN.
     * 
     * @param vdcId virtual datacenter id
     * @param vlanId vlan id
     * @return list of used IpPoolManagement.
     */
    public List<IpPoolManagement> findUsedIpsByPrivateVLAN(final Integer vdcId, final Integer vlanId)
    {
        Query finalQuery = getSession().createQuery(BY_VLAN + " " + defineFilterUsed());
        finalQuery.setParameter("vdc_id", vdcId);
        finalQuery.setParameter("vlan_id", vlanId);
        finalQuery.setParameter("filterLike", "%");

        Integer totalResults = finalQuery.list().size();

        PagedList<IpPoolManagement> ipList = new PagedList<IpPoolManagement>(finalQuery.list());
        ipList.setTotalResults(totalResults);

        return ipList;
    }

    public Collection<String> getAllMacs()
    {
        Criteria criteria = getSession().createCriteria(IpPoolManagement.class);
        ProjectionList projList = Projections.projectionList();
        projList.add(Projections.property(IpPoolManagement.MAC_PROPERTY));

        criteria.setProjection(projList);
        return criteria.list();
    }

    public List<IpPoolManagement> getPublicNetworkPoolPurchasedByEnterprise(
        final Integer enterpriseId)
    {
        Query query = getSession().createQuery(GET_NETWORK_POOL_PURCHASED_BY_ENTERPRISE);
        query.setParameter("enterpriseId", enterpriseId);

        return query.list();
    }

    public boolean isDefaultNetworkofanyVDC(final Integer vlanId)
    {
        Query query = getSession().createSQLQuery(BY_DEFAULT_VLAN_USED_BY_ANY_VDC);
        query.setParameter("vlan_id", vlanId);
        return !query.list().isEmpty();
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

    public boolean privateVLANinUseByAnyVDC(final Integer vlanId)
    {
        List<IpPoolManagement> ippoolList;
        Query query = getSession().createSQLQuery(BY_VLAN_USED_BY_ANY_VDC);
        query.setParameter("vlan_id", vlanId);
        ippoolList = query.list();

        if (ippoolList.isEmpty())
        {
            return false;
        }
        return true;
    }

    /**
     * If the 'all' is set, return all the IPs by a public vlan.
     * 
     * @param all boolean to set if we should return all the IPs.
     * @return a String filter.
     */
    private String defineAllFilter(final Boolean all)
    {
        if (!all)
        {
            return " AND ip.available = 1";
        }

        return "";
    }

    /**
     * Adds the filter for only available ip addresses in the private network.
     * 
     * @return the query string that defines the filter.
     */
    private String defineFilterAvailable()
    {
        return " AND vm is null";
    }

    /**
     * Adds the filter to return only the VLANs used.
     * 
     * @return the string with the filter.
     */
    private String defineFilterUsed()
    {
        return " AND vm is not null";
    }

    private String defineOnlyAvailableFilter(final Boolean onlyAvailable)
    {
        if (onlyAvailable)
        {
            return " AND vm is null ";
        }
        else
        {
            return "";
        }
    }

    private String defineOrderBy(final IpPoolManagement.OrderByEnum orderBy, final Boolean asc)
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
                queryString.append("vdc.name ");
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

            case ENTERPRISENAME:
            {
                queryString.append("ent.name ");
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
