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
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.springframework.stereotype.Repository;

import com.abiquo.server.core.cloud.VirtualAppliance.OrderByEnum;
import com.abiquo.server.core.common.persistence.DefaultDAOBase;
import com.abiquo.server.core.enterprise.Enterprise;
import com.abiquo.server.core.enterprise.User;
import com.abiquo.server.core.util.FilterOptions;
import com.abiquo.server.core.util.PagedList;
import com.softwarementors.bzngine.entities.PersistentEntity;

@Repository("jpaVirtualApplianceDAO")
public class VirtualApplianceDAO extends DefaultDAOBase<Integer, VirtualAppliance>
{
    private static final String GET_VAPPS_BY_ENTERPRISE_AND_DATACENTER = " SELECT vapp "//
        + "FROM VirtualAppliance vapp "//
        + "WHERE vapp.enterprise.id = :entId "//
        + "and vapp.virtualDatacenter.datacenter = :dcId";

    private static final String GET_VAPPS_BY_ENTERPRISE_AND_DATACENTER_AND_USER = " SELECT vapp "//
        + "FROM VirtualAppliance vapp "//
        + "WHERE vapp.enterprise.id = :entId "//
        + "and vapp.virtualDatacenter.datacenter = :dcId "//
        + "and vapp.virtualDatacenter.id IN (:allowedVDCs) ";

    private static Criterion sameEnterprise(final Enterprise enterprise)
    {
        return Restrictions.eq(VirtualAppliance.ENTERPRISE_PROPERTY, enterprise);
    }

    private static Criterion sameVirtualDatacenter(final VirtualDatacenter virtualDatacenter)
    {
        return Restrictions.eq(VirtualAppliance.VIRTUAL_DATACENTER_PROPERTY, virtualDatacenter);
    }

    public VirtualApplianceDAO()
    {
        super(VirtualAppliance.class);
    }

    public VirtualApplianceDAO(final EntityManager entityManager)
    {
        super(VirtualAppliance.class, entityManager);
    }

    public List<VirtualAppliance> findByEnterprise(final Enterprise enterprise)
    {
        Criteria criteria = createCriteria(sameEnterprise(enterprise));
        criteria.addOrder(Order.asc(VirtualAppliance.NAME_PROPERTY));

        return criteria.list();
    }

    /**
     * @param user, only users with restricted VDCs. If no restricted then use user = null
     */
    public List<VirtualAppliance> findByEnterprise(final Enterprise enterprise,
        final FilterOptions filterOptions, final User user)
    {
        if (filterOptions == null)
        {
            return findByEnterprise(enterprise);
        }

        // Check if the orderBy element is actually one of the available ones
        VirtualAppliance.OrderByEnum orderByEnum =
            VirtualAppliance.OrderByEnum.valueOf(filterOptions.getOrderBy().toUpperCase());

        Integer limit = filterOptions.getLimit();
        Integer startwith = filterOptions.getStartwith();
        String filter = filterOptions.getFilter();
        boolean asc = filterOptions.getAsc();

        Criteria criteria = createCriteria(enterprise, filter, orderByEnum, asc);

        // Check if the page requested is bigger than the last one
        Long total = count(criteria);
        criteria = createCriteria(enterprise, filter, orderByEnum, asc);
        Integer totalResults = total.intValue();
        limit = limit != 0 ? limit : totalResults;
        if (limit != null)
        {
            criteria.setMaxResults(limit);
        }

        if (startwith >= totalResults)
        {
            startwith = totalResults - limit;
        }
        criteria.setFirstResult(startwith);
        criteria.setMaxResults(limit);

        if (user != null)
        {
            criteria.createAlias(VirtualAppliance.VIRTUAL_DATACENTER_PROPERTY, "virtualdatacenter");
            criteria.add(Restrictions.in("virtualdatacenter." + PersistentEntity.ID_PROPERTY,
                availableVdsToUser(user)));
        }

        List<VirtualAppliance> result = getResultList(criteria);

        PagedList<VirtualAppliance> page = new PagedList<VirtualAppliance>();
        page.addAll(result);
        page.setCurrentElement(startwith);
        page.setPageSize(limit);
        page.setTotalResults(totalResults);

        return page;
    }

    public List<VirtualAppliance> findByEnterpriseAndDatacenter(final Integer entId,
        final Integer dcId)
    {
        Query query = getSession().createQuery(GET_VAPPS_BY_ENTERPRISE_AND_DATACENTER);
        query.setInteger("entId", entId);
        query.setInteger("dcId", dcId);

        return query.list();
    }

