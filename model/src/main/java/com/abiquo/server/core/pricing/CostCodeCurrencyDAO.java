package com.abiquo.server.core.pricing;

import javax.persistence.EntityManager;

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

}
