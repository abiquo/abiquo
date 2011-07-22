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

package com.abiquo.server.core.pricing;

import java.util.Collection;
import java.util.List;

import javax.persistence.EntityManager;

import org.apache.commons.lang.StringUtils;
import org.hibernate.Criteria;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.Disjunction;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.springframework.stereotype.Repository;

import com.abiquo.server.core.common.persistence.DefaultDAOBase;
import com.abiquo.server.core.util.PagedList;

@Repository("jpaPricingTemplateDAO")
public class PricingTemplateDAO extends DefaultDAOBase<Integer, PricingTemplate>
{
    public PricingTemplateDAO()
    {
        super(PricingTemplate.class);
    }

    public PricingTemplateDAO(final EntityManager entityManager)
    {
        super(PricingTemplate.class, entityManager);
    }

    public static Criterion sameName(final String name)
    {
        return Restrictions.eq(PricingTemplate.NAME_PROPERTY, name);
    }

    private Criterion filterBy(final String filter)
    {
        Disjunction filterDisjunction = Restrictions.disjunction();

        filterDisjunction.add(Restrictions.like(PricingTemplate.NAME_PROPERTY, '%' + filter + '%'));

        return filterDisjunction;
    }

    /**
     * Look up in the DB for a pricing template with same name.
     * 
     * @param name.
     * @return boolean true if exists, false otherwise.
     */
    public boolean existAnyPricTempWithSameName(final String name)
    {
        return existsAnyByCriterions(sameName(name));
    }

    public Collection<PricingTemplate> find(final String filter, final String orderBy,
        final boolean desc, final Integer offset, final Integer numResults)
    {
        Criteria criteria = createCriteria(filter, orderBy, desc);

        Long total = count(criteria);

        criteria = createCriteria(filter, orderBy, desc);

        criteria.setFirstResult(offset * numResults);
        criteria.setMaxResults(numResults);

        List<PricingTemplate> result = getResultList(criteria);

        PagedList<PricingTemplate> page = new PagedList<PricingTemplate>();
        page.addAll(result);
        page.setCurrentElement(offset);
        page.setPageSize(numResults);
        page.setTotalResults(total.intValue());

        return page;
    }

    private Criteria createCriteria(final String filter, final String orderBy, final boolean desc)
    {
        Criteria criteria = createCriteria();

        if (!StringUtils.isEmpty(filter))
        {
            criteria.add(filterBy(filter));
        }

        if (!StringUtils.isEmpty(orderBy))
        {
            Order order = Order.asc(orderBy);
            if (desc)
            {
                order = Order.desc(orderBy);
            }
            criteria.addOrder(order);
            criteria.addOrder(Order.asc(PricingTemplate.NAME_PROPERTY));
        }

        return criteria;
    }
}
