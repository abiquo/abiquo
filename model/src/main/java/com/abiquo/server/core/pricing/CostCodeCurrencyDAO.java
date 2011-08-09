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

import org.hibernate.Criteria;
import org.hibernate.Query;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.Restrictions;
import org.springframework.stereotype.Repository;

import com.abiquo.server.core.common.persistence.DefaultDAOBase;

@Repository("jpaCosteCodeCurrencyDAO")
public class CostCodeCurrencyDAO extends DefaultDAOBase<Integer, CostCodeCurrency>
{
    public CostCodeCurrencyDAO()
    {
        super(CostCodeCurrency.class);
    }

    public CostCodeCurrencyDAO(final EntityManager entityManager)
    {
        super(CostCodeCurrency.class, entityManager);
    }

    private final static String COST_CODES =
        " select distinct ccc.costCode FROM com.abiquo.server.core.pricing.CostCodeCurrency ccc ";

    private Criterion sameCostCode(final CostCode costCode)
    {
        return Restrictions.eq(CostCodeCurrency.COST_CODE_PROPERTY, costCode);
    }

    private Criterion sameCurrency(final Currency currency)
    {
        return Restrictions.eq(CostCodeCurrency.CURRENCY_PROPERTY, currency);
    }

    public CostCodeCurrency findCurrencyCostCode(final CostCode costCode, final Currency currency)
    {
        return (CostCodeCurrency) createCriteria(sameCostCode(costCode), sameCurrency(currency))
            .uniqueResult();
    }

    public boolean existAnyOtherWithCurrency(final CostCodeCurrency costCodeCurrency,
        final Currency currency, final CostCode costCode)
    {
        return existsAnyOtherByCriterions(costCodeCurrency, sameCurrency(currency),
            sameCostCode(costCode));
    }

    public Collection<CostCodeCurrency> find(final CostCode cc)
    {

        Criteria criteria = createCriteria(sameCostCode(cc));

        return criteria.list();
    }

    @SuppressWarnings("unchecked")
    public List<CostCode> findCostCodesIds()
    {
        Query query = getSession().createQuery(COST_CODES);
        return query.list();
    }

}
