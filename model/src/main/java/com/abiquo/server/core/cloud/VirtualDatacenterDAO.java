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

package com.abiquo.server.core.cloud;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import javax.persistence.EntityManager;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.Transformer;
import org.apache.commons.lang.StringUtils;
import org.hibernate.Criteria;
import org.hibernate.Query;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.Disjunction;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.springframework.stereotype.Repository;

import com.abiquo.server.core.cloud.VirtualDatacenter.OrderByEnum;
import com.abiquo.server.core.common.DefaultEntityCurrentUsed;
import com.abiquo.server.core.common.persistence.DefaultDAOBase;
import com.abiquo.server.core.enterprise.Enterprise;
import com.abiquo.server.core.enterprise.User;
import com.abiquo.server.core.infrastructure.Datacenter;
import com.abiquo.server.core.infrastructure.network.VLANNetwork;
import com.abiquo.server.core.util.FilterOptions;
import com.abiquo.server.core.util.PagedList;
import com.softwarementors.bzngine.entities.PersistentEntity;

@Repository("jpaVirtualDatacenterDAO")
@SuppressWarnings("unchecked")
public class VirtualDatacenterDAO extends DefaultDAOBase<Integer, VirtualDatacenter>
{
    public VirtualDatacenterDAO()
    {
        super(VirtualDatacenter.class);
    }

    public VirtualDatacenterDAO(final EntityManager entityManager)
    {
        super(VirtualDatacenter.class, entityManager);
    }

    private static Criterion sameEnterprise(final Enterprise enterprise)
    {
        return Restrictions.eq(VirtualDatacenter.ENTERPRISE_PROPERTY, enterprise);
    }

    private static Criterion sameDatacenter(final Datacenter datacenter)
    {
        return Restrictions.eq(VirtualDatacenter.DATACENTER_PROPERTY, datacenter);
    }

    private static Criterion availableToUser(final User user)
    {
        Collection<String> idsStrings =
            Arrays.asList(user.getAvailableVirtualDatacenters().split(","));

        Collection<Integer> ids = CollectionUtils.collect(idsStrings, new Transformer()
        {
            @Override
            public Object transform(final Object input)
            {
                return Integer.valueOf(input.toString());
            }
        });

        return Restrictions.in(PersistentEntity.ID_PROPERTY, ids);
    }

    public Collection<VirtualDatacenter> findByEnterpriseAndDatacenter(final Enterprise enterprise,
        final Datacenter datacenter, final User user, final Integer startwith, final Integer limit,
        final String filter, final OrderByEnum orderByEnum, final Boolean asc)
    {
        Collection<Criterion> restrictions = new ArrayList<Criterion>();
        if (enterprise != null)
        {
            restrictions.add(sameEnterprise(enterprise));
        }
        if (datacenter != null)
        {
            restrictions.add(sameDatacenter(datacenter));
        }
        if (user != null)
        {
            restrictions.add(availableToUser(user));
        }

        return findVirtualDatacentersByCriterions(restrictions, null);
    }

    public Collection<VirtualDatacenter> findByDatacenter(final Datacenter datacenter,
        final Integer startwith, final Integer limit, final String filter,
        final OrderByEnum orderByEnum, final Boolean asc)
    {
        Collection<Criterion> restrictions = new ArrayList<Criterion>();
        if (datacenter != null)
        {
            restrictions.add(sameDatacenter(datacenter));
        }

        return findVirtualDatacentersByCriterions(restrictions, null);
    }

    public Collection<VirtualDatacenter> findByEnterpriseAndDatacenter(final Enterprise enterprise,
        final Datacenter datacenter, final Integer startwith, final Integer limit,
        final String filter, final OrderByEnum orderByEnum, final Boolean asc)
    {
        return findByEnterpriseAndDatacenter(enterprise, datacenter, null, startwith, limit,
            filter, orderByEnum, asc);
    }

    public Collection<VirtualDatacenter> findByEnterpriseAndDatacenterFilter(
        final Enterprise enterprise, final Datacenter datacenter, final FilterOptions filterOptions)
    {
        return findByEnterpriseAndDatacenterFilter(enterprise, datacenter, null, filterOptions);
    }