    /**
     * @param user, only users with restricted VDCs
     */
    public List<VirtualAppliance> findByEnterpriseAndDatacenter(final Integer entId,
        final Integer dcId, final User user)
    {
        Query query = getSession().createQuery(GET_VAPPS_BY_ENTERPRISE_AND_DATACENTER_AND_USER);
        query.setInteger("entId", entId);
        query.setInteger("dcId", dcId);
        query.setParameterList("allowedVDCs", availableVdsToUser(user));

        return query.list();
    }

    @SuppressWarnings("unchecked")
    private static Collection<Integer> availableVdsToUser(final User user)
    {
        Collection<String> idsStrings =
            Arrays.asList(user.getAvailableVirtualDatacenters().split(","));

        return CollectionUtils.collect(idsStrings, new Transformer()
        {
            @Override
            public Object transform(final Object input)
            {
                return Integer.valueOf(input.toString());
            }
        });
    }

    public VirtualAppliance findById(final VirtualDatacenter vdc, final Integer vappId)
    {
        Criteria criteria = createCriteria(sameVirtualDatacenter(vdc));
        criteria.add(Restrictions.eq(PersistentEntity.ID_PROPERTY, vappId));

        return (VirtualAppliance) criteria.uniqueResult();
    }

    public VirtualAppliance findByName(final String name)
    {
        return findUniqueByProperty(VirtualAppliance.NAME_PROPERTY, name);
    }

    public List<VirtualAppliance> findByVirtualDatacenter(final VirtualDatacenter virtualDatacenter)
    {
        Criteria criteria = createCriteria(sameVirtualDatacenter(virtualDatacenter));
        criteria.addOrder(Order.asc(VirtualAppliance.NAME_PROPERTY));

        return criteria.list();
    }

    public List<VirtualAppliance> findByVirtualDatacenter(
        final VirtualDatacenter virtualDatacenter, final FilterOptions filterOptions)
    {
        if (filterOptions == null)
        {
            return findByVirtualDatacenter(virtualDatacenter);
        }

        // Check if the orderBy element is actually one of the available ones
        VirtualAppliance.OrderByEnum orderByEnum =
            VirtualAppliance.OrderByEnum.valueOf(filterOptions.getOrderBy().toUpperCase());

        Integer limit = filterOptions.getLimit();
        Integer startwith = filterOptions.getStartwith();
        String filter = filterOptions.getFilter();
        boolean asc = filterOptions.getAsc();

        Criteria criteria = createCriteria(virtualDatacenter, filter, orderByEnum, asc);

        // Check if the page requested is bigger than the last one
        Long total = count(criteria);
        criteria = createCriteria(virtualDatacenter, filter, orderByEnum, asc);
        Integer totalResults = total.intValue();
        limit = limit != 0 ? limit : totalResults;
        if (limit != null)
        {
            criteria.setMaxResults(limit);
        }

        if (startwith >= totalResults)
        {
            startwith = totalResults - limit;
        }
        criteria.setFirstResult(startwith);
        criteria.setMaxResults(limit);

        List<VirtualAppliance> result = getResultList(criteria);

        PagedList<VirtualAppliance> page = new PagedList<VirtualAppliance>();
        page.addAll(result);
        page.setCurrentElement(startwith);
        page.setPageSize(limit);
        page.setTotalResults(totalResults);

        return page;
    }

    private Criteria createCriteria(final VirtualDatacenter virtualDatacenter, final String filter,
        final OrderByEnum orderby, final boolean asc)
    {
        Criteria criteria = createCriteria();

        criteria.add(sameVirtualDatacenter(virtualDatacenter));

        if (!StringUtils.isEmpty(filter))
        {
            criteria.add(filterBy(filter));
        }

        if (!StringUtils.isEmpty(orderby.getColumnSQL()))
        {
            Order order = Order.desc(orderby.getColumnSQL());
            if (asc)
            {
                order = Order.asc(orderby.getColumnSQL());
            }
            criteria.addOrder(order);
        }

        return criteria;
    }

    private Criteria createCriteria(final Enterprise enterprise, final String filter,
        final OrderByEnum orderby, final boolean asc)
    {
        Criteria criteria = createCriteria();

        criteria.add(sameEnterprise(enterprise));

        if (!StringUtils.isEmpty(filter))
        {
            criteria.add(filterBy(filter));
        }

        if (!StringUtils.isEmpty(orderby.getColumnSQL()))
        {
            Order order = Order.desc(orderby.getColumnSQL());
            if (asc)
            {
                order = Order.asc(orderby.getColumnSQL());
            }
            criteria.addOrder(order);
        }

        return criteria;
    }

    private Criterion filterBy(final String filter)
    {
        Disjunction filterDisjunction = Restrictions.disjunction();

        filterDisjunction
            .add(Restrictions.like(VirtualAppliance.NAME_PROPERTY, '%' + filter + '%'));

        return filterDisjunction;
    }
}
