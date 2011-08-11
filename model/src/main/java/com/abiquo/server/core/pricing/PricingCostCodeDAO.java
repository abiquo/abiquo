package com.abiquo.server.core.pricing;

import java.util.Collection;

import javax.persistence.EntityManager;

import org.hibernate.Criteria;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.Restrictions;
import org.springframework.stereotype.Repository;

import com.abiquo.server.core.common.persistence.DefaultDAOBase;

@Repository("jpaPricingCostCodeDAO")
public class PricingCostCodeDAO extends DefaultDAOBase<Integer, PricingCostCode>
{
    public PricingCostCodeDAO()
    {
        super(PricingCostCode.class);
    }

    public PricingCostCodeDAO(final EntityManager entityManager)
    {
        super(PricingCostCode.class, entityManager);
    }

    private Criterion sameCostCode(final CostCode costCode)
    {
        return Restrictions.eq(PricingCostCode.COST_CODE_PROPERTY, costCode);
    }

    private Criterion samePricing(final PricingTemplate pricing)
    {
        return Restrictions.eq(PricingCostCode.PRICING_TEMPLATE_PROPERTY, pricing);
    }

    public Object findPricingCostCode(final CostCode costCode, final PricingTemplate pricing)
    {
        return createCriteria(sameCostCode(costCode), samePricing(pricing)).uniqueResult();
    }

    public Collection<PricingCostCode> findPricingCostCodes(final PricingTemplate pricing)
    {

        Criteria criteria = createCriteria(samePricing(pricing));

        return criteria.list();
    }

}