    public Collection<VirtualDatacenter> findByEnterpriseAndDatacenterFilter(
        final Enterprise enterprise, final Datacenter datacenter, final User user,
        final FilterOptions filterOptions)
    {
        Collection<Criterion> restrictions = new ArrayList<Criterion>();
        if (enterprise != null)
        {
            restrictions.add(sameEnterprise(enterprise));
        }
        if (datacenter != null)
        {
            restrictions.add(sameDatacenter(datacenter));
        }
        if (user != null)
        {
            restrictions.add(availableToUser(user));
        }

        if (filterOptions != null)
        {
            restrictions.add(Restrictions.like(VirtualDatacenter.NAME_PROPERTY, filterOptions
                .getFilter(), MatchMode.ANYWHERE));
        }

        return findVirtualDatacentersByCriterions(restrictions, filterOptions);
    }

    private Collection<VirtualDatacenter> findVirtualDatacentersByCriterions(
        final Collection<Criterion> criterions, final FilterOptions filterOptions)
    {
        Criteria criteria = getSession().createCriteria(VirtualDatacenter.class);

        for (Criterion criterion : criterions)
        {
            criteria.add(criterion);
        }

        criteria.addOrder(Order.asc(VirtualDatacenter.NAME_PROPERTY));

        if (filterOptions != null)
        {
            Integer size = criteria.list().size();

            criteria.setFirstResult(filterOptions.getStartwith());
            criteria.setMaxResults(filterOptions.getLimit());

            PagedList<VirtualDatacenter> vdcList =
                new PagedList<VirtualDatacenter>(criteria.list());
            vdcList.setTotalResults(size);
            vdcList.setPageSize(filterOptions.getLimit() > size ? size : filterOptions.getLimit());
            vdcList.setCurrentElement(filterOptions.getStartwith());

            return vdcList;
        }
        else
        {
            return getResultList(criteria);
        }
    }

    private Criteria createCriteria(final Collection<Criterion> criterions, final String filter,
        final OrderByEnum orderByEnum, final boolean asc)
    {
        Criteria criteria = getSession().createCriteria(VirtualDatacenter.class);

        for (Criterion criterion : criterions)
        {
            criteria.add(criterion);
        }

        if (!StringUtils.isEmpty(filter))
        {
            criteria.add(filterBy(filter));
        }

        if (!StringUtils.isEmpty(orderByEnum.getColumnSQL()))
        {
            Order order = Order.desc(orderByEnum.getColumnSQL());
            if (asc)
            {
                order = Order.asc(orderByEnum.getColumnSQL());
            }
            criteria.addOrder(order);
        }
        return criteria;
    }

    private Criterion filterBy(final String filter)
    {
        Disjunction filterDisjunction = Restrictions.disjunction();

        filterDisjunction.add(Restrictions
            .like(VirtualDatacenter.NAME_PROPERTY, '%' + filter + '%'));

        return filterDisjunction;
    }

    public Collection<VirtualDatacenter> findByEnterprise(final Enterprise enterprise)
    {
        assert enterprise != null;
        assert isManaged2(enterprise);

        Criteria criteria = createCriteria(sameEnterprise(enterprise));
        criteria.addOrder(Order.asc(VirtualDatacenter.NAME_PROPERTY));
        List<VirtualDatacenter> result = getResultList(criteria);
        return result;
    }

    private static final String SUM_VM_RESOURCES =
        "select sum(vm.cpu), sum(vm.ram), sum(vm.hd) from virtualmachine vm, nodevirtualimage vi, node n, virtualapp a "
            + "where vi.idVM = vm.idVM and vi.idNode = n.idNode and n.idVirtualApp = a.idVirtualApp "
            + "and a.idVirtualDataCenter = :virtualDatacenterId and vm.state != 'NOT_ALLOCATED' and vm.idHypervisor is not null";

    // +
    // "and hy.id = vm.idHypervisor and pm.idPhysicalMachine = hy.idPhysicalMachine and pm.idState != 7";
    // // not HA_DISABLED

    private static final String SUM_VOLUMES_RESOURCES =
        "select sum(r.limitResource) from rasd r, rasd_management rm where r.instanceID = rm.idResource "
            + "and rm.idResourceType = '8' and rm.idVirtualDatacenter = :virtualDatacenterId";

    private static final String SUM_EXTRA_HD_RESOURCES =
        "select sum(r.limitResource) from rasd r, rasd_management rm, virtualmachine vm where r.instanceID = rm.idResource "
            + "and rm.idResourceType = '17' and rm.idVirtualDatacenter = :virtualDatacenterId "
            + "and  rm.idVM = vm.idVM and vm.state != 'NOT_ALLOCATED' and vm.idHypervisor is not null";

    private static final String COUNT_PUBLIC_IP_RESOURCES =
        "select count(*) from ip_pool_management ipm, rasd_management rm, vlan_network vn, virtualdatacenter vdc "
            + " where ipm.vlan_network_id = vn.vlan_network_id "
            + " and rm.idManagement = ipm.idManagement "
            + " and rm.idVirtualDataCenter = vdc.idVirtualDataCenter "
            + " and vdc.idVirtualDataCenter = :virtualDatacenterId "
            + " and vn.networktype = 'PUBLIC' ";

    private static final String COUNT_PRIVATE_VLANS_RESOURCES = " SELECT vlan "//
        + "FROM com.abiquo.server.core.infrastructure.network.VLANNetwork vlan, "//
        + "com.abiquo.server.core.cloud.VirtualDatacenter vdc "//
        + "WHERE vlan.network.id = vdc.network.id "//
        + "and vdc.id = :virtualDatacenterId";

    private static final String GET_VDC_FROM_DEFAULT_VLAN = " SELECT vdc "//
        + "FROM VirtualDatacenter vdc "//
        + "WHERE vdc.defaultVlan.id = :vlanId "//
        + "and vdc.defaultVlan.type = 'EXTERNAL'";

    public DefaultEntityCurrentUsed getCurrentResourcesAllocated(final int virtualDatacenterId)
    {
        Object[] vmResources =
            (Object[]) getSession().createSQLQuery(SUM_VM_RESOURCES).setParameter(
                "virtualDatacenterId", virtualDatacenterId).uniqueResult();

        Long cpu = vmResources[0] == null ? 0 : ((BigDecimal) vmResources[0]).longValue();
        Long ram = vmResources[1] == null ? 0 : ((BigDecimal) vmResources[1]).longValue();
        Long hd = vmResources[2] == null ? 0 : ((BigDecimal) vmResources[2]).longValue();

        BigDecimal extraHd =
            (BigDecimal) getSession().createSQLQuery(SUM_EXTRA_HD_RESOURCES).setParameter(
                "virtualDatacenterId", virtualDatacenterId).uniqueResult();
        Long hdTot = extraHd == null ? hd : hd + extraHd.longValue() * 1024 * 1024;

        BigDecimal storage =
            (BigDecimal) getSession().createSQLQuery(SUM_VOLUMES_RESOURCES).setParameter(
                "virtualDatacenterId", virtualDatacenterId).uniqueResult();

        BigInteger publicIps =
            (BigInteger) getSession().createSQLQuery(COUNT_PUBLIC_IP_RESOURCES).setParameter(
                "virtualDatacenterId", virtualDatacenterId).uniqueResult();

        DefaultEntityCurrentUsed used = new DefaultEntityCurrentUsed(cpu.intValue(), ram, hdTot);

        // Storage usage is stored in MB
        used.setStorage(storage == null ? 0 : storage.longValue() * 1024 * 1024);
        used.setPublicIp(publicIps == null ? 0 : publicIps.longValue());
        used.setVlanCount(getVLANUsage(virtualDatacenterId).size());

        return used;
    }

    private List<VLANNetwork> getVLANUsage(final Integer virtualdatacenterId)
    {
        Query query = getSession().createQuery(COUNT_PRIVATE_VLANS_RESOURCES);
        query.setParameter("virtualDatacenterId", virtualdatacenterId);

        return query.list();
    }

    public List<VirtualDatacenter> getVirualDatacenterFromDefaultVlan(final Integer defaultVlanId)
    {
        Query query = getSession().createQuery(GET_VDC_FROM_DEFAULT_VLAN);
        query.setParameter("vlanId", defaultVlanId);

        return query.list();
    }
}
